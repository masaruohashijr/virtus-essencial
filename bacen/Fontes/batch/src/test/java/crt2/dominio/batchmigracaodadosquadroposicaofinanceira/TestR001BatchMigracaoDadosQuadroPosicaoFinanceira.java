package crt2.dominio.batchmigracaodadosquadroposicaofinanceira;

import br.gov.bcb.sisaps.batch.main.BatchMigracaoDadosQuadroPosicaoFinanceira;
import crt2.ConfiguracaoTestesBatch;

public class TestR001BatchMigracaoDadosQuadroPosicaoFinanceira extends ConfiguracaoTestesBatch {
    
    public void executarBatch() {
        new BatchMigracaoDadosQuadroPosicaoFinanceira().init();
    }

}
