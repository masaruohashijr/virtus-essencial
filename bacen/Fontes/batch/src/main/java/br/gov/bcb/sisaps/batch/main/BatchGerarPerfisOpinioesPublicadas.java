package br.gov.bcb.sisaps.batch.main;

import br.gov.bcb.sisaps.batch.AbstractBatchApplication;
import br.gov.bcb.sisaps.src.mediator.PerfilAtuacaoESMediator;
import br.gov.bcb.sisaps.util.geral.DataUtil;
import br.gov.bcb.utils.logging.BCLogFactory;
import br.gov.bcb.utils.logging.BCLogger;

public class BatchGerarPerfisOpinioesPublicadas extends AbstractBatchApplication {

    private static final BCLogger LOG = BCLogFactory.getLogger("BatchGerarPerfisOpinioesPublicadas");
    
    public static void main(String[] args) {
        new BatchGerarPerfisOpinioesPublicadas().init();
    }
    
    @Override
    protected void executar() {
        LOG.info("ROTINA DE CRIAÇÃO DA ESTRUTURA DO BATCH DE PERFIL DE ATUAÇÃO E OPINIÃO CONCLUSIVA ");
        LOG.info("Iniciado em: " + DataUtil.getDateTimeAtual());
        
        LOG.info("PERFIL ATUAÇÃO GERANDO VERSÕES PUBLICADAS :");
        PerfilAtuacaoESMediator.get().rotinaBatchGerarPerfilsOpinioes();   

    }

}
