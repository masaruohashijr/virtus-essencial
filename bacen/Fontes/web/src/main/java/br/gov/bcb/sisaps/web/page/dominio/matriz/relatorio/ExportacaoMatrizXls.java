package br.gov.bcb.sisaps.web.page.dominio.matriz.relatorio;

import java.io.IOException;
import java.math.BigDecimal;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.CelulaRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Metodologia;
import br.gov.bcb.sisaps.src.dominio.NotaMatriz;
import br.gov.bcb.sisaps.src.dominio.ParametroGrupoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.ParametroNota;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoLinhaMatrizVOEnum;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.MatrizCicloMediator;
import br.gov.bcb.sisaps.src.mediator.ParametroNotaMediator;
import br.gov.bcb.sisaps.src.vo.LinhaMatrizVO;
import br.gov.bcb.sisaps.src.vo.LinhaNotasMatrizVO;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.wicket.pages.AbstractBacenWebPage;

public class ExportacaoMatrizXls extends ExportacaoXlsHelper {

    private static final String A_AVALIAR = "*A";
    private static final int LINHA_GRUPOS = 11;
    private static final int LINHA_PERCENTUAIS_GRUPOS = 12;
    private static final int LINHA_RISCOS_CONTROLES = 13;

    private static final int PRIMEIRA_LINHA_ATIVIDADES = 14;
    private static final int PRIMEIRA_COLUNA_GRUPOS = 2;

    private final RelatorioMatriz relatorioMatriz;
    private int quantidadeColunasMaximaRelatorio;
    private int linhaBrancaAposNotaAjustada;
    private Row linhaGrupos;
    private Row linhaNotasResiduaisGrupos;

    public ExportacaoMatrizXls(RelatorioMatriz relatorioMatriz) {
        super();
        this.relatorioMatriz = relatorioMatriz;
        if (relatorioMatriz.getParametroGrupo().size() >= 4) {
            quantidadeColunasMaximaRelatorio = (relatorioMatriz.getParametroGrupo().size() * 2) + 1;
        } else {
            quantidadeColunasMaximaRelatorio = 9;
        }
    }

    public Workbook exportarParaXls(AbstractBacenWebPage paginaAtual) throws IOException {
        aba.setColumnWidth(COLUNA_ZERO, 28 * 256);
        aba.setColumnWidth(COLUNA_UM, 1750);
        gerarCabecalho(paginaAtual);
        if (relatorioMatriz.getMatriz() != null) {
            gerarNotaCalculadaMatriz();
            gerarMatriz();
            gerarArcExterno();
            gerarNotasMatriz();
        }
        gerarRodape();
        return planilha;
    }

    private void gerarNotaCalculadaMatriz() {
        Row linhaNotaCalculada = criarLinha(10);
        CellStyle estiloDescricao = getEstiloCabecalhosMatriz();
        estiloDescricao.setBorderBottom(CellStyle.BORDER_NONE);
        estiloDescricao.setAlignment(CellStyle.ALIGN_LEFT);
        criarCelula(linhaNotaCalculada, COLUNA_ZERO, "Nota calculada", estiloDescricao);
        criarCelula(linhaNotaCalculada, 1, relatorioMatriz.getNotaCalculada(), getEstiloPercentualAtividadesENotas());
        criarCelulas(linhaNotaCalculada, 3, quantidadeColunasMaximaRelatorio, "");
    }

