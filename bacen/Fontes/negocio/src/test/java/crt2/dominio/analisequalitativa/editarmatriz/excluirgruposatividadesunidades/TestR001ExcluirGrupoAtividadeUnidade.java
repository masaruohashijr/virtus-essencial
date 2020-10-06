package crt2.dominio.analisequalitativa.editarmatriz.excluirgruposatividadesunidades;

import br.gov.bcb.sisaps.src.mediator.CelulaRiscoControleMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR001ExcluirGrupoAtividadeUnidade extends ConfiguracaoTestesNegocio {

  
    
    
    public String excluir() {
        return CelulaRiscoControleMediator.get().msgAoExcluirCelula();
    }

}
