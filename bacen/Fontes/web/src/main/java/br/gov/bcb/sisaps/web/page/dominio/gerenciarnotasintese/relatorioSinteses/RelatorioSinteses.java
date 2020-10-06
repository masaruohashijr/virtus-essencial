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

package br.gov.bcb.sisaps.web.page.dominio.gerenciarnotasintese.relatorioSinteses;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.wicket.protocol.http.WebApplication;

import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.ParametroGrupoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.SinteseDeRisco;
import br.gov.bcb.sisaps.src.dominio.VersaoPerfilRisco;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoObjetoVersionadorEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoParametroGrupoRiscoControleEnum;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.CelulaRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.SinteseDeRiscoMediator;
import br.gov.bcb.sisaps.src.mediator.VersaoPerfilRiscoMediator;
import br.gov.bcb.sisaps.src.vo.CelulaRiscoControleVO;
import br.gov.bcb.sisaps.src.vo.LinhaNotasMatrizVO;
import br.gov.bcb.sisaps.src.vo.relatorio.RelatorioSinteseVO;
import br.gov.bcb.sisaps.src.vo.relatorio.RelatorioSintesesMatrizVO;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.Util;
import br.gov.bcb.sisaps.util.validacao.InfraException;
import br.gov.bcb.sisaps.web.page.componentes.util.RelatorioAbstrato;
import br.gov.bcb.wicket.pages.AbstractBacenWebPage;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

/**
 * Relatorio de Pendencias.
 */
public class RelatorioSinteses extends RelatorioAbstrato {

    /**
     * Título do relatório.
     */
    public static final String TITULO_RELATORIO = "Relatório de Sínteses";

    /**
     * Nome do arquivo do relatório enviado ao usuário.
     */
    public static final String NOME_ARQUIVO_RELATORIO = "RelatorioImpressaoSinteses";

    private static final String NOME_ARQUIVO_JASPER = "/relatorios/RelatorioImpressaoSinteses.jasper";
    private static final String TRACO = " - ";
    private List<RelatorioSinteseVO> dadosRelatorioSintese;
    private List<RelatorioSintesesMatrizVO> dadosRelatorioSintesesMatriz;
    private final RelatorioSintesesMatrizVO relatorioSinteses = new RelatorioSintesesMatrizVO();
    private final Matriz matriz;
    private final List<ParametroGrupoRiscoControle> lista;
    private final PerfilRisco perfilRisco;
    private List<VersaoPerfilRisco> versoesSintesesMatriz;
    private final PerfilAcessoEnum perfilMenu;
    private final boolean isRelatorioResumido;