    private void gerarArcExterno() {
        if (relatorioMatriz.getArcExterno() != null) {
            int linhaAtual = linhaNotasResiduaisGrupos.getRowNum() + 2;
            Row linhaBrancaAntes = criarLinha(linhaAtual);
            Row linhaTituloArcExterno = criarLinha(linhaAtual + 1);
            Row linhaBrancoTitulo = criarLinha(linhaAtual + 2);
            Row linhaNotaArcExterno = criarLinha(linhaAtual + 3);
            Row linhaBranco = criarLinha(linhaAtual + 4);
            Row linhaBranco2 = criarLinha(linhaAtual + 5);

            criarCelulas(linhaBrancaAntes, COLUNA_ZERO, quantidadeColunasMaximaRelatorio, "");

            criarCelulas(linhaTituloArcExterno, COLUNA_ZERO, quantidadeColunasMaximaRelatorio, relatorioMatriz
                    .getArcExterno()
                    .getParametroGrupoRiscoControle().getNomeAbreviado(), getEstiloTituloMatriz());
            criarCelulas(linhaBrancoTitulo, COLUNA_ZERO, quantidadeColunasMaximaRelatorio, "");

            CellStyle estiloDescricao = getEstiloCabecalhosMatriz();
            estiloDescricao.setBorderBottom(CellStyle.BORDER_NONE);
            estiloDescricao.setAlignment(CellStyle.ALIGN_LEFT);

            criarCelula(linhaNotaArcExterno, COLUNA_ZERO, "Nota", estiloDescricao);
            criarCelula(linhaNotaArcExterno, 1, relatorioMatriz.getArcExterno().getCiclo().getMatriz()
                    .getPercentualGovernancaCorporativa(), getEstiloPercentualAtividadesENotas());
            criarCelula(
                    linhaNotaArcExterno,
                    2,
                    obterNotaARC(relatorioMatriz.getArcExterno().getAvaliacaoRiscoControle(), relatorioMatriz
                            .getArcExterno().getCiclo()), getEstiloPercentualAtividadesENotas());
            criarCelulas(linhaNotaArcExterno, 3, quantidadeColunasMaximaRelatorio, "");

            CellStyle estilo = ExportacaoXlsHelperUtil.get().criarEstilo((HSSFWorkbook) planilha);
            estilo.setBorderTop(CellStyle.BORDER_THIN);
            criarCelulas(linhaBranco, COLUNA_ZERO, 2, "", estilo);
            criarCelulas(linhaBranco, 2, quantidadeColunasMaximaRelatorio, "");
            criarCelulas(linhaBranco2, COLUNA_ZERO, quantidadeColunasMaximaRelatorio, "");

        }
    }

    private void gerarCabecalho(AbstractBacenWebPage paginaAtual) throws IOException {
        Row linhaBCB = criarLinha(LINHA_BANCO_CENTRAL_BRASIL);
        Row linhaBranco1 = criarLinha(LINHA_BRANCO_APOS_BCB);
        Row linhaDepartamento = criarLinha(LINHA_DEPARTAMENTO);
        Row linhaAssunto = criarLinha(LINHA_ASSUNTO);
        Row linhaConglomerado = criarLinha(LINHA_NOME_ES);
        Row linhaMatrizRiscoControle = criarLinha(LINHA_TITULO_MATRIZ);
        Row linhaBranco2 = criarLinha(LINHA_BRANCO_APOS_TITULO_MATRIZ);

        criarCelulas(linhaBCB, linhaBranco1, 0, quantidadeColunasMaximaRelatorio, "");

        criarCelulas(linhaDepartamento, 0, quantidadeColunasMaximaRelatorio, "Departamento de Supervisão Bancária",
                getEstiloDepartamento());
        criarCelulas(linhaAssunto, 0, quantidadeColunasMaximaRelatorio, "Assunto", getEstiloAssunto());
        criarCelulas(linhaConglomerado, 0, quantidadeColunasMaximaRelatorio, relatorioMatriz
                .getEntidadeSupervisionavel().getNomeConglomeradoFormatado(), getEstiloNomeES());
        criarCelulas(linhaMatrizRiscoControle, 0, quantidadeColunasMaximaRelatorio, "Análise de riscos e controles",
                getEstiloTituloMatriz());
        criarCelulas(linhaBranco2, 0, quantidadeColunasMaximaRelatorio, "");

        inserirLogo(paginaAtual);
    }

    private void gerarMatriz() {
        Row linhaMatrizRiscoControle = criarLinha(8);
        Row linhaBranco2 = criarLinha(9);

        criarCelulas(linhaMatrizRiscoControle, 0, quantidadeColunasMaximaRelatorio, "Matriz de Riscos e Controles",
                getEstiloTituloMatriz());
        criarCelulas(linhaBranco2, 0, quantidadeColunasMaximaRelatorio, "");

        gerarColunasGruposRisco();
        gerarLinhasMatriz();
        gerarNotasResiduais();

        Row linhaBranco = criarLinha(linhaNotasResiduaisGrupos.getRowNum() + 1);
        CellStyle estilo = ExportacaoXlsHelperUtil.get().criarEstilo((HSSFWorkbook) planilha);
        estilo.setBorderTop(CellStyle.BORDER_THIN);
        int ultimaColunaMatriz = (relatorioMatriz.getParametroGrupo().size() * 2) + 1;
        criarCelulas(linhaBranco, 0, ultimaColunaMatriz, "", estilo);

        if (relatorioMatriz.getParametroGrupo().size() < 4) {
            criarCelulas(linhaGrupos, linhaBranco, ultimaColunaMatriz + 1, quantidadeColunasMaximaRelatorio, "");
        }

        Row linhaBranco3 = criarLinha(linhaNotasResiduaisGrupos.getRowNum() + 2);
        criarCelulas(linhaBranco3, 0, quantidadeColunasMaximaRelatorio, "");
    }

