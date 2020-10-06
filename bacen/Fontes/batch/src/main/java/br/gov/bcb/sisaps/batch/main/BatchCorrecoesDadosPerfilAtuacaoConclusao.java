package br.gov.bcb.sisaps.batch.main;

import java.util.List;

import org.joda.time.DateTime;

import br.gov.bcb.comum.pessoa.negocio.componenteorganizacional.ConsultaChefia;
import br.gov.bcb.sisaps.adaptadores.pessoa.BcPessoaAdapter;
import br.gov.bcb.sisaps.adaptadores.pessoa.ChefiaVO;
import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.batch.AbstractBatchApplication;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.ConclusaoES;
import br.gov.bcb.sisaps.src.dominio.EntidadeSupervisionavel;
import br.gov.bcb.sisaps.src.dominio.PerfilAtuacaoES;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.ConclusaoESMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilAtuacaoESMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.util.Util;
import br.gov.bcb.sisaps.util.geral.DataUtil;
import br.gov.bcb.utils.logging.BCLogFactory;
import br.gov.bcb.utils.logging.BCLogger;

public class BatchCorrecoesDadosPerfilAtuacaoConclusao extends AbstractBatchApplication {
    
    private static final String PERFIL_DE_ATUACAO_DE_ID = "Perfil de atuação de ID ";
    private static final String NAO_FOI_POSSIVEL_ENCONTRAR_O_GERENTE = "Não foi possível encontrar o gerente.";
    private static final String NAO_FOI_POSSIVEL_ENCONTRAR_O_SUPERVISOR = "Não foi possível encontrar o supervisor.";
    private static final String DIVISOR_LOCALIZACAO = "/";
    private static final String E_VER_ID = " e VER_ID ";
    private static final String PRIMEIRO_PERFIL_DE_RISCO_E = "Primeiro perfil de risco é: ";

    private static final BCLogger LOG = BCLogFactory.getLogger("BatchCorrecoesDadosPerfilAtuacaoConclusao");
    
    public static void main(String[] args) {
        new BatchCorrecoesDadosPerfilAtuacaoConclusao().init();
    }
    
    @Override
    protected void executar() {
        LOG.info("ROTINA DE AJUSTE DE DADOS DE PERFIL DE ATUAÇÃO E CONCLUSÃO ");
        LOG.info("Iniciado em: " + DataUtil.getDateTimeAtual());
          
        try {
            corrigirDadosPerfilAtuacaoConclusao(); 
        } catch (Exception e) {
            LOG.info(e.toString());
            e.printStackTrace();
        } finally {
            //retirar para rodar teste local
            System.exit(0);
        }

    }
    
    public void corrigirDadosPerfilAtuacaoConclusao() {
        Util.setBatch(Boolean.TRUE);
        atualizarUsuarioPublicacaoPerfisAtuacao();
        atualizarUsuarioPublicacaoConclusoes();
    }
    
