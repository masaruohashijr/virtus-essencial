package crt2.dominio.analisequantitativa.excluirdesignacaoaqt;

import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.DesignacaoAQTMediator;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import crt2.dominio.analisequantitativa.designaraqt.TestR003DesignarAQT;


public class TestR003ExcluirDesignacaoAQT extends TestR003DesignarAQT {

    public void excluirDesignacaoAQT(int aqt) {
        erro = null;
       
        try {
            DesignacaoAQTMediator.get().excluirDesignacao(AnaliseQuantitativaAQTMediator.get().buscar(aqt));
        } catch (NegocioException e) {
            erro = e;
        }
        
        
    }
}