    private void gerarColunasGruposRisco() {
        linhaGrupos = criarLinha(LINHA_GRUPOS);
        Row linhaPercentuaisGrupos = criarLinha(LINHA_PERCENTUAIS_GRUPOS);
        Row linhaRiscosControles = criarLinha(LINHA_RISCOS_CONTROLES);

        CellStyle estilo = getEstiloCabecalhosMatriz();

        criarCelulasEmBrancoCabecalho(linhaPercentuaisGrupos, linhaRiscosControles);

        int colunaAtual = PRIMEIRA_COLUNA_GRUPOS;
        for (ParametroGrupoRiscoControle grupo : relatorioMatriz.getParametroGrupo()) {
            criarCelulas(linhaGrupos, colunaAtual, colunaAtual + 1, grupo.getNomeAbreviado(), estilo);

            String percentualDoGrupo =
                    MatrizCicloMediator.get().percentualDoGrupo(relatorioMatriz.getMatriz(),
                            relatorioMatriz.getCelulasPorGrupo(grupo), relatorioMatriz.getVersoesPerfilRiscoARCs());
            criarCelulas(linhaPercentuaisGrupos, colunaAtual, colunaAtual + 1, percentualDoGrupo, estilo);

            criarCelula(linhaRiscosControles, colunaAtual, "R", estilo);
            criarCelula(linhaRiscosControles, colunaAtual + 1, "C", estilo);

            colunaAtual = colunaAtual + 2;
        }
    }

    private void criarCelulasEmBrancoCabecalho(Row linhaPercentuaisGrupos, Row linhaRiscosControles) {
        CellStyle estilo = ExportacaoXlsHelperUtil.get().criarEstilo((HSSFWorkbook) planilha);
        estilo.setBorderTop(CellStyle.BORDER_THIN);
        estilo.setBorderLeft(CellStyle.BORDER_THIN);
        criarCelula(linhaGrupos, 0, "", estilo);
        estilo = ExportacaoXlsHelperUtil.get().criarEstilo((HSSFWorkbook) planilha);
        estilo.setBorderTop(CellStyle.BORDER_THIN);
        estilo.setBorderRight(CellStyle.BORDER_THIN);
        criarCelula(linhaGrupos, 1, "", estilo);

        estilo = ExportacaoXlsHelperUtil.get().criarEstilo((HSSFWorkbook) planilha);
        estilo.setBorderLeft(CellStyle.BORDER_THIN);
        criarCelula(linhaPercentuaisGrupos, 0, "", estilo);
        estilo = ExportacaoXlsHelperUtil.get().criarEstilo((HSSFWorkbook) planilha);
        estilo.setBorderRight(CellStyle.BORDER_THIN);
        criarCelula(linhaPercentuaisGrupos, 1, "", estilo);

        estilo = ExportacaoXlsHelperUtil.get().criarEstilo((HSSFWorkbook) planilha);
        estilo.setBorderLeft(CellStyle.BORDER_THIN);
        estilo.setBorderBottom(CellStyle.BORDER_THIN);
        criarCelula(linhaRiscosControles, 0, "", estilo);
        estilo = ExportacaoXlsHelperUtil.get().criarEstilo((HSSFWorkbook) planilha);
        estilo.setBorderRight(CellStyle.BORDER_THIN);
        estilo.setBorderBottom(CellStyle.BORDER_THIN);
        criarCelula(linhaRiscosControles, 1, "", estilo);

        aba.addMergedRegion(new CellRangeAddress(linhaGrupos.getRowNum(), linhaRiscosControles.getRowNum(), 0, 1));
    }

