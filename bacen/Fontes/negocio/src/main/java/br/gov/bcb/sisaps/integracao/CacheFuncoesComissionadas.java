package br.gov.bcb.sisaps.integracao;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.Hours;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.comum.excecoes.BCConsultaInvalidaException;
import br.gov.bcb.sisaps.adaptadores.pessoa.FuncaoComissionadaVO;
import br.gov.bcb.sisaps.adaptadores.pessoa.IBcPessoa;
import br.gov.bcb.sisaps.util.geral.DataUtil;

public class CacheFuncoesComissionadas extends Cache<ArrayList<FuncaoComissionadaVO>> {

    private static final Log LOG = LogFactory.getLog(CacheFuncoesComissionadas.class);

    public static final String CHAVE_FUNCOES = "funcoesComissionadas";
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
                            runCacheFuncoes();
                        }

                    }).start();
                }
            };

        }
        return carregador;
    }

    //CHECKSTYLE:OFF
	private void runCacheFuncoes() {
		Cache.getInstancia(CacheFuncoesComissionadas.class).clear();
		try {
            getFuncoes();
		} catch (Throwable t) {
			t.printStackTrace();
			// Morreu a thread. Um minuto de silêncio em sua memória...
		}
	}
    //CHECKSTYLE:ON

    @Override
    public int getTempoExpiracao() {
        return TEMPO_EXPIRACAO;
    }
    
    public ArrayList<FuncaoComissionadaVO> getFuncoes() throws BCConsultaInvalidaException {
        ObjetoCacheavel<ArrayList<FuncaoComissionadaVO>> objetoCacheavel = get(CHAVE_FUNCOES);
        
        if (objetoCacheavel == null 
                || Hours.hoursBetween(objetoCacheavel.getDataHoraAtualizacao(), DataUtil.getDateTimeAtual())
                    .getHours() > 23) {
            LOG.info("#CACHE CacheFuncoesComissionadas. CHAVE: " + CHAVE_FUNCOES);
            ArrayList<FuncaoComissionadaVO> funcoes = ((IBcPessoa) SpringUtils.get().getBean(IBcPessoa.NOME))
                    .consultarTodasFuncoesComissionadas();
            objetoCacheavel = put(CHAVE_FUNCOES, funcoes);
        }
        
        return objetoCacheavel == null ? null : objetoCacheavel.getObjeto();
    }

}