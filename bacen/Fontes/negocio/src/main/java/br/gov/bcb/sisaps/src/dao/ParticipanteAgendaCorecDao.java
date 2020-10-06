package br.gov.bcb.sisaps.src.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.gov.bcb.app.stuff.seguranca.UsuarioCorrente;
import br.gov.bcb.sisaps.dao.GenericDAOLocal;
import br.gov.bcb.sisaps.seguranca.UsuarioAplicacao;
import br.gov.bcb.sisaps.src.dominio.ParticipanteAgendaCorec;
import br.gov.bcb.sisaps.src.vo.ParticipanteComiteVO;

@Repository
public class ParticipanteAgendaCorecDao extends GenericDAOLocal<ParticipanteAgendaCorec, Integer> {

    private static final String FROM_PARTICIPANTE_AGENDA_COREC_PARTICIPANTE =
            "from ParticipanteAgendaCorec participante ";
    private static final String AGENDA_PK = "agenda.pk";
    private static final String PROP_PK = "pk";


    public ParticipanteAgendaCorecDao() {
        super(ParticipanteAgendaCorec.class);
    }

    @SuppressWarnings("unchecked")
    public List<ParticipanteAgendaCorec> buscarParticipanteAgendaCorec(Integer pkAgenda) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(AGENDA_PK, pkAgenda));
        return criteria.list();
    }

    public boolean existeParticipanteAgendaCorec(Integer pkCiclo) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        Criteria criteriaAgenda = criteria.createCriteria("agenda");
        Criteria criteriaCiclo = criteriaAgenda.createCriteria("ciclo");
        criteriaCiclo.add(Restrictions.eq(PROP_PK, pkCiclo));
        criteria.setProjection(Projections.rowCount());
        return ((Long) criteria.uniqueResult()) > 0;
    }

    @SuppressWarnings("unchecked")
    public List<ParticipanteComiteVO> buscarParticipantesEfetivos(Integer pkAgenda) {
        StringBuilder hql = new StringBuilder();
        hql.append("select new br.gov.bcb.sisaps.src.vo.ParticipanteComiteVO(participante.matricula, participante.pk, ");
        hql.append("(select max(carga.nome) from CargaParticipante carga where carga.matricula = participante.matricula)) ");
        hql.append(FROM_PARTICIPANTE_AGENDA_COREC_PARTICIPANTE);
        hql.append("where participante.agenda.pk = :idAgenda ");
        Query query = getCurrentSession().createQuery(hql.toString());
        if (pkAgenda == null) {
            return new ArrayList<ParticipanteComiteVO>();
        }
        query.setInteger("idAgenda", pkAgenda);
        return query.list();
    }

    public List<ParticipanteAgendaCorec> consultaAtasPendente() {
        return consultaAtas(true);
    }

    public List<ParticipanteAgendaCorec> consultaAtasAssinadas() {
        return consultaAtas(false);
    }

    @SuppressWarnings("unchecked")
    private List<ParticipanteAgendaCorec> consultaAtas(boolean isPendente) {
        StringBuilder hql = new StringBuilder();
        hql.append("select participante ");
        hql.append(FROM_PARTICIPANTE_AGENDA_COREC_PARTICIPANTE);
        hql.append("left outer join participante.agenda as agenda ");
        hql.append("left outer join agenda.ciclo as ciclo ");
        hql.append("inner join ciclo.estadoCiclo as estado ");
        hql.append("inner join ciclo.entidadeSupervisionavel as entidadeSupervisionavel ");
        if (isPendente) {
            hql.append("where estado.estado = 5 ");
            hql.append("and participante.assinatura is null ");
        } else {
            hql.append("where participante.assinatura is not null ");
        }
        hql.append("and participante.matricula = :matricula");
        hql.append(" order by entidadeSupervisionavel.nome asc");
        Query query = getCurrentSession().createQuery(hql.toString());
        query.setString("matricula", ((UsuarioAplicacao) UsuarioCorrente.get()).getMatricula());
        return query.list();
    }

}
