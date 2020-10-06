package br.gov.bcb.sisaps.web.page.painelComite;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.PropertyModel;

import br.gov.bcb.sisaps.src.dominio.AjusteCorec;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.ParametroNota;
import br.gov.bcb.sisaps.src.dominio.ParametroPerspectiva;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroNotaAQT;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.mediator.AjusteCorecMediator;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.GrauPreocupacaoESMediator;
import br.gov.bcb.sisaps.src.mediator.ParametroNotaMediator;
import br.gov.bcb.sisaps.src.mediator.ParametroPerspectivaMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.ParametroNotaAQTMediator;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.botoes.AjaxSubmitLinkSisAps;
import br.gov.bcb.sisaps.web.page.componentes.drops.CustomDropDownChoice;

public class PainelAjusteCorec extends PainelSisAps {

    private static final String NOTA = "descricao";
    private static final String OPCAO_SELECIONE = "Selecione";
    private static final String ASTERISTICO = " *";

    private Label labelAlertaPainel;
    private AjaxSubmitLinkSisAps botaoSalvarInformacoes;
    private final Ciclo ciclo;
    private AjusteCorec ajusteCorec;
    private Label labelAlertaGrau;
    private Label labelAlertaQualitativa;
    private Label labelAlertaQuantitativa;
    private Label labelAlertaPerspectiva;
    private PerfilRisco perfilRiscoAtual;
    private final List<String> idsAlertas = new ArrayList<String>();

    public PainelAjusteCorec(String id, Ciclo ciclo) {
        super(id);
        setOutputMarkupId(true);
        this.ciclo = ciclo;

    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        perfilRiscoAtual = PerfilRiscoMediator.get().obterPerfilRiscoAtual(ciclo.getPk());
        ajusteCorec = AjusteCorecMediator.get().buscarPorCiclo(ciclo);
    }
    
    @Override
    protected void onConfigure() {
        super.onConfigure();
        addComponents();
    }

    private void addComponents() {
        addDadosGrauPreocupacao();
        addDadosNotaQualitativa();
        addDadosNotaQuantitativa();
        addDadosPerspectiva();
        addBotaoSalvarInformacoes();

    }

    private void addBotaoSalvarInformacoes() {
        botaoSalvarInformacoes = botaoSalvarInformacoes();
        botaoSalvarInformacoes.setOutputMarkupId(true);
        botaoSalvarInformacoes.setEnabled(false);
        addOrReplace(botaoSalvarInformacoes);

        labelAlertaPainel = new Label("idAlertaPainelCorec", "");
        labelAlertaPainel.setOutputMarkupId(true);
        addOrReplace(labelAlertaPainel);

    }

    private AjaxSubmitLinkSisAps botaoSalvarInformacoes() {
        return new AjaxSubmitLinkSisAps("bttSalvarAjusteCorec") {
            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                success(AjusteCorecMediator.get().salvarAjusteCorec(atualizarAjuste()));
                atualizar(target);
            }

        };
    }

    private void atualizar(AjaxRequestTarget target) {
        labelAlertaPainel.setDefaultModelObject("");
        target.add(labelAlertaPainel);
        botaoSalvarInformacoes.setEnabled(false);
        target.add(botaoSalvarInformacoes);
        labelAlertaGrau.setDefaultModelObject("");
        labelAlertaQualitativa.setDefaultModelObject("");
        labelAlertaQuantitativa.setDefaultModelObject("");
        labelAlertaPerspectiva.setDefaultModelObject("");
        target.add(labelAlertaGrau);
        target.add(labelAlertaQualitativa);
        target.add(labelAlertaQuantitativa);
        target.add(labelAlertaPerspectiva);
        ((GestaoCorecPage) getPage()).exibirBotaoVoltarAoSalvar();
        atualizarBotaoEncerrarCorec(target);
    }

    private AjusteCorec atualizarAjuste() {
        AjusteCorec ajusteCorecBase = AjusteCorecMediator.get().buscarPorCiclo(ciclo);
        ajusteCorecBase.setPerspectiva(ajusteCorec.getPerspectiva());
        ajusteCorecBase.setNotaFinal(ajusteCorec.getNotaFinal());
        ajusteCorecBase.setNotaQualitativa(ajusteCorec.getNotaQualitativa());
        ajusteCorecBase.setNotaQuantitativa(ajusteCorec.getNotaQuantitativa());
        return ajusteCorecBase;
    }

    private void addDadosGrauPreocupacao() {
        String notaFinal = GrauPreocupacaoESMediator.get().getNotaFinal(perfilRiscoAtual, PerfilAcessoEnum.COMITE,
                ciclo.getMetodologia());
        
        Label labelNotaGrauSupervisor =
                new Label("idParametroNotaGrauSupervisor", notaFinal == null ? "" : notaFinal);

        labelAlertaGrau = new Label("idAlertaGrau", ajusteCorec.isAlterouGrau() ? ASTERISTICO : "");
        labelAlertaGrau.setOutputMarkupId(true);
        ChoiceRenderer<ParametroNota> renderer =
                new ChoiceRenderer<ParametroNota>(NOTA, ParametroNota.PROP_ID);
        List<ParametroNota> listaChoices =
                ParametroNotaMediator.get().buscarParametrosNotaFinal(ciclo,
                        ciclo.getMetodologia().getPk(), ajusteCorec);

        PropertyModel<ParametroNota> propertyModel =
                new PropertyModel<ParametroNota>(ajusteCorec, "notaFinal");
        DropDownChoice<ParametroNota> selectParametroNotaGrau =
                new CustomDropDownChoice<ParametroNota>("idParametroNotaGrau", OPCAO_SELECIONE,
                        propertyModel, listaChoices, renderer);
        selectParametroNotaGrau.setOutputMarkupId(true);
        selectParametroNotaGrau.add(new OnChangeAjaxBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                ajusteCorec.setAlterouGrau(true);
                labelAlertaGrau.setDefaultModelObject(ASTERISTICO);
                target.add(labelAlertaGrau);
                ((GestaoCorecPage) getPage()).exibirBotaoVoltarSemSalvar();
                atualizarBotaoSalvarEAlerta(target);
                atualizarBotaoEncerrarCorec(target);
            }

        });
        addOrReplace(labelNotaGrauSupervisor);
        addOrReplace(labelAlertaGrau);
        addOrReplace(selectParametroNotaGrau);

    }

    private void atualizarBotaoSalvarEAlerta(AjaxRequestTarget target) {
        labelAlertaPainel.setDefaultModelObject("*Atenção informações não salvas.");
        botaoSalvarInformacoes.setEnabled(true);
        labelAlertaPainel.setOutputMarkupId(true);
        idsAlertas.add(labelAlertaPainel.getMarkupId());
        target.add(labelAlertaPainel);
        target.add(botaoSalvarInformacoes);
    }

    private void addDadosNotaQualitativa() {
        ParametroNota notaQualitativa = CicloMediator.get().notaQualitativa(ciclo, PerfilAcessoEnum.COMITE);
        Label labelNotaQualitativa =
                new Label("idParametroNotaQualitativaSupervisor", notaQualitativa == null ? ""
                        : notaQualitativa.getDescricao());

        labelAlertaQualitativa =
                new Label("idAlertaQualitativa", ajusteCorec.isAlterouQualitativa() ? ASTERISTICO : "");
        labelAlertaQualitativa.setOutputMarkupId(true);
        ChoiceRenderer<ParametroNota> renderer = new ChoiceRenderer<ParametroNota>(NOTA, ParametroNota.PROP_ID);
        List<ParametroNota> listaChoices =
                ParametroNotaMediator.get().buscarParametrosNotaQualitativa(ciclo,
                        ciclo.getMetodologia().getPk(), ajusteCorec);

        PropertyModel<ParametroNota> propertyModel = new PropertyModel<ParametroNota>(ajusteCorec, "notaQualitativa");
        DropDownChoice<ParametroNota> selectParametroNotaQualitativa =
                new CustomDropDownChoice<ParametroNota>("idParametroNotaQualitativa", OPCAO_SELECIONE, propertyModel,
                        listaChoices, renderer);
        selectParametroNotaQualitativa.setOutputMarkupId(true);
        selectParametroNotaQualitativa.add(new OnChangeAjaxBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                ajusteCorec.setAlterouQualitativa(true);
                labelAlertaQualitativa.setDefaultModelObject(ASTERISTICO);
                target.add(labelAlertaQualitativa);
                atualizarBotaoSalvarEAlerta(target);
                atualizarBotaoEncerrarCorec(target);
                ((GestaoCorecPage) getPage()).exibirBotaoVoltarSemSalvar();
            }
        });
        addOrReplace(labelNotaQualitativa);
        addOrReplace(labelAlertaQualitativa);
        addOrReplace(selectParametroNotaQualitativa);

    }

    private void addDadosNotaQuantitativa() {
        Label labelNotaQuantitativa =
                new Label("idParametroNotaQuantitativaSupervisor", CicloMediator.get()
                        .notaQuantitativa(ciclo, PerfilAcessoEnum.SUPERVISOR).getDescricao());

        labelAlertaQuantitativa =
                new Label("idAlertaQuantitativa", ajusteCorec.isAlterouQualitativa() ? ASTERISTICO : "");
        labelAlertaQuantitativa.setOutputMarkupId(true);
        ChoiceRenderer<ParametroNotaAQT> renderer = new ChoiceRenderer<ParametroNotaAQT>(NOTA, ParametroNota.PROP_ID);
        List<ParametroNotaAQT> listaChoices =
                ParametroNotaAQTMediator.get().buscarParametrosNotaQuantitativa(ciclo,
                        ciclo.getMetodologia().getPk(), ajusteCorec);

        PropertyModel<ParametroNotaAQT> propertyModel =
                new PropertyModel<ParametroNotaAQT>(ajusteCorec, "notaQuantitativa");
        DropDownChoice<ParametroNotaAQT> selectParametroNotaQuantitativa =
                new CustomDropDownChoice<ParametroNotaAQT>("idParametroNotaQuantitativa", OPCAO_SELECIONE,
                        propertyModel, listaChoices, renderer);
        selectParametroNotaQuantitativa.setOutputMarkupId(true);
        selectParametroNotaQuantitativa.add(new OnChangeAjaxBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                ajusteCorec.setAlterouQuantitativa(true);
                labelAlertaQuantitativa.setDefaultModelObject(ASTERISTICO);
                target.add(labelAlertaQuantitativa);
                atualizarBotaoSalvarEAlerta(target);
                atualizarBotaoEncerrarCorec(target);
                ((GestaoCorecPage) getPage()).exibirBotaoVoltarSemSalvar();
            }
        });
        addOrReplace(labelNotaQuantitativa);
        addOrReplace(labelAlertaQuantitativa);
        addOrReplace(selectParametroNotaQuantitativa);

    }

    private void addDadosPerspectiva() {
        Label labelNotaPerspectivaSupervisor =
                new Label("idParametroPerspectivaSupervisor", PerfilRiscoMediator.get()
                        .getPerspectivaESPerfilRisco(perfilRiscoAtual).getParametroPerspectiva().getNome());

        labelAlertaPerspectiva = new Label("idAlertaPerspectiva", ajusteCorec.isAlterouGrau() ? ASTERISTICO : "");
        labelAlertaPerspectiva.setOutputMarkupId(true);
        ChoiceRenderer<ParametroPerspectiva> renderer =
                new ChoiceRenderer<ParametroPerspectiva>("nome", ParametroPerspectiva.PROP_ID);
        List<ParametroPerspectiva> listaChoices =
                ParametroPerspectivaMediator.get().buscarParametrosPerspectivaCorec(
                        ciclo.getMetodologia(), ciclo, ajusteCorec);

        PropertyModel<ParametroPerspectiva> propertyModel =
                new PropertyModel<ParametroPerspectiva>(ajusteCorec, "perspectiva");
        DropDownChoice<ParametroPerspectiva> selectParametroNotaGrau =
                new CustomDropDownChoice<ParametroPerspectiva>("idParametroPerspectiva", OPCAO_SELECIONE,
                        propertyModel, listaChoices, renderer);
        selectParametroNotaGrau.setOutputMarkupId(true);
        selectParametroNotaGrau.add(new OnChangeAjaxBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                ajusteCorec.setAlterouGrau(true);
                labelAlertaPerspectiva.setDefaultModelObject(ASTERISTICO);
                target.add(labelAlertaPerspectiva);
                atualizarBotaoSalvarEAlerta(target);
                atualizarBotaoEncerrarCorec(target);
                ((GestaoCorecPage) getPage()).exibirBotaoVoltarSemSalvar();
            }

        });
        addOrReplace(labelNotaPerspectivaSupervisor);
        addOrReplace(labelAlertaPerspectiva);
        addOrReplace(selectParametroNotaGrau);

    }

    public AjaxSubmitLinkSisAps getBotaoSalvarInformacoes() {
        return botaoSalvarInformacoes;
    }

    private void atualizarBotaoEncerrarCorec(AjaxRequestTarget target) {
        ((GestaoCorecPage) getPage()).atualizarBotaoEncerrarCorec(target);
    }

    public void setBotaoSalvarInformacoes(AjaxSubmitLinkSisAps botaoSalvarInformacoes) {
        this.botaoSalvarInformacoes = botaoSalvarInformacoes;
    }

    public List<String> getIdsAlertas() {
        return idsAlertas;
    }

}
