package br.gov.bcb.sisaps.web.page.dominio.perfilderisco.apresentacao.relatorio;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.request.IRequestCycle;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.util.file.Files;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.time.Duration;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.SecaoApresentacaoEnum;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.src.vo.AnexoApresentacaoVO;
import br.gov.bcb.sisaps.src.vo.ApresentacaoVO;
import br.gov.bcb.sisaps.src.vo.TextoApresentacaoVO;
import br.gov.bcb.sisaps.src.vo.analisequantitativa.QuadroPosicaoFinanceiraVO;
import br.gov.bcb.sisaps.util.enumeracoes.NormalidadeEnum;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.relatorioArc.JuncaoPDFMap;
import br.gov.bcb.sisaps.web.page.dominio.matriz.relatorio.ExportarMatrizPdf;
import br.gov.bcb.sisaps.web.page.dominio.matriz.relatorio.RelatorioMatriz;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfCopyFields;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

public class GerarRelatorioApresentacaoLink extends DownloadLink {
    private static final String PDF = ".pdf";
    private final PerfilRisco perfilRisco;
    private final Page paginaAtual;
    private final Ciclo ciclo;
    private final ApresentacaoVO apresentacaoVO;
    private final int totalSlides;
    private int indice;
    private final PerfilAcessoEnum perfilMenu;

    public GerarRelatorioApresentacaoLink(String id, File file, PerfilRisco perfilRisco, Ciclo ciclo,
            ApresentacaoVO apresentacaoVO, Page paginaAtual, int totalSlides, PerfilAcessoEnum perfilMenu) {
        super(id, file);
        this.perfilRisco = perfilRisco;
        this.paginaAtual = paginaAtual;
        this.apresentacaoVO = apresentacaoVO;
        this.ciclo = ciclo;
        this.perfilMenu = perfilMenu;
        this.totalSlides = totalSlides;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
    }

