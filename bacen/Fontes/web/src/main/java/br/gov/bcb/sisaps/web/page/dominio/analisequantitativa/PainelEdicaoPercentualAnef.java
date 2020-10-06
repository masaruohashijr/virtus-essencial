package br.gov.bcb.sisaps.web.page.dominio.analisequantitativa;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.PesoAQT;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.ParametroAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.PesoAQTMediator;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.botoes.AjaxSubmitLinkSisAps;
import br.gov.bcb.sisaps.web.page.dominio.gerenciarnotasintese.aqt.GerenciarNotaSinteseAQTPage;

@SuppressWarnings("serial")
public class PainelEdicaoPercentualAnef extends PainelSisAps {

    private static final String BTT_CONFIRMAR_PERCENTUAL = "bttConfirmarPercentual";
    private static final String PORCENTAGEM = "%";
    private static final String ID_NOVO_PERCENTUAL_CORPORATIVO = "idNovoPercentualCorporativo";
    private static final String BTT_ALTERAR_PERCENTUAL = "bttAlterarPercentual";

    @SpringBean
    private ParametroAQTMediator parametroAQTMediator;
    @SpringBean
    private AnaliseQuantitativaAQTMediator analiseQuantitativaAQTMediator;
    @SpringBean
    private PesoAQTMediator pesoAQTMediator;

    private List<AnaliseQuantitativaAQT> aqtsRascunhos;
    private TextField<Short> txtPercentual;
    private AjaxSubmitLink botaoConfirmar;
    private List<PesoAQT> listaPesoVigente = new ArrayList<PesoAQT>();
    private List<PesoAQT> listaPesoRascunho = new ArrayList<PesoAQT>();
    private List<PesoAQT> listaPesoNovo = new ArrayList<PesoAQT>();
    private Short valor;
    private final Ciclo ciclo;
    private ListView<AnaliseQuantitativaAQT> listView;
    private int contador;

    public PainelEdicaoPercentualAnef(String id, Ciclo ciclo) {
        super(id);
        this.ciclo = ciclo;
        listaPesoRascunho.clear();
        listaPesoVigente.clear();

        listaPesoNovo.clear();
        setOutputMarkupId(true);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        aqtsRascunhos = analiseQuantitativaAQTMediator.buscarANEFsRascunho(ciclo);
        listaPesoRascunho = pesoAQTMediator.montarListaPesoEmEdicao(ciclo);
        listaPesoVigente = pesoAQTMediator.montarListaPesoVigente(ciclo);
        listaPesoNovo = pesoAQTMediator.montarListaNovoPeso(ciclo);
        addComponents();
        addBotoes();
    }

    private void addBotoes() {
        addBotaoConfirmar();
    }

    private void addComponents() {
        contador = 0;
        listView = new ListView<AnaliseQuantitativaAQT>("lista", aqtsRascunhos) {
            @Override
            protected void populateItem(ListItem<AnaliseQuantitativaAQT> item) {
                addItens(item);
                contador++;
            }

        };
        addOrReplace(listView);
    }

    private void addItens(ListItem<AnaliseQuantitativaAQT> item) {
        final AnaliseQuantitativaAQT aqtRascunho =
                AnaliseQuantitativaAQTMediator.get().buscar(item.getModelObject().getPk());
        ParametroAQT parametroAQT = parametroAQTMediator.buscarParemetroAQT(aqtRascunho);
        AnaliseQuantitativaAQT aqtVigente = analiseQuantitativaAQTMediator.buscarAQTVigente(aqtRascunho);
        item.add(new Label("componente", parametroAQT.getDescricao()));
        montarPesoVigente(item, aqtVigente);
        montarPesoRascunho(item, aqtRascunho);
        addTextPercentual(item, parametroAQT, item.getModelObject());
        AjaxSubmitLinkSisAps botaoAlterarPercentual = getBotaoAlterarPercentual();
        if (contador != 0) {
            botaoAlterarPercentual.setVisible(false);
        }
        item.add(botaoAlterarPercentual);
    }

