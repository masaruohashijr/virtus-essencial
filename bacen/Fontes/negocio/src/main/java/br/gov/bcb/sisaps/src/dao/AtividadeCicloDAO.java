package br.gov.bcb.sisaps.src.dao;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.gov.bcb.sisaps.dao.GenericDAOParaListagens;
import br.gov.bcb.sisaps.src.dominio.AtividadeCiclo;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.vo.AtividadeCicloVO;
import br.gov.bcb.sisaps.src.vo.ConsultaAtividadeCicloVO;

@Repository
public class AtividadeCicloDAO extends GenericDAOParaListagens<AtividadeCiclo, Integer, 
    AtividadeCicloVO, ConsultaAtividadeCicloVO> {

    private static final String PROP_VERSAO_PERFIL_RISCO = "versaoPerfilRisco";
    private static final String PROP_SITUACAO = "situacao";
    private static final String PROP_DESCRICAO = "descricao";
    private static final String PROP_ANO = "ano";
    private static final String PROP_DATA_BASE = "dataBase";
    private static final String PROP_CODIGO = "codigo";
    private static final String PROP_CNPJ_ES = "cnpjES";
    private static final String PROP_PK = "pk";
    private static final long serialVersionUID = 1L;

    public AtividadeCicloDAO() {
        super(AtividadeCiclo.class, AtividadeCicloVO.class);
    }

    @Override
    protected void montarCriterios(Criteria criteria, ConsultaAtividadeCicloVO consulta) {
        if (consulta.getCnpjES() != null) {
            criteria.add(Restrictions.eq(PROP_CNPJ_ES, consulta.getCnpjES()));
        }
        if (consulta.getAno() != null) {
            criteria.add(Restrictions.eq(PROP_ANO, consulta.getAno()));
        }
        
        if (CollectionUtils.isNotEmpty(consulta.getVersoesPerfilRisco())) {
            criteria.add(Restrictions.in(PROP_VERSAO_PERFIL_RISCO, consulta.getVersoesPerfilRisco()));
        }
        
        criteria.setProjection(Projections.projectionList().add(Projections.property(PROP_PK), PROP_PK)
                .add(Projections.property(PROP_CNPJ_ES), PROP_CNPJ_ES)
                .add(Projections.property(PROP_CODIGO), PROP_CODIGO)
                .add(Projections.property(PROP_DATA_BASE), PROP_DATA_BASE)
                .add(Projections.property(PROP_ANO), PROP_ANO)
                .add(Projections.property(PROP_DESCRICAO), PROP_DESCRICAO)
                .add(Projections.property(PROP_SITUACAO), PROP_SITUACAO));
    }
    
    public AtividadeCiclo buscarUltimaAtividadeCiclo(String cnpjES, PerfilRisco perfilRisco, String codigoAtividade) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(PROP_CNPJ_ES, cnpjES));
        criteria.add(Restrictions.eq(PROP_CODIGO, codigoAtividade));
        
        Criteria versaoPerfilRiscoCriteria = criteria.createCriteria(PROP_VERSAO_PERFIL_RISCO);
        Criteria perfilRiscoCriteria = versaoPerfilRiscoCriteria.createCriteria("perfisRisco");
        perfilRiscoCriteria.add(Restrictions.eq(PROP_PK, perfilRisco.getPk()));
        
        criteria.addOrder(Order.desc(AtividadeCiclo.PROP_ULTIMA_ATUALIZACAO));
        criteria.setMaxResults(1);
        return CollectionUtils.isNotEmpty(criteria.list()) ? (AtividadeCiclo) criteria.list().get(0) : null;
    }

    public List<AtividadeCiclo> buscarAtividadesCiclo(Short ano) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(PROP_ANO, ano));
        return cast(criteria.list());
    }

    public List<AtividadeCiclo> buscarPorPerfilRisco(Integer pkPerfilRisco) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        Criteria versaoPerfilRiscoCriteria = criteria.createCriteria(PROP_VERSAO_PERFIL_RISCO);
        Criteria perfilRiscoCriteria = versaoPerfilRiscoCriteria.createCriteria("perfisRisco");
        perfilRiscoCriteria.add(Restrictions.eq(PROP_PK, pkPerfilRisco));
        return cast(criteria.list());
    }

}
