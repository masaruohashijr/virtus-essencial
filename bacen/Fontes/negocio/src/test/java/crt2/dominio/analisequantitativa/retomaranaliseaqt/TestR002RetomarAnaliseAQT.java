package crt2.dominio.analisequantitativa.retomaranaliseaqt;

import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR002RetomarAnaliseAQT extends ConfiguracaoTestesNegocio {

    private AnaliseQuantitativaAQT analiseQuantitativaAQT;
    
    public void analisarAQT(int aqt) {
        analiseQuantitativaAQT = AnaliseQuantitativaAQTMediator.get().buscar(aqt);
        AnaliseQuantitativaAQTMediator.get().alterarEstadoAnefBotaoAnalisar(analiseQuantitativaAQT);
    }
}