    private void gerarLinhasMatriz() {
        int linhaAtual = PRIMEIRA_LINHA_ATIVIDADES;
        for (LinhaMatrizVO linhaMatrizVO : relatorioMatriz.getLinhaMatrizVO()) {
            Row linhaAtividade = criarLinha(linhaAtual);

            CellStyle estiloNome = null;
            CellStyle estiloPercentual = null;
            if (linhaMatrizVO.isFilho()) {
                estiloNome = getEstiloAtividades();
                estiloPercentual = getEstiloPercentualAtividadesENotas();
            } else {
                estiloNome = getEstiloUnidadeEAtividadeSemUnidade();
                estiloPercentual = getEstiloCabecalhosMatriz();
            }

            criarCelula(linhaAtividade, COLUNA_ZERO, linhaMatrizVO.getNomeFormatadoVigenteRelatorio(),
                    estiloNome);

            String percentualDaLinha =
                    MatrizCicloMediator.get().percentualDaLinha(relatorioMatriz.getMatriz(), linhaMatrizVO);
            criarCelula(linhaAtividade, COLUNA_UM, percentualDaLinha, estiloPercentual);

            addDadosGruposDaLinha(linhaAtividade, linhaMatrizVO);

            linhaAtual++;
        }
    }

    private void addDadosGruposDaLinha(Row linhaAtividade, LinhaMatrizVO linhaMatrizVO) {
        int colunaAtual = PRIMEIRA_COLUNA_GRUPOS;
        CellStyle estilo = getEstiloPercentualAtividadesENotas();
        for (ParametroGrupoRiscoControle grupo : relatorioMatriz.getParametroGrupo()) {
            CelulaRiscoControle celula =
                    TipoLinhaMatrizVOEnum.UNIDADE.equals(linhaMatrizVO.getTipo()) ? null : relatorioMatriz
                            .getCelulaPelaAtividadeEGrupo(grupo, linhaMatrizVO.getPk());

            String notaRisco =
                    obterNotaARC(celula == null ? null : celula.getArcRisco(), celula == null ? null : celula
                            .getAtividade().getMatriz().getCiclo());
            criarCelula(linhaAtividade, colunaAtual, notaRisco, estilo);
            colunaAtual++;

            notaRisco =
                    obterNotaARC(celula == null ? null : celula.getArcControle(), celula == null ? null : celula
                            .getAtividade().getMatriz().getCiclo());
            criarCelula(linhaAtividade, colunaAtual, notaRisco, estilo);
            colunaAtual++;
        }
    }

    private String obterNotaARC(AvaliacaoRiscoControle arc, Ciclo ciclo) {
        return arc == null ? "" : AvaliacaoRiscoControleMediator.get().notaArc(arc, ciclo,
                relatorioMatriz.getPerfilMenu(), relatorioMatriz.getPerfilRisco());
    }

    private void gerarNotasResiduais() {
        int linhaAtual = relatorioMatriz.getLinhaMatrizVO().size() + PRIMEIRA_LINHA_ATIVIDADES;
        Row linhaNotasResiduaisGruposRiscoControle = criarLinha(linhaAtual);
        linhaNotasResiduaisGrupos = criarLinha(linhaAtual + 1);

        criarCelulasNotasResiduais(linhaNotasResiduaisGruposRiscoControle);
        /*
         * criarCelulas(linhaNotasResiduaisGruposRiscoControle, linhaNotasResiduaisGrupos, 0, 1,
         * "Notas residuais", getEstiloUnidadeEAtividadeSemUnidade());
         */

        CellStyle estilo = getEstiloPercentualAtividadesENotas();
        addNotasResiduaisGruposRiscosControles(linhaNotasResiduaisGruposRiscoControle, estilo);
        addNotasResiduaisGrupos(linhaNotasResiduaisGrupos, estilo);
    }

