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
import br.gov.bcb.sisaps.src.dominio.Atividade;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.Unidade;
import br.gov.bcb.sisaps.src.dominio.VersaoPerfilRisco;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoUnidadeAtividadeEnum;
import br.gov.bcb.sisaps.src.vo.AtividadeVO;
import br.gov.bcb.sisaps.src.vo.ConsultaAtividadeVO;

@Repository
public class AtividadeDAO extends GenericDAOParaListagens<Atividade, Integer, AtividadeVO, ConsultaAtividadeVO> {

    private static final String CELULAS_RISCO_CONTROLE = "celulasRiscoControle";
    private static final String NOME = "nome";
    private static final String UNIDADE = "unidade";
    private static final String MATRIZ = "matriz";
    private static final String PARAMETRO_PESO = "parametroPeso";
    private static final String PARAMETRO_PESO_VALOR = "parametroPeso.valor";
    private static final String MATRIZ_PK = "matriz.pk";
    private static final String PK = "pk";

    public AtividadeDAO() {
        super(Atividade.class, AtividadeVO.class);
    }

    @Override
    protected void montarCriterios(Criteria criteria, ConsultaAtividadeVO consulta) {
        adicionarFiltroNome(consulta, criteria);
        // Atributos que serão retornados na consulta
        criteria.setProjection(Projections.projectionList().add(Projections.property(PK), PK)
                .add(Projections.property(NOME), NOME).add(Projections.property(MATRIZ), MATRIZ));
    }

    private void adicionarFiltroNome(ConsultaAtividadeVO consulta, Criteria criteria) {
        if (StringUtils.isNotEmpty(consulta.getNome())) {
            criteria.add(Restrictions.eq(NOME, consulta.getNome()));
        }

        if (consulta.getMatriz() != null) {
            criteria.add(Restrictions.eq(MATRIZ, consulta.getMatriz()));
        }
    }

    public Atividade buscarAtividadesMatriz(Matriz matriz, AvaliacaoRiscoControle avaliacao) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        Criteria celulasCriteria = criteria.createCriteria(CELULAS_RISCO_CONTROLE);
        celulasCriteria.add(Restrictions.or(Restrictions.eq("arcRisco", avaliacao),
                Restrictions.eq("arcControle", avaliacao)));
        
        criteria.add(Restrictions.eq(MATRIZ, matriz));
        return (Atividade) criteria.uniqueResult();
    }

    public List<Atividade> buscarAtividadesMatriz(Matriz matriz) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(MATRIZ, matriz));
        criteria.add(Restrictions.isNull(UNIDADE));
        criteria.addOrder(Order.asc(NOME));
        return cast(criteria.list());
    }

    public List<Atividade> buscarTodasAtividadesMatriz(Matriz matriz) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(MATRIZ, matriz));
        criteria.addOrder(Order.asc(NOME));
        return cast(criteria.list());
    }

    public List<Atividade> buscarTodasAtividadesMatrizPorPerfil(Matriz matriz,
            List<VersaoPerfilRisco> versoesPerfilRiscoCelula) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        Criteria celulasCriteria = criteria.createCriteria(CELULAS_RISCO_CONTROLE);
        celulasCriteria.add(Restrictions.in("versaoPerfilRisco", versoesPerfilRiscoCelula));
        criteria.add(Restrictions.eq(MATRIZ, matriz));
        criteria.addOrder(Order.asc(NOME));
        return cast(criteria.list());
    }

    public List<Atividade> buscarAtividadesUnidade(Unidade unidade) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(UNIDADE, unidade));
        criteria.addOrder(Order.asc(NOME));
        return cast(criteria.list());
    }

    public Long somarPesoAtividade(Matriz matriz, TipoUnidadeAtividadeEnum tipoAtividade, Integer pkUnidade) {
        Criteria criteria = getCurrentSession().createCriteria(Atividade.class);
        criteria.createAlias(PARAMETRO_PESO, PARAMETRO_PESO, JoinType.LEFT_OUTER_JOIN);
        criteria.add(Restrictions.eq(MATRIZ_PK, matriz.getPk()));

        criteria.add(Restrictions.eq("tipoAtividade", tipoAtividade));

        if (pkUnidade == null) {
            criteria.add(Restrictions.isNull(UNIDADE));
        } else {
            criteria.add(Restrictions.eq("unidade.pk", pkUnidade));
        }
        criteria.setProjection(Projections.sum(PARAMETRO_PESO_VALOR));
        Long retorno = (Long) criteria.uniqueResult();
        return retorno == null ? 0 : retorno;
    }

}
