package crt2.dominio.corec.ajustegraupreocupacaocomite;

import br.gov.bcb.sisaps.src.dominio.AjusteCorec;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.mediator.AjusteCorecMediator;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.ParametroNotaMediator;
import crt2.ConfiguracaoTestesNegocio;


public class TestR004AjusteGrauPreocupacaoComite extends ConfiguracaoTestesNegocio {
    
    public String ajustarNotasGrauCorec(int idCiclo, int notaFinalId) {
        Ciclo ciclo = CicloMediator.get().loadPK(idCiclo);
        AjusteCorec ajusteCorec = AjusteCorecMediator.get().buscarPorCiclo(ciclo);
        ajusteCorec.setNotaFinal(ParametroNotaMediator.get().buscarPorPK(notaFinalId));

        return AjusteCorecMediator.get().salvarAjusteCorec(ajusteCorec);
    }
    
}
