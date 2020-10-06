package crt2.dominio.analisequantitativa.retomaranaliseaqt;

import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import crt2.ConfiguracaoTestesNegocio;

public class TestR001RetomarAnaliseAQT extends ConfiguracaoTestesNegocio {

    private int anef;
    private String estado;
    private AnaliseQuantitativaAQT analiseQuantitativaAQT;

    public String podeTerRetomada() {
        analiseQuantitativaAQT = AnaliseQuantitativaAQTMediator.get().buscar(anef);
        return SimNaoEnum.getTipo(AnaliseQuantitativaAQTMediator.get().podeAnalisar(analiseQuantitativaAQT))
                .getDescricao();
    }

    public String haDelegacao() {
        analiseQuantitativaAQT = AnaliseQuantitativaAQTMediator.get().buscar(anef);
        return SimNaoEnum.getTipo(analiseQuantitativaAQT.getDelegacao() != null).getDescricao();
    }

    public int getAnef() {
        return anef;
    }

    public void setAnef(int anef) {
        this.anef = anef;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

}