    private void atualizarUsuarioPublicacaoPerfisAtuacao() {
        LOG.info("Atualizar último operador dos perfis de atuação.");
        int quantidadeRegistrosAlterados = 0;
        // Obter todos perfis de atuação publicados
        List<PerfilAtuacaoES> perfisPublicados = PerfilAtuacaoESMediator.get().buscarTodosPerfisPublicados();
        LOG.info(perfisPublicados.size() + " perfis de atuação publicados encontrados.");
        // Para cada perfil de atuação:
        for (PerfilAtuacaoES perfilAtuacao : perfisPublicados) {
            LOG.info(PERFIL_DE_ATUACAO_DE_ID + perfilAtuacao.getPk() 
                    + E_VER_ID + perfilAtuacao.getVersaoPerfilRisco().getPk());
            // Obter o seu primeiro perfil de risco
            PerfilRisco perfilRisco = PerfilRiscoMediator.get().buscarPrimeiroPerfil(perfilAtuacao.getVersaoPerfilRisco());
            LOG.info(PRIMEIRO_PERFIL_DE_RISCO_E + perfilRisco.getPk());
            
            if (perfilAtuacao.getOperadorEncaminhamento() == null) {
                LOG.info("Perfil de atuação sem operador de encaminhamento. Atualizando as informações...");
                ServidorVO supervisor = obterSupervisorESPerfilRisco(perfilRisco);
                perfilAtuacao.setAlterarDataUltimaAtualizacao(false);
                perfilAtuacao.setUltimaAtualizacao(perfilRisco.getDataCriacao());
                perfilAtuacao.setDataEncaminhamento(perfilRisco.getDataCriacao());
                if (supervisor == null) {
                    LOG.info(NAO_FOI_POSSIVEL_ENCONTRAR_O_SUPERVISOR);
                } else {
                    perfilAtuacao.setOperadorAtualizacao(supervisor.getLogin());
                    perfilAtuacao.setOperadorEncaminhamento(supervisor.getLogin());
                }
                PerfilAtuacaoESMediator.get().update(perfilAtuacao);
                quantidadeRegistrosAlterados++;
            } else if (perfilAtuacao.getUltimaAtualizacao().equals(perfilRisco.getDataCriacao())
                    || !isDataAtualizacaoIgualDataPerfilRisco(perfilRisco, perfilAtuacao.getUltimaAtualizacao())) {
                LOG.info("Data de criação do perfil de risco é exatamente igual a data de última " 
                        + "atualização do Perfil de atuação ou data de última alteração " 
                        + "'diferente' da data do perfil de risco.");
                // atualizar o perfil de atuação com a data do perfil de risco e com o chefe da localização superior à ES
                ServidorVO gerente = obterGerenteESPerfilRisco(perfilRisco);
                perfilAtuacao.setAlterarDataUltimaAtualizacao(false);
                perfilAtuacao.setUltimaAtualizacao(perfilRisco.getDataCriacao());
                if (gerente == null) {
                    LOG.info(NAO_FOI_POSSIVEL_ENCONTRAR_O_GERENTE);
                } else {
                    perfilAtuacao.setOperadorAtualizacao(gerente.getLogin());
                }
                PerfilAtuacaoESMediator.get().update(perfilAtuacao);
                quantidadeRegistrosAlterados++;
            }
        }
        LOG.info(quantidadeRegistrosAlterados + " perfis de atuação alterados.");
    }

    private boolean isDataAtualizacaoIgualDataPerfilRisco(PerfilRisco perfilRisco, DateTime ultimaAtualizacao) {
        // tolerância de 10s pra mais ou pra menos
        return ultimaAtualizacao.isAfter(perfilRisco.getDataCriacao().minusSeconds(10)) 
                && ultimaAtualizacao.isBefore(perfilRisco.getDataCriacao().plusSeconds(10));
    }

    private void atualizarUsuarioPublicacaoConclusoes() {
        LOG.info("Atualizar último operador das conclusões.");
        int quantidadeRegistrosAlterados = 0;
        // Obter todas conclusões publicadas
        List<ConclusaoES> conclusoesPublicadas = ConclusaoESMediator.get().buscarTodasOpinioesPublicadas();
        LOG.info(conclusoesPublicadas.size() + " conclusões publicadas encontrados.");
        // Para cada conclusão:
        for (ConclusaoES conclusao : conclusoesPublicadas) {
            LOG.info("Conclusão de ID " + conclusao.getPk() 
                    + E_VER_ID + conclusao.getVersaoPerfilRisco().getPk());
            // Obter o seu primeiro perfil de risco
            PerfilRisco perfilRisco = PerfilRiscoMediator.get().buscarPrimeiroPerfil(conclusao.getVersaoPerfilRisco());
            LOG.info(PRIMEIRO_PERFIL_DE_RISCO_E + perfilRisco.getPk());
            
            if (conclusao.getOperadorEncaminhamento() == null) {
                LOG.info("Conclusão sem operador de encaminhamento. Atualizando as informações...");
                ServidorVO supervisor = obterSupervisorESPerfilRisco(perfilRisco);
                conclusao.setAlterarDataUltimaAtualizacao(false);
                conclusao.setUltimaAtualizacao(perfilRisco.getDataCriacao());
                conclusao.setDataEncaminhamento(perfilRisco.getDataCriacao());
                if (supervisor == null) {
                    LOG.info(NAO_FOI_POSSIVEL_ENCONTRAR_O_SUPERVISOR);
                } else {
                    conclusao.setOperadorAtualizacao(supervisor.getLogin());
                    conclusao.setOperadorEncaminhamento(supervisor.getLogin());
                }
                ConclusaoESMediator.get().update(conclusao);
                quantidadeRegistrosAlterados++;
            } else if (conclusao.getUltimaAtualizacao().equals(perfilRisco.getDataCriacao())
                    || !isDataAtualizacaoIgualDataPerfilRisco(perfilRisco, conclusao.getUltimaAtualizacao())) {
                LOG.info("Data de criação do perfil de risco é exatamente igual a data de última atualização da conclusão " 
                        + "ou data de última alteração 'diferente' da data do perfil de risco.");
                // atualizar o perfil de atuação com a data do perfil de risco e com o chefe da localização superior à ES
                ServidorVO gerente = obterGerenteESPerfilRisco(perfilRisco);
                conclusao.setAlterarDataUltimaAtualizacao(false);
                conclusao.setUltimaAtualizacao(perfilRisco.getDataCriacao());
                if (gerente == null) {
                    LOG.info(NAO_FOI_POSSIVEL_ENCONTRAR_O_GERENTE);
                } else {
                    conclusao.setOperadorAtualizacao(gerente.getLogin());
                }
                ConclusaoESMediator.get().update(conclusao);
                quantidadeRegistrosAlterados++;
            }
        }
        LOG.info(quantidadeRegistrosAlterados + " conclusões alteradas.");
    }
    
