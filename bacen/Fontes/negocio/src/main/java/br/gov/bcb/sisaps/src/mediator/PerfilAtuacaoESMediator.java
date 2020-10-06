package br.gov.bcb.sisaps.src.mediator;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.seguranca.UsuarioCorrente;
import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.seguranca.UsuarioAplicacao;
import br.gov.bcb.sisaps.src.dao.PerfilAtuacaoESDAO;
import br.gov.bcb.sisaps.src.dominio.AnexoDocumento;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.ConclusaoES;
import br.gov.bcb.sisaps.src.dominio.Documento;
import br.gov.bcb.sisaps.src.dominio.EntidadeUnicad;
import br.gov.bcb.sisaps.src.dominio.OpiniaoConclusivaConsolidado;
import br.gov.bcb.sisaps.src.dominio.PerfilAtuacaoConsolidado;
import br.gov.bcb.sisaps.src.dominio.PerfilAtuacaoES;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.VersaoPerfilRisco;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoEntidadeAnexoDocumentoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoObjetoVersionadorEnum;
import br.gov.bcb.sisaps.src.util.BufferAnexos;
import br.gov.bcb.sisaps.src.validacao.RegraSalvarPerfilAtuacaoES;
import br.gov.bcb.sisaps.src.vo.AnexoDocumentoVo;
import br.gov.bcb.sisaps.util.Util;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import br.gov.bcb.sisaps.util.enumeracoes.TipoSubEventoPerfilRiscoSRC;
import br.gov.bcb.sisaps.util.geral.DataUtil;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;

@Service
@Transactional(readOnly = true)
public class PerfilAtuacaoESMediator {

    @Autowired
    private PerfilAtuacaoESDAO perfilAtuacaoESDAO;

    @Autowired
    private AnexoDocumentoMediator anexoDocumentoMediator;

    @Autowired
    private PerfilAtuacaoConsolidadoMediator perfilAtuacaoConsolidadoMediator;

    @Autowired
    private EventoConsolidadoMediator eventoConsolidadoMediator;
    
    @Autowired
    private EntidadeUnicadMediator entidadeUnicadMediator;

    @Autowired
    private EntidadeSupervisionavelMediator entidadeSupervisionavelMediator;

    public static PerfilAtuacaoESMediator get() {
        return SpringUtils.get().getBean(PerfilAtuacaoESMediator.class);
    }

    public PerfilAtuacaoES buscarPorPk(Integer pk) {
        PerfilAtuacaoES perfil = perfilAtuacaoESDAO.buscarPerfilAtuacaoESPorPk(pk);
        inicializar(perfil);
        if (perfil.getCiclo() != null) {
            Hibernate.initialize(perfil.getCiclo());
        }
        return perfil;
    }

    public PerfilAtuacaoES getUltimoPerfilAtuacaoES(Ciclo ciclo) {
        return perfilAtuacaoESDAO.getUltimoPerfilAtuacaoES(ciclo);
    }

    @Transactional(readOnly = true)
    public PerfilAtuacaoES getPerfilAtuacaoESSemPerfilRisco(Ciclo ciclo) {
        PerfilAtuacaoES ultimoPerfilAtuacaoES = perfilAtuacaoESDAO.buscarSemPerfil(ciclo);
        inicializar(ultimoPerfilAtuacaoES);
        return ultimoPerfilAtuacaoES;
    }

    @Transactional(readOnly = true)
    public PerfilAtuacaoES getPerfilAtuacaoESPorPerfil(int perfilPK) {
        PerfilAtuacaoES ultimoPerfilAtuacaoES = perfilAtuacaoESDAO.buscarPorPerfilRisco(perfilPK);
        if (ultimoPerfilAtuacaoES != null) {
        	ultimoPerfilAtuacaoES.setAlterarDataUltimaAtualizacao(false);
        }
        inicializar(ultimoPerfilAtuacaoES);
        return ultimoPerfilAtuacaoES;
    }

