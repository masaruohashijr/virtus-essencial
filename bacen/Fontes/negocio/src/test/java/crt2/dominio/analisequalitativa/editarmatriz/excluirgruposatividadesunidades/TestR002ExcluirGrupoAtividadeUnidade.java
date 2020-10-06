package crt2.dominio.analisequalitativa.editarmatriz.excluirgruposatividadesunidades;

import java.util.ArrayList;
import java.util.List;

import br.gov.bcb.sisaps.src.dominio.Atividade;
import br.gov.bcb.sisaps.src.dominio.CelulaRiscoControle;
import br.gov.bcb.sisaps.src.mediator.AtividadeMediator;
import br.gov.bcb.sisaps.src.mediator.CelulaRiscoControleMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR002ExcluirGrupoAtividadeUnidade extends ConfiguracaoTestesNegocio {

    private String mensagem;

    public void confirmarExclusao(Integer idMatriz, Integer idAtividade, Integer idGruporc) {
        CelulaRiscoControle celula = CelulaRiscoControleMediator.get().buscar(idGruporc);
        List<CelulaRiscoControle> excluidos = new ArrayList<CelulaRiscoControle>();
        excluidos.add(celula);
        Atividade atividadeLoad = AtividadeMediator.get().loadPK(idAtividade);
        atividadeLoad.getCelulasRiscoControle().remove(celula);
        mensagem = AtividadeMediator.get().alterar(atividadeLoad, excluidos);
    }
    
    
    public String getMensagem() {
        return mensagem;
    }

}
