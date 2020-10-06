package br.gov.bcb.sisaps.batch.main;

import java.util.List;

import br.gov.bcb.sisaps.batch.AbstractBatchApplication;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.util.geral.DataUtil;
import br.gov.bcb.utils.logging.BCLogFactory;
import br.gov.bcb.utils.logging.BCLogger;

public class BatchCriarEstruturaANEF extends AbstractBatchApplication {

    private static final BCLogger LOG = BCLogFactory.getLogger("BatchCriarEstruturaANEF");
    
    public static void main(String[] args) {
        new BatchCriarEstruturaANEF().init();
    }
    
    @Override
    protected void executar() {
        LOG.info("ROTINA DE CRIAÇÃO DA ESTRUTURA DA ANÁLISE ECONÔMICO-FINANCEIRA " 
                + "PARA OS CICLOS EM ANDAMENTO JÁ EXISTENTES");
        LOG.info("Iniciado em: " + DataUtil.getDateTimeAtual());
        
        CicloMediator cicloMediator = (CicloMediator) getSpringContext().getBean("cicloMediator");
        
        // Buscar todos os ciclos 'Em andamento'
        LOG.info("Buscar todos os ciclos 'Em andamento'");
        List<Ciclo> ciclos = cicloMediator.consultarCiclosEmAndamento();
        LOG.info(ciclos.size() + " ciclos 'Em andamento' encontrados.");
        
        // Para cada ciclo
        for (Ciclo ciclo : ciclos) {
            cicloMediator.criarEstruturaANEF(ciclo);
        }
    }

}
