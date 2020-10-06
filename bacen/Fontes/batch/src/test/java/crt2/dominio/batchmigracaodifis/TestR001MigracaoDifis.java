package crt2.dominio.batchmigracaodifis;

import org.springframework.beans.factory.annotation.Autowired;

import br.gov.bcb.sisaps.batch.main.BatchMigracaoDifis;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.EncerrarCorecMediator;
import crt2.ConfiguracaoTestesBatch;

public class TestR001MigracaoDifis extends ConfiguracaoTestesBatch {
    
    @Autowired
    private CicloMediator cicloMediator;

    @Autowired
    private EncerrarCorecMediator encerrarCorecMediator;
    
    public void executarBatch() {
        BatchMigracaoDifis batch = new BatchMigracaoDifis();
        batch.init();
    }
    
    public void concluirCorec(Integer idCiclo) {
        Ciclo ciclo = cicloMediator.buscarCicloPorPK(idCiclo);
        encerrarCorecMediator.encerrarCorecCriarNovoCiclo(ciclo);
        limparSessao();
    }

}
