package crt2.dominio.analisequantitativa.alterarpercentuais;

import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.PesoAQTMediator;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import crt2.ConfiguracaoTestesNegocio;

public class TestR004AlterarPercentuaisANEF extends ConfiguracaoTestesNegocio {

    public void confirmarPercentual(Integer ciclo) {
        try {
            PesoAQTMediator.get().confirmarPercentual(ciclo);
        } catch (NegocioException e) {
            erro = e;
        }
    }
}
