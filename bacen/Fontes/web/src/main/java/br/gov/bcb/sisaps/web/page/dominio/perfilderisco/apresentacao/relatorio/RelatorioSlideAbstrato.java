package br.gov.bcb.sisaps.web.page.dominio.perfilderisco.apresentacao.relatorio;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;

import br.gov.bcb.sisaps.src.dominio.enumeracoes.SecaoApresentacaoEnum;
import br.gov.bcb.sisaps.src.vo.ApresentacaoVO;
import br.gov.bcb.sisaps.util.Util;
import br.gov.bcb.sisaps.util.validacao.InfraException;
import br.gov.bcb.sisaps.web.page.componentes.util.JasperUtils;

/**
 * Classe abstrata de relatório.
 */
public abstract class RelatorioSlideAbstrato {

    private static final String PARAM_LOGO = "LOGO";
    private static final Log LOG = LogFactory.getLog(RelatorioSlideAbstrato.class.getName());
    protected SecaoApresentacaoEnum secao;
    protected ApresentacaoVO apresentacaoVO;
    private boolean comTitulo;
    private int qtdPaginas;
    private int indice;

    public RelatorioSlideAbstrato(SecaoApresentacaoEnum secao, ApresentacaoVO apresentacaoVO, boolean comTitulo,
            int qtdPaginas, int indice) {
        this.secao = secao;
        this.apresentacaoVO = apresentacaoVO;
        this.comTitulo = comTitulo;
        this.indice = indice;
        this.qtdPaginas = qtdPaginas;
    }

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
        return "/relatorios/apresentacao/logobc.gif";
    };

    /**
     * Retorna o stream do relatório gerado em pdf.
     * 
     * @return ByteArrayOutputStream do relatório
     * @throws InfraException caso não seja possivel gerar o relatório em pdf.
     */
    public byte[] gerarRelatorioPDF(Page paginaAtual) throws InfraException {
        WebApplication wa = (WebApplication) paginaAtual.getApplication();
        Map<String, Object> parametrosJasper = new HashMap<String, Object>();

        InputStream imgLogo = wa.getServletContext().getResourceAsStream(getLogo());
        parametrosJasper.put(PARAM_LOGO, imgLogo);
        parametrosJasper.put("titulo", comTitulo ? secao.getDescricao() : " ");
        parametrosJasper.put("conglomerado", apresentacaoVO.getNomeEs());

        parametrosJasper.put("pagina", "SRC " + indice + "/" + qtdPaginas);

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
     * Metodo utilizado para adicionar parametro aos relatorios.
     * 
     * @param parametrosJasper mapa com os parametros.
     */
    protected void adicionarParametros(Map<String, Object> parametrosJasper, Page paginaAtual) {
        // metodo criado para ser um template de implementação não obrigatória.
    }

    protected String normalizaTexto(String texto) {

        return Util.normalizaTexto(texto);

    }

}
