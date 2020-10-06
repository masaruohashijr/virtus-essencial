package crt2.dominio.analisequantitativa.editarajustenotasanef;

import java.util.List;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroNotaAQT;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.ParametroNotaAQTMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR001EditarAjusteNotasANEF extends ConfiguracaoTestesNegocio {
    
    public List<ParametroNotaAQT> isNotasAjuste( int anefPK) {
        AnaliseQuantitativaAQT anef = AnaliseQuantitativaAQTMediator.get().buscar(anefPK);
        Ciclo ciclo = CicloMediator.get().loadPK(anef.getCiclo().getPk());

        return ParametroNotaAQTMediator.get().buscarNotaANEFAjusteInspetor(anef,
                ciclo.getMetodologia().getPk());
    }

    public String getNota(ParametroNotaAQT paramentroNotaAQT) {
        return paramentroNotaAQT.getDescricaoValor();
    }

    

   
}
