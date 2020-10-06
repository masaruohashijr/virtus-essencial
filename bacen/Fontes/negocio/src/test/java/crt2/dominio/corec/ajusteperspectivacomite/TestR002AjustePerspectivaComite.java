package crt2.dominio.corec.ajusteperspectivacomite;

import br.gov.bcb.sisaps.src.dominio.AjusteCorec;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.mediator.AjusteCorecMediator;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.ParametroPerspectivaMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR002AjustePerspectivaComite extends ConfiguracaoTestesNegocio {

    public String ajustarNotasPerspectivaCorec(int idCiclo, int parametroPerspectiva) {

        Ciclo ciclo = CicloMediator.get().loadPK(idCiclo);
        AjusteCorec ajusteCorec = AjusteCorecMediator.get().buscarPorCiclo(ciclo);
       
        ajusteCorec.setPerspectiva(ParametroPerspectivaMediator.get().buscarPorPK(parametroPerspectiva));

        return AjusteCorecMediator.get().salvarAjusteCorec(ajusteCorec);

    }


}
