/*
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software � confidencial e propriedade do Banco Central do Brasil.
 * N�o � permitida sua distribui��o ou divulga��o do seu conte�do sem
 * expressa autoriza��o do Banco Central.
 * Este arquivo cont�m informa��es propriet�rias.
 */
package br.gov.bcb.sisaps.batch.main;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import br.gov.bcb.sisaps.batch.AbstractBatchApplication;
import br.gov.bcb.sisaps.src.dao.AtividadeCicloETLDAO;
import br.gov.bcb.sisaps.src.dominio.AtividadeCiclo;
import br.gov.bcb.sisaps.src.dominio.AtividadeCicloETL;
import br.gov.bcb.sisaps.src.mediator.AtividadeCicloMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.util.geral.DataUtil;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import br.gov.bcb.utils.logging.BCLogFactory;
import br.gov.bcb.utils.logging.BCLogger;

public class BatchMigracaoDadosETLAtividadesCiclo extends AbstractBatchApplication {
    
    private static final String ATIVIDADE_JA_CADASTRADA = "# Atividade j� cadastrada.";
    private static final String ATIVIDADES_CADASTRADAS_PARA_O_ANO = " atividade(s) cadastradas para o ano ";
    private static final String FORAM_ENCONTRADAS = "## Foi/foram encontrada(s) ";
    private static final BCLogger LOG = BCLogFactory.getLogger("BatchMigracaoDadosETLAtividadesCiclo");

    public static void main(String[] args) {
        new BatchMigracaoDadosETLAtividadesCiclo().init();
    }
    
    protected void executar() {
        LOG.info("ROTINA DE MIGRA��O DOS DADOS DO ETL(EAT_ETL_ATC) PARA A TABELA DO SISTEMA (ATC_ATIVIDADE_CICLO)");
        LOG.info("Iniciado em: " + DataUtil.getDateTimeAtual());
        
        AtividadeCicloMediator atividadeCicloMediator = 
                (AtividadeCicloMediator) getSpringContext().getBean("atividadeCicloMediator");
        AtividadeCicloETLDAO atividadeCicloETLDAO = 
                (AtividadeCicloETLDAO) getSpringContext().getBean("atividadeCicloETLDAO");
        
        verificarExclusoesAtividades(atividadeCicloMediator, atividadeCicloETLDAO);
        
        verificarInclusoesAlteracoesAtividades(atividadeCicloMediator, atividadeCicloETLDAO);
        
        //retirar para rodar teste local
        System.exit(0);
        
    }
    
    /**
     * Verificar quais atividades dos ciclos do ano atual e do anterior n�o est�o nos registros vindos do ETL 
     * e realizar as a��es necess�rias caso n�o esteja.
     * 
     * @param atividadeCicloMediator
     * @param atividadeCicloETLDAO 
     */
    private void verificarExclusoesAtividades(AtividadeCicloMediator atividadeCicloMediator, 
            AtividadeCicloETLDAO atividadeCicloETLDAO) {
        LOG.info("### Verificar exclus�es de atividades");
        DateTime dateTimeAtual = DataUtil.getDateTimeAtual();
        List<AtividadeCiclo> atividadesCicloAnoAtualEAnterior = new ArrayList<AtividadeCiclo>();
        
        List<AtividadeCiclo> atividadesCicloAnoAtual = 
                atividadeCicloMediator.buscarAtividadesCiclo((short) dateTimeAtual.getYear());
        atividadesCicloAnoAtualEAnterior.addAll(atividadesCicloAnoAtual);
        LOG.info(FORAM_ENCONTRADAS + atividadesCicloAnoAtual.size() 
                + ATIVIDADES_CADASTRADAS_PARA_O_ANO + dateTimeAtual.getYear());
        
        List<AtividadeCiclo> atividadesCicloAnoAnterior = 
                atividadeCicloMediator.buscarAtividadesCiclo((short) (dateTimeAtual.getYear() - 1));
        atividadesCicloAnoAtualEAnterior.addAll(atividadesCicloAnoAnterior);
        LOG.info(FORAM_ENCONTRADAS + atividadesCicloAnoAnterior.size() 
                + ATIVIDADES_CADASTRADAS_PARA_O_ANO + (dateTimeAtual.getYear() - 1));
        
        for (AtividadeCiclo atividadeCiclo : atividadesCicloAnoAtualEAnterior) {
            try  {
                verificarExclusaoAtividadeCicloETL(atividadeCiclo, atividadeCicloMediator, atividadeCicloETLDAO);
            } catch (NegocioException e) {
                LOG.info("# A atividade " + atividadeCiclo.getCodigo() 
                        + " n�o foi exclu�da porque n�o est� no perfil de risco.");
            }
        }
    }
    
