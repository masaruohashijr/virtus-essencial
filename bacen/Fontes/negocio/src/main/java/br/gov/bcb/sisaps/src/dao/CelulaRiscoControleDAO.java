package br.gov.bcb.sisaps.src.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.springframework.stereotype.Repository;

import br.gov.bcb.app.stuff.dao.GenericDAO;
import br.gov.bcb.sisaps.src.dominio.Atividade;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.CelulaRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.ParametroGrupoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.VersaoPerfilRisco;
import br.gov.bcb.sisaps.src.mediator.AtividadeMediator;
import br.gov.bcb.sisaps.src.vo.CelulaRiscoControleVO;

@Repository
public class CelulaRiscoControleDAO extends GenericDAO<CelulaRiscoControle, Integer> {

    private static final String PARAMETRO_GRUPO_RISCO_CONTROLE_ORDEM = "parametroGrupoRiscoControle.ordem";
    private static final String PROP_VERSAO_PERFIL_RISCO = "versaoPerfilRisco";
    private static final String PROP_ARC_CONTROLE = "arcControle";
    private static final String PROP_ARC_RISCO = "arcRisco";
    private static final String PROP_PARAMETRO_GRUPO_RISCO_CONTROLE = "parametroGrupoRiscoControle";
    private static final String ATV1 = "atv1";
    private static final String PROP_ATIVIDADE = "atividade";
    private static final String PARAMETRO_PESO = "parametroPeso";
    private static final String MATRIZ_PK = "matriz.pk";
    private static final String PK = "pk";
    private static final String PARAMETRO_PESO_VALOR = "parametroPeso.valor";

    public CelulaRiscoControleDAO() {
        super(CelulaRiscoControle.class);
    }

