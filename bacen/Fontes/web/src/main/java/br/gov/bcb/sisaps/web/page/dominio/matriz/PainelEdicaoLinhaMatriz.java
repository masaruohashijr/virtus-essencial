package br.gov.bcb.sisaps.web.page.dominio.matriz;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.StringValidator;

import br.gov.bcb.sisaps.src.dominio.Atividade;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.Metodologia;
import br.gov.bcb.sisaps.src.dominio.ParametroPeso;
import br.gov.bcb.sisaps.src.dominio.ParametroTipoAtividadeNegocio;
import br.gov.bcb.sisaps.src.dominio.Unidade;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoUnidadeAtividadeEnum;
import br.gov.bcb.sisaps.src.mediator.AtividadeMediator;
import br.gov.bcb.sisaps.src.mediator.MatrizCicloMediator;
import br.gov.bcb.sisaps.src.mediator.MetodologiaMediator;
import br.gov.bcb.sisaps.web.page.componentes.drops.CustomDropDownChoice;
import br.gov.bcb.sisaps.web.page.componentes.modal.AjaxSubmitLinkModalWindow;
import br.gov.bcb.sisaps.web.page.componentes.modal.PainelModalWindow;

public class PainelEdicaoLinhaMatriz extends PainelModalWindow {

    private static final String NOME_ATIVIDADE = "nomeAtividade";
    private static final String ID_PARAMETRO_PESO = "idParametroPeso";
    private static final String CORPORATIVA = "Corporativa";
    private static final String ID_TIPO_ATIVIDADE = "idTipoAtividade";
    private static final String ID_UNIDADE = "idUnidade";
    private static final String OPCAO_SELECIONE = "Selecionar";
    private static final String PROP_NOME = "nome";

    private ModalWindow modalWindow;
    private Ciclo ciclo;
    private Atividade atividade;
    private boolean isInclusao;
    private Matriz matrizAtual;
    private Form<Object> form;
    private ParametroTipoAtividadeNegocio parametroTipoAtividadeCorporativa;
    private CustomDropDownChoice<Unidade> selectUnidade;
    private PainelAssociacaoCelulasRiscoControle painel;

    private String sufixoMarkupId;

    public PainelEdicaoLinhaMatriz(ModalWindow modalWindow, Ciclo ciclo, Atividade atividade, boolean isInclusao) {
        super(modalWindow.getContentId());
        this.modalWindow = modalWindow;
        this.ciclo = ciclo;
        this.atividade = atividade;
        this.isInclusao = isInclusao;
        if (isInclusao) {
            sufixoMarkupId = "_I";
        } else {
            sufixoMarkupId = "_A";
        }
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        form = new Form<Object>("form");
        this.matrizAtual = MatrizCicloMediator.get().getUltimaMatrizCiclo(ciclo);
        if (isInclusao) {
            atividade = new Atividade();
            atividade.setMatriz(matrizAtual);
        }
        addComponentesAtividade();
        form.addOrReplace(new LinkFechar());
        form.addOrReplace(new LinkConcluir());
        addOrReplace(form);
    }

    private void addComponentesAtividade() {
        addNomeAtividade();
        addTipoAtividade();
        addPeso();
        addUnidade();
        addPainelCelulasRiscoControle();
    }

    private void addNomeAtividade() {
        TextField<String> nomeAtividade =
                new TextField<String>(NOME_ATIVIDADE, new PropertyModel<String>(atividade, PROP_NOME));
        nomeAtividade.add(StringValidator.maximumLength(100));
        nomeAtividade.setMarkupId(NOME_ATIVIDADE + sufixoMarkupId);
        form.addOrReplace(nomeAtividade);
    }

    private void addTipoAtividade() {
        Metodologia metodologia = ciclo.getMetodologia();

        ChoiceRenderer<ParametroTipoAtividadeNegocio> renderer =
                new ChoiceRenderer<ParametroTipoAtividadeNegocio>(PROP_NOME, ParametroTipoAtividadeNegocio.PROP_ID);
        PropertyModel<ParametroTipoAtividadeNegocio> propertyModel =
                new PropertyModel<ParametroTipoAtividadeNegocio>(atividade, "parametroTipoAtividadeNegocio");
        List<ParametroTipoAtividadeNegocio> listaChoices;

        listaChoices = metodologia.getParametrosTipoAtividadeNegocio();
        parametroTipoAtividadeCorporativa = new ParametroTipoAtividadeNegocio();
        parametroTipoAtividadeCorporativa.setNome(CORPORATIVA);
        parametroTipoAtividadeCorporativa.setPk(99);
        listaChoices.add(parametroTipoAtividadeCorporativa);
        CustomDropDownChoice<ParametroTipoAtividadeNegocio> selectAtividade =
                new CustomDropDownChoice<ParametroTipoAtividadeNegocio>(ID_TIPO_ATIVIDADE, OPCAO_SELECIONE,
                        propertyModel, listaChoices, renderer);
        validacaoParametroTipoAtividade(selectAtividade);
        selectTipoAtividade(selectAtividade);

        if (atividade.getUnidade() != null
                && (!isInclusao && atividade.getUnidade().equals(matrizAtual.getUnidadeCorporativa()))) {
            selectAtividade.setEnabled(false);
        }
        selectAtividade.setMarkupId(ID_TIPO_ATIVIDADE + sufixoMarkupId);
        form.addOrReplace(selectAtividade);
    }

