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

package br.gov.bcb.sisaps.web.page.dominio.aqt.relatorio;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

import org.apache.wicket.protocol.http.WebApplication;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Metodologia;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.VersaoPerfilRisco;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.NotaAjustadaAEF;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroNotaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.PesoAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.SinteseDeRiscoAQT;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoObjetoVersionadorEnum;
import br.gov.bcb.sisaps.src.mediator.AjusteCorecMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.src.mediator.VersaoPerfilRiscoMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.NotaAjustadaAEFMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.PesoAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.SinteseDeRiscoAQTMediator;
import br.gov.bcb.sisaps.src.vo.relatorio.RelatorioDetalhesANEF;
import br.gov.bcb.sisaps.src.vo.relatorio.RelatorioSinteseANEFVO;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.Util;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import br.gov.bcb.sisaps.util.validacao.InfraException;
import br.gov.bcb.sisaps.web.page.componentes.util.RelatorioAbstrato;
import br.gov.bcb.wicket.pages.AbstractBacenWebPage;

/**
 * Relatorio de Pendencias.
 */
public class RelatorioDetalhesAQTMatriz extends RelatorioAbstrato {

    /**
     * Título do relatório.
     */
    public static final String TITULO_RELATORIO = "Relatório de Detalhes ANEFs da Matriz";

    /**
     * Nome do arquivo do relatório enviado ao usuário.
     */
    public static final String NOME_ARQUIVO_RELATORIO = "RelatorioImpressaoDetalhesANEF";

    private static final String NOME_ARQUIVO_JASPER = "/relatorios/RelatorioImpressaoDetalhesANEF.jasper";
    private static final String TRACO = " - ";
    private static final String ESPACO = ". ";

    private List<RelatorioSinteseANEFVO> dadosRelatorioSintese;
    private List<RelatorioDetalhesANEF> dadosRelatorioDetalhesANEF;
    private final RelatorioDetalhesANEF relatorioDetalhesAnef = new RelatorioDetalhesANEF();
    private final Ciclo ciclo;
    private final List<AnaliseQuantitativaAQT> aqtsVigentes;
    private final List<VersaoPerfilRisco> versoesSintesesAQT;
    private final PerfilRisco perfilRisco;
    private final PerfilAcessoEnum perfilEmun;

    private int contElemento;

