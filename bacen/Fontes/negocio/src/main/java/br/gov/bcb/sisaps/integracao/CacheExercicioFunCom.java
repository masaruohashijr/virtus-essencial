package br.gov.bcb.sisaps.integracao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.Hours;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.comum.excecoes.BCConsultaInvalidaException;
import br.gov.bcb.comum.pessoa.negocio.funcaocomissionada.ConsultaExercicioFuncaoComissionada;
import br.gov.bcb.sisaps.adaptadores.pessoa.ExercicioFuncaoComissionadaVO;
import br.gov.bcb.sisaps.adaptadores.pessoa.IBcPessoa;
import br.gov.bcb.sisaps.util.geral.DataUtil;

public class CacheExercicioFunCom extends Cache<ExercicioFuncaoComissionadaVO> {

    private static final Log LOG = LogFactory.getLog(CacheExercicioFunCom.class);

    private Runnable carregador;

    @Override
    public Runnable getCarregador() {

        if (carregador == null) {
            carregador = new Runnable() {

                public void run() {
                    // Não precisa de carregamento. É um cache à moda antiga.
                }

            };

        }
        return carregador;
    }

    @Override
    public int getTempoExpiracao() {
        return TEMPO_EXPIRACAO;
    }
    
    public ExercicioFuncaoComissionadaVO getExercicio(String chave) throws BCConsultaInvalidaException {
        ObjetoCacheavel<ExercicioFuncaoComissionadaVO> objetoCacheavel = get(chave);
        
        if (objetoCacheavel == null 
                || Hours.hoursBetween(objetoCacheavel.getDataHoraAtualizacao(), DataUtil.getDateTimeAtual())
                    .getHours() > 23) {
            LOG.info("#CACHE CacheExercicioFunCom. CHAVE: " + chave);
            ConsultaExercicioFuncaoComissionada consulta = new ConsultaExercicioFuncaoComissionada();
            consulta.setMatricula(chave);
            ExercicioFuncaoComissionadaVO funcao = ((IBcPessoa) SpringUtils.get().getBean(IBcPessoa.NOME))
                    .buscarExercicioFuncaoComissionada(consulta);
            objetoCacheavel = put(chave, funcao);
        }
        
        return objetoCacheavel == null ? null : objetoCacheavel.getObjeto();
    }

}