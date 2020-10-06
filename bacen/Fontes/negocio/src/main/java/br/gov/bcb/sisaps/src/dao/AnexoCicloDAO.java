package br.gov.bcb.sisaps.src.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.sisaps.dao.GenericDAOParaListagens;
import br.gov.bcb.sisaps.src.dominio.AnexoCiclo;
import br.gov.bcb.sisaps.src.dominio.VersaoPerfilRisco;
import br.gov.bcb.sisaps.src.vo.AnexoCicloVO;
import br.gov.bcb.sisaps.util.consulta.Consulta;

@Repository
@Transactional
public class AnexoCicloDAO extends GenericDAOParaListagens<AnexoCiclo, Integer, AnexoCicloVO, Consulta<AnexoCicloVO>> {

    public AnexoCicloDAO() {
        super(AnexoCiclo.class, AnexoCicloVO.class);
    }

    @Override
    protected void montarCriterios(Criteria criteria, Consulta<AnexoCicloVO> consulta) {
        //TODO não precisa implementar
    }

    public List<AnexoCiclo> buscarAnexosCicloPerfilRisco(List<VersaoPerfilRisco> versoesPerfilRisco) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.in("versaoPerfilRisco", versoesPerfilRisco));
        criteria.addOrder(Order.asc("ordem"));
        return cast(criteria.list());
    }

    public List<AnexoCiclo> listarAnexos() {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        return cast(criteria.list());
    }

}
