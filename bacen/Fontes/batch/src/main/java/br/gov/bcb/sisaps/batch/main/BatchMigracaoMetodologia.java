package br.gov.bcb.sisaps.batch.main;

import java.util.List;

import br.gov.bcb.sisaps.batch.AbstractBatchApplication;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.MetodologiaMediator;
import br.gov.bcb.utils.logging.BCLogFactory;
import br.gov.bcb.utils.logging.BCLogger;

public class BatchMigracaoMetodologia extends AbstractBatchApplication {

    private static final String MIGRACAO_SITUACAO_1 = "migracao1";
    private static final String MIGRACAO_SITUACAO_2 = "migracao2";
    private static final String MIGRACAO_SITUACAO_3 = "migracao3";
    private static final BCLogger LOG = BCLogFactory.getLogger("BatchMigracaoMetodologia");
    protected Boolean possuiErros = false;

    public static void main(String[] args) {
        new BatchMigracaoMetodologia().init();
    }

    @Override
    protected void executar() {
        LOG.info("INÍCIO DA ROTINA DE MIGRAÇÃO DE DADOS PARA NOVA METODOLOGIA.");
        CicloMediator cicloMediator = (CicloMediator) getSpringContext().getBean("cicloMediator");

        // Ciclos em andamento.
        List<Ciclo> ciclosAndamento = cicloMediator.consultarCiclosAndamentoNovaMetodologia();
        LOG.info("QUANTIDADE DE CICLOS EM ANDAMENTO: " + ciclosAndamento.size());
        
        // Ciclos em corec.
//        List<Ciclo> ciclosCorec1 = cicloMediator.consultarCiclosNovaMetodologiaCorec(MIGRACAO_SITUACAO_1);
//        LOG.info("QUANTIDADE DE CICLOS COREC PRA SITUAÇÃO 1: " + ciclosCorec1.size());
//        List<Ciclo> ciclosCorec2 = cicloMediator.consultarCiclosNovaMetodologiaCorec(MIGRACAO_SITUACAO_2);
//        LOG.info("QUANTIDADE DE CICLOS COREC PRA SITUAÇÃO 2: " + ciclosCorec2.size());
//        List<Ciclo> ciclosCorec3 = cicloMediator.consultarCiclosNovaMetodologiaCorec(MIGRACAO_SITUACAO_3);
//        LOG.info("QUANTIDADE DE CICLOS COREC PRA SITUAÇÃO 3: " + ciclosCorec3.size());
        
        try {
            criarDados(ciclosAndamento);
            // CHECKSTYLE:OFF
        } catch (Throwable e) {
            LOG.info("ERRO: " + e.getClass());
            LOG.info("CAUSA: " + e.getCause());
            LOG.info(e.getMessage());
            LOG.info(e.getLocalizedMessage());
        } finally {
            LOG.info("FIM DA ROTINA DE MIGRAÇÃO DE DADOS PARA NOVA METODOLOGIA.");
            // retirar para rodar teste local
            // System.exit(0);
        }
    }

    private void criarDados(List<Ciclo> ciclosAndamento) {
        MetodologiaMediator metodologiaMediator =
                (MetodologiaMediator) getSpringContext().getBean("metodologiaMediator");
        for (Ciclo ciclo : ciclosAndamento) {
            LOG.info("PROCESSANDO CICLO: " + ciclo.getPk());
            metodologiaMediator.migrarDadosNovaMetodologia(ciclo.getPk(), true, true);
        }
//        for (Ciclo ciclo : ciclosCorec1) {
//            LOG.info("PROCESSANDO CICLO: " + ciclo.getPk());
//            metodologiaMediator.migrarDadosNovaMetodologia(ciclo.getPk(), true, true);
//        }
//        for (Ciclo ciclo : ciclosCorec2) {
//            LOG.info("PROCESSANDO CICLO: " + ciclo.getPk());
//            metodologiaMediator.migrarDadosNovaMetodologia(ciclo.getPk(), false, false);
//        }
//        for (Ciclo ciclo : ciclosCorec3) {
//            LOG.info("PROCESSANDO CICLO: " + ciclo.getPk());
//            metodologiaMediator.migrarDadosNovaMetodologia(ciclo.getPk(), false, true);
//        }
    }
}