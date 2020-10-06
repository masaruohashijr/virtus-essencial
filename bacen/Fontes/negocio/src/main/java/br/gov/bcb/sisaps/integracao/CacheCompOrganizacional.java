package br.gov.bcb.sisaps.integracao;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.Hours;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.comum.excecoes.BCConsultaInvalidaException;
import br.gov.bcb.sisaps.adaptadores.pessoa.IBcPessoa;
import br.gov.bcb.sisaps.src.vo.ComponenteOrganizacionalVO;
import br.gov.bcb.sisaps.util.geral.DataUtil;

public class CacheCompOrganizacional extends Cache<ComponenteOrganizacionalVO> {

    private static final Log LOG = LogFactory.getLog(CacheCompOrganizacional.class);

    private static final String UNDERLINE = "_";
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
    
    public ComponenteOrganizacionalVO get(String rotulo, Date dataBase, String dataChave)
            throws BCConsultaInvalidaException {
        ObjetoCacheavel<ComponenteOrganizacionalVO> objetoCacheavel = get(rotulo + UNDERLINE + dataChave);
        
        if (objetoCacheavel == null 
                || Hours.hoursBetween(objetoCacheavel.getDataHoraAtualizacao(), DataUtil.getDateTimeAtual())
                    .getHours() > 23) {
            LOG.info("#CACHE CacheCompOrganizacional. CHAVE: " + rotulo + UNDERLINE + dataChave);
            ComponenteOrganizacionalVO componente = ((IBcPessoa) SpringUtils.get().getBean(IBcPessoa.NOME))
                    .buscarComponenteOrganizacionalPorRotulo(rotulo, dataBase);
            objetoCacheavel = put(rotulo + UNDERLINE + dataChave, componente);
        }
        
        return objetoCacheavel == null ? null : objetoCacheavel.getObjeto();
    }
    

}