package crt2.dominio.analisequantitativa.listaraqtsintese;

import java.util.List;

import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.mediator.EntidadeSupervisionavelMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR001ListarAQTRevisaoSupervisor extends ConfiguracaoTestesNegocio {

    
    public  List<AnaliseQuantitativaAQT> consultaPainelSupervisor() {
        return AnaliseQuantitativaAQTMediator.get().consultaPainelAQTAnalisados();
    }
    
    public String getID(AnaliseQuantitativaAQT aqt) {
        return aqt.getPk().toString();
    }
    
    public String getES(AnaliseQuantitativaAQT aqt) {
        return EntidadeSupervisionavelMediator.get().loadPK(aqt.getCiclo().getEntidadeSupervisionavel().getPk()).getNome();
    }

    public String getSintese(AnaliseQuantitativaAQT aqt) {
        return aqt.getParametroAQT().getDescricao();
    }

   
    
}
