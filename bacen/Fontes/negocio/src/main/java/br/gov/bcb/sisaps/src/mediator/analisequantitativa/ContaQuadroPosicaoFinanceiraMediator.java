package br.gov.bcb.sisaps.src.mediator.analisequantitativa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.sisaps.src.dao.analisequantitativa.ContaQuadroPosicaoFinanceiraDAO;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.ContaAnaliseQuantitativa;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.ContaQuadroPosicaoFinanceira;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.QuadroPosicaoFinanceira;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoConta;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import br.gov.bcb.sisaps.util.spring.SpringUtilsExtended;

@Service
public class ContaQuadroPosicaoFinanceiraMediator {

    @Autowired
    private ContaQuadroPosicaoFinanceiraDAO contaQuadroPosicaoFinanceiraDAO;

    public static ContaQuadroPosicaoFinanceiraMediator get() {
        return SpringUtilsExtended.get().getBean(ContaQuadroPosicaoFinanceiraMediator.class);
    }

    @Transactional(readOnly = true)
    public List<ContaQuadroPosicaoFinanceira> buscarPorQuadro(QuadroPosicaoFinanceira quadroPosicaoFinanceira) {
        List<ContaQuadroPosicaoFinanceira> contas =
                contaQuadroPosicaoFinanceiraDAO.buscarPorQuadro(quadroPosicaoFinanceira);
        return contas;
    }
    
    @Transactional(readOnly = true)
    public List<ContaQuadroPosicaoFinanceira> obterContasNovo(QuadroPosicaoFinanceira quadro) {
        List<ContaQuadroPosicaoFinanceira> retorno = new ArrayList<ContaQuadroPosicaoFinanceira>();

        List<ContaQuadroPosicaoFinanceira> contasAtivo = 
                contaQuadroPosicaoFinanceiraDAO.buscarPorQuadroETipo(quadro, TipoConta.ATIVO);
        preencherContaNovo(contasAtivo, TipoConta.ATIVO);
        
        List<ContaQuadroPosicaoFinanceira> contasPassivo = 
                contaQuadroPosicaoFinanceiraDAO.buscarPorQuadroETipo(quadro, TipoConta.PASSIVO);
        preencherContaNovo(contasPassivo, TipoConta.PASSIVO);
        
        retorno.addAll(contasAtivo);
        retorno.addAll(contasPassivo);
        return retorno;
    }
    
    public List<ContaQuadroPosicaoFinanceira> preencherContaNovo(
            List<ContaQuadroPosicaoFinanceira> contas, TipoConta tipoConta) {
        Integer valorTotalAjustadoPai = 0;
        
        for (ContaQuadroPosicaoFinanceira contaQuadro : contas) {
            if (contaQuadro.isContaRaiz()) {
                valorTotalAjustadoPai = totalAjustadoPai(contaQuadro);
            }
        }
        
        return ajustarTotalAjustadoPaiEDiversosNovo(contas, valorTotalAjustadoPai);
    }
    
    public List<ContaQuadroPosicaoFinanceira> ajustarTotalAjustadoPaiEDiversosNovo(
            List<ContaQuadroPosicaoFinanceira> contas, Integer valorTotalAjustadoPai) {
        List<ContaQuadroPosicaoFinanceira> lista = new LinkedList<ContaQuadroPosicaoFinanceira>();
        
        for (ContaQuadroPosicaoFinanceira contaQuadro : contas) {
            contaQuadro.setValorTotalAjustado(valorTotalAjustadoPai);
            ContaAnaliseQuantitativa contaAnaliseQuantitativa = 
                    contaQuadro.getLayoutContaAnaliseQuantitativa().getContaAnaliseQuantitativa();
            if (SimNaoEnum.SIM.equals(contaAnaliseQuantitativa.getDiversos())) {
                ContaQuadroPosicaoFinanceira contaQuadroSuperior =
                        buscaContaNivelSuperior(contaQuadro.getLayoutContaAnaliseQuantitativa()
                                .getContaAnaliseQuantitativaPai(), contaQuadro);
                List<ContaQuadroPosicaoFinanceira> contasTodasFilhas = new LinkedList<ContaQuadroPosicaoFinanceira>();
                List<ContaQuadroPosicaoFinanceira> contasFilhas =
                        ContaQuadroPosicaoFinanceiraMediator.get().buscarContasSubNivelPorPkContaPai(
                                contaQuadroSuperior.getLayoutContaAnaliseQuantitativa().getContaAnaliseQuantitativa(),
                                contaQuadroSuperior.getQuadroPosicaoFinanceira());
                montarCalculoDiverso(contasFilhas, contasTodasFilhas);
                int somatorio = 0;
                for (ContaQuadroPosicaoFinanceira contaFilha : contasTodasFilhas) {
                    if (SimNaoEnum.SIM.equals(contaFilha.getExibir())
                            && SimNaoEnum.NAO.equals(contaFilha.getLayoutContaAnaliseQuantitativa()
                                    .getContaAnaliseQuantitativa().getDiversos()) && contaFilha.getValor() != null) {
                            somatorio = somatorio + Integer.valueOf(contaFilha.getValor());
                    }
                }
                if (contaQuadroSuperior.getValor() != null) {
                    contaQuadro.setValor((contaQuadroSuperior.getValor() - somatorio));
                }
            }
            lista.add(contaQuadro);
        }
        
        return lista;
    }
    
