package crt2.dominio.corec.ajustenotaanalisequalitativacomite;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR003AjusteAnaliseQualitativaComite extends ConfiguracaoTestesNegocio {

    public String getNotaMatriz(int cicloPK) {
        Ciclo ciclo = CicloMediator.get().loadPK(cicloPK);

        return CicloMediator.get().notaQualitativa(ciclo, PerfilAcessoEnum.COMITE).getDescricaoValor();

    }

}
