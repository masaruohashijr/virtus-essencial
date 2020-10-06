package br.gov.bcb.sisaps.src.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.gov.bcb.sisaps.dao.GenericDAOLocal;
import br.gov.bcb.sisaps.src.dominio.EventoConsolidado;
import br.gov.bcb.sisaps.src.dominio.EventoPerfilDeRiscoSrc;

@Repository
public class EventoPerfilDeRiscoSrcDao extends GenericDAOLocal<EventoPerfilDeRiscoSrc, Integer> {

    public EventoPerfilDeRiscoSrcDao() {
        super(EventoPerfilDeRiscoSrc.class);
    }

    @SuppressWarnings("unchecked")
    public EventoPerfilDeRiscoSrc getEventoPerfilRiscoPorConsolidado(EventoConsolidado eventoConsolidado) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq("eventoConsolidado", eventoConsolidado));
        return (EventoPerfilDeRiscoSrc) criteria.uniqueResult();
    }

    public List<EventoPerfilDeRiscoSrc> buscarEventosPerfilDeRiscoSrc(String cnpj) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.createAlias("perfilRisco", "perfil");
        criteria.createAlias("perfil.ciclo", "ciclo");
        criteria.createAlias("ciclo.entidadeSupervisionavel", "es");
        criteria.add(Restrictions.eq("es.conglomeradoOuCnpj", cnpj));
        return criteria.list();
    }

}
