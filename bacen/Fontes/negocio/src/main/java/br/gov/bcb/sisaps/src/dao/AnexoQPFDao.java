package br.gov.bcb.sisaps.src.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.sisaps.dao.GenericDAOParaListagens;
import br.gov.bcb.sisaps.src.dominio.AnexoQuadroPosicaoFinanceira;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.QuadroPosicaoFinanceira;
import br.gov.bcb.sisaps.src.vo.AnexoArcVo;
import br.gov.bcb.sisaps.util.consulta.Consulta;

@Repository
@Transactional
public class AnexoQPFDao extends
        GenericDAOParaListagens<AnexoQuadroPosicaoFinanceira, Integer, AnexoArcVo, Consulta<AnexoArcVo>> {

    public AnexoQPFDao() {
        super(AnexoQuadroPosicaoFinanceira.class, AnexoArcVo.class);
    }

    @Override
    protected void montarCriterios(Criteria criteria, Consulta<AnexoArcVo> consulta) {
        //TODO não precisa implementar
    }

    public List<AnexoQuadroPosicaoFinanceira> buscarAnexosQuadro(QuadroPosicaoFinanceira quadro) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(QuadroPosicaoFinanceira.PROP_QUADRO_POSICAO_FINANCEIRA, quadro));
        return cast(criteria.list());
    }

    public AnexoQuadroPosicaoFinanceira buscarAnexoQuadroMesmoNome(QuadroPosicaoFinanceira quadro, String link) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(QuadroPosicaoFinanceira.PROP_QUADRO_POSICAO_FINANCEIRA, quadro));
        criteria.add(Restrictions.eq("link", link));
        return (AnexoQuadroPosicaoFinanceira) criteria.uniqueResult();
    }

    public List<AnexoQuadroPosicaoFinanceira> listarAnexos() {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        return cast(criteria.list());
    }

}
