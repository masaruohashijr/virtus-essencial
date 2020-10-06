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

package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.relatorioArc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.protocol.http.WebApplication;

import br.gov.bcb.sisaps.src.dominio.AnexoARC;
import br.gov.bcb.sisaps.src.dominio.AnexoDocumento;
import br.gov.bcb.sisaps.src.dominio.Atividade;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.CelulaRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Elemento;
import br.gov.bcb.sisaps.src.dominio.ItemElemento;
import br.gov.bcb.sisaps.src.dominio.ParametroGrupoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoEntidadeAnexoDocumentoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoGrupoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoUnidadeAtividadeEnum;
import br.gov.bcb.sisaps.src.mediator.AnexoArcMediator;
import br.gov.bcb.sisaps.src.mediator.AnexoDocumentoMediator;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleExternoMediator;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.CelulaRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.ElementoMediator;
import br.gov.bcb.sisaps.src.mediator.ItemElementoMediator;
import br.gov.bcb.sisaps.src.vo.relatorio.RelatorioArcVO;
import br.gov.bcb.sisaps.src.vo.relatorio.RelatorioElementoVO;
import br.gov.bcb.sisaps.src.vo.relatorio.RelatorioItemVO;
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
public class RelatorioArcMatriz extends RelatorioAbstrato {

    /**
     * Título do relatório.
     */
    public static final String TITULO_RELATORIO = "Relatório de Arc da Matriz";

    /**
     * Nome do arquivo do relatório enviado ao usuário.
     */
    public static final String NOME_ARQUIVO_RELATORIO = "RelatorioImpressaoArc";

    private static final String NOME_ARQUIVO_JASPER = "/relatorios/RelatorioImpressaoArc.jasper";
    private static final String TRACO = " - ";
    private static final String INICIO_TITULO = "1.";
    private static final String ESPACO = " ";
    private List<byte[]> anexoElementoArc = new ArrayList<byte[]>();

    private Map<String, List<byte[]>> mapAnexoElemento = new HashMap<String, List<byte[]>>();
    private List<RelatorioElementoVO> dadosRelatorioElementos;
    private List<RelatorioArcVO> dadosRelatorioArc;
    private final RelatorioArcVO relatorioAvaliacao = new RelatorioArcVO();
    private final AvaliacaoRiscoControle arcVigente;
    private final AvaliacaoRiscoControle arcPerfil;
    private final Ciclo ciclo;
    private Atividade atividade;
    private final PerfilAcessoEnum perfilMenu;

    private int contElemento;
    private int contElementoItem;

    private final boolean isPerfilRiscoAtual;

