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

import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.PesoAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.SinteseDeRiscoAQT;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.SecaoApresentacaoEnum;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.PesoAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.SinteseDeRiscoAQTMediator;
import br.gov.bcb.sisaps.src.vo.ApresentacaoVO;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.validacao.InfraException;
import br.gov.bcb.sisaps.web.page.dominio.perfilderisco.apresentacao.relatorio.vo.RelatorioSlide14VO;
import br.gov.bcb.sisaps.web.page.dominio.perfilderisco.apresentacao.relatorio.vo.RelatorioSubSinteseVO;

/**
 * Relatorio de Pendencias.
 */
public class RelatorioSlide14 extends RelatorioSlideAbstrato {

    /**
     * Título do relatório.
     */
    public static final String TITULO_RELATORIO = "Relatório Slide 14";

    /**
     * Nome do arquivo do relatório enviado ao usuário.
     */
    public static final String NOME_ARQUIVO_RELATORIO = "RelatorioSlide14";

    private static final String NOME_ARQUIVO_JASPER = "/relatorios/apresentacao/slide14.jasper";
    private static final String PONTOEVIRGULA = "; ";

    private List<RelatorioSlide14VO> dadosRelatorio;
    private final PerfilRisco perfilRisco;
    private final PerfilAcessoEnum perfilMenu;

    public RelatorioSlide14(SecaoApresentacaoEnum secao, ApresentacaoVO apresentacaoVO, int qtdPaginas, int indice,
            PerfilRisco perfilRisco, PerfilAcessoEnum perfilMenu) {
        super(secao, apresentacaoVO, true, qtdPaginas, indice);
        this.perfilRisco = perfilRisco;
        this.perfilMenu = perfilMenu;
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

        dadosRelatorio = new ArrayList<RelatorioSlide14VO>();

        RelatorioSlide14VO relatorio = new RelatorioSlide14VO();
        relatorio.setNotaCalculada("Nota calculada: " + apresentacaoVO.getNotaQuantitativa()[0]);
        relatorio.setNotaRefinada("Nota refinada: " + apresentacaoVO.getNotaQuantitativa()[1]);

        final String notaAjustada = apresentacaoVO.getNotaQuantitativa()[3];
        if (!notaAjustada.isEmpty()) {
            relatorio.setNotaAjustada("Nota ajustada: " + notaAjustada);
        }
        relatorio.setSinteses(new ArrayList<RelatorioSubSinteseVO>());

        List<AnaliseQuantitativaAQT> aqtsVigentes =
                PerfilRiscoMediator.get().getAnalisesQuantitativasAQTPerfilRisco(perfilRisco);
        for (AnaliseQuantitativaAQT aqt : aqtsVigentes) {

            RelatorioSubSinteseVO sub = new RelatorioSubSinteseVO();

            // Declarações
            ParametroAQT parametroAQT;
            SinteseDeRiscoAQT sinteseDeRiscoAQT;
            PesoAQT pesoVigente;
            String nomeCampo;
            String pesoCampo;
            String nota;
            String justificativa;

            // Inicializações
            parametroAQT = aqt.getParametroAQT();
            sinteseDeRiscoAQT =
                    SinteseDeRiscoAQTMediator.get().getUltimaSinteseVigente(parametroAQT, perfilRisco.getCiclo());

            // Nome do campo.
            nomeCampo = parametroAQT.getDescricao();

            // Peso do campo.
            pesoVigente = PesoAQTMediator.get().obterPesoVigente(parametroAQT, aqt.getCiclo());
            pesoCampo = pesoVigente.getValor().toString();

            // Nota do AQT.
            if (aqt.getNotaSupervisor() == null && aqt.getValorNota() == null) {
                nota = Constantes.ASTERISCO_A;
            } else {
                String notaAnef =
                        AnaliseQuantitativaAQTMediator.get().notaAnef(aqt, perfilRisco.getCiclo(), perfilMenu, false,
                                perfilRisco);
                if (!aqt.getNotaSupervisorDescricaoValor().equals(notaAnef)) {
                    nota = notaAnef + " (Corec)";
                } else {
                    nota = notaAnef;
                }
            }

            // Justificativa.
            if (sinteseDeRiscoAQT == null || sinteseDeRiscoAQT.getJustificativa() == null) {
                justificativa = "";
            } else {
                justificativa = " " + sinteseDeRiscoAQT.getJustificativa();
            }

            // Adiciona os componentes.

            sub.setNome(nomeCampo + " (" + pesoCampo + "%)");

            sub.setValor("Nota " + nota);
            sub.setSintese(justificativa);

            relatorio.getSinteses().add(sub);

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
                            "/relatorios/apresentacao/subSlide14.jasper"));
        } catch (JRException e) {
            throw new InfraException("Erro ao carregar o subrelatorio do slide 14.", e);
        }

        parametrosJasper.put("SUB_RELATORIO", subRelatorio);

    }

}
