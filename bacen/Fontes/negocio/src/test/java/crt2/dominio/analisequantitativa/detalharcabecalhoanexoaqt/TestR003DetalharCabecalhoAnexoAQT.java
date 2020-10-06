package crt2.dominio.analisequantitativa.detalharcabecalhoanexoaqt;

import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import crt2.ConfiguracaoTestesNegocio;

public class TestR003DetalharCabecalhoAnexoAQT extends ConfiguracaoTestesNegocio {

    private int anef;

    public String secaoDeveSerExibida() {
        AnaliseQuantitativaAQT aqt = AnaliseQuantitativaAQTMediator.get().buscar(anef);
        return SimNaoEnum.getTipo(!aqt.getAnexosAqt().isEmpty()).getDescricao();
    }

    public int getAnef() {
        return anef;
    }

    public void setAnef(int anef) {
        this.anef = anef;
    }
}
