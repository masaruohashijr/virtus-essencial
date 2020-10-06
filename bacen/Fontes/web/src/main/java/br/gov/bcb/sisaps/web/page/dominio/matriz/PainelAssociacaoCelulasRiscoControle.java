package br.gov.bcb.sisaps.web.page.dominio.matriz;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.PropertyModel;

import br.gov.bcb.sisaps.src.dominio.Atividade;
import br.gov.bcb.sisaps.src.dominio.CelulaRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Metodologia;
import br.gov.bcb.sisaps.src.dominio.ParametroGrupoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.ParametroPeso;
import br.gov.bcb.sisaps.src.mediator.CelulaRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.MetodologiaMediator;
import br.gov.bcb.sisaps.util.validacao.ValidadorCelulaRiscoControle;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.drops.CustomDropDownChoice;
import br.gov.bcb.sisaps.web.page.componentes.util.ConstantesImagens;

public class PainelAssociacaoCelulasRiscoControle extends PainelSisAps {

    private static final String OPCAO_SELECIONE = "Selecionar";
    
    private Metodologia metodologia;
    private Atividade atividade;
    private CelulaRiscoControle celulaRiscoControle;
    private CustomDropDownChoice<ParametroGrupoRiscoControle> selectParametroGrupoRiscoControle;

    private CustomDropDownChoice<ParametroPeso> selectParametroPeso;

    private ParametroPeso maiorPeso;

    private PainelCelulasRiscoControleListagem painelCelulasRiscoControleListagem;

    private String sufixoMarkupId;
    private  boolean isInclusao;

    public PainelAssociacaoCelulasRiscoControle(String id, Metodologia metodologia, Atividade atividade, 
            String sufixoMarkupId,  boolean isInclusao) {
        super(id);
        this.metodologia = metodologia;
        this.atividade = atividade;
        this.sufixoMarkupId = sufixoMarkupId;
        this.isInclusao = isInclusao;
        setOutputMarkupId(true);
    }
    
    @Override
    protected void onInitialize() {
        super.onInitialize();
        celulaRiscoControle = new CelulaRiscoControle();
        maiorPeso = MetodologiaMediator.get().buscarMaiorPesoMetodologia(metodologia);
        addGrupoRiscoControle();
        addParametroPeso();
        addLinkAdicionar();
        addPainelListagem();
    }

    private void addGrupoRiscoControle() {
        ChoiceRenderer<ParametroGrupoRiscoControle> renderer =
                new ChoiceRenderer<ParametroGrupoRiscoControle>("abreviado", ParametroGrupoRiscoControle.PROP_ID);
        List<ParametroGrupoRiscoControle> listaParametrosGrupoRC = metodologia.getParametrosGrupoRiscoControleMatriz();
        PropertyModel<ParametroGrupoRiscoControle> propertyModel =
                new PropertyModel<ParametroGrupoRiscoControle>(celulaRiscoControle, "parametroGrupoRiscoControle");
        selectParametroGrupoRiscoControle =
                new CustomDropDownChoice<ParametroGrupoRiscoControle>("idParametroGrupoRC", OPCAO_SELECIONE,
                        propertyModel, listaParametrosGrupoRC, renderer);
        selectParametroGrupoRiscoControle.setOutputMarkupId(true);
        selectParametroGrupoRiscoControle.setDefaultModelObject(null);
        selectParametroGrupoRiscoControle.setMarkupId(selectParametroGrupoRiscoControle.getId() + sufixoMarkupId);
        addOrReplace(selectParametroGrupoRiscoControle);
    }

    private void addParametroPeso() {
        ChoiceRenderer<ParametroPeso> renderer = new ChoiceRenderer<ParametroPeso>("descricao", ParametroPeso.PROP_ID);
        List<ParametroPeso> listaChoices = MetodologiaMediator.get().buscarListaParametroPesoMetodologia(metodologia);
        PropertyModel<ParametroPeso> propertyModel = new PropertyModel<ParametroPeso>(celulaRiscoControle, "parametroPeso");
        selectParametroPeso =
                new CustomDropDownChoice<ParametroPeso>("idPesoGrupoRC", OPCAO_SELECIONE, propertyModel,
                        listaChoices, renderer);
        selectParametroPeso.setOutputMarkupId(true);
        selectParametroPeso.setDefaultModelObject(maiorPeso);
        selectParametroPeso.setMarkupId(selectParametroPeso.getId() + sufixoMarkupId);
        addOrReplace(selectParametroPeso);
    }

    private void addLinkAdicionar() {
        AjaxSubmitLink link = new AjaxSubmitLink("linkAdicionar") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                CelulaRiscoControle novaCelula = extrairDadosNovaCelula();
                adicionarCelula(target, novaCelula);
            }
        };
        link.add(new Image("adicionar", ConstantesImagens.IMG_INCLUIR));
        link.setMarkupId("linkAdicionarARC" + sufixoMarkupId);
        add(link);
    }
    
    private void adicionarCelula(AjaxRequestTarget target, CelulaRiscoControle novaCelula) {
        ValidadorCelulaRiscoControle validador = new ValidadorCelulaRiscoControle();
        validador.validadorComum(novaCelula, atividade.getCelulasRiscoControle());
        if (validador.getMensagens().isEmpty()) {
            atividade.getCelulasRiscoControle().add(novaCelula);
            atualizarPainelEstadoInicial(target);
        } else {
            adicionarErros(validador);
        }
        target.add(getParent().getParent().get("feedbackmodalwindow"));
    }
    
    private void atualizarPainelEstadoInicial(AjaxRequestTarget target) {
        limparCampos();
        getPage().getFeedbackMessages().clear();
        target.add(painelCelulasRiscoControleListagem);
        target.add(selectParametroGrupoRiscoControle);
        target.add(selectParametroPeso);
        target.add(this);
    }
    
    private void limparCampos() {
        selectParametroGrupoRiscoControle.setEnabled(true);
        selectParametroGrupoRiscoControle.setDefaultModelObject(null);
        selectParametroPeso.setDefaultModelObject(maiorPeso);
    }

    private void adicionarErros(ValidadorCelulaRiscoControle validador) {
        for (String erro : validador.getMensagens()) {
            error(erro);
        }
    }
    
    private CelulaRiscoControle extrairDadosNovaCelula() {
        CelulaRiscoControle novaCelula = new CelulaRiscoControle();
        novaCelula.setParametroGrupoRiscoControle(celulaRiscoControle.getParametroGrupoRiscoControle());
        novaCelula.setParametroPeso(celulaRiscoControle.getParametroPeso());
        novaCelula.setAtividade(atividade);
        CelulaRiscoControleMediator.get().criarARCsCelula(novaCelula);
        return novaCelula;
    }

    private void addPainelListagem() {
        painelCelulasRiscoControleListagem =
                new PainelCelulasRiscoControleListagem("listagemCelulasRiscoControle", atividade, metodologia, 
                        sufixoMarkupId, isInclusao);
        add(painelCelulasRiscoControleListagem);
    }

    public PainelCelulasRiscoControleListagem getPainelCelulasRiscoControleListagem() {
        return painelCelulasRiscoControleListagem;
    }
    
}
