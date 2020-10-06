package crt2.dominio.analisequantitativa.excluirdesignacaoaqt;

import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.DesignacaoAQTMediator;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import crt2.ConfiguracaoTestesNegocio;

public class TestR001ExcluirDesignacaoAQT extends ConfiguracaoTestesNegocio {

    private int anef;
    private AnaliseQuantitativaAQT analiseQuantitativaAQT;

    public String podeTerDesignacaoExcluida() {

        analiseQuantitativaAQT = AnaliseQuantitativaAQTMediator.get().buscar(anef);

        return SimNaoEnum.getTipo(DesignacaoAQTMediator.get().podeExcluirDesignar(analiseQuantitativaAQT)).getDescricao();
    }
    
    
    
    public String estado() {

        analiseQuantitativaAQT = AnaliseQuantitativaAQTMediator.get().buscar(anef);

        return analiseQuantitativaAQT.getEstado().getDescricao();
    }


    public int getAnef() {
        return anef;
    }

    public void setAnef(int anef) {
        this.anef = anef;
    }
    
    

}
