package crt2.dominio.corec.ajustenotasanefscomite;

import java.util.List;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroNotaAQT;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.ParametroNotaAQTMediator;
import crt2.ConfiguracaoTestesNegocio;


public class TestR004AjusteNotasANEFsComite extends ConfiguracaoTestesNegocio {
    
    public List<ParametroNotaAQT> buscarNotasAjusteCorec(int cicloPK, int arcPK) {
        AnaliseQuantitativaAQT anef = AnaliseQuantitativaAQTMediator.get().buscar(arcPK);
        Ciclo ciclo = CicloMediator.get().loadPK(cicloPK);

        return ParametroNotaAQTMediator.get().buscarParametrosNotaANEFCorec(anef,
                ciclo.getMetodologia().getPk());
    }

    public String getNota(ParametroNotaAQT paramentroNotaAQT) {
        return paramentroNotaAQT.getDescricaoValor();
    }

}
