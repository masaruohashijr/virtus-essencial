package crt2.dominio.analisequalitativa.editarmatriz.excluirgruposatividadesunidades;

import java.util.List;

import br.gov.bcb.sisaps.src.dominio.Metodologia;
import br.gov.bcb.sisaps.src.dominio.ParametroGrupoRiscoControle;
import br.gov.bcb.sisaps.src.mediator.MetodologiaMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR004ExcluirGrupoAtividadeUnidade extends ConfiguracaoTestesNegocio {
    
    public List<ParametroGrupoRiscoControle> buscarGruposMatriz(int metodologiaPk) {

        Metodologia metodologia = MetodologiaMediator.get().loadPK(metodologiaPk);

        return metodologia.getParametrosGrupoRiscoControleMatriz();
    }

    public String getDescricao(ParametroGrupoRiscoControle parametro) {
        return parametro.getAbreviado();
    }

}
