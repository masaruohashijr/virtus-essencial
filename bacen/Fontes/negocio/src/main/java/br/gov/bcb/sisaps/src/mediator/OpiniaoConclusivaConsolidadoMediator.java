package br.gov.bcb.sisaps.src.mediator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.seguranca.UsuarioCorrente;
import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.src.dao.OpiniaoConclusivaConsolidadoDao;
import br.gov.bcb.sisaps.src.dominio.ConclusaoES;
import br.gov.bcb.sisaps.src.dominio.DocumentoAPS;
import br.gov.bcb.sisaps.src.dominio.EntidadeUnicad;
import br.gov.bcb.sisaps.src.dominio.OpiniaoConclusivaConsolidado;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoEntidadeAnexoDocumentoEnum;
import br.gov.bcb.sisaps.util.Util;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import br.gov.bcb.sisaps.util.enumeracoes.TipoStatusDocumento;
import br.gov.bcb.sisaps.util.geral.DataUtil;

@Service
@Transactional(readOnly = true)
public class OpiniaoConclusivaConsolidadoMediator {
    
    @Autowired
    private OpiniaoConclusivaConsolidadoDao opiniaoConclusivaConsolidadoDao;
    
    @Autowired
    private BloqueioESMediator bloqueioESMediator;
    
    @Autowired
    private DocumentoMediatorAPS documentoMediatorAPS;
    
    @Autowired
    private AnexoDocumentoApsMediator anexoDocumentoApsMediator;
    
    public static OpiniaoConclusivaConsolidadoMediator get() {
        return SpringUtils.get().getBean(OpiniaoConclusivaConsolidadoMediator.class);
    }
    