    private ServidorVO obterSupervisorESPerfilRisco(PerfilRisco perfilRisco) {
        // Obter a localização da ES desse perfil de risco
        EntidadeSupervisionavel entidadeSupervisionavel = 
                PerfilRiscoMediator.get().getEntidadeSupervisionavelPerfilRisco(perfilRisco);
        // Buscar o chefe dessa localização na data do perfil de risco
        ChefiaVO chefia = buscarChefia(perfilRisco, entidadeSupervisionavel.getLocalizacao());
        
        if (chefia.getChefeTitular() == null) {
            String localizacaoBugada = entidadeSupervisionavel.getLocalizacao().replaceAll("-", "_");
            chefia = buscarChefia(perfilRisco, localizacaoBugada);
        }
        
        return chefia.getChefeTitular();
    }
    
    private ServidorVO obterGerenteESPerfilRisco(PerfilRisco perfilRisco) {
        // Obter a localização da ES desse perfil de risco
        EntidadeSupervisionavel entidadeSupervisionavel = 
                PerfilRiscoMediator.get().getEntidadeSupervisionavelPerfilRisco(perfilRisco);
        
        String localizacaoSuperior = extrairLocalizacaoSuperior(entidadeSupervisionavel.getLocalizacao());
        
        ChefiaVO chefia = buscarChefia(perfilRisco, localizacaoSuperior);
        
        if (chefia.getChefeTitular() == null) {
            PerfilRisco perfilRiscoAnterior = PerfilRiscoMediator.get().buscarPerfilRiscoAnterior(perfilRisco);
            if (perfilRiscoAnterior == null) {
                Ciclo cicloAnterior = CicloMediator.get().consultarCicloAnteriorEntidadeSupervisionavel(
                        perfilRisco.getCiclo(), entidadeSupervisionavel.getConglomeradoOuCnpj());
                if (cicloAnterior != null) {
                    perfilRiscoAnterior = PerfilRiscoMediator.get().obterPerfilRiscoAtual(cicloAnterior.getPk());
                }
            }
            if (perfilRiscoAnterior != null) {
                EntidadeSupervisionavel esPerfilRiscoAnterior = 
                        PerfilRiscoMediator.get().getEntidadeSupervisionavelPerfilRisco(perfilRiscoAnterior);
                localizacaoSuperior = extrairLocalizacaoSuperior(esPerfilRiscoAnterior.getLocalizacao());
                chefia = buscarChefia(perfilRiscoAnterior, localizacaoSuperior);
            }
        }
        
        return chefia.getChefeTitular();
    }

    private ChefiaVO buscarChefia(PerfilRisco perfilRisco, String localizacao) {
        ConsultaChefia consulta = new ConsultaChefia();
        consulta.setComponenteOrganizacionalRotulo(localizacao);
        consulta.setDataBase(perfilRisco.getDataCriacao().toDate());
        return BcPessoaAdapter.get().buscarChefia(consulta);
    }

    private String extrairLocalizacaoSuperior(String localizacaoES) {
        String[] niveis = localizacaoES.split(DIVISOR_LOCALIZACAO);
        StringBuilder localizacao = new StringBuilder();
        for (int i = 0; i < (niveis.length - 1); i++) {
            localizacao.append(niveis[i]);
            if ((i + 1) < (niveis.length - 1)) {
                localizacao.append(DIVISOR_LOCALIZACAO);
            }
        }
        return localizacao.toString();
    }

}
