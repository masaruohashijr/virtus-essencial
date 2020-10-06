package br.gov.bcb.sisaps.integracao;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.Hours;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.comum.excecoes.BCConsultaInvalidaException;
import br.gov.bcb.sisaps.adaptadores.pessoa.IBcPessoa;
import br.gov.bcb.sisaps.src.vo.ComponenteOrganizacionalVO;
import br.gov.bcb.sisaps.util.geral.DataUtil;

public class CacheUnidades extends Cache<ArrayList<ComponenteOrganizacionalVO>> {

    private static final Log LOG = LogFactory.getLog(CacheUnidades.class);

    private static final String CHAVE_UNIDADES_ATIVAS = "unidadesAtivas";
    private Runnable carregador;

    @Override
    public Runnable getCarregador() {

        if (carregador == null) {
            carregador = new Runnable() {

                public void run() {

                    // Precisa iniciar outra thread porque a abertura
                    // da sessão hibernate bloqueia o agendamento da thread raiz
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            runCacheUnidades();
                        }

                    }).start();
                }
            };

        }
        return carregador;
    }

    //CHECKSTYLE:OFF
	private void runCacheUnidades() {
		Cache.getInstancia(CacheUnidades.class).clear();

		try {
            getUnidades();
		} catch (Throwable t) {
			t.printStackTrace();
			// Morreu a thread. Um minuto de silêncio em sua memória...
		}
	}
    //CHECKSTYLE:ON
    
    public ArrayList<ComponenteOrganizacionalVO> getUnidades() throws BCConsultaInvalidaException {
        ObjetoCacheavel<ArrayList<ComponenteOrganizacionalVO>> objetoCacheavel = get(CHAVE_UNIDADES_ATIVAS);
        
        if (objetoCacheavel == null 
                || Hours.hoursBetween(objetoCacheavel.getDataHoraAtualizacao(), DataUtil.getDateTimeAtual())
                    .getHours() > 23) {
            LOG.info("#CACHE CacheUnidades. CHAVE: " + CHAVE_UNIDADES_ATIVAS);
            ArrayList<ComponenteOrganizacionalVO> funcoes = ((IBcPessoa) SpringUtils.get().getBean(IBcPessoa.NOME))
                    .consultarUnidadesAtivas();
            objetoCacheavel = put(CHAVE_UNIDADES_ATIVAS, funcoes);
        }
        
        return objetoCacheavel == null ? null : objetoCacheavel.getObjeto();
    }

    @Override
    public int getTempoExpiracao() {
        return TEMPO_EXPIRACAO;
    }

}