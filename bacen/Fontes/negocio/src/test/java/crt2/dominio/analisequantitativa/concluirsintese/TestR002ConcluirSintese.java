package crt2.dominio.analisequantitativa.concluirsintese;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.SinteseDeRiscoAQTMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR002ConcluirSintese extends ConfiguracaoTestesNegocio {

    private String msg;

    public String concluirSintese(int anefRascunhoPK, int cicloPK) {
        AnaliseQuantitativaAQT aqtRascunho = AnaliseQuantitativaAQTMediator.get().buscar(anefRascunhoPK);
        Ciclo ciclo = CicloMediator.get().buscarCicloPorPK(cicloPK);

        msg = SinteseDeRiscoAQTMediator.get().concluirNovaSintesAQT(aqtRascunho.getParametroAQT(), ciclo);
        return msg;
    }

    public String getMensagem() {
        return msg;
    }
}
