package crt2.dominio.analisequantitativa.detalharanaliseeconomicofinanceira;

import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import crt2.ConfiguracaoTestesNegocio;

public class TestR002DetalharAnaliseEconomicoFinanceira extends ConfiguracaoTestesNegocio {

    public String isBotaoGerenciarVisivel(Long perfil) {
        PerfilRisco perfilRisco = PerfilRiscoMediator.get().obterPerfilRiscoPorPk(Integer.valueOf(perfil.toString()));
        SimNaoEnum retorno =
                SimNaoEnum.getTipo(AnaliseQuantitativaAQTMediator.get().isBotaoGerenciarVisivel(perfilRisco.getCiclo(),
                        perfilRisco, perfilUsuario()));
        return retorno.getDescricao();
    }

}