    @Transactional
    public OpiniaoConclusivaConsolidado criarOpiniaoConclusivaRascunhoEDocumento(ConclusaoES conclusaoEsDB,
            EntidadeUnicad entidadeUnicad, OpiniaoConclusivaConsolidado opiniaoPublicada, boolean isBatch) {
        OpiniaoConclusivaConsolidado rascunho = criarOpiniaoRascunho(conclusaoEsDB, entidadeUnicad, isBatch);
        criarDocumentoAps(conclusaoEsDB, rascunho, opiniaoPublicada, isBatch);
        setPerfilPosterior(opiniaoPublicada, rascunho);
        return rascunho;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public OpiniaoConclusivaConsolidado criarOpiniaoConclusivaRascunhoEDocumentoBatch(ConclusaoES conclusaoEsDB,
            EntidadeUnicad entidadeUnicad, boolean isBatch) {
        OpiniaoConclusivaConsolidado opiniaoPublicada =
                opiniaoConclusivaConsolidadoDao.buscarPublicadoBatch(entidadeUnicad.getConsolidado().getPk());
        OpiniaoConclusivaConsolidado rascunho = criarOpiniaoRascunho(conclusaoEsDB, entidadeUnicad, isBatch);
        criarDocumentoAps(conclusaoEsDB, rascunho, opiniaoPublicada, isBatch);
        rascunho.setOpiniaoPosterior(opiniaoPublicada);
        opiniaoConclusivaConsolidadoDao.saveOrUpdate(rascunho);
        opiniaoConclusivaConsolidadoDao.flush();
        return rascunho;
    }


    private void setPerfilPosterior(OpiniaoConclusivaConsolidado opiniaoPublicada, OpiniaoConclusivaConsolidado rascunho) {
        opiniaoConclusivaConsolidadoDao.saveOrUpdate(rascunho);
        if (opiniaoPublicada != null && opiniaoPublicada.getOpiniaoPosterior() == null) {
            opiniaoPublicada.setOpiniaoPosterior(rascunho);
            opiniaoConclusivaConsolidadoDao.saveOrUpdate(opiniaoPublicada);
            opiniaoConclusivaConsolidadoDao.flush();
        }
    }


    private void criarDocumentoAps(ConclusaoES conclusaoEsDB, OpiniaoConclusivaConsolidado rascunho,
            OpiniaoConclusivaConsolidado perfilPublicado, boolean isBatch) {
        DocumentoAPS novoDocumento = new DocumentoAPS();
        novoDocumento.setJustificativa(conclusaoEsDB.getDocumento().getJustificativa());
        if (isBatch) {
            novoDocumento.setUltimaAtualizacao((conclusaoEsDB.getDocumento().getUltimaAtualizacao()));
            novoDocumento.setOperadorAtualizacao(conclusaoEsDB.getDocumento().getOperadorAtualizacao());
            novoDocumento.setAlterarDataUltimaAtualizacao(false);
            documentoMediatorAPS.salvarAtualizar(novoDocumento);
        } else {
            documentoMediatorAPS.salvar(novoDocumento);
        }
        anexoDocumentoApsMediator.duplicarAnexoDocumento(conclusaoEsDB.getCiclo(),
                TipoEntidadeAnexoDocumentoEnum.CONCLUSAO, conclusaoEsDB, conclusaoEsDB.getDocumento(),
                novoDocumento, false, isBatch);
        rascunho.setDocumento(novoDocumento);
    }


    private OpiniaoConclusivaConsolidado criarOpiniaoRascunho(ConclusaoES conclusaoEsDB,
            EntidadeUnicad entidadeUnicad, boolean isBatch) {
        OpiniaoConclusivaConsolidado rascunho = new OpiniaoConclusivaConsolidado();
        if (bloqueioESMediator.isESBloqueada(entidadeUnicad.getCnpjConglomerado())) {
            rascunho.setBloqueado(SimNaoEnum.SIM);
        } else {
            rascunho.setBloqueado(SimNaoEnum.NAO);
        }
        rascunho.setStatus(TipoStatusDocumento.PUBLICADO);
        rascunho.setConsolidado(entidadeUnicad.getConsolidado());
        if (isBatch) {
            rascunho.setDataEncaminhamento(conclusaoEsDB.getDataEncaminhamento());
            rascunho.setDataPublicacao(conclusaoEsDB.getUltimaAtualizacao());
            rascunho.setUltimaAtualizacao(conclusaoEsDB.getUltimaAtualizacao());
            rascunho.setDataAlteracao(conclusaoEsDB.getUltimaAtualizacao());
            rascunho.setOperadorAtualizacao(conclusaoEsDB.getOperadorAtualizacao());
        } else {
            rascunho.setDataEncaminhamento(Util.ultimaHora(conclusaoEsDB.getDataEncaminhamento().toDate()));
            rascunho.setDataPublicacao(Util.ultimaHora(conclusaoEsDB.getUltimaAtualizacao().toDate()));
            rascunho.setUltimaAtualizacao(Util.ultimaHora(DataUtil.getDateTimeAtual().toDate()));
            rascunho.setDataAlteracao(Util.ultimaHora(DataUtil.getDateTimeAtual().toDate()));
            rascunho.setOperadorAtualizacao(UsuarioCorrente.get().getLogin());
        }
        rascunho.setAlterarDataUltimaAtualizacao(false);
        rascunho.setOperadorAlteracao(conclusaoEsDB.getOperadorAtualizacao());
        rascunho.setOperadorPublicacao(conclusaoEsDB.getOperadorAtualizacao());
        rascunho.setOperadorEncaminhamento(conclusaoEsDB.getOperadorEncaminhamento());
        return rascunho;
    }


    public OpiniaoConclusivaConsolidado buscarPublicado(Integer pk) {
        return opiniaoConclusivaConsolidadoDao.buscarPublicado(pk);
    }

    public OpiniaoConclusivaConsolidado buscarPublicadoBatch(Integer pk) {
        return opiniaoConclusivaConsolidadoDao.buscarPublicadoBatch(pk);
    }

    public OpiniaoConclusivaConsolidado buscarEmEdicao(Integer pk) {
        return opiniaoConclusivaConsolidadoDao.buscarEmEdicao(pk);
    }

    public OpiniaoConclusivaConsolidado buscarEmHomologacao(Integer pk) {
        return opiniaoConclusivaConsolidadoDao.buscarEmHomologacao(pk);
    }

    @Transactional
    public void delete(OpiniaoConclusivaConsolidado opiniaoConclusiva) {
        opiniaoConclusivaConsolidadoDao.delete(opiniaoConclusiva);
        documentoMediatorAPS.deletar(opiniaoConclusiva.getDocumento());
    }
}
