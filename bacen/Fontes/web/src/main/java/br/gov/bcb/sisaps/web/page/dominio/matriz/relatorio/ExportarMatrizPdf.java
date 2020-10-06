package br.gov.bcb.sisaps.web.page.dominio.matriz.relatorio;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.List;

import org.apache.poi.util.IOUtils;
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.html.simpleparser.StyleSheet;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.CelulaRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Metodologia;
import br.gov.bcb.sisaps.src.dominio.ParametroGrupoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.ParametroNota;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.SecaoApresentacaoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoLinhaMatrizVOEnum;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.MatrizCicloMediator;
import br.gov.bcb.sisaps.src.mediator.ParametroNotaMediator;
import br.gov.bcb.sisaps.src.vo.ApresentacaoVO;
import br.gov.bcb.sisaps.src.vo.LinhaMatrizVO;
import br.gov.bcb.sisaps.src.vo.LinhaNotasMatrizVO;
import br.gov.bcb.sisaps.util.Util;

public class ExportarMatrizPdf extends PdfPageEventHelper {
    private static final String VAZIO = "   ";
    private static final String A_AVALIAR = "*A";
    private final RelatorioMatriz relatorioMatriz;
    private int quantidadeColunasMaximaRelatorio;
    private PdfPTable table;
    private final Page paginaAtual;
    private float[] columnWidth;
    private ApresentacaoVO apresentacaoVO;
    private SecaoApresentacaoEnum secao;
    private int indice;
    private int qtdPaginas;
    private int qtdPaginasTotalDossie;
    private int qtdPaginasAtualDossie;
    private boolean isDossie;

    public ExportarMatrizPdf(RelatorioMatriz relatorioMatriz, Page paginaAtual) throws DocumentException {
        this(relatorioMatriz, paginaAtual, 0, 0, false);
    }

    public ExportarMatrizPdf(RelatorioMatriz relatorioMatriz, Page paginaAtual, int qtdPaginasAtualDossie,
            int qtdPaginasTotalDossie, boolean isDossie)
            throws DocumentException {
        this.relatorioMatriz = relatorioMatriz;
        this.paginaAtual = paginaAtual;
        this.qtdPaginasAtualDossie = qtdPaginasAtualDossie;
        this.qtdPaginasTotalDossie = qtdPaginasTotalDossie;
        this.isDossie = isDossie;
    }

    public ExportarMatrizPdf(RelatorioMatriz relatorioMatriz, Page paginaAtual, ApresentacaoVO apresentacaoVO,
            SecaoApresentacaoEnum secao, int indice, int qtdPaginas) throws DocumentException {
        this.relatorioMatriz = relatorioMatriz;
        this.paginaAtual = paginaAtual;
        this.secao = secao;
        this.apresentacaoVO = apresentacaoVO;
        this.indice = indice;
        this.qtdPaginas = qtdPaginas;

    }

    private byte[] inserirLogo(Page paginaAtual) throws IOException {
        WebApplication wa = (WebApplication) paginaAtual.getApplication();
        InputStream imgLogo = wa.getServletContext().getResourceAsStream("/relatorios/logoBacenRelatorioMatriz2.png");
        return IOUtils.toByteArray(imgLogo);
    }

    private byte[] inserirLogoApresentacao(Page paginaAtual) throws IOException {
        WebApplication wa = (WebApplication) paginaAtual.getApplication();
        InputStream imgLogo = wa.getServletContext().getResourceAsStream("/relatorios/apresentacao/logobc.gif");
        return IOUtils.toByteArray(imgLogo);
    }

    public PdfPTable exportarCabecalho() throws IOException, DocumentException {
        tabelaPagina();
        if (apresentacaoVO == null) {
            addCabecalho();
        } else {
            addCabecalhoApresentacao();
        }
        return table;
    }

