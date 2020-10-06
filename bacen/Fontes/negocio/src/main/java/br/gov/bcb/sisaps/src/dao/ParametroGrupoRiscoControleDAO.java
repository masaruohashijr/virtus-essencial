package br.gov.bcb.sisaps.src.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.stereotype.Repository;

import br.gov.bcb.app.stuff.dao.GenericDAO;
import br.gov.bcb.sisaps.src.dominio.Atividade;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.CelulaRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.Metodologia;
import br.gov.bcb.sisaps.src.dominio.ParametroGrupoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.VersaoPerfilRisco;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoARCEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoParametroGrupoRiscoControleEnum;
import br.gov.bcb.sisaps.util.Util;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;

@Repository
public class ParametroGrupoRiscoControleDAO extends GenericDAO<ParametroGrupoRiscoControle, Integer> {

    private static final String METODOLOGIA2 = "metodologia";
    private static final String UNCHECKED = "unchecked";
    private static final String ARC_CONTROLE = "arcControle";
    private static final String ARC_RISCO = "arcRisco";
    private static final String PROP_PARAMETRO_GRUPO_RISCO_CONTROLE = "parametroGrupoRiscoControle";
    private static final String PROP_MATRIZ_PK = "matriz.pk";
    private static final String PROP_ATIVIDADE = "atividade";
    private static final String PROP_SINTESE_OBRIGATORIA = "sinteseObrigatoria";
    private static final String PROP_PK = "pk";
    private static final String PROP_VERSAO_PERFIL_RISCO = "versaoPerfilRisco";

    public ParametroGrupoRiscoControleDAO() {
        super(ParametroGrupoRiscoControle.class);
    }

    /**
     * Busca todos os parâmetros de grupo de RISCO que estão na matriz passada por parâmetro e
     * também os parâmetros marcadas com síntese obrigatória que NÃO estão na matriz
     * 
     * @param matriz
     * @return
     */
    @SuppressWarnings(UNCHECKED)
    public List<ParametroGrupoRiscoControle> buscarGruposRiscoDaMatrizESinteseObrigatoria(
            List<Integer> idsGrupoRiscoDaMatriz, Metodologia metodologia) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        if (CollectionUtils.isEmpty(idsGrupoRiscoDaMatriz)) {
            criteria.add(Restrictions.eq(PROP_SINTESE_OBRIGATORIA, SimNaoEnum.SIM));
        } else {
            criteria.add(Restrictions.or(Restrictions.eq(PROP_SINTESE_OBRIGATORIA, SimNaoEnum.SIM),
                    Restrictions.in(PROP_PK, idsGrupoRiscoDaMatriz)));
        }
        criteria.add(Restrictions.eq(METODOLOGIA2, metodologia));
        
