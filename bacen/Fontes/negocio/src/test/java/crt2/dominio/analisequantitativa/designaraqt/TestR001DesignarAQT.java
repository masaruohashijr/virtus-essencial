package crt2.dominio.analisequantitativa.designaraqt;

import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.DesignacaoAQTMediator;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import crt2.ConfiguracaoTestesNegocio;

public class TestR001DesignarAQT extends ConfiguracaoTestesNegocio {

    private int anef;
    private AnaliseQuantitativaAQT analiseQuantitativaAQT;

    public String podeSerDesignado() {

        analiseQuantitativaAQT = AnaliseQuantitativaAQTMediator.get().buscar(anef);

        return SimNaoEnum.getTipo(DesignacaoAQTMediator.get().podeDesignar(analiseQuantitativaAQT)).getDescricao();
    }

    public String estado() {

        analiseQuantitativaAQT = AnaliseQuantitativaAQTMediator.get().buscar(anef);

        return analiseQuantitativaAQT.getEstado().getDescricao();
    }

    public int getAnef() {
        return anef;
    }

    public void setAnef(int anef) {
        this.anef = anef;
    }

}
