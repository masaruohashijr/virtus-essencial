package br.gov.bcb.sisaps.src.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.ColecaoChecada;
import br.gov.bcb.app.stuff.util.props.PropertyUtils;
import br.gov.bcb.sisaps.dao.GenericDAOParaListagens;
import br.gov.bcb.sisaps.src.dominio.Atividade;
import br.gov.bcb.sisaps.src.dominio.Metodologia;
import br.gov.bcb.sisaps.src.dominio.ParametroGrupoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.ParametroPeso;
import br.gov.bcb.sisaps.src.dominio.VersaoPerfilRisco;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoMetodologiaEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoGrupoEnum;
import br.gov.bcb.sisaps.src.vo.ConsultaMetodologiaVO;
import br.gov.bcb.sisaps.src.vo.MetodologiaVO;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;

@Repository
@Transactional(readOnly = true)
public class MetodologiaDAO extends GenericDAOParaListagens<Metodologia, Integer, MetodologiaVO, ConsultaMetodologiaVO> {

    private static final String PROP_METODOLOGIA = "metodologia";

    private static final String PK = "pk";

    private static final long serialVersionUID = 1L;

    private static final ParametroPeso PROP_PARAMETRO_PESO = PropertyUtils.getPropertyObject(ParametroPeso.class);

    private static final String PROP_VALOR = PropertyUtils.property(PROP_PARAMETRO_PESO.getValor());

    public MetodologiaDAO() {
        super(Metodologia.class);
    }
    
    public Metodologia buscarMetodologiaDefault() {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq("metodologiaDefault", SimNaoEnum.SIM));
        criteria.add(Restrictions.eq("estado", EstadoMetodologiaEnum.CONCLUIDA));
        return (Metodologia) criteria.uniqueResult();
    }

    public Metodologia buscarMetodologiaPorPK(Integer id) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(PK, id));
        return (Metodologia) criteria.uniqueResult();
    }

    @Override
    public List<MetodologiaVO> consultar(ConsultaMetodologiaVO consultaMetodologiaVO) {
        String hql = "";
        Query query = getCurrentSession().createQuery(hql);
        return ColecaoChecada.checkedList(query.list(), MetodologiaVO.class);
    }

    @Override
    protected void montarCriterios(Criteria criteria, ConsultaMetodologiaVO consulta) {
        //TODO não precisa implementar
    }

    public ParametroPeso buscarMaiorPesoMetodologia(Metodologia metodologia) {
        Criteria criteria = addCriteriaParametro(metodologia);
        criteria.setMaxResults(1);
        return (ParametroPeso) criteria.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<ParametroPeso> buscarListaParametroPesoMetodologia(Metodologia metodologia) {
        Criteria criteria = addCriteriaParametro(metodologia);
        return criteria.list();
    }

    private Criteria addCriteriaParametro(Metodologia metodologia) {
        Criteria criteria = getCurrentSession().createCriteria(ParametroPeso.class);
        criteria.add(Restrictions.eq(PROP_METODOLOGIA, metodologia));
        criteria.addOrder(Order.desc(PROP_VALOR));
        return criteria;
    }

    @SuppressWarnings("unchecked")
    public List<ParametroGrupoRiscoControle> buscarGrupoParametroGrupoRisco(List<Atividade> listaAtividade,
            List<VersaoPerfilRisco> versoesPerfilRiscoARCs) {
        ArrayList<Integer> idsAtividades = new ArrayList<Integer>();
        for (Atividade atv : listaAtividade) {
            idsAtividades.add(atv.getPk());
        }

        Criteria paramRiscoCriteria = getCurrentSession().createCriteria(ParametroGrupoRiscoControle.class);
        Criteria arcsCriteria = paramRiscoCriteria.createCriteria("avaliacaoRiscoControles");
        Criteria atividadesCriteria = arcsCriteria.createCriteria("atividades");
        atividadesCriteria.add(Restrictions.in(PK, idsAtividades));
        paramRiscoCriteria.add(Restrictions.isNotNull("parametroControle"));
        paramRiscoCriteria.addOrder(Order.asc("ordem"));
        if (CollectionUtils.isNotEmpty(versoesPerfilRiscoARCs)) {
            arcsCriteria.add(Restrictions.in("versaoPerfilRisco", versoesPerfilRiscoARCs));
        }

        return paramRiscoCriteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }

    @SuppressWarnings("unchecked")
    public List<ParametroGrupoRiscoControle> buscarApenasRiscos(Metodologia metodologia) {
        Criteria paramRiscoCriteria = getCurrentSession().createCriteria(ParametroGrupoRiscoControle.class);
        paramRiscoCriteria.add(Restrictions.eq("tipo", TipoGrupoEnum.RISCO));
        paramRiscoCriteria.add(Restrictions.eq(PROP_METODOLOGIA, metodologia));
        return paramRiscoCriteria.list();
    }

}