        criteria.addOrder(Order.asc("ordem"));
        return criteria.list();
    }

    @SuppressWarnings(UNCHECKED)
    public List<Integer> buscarIdsGruposRiscoDaMatriz(Matriz matriz) {
        Criteria criteria = getCurrentSession().createCriteria(CelulaRiscoControle.class);
        criteria.createAlias(PROP_PARAMETRO_GRUPO_RISCO_CONTROLE, PROP_PARAMETRO_GRUPO_RISCO_CONTROLE,
                JoinType.LEFT_OUTER_JOIN);
        Criteria atividadesCriteria = criteria.createCriteria(PROP_ATIVIDADE);
        atividadesCriteria.add(Restrictions.eq(PROP_MATRIZ_PK, matriz.getPk()));
        criteria.setProjection(Projections.groupProperty("parametroGrupoRiscoControle.pk"));
        return criteria.list();
    }

    /**
     * Buscar o Parametro de Grupo de Risco a partir do seu controle.
     * 
     * @param pkParametroGrupoControle
     * @return
     */
    public ParametroGrupoRiscoControle buscarParametroGrupoRisco(Integer pkParametroGrupoControle) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(PROP_PK, pkParametroGrupoControle));
        return (ParametroGrupoRiscoControle) criteria.uniqueResult();
    }

    public boolean existeARCAnalisadoGrupoRisco(ParametroGrupoRiscoControle parametroGrupoRisco, Matriz matriz) {
        Criteria criteria = addCriteriaARCsAnalisadosGrupoRisco(parametroGrupoRisco, matriz, null);
        criteria.setProjection(Projections.rowCount());
        return (Long) criteria.uniqueResult() > 0;
    }

    @SuppressWarnings(UNCHECKED)
    public List<AvaliacaoRiscoControle> buscarARCsAnalisadosGrupoRisco(ParametroGrupoRiscoControle parametroGrupoRisco,
            Matriz matriz) {
        Criteria criteria = addCriteriaARCsAnalisadosGrupoRisco(parametroGrupoRisco, matriz, null);
        List<AvaliacaoRiscoControle> arcAnalisados = new ArrayList<AvaliacaoRiscoControle>();
        for (CelulaRiscoControle celula : (List<CelulaRiscoControle>) criteria.list()) {
            if (EstadoARCEnum.ANALISADO.equals(celula.getArcControle().getEstado())) {
                arcAnalisados.add(celula.getArcControle());
            }
            if (EstadoARCEnum.ANALISADO.equals(celula.getArcRisco().getEstado())) {
                arcAnalisados.add(celula.getArcRisco());
            }
        }
        return arcAnalisados;
    }

    @SuppressWarnings(UNCHECKED)
    public List<CelulaRiscoControle> buscarCelulasARCsAnalisadosGrupoRiscoControle(
            ParametroGrupoRiscoControle parametroGrupoRisco, Matriz matriz, List<VersaoPerfilRisco> versoes) {
        return addCriteriaARCsAnalisadosGrupoRisco(parametroGrupoRisco, matriz, versoes).list();
    }

    private Criteria addCriteriaARCsAnalisadosGrupoRisco(ParametroGrupoRiscoControle parametroGrupoRisco,
            Matriz matriz, List<VersaoPerfilRisco> versoes) {
        Criteria criteria = getCurrentSession().createCriteria(CelulaRiscoControle.class);
        criteria.createAlias(PROP_PARAMETRO_GRUPO_RISCO_CONTROLE, PROP_PARAMETRO_GRUPO_RISCO_CONTROLE);
        criteria.createAlias(ARC_RISCO, ARC_RISCO);
        criteria.createAlias(ARC_CONTROLE, ARC_CONTROLE);
        criteria.add(Restrictions.eq(PROP_PARAMETRO_GRUPO_RISCO_CONTROLE, parametroGrupoRisco));
        if (matriz != null) {
            Criteria atividadesCriteria = criteria.createCriteria(PROP_ATIVIDADE);
            atividadesCriteria.add(Restrictions.eq(PROP_MATRIZ_PK, matriz.getPk()));
        }
        criteria.add(Restrictions.or(Restrictions.eq("arcRisco.estado", EstadoARCEnum.ANALISADO),
                Restrictions.eq("arcControle.estado", EstadoARCEnum.ANALISADO)));

        if (!Util.isNuloOuVazio(versoes)) {
            criteria.add(Restrictions.in(PROP_VERSAO_PERFIL_RISCO, versoes));
        }

        return criteria;
    }

    public ParametroGrupoRiscoControle buscarGrupoPorAvaliacaoEAtividade(AvaliacaoRiscoControle arc, Atividade atividade) {
        Criteria criteria = getCurrentSession().createCriteria(CelulaRiscoControle.class);
        criteria.createAlias(PROP_PARAMETRO_GRUPO_RISCO_CONTROLE, PROP_PARAMETRO_GRUPO_RISCO_CONTROLE,
                JoinType.LEFT_OUTER_JOIN);
        criteria.add(Restrictions.eq(PROP_ATIVIDADE, atividade));
        criteria.add(Restrictions.or(Restrictions.eq(ARC_RISCO, arc), Restrictions.eq(ARC_CONTROLE, arc)));
        criteria.setProjection(Projections.groupProperty(PROP_PARAMETRO_GRUPO_RISCO_CONTROLE));
        criteria.setMaxResults(1);
        return (ParametroGrupoRiscoControle) criteria.list().get(0);
    }

    public List<ParametroGrupoRiscoControle> buscarGruposSinteseObrigatorios(Metodologia metodologia) {
        Criteria criteria = getCurrentSession().createCriteria(ParametroGrupoRiscoControle.class);
        criteria.add(Restrictions.eq(PROP_SINTESE_OBRIGATORIA, SimNaoEnum.SIM));
        criteria.add(Restrictions.eq(METODOLOGIA2, metodologia));
        return cast(criteria.list());
    }    

    public List<ParametroGrupoRiscoControle> buscarGrupos() {
        Criteria criteria = getCurrentSession().createCriteria(ParametroGrupoRiscoControle.class);
        criteria.addOrder(Order.asc("nomeAbreviado"));
        return cast(criteria.list());
    }

    public ParametroGrupoRiscoControle buscarParametroRCExterno(Metodologia metodologia) {
        Criteria criteria = getCurrentSession().createCriteria(ParametroGrupoRiscoControle.class);
        criteria.add(Restrictions.eq(METODOLOGIA2, metodologia));
        criteria.add(Restrictions.eq("tipoGrupo", TipoParametroGrupoRiscoControleEnum.EXTERNO));
        return (ParametroGrupoRiscoControle) criteria.uniqueResult();
    }
}