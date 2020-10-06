package br.gov.bcb.sisaps.web.page.dominio.gerenciarnotasintese.aqt;

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
import br.gov.bcb.sisaps.src.mediator.VersaoPerfilRiscoMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.ParametroAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.ParametroNotaAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.PesoAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.SinteseDeRiscoAQTMediator;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.dominio.aqt.consulta.PainelResumoSinteseAQT;

@SuppressWarnings("serial")
public class PainelNovoQuadroAQT extends PainelSisAps {

    private static final String LINHA = "linhaAnalise";

    private static final String GRUPO_EXPANSIVEL = "grupoExpansivel";

    @SpringBean
    private VersaoPerfilRiscoMediator versaoPerfilRiscoMediator;
    @SpringBean
    private ParametroAQTMediator parametroAQTMediator;
    @SpringBean
    private SinteseDeRiscoAQTMediator sinteseDeRiscoAQTMediator;
    @SpringBean
    private AnaliseQuantitativaAQTMediator analiseQuantitativaAQTMediator;
    @SpringBean
    private ParametroNotaAQTMediator parametroNotaAQTMediator;

    private PerfilRisco perfilRisco;
    private List<VersaoPerfilRisco> versoesSintesesMatriz;

    private List<AnaliseQuantitativaAQT> aqtsVigentes;

    private String novoValorCalculado;

    public PainelNovoQuadroAQT(String id, PerfilRisco perfilRisco) {
        super(id);
        setMarkupId(id);
        this.perfilRisco = perfilRisco;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        aqtsVigentes = analiseQuantitativaAQTMediator.listarANEFsNovoQuadro(perfilRisco.getCiclo());
        versoesSintesesMatriz =
                versaoPerfilRiscoMediator.buscarVersoesPerfilRisco(perfilRisco.getPk(),
                        TipoObjetoVersionadorEnum.SINTESE_AQT);
        PainelResumoSinteseAQT painelResumoInferior =
                new PainelResumoSinteseAQT("idPainelResumoInferior", perfilRisco, false,
                        getPaginaAtual().getPerfilPorPagina(), true);
        painelResumoInferior.setOutputMarkupId(true);
        painelResumoInferior.setMarkupId(painelResumoInferior.getId());
        painelResumoInferior.setVisible(true);
        addOrReplace(painelResumoInferior);
        addComponents();
        setOutputMarkupId(true);
    }

    private void addComponents() {
        addOrReplace(new ListView<AnaliseQuantitativaAQT>("listaSintesesAQT", aqtsVigentes) {
            @Override
            protected void populateItem(ListItem<AnaliseQuantitativaAQT> item) {
                if (item != null) {
                    addGrupoSinteseDeRiscoAQT(item);
                }

            }
        });
    }

    private void addGrupoSinteseDeRiscoAQT(ListItem<AnaliseQuantitativaAQT> item) {
        final AnaliseQuantitativaAQT aqt = analiseQuantitativaAQTMediator.buscar(item.getModelObject().getPk());
        ParametroAQT parametroAQT = parametroAQTMediator.buscarParemetroAQT(aqt);
        boolean isSupervisor = PerfilAcessoEnum.SUPERVISOR.equals(getPerfilPorPagina());

        SinteseDeRiscoAQT sinteseDeRiscoAQT = null;
        if (CollectionUtils.isNotEmpty(versoesSintesesMatriz)) {
            sinteseDeRiscoAQT = sinteseDeRiscoAQTMediator.getUltimaSinteseVigente(parametroAQT, perfilRisco.getCiclo());
        }
        String nomeDoCampo = parametroAQT.getDescricao();

        String notaAQT = null;

        if (analiseQuantitativaAQTMediator.estadoAnalisadoConcluido(aqt.getEstado())) {
            notaAQT =
                    analiseQuantitativaAQTMediator.notaAnef(aqt, perfilRisco.getCiclo(), getPaginaAtual()
                            .getPerfilPorPagina(), true, perfilRisco);
        } else {
            AnaliseQuantitativaAQT anefVigente =
                    analiseQuantitativaAQTMediator.buscarAQTVigentePorPerfil(aqt.getParametroAQT(),
                            perfilRisco.getCiclo(), perfilRisco);
            notaAQT =
                    analiseQuantitativaAQTMediator.notaAnef(anefVigente, perfilRisco.getCiclo(), getPaginaAtual()
                            .getPerfilPorPagina(), true, perfilRisco);
        }

        novoValorCalculado = notaAQT;

        WebMarkupContainer linhaSituacao = new WebMarkupContainer(LINHA);
        linhaSituacao.setMarkupId(linhaSituacao.getId());
        addOrReplace(linhaSituacao);

        Label justificativaSituacao =
                new Label("idDescricaoSintese", sinteseDeRiscoAQT == null
                        || sinteseDeRiscoAQT.getJustificativa() == null ? "" : sinteseDeRiscoAQT.getJustificativa());
        justificativaSituacao.setEscapeModelStrings(false);
        linhaSituacao.addOrReplace(justificativaSituacao);

        GrupoExpansivelAQTCampoValorCor grupo =
                montarGrupoExpansivel(aqt, parametroAQT, nomeDoCampo, notaAQT, linhaSituacao, isSupervisor);
        item.addOrReplace(linhaSituacao);
        item.addOrReplace(grupo);

    }

