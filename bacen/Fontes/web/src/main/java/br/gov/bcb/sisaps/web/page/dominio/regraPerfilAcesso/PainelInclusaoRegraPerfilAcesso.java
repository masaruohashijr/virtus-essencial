package br.gov.bcb.sisaps.web.page.dominio.regraPerfilAcesso;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;

import br.gov.bcb.sisaps.adaptadores.pessoa.BcPessoaAdapter;
import br.gov.bcb.sisaps.adaptadores.pessoa.FuncaoComissionadaVO;
import br.gov.bcb.sisaps.src.dominio.RegraPerfilAcesso;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.SituacaoFuncionalEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.SubstitutoEventualEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.SubstitutoPrazoCertoEnum;
import br.gov.bcb.sisaps.src.mediator.RegraPerfilAcessoMediator;
import br.gov.bcb.sisaps.src.vo.RegraPerfilAcessoVO;
import br.gov.bcb.sisaps.util.Util;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import br.gov.bcb.sisaps.web.page.componentes.drops.CustomDropDownChoice;
import br.gov.bcb.sisaps.web.page.componentes.mascara.MascaraDeMatriculaBehavior;
import br.gov.bcb.sisaps.web.page.componentes.modal.AjaxSubmitLinkModalWindow;
import br.gov.bcb.sisaps.web.page.componentes.modal.PainelModalWindow;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Tabela;

public class PainelInclusaoRegraPerfilAcesso extends PainelModalWindow {

    private static final String ONCLICK = "onclick";
    private static final String ID_MATRICULA = "idMatricula";
    private static final String ID_LOCALIZACAO = "idLocalizacao";
    private static final String PROP_DESCRICAO = "descricao";
    private static final String OPCAO_SELECIONE = "Selecione";
    private static final String PROP_CODIGO_DESCRICAO = "codigoDescricao";
    private static final String PROP_CODIGO = "codigo";

    @SpringBean
    private RegraPerfilAcessoMediator regraPerfilAcessoMediator;

    private final ModalWindow modalWindow;
    private final PerfilAcessoEnum perfilAcesso;
    private final IModel<FuncaoComissionadaVO> modelFuncaoComissionada = new Model<FuncaoComissionadaVO>();

    private RegraPerfilAcesso regraPerfilAcesso;
    private TextField<String> localizacao;
    private RadioGroup<SimNaoEnum> radioLocalizacoesSubordinadas;
    private DropDownChoice<SubstitutoEventualEnum> selectSubstitutoEventual;
    private DropDownChoice<SubstitutoPrazoCertoEnum> selectSubstitutoPrazoCerto;
    private final Tabela<RegraPerfilAcessoVO> tabela;
    private String matricula;
    private Radio<SimNaoEnum> radioLocalizacoesSubordinadasSim;
    private Radio<SimNaoEnum> radioLocalizacoesSubordinadasNao;

    public PainelInclusaoRegraPerfilAcesso(ModalWindow modalWindow, PerfilAcessoEnum perfilAcesso,
            Tabela<RegraPerfilAcessoVO> tabela) {
        super(modalWindow.getContentId());
        this.modalWindow = modalWindow;
        this.perfilAcesso = perfilAcesso;
        this.tabela = tabela;
        setOutputMarkupId(true);
        setMarkupId(getId());

    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        regraPerfilAcesso = new RegraPerfilAcesso();
        regraPerfilAcesso.setPerfilAcesso(perfilAcesso);
        Form<RegraPerfilAcesso> form = new Form<RegraPerfilAcesso>("formInclusaoRegra");
        addComponentes(form);
        addBotoes(form);
        addOrReplace(form);
    }

    private void addComponentes(Form<RegraPerfilAcesso> form) {
        addTextFieldLocalizacao(form);
        addRadioLocalizacoesSubordinadas2(form);
        addComboFuncoes(form);
        addComboSubstitutoEventual(form);
        addComboSubstitutoPrazoCerto(form);
        addComboSituacao(form);
        addTextFieldMatricula(form);
    }

    private void addBotoes(Form<RegraPerfilAcesso> form) {
        form.addOrReplace(new LinkFechar());
        form.addOrReplace(new LinkIncluir());
    }

    private void addTextFieldLocalizacao(Form<RegraPerfilAcesso> form) {
        localizacao =
                new TextField<String>(ID_LOCALIZACAO, new PropertyModel<String>(regraPerfilAcesso, "localizacao"));
        localizacao.add(StringValidator.maximumLength(30));
        localizacao.add(new AjaxFormComponentUpdatingBehavior("onkeyup") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                if (localizacao.getInput() == null || "".equals(localizacao.getInput())) {
                    radioLocalizacoesSubordinadas.setModelObject(SimNaoEnum.NAO);
                }
                atualizarRadioLocalizacoesSubordinadas(target);
            }
        });
        localizacao.clearInput();
        localizacao.setMarkupId(ID_LOCALIZACAO);
        form.addOrReplace(localizacao);
    }

    private void addRadioLocalizacoesSubordinadas2(Form<RegraPerfilAcesso> form) {
        PropertyModel<SimNaoEnum> propertyModel =
                new PropertyModel<SimNaoEnum>(regraPerfilAcesso, "localizacoesSubordinadas");
        radioLocalizacoesSubordinadas = new RadioGroup<SimNaoEnum>("idLocalizacoesSubordinadas", propertyModel);

        radioLocalizacoesSubordinadasSim =
                new Radio<SimNaoEnum>("idLocalizacoesSubordinadasSim", new Model<SimNaoEnum>(SimNaoEnum.SIM)) {
                    @Override
                    protected void onConfigure() {
                        super.onConfigure();
                        setEnabled(localizacao.getInput() != null && !"".equals(localizacao.getInput()));
                    }
                };
        radioLocalizacoesSubordinadasSim.setOutputMarkupId(true);
        radioLocalizacoesSubordinadasSim.setMarkupId(radioLocalizacoesSubordinadasSim.getId());
        radioLocalizacoesSubordinadasSim.add(new AjaxFormSubmitBehavior(ONCLICK) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                radioLocalizacoesSubordinadas.setModelObject(radioLocalizacoesSubordinadasSim.getModelObject());
                atualizarRadioLocalizacoesSubordinadas(target);
            }
        });
        radioLocalizacoesSubordinadas.addOrReplace(radioLocalizacoesSubordinadasSim);

        radioLocalizacoesSubordinadasNao =
                new Radio<SimNaoEnum>("idLocalizacoesSubordinadasNao", new Model<SimNaoEnum>(SimNaoEnum.NAO)) {
                    @Override
                    protected void onConfigure() {
                        super.onConfigure();
                        setEnabled(localizacao.getInput() != null && !"".equals(localizacao.getInput()));
                    }
                };
        radioLocalizacoesSubordinadasNao.setOutputMarkupId(true);
        radioLocalizacoesSubordinadasNao.setMarkupId(radioLocalizacoesSubordinadasNao.getId());
        radioLocalizacoesSubordinadasNao.add(new AjaxFormSubmitBehavior(ONCLICK) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                radioLocalizacoesSubordinadas.setModelObject(radioLocalizacoesSubordinadasNao.getModelObject());
                atualizarRadioLocalizacoesSubordinadas(target);
            }
        });
        radioLocalizacoesSubordinadas.addOrReplace(radioLocalizacoesSubordinadasNao);
        radioLocalizacoesSubordinadas.setModelObject(SimNaoEnum.NAO);
        form.addOrReplace(radioLocalizacoesSubordinadas);
    }

    private void addComboFuncoes(Form<RegraPerfilAcesso> form) {
        ChoiceRenderer<FuncaoComissionadaVO> renderer =
                new ChoiceRenderer<FuncaoComissionadaVO>(PROP_CODIGO_DESCRICAO, PROP_CODIGO);
        List<FuncaoComissionadaVO> lista = BcPessoaAdapter.get().consultarTodasFuncoesComissionadas();
        CustomDropDownChoice<FuncaoComissionadaVO> selectServidorEquipe =
                new CustomDropDownChoice<FuncaoComissionadaVO>("idFuncao", OPCAO_SELECIONE, modelFuncaoComissionada,
                        lista, renderer);
        selectServidorEquipe.add(new OnChangeAjaxBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                if (modelFuncaoComissionada.getObject() == null) {
                    selectSubstitutoEventual.setDefaultModelObject(null);
                    selectSubstitutoPrazoCerto.setDefaultModelObject(null);
                }
                target.add(selectSubstitutoEventual);
                target.add(selectSubstitutoPrazoCerto);
            }
        });
        form.addOrReplace(selectServidorEquipe);
    }

    private void addComboSubstitutoEventual(Form<RegraPerfilAcesso> form) {
        ChoiceRenderer<SubstitutoEventualEnum> renderer =
                new ChoiceRenderer<SubstitutoEventualEnum>(PROP_DESCRICAO, PROP_CODIGO);
        List<SubstitutoEventualEnum> listaChoices = Arrays.asList(SubstitutoEventualEnum.values());
        PropertyModel<SubstitutoEventualEnum> propertyModel =
                new PropertyModel<SubstitutoEventualEnum>(regraPerfilAcesso, "substitutoEventualFuncao");
        selectSubstitutoEventual =
                new CustomDropDownChoice<SubstitutoEventualEnum>("idSubstitutoEventual", OPCAO_SELECIONE,
                        propertyModel, listaChoices, renderer) {
                    @Override
                    protected void onConfigure() {
                        super.onConfigure();
                        setEnabled(modelFuncaoComissionada.getObject() != null);
                    }
                };
        selectSubstitutoEventual.add(new OnChangeAjaxBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                //TODO não precisa implementar
            }
        });
        selectSubstitutoEventual.setOutputMarkupId(true);
        form.addOrReplace(selectSubstitutoEventual);
    }

    private void addComboSubstitutoPrazoCerto(Form<RegraPerfilAcesso> form) {
        ChoiceRenderer<SubstitutoPrazoCertoEnum> renderer =
                new ChoiceRenderer<SubstitutoPrazoCertoEnum>(PROP_DESCRICAO, PROP_CODIGO);
        List<SubstitutoPrazoCertoEnum> listaChoices = Arrays.asList(SubstitutoPrazoCertoEnum.values());
        PropertyModel<SubstitutoPrazoCertoEnum> propertyModel =
                new PropertyModel<SubstitutoPrazoCertoEnum>(regraPerfilAcesso, "substitutoPrazoCerto");
        selectSubstitutoPrazoCerto =
                new CustomDropDownChoice<SubstitutoPrazoCertoEnum>("idSubstitutoPrazoCerto", OPCAO_SELECIONE,
                        propertyModel, listaChoices, renderer) {
                    @Override
                    protected void onConfigure() {
                        super.onConfigure();
                        setEnabled(modelFuncaoComissionada.getObject() != null);
                    }
                };
        selectSubstitutoPrazoCerto.add(new OnChangeAjaxBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                //TODO não precisa implementar
            }
        });
        selectSubstitutoPrazoCerto.setOutputMarkupId(true);
        form.addOrReplace(selectSubstitutoPrazoCerto);
    }

    private void addComboSituacao(Form<RegraPerfilAcesso> form) {
        ChoiceRenderer<SituacaoFuncionalEnum> renderer =
                new ChoiceRenderer<SituacaoFuncionalEnum>(PROP_CODIGO_DESCRICAO, PROP_CODIGO);
        List<SituacaoFuncionalEnum> listaChoices = Arrays.asList(SituacaoFuncionalEnum.values());
        PropertyModel<SituacaoFuncionalEnum> propertyModel =
                new PropertyModel<SituacaoFuncionalEnum>(regraPerfilAcesso, "situacao");
        DropDownChoice<SituacaoFuncionalEnum> selectSubstitutoEventual =
                new CustomDropDownChoice<SituacaoFuncionalEnum>("idSituacao", OPCAO_SELECIONE, propertyModel,
                        listaChoices, renderer);

        if (regraPerfilAcesso.getSituacao() == null) {
            selectSubstitutoEventual.setDefaultModelObject(null);
        }
        form.addOrReplace(selectSubstitutoEventual);
    }

    private void addTextFieldMatricula(Form<RegraPerfilAcesso> form) {
        TextField<String> matricula = new TextField<String>(ID_MATRICULA, new PropertyModel<String>(this, "matricula"));
        matricula.setMarkupId(ID_MATRICULA);
        matricula.add(new MascaraDeMatriculaBehavior());
        form.addOrReplace(matricula);

    }

    private class LinkFechar extends AjaxLink<Void> {
        public LinkFechar() {
            super("bttVoltarInclusaoRegra");
        }

        @Override
        public void onClick(AjaxRequestTarget target) {
            modalWindow.close(target);
        }
    }

    private class LinkIncluir extends AjaxSubmitLinkModalWindow {
        public LinkIncluir() {
            super("bttIncluirRegra");
        }

        @Override
        public void executeSubmit(AjaxRequestTarget target) {
            regraPerfilAcesso.setMatricula(Util.normalizarMatriculaCpf(matricula));

            if (modelFuncaoComissionada.getObject() == null) {
                regraPerfilAcesso.setCodigoFuncao(null);
            } else {
                regraPerfilAcesso.setCodigoFuncao(modelFuncaoComissionada.getObject().getCodigo());
            }
            regraPerfilAcessoMediator.incluir(regraPerfilAcesso);
            target.add(tabela, tabela.getMarkupId());
            modalWindow.close(target);
            atualizar(target);
        }
    }

    private void atualizar(AjaxRequestTarget target) {
        ((GestaoPerfilAcessoPage) getPage()).atualizarPaineis(target);
    }

    private void atualizarRadioLocalizacoesSubordinadas(AjaxRequestTarget target) {
        target.add(radioLocalizacoesSubordinadasSim);
        target.add(radioLocalizacoesSubordinadasNao);
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

}
