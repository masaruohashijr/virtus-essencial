package br.gov.bcb.sisaps.web.page.dominio.ciclo.painel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.gov.bcb.app.stuff.seguranca.UsuarioCorrente;
import br.gov.bcb.sisaps.seguranca.UsuarioAplicacao;
import br.gov.bcb.sisaps.src.dominio.Atividade;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.ParametroGrupoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.ParametroNota;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.VersaoPerfilRisco;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoARCEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoGrupoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoLinhaMatrizVOEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoObjetoVersionadorEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TituloTelaARCEnum;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleExternoMediator;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.CelulaRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.LinhaMatrizMediator;
import br.gov.bcb.sisaps.src.mediator.MatrizCicloMediator;
import br.gov.bcb.sisaps.src.mediator.ParametroGrupoRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.ParametroNotaMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.src.mediator.VersaoPerfilRiscoMediator;
import br.gov.bcb.sisaps.src.vo.ArcNotasVO;
import br.gov.bcb.sisaps.src.vo.CelulaRiscoControleVO;
import br.gov.bcb.sisaps.src.vo.LinhaMatrizVO;
import br.gov.bcb.sisaps.src.vo.LinhaNotasMatrizVO;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.Util;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.util.ConstantesWeb;
import br.gov.bcb.sisaps.web.page.dominio.matriz.PainelAnaliseRiscoControleGovernanca;
import br.gov.bcb.sisaps.web.page.dominio.matriz.PainelResumoMatriz;
import br.gov.bcb.sisaps.web.page.dominio.perfilderisco.apresentacao.ApresentacaoPage;

@SuppressWarnings("serial")
public class PainelDadosMatrizPercentual extends PainelSisAps {

    private static final String NOME_MATRIZ = "nomeMatriz";
    private static final String STYLE = "style";
    private static final String BGCOLOR = "bgcolor";
    private static final String T_PAINEL_CORPO = "tPainelCorpo";
    private static final String ESPACO = " ";
    private static final String CLASS = "class";
    private List<Atividade> listaAtividade;
    private List<CelulaRiscoControleVO> listaChoices;
    private List<LinhaMatrizVO> linhaMatrizVO;
    @SpringBean
    private LinhaMatrizMediator linhaMatrizMediator;
    @SpringBean
    private PerfilRiscoMediator perfilRiscoMediator;
    @SpringBean
    private VersaoPerfilRiscoMediator versaoPerfilRiscoMediator;
    @SpringBean
    private MatrizCicloMediator matrizCicloMediator;

    private boolean perfilAtual = true;

    private Matriz matriz;

    private PerfilRisco perfilRisco;

    private List<VersaoPerfilRisco> versoesPerfilRiscoCelulas;
    private String titulo;
    private WebMarkupContainer corpo;
    private final boolean emAnalise;
    private final boolean mostrarResumoInferior;
    private boolean isExibirStringAAvaliar;
    private boolean isExibirNotaAjustada;
    private final boolean isExibirLinkArc;

    private List<LinhaNotasMatrizVO> notaResiduais;
    private List<LinhaNotasMatrizVO> mediaResiduais;
    private String notaCalculadaFinal;
    private String notaCalculadaMatriz;
    private PainelDadosMatrizPercentual painelVigente;
    private PerfilAcessoEnum perfilPorMenu;
    private PainelResumoMatriz painelResumoMatriz;
    private PainelAnaliseRiscoControleGovernanca painelAnaliseRiscoControleGovernanca;

    public PainelDadosMatrizPercentual(String id, PerfilRisco perfilRisco, String titulo, Matriz matriz,
            boolean emAnalise, boolean mostrarResumoInferior, boolean isExibirLinkArc) {

        super(id);
        this.perfilRisco = perfilRisco;
        this.titulo = titulo;
        this.matriz = matriz;
        this.emAnalise = emAnalise;
        this.mostrarResumoInferior = mostrarResumoInferior;
        this.isExibirStringAAvaliar = mostrarResumoInferior;
        this.isExibirLinkArc = isExibirLinkArc;
    }

    public PainelDadosMatrizPercentual(String id, PerfilRisco perfilRisco, String titulo,
            boolean mostrarResumoInferior, boolean isExibirNotaAjustada, boolean isExibirLinkArc) {

        super(id);
        this.perfilRisco = perfilRisco;
        this.titulo = titulo;
        this.emAnalise = false;
        this.mostrarResumoInferior = mostrarResumoInferior;
        this.isExibirNotaAjustada = isExibirNotaAjustada;
        this.isExibirLinkArc = isExibirLinkArc;
    }

    public PainelDadosMatrizPercentual(String id, PerfilRisco perfilRisco, boolean emAnalise, String titulo,
            boolean mostrarResumoInferior, boolean isExibirLinkArc) {
        super(id);
        this.perfilRisco = perfilRisco;
        this.titulo = titulo;
        this.emAnalise = emAnalise;
        this.mostrarResumoInferior = mostrarResumoInferior;
        this.isExibirStringAAvaliar = mostrarResumoInferior;
        this.isExibirLinkArc = isExibirLinkArc;
    }

    public PainelDadosMatrizPercentual(String id, PerfilRisco perfilRisco, String titulo, boolean emAnalise,
            boolean mostrarResumoInferior, boolean isExibirLinkArc, PainelDadosMatrizPercentual painelVigente) {
        super(id);
        this.perfilRisco = perfilRisco;
        this.titulo = titulo;
        this.emAnalise = emAnalise;
        this.mostrarResumoInferior = mostrarResumoInferior;
        this.isExibirStringAAvaliar = mostrarResumoInferior;
        this.isExibirLinkArc = isExibirLinkArc;
        this.painelVigente = painelVigente;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        perfilPorMenu = getPerfilPorPagina();
        inicializarDados();
        addTituloMatriz();
        addListNomeGrupoRiscoEControle();
        addListPercentualGrupoRiscoEControle();
        addListRiscoEControle();
        addListUnidadeEAtividades();
        addListNotasResiduais();
        setVisibilityAllowed(matriz != null);
        addOrReplace(corpo);
        addGovernancaCorporativa();
        addResumoInferior();
        addTituloClicarNosLink();
        addNotaMatriz();
    }

    private void addTituloMatriz() {
        addOrReplace(new Label(NOME_MATRIZ, titulo));
    }

    private void addTituloClicarNosLink() {
        corpo.addOrReplace(new Label("labelPossuiLink", isExibirLinkArc ? "Clique nas notas para ler o ARC." : ""));
    }

    private void inicializarDados() {
        listaAtividade = new LinkedList<Atividade>();
        notaResiduais = new LinkedList<LinhaNotasMatrizVO>();
        mediaResiduais = new LinkedList<LinhaNotasMatrizVO>();
        setOutputMarkupPlaceholderTag(true);
        corpo = new WebMarkupContainer(T_PAINEL_CORPO);
        int cicloPK;
        if (perfilRisco == null) {
            this.matriz = matrizCicloMediator.load(matriz.getPk());
            cicloPK = matriz.getCiclo().getPk();
        } else {
            this.matriz = perfilRiscoMediator.getMatrizAtualPerfilRisco(perfilRisco);
            cicloPK = perfilRisco.getCiclo().getPk();
            this.versoesPerfilRiscoCelulas =
                    versaoPerfilRiscoMediator.buscarVersoesPerfilRisco(perfilRisco.getPk(),
                            TipoObjetoVersionadorEnum.CELULA_RISCO_CONTROLE);
            PerfilRisco perfilRiscoAtual =
                    PerfilRiscoMediator.get().obterPerfilRiscoAtual(perfilRisco.getCiclo().getPk());
            perfilAtual = perfilRiscoAtual.equals(perfilRisco);
        }

        if (matriz != null) {
            this.matriz.setCiclo(CicloMediator.get().buscarCicloPorPK(cicloPK));
        }

        linhaMatrizVO = new ArrayList<LinhaMatrizVO>();
        linhaMatrizVO.addAll(obterListaLinhaMatrizNegocio());
        linhaMatrizVO.addAll(obterListaLinhaMatrizVOAtividades());
        linhaMatrizVO.addAll(obterLinhasMatrizCorporativa());
        listaChoices =
                CelulaRiscoControleMediator.get().buscarParametroDaMatrizVO(listaAtividade, versoesPerfilRiscoCelulas);
        corpo.setVisible(!Util.isNuloOuVazio(listaChoices));
        notaCalculadaFinal = null;
        if (matriz != null) {
            corpo.setMarkupId(T_PAINEL_CORPO + matriz.getEstadoMatriz().getDescricao());
            linhaMatrizMediator.montarListaNotasEMediasResiduaisVO(matriz, versoesPerfilRiscoCelulas, !isEmAnalise(),
                    getParametroGrupo(), notaResiduais, mediaResiduais, listaChoices, perfilPorMenu, perfilRisco);

            ArcNotasVO arcExternoVO = null;

            if (perfilRisco == null) {
                arcExternoVO =
                        AvaliacaoRiscoControleExternoMediator.get().buscarUltimoArcExternoVO(matriz.getCiclo().getPk());
            } else {
                arcExternoVO = AvaliacaoRiscoControleMediator.get().consultarNotasArcExterno(perfilRisco.getPk());
            }

            notaCalculadaMatriz =
                    matrizCicloMediator.notaCalculadaVO(matriz, listaChoices, versoesPerfilRiscoCelulas, !isEmAnalise(),
                            perfilPorMenu, perfilRisco);

            notaCalculadaFinal =
                    matrizCicloMediator.notaCalculadaFinalVO(matriz, listaChoices, versoesPerfilRiscoCelulas,
                            !isEmAnalise(), perfilPorMenu, perfilRisco, arcExternoVO);
        }
    }

