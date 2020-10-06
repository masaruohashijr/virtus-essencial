package crt2.dominio.analisequantitativa.editarelementoitemanef;

import java.util.List;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroNotaAQT;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR001EditarElementoItemANEF extends ConfiguracaoTestesNegocio {

    public List<ParametroNotaAQT> getListaParametrosNotaANEF(int idCiclo) {
        Ciclo ciclo = CicloMediator.get().buscarCicloPorPK(idCiclo);
        return ciclo.getMetodologia().getParametrosNotaAQT();
    }

    public String getNota(ParametroNotaAQT parametroNota) {
        return parametroNota.getDescricaoValor();
    }
}
