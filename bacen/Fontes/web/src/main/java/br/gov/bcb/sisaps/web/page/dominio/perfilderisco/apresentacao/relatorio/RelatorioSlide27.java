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

import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;

import br.gov.bcb.sisaps.src.dominio.PerspectivaES;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.SecaoApresentacaoEnum;
import br.gov.bcb.sisaps.src.vo.ApresentacaoVO;
import br.gov.bcb.sisaps.util.Constantes;

/**
 * Relatorio de Pendencias.
 */
public class RelatorioSlide27 extends RelatorioSlideAbstrato {

    /**
     * Título do relatório.
     */
    public static final String TITULO_RELATORIO = "Relatório Slide 27";

    /**
     * Nome do arquivo do relatório enviado ao usuário.
     */
    public static final String NOME_ARQUIVO_RELATORIO = "RelatorioSlide27";

    private static final String NOME_ARQUIVO_JASPER = "/relatorios/apresentacao/slide27.jasper";

    private List<String> dadosRelatorio;

    public RelatorioSlide27(SecaoApresentacaoEnum secao, ApresentacaoVO apresentacaoVO, int qtdPaginas, int indice) {
        super(secao, apresentacaoVO, true, qtdPaginas, indice);
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

        parametrosJasper.put("possuiAnterior", apresentacaoVO.getDadosCicloVOAnteriorVO() != null);
        parametrosJasper.put("tituloAnterior", "Perspectiva no SRC anterior");
        PerspectivaES perspectivaESAnterior =
                apresentacaoVO.getDadosCicloVOAnteriorVO() == null ? null : apresentacaoVO.getDadosCicloVOAnteriorVO()
                        .getPerspectivaES();
        String parametroAnterior = "";
        String conteudoAnterior = "";

        if (perspectivaESAnterior != null) {
            String nome = perspectivaESAnterior.getParametroPerspectiva().getNome();
            parametroAnterior = perspectivaESAnterior.getPk() == null ? nome + Constantes.COREC : nome;
            conteudoAnterior = perspectivaESAnterior.getDescricao();
        }

        parametrosJasper.put("parametroAnterior", parametroAnterior);
        parametrosJasper.put("conteudoAnterior", conteudoAnterior);

        PerspectivaES perspectivaES = apresentacaoVO.getDadosCicloVO().getPerspectivaES();
        String parametroAtual = "";
        String conteudoAtual = "";

        if (perspectivaES != null) {
            String nome = perspectivaES.getParametroPerspectiva().getNome();
            parametroAtual = perspectivaES.getPk() == null ? nome + Constantes.COREC : nome;
            conteudoAtual = perspectivaES.getDescricao();
        }

        parametrosJasper.put("parametroAtual", parametroAtual);
        parametrosJasper.put("conteudoAtual", conteudoAtual);

    }

}
