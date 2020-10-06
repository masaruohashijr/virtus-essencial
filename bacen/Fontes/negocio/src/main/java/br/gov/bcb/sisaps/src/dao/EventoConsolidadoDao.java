package br.gov.bcb.sisaps.src.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.gov.bcb.sisaps.dao.GenericDAOLocal;
import br.gov.bcb.sisaps.src.dominio.Consolidado;
import br.gov.bcb.sisaps.src.dominio.EventoConsolidado;
import br.gov.bcb.sisaps.util.enumeracoes.TipoEventoConsolidadoEnum;
import br.gov.bcb.sisaps.util.geral.DataUtil;

@Repository
public class EventoConsolidadoDao extends GenericDAOLocal<EventoConsolidado, Integer> {

    private static final String ULTIMA_ATUALIZACAO = "ultimaAtualizacao";

    public EventoConsolidadoDao() {
        super(EventoConsolidado.class);
    }

    @SuppressWarnings("unchecked")
    public List<EventoConsolidado> buscarEventos() {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        return criteria.list();
    }

    public EventoConsolidado buscarEventoConsolidadoPerfilRiscoDataAtual(Consolidado consolidado) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq("tipo", TipoEventoConsolidadoEnum.PERFIL_DE_RISCO));
        criteria.add(Restrictions.eq("consolidado", consolidado));
        criteria.add(Restrictions.and(
                Restrictions.ge(ULTIMA_ATUALIZACAO, DataUtil.getDateTimeAtual().withTimeAtStartOfDay()),
                Restrictions.lt(ULTIMA_ATUALIZACAO, DataUtil.getDateTimeAtual().plusDays(1).withTimeAtStartOfDay())));
        return (EventoConsolidado) criteria.uniqueResult();
    }

}