    private void inicializar(PerfilAtuacaoES ultimoPerfilAtuacaoES) {
        if (ultimoPerfilAtuacaoES != null) {
            Hibernate.initialize(ultimoPerfilAtuacaoES.getDocumento());
            if (ultimoPerfilAtuacaoES.getPerfilAtuacaoESAnterior() != null) {
                Hibernate.initialize(ultimoPerfilAtuacaoES.getPerfilAtuacaoESAnterior());
                Hibernate.initialize(ultimoPerfilAtuacaoES.getPerfilAtuacaoESAnterior().getDocumento());
            }
        }
    }

    @Transactional(readOnly = true)
    public PerfilAtuacaoES getPerfilAtuacaoESPendencia(Ciclo ciclo) {
        PerfilAtuacaoES ultimoPerfilAtuacaoES = perfilAtuacaoESDAO.buscarPorPendencia(ciclo);
        inicializar(ultimoPerfilAtuacaoES);
        return ultimoPerfilAtuacaoES;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public String salvarNovoPerfilAtuacao(PerfilAtuacaoES perfilAtuacaoES, boolean isSalvar) {
        new RegraSalvarPerfilAtuacaoES(perfilAtuacaoES).validar();

        if (SisapsUtil.isTextoCKEditorBrancoOuNulo(perfilAtuacaoES.getDocumento().getJustificativa())) {
            perfilAtuacaoES.getDocumento().setJustificativa(null);
        }
        DocumentoMediator.get().salvar(perfilAtuacaoES.getDocumento());
        if (isSalvar) {
            perfilAtuacaoES.setPendente(SimNaoEnum.NAO);
            perfilAtuacaoES.setDataEncaminhamento(null);
            perfilAtuacaoES.setOperadorEncaminhamento(null);
        }
        perfilAtuacaoESDAO.saveOrUpdate(perfilAtuacaoES);
        perfilAtuacaoESDAO.getSessionFactory().getCurrentSession().flush();

        return "Perfil de atuação salvo com sucesso.";
    }

    @Transactional
    public String confirmarNovoPerfilAtuacao(PerfilAtuacaoES perfilAtuacaoES) {
        Util.setIncluirBufferAnexos(true);
        BufferAnexos.resetLocalThreadBufferInclusao();
        PerfilAtuacaoES perfilAtuacaoESDB = perfilAtuacaoESDAO.buscarPerfilAtuacaoESPorPk(perfilAtuacaoES.getPk());
        perfilAtuacaoESDB.setPendente(null);
        validarNovoPerfilAtuacao(perfilAtuacaoESDB);
        VersaoPerfilRisco versaoPerfilRisco =
                PerfilRiscoMediator.get().gerarNovaVersaoPerfilRisco(perfilAtuacaoESDB.getCiclo(),
                        perfilAtuacaoESDB.getPerfilAtuacaoESAnterior(), TipoObjetoVersionadorEnum.PERFIL_ATUACAO_ES);
        perfilAtuacaoESDB.setVersaoPerfilRisco(versaoPerfilRisco);
        perfilAtuacaoESDAO.saveOrUpdate(perfilAtuacaoESDB);
        perfilAtuacaoESDAO.getSessionFactory().getCurrentSession().flush();
        criarPerfilAtuacaoRascunho(perfilAtuacaoES);
        
        EntidadeUnicad entidadeUnicad =
                entidadeUnicadMediator.buscarEntidadeUnicadPorCnpj(perfilAtuacaoESDB.getCiclo()
                        .getEntidadeSupervisionavel().getConglomeradoOuCnpj());
        
        if (entidadeSupervisionavelMediator.possuiPermissaoGeracaoEventos(entidadeUnicad)) {
            PerfilAtuacaoConsolidado perfilEmEdicao =
                    perfilAtuacaoConsolidadoMediator.buscarEmEdicao(entidadeUnicad.getConsolidado().getPk());
            PerfilAtuacaoConsolidado perfilEmHomologacao =
                    perfilAtuacaoConsolidadoMediator.buscarEmHomologacao(entidadeUnicad.getConsolidado().getPk());

            if (perfilEmEdicao != null) {
                perfilAtuacaoConsolidadoMediator.delete(perfilEmEdicao);
            }
            if (perfilEmHomologacao != null) {
                perfilAtuacaoConsolidadoMediator.delete(perfilEmHomologacao);
            }

            PerfilAtuacaoConsolidado perfilPublicado =
                    perfilAtuacaoConsolidadoMediator.buscarPublicado(entidadeUnicad.getConsolidado().getPk());
            eventoConsolidadoMediator.incluirEventoPerfilDeRisco(perfilAtuacaoESDB.getCiclo(),
                    TipoSubEventoPerfilRiscoSRC.PERFIL_ATUACAO);
            PerfilAtuacaoConsolidado perfilAtuacaoConsolidado = perfilAtuacaoConsolidadoMediator
                    .criarPerfilAtuacaoRascunhoEDocumento(perfilAtuacaoESDB, entidadeUnicad, perfilPublicado);
            eventoConsolidadoMediator.incluirEventoPerfilAtuacao(entidadeUnicad, perfilAtuacaoConsolidado, false);
        }
        
        GeradorAnexoMediator.get().incluirAnexosBuffer();
        return "Atualização do Perfil de atuação da ES no perfil de risco realizada com sucesso.";
    }
    
    @Transactional
    public String encaminharNovoPerfilAtuacao(PerfilAtuacaoES perfilAtuacaoES) {
        validarNovoPerfilAtuacao(perfilAtuacaoES);
        UsuarioAplicacao usuarioAplicacao = (UsuarioAplicacao) UsuarioCorrente.get();
        perfilAtuacaoES.setPendente(SimNaoEnum.SIM);
        perfilAtuacaoES.setDataEncaminhamento(DataUtil.getDateTimeAtual());
        perfilAtuacaoES.setOperadorEncaminhamento(usuarioAplicacao.getLogin());
        perfilAtuacaoESDAO.saveOrUpdate(perfilAtuacaoES);
        return "Perfil de atuação da ES encaminhado(a) para aprovação do gerente com sucesso.";
    }

    private void validarNovoPerfilAtuacao(PerfilAtuacaoES perfilAtuacaoES) {
        ArrayList<ErrorMessage> erros = new ArrayList<ErrorMessage>();
        SisapsUtil
                .validarObrigatoriedade(perfilAtuacaoES.getDocumento().getJustificativa(), "Perfil de atuação", erros);
        SisapsUtil.lancarNegocioException(erros);
    }

    public PerfilAtuacaoES obterPerfilAtuacaoESPorDocumento(Documento documento) {
        return perfilAtuacaoESDAO.obterPerfilAtuacaoESPorDocumento(documento);
    }

    @Transactional
    public void salvarAnexo(Ciclo ciclo, PerfilAtuacaoES perfilAtuacaoES, String clientFileName,
            InputStream inputStream, boolean isSalvar) {
        Util.setIncluirBufferAnexos(true);
        BufferAnexos.resetLocalThreadBufferInclusao();
        salvarNovoPerfilAtuacao(perfilAtuacaoES, isSalvar);
        anexoDocumentoMediator.anexarArquivo(ciclo, perfilAtuacaoES, perfilAtuacaoES.getDocumento(),
                TipoEntidadeAnexoDocumentoEnum.PERFIL_ATUACAO, clientFileName, inputStream, false);
        GeradorAnexoMediator.get().incluirAnexosBuffer();
    }

    @Transactional
    public void excluirAnexo(AnexoDocumentoVo anexoVo, PerfilAtuacaoES perfilAtuacaoES, Ciclo ciclo, boolean isSalvar) {
        salvarNovoPerfilAtuacao(perfilAtuacaoES, isSalvar);
        anexoDocumentoMediator.excluirAnexo(anexoVo, perfilAtuacaoES, TipoEntidadeAnexoDocumentoEnum.PERFIL_ATUACAO,
                ciclo);

    }

    private void duplicarAnexo(Ciclo ciclo, PerfilAtuacaoES perfilAtuacaoESNovo, AnexoDocumento anexoAnterior,
            boolean isCopiarUsuarioAnterior) {
        ByteArrayInputStream inputStream = null;
        if (!SisapsUtil.isExecucaoTeste()) {
            byte[] arquivoAnexo =
                    anexoDocumentoMediator.recuperarArquivo(anexoAnterior.getLink(), perfilAtuacaoESNovo
                            .getPerfilAtuacaoESAnterior(), TipoEntidadeAnexoDocumentoEnum.PERFIL_ATUACAO,
                            perfilAtuacaoESNovo.getPerfilAtuacaoESAnterior().getCiclo());
            inputStream = new ByteArrayInputStream(arquivoAnexo);
        }
        AnexoDocumento novoAnexo =
                anexoDocumentoMediator.anexarArquivo(ciclo, perfilAtuacaoESNovo, perfilAtuacaoESNovo.getDocumento(),
                        TipoEntidadeAnexoDocumentoEnum.PERFIL_ATUACAO, anexoAnterior.getLink(), inputStream, true);
        if (isCopiarUsuarioAnterior) {
            novoAnexo.setAlterarDataUltimaAtualizacao(false);
            novoAnexo.setUltimaAtualizacao(anexoAnterior.getUltimaAtualizacao());
            novoAnexo.setOperadorAtualizacao(anexoAnterior.getOperadorAtualizacao());
            AnexoDocumentoMediator.get().saveOrUpdate(novoAnexo);
        }
    }

    public PerfilAtuacaoES buscarPorPerfilRisco(Integer pkPerfilRisco) {
        return perfilAtuacaoESDAO.buscarPorPerfilRisco(pkPerfilRisco);
    }

    public PerfilAtuacaoES buscarPerfilAtuacaoESRascunho(Integer pkCiclo) {
        return perfilAtuacaoESDAO.buscarPerfilAtuacaoESRascunho(pkCiclo);
    }

    @Transactional
    public void criarPerfilAtuacaoNovoCiclo(PerfilRisco perfilRiscoCicloAtual, Ciclo novoCiclo) {
        PerfilAtuacaoES perfilAtuacaoCicloAtual = buscarPorPerfilRisco(perfilRiscoCicloAtual.getPk());
        perfilAtuacaoESDAO.evict(perfilAtuacaoCicloAtual);
        PerfilAtuacaoES novoPerfilAtuacao = new PerfilAtuacaoES();
        novoPerfilAtuacao.setCiclo(novoCiclo);
        novoPerfilAtuacao.setPerfilAtuacaoESAnterior(perfilAtuacaoCicloAtual);
        novoPerfilAtuacao.setOperadorAtualizacao(perfilAtuacaoCicloAtual.getOperadorAtualizacao());
        novoPerfilAtuacao.setUltimaAtualizacao(perfilAtuacaoCicloAtual.getUltimaAtualizacao());
        novoPerfilAtuacao.setOperadorEncaminhamento(perfilAtuacaoCicloAtual.getOperadorEncaminhamento());
        novoPerfilAtuacao.setDataEncaminhamento(perfilAtuacaoCicloAtual.getDataEncaminhamento());

        Documento novoDocumento = new Documento();
        novoDocumento.setJustificativa(perfilAtuacaoCicloAtual.getDocumento().getJustificativa());
        novoDocumento.setOperadorAtualizacao(perfilAtuacaoCicloAtual.getDocumento().getOperadorAtualizacao());
        novoDocumento.setUltimaAtualizacao(perfilAtuacaoCicloAtual.getDocumento().getUltimaAtualizacao());
        novoDocumento.setAlterarDataUltimaAtualizacao(false);
        novoPerfilAtuacao.setDocumento(novoDocumento);

        DocumentoMediator.get().salvar(novoDocumento);
        novoPerfilAtuacao.setAlterarDataUltimaAtualizacao(false);

        PerfilRiscoMediator.get().incluirVersaoPerfilRiscoAtual(novoCiclo, novoPerfilAtuacao,
                TipoObjetoVersionadorEnum.PERFIL_ATUACAO_ES);
        perfilAtuacaoESDAO.save(novoPerfilAtuacao);
        duplicarAnexosDocumento(novoCiclo, perfilAtuacaoCicloAtual.getDocumento(), novoPerfilAtuacao);
        perfilAtuacaoESDAO.evict(novoPerfilAtuacao);
    }

    @Transactional
    public void criarPerfilAtuacaoRascunho(PerfilAtuacaoES perfilAtuacaoCicloAtual) {
        perfilAtuacaoESDAO.evict(perfilAtuacaoCicloAtual);
        PerfilAtuacaoES novoPerfilAtuacao = new PerfilAtuacaoES();
        novoPerfilAtuacao.setCiclo(perfilAtuacaoCicloAtual.getCiclo());
        novoPerfilAtuacao.setPerfilAtuacaoESAnterior(perfilAtuacaoCicloAtual);
        novoPerfilAtuacao.setOperadorAtualizacao(perfilAtuacaoCicloAtual.getOperadorAtualizacao());
        novoPerfilAtuacao.setUltimaAtualizacao(perfilAtuacaoCicloAtual.getUltimaAtualizacao());
        novoPerfilAtuacao.setOperadorEncaminhamento(null);
        novoPerfilAtuacao.setDataEncaminhamento(null);
        Documento novoDocumento = new Documento();
        novoDocumento.setJustificativa(perfilAtuacaoCicloAtual.getDocumento().getJustificativa());
        novoDocumento.setOperadorAtualizacao(perfilAtuacaoCicloAtual.getDocumento().getOperadorAtualizacao());
        novoDocumento.setUltimaAtualizacao(perfilAtuacaoCicloAtual.getDocumento().getUltimaAtualizacao());
        novoDocumento.setAlterarDataUltimaAtualizacao(false);
        novoPerfilAtuacao.setDocumento(novoDocumento);

        DocumentoMediator.get().salvar(novoDocumento);
        novoPerfilAtuacao.setAlterarDataUltimaAtualizacao(false);
        novoPerfilAtuacao.setPendente(null);
        perfilAtuacaoESDAO.save(novoPerfilAtuacao);
        duplicarAnexosDocumento(perfilAtuacaoCicloAtual.getCiclo(), perfilAtuacaoCicloAtual.getDocumento(),
                novoPerfilAtuacao);

    }

    private void duplicarAnexosDocumento(Ciclo novoCiclo, Documento documentoAnterior, PerfilAtuacaoES novoPerfilAtuacao) {
        List<AnexoDocumento> anexosDocumentoAnterior = anexoDocumentoMediator.buscar(documentoAnterior);
        for (AnexoDocumento anexoDocumento : anexosDocumentoAnterior) {
            duplicarAnexo(novoCiclo, novoPerfilAtuacao, anexoDocumento, true);
        }
    }

    @Transactional
    public void setarPendenteNull(Ciclo ciclo) {
        PerfilAtuacaoES perfilAtuacaoESDB = getPerfilAtuacaoESSemPerfilRisco(ciclo);
        perfilAtuacaoESDB.setPendente(null);
        perfilAtuacaoESDAO.saveOrUpdate(perfilAtuacaoESDB);
    }

    public void evict(PerfilAtuacaoES perfil) {
        perfilAtuacaoESDAO.evict(perfil);
    }
    
 

    
    @Transactional
    public void rotinaBatchGerarPerfilsOpinioes() {
        
        Util.setBatch(Boolean.TRUE);
        
        System.out.println("ROTINA DE CRIAÇÃO DA ESTRUTURA DO BATCH DE PERFIL DE ATUAÇÃO E OPINIÃO CONCLUSIVA ");
        System.out.println("Iniciado em: " + DataUtil.getDateTimeAtual());
        List<Ciclo> ciclos = CicloMediator.get().consultarCiclosAndamentoCorec(); 
        for (Ciclo ciclo : ciclos) {
            System.out.println("CNPJ :  " + ciclo.getEntidadeSupervisionavel().getConglomeradoOuCnpj());
            EntidadeUnicad entidadeUnicad =
                    EntidadeUnicadMediator.get().buscarEntidadeUnicadPorCnpj(
                            ciclo.getEntidadeSupervisionavel().getConglomeradoOuCnpj());
            
           
           if (entidadeSupervisionavelMediator.possuiPermissaoGeracaoEventos(entidadeUnicad)) {
                System.out.println("GERAR PERFILS DE ATUAÇÃO PUBLICADO");
                
                PerfilRisco perfilRisco = PerfilRiscoMediator.get().obterPerfilRiscoAtual(ciclo.getPk());
                PerfilAtuacaoES perfilAtuacaoES = PerfilRiscoMediator.get().getPerfilAtuacaoESPerfilRisco(perfilRisco);
                
                while (perfilAtuacaoES != null) {
                   PerfilAtuacaoConsolidado perfilAtuacaoConsolidado =
                            PerfilAtuacaoConsolidadoMediator.get().criarPerfilAtuacaoRascunhoEDocumentoBatch(
                                    perfilAtuacaoES, entidadeUnicad);
                   eventoConsolidadoMediator.incluirEventoPerfilAtuacao(entidadeUnicad, perfilAtuacaoConsolidado, true);
                    if (perfilAtuacaoES.getPerfilAtuacaoESAnterior() != null
                            && perfilAtuacaoES.getCiclo() == perfilAtuacaoES.getPerfilAtuacaoESAnterior().getCiclo()) {
                        perfilAtuacaoES = perfilAtuacaoES.getPerfilAtuacaoESAnterior();
                    } else {
                        if (perfilAtuacaoES.getPerfilAtuacaoESAnterior() == null) {
                            perfilAtuacaoES = perfilAtuacaoES.getPerfilAtuacaoESAnterior();
                        } else {
                            perfilAtuacaoES = perfilAtuacaoES.getPerfilAtuacaoESAnterior().getPerfilAtuacaoESAnterior();
                        }
                    }
                }
                
                ConclusaoES conclusaoES = PerfilRiscoMediator.get().getConclusaoESPerfilRisco(perfilRisco);
                
                while (conclusaoES != null) {
                        OpiniaoConclusivaConsolidado opiniaoConclusivaConsolidado =
                                OpiniaoConclusivaConsolidadoMediator.get().criarOpiniaoConclusivaRascunhoEDocumentoBatch(
                                        conclusaoES, entidadeUnicad, true);
                        eventoConsolidadoMediator.incluirEventoOpiniao(entidadeUnicad,
                                opiniaoConclusivaConsolidado, true);
                        if (conclusaoES.getConclusaoESAnterior() != null
                                && conclusaoES.getCiclo() == conclusaoES.getConclusaoESAnterior().getCiclo()) {
                            conclusaoES = conclusaoES.getConclusaoESAnterior();
                        } else {
                            if (conclusaoES.getConclusaoESAnterior() == null) {
                                conclusaoES = conclusaoES.getConclusaoESAnterior();
                            } else {
                                conclusaoES = conclusaoES.getConclusaoESAnterior().getConclusaoESAnterior();
                            }
                        }
                }
            }
        }
    }
    
    @Transactional(readOnly = true)
    public List<PerfilAtuacaoES> buscarTodosPerfisPublicados() {
        return perfilAtuacaoESDAO.buscarTodosPerfisPublicados();
    }
    
    @Transactional
    public void update(PerfilAtuacaoES perfilAtuacaoES) {
        perfilAtuacaoESDAO.update(perfilAtuacaoES);
        perfilAtuacaoESDAO.getSessionFactory().getCurrentSession().flush();
    }


}
