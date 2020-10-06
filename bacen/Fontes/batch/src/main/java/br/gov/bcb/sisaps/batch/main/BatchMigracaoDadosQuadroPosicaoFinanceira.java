package br.gov.bcb.sisaps.batch.main;

import br.gov.bcb.sisaps.batch.AbstractBatchApplication;
import br.gov.bcb.sisaps.src.mediator.MigracaoDadosQuadroPosicaoFinanceiraMediator;

public class BatchMigracaoDadosQuadroPosicaoFinanceira extends AbstractBatchApplication {
    
    public static void main(String[] args) {
        new BatchMigracaoDadosQuadroPosicaoFinanceira().init();
    }

    @Override
    protected void executar() {
        MigracaoDadosQuadroPosicaoFinanceiraMediator migracaoDadosQuadroPosicaoFinanceiraMediator = 
                (MigracaoDadosQuadroPosicaoFinanceiraMediator) getSpringContext().getBean(
                        "migracaoDadosQuadroPosicaoFinanceiraMediator");
        migracaoDadosQuadroPosicaoFinanceiraMediator.migrarDadosQuadroPosicaoFinanceira();
    }

}
