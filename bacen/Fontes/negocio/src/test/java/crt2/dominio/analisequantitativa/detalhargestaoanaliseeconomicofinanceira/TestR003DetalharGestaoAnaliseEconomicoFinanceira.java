package crt2.dominio.analisequantitativa.detalhargestaoanaliseeconomicofinanceira;

import java.util.List;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroAQT;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.ParametroAQTMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR003DetalharGestaoAnaliseEconomicoFinanceira extends ConfiguracaoTestesNegocio {

    private Ciclo ciclo;

    public List<AnaliseQuantitativaAQT> consultaPainelSupervisor(String loginUsuarioLogado, Integer pkCiclo) {
        logar(loginUsuarioLogado);
        ciclo = CicloMediator.get().buscarCicloPorPK(pkCiclo);
        return AnaliseQuantitativaAQTMediator.get().buscarANEFsRascunho(ciclo);
    }
    
    public Integer getID(AnaliseQuantitativaAQT aqt) {
        ciclo = aqt.getCiclo();
        return aqt.getPk();
    }
    
    public String getComponente(AnaliseQuantitativaAQT aqt) {
        ParametroAQT parametroAQT = ParametroAQTMediator.get().buscarParemetroAQT(aqt);
        return parametroAQT.getDescricao();
    }
    
    public String getEstado(AnaliseQuantitativaAQT aqt) {
        return aqt.getEstado().getDescricao();
    }
    
    public String getResponsavel(AnaliseQuantitativaAQT aqt) {
        return AnaliseQuantitativaAQTMediator.get().buscarResponsavel(aqt);
    }
}
