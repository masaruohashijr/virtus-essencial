package br.gov.bcb.sisaps.batch.main;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.sisaps.batch.AbstractBatchApplication;

import br.gov.bcb.sisaps.src.mediator.EnvioEmailMediator;

import br.gov.bcb.utils.logging.BCLogFactory;
import br.gov.bcb.utils.logging.BCLogger;

public class BatchEnviarEmailApresentacao extends AbstractBatchApplication {

    private static final BCLogger LOG = BCLogFactory.getLogger("BatchEnviarEmailApresentacao");

    public static void main(String[] args) {
        new BatchEnviarEmailApresentacao().init();
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    protected void executar() {
        LOG.info("ROTINA DE AUTOMATICA DE ENVIO DE E-MAIL APRESENTACAO");
        try {
            EnvioEmailMediator envioMediator = (EnvioEmailMediator) getSpringContext().getBean("envioEmailMediator");
            envioMediator.rotinaBatchApresentacao();
        } catch (Exception e) {
            LOG.info(e.toString());
        } finally {
            //retirar para rodar teste local
            System.exit(0);
        }
    }

}
