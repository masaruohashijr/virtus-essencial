package br.gov.bcb.sisaps.web.page.componentes.relatorios;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.request.IRequestCycle;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.util.file.Files;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.time.Duration;

import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.ParametroGrupoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleExternoMediator;
import br.gov.bcb.sisaps.src.mediator.ParametroGrupoRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.src.vo.analisequantitativa.QuadroPosicaoFinanceiraVO;
import br.gov.bcb.sisaps.web.page.DefaultPage;
import br.gov.bcb.sisaps.web.page.dominio.analisequantitativa.relatorios.RelatorioAnaliseQuantitativa;
import br.gov.bcb.sisaps.web.page.dominio.aqt.relatorio.RelatorioDetalhesAQTMatriz;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.relatorioArc.JuncaoPDFMap;
import br.gov.bcb.sisaps.web.page.dominio.gerenciaes.relatorio.RelatorioDetalharES;
import br.gov.bcb.sisaps.web.page.dominio.gerenciarnotasintese.relatorioSinteses.RelatorioSinteses;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopyFields;
import com.itextpdf.text.pdf.PdfReader;

public class GerarRelatorioDossieResumidoLink extends DownloadLink {
    private final DefaultPage paginaAtual;
    private final PerfilAcessoEnum perfilMenu;

    public GerarRelatorioDossieResumidoLink(String id, File file, DefaultPage paginaAtual) {
        super(id, file);
        this.paginaAtual = paginaAtual;
        this.perfilMenu = paginaAtual.getPerfilPorPagina();
    }

    public PerfilRisco getPerfilRiscoSelecionado() {
        return null;
    }

    @Override
    public void onClick() {
        final File file = addAnexoImpressaoDossie();

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
                }.setFileName("Dossie.pdf").setContentDisposition(ContentDisposition.ATTACHMENT)
                        .setCacheDuration(Duration.NONE));
    }

    private File addAnexoImpressaoDossie() {
        File tempFile;
        PdfCopyFields copy;
        BufferedOutputStream bos = null;
        try {
            tempFile = File.createTempFile("Dossie", ".pdf");

            bos = new BufferedOutputStream(new FileOutputStream(tempFile)); //Criamos o arquivo  
            copy = new PdfCopyFields(bos);

            PerfilRisco perfilRisco = getPerfilRiscoSelecionado();

            if (perfilRisco.getCiclo().getEntidadeSupervisionavel() != null) {
                addRelatorioDetalhesES(copy);
            }

            if (perfilRisco.getCiclo().getMatriz() != null) {
                addImpressaoSinteses(copy);
            }

            if (PerfilRiscoMediator.get().getAnalisesQuantitativasAQTPerfilRisco(perfilRisco) != null) {
                addImpressaoSintesesAQT(copy);
            }

            addImpressaoAnaliseQuantitativa(copy);

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

    private void addImpressaoSinteses(PdfCopyFields copy) {
        try {
            PerfilRisco perfilRisco = getPerfilRiscoSelecionado();
            List<Integer> idsParametrosGrupoRiscoMatriz =
                    ParametroGrupoRiscoControleMediator.get().buscarIdsGruposRiscoDaMatriz(
                            perfilRisco.getCiclo().getMatriz());
            List<ParametroGrupoRiscoControle> lista =
                    ParametroGrupoRiscoControleMediator.get().buscarGruposRiscoDaMatrizESinteseObrigatoria(
                            idsParametrosGrupoRiscoMatriz);
            AvaliacaoRiscoControle arcExterno =
                    AvaliacaoRiscoControleExternoMediator.get().buscarArcExternoPerfilAtual(perfilRisco.getCiclo(),
                            perfilRisco);
            if (arcExterno != null) {
                lista.add(arcExterno.getAvaliacaoRiscoControleExterno().getParametroGrupoRiscoControle());
            }
            RelatorioSinteses relatorio =
                    new RelatorioSinteses(lista, perfilRisco.getCiclo().getMatriz(), perfilRisco, perfilMenu, true);
            copy.addDocument(new PdfReader(relatorio.gerarRelatorioPDF(paginaAtual)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    private void addImpressaoAnaliseQuantitativa(PdfCopyFields copy) {
        try {
            PerfilRisco perfilRisco = getPerfilRiscoSelecionado();
            QuadroPosicaoFinanceiraVO quadroVO = PerfilRiscoMediator.get().obterQuadroVigente(perfilRisco);
            if (quadroVO.getPk() != null) {
                RelatorioAnaliseQuantitativa relatorio = new RelatorioAnaliseQuantitativa(perfilRisco, quadroVO);
                copy.addDocument(new PdfReader(relatorio.gerarRelatorioPDF(paginaAtual)));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    private void addRelatorioDetalhesES(PdfCopyFields copy) {
        try {
            PerfilRisco perfilRisco = getPerfilRiscoSelecionado();
            RelatorioDetalharES relatorio =
                    new RelatorioDetalharES(perfilRisco.getCiclo(), perfilRisco, perfilMenu, paginaAtual);

            copy.addDocument(JuncaoPDFMap.gerarPDF(relatorio.gerarRelatorioPDF(paginaAtual), relatorio.getMapAnexo()));

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    private void addImpressaoSintesesAQT(PdfCopyFields copy) {
        try {
            RelatorioDetalhesAQTMatriz relatorio =
                    new RelatorioDetalhesAQTMatriz(getPerfilRiscoSelecionado(), perfilMenu);
            copy.addDocument(new PdfReader(relatorio.gerarRelatorioPDF(paginaAtual)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }

}
