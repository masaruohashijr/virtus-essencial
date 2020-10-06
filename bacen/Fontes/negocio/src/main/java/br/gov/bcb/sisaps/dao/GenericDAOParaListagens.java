/*
 * Sistema APS
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arquivo contém informações proprietárias.
 */
package br.gov.bcb.sisaps.dao;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.hibernate.IObjetoPersistente;
import br.gov.bcb.app.stuff.seguranca.UsuarioCorrente;
import br.gov.bcb.sisaps.seguranca.UsuarioAplicacao;
import br.gov.bcb.sisaps.src.vo.ObjetoPersistenteVO;
import br.gov.bcb.sisaps.util.consulta.Consulta;
import br.gov.bcb.sisaps.util.consulta.IPaginado;
import br.gov.bcb.sisaps.util.consulta.Ordenacao;

@Repository
public abstract class GenericDAOParaListagens<T extends IObjetoPersistente<PK>, 
        PK extends Serializable, V extends ObjetoPersistenteVO, C extends Consulta<V>>
        extends GenericDAOLocal<T, PK> implements IPaginado<V, PK, C> {
    protected static final String UNCHECKED = "unchecked";
    protected Class<V> classeVO;

    public GenericDAOParaListagens(Class<T> type) {
        super(type);
    }

    public GenericDAOParaListagens(Class<T> type, Class<V> typeVO) {
        super(type);
        this.classeVO = typeVO;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public V buscar(C consulta) {
        List<V> list = consultar(consulta);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public List<V> consultar(C consulta) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        montarPaginacao(consulta, criteria);
        montarCriterios(criteria, consulta);
        montarOrdenacao(criteria, consulta);
        criteria.setResultTransformer(new AliasToBeanResultTransformer(classeVO));
        return criteria.list();
    }

    protected void montarPaginacao(C consulta, Criteria criteria) {
        if (consulta.isPaginada()) {
            if (consulta.getInicio() != null) {
                criteria.setFirstResult(consulta.getInicio().intValue());
            }
            if (consulta.getQuantidade() != null) {
                criteria.setMaxResults(consulta.getQuantidade().intValue());
            }
        }
    }

    protected abstract void montarCriterios(Criteria criteria, C consulta);

    protected void montarOrdenacao(Criteria criteria, C consulta) {
        if (consulta.getOrdenacao() != null) {
            ordenacao(criteria, Arrays.asList(consulta.getOrdenacao()));
        }
        if (consulta.getOrdenacaoSecundaria() != null) {
            ordenacao(criteria, consulta.getOrdenacaoSecundaria());
        }
    }

    protected void ordenacao(Criteria criteria, List<Ordenacao> ordem) {
        for (Ordenacao o : ordem) {
            Order order = null;
            if (o.isCrescente()) {
                order = Order.asc(o.getPropriedade());
            } else {
                order = Order.desc(o.getPropriedade());
            }

            criteria.addOrder(order);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Long total(C consulta) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        montarCriterios(criteria, consulta);
        criteria.setProjection(Projections.rowCount());
        return (Long) criteria.uniqueResult();
    }
    
    protected UsuarioAplicacao usuarioAplicacao() {
        return ((UsuarioAplicacao) UsuarioCorrente.get());
    }


}