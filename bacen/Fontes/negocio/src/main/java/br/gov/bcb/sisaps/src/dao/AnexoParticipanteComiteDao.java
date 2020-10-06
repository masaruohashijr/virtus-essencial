package br.gov.bcb.sisaps.src.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.sisaps.dao.GenericDAOParaListagens;
import br.gov.bcb.sisaps.src.dominio.AnexoParticipanteComite;
import br.gov.bcb.sisaps.src.vo.AnexoParticipanteComiteVO;
import br.gov.bcb.sisaps.src.vo.ConsultaAnexo;

@Transactional
@Repository
public class AnexoParticipanteComiteDao
        extends
        GenericDAOParaListagens<AnexoParticipanteComite, Integer, AnexoParticipanteComiteVO, ConsultaAnexo<AnexoParticipanteComiteVO>> {

    public AnexoParticipanteComiteDao() {
        super(AnexoParticipanteComite.class, AnexoParticipanteComiteVO.class);
    }

    @Override
    protected void montarCriterios(Criteria criteria, ConsultaAnexo<AnexoParticipanteComiteVO> consulta) {
        // TODO Auto-generated method stub

    }

    @SuppressWarnings("unchecked")
    public List<AnexoParticipanteComite> listarAnexos() {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.addOrder(Order.desc("dataHoraUpload"));
        criteria.setMaxResults(5);
        return criteria.list();
    }

}
