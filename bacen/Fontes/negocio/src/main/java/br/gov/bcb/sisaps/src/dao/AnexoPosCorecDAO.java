package br.gov.bcb.sisaps.src.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.sisaps.dao.GenericDAOParaListagens;
import br.gov.bcb.sisaps.src.dominio.AnexoPosCorec;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.vo.AnexoPosCorecVO;
import br.gov.bcb.sisaps.util.consulta.Consulta;

@Repository
@Transactional
public class AnexoPosCorecDAO extends
        GenericDAOParaListagens<AnexoPosCorec, Integer, AnexoPosCorecVO, Consulta<AnexoPosCorecVO>> {
    private static final String CICLO = "ciclo";
    private static final String TIPO = "tipo";

    public AnexoPosCorecDAO() {
        super(AnexoPosCorec.class, AnexoPosCorecVO.class);
    }

    @Override
    protected void montarCriterios(Criteria criteria, Consulta<AnexoPosCorecVO> consulta) {

    }

    public List<AnexoPosCorec> listarAnexos(Ciclo ciclo, String tipo) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(CICLO, ciclo));
        criteria.add(Restrictions.eq(TIPO, tipo));
        return cast(criteria.list());
    }

    public AnexoPosCorec buscarAnexo(Ciclo ciclo, String tipo) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(CICLO, ciclo));
        criteria.add(Restrictions.eq(TIPO, tipo));
        return (AnexoPosCorec) criteria.uniqueResult();
    }

    public List<AnexoPosCorec> listarAnexos() {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        return cast(criteria.list());
    }

}