    public RelatorioDetalhesAQTMatriz(PerfilRisco perfilRisco, PerfilAcessoEnum perfilEmun) {
        this.ciclo = perfilRisco.getCiclo();
        this.perfilRisco = perfilRisco;
        this.perfilEmun = perfilEmun;
        aqtsVigentes = PerfilRiscoMediator.get().getAnalisesQuantitativasAQTPerfilRisco(perfilRisco);
        versoesSintesesAQT =
                VersaoPerfilRiscoMediator.get().buscarVersoesPerfilRisco(perfilRisco.getPk(),
                        TipoObjetoVersionadorEnum.SINTESE_AQT);

        converterDados();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<?> getDadosRelatorio() {
        return dadosRelatorioDetalhesANEF;
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
        dadosRelatorioDetalhesANEF = new ArrayList<RelatorioDetalhesANEF>();
        dadosRelatorioSintese = new ArrayList<RelatorioSinteseANEFVO>();

        relatorioDetalhesAnef.setNomeConglomerado(ciclo.getEntidadeSupervisionavel().getNomeConglomeradoFormatado());
        relatorioDetalhesAnef.setNotaAjustada(montarNotaAjustada());

        relatorioDetalhesAnef.setNotaCalculada(AnaliseQuantitativaAQTMediator.get().getNotaCalculadaAEF(perfilRisco,
                perfilEmun, false));
        relatorioDetalhesAnef.setNotaRefinada(AnaliseQuantitativaAQTMediator.get().getNotaRefinadaAEF(perfilRisco,
                perfilEmun, false));

        relatorioDetalhesAnef.setRodape(montarRodape());

        montarListaSinteses();
        relatorioDetalhesAnef.setSinteses(dadosRelatorioSintese);
        dadosRelatorioDetalhesANEF.add(relatorioDetalhesAnef);

    }

    private String montarNotaAjustada() {
        NotaAjustadaAEF notaAjustada = NotaAjustadaAEFMediator.get().buscarNotaAjustadaAEF(ciclo, perfilRisco);
        ParametroNotaAQT ajusteCorec = AjusteCorecMediator.get().notaAjustadaCorecAEF(perfilRisco, ciclo, perfilEmun);
        Metodologia metodologia = ciclo.getMetodologia();
        boolean possuiCorec = ajusteCorec != null;
        relatorioDetalhesAnef.setPossuiCorec(possuiCorec);
        if (possuiCorec) {
        	String descricaoCorec = PerfilAcessoEnum.CONSULTA_RESUMO_NAO_BLOQUEADOS.equals(perfilEmun) 
        			? "" : Constantes.COREC; 
            relatorioDetalhesAnef.setJustificativaNotaAjustada("");
            return ajusteCorec.getDescricaoValor() + descricaoCorec;
        }
        relatorioDetalhesAnef.setJustificativaNotaAjustada(notaAjustada == null ? "" : normalizaTexto(notaAjustada
                .getJustificativa()));
        return notaAjustada == null ? "" : notaAjustada.getParamentroNotaAQT() == null ? "" : metodologia
                .getIsCalculoMedia() == null || metodologia.getIsCalculoMedia().equals(SimNaoEnum.NAO) ? notaAjustada
                .getParamentroNotaAQT().getDescricaoValor() : notaAjustada.getParamentroNotaAQT().getDescricao();
    }

    private void montarListaSinteses() {

        contElemento = 1;
        for (AnaliseQuantitativaAQT anef : aqtsVigentes) {
            contElemento++;
            RelatorioSinteseANEFVO sinteseAnefVO = new RelatorioSinteseANEFVO();

            SinteseDeRiscoAQT sinteseDeRiscoAQT =
                    Util.isNuloOuVazio(versoesSintesesAQT) ? null : SinteseDeRiscoAQTMediator.get()
                            .getSintesePorVersaoPerfil(anef.getParametroAQT(), perfilRisco.getCiclo(),
                                    versoesSintesesAQT);
            PesoAQT pesoVigente =
                    PesoAQTMediator.get().getPesoAqtPorVersaoPerfil(anef.getParametroAQT(), anef.getCiclo(),
                            perfilRisco.getVersoesPerfilRisco());
            sinteseAnefVO.setRelevancia(pesoVigente.getValor().toString() + "%");
            sinteseAnefVO.setTitulo(montarTitulo(contElemento, anef));
            sinteseAnefVO.setNota(montarNotaAnef(anef));
            sinteseAnefVO.setJustificativaNota(normalizaTexto(sinteseDeRiscoAQT == null
                    || sinteseDeRiscoAQT.getJustificativa() == null ? "" : " " + sinteseDeRiscoAQT.getJustificativa()));
            dadosRelatorioSintese.add(sinteseAnefVO);

        }

    }

    private String montarNotaAnef(AnaliseQuantitativaAQT anef) {
        String notaAnef =
                AnaliseQuantitativaAQTMediator.get().notaAnef(anef, perfilRisco.getCiclo(), perfilEmun, false, perfilRisco);
        if (!anef.getNotaSupervisorDescricaoValor().equals(notaAnef)) {
            return isPerfilConsulta(perfilEmun) ? notaAnef : notaAnef + Constantes.COREC;
        }
        return notaAnef;
    }
    
    private boolean isPerfilConsulta(PerfilAcessoEnum perfil) {
		return perfil.equals(PerfilAcessoEnum.CONSULTA_NAO_BLOQUEADOS)
				|| perfil.equals(PerfilAcessoEnum.CONSULTA_RESUMO_NAO_BLOQUEADOS)
				|| perfil.equals(PerfilAcessoEnum.CONSULTA_TUDO);
    }

    private String montarTitulo(int contElemento2, AnaliseQuantitativaAQT anef) {
        StringBuffer nomeElemento = new StringBuffer();
        nomeElemento.append(contElemento2);
        nomeElemento.append(ESPACO);
        nomeElemento.append(anef.getParametroAQT().getDescricao());
        return nomeElemento.toString();
    }

    private String montarRodape() {
        StringBuffer rodape = new StringBuffer();
        rodape.append("Versão ");
        rodape.append(perfilRisco.getDataCriacaoFormatada());
        rodape.append(TRACO);
        rodape.append("Ciclo ");
        rodape.append(ciclo.getDataInicioFormatada());
        rodape.append(" ~ ");
        rodape.append(ciclo.getDataPrevisaoFormatada());
        return rodape.toString();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void adicionarParametros(Map<String, Object> parametrosJasper, AbstractBacenWebPage paginaAtual) {
        WebApplication wa = (WebApplication) paginaAtual.getApplication();
        JasperReport subRelatorioSinteses = null;

        parametrosJasper.put("notaCalculada", relatorioDetalhesAnef.getNotaCalculada());
        parametrosJasper.put("notaRefinada", relatorioDetalhesAnef.getNotaRefinada());
        parametrosJasper.put("notaAjustada", relatorioDetalhesAnef.getNotaAjustada());
        parametrosJasper.put("ossuiCorec", relatorioDetalhesAnef.isPossuiCorec());
        parametrosJasper.put("booleanAjustada", !"".equals(relatorioDetalhesAnef.getNotaAjustada()));
        parametrosJasper.put("booleanJustificativaAjustada",
                !"".equals(relatorioDetalhesAnef.getJustificativaNotaAjustada()));
        parametrosJasper.put("justificativaNotaAjustada", relatorioDetalhesAnef.getJustificativaNotaAjustada());
        parametrosJasper.put("rodape", relatorioDetalhesAnef.getRodape());

        try {
            subRelatorioSinteses =
                    (JasperReport) JRLoader.loadObject(wa.getServletContext().getResourceAsStream(
                            "/relatorios/RelatorioImpressaoDetalhesANEF_Sintese.jasper"));
        } catch (JRException e) {
            throw new InfraException("Erro ao carregar o subrelatorio de sintese anef.", e);
        }

        parametrosJasper.put("SUB_RELATORIO", subRelatorioSinteses);

    }

}
