package br.gov.bcb.sisaps.web.page.dominio.matriz.relatorio;

import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFHeader;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;
import org.apache.wicket.protocol.http.WebApplication;

import br.gov.bcb.wicket.pages.AbstractBacenWebPage;

public class ExportacaoXlsHelper {

    protected static final int LINHA_BANCO_CENTRAL_BRASIL = 0;
    protected static final int LINHA_BRANCO_APOS_BCB = 2;
    protected static final int LINHA_DEPARTAMENTO = 3;
    protected static final int LINHA_ASSUNTO = 4;
    protected static final int LINHA_NOME_ES = 5;
    protected static final int LINHA_TITULO_MATRIZ = 6;
    protected static final int LINHA_BRANCO_APOS_TITULO_MATRIZ = 7;
    protected static final int COLUNA_ZERO = 0;
    protected static final int COLUNA_UM = 1;
    protected static final short ALTURA_LINHA = (short) 305;
    protected static final short NEGRITO = (short) 700;
    protected static final short TAMANHO_DA_FONTE_8 = (short) 8;
    protected static final short TAMANHO_DA_FONTE_10 = (short) 10;
    protected static final short TAMANHO_DA_FONTE_12 = (short) 12;

    protected Workbook planilha;
    protected Sheet aba;
    protected int numeroDaLinhaAtual;

    private CellStyle estilo;

    public ExportacaoXlsHelper() {
        planilha = new HSSFWorkbook();
        aba = planilha.createSheet("Matriz");
        estilo = ExportacaoXlsHelperUtil.get().criarEstilo((HSSFWorkbook) planilha);
        inserirPaginacao();
    }

    protected Row criarLinha(int indiceDaLinha) {
        return criarLinha(indiceDaLinha, null);
    }
    
    protected Row criarLinha(int indiceDaLinha, Short altura) {
        Row linha = aba.createRow(indiceDaLinha);
        if (altura != null) {
            linha.setHeight(altura);
        }
        return linha;
    }
    
    protected void criarCelulas(Row linhaInicial, int colunaInicial, int colunaFinal, String valor) {
        criarCelulas(linhaInicial, linhaInicial, colunaInicial, colunaFinal, valor, null);
    }

    protected void criarCelulas(Row linhaInicial, int colunaInicial, int colunaFinal, String valor, CellStyle estilo) {
        criarCelulas(linhaInicial, linhaInicial, colunaInicial, colunaFinal, valor, estilo);
    }
    
    protected void criarCelulas(Row linhaInicial, Row linhaFinal, int colunaInicial, int colunaFinal, String valor) {
        criarCelulas(linhaInicial, linhaFinal, colunaInicial, colunaFinal, valor, null);
    }
    
    protected void criarCelulas(Row linhaInicial, Row linhaFinal, int colunaInicial, int colunaFinal, 
            String valor, CellStyle estilo) {
        for (int coluna = colunaInicial; coluna <= colunaFinal; coluna++) {
            Cell celula = criarCelula(linhaInicial, coluna, estilo);

            if (coluna == colunaInicial) {
                celula.setCellValue(valor);
            }
        }

        aba.addMergedRegion(new CellRangeAddress(linhaInicial.getRowNum(), linhaFinal.getRowNum(), 
                colunaInicial, colunaFinal));
    }
    
    protected Cell criarCelula(Row linha, int coluna, String valor) {
        return criarCelula(linha, coluna, valor, null);
    }

    protected Cell criarCelula(Row linha, int coluna, String valor, CellStyle estilo) {
        Cell celula = criarCelula(linha, coluna, estilo);
        celula.setCellValue(valor);
        return celula;
    }

    private Cell criarCelula(Row linha, int coluna, CellStyle estilo) {
        Cell celula = linha.createCell(coluna);
        if (estilo == null) {
            celula.setCellStyle(this.estilo);
        } else {
            celula.setCellStyle(estilo);
        }
        return celula;
    }
    
    protected void inserirLogo(AbstractBacenWebPage paginaAtual) throws IOException {
        WebApplication wa = (WebApplication) paginaAtual.getApplication();
        InputStream imgLogo = wa.getServletContext().getResourceAsStream("/relatorios/logoBacenRelatorioMatriz.png");
        byte[] bytes = IOUtils.toByteArray(imgLogo);
        int pictureIdx = planilha.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
        imgLogo.close();
        //Returns an object that handles instantiating concrete classes
        CreationHelper helper = planilha.getCreationHelper();
        //Creates the top-level drawing patriarch.
        Drawing drawing = aba.createDrawingPatriarch();
        //Create an anchor that is attached to the worksheet
        ClientAnchor anchor = helper.createClientAnchor();
        //set top-left corner for the image
        anchor.setCol1(0);
        anchor.setRow1(0);
        //Creates a picture
        Picture pict = drawing.createPicture(anchor, pictureIdx);
        //Reset the image to the original size
        pict.resize();
    }
    
    protected CellStyle getEstiloDepartamento() {
        CellStyle estilo = ExportacaoXlsHelperUtil.get().criarEstilo((HSSFWorkbook) planilha);
        Font fonte = ExportacaoXlsHelperUtil.get().criarFonte((HSSFWorkbook) planilha);
        fonte.setBoldweight(NEGRITO);
        fonte.setFontHeightInPoints(TAMANHO_DA_FONTE_12);
        estilo.setFont(fonte);
        estilo.setBorderBottom(CellStyle.BORDER_THIN);
        return estilo;
    }

    protected CellStyle getEstiloAssunto() {
        CellStyle estilo = ExportacaoXlsHelperUtil.get().criarEstilo((HSSFWorkbook) planilha);
        Font fonte = ExportacaoXlsHelperUtil.get().criarFonte((HSSFWorkbook) planilha);
        fonte.setFontHeightInPoints(TAMANHO_DA_FONTE_8);
        estilo.setFont(fonte);
        return estilo;
    }

    protected CellStyle getEstiloNomeES() {
        CellStyle estilo = ExportacaoXlsHelperUtil.get().criarEstilo((HSSFWorkbook) planilha);
        Font fonte = ExportacaoXlsHelperUtil.get().criarFonte((HSSFWorkbook) planilha);
        fonte.setFontHeightInPoints(TAMANHO_DA_FONTE_12);
        estilo.setFont(fonte);
        return estilo;
    }

    protected CellStyle getEstiloTituloMatriz() {
        CellStyle estilo = ExportacaoXlsHelperUtil.get().criarEstilo((HSSFWorkbook) planilha);
        Font fonte = ExportacaoXlsHelperUtil.get().criarFonte((HSSFWorkbook) planilha);
        fonte.setFontHeightInPoints(TAMANHO_DA_FONTE_12);
        estilo.setFont(fonte);
        estilo.setBorderBottom(CellStyle.BORDER_MEDIUM);
        return estilo;
    }

    private void inserirPaginacao() {
        aba.getHeader().setLeft(
                "Informação protegida por sigilo legal, nos termos da Lei Complementar nº 105, de 10 de janeiro de 2001, e/ou de acesso restrito, nos termos do art. 5º do Decreto nº 7.724, de 16 de maio de 2012.");
        aba.getFooter().setLeft(
                "Informação protegida por sigilo legal, nos termos da Lei Complementar nº 105, de 10 de janeiro de 2001, e/ou de acesso restrito, nos termos do art. 5º do Decreto nº 7.724, de 16 de maio de 2012."
                        + "                               " + HSSFHeader.page() + " / "
                        + HSSFHeader.numPages());
    }

}
