package crt2.dominio.corec.ajustegraupreocupacaocomite;

import java.util.List;

import br.gov.bcb.sisaps.src.dominio.AjusteCorec;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.ParametroNota;
import br.gov.bcb.sisaps.src.mediator.AjusteCorecMediator;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.ParametroNotaMediator;
import crt2.ConfiguracaoTestesNegocio;


public class TestR003AjusteGrauPreocupacaoComite extends ConfiguracaoTestesNegocio {
    
    public List<ParametroNota> buscarNotasAjusteCorec(int cicloPK) {
        Ciclo ciclo = CicloMediator.get().loadPK(cicloPK);
        AjusteCorec ajusteCorec = AjusteCorecMediator.get().buscarPorCiclo(cicloPK);
        return ParametroNotaMediator.get().buscarParametrosNotaFinal(ciclo,
                ciclo.getMetodologia().getPk(), ajusteCorec);
    }
    
    public String getDescricaoNota(ParametroNota parametroNota) {
        return parametroNota.getDescricao();
    }
    
}
