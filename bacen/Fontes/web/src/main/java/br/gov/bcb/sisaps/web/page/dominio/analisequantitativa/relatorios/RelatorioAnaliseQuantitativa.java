/*
 * Sistema: AUD
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arquivo contém informações proprietárias.
 */

package br.gov.bcb.sisaps.web.page.dominio.analisequantitativa.relatorios;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

import org.apache.commons.collections.CollectionUtils;
import org.apache.wicket.protocol.http.WebApplication;

import br.gov.bcb.sisaps.src.dominio.AnexoQuadroPosicaoFinanceira;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.ContaAnaliseQuantitativa;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.ContaQuadroPosicaoFinanceira;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.LayoutContaAnaliseQuantitativa;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.QuadroPosicaoFinanceira;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoConta;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoInformacaoEnum;
import br.gov.bcb.sisaps.src.mediator.AnexoQuadroPosicaoFinanceiraMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativa.QuadroPosicaoFinanceiraMediator;
import br.gov.bcb.sisaps.src.vo.analisequantitativa.OutraInformacaoVO;
import br.gov.bcb.sisaps.src.vo.analisequantitativa.QuadroPosicaoFinanceiraVO;
import br.gov.bcb.sisaps.src.vo.relatorio.RelatorioAnaliseQuantitativaVO;
import br.gov.bcb.sisaps.src.vo.relatorio.RelatorioContaQuadroPosicaoFinanceiraVO;
import br.gov.bcb.sisaps.src.vo.relatorio.RelatorioIndiceVO;
import br.gov.bcb.sisaps.src.vo.relatorio.RelatorioPatrimonioVO;
import br.gov.bcb.sisaps.src.vo.relatorio.RelatorioResultadoQuadroPosicaoFinanceiraVO;
import br.gov.bcb.sisaps.src.vo.relatorio.RelatorioResultadoVO;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.Util;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import br.gov.bcb.sisaps.util.validacao.InfraException;
import br.gov.bcb.sisaps.web.page.componentes.util.RelatorioAbstrato;
import br.gov.bcb.wicket.pages.AbstractBacenWebPage;

/**
 * Relatorio de Análise Quantitativa.
 */
public class RelatorioAnaliseQuantitativa extends RelatorioAbstrato {

    /**
     * Título do relatório.
     */
    public static final String TITULO_RELATORIO = "Relatório de Análise Quantitativa";

    /**
     * Nome do arquivo do relatório enviado ao usuário.
     */
    public static final String NOME_ARQUIVO_RELATORIO = "RelatorioAnaliseQuantitativa";

    private static final String NOME_ARQUIVO_JASPER = "/relatorios/RelatorioAnaliseQuantitativa.jasper";
    private static final String TRACO = " - ";

    private List<RelatorioAnaliseQuantitativaVO> dadosRelatorioAnaliseQuantitativa;
    private List<RelatorioContaQuadroPosicaoFinanceiraVO> dadosSubrelatorioContasAtivo;
    private List<RelatorioContaQuadroPosicaoFinanceiraVO> dadosSubrelatorioContasPassivo;
    private List<RelatorioPatrimonioVO> dadosSubrelatorioPatrimonios;
    private List<RelatorioResultadoQuadroPosicaoFinanceiraVO> dadosSubrelatorioResultados;
    private List<RelatorioResultadoVO> dadosSubrelatorioResultadosNovo;
    private List<RelatorioResultadoVO> periodosResultados;
    private List<RelatorioIndiceVO> dadosSubrelatorioIndices;
    private final RelatorioAnaliseQuantitativaVO relatorioAnaliseQuantitativaVO = new RelatorioAnaliseQuantitativaVO();
    private final PerfilRisco perfilRiscoAtual;
    private final QuadroPosicaoFinanceiraVO quadroVO;
    private Boolean exibirAjustadoContasAtivo = false;
    private Boolean exibirAjustadoContasPassivo = false;
    private Boolean exibirAjustadoPatrimonios = false;
    private Boolean exibirAjustadoLucros = false;
    private Boolean exibirAjustadoRspla = false;
    private Boolean exibirAjustadoIndices = false;
    private Boolean possuiAnexo = false;
    private String nomeChaveAnexo = "chaveAnexoQPF";
    private Map<String, List<byte[]>> mapAnexo = new HashMap<String, List<byte[]>>();

