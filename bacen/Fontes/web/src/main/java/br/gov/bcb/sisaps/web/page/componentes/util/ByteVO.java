package br.gov.bcb.sisaps.web.page.componentes.util;

import br.gov.bcb.sisaps.web.page.dominio.agenda.GerarAtaCorec;
import br.gov.bcb.sisaps.web.page.dominio.matriz.relatorio.RelatorioMatriz;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;

public class ByteVO {

    private SimplePdfExporterConfiguration configuration;
    private SimpleExporterInput print;
    private JasperPrint jasperPrint;
    private RelatorioAbstrato relatorio;
    private int qtdPaginasTotal;
    private int qtdPaginasAtual;
    private RelatorioMatriz relatorioMatriz;
    private GerarAtaCorec gerarAtaCorec;

    public SimplePdfExporterConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(SimplePdfExporterConfiguration configuration) {
        this.configuration = configuration;
    }

    public SimpleExporterInput getPrint() {
        return print;
    }

    public void setPrint(SimpleExporterInput print) {
        this.print = print;
    }

    public JasperPrint getJasperPrint() {
        return jasperPrint;
    }

    public void setJasperPrint(JasperPrint jasperPrint) {
        this.jasperPrint = jasperPrint;
    }

    public RelatorioAbstrato getRelatorio() {
        return relatorio;
    }

    public void setRelatorio(RelatorioAbstrato relatorio) {
        this.relatorio = relatorio;
    }

    public int getQtdPaginasTotal() {
        return qtdPaginasTotal;
    }

    public void setQtdPaginasTotal(int qtdPaginasTotal) {
        this.qtdPaginasTotal = qtdPaginasTotal;
    }

    public int getQtdPaginasAtual() {
        return qtdPaginasAtual;
    }

    public void setQtdPaginasAtual(int qtdPaginasAtual) {
        this.qtdPaginasAtual = qtdPaginasAtual;
    }

    public RelatorioMatriz getRelatorioMatriz() {
        return relatorioMatriz;
    }

    public void setRelatorioMatriz(RelatorioMatriz relatorioMatriz) {
        this.relatorioMatriz = relatorioMatriz;
    }

    public GerarAtaCorec getGerarAtaCorec() {
        return gerarAtaCorec;
    }

    public void setGerarAtaCorec(GerarAtaCorec gerarAtaCorec) {
        this.gerarAtaCorec = gerarAtaCorec;
    }

}
