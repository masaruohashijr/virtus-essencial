package br.gov.bcb.sisaps.web.page.componentes.relatorios;

import java.io.IOException;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;

import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.vo.CicloVO;
import br.gov.bcb.sisaps.src.vo.ConsultaCicloVO;
import br.gov.bcb.sisaps.web.page.dominio.matriz.relatorio.ExportacaoXlsHelper;
import br.gov.bcb.sisaps.web.page.dominio.matriz.relatorio.ExportacaoXlsHelperUtil;
import br.gov.bcb.wicket.pages.AbstractBacenWebPage;

public class ExportacaoComitesXls extends ExportacaoXlsHelper {

    private static final int LINHA_CABECALHO_LISTAGEM = 7;
    private static final int LINHA_INICIAL_LISTAGEM = 8;
    private static final int COLUNA_INICIO_CICLO = 0;
    private static final int COLUNA_COREC = 1;
    private static final int COLUNA_ES = 2;
    private static final int COLUNA_EQUIPE = 3;
    private static final int COLUNA_SUPERVISOR = 4;
    private static final int COLUNA_PRIORIDADE = 5;

    private static final int QTD_COLUNAS = 6;
    private List<CicloVO> comites;
    private final boolean isRelatorioComitesRealizados;
    private CellStyle estiloCentralizado;
    private CellStyle estiloAEsquerda;

    public ExportacaoComitesXls(boolean isRelatorioComitesRealizados, ConsultaCicloVO consulta) {
        super();
        this.isRelatorioComitesRealizados = isRelatorioComitesRealizados;
        if (isRelatorioComitesRealizados) {
            comites = CicloMediator.get().listarComitesRealizados(consulta);
        } else {
            comites = CicloMediator.get().listarComitesRealizar(consulta);
        }
    }

    public Workbook exportarParaXls(AbstractBacenWebPage paginaAtual) throws IOException {
        estiloCentralizado = getEstiloListagemCentralizado();
        estiloAEsquerda = getEstiloListagemAEsquerda();
        aba.setColumnWidth(COLUNA_INICIO_CICLO, 16 * 256);
        aba.setColumnWidth(COLUNA_COREC, 16 * 256);
        aba.setColumnWidth(COLUNA_ES, 30 * 256);
        aba.setColumnWidth(COLUNA_EQUIPE, 28 * 256);
        aba.setColumnWidth(COLUNA_SUPERVISOR, 30 * 256);
        aba.setColumnWidth(COLUNA_PRIORIDADE, 13 * 256);
        gerarCabecalhoRelatorio(paginaAtual);
        gerarListaComites();
        gerarRodape();
        return planilha;
    }

    private void gerarCabecalhoRelatorio(AbstractBacenWebPage paginaAtual) throws IOException {
        Row linhaBCB = criarLinha(LINHA_BANCO_CENTRAL_BRASIL);
        Row linhaBranco1 = criarLinha(LINHA_BRANCO_APOS_BCB);
        Row linhaDepartamento = criarLinha(LINHA_DEPARTAMENTO);
        Row linhaAssunto = criarLinha(LINHA_ASSUNTO);
        Row linhaComite = criarLinha(LINHA_NOME_ES);
        Row linhaBranco2 = criarLinha(LINHA_TITULO_MATRIZ);

        criarCelulas(linhaBCB, linhaBranco1, 0, QTD_COLUNAS - 1, "");

        criarCelulas(linhaDepartamento, 0, QTD_COLUNAS - 1, "Departamento de Supervisão Bancária",
                getEstiloDepartamento());
        criarCelulas(linhaAssunto, 0, QTD_COLUNAS - 1, "Assunto", getEstiloAssunto());
        criarCelulas(linhaComite, 0, QTD_COLUNAS - 1, isRelatorioComitesRealizados ? "Comitês realizados"
                : "Comitês a realizar", getEstiloTituloMatriz());
        criarCelulas(linhaBranco2, 0, QTD_COLUNAS - 1, "");

        inserirLogo(paginaAtual);
    }

    private void gerarListaComites() {
        gerarCabecalhoListagem();
        gerarValoresListagem();
    }

