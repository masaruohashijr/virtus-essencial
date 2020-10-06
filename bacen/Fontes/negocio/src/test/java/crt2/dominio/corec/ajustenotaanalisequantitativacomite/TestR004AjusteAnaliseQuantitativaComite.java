package crt2.dominio.corec.ajustenotaanalisequantitativacomite;

import java.util.List;

import br.gov.bcb.sisaps.src.dominio.AjusteCorec;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroNotaAQT;
import br.gov.bcb.sisaps.src.mediator.AjusteCorecMediator;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.ParametroNotaAQTMediator;
import crt2.ConfiguracaoTestesNegocio;


public class TestR004AjusteAnaliseQuantitativaComite extends ConfiguracaoTestesNegocio {
    
    public List<ParametroNotaAQT> buscarNotasAjusteCorec(int cicloPK) {
        Ciclo ciclo = CicloMediator.get().loadPK(cicloPK);

        AjusteCorec ajuste = AjusteCorecMediator.get().buscarPorCiclo(cicloPK);

        return ParametroNotaAQTMediator.get().buscarParametrosNotaQuantitativa(ciclo,
                ciclo.getMetodologia().getPk(), ajuste);
    }
    
    public String getDescricaoNota(ParametroNotaAQT paramentroNota) {
        return paramentroNota.getDescricao();
    }

}
