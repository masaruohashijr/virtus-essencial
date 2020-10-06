package crt2.dominio.perfilderisco.test;

import br.gov.bcb.sisaps.batch.main.BatchIniciarCiclosAutomaticamenteESsMigradas;
import crt2.ConfiguracaoTestesBatch;

public class TestCricaoAutomaticaCicloESsMigradas extends ConfiguracaoTestesBatch {
    
    public void executarBatch() {
        new BatchIniciarCiclosAutomaticamenteESsMigradas().init();
    }
    
}
