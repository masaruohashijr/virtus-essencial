package crt2.dominio.analisequantitativa.listaraqtdelegado;

import java.util.List;

import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.mediator.EntidadeSupervisionavelMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR001ListarAQTDelegadoInspetor extends ConfiguracaoTestesNegocio {
    
    
    public  List<AnaliseQuantitativaAQT> consultaPainelSupervisor() {
        return AnaliseQuantitativaAQTMediator.get().consultaPainelAQTDelegados();
    }
    
    public String getPK(AnaliseQuantitativaAQT aqt) {
        return aqt.getPk().toString();
    }
    
    public String getES(AnaliseQuantitativaAQT aqt) {
        return EntidadeSupervisionavelMediator.get().loadPK(aqt.getCiclo().getEntidadeSupervisionavel().getPk()).getNome();
    }

    public String getComponente(AnaliseQuantitativaAQT aqt) {
        return aqt.getParametroAQT().getDescricao();
    }

    public String getEstado(AnaliseQuantitativaAQT aqt) {
        return aqt.getEstado().getDescricao();
    }
}