    private void gerarCabecalhoListagem() {
        Row linha = criarLinha(LINHA_CABECALHO_LISTAGEM);
        CellStyle estilo = getEstiloCabecalhoListagem();
        criarCelula(linha, COLUNA_INICIO_CICLO, "Início do ciclo", estilo);
        criarCelula(linha, COLUNA_COREC, "Corec", estilo);
        criarCelula(linha, COLUNA_ES, "ES", estilo);
        criarCelula(linha, COLUNA_EQUIPE, "Equipe", estilo);
        criarCelula(linha, COLUNA_SUPERVISOR, "Supervisor titular", estilo);
        criarCelula(linha, COLUNA_PRIORIDADE, "Prioridade", estilo);
    }

    private void gerarValoresListagem() {
        int linhaAtual = LINHA_INICIAL_LISTAGEM;
        for (CicloVO cicloVO : comites) {
            Row linha = criarLinha(linhaAtual);
            linhaAtual++;
            criarCelula(linha, COLUNA_INICIO_CICLO, cicloVO.getDataInicioFormatada(), estiloCentralizado);
            criarCelula(linha, COLUNA_COREC, cicloVO.getDataPrevisaoFormatada(), estiloCentralizado);
            criarCelula(linha, COLUNA_ES, cicloVO.getEntidadeSupervisionavel().getNome(), estiloAEsquerda);
            criarCelula(linha, COLUNA_EQUIPE, cicloVO.getEntidadeSupervisionavel().getLocalizacao(), estiloAEsquerda);
            criarCelula(linha, COLUNA_SUPERVISOR, cicloVO.getNomeSupervisor(), estiloAEsquerda);
            String prioridade = "";
            if (cicloVO != null && cicloVO.getEntidadeSupervisionavel() != null
                    && cicloVO.getEntidadeSupervisionavel().getPrioridade() != null
                    && cicloVO.getEntidadeSupervisionavel().getPrioridade().getDescricao() != null) {
                prioridade = cicloVO.getEntidadeSupervisionavel().getPrioridade().getDescricao();
            }
            criarCelula(linha, COLUNA_PRIORIDADE, prioridade, estiloCentralizado);
        }
    }

    private void gerarRodape() {
        Row linhaBranco = criarLinha(comites.size() + 8);
        criarCelulas(linhaBranco, 0, QTD_COLUNAS - 1, "", getEstiloTituloMatriz());
        Row linhaRodape = criarLinha(comites.size() + 9);
        CellStyle estiloSRC = getEstiloNomeES();
        estiloSRC.setAlignment(CellStyle.ALIGN_RIGHT);
        criarCelulas(linhaRodape, 0, QTD_COLUNAS - 1, "SRC - Sistema de Avaliações de Riscos e Controles", estiloSRC);
    }

    private CellStyle getEstiloCabecalhoListagem() {
        CellStyle estilo = ExportacaoXlsHelperUtil.get().criarEstilo((HSSFWorkbook) planilha);
        Font fonte = ExportacaoXlsHelperUtil.get().criarFonte((HSSFWorkbook) planilha);
        fonte.setBoldweight(NEGRITO);
        fonte.setFontHeightInPoints(TAMANHO_DA_FONTE_12);
        estilo.setFont(fonte);
        estilo.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
        estilo.setAlignment(CellStyle.ALIGN_CENTER);
        ExportacaoXlsHelperUtil.get().addBordasFinas(estilo);
        return estilo;
    }

    private CellStyle getEstiloListagemCentralizado() {
        CellStyle estilo = ExportacaoXlsHelperUtil.get().criarEstilo((HSSFWorkbook) planilha);
        estilo.setAlignment(CellStyle.ALIGN_CENTER);
        ExportacaoXlsHelperUtil.get().addBordasFinas(estilo);
        return estilo;
    }

    private CellStyle getEstiloListagemAEsquerda() {
        CellStyle estilo = ExportacaoXlsHelperUtil.get().criarEstilo((HSSFWorkbook) planilha);
        estilo.setAlignment(CellStyle.ALIGN_LEFT);
        ExportacaoXlsHelperUtil.get().addBordasFinas(estilo);
        return estilo;
    }

}
