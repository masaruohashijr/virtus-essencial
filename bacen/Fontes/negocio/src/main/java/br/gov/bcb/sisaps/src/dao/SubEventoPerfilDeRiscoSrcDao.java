package br.gov.bcb.sisaps.src.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.gov.bcb.sisaps.dao.GenericDAOLocal;
import br.gov.bcb.sisaps.src.dominio.EventoPerfilDeRiscoSrc;
import br.gov.bcb.sisaps.src.dominio.SubEventoPerfilDeRiscoSrc;
import br.gov.bcb.sisaps.util.enumeracoes.TipoSubEventoPerfilRiscoSRC;

@Repository
public class SubEventoPerfilDeRiscoSrcDao extends GenericDAOLocal<SubEventoPerfilDeRiscoSrc, Integer> {

    public SubEventoPerfilDeRiscoSrcDao() {
        super(SubEventoPerfilDeRiscoSrc.class);
    }

    @SuppressWarnings("unchecked")
    public SubEventoPerfilDeRiscoSrc buscarSubEventoPerfil(EventoPerfilDeRiscoSrc eventoPerfil,
            TipoSubEventoPerfilRiscoSRC tipo) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq("eventoPerfilDeRiscoSrc", eventoPerfil));
        criteria.add(Restrictions.eq("tipo", tipo));
        return (SubEventoPerfilDeRiscoSrc) criteria.uniqueResult();
    }


}