    private void montarPesoRascunho(ListItem<AnaliseQuantitativaAQT> item, final AnaliseQuantitativaAQT aqtRascunho) {
        PesoAQT pesoRascunho =
                PesoAQTMediator.get().obterPesoRascunho(aqtRascunho.getParametroAQT(), aqtRascunho.getCiclo());
        item.add(new Label("idPercentualEmEdicao", new Model<String>(peso(pesoRascunho) + PORCENTAGEM)));
    }

    private void montarPesoVigente(ListItem<AnaliseQuantitativaAQT> item, AnaliseQuantitativaAQT aqtVigente) {
        PesoAQT pesoVigente =
                PesoAQTMediator.get().obterPesoVigente(aqtVigente.getParametroAQT(), aqtVigente.getCiclo());
        item.add(new Label("idPercentualVigente", new Model<String>(peso(pesoVigente) + PORCENTAGEM)));
    }

    private String peso(PesoAQT pesoAqt) {
        return pesoAqt == null ? "" : pesoAqt.getValor().toString();
    }
    
    private AjaxSubmitLinkSisAps getBotaoAlterarPercentual() {
        AjaxSubmitLinkSisAps alterar = new AjaxSubmitLinkSisAps(BTT_ALTERAR_PERCENTUAL) {
            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                alterar(target);
            }
        };
        return alterar;
    }

    private void alterar(AjaxRequestTarget target) {
        pesoAQTMediator.alterarPercentual(listaPesoNovo, listaPesoVigente, listaPesoRascunho);
        GerenciarNotaSinteseAQTPage gerenciarNotaSintesePage = (GerenciarNotaSinteseAQTPage) getPage();
        gerenciarNotaSintesePage.atualizarPainelNovoQuadroAQT(target);
        target.add(PainelEdicaoPercentualAnef.this);
        valor = null;
    }

    private void addTextPercentual(ListItem<AnaliseQuantitativaAQT> item, final ParametroAQT parametroAQT,
            final AnaliseQuantitativaAQT analiseQuantitativaAQT) {

        txtPercentual = new TextField<Short>(ID_NOVO_PERCENTUAL_CORPORATIVO, new PropertyModel<Short>(this, "valor"));
        txtPercentual.add(new AjaxFormComponentUpdatingBehavior("onKeyUp") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                adicionarPeso(parametroAQT, analiseQuantitativaAQT);
            }

        });
        txtPercentual.setMarkupId("id" + parametroAQT.getDescricao());
        txtPercentual.setOutputMarkupId(true);
        txtPercentual.setOutputMarkupPlaceholderTag(true);
        item.add(txtPercentual);
    }

    private void adicionarPeso(final ParametroAQT parametroAQT, final AnaliseQuantitativaAQT analiseQuantitativaAQT) {
        for (int i = 0; i < listaPesoNovo.size(); i++) {
            if (listaPesoNovo.get(i).getParametroAQT().equals(parametroAQT)) {
                listaPesoNovo.get(i).setValor(valor);
            }
        }
    }

    private void addBotaoConfirmar() {
        botaoConfirmar = new AjaxSubmitLinkSisAps(BTT_CONFIRMAR_PERCENTUAL) {
            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                pesoAQTMediator.confirmarPercentual(ciclo.getPk());
                GerenciarNotaSinteseAQTPage gerenciarNotaSintesePage = (GerenciarNotaSinteseAQTPage) getPage();
                gerenciarNotaSintesePage.atualizarPerfilRiscoAtual();
                gerenciarNotaSintesePage.atualizaPainelQuadroVigenteAQT(target);
                target.add(PainelEdicaoPercentualAnef.this);
                valor = null;
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                botaoConfirmar.setEnabled(pesoAQTMediator.exibirBotaoConfirmar(listaPesoVigente, listaPesoRascunho));
            }

        };
        botaoConfirmar.setOutputMarkupId(true);
        botaoConfirmar.setMarkupId(BTT_CONFIRMAR_PERCENTUAL);
        addOrReplace(botaoConfirmar);
    }

    public Short getValor() {
        return valor;
    }

    public void setValor(Short valor) {
        this.valor = valor;
    }

}
