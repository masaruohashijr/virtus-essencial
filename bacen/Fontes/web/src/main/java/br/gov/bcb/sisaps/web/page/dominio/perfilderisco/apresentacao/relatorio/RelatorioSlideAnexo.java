/*
 * Sistema: AUD
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software � confidencial e propriedade do Banco Central do Brasil.
 * N�o � permitida sua distribui��o ou divulga��o do seu conte�do sem
 * expressa autoriza��o do Banco Central.
 * Este arquivo cont�m informa��es propriet�rias.
 */

package br.gov.bcb.sisaps.web.page.dominio.perfilderisco.apresentacao.relatorio;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;

import br.gov.bcb.sisaps.src.dominio.enumeracoes.SecaoApresentacaoEnum;
import br.gov.bcb.sisaps.src.mediator.AnexoApresentacaoMediator;
import br.gov.bcb.sisaps.src.vo.AnexoApresentacaoVO;
import br.gov.bcb.sisaps.src.vo.ApresentacaoVO;

/**
 * Relatorio de Pendencias.
 */
public class RelatorioSlideAnexo extends RelatorioSlideAbstrato {

    /**
     * T�tulo do relat�rio.
     */
    public static final String TITULO_RELATORIO = "Relat�rio Slide anexo";

    /**
     * Nome do arquivo do relat�rio enviado ao usu�rio.
     */
    public static final String NOME_ARQUIVO_RELATORIO = "RelatorioSlideAnexo";

    private static final String NOME_ARQUIVO_JASPER = "/relatorios/apresentacao/slideAnexo.jasper";

    private List<String> dadosRelatorio;
    private AnexoApresentacaoVO anexoVO;

    public RelatorioSlideAnexo(SecaoApresentacaoEnum secao, final AnexoApresentacaoVO anexoVO,
            ApresentacaoVO apresentacaoVO, int qtdPaginas, int indice) {
        super(secao, apresentacaoVO, true, qtdPaginas, indice);
        this.anexoVO = anexoVO;

        converterDados();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<?> getDadosRelatorio() {
        return dadosRelatorio;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNomeArquivoJasper() {
        return NOME_ARQUIVO_JASPER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTituloRelatorio() {
        return TITULO_RELATORIO;
    }

    private void converterDados() {
        dadosRelatorio = new ArrayList<String>();
        dadosRelatorio.add("teste");

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void adicionarParametros(Map<String, Object> parametrosJasper, Page paginaAtual) {
        WebApplication wa = (WebApplication) paginaAtual.getApplication();
        InputStream imgLogo = new ByteArrayInputStream(AnexoApresentacaoMediator.get().recuperarArquivo(anexoVO));
        parametrosJasper.put("anexo", imgLogo);

    }

}
