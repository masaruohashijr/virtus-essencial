package crt2.dominio.analisequantitativa.analisarelementoanef;

import java.util.List;

import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ElementoAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroNotaAQT;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.ElementoAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.ParametroNotaAQTMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR001AnalisarElementoANEF extends ConfiguracaoTestesNegocio {

    public List<ParametroNotaAQT> getListaParametrosNotaElementoANEF(Integer idElemento) {
        ElementoAQT elemento = ElementoAQTMediator.get().buscarPorPk(idElemento);
        return ParametroNotaAQTMediator.get().buscarNotaANEFElementoSupervisor(elemento,
                elemento.getAnaliseQuantitativaAQT().getCiclo().getMetodologia().getPk());
    }

    public String getNota(ParametroNotaAQT parametroNota) {
        return parametroNota.getDescricaoValor();
    }
}
