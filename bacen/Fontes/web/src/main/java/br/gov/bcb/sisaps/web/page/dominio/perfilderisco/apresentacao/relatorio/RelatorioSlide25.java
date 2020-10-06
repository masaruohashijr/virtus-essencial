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

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Page;

import br.gov.bcb.sisaps.src.dominio.ConclusaoES;
import br.gov.bcb.sisaps.src.dominio.GrauPreocupacaoES;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.SecaoApresentacaoEnum;
import br.gov.bcb.sisaps.src.mediator.ConclusaoESMediator;
import br.gov.bcb.sisaps.src.mediator.GrauPreocupacaoESMediator;
import br.gov.bcb.sisaps.src.vo.ApresentacaoVO;
import br.gov.bcb.sisaps.util.Util;

/**
 * Relatorio de Pendencias.
 */
public class RelatorioSlide25 extends RelatorioSlideAbstrato {

    private static final String FECHAR = ")";

    private static final String ABRIR = " (";

    private static final String NOTA_FINAL = "Nota final: ";

    /**
     * Título do relatório.
     */
    public static final String TITULO_RELATORIO = "Relatório Slide 25";

    /**
     * Nome do arquivo do relatório enviado ao usuário.
     */
    public static final String NOME_ARQUIVO_RELATORIO = "RelatorioSlide25";

    private static final String NOME_ARQUIVO_JASPER = "/relatorios/apresentacao/slide25.jasper";

    private List<String> dadosRelatorio;
    private final PerfilRisco perfil;
    private final PerfilAcessoEnum perfilUsuario;

    public RelatorioSlide25(SecaoApresentacaoEnum secao, ApresentacaoVO apresentacaoVO, int qtdPaginas, int indice,
            PerfilRisco perfil, PerfilAcessoEnum perfilUsuario) {
        super(secao, apresentacaoVO, true, qtdPaginas, indice);
        this.perfil = perfil;
        this.perfilUsuario = perfilUsuario;
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
        addNotaQuantitativa(parametrosJasper);
        addNotaQualitativa(parametrosJasper);
        
        // Nota final.
        GrauPreocupacaoES grauPreocupacaoES =
                GrauPreocupacaoESMediator.get().getGrauPreocupacaoESPorPerfil(perfil, false,
                        perfilUsuario);
        
        // Nota final
        String notaFinalDescricao = GrauPreocupacaoESMediator.get().getNotaFinal(perfil, perfilUsuario,
                perfil.getCiclo().getMetodologia());
        
        String notaFinal = "";
        
        if (GrauPreocupacaoESMediator.get().isNotaFinal(grauPreocupacaoES) 
                && StringUtils.isNotBlank(notaFinalDescricao)) {
        	String notaAnef = GrauPreocupacaoESMediator.get().getNotaAEF(perfil, perfilUsuario);
            String notaMatriz = GrauPreocupacaoESMediator.get().getNotaMatrizFinal(perfil, perfilUsuario);
            String notaCalculada =
                    GrauPreocupacaoESMediator.get().getNotaFinalCalculada(grauPreocupacaoES, notaAnef, notaMatriz, perfil.getCiclo());
            notaFinal = NOTA_FINAL + notaFinalDescricao + ABRIR + notaCalculada + FECHAR;
        }
                
        parametrosJasper.put("notaFinal", notaFinal);

        // Conclusão
        ConclusaoES conclusaoES = ConclusaoESMediator.get().buscarPorPerfilRisco(perfil.getPk());
        String conclusao = "";
        if (conclusaoES != null && conclusaoES.getDocumento() != null) {
            conclusao = conclusaoES.getDocumento().getJustificativa();
        }
        parametrosJasper.put("conclusao", conclusao);
    }

    private void addNotaQualitativa(Map<String, Object> parametrosJasper) {
        // Nota qualitativa.
        StringBuffer notaAnaliseRisco = new StringBuffer("Nota da Análise de Riscos e Controles:  ");

        if (!Util.isNuloOuVazio(apresentacaoVO.getNotaQualitativa()[3])) {
            notaAnaliseRisco.append(apresentacaoVO.getNotaQualitativa()[3]);
        } else {
        	notaAnaliseRisco.append(apresentacaoVO.getNotaQualitativa()[1]);
            notaAnaliseRisco.append(ABRIR);
            notaAnaliseRisco.append(apresentacaoVO.getNotaQualitativa()[0]);
            notaAnaliseRisco.append(FECHAR);
        }

        parametrosJasper.put("notaAnaliseRiscoEControle", notaAnaliseRisco.toString());
    }

    private void addNotaQuantitativa(Map<String, Object> parametrosJasper) {
        // Nota quantitativa.
        StringBuffer notaAnaliseEconomica = new StringBuffer("Nota da Análise Econômico-Financeira:  ");

        if (!Util.isNuloOuVazio(apresentacaoVO.getNotaQuantitativa()[3])) {
            notaAnaliseEconomica.append(apresentacaoVO.getNotaQuantitativa()[3]);
        } else {
        	notaAnaliseEconomica.append(apresentacaoVO.getNotaQuantitativa()[1]);
            notaAnaliseEconomica.append(ABRIR);
            notaAnaliseEconomica.append(apresentacaoVO.getNotaQuantitativa()[0]);
            notaAnaliseEconomica.append(FECHAR);

        }
        parametrosJasper.put("notaAnaliseEconomica", notaAnaliseEconomica.toString());
    }
}
