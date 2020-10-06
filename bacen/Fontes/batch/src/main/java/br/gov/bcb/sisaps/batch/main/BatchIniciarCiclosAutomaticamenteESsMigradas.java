package br.gov.bcb.sisaps.batch.main;

import java.util.List;

import org.hibernate.HibernateException;

import br.gov.bcb.app.stuff.exception.NegocioException;
import br.gov.bcb.sisaps.batch.AbstractBatchApplication;
import br.gov.bcb.sisaps.src.dao.EntidadeSupervisionavelETLDAO;
import br.gov.bcb.sisaps.src.dominio.EntidadeSupervisionavelETL;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.util.geral.DataUtil;
import br.gov.bcb.utils.logging.BCLogFactory;
import br.gov.bcb.utils.logging.BCLogger;

public class BatchIniciarCiclosAutomaticamenteESsMigradas extends AbstractBatchApplication {
    
    private static final BCLogger LOG = BCLogFactory.getLogger("BatchIniciarCiclosAutomaticamenteESsMigradas");
    private static final String ERRO = "###### ERRO: ";
    
    public static void main(String[] args) {
        new BatchIniciarCiclosAutomaticamenteESsMigradas().init();
    }

    @Override
    protected void executar() {
        LOG.info("ROTINA DE INICIALIZAÇÃO DOS CICLOS DAS ES'S MIGRADAS DO SIGAS");
        LOG.info("Iniciado em: " + DataUtil.getDateTimeAtual());
        
        EntidadeSupervisionavelETLDAO entidadeSupervisionavelETLDAO = 
                (EntidadeSupervisionavelETLDAO) getSpringContext().getBean("entidadeSupervisionavelETLDAO");
        CicloMediator cicloMediator = (CicloMediator) getSpringContext().getBean("cicloMediator");
        
        // Consultar todas as ESs que estão na tabela do ETL (EET_ETL_ENS) que tem o indicador que pertence ao SRC
        List<EntidadeSupervisionavelETL> entidadesETL = entidadeSupervisionavelETLDAO.buscarEntidadesETLPertenceSRC();
        LOG.info(entidadesETL.size() + " ES'S foram encontrados na tabela EET_ETL_ENS.");
        
        try {
            cicloMediator.iniciarCiclosAutomaticamente(entidadesETL);
        } catch (NegocioException e) {
            LOG.info(ERRO + e.getMessage());
        } catch (HibernateException e) {
            LOG.info(ERRO + e.getMessage());
        }
    }
    
}
