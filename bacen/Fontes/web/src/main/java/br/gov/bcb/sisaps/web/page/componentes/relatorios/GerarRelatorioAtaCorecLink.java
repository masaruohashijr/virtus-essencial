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

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfCopyFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.web.page.DefaultPage;
import br.gov.bcb.sisaps.web.page.dominio.agenda.ExportarAtaCorecPdf;
import br.gov.bcb.sisaps.web.page.dominio.agenda.GerarAtaCorec;

public class GerarRelatorioAtaCorecLink extends DownloadLink {
    private static final String PDF = ".pdf";
    private Ciclo ciclo;
    private DefaultPage paginaAtual;

    public GerarRelatorioAtaCorecLink(String id, File file, Ciclo ciclo, DefaultPage paginaAtual) {
        super(id, file);
        this.ciclo = CicloMediator.get().buscarCicloPorPK(ciclo.getPk());
        this.paginaAtual = paginaAtual;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

    }

    @Override
    public void onClick() {
        final File file = addImpressao();

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
                }.setFileName("Ata Corec.pdf").setContentDisposition(ContentDisposition.ATTACHMENT)
                        .setCacheDuration(Duration.NONE));
    }

    private File addImpressao() {
        File tempFile;
        PdfCopyFields copy;
        BufferedOutputStream bos = null;
        try {
            tempFile = File.createTempFile("ata", PDF);
            bos = new BufferedOutputStream(new FileOutputStream(tempFile)); 
            copy = new PdfCopyFields(bos);
            addImpressaoAtaCorec(copy);
            copy.close();
            bos.flush();
            getPage().getResponse().close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return tempFile;
    }

    private void addImpressaoAtaCorec(PdfCopyFields copy) {

        File tempFile;
        BufferedOutputStream bos = null;
        try {
            tempFile = File.createTempFile("Ata Corec", PDF);
            tempFile.deleteOnExit();
            bos = new BufferedOutputStream(new FileOutputStream(tempFile));
            Document document = new Document(PageSize.A4);
            document.setPageSize(PageSize.A4);
            PdfWriter writer = PdfWriter.getInstance(document, bos);

            ExportarAtaCorecPdf export = new ExportarAtaCorecPdf(new GerarAtaCorec(ciclo), paginaAtual);
            writer.setPageEvent(export);

            document.open();
            writer.setMargins(25, 25, 30, 30);
            export.addDados(document);
            document.close();

            copy.addDocument(new PdfReader(tempFile.getPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }

    }

}