    private void criarCelulasNotasResiduais(Row linhaNotasResiduaisGruposRiscoControle) {
        CellStyle estilo = getEstiloUnidadeEAtividadeSemUnidade();
        estilo.setBorderTop(CellStyle.BORDER_THIN);
        estilo.setBorderLeft(CellStyle.BORDER_THIN);
        criarCelula(linhaNotasResiduaisGruposRiscoControle, 0, "Notas residuais", estilo);
        estilo = ExportacaoXlsHelperUtil.get().criarEstilo((HSSFWorkbook) planilha);
        estilo.setBorderTop(CellStyle.BORDER_THIN);
        estilo.setBorderRight(CellStyle.BORDER_THIN);
        criarCelula(linhaNotasResiduaisGruposRiscoControle, 1, "", estilo);

        estilo = ExportacaoXlsHelperUtil.get().criarEstilo((HSSFWorkbook) planilha);
        estilo.setBorderLeft(CellStyle.BORDER_THIN);
        estilo.setBorderBottom(CellStyle.BORDER_THIN);
        criarCelula(linhaNotasResiduaisGrupos, 0, "", estilo);
        estilo = ExportacaoXlsHelperUtil.get().criarEstilo((HSSFWorkbook) planilha);
        estilo.setBorderRight(CellStyle.BORDER_THIN);
        estilo.setBorderBottom(CellStyle.BORDER_THIN);
        criarCelula(linhaNotasResiduaisGrupos, 1, "", estilo);

        aba.addMergedRegion(new CellRangeAddress(linhaNotasResiduaisGruposRiscoControle.getRowNum(),
                linhaNotasResiduaisGrupos.getRowNum(), 0, 1));
    }

    private void addNotasResiduaisGruposRiscosControles(Row linhaNotasResiduaisGruposRiscoControle, CellStyle estilo) {
        int colunaAtual = PRIMEIRA_COLUNA_GRUPOS;
        for (LinhaNotasMatrizVO linhaNotasMatrizVO : relatorioMatriz.getNotaResiduais()) {
            criarCelula(linhaNotasResiduaisGruposRiscoControle, colunaAtual, linhaNotasMatrizVO.getNota(), estilo);
            colunaAtual++;
        }
    }

    private void addNotasResiduaisGrupos(Row linhaNotasResiduaisGrupos, CellStyle estilo) {
        int colunaAtual = PRIMEIRA_COLUNA_GRUPOS;
        for (LinhaNotasMatrizVO linhaNotasMatrizVO : relatorioMatriz.getMediaResiduais()) {
            criarCelulas(linhaNotasResiduaisGrupos, colunaAtual, colunaAtual + 1, linhaNotasMatrizVO.getNota(), estilo);
            colunaAtual = colunaAtual + 2;
        }
    }

    private void gerarNotasMatriz() {
        int linhaAtual;
        if (relatorioMatriz.getArcExterno() == null) {
            linhaAtual = linhaNotasResiduaisGrupos.getRowNum() + 3;
        } else {
            linhaAtual = linhaNotasResiduaisGrupos.getRowNum() + 8;
        }

        Row linhaNotaCalculada = criarLinha(linhaAtual);
        Row linhaNotaRefinada = criarLinha(linhaAtual + 1);
        Row linhaNotaAjustada = criarLinha(linhaAtual + 2);
        Row linhaBranco = criarLinha(linhaAtual + 3);
        linhaBrancaAposNotaAjustada = linhaBranco.getRowNum();
        criarCelulas(linhaBranco, 0, quantidadeColunasMaximaRelatorio, "");

        CellStyle estiloDescricao = getEstiloDepartamento();
        estiloDescricao.setBorderBottom(CellStyle.BORDER_NONE);
        CellStyle estiloNota = getEstiloNomeES();
        estiloNota.setAlignment(CellStyle.ALIGN_RIGHT);

        addNotaCalculada(linhaNotaCalculada, estiloDescricao, estiloNota);

        addNotaRefinada(linhaNotaRefinada, estiloDescricao, estiloNota);

        addNotaAjustadaEJustificativa(linhaAtual, linhaNotaAjustada, estiloDescricao, estiloNota);
    }

    private void addNotaCalculada(Row linhaNotaCalculada, CellStyle estiloDescricao, CellStyle estiloNota) {
        criarCelula(linhaNotaCalculada, COLUNA_ZERO, "Nota final calculada:", estiloDescricao);
        criarCelula(linhaNotaCalculada, COLUNA_UM,
                A_AVALIAR.equals(relatorioMatriz.getNotaCalculadaFinal()) ? relatorioMatriz.getNotaCalculadaFinal()
                        : relatorioMatriz.getNotaCalculadaFinal().replace('.', ','), estiloNota);
        criarCelulas(linhaNotaCalculada, PRIMEIRA_COLUNA_GRUPOS, quantidadeColunasMaximaRelatorio, "");
    }

