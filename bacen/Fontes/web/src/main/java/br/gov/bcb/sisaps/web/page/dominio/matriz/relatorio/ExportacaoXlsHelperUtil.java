package br.gov.bcb.sisaps.web.page.dominio.matriz.relatorio;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;

public class ExportacaoXlsHelperUtil {

    private static ExportacaoXlsHelperUtil instancia = new ExportacaoXlsHelperUtil();

    private static final short TAMANHO_DA_FONTE = (short) 10;

    protected ExportacaoXlsHelperUtil() {
        super();
    }

    public static ExportacaoXlsHelperUtil get() {
        return instancia;
    }

    public CellStyle criarEstilo(HSSFWorkbook planilha) {
        CellStyle estilo = planilha.createCellStyle();
        estilo.setAlignment(CellStyle.ALIGN_LEFT);
        estilo.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        estilo.setFillForegroundColor(HSSFColor.WHITE.index);
        estilo.setFillPattern(CellStyle.SOLID_FOREGROUND);
        estilo.setWrapText(true);
        estilo.setFont(criarFonte(planilha));
        return estilo;
    }

    public Font criarFonte(HSSFWorkbook planilha) {
        Font fonte = planilha.createFont();
        fonte.setColor(HSSFColor.BLACK.index);
        fonte.setFontHeightInPoints(TAMANHO_DA_FONTE);
        fonte.setFontName("Times New Roman");
        return fonte;
    }
    
    public void addBordasFinas(CellStyle estilo) {
        estilo.setBorderLeft(CellStyle.BORDER_THIN);
        estilo.setBorderBottom(CellStyle.BORDER_THIN);
        estilo.setBorderRight(CellStyle.BORDER_THIN);
        estilo.setBorderTop(CellStyle.BORDER_THIN);
    }

}
