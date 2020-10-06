package br.gov.bcb.sisaps.web.page.componentes.relatorios;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.request.IRequestCycle;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.util.file.Files;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.time.Duration;

import br.gov.bcb.sisaps.src.vo.ConsultaCicloVO;
import br.gov.bcb.sisaps.web.page.DefaultPage;

import com.itextpdf.text.DocumentException;

public class GerarRelatorioComitesLink extends DownloadLink {
    private final DefaultPage paginaAtual;
    private final boolean isComitesRealizados;
    private final ConsultaCicloVO consulta;

    public GerarRelatorioComitesLink(String id, File file, DefaultPage paginaAtual,
            boolean isRelatorioComitesRealizados, ConsultaCicloVO consulta) {
        super(id, file);
        this.paginaAtual = paginaAtual;
        this.isComitesRealizados = isRelatorioComitesRealizados;
        this.consulta = consulta;
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
                            Files.remove(file);
                        }
                    }.setFileName(getFileName()).setContentDisposition(ContentDisposition.ATTACHMENT)
                            .setCacheDuration(Duration.NONE));
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    private File gerarRelatorio() throws DocumentException {
        File tempFile;
        BufferedOutputStream bos = null;
        try {
            tempFile = File.createTempFile(getFileName(), ".xls");
            tempFile.deleteOnExit();
            ExportacaoComitesXls exportacaoComitesXls = new ExportacaoComitesXls(isComitesRealizados, consulta);
            Workbook planilha = exportacaoComitesXls.exportarParaXls(paginaAtual);
            bos = new BufferedOutputStream(new FileOutputStream(tempFile));
            planilha.write(bos);
            bos.flush();

            getPage().getResponse().close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return tempFile;
    }

    private String getFileName() {
        return isComitesRealizados ? "ListaComitesRealizados.xls" : "ListaComitesARealizar.xls";
    }
}
