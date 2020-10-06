package br.gov.bcb.sisaps.src.dao;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.sisaps.dao.GenericDAOParaListagens;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.VersaoPerfilRisco;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoObjetoVersionadorEnum;
import br.gov.bcb.sisaps.src.vo.ConsultaPerfilRiscoVO;
import br.gov.bcb.sisaps.src.vo.PerfilRiscoVO;

@Repository
@Transactional(readOnly = true)
public class PerfilRiscoDAO extends GenericDAOParaListagens<PerfilRisco, Integer, PerfilRiscoVO, ConsultaPerfilRiscoVO> {

    private static final String PROP_PK = "pk";
    private static final String PROP_VERSOES_PERFIL_RISCO = "versoesPerfilRisco";
    private static final String PROP_CICLO_PK = "ciclo.pk";
    private static final String PROP_DATA_CRIACAO = "dataCriacao";

    public PerfilRiscoDAO() {
        super(PerfilRisco.class, PerfilRiscoVO.class);
    }

    @Override
    protected void montarCriterios(Criteria criteria, ConsultaPerfilRiscoVO consulta) {
        //TODO método não precisa ser implementado
    }

    public PerfilRisco obterPerfilRiscoAtual(Integer pkCiclo) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(PROP_CICLO_PK, pkCiclo));
        criteria.addOrder(Order.desc(PROP_DATA_CRIACAO));
        criteria.setCacheable(true);
        criteria.setCacheRegion("query.PerfilRiscoAtual");
        criteria.setMaxResults(1);
        return CollectionUtils.isNotEmpty(criteria.list()) ? (PerfilRisco) criteria.list().get(0) : null;
    }

    @SuppressWarnings("unchecked")
    public List<PerfilRisco> consultarPerfisRiscoCiclo(Integer pkCiclo) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(PROP_CICLO_PK, pkCiclo));
        criteria.addOrder(Order.desc(PROP_DATA_CRIACAO));
        return criteria.list();
    }

    public PerfilRisco buscaPrimeiraVersaoPerfil(Integer pkCiclo) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(PROP_CICLO_PK, pkCiclo));
        criteria.addOrder(Order.asc(PROP_DATA_CRIACAO));
        criteria.setMaxResults(1);
        return (PerfilRisco) criteria.list().get(0);
    }

    public boolean versaoEmMaisDeUmPerfilRisco(VersaoPerfilRisco versaoPerfilRisco) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        Criteria versoesCriteria = criteria.createCriteria(PROP_VERSOES_PERFIL_RISCO);
        versoesCriteria.add(Restrictions.eq(PROP_PK, versaoPerfilRisco.getPk()));
        List<PerfilRisco> perfis = cast(criteria.list());
        return perfis.size() > 1;
    }

    public PerfilRisco obterPerfilRiscoAtualPorES(Integer pkES) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(PROP_CICLO_PK, pkES));
        criteria.addOrder(Order.desc(PROP_DATA_CRIACAO));
        criteria.setMaxResults(1);
        return CollectionUtils.isNotEmpty(criteria.list()) ? (PerfilRisco) criteria.list().get(0) : null;
    }

    public PerfilRisco buscarPrimeiroPerfil(VersaoPerfilRisco versaoPerfilRisco) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        Criteria versoesCriteria = criteria.createCriteria(PROP_VERSOES_PERFIL_RISCO);
        versoesCriteria.add(Restrictions.eq(PROP_PK, versaoPerfilRisco.getPk()));
        criteria.addOrder(Order.asc(PROP_DATA_CRIACAO));
        criteria.setMaxResults(1);
        return CollectionUtils.isNotEmpty(criteria.list()) ? (PerfilRisco) criteria.list().get(0) : null;
    }

    public PerfilRisco buscarPerfilRiscoAnterior(PerfilRisco perfilRisco) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.lt(PROP_DATA_CRIACAO, perfilRisco.getDataCriacao()));
        criteria.add(Restrictions.eq(PROP_CICLO_PK, perfilRisco.getCiclo().getPk()));
        criteria.addOrder(Order.desc(PROP_DATA_CRIACAO));
        criteria.setMaxResults(1);
        return CollectionUtils.isNotEmpty(criteria.list()) ? (PerfilRisco) criteria.list().get(0) : null;
    }

    public PerfilRisco buscarPrimeiroPerfilComAnef(Integer pkCiclo) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        Criteria versoesCriteria = criteria.createCriteria(PROP_VERSOES_PERFIL_RISCO);
        versoesCriteria.add(Restrictions.eq("tipoObjetoVersionador", TipoObjetoVersionadorEnum.AQT));
        criteria.add(Restrictions.eq(PROP_CICLO_PK, pkCiclo));
        criteria.addOrder(Order.asc(PROP_DATA_CRIACAO));
        criteria.setMaxResults(1);
        return CollectionUtils.isNotEmpty(criteria.list()) ? (PerfilRisco) criteria.list().get(0) : null;
    }

}