    private void tabelaPagina() throws DocumentException {
        if (apresentacaoVO == null) {
            quantidadeColunasMaximaRelatorio = 1;
        } else {
            quantidadeColunasMaximaRelatorio = 2;
        }

        table = new PdfPTable(quantidadeColunasMaximaRelatorio);
        table.setWidthPercentage(apresentacaoVO == null ? new float[] {600} : new float[] {250, 350},
                PageSize.A4_LANDSCAPE);
        table.setHorizontalAlignment(Element.ALIGN_JUSTIFIED_ALL);
    }

    public PdfPTable exportarTabelaMatriz() throws IOException, DocumentException {
        montarTabelaMatriz();

        montarMatriz();

        return table;
    }

    private void montarTabelaMatriz() throws IOException, DocumentException {
        quantidadeColunasMaximaRelatorio = (relatorioMatriz.getParametroGrupo().size() * 2) + 2;
        table = new PdfPTable(quantidadeColunasMaximaRelatorio);
        table.setWidthPercentage(90);
        columnWidth = new float[quantidadeColunasMaximaRelatorio];
        montarColunas();
        table.setWidthPercentage(columnWidth, PageSize.A4_LANDSCAPE);
        table.setHorizontalAlignment(Element.ALIGN_LEFT);
    }

    public PdfPTable exportarTabelaNotas() throws IOException, DocumentException {
        quantidadeColunasMaximaRelatorio = 10;
        table = new PdfPTable(quantidadeColunasMaximaRelatorio);
        table.setWidthPercentage(90);
        columnWidth = new float[quantidadeColunasMaximaRelatorio];
        montarColunasNotas();
        table.setWidthPercentage(columnWidth, PageSize.A4_LANDSCAPE);
        table.setHorizontalAlignment(Element.ALIGN_LEFT);
        addLinhaNotaFinalCalculada();
        addLinhaNotaFinalRefinada();
        addLinhaNotaFinalAjustada();
        return table;
    }

    private void montarColunasNotas() {
        this.columnWidth[0] = 100;
        this.columnWidth[1] = 40;
        adicionar(40);
    }

    private void montarColunas() {
        this.columnWidth[0] = 100;
        this.columnWidth[1] = 30;
        adicionar(25);
    }

    public void adicionar(int valor) {
        for (int i = 2; i < columnWidth.length; i++) {
            this.columnWidth[i] = valor;
        }
    }

    public PdfPTable exportarRodape() throws IOException, DocumentException {
        quantidadeColunasMaximaRelatorio = 2;
        table = new PdfPTable(quantidadeColunasMaximaRelatorio);
        table.setWidthPercentage(new float[] {300, 300}, PageSize.A4_LANDSCAPE);
        table.setHorizontalAlignment(Element.ALIGN_JUSTIFIED_ALL);
        gerarRodape();
        return table;
    }

    private void addLinhaVazia() {
        PdfPCell linhaBranco = new PdfPCell(new Phrase(VAZIO));
        linhaBranco.setColspan(quantidadeColunasMaximaRelatorio);
        linhaBranco.setBorderColor(BaseColor.WHITE);
        linhaBranco.setBorder(0);
        table.addCell(linhaBranco);
    }

    private void addLinhaVazia2() {
        PdfPCell linhaBranco = new PdfPCell(new Phrase(""));
        linhaBranco.setColspan(quantidadeColunasMaximaRelatorio);
        linhaBranco.setBorder(2);
        linhaBranco.setBorderColorTop(BaseColor.BLACK);
        table.addCell(linhaBranco);
    }

    private void addCabecalhoApresentacao() throws BadElementException, IOException {
        addLinhaImagemApresentacao();
        addLinhaVazia2();
        addLinhaVazia();

    }

