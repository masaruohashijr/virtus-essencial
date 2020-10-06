package br.gov.bcb.sisaps.src.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.sisaps.dao.GenericDAOParaListagens;
import br.gov.bcb.sisaps.src.dominio.AnexoARC;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.vo.AnexoArcVo;
import br.gov.bcb.sisaps.util.consulta.Consulta;

@Repository
@Transactional
public class AnexoArcDao extends GenericDAOParaListagens<AnexoARC, Integer, AnexoArcVo, Consulta<AnexoArcVo>> {

    public AnexoArcDao() {
        super(AnexoARC.class, AnexoArcVo.class);
    }

    @Override
    protected void montarCriterios(Criteria criteria, Consulta<AnexoArcVo> consulta) {
        //TODO não precisa implementar
    }

    public List<AnexoARC> buscarAnexosArc(AvaliacaoRiscoControle arc) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(AvaliacaoRiscoControle.PROP_AVALIACAO_RISCO_CONTROLE, arc));
        return cast(criteria.list());
    }

    public AnexoARC buscarAnexoArcMesmoNome(AvaliacaoRiscoControle arc, String link) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(AvaliacaoRiscoControle.PROP_AVALIACAO_RISCO_CONTROLE, arc));
        criteria.add(Restrictions.eq("link", link));
        return (AnexoARC) criteria.uniqueResult();
    }

    public List<AnexoARC> listarAnexos() {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        return cast(criteria.list());
    }

}