    private void addGovernancaCorporativa() {
        painelAnaliseRiscoControleGovernanca =
                new PainelAnaliseRiscoControleGovernanca("infoGovernancaCorporativa", perfilPorMenu, this,
                        titulo.contains("recalculada") ? "Governança corporativa recalculada (ARC analisado)"
                                : titulo.contains("edição") ? "Governança corporativa em edição (nota vigente)"
                                        : titulo.contains("vigente") ? "Governança corporativa vigente"
                                                : "Governança corporativa") {
                    @Override
                    protected void onConfigure() {
                        super.onConfigure();
                        setVisibilityAllowed(matriz != null && matriz.getPercentualGovernancaCorporativoInt() > 0);
                    }
                };
        addOrReplace(painelAnaliseRiscoControleGovernanca);
    }

    private void addNotaMatriz() {
        WebMarkupContainer colunaNotaCalculada = new WebMarkupContainer("colunaNotaCalculada") {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(isResumosVisiveis());
            }
        };
        colunaNotaCalculada.addOrReplace(new Label("notaMatriz", formatarNota(notaCalculadaMatriz)));
        addOrReplace(colunaNotaCalculada);
    }

    private String formatarNota(String nota) {
        String notaFormatada = null;
        if (nota == null || nota.isEmpty()) {
            notaFormatada = "";
        } else {
            notaFormatada = nota.replace('.', ',');
        }

        return notaFormatada;
    }

    private void addResumoInferior() {
        PerfilAcessoEnum perfilPorPagina = getPerfil();

        String tituloResumoInferior = "Nota final da análise de riscos e controles";

        painelResumoMatriz =
                new PainelResumoMatriz("resumoMatrizRecalculadaPanel", matriz, perfilRisco, true, isExibirNotaAjustada, true,
                        getExibirStringAAvaliar(), perfilPorPagina, tituloResumoInferior, emAnalise) {
                    @Override
                    public String getMarkupIdTabelaPainel() {
                        return "tResumoMatrizRecalculada";
                    }

                    @Override
                    public boolean isVisible() {
                        return isResumosVisiveis();
                    }

                    @Override
                    public String getNotaCalculada() {
                        return notaCalculadaFinal;
                    }
                };
        addOrReplace(painelResumoMatriz);
    }
    
    private boolean isResumosVisiveis() {
        return (perfilRisco == null && !Util.isNuloOuVazio(listaChoices)) || perfilRisco != null;
    }

    private PerfilAcessoEnum getPerfil() {
        PerfilAcessoEnum perfilPorPagina = null;
        if (getPage() instanceof ApresentacaoPage) {
            perfilPorPagina = PerfilAcessoEnum.SUPERVISOR;
        } else {
            perfilPorPagina = getPaginaAtual().getPerfilPorPagina();
        }
        return perfilPorPagina;
    }

    public void addListNomeGrupoRiscoEControle() {
        corpo.addOrReplace(new ListView<ParametroGrupoRiscoControle>("listaNomeGrupoParametro", getParametroGrupo()) {
            @Override
            protected void populateItem(ListItem<ParametroGrupoRiscoControle> item) {
                addNomeGrupoRisco(item);
            }
        });
    }

    private void addNomeGrupoRisco(ListItem<ParametroGrupoRiscoControle> item) {
        ParametroGrupoRiscoControle grupoRiscoControle = item.getModelObject();
        WebMarkupContainer component = new WebMarkupContainer("linkNomeGrupo") {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(isEmAnalise());
            }
        };
        component.add(new AttributeAppender(ConstantesWeb.HREF, Model.of("#idTituloSintese"
                + SisapsUtil.criarMarkupId(grupoRiscoControle.getNomeAbreviado()))));
        component.add(new Label("nomeGrupoComLink", grupoRiscoControle.getNomeAbreviado()));
        item.add(component);
        item.add(new Label("nomeGrupo", grupoRiscoControle.getNomeAbreviado()) {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(!isEmAnalise());
            }
        });
    }

    public void addListPercentualGrupoRiscoEControle() {
        corpo.addOrReplace(new ListView<ParametroGrupoRiscoControle>("listaPercentualGrupoParametro",
                getParametroGrupo()) {
            @Override
            protected void populateItem(ListItem<ParametroGrupoRiscoControle> item) {
                String idLabel = "percentualGrupo";
                if (matriz == null) {
                    item.add(new Label(idLabel, ""));
                } else {
                    item.add(new Label(idLabel, matrizCicloMediator.percentualDoGrupoVO(matriz,
                            CelulaRiscoControleMediator.get().getCelulasPorGrupoVO(listaChoices, item.getModelObject()),
                            versoesPerfilRiscoCelulas)));
                }
            }
        });
    }

    public void addListRiscoEControle() {
        corpo.addOrReplace(new ListView<ParametroGrupoRiscoControle>("listaRiscoControle", getParametroGrupo()) {
            @Override
            protected void populateItem(ListItem<ParametroGrupoRiscoControle> item) {
                //TODO implementado nas próximas iterações.
            }
        });
    }

    public void addListUnidadeEAtividades() {
        corpo.addOrReplace(new ListView<LinhaMatrizVO>("listaLinhaMatrizVo", linhaMatrizVO) {
            @Override
            protected void populateItem(final ListItem<LinhaMatrizVO> itemLinha) {
                itemLinha.add(new Label("nomeLinhaVo", itemLinha.getModelObject().getNomeFormatadoVigente())
                        .setEscapeModelStrings(false).add(
                                new AttributeAppender(CLASS, obterClassCelula(itemLinha.getModel()), ESPACO)));
                itemLinha.add(new Label("percentualLinhaVo", matrizCicloMediator.percentualDaLinha(matriz,
                        itemLinha.getModelObject())).setEscapeModelStrings(false).add(
                        new AttributeAppender(CLASS, obterClassCelula(itemLinha.getModel()), ESPACO)));
                itemLinha.add(new ListView<ParametroGrupoRiscoControle>("listaArc", getParametroGrupo()) {
                    @Override
                    protected void populateItem(ListItem<ParametroGrupoRiscoControle> itemGrupo) {
                        addLinkRiscoControleAtividade(itemLinha.getModel(), itemGrupo, true);
                    }
                });

            }

        });
    }

    private IModel<?> obterClassCelula(final IModel<LinhaMatrizVO> linha) {

        IModel<String> model = new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {

                if (linha.getObject().isFilho()) {
                    return "nomeMatrizSemNegrito";
                } else {
                    return NOME_MATRIZ;
                }

            }
        };
        return model;

    }

    private void addLinkRiscoControleAtividade(final IModel<LinhaMatrizVO> rowModel,
            final ListItem<ParametroGrupoRiscoControle> item, boolean mostrarLink) {

        CelulaRiscoControleVO celula =
                getCelulaPelaAtividadeEGrupo(item.getModelObject(), rowModel.getObject().getPk());

        addLinkRiscoControleAtividade(rowModel, item, celula, "dadosRisco", "risco", mostrarLink, TipoGrupoEnum.RISCO);
        addLinkRiscoControleAtividade(rowModel, item, celula, "dadosControle", "controle", mostrarLink,
                TipoGrupoEnum.CONTROLE);
    }

    private void addLinkRiscoControleAtividade(final IModel<LinhaMatrizVO> rowModel,
            final ListItem<ParametroGrupoRiscoControle> item, CelulaRiscoControleVO celula, String webMarkupContainerId,
            String labelLinkId, boolean mostrarLink, TipoGrupoEnum tipo) {
        final LinhaMatrizVO atividade = rowModel.getObject();
        final ArcNotasVO avaliacaoRisco =
                TipoLinhaMatrizVOEnum.UNIDADE.equals(atividade.getTipo()) || celula == null ? null
                        : (TipoGrupoEnum.RISCO.equals(tipo)
                                ? AvaliacaoRiscoControleMediator.get().consultarNotasArc(celula.getArcRiscoPk())
                                : AvaliacaoRiscoControleMediator.get().consultarNotasArc(celula.getArcControlePk()));

        WebMarkupContainer dados = new WebMarkupContainer(webMarkupContainerId);

        Link<LinhaMatrizVO> link = new AjaxFallbackLink<LinhaMatrizVO>("link" + tipo.getDescricao(), rowModel) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                TituloTelaARCEnum titulo =
                        UtilNavegabilidadeARC.novaPaginaDeAcordoStatus(matriz, avaliacaoRisco, isEmAnalise(),
                                perfilAtual, getPaginaAtual().getPerfilPorPagina());
                instanciarPaginaDestinoARC(avaliacaoRisco, matriz, atividade.getPk(), titulo, perfilAtual);
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(mostrarLinkMatriz(avaliacaoRisco));
            }
        };
        link.setOutputMarkupId(true);
        if (avaliacaoRisco != null) {
            link.setMarkupId(link.getId() + avaliacaoRisco.getPk().toString());
        }

        String notaRisco = avaliacaoRisco == null ? ESPACO : buscarNota(avaliacaoRisco);
        link.add(new Label(labelLinkId, notaRisco));
        link.setVisible(mostrarLink);
        dados.add(link);
        dados.add(labelValorNotaString(labelLinkId, avaliacaoRisco, notaRisco));
        if (montarBordaEmAnalise(avaliacaoRisco)) {
            dados.add(new AttributeAppender(STYLE, obterBorda(), ESPACO));
        }
        dados.add(new AttributeAppender(BGCOLOR, obterCorNota(notaRisco), ESPACO));
        dados.add(new Label(labelLinkId + "Label", notaRisco).setVisible(!mostrarLink));
        item.add(dados);
    }

    public boolean mostrarLinkMatriz(final ArcNotasVO avaliacaoRisco) {
        // Declarações
        boolean mostraLink;

        if (!isExibirLinkArc) {
            mostraLink = false;

        } else {

            mostraLink = true;

            if (avaliacaoRisco != null && Constantes.ASTERISCO_A.equals(buscarNota(avaliacaoRisco))) {

                if (!perfilAtual) {
                    mostraLink = false;
                } else if (getPaginaAtual().perfilConsulta(getPaginaAtual())
                        || getPaginaAtual().perfilConsultaResumido(getPaginaAtual())) {
                    mostraLink = false;
                } else if (getPaginaAtual().perfilSupervisor(getPaginaAtual())) {
                    mostraLink = true;
                } else if (mostrarNotaInspetor(avaliacaoRisco)) {
                    mostraLink = true;
                } else {
                    mostraLink = false;
                }
            }
        }

        return mostraLink;
    }

    private boolean mostrarNotaInspetor(ArcNotasVO avaliacaoRisco) {
        if (getPaginaAtual().perfilInspetor(getPaginaAtual())) {
            return AvaliacaoRiscoControleMediator.get().mostraLinkArcsInspetor(avaliacaoRisco,
                    ((UsuarioAplicacao) UsuarioCorrente.get()));
        }
        return true;
    }

    public boolean montarBordaEmAnalise(final ArcNotasVO avaliacaoRisco) {
        if (isEmAnalise() && avaliacaoRisco != null) {
            return EstadoARCEnum.ANALISADO.equals(avaliacaoRisco.getEstado());
        }
        return false;
    }

    public Label labelValorNotaString(String labelLinkId, final ArcNotasVO avaliacaoRisco, String notaRisco) {
        return new Label(labelLinkId, notaRisco) {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(!mostrarLinkMatriz(avaliacaoRisco));
            }
        };
    }

    public String buscarNota(final ArcNotasVO avaliacaoRisco) {
        if (isEmAnalise()) {
            return AvaliacaoRiscoControleMediator.get().notaArcAnalise(avaliacaoRisco, matriz.getCiclo(),
                    perfilPorMenu, perfilRisco);
        } else {
            return AvaliacaoRiscoControleMediator.get().notaArc(avaliacaoRisco, matriz.getCiclo(), perfilPorMenu,
                    perfilRisco);
        }
    }

    public void addListNotasResiduais() {
        corpo.addOrReplace(new ListView<LinhaNotasMatrizVO>("listaNotasResiduais", notaResiduais) {
            @Override
            protected void populateItem(ListItem<LinhaNotasMatrizVO> item) {
                Label labelNotas = new Label("notasResiduais", item.getModelObject().getNota());
                labelNotas.add(new AttributeAppender(BGCOLOR, obterCorNota(item.getModelObject().getNota()), ESPACO));
                if (painelVigente != null
                        && possuiArcsAnalisadosPorTipo(item.getModelObject().getGrupo(), item.getModelObject()
                                .getTipo())) {
                    labelNotas.add(new AttributeAppender(STYLE, obterBorda(), ESPACO));
                }
                item.add(labelNotas);
            }
        });

        corpo.addOrReplace(new ListView<LinhaNotasMatrizVO>("listaMediaNotasResiduais", mediaResiduais) {

            @Override
            protected void populateItem(ListItem<LinhaNotasMatrizVO> item) {
                Label labelMedias = new Label("mediasResiduais", item.getModelObject().getNota());
                labelMedias.add(new AttributeAppender(BGCOLOR, obterCorNota(item.getModelObject().getNota()), ESPACO));
                if (painelVigente != null && possuiArcsAnalisados(item.getModelObject().getGrupo())) {
                    labelMedias.add(new AttributeAppender(STYLE, obterBorda(), ESPACO));
                }
                item.add(labelMedias);
            }

        });

    }

    public IModel<String> obterCorNota(final String nota) {
        final ParametroNota parametroNota;

        if (nota.isEmpty() || (!nota.isEmpty() && ("*A".equals(nota) || ESPACO.equals(nota)))) {
            parametroNota = null;
        } else {
            parametroNota =
                    ParametroNotaMediator.get().buscarPorMetodologiaENota(matriz.getCiclo().getMetodologia(),
                            new BigDecimal(nota.replace(',', '.')), true);
        }

        IModel<String> model = new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                if (ESPACO.equals(nota)) {
                    return "#F2F4F6";
                } else {
                    if (parametroNota == null) {
                        return "#bbbbbb";
                    } else {
                        return parametroNota.getCor();
                    }
                }

            }
        };
        return model;
    }

    public IModel<String> obterBorda() {
        IModel<String> model = new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                return "border:2px solid #003d79; font-weight: bold;  color: #003d79;";
            }
        };
        return model;
    }

    private List<LinhaMatrizVO> obterListaLinhaMatrizVOAtividades() {
        return linhaMatrizMediator.montarListaVOAtividadeNegocio(matriz, listaAtividade);
    }

    private List<LinhaMatrizVO> obterListaLinhaMatrizNegocio() {
        return linhaMatrizMediator.consultar(matriz, false, listaAtividade);
    }

    private List<LinhaMatrizVO> obterLinhasMatrizCorporativa() {
        return linhaMatrizMediator.consultar(matriz, true, listaAtividade);
    }

    public void setPerfilRisco(PerfilRisco perfilRisco) {
        this.perfilRisco = perfilRisco;
    }

    public PerfilRisco getPerfilRisco() {
        return this.perfilRisco;
    }

    public Matriz getMatriz() {
        return matriz;
    }

    public void setMatriz(Matriz matriz) {
        this.matriz = matriz;
    }

    public boolean getExibirStringAAvaliar() {
        return isExibirStringAAvaliar;
    }

    public void setExibirStringAAvaliar(boolean isExibirStringAAvaliar) {
        this.isExibirStringAAvaliar = isExibirStringAAvaliar;
    }

    public List<LinhaNotasMatrizVO> getNotaResiduais() {
        return notaResiduais;
    }

    public void setNotaResiduais(List<LinhaNotasMatrizVO> notaResiduais) {
        this.notaResiduais = notaResiduais;
    }

    public List<LinhaNotasMatrizVO> getMediaResiduais() {
        return mediaResiduais;
    }

    public void setMediaResiduais(List<LinhaNotasMatrizVO> mediaResiduais) {
        this.mediaResiduais = mediaResiduais;
    }

    public List<CelulaRiscoControleVO> getCelulaMatriz() {
        return listaChoices;
    }

    public List<ParametroGrupoRiscoControle> getParametroGrupo() {
        List<ParametroGrupoRiscoControle> listaParametro = new ArrayList<ParametroGrupoRiscoControle>();
        for (CelulaRiscoControleVO celula : listaChoices) {
            ParametroGrupoRiscoControle parametroGrupo =
                    ParametroGrupoRiscoControleMediator.get().buscarPorPk(celula.getParametroGrupoPk());
            if (!listaParametro.contains(parametroGrupo)) {
                listaParametro.add(parametroGrupo);
            }
        }
        return listaParametro;
    }

    public CelulaRiscoControleVO getCelulaPelaAtividadeEGrupo(ParametroGrupoRiscoControle grupo, Integer pkAtividade) {
        for (CelulaRiscoControleVO celula : listaChoices) {
            if (celula.getParametroGrupoPk().equals(grupo.getPk())
                    && celula.getAtividadePk().equals(pkAtividade)) {
                return celula;
            }
        }
        return null;
    }

    public boolean possuiArcsAnalisadosPorTipo(ParametroGrupoRiscoControle grupo, TipoGrupoEnum tipo) {
        for (CelulaRiscoControleVO celula : listaChoices) {
            if (celula.getParametroGrupoPk().equals(grupo.getPk())) {
                ArcNotasVO arcRisco = AvaliacaoRiscoControleMediator.get().consultarNotasArc(celula.getArcRiscoPk());
                ArcNotasVO arcControle =
                        AvaliacaoRiscoControleMediator.get().consultarNotasArc(celula.getArcControlePk());
                if (EstadoARCEnum.ANALISADO.equals(arcRisco.getEstado()) && TipoGrupoEnum.RISCO.equals(tipo)) {
                    return true;
                }
                if (EstadoARCEnum.ANALISADO.equals(arcControle.getEstado()) && TipoGrupoEnum.CONTROLE.equals(tipo)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean possuiArcsAnalisados(ParametroGrupoRiscoControle grupo) {
        for (CelulaRiscoControleVO celula : listaChoices) {
            if (celula.getParametroGrupoPk().equals(grupo.getPk())) {
                ArcNotasVO arcRisco = AvaliacaoRiscoControleMediator.get().consultarNotasArc(celula.getArcRiscoPk());
                ArcNotasVO arcControle =
                        AvaliacaoRiscoControleMediator.get().consultarNotasArc(celula.getArcControlePk());
                if (EstadoARCEnum.ANALISADO.equals(arcRisco.getEstado())) {
                    return true;
                }
                if (EstadoARCEnum.ANALISADO.equals(arcControle.getEstado())) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isPerfilAtual() {
        return perfilAtual;
    }

    public void setPerfilAtual(boolean perfilAtual) {
        this.perfilAtual = perfilAtual;
    }

    public PainelResumoMatriz getPainelResumoMatriz() {
        return painelResumoMatriz;
    }

    public void setPainelResumoMatriz(PainelResumoMatriz painelResumoMatriz) {
        this.painelResumoMatriz = painelResumoMatriz;
    }

    public boolean isEmAnalise() {
        return emAnalise;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getNotaCalculadaFinal() {
        return notaCalculadaFinal;
    }

}
