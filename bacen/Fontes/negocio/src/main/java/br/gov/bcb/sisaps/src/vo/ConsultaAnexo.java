package br.gov.bcb.sisaps.src.vo;

import br.gov.bcb.sisaps.util.consulta.Consulta;

public class ConsultaAnexo<T> extends Consulta<T> {
    private String link;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

}
