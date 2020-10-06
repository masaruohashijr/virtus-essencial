package crt2.dominio.perfilderisco.test;

import org.junit.Ignore;

import br.gov.bcb.sisaps.batch.main.BatchMigracaoDadosETLAtividadesCiclo;
import crt2.ConfiguracaoTestesBatch;

@Ignore
public class TestAPSFW0202_Perfil_de_Risco_Acoes_Ciclo extends ConfiguracaoTestesBatch {
    
    public void executarBatch() {
        new BatchMigracaoDadosETLAtividadesCiclo().init();
    }
    
}
