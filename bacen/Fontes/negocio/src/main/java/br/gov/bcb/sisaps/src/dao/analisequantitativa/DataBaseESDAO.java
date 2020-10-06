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
package br.gov.bcb.sisaps.src.dao.analisequantitativa;

import static br.gov.bcb.app.stuff.util.props.PropertyUtils.getPropertyObject;
import static br.gov.bcb.app.stuff.util.props.PropertyUtils.property;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.DataBaseES;


/**
 * Este DAO trata de uma entidade com chave composta e não realiza persistência de dados, por isso
 * ele não faz parte na hierarquia de DAOs comum do sistema, que usam o GenericDAO.
 */
@Repository
@Transactional
public class DataBaseESDAO {

    private static final int QUANTIDADE_REGISTROS = 12;

    @Autowired 
    protected SessionFactory sessionFactory;
    
    public DataBaseESDAO() {
    }
    
    public Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    @SuppressWarnings("unchecked")
    public List<DataBaseES> listarDataBaseES(PerfilRisco perfilRisco) {
        Criteria criteria = getCurrentSession().createCriteria(DataBaseES.class);
        criteria.addOrder(Order.desc(property(getPropertyObject(DataBaseES.class).getCodigoDataBase())));
        criteria.add(Restrictions.eq(property(getPropertyObject(DataBaseES.class).getCnpjES()), perfilRisco.getCiclo()
                .getEntidadeSupervisionavel().getConglomeradoOuCnpj()));
        criteria.setMaxResults(QUANTIDADE_REGISTROS);
        return (List<DataBaseES>) criteria.list();
    }

    public DataBaseES obterDataBaseESRecente(PerfilRisco perfilRisco) {
        Criteria criteria = getCurrentSession().createCriteria(DataBaseES.class);
        String cnpj = perfilRisco.getCiclo().getEntidadeSupervisionavel().getConglomeradoOuCnpj();
        criteria.add(Restrictions.eq(property(getPropertyObject(DataBaseES.class).getCnpjES()), cnpj));
        criteria.setProjection(Projections.max(property(getPropertyObject(DataBaseES.class).getCodigoDataBase())));
        return buscarPorCodigoDataBase((Integer) criteria.uniqueResult(), cnpj);
    }

    public DataBaseES buscarPorCodigoDataBase(Integer codigoDataBase, String cnpj) {
        Criteria criteria = getCurrentSession().createCriteria(DataBaseES.class);
        criteria.add(Restrictions.eq(property(getPropertyObject(DataBaseES.class).getCodigoDataBase()), codigoDataBase));
        criteria.add(Restrictions.eq(property(getPropertyObject(DataBaseES.class).getCnpjES()), cnpj));
        return (DataBaseES) criteria.uniqueResult();
    }
}