    private void addLinhaImagemApresentacao() throws BadElementException, IOException {
        Image imagem = Image.getInstance(inserirLogoApresentacao(paginaAtual));
        imagem.scaleToFit(160, 32);
        PdfPCell linhaLogo = new PdfPCell(imagem);
        linhaLogo.setBorderColor(BaseColor.WHITE);
        linhaLogo.setBorder(0);
        linhaLogo.setHorizontalAlignment(Element.ALIGN_LEFT);
        linhaLogo.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(linhaLogo);

        PdfPCell linhaTitulo =
                new PdfPCell(new Phrase(secao.getDescricao(), new Font(FontFamily.TIMES_ROMAN, 18, Font.BOLDITALIC)));
        linhaTitulo.setBorder(0);
        linhaTitulo.setBorderColor(BaseColor.WHITE);
        linhaTitulo.setHorizontalAlignment(Element.ALIGN_RIGHT);
        linhaTitulo.setVerticalAlignment(Element.ALIGN_BOTTOM);
        table.addCell(linhaTitulo);

    }

    private void addCabecalho() throws BadElementException, IOException {
        addLinhaSigilo(false);
        addLinhaImagem();
        addLinhaTitulo();
        addLinhaAssunto();
        addLinhaConglomerado();
        addLinhaAnaliseRiscoEControle();
        addLinhaVazia2();
        addLinhaVazia();
        addLinhaMatrizRiscoControle();
        addLinhaVazia2();
    }

    private void addLinhaSigilo(boolean isPaginacao) {
        String paginacao = isPaginacao ? qtdPaginasAtualDossie + " / " + qtdPaginasTotalDossie : "";
        PdfPCell linhaTitulo = new PdfPCell(new Phrase(
                "Informação protegida por sigilo legal, nos termos da Lei Complementar nº 105, de 10 de janeiro de 2001, e/ou de acesso restrito, nos termos do art. 5º do Decreto nº 7.724, de 16 de maio de 2012."
                        + "            " + "          " + "          " + "            " + "         " + paginacao,
                new Font(FontFamily.TIMES_ROMAN, 8, Font.NORMAL)));
        linhaTitulo.setColspan(quantidadeColunasMaximaRelatorio);
        linhaTitulo.setBorder(0);
        table.addCell(linhaTitulo);
    }

    private void addLinhaAnaliseRiscoEControle() {
        PdfPCell linhaTitulo =
                new PdfPCell(new Phrase("Análise de riscos e controles", new Font(FontFamily.TIMES_ROMAN, 12,
                        Font.NORMAL)));
        linhaTitulo.setColspan(quantidadeColunasMaximaRelatorio);
        linhaTitulo.setBorderColor(BaseColor.WHITE);
        linhaTitulo.setBorder(1);
        linhaTitulo.setBorderColorBottom(BaseColor.BLACK);
        table.addCell(linhaTitulo);
    }

    private void addLinhaMatrizRiscoControle() {
        PdfPCell linhaTitulo =
                new PdfPCell(new Phrase("Matriz de Riscos e Controles", new Font(FontFamily.TIMES_ROMAN, 12,
                        Font.NORMAL)));
        linhaTitulo.setColspan(quantidadeColunasMaximaRelatorio);
        linhaTitulo.setBorderColor(BaseColor.WHITE);
        linhaTitulo.setBorder(1);
        linhaTitulo.setBorderColorBottom(BaseColor.BLACK);
        table.addCell(linhaTitulo);
    }

    private void addLinhaTituloArcExterno() {
        PdfPCell linhaTitulo =
                new PdfPCell(new Phrase(relatorioMatriz.getArcExterno().getParametroGrupoRiscoControle()
                        .getNomeAbreviado(), new Font(FontFamily.TIMES_ROMAN, 12, Font.NORMAL)));
        linhaTitulo.setColspan(quantidadeColunasMaximaRelatorio);
        linhaTitulo.setBorderColor(BaseColor.WHITE);
        linhaTitulo.setBorder(1);
        linhaTitulo.setBorderColorBottom(BaseColor.BLACK);
        table.addCell(linhaTitulo);
        addLinhaVazia2();
        addLinhaVazia();
    }

    private void addLinhaConglomerado() {
        PdfPCell linhaTitulo =
                new PdfPCell(new Phrase(relatorioMatriz.getEntidadeSupervisionavel().getNomeConglomeradoFormatado(),
                        new Font(FontFamily.TIMES_ROMAN, 12, Font.NORMAL)));
        linhaTitulo.setColspan(quantidadeColunasMaximaRelatorio);
        linhaTitulo.setBorderColor(BaseColor.WHITE);
        linhaTitulo.setBorder(0);
        table.addCell(linhaTitulo);

    }

    private void addLinhaTitulo() {

        PdfPCell linhaTitulo =
                new PdfPCell(new Phrase("Departamento de Supervisão Bancária", new Font(FontFamily.TIMES_ROMAN, 12,
                        Font.BOLD)));
        linhaTitulo.setColspan(quantidadeColunasMaximaRelatorio);
        linhaTitulo.setBorder(0);
        table.addCell(linhaTitulo);
    }

    private void addLinhaImagem() throws BadElementException, IOException {
        PdfPCell linhaLogo = new PdfPCell(Image.getInstance(inserirLogo(paginaAtual)));
        linhaLogo.setColspan(quantidadeColunasMaximaRelatorio);
        linhaLogo.setBorderColor(BaseColor.WHITE);
        linhaLogo.setBorder(0);
        table.addCell(linhaLogo);
    }

    private void addLinhaAssunto() {
        PdfPCell linhaAssunto = new PdfPCell(new Phrase("Assunto", new Font(FontFamily.TIMES_ROMAN, 8, Font.NORMAL)));
        linhaAssunto.setColspan(quantidadeColunasMaximaRelatorio);
        linhaAssunto.setBorder(1);
        linhaAssunto.setBorderColorTop(BaseColor.BLACK);
        table.addCell(linhaAssunto);
    }

    private void montarMatriz() {
        addLinhaVazia();
        addNotaCalculadaMatriz();
        montarCabecalhoMatriz();
        addLinhaResidual();
        addLinhaVazia();
        addLinhaVazia();

    }

    private void addNotaCalculadaMatriz() {

        PdfPCell vazioResidual =
                new PdfPCell(new Phrase("Nota calculada", new Font(FontFamily.TIMES_ROMAN, 12, Font.NORMAL)));
        vazioResidual.setVerticalAlignment(Element.ALIGN_MIDDLE);
        vazioResidual.setColspan(2);
        table.addCell(vazioResidual);

        PdfPCell cellNotaRisco =
                new PdfPCell(new Phrase(relatorioMatriz.getNotaCalculada(), new Font(FontFamily.TIMES_ROMAN, 11,
                        Font.NORMAL)));
        cellNotaRisco.setHorizontalAlignment(Element.ALIGN_CENTER);
        cellNotaRisco.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cellNotaRisco);

