package crt2.dominio.analisequantitativa.editarconcluiranef;

import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR002EditarConcluirANEF extends ConfiguracaoTestesNegocio {

    private String msg;

    public String concluirEdicaoAnef(Integer integer) {
        AnaliseQuantitativaAQT aqt = AnaliseQuantitativaAQTMediator.get().buscar(integer);
        msg = AnaliseQuantitativaAQTMediator.get().concluirEdicaoANEFInspetor(aqt);
        return msg;
    }

    public String getMensagem() {
        return msg;
    }
}
