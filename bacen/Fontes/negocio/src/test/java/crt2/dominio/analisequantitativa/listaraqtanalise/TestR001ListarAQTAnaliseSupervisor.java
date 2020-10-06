package crt2.dominio.analisequantitativa.listaraqtanalise;

import java.util.List;

import br.gov.bcb.sisaps.adaptadores.pessoa.BcPessoaAdapter;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.mediator.EntidadeSupervisionavelMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR001ListarAQTAnaliseSupervisor extends ConfiguracaoTestesNegocio {

    
    public  List<AnaliseQuantitativaAQT> consultaPainelSupervisor(String usuario) {
        logar(usuario, BcPessoaAdapter.get().buscarServidorPorLoginSemExcecao(usuario), true);
        return AnaliseQuantitativaAQTMediator.get().consultaPainelAQTAnalise();
    }
    
    public String getID(AnaliseQuantitativaAQT aqt) {
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
