package crt2.dominio.analisequantitativa.editarconcluiranef;

import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import crt2.ConfiguracaoTestesNegocio;

public class TestR001EditarConcluirANEF extends ConfiguracaoTestesNegocio {

    public void concluirEdicaoAnef(Integer idAnef) {
        try {
            erro = null;
            AnaliseQuantitativaAQT aqt = AnaliseQuantitativaAQTMediator.get().buscar(idAnef);
            AnaliseQuantitativaAQTMediator.get().concluirEdicaoANEFInspetor(aqt);
        } catch (NegocioException e) {
            erro = e;
        }
    }
}
