package br.gov.bcb.sisaps.src.mediator;

import java.util.LinkedList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.seguranca.UsuarioCorrente;
import br.gov.bcb.sisaps.src.dao.DocumentoApsDao;
import br.gov.bcb.sisaps.src.dominio.AnexoDocumentoAPS;
import br.gov.bcb.sisaps.src.dominio.DocumentoAPS;
import br.gov.bcb.sisaps.util.geral.DataUtil;

@Service
public class DocumentoMediatorAPS {

    @Autowired
    DocumentoApsDao repository;

    @Autowired
    AnexoDocumentoApsMediator anexoDocumentoApsMediator;

    @Transactional
    public DocumentoAPS duplicarDocumento(DocumentoAPS anterior, boolean isCopiarUsuarioAnterior) {
        DocumentoAPS novoDocumento = null;
        if (anterior != null) {
            novoDocumento = new DocumentoAPS();
            novoDocumento.setAnexosDocumentoAps(new LinkedList<AnexoDocumentoAPS>());
            novoDocumento.setJustificativa(anterior.getJustificativa());
            if (isCopiarUsuarioAnterior) {
                novoDocumento.setUltimaAtualizacao(anterior.getUltimaAtualizacao());
                novoDocumento.setOperadorAtualizacao(anterior.getOperador());
            } else {
                setAuditoria(novoDocumento);
            }

            repository.save(novoDocumento);
        }
        return novoDocumento;
    }

    private void setAuditoria(DocumentoAPS documento) {
        documento.setUltimaAtualizacao(DataUtil.getDateTimeAtual());
        documento.setOperadorAtualizacao(UsuarioCorrente.get().getLogin());
    }

    @Transactional
    public void salvar(DocumentoAPS documento) {
        setAuditoria(documento);
        repository.saveOrUpdate(documento);
    }

    @Transactional
    public void salvarAtualizar(DocumentoAPS documento) {
        repository.saveOrUpdate(documento);
    }
    
    @Transactional
    public DocumentoAPS buscar(Integer codigoDocumento) {
        return repository.buscarDocumento(codigoDocumento);
    }

    @Transactional
    public void deletar(DocumentoAPS documento) {
        anexoDocumentoApsMediator.deletar(documento.getAnexosDocumentoAps());
        documento.setAnexosDocumentoAps(null);
        repository.delete(documento);
    }

}
