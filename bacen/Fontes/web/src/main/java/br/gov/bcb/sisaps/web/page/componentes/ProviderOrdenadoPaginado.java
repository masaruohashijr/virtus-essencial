package br.gov.bcb.sisaps.web.page.componentes;

import java.io.Serializable;
import java.util.Iterator;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import br.gov.bcb.sisaps.src.vo.ObjetoPersistenteVO;
import br.gov.bcb.sisaps.util.consulta.Consulta;
import br.gov.bcb.sisaps.util.consulta.IPaginado;
import br.gov.bcb.sisaps.util.consulta.Ordenacao;
import br.gov.bcb.sisaps.web.page.componentes.tabela.ProviderOrdenado;

public class ProviderOrdenadoPaginado<T extends ObjetoPersistenteVO, PK extends Serializable, Q extends Consulta<T>>
        extends ProviderOrdenado<T> {

    private IPaginado<T, PK, Q> paginador;
    private IModel<Q> modelConsulta;

    public ProviderOrdenadoPaginado(String propriedade, SortOrder ordem, IPaginado<T, PK, Q> paginador,
            IModel<Q> modelConsulta) {
        super(propriedade, ordem);
        this.paginador = paginador;
        this.modelConsulta = modelConsulta;
    }

    @Override
    public Iterator<? extends T> iterator(String propriedade, SortOrder ordem, long inicio, long quantidade) {
        Q consulta = modelConsulta.getObject();
        consulta.setPaginada(true);
        Ordenacao ordenacao = new Ordenacao();
        ordenacao.setPropriedade(propriedade);
        ordenacao.setCrescente(ordem == SortOrder.ASCENDING);
        ordenacao.setNulosNoInicio(Boolean.TRUE);
        consulta.setOrdenacao(ordenacao);
        consulta.setInicio(inicio);
        consulta.setQuantidade(quantidade);
        return paginador.consultar(consulta).iterator();
    }

    @Override
    public long size() {
        Q consulta = modelConsulta.getObject();
        return paginador.total(consulta);
    }

    @Override
    public IModel<T> model(final T object) {
        return new AbstractReadOnlyModel<T>() {

            @Override
            public T getObject() {
                return object;
            }
        };
    }
}
