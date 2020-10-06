package br.gov.bcb.sisaps.src.vo.analisequantitativa.aqt;

import java.io.InputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnexoAQT;
import br.gov.bcb.sisaps.src.vo.ObjetoPersistenteVO;

public class AnexoAQTVO extends ObjetoPersistenteVO implements Serializable {

    public static final String CAMPO_ID = "ANA_ID";
    private static final int TAMANHO_LINK = 200;
    private AnaliseQuantitativaAQT analiseQuantitativaAQT;
    private String link;
    private String descricao;
    private transient InputStream inputStream;

    public AnaliseQuantitativaAQT getAnaliseQuantitativaAQT() {
        return analiseQuantitativaAQT;
    }

    public void setAnaliseQuantitativaAQT(AnaliseQuantitativaAQT analiseQuantitativaAQT) {
        this.analiseQuantitativaAQT = analiseQuantitativaAQT;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public static int getTamanhoLink() {
        return TAMANHO_LINK;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public static List<AnexoAQTVO> converterParaListaVo(List<AnexoAQT> anexos) {
        List<AnexoAQTVO> listaVo = new LinkedList<AnexoAQTVO>();
        for (AnexoAQT anexoAqt : anexos) {
            listaVo.add(converterParaEntidadeVo(anexoAqt));
        }
        return listaVo;
    }

    public static AnexoAQTVO converterParaEntidadeVo(AnexoAQT anexoAqt) {
        AnexoAQTVO anexoVo = new AnexoAQTVO();
        anexoVo.setPk(anexoAqt.getPk());
        anexoVo.setLink(anexoAqt.getLink());
        anexoVo.setDescricao("Anexo");
        return anexoVo;
    }


}
