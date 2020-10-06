package br.gov.bcb.sisaps.integracao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.Hours;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.comum.excecoes.BCConsultaInvalidaException;
import br.gov.bcb.comum.pessoa.negocio.componenteorganizacional.ConsultaChefiaSubstitutoEventual;
import br.gov.bcb.sisaps.adaptadores.pessoa.ChefiaVO;
import br.gov.bcb.sisaps.adaptadores.pessoa.IBcPessoa;
import br.gov.bcb.sisaps.util.geral.DataUtil;

public class CacheChefiaPorSubstitutoEventual extends Cache<ChefiaVO> {

    private static final Log LOG = LogFactory.getLog(CacheChefiaPorSubstitutoEventual.class);

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
    
    public ChefiaVO get(String chave, ConsultaChefiaSubstitutoEventual consulta) throws BCConsultaInvalidaException {
        ObjetoCacheavel<ChefiaVO> objetoCacheavel = get(chave);
        
        if (objetoCacheavel == null 
                || Hours.hoursBetween(objetoCacheavel.getDataHoraAtualizacao(), DataUtil.getDateTimeAtual())
                    .getHours() > 23) {
            LOG.info("#CACHE CacheChefiaPorSubstitutoEventual. CHAVE: " + chave);
            ChefiaVO chefiaVO = ((IBcPessoa) SpringUtils.get().getBean(IBcPessoa.NOME))
                    .buscarChefiaPorSubstitutoEventual(consulta);
            objetoCacheavel = put(chave, chefiaVO);
        }
        
        return objetoCacheavel == null ? null : objetoCacheavel.getObjeto();
    }

}
