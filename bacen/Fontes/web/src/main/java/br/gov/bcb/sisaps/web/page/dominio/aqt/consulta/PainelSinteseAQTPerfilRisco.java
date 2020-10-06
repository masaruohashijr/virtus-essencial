package br.gov.bcb.sisaps.web.page.dominio.aqt.consulta;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.VersaoPerfilRisco;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroNotaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.PesoAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.SinteseDeRiscoAQT;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoObjetoVersionadorEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TituloTelaAnefEnum;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.src.mediator.VersaoPerfilRiscoMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.ParametroAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.ParametroNotaAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.PesoAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.SinteseDeRiscoAQTMediator;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.LabelLinhas;
import br.gov.bcb.sisaps.web.page.dominio.ciclo.painel.UtilNavegabilidadeAQT;
import br.gov.bcb.sisaps.web.page.dominio.gerenciarnotasintese.aqt.GrupoExpansivelAQTCampoValorCor;

@SuppressWarnings("serial")
public class PainelSinteseAQTPerfilRisco extends PainelSisAps {

    private static final String LINHA = "linha";

    private static final String GRUPO_EXPANSIVEL = "grupoExpansivel";

    @SpringBean
    private VersaoPerfilRiscoMediator versaoPerfilRiscoMediator;
    @SpringBean
    private ParametroAQTMediator parametroAQTMediator;
    @SpringBean
    private SinteseDeRiscoAQTMediator sinteseDeRiscoAQTMediator;
    @SpringBean
    private PerfilRiscoMediator perfilRiscoMediator;

    private PerfilRisco perfilRisco;
    private List<VersaoPerfilRisco> versoesSintesesMatriz;

    private List<AnaliseQuantitativaAQT> aqtsVigentes;
    private final PerfilAcessoEnum perfilEnum;

    private PerfilRisco perfilAtual;

    private final boolean isResumoInferior;

    private final boolean isNovoQuadro;

    private final String titulo;
    private PainelResumoSinteseAQT painelResumoInferior;
    private final WebMarkupContainer mostrarAAvaliar = new WebMarkupContainer("idMostrarAAvaliar");

    private final boolean isTelaPerfilRisco;

    public PainelSinteseAQTPerfilRisco(String id, PerfilRisco perfilRisco, PerfilAcessoEnum perfilEnum, String titulo,
            boolean isNovoQuadro, boolean isResumoInferior, boolean isTelaGestao) {
        super(id);

        setMarkupId(id);
        this.perfilEnum = perfilEnum;
        this.perfilRisco = perfilRisco;
        this.titulo = titulo;
        this.isNovoQuadro = isNovoQuadro;
        this.isResumoInferior = isResumoInferior;
        this.isTelaPerfilRisco = isTelaGestao;

    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        this.versoesSintesesMatriz = null;
        aqtsVigentes = perfilRiscoMediator.getAnalisesQuantitativasAQTPerfilRisco(perfilRisco);
        versoesSintesesMatriz =
                versaoPerfilRiscoMediator.buscarVersoesPerfilRisco(perfilRisco.getPk(),
                        TipoObjetoVersionadorEnum.SINTESE_AQT);
        mostrarAAvaliar.setOutputMarkupId(true);
        mostrarAAvaliar.setMarkupId(mostrarAAvaliar.getId());
        mostrarAAvaliar.setVisible(!isResumoInferior);
        addOrReplace(mostrarAAvaliar);
        addOrReplace(new Label("idTitulo", titulo));

        addComponents();
        painelResumoInferior =
                new PainelResumoSinteseAQT("idPainelResumoInferior", perfilRisco, false, getPaginaAtual()
                        .getPerfilPorPagina(), isNovoQuadro);
        painelResumoInferior.setOutputMarkupId(true);
        painelResumoInferior.setMarkupId(painelResumoInferior.getId());
        painelResumoInferior.setVisible(isResumoInferior);
        addOrReplace(painelResumoInferior);

    }

    private void addComponents() {
        addOrReplace(new ListView<AnaliseQuantitativaAQT>("listaSintesesAQT", aqtsVigentes) {
            @Override
            protected void populateItem(ListItem<AnaliseQuantitativaAQT> item) {
                addGrupoSinteseDeRiscoAQT(item);
            }
        });
    }

    private void addGrupoSinteseDeRiscoAQT(ListItem<AnaliseQuantitativaAQT> item) {
        final AnaliseQuantitativaAQT aqt = AnaliseQuantitativaAQTMediator.get().buscar(item.getModelObject().getPk());
        ParametroAQT parametroAQT = parametroAQTMediator.buscarParemetroAQT(aqt);
        boolean isSupervisorOuGerente =
                PerfilAcessoEnum.SUPERVISOR.equals(getPerfilPorPagina())
                        || PerfilAcessoEnum.GERENTE.equals(getPerfilPorPagina());

        SinteseDeRiscoAQT sinteseDeRiscoAQT = null;
        if (CollectionUtils.isNotEmpty(versoesSintesesMatriz)) {
            sinteseDeRiscoAQT =
                    sinteseDeRiscoAQTMediator.getSintesePorVersaoPerfil(parametroAQT, perfilRisco.getCiclo(),
                            versoesSintesesMatriz);
        }
        String nomeDoCampo = parametroAQT.getDescricao();

        final String notaAQT =
                AnaliseQuantitativaAQTMediator.get().notaAnef(aqt, perfilRisco.getCiclo(), perfilEnum, isNovoQuadro, 
                        perfilRisco);

        WebMarkupContainer linhaSituacao = new WebMarkupContainer(LINHA);
        linhaSituacao.setMarkupId(linhaSituacao.getId());
        addOrReplace(linhaSituacao);

        Label justificativaSituacao =
                new LabelLinhas("idDescricaoSintese", sinteseDeRiscoAQT == null
                        || sinteseDeRiscoAQT.getJustificativa() == null ? "" : " "
                        + sinteseDeRiscoAQT.getJustificativa());
        justificativaSituacao.setEscapeModelStrings(false);
        linhaSituacao.addOrReplace(justificativaSituacao);

        GrupoExpansivelAQTCampoValorCor grupo =
                montarGrupoExpansivel(aqt, parametroAQT, nomeDoCampo, notaAQT, linhaSituacao, isSupervisorOuGerente,
                        sinteseDeRiscoAQT);
        item.addOrReplace(linhaSituacao);
        item.addOrReplace(grupo);

    }

    private GrupoExpansivelAQTCampoValorCor montarGrupoExpansivel(final AnaliseQuantitativaAQT aqt,
            final ParametroAQT parametroAQT, String nomeDoCampo, final String notaAQT,
            WebMarkupContainer linhaSituacao, final boolean isSupervisor, final SinteseDeRiscoAQT sinteseDeRiscoAQT) {
        perfilAtual = perfilRiscoMediator.obterPerfilRiscoAtual(perfilRisco.getCiclo().getPk());
        GrupoExpansivelAQTCampoValorCor grupo =
                new GrupoExpansivelAQTCampoValorCor(GRUPO_EXPANSIVEL, nomeDoCampo, null, false, linhaSituacao) {
                    @Override
                    public String getMarkupIdControle() {
                        return "grupo_controle_" + SisapsUtil.criarMarkupId(getMarkupId());
                    }

                    @Override
                    protected void onConfigure() {
                        PesoAQT pesoVigente =
                                PesoAQTMediator.get().obterPesoVigente(aqt.getParametroAQT(), aqt.getCiclo());
                        exibirPercent(getLinkExpandirColapsarPerc(), parametroAQT, aqt, pesoVigente.getValor());
                        exibirLink(getLinkExpandirColapsar(), aqt, parametroAQT, notaAQT, isSupervisor, perfilAtual);
                    }

                    @Override
                    public void executeOnClick() {
                        avancarParaNovaPagina(aqt, perfilAtual);
                    }

                    @Override
                    public String definirCorGrupoExpansivel() {
                        return obterCorNota(parametroAQT, notaAQT);
                    }

                    @Override
                    public String definirStyleGrupoExpansivel() {
                        return obterBorda();
                    }

                    @Override
                    public boolean isControleVisivel() {
                        return isTelaPerfilRisco && sinteseDeRiscoAQT != null
                                && sinteseDeRiscoAQT.getJustificativa() != null;
                    }
                };
        grupo.setMarkupId(GRUPO_EXPANSIVEL + parametroAQT.getDescricao());
        linhaSituacao.setMarkupId(LINHA + SisapsUtil.criarMarkupId(parametroAQT.getDescricao()));
        return grupo;
    }

