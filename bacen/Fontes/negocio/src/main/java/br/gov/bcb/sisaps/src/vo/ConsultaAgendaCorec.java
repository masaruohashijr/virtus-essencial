package br.gov.bcb.sisaps.src.vo;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.util.consulta.Consulta;

public class ConsultaAgendaCorec extends Consulta<AgendaCorecVO> {

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
