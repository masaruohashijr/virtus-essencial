package br.gov.bcb.sisaps.src.dao;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.sisaps.dao.GenericDAOParaListagens;
import br.gov.bcb.sisaps.src.dominio.AgendaCorec;
import br.gov.bcb.sisaps.src.dominio.CargaParticipante;
import br.gov.bcb.sisaps.src.dominio.EntidadeSupervisionavel;
import br.gov.bcb.sisaps.src.vo.CargaParticipanteVO;
import br.gov.bcb.sisaps.src.vo.ConsultaAnexo;
import br.gov.bcb.sisaps.src.vo.ParticipanteComiteVO;

@Transactional(readOnly = true)
@Repository
public class CargaParticipanteDao extends
        GenericDAOParaListagens<CargaParticipante, Integer, CargaParticipanteVO, ConsultaAnexo<CargaParticipanteVO>> {

    private static final String BARRA = "/";

    public CargaParticipanteDao() {
        super(CargaParticipante.class, CargaParticipanteVO.class);
    }

    @Override
    protected void montarCriterios(Criteria criteria, ConsultaAnexo<CargaParticipanteVO> consulta) {
        // TODO Auto-generated method stub

    }

    @SuppressWarnings("unchecked")
    public List<CargaParticipante> carregarParticipantes() {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        return criteria.list();
    }

    @SuppressWarnings(UNCHECKED)
    public List<ParticipanteComiteVO> buscarParticipantesPossiveisES(EntidadeSupervisionavel es) {

        StringBuilder hqlExcluir = criarHqlExcluir();
        StringBuilder hqlIncluir = criarHqlIncluir();

        StringBuilder hql = new StringBuilder();
        inicilizarSelectParticipante(hql);
        criarClausulaWhere(hql);
        hql.append(hqlExcluir);
        hql.append(hqlIncluir);

        addOrderBy(hql);
        Query query = getCurrentSession().createQuery(hql.toString());
        addFiltros(es, query);
        return query.list();

    }

    private StringBuilder criarHqlIncluir() {
        StringBuilder hqlIncluir = new StringBuilder();
        hqlIncluir.append("and (carga.subordinadasIncluir is null  or");
        hqlIncluir.append(" ( (carga.subordinadasIncluir like 'N' and (carga.equipeIncluir like :localizacao)) or ");
        hqlIncluir.append(" (carga.subordinadasIncluir like 'S' and "
                + "(:localizacao like (concat(carga.equipeIncluir,'%')))) ))");
        return hqlIncluir;
    }

    private StringBuilder criarHqlExcluir() {
        StringBuilder hqlExcluir = new StringBuilder();
        hqlExcluir.append("and (carga.subordinadasExcluir is null  or");
        hqlExcluir
                .append(" ( (carga.subordinadasExcluir like 'N' and (carga.equipeExcluir not like :localizacao)) or ");
        hqlExcluir.append("(carga.subordinadasExcluir like 'S' and"
                + " (:localizacao not like (concat(carga.equipeExcluir,'%')))))) ");
        return hqlExcluir;
    }

    private void criarClausulaWhere(StringBuilder hql) {
        hql.append("where  UPPER(carga.prioridade) = UPPER(:prioridade) ");
    }

    private void addOrderBy(StringBuilder hql) {
        hql.append(" order by carga.nome");
    }

    private void addFiltros(EntidadeSupervisionavel es, Query query) {
        query.setString("prioridade", es.getPrioridade().getPk().toString());
        query.setString("localizacao", es.getLocalizacao());
    }

    @SuppressWarnings(UNCHECKED)
    public List<ParticipanteComiteVO> buscarParticipantesPossiveisESSemEfetivos(EntidadeSupervisionavel es,
            AgendaCorec agenda) {

        StringBuilder hqlExcluir = criarHqlExcluir();
        StringBuilder hqlIncluir = criarHqlIncluir();

        StringBuilder hql = new StringBuilder();
        inicilizarSelectParticipante(hql);
        criarClausulaWhere(hql);
        if (agenda != null) {
            hql.append("and carga.matricula not in (select participante.matricula "
                    + "from ParticipanteAgendaCorec participante where participante.agenda.pk = :idAgenda)");
        }
        hql.append(hqlExcluir);
        hql.append(hqlIncluir);

        addOrderBy(hql);
        Query query = getCurrentSession().createQuery(hql.toString());
        addFiltros(es, query);
        if (agenda != null) {
            query.setInteger("idAgenda", agenda.getPk());
        }
        return query.list();
    }

    private void inicilizarSelectParticipante(StringBuilder hql) {
        hql.append("select new br.gov.bcb.sisaps.src.vo.ParticipanteComiteVO(carga.matricula, carga.nome, carga.pk, carga.email) ");
        hql.append("from CargaParticipante carga ");
    }
    
    @SuppressWarnings("unchecked")
    public CargaParticipante buscarCargaPorMatricula(String matricula) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq("matricula", matricula));
        List<CargaParticipante> resultado = criteria.list();
        return CollectionUtils.isNotEmpty(resultado) ? (CargaParticipante) resultado.get(0) : null;
    }

}
