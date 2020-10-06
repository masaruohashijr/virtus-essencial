package crt2.dominio.corec.ajustenotaanalisequalitativacomite;

import java.util.List;

import br.gov.bcb.sisaps.src.dominio.AjusteCorec;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.ParametroNota;
import br.gov.bcb.sisaps.src.mediator.AjusteCorecMediator;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.ParametroNotaMediator;
import crt2.ConfiguracaoTestesNegocio;


public class TestR004AjusteAnaliseQualitativaComite extends ConfiguracaoTestesNegocio {
    
    public List<ParametroNota> buscarNotasAjusteCorec(int cicloPK) {
        Ciclo ciclo = CicloMediator.get().loadPK(cicloPK);

        AjusteCorec ajuste = AjusteCorecMediator.get().buscarPorCiclo(cicloPK);

        return ParametroNotaMediator.get().buscarParametrosNotaQualitativa(ciclo,
                ciclo.getMetodologia().getPk(), ajuste);
    }
    
    public String getDescricaoNota(ParametroNota paramentroNota) {
        return paramentroNota.getDescricao();
    }

}
