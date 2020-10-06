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
import br.gov.bcb.sisaps.src.dao.ConclusaoESDAO;
import br.gov.bcb.sisaps.src.dominio.AnexoDocumento;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.ConclusaoES;
import br.gov.bcb.sisaps.src.dominio.Documento;
import br.gov.bcb.sisaps.src.dominio.EntidadeUnicad;
import br.gov.bcb.sisaps.src.dominio.OpiniaoConclusivaConsolidado;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.VersaoPerfilRisco;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoEntidadeAnexoDocumentoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoObjetoVersionadorEnum;
import br.gov.bcb.sisaps.src.util.BufferAnexos;
import br.gov.bcb.sisaps.src.validacao.RegraSalvarConclusaoES;
import br.gov.bcb.sisaps.src.vo.AnexoDocumentoVo;
import br.gov.bcb.sisaps.util.Util;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import br.gov.bcb.sisaps.util.enumeracoes.TipoSubEventoPerfilRiscoSRC;
import br.gov.bcb.sisaps.util.geral.DataUtil;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;

@Service
@Transactional(readOnly = true)
public class ConclusaoESMediator {

    @Autowired
    private ConclusaoESDAO conclusaoESDAO;

    @Autowired
    private PerfilRiscoMediator perfilRiscoMediator;

    @Autowired
    private DocumentoMediator documentoMediator;

    @Autowired
    private AnexoDocumentoMediator anexoDocumentoMediator;
    
    @Autowired
    private EntidadeUnicadMediator entidadeUnicadMediator;

    @Autowired
    private OpiniaoConclusivaConsolidadoMediator opiniaoConclusivaConsolidadoMediator;
    
    @Autowired
    private EventoConsolidadoMediator eventoConsolidadoMediator;
    
    @Autowired
    private EntidadeSupervisionavelMediator entidadeSupervisionavelMediator;

    public static ConclusaoESMediator get() {
        return SpringUtils.get().getBean(ConclusaoESMediator.class);
    }

    public ConclusaoES buscarPorPk(Integer pk) {
        ConclusaoES conclusao = conclusaoESDAO.buscarConclusaoESPorPk(pk);
        inicializar(conclusao);
        if (conclusao.getCiclo() != null) {
            Hibernate.initialize(conclusao.getCiclo());
        }
        return conclusao;
    }

    public ConclusaoES getUltimaConclusaoES(Ciclo ciclo) {
        return conclusaoESDAO.getUltimaConclusaoES(ciclo);
    }

    @Transactional(readOnly = true)
    public ConclusaoES getConclusaoESPorPerfil(int perfilPK) {
        ConclusaoES ultimoConclusaoES = conclusaoESDAO.buscarPorPerfilRisco(perfilPK);
        if (ultimoConclusaoES != null) {
        	ultimoConclusaoES.setAlterarDataUltimaAtualizacao(false);
        }
        inicializar(ultimoConclusaoES);
        return ultimoConclusaoES;
    }

    private void inicializar(ConclusaoES ultimoConclusaoES) {
        if (ultimoConclusaoES != null) {
            Hibernate.initialize(ultimoConclusaoES.getDocumento());
            if (ultimoConclusaoES.getConclusaoESAnterior() != null) {
                Hibernate.initialize(ultimoConclusaoES.getConclusaoESAnterior());
                Hibernate.initialize(ultimoConclusaoES.getConclusaoESAnterior().getDocumento());
            }
        }
    }

    @Transactional(readOnly = true)
    public ConclusaoES getConclusaoESSemPerfilRisco(Ciclo ciclo) {
        ConclusaoES ultimoconclusaoES = conclusaoESDAO.buscarSemPerfil(ciclo);
        inicializar(ultimoconclusaoES);
        return ultimoconclusaoES;
    }

