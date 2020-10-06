package br.gov.bcb.sisaps.batch.main;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.sisaps.batch.AbstractBatchApplication;
import br.gov.bcb.sisaps.src.dominio.AgendaCorec;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.EmailCorec;
import br.gov.bcb.sisaps.src.dominio.ParametroEmail;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoEmailCorecEnum;
import br.gov.bcb.sisaps.src.mediator.AgendaCorecMediator;
import br.gov.bcb.sisaps.src.mediator.CargaParticipanteMediator;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.EmailCorecMediator;
import br.gov.bcb.sisaps.src.mediator.EnvioEmailMediator;
import br.gov.bcb.sisaps.src.mediator.ObservacaoAgendaCorecMediator;
import br.gov.bcb.sisaps.src.mediator.ParametroEmailMediator;
import br.gov.bcb.sisaps.src.vo.CicloVO;
import br.gov.bcb.sisaps.src.vo.ParticipanteComiteVO;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.geral.DataUtil;
import br.gov.bcb.utils.logging.BCLogFactory;
import br.gov.bcb.utils.logging.BCLogger;

public class BatchEnviarEmailDisponibilidade extends AbstractBatchApplication {

    private static final BCLogger LOG = BCLogFactory.getLogger("BatchEnviarEmailDisponibilidade");

    public static void main(String[] args) {
        new BatchEnviarEmailDisponibilidade().init();
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    protected void executar() {
        
        LOG.info("ROTINA DE AUTOMATICA DE ENVIO DE E-MAIL Disponibilidade");
        try {
            EnvioEmailMediator envioMediator = (EnvioEmailMediator) getSpringContext().getBean("envioEmailMediator");
            envioMediator.rotinaBatchDisponibilidade();
        } catch (Exception e) {
            LOG.info(e.toString());
        } finally {
            //retirar para rodar teste local
            System.exit(0);
        }

      
        
    }

  

}
