package crt2.dominio.analisequantitativa.editarelementoitemanef;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ElementoAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroNotaAQT;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.ElementoAQTMediator;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import crt2.ConfiguracaoTestesNegocio;

public class TestR005EditarElementoItemANEF extends ConfiguracaoTestesNegocio {

    public void salvarElemento(Integer idAnef, Integer idElemento, BigDecimal nota) {
        erro = null;
        try {
            AnaliseQuantitativaAQT anAqt = AnaliseQuantitativaAQTMediator.get().buscar(idAnef);
            ElementoAQT elemento = ElementoAQTMediator.get().buscarPorPk(idElemento);

            Ciclo ciclo = CicloMediator.get().buscarCicloPorPK(anAqt.getCiclo().getPk());

            ParametroNotaAQT parametroNotaInspetor = null;
            List<ParametroNotaAQT> listaNotas = ciclo.getMetodologia().getNotasAnef();
            for (ParametroNotaAQT paramenNotaAQT : listaNotas) {
                if (paramenNotaAQT.getValor().equals(nota.setScale(2, RoundingMode.HALF_UP))) {
                    parametroNotaInspetor = paramenNotaAQT;
                    break;
                }
            }

            elemento.setParametroNotaInspetor(parametroNotaInspetor);
            elemento.setAnaliseQuantitativaAQT(anAqt);
            ElementoAQTMediator.get().salvarNovaNotaElementoARC(anAqt, elemento);
        } catch (NegocioException e) {
            erro = e;
        }
    }

}
