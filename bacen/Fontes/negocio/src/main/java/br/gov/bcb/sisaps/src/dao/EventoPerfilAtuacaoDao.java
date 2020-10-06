package br.gov.bcb.sisaps.src.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.gov.bcb.sisaps.dao.GenericDAOLocal;
import br.gov.bcb.sisaps.src.dominio.EventoPerfilAtuacao;

@Repository
public class EventoPerfilAtuacaoDao extends GenericDAOLocal<EventoPerfilAtuacao, Integer> {

    public EventoPerfilAtuacaoDao() {
        super(EventoPerfilAtuacao.class);
    }

    @SuppressWarnings("unchecked")
    public List<EventoPerfilAtuacao> buscarEventosPerfilAtuacao(String cnpj) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq("cnpjCodigoOrigemSrc", cnpj));
        return criteria.list();
    }

}