    public RelatorioAnaliseQuantitativa(PerfilRisco perfilRiscoAtual, QuadroPosicaoFinanceiraVO quadroVO) {
        this.perfilRiscoAtual = perfilRiscoAtual;
        this.quadroVO = quadroVO;
        converterDados();

        QuadroPosicaoFinanceira quadro = QuadroPosicaoFinanceiraMediator.get().buscarQuadroPorPk(quadroVO.getPk());
        if (!Util.isNuloOuVazio(quadro.getAnexosQuadro())) {
            addMapAnexo(nomeChaveAnexo, AnexoQuadroPosicaoFinanceiraMediator.get().buscar(quadro));
            possuiAnexo = true;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<?> getDadosRelatorio() {
        return dadosRelatorioAnaliseQuantitativa;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNomeArquivoJasper() {
        return NOME_ARQUIVO_JASPER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTituloRelatorio() {
        return TITULO_RELATORIO;
    }

    private void addMapAnexo(String chave, List<AnexoQuadroPosicaoFinanceira> anexos) {
        List<byte[]> conclusao = new ArrayList<byte[]>();
        for (AnexoQuadroPosicaoFinanceira anexo : anexos) {
            conclusao.add(AnexoQuadroPosicaoFinanceiraMediator.get().recuperarArquivo(anexo.getLink(),
                    anexo.getQuadroPosicaoFinanceira()));
        }
        mapAnexo.put(chave, conclusao);
    }

    private void converterDados() {
        dadosRelatorioAnaliseQuantitativa = new ArrayList<RelatorioAnaliseQuantitativaVO>();
        dadosSubrelatorioContasAtivo = new ArrayList<RelatorioContaQuadroPosicaoFinanceiraVO>();
        dadosSubrelatorioContasPassivo = new ArrayList<RelatorioContaQuadroPosicaoFinanceiraVO>();
        dadosSubrelatorioPatrimonios = new ArrayList<RelatorioPatrimonioVO>();
        dadosSubrelatorioResultados = new ArrayList<RelatorioResultadoQuadroPosicaoFinanceiraVO>();
        dadosSubrelatorioResultadosNovo = new ArrayList<RelatorioResultadoVO>();
        periodosResultados = new ArrayList<RelatorioResultadoVO>();
        dadosSubrelatorioIndices = new ArrayList<RelatorioIndiceVO>();
        relatorioAnaliseQuantitativaVO.setNomeConglomerado(perfilRiscoAtual.getCiclo().getEntidadeSupervisionavel()
                .getNomeConglomeradoFormatado());
        relatorioAnaliseQuantitativaVO.setRodape(montarRodape());
        montarListaContas();
        montarListaPatrimonios();
        montarListaResultadosNovo();
        montarListaIndices();
        relatorioAnaliseQuantitativaVO.setDataBase(quadroVO.getDataBaseES() == null ? "" : quadroVO.getDataBaseES()
                .getDataBaseFormatada());
        relatorioAnaliseQuantitativaVO.setContasQuadroAtivo(dadosSubrelatorioContasAtivo);
        relatorioAnaliseQuantitativaVO.setContasQuadroPassivo(dadosSubrelatorioContasPassivo);
        relatorioAnaliseQuantitativaVO.setPatrimonios(dadosSubrelatorioPatrimonios);
        relatorioAnaliseQuantitativaVO.setResultados(dadosSubrelatorioResultados);
        relatorioAnaliseQuantitativaVO.setResultadosNovo(dadosSubrelatorioResultadosNovo);
        relatorioAnaliseQuantitativaVO.setPeriodos(periodosResultados);
        relatorioAnaliseQuantitativaVO.setIndices(dadosSubrelatorioIndices);
        dadosRelatorioAnaliseQuantitativa.add(relatorioAnaliseQuantitativaVO);
    }

    private void montarListaContas() {
        List<ContaQuadroPosicaoFinanceira> listaContasAtivo =
                quadroVO.getListaContasSelecionadasQuadroPorTipo(TipoConta.ATIVO);
        List<ContaQuadroPosicaoFinanceira> listaContasPassivo =
                quadroVO.getListaContasSelecionadasQuadroPorTipo(TipoConta.PASSIVO);
        adicionarLinhasSeparacaoDepois(listaContasAtivo);
        adicionarLinhasSeparacaoDepois(listaContasPassivo);
        montarListaContasPorTipo(listaContasAtivo, listaContasPassivo.size(), TipoConta.ATIVO);
        montarListaContasPorTipo(listaContasPassivo, listaContasAtivo.size(), TipoConta.PASSIVO);
    }

    private void montarListaContasPorTipo(List<ContaQuadroPosicaoFinanceira> listaContas, int sizeQuadroOposto,
            TipoConta tipoConta) {
        if (!Util.isNuloOuVazio(listaContas)) {
            for (ContaQuadroPosicaoFinanceira conta : listaContas) {
                RelatorioContaQuadroPosicaoFinanceiraVO contaVO = new RelatorioContaQuadroPosicaoFinanceiraVO();
                contaVO.setDescricaoConta(conta.getLayoutContaAnaliseQuantitativa().getContaAnaliseQuantitativa()
                        .getDescricao());
                contaVO.setValor(conta.getValorFormatado());
                contaVO.setValorAjustado(conta.getAjustadoFormatado());
                contaVO.setEscore(conta.getEscore());
                contaVO.setTipoConta(tipoConta.getDescricao());
                contaVO.setExibirValorAjustado(false);
                contaVO.setIsNegrito(conta.getLayoutContaAnaliseQuantitativa().getNegrito() == null ? false : conta
                        .getLayoutContaAnaliseQuantitativa().getNegrito().booleanValue());
                contaVO.setSequencial(conta.getLayoutContaAnaliseQuantitativa().getSequencial());
                if (tipoConta.equals(TipoConta.ATIVO)) {
                    dadosSubrelatorioContasAtivo.add(contaVO);
                } else {
                    dadosSubrelatorioContasPassivo.add(contaVO);
                }
                if (conta.getAjustado() != null) {
                    if (tipoConta.equals(TipoConta.ATIVO)) {
                        exibirAjustadoContasAtivo = true;
                    } else {
                        exibirAjustadoContasPassivo = true;
                    }
                }
            }
            if (tipoConta.equals(TipoConta.ATIVO)) {
                Collections.sort(dadosSubrelatorioContasAtivo, new ContasComparator());
                complementarTabela(listaContas, sizeQuadroOposto, dadosSubrelatorioContasAtivo);
            } else {
                Collections.sort(dadosSubrelatorioContasPassivo, new ContasComparator());
                complementarTabela(listaContas, sizeQuadroOposto, dadosSubrelatorioContasPassivo);
            }
        }
    }

    public class ContasComparator implements Comparator<RelatorioContaQuadroPosicaoFinanceiraVO> {
        @Override
        public int compare(RelatorioContaQuadroPosicaoFinanceiraVO contaVO,
                RelatorioContaQuadroPosicaoFinanceiraVO contaVO2) {
            return contaVO.getSequencial().compareTo(contaVO2.getSequencial());
        }
    }

    private void montarListaPatrimonios() {
        if (CollectionUtils.isNotEmpty(quadroVO.getPatrimoniosNovo())) {
            exibirAjustadoPatrimonios = quadroVO.existeValorEditado(TipoInformacaoEnum.PATRIMONIO, null);
            for (OutraInformacaoVO patrimonio : quadroVO.getPatrimoniosNovo()) {
                RelatorioPatrimonioVO patrimonioVO = new RelatorioPatrimonioVO();
                patrimonioVO.setDescricaoPatrimonio(patrimonio.getDescricao());
                patrimonioVO.setValor(patrimonio.getValorFormatado());
                if (exibirAjustadoPatrimonios) {
                    patrimonioVO.setValorAjustado(patrimonio.getValorAjustadoFormatado());
                }
                dadosSubrelatorioPatrimonios.add(patrimonioVO);
            }
        }
    }

    private void montarListaResultadosNovo() {
        if (CollectionUtils.isNotEmpty(quadroVO.getNomesResultados())) {
            List<OutraInformacaoVO> resultados = quadroVO.getResultadosPorNome().get(
                    quadroVO.getNomesResultados().get(0));
            for (OutraInformacaoVO resultado : resultados) {
                RelatorioResultadoVO periodo = new RelatorioResultadoVO();
                periodo.setPeriodo(resultado.getPeriodoFormatado());
                periodosResultados.add(periodo);
            }
            criarRelatorioResultadosVO();
        }
    }

    private void criarRelatorioResultadosVO() {
        for (String nomeResultado : quadroVO.getNomesResultados()) {
            List<OutraInformacaoVO> resultados = quadroVO.getResultadosPorNome().get(nomeResultado);
            RelatorioResultadoVO resultadoVO = new RelatorioResultadoVO();
            RelatorioResultadoVO ajusteResultadoVO = null;
            boolean existeValorEditado = quadroVO.existeValorEditado(TipoInformacaoEnum.RESULTADO, nomeResultado);
            if (existeValorEditado) {
                ajusteResultadoVO = new RelatorioResultadoVO();
            }
            int i = 1;
            for (OutraInformacaoVO outraInfoVO : resultados) {
                String valor = outraInfoVO.getValorFormatado();
                String valorEditado = existeValorEditado ? outraInfoVO.getValorAjustadoFormatado() : null;
                
                resultadoVO.setTitulo(nomeResultado);
                if (existeValorEditado) {
                    ajusteResultadoVO.setTitulo("Ajustado");
                }
                setValores(resultadoVO, ajusteResultadoVO, existeValorEditado, i, valor, valorEditado);
                i++;
            }
            dadosSubrelatorioResultadosNovo.add(resultadoVO);
            if (existeValorEditado) {
                dadosSubrelatorioResultadosNovo.add(ajusteResultadoVO);
            }
        }
    }

    private void setValores(RelatorioResultadoVO resultadoVO, RelatorioResultadoVO ajusteResultadoVO,
            boolean existeValorEditado, int i, String valor, String valorEditado) {
        switch (i) {
            case 1:
                resultadoVO.setValorP1(valor);
                if (existeValorEditado) {
                    ajusteResultadoVO.setValorP1(valorEditado);
                }
                break;
            case 2:
                resultadoVO.setValorP2(valor);
                if (existeValorEditado) {
                    ajusteResultadoVO.setValorP2(valorEditado);
                }
                break;
            case 3:
                resultadoVO.setValorP3(valor);
                if (existeValorEditado) {
                    ajusteResultadoVO.setValorP3(valorEditado);
                }
                break;
            case 4:
                resultadoVO.setValorP4(valor);
                if (existeValorEditado) {
                    ajusteResultadoVO.setValorP4(valorEditado);
                }
                break;
            default:
                break;
        }
    }

    private void montarListaIndices() {
        if (CollectionUtils.isNotEmpty(quadroVO.getIndicesNovo())) {
            exibirAjustadoIndices = quadroVO.existeValorEditado(TipoInformacaoEnum.INDICE, null);
            for (OutraInformacaoVO indice : quadroVO.getIndicesNovo()) {
                RelatorioIndiceVO patrimonioVO = new RelatorioIndiceVO();
                patrimonioVO.setDescricaoIndice(indice.getDescricao());
                patrimonioVO.setValor(indice.getValorFormatado());
                if (exibirAjustadoIndices) {
                    patrimonioVO.setValorAjustado(indice.getValorAjustadoFormatado());
                }
                dadosSubrelatorioIndices.add(patrimonioVO);
            }
        }
    }

    private String montarRodape() {
        StringBuffer rodape = new StringBuffer();
        rodape.append("Versão ");
        rodape.append(perfilRiscoAtual.getDataCriacao() == null ? "" : perfilRiscoAtual.getDataCriacao().toString(
                Constantes.FORMATO_DATA_COM_BARRAS));
        rodape.append(TRACO);
        rodape.append("Ciclo ");
        rodape.append(perfilRiscoAtual.getCiclo().getDataInicioFormatada());
        rodape.append(" ~ ");
        rodape.append(perfilRiscoAtual.getCiclo().getDataPrevisaoFormatada());
        return rodape.toString();
    }

    @Override
    protected void adicionarParametros(Map<String, Object> parametrosJasper, AbstractBacenWebPage paginaAtual) {
        WebApplication wa = (WebApplication) paginaAtual.getApplication();
        JasperReport subRelatorioContas = null;
        JasperReport subRelatorioPatrimonios = null;
        JasperReport subRelatorioResultados = null;
        JasperReport subRelatorioResultadosNovo = null;
        JasperReport subRelatorioResultadosPeriodos = null;
        JasperReport subRelatorioIndices = null;
        parametrosJasper.put("rodape", relatorioAnaliseQuantitativaVO.getRodape());

        try {
            subRelatorioContas =
                    (JasperReport) JRLoader.loadObject(wa.getServletContext().getResourceAsStream(
                            "/relatorios/SubrelatorioContas.jasper"));
            subRelatorioPatrimonios =
                    (JasperReport) JRLoader.loadObject(wa.getServletContext().getResourceAsStream(
                            "/relatorios/SubrelatorioPatrimonios.jasper"));
            subRelatorioResultados =
                    (JasperReport) JRLoader.loadObject(wa.getServletContext().getResourceAsStream(
                            "/relatorios/SubrelatorioResultados.jasper"));
            
            subRelatorioResultadosNovo =
                    (JasperReport) JRLoader.loadObject(wa.getServletContext().getResourceAsStream(
                            "/relatorios/SubrelatorioResultadosNovo.jasper"));
            
            subRelatorioResultadosPeriodos =
                    (JasperReport) JRLoader.loadObject(wa.getServletContext().getResourceAsStream(
                            "/relatorios/SubrelatorioResultadosPeriodos.jasper"));
            subRelatorioIndices =
                    (JasperReport) JRLoader.loadObject(wa.getServletContext().getResourceAsStream(
                            "/relatorios/SubrelatorioIndices.jasper"));
        } catch (JRException e) {
            throw new InfraException("Erro ao carregar o subrelatorio de sínteses.", e);
        }

        setarParametros(parametrosJasper, subRelatorioContas, subRelatorioPatrimonios, subRelatorioResultados,
                subRelatorioResultadosNovo, subRelatorioResultadosPeriodos, subRelatorioIndices);
    }

    private void setarParametros(Map<String, Object> parametrosJasper, JasperReport subRelatorioContas,
            JasperReport subRelatorioPatrimonios, JasperReport subRelatorioResultados,
            JasperReport subRelatorioResultadosNovo, JasperReport subRelatorioResultadosPeriodos,
            JasperReport subRelatorioIndices) {
        parametrosJasper.put("SUB_RELATORIO_CONTAS", subRelatorioContas);
        parametrosJasper.put("SUB_RELATORIO_PATRIMONIOS", subRelatorioPatrimonios);
        parametrosJasper.put("SUB_RELATORIO_RESULTADOS", subRelatorioResultados);
        parametrosJasper.put("SUB_RELATORIO_RESULTADOS_NOVO", subRelatorioResultadosNovo);
        parametrosJasper.put("SUB_RELATORIO_RESULTADOS_PERIODOS", subRelatorioResultadosPeriodos);
        parametrosJasper.put("SUB_RELATORIO_INDICES", subRelatorioIndices);
        List<String> result = new ArrayList<String>();
        result.add("");
        parametrosJasper.put("result", result);
        parametrosJasper.put("exibirAjustadoContasAtivo", exibirAjustadoContasAtivo);
        parametrosJasper.put("exibirAjustadoContasPassivo", exibirAjustadoContasPassivo);
        parametrosJasper.put("exibirAjustadoIndices", exibirAjustadoIndices);
        parametrosJasper.put("exibirAjustadoLucros", exibirAjustadoLucros);
        parametrosJasper.put("exibirAjustadoPatrimonios", exibirAjustadoPatrimonios);
        parametrosJasper.put("exibirAjustadoRspla", exibirAjustadoRspla);
        parametrosJasper.put("nomeChave", nomeChaveAnexo);
        parametrosJasper.put("possuiAnexo", possuiAnexo);
    }

    private void adicionarLinhasSeparacaoDepois(final List<ContaQuadroPosicaoFinanceira> listaContas) {
        for (ContaQuadroPosicaoFinanceira conta : new LinkedList<ContaQuadroPosicaoFinanceira>(listaContas)) {
            LayoutContaAnaliseQuantitativa layout = conta.getLayoutContaAnaliseQuantitativa();
            if (SimNaoEnum.SIM.equals(layout.getNegrito()) && !conta.isContaRaiz()) {
                criarLinhaEmBranco(listaContas, layout, layout.getSequencial() - 1);
            }
        }
    }

    private void criarLinhaEmBranco(final List<ContaQuadroPosicaoFinanceira> listaContas,
            LayoutContaAnaliseQuantitativa layout, Integer sequencial) {
        ContaAnaliseQuantitativa contaAnaliseQuantitativa = layout.getContaAnaliseQuantitativa();
        ContaQuadroPosicaoFinanceira contaEmBrancoVo = new ContaQuadroPosicaoFinanceira();
        LayoutContaAnaliseQuantitativa layoutContaEmBranco = new LayoutContaAnaliseQuantitativa();
        layoutContaEmBranco.setEditarAjuste(SimNaoEnum.NAO);
        layoutContaEmBranco.setObrigatorio(SimNaoEnum.SIM);
        ContaAnaliseQuantitativa contaAnaliseQuantitativaEmBranco = new ContaAnaliseQuantitativa();
        contaAnaliseQuantitativaEmBranco.setTipoConta(contaAnaliseQuantitativa.getTipoConta());
        contaAnaliseQuantitativaEmBranco.setDiversos(contaAnaliseQuantitativa.getDiversos());
        contaAnaliseQuantitativaEmBranco.setDescricao("\n");
        layoutContaEmBranco.setContaAnaliseQuantitativa(contaAnaliseQuantitativaEmBranco);
        layoutContaEmBranco.setContaAnaliseQuantitativaPai(layout.getContaAnaliseQuantitativaPai());
        layoutContaEmBranco.setSequencial(sequencial);
        contaEmBrancoVo.setLayoutContaAnaliseQuantitativa(layoutContaEmBranco);
        contaEmBrancoVo.setExibir(SimNaoEnum.NAO);
        listaContas.add(contaEmBrancoVo);
    }

    private void complementarTabela(List<ContaQuadroPosicaoFinanceira> listaContas, int sizeLista,
            List<RelatorioContaQuadroPosicaoFinanceiraVO> dadosSubrelatorioContas) {
        if (listaContas.size() < sizeLista) {
            for (int i = listaContas.size(); i < sizeLista; i++) {
                dadosSubrelatorioContas.add(new RelatorioContaQuadroPosicaoFinanceiraVO());
            }
        }
    }
    
    public Map<String, List<byte[]>> getMapAnexo() {
        return mapAnexo;
    }

    public void setMapAnexo(Map<String, List<byte[]>> mapAnexo) {
        this.mapAnexo = mapAnexo;
    }

}
