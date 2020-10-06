package br.gov.bcb.sisaps.src.mediator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.src.dao.DocumentoDAO;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Documento;
import br.gov.bcb.sisaps.src.dominio.ItemElemento;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ItemElementoAQT;

@Service
@Transactional(readOnly = true)
public class DocumentoMediator {
    
    @Autowired
    private DocumentoDAO documentoDAO;
    
    public static DocumentoMediator get() {
        return SpringUtils.get().getBean(DocumentoMediator.class);
    }
    
    @Transactional
    public Documento duplicarDocumentoItemElementoARCConclusaoAnalise(ItemElemento itemElemento,
            ItemElemento novoItemElemento, boolean isCopiarUsuarioAnterior) {
        Documento novoDocumento = null;
        if (itemElemento.getDocumento() != null) {
            novoDocumento = new Documento();
            novoDocumento.setJustificativa(itemElemento.getDocumento().getJustificativa());
            if (isCopiarUsuarioAnterior) {
                novoDocumento.setUltimaAtualizacao(itemElemento.getDocumento().getUltimaAtualizacao());
                novoDocumento.setOperadorAtualizacao(itemElemento.getDocumento().getOperadorAtualizacao());
                novoDocumento.setAlterarDataUltimaAtualizacao(false);
            }
            documentoDAO.save(novoDocumento);
        }
        return novoDocumento;
    }
    
    @Transactional
    public Documento duplicarDocumentoItemElementoAQTConclusaoAnalise(Ciclo ciclo, ItemElementoAQT itemElemento,
            ItemElementoAQT novoItemElemento, boolean isConclusaoCorec) {
        Documento novoDocumento = null;
        if (itemElemento.getDocumento() != null) {
            novoDocumento = new Documento();
            novoDocumento.setJustificativa(itemElemento.getDocumento().getJustificativa());
            if (isConclusaoCorec) {
                novoDocumento.setUltimaAtualizacao(itemElemento.getDocumento().getUltimaAtualizacao());
                novoDocumento.setOperadorAtualizacao(itemElemento.getDocumento().getOperadorAtualizacao());
                novoDocumento.setAlterarDataUltimaAtualizacao(false);
            }
            documentoDAO.save(novoDocumento);
        }
        return novoDocumento;
    }
    
    @Transactional
    public void salvar(Documento documento) {
        if (documento.getPk() == null) {
            documentoDAO.save(documento);
        } else {
            documentoDAO.merge(documento);
        }
        documentoDAO.getSessionFactory().getCurrentSession().flush();
    }
    
    public Documento load(Documento documento) {
        return documentoDAO.load(documento.getPk());
    }
    
    @Transactional(readOnly = true)
    public Documento buscarPorPk(Integer pk) {
        return documentoDAO.getRecord(pk);
    }

    public void saveOrUpdate(Documento documento) {
        documentoDAO.saveOrUpdate(documento);
        
    }

    @Transactional
    public void copiarDadosDocumentoARCAnterior(Ciclo ciclo, ItemElemento item, ItemElemento itemAnterior) {
        if (item.getDocumento() == null) {
            item.setDocumento(new Documento());
        }
        item.getDocumento().setJustificativa(itemAnterior.getDocumento().getJustificativa());
        item.getDocumento().setUltimaAtualizacao(itemAnterior.getDocumento().getUltimaAtualizacao());
        item.getDocumento().setOperadorAtualizacao(itemAnterior.getDocumento().getOperadorAtualizacao());
        item.getDocumento().setAlterarDataUltimaAtualizacao(false);
        documentoDAO.saveOrUpdate(item.getDocumento());
        AnexoDocumentoMediator.get().copiarDadosAnexoARCAnterior(ciclo, item, itemAnterior);
    }
    
    @Transactional
    public void excluirDocumento(Ciclo ciclo, ItemElemento item) {
        if (item.getDocumento() != null) {
            AnexoDocumentoMediator.get().excluirAnexosDocumento(ciclo, item);
            documentoDAO.delete(item.getDocumento());
            item.setDocumento(null);
        }
    }

}
