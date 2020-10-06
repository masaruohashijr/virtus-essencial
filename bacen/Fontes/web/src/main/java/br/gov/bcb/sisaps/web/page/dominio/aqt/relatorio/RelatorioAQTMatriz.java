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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

import org.apache.wicket.protocol.http.WebApplication;

import br.gov.bcb.sisaps.src.dominio.AnexoDocumento;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnexoAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ElementoAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ItemElementoAQT;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoEntidadeAnexoDocumentoEnum;
import br.gov.bcb.sisaps.src.mediator.AnexoDocumentoMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnexoAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.ElementoAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.ItemElementoAQTMediator;
import br.gov.bcb.sisaps.src.vo.relatorio.RelatorioAnefVO;
import br.gov.bcb.sisaps.src.vo.relatorio.RelatorioElementoVO;
import br.gov.bcb.sisaps.src.vo.relatorio.RelatorioItemVO;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.Util;
import br.gov.bcb.sisaps.util.validacao.InfraException;
import br.gov.bcb.sisaps.web.page.componentes.util.RelatorioAbstrato;
import br.gov.bcb.wicket.pages.AbstractBacenWebPage;

/**
 * Relatorio de Pendencias.
 */
public class RelatorioAQTMatriz extends RelatorioAbstrato {
    
    /**
     * Título do relatório.
     */
    public static final String TITULO_RELATORIO = "Relatório de ANEF da Matriz";
    
    /**
     * Nome do arquivo do relatório enviado ao usuário.
     */
    public static final String NOME_ARQUIVO_RELATORIO = "RelatorioImpressaoAnef";

    private static final String COREC = " (Corec)";

    private static final String NOME_ARQUIVO_JASPER = "/relatorios/RelatorioImpressaoAnef.jasper";
    private static final String TRACO = " - ";
    private List<byte[]> anexoElementoArc = new ArrayList<byte[]>();

    private Map<String, List<byte[]>> mapAnexoElemento = new HashMap<String, List<byte[]>>();
    private List<RelatorioElementoVO> dadosRelatorioElementos;
    private List<RelatorioAnefVO> dadosRelatorioArc;
    private final RelatorioAnefVO relatorioAnef = new RelatorioAnefVO();
    private final AnaliseQuantitativaAQT anef;
    private final Ciclo ciclo;

    private int contElemento;
    private int contElementoItem;
    private final String inicioTitulo;
    private final PerfilAcessoEnum perfilmenu;

    private final boolean isPerfilRiscoAtual;

    public RelatorioAQTMatriz(AnaliseQuantitativaAQT anef, Ciclo ciclo, String inicioTitulo, 
            PerfilAcessoEnum perfilmenu, boolean isPerfilRiscoAtual) {
        this.isPerfilRiscoAtual = isPerfilRiscoAtual;
        this.anef = AnaliseQuantitativaAQTMediator.get().buscar(anef.getPk());
        this.ciclo = ciclo;
        this.inicioTitulo = inicioTitulo;
        this.perfilmenu = perfilmenu;
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
        dadosRelatorioArc = new ArrayList<RelatorioAnefVO>();
        dadosRelatorioElementos = new ArrayList<RelatorioElementoVO>();

        relatorioAnef.setNomeAnef(anef.getParametroAQT().getDescricao());
        relatorioAnef.setNomeConglomerado(ciclo.getEntidadeSupervisionavel().getNomeConglomeradoFormatado());

        relatorioAnef.setNotaAjustada(montarNotaAjustada());

        relatorioAnef.setNotaCalculada(anef.getNotaCalculadaSupervisorOuInspetor());

        relatorioAnef.setRodape(montarRodape());
        relatorioAnef.setAnexoAnef(!Util.isNuloOuVazio(anef.getAnexosAqt()));
        montarListaElementos();
        relatorioAnef.setElementos(dadosRelatorioElementos);
        relatorioAnef.setNomeChaveAnef("anef" + anef.getPk());
        addAnexoDoAnef();
        dadosRelatorioArc.add(relatorioAnef);

    }

    private String montarNotaAjustada() {

       String notaCorecAjustada =
                AnaliseQuantitativaAQTMediator.get().notaAjustadaFinal(anef, perfilmenu, isPerfilRiscoAtual);
       
        if (notaCorecAjustada.contains(COREC)) {
            relatorioAnef.setJustificativaNotaAjustada("");
            return perfilmenu.equals(PerfilAcessoEnum.CONSULTA_TUDO) ? 
                    notaCorecAjustada.replace(COREC, Constantes.ESPACO_EM_BRANCO) : notaCorecAjustada;
        }
       
        relatorioAnef.setJustificativaNotaAjustada(anef.getAvaliacaoANEF() == null ? "" : normalizaTexto(anef
                .getAvaliacaoANEF().getJustificativa()));

        return anef.getAvaliacaoANEF() == null ? "" : anef.getAvaliacaoANEF().getValorFormatado();
    }