    /**
     * Verificar se a atividade do ciclo est� nos registros vindos do ETL. Se n�o estiver, realizar as seguintes a��es:
     * - Caso a atividade do ciclo estiver em apenas um perfil de risco do ciclo da ES, 
     * excluir do perfil de risco e excluir o registro da atividade do ciclo.
     * - Caso a atividade do ciclo estiver em mais de um perfil de risco do ciclo da ES, 
     * excluir apenas do perfil de risco atual
     * 
     * @param atividadeCiclo
     * @param atividadeCicloMediator
     * @param atividadeCicloETLDAO
     */
    private void verificarExclusaoAtividadeCicloETL(AtividadeCiclo atividadeCiclo, 
            AtividadeCicloMediator atividadeCicloMediator, AtividadeCicloETLDAO atividadeCicloETLDAO) {
        LOG.info("## Verificar exclus�o da atividade: " + atividadeCiclo.getNomeAtividadeCicloFormatado());
        AtividadeCicloETL atividadeCicloETL = atividadeCicloETLDAO.buscarAtividadeCicloETL(
                atividadeCiclo.getCnpjES(), atividadeCiclo.getCodigo());
        if (atividadeCicloETL == null) {
            LOG.info("# Atividade n�o encontrada na tabela do ETL.");
            atividadeCicloMediator.excluirAtividadeCiclo(atividadeCiclo);
        }
    }

    /**
     * Verificar quais registros vindos do ETL s�o novos registros e quais registros j� existiam e 
     * foram ou n�o alterados e realizar as a��es necess�rias.
     * 
     * @param atividadeCicloMediator
     * @param atividadeCicloETLDAO
     */
    private void verificarInclusoesAlteracoesAtividades(AtividadeCicloMediator atividadeCicloMediator, 
            AtividadeCicloETLDAO atividadeCicloETLDAO) {
        LOG.info("### Verificar inclus�es e altera��es de atividades vindas do ETL");
        List<AtividadeCicloETL> atividadesCicloETL = atividadeCicloETLDAO.consultarTodasAtividadesCicloETL();
        LOG.info(FORAM_ENCONTRADAS + atividadesCicloETL.size() + " atividades vindas do ETL");
        PerfilRiscoMediator perfilRiscoMediator = 
                (PerfilRiscoMediator) getSpringContext().getBean("perfilRiscoMediator");
        
        for (AtividadeCicloETL atividadeCicloETL : atividadesCicloETL) {
            try {
                verificarInclusaoAlteracaoAtividadeCicloETL(atividadeCicloMediator, atividadeCicloETL, perfilRiscoMediator);
            } catch (NegocioException e) {
                LOG.info("######## NegocioException: " + e.getMessage());
            }
        }
    }

