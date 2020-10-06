package br.gov.bcb.sisaps.src.vo;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import br.gov.bcb.sisaps.src.dominio.AnexoARC;
import br.gov.bcb.sisaps.util.FileComparator;

public class AnexoArcVo extends ObjetoPersistenteVO implements Serializable {

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

    public static List<AnexoArcVo> converterParaListaVo(List<AnexoARC> anexos) {
        List<AnexoArcVo> listaVo = new LinkedList<AnexoArcVo>();
        AnexoArcVo anexoVo = null;

        for (AnexoARC anexoArc : anexos) {
            anexoVo = new AnexoArcVo();
            anexoVo.setPk(anexoArc.getPk());
            anexoVo.setLink(anexoArc.getLink());
            anexoVo.setDescricao("Anexo");
            listaVo.add(anexoVo);
        }
        
        Collections.sort(listaVo, new FileComparator());
        return listaVo;
    }
    
    
    

}
