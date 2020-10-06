package br.gov.bcb.sisaps.web.page.componentes.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.protocol.http.WebApplication;

import br.gov.bcb.sisaps.util.Util;
import br.gov.bcb.sisaps.util.validacao.InfraException;
import br.gov.bcb.wicket.pages.AbstractBacenWebPage;

/**
 * Classe abstrata de relatório.
 */
public abstract class RelatorioAbstrato {

    private static final String PARAM_LOGO = "LOGO";
    private static final Log LOG = LogFactory.getLog(RelatorioAbstrato.class.getName());

    /**
     * getTituloRelatorio.
     * 
     * @return
     */
    public abstract String getTituloRelatorio();

    /**
     * getNomeArquivoJasper.
     * 
     * @return
     */
    public abstract String getNomeArquivoJasper();

    /**
     * getDadosRelatorio.
     * 
     * @return
     */
    public abstract List<?> getDadosRelatorio();

    public String getLogo() {
        return "/relatorios/logobacen2.gif";
    };

    /**
     * Retorna o stream do relatório gerado em pdf.
     * 
     * @return ByteArrayOutputStream do relatório
     * @throws InfraException caso não seja possivel gerar o relatório em pdf.
     */
    public byte[] gerarRelatorioPDF(AbstractBacenWebPage paginaAtual) throws InfraException {
        WebApplication wa = (WebApplication) paginaAtual.getApplication();
        Map<String, Object> parametrosJasper = new HashMap<String, Object>();

        InputStream imgLogo = wa.getServletContext().getResourceAsStream(getLogo());
        parametrosJasper.put(PARAM_LOGO, imgLogo);

        this.adicionarParametros(parametrosJasper, paginaAtual);

        LOG.info("Lendo o arquivo '" + getNomeArquivoJasper() + "'...");

        InputStream arquivoJasper = wa.getServletContext().getResourceAsStream(getNomeArquivoJasper());

        LOG.info("Arquivo " + (arquivoJasper == null ? "null" : "ok"));

        LOG.info("Chamando o jasper para gerar o relatório...");
        ByteArrayOutputStream outputStream =
                JasperUtils.exportReportToPdfOutputStream(arquivoJasper, getDadosRelatorio(), parametrosJasper,
                        getTituloRelatorio());
        byte[] relatorioGerado = outputStream.toByteArray();

        LOG.info("Relatório gerado com " + relatorioGerado.length + " bytes.");
        return relatorioGerado;
    }
    
    /**
     * Retorna o stream do relatório gerado em pdf.
     * 
     * @return ByteArrayOutputStream do relatório
     * @throws InfraException caso não seja possivel gerar o relatório em pdf.
     */
    public ByteVO montarDossiePDF(AbstractBacenWebPage paginaAtual, RelatorioAbstrato relatorio) throws InfraException {
        WebApplication wa = (WebApplication) paginaAtual.getApplication();
        Map<String, Object> parametrosJasper = new HashMap<String, Object>();
        InputStream imgLogo = wa.getServletContext().getResourceAsStream(getLogo());
        parametrosJasper.put(PARAM_LOGO, imgLogo);
        parametrosJasper.put("CURRENTPAGE", "CURRENTPAGE");
        parametrosJasper.put("TOTALPAGE", "TOTALPAGE");
        this.adicionarParametros(parametrosJasper, paginaAtual);
        LOG.info("Lendo o arquivo '" + getNomeArquivoJasper() + "'...");
        InputStream arquivoJasper = wa.getServletContext().getResourceAsStream(getNomeArquivoJasper());
        LOG.info("Arquivo " + (arquivoJasper == null ? "null" : "ok"));
        LOG.info("Chamando o jasper para gerar o relatório...");
        ByteVO vo = JasperUtils.montarDossieToPdfOutputStream(arquivoJasper, getDadosRelatorio(), parametrosJasper,
                getTituloRelatorio(), relatorio);
        return vo;
    }

    public ByteArrayOutputStream gerarDossiePDF(ByteVO vo) {
        return JasperUtils.exportarDossie(vo);
    }

    /**
     * This method ensures that the output String has only
     * 
     * @param in the string that has a non valid character.
     * @return the string that is stripped of the non-valid character
     */
    private String stripNonValidXMLCharacters(String in) {      
        if (in == null || ("".equals(in))) return null;
        String text = in.replaceAll("calibri", "Calibri").replaceAll("times new roman", "Times New Roman");
        StringBuffer out = new StringBuffer(text);
        for (int i = 0; i < out.length(); i++) {
            if(out.charAt(i) == 0x1a) {
                out.setCharAt(i, '-');
            }
        }
        return out.toString();
    }

    /**
     * Metodo utilizado para adicionar parametro aos relatorios.
     * 
     * @param parametrosJasper mapa com os parametros.
     */
    protected void adicionarParametros(Map<String, Object> parametrosJasper, AbstractBacenWebPage paginaAtual) {
        // metodo criado para ser um template de implementação não obrigatória.
    }

    protected String normalizaTexto(String texto) {
    	String novoTexto;
    	novoTexto = stripNonValidXMLCharacters(texto);
        return Util.normalizaTexto(novoTexto);
    }

}
