package br.gov.bcb.sisaps.src.vo;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import br.gov.bcb.sisaps.src.dominio.AnexoDocumento;
import br.gov.bcb.sisaps.src.dominio.Documento;
import br.gov.bcb.sisaps.util.FileComparator;

public class AnexoDocumentoVo extends ObjetoPersistenteVO {

    private String link;
    private String descricao;
    private Documento documento;
    private int sequencial;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Documento getDocumento() {
        return documento;
    }

    public void setDocumento(Documento documento) {
        this.documento = documento;
    }

    public int getSequencial() {
        return sequencial;
    }

    public void setSequencial(int sequencial) {
        this.sequencial = sequencial;
    }

    public static List<AnexoDocumentoVo> converterParaListaVo(List<AnexoDocumento> anexosItem) {
        List<AnexoDocumentoVo> listaVo = new LinkedList<AnexoDocumentoVo>();
        for (AnexoDocumento anexoItemElemento : anexosItem) {
            listaVo.add(converterParaEntidade(anexoItemElemento));
        }
        Collections.sort(listaVo, new FileComparator());
        return listaVo;
    }

    public static AnexoDocumentoVo converterParaEntidade(AnexoDocumento anexoItemElemento) {
        AnexoDocumentoVo anexoVo = new AnexoDocumentoVo();
        anexoVo.setPk(anexoItemElemento.getPk());
        anexoVo.setDocumento(anexoItemElemento.getDocumento());
        anexoVo.setLink(anexoItemElemento.getLink());
        anexoVo.setDescricao("Anexo");
        return anexoVo;
    }

}