    private void addAnexoDoAnef() {
        if (!Util.isNuloOuVazio(anef.getAnexosAqt())) {
            List<byte[]> anexoAnef = new ArrayList<byte[]>();
            for (AnexoAQT anexo : anef.getAnexosAqt()) {
                anexoAnef.add(AnexoAQTMediator.get().recuperarArquivo(anexo.getLink(), ciclo, anef));
            }
            mapAnexoElemento.put(relatorioAnef.getNomeChaveAnef(), anexoAnef);
        }
    }

    private void montarListaElementos() {
        List<ElementoAQT> elementos = ElementoAQTMediator.get().buscarElementosOrdenadosDoAnef(anef.getPk());

        if (!Util.isNuloOuVazio(elementos)) {
            contElemento = 0;
            for (ElementoAQT elemento : elementos) {
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

    private List<RelatorioItemVO> montarListaItensElemento(int contElemento2, ElementoAQT elemento) {
        List<RelatorioItemVO> itens = new ArrayList<RelatorioItemVO>();
        List<ItemElementoAQT> listaItem = ItemElementoAQTMediator.get().buscarItensOrdenadosDoElemento(elemento);

        if (!Util.isNuloOuVazio(listaItem)) {
            contElementoItem = 0;

            for (ItemElementoAQT itemElemento : listaItem) {
                contElementoItem++;
                RelatorioItemVO itemVo = new RelatorioItemVO();
                itemVo.setNomeItem(montarNomeItemElemento(contElemento2, contElementoItem, itemElemento
                        .getParametroItemElemento().getNome()));
                if (itemElemento.getDocumento() != null) {
                    itemVo.setAvaliacaoInspetor(normalizaTexto(itemElemento.getDocumento().getJustificativa()));
                    boolean possuiAnexos = !Util.isNuloOuVazio(itemElemento.getDocumento().getAnexosItemElemento());
                    itemVo.setAnexos(possuiAnexos);
                    itemVo.setNomeChave("itemElemento" + elemento.getPk() + itemElemento.getPk());
                    if (possuiAnexos) {
                        List<byte[]> anexoElementoArc = new ArrayList<byte[]>();
                        for (AnexoDocumento anexo : itemElemento.getDocumento().getAnexosItemElemento()) {
                            byte[] arquivoBytes =
                                    AnexoDocumentoMediator.get().recuperarArquivo(anexo.getLink(), itemElemento,
                                            TipoEntidadeAnexoDocumentoEnum.ITEM_ELEMENTO_AQT, ciclo);
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
        tituloItemElemento.append(inicioTitulo);
        tituloItemElemento.append(contElementoItem2);
        tituloItemElemento.append(Constantes.ESPACO_EM_BRANCO);
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
        tituloInspetor.append(inicioTitulo);
        tituloInspetor.append(" Itens avaliados pelo Inspetor");
        return tituloInspetor.toString();
    }

    private void addInicioTitulo(int contElemento2, StringBuffer tituloString) {
        tituloString.append(inicioTitulo);
        tituloString.append(contElemento2);
        tituloString.append(".");
    }

    private String montarNomeElemento(int contElemento2, ElementoAQT elemento) {
        StringBuffer nomeElemento = new StringBuffer();
        addInicioTitulo(contElemento2, nomeElemento);
        nomeElemento.append(Constantes.ESPACO_EM_BRANCO);
        nomeElemento.append(elemento.getParametroElemento().getDescricao());
        return nomeElemento.toString();
    }

    private String montarRodape() {
        StringBuffer rodape = new StringBuffer();
        rodape.append("Versão ");
        rodape.append(anef.getDataFormatada());
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
        parametrosJasper.put("nomeAnef", relatorioAnef.getNomeAnef());
        parametrosJasper.put("nomeChaveAnef", relatorioAnef.getNomeChaveAnef());
        parametrosJasper.put("notaCalculada", relatorioAnef.getNotaCalculada());
        parametrosJasper.put("notaAjustada", relatorioAnef.getNotaAjustada());
        parametrosJasper.put("booleanAjustada", !"".equals(relatorioAnef.getNotaAjustada()));
        parametrosJasper.put("booleanJustifucativaAjustada", !"".equals(relatorioAnef.getJustificativaNotaAjustada()));
        parametrosJasper.put("justificativaNotaAjustada", relatorioAnef.getJustificativaNotaAjustada());
        parametrosJasper.put("rodape", relatorioAnef.getRodape());
        parametrosJasper.put("inicioTitulo", inicioTitulo 
                + Constantes.ESPACO_EM_BRANCO + relatorioAnef.getNomeAnef().toUpperCase());

        try {
            subRelatorioArcs =
                    (JasperReport) JRLoader.loadObject(wa.getServletContext().getResourceAsStream(
                            "/relatorios/RelatorioImpressaoAnef_Elementos.jasper"));
        } catch (JRException e) {
            throw new InfraException("Erro ao carregar o subrelatorio de elementos do arc.", e);
        }

        parametrosJasper.put("SUB_RELATORIO", subRelatorioArcs);

        try {
            subRelatorioElemento =
                    (JasperReport) JRLoader.loadObject(wa.getServletContext().getResourceAsStream(
                            "/relatorios/RelatorioImpressaoAnef_ItemElemento.jasper"));
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
