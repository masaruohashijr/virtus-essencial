package crt2.dominio.corec.ajustenotaanalisequalitativacomite;

import br.gov.bcb.sisaps.src.dominio.AjusteCorec;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.mediator.AjusteCorecMediator;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.ParametroNotaMediator;
import crt2.ConfiguracaoTestesNegocio;


public class TestR005AjusteAnaliseQualitativaComite extends ConfiguracaoTestesNegocio {
    
    public String ajustarNotaQualitativaCorec(int idCiclo, int notaQualitativa) {

        Ciclo ciclo = CicloMediator.get().loadPK(idCiclo);
        AjusteCorec ajusteCorec = AjusteCorecMediator.get().buscarPorCiclo(ciclo);
        ajusteCorec.setNotaQualitativa(ParametroNotaMediator.get().buscarPorPK(notaQualitativa));

        return AjusteCorecMediator.get().salvarAjusteCorec(ajusteCorec);

    }

}