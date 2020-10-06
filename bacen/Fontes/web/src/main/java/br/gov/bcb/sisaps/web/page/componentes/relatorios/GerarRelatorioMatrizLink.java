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

import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.web.page.DefaultPage;
import br.gov.bcb.sisaps.web.page.dominio.matriz.relatorio.ExportacaoMatrizXls;
import br.gov.bcb.sisaps.web.page.dominio.matriz.relatorio.RelatorioMatriz;

import com.itextpdf.text.DocumentException;

public class GerarRelatorioMatrizLink extends DownloadLink {
    private PerfilRisco perfilRisco;
    private DefaultPage paginaAtual;

    public GerarRelatorioMatrizLink(String id, File file, int perfilRisco, DefaultPage paginaAtual) {
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
                    }.setFileName("Matriz.xls").setContentDisposition(ContentDisposition.ATTACHMENT)
                            .setCacheDuration(Duration.NONE));
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        
    }

    private File gerarRelatorio() throws DocumentException {
        File tempFile;
        BufferedOutputStream bos = null;
        try {
            tempFile = File.createTempFile(RelatorioMatriz.NOME_ARQUIVO_RELATORIO, ".xls");
            tempFile.deleteOnExit();
            RelatorioMatriz relatorioMatriz = new RelatorioMatriz(perfilRisco, paginaAtual.getPerfilPorPagina());
            ExportacaoMatrizXls exportacaoMatrizXls = new ExportacaoMatrizXls(relatorioMatriz);
            Workbook planilha = exportacaoMatrizXls.exportarParaXls(paginaAtual);
            bos = new BufferedOutputStream(new FileOutputStream(tempFile));
            planilha.write(bos);
            bos.flush();

            getPage().getResponse().close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return tempFile;
    }
}