    private Integer totalAjustadoPai(ContaQuadroPosicaoFinanceira contaQuadro) {
        Integer valorTotalAjustadoPai = 0;
        if (contaQuadro.getValor() == null) {
            valorTotalAjustadoPai =
                    0 - (contaQuadro.getValorAjuste() == null ? 0 : contaQuadro.getValorAjuste());
        } else {
            valorTotalAjustadoPai =
                    contaQuadro.getValorAjuste() == null ? contaQuadro.getValor() : contaQuadro.getValor()
                            - contaQuadro.getValorAjuste();
        }
        return valorTotalAjustadoPai;
    }

    public void montarCalculoDiverso(List<ContaQuadroPosicaoFinanceira> contas,
            List<ContaQuadroPosicaoFinanceira> contasTodosFilhos) {
        for (ContaQuadroPosicaoFinanceira contaQuadroPosicaoFinanceira : contas) {
            List<ContaQuadroPosicaoFinanceira> contasFilhas =
                    ContaQuadroPosicaoFinanceiraMediator.get().buscarContasSubNivelPorPkContaPai(
                            contaQuadroPosicaoFinanceira.getLayoutContaAnaliseQuantitativa()
                                    .getContaAnaliseQuantitativa(),
                            contaQuadroPosicaoFinanceira.getQuadroPosicaoFinanceira());
            contasTodosFilhos.add(contaQuadroPosicaoFinanceira);
            if (CollectionUtils.isNotEmpty(contasFilhas)) {
                montarCalculoDiverso(contasFilhas, contasTodosFilhos);
            }
        }
    }

    @Transactional(readOnly = true)
    public ContaQuadroPosicaoFinanceira buscarPorPk(Integer pk) {
        return contaQuadroPosicaoFinanceiraDAO.getRecord(pk);
    }

    @Transactional(readOnly = true)
    public List<ContaQuadroPosicaoFinanceira> buscarContasSubNivelPorPkContaPai(ContaAnaliseQuantitativa contaSubNivel,
            QuadroPosicaoFinanceira quadro) {
        List<ContaQuadroPosicaoFinanceira> contas =
                contaQuadroPosicaoFinanceiraDAO.buscarPorPkContaPai(contaSubNivel, quadro);
        for (ContaQuadroPosicaoFinanceira conta : contas) {
            if (!Hibernate.isInitialized(conta.getLayoutContaAnaliseQuantitativa())) {
                Hibernate.initialize(conta.getLayoutContaAnaliseQuantitativa());
            }
            if (!Hibernate.isInitialized(conta.getLayoutContaAnaliseQuantitativa().getContaAnaliseQuantitativa())) {
                Hibernate.initialize(conta.getLayoutContaAnaliseQuantitativa().getContaAnaliseQuantitativa());
            }
            if (!Hibernate.isInitialized(conta.getLayoutContaAnaliseQuantitativa().getContaAnaliseQuantitativaPai())) {
                Hibernate.initialize(conta.getLayoutContaAnaliseQuantitativa().getContaAnaliseQuantitativaPai());
            }
        }
        return contas;
    }

    @Transactional(readOnly = true)
    public ContaQuadroPosicaoFinanceira buscaContaNivelSuperior(ContaAnaliseQuantitativa contaRoot,
            ContaQuadroPosicaoFinanceira contaQuadro) {
        return contaQuadroPosicaoFinanceiraDAO.buscaContaRootSubNivel(contaRoot, contaQuadro);
    }

    @Transactional(readOnly = true)
    public ContaQuadroPosicaoFinanceira buscaContaPorNome(QuadroPosicaoFinanceira quadroVigente, String nome) {
        return contaQuadroPosicaoFinanceiraDAO.obterContaVigenteEmExibicaoPorNome(nome, quadroVigente);
    }

