package br.gov.bcb.sisaps.src.vo;

import br.gov.bcb.sisaps.src.dominio.Ciclo;

public class AgendaCorecVO extends ObjetoPersistenteVO {

    private Ciclo ciclo;
    private String local;

    public Ciclo getCiclo() {
        return ciclo;
    }

    public void setCiclo(Ciclo ciclo) {
        this.ciclo = ciclo;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }
}
