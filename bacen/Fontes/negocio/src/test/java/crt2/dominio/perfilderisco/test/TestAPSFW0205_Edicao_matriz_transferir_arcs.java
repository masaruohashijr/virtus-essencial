package crt2.dominio.perfilderisco.test;

import br.gov.bcb.sisaps.src.mediator.AtividadeMediator;
import br.gov.bcb.sisaps.src.mediator.CelulaRiscoControleMediator;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import crt2.ConfiguracaoTestesNegocio;

public class TestAPSFW0205_Edicao_matriz_transferir_arcs extends ConfiguracaoTestesNegocio {

    public void transferir(int idAtividade1, int idAtividade2, int idCelula) {

        erro = null;
        try {
            AtividadeMediator.get().transferirArcs(AtividadeMediator.get().loadPK(idAtividade1),
                    AtividadeMediator.get().loadPK(idAtividade2), CelulaRiscoControleMediator.get().buscar(idCelula));
        } catch (NegocioException e) {
            erro = e;
        }
    }

}