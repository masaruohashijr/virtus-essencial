package br.gov.bcb.sisaps.web.page.dominio.agenda;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.util.IOUtils;
import org.apache.wicket.protocol.http.WebApplication;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.html.simpleparser.StyleSheet;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import br.gov.bcb.wicket.pages.AbstractBacenWebPage;

public class ExportarAtaCorecPdf extends PdfPageEventHelper {
    private static final String VAZIO = "   ";
    private GerarAtaCorec relatorioAta;
    private PdfPTable table;
    private AbstractBacenWebPage paginaAtual;
    private int qtdPaginasTotalDossie;
    private int qtdPaginasAtualDossie;
    private boolean isDossie;

    public ExportarAtaCorecPdf(GerarAtaCorec relatorioAta, AbstractBacenWebPage paginaAtual) throws DocumentException {
        this(relatorioAta, paginaAtual, 0, 0, false);
    }

    public ExportarAtaCorecPdf(GerarAtaCorec relatorioAta, AbstractBacenWebPage paginaAtual, int qtdPaginasAtualDossie,
            int qtdPaginasTotalDossie, boolean isDossie) throws DocumentException {
        this.relatorioAta = relatorioAta;
        this.paginaAtual = paginaAtual;
        this.qtdPaginasAtualDossie = qtdPaginasAtualDossie;
        this.qtdPaginasTotalDossie = qtdPaginasTotalDossie;
        this.isDossie = isDossie;
    }

    private byte[] inserirLogo(AbstractBacenWebPage paginaAtual) throws IOException {
        WebApplication wa = (WebApplication) paginaAtual.getApplication();
        InputStream imgLogo = wa.getServletContext().getResourceAsStream("/relatorios/logobacen3.png");
        return IOUtils.toByteArray(imgLogo);
    }

    public PdfPTable exportarCabecalho(PdfWriter writer) throws IOException, DocumentException {
        table = new PdfPTable(10);
        table.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.setWidthPercentage(90);
        table.setFooterRows(1);
        addLinhaSigilo(false, false, writer);
        addCabecalho();
        return table;
    }

    private void addCabecalho() throws BadElementException, IOException {
        addLinhaImagem();
    }

    private void addLinhaImagem() throws BadElementException, IOException {

        Image image = Image.getInstance(inserirLogo(paginaAtual));
        image.scalePercent(60, 60);
        PdfPCell linhaLogo = new PdfPCell(image);
        linhaLogo.setColspan(20);
        linhaLogo.setBorderColor(BaseColor.WHITE);
        linhaLogo.setBorder(0);
        linhaLogo.setHorizontalAlignment(Element.ALIGN_CENTER);  
        linhaLogo.setVerticalAlignment(Element.ALIGN_TOP);  
        table.addCell(linhaLogo);
        PdfPCell linhaBranco = new PdfPCell(new Phrase(VAZIO));
        linhaBranco.setColspan(20);
        linhaBranco.setBorderColor(BaseColor.WHITE);
        linhaBranco.setBorder(0);
        table.addCell(linhaBranco);
    }

    public void addDados(Document document) throws IOException, DocumentException {
        String corpo = "";
        corpo = relatorioAta.textoFormatado();

        Paragraph texto = new Paragraph();
        StyleSheet styles = new StyleSheet();
        String tag = "p";
        styles.loadTagStyle(tag, "font-size", "12px");
        styles.loadTagStyle(tag, "font-family", "Times-Roman");

        List<Element> elements = HTMLWorker.parseToList(new StringReader(corpo), styles);
        for (int k = 0; k < elements.size(); ++k) {
            texto.add(elements.get(k));
        }
        document.add(texto);

    }

    @Override
    public void onStartPage(PdfWriter writer, Document document) {
        try {
            Rectangle page = document.getPageSize();
            PdfPTable tableCabecalho;
            tableCabecalho = exportarCabecalho(writer);
            tableCabecalho.setTotalWidth(page.getWidth() - document.leftMargin() - document.rightMargin());
            document.add(tableCabecalho);

        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {

        Rectangle page = document.getPageSize();
        try {
            PdfPTable tableRodape;
            tableRodape = exportarRodape(writer);
            tableRodape.setTotalWidth(page.getWidth() - document.leftMargin() - document.rightMargin());
            tableRodape.writeSelectedRows(0, -1, document.leftMargin(), document.bottom(), writer.getDirectContent());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

    }

    public PdfPTable exportarRodape(PdfWriter writer) throws IOException, DocumentException {
        table = new PdfPTable(10);
        table.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.setWidthPercentage(120);
        table.setFooterRows(1);
        addLinhaSigilo(isDossie, true, writer);
        return table;
    }

    private void addLinhaSigilo(boolean isPaginacao, boolean isRodape, PdfWriter writer) {
        String qtdPaginasAta = "";
        if (CollectionUtils.isNotEmpty(relatorioAta.getListaArcs()) && relatorioAta.getListaArcs().size() > 10) {
            qtdPaginasAta = "3";
        } else {
            qtdPaginasAta = "2";
        }
        String paginacao = isPaginacao ? qtdPaginasAtualDossie + " / " + qtdPaginasTotalDossie
                : (writer.getCurrentPageNumber() + " / " + qtdPaginasAta);
        PdfPCell linhaTitulo = new PdfPCell(new Phrase(
                "Informação protegida por sigilo legal, nos termos da Lei Complementar nº 105, de 10 de janeiro de 2001, e/ou de acesso restrito, nos termos do art. 5º do Decreto nº 7.724, de 16 de maio de 2012."
                        + "                     " + "                     " + "                         "
                        + "                   "
                        + (isRodape ? paginacao : ""),
                new Font(FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
        linhaTitulo.setColspan(120);
        linhaTitulo.setBorder(0);
        linhaTitulo.setHorizontalAlignment(Element.ALIGN_LEFT);
        linhaTitulo.setVerticalAlignment(Element.ALIGN_TOP);
        table.addCell(linhaTitulo);
        if (isRodape) {
            qtdPaginasAtualDossie++;
        }
    }

}