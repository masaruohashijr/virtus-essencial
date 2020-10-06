package br.gov.bcb.sisaps.src.mediator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistente;
import br.gov.bcb.sisaps.src.dao.AnexoDocumentoApsDao;
import br.gov.bcb.sisaps.src.dominio.AnexoDocumento;
import br.gov.bcb.sisaps.src.dominio.AnexoDocumentoAPS;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Documento;
import br.gov.bcb.sisaps.src.dominio.DocumentoAPS;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoEntidadeAnexoDocumentoEnum;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;

@Service
public class AnexoDocumentoApsMediator {

    @Autowired
    AnexoDocumentoApsDao repository;

    @Transactional
    public void deletar(List<AnexoDocumentoAPS> anexosDocumento) {
        for (AnexoDocumentoAPS anexoDocumentoAPS : anexosDocumento) {
            repository.delete(anexoDocumentoAPS);
            repository.flush();
        }
    }

    public void duplicarAnexoDocumento(Ciclo ciclo, TipoEntidadeAnexoDocumentoEnum tipo,
            ObjetoPersistente<Integer> objeto, Documento documentoAnterior, DocumentoAPS novoDocumento,
            boolean isCopiarUsuarioAnexoAnterior, boolean isBatch) {

        if (documentoAnterior != null) {
            List<AnexoDocumento> anexosItemElemento = documentoAnterior.getAnexosItemElemento();

            for (AnexoDocumento anexoDocumento : anexosItemElemento) {
                byte[] arquivoBytes = null;
                if (!SisapsUtil.isExecucaoTeste()) {
                    arquivoBytes =
                            AnexoDocumentoMediator.get().recuperarArquivo(anexoDocumento.getLink(), objeto, tipo, ciclo);
                }
                AnexoDocumentoAPS anexoDocumentoAPS = new AnexoDocumentoAPS();
                anexoDocumentoAPS.setLink(anexoDocumento.getLink());
                anexoDocumentoAPS.setArquivo(arquivoBytes);
                anexoDocumentoAPS.setDocumentoAps(novoDocumento);
                anexoDocumentoAPS.setExcluido(SimNaoEnum.NAO.getCodigo().toString());
                if (isBatch) {
                    anexoDocumentoAPS.setOperadorAtualizacao(anexoDocumento.getOperadorAtualizacao());
                    anexoDocumentoAPS.setUltimaAtualizacao(anexoDocumento.getUltimaAtualizacao());
                }
                repository.save(anexoDocumentoAPS);
            }
        }
    }

}
