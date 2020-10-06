package crt2.dominio.corec.outrasdeliberacoescomite;

import br.gov.bcb.sisaps.src.dominio.AjusteCorec;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.mediator.AjusteCorecMediator;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import crt2.ConfiguracaoTestesNegocio;

public class TestR001OutrasDeliberacoesDoComite extends ConfiguracaoTestesNegocio {
    public void outrasDeliberacoesDoComite(int idCiclo, String outrasDeliberacoes) {
        erro = null;

        try {
            Ciclo ciclo = CicloMediator.get().buscarCicloPorPK(idCiclo);
            AjusteCorec ajusteCorec = AjusteCorecMediator.get().buscarPorCiclo(ciclo);

            ajusteCorec.setOutrasDeliberacoes(outrasDeliberacoes);
            AjusteCorecMediator.get().salvarAjusteCorec(ajusteCorec);
        } catch (NegocioException e) {
            erro = e;
        }
    }
}