    public RelatorioSinteses(List<ParametroGrupoRiscoControle> lista, Matriz matriz, PerfilRisco perfilRisco,
            PerfilAcessoEnum perfilMenu, boolean isRelatorioResumido) {
        this.lista = lista;
        this.matriz = matriz;
        this.perfilRisco = perfilRisco;
        this.perfilMenu = perfilMenu;
        this.isRelatorioResumido = isRelatorioResumido;
        converterDados();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<?> getDadosRelatorio() {
        return dadosRelatorioSintesesMatriz;
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
        dadosRelatorioSintesesMatriz = new ArrayList<RelatorioSintesesMatrizVO>();
        dadosRelatorioSintese = new ArrayList<RelatorioSinteseVO>();
        versoesSintesesMatriz =
                VersaoPerfilRiscoMediator.get().buscarVersoesPerfilRisco(perfilRisco.getPk(),
                        TipoObjetoVersionadorEnum.SINTESE_RISCO);
        relatorioSinteses.setNomeConglomerado(perfilRisco.getCiclo().getEntidadeSupervisionavel()
                .getNomeConglomeradoFormatado());
        relatorioSinteses.setRodape(montarRodape());
        montarListaSinteses();
        relatorioSinteses.setSinteses(dadosRelatorioSintese);
        dadosRelatorioSintesesMatriz.add(relatorioSinteses);
    }

    private void montarListaSinteses() {
        if (!Util.isNuloOuVazio(lista)) {
            for (ParametroGrupoRiscoControle parametroGRC : lista) {
                RelatorioSinteseVO sinteseVO = new RelatorioSinteseVO();
                SinteseDeRisco sinteseDeRisco = null;

                if (CollectionUtils.isNotEmpty(versoesSintesesMatriz)) {
                    sinteseDeRisco =
                            SinteseDeRiscoMediator.get().getUltimaSinteseParametroGrupoRisco(parametroGRC,
                                    perfilRisco.getCiclo(), versoesSintesesMatriz);
                }
                sinteseVO.setNomeParametroGrupoRisco(parametroGRC.getNomeAbreviado());
                sinteseVO.setDescricaoRiscoSintese(getRiscoSinteseLabel(parametroGRC, sinteseDeRisco));
                sinteseVO.setDescricaoSinteseVigente(normalizaTexto(getSinteseVigente(sinteseDeRisco)));
                if (TipoParametroGrupoRiscoControleEnum.EXTERNO.equals(parametroGRC.getTipoGrupo())) {
                    sinteseVO.setCampo(isRelatorioResumido ? "" : "Nota:  ");
                } else {
                    sinteseVO.setCampo("Risco: ");
                }
                dadosRelatorioSintese.add(sinteseVO);
            }
        }
    }

    private String montarRodape() {
        StringBuffer rodape = new StringBuffer();
        rodape.append("Versão ");
        rodape.append(perfilRisco.getDataCriacao() == null ? "" : perfilRisco.getDataCriacao().toString(
                Constantes.FORMATO_DATA_COM_BARRAS));
        rodape.append(TRACO);
        rodape.append("Ciclo ");
        rodape.append(perfilRisco.getCiclo().getDataInicioFormatada());
        rodape.append(" ~ ");
        rodape.append(perfilRisco.getCiclo().getDataPrevisaoFormatada());
        return rodape.toString();

    }

    private String getSinteseVigente(SinteseDeRisco sinteseDeRisco) {
        return sinteseDeRisco == null || sinteseDeRisco.getDescricaoSintese() == null ? "" : sinteseDeRisco
                .getDescricaoSintese();
    }

    private String getRiscoSinteseLabel(ParametroGrupoRiscoControle parametroGrupoRisco, SinteseDeRisco sinteseDeRisco) {
        String riscoVigente = "";

        if (TipoParametroGrupoRiscoControleEnum.EXTERNO.equals(parametroGrupoRisco.getTipoGrupo())
                && !isRelatorioResumido) {
            AvaliacaoRiscoControle arcExterno = 
                   AvaliacaoRiscoControleMediator.get().buscarArcExternoPorPerfilRisco(perfilRisco.getPk());
            riscoVigente = arcExterno.getNotaVigenteDescricaoValor();
            riscoVigente = 
                    AvaliacaoRiscoControleMediator.get().notaArc(
                            arcExterno, perfilRisco.getCiclo(), perfilMenu, perfilRisco);
        } else {

            if (sinteseDeRisco != null && sinteseDeRisco.getRisco() != null) {
                riscoVigente = Constantes.ESPACO_EM_BRANCO + sinteseDeRisco.getRisco().getDescricao();
            } else {

            List<ParametroGrupoRiscoControle> listaParamentro = new ArrayList<ParametroGrupoRiscoControle>();
            listaParamentro.add(parametroGrupoRisco);

            List<VersaoPerfilRisco> versaoPerfilRiscos =
                    VersaoPerfilRiscoMediator.get().buscarVersoesPerfilRisco(perfilRisco.getPk(),
                            TipoObjetoVersionadorEnum.CELULA_RISCO_CONTROLE);
                List<CelulaRiscoControleVO> celulas =
                        CelulaRiscoControleMediator.get().buscarCelulaPorVersaoPerfilVO(versaoPerfilRiscos);
            riscoVigente = Constantes.ESPACO_EM_BRANCO 
                    + SinteseDeRiscoMediator.get().obterDescricaoRisco(celulas, matriz, versaoPerfilRiscos, true,
                            listaParamentro, new LinkedList<LinhaNotasMatrizVO>(), new LinkedList<LinhaNotasMatrizVO>(),
                            perfilMenu, perfilRisco);
            }
        }
        return riscoVigente;
    }
    
    @Override
    protected void adicionarParametros(Map<String, Object> parametrosJasper, AbstractBacenWebPage paginaAtual) {
        WebApplication wa = (WebApplication) paginaAtual.getApplication();
        JasperReport subRelatorioSinteses = null;
        parametrosJasper.put("rodape", relatorioSinteses.getRodape());

        try {
            subRelatorioSinteses =
                    (JasperReport) JRLoader.loadObject(wa.getServletContext().getResourceAsStream(
                            "/relatorios/SubrelatorioSinteses.jasper"));
        } catch (JRException e) {
            throw new InfraException("Erro ao carregar o subrelatorio de sínteses.", e);
        }

        parametrosJasper.put("SUB_RELATORIO_SINTESES", subRelatorioSinteses);
    }

}
