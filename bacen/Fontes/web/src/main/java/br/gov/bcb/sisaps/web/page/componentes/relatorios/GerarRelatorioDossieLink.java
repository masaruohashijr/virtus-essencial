package br.gov.bcb.sisaps.web.page.componentes.relatorios;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
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
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

import br.gov.bcb.sisaps.src.dominio.AnexoPosCorec;
import br.gov.bcb.sisaps.src.dominio.Atividade;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.CelulaRiscoControle;
import br.gov.bcb.sisaps.src.dominio.ParametroGrupoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoAQTEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoARCEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.mediator.AnexoPosCorecMediator;
import br.gov.bcb.sisaps.src.mediator.CelulaRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.ParametroGrupoRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.src.vo.analisequantitativa.QuadroPosicaoFinanceiraVO;
import br.gov.bcb.sisaps.web.page.DefaultPage;
import br.gov.bcb.sisaps.web.page.componentes.util.ByteVO;
import br.gov.bcb.sisaps.web.page.componentes.util.RelatorioAbstrato;
import br.gov.bcb.sisaps.web.page.dominio.agenda.ExportarAtaCorecPdf;
import br.gov.bcb.sisaps.web.page.dominio.agenda.GerarAtaCorec;
import br.gov.bcb.sisaps.web.page.dominio.analisequantitativa.relatorios.RelatorioAnaliseQuantitativa;
import br.gov.bcb.sisaps.web.page.dominio.aqt.relatorio.RelatorioAQTMatriz;
import br.gov.bcb.sisaps.web.page.dominio.aqt.relatorio.RelatorioDetalhesAQTMatriz;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.relatorioArc.JuncaoPDFMap;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.relatorioArc.RelatorioArcMatriz;
import br.gov.bcb.sisaps.web.page.dominio.gerenciaes.relatorio.RelatorioDetalharES;
import br.gov.bcb.sisaps.web.page.dominio.gerenciarnotasintese.relatorioSinteses.RelatorioSinteses;
import br.gov.bcb.sisaps.web.page.dominio.matriz.relatorio.ExportarMatrizPdf;
import br.gov.bcb.sisaps.web.page.dominio.matriz.relatorio.RelatorioMatriz;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.export.SimpleExporterInput;

public class GerarRelatorioDossieLink extends DownloadLink {

    private static final String CURRENTPAGE = "CURRENTPAGE";
    private static final String TOTALPAGE = "TOTALPAGE";
    private static final String PDF = ".pdf";
    private final PerfilRisco perfilRisco;
    private final DefaultPage paginaAtual;
    private final PerfilAcessoEnum perfilMenu;
    private final PerfilRisco perfilRiscoAtual;
    private RelatorioMatriz relatorioMatriz;
    private GerarAtaCorec gerarAtaCorec;