    public List<CelulaRiscoControle> buscarCelulasMatriz(Integer pkMatriz) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        Criteria atividadeCriteria = criteria.createCriteria(PROP_ATIVIDADE);
        atividadeCriteria.add(Restrictions.eq(MATRIZ_PK, pkMatriz));
        return cast(criteria.list());
    }

    public CelulaRiscoControle buscarCelularPorAvaliacaoEAtividade(Atividade atividade, AvaliacaoRiscoControle arc) {
        Criteria arcsCelula = createCriteriaCelulaRiscoControle();
        Atividade atividadeBase = AtividadeMediator.get().loadPK(atividade.getPk());
        arcsCelula.add(Restrictions.eq(PROP_ATIVIDADE, atividadeBase));
        arcsCelula.add(Restrictions.or(Restrictions.eq(PROP_ARC_RISCO, arc), Restrictions.eq(PROP_ARC_CONTROLE, arc)));
        return CollectionUtils.isNotEmpty(arcsCelula.list()) ? (CelulaRiscoControle) arcsCelula.list().get(0) : null;
    }
    
    public List<CelulaRiscoControle> buscarCelularPorAtividade(Atividade atividade) {
        Criteria arcsCelula = createCriteriaCelulaRiscoControle();
        Atividade atividadeBase = AtividadeMediator.get().loadPK(atividade.getPk());
        arcsCelula.add(Restrictions.eq(PROP_ATIVIDADE, atividadeBase));
        return cast(arcsCelula.list());
    }
    
    public CelulaRiscoControle buscarCelularPorAvaliacao( AvaliacaoRiscoControle arc) {
        Criteria arcsCelula = createCriteriaCelulaRiscoControle();
        arcsCelula.add(Restrictions.or(Restrictions.eq(PROP_ARC_RISCO, arc), Restrictions.eq(PROP_ARC_CONTROLE, arc)));
        return CollectionUtils.isNotEmpty(arcsCelula.list()) ? (CelulaRiscoControle) arcsCelula.list().get(0) : null;
    }

    private Criteria createCriteriaCelulaRiscoControle() {
        return getCurrentSession().createCriteria(CelulaRiscoControle.class, "celula");
    }

    public Long somarPesoDosArcPorAtividades(Matriz matriz, int pkAtividade,
            List<VersaoPerfilRisco> versoesPerfilRiscoARCs) {
        Criteria criteria = createCriteriaCelulaRiscoControle();
        adicionarFiltroArcRecenteOUVersaoesPerfilRisco(null, versoesPerfilRiscoARCs, criteria);
        criteria.createAlias(PROP_PARAMETRO_GRUPO_RISCO_CONTROLE, PROP_PARAMETRO_GRUPO_RISCO_CONTROLE);
        criteria.createAlias(PARAMETRO_PESO, PARAMETRO_PESO, JoinType.LEFT_OUTER_JOIN);
        Criteria atividadesCriteria = criteria.createCriteria(PROP_ATIVIDADE, ATV1);
        atividadesCriteria.add(Restrictions.eq(MATRIZ_PK, matriz.getPk()));
        atividadesCriteria.add(Restrictions.eq(PK, pkAtividade));
        criteria.setProjection(Projections.sum(PARAMETRO_PESO_VALOR));
        Long retorno = (Long) criteria.uniqueResult();
        return retorno == null ? 0 : retorno * 2;
    }

   
    private void adicionarFiltroArcRecenteOUVersaoesPerfilRisco(ParametroGrupoRiscoControle grupo,
            List<VersaoPerfilRisco> versoesPerfilRiscoARCs, Criteria criteria) {
        if (versoesPerfilRiscoARCs == null || versoesPerfilRiscoARCs.isEmpty()) {
            DetachedCriteria subCelulaCriteria = DetachedCriteria.forClass(CelulaRiscoControle.class, "celula2");
            if (grupo == null) {
                subCelulaCriteria.add(Restrictions.eqProperty(PROP_PARAMETRO_GRUPO_RISCO_CONTROLE,
                        "celula.parametroGrupoRiscoControle"));
            } else {
                subCelulaCriteria.add(Restrictions.eq(PROP_PARAMETRO_GRUPO_RISCO_CONTROLE, grupo));
            }
            DetachedCriteria subAtividadesCriteria = subCelulaCriteria.createCriteria(PROP_ATIVIDADE, "atv2");
            subAtividadesCriteria.add(Property.forName("atv1.pk").eqProperty("atv2.pk"));
            subCelulaCriteria.add(Property.forName("celula2.ultimaAtualizacao").gtProperty("celula.ultimaAtualizacao"));

            criteria.add(Subqueries.notExists(subCelulaCriteria.setProjection(Projections.property("celula2.pk"))));
        } else {
            adicionarFiltroVersoesPerfilRisco(versoesPerfilRiscoARCs, criteria);
        }
    }

    private void adicionarFiltroVersoesPerfilRisco(List<VersaoPerfilRisco> versoesPerfilRiscoCelula, Criteria criteria) {
        if (CollectionUtils.isNotEmpty(versoesPerfilRiscoCelula)) {
            criteria.add(Restrictions.in(PROP_VERSAO_PERFIL_RISCO, versoesPerfilRiscoCelula));
        }
    }

    @SuppressWarnings("unchecked")
    public List<CelulaRiscoControle> buscarArcDaMatrizPorGrupo(Matriz matriz, ParametroGrupoRiscoControle grupo,
            List<VersaoPerfilRisco> versoesPerfilRiscoCelula) {
        Criteria criteria = createCriteriaCelulaRiscoControle();
        criteria.createAlias(PROP_PARAMETRO_GRUPO_RISCO_CONTROLE, PROP_PARAMETRO_GRUPO_RISCO_CONTROLE);
        criteria.add(Restrictions.eq(PROP_PARAMETRO_GRUPO_RISCO_CONTROLE, grupo));
        Criteria atividadesCriteria = criteria.createCriteria(PROP_ATIVIDADE, ATV1);
        atividadesCriteria.add(Restrictions.eq(MATRIZ_PK, matriz.getPk()));

        adicionarFiltroArcRecenteOUVersaoesPerfilRisco(null, versoesPerfilRiscoCelula, criteria);
        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    public List<AvaliacaoRiscoControle> buscarArcsConcluidoDaMatriz(Matriz matriz,
            List<VersaoPerfilRisco> versoesPerfilRiscoARCs) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.createAlias(PROP_PARAMETRO_GRUPO_RISCO_CONTROLE, PROP_PARAMETRO_GRUPO_RISCO_CONTROLE);
        criteria.add(Restrictions.isNotNull("avaliacaoRiscoControleVigente"));
        Criteria atividadesCriteria = criteria.createCriteria(PROP_ATIVIDADE, ATV1);
        atividadesCriteria.add(Restrictions.eq(MATRIZ_PK, matriz.getPk()));
        adicionarFiltroArcRecenteOUVersaoesPerfilRisco(null, versoesPerfilRiscoARCs, criteria);
        criteria.addOrder(Order.asc(PARAMETRO_GRUPO_RISCO_CONTROLE_ORDEM));
        return criteria.list();
    }
    
    public List<CelulaRiscoControle> buscarCelulasPorPerfilRisco(Integer pkPerfilRisco) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        Criteria criteriaVersaoPerfilRisco = criteria.createCriteria(PROP_VERSAO_PERFIL_RISCO);
        Criteria criteriaPerfilRisco = criteriaVersaoPerfilRisco.createCriteria("perfisRisco");
        criteriaPerfilRisco.add(Restrictions.eq(PK, pkPerfilRisco));
        return cast(criteria.list());
    }

    public List<CelulaRiscoControle> buscarCelulaPorVersaoPerfil(List<VersaoPerfilRisco> versaoPerfilRisco) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.createAlias(PROP_PARAMETRO_GRUPO_RISCO_CONTROLE, PROP_PARAMETRO_GRUPO_RISCO_CONTROLE);
        adicionarFiltroVersoesPerfilRisco(versaoPerfilRisco, criteria);
        criteria.addOrder(Order.asc(PARAMETRO_GRUPO_RISCO_CONTROLE_ORDEM));
        return cast(criteria.list());
    }
    
    public List<CelulaRiscoControleVO> buscarCelulaPorVersaoPerfilVO(List<VersaoPerfilRisco> versoesPerfilRiscoARCs) {
        return buscarParametroDaMatrizVO(null, versoesPerfilRiscoARCs);
    }

    @SuppressWarnings("unchecked")
    public List<CelulaRiscoControleVO> buscarParametroDaMatrizVO(List<Atividade> listaAtividade,
            List<VersaoPerfilRisco> versoesPerfilRiscoARCs) {
        StringBuilder hql = new StringBuilder();
        hql.append("select distinct new br.gov.bcb.sisaps.src.vo.CelulaRiscoControleVO(");

        hql.append("celula.pk, atividade.pk, parametroGrupoRiscoControle.pk, parametroGrupoRiscoControle.ordem, ");
        hql.append("arcRisco.pk, arcControle.pk, parametroPeso.valor, parametroTipoAtividadeNegocio.pk, ");
        hql.append("unidade.pk, matriz.pk, ciclo.pk, metodologia.pk, ");
        hql.append("parametroPesoAtividade.valor, parametroPesoUnidade.valor, unidade.tipoUnidade) ");

        hql.append("from CelulaRiscoControle celula ");
        hql.append("inner join celula.atividade as atividade ");
        hql.append("inner join atividade.matriz as matriz ");
        hql.append("inner join matriz.ciclo as ciclo ");
        hql.append("inner join ciclo.metodologia as metodologia ");
        hql.append("inner join celula.parametroPeso as parametroPeso ");
        hql.append("inner join celula.parametroGrupoRiscoControle as parametroGrupoRiscoControle ");
        hql.append("inner join celula.arcRisco as arcRisco ");
        hql.append("inner join celula.arcControle as arcControle ");

        hql.append("left join atividade.parametroPeso as parametroPesoAtividade ");
        hql.append("left join atividade.parametroTipoAtividadeNegocio as parametroTipoAtividadeNegocio ");
        hql.append("left join atividade.unidade as unidade ");
        hql.append("left join unidade.parametroPeso as parametroPesoUnidade ");
        hql.append("left join celula.versaoPerfilRisco as versao ");

        hql.append("where 1 = 1 ");
        if (listaAtividade != null) {
            hql.append("and atividade in (:atividades) ");
        }
        if (versoesPerfilRiscoARCs != null) {
            hql.append("and versao in (:versoes) ");
        }
        hql.append("order by parametroGrupoRiscoControle.ordem asc ");
        Query query = getCurrentSession().createQuery(hql.toString());
        if (listaAtividade != null) {
            query.setParameterList("atividades", listaAtividade);
        }
        if (versoesPerfilRiscoARCs != null) {
            query.setParameterList("versoes", versoesPerfilRiscoARCs);
        }
        return query.list();
    }

    @SuppressWarnings("unchecked")
    public List<CelulaRiscoControle> buscarParametroDaMatriz(List<Atividade> listaAtividade,
            List<VersaoPerfilRisco> versoesPerfilRiscoARCs) {
        ArrayList<Integer> idsAtividades = new ArrayList<Integer>();
        for (Atividade atv : listaAtividade) {
            idsAtividades.add(atv.getPk());
        }
        Criteria arcsCelula = createCriteriaCelulaRiscoControle();
        Criteria paramRiscoCriteria = arcsCelula.createAlias(PROP_PARAMETRO_GRUPO_RISCO_CONTROLE, "parametro");
        Criteria atividadesCriteria = arcsCelula.createAlias(PROP_ATIVIDADE, PROP_ATIVIDADE);
        atividadesCriteria.add(Restrictions.in("atividade.pk", idsAtividades));
        paramRiscoCriteria.addOrder(Order.asc("parametro.ordem"));
        if (CollectionUtils.isNotEmpty(versoesPerfilRiscoARCs)) {
            adicionarFiltroVersoesPerfilRisco(versoesPerfilRiscoARCs, arcsCelula);
        }
        return arcsCelula.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }

    public List<CelulaRiscoControle> buscarCelulaPorAtividadeEVersaoPerfil(Atividade atividade,
            List<VersaoPerfilRisco> versaoPerfilRisco) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(PROP_ATIVIDADE, atividade));
        adicionarFiltroVersoesPerfilRisco(versaoPerfilRisco, criteria);
        return cast(criteria.list());
    }
    
    public CelulaRiscoControle buscarCelulaMatrizEsbocada(Matriz matrizEmEdicao, AvaliacaoRiscoControle arc) {
        Criteria criteria = createCriteriaCelulaRiscoControle();
        Criteria criteriaAtividade = criteria.createCriteria(PROP_ATIVIDADE);
        criteriaAtividade.add(Restrictions.eq(MATRIZ_PK, matrizEmEdicao.getPk()));
        criteria.add(Restrictions.isNull(PROP_VERSAO_PERFIL_RISCO));
        criteria.add(Restrictions.or(Restrictions.eq(PROP_ARC_RISCO, arc), Restrictions.eq(PROP_ARC_CONTROLE, arc)));
        return (CelulaRiscoControle) criteria.uniqueResult();
    }

}