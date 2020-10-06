package br.gov.bcb.sisaps.src.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.ColecaoChecada;
import br.gov.bcb.sisaps.dao.GenericDAOParaListagens;
import br.gov.bcb.sisaps.src.dominio.EstadoCiclo;
import br.gov.bcb.sisaps.src.vo.ConsultaEstadoCicloVO;
import br.gov.bcb.sisaps.src.vo.EstadoCicloVO;

@Repository
@Transactional(readOnly = true)
public class EstadoCicloDAO extends GenericDAOParaListagens<EstadoCiclo, Integer, EstadoCicloVO, ConsultaEstadoCicloVO> {

    private static final long serialVersionUID = 1L;

    public EstadoCicloDAO() {
        super(EstadoCiclo.class);
    }

    @Override
    public List<EstadoCicloVO> consultar(ConsultaEstadoCicloVO consultaEstadoVO) {
        String hql = "";
        Query query = getCurrentSession().createQuery(hql);
        return ColecaoChecada.checkedList(query.list(), EstadoCicloVO.class);
    }

    @Override
    protected void montarCriterios(Criteria criteria, ConsultaEstadoCicloVO consulta) {
        //TODO método não precisa ser implementado
    }

    public EstadoCiclo buscarPorPerfilRisco(Integer pkPerfilRisco) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        Criteria versaoPerfilRiscoCriteria = criteria.createCriteria("versaoPerfilRisco");
        Criteria perfilRiscoCriteria = versaoPerfilRiscoCriteria.createCriteria("perfisRisco");
        perfilRiscoCriteria.add(Restrictions.eq("pk", pkPerfilRisco));
        return (EstadoCiclo) criteria.uniqueResult();
    }

}
