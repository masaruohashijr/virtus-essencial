package br.gov.bcb.sisaps.web.page.componentes.relatorios;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.request.IRequestCycle;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.util.file.Files;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.time.Duration;

import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.web.page.DefaultPage;
import br.gov.bcb.sisaps.web.page.dominio.matriz.relatorio.ExportarMatrizPdf;
import br.gov.bcb.sisaps.web.page.dominio.matriz.relatorio.RelatorioMatriz;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class GerarRelatorioMatrizPdfLink extends DownloadLink {
    private PerfilRisco perfilRisco;
    private DefaultPage paginaAtual;

    public GerarRelatorioMatrizPdfLink(String id, File file, int perfilRisco, DefaultPage paginaAtual) {
        super(id, file);
        this.paginaAtual = paginaAtual;
        this.perfilRisco = PerfilRiscoMediator.get().obterPerfilRiscoPorPk(perfilRisco);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        setVisibilityAllowed(perfilRisco.getCiclo().getMatriz() != null);

    }

    @Override
    public void onClick() {
        final File file;
        try {
            file = gerarRelatorio();
            IResourceStream resourceStream = new FileResourceStream(new org.apache.wicket.util.file.File(file));
            getRequestCycle().scheduleRequestHandlerAfterCurrent(
                    new ResourceStreamRequestHandler(resourceStream) {
                        @Override
                        public void respond(IRequestCycle requestCycle) {
                            super.respond(requestCycle);

                            if (true) {
                                Files.remove(file);
                            }
                        }
                    }.setFileName("Matriz.pdf").setContentDisposition(ContentDisposition.ATTACHMENT)
                            .setCacheDuration(Duration.NONE));
        } catch (DocumentException e) {
            e.printStackTrace();
        }

    }

    private File gerarRelatorio() throws DocumentException {
        File tempFile;
        BufferedOutputStream bos = null;
        try {

            tempFile = File.createTempFile(RelatorioMatriz.NOME_ARQUIVO_RELATORIO, ".pdf");
            tempFile.deleteOnExit();
            bos = new BufferedOutputStream(new FileOutputStream(tempFile));

            Document document = new Document(PageSize.A4_LANDSCAPE);
            document.setPageSize(PageSize.A4.rotate());
            PdfWriter writer = PdfWriter.getInstance(document, bos);
        
            RelatorioMatriz relatorioMatriz = new RelatorioMatriz(perfilRisco, paginaAtual.getPerfilPorPagina());
            ExportarMatrizPdf export = new ExportarMatrizPdf(relatorioMatriz, paginaAtual);
            writer.setPageEvent(export);
            document.open();
            
            PdfPTable tablecabecalho = export.exportarCabecalho();
            document.add(tablecabecalho);
            
            PdfPTable table = export.exportarTabelaMatriz();
            document.add(table);
            
            document.close();

            getPage().getResponse().close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return tempFile;
    }
}
