package crt2.dominio.analisequantitativa.analisarelementoanef;

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

public class TestR002AnalisarElementoANEF extends ConfiguracaoTestesNegocio {

    public void salvarElemento(Integer idAnef, Integer idElemento, short nota) {
        erro = null;
        try {
            AnaliseQuantitativaAQT anAqt = AnaliseQuantitativaAQTMediator.get().buscar(idAnef);
            ElementoAQT elemento = ElementoAQTMediator.get().buscarPorPk(idElemento);

            Ciclo ciclo = CicloMediator.get().buscarCicloPorPK(anAqt.getCiclo().getPk());

            ParametroNotaAQT parametroNotaSupervisor = null;
            List<ParametroNotaAQT> listaNotas = ciclo.getMetodologia().getParametrosNotaAQT();
            for (ParametroNotaAQT paramenNotaAQT : listaNotas) {
                if (paramenNotaAQT.getValor().equals(nota)) {
                    parametroNotaSupervisor = paramenNotaAQT;
                    break;
                }
            }

            elemento.setParametroNotaSupervisor(parametroNotaSupervisor);
            elemento.setAnaliseQuantitativaAQT(anAqt);
            ElementoAQTMediator.get().salvarNovaNotaElementoAQTSupervisor(anAqt, elemento,  true, true);
        } catch (NegocioException e) {
            erro = e;
        }
    }

}