    public GerarRelatorioDossieLink(String id, File file, int perfilRisco, DefaultPage paginaAtual) {
        super(id, file);
        this.perfilRisco = PerfilRiscoMediator.get().obterPerfilRiscoPorPk(perfilRisco);
        this.paginaAtual = paginaAtual;
        this.perfilMenu = paginaAtual.getPerfilPorPagina();
        this.perfilRiscoAtual = PerfilRiscoMediator.get().obterPerfilRiscoAtual(this.perfilRisco.getCiclo().getPk());
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
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
            tempFile = File.createTempFile("Dossie", PDF);
            bos = new BufferedOutputStream(new FileOutputStream(tempFile)); //Criamos o arquivo  
            copy = new PdfCopyFields(bos);
            List<ByteVO> listaBytesVOTemp = new ArrayList<ByteVO>();
            List<ByteVO> listaBytesVOExport = null;
            List<JasperPrint> jasperPrintList = new ArrayList<JasperPrint>();
            boolean possuiMatriz = false;
            boolean possuiAtaCorec = false;
            // Detalhes ES
            if (perfilRisco.getCiclo().getEntidadeSupervisionavel() != null) {
                listaBytesVOTemp.add(montarRelatorioDetalhesES(jasperPrintList));
            }
            if (perfilRisco.getCiclo().getMatriz() != null) {
                possuiMatriz = true;
                listaBytesVOTemp.add(montarRelatorioMatriz(jasperPrintList));
                // Sinteses
                listaBytesVOTemp.add(montarRelatorioSinteses(jasperPrintList));
                // ARCs
                listaBytesVOTemp.addAll(montarArcsConcluidosDoPerfil(jasperPrintList));
            }
            if (PerfilRiscoMediator.get().getAnalisesQuantitativasAQTPerfilRisco(perfilRisco) != null) {
                // Sinteses AQT
                listaBytesVOTemp.add(montarImpressaoSintesesAQT(jasperPrintList));
                // Anefs
                listaBytesVOTemp.addAll(montarANEFSConcluidosDoPerfil(jasperPrintList));
            }
            ByteVO voAnalise = montarImpressaoAnaliseQuantitativa(jasperPrintList);
            if (voAnalise != null) {
                listaBytesVOTemp.add(voAnalise);
            }
            // Ata Corec
            if (CicloMediator.get().cicloPosCorecEncerrado(perfilRisco.getCiclo())) {
                possuiAtaCorec = true;
                listaBytesVOTemp.add(montarAtaCorec(jasperPrintList));
            }
            // Criando lista VO Export
            for (ByteVO voTemp : listaBytesVOTemp) {
                listaBytesVOExport = correctPageNumbers(jasperPrintList, voTemp, possuiMatriz, possuiAtaCorec);
            }
            // Exportando relatórios
            for (int i = 0; i < listaBytesVOTemp.size(); i++) {
                ByteVO voExport = listaBytesVOExport.get(i);
                RelatorioAbstrato relatorio = listaBytesVOTemp.get(i).getRelatorio();
                if (perfilRisco.getCiclo().getEntidadeSupervisionavel() != null) {
                    if (relatorio instanceof RelatorioDetalharES) {
                        RelatorioDetalharES relatorioDetalhesES = (RelatorioDetalharES) relatorio;
                        byte[] arquivo = addRelatorio(voExport, relatorioDetalhesES);
                        copy.addDocument(JuncaoPDFMap.gerarPDF(arquivo, relatorioDetalhesES.getMapAnexo()));
                    }
                }
                if (perfilRisco.getCiclo().getMatriz() != null) {
                    RelatorioMatriz relatorioMatriz = listaBytesVOTemp.get(i).getRelatorioMatriz();
                    // Matriz
                    if (relatorioMatriz != null) {
                        addImpressaoMatriz(copy, voExport.getQtdPaginasAtual(), voExport.getQtdPaginasTotal());
                    }
                    // Sinteses
                    if (relatorio instanceof RelatorioSinteses) {
                        RelatorioSinteses relatorioSinteses = (RelatorioSinteses) relatorio;
                        byte[] arquivoSinteses = addRelatorio(voExport, relatorioSinteses);
                        copy.addDocument(new PdfReader(arquivoSinteses));
                    }
                    // ARCs
                    if (relatorio instanceof RelatorioArcMatriz) {
                        RelatorioArcMatriz relatorioARCs = (RelatorioArcMatriz) relatorio;
                        byte[] arquivoArcs = addRelatorio(voExport, relatorioARCs);
                        copy.addDocument(JuncaoPDFMap.gerarPDF(arquivoArcs, relatorioARCs.getMapAnexoElemento()));
                    }
                    // Sinteses Anef
                    if (relatorio instanceof RelatorioDetalhesAQTMatriz) {
                        RelatorioDetalhesAQTMatriz relatorioSintesesAQT = (RelatorioDetalhesAQTMatriz) relatorio;
                        byte[] arquivoSintesesAnefs = addRelatorio(voExport, relatorioSintesesAQT);
                        copy.addDocument(new PdfReader(arquivoSintesesAnefs));
                    }
                    // Anefs
                    if (relatorio instanceof RelatorioAQTMatriz) {
                        RelatorioAQTMatriz relatorioAQT = (RelatorioAQTMatriz) relatorio;
                        byte[] arquivoAnefs = addRelatorio(voExport, relatorioAQT);
                        copy.addDocument(JuncaoPDFMap.gerarPDF(arquivoAnefs, relatorioAQT.getMapAnexoElemento()));
                    }
                    // Análise quantitativa
                    if (relatorio instanceof RelatorioAnaliseQuantitativa) {
                        RelatorioAnaliseQuantitativa relatorioAnaliseQuantitativa =
                                (RelatorioAnaliseQuantitativa) relatorio;
                        byte[] arquivoAnalise = addRelatorio(voExport, relatorioAnaliseQuantitativa);
                        copy.addDocument(
                                JuncaoPDFMap.gerarPDF(arquivoAnalise, relatorioAnaliseQuantitativa.getMapAnexo()));
                    }
                    if (CicloMediator.get().cicloPosCorecEncerrado(perfilRisco.getCiclo())) {
                        GerarAtaCorec ataCorec = listaBytesVOTemp.get(i).getGerarAtaCorec();
                        if (ataCorec != null) {
                            addImpressaoAtaCorec(copy, voExport.getQtdPaginasAtual(), voExport.getQtdPaginasTotal());
                        }
                    }
                }
            }
            if (CicloMediator.get().cicloPosCorecEncerrado(perfilRisco.getCiclo())) {
                addImpressaoOfficioCorec(copy);
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
    
    private ByteVO montarRelatorioDetalhesES(List<JasperPrint> jasperPrintList) {
        RelatorioDetalharES relatorio =
                new RelatorioDetalharES(perfilRisco.getCiclo(), perfilRisco, perfilMenu, paginaAtual);
        ByteVO byteVO = relatorio.montarDossiePDF(paginaAtual, relatorio);
        jasperPrintList.add(byteVO.getJasperPrint());
        return byteVO;
    }

    private ByteVO montarAtaCorec(List<JasperPrint> jasperPrintList) {
        gerarAtaCorec = new GerarAtaCorec(perfilRisco.getCiclo());
        JasperPrint jp = new JasperPrint();
        jp.setName("relatorioAtaCorecTeste");
        jasperPrintList.add(jp);
        ByteVO byteVO = new ByteVO();
        byteVO.setGerarAtaCorec(gerarAtaCorec);
        return byteVO;
    }

    private ByteVO montarRelatorioMatriz(List<JasperPrint> jasperPrintList) {
        relatorioMatriz = new RelatorioMatriz(perfilRisco, paginaAtual.getPerfilPorPagina());
        JasperPrint jp = new JasperPrint();
        jp.setName("relatorioMatrizTeste");
        jasperPrintList.add(jp);
        ByteVO byteVO = new ByteVO();
        byteVO.setRelatorioMatriz(relatorioMatriz);
        return byteVO;
    }

    private byte[] addRelatorio(ByteVO vo, RelatorioAbstrato relatorio) {
        return relatorio.gerarDossiePDF(vo).toByteArray();
    }

    private ByteVO montarRelatorioSinteses(List<JasperPrint> jasperPrintList) {
        List<Integer> idsParametrosGrupoRiscoMatriz = ParametroGrupoRiscoControleMediator.get()
                .buscarIdsGruposRiscoDaMatriz(perfilRisco.getCiclo().getMatriz());
        List<ParametroGrupoRiscoControle> lista = ParametroGrupoRiscoControleMediator.get()
                .buscarGruposRiscoDaMatrizESinteseObrigatoria(idsParametrosGrupoRiscoMatriz);
        if (relatorioMatriz.getArcExterno() != null) {
            lista.add(relatorioMatriz.getArcExterno().getParametroGrupoRiscoControle());
        }
        RelatorioSinteses relatorio =
                new RelatorioSinteses(lista, perfilRisco.getCiclo().getMatriz(), perfilRisco,
                perfilMenu, false);
        ByteVO byteVO = relatorio.montarDossiePDF(paginaAtual, relatorio);
        jasperPrintList.add(byteVO.getJasperPrint());
        return byteVO;
    }

    private void addImpressaoOfficioCorec(PdfCopyFields copy) {
        try {
            AnexoPosCorec anexo =
                    AnexoPosCorecMediator.get().buscarAnexo(perfilRisco.getCiclo(), AnexoPosCorecMediator.OFICIO);
            if (anexo != null) {
                copy.addDocument(new PdfReader(AnexoPosCorecMediator.get().recuperarArquivo(anexo.getLink(),
                        perfilRisco.getCiclo(), anexo.getTipo())));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    private void addImpressaoAtaCorec(PdfCopyFields copy, int qtdPaginasAtual, int qtdPaginasTotal) {
        File tempFile;
        BufferedOutputStream bos = null;
        try {
            AnexoPosCorec anexo =
                    AnexoPosCorecMediator.get().buscarAnexo(perfilRisco.getCiclo(), AnexoPosCorecMediator.ATA);
            if (anexo != null) {
                copy.addDocument(new PdfReader(AnexoPosCorecMediator.get().recuperarArquivo(anexo.getLink(),
                        perfilRisco.getCiclo(), anexo.getTipo())));
            } else {
                tempFile = File.createTempFile("Ata Corec", PDF);
                tempFile.deleteOnExit();
                bos = new BufferedOutputStream(new FileOutputStream(tempFile));
                Document document = new Document(PageSize.A4);
                document.setPageSize(PageSize.A4);
                PdfWriter writer = PdfWriter.getInstance(document, bos);
                ExportarAtaCorecPdf export =
                        new ExportarAtaCorecPdf(gerarAtaCorec, paginaAtual, qtdPaginasAtual, qtdPaginasTotal, true);
                writer.setPageEvent(export);
                document.open();
                writer.setMargins(25, 25, 30, 30);
                export.addDados(document);
                document.close();
                copy.addDocument(new PdfReader(tempFile.getPath()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    private void addImpressaoMatriz(PdfCopyFields copy, int qtdPaginasAtual, int qtdPaginasTotal) {
        File tempFile;
        BufferedOutputStream bos = null;
        try {
            tempFile = File.createTempFile(RelatorioMatriz.NOME_ARQUIVO_RELATORIO, PDF);
            tempFile.deleteOnExit();
            bos = new BufferedOutputStream(new FileOutputStream(tempFile));
            Document document = new Document(PageSize.A4_LANDSCAPE);
            document.setPageSize(PageSize.A4.rotate());
            PdfWriter writer = PdfWriter.getInstance(document, bos);
            ExportarMatrizPdf export =
                    new ExportarMatrizPdf(relatorioMatriz, paginaAtual, qtdPaginasAtual, qtdPaginasTotal, true);
            writer.setPageEvent(export);
            document.open();
            writer.setMargins(25, 25, 30, 30);
            PdfPTable table = export.exportarTabelaMatriz();
            document.add(table);
            if (export.possuiArcExterno()) {
                PdfPTable tableTituloARCExterno = export.exportarTituloTabelaArcExterno();
                document.add(tableTituloARCExterno);

                PdfPTable tableARCExterno = export.exportarTabelaArcExterno();
                document.add(tableARCExterno);
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

    private ByteVO montarImpressaoAnaliseQuantitativa(List<JasperPrint> jasperPrintList) {
        QuadroPosicaoFinanceiraVO quadroVO = PerfilRiscoMediator.get().obterQuadroVigente(perfilRisco);
        ByteVO byteVO = null;
        if (quadroVO.getPk() != null) {
            RelatorioAnaliseQuantitativa relatorio = new RelatorioAnaliseQuantitativa(perfilRisco, quadroVO);
            byteVO = relatorio.montarDossiePDF(paginaAtual, relatorio);
            jasperPrintList.add(byteVO.getJasperPrint());
        }
        return byteVO;
    }

    private List<ByteVO> montarArcsConcluidosDoPerfil(List<JasperPrint> jasperPrintList) {
        List<CelulaRiscoControle> listaCelula =
                CelulaRiscoControleMediator.get().buscarCelulaPorVersaoPerfil(perfilRisco.getVersoesPerfilRisco());
        List<ByteVO> bytesVO = new ArrayList<ByteVO>();
        for (CelulaRiscoControle celula : listaCelula) {
            AvaliacaoRiscoControle riscoVigente = celula.getArcRisco().getAvaliacaoRiscoControleVigente();
            if (riscoVigente != null && EstadoARCEnum.CONCLUIDO.equals(riscoVigente.getEstado())) {
                bytesVO.add(montarRelatorioParaAvaliacao(jasperPrintList, celula.getAtividade(), riscoVigente,
                        celula.getArcRisco()));
            }
            AvaliacaoRiscoControle controleVigente = celula.getArcControle().getAvaliacaoRiscoControleVigente();
            if (controleVigente != null && EstadoARCEnum.CONCLUIDO.equals(controleVigente.getEstado())) {
                bytesVO.add(montarRelatorioParaAvaliacao(jasperPrintList, celula.getAtividade(), controleVigente,
                        celula.getArcControle()));
            }
        }
        if (relatorioMatriz.getMatriz().getPercentualGovernancaCorporativoInt() > 0) {
            AvaliacaoRiscoControle arcExterno = PerfilRiscoMediator.get().getArcExterno(perfilRisco);
            if (arcExterno.getAvaliacaoRiscoControleVigente() != null) {
                bytesVO.add(montarRelatorioParaAvaliacao(jasperPrintList, null,
                        arcExterno.getAvaliacaoRiscoControleVigente(), arcExterno));
            }
        }
        return bytesVO;
    }

    private List<ByteVO> montarANEFSConcluidosDoPerfil(List<JasperPrint> jasperPrintList) {
        int titulo = 0;
        List<AnaliseQuantitativaAQT> listaAnefs =
                AnaliseQuantitativaAQTMediator.get().buscarAQTsPerfilRisco(perfilRisco.getVersoesPerfilRisco());
        List<ByteVO> bytesVO = new ArrayList<ByteVO>();
        for (AnaliseQuantitativaAQT anef : listaAnefs) {
            titulo++;
            if (EstadoAQTEnum.CONCLUIDO.equals(anef.getEstado())) {
                bytesVO.add(montarRelatorioANEFS(jasperPrintList, anef, titulo + "."));
            }
        }
        return bytesVO;
    }

    private ByteVO montarRelatorioANEFS(List<JasperPrint> jasperPrintList, AnaliseQuantitativaAQT anefs,
            String inicioTitulo) {
        RelatorioAQTMatriz relatorio = new RelatorioAQTMatriz(anefs, anefs.getCiclo(), inicioTitulo, perfilMenu,
                perfilRiscoAtual.getPk().equals(perfilRisco.getPk()));
        ByteVO byteVO = relatorio.montarDossiePDF(paginaAtual, relatorio);
        jasperPrintList.add(byteVO.getJasperPrint());
        return byteVO;
    }

    private ByteVO montarRelatorioParaAvaliacao(List<JasperPrint> jasperPrintList, Atividade atv,
            AvaliacaoRiscoControle arcVigente, AvaliacaoRiscoControle arcPerfil) {
        RelatorioArcMatriz relatorio = new RelatorioArcMatriz(arcVigente, arcPerfil, perfilRisco.getCiclo(), atv,
                perfilMenu, perfilRiscoAtual.getPk().equals(perfilRisco.getPk()));
        ByteVO byteVO = relatorio.montarDossiePDF(paginaAtual, relatorio);
        jasperPrintList.add(byteVO.getJasperPrint());
        return byteVO;
    }

    private ByteVO montarImpressaoSintesesAQT(List<JasperPrint> jasperPrintList) {
        RelatorioDetalhesAQTMatriz relatorio = new RelatorioDetalhesAQTMatriz(perfilRisco, perfilMenu);
        ByteVO byteVO = relatorio.montarDossiePDF(paginaAtual, relatorio);
        jasperPrintList.add(byteVO.getJasperPrint());
        return byteVO;
    }

    private List<ByteVO> correctPageNumbers(List<JasperPrint> jasperPrintList, ByteVO byteVOTemp, boolean possuiMatriz,
            boolean possuiAtaCorec) {
        List<ByteVO> listaExport = new ArrayList<ByteVO>();
        //First loop on all reports to get totale page number
        int totPageNumber = 0;
        for (JasperPrint jp : jasperPrintList) {
            totPageNumber += jp.getPages().size();
        }
        if (possuiMatriz) {
            totPageNumber += 1;
        }
        if (possuiAtaCorec) {
            GerarAtaCorec gerarAtaCorec = byteVOTemp.getGerarAtaCorec();
            if (gerarAtaCorec != null && CollectionUtils.isNotEmpty(gerarAtaCorec.getListaArcs())
                    && gerarAtaCorec.getListaArcs().size() > 10) {
                totPageNumber += 3;
            } else {
                totPageNumber += 2;
            }
        }
        //Second loop all reports to replace our markers with current and total number
        int currentPage = 1;
        for (JasperPrint jp : jasperPrintList) {
            // Relatório matriz
            ByteVO vo = new ByteVO();
            if (StringUtils.isNotBlank(jp.getName()) && jp.getName().equals("relatorioMatrizTeste")) {
                vo.setRelatorioMatriz(byteVOTemp.getRelatorioMatriz());
                vo.setQtdPaginasTotal(totPageNumber);
                vo.setQtdPaginasAtual(currentPage);
                listaExport.add(vo);
                currentPage++;
            } else if (StringUtils.isNotBlank(jp.getName()) && jp.getName().equals("relatorioAtaCorecTeste")) {
                vo.setGerarAtaCorec(byteVOTemp.getGerarAtaCorec());
                vo.setQtdPaginasTotal(totPageNumber);
                vo.setQtdPaginasAtual(currentPage);
                listaExport.add(vo);
                currentPage++;
            } else {
                List<JRPrintPage> pages = jp.getPages();
                //Loop all pages of report
                for (JRPrintPage jpp : pages) {
                    List<JRPrintElement> elements = jpp.getElements();
                    //Loop all elements on page
                    for (JRPrintElement jpe : elements) {
                        //Check if text element
                        if (jpe instanceof JRPrintText) {
                            JRPrintText jpt = (JRPrintText) jpe;
                            //Check if current page marker
                            if (CURRENTPAGE.equals(jpt.getValue())) {
                                jpt.setText(String.valueOf(currentPage)); //Replace marker
                            }
                            //Check if totale page marker
                            if (TOTALPAGE.equals(jpt.getValue())) {
                                jpt.setText(String.valueOf(totPageNumber)); //Replace marker
                            }
                        }
                    }
                    currentPage++;
                }
                vo.setJasperPrint(jp);
                vo.setConfiguration(byteVOTemp.getConfiguration());
                vo.setRelatorio(byteVOTemp.getRelatorio());
                vo.setPrint(new SimpleExporterInput(jp));
                vo.setQtdPaginasTotal(totPageNumber);
                vo.setQtdPaginasAtual(currentPage);
                listaExport.add(vo);
            }
        }
        return listaExport;
    }
}