    private GrupoExpansivelAQTCampoValorCor montarGrupoExpansivel(final AnaliseQuantitativaAQT aqt,
            final ParametroAQT parametroAQT, String nomeDoCampo, final String notaAQT,
            WebMarkupContainer linhaSituacao, final boolean isSupervisor) {
        GrupoExpansivelAQTCampoValorCor grupo =
                new GrupoExpansivelAQTCampoValorCor(GRUPO_EXPANSIVEL, nomeDoCampo, null, false, linhaSituacao) {
                    @Override
                    public String getMarkupIdControle() {
                        return "grupo_controle_analise_" + SisapsUtil.criarMarkupId(getMarkupId());
                    }

                    @Override
                    protected void onConfigure() {
                        PesoAQT pesoRascunho =
                                PesoAQTMediator.get().obterPesoRascunho(aqt.getParametroAQT(), aqt.getCiclo());
                        exibirPercent(getLinkExpandirColapsarPerc(), parametroAQT, aqt, pesoRascunho.getValor());
                        exibirLink(getLinkExpandirColapsar(), aqt, parametroAQT, notaAQT, isSupervisor);
                    }

                    @Override
                    public String definirCorGrupoExpansivel() {
                        return obterCorNota(parametroAQT, notaAQT);
                    }

                    @Override
                    public String definirStyleGrupoExpansivel() {
                        return getBorda(aqt);
                    }

                    @Override
                    public boolean isControleVisivel() {
                        return false;
                    }
                };
        grupo.setMarkupId(GRUPO_EXPANSIVEL + parametroAQT.getDescricao());
        linhaSituacao.setMarkupId(LINHA + SisapsUtil.criarMarkupId(parametroAQT.getDescricao()));
        return grupo;
    }

    private String getBorda(final AnaliseQuantitativaAQT aqt) {
        String borda;
        if (analiseQuantitativaAQTMediator.corDestaqueAnefRascunho(aqt)) {
            borda = obterBordaDestaque();
        } else {
            borda = obterBorda();
        }
        return borda;
    }

    private void exibirLink(AjaxLink<String> linkExpandirColapsar, final AnaliseQuantitativaAQT aqt,
            final ParametroAQT parametroAQT, final String notaAQT, final boolean isSupervisor) {
        linkExpandirColapsar.setBody(new Model<String>(notaAQT));
        linkExpandirColapsar.setEnabled(false);
        linkExpandirColapsar.setOutputMarkupId(true);
        linkExpandirColapsar.setMarkupId("linkNotaAQT" + parametroAQT.getDescricao());
    };

    private void exibirPercent(Label linkExpandirColapsar, ParametroAQT parametroAQT, AnaliseQuantitativaAQT aqt,
            Short short1) {
        linkExpandirColapsar.setDefaultModel(new Model<String>(short1.toString() + "%"));
        linkExpandirColapsar.setEnabled(false);
        linkExpandirColapsar.setOutputMarkupId(true);
        linkExpandirColapsar.setMarkupId("linkPercAQT" + parametroAQT.getDescricao());
    }

    private String obterCorNota(ParametroAQT parametroAQT, String notaAQT) {
        String cor = "#D8D8D8";
        if (!notaAQT.equals(Constantes.ASTERISCO_A)) {
            ParametroNotaAQT buscarCorMetodologia =
                    parametroNotaAQTMediator.buscarCorPorMetodologia(parametroAQT.getMetodologia(), new BigDecimal(
                            notaAQT.replace(',', '.')));
            if (buscarCorMetodologia != null) {
                cor = buscarCorMetodologia.getCor();
            }
        }
        return cor;
    }

    private String obterBorda() {
        return "width: 4%;  text-align: center;";
    }

    private String obterBordaDestaque() {
        return "width: 4%; border:2px solid #003d79; font-weight: bold;  color: #003d79; text-align: center;";

    }

    public void setPerfilRisco(PerfilRisco perfilRisco) {
        this.perfilRisco = perfilRisco;
    }

    public String getNotaCalculadaFinal() {
        return this.novoValorCalculado;
    }
}
