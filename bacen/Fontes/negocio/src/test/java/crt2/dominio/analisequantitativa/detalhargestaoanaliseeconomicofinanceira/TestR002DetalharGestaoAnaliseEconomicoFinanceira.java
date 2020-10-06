package crt2.dominio.analisequantitativa.detalhargestaoanaliseeconomicofinanceira;

import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR002DetalharGestaoAnaliseEconomicoFinanceira extends ConfiguracaoTestesNegocio {

    public String getNotaCalculada(String loginUsuarioLogado, Integer pkCiclo) {
        logar(loginUsuarioLogado);
        AnaliseQuantitativaAQTMediator aqtMediator = AnaliseQuantitativaAQTMediator.get();
        PerfilRisco perfil =  PerfilRiscoMediator.get().obterPerfilRiscoAtual(pkCiclo);
        return aqtMediator.getNotaCalculadaAEF(perfil, perfilUsuario(), true);
    }

    public String getNotaRefinada(String loginUsuarioLogado, Integer pkCiclo) {
        logar(loginUsuarioLogado);
        AnaliseQuantitativaAQTMediator aqtMediator = AnaliseQuantitativaAQTMediator.get();
        PerfilRisco perfil =  PerfilRiscoMediator.get().obterPerfilRiscoAtual(pkCiclo);
        return aqtMediator.getNotaRefinadaAEF(perfil, perfilUsuario(), true);
    }

}
