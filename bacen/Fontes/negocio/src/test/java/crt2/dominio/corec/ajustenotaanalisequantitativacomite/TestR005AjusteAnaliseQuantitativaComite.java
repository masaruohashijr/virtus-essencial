package crt2.dominio.corec.ajustenotaanalisequantitativacomite;

import br.gov.bcb.sisaps.src.dominio.AjusteCorec;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.mediator.AjusteCorecMediator;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.ParametroNotaAQTMediator;
import crt2.ConfiguracaoTestesNegocio;


public class TestR005AjusteAnaliseQuantitativaComite extends ConfiguracaoTestesNegocio {
    
    public String ajustarNotaQuantitativaCorec(int idCiclo, int notaQuantitativa) {

        Ciclo ciclo = CicloMediator.get().loadPK(idCiclo);
        AjusteCorec ajusteCorec = AjusteCorecMediator.get().buscarPorCiclo(ciclo);
        ajusteCorec.setNotaQuantitativa(ParametroNotaAQTMediator.get().buscarPorPK(notaQuantitativa));

        return AjusteCorecMediator.get().salvarAjusteCorec(ajusteCorec);

    }

}