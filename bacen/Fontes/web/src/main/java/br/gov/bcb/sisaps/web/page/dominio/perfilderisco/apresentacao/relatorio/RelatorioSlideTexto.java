/*
 * Sistema: AUD
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arquivo contém informações proprietárias.
 */

package br.gov.bcb.sisaps.web.page.dominio.perfilderisco.apresentacao.relatorio;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;

import br.gov.bcb.sisaps.src.dominio.enumeracoes.SecaoApresentacaoEnum;
import br.gov.bcb.sisaps.src.vo.ApresentacaoVO;
import br.gov.bcb.sisaps.src.vo.TextoApresentacaoVO;
import br.gov.bcb.sisaps.util.validacao.InfraException;
import br.gov.bcb.sisaps.web.page.dominio.perfilderisco.apresentacao.relatorio.vo.RelatorioSlideTextoVO;
import br.gov.bcb.sisaps.web.page.dominio.perfilderisco.apresentacao.relatorio.vo.RelatorioSubSlideTextoVO;

/**
 * Relatorio de Pendencias.
 */
public class RelatorioSlideTexto extends RelatorioSlideAbstrato {

    /**
     * Título do relatório.
     */
    public static final String TITULO_RELATORIO = "Relatório Slide texto";

    /**
     * Nome do arquivo do relatório enviado ao usuário.
     */
    public static final String NOME_ARQUIVO_RELATORIO = "RelatorioSlideTexto";

    private static final String NOME_ARQUIVO_JASPER = "/relatorios/apresentacao/slideTexto.jasper";

    private List<RelatorioSlideTextoVO> dadosRelatorio;
    private List<TextoApresentacaoVO> textosVO;

    public RelatorioSlideTexto(SecaoApresentacaoEnum secao, List<TextoApresentacaoVO> textosVO,
            ApresentacaoVO apresentacaoVO, int qtdPaginas, int indice) {
        super(secao, apresentacaoVO, true, qtdPaginas, indice);
        this.textosVO = textosVO;

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
        dadosRelatorio = new ArrayList<RelatorioSlideTextoVO>();
        RelatorioSlideTextoVO relatorioSlideTextoVO = new RelatorioSlideTextoVO();
        relatorioSlideTextoVO.setTexto(new ArrayList<RelatorioSubSlideTextoVO>());
        for (TextoApresentacaoVO textoApresentacaoVO : textosVO) {
            RelatorioSubSlideTextoVO sub = new RelatorioSubSlideTextoVO();
            sub.setNome(textoApresentacaoVO.getCampo().getDescricao());
            sub.setConteudo(textoApresentacaoVO.getTexto());
            relatorioSlideTextoVO.getTexto().add(sub);
        }
        dadosRelatorio.add(relatorioSlideTextoVO);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void adicionarParametros(Map<String, Object> parametrosJasper, Page paginaAtual) {
        WebApplication wa = (WebApplication) paginaAtual.getApplication();
        JasperReport subRelatorio = null;

        try {
            subRelatorio =
                    (JasperReport) JRLoader.loadObject(wa.getServletContext().getResourceAsStream(
                            "/relatorios/apresentacao/subSlideTexto.jasper"));
        } catch (JRException e) {
            throw new InfraException("Erro ao carregar o subrelatorio do slide texto.", e);
        }

        parametrosJasper.put("SUB_RELATORIO", subRelatorio);

    }

}
