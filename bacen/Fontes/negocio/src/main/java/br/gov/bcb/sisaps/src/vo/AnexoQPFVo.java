package br.gov.bcb.sisaps.src.vo;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import br.gov.bcb.sisaps.src.dominio.AnexoQuadroPosicaoFinanceira;

public class AnexoQPFVo extends ObjetoPersistenteVO implements Serializable {

    private String link;
    private String descricao;

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

    public static List<AnexoQPFVo> converterParaListaVo(List<AnexoQuadroPosicaoFinanceira> anexos) {
        List<AnexoQPFVo> listaVo = new LinkedList<AnexoQPFVo>();
        AnexoQPFVo anexoVo = null;

        for (AnexoQuadroPosicaoFinanceira anexoArc : anexos) {
            anexoVo = new AnexoQPFVo();
            anexoVo.setPk(anexoArc.getPk());
            anexoVo.setLink(anexoArc.getLink());
            anexoVo.setDescricao("Anexo");
            listaVo.add(anexoVo);
        }
        return listaVo;
    }
    
    
    

}
