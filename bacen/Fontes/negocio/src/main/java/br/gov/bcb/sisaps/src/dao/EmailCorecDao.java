package br.gov.bcb.sisaps.src.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.gov.bcb.sisaps.dao.GenericDAOLocal;
import br.gov.bcb.sisaps.src.dominio.EmailCorec;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoEmailCorecEnum;

@Repository
public class EmailCorecDao extends GenericDAOLocal<EmailCorec, Integer> {

    private static final String TIPO = "tipo";
    private static final String AGENDA_PK = "agenda.pk";

    public EmailCorecDao() {
        super(EmailCorec.class);
    }

    public List<EmailCorec> buscarListEmailParticipante(Integer agendaPK, TipoEmailCorecEnum tipo) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(AGENDA_PK, agendaPK));
        criteria.add(Restrictions.eq(TIPO, tipo));
        return cast(criteria.list());
    }

    public EmailCorec buscarEmailParticipante(Integer agendaPK, String matricula, TipoEmailCorecEnum tipo) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(AGENDA_PK, agendaPK));
        criteria.add(Restrictions.eq("matricula", matricula));
        criteria.add(Restrictions.eq(TIPO, tipo));
        return (EmailCorec) criteria.uniqueResult();
    }

}
