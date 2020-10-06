package br.gov.bcb.sisaps.src.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.gov.bcb.app.stuff.dao.GenericDAO;
import br.gov.bcb.app.stuff.util.props.PropertyUtils;
import br.gov.bcb.sisaps.src.dominio.ParametroElemento;
import br.gov.bcb.sisaps.src.dominio.ParametroItemElemento;
import br.gov.bcb.sisaps.util.geral.ColecaoChecada;
import br.gov.bcb.sisaps.util.geral.SisapsExcecaoUtil;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistente;

@Repository
public class ParametroItemElementoDAO extends GenericDAO<ParametroItemElemento, Integer> {

    private static final ParametroItemElemento PROP_COMPONENTE = PropertyUtils
            .getPropertyObject(ParametroItemElemento.class);
    private static final String PROP_ANALISE = PropertyUtils.property(PROP_COMPONENTE.getParametroElemento());

    public ParametroItemElementoDAO() {
        super(ParametroItemElemento.class);
    }

    public List<ParametroItemElemento> buscarItensAvaliarPorIdParametroElemento(
            ParametroElemento parametroElemento) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        addRestrictionEq(criteria, concat(PROP_ANALISE, SisapsExcecaoUtil.PONTO, ObjetoPersistente.PROP_ID),
                parametroElemento.getPk());
        return ColecaoChecada.checkedList(criteria.list(), ParametroItemElemento.class);
    }
    
    private String concat(String... textos) {
        return StringUtils.join(textos, SisapsExcecaoUtil.PONTO).replaceAll("\\.+", SisapsExcecaoUtil.PONTO);
    }

    protected void addRestrictionEq(Criteria criteria, String propertyName, Object value) {
        if (SisapsUtil.isNaoNuloOuVazio(value)) {
            criteria.add(Restrictions.eq(propertyName, value));
        }
    }
}