    private void validacaoParametroTipoAtividade(CustomDropDownChoice<ParametroTipoAtividadeNegocio> selectAtividade) {
        if (isInclusao) {
            selectAtividade.setDefaultModelObject(null);
        } else if (atividade.getParametroTipoAtividadeNegocio() == null) {
            selectAtividade.setDefaultModelObject(parametroTipoAtividadeCorporativa);
        } else if (atividade.getParametroTipoAtividadeNegocio() != null) {
            selectAtividade.setDefaultModelObject(atividade.getParametroTipoAtividadeNegocio());
        }
    }

    private void selectTipoAtividade(final CustomDropDownChoice<ParametroTipoAtividadeNegocio> selectAtividade) {
        selectAtividade.add(new OnChangeAjaxBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                if (selectAtividade.getModelObject() != null
                        && selectAtividade.getModelObject().equals(parametroTipoAtividadeCorporativa)) {
                    selectUnidade.setEnabled(false);
                } else {
                    selectUnidade.setEnabled(true);
                }
                target.add(selectUnidade);
            }
        });
    }

    private void addPeso() {
        Metodologia metodologia = ciclo.getMetodologia();
        ChoiceRenderer<ParametroPeso> renderer = new ChoiceRenderer<ParametroPeso>("descricao", ParametroPeso.PROP_ID);
        List<ParametroPeso> listaChoices = MetodologiaMediator.get().buscarListaParametroPesoMetodologia(metodologia);
        PropertyModel<ParametroPeso> propertyModel = new PropertyModel<ParametroPeso>(atividade, "parametroPeso");
        DropDownChoice<ParametroPeso> selectParametroPeso =
                new CustomDropDownChoice<ParametroPeso>(ID_PARAMETRO_PESO, OPCAO_SELECIONE, propertyModel,
                        listaChoices, renderer);
        selectParametroPeso.setMarkupId(ID_PARAMETRO_PESO + sufixoMarkupId);
        if (isInclusao) {
            ParametroPeso maiorPeso = MetodologiaMediator.get().buscarMaiorPesoMetodologia(metodologia);
            selectParametroPeso.setDefaultModelObject(maiorPeso);
        }
        form.addOrReplace(selectParametroPeso);
    }

    private void addUnidade() {
        ChoiceRenderer<Unidade> renderer = new ChoiceRenderer<Unidade>(PROP_NOME, Unidade.PROP_ID);
        PropertyModel<Unidade> propertyModel = new PropertyModel<Unidade>(atividade, "unidade");
        selectUnidade =
                new CustomDropDownChoice<Unidade>(ID_UNIDADE, OPCAO_SELECIONE, propertyModel,
                        matrizAtual.getUnidadesNegocio(), renderer);
        if (!isInclusao && atividade.getUnidade() != null) {
            if (atividade.getUnidade().equals(matrizAtual.getUnidadeCorporativa())) {
                selectUnidade.setEnabled(false);
            }
            selectUnidade.setDefaultModelObject(atividade.getUnidade());
        }
        selectUnidade.setMarkupId(ID_UNIDADE + sufixoMarkupId);
        form.addOrReplace(selectUnidade);
    }

    private void addPainelCelulasRiscoControle() {
        painel =
                new PainelAssociacaoCelulasRiscoControle("idPainelCelulas", ciclo.getMetodologia(), atividade,
                        sufixoMarkupId, isInclusao);
        form.add(painel);
    }

    protected class LinkFechar extends AjaxLink<Void> {
        public LinkFechar() {
            super("bttVoltarEdicaoLinhasMatriz");
            setMarkupId(getId());
        }

        @Override
        public void onClick(AjaxRequestTarget target) {
            modalWindow.close(target);
        }
    }

    protected class LinkConcluir extends AjaxSubmitLinkModalWindow {
        public LinkConcluir() {
            super("bttConcluir");
        }

        @Override
        protected void onComponentTag(ComponentTag tag) {
            super.onComponentTag(tag);
            tag.put("value", (isInclusao ? "Concluir" : "Confirmar"));
        }

        @Override
        public void executeSubmit(AjaxRequestTarget target) {
            String msgSucesso = "";
            if (parametroTipoAtividadeCorporativa.equals(atividade.getParametroTipoAtividadeNegocio())) {
                atividade.setParametroTipoAtividadeNegocio(null);
                atividade.setTipoAtividade(TipoUnidadeAtividadeEnum.CORPORATIVO);
                atividade.setUnidade(matrizAtual.getUnidadeCorporativa());
            } else {
                atividade.setTipoAtividade(TipoUnidadeAtividadeEnum.NEGOCIO);
            }
            if (isInclusao) {
                msgSucesso = AtividadeMediator.get().incluir(atividade);
            } else {
                msgSucesso =
                        AtividadeMediator.get().alterar(atividade,
                                painel.getPainelCelulasRiscoControleListagem().getListaExcluidos());
            }

            modalWindow.close(target);
            atualizar(target, msgSucesso);
        }
    }

    private void atualizar(AjaxRequestTarget target, String sucess) {
        ((EdicaoMatrizPage) getPage()).atualizarPaineis(target, sucess, true);
    }
}