    private void addNotaRefinada(Row linhaNotaRefinada, CellStyle estiloDescricao, CellStyle estiloNota) {
        String notaRefinada = null;
        if (A_AVALIAR.equals(relatorioMatriz.getNotaCalculadaFinal())) {
            notaRefinada = "";
        } else {
            Metodologia metodologia = relatorioMatriz.getPerfilRisco().getCiclo().getMetodologia();
            ParametroNota parametroNota = ParametroNotaMediator.get().buscarPorMetodologiaENota(metodologia,
                            new BigDecimal(relatorioMatriz.getNotaCalculadaFinal()), false);
            notaRefinada = metodologia.getIsMetodologiaNova() ? parametroNota
                    .getDescricao() : parametroNota.getDescricaoValor();
        }
        criarCelula(linhaNotaRefinada, COLUNA_ZERO, "Nota final refinada:", estiloDescricao);
        criarCelula(linhaNotaRefinada, COLUNA_UM, notaRefinada, estiloNota);
        criarCelulas(linhaNotaRefinada, PRIMEIRA_COLUNA_GRUPOS, quantidadeColunasMaximaRelatorio, "");
    }

    private void addNotaAjustadaEJustificativa(int linhaAtual, Row linhaNotaAjustada, CellStyle estiloDescricao,
            CellStyle estiloNota) {
        String notaAjustadaMatriz = "";
        boolean possuiNotaAjustada = false;
        Metodologia metodologia = relatorioMatriz.getPerfilRisco().getCiclo().getMetodologia();
        if (relatorioMatriz.getNotaAjusteCorec() != null) {
            notaAjustadaMatriz =
                    !metodologia.getIsMetodologiaNova() ? relatorioMatriz
                            .getNotaAjusteCorec().getDescricaoValor() : relatorioMatriz.getNotaAjusteCorec()
                            .getDescricao();
            possuiNotaAjustada = true;
        } else if (relatorioMatriz.getNotaMatriz() != null 
                && relatorioMatriz.getNotaMatriz().getNotaFinalMatriz() != null) {
            possuiNotaAjustada = true;
            NotaMatriz notaMatriz = relatorioMatriz.getNotaMatriz();
            notaAjustadaMatriz =
                    notaMatriz.getNotaFinalMatriz() == null ? "" : metodologia.getIsCalculoMedia() == null
                            || metodologia.getIsCalculoMedia().equals(SimNaoEnum.NAO) ? notaMatriz.getNotaFinalMatriz()
                            .getDescricaoValor() : notaMatriz.getNotaFinalMatriz().getDescricao();

            Row linhaJustificativaNotaAjustada = criarLinha(linhaAtual + 4);
            Row linhaJustificativa = criarLinha(linhaAtual + 5);
            criarCelula(linhaJustificativaNotaAjustada, COLUNA_ZERO, "Justificativa do ajuste:",
                    estiloDescricao);
            String justificativaNotaAjustada =
                    SisapsUtil.extrairTextoCKEditorSemEspacosEmBranco(notaMatriz.getJustificativaNota());
            criarCelulas(linhaJustificativa, COLUNA_ZERO, 9, justificativaNotaAjustada,
                    getEstiloJustificativaNotaAjustada());
            if (relatorioMatriz.getParametroGrupo().size() >= 4) {
                criarCelulas(linhaJustificativaNotaAjustada, COLUNA_UM,
                        quantidadeColunasMaximaRelatorio, "");
                criarCelulas(linhaJustificativa, 10, quantidadeColunasMaximaRelatorio, "");
            } else {
                criarCelulas(linhaJustificativaNotaAjustada, COLUNA_UM, 9, "");
            }

            int quantidadeDeLinhasJustificativa = (justificativaNotaAjustada.length() / 150);
            quantidadeDeLinhasJustificativa =
                    quantidadeDeLinhasJustificativa == 0 ? 1 : quantidadeDeLinhasJustificativa;
            linhaJustificativa.setHeightInPoints(quantidadeDeLinhasJustificativa * 17);
        }
        criarCelula(linhaNotaAjustada, COLUNA_ZERO, possuiNotaAjustada ? "Nota final ajustada:" : "",
                estiloDescricao);
        criarCelula(linhaNotaAjustada, COLUNA_UM, notaAjustadaMatriz, estiloNota);
        criarCelulas(linhaNotaAjustada, PRIMEIRA_COLUNA_GRUPOS, quantidadeColunasMaximaRelatorio,
                relatorioMatriz.getNotaAjusteCorec() == null ? "" : "(Corec)");
    }

