package br.gov.bcb.sisaps.src.dao.analisequantitativaaqt;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.gov.bcb.sisaps.dao.GenericDAOLocal;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.SinteseDeRisco;
import br.gov.bcb.sisaps.src.dominio.VersaoPerfilRisco;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.SinteseDeRiscoAQT;

@Repository
public class SinteseDeRiscoAQTDAO extends GenericDAOLocal<SinteseDeRiscoAQT, Integer> {

    private static final String CICLO = "ciclo";
    private static final String VERSAO_PERFIL_RISCO = "versaoPerfilRisco";

    public SinteseDeRiscoAQTDAO() {
        super(SinteseDeRiscoAQT.class);
    }

    public SinteseDeRiscoAQT getSinteseRascunho(ParametroAQT parametroAQT, Ciclo ciclo) {
        return consultaSintese(parametroAQT, ciclo, false);
    }

    public SinteseDeRiscoAQT getSinteseVigente(ParametroAQT parametroAQT, Ciclo ciclo) {
        return consultaSintese(parametroAQT, ciclo, true);
    }

    private SinteseDeRiscoAQT consultaSintese(ParametroAQT parametroAQT, Ciclo ciclo, boolean vigente) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        addRestrictionPorCicloEParametro(parametroAQT, ciclo, criteria);

        if (vigente) {
            criteria.add(Restrictions.isNotNull(VERSAO_PERFIL_RISCO));
            criteria.addOrder(Order.desc(SinteseDeRisco.PROP_ULTIMA_ATUALIZACAO));
        } else {
            criteria.add(Restrictions.isNull(VERSAO_PERFIL_RISCO));
        }

        List<SinteseDeRiscoAQT> resultado = cast(criteria.list());
        return CollectionUtils.isNotEmpty(resultado) ? (SinteseDeRiscoAQT) resultado.get(0) : null;
    }

    public SinteseDeRiscoAQT getSintesePorVersaoPerfil(ParametroAQT parametroAQT, Ciclo ciclo,
            List<VersaoPerfilRisco> listaVersao) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        addRestrictionPorCicloEParametro(parametroAQT, ciclo, criteria);

        criteria.add(Restrictions.in(VERSAO_PERFIL_RISCO, listaVersao));
        
        return (SinteseDeRiscoAQT) criteria.uniqueResult();
    }

    private void addRestrictionPorCicloEParametro(ParametroAQT parametroAQT, Ciclo ciclo, Criteria criteria) {
        if (ciclo != null) {
            criteria.add(Restrictions.eq(CICLO, ciclo));
        }
        if (parametroAQT != null) {
            criteria.add(Restrictions.eq("parametroAQT", parametroAQT));
        }
    }

    public List<SinteseDeRiscoAQT> buscarPorPerfilRisco(Integer pkPerfilRisco) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        Criteria versaoPerfilRiscoCriteria = criteria.createCriteria(VERSAO_PERFIL_RISCO);
        Criteria perfilRiscoCriteria = versaoPerfilRiscoCriteria.createCriteria("perfisRisco");
        perfilRiscoCriteria.add(Restrictions.eq("pk", pkPerfilRisco));
        return cast(criteria.list());
    }

    public List<SinteseDeRiscoAQT> buscarSintesesRascunho(Ciclo ciclo) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.createAlias(CICLO, CICLO);
        criteria.add(Restrictions.eq("ciclo.pk", ciclo.getPk()));
        criteria.add(Restrictions.isNull(VERSAO_PERFIL_RISCO));
        return cast(criteria.list());
    }
}
