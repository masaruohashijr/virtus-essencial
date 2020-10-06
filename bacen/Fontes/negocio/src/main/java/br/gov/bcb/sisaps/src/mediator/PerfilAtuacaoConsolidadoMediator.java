package br.gov.bcb.sisaps.src.mediator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.seguranca.UsuarioCorrente;
import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.src.dao.PerfilAtuacaoConsolidadoDao;
import br.gov.bcb.sisaps.src.dominio.DocumentoAPS;
import br.gov.bcb.sisaps.src.dominio.EntidadeUnicad;
import br.gov.bcb.sisaps.src.dominio.PerfilAtuacaoConsolidado;
import br.gov.bcb.sisaps.src.dominio.PerfilAtuacaoES;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoEntidadeAnexoDocumentoEnum;
import br.gov.bcb.sisaps.util.Util;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import br.gov.bcb.sisaps.util.enumeracoes.TipoStatusDocumento;
import br.gov.bcb.sisaps.util.geral.DataUtil;

@Service
@Transactional(readOnly = true)
public class PerfilAtuacaoConsolidadoMediator {
    
    @Autowired
    private DocumentoMediatorAPS documentoMediatorAPS;

    @Autowired
    private PerfilAtuacaoConsolidadoDao perfilAtuacaoConsolidadoDao;
    
    @Autowired
    private AnexoDocumentoApsMediator anexoDocumentoApsMediator;
    
    @Autowired
    private BloqueioESMediator bloqueioESMediator;
    
    
    public static PerfilAtuacaoConsolidadoMediator get() {
        return SpringUtils.get().getBean(PerfilAtuacaoConsolidadoMediator.class);
    }
    
    
    @Transactional
    public PerfilAtuacaoConsolidado criarPerfilAtuacaoRascunhoEDocumento(PerfilAtuacaoES perfilAtuacaoESDB,
            EntidadeUnicad entidadeUnicad, PerfilAtuacaoConsolidado perfilPublicado) {
        PerfilAtuacaoConsolidado rascunho = criarPerfilRascunho(perfilAtuacaoESDB, entidadeUnicad, false);
        criarDocumentoAps(perfilAtuacaoESDB, rascunho, perfilPublicado, false);
        setPerfilPosterior(perfilPublicado, rascunho);
        return rascunho;
    }
    
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public PerfilAtuacaoConsolidado criarPerfilAtuacaoRascunhoEDocumentoBatch(PerfilAtuacaoES perfilAtuacaoESDB,
            EntidadeUnicad entidadeUnicad) {
        PerfilAtuacaoConsolidado perfilPublicado = buscarPublicadoBatch(entidadeUnicad.getConsolidado().getPk());
        PerfilAtuacaoConsolidado rascunho = criarPerfilRascunho(perfilAtuacaoESDB, entidadeUnicad, true);
        criarDocumentoAps(perfilAtuacaoESDB, rascunho, perfilPublicado, true);
        rascunho.setPerfilPosterior(perfilPublicado);
        perfilAtuacaoConsolidadoDao.saveOrUpdate(rascunho);
        perfilAtuacaoConsolidadoDao.flush();
        return rascunho;
    }


    private void setPerfilPosterior(PerfilAtuacaoConsolidado perfilPublicado, PerfilAtuacaoConsolidado rascunho) {
        perfilAtuacaoConsolidadoDao.saveOrUpdate(rascunho);
        if (perfilPublicado != null && perfilPublicado.getPerfilPosterior() == null) {
            perfilPublicado.setPerfilPosterior(rascunho);
            perfilAtuacaoConsolidadoDao.saveOrUpdate(perfilPublicado);
            perfilAtuacaoConsolidadoDao.flush();
        }
    }


