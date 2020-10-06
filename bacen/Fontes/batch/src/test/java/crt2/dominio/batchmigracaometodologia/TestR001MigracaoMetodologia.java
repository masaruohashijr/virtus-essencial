package crt2.dominio.batchmigracaometodologia;

import br.gov.bcb.sisaps.batch.main.BatchMigracaoMetodologia;
import crt2.ConfiguracaoTestesBatch;

public class TestR001MigracaoMetodologia extends ConfiguracaoTestesBatch {
    
    public void executarBatch() {
        BatchMigracaoMetodologia batch = new BatchMigracaoMetodologia();
        batch.init();
    }
    
}