    @Transactional(readOnly = true)
    public ConclusaoES getConclusaoESPendencia(Ciclo ciclo) {
        ConclusaoES ultimoConclusaoES = conclusaoESDAO.buscarPorPendencia(ciclo);
        inicializar(ultimoConclusaoES);
        return ultimoConclusaoES;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public String salvarNovaConclusao(ConclusaoES conclusaoES, boolean isSalvar) {
        new RegraSalvarConclusaoES(conclusaoES).validar();
        if (SisapsUtil.isTextoCKEditorBrancoOuNulo(conclusaoES.getDocumento().getJustificativa())) {
            conclusaoES.getDocumento().setJustificativa(null);
        }
        if (isSalvar) {
            conclusaoES.setPendente(SimNaoEnum.NAO);
            conclusaoES.setDataEncaminhamento(null);
            conclusaoES.setOperadorEncaminhamento(null);
        }
        documentoMediator.salvar(conclusaoES.getDocumento());
        conclusaoESDAO.saveOrUpdate(conclusaoES);
        conclusaoESDAO.getSessionFactory().getCurrentSession().flush();
        return "Conclusão salva com sucesso.";
    }

    @Transactional
    public String confirmarNovaConclusao(ConclusaoES conclusaoES) {
        Util.setIncluirBufferAnexos(true);
        BufferAnexos.resetLocalThreadBufferInclusao();
        ConclusaoES conclusaoESDB = conclusaoESDAO.buscarConclusaoESPorPk(conclusaoES.getPk());
        validarNovaConclusao(conclusaoESDB);
        conclusaoESDB.setPendente(null);
        VersaoPerfilRisco versaoPerfilRisco = perfilRiscoMediator.gerarNovaVersaoPerfilRisco(conclusaoESDB.getCiclo(),
                conclusaoESDB.getConclusaoESAnterior(), TipoObjetoVersionadorEnum.CONCLUSAO_ES);
        conclusaoESDB.setVersaoPerfilRisco(versaoPerfilRisco);
        conclusaoESDAO.saveOrUpdate(conclusaoESDB);
        conclusaoESDAO.getSessionFactory().getCurrentSession().flush();
        criarConclusaoRascunho(conclusaoESDB);

        EntidadeUnicad entidadeUnicad = null;
        //criar sysout para testar
        if (conclusaoESDB.getCiclo() != null) {
            entidadeUnicad = entidadeUnicadMediator.buscarEntidadeUnicadPorCnpj(
                    conclusaoESDB.getCiclo().getEntidadeSupervisionavel().getConglomeradoOuCnpj());
        }

        if (entidadeSupervisionavelMediator.possuiPermissaoGeracaoEventos(entidadeUnicad)) {
            OpiniaoConclusivaConsolidado opiniaoEmEdicao =
                    opiniaoConclusivaConsolidadoMediator.buscarEmEdicao(entidadeUnicad.getConsolidado().getPk());
            OpiniaoConclusivaConsolidado opiniaoEmHomologacao =
                    opiniaoConclusivaConsolidadoMediator.buscarEmHomologacao(entidadeUnicad.getConsolidado().getPk());
            if (opiniaoEmEdicao != null) {
                opiniaoConclusivaConsolidadoMediator.delete(opiniaoEmEdicao);
            }

            if (opiniaoEmHomologacao != null) {
                opiniaoConclusivaConsolidadoMediator.delete(opiniaoEmHomologacao);
            }

            OpiniaoConclusivaConsolidado opiniaoPublicada =
                    opiniaoConclusivaConsolidadoMediator.buscarPublicado(entidadeUnicad.getConsolidado().getPk());

            eventoConsolidadoMediator.incluirEventoPerfilDeRisco(conclusaoESDB.getCiclo(),
                    TipoSubEventoPerfilRiscoSRC.CONCLUSAO);

            OpiniaoConclusivaConsolidado opiniaoConclusivaConsolidado = opiniaoConclusivaConsolidadoMediator
                    .criarOpiniaoConclusivaRascunhoEDocumento(conclusaoESDB, entidadeUnicad, opiniaoPublicada, false);

            eventoConsolidadoMediator.incluirEventoOpiniao(entidadeUnicad, opiniaoConclusivaConsolidado, false);

        }

        GeradorAnexoMediator.get().incluirAnexosBuffer();
        return "Atualização da Conclusão da ES no perfil de risco realizada com sucesso.";
    }

    @Transactional
    public String encaminharNovaConclusao(ConclusaoES conclusaoES) {
        validarNovaConclusao(conclusaoES);
        UsuarioAplicacao usuarioAplicacao = (UsuarioAplicacao) UsuarioCorrente.get();
        conclusaoES.setPendente(SimNaoEnum.SIM);
        conclusaoES.setDataEncaminhamento(DataUtil.getDateTimeAtual());
        conclusaoES.setOperadorEncaminhamento(usuarioAplicacao.getLogin());
        conclusaoESDAO.saveOrUpdate(conclusaoES);
        return "Conclusão da ES encaminhado(a) para aprovação do gerente com sucesso.";
    }

    private void validarNovaConclusao(ConclusaoES conclusaoES) {
        ArrayList<ErrorMessage> erros = new ArrayList<ErrorMessage>();
        SisapsUtil.validarObrigatoriedade(conclusaoES.getDocumento().getJustificativa(), "Conclusão", erros);
        SisapsUtil.lancarNegocioException(erros);
    }

    public ConclusaoES obterConclusaoESPorDocumento(Documento documento) {
        return conclusaoESDAO.obterConclusaoESPorDocumento(documento);
    }

    @Transactional
    public void salvarEDuplicarAnexosConclusaoAnterior(Ciclo ciclo, ConclusaoES conclusaoES, boolean isSalvar) {
        salvarNovaConclusao(conclusaoES, isSalvar);
        duplicarAnexosConclusaoAnterior(ciclo, conclusaoES, null);
    }

    @Transactional
    public void salvarAnexo(Ciclo ciclo, ConclusaoES conclusaoES, String clientFileName, InputStream inputStream,
            boolean isSalvar) {
        Util.setIncluirBufferAnexos(true);
        BufferAnexos.resetLocalThreadBufferInclusao();
        salvarNovaConclusao(conclusaoES, isSalvar);
        anexoDocumentoMediator.anexarArquivo(ciclo, conclusaoES, conclusaoES.getDocumento(),
                TipoEntidadeAnexoDocumentoEnum.CONCLUSAO, clientFileName, inputStream, false);
        GeradorAnexoMediator.get().incluirAnexosBuffer();
    }

    @Transactional
    public void excluirAnexo(AnexoDocumentoVo anexoVo, ConclusaoES conclusaoES, Ciclo ciclo, boolean isSalvar) {
        salvarNovaConclusao(conclusaoES, isSalvar);
        anexoDocumentoMediator.excluirAnexo(anexoVo, conclusaoES, TipoEntidadeAnexoDocumentoEnum.CONCLUSAO, ciclo);
    }

    private void duplicarAnexosConclusaoAnterior(Ciclo ciclo, ConclusaoES conclusaoES, Integer pkAnexoNaoDuplicar) {
        if (conclusaoES.getConclusaoESAnterior() != null) {
            List<AnexoDocumento> anexos =
                    anexoDocumentoMediator.buscar(conclusaoES.getConclusaoESAnterior().getDocumento());
            for (AnexoDocumento anexo : anexos) {
                if (!anexo.getPk().equals(pkAnexoNaoDuplicar)) {
                    duplicarAnexo(ciclo, conclusaoES, anexo, false);
                }
            }
        }
    }

    private void duplicarAnexo(Ciclo ciclo, ConclusaoES conclusaoESNovo, AnexoDocumento anexoAnterior,
            boolean isCopiarUsuarioAnterior) {
        ByteArrayInputStream inputStream = null;
        if (!SisapsUtil.isExecucaoTeste()) {
            byte[] arquivoAnexo =
                    anexoDocumentoMediator.recuperarArquivo(anexoAnterior.getLink(),
                            conclusaoESNovo.getConclusaoESAnterior(), TipoEntidadeAnexoDocumentoEnum.CONCLUSAO,
                            conclusaoESNovo.getConclusaoESAnterior().getCiclo());
            inputStream = new ByteArrayInputStream(arquivoAnexo);
        }
        AnexoDocumento novoAnexo =
                anexoDocumentoMediator.anexarArquivo(ciclo, conclusaoESNovo, conclusaoESNovo.getDocumento(),
                        TipoEntidadeAnexoDocumentoEnum.CONCLUSAO, anexoAnterior.getLink(), inputStream, true);
        if (isCopiarUsuarioAnterior) {
            novoAnexo.setAlterarDataUltimaAtualizacao(false);
            novoAnexo.setUltimaAtualizacao(anexoAnterior.getUltimaAtualizacao());
            novoAnexo.setOperadorAtualizacao(anexoAnterior.getOperadorAtualizacao());
            AnexoDocumentoMediator.get().saveOrUpdate(novoAnexo);
        }
    }

    public ConclusaoES buscarPorPerfilRisco(Integer pkPerfilRisco) {
        return conclusaoESDAO.buscarPorPerfilRisco(pkPerfilRisco);
    }

    public ConclusaoES buscarConclusaoESRascunho(Integer pkCiclo) {
        return conclusaoESDAO.buscarConclusaoESRascunho(pkCiclo);
    }

    @Transactional
    public void criarConclusaoNovoCiclo(PerfilRisco perfilRiscoCicloAtual, Ciclo novoCiclo) {
        ConclusaoES conclusaoCicloAtual = buscarPorPerfilRisco(perfilRiscoCicloAtual.getPk());
        conclusaoESDAO.evict(conclusaoCicloAtual);
        ConclusaoES novaConclusao = new ConclusaoES();
        novaConclusao.setCiclo(novoCiclo);
        novaConclusao.setConclusaoESAnterior(conclusaoCicloAtual);
        novaConclusao.setOperadorAtualizacao(conclusaoCicloAtual.getOperadorAtualizacao());
        novaConclusao.setUltimaAtualizacao(conclusaoCicloAtual.getUltimaAtualizacao());
        novaConclusao.setOperadorEncaminhamento(conclusaoCicloAtual.getOperadorEncaminhamento());
        novaConclusao.setDataEncaminhamento(conclusaoCicloAtual.getDataEncaminhamento());

        Documento novoDocumento = new Documento();
        novoDocumento.setJustificativa(conclusaoCicloAtual.getDocumento().getJustificativa());
        novoDocumento.setOperadorAtualizacao(conclusaoCicloAtual.getDocumento().getOperadorAtualizacao());
        novoDocumento.setUltimaAtualizacao(conclusaoCicloAtual.getDocumento().getUltimaAtualizacao());
        novoDocumento.setAlterarDataUltimaAtualizacao(false);
        novaConclusao.setDocumento(novoDocumento);

        DocumentoMediator.get().salvar(novoDocumento);
        novaConclusao.setAlterarDataUltimaAtualizacao(false);
        PerfilRiscoMediator.get().incluirVersaoPerfilRiscoAtual(novoCiclo, novaConclusao,
                TipoObjetoVersionadorEnum.CONCLUSAO_ES);
        conclusaoESDAO.save(novaConclusao);
        duplicarAnexosDocumento(novoCiclo, conclusaoCicloAtual.getDocumento(), novaConclusao);
        conclusaoESDAO.evict(novaConclusao);
    }

    private void duplicarAnexosDocumento(Ciclo novoCiclo, Documento documentoAnterior, ConclusaoES novaConclusao) {
        List<AnexoDocumento> anexosDocumentoAnterior = anexoDocumentoMediator.buscar(documentoAnterior);
        for (AnexoDocumento anexoDocumento : anexosDocumentoAnterior) {
            duplicarAnexo(novoCiclo, novaConclusao, anexoDocumento, true);
        }
    }

    @Transactional
    public void criarConclusaoRascunho(ConclusaoES conclusaoCicloAtual) {
        conclusaoESDAO.evict(conclusaoCicloAtual);
        ConclusaoES novaConclusao = new ConclusaoES();
        novaConclusao.setCiclo(conclusaoCicloAtual.getCiclo());
        novaConclusao.setConclusaoESAnterior(conclusaoCicloAtual);
        novaConclusao.setOperadorAtualizacao(conclusaoCicloAtual.getOperadorAtualizacao());
        novaConclusao.setUltimaAtualizacao(conclusaoCicloAtual.getUltimaAtualizacao());
        novaConclusao.setOperadorEncaminhamento(null);
        novaConclusao.setDataEncaminhamento(null);
        Documento novoDocumento = new Documento();
        novoDocumento.setJustificativa(conclusaoCicloAtual.getDocumento().getJustificativa());
        novoDocumento.setOperadorAtualizacao(conclusaoCicloAtual.getDocumento().getOperadorAtualizacao());
        novoDocumento.setUltimaAtualizacao(conclusaoCicloAtual.getDocumento().getUltimaAtualizacao());
        novoDocumento.setAlterarDataUltimaAtualizacao(false);
        novaConclusao.setDocumento(novoDocumento);
        DocumentoMediator.get().salvar(novoDocumento);
        novaConclusao.setPendente(null);
        novaConclusao.setAlterarDataUltimaAtualizacao(false);
        conclusaoESDAO.save(novaConclusao);
        duplicarAnexosDocumento(conclusaoCicloAtual.getCiclo(), conclusaoCicloAtual.getDocumento(), novaConclusao);
    }

    @Transactional
    public void setarPendenteNull(Ciclo ciclo) {
        ConclusaoES conclusao = getConclusaoESSemPerfilRisco(ciclo);
        conclusao.setPendente(null);
        conclusaoESDAO.saveOrUpdate(conclusao);
    }
    
    
    public void evict(ConclusaoES conclusao) {
        conclusaoESDAO.evict(conclusao);
    }

    @Transactional(readOnly = true)
    public List<ConclusaoES> buscarTodasOpinioesPublicadas() {
        return conclusaoESDAO.buscarTodasOpinioesPublicadas();
    }

    @Transactional
    public void update(ConclusaoES conclusao) {
        conclusaoESDAO.update(conclusao);
        conclusaoESDAO.getSessionFactory().getCurrentSession().flush();
    }

}