    private void exibirPercent(Label linkExpandirColapsar, ParametroAQT parametroAQT, AnaliseQuantitativaAQT aqt,
            Short short1) {
        linkExpandirColapsar.setDefaultModel(new Model<String>(short1.toString() + "%"));
        linkExpandirColapsar.setEnabled(true);
        linkExpandirColapsar.setOutputMarkupId(true);
        linkExpandirColapsar.setMarkupId("linkPercAQT" + parametroAQT.getDescricao());
    }

    private void exibirLink(AjaxLink<String> linkExpandirColapsar, final AnaliseQuantitativaAQT aqt,
            final ParametroAQT parametroAQT, final String notaAQT, final boolean isSupervisor, PerfilRisco perfilAtual) {
        linkExpandirColapsar.setBody(new Model<String>(notaAQT));

        AnaliseQuantitativaAQT anef = AnaliseQuantitativaAQTMediator.get().buscarAQTRascunho(aqt);

        if (aqt.getValorNota() == null && aqt.getNotaSupervisor() == null) {
            if (!perfilAtual.equals(perfilRisco)) {
                linkExpandirColapsar.setEnabled(false);
            } else if (isSupervisor) {
                linkExpandirColapsar.setEnabled(true);
            } else if (inspetorResponsavel(anef)) {
                linkExpandirColapsar.setEnabled(true);
            } else {
                linkExpandirColapsar.setEnabled(false);
            }
        }
        if (getPaginaAtual().perfilConsultaResumido(getPaginaAtual())) {
            linkExpandirColapsar.setEnabled(false);
        }

        linkExpandirColapsar.setOutputMarkupId(true);
        linkExpandirColapsar.setMarkupId("link_ANEF" + aqt.getPk());
    }

    private boolean inspetorResponsavel(AnaliseQuantitativaAQT anef) {
        return PerfilAcessoEnum.INSPETOR.equals(getPaginaAtual().getPerfilPorPagina())
                && AnaliseQuantitativaAQTMediator.get().isResponsavelDesignacao(anef)
                && AnaliseQuantitativaAQTMediator.get().estadosDesignacaoDelegacaoInspetor(anef);
    };

    private void avancarParaNovaPagina(final AnaliseQuantitativaAQT anefTela, PerfilRisco perfilAtual) {
        AnaliseQuantitativaAQT anef = null;
        if (perfilAtualCicloAndamento(perfilAtual, anefTela)
                && (perfilSupervisor(getPaginaAtual()) || perfilGerente(getPaginaAtual()))) {
            anef = AnaliseQuantitativaAQTMediator.get().buscarAQTRascunho(anefTela);
        } else if (perfilAtualCicloAndamento(perfilAtual, anefTela) && (perfilInspetor(getPaginaAtual()))) {
            anef = AnaliseQuantitativaAQTMediator.get().buscarAQTRascunho(anefTela);
            if (!AnaliseQuantitativaAQTMediator.get().estadosDesignacaoDelegacaoInspetor(anef)) {
                anef = anefTela;
            }
        } else {
            anef = anefTela;
        }

        TituloTelaAnefEnum titulo =
                UtilNavegabilidadeAQT.buscarPaginaDeAcordoEstado(new Model<AnaliseQuantitativaAQT>(anef), perfilRisco,
                        getPaginaAtual().getPerfilPorPagina(), perfilAtual.equals(perfilRisco));

        instanciarPaginaDestino(anef, titulo, perfilAtual.equals(perfilRisco));
    }

    private boolean perfilAtualCicloAndamento(PerfilRisco perfilAtual, AnaliseQuantitativaAQT anefTela) {
        return perfilAtual.equals(perfilRisco) && CicloMediator.get().cicloEmAndamento(anefTela.getCiclo());

    }

    private String obterCorNota(ParametroAQT parametroAQT, String notaAQT) {
        String cor = "#D8D8D8";
        if (!notaAQT.equals(Constantes.ASTERISCO_A)) {
            ParametroNotaAQT buscarCorMetodologia =
                    ParametroNotaAQTMediator.get().buscarCorPorMetodologia(parametroAQT.getMetodologia(),
                            new BigDecimal(notaAQT.replace(',', '.')));
            if (buscarCorMetodologia != null) {
                cor = buscarCorMetodologia.getCor();
            }
        }
        return cor;
    }

    private String obterBorda() {
        return "width: 4%;  text-align: center;";
    }

    public void setPerfilRisco(PerfilRisco perfilRisco) {
        this.perfilRisco = perfilRisco;
    }

    public String getNotaCalculadaFinal() {
        return painelResumoInferior.getNotaCalculada();
    }

}
