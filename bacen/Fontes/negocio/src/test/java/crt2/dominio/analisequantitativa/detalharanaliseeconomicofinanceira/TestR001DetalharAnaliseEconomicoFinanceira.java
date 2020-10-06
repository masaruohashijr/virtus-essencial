package crt2.dominio.analisequantitativa.detalharanaliseeconomicofinanceira;

import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR001DetalharAnaliseEconomicoFinanceira extends ConfiguracaoTestesNegocio {

    public String getNotaCalculada(Integer perfil) {
        PerfilRisco perfilRisco = PerfilRiscoMediator.get().obterPerfilRiscoPorPk(perfil);
        return AnaliseQuantitativaAQTMediator.get().getNotaCalculadaAEF(perfilRisco, perfilUsuario(), false);
    }

    public String getNotaRefinada(Integer perfil) {
        PerfilRisco perfilRisco = PerfilRiscoMediator.get().obterPerfilRiscoPorPk(perfil);
        return AnaliseQuantitativaAQTMediator.get().getNotaRefinadaAEF(perfilRisco, perfilUsuario(), false);
    }

}