    public RelatorioArcMatriz(AvaliacaoRiscoControle arcVigente, AvaliacaoRiscoControle arcPerfil, Ciclo ciclo,
            Atividade atividade, PerfilAcessoEnum perfilMenu, boolean isPerfilRiscoAtual) {
        this.arcVigente = arcVigente;
        this.arcPerfil = arcPerfil;
        this.ciclo = ciclo;
        this.atividade = atividade;
        this.perfilMenu = perfilMenu;
        this.isPerfilRiscoAtual = isPerfilRiscoAtual;
        converterDados();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<?> getDadosRelatorio() {
        return dadosRelatorioArc;
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
        dadosRelatorioArc = new ArrayList<RelatorioArcVO>();
        dadosRelatorioElementos = new ArrayList<RelatorioElementoVO>();

        ParametroGrupoRiscoControle grupo = null;
        if (this.atividade == null) {
            grupo = AvaliacaoRiscoControleExternoMediator.get().buscarArcExterno(arcVigente.getPk())
                    .getParametroGrupoRiscoControle();
        } else {
            CelulaRiscoControle celulaRiscoControle =
                    CelulaRiscoControleMediator.get().buscarCelularPorAvaliacao(arcVigente);
            grupo = celulaRiscoControle.getParametroGrupoRiscoControle();
            this.atividade = celulaRiscoControle.getAtividade();
        }
        relatorioAvaliacao.setNomeArc(TipoGrupoEnum.EXTERNO.equals(arcVigente.getTipo()) ? grupo.getNomeAbreviado() 
                : TipoGrupoEnum.RISCO.equals(arcVigente.getTipo()) ? grupo.getNomeRisco() : grupo.getNomeControle());

        relatorioAvaliacao.setNomeConglomerado(ciclo.getEntidadeSupervisionavel().getNomeConglomeradoFormatado());

        if (ciclo != null) {
            System.out.println("##BUGANEXO ciclo: " + ciclo.getPk());

            if (ciclo.getEntidadeSupervisionavel() != null) {
                System.out.println(
                        "##BUGANEXO getEntidadeSupervisionavel: " + ciclo.getEntidadeSupervisionavel().getPk());
            }
        }

        if (arcVigente != null) {
            System.out.println("##BUGANEXO arcVigente: " + arcVigente.getPk());
        }

        relatorioAvaliacao.setNotaAjustada(montarNotaAjustada());
        relatorioAvaliacao.setNotaCalculada(AvaliacaoRiscoControleMediator.get().getNotaCalculadaFinal(arcVigente));
        relatorioAvaliacao.setTendencia(arcVigente.getTendenciaARCInspetorOuSupervisorValor());
        relatorioAvaliacao.setJustificativatendencia(normalizaTexto(arcVigente
                .getTendenciaARCInspetorOuSupervisorJustificativa()));
        relatorioAvaliacao.setNomeUnidadeAtividade(montarNomeUnidadeAtividade());
        relatorioAvaliacao.setRodape(montarRodape());
        relatorioAvaliacao.setAnexoArc(!Util.isNuloOuVazio(arcVigente.getAnexosArc()));
        montarListaElementos();
        relatorioAvaliacao.setElementos(dadosRelatorioElementos);
        relatorioAvaliacao.setNomeChaveArc("arc" + arcVigente.getPk());
        addAnexoDoArc();
        dadosRelatorioArc.add(relatorioAvaliacao);

    }

    private String montarNotaAjustada() {

        String nota =
                AvaliacaoRiscoControleMediator.get().notaArcIndicadorCorec(arcPerfil, ciclo, perfilMenu,
                        !perfilMenu.equals(PerfilAcessoEnum.CONSULTA_TUDO), isPerfilRiscoAtual);

        if (!"".equals(nota)) {
            relatorioAvaliacao.setJustificativaNotaAjustada("");
            return nota;
        }

        relatorioAvaliacao.setJustificativaNotaAjustada(arcVigente.getAvaliacaoARC() == null ? ""
                : normalizaTexto(arcVigente.getAvaliacaoARC().getJustificativa()));

        return arcVigente.getAvaliacaoARC() == null ? "" : arcVigente.getAvaliacaoARC().getValorFormatado();
    }

    private void addAnexoDoArc() {
        if (!Util.isNuloOuVazio(arcVigente.getAnexosArc())) {
            List<byte[]> anexoElementoArc = new ArrayList<byte[]>();
            for (AnexoARC anexo : arcVigente.getAnexosArc()) {
                System.out.println("##BUGANEXO anexoArc: " + anexo.getPk() + " " + anexo.getLink());
                anexoElementoArc.add(AnexoArcMediator.get().recuperarArquivo(anexo.getLink(), ciclo, arcVigente));
            }
            mapAnexoElemento.put(relatorioAvaliacao.getNomeChaveArc(), anexoElementoArc);
        }
    }

    private void montarListaElementos() {
        if (!Util.isNuloOuVazio(arcVigente.getElementos())) {
            contElemento = 0;
            for (Elemento elemento : ElementoMediator.get().buscarElementosOrdenadosDoArc(arcVigente.getPk())) {
                contElemento++;
                RelatorioElementoVO elementoVo = new RelatorioElementoVO();

                elementoVo.setNomeElemento(montarNomeElemento(contElemento, elemento));
                elementoVo.setNotaElemento(elemento.getNotaSupervisor());
                elementoVo.setNomeAvaliacaoInspetorItem(montarTituloInspetor(contElemento));
                elementoVo.setNomeAnaliseSupervisor(montarTituloAnaliseSupervisor(contElemento));
                elementoVo.setJustificativaAnaliseSupervisor(normalizaTexto(elemento.getJustificativaSupervisor()));
                elementoVo.setItens(montarListaItensElemento(contElemento, elemento));
                dadosRelatorioElementos.add(elementoVo);

            }
        }

    }

    private List<RelatorioItemVO> montarListaItensElemento(int contElemento2, Elemento elemento) {
        List<RelatorioItemVO> itens = new ArrayList<RelatorioItemVO>();

        if (!Util.isNuloOuVazio(elemento.getItensElemento())) {
            contElementoItem = 0;
            for (ItemElemento itemElemento : ItemElementoMediator.get().buscarItensOrdenadosDoElemento(elemento)) {

                System.out.println("##BUGANEXO itemElemento: " + itemElemento.getPk());

                contElementoItem++;
                RelatorioItemVO itemVo = new RelatorioItemVO();
                itemVo.setNomeItem(montarNomeItemElemento(contElemento2, contElementoItem, itemElemento
                        .getParametroItemElemento().getNome()));
                if (itemElemento.getDocumento() != null) {
                    System.out.println("##BUGANEXO documento: " + itemElemento.getDocumento().getPk());
                    itemVo.setAvaliacaoInspetor(normalizaTexto(itemElemento.getDocumento().getJustificativa()));
                    boolean possuiAnexos = !Util.isNuloOuVazio(itemElemento.getDocumento().getAnexosItemElemento());
                    itemVo.setAnexos(possuiAnexos);
                    itemVo.setNomeChave("itemElemento" + elemento.getPk() + itemElemento.getPk());
                    System.out.println("##BUGANEXO setNomeChave: " + elemento.getPk() + " " + itemElemento.getPk());
                    if (possuiAnexos) {
                        List<byte[]> anexoElementoArc = new ArrayList<byte[]>();
                        for (AnexoDocumento anexo : itemElemento.getDocumento().getAnexosItemElemento()) {
                            System.out.println("##BUGANEXO anexoDocumento: " + anexo.getPk() + " " + anexo.getLink());
                            System.out.println("##BUGANEXO anexoDocumento ciclo: " + ciclo.getPk());
                            byte[] arquivoBytes =
                                    AnexoDocumentoMediator.get().recuperarArquivo(anexo.getLink(), itemElemento,
                                            TipoEntidadeAnexoDocumentoEnum.ITEM_ELEMENTO, ciclo);
                            anexoElementoArc.add(arquivoBytes);
                        }
                        mapAnexoElemento.put(itemVo.getNomeChave(), anexoElementoArc);
                    }
                }
                itens.add(itemVo);
            }
        }
        return itens;
    }

    private String montarNomeItemElemento(int contElemento2, int contElementoItem2, String nome) {
        StringBuffer tituloItemElemento = new StringBuffer();
        addInicioTitulo(contElemento2, tituloItemElemento);
        tituloItemElemento.append(INICIO_TITULO);
        tituloItemElemento.append(contElementoItem2);
        tituloItemElemento.append(ESPACO);
        tituloItemElemento.append(nome);
        return tituloItemElemento.toString();
    }

    private String montarTituloAnaliseSupervisor(int contElemento2) {
        StringBuffer tituloSupervisor = new StringBuffer();
        addInicioTitulo(contElemento2, tituloSupervisor);
        tituloSupervisor.append("2.");
        tituloSupervisor.append(" Análise do Supervisor");
        return tituloSupervisor.toString();
    }

    private String montarTituloInspetor(int contElemento2) {
        StringBuffer tituloInspetor = new StringBuffer();
        addInicioTitulo(contElemento2, tituloInspetor);
        tituloInspetor.append(INICIO_TITULO);
        tituloInspetor.append(" Itens avaliados pelo Inspetor");
        return tituloInspetor.toString();
    }

    private void addInicioTitulo(int contElemento2, StringBuffer tituloString) {
        tituloString.append(INICIO_TITULO);
        tituloString.append(contElemento2);
        tituloString.append(".");
    }

    private String montarNomeElemento(int contElemento2, Elemento elemento) {
        StringBuffer nomeElemento = new StringBuffer();
        addInicioTitulo(contElemento2, nomeElemento);
        nomeElemento.append(ESPACO);
        nomeElemento.append(elemento.getParametroElemento().getNome());
        return nomeElemento.toString();
    }

    private String montarNomeUnidadeAtividade() {
        StringBuffer nome = new StringBuffer("");
        if (atividade != null) {
            if (atividade.getUnidade() != null
                    && atividade.getUnidade().getTipoUnidade().equals(TipoUnidadeAtividadeEnum.NEGOCIO)) {
                nome.append(atividade.getUnidade().getNome());
                nome.append(TRACO);
            }
            nome.append(atividade.getNome());
        }

        return nome.toString();

    }

    private String montarRodape() {
        StringBuffer rodape = new StringBuffer();
        rodape.append("Versão ");
        rodape.append(arcVigente.getDataFormatada());
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
        JasperReport subRelatorioArcs = null;
        JasperReport subRelatorioElemento = null;
        parametrosJasper.put("nomeArc", relatorioAvaliacao.getNomeArc());
        parametrosJasper.put("nomeChaveArc", relatorioAvaliacao.getNomeChaveArc());
        parametrosJasper.put("notaCalculada", relatorioAvaliacao.getNotaCalculada());
        parametrosJasper.put("notaAjustada", relatorioAvaliacao.getNotaAjustada());
        parametrosJasper.put("booleanAjustada", !"".equals(relatorioAvaliacao.getNotaAjustada()));
        parametrosJasper.put("booleanJustifucativaAjustada",
                !"".equals(relatorioAvaliacao.getJustificativaNotaAjustada()));
        parametrosJasper.put("justificativaNotaAjustada", relatorioAvaliacao.getJustificativaNotaAjustada());
        parametrosJasper.put("tendencia", relatorioAvaliacao.getTendencia());
        parametrosJasper.put("justificativaTendencia", relatorioAvaliacao.getJustificativatendencia());
        parametrosJasper.put("rodape", relatorioAvaliacao.getRodape());

        try {
            subRelatorioArcs =
                    (JasperReport) JRLoader.loadObject(wa.getServletContext().getResourceAsStream(
                            "/relatorios/RelatorioImpressaoArc_Elementos.jasper"));
        } catch (JRException e) {
            throw new InfraException("Erro ao carregar o subrelatorio de elementos do arc.", e);
        }

        parametrosJasper.put("SUB_RELATORIO", subRelatorioArcs);

        try {
            subRelatorioElemento =
                    (JasperReport) JRLoader.loadObject(wa.getServletContext().getResourceAsStream(
                            "/relatorios/RelatorioImpressaoElementosItem.jasper"));
        } catch (JRException e) {
            throw new InfraException("Erro ao carregar o subrelatorio de items dos elementos.", e);
        }

        parametrosJasper.put("SUB_RELATORIO_ITEM", subRelatorioElemento);

    }

    public List<byte[]> getAnexoElementoArc() {
        return anexoElementoArc;
    }

    public void setAnexoElementoArc(List<byte[]> anexoElementoArc) {
        this.anexoElementoArc = anexoElementoArc;
    }

    public Map<String, List<byte[]>> getMapAnexoElemento() {
        return mapAnexoElemento;
    }

    public void setMapAnexoElemento(Map<String, List<byte[]>> mapAnexoElemento) {
        this.mapAnexoElemento = mapAnexoElemento;
    }

}
