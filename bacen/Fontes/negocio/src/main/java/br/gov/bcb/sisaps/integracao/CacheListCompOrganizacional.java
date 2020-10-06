package br.gov.bcb.sisaps.integracao;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.Hours;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.comum.excecoes.BCConsultaInvalidaException;
import br.gov.bcb.comum.pessoa.negocio.componenteorganizacional.ConsultaComponentesOrganizacionais;
import br.gov.bcb.sisaps.adaptadores.pessoa.IBcPessoa;
import br.gov.bcb.sisaps.src.vo.ComponenteOrganizacionalVO;
import br.gov.bcb.sisaps.util.geral.DataUtil;

public class CacheListCompOrganizacional extends Cache<ArrayList<ComponenteOrganizacionalVO>> {

    private static final Log LOG = LogFactory.getLog(CacheListCompOrganizacional.class);

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

    public ArrayList<ComponenteOrganizacionalVO> get(String rotulo, String dataAtual)
            throws BCConsultaInvalidaException {
        ObjetoCacheavel<ArrayList<ComponenteOrganizacionalVO>> objetoCacheavel = get(rotulo + UNDERLINE + dataAtual);

        if (objetoCacheavel == null || Hours
                .hoursBetween(objetoCacheavel.getDataHoraAtualizacao(), DataUtil.getDateTimeAtual()).getHours() > 23) {
            LOG.info("#CACHE CacheListCompOrganizacional. CHAVE: " + rotulo + UNDERLINE + dataAtual);
            ConsultaComponentesOrganizacionais consulta = new ConsultaComponentesOrganizacionais();
            consulta.setRotuloInicio(rotulo);
            ArrayList<ComponenteOrganizacionalVO> componente = ((IBcPessoa) SpringUtils.get().getBean(IBcPessoa.NOME))
                    .consultarComponentesOrganizacionais(consulta);
            objetoCacheavel = put(rotulo + UNDERLINE + dataAtual, componente);
        }

        return objetoCacheavel == null ? null : objetoCacheavel.getObjeto();
    }

}