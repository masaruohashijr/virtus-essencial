package br.gov.bcb.sisaps.src.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.stereotype.Repository;

import br.gov.bcb.sisaps.dao.GenericDAOParaListagens;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.Unidade;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoUnidadeAtividadeEnum;
import br.gov.bcb.sisaps.src.vo.ConsultaUnidadeVO;
import br.gov.bcb.sisaps.src.vo.UnidadeVO;

@Repository
public class UnidadeDAO extends GenericDAOParaListagens<Unidade, Integer, UnidadeVO, ConsultaUnidadeVO> {

    private static final long serialVersionUID = 1L;
    private static final String NOME = "nome";
    private static final String MATRIZ = "matriz";
    private static final String PK = "pk";
    private static final String TIPO_UNIDADE = "tipoUnidade";
    private static final String PARAMETRO_PESO = "parametroPeso";
    private static final String PARAMETRO_PESO_VALOR = "parametroPeso.valor";
    private static final String MATRIZ_PK = "matriz.pk";

    public UnidadeDAO() {
        super(Unidade.class, UnidadeVO.class);
    }

    @Override
    protected void montarCriterios(Criteria criteria, ConsultaUnidadeVO consulta) {
        adicionarFiltroNome(consulta, criteria);
        // Atributos que serão retornados na consulta
        criteria.setProjection(Projections.projectionList().add(Projections.property(PK), PK)
                .add(Projections.property(NOME), NOME));
    }

    private void adicionarFiltroNome(ConsultaUnidadeVO consulta, Criteria criteria) {
        if (StringUtils.isNotEmpty(consulta.getNome())) {
            criteria.add(Restrictions.eq(NOME, consulta.getNome()));
        }

        if (consulta.getMatriz() != null) {
            criteria.add(Restrictions.eq(MATRIZ, consulta.getMatriz()));
        }

        if (consulta.getTipo() != null) {
            criteria.add(Restrictions.eq(TIPO_UNIDADE, consulta.getTipo()));
        }
    }

    public List<Unidade> buscarUnidadesMatriz(Matriz matriz, TipoUnidadeAtividadeEnum tipo) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(MATRIZ, matriz));
        criteria.add(Restrictions.eq(TIPO_UNIDADE, tipo));
        criteria.addOrder(Order.asc(NOME));
        return cast(criteria.list());
    }
     
    public List<Unidade> buscarUnidadesMatriz(Matriz matriz) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(MATRIZ, matriz));
        criteria.addOrder(Order.asc(NOME));
        return cast(criteria.list());
    }
    
    
    public Long somarPesoTipoUnidade(Matriz matriz, TipoUnidadeAtividadeEnum tipoUnidade) {
        Criteria criteria = getCurrentSession().createCriteria(Unidade.class);
        criteria.createAlias(PARAMETRO_PESO, PARAMETRO_PESO, JoinType.LEFT_OUTER_JOIN);
        criteria.add(Restrictions.eq(MATRIZ_PK, matriz.getPk()));
        criteria.add(Restrictions.eq(TIPO_UNIDADE, tipoUnidade));
        criteria.setProjection(Projections.sum(PARAMETRO_PESO_VALOR));
        Long retorno = (Long) criteria.uniqueResult();
        return retorno == null ? 0 : retorno;
    }
    
    
}