    private void criarDocumentoAps(PerfilAtuacaoES perfilAtuacaoESDB, PerfilAtuacaoConsolidado rascunho,
            PerfilAtuacaoConsolidado perfilPublicado, boolean isBatch) {
        DocumentoAPS novoDocumento = new DocumentoAPS();
        novoDocumento.setJustificativa(perfilAtuacaoESDB.getDocumento().getJustificativa());
        if (isBatch) {
            novoDocumento.setUltimaAtualizacao((perfilAtuacaoESDB.getDocumento().getUltimaAtualizacao()));
            novoDocumento.setOperadorAtualizacao(perfilAtuacaoESDB.getDocumento().getOperadorAtualizacao());
            novoDocumento.setAlterarDataUltimaAtualizacao(false);
            documentoMediatorAPS.salvarAtualizar(novoDocumento);
        } else {
            documentoMediatorAPS.salvar(novoDocumento);
        }
        anexoDocumentoApsMediator.duplicarAnexoDocumento(perfilAtuacaoESDB.getCiclo(),
                TipoEntidadeAnexoDocumentoEnum.PERFIL_ATUACAO, perfilAtuacaoESDB, perfilAtuacaoESDB.getDocumento(),
                novoDocumento, false, isBatch);
        rascunho.setDocumento(novoDocumento);
    }


    private PerfilAtuacaoConsolidado criarPerfilRascunho(PerfilAtuacaoES perfilAtuacaoESDB,
            EntidadeUnicad entidadeUnicad, boolean isBatch) {
        PerfilAtuacaoConsolidado rascunho = new PerfilAtuacaoConsolidado();
        if (bloqueioESMediator.isESBloqueada(entidadeUnicad.getCnpjConglomerado())) {
            rascunho.setBloqueado(SimNaoEnum.SIM);
        } else {
            rascunho.setBloqueado(SimNaoEnum.NAO);
        }
        rascunho.setConsolidado(entidadeUnicad.getConsolidado());
        rascunho.setStatus(TipoStatusDocumento.PUBLICADO);
        if (isBatch) {
            rascunho.setDataEncaminhamento(perfilAtuacaoESDB.getDataEncaminhamento());
            rascunho.setDataPublicacao(perfilAtuacaoESDB.getUltimaAtualizacao());
            rascunho.setUltimaAtualizacao(perfilAtuacaoESDB.getUltimaAtualizacao());
            rascunho.setDataAlteracao(perfilAtuacaoESDB.getUltimaAtualizacao());
            rascunho.setOperadorAtualizacao(perfilAtuacaoESDB.getOperadorAtualizacao());
        } else {
            rascunho.setDataEncaminhamento(Util.ultimaHora(perfilAtuacaoESDB.getDataEncaminhamento().toDate()));
            rascunho.setDataPublicacao(Util.ultimaHora(perfilAtuacaoESDB.getUltimaAtualizacao().toDate()));
            rascunho.setUltimaAtualizacao(Util.ultimaHora(DataUtil.getDateTimeAtual().toDate()));
            rascunho.setDataAlteracao(Util.ultimaHora(DataUtil.getDateTimeAtual().toDate()));
            rascunho.setOperadorAtualizacao(UsuarioCorrente.get().getLogin()); 
        }
        rascunho.setAlterarDataUltimaAtualizacao(false);
        rascunho.setOperadorAlteracao(perfilAtuacaoESDB.getOperadorAtualizacao());
        rascunho.setOperadorPublicacao(perfilAtuacaoESDB.getOperadorAtualizacao());
        rascunho.setOperadorEncaminhamento(perfilAtuacaoESDB.getOperadorEncaminhamento());
        return rascunho;
    }


    public PerfilAtuacaoConsolidado buscarPublicado(Integer pk) {
        return perfilAtuacaoConsolidadoDao.buscarPublicado(pk);
    }

    public PerfilAtuacaoConsolidado buscarPublicadoBatch(Integer pk) {
        return perfilAtuacaoConsolidadoDao.buscarPublicadoBatch(pk);
    }

    public PerfilAtuacaoConsolidado buscarEmEdicao(Integer pk) {
        return perfilAtuacaoConsolidadoDao.buscarEmEdicao(pk);
    }

    public PerfilAtuacaoConsolidado buscarEmHomologacao(Integer pk) {
        return perfilAtuacaoConsolidadoDao.buscarEmHomologacao(pk);
    }

    @Transactional
    public void delete(PerfilAtuacaoConsolidado perfilAtuacao) {
        perfilAtuacaoConsolidadoDao.delete(perfilAtuacao);
        documentoMediatorAPS.deletar(perfilAtuacao.getDocumento());
    }
}