        PdfPCell linhaBranco = new PdfPCell(new Phrase(VAZIO));
        linhaBranco.setColspan(quantidadeColunasMaximaRelatorio);
        linhaBranco.setBorderColor(BaseColor.WHITE);
        linhaBranco.setBorder(0);
        table.addCell(linhaBranco);

    }

    public void addLInhaNotaFinalAjustadaJustificativa(Document document) throws IOException, DocumentException {
        String justificativaNotaAjustada = "";
        if (relatorioMatriz.getNotaMatriz() != null && relatorioMatriz.getNotaMatriz().getJustificativaNota() != null
                && !relatorioMatriz.possuiNotaAjustadaCorec()) {
            justificativaNotaAjustada = relatorioMatriz.getNotaMatriz().getJustificativaNota();

            int quantidadeDeLinhasJustificativa = (justificativaNotaAjustada.length() / 150);

            PdfPCell linhajustificativa = new PdfPCell();

            Paragraph texto = new Paragraph();
            StyleSheet styles = new StyleSheet();
            String tag = "p";
            styles.loadTagStyle(tag, "font-size", "12px");
            styles.loadTagStyle(tag, "font-family", "Times-Roman");

            List<Element> elements =
                    HTMLWorker.parseToList(new StringReader(Util.normalizaTexto(justificativaNotaAjustada)), styles);
            for (int k = 0; k < elements.size(); ++k) {
                texto.add(elements.get(k));
            }
            document.add(texto);

            linhajustificativa.setBorderColor(BaseColor.WHITE);
            linhajustificativa.setRowspan(quantidadeDeLinhasJustificativa);
            linhajustificativa.setColspan(quantidadeColunasMaximaRelatorio);
            linhajustificativa.setBorder(0);
            table.addCell(linhajustificativa);
        }

    }

    private void addLinhaNotaFinalAjustada() {

        PdfPCell vazioResidual =
                new PdfPCell(new Phrase(relatorioMatriz.possuiNotaAjustada() ? "Nota final ajustada:" : "", new Font(
                        FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
        vazioResidual.setHorizontalAlignment(Element.ALIGN_LEFT);
        vazioResidual.setBorder(0);
        table.addCell(vazioResidual);
        PdfPCell valor =
                new PdfPCell(new Phrase(relatorioMatriz.getNotaAjustada(), new Font(FontFamily.TIMES_ROMAN, 12,
                        Font.NORMAL)));
        valor.setBorder(0);
        valor.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(valor);
        PdfPCell labelCorec =
                new PdfPCell(new Phrase(relatorioMatriz.possuiNotaAjustadaCorec() ? "(Corec)" : "", new Font(
                        FontFamily.TIMES_ROMAN, 12, Font.NORMAL)));
        labelCorec.setHorizontalAlignment(Element.ALIGN_RIGHT);
        labelCorec.setBorder(0);
        table.addCell(labelCorec);
        PdfPCell vazio = new PdfPCell(new Phrase(VAZIO));
        vazio.setHorizontalAlignment(Element.ALIGN_LEFT);
        vazio.setColspan(quantidadeColunasMaximaRelatorio - 3);
        vazio.setBorder(0);
        table.addCell(vazio);
    }

    private void addLinhaNotaFinalCalculada() {
        PdfPCell vazioResidual =
                new PdfPCell(new Phrase("Nota final calculada:", new Font(FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
        vazioResidual.setHorizontalAlignment(Element.ALIGN_LEFT);
        vazioResidual.setBorder(0);
        table.addCell(vazioResidual);
        PdfPCell valor =
                new PdfPCell(new Phrase(
                        relatorioMatriz.getNotaCalculadaFinal() == null ? A_AVALIAR : A_AVALIAR.equals(relatorioMatriz
                                .getNotaCalculadaFinal()) ? relatorioMatriz.getNotaCalculadaFinal() : relatorioMatriz
                                .getNotaCalculadaFinal().replace('.', ','), new Font(FontFamily.TIMES_ROMAN, 12,
                                Font.NORMAL)));
        valor.setBorder(0);
        valor.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(valor);
        PdfPCell vazio = new PdfPCell(new Phrase(VAZIO));
        vazio.setHorizontalAlignment(Element.ALIGN_LEFT);
        vazio.setColspan(quantidadeColunasMaximaRelatorio - 2);
        vazio.setBorder(0);
        table.addCell(vazio);

    }

    private void addLinhaNotaFinalRefinada() {
        PdfPCell vazioRefinada =
                new PdfPCell(new Phrase("Nota final refinada:", new Font(FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
        vazioRefinada.setHorizontalAlignment(Element.ALIGN_MIDDLE);
        vazioRefinada.setBorder(0);
        table.addCell(vazioRefinada);
        String notaRefinada = null;
        if (relatorioMatriz.getNotaCalculadaFinal() == null) {
            notaRefinada = A_AVALIAR;
        } else if (A_AVALIAR.equals(relatorioMatriz.getNotaCalculadaFinal())) {
            notaRefinada = "";
        } else {
            Metodologia metodologia = relatorioMatriz.getPerfilRisco().getCiclo().getMetodologia();
            ParametroNota parametroNota = ParametroNotaMediator.get().buscarPorMetodologiaENota(metodologia,
                            new BigDecimal(relatorioMatriz.getNotaCalculadaFinal()), false);
            notaRefinada = metodologia.getIsMetodologiaNova() ? parametroNota
                    .getDescricao() : parametroNota.getDescricaoValor();
        }

        PdfPCell valor = new PdfPCell(new Phrase(notaRefinada, new Font(FontFamily.TIMES_ROMAN, 12, Font.NORMAL)));
        valor.setHorizontalAlignment(Element.ALIGN_RIGHT);
        valor.setBorder(0);
        table.addCell(valor);

        PdfPCell vazio = new PdfPCell(new Phrase(" "));
        vazio.setHorizontalAlignment(Element.ALIGN_LEFT);
        vazio.setColspan(quantidadeColunasMaximaRelatorio - 2);
        vazio.setBorder(0);
        table.addCell(vazio);

    }

    private void addLinhaResidual() {
        PdfPCell vazioResidual =
                new PdfPCell(new Phrase("Notas residuais", new Font(FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
        vazioResidual.setVerticalAlignment(Element.ALIGN_MIDDLE);
        vazioResidual.setColspan(2);
        vazioResidual.setRowspan(2);
        table.addCell(vazioResidual);
        for (LinhaNotasMatrizVO linhaNotasMatrizVO : relatorioMatriz.getNotaResiduais()) {
            PdfPCell notaResidual =
                    new PdfPCell(new Phrase(linhaNotasMatrizVO.getNota(), new Font(FontFamily.TIMES_ROMAN, 11,
                            Font.NORMAL)));
            notaResidual.setVerticalAlignment(Element.ALIGN_MIDDLE);
            notaResidual.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(notaResidual);
        }

        for (LinhaNotasMatrizVO linhaNotasMatrizVO : relatorioMatriz.getMediaResiduais()) {
            PdfPCell mediaResidual =
                    new PdfPCell(new Phrase(linhaNotasMatrizVO.getNota(), new Font(FontFamily.TIMES_ROMAN, 11,
                            Font.NORMAL)));
            mediaResidual.setColspan(2);
            mediaResidual.setHorizontalAlignment(Element.ALIGN_CENTER);
            mediaResidual.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(mediaResidual);
        }

    }

    private void montarCabecalhoMatriz() {
        PdfPCell vazioMatriz = new PdfPCell(new Phrase(""));
        vazioMatriz.setColspan(2);
        vazioMatriz.setRowspan(3);
        table.addCell(vazioMatriz);

        for (ParametroGrupoRiscoControle grupo : relatorioMatriz.getParametroGrupo()) {
            addCellGrupo(grupo);
        }

        for (ParametroGrupoRiscoControle grupo : relatorioMatriz.getParametroGrupo()) {
            addCellPercentualGrupo(grupo);
        }

        for (ParametroGrupoRiscoControle grupo : relatorioMatriz.getParametroGrupo()) {
            PdfPCell linhaRisco = new PdfPCell(new Phrase("R", new Font(FontFamily.TIMES_ROMAN, 11, Font.BOLD)));
            linhaRisco.setHorizontalAlignment(Element.ALIGN_CENTER);
            linhaRisco.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(linhaRisco);

            PdfPCell linhaControle = new PdfPCell(new Phrase("C", new Font(FontFamily.TIMES_ROMAN, 11, Font.BOLD)));
            linhaControle.setHorizontalAlignment(Element.ALIGN_CENTER);
            linhaControle.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(linhaControle);
        }

        addLinhaMatriz();

    }

    private void addLinhaMatriz() {
        for (LinhaMatrizVO linha : relatorioMatriz.getLinhaMatrizVO()) {
            PdfPCell linhaAtividade =
                    new PdfPCell(new Phrase(linha.getNomeFormatadoVigenteRelatorio(), new Font(FontFamily.TIMES_ROMAN,
                            12, linha.isFilho() ? Font.NORMAL : Font.BOLD)));
            linhaAtividade.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(linhaAtividade);

            PdfPCell linhaPercentual =
                    new PdfPCell(new Phrase(MatrizCicloMediator.get().percentualDaLinha(relatorioMatriz.getMatriz(),
                            linha), new Font(FontFamily.TIMES_ROMAN, 12, linha.isFilho() ? Font.NORMAL : Font.BOLD)));
            linhaPercentual.setHorizontalAlignment(Element.ALIGN_CENTER);
            linhaPercentual.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(linhaPercentual);
            for (ParametroGrupoRiscoControle grupo : relatorioMatriz.getParametroGrupo()) {
                CelulaRiscoControle celula =
                        TipoLinhaMatrizVOEnum.UNIDADE.equals(linha.getTipo()) ? null : relatorioMatriz
                                .getCelulaPelaAtividadeEGrupo(grupo, linha.getPk());

                String notaRisco =
                        obterNotaARC(celula == null ? null : celula.getArcRisco(), celula == null ? null : celula
                                .getAtividade().getMatriz().getCiclo());

                PdfPCell cellNotaRisco =
                        new PdfPCell(new Phrase(notaRisco, new Font(FontFamily.TIMES_ROMAN, 11, Font.NORMAL)));
                cellNotaRisco.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellNotaRisco.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cellNotaRisco);

                notaRisco =
                        obterNotaARC(celula == null ? null : celula.getArcControle(), celula == null ? null : celula
                                .getAtividade().getMatriz().getCiclo());

                PdfPCell cellNotaControle =
                        new PdfPCell(new Phrase(notaRisco, new Font(FontFamily.TIMES_ROMAN, 11, Font.NORMAL)));
                cellNotaControle.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellNotaControle.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cellNotaControle);

            }

        }
    }

    private String obterNotaARC(AvaliacaoRiscoControle arc, Ciclo ciclo) {
        return arc == null ? "" : AvaliacaoRiscoControleMediator.get().notaArc(arc, ciclo,
                relatorioMatriz.getPerfilMenu(), relatorioMatriz.getPerfilRisco());
    }

    private void addCellPercentualGrupo(ParametroGrupoRiscoControle grupo) {
        PdfPCell linhaGrupo =
                new PdfPCell(new Phrase(MatrizCicloMediator.get().percentualDoGrupo(relatorioMatriz.getMatriz(),
                        relatorioMatriz.getCelulasPorGrupo(grupo), relatorioMatriz.getVersoesPerfilRiscoARCs()),
                        new Font(FontFamily.TIMES_ROMAN, 11, Font.BOLD)));
        linhaGrupo.setColspan(2);
        linhaGrupo.setHorizontalAlignment(Element.ALIGN_CENTER);
        linhaGrupo.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(linhaGrupo);

    }

    private void addCellGrupo(ParametroGrupoRiscoControle grupo) {
        PdfPCell linhaGrupo =
                new PdfPCell(new Phrase(grupo.getNomeAbreviado(), new Font(FontFamily.TIMES_ROMAN, 11, Font.BOLD)));
        linhaGrupo.setColspan(2);
        linhaGrupo.setHorizontalAlignment(Element.ALIGN_CENTER);
        linhaGrupo.setVerticalAlignment(Element.ALIGN_CENTER);
        table.addCell(linhaGrupo);

    }

    private void gerarRodape() {
        PdfPCell linhaTextoRodape =
                new PdfPCell(new Phrase(getTextoRodape(), new Font(FontFamily.TIMES_ROMAN, 10, Font.ITALIC)));
        linhaTextoRodape.setBorder(1);
        linhaTextoRodape.setBorderColorTop(BaseColor.BLACK);
        linhaTextoRodape.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(linhaTextoRodape);

        PdfPCell linhaTituloTextoRodape = new PdfPCell(paginacaoRodape());
        linhaTituloTextoRodape.setBorder(1);
        linhaTituloTextoRodape.setBorderColorTop(BaseColor.BLACK);
        linhaTituloTextoRodape.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(linhaTituloTextoRodape);

        addLinhaSigilo(isDossie);
    }

    private Phrase paginacaoRodape() {
        if (apresentacaoVO == null) {
            return new Phrase("SRC - Sistema de Avaliações de Riscos e Controles", new Font(FontFamily.TIMES_ROMAN, 10,
                    Font.ITALIC));
        } else {
            return new Phrase("SRC " + indice + "/" + qtdPaginas, new Font(FontFamily.TIMES_ROMAN, 10, Font.NORMAL));
        }

    }

    private String getTextoRodape() {
        StringBuffer rodape = new StringBuffer();
        if (apresentacaoVO == null) {

            rodape.append("Versão ");
            rodape.append(relatorioMatriz.getPerfilRisco().getDataCriacaoFormatadaSemHora());
            rodape.append(" - Ciclo ");
            rodape.append(relatorioMatriz.getPerfilRisco().getCiclo().getDataInicioFormatada());
            rodape.append(" ~ ");
            rodape.append(relatorioMatriz.getPerfilRisco().getCiclo().getDataPrevisaoFormatada());
        } else {
            rodape.append(apresentacaoVO.getNomeEs());
        }
        return rodape.toString();

    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {

        Rectangle page = document.getPageSize();
        try {
            PdfPTable tableRodape;
            tableRodape = exportarRodape();
            tableRodape.setTotalWidth(page.getWidth() - document.leftMargin() - document.rightMargin());
            tableRodape.writeSelectedRows(0, -1, document.leftMargin(), document.bottom(), writer.getDirectContent());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onStartPage(PdfWriter writer, Document document) {
        try {
            Rectangle page = document.getPageSize();
            PdfPTable tableCabecalho;
            tableCabecalho = exportarCabecalho();
            tableCabecalho.setTotalWidth(page.getWidth() - document.leftMargin() - document.rightMargin());
            document.add(tableCabecalho);

        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PdfPTable exportarTituloTabelaArcExterno() throws IOException, DocumentException {
        tabelaPagina();
        addLinhaTituloArcExterno();
        return table;
    }

    public PdfPTable exportarTabelaArcExterno() throws IOException, DocumentException {

        montarTabelaMatriz();

        PdfPCell vazioResidual =
                new PdfPCell(new Phrase("Nota", new Font(FontFamily.TIMES_ROMAN, 12, Font.NORMAL)));
        vazioResidual.setVerticalAlignment(Element.ALIGN_MIDDLE);
        vazioResidual.setColspan(2);
        table.addCell(vazioResidual);

        PdfPCell linhaPercentual =
                new PdfPCell(new Phrase(relatorioMatriz.getMatriz().getPercentualGovernancaCorporativa(), new Font(
                        FontFamily.TIMES_ROMAN, 12, Font.NORMAL)));
        linhaPercentual.setHorizontalAlignment(Element.ALIGN_CENTER);
        linhaPercentual.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(linhaPercentual);
        
        String notaARCExterno = 
                obterNotaARC(relatorioMatriz.getArcExterno().getAvaliacaoRiscoControle(), 
                        relatorioMatriz.getArcExterno().getCiclo());
        
        PdfPCell cellNotaRisco =
                new PdfPCell(new Phrase(notaARCExterno, new Font(FontFamily.TIMES_ROMAN, 11, Font.NORMAL)));
        cellNotaRisco.setHorizontalAlignment(Element.ALIGN_CENTER);
        cellNotaRisco.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cellNotaRisco);

        PdfPCell linhaBranco = new PdfPCell(new Phrase(VAZIO));
        linhaBranco.setColspan(quantidadeColunasMaximaRelatorio);
        linhaBranco.setBorderColor(BaseColor.WHITE);
        linhaBranco.setBorder(0);
        table.addCell(linhaBranco);

        addLinhaVazia();
        addLinhaVazia();

        return table;

    }

    public boolean possuiArcExterno() {
        return relatorioMatriz.getArcExterno() != null && relatorioMatriz.getMatriz() != null
                && relatorioMatriz.getMatriz().getNumeroFatorRelevanciaAE() != null
                && relatorioMatriz.getMatriz().getNumeroFatorRelevanciaAE().compareTo(BigDecimal.ZERO) == 1;
    }

}