    @Transactional(readOnly = true)
    public List<ContaQuadroPosicaoFinanceira> buscaContasNivelSuperior(List<ContaQuadroPosicaoFinanceira> contas,
            ContaQuadroPosicaoFinanceira contaQuadro) {
        if (contaQuadro.getLayoutContaAnaliseQuantitativa().getContaAnaliseQuantitativaPai() != null) {

            System.out.println("##BUGQUADRO contaQuadro.getPk(): " + contaQuadro.getPk());
            System.out.println("##BUGQUADRO contaQuadro.getLayoutContaAnaliseQuantitativa().getPk(): "
                    + contaQuadro.getLayoutContaAnaliseQuantitativa().getPk());

            if (contaQuadro.getLayoutContaAnaliseQuantitativa().getRoot() != null) {
                System.out.println(
                        "##BUGQUADRO contaQuadro.getLayoutContaAnaliseQuantitativa().getRoot().getPk(): "
                        + contaQuadro.getLayoutContaAnaliseQuantitativa().getRoot().getPk());
            } else {
                System.out
                        .println("##BUGQUADRO contaQuadro.getLayoutContaAnaliseQuantitativa().getRoot().getPk() NULL ");
            }

            ContaQuadroPosicaoFinanceira contaNivelSuperior =
                    ContaQuadroPosicaoFinanceiraMediator.get().buscaContaNivelSuperior(
                            contaQuadro.getLayoutContaAnaliseQuantitativa().getRoot(), contaQuadro);

            if (contaNivelSuperior != null) {
                System.out.println("##BUGQUADRO contaNivelSuperior.getPk(): " + contaNivelSuperior.getPk());

                if (contaNivelSuperior.getLayoutContaAnaliseQuantitativa() != null) {
                    System.out.println("##BUGQUADRO contaNivelSuperior.getLayoutContaAnaliseQuantitativa().getPk(): "
                            + contaNivelSuperior.getLayoutContaAnaliseQuantitativa().getPk());

                    if (contaNivelSuperior.getLayoutContaAnaliseQuantitativa().getSubNivel() != null) {
                        System.out.println(
                                "##BUGQUADRO contaNivelSuperior.getLayoutContaAnaliseQuantitativa().getSubNivel().getCodigo(): "
                                + contaNivelSuperior.getLayoutContaAnaliseQuantitativa().getSubNivel().getCodigo());
                    } else {
                        System.out.println(
                                "##BUGQUADRO contaNivelSuperior.getLayoutContaAnaliseQuantitativa().getSubNivel() NULL ");
                    }
                } else {
                    System.out.println("##BUGQUADRO contaNivelSuperior.getLayoutContaAnaliseQuantitativa() NULL ");
                }
            } else {
                System.out.println("##BUGQUADRO contaNivelSuperior NULL ");
            }

            if (SimNaoEnum.NAO.equals(contaNivelSuperior.getLayoutContaAnaliseQuantitativa().getSubNivel())) {
                contas.add(contaNivelSuperior);
                buscaContasNivelSuperior(contas, contaNivelSuperior);
            } else {
                buscaContasNivelSuperior(contas, contaNivelSuperior);
            }
        }
        return contas;
    }

    public void alterar(ContaQuadroPosicaoFinanceira conta) {
        contaQuadroPosicaoFinanceiraDAO.update(conta);
    }

    public void ajustarCalculoAjusteContasSuperiores(ContaQuadroPosicaoFinanceira conta) {
        List<ContaQuadroPosicaoFinanceira> contasNivelSuperior =
                ContaQuadroPosicaoFinanceiraMediator.get().buscaContasNivelSuperior(
                        new LinkedList<ContaQuadroPosicaoFinanceira>(), conta);
        Collections.reverse(contasNivelSuperior);
        Integer valorTotalAjustadoContaPai = null;
        for (ContaQuadroPosicaoFinanceira contaNivelSuperior : contasNivelSuperior) {
            List<ContaQuadroPosicaoFinanceira> contas =
                    ContaQuadroPosicaoFinanceiraMediator.get().buscarContasSubNivelPorPkContaPai(
                            contaNivelSuperior.getLayoutContaAnaliseQuantitativa().getContaAnaliseQuantitativa(),
                            contaNivelSuperior.getQuadroPosicaoFinanceira());
            Integer somatorio = montarSomatorio(contas);
            contaNivelSuperior.setValorAjuste(somatorio);
            if (contaNivelSuperior.isContaRaiz()) {
                valorTotalAjustadoContaPai = contaNivelSuperior.getAjustado();
            }
            contaNivelSuperior.setValorTotalAjustado(valorTotalAjustadoContaPai);
        }
    }

    private Integer montarSomatorio(List<ContaQuadroPosicaoFinanceira> contas) {
        Integer somatorio = 0;
        for (ContaQuadroPosicaoFinanceira contaQuadroPosicaoFinanceira : contas) {
            List<ContaQuadroPosicaoFinanceira> contasFilhas =
                    ContaQuadroPosicaoFinanceiraMediator.get().buscarContasSubNivelPorPkContaPai(
                            contaQuadroPosicaoFinanceira.getLayoutContaAnaliseQuantitativa()
                                    .getContaAnaliseQuantitativa(),
                            contaQuadroPosicaoFinanceira.getQuadroPosicaoFinanceira());
            if (SimNaoEnum.SIM.equals(contaQuadroPosicaoFinanceira.getLayoutContaAnaliseQuantitativa()
                    .getEditarAjuste()) && contaQuadroPosicaoFinanceira.getValorAjuste() != null) {
                    somatorio = somatorio + contaQuadroPosicaoFinanceira.getValorAjuste();
            }
            if (CollectionUtils.isNotEmpty(contasFilhas)) {
                somatorio = somatorio + montarSomatorio(contasFilhas);
            }
        }
        return somatorio;
    }

}
