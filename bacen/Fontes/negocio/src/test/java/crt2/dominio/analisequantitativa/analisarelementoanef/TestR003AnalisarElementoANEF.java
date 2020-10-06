package crt2.dominio.analisequantitativa.analisarelementoanef;

import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ElementoAQT;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.ElementoAQTMediator;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import crt2.ConfiguracaoTestesNegocio;

public class TestR003AnalisarElementoANEF extends ConfiguracaoTestesNegocio {

    public void salvarAnaliseElemento(Integer idAnef, Integer idElemento, String justificativa) {
        erro = null;
        try {
            AnaliseQuantitativaAQT anAqt = AnaliseQuantitativaAQTMediator.get().buscar(idAnef);
            ElementoAQT elemento = ElementoAQTMediator.get().buscarPorPk(idElemento);
            elemento.setJustificativaSupervisor(justificativa);
            ElementoAQTMediator.get().salvarNovaNotaElementoAQTSupervisor(anAqt, elemento, true, true);
        } catch (NegocioException e) {
            erro = e;
        }
    }
}