    private void gerarRodape() {
        int linhaAtual;
        if (relatorioMatriz.getMatriz() == null) {
            linhaAtual = LINHA_GRUPOS;
        } else {
            if (relatorioMatriz.getNotaMatriz() == null 
                    || relatorioMatriz.getNotaMatriz().getNotaFinalMatriz() == null) {
                linhaAtual = linhaBrancaAposNotaAjustada + 1;
            } else {
                linhaAtual = linhaBrancaAposNotaAjustada + 3;
            }
        }
        Row linhaBranco = criarLinha(linhaAtual);
        criarCelulas(linhaBranco, 0, quantidadeColunasMaximaRelatorio, "", getEstiloTituloMatriz());
        Row linhaRodape = criarLinha(linhaAtual + 1);
        criarCelulas(linhaRodape, 0, 3, getTextoRodape(), getEstiloNomeES());
        CellStyle estiloSRC = getEstiloNomeES();
        estiloSRC.setAlignment(CellStyle.ALIGN_RIGHT);
        criarCelulas(linhaRodape, 4, quantidadeColunasMaximaRelatorio,
                "SRC - Sistema de Avaliações de Riscos e Controles", estiloSRC);
    }

    private String getTextoRodape() {
        StringBuffer rodape = new StringBuffer();
        rodape.append("Versão ");
        rodape.append(relatorioMatriz.getPerfilRisco().getDataCriacaoFormatadaSemHora());
        rodape.append(" - Ciclo ");
        rodape.append(relatorioMatriz.getPerfilRisco().getCiclo().getDataInicioFormatada());
        rodape.append(" ~ ");
        rodape.append(relatorioMatriz.getPerfilRisco().getCiclo().getDataPrevisaoFormatada());
        return rodape.toString();
    }

    private CellStyle getEstiloCabecalhosMatriz() {
        CellStyle estilo = ExportacaoXlsHelperUtil.get().criarEstilo((HSSFWorkbook) planilha);
        Font fonte = ExportacaoXlsHelperUtil.get().criarFonte((HSSFWorkbook) planilha);
        fonte.setBoldweight(NEGRITO);
        estilo.setFont(fonte);
        estilo.setAlignment(CellStyle.ALIGN_CENTER);
        ExportacaoXlsHelperUtil.get().addBordasFinas(estilo);
        return estilo;
    }

    private CellStyle getEstiloUnidadeEAtividadeSemUnidade() {
        CellStyle estilo = ExportacaoXlsHelperUtil.get().criarEstilo((HSSFWorkbook) planilha);
        Font fonte = ExportacaoXlsHelperUtil.get().criarFonte((HSSFWorkbook) planilha);
        fonte.setBoldweight(NEGRITO);
        estilo.setFont(fonte);
        ExportacaoXlsHelperUtil.get().addBordasFinas(estilo);
        return estilo;
    }

    private CellStyle getEstiloAtividades() {
        CellStyle estilo = ExportacaoXlsHelperUtil.get().criarEstilo((HSSFWorkbook) planilha);
        ExportacaoXlsHelperUtil.get().addBordasFinas(estilo);
        return estilo;
    }

    private CellStyle getEstiloPercentualAtividadesENotas() {
        CellStyle estilo = ExportacaoXlsHelperUtil.get().criarEstilo((HSSFWorkbook) planilha);
        estilo.setAlignment(CellStyle.ALIGN_CENTER);
        ExportacaoXlsHelperUtil.get().addBordasFinas(estilo);
        return estilo;
    }

    private CellStyle getEstiloJustificativaNotaAjustada() {
        CellStyle estilo = ExportacaoXlsHelperUtil.get().criarEstilo((HSSFWorkbook) planilha);
        estilo.setAlignment(CellStyle.ALIGN_JUSTIFY);
        return estilo;
    }

}
