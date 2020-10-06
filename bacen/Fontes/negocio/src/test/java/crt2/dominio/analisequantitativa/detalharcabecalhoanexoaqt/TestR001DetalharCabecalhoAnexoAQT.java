package crt2.dominio.analisequantitativa.detalharcabecalhoanexoaqt;

import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.mediator.EstadoCicloMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR001DetalharCabecalhoAnexoAQT extends ConfiguracaoTestesNegocio {

    private int anef;

    public String estadoDoCiclo() {
        AnaliseQuantitativaAQT aqt = AnaliseQuantitativaAQTMediator.get().buscar(anef);
        return aqt.getCiclo().getEstadoCiclo().getEstado().getDescricao();
    }

    public String oQueDeveSerExibido() {
        AnaliseQuantitativaAQT aqt = AnaliseQuantitativaAQTMediator.get().buscar(anef);
        return EstadoCicloMediator.get().obterLabelCorec(aqt.getCiclo().getEstadoCiclo());
    }

    public int getAnef() {
        return anef;
    }

    public void setAnef(int anef) {
        this.anef = anef;
    }
}
