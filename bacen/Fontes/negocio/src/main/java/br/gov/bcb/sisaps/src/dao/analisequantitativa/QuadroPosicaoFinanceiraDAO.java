package br.gov.bcb.sisaps.src.dao.analisequantitativa;

import static br.gov.bcb.app.stuff.util.props.PropertyUtils.getPropertyObject;
import static br.gov.bcb.app.stuff.util.props.PropertyUtils.property;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.sisaps.dao.GenericDAOLocal;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.DataBaseES;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.QuadroPosicaoFinanceira;

@Repository
@Transactional
public class QuadroPosicaoFinanceiraDAO extends GenericDAOLocal<QuadroPosicaoFinanceira, Integer> {
    private static final String VERSAO_PERFIL_RISCO = "versaoPerfilRisco";
    private static final String PK = "pk";
    private static final String PONTO = ".";
    private static final String CICLO = "ciclo";

    public QuadroPosicaoFinanceiraDAO() {
        super(QuadroPosicaoFinanceira.class);
    }

    @Transactional(readOnly = true)
    public QuadroPosicaoFinanceira buscarQuadroPorCiclo(Ciclo ciclo) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        adicionarCriteriaQuadroComum(ciclo, criteria);
        return (QuadroPosicaoFinanceira) criteria.uniqueResult();
    }

    private void adicionarCriteriaQuadroComum(Ciclo ciclo, Criteria criteria) {
        criteria.createAlias(property(getPropertyObject(QuadroPosicaoFinanceira.class).getCiclo()), CICLO);
        criteria.add(Restrictions.eq(CICLO + PONTO + PK, ciclo.getPk()));
        criteria.add(Restrictions.isNull(VERSAO_PERFIL_RISCO));
    }

    public QuadroPosicaoFinanceira obterQuadroVigente(PerfilRisco perfilRiscoAtual, Integer versaoPerfilRiscoPk) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.createAlias(property(getPropertyObject(QuadroPosicaoFinanceira.class).getCiclo()), CICLO);
        criteria.add(Restrictions.eq(CICLO + PONTO + PK, perfilRiscoAtual.getCiclo().getPk()));
        criteria.createAlias(property(getPropertyObject(QuadroPosicaoFinanceira.class).getVersaoPerfilRisco()),
                VERSAO_PERFIL_RISCO);
        criteria.add(Restrictions.eq(VERSAO_PERFIL_RISCO + PONTO + PK, versaoPerfilRiscoPk));
        return (QuadroPosicaoFinanceira) criteria.uniqueResult();
    }

    public QuadroPosicaoFinanceira obterQuadroPorPk(Integer pk) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(property(getPropertyObject(QuadroPosicaoFinanceira.class).getPk()), pk));
        return (QuadroPosicaoFinanceira) criteria.uniqueResult();
    }

    public QuadroPosicaoFinanceira buscarQuadroRascunhoPorDataBase(PerfilRisco perfilRiscoAtual) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.createAlias(property(getPropertyObject(QuadroPosicaoFinanceira.class).getCiclo()), CICLO);
        criteria.add(Restrictions.eq(CICLO + PONTO + PK, perfilRiscoAtual.getCiclo().getPk()));
        criteria.add(Restrictions.isNull(VERSAO_PERFIL_RISCO));
        return (QuadroPosicaoFinanceira) criteria.uniqueResult();
    }

    public QuadroPosicaoFinanceira buscarQuadroRascunhoPorDataBase(DataBaseES dataBaseES, Ciclo ciclo) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.createAlias(property(getPropertyObject(QuadroPosicaoFinanceira.class).getCiclo()), CICLO);
        criteria.add(Restrictions.eq(CICLO + PONTO + PK, ciclo.getPk()));
        if (dataBaseES != null) {
            criteria.add(Restrictions.eq("codigoDataBase", dataBaseES.getCodigoDataBase()));
        }
        criteria.add(Restrictions.isNull(VERSAO_PERFIL_RISCO));
        return (QuadroPosicaoFinanceira) criteria.uniqueResult();
    }
}
