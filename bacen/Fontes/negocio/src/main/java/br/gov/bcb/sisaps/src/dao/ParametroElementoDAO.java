package br.gov.bcb.sisaps.src.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.gov.bcb.sisaps.dao.GenericDAOParaListagens;
import br.gov.bcb.sisaps.src.dominio.ParametroElemento;
import br.gov.bcb.sisaps.src.dominio.ParametroGrupoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoGrupoEnum;
import br.gov.bcb.sisaps.src.vo.ConsultaParametroElementoVO;
import br.gov.bcb.sisaps.src.vo.ParametroElementoVO;

@Repository
public class ParametroElementoDAO extends
        GenericDAOParaListagens<ParametroElemento, Integer, ParametroElementoVO, ConsultaParametroElementoVO> {

    private static final long serialVersionUID = 1L;

    public ParametroElementoDAO() {
        super(ParametroElemento.class);
    }

    @Override
    protected void montarCriterios(Criteria criteria, ConsultaParametroElementoVO consulta) {
        //TODO método não precisa ser implementado
    }

    public List<ParametroElemento> buscarElementosArc(ParametroGrupoRiscoControle grupoRiscoControle) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq("grupoRiscoControle", grupoRiscoControle));
        return cast(criteria.list());
    }

    public List<ParametroElemento> buscarParametrosElementoPorTipo(
            ParametroGrupoRiscoControle parametroGrupoRiscoControle, TipoGrupoEnum tipoGrupoEnum) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq("grupoRiscoControle.pk", parametroGrupoRiscoControle.getPk()));
        criteria.add(Restrictions.eq("tipo", tipoGrupoEnum));
        criteria.addOrder(Order.asc("ordem"));
        return cast(criteria.list());
    }

}