    /**
     * Processa o registro da atividade do ciclo vindo do ETL e verifica a a��o a ser tomada:
     * - Se o registro n�o existir em nenhum perfil de risco, adicionar ao perfil de risco atual do ciclo da ES;
     * - Se o registro existir e estiver em apenas um perfil de risco do ciclo da ES, alterar o registro;
     * - Se o registro existir e estiver em mais de um perfil de risco do ciclo da ES, incluir um novo registro 
     * no perfil de risco atual do ciclo da ES e excluir o antigo, deixando ele nos perfis de risco anteriores.
     * 
     * @param atividadeCicloMediator 
     * @param atividadeCicloETL
     * @param perfilRiscoMediator 
     */
    private void verificarInclusaoAlteracaoAtividadeCicloETL(AtividadeCicloMediator atividadeCicloMediator, 
            AtividadeCicloETL atividadeCicloETL, PerfilRiscoMediator perfilRiscoMediator) {
        LOG.info("## Verificar inclus�o/altera��o da atividade " + atividadeCicloETL.getNomeAtividadeCicloETLFormatado()
                + " vinda do ETL");
        
        AtividadeCiclo atividadeCiclo = atividadeCicloMediator.buscarUltimaAtividadeCiclo(atividadeCicloETL.getCnpjES(), 
                atividadeCicloETL.getCodigo());
        
        if (atividadeCiclo == null) {
            LOG.info("# Atividade n�o cadastrada. Incluindo atividade no Perfil de Risco atual do ciclo da ES.");
            atividadeCicloMediator.incluirAtividadeCicloPerfilRiscoAtual(criarAtividadeCiclo(atividadeCicloETL));
        } else if (houveAlteracaoRegistro(atividadeCicloETL, atividadeCiclo)) {
            LOG.info(ATIVIDADE_JA_CADASTRADA + " Houve altera��o no seu registro.");
            if (perfilRiscoMediator.versaoEmMaisDeUmPerfilRisco(atividadeCiclo.getVersaoPerfilRisco())) {
                LOG.info("# Atividade em mais de um Perfil de Risco. Criando novo registro de atividade e " 
                        + "incluindo no Perfil de Risco atual do ciclo da ES no lugar no antigo.");
                atividadeCicloMediator.alterarAtividadeCicloPerfilRiscoAtual(
                        atividadeCiclo, criarAtividadeCiclo(atividadeCicloETL));
            } else {
                LOG.info("# Atividade apenas em um Perfil de Risco. Alterando seu registro.");
                atividadeCiclo.setDataBase(atividadeCicloETL.getDataBase());
                atividadeCiclo.setDescricao(atividadeCicloETL.getDescricao());
                atividadeCiclo.setSituacao(atividadeCicloETL.getSituacao());
                atividadeCicloMediator.alterar(atividadeCiclo);
            }
        } else {
            LOG.info(ATIVIDADE_JA_CADASTRADA + " N�o houve altera��o no seu registro.");
        }
        
    }
    
    /**
     * Verificar se houver altera��o no registro da Atividade do Ciclo com rela��o ao registro do ETL.
     * 
     * @param atividadeCicloETL
     * @param atividadeCiclo
     * @return
     * @true Caso houve alguma altera��o.
     * @false Caso n�o houve nenhuma altera��o.
     */
    private boolean houveAlteracaoRegistro(AtividadeCicloETL atividadeCicloETL, AtividadeCiclo atividadeCiclo) {
        return atividadeCiclo.getDataBase().compareTo(atividadeCicloETL.getDataBase()) != 0 
                || !atividadeCiclo.getDescricao().equals(atividadeCicloETL.getDescricao())
                || !atividadeCiclo.getSituacao().equals(atividadeCicloETL.getSituacao());
    }

    /**
     * Criar uma nova Atividade do Ciclo com base nos dados vindos do ETL.
     * 
     * @param atividadeCicloETL
     * @return AtividadeCiclo
     */
    private AtividadeCiclo criarAtividadeCiclo(AtividadeCicloETL atividadeCicloETL) {
        AtividadeCiclo novaAtividadeCiclo = new AtividadeCiclo();
        novaAtividadeCiclo.setCnpjES(atividadeCicloETL.getCnpjES());
        novaAtividadeCiclo.setCodigo(atividadeCicloETL.getCodigo());
        novaAtividadeCiclo.setAno(atividadeCicloETL.getAno());
        novaAtividadeCiclo.setDataBase(atividadeCicloETL.getDataBase());
        novaAtividadeCiclo.setDescricao(atividadeCicloETL.getDescricao());
        novaAtividadeCiclo.setSituacao(atividadeCicloETL.getSituacao());
        return novaAtividadeCiclo;
    }

}