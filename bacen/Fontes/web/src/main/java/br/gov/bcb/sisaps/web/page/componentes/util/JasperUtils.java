/*
 * Sistema: AUD.
 * 
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software e confidencial e propriedade do Banco Central do Brasil.
 * Nao e permitida sua distribuicao ou divulgacao do seu conteudo sem
 * expressa autorizacao do Banco Central.
 * Este arquivo contem informac�es proprietarias.
 */

package br.gov.bcb.sisaps.web.page.componentes.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import br.gov.bcb.sisaps.util.validacao.InfraException;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;

/**
 * Classe utilit�ria para exporta��o dos relat�rios do Jasper.
 * 
 */
public final class JasperUtils {

    /**
     * 
     * Construtor para a classe ExpectativasJasperUtils.
     */
    private JasperUtils() {

    }

    /**
     * Retorna o DataSource do relat�rio.
     * 
     * @param dados fonte de dados que ser�o utilizados para criacao do relat�rio.
     * @return O datasource do relat�rio.
     */
    private static JRDataSource getReportDataSource(List<?> dados) {
        JRDataSource dataSource;

        if (dados == null) {
            dataSource = new JREmptyDataSource();
        } else {
            dataSource = new JRBeanCollectionDataSource(dados);
        }
        return dataSource;
    }

    /**
     * Exporta o relat�rio em pdf para um outputStream.
     * 
     * @param arquivoJasper Arquivo Jasper.
     * @param dados Datasource do relat�rio.
     * @param reportParameters Par�metros do relat�rio.
     * @param title T�tulo que ser� colocado no metadata do pdf.
     * @return {@link ByteArrayOutputStream} que conter� os bytes do relat�rio gerado.
     */
    public static ByteArrayOutputStream exportReportToPdfOutputStream(InputStream arquivoJasper, List<?> dados,
            Map<String, Object> reportParameters, String title) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
        	
            JasperPrint print =
                    JasperFillManager.fillReport(arquivoJasper, reportParameters, getReportDataSource(dados));

            SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
            configuration.setMetadataTitle(title);
            configuration.setMetadataAuthor("Banco Central do Brasil");
            configuration.setCompressed(Boolean.TRUE);

            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setConfiguration(configuration);
            exporter.setExporterInput(new SimpleExporterInput(print));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
            exporter.exportReport();

        } catch (JRException e) {
        	e.printStackTrace();
            throw new InfraException("N�o foi possivel gerar o relat�rio e obter o stream de sa�da.", e);
        } 

        return outputStream;
    }

    public static ByteVO montarDossieToPdfOutputStream(InputStream arquivoJasper, List<?> dados,
            Map<String, Object> reportParameters, String title, RelatorioAbstrato relatorio) {

        ByteVO byteVO = new ByteVO();
        try {
            JasperPrint print =
                    JasperFillManager.fillReport(arquivoJasper, reportParameters, getReportDataSource(dados));

            SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
            configuration.setMetadataTitle(title);
            configuration.setMetadataAuthor("Banco Central do Brasil");
            configuration.setCompressed(Boolean.TRUE);

            byteVO.setConfiguration(configuration);
            byteVO.setJasperPrint(print);
            byteVO.setRelatorio(relatorio);
        } catch (JRException e) {
            e.printStackTrace();
            throw new InfraException("N�o foi possivel gerar o relat�rio e obter o stream de sa�da.", e);
        }

        return byteVO;
    }

    public static ByteArrayOutputStream exportarDossie(ByteVO byteVO) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setConfiguration(byteVO.getConfiguration());
            exporter.setExporterInput(byteVO.getPrint());
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
            exporter.exportReport();
        } catch (JRException e) {
            e.printStackTrace();
            throw new InfraException("N�o foi possivel gerar o relat�rio e obter o stream de sa�da.", e);
        }
        return outputStream;
    }
}