    @Override
    public void onClick() {
        indice = 0;
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
                }.setFileName("Apresentacao.pdf").setContentDisposition(ContentDisposition.ATTACHMENT)
                        .setCacheDuration(Duration.NONE));
    }

    private File addAnexoImpressaoDossie() {
        File tempFile;
        PdfCopyFields copy;
        BufferedOutputStream bos = null;

        try {
            tempFile = File.createTempFile("Apresentacao", PDF);
            bos = new BufferedOutputStream(new FileOutputStream(tempFile)); //Criamos o arquivo  
            copy = new PdfCopyFields(bos);

            // Monta os slides das seções.
            for (SecaoApresentacaoEnum secao : SecaoApresentacaoEnum.values()) {

                // Verifica qual é a seção.
                switch (secao) {
                // Seções constantes sem highlight.
                    case ANALISE_QUANTITATIVA:
                    case ANALISE_QUALITATIVA:
                    case CONCLUSAO:
                        adicionarRelatorioConstante(copy, apresentacaoVO, secao);
                        break;
                    // Seções constantes com highlight.
                    case VOTACAO_NOTA_QUANTITATIVA:
                    case VOTACAO_NOTA_QUALITATIVA:
                    case VOTACAO_NOTA_FINAL:
                    case VOTACAO_PERSPECTIVA:
                    case VOTACAO_PROPOSTA_ACOES:
                        adicionarRelatorioConstante(copy, apresentacaoVO, secao);
                        break;

                    // Seções de anexo.
                    case EVOLUCAO_DAS_AVALIACOES:
                    case ESTRUTURA_JURIDICA_SOCIETARIA_ORGANIZACIONAL:
                    case GRUPO_ECONOMICO:
                    case ORGANOGRAMA_FUNCIONAL:
                    case NOTAS_QUANTITATIVAS_EVOLUCAO:
                    case NOTAS_QUALITATIVAS_EVOLUCAO:
                    case ANEXO_ANALISE_ECONOMICO_FINANCEIRA:
                        adicionarAnexos(copy, apresentacaoVO, secao);
                        break;
                    // Seções de Texto e anexo.
                    case PERFIL:
                    case PROPOSTA_ACOES_CICLO:
                    case CARACTERISTICAS_UNIDADES_ATIVIDADES:
                    case ESTRATEGIAS:
                        adicionarTextos(copy, apresentacaoVO, secao);
                        adicionarAnexos(copy, apresentacaoVO, secao);
                        break;

                    // Seções de texto.

                    case INFORMACOES_OUTROS_DEPARTAMANETOS:
                    case INFORMACOES_OUTROS_ORGAOS:
                    case EQUIPE:
                        adicionarTextos(copy, apresentacaoVO, secao);
                        break;

                    // Slides únicos.
                    case SRC:
                        adicionarRelatorio01(copy, apresentacaoVO, secao);
                        break;
                    case TRABALHOS_REALIZADOS:
                        adicionarRelatorio03(copy, apresentacaoVO, secao);
                        break;
                    case SITUACAO:
                        adicionarRelatorio11(copy, apresentacaoVO, secao);
                        break;
                    case POSICAO_FINANCEIRA_RESULTADOS:
                        adicionarRelatorio13(copy, apresentacaoVO, secao);
                        break;
                    case SINTESE_NOTA_QUANTITATIVA:
                        adicionarRelatorio14(copy, apresentacaoVO, secao);
                        break;
                    case IDENTIFICACAO_UNIDADES_ATIVIDADES:
                        adicionarRelatorio18(copy, apresentacaoVO, secao);
                        break;
                    case ANALISE_QUALITATIVA_POR_RISCOS:
                        adicionarRelatorio20(copy, apresentacaoVO, secao);
                        break;
                    case NOTAS_ARCS:
                        addImpressaoMatriz(copy, apresentacaoVO, secao);
                        break;
                    case NOTA_FINAL_INSTITUICAO:
                        adicionarRelatorio25(copy, apresentacaoVO, secao);
                        break;
                    case PERSPECTIVA_INSTITUICAO:
                        adicionarRelatorio27(copy, apresentacaoVO, secao);
                        break;

                }
            }

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

    private void addImpressaoMatriz(PdfCopyFields copy, ApresentacaoVO apresentacaoVO, SecaoApresentacaoEnum secao) {
        File tempFile;
        BufferedOutputStream bos = null;

        try {
            indice++;
            tempFile = File.createTempFile(RelatorioMatriz.NOME_ARQUIVO_RELATORIO, PDF);
            tempFile.deleteOnExit();
            bos = new BufferedOutputStream(new FileOutputStream(tempFile));
            Document document = new Document(PageSize.A4_LANDSCAPE);
            document.setPageSize(PageSize.A4.rotate());
            PdfWriter writer = PdfWriter.getInstance(document, bos);
            RelatorioMatriz relatorioMatriz = new RelatorioMatriz(perfilRisco, perfilMenu);

            ExportarMatrizPdf export =
                    new ExportarMatrizPdf(relatorioMatriz, paginaAtual, apresentacaoVO, secao, indice, totalSlides);
            writer.setPageEvent(export);
            document.open();
            writer.setMargins(25, 25, 30, 30);

            if (ciclo.getMatriz() != null) {
                PdfPTable table = export.exportarTabelaMatriz();
                document.add(table);

                if (relatorioMatriz.getArcExterno() != null) {
                    PdfPTable tableTitulo = export.exportarTituloTabelaArcExterno();
                    document.add(tableTitulo);
                    
                    PdfPTable tableNota = export.exportarTabelaArcExterno();
                    document.add(tableNota);
                }
            }

            PdfPTable tableNotas = export.exportarTabelaNotas();
            document.add(tableNotas);
            export.addLInhaNotaFinalAjustadaJustificativa(document);

            document.close();

            copy.addDocument(new PdfReader(tempFile.getPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    private void adicionarAnexos(PdfCopyFields copy, ApresentacaoVO apresentacaoVO, SecaoApresentacaoEnum secao) {

        // Declarações
        List<AnexoApresentacaoVO> anexosVO;

        // Recupera os anexos da seção.
        anexosVO = apresentacaoVO.getAnexosVO(secao);
        
        Collections.sort(anexosVO, new Comparator<AnexoApresentacaoVO>() {
            @Override
            public int compare(AnexoApresentacaoVO a1, AnexoApresentacaoVO a2) {
                return a1.getLink().compareToIgnoreCase(a2.getLink());
            }
        });
        if (!anexosVO.isEmpty()) {

            // Monta os slides dos anexos.
            for (AnexoApresentacaoVO anexoVO : anexosVO) {
                try {
                    indice++;
                    RelatorioSlideAnexo relatorio =
                            new RelatorioSlideAnexo(secao, anexoVO, apresentacaoVO, totalSlides, indice);
                    copy.addDocument(new PdfReader(relatorio.gerarRelatorioPDF(paginaAtual)));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (DocumentException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    // Adiciona os textos de uma seção.
    private void adicionarTextos(PdfCopyFields copy, ApresentacaoVO apresentacaoVO, SecaoApresentacaoEnum secao) {

        // Declarações
        List<TextoApresentacaoVO> textosVO;

        // Recupera os anexos da seção.
        textosVO = apresentacaoVO.getTextosVO(secao);

        if (secao.equals(SecaoApresentacaoEnum.PERFIL)) {
            ordenarCampos(textosVO);
        }

        // Verifica se há texto para adicionar.
        if (textosVO.size() > 0) {
            indice++;
            try {
                RelatorioSlideTexto relatorio =
                        new RelatorioSlideTexto(secao, textosVO, apresentacaoVO, totalSlides, indice);
                copy.addDocument(new PdfReader(relatorio.gerarRelatorioPDF(paginaAtual)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (DocumentException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void ordenarCampos(List<TextoApresentacaoVO> textosVO) {
        Comparator<TextoApresentacaoVO> c = new Comparator<TextoApresentacaoVO>() {

            @Override
            public int compare(TextoApresentacaoVO o1, TextoApresentacaoVO o2) {
                return o1.getCampo().getCodigo() - o2.getCampo().getCodigo();
            }

        };

        Collections.sort(textosVO, c);
    }

    private void adicionarRelatorioConstante(PdfCopyFields copy, ApresentacaoVO apresentacaoVO,
            SecaoApresentacaoEnum secao) {
        try {
            indice++;
            RelatorioSlideConstante relatorio = new RelatorioSlideConstante(secao, apresentacaoVO, totalSlides, indice);
            copy.addDocument(new PdfReader(relatorio.gerarRelatorioPDF(paginaAtual)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    private void adicionarRelatorio01(PdfCopyFields copy, ApresentacaoVO apresentacaoVO, SecaoApresentacaoEnum secao) {
        try {
            indice++;
            RelatorioSlide01 relatorio = new RelatorioSlide01(secao, apresentacaoVO, totalSlides, indice, ciclo);
            copy.addDocument(new PdfReader(relatorio.gerarRelatorioPDF(paginaAtual)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    private void adicionarRelatorio20(PdfCopyFields copy, ApresentacaoVO apresentacaoVO, SecaoApresentacaoEnum secao) {
        try {
            indice++;
            RelatorioSlide20 relatorio = new RelatorioSlide20(secao, apresentacaoVO, totalSlides, indice);
            copy.addDocument(new PdfReader(relatorio.gerarRelatorioPDF(paginaAtual)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    private void adicionarRelatorio27(PdfCopyFields copy, ApresentacaoVO apresentacaoVO, SecaoApresentacaoEnum secao) {
        try {
            indice++;
            RelatorioSlide27 relatorio = new RelatorioSlide27(secao, apresentacaoVO, totalSlides, indice);
            copy.addDocument(new PdfReader(relatorio.gerarRelatorioPDF(paginaAtual)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    private void adicionarRelatorio25(PdfCopyFields copy, ApresentacaoVO apresentacaoVO, SecaoApresentacaoEnum secao) {
        try {
            indice++;
            RelatorioSlide25 relatorio = new RelatorioSlide25(secao, apresentacaoVO, totalSlides, indice, perfilRisco, perfilMenu);
            copy.addDocument(new PdfReader(relatorio.gerarRelatorioPDF(paginaAtual)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    private void adicionarRelatorio13(PdfCopyFields copy, ApresentacaoVO apresentacaoVO, SecaoApresentacaoEnum secao) {
        try {
            QuadroPosicaoFinanceiraVO quadroVO = PerfilRiscoMediator.get().obterQuadroVigente(perfilRisco);
            indice++;
            if (quadroVO.getPk() != null) {
                RelatorioSlide13 relatorio =
                        new RelatorioSlide13(secao, apresentacaoVO, totalSlides, indice, perfilRisco, quadroVO);
                copy.addDocument(JuncaoPDFMap.gerarPDF(relatorio.gerarRelatorioPDF(paginaAtual),
                        relatorio.getMapAnexo()));

            } else {
                RelatorioSlideVazio relatorio = new RelatorioSlideVazio(secao, apresentacaoVO, totalSlides, indice);
                copy.addDocument(new PdfReader(relatorio.gerarRelatorioPDF(paginaAtual)));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    private void adicionarRelatorio14(PdfCopyFields copy, ApresentacaoVO apresentacaoVO, SecaoApresentacaoEnum secao) {
        try {
            indice++;
            RelatorioSlide14 relatorio =
                    new RelatorioSlide14(secao, apresentacaoVO, totalSlides, indice, perfilRisco, perfilMenu);
            copy.addDocument(new PdfReader(relatorio.gerarRelatorioPDF(paginaAtual)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    private void adicionarRelatorio11(PdfCopyFields copy, ApresentacaoVO apresentacaoVO, SecaoApresentacaoEnum secao) {
        try {
            if (apresentacaoVO.getSituacaoNormalidade() == null
                    || apresentacaoVO.getSituacaoNormalidade() == NormalidadeEnum.NORMAL) {
                return;
            } else {
                indice++;
                RelatorioSlide11 relatorio = new RelatorioSlide11(secao, apresentacaoVO, totalSlides, indice);
                copy.addDocument(new PdfReader(relatorio.gerarRelatorioPDF(paginaAtual)));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    private void adicionarRelatorio03(PdfCopyFields copy, ApresentacaoVO apresentacaoVO, SecaoApresentacaoEnum secao) {
        try {
            indice++;
            RelatorioSlide03 relatorio =
                    new RelatorioSlide03(secao, apresentacaoVO, totalSlides, indice, perfilRisco, ciclo);
            copy.addDocument(new PdfReader(relatorio.gerarRelatorioPDF(paginaAtual)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }

    }

    private void adicionarRelatorio18(PdfCopyFields copy, ApresentacaoVO apresentacaoVO, SecaoApresentacaoEnum secao) {
        try {
            indice++;
            RelatorioSlide18 relatorio = new RelatorioSlide18(secao, apresentacaoVO, totalSlides, indice);
            copy.addDocument(new PdfReader(relatorio.gerarRelatorioPDF(paginaAtual)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }

    }

}
