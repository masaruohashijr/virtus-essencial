package br.gov.bcb.sisaps.src.dao.analisequantitativa;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.props.PropertyUtils;
import br.gov.bcb.sisaps.dao.GenericDAOLocal;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.ContaAnaliseQuantitativa;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.ContaQuadroPosicaoFinanceira;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.QuadroPosicaoFinanceira;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoConta;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;

@Repository
@Transactional
public class ContaQuadroPosicaoFinanceiraDAO extends GenericDAOLocal<ContaQuadroPosicaoFinanceira, Integer> {
    private static final String CONTA_ANALISE_QUANTITATIVA = "contaAnaliseQuantitativa";
    private static final String CONTA = "conta";
    private static final String LAYOUT_CONTA_CONTA_ANALISE_QUANTITATIVA = "layoutConta.contaAnaliseQuantitativa";
    private static final String QUADRO_POSICAO_FINANCEIRA_PK = "quadroPosicaoFinanceira.pk";
    private static final String LAYOUT_CONTA = "layoutConta";
    private static final String LAYOUT_CONTA_ANALISE_QUANTITATIVA = "layoutContaAnaliseQuantitativa";
    private static final String PONTO = ".";
    private static final String QUADRO = "quadro";
    private static final String PK = "pk";

    public ContaQuadroPosicaoFinanceiraDAO() {
        super(ContaQuadroPosicaoFinanceira.class);
    }

    @Transactional
    public void salvar(ContaQuadroPosicaoFinanceira contaQuadroPosicaoFinanceira) {
        save(contaQuadroPosicaoFinanceira);
    }

    @SuppressWarnings("unchecked")
    public List<ContaQuadroPosicaoFinanceira> buscarPorQuadro(QuadroPosicaoFinanceira quadroPosicaoFinanceira) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.createAlias(PropertyUtils.property(PropertyUtils.getPropertyObject(ContaQuadroPosicaoFinanceira.class)
                .getQuadroPosicaoFinanceira()), QUADRO);
        criteria.add(Restrictions.eq(QUADRO + PONTO + PK, quadroPosicaoFinanceira.getPk()));
        return (List<ContaQuadroPosicaoFinanceira>) criteria.list();
    }
    
    @SuppressWarnings("unchecked")
    public List<ContaQuadroPosicaoFinanceira> buscarPorQuadroETipo(QuadroPosicaoFinanceira quadroPosicaoFinanceira, 
            TipoConta tipoConta) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.createAlias(PropertyUtils.property(PropertyUtils.getPropertyObject(ContaQuadroPosicaoFinanceira.class)
                .getQuadroPosicaoFinanceira()), QUADRO);
        Criteria criteriaLayout = criteria.createCriteria(LAYOUT_CONTA_ANALISE_QUANTITATIVA);
        criteriaLayout.createAlias(CONTA_ANALISE_QUANTITATIVA, CONTA);
        criteria.add(Restrictions.eq(QUADRO + PONTO + PK, quadroPosicaoFinanceira.getPk()));
        criteriaLayout.add(Restrictions.eq("conta.tipoConta", tipoConta));
        return (List<ContaQuadroPosicaoFinanceira>) criteria.list();
    }

    @SuppressWarnings("unchecked")
    public List<ContaQuadroPosicaoFinanceira> buscarPorPkContaPai(ContaAnaliseQuantitativa contaSubNivel,
            QuadroPosicaoFinanceira quadro) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.createAlias(LAYOUT_CONTA_ANALISE_QUANTITATIVA, LAYOUT_CONTA);
        criteria.createAlias("layoutConta.contaAnaliseQuantitativaPai", "contaPai");
        criteria.add(Restrictions.eq("contaPai.pk", contaSubNivel.getPk()));
        criteria.add(Restrictions.eq(QUADRO_POSICAO_FINANCEIRA_PK, quadro.getPk()));
        return (List<ContaQuadroPosicaoFinanceira>) criteria.list();
    }

    public ContaQuadroPosicaoFinanceira buscaContaRootSubNivel(ContaAnaliseQuantitativa contaRoot,
            ContaQuadroPosicaoFinanceira contaQuadro) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.createAlias(LAYOUT_CONTA_ANALISE_QUANTITATIVA, LAYOUT_CONTA);
        criteria.createAlias(LAYOUT_CONTA_CONTA_ANALISE_QUANTITATIVA, "contaFilha");
        criteria.add(Restrictions.eq("contaFilha.pk", contaRoot.getPk()));
        criteria.add(Restrictions.eq(QUADRO_POSICAO_FINANCEIRA_PK, contaQuadro.getQuadroPosicaoFinanceira().getPk()));
        return (ContaQuadroPosicaoFinanceira) criteria.uniqueResult();
    }

    public ContaQuadroPosicaoFinanceira obterContaVigenteEmExibicaoPorNome(String nome,
            QuadroPosicaoFinanceira quadroVigente) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteriaComum(nome, quadroVigente, criteria);
        criteria.add(Restrictions.eq("exibir", SimNaoEnum.SIM));
        return (ContaQuadroPosicaoFinanceira) criteria.uniqueResult();
    }

    private void criteriaComum(String nome, QuadroPosicaoFinanceira quadroVigente, Criteria criteria) {
        criteria.createAlias(PropertyUtils.property(PropertyUtils.getPropertyObject(ContaQuadroPosicaoFinanceira.class)
                .getQuadroPosicaoFinanceira()), QUADRO);
        criteria.createAlias(LAYOUT_CONTA_ANALISE_QUANTITATIVA, LAYOUT_CONTA);
        criteria.createAlias(LAYOUT_CONTA + PONTO + CONTA_ANALISE_QUANTITATIVA, CONTA);
        criteria.add(Restrictions.eq(CONTA + PONTO + "nome", nome));
        criteria.add(Restrictions.eq(QUADRO + PONTO + PK, quadroVigente.getPk()));
    }
}
