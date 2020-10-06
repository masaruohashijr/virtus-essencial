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

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;

import br.gov.bcb.sisaps.src.dominio.enumeracoes.SecaoApresentacaoEnum;
import br.gov.bcb.sisaps.src.vo.ApresentacaoVO;
import br.gov.bcb.sisaps.src.vo.RiscoVO;
import br.gov.bcb.sisaps.util.validacao.InfraException;
import br.gov.bcb.sisaps.web.page.dominio.perfilderisco.apresentacao.relatorio.vo.RelatorioSlide20VO;
import br.gov.bcb.sisaps.web.page.dominio.perfilderisco.apresentacao.relatorio.vo.RelatorioSubSinteseVO;

/**
 * Relatorio de Pendencias.
 */
public class RelatorioSlide20 extends RelatorioSlideAbstrato {

    /**
     * Título do relatório.
     */
    public static final String TITULO_RELATORIO = "Relatório Slide 20";

    /**
     * Nome do arquivo do relatório enviado ao usuário.
     */
    public static final String NOME_ARQUIVO_RELATORIO = "RelatorioSlide20";

    private static final String NOME_ARQUIVO_JASPER = "/relatorios/apresentacao/slide20.jasper";
    private static final String PONTOEVIRGULA = "; ";
    private static final String ESPACO = " ";

    private List<RelatorioSlide20VO> dadosRelatorio;

    public RelatorioSlide20(SecaoApresentacaoEnum secao, ApresentacaoVO apresentacaoVO, int qtdPaginas, int indice) {
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

        dadosRelatorio = new ArrayList<RelatorioSlide20VO>();

        RelatorioSlide20VO relatorio = new RelatorioSlide20VO();
        relatorio.setSinteses(new ArrayList<RelatorioSubSinteseVO>());
        for (RiscoVO risco : apresentacaoVO.getRiscosVO()) {
            if (!risco.getNome().isEmpty()) {
                RelatorioSubSinteseVO sub = new RelatorioSubSinteseVO();
                sub.setNome(risco.getNome());
                if (risco.isArcExterno()) {
                    sub.setValor("Nota: " + risco.getNotaRisco());
                } else {
                    String descricaoRisco = "";
                    String descricaoControle = "";
                    String descricaoNotaResidual = "";
                    String descricaoConceitoResidual = "";
                    if (StringUtils.isNotBlank(risco.getNotaRisco())) {
                        descricaoRisco = "Risco: " + risco.getNotaRisco();
                    }
                    if (StringUtils.isNotBlank(risco.getNotaControle())) {
                        descricaoControle = PONTOEVIRGULA + "Controle: " + risco.getNotaControle();
                    }
                    if (StringUtils.isNotBlank(risco.getNotaResidual())) {
                        descricaoNotaResidual = PONTOEVIRGULA + "Residual: " + risco.getNotaResidual();
                    }
                    if (StringUtils.isNotBlank(risco.getConceitoResidual())) {
                        descricaoConceitoResidual = ESPACO + "(" + risco.getConceitoResidual() + ")";
                    }
                    sub.setValor(descricaoRisco + descricaoControle
                            + descricaoNotaResidual + descricaoConceitoResidual);
                }
                sub.setSintese(risco.getSintese());
                relatorio.getSinteses().add(sub);
            }
        }
        dadosRelatorio.add(relatorio);

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
                            "/relatorios/apresentacao/subSlide20.jasper"));
        } catch (JRException e) {
            throw new InfraException("Erro ao carregar o subrelatorio do slide 20.", e);
        }

        parametrosJasper.put("SUB_RELATORIO", subRelatorio);

    }

}
