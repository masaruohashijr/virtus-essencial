package crt2.dominio.corec.ajustenotaanalisequantitativacomite;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR003AjusteAnaliseQuantitativaComite extends ConfiguracaoTestesNegocio {

    public String getNotaAEF(int cicloPK) {
        Ciclo ciclo = CicloMediator.get().loadPK(cicloPK);

        return CicloMediator.get().notaQuantitativa(ciclo, PerfilAcessoEnum.COMITE).getDescricaoValor();

    }

}
