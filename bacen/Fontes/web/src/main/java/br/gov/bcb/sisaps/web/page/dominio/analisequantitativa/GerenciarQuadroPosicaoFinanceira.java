package br.gov.bcb.sisaps.web.page.dominio.analisequantitativa;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.PackageResourceReference;

import br.gov.bcb.app.stuff.util.props.PropertyUtils;
import br.gov.bcb.sisaps.integracao.seguranca.PerfisAcesso;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.DataBaseES;
import br.gov.bcb.sisaps.src.mediator.analisequantitativa.DataBaseESMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativa.QuadroPosicaoFinanceiraMediator;
import br.gov.bcb.sisaps.src.vo.analisequantitativa.QuadroPosicaoFinanceiraVO;
import br.gov.bcb.sisaps.web.page.DefaultPage;
import br.gov.bcb.sisaps.web.page.componentes.autorizacao.anotacao.SisApsAuthorizeAction;
import br.gov.bcb.sisaps.web.page.componentes.behavior.AjaxLinkIndicator;
import br.gov.bcb.sisaps.web.page.dominio.analisequantitativa.anexo.PainelAnexoQPF;
import br.gov.bcb.sisaps.web.page.dominio.ciclo.painel.PainelResumoCiclo;
import br.gov.bcb.sisaps.web.page.dominio.perfilderisco.PerfilDeRiscoPage;

@SisApsAuthorizeAction(action = Action.RENDER, roles = {PerfisAcesso.SUPERVISOR})
public class GerenciarQuadroPosicaoFinanceira extends DefaultPage {
    private QuadroPosicaoFinanceiraVO novoQuadroVO = new QuadroPosicaoFinanceiraVO();
    private PainelQuadroPosicaoFinanceira painelQuadroPosicaoFinanceira;
    private PainelSelecaoContaAtivo painelSelecaoContaAtivo;
    private PerfilRisco perfilRiscoAtual;
    private PainelSelecaoContaPassivo painelSelecaoContaPassivo;
    private PainelAjustePatrimonioReferencia painelAjustePatrimonioReferencia;
    private PainelAjusteResultado painelAjusteResultado;
    private PainelAjusteIndices painelAjusteIndices;
    private SelecaoDataBase selecaoDataBase;
    private BotaoConcluirNovoQuadro botaoConcluirNovoQuadro;
    private PainelAnexoQPF painelAnexo;
    private PainelAjustePatrimonioNovo painelAjustePatrimonioNovo;
    private PainelAjusteIndicesNovo painelAjusteIndicesNovo;
    private PainelAjusteResultadoNovo painelAjusteResultadoNovo;

    public GerenciarQuadroPosicaoFinanceira(final PerfilRisco perfilRiscoAtual) {
        this(perfilRiscoAtual, obterDataBasePreenchimentoInicial(perfilRiscoAtual));
    }

    public GerenciarQuadroPosicaoFinanceira(final PerfilRisco perfilRiscoAtual, final DataBaseES dataBaseES) {
        this.perfilRiscoAtual = perfilRiscoAtual;
        this.novoQuadroVO = 
                QuadroPosicaoFinanceiraMediator.get().preencherNovoQuadroNovoModelo(dataBaseES, perfilRiscoAtual);
    }

    private static DataBaseES obterDataBasePreenchimentoInicial(final PerfilRisco perfilRiscoAtual) {
        return QuadroPosicaoFinanceiraMediator.get().obterDataBaseESVerificandoExistenciaQuadroCadastrado(
                perfilRiscoAtual);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        Form<?> form = new Form<Object>("formulario");
        final Integer pkCiclo = perfilRiscoAtual.getCiclo().getPk();
        botaoConcluirNovoQuadro = new BotaoConcluirNovoQuadro();
        form.add(new PainelResumoCiclo("resumoCicloPanel", pkCiclo, perfilRiscoAtual)).add(new LinkVoltar())
                .add(botaoConcluirNovoQuadro).add(new LinkVoltarPerfilRisco("bttVoltarSemSalvar", pkCiclo));
        selecaoDataBase =
                new SelecaoDataBase("selecaoDataBase", DataBaseESMediator.get().consultarDatasBaseDisponiveis(
                        perfilRiscoAtual), new PropertyModel<DataBaseES>(novoQuadroVO,
                        PropertyUtils.property(PropertyUtils.getPropertyObject(QuadroPosicaoFinanceiraVO.class)
                                .getDataBaseES())));
        form.add(selecaoDataBase);

        painelQuadroPosicaoFinanceira =
                new PainelQuadroPosicaoFinanceira("quadroPosicaoFinanceira", novoQuadroVO, perfilRiscoAtual);
        painelQuadroPosicaoFinanceira.setOutputMarkupId(true);
        form.addOrReplace(painelQuadroPosicaoFinanceira);

        painelSelecaoContaAtivo = new PainelSelecaoContaAtivo("selecaoContaAtivo", novoQuadroVO);
        painelSelecaoContaPassivo = new PainelSelecaoContaPassivo("selecaoContaPassivo", novoQuadroVO);

        form.addOrReplace(painelSelecaoContaAtivo);
        form.addOrReplace(painelSelecaoContaPassivo);
        
        painelAjustePatrimonioNovo = 
                new PainelAjustePatrimonioNovo("ajustePatrimonioReferenciaNovo", novoQuadroVO, perfilRiscoAtual);
        form.addOrReplace(painelAjustePatrimonioNovo);
        
        painelAjusteResultadoNovo = new PainelAjusteResultadoNovo("ajusteResultadoNovo", novoQuadroVO);
        form.addOrReplace(painelAjusteResultadoNovo);
        
        painelAjusteIndicesNovo = new PainelAjusteIndicesNovo("ajusteIndicesNovo", novoQuadroVO, perfilRiscoAtual);
        form.addOrReplace(painelAjusteIndicesNovo);

        painelAnexo =
                new PainelAnexoQPF("painelAnexos", QuadroPosicaoFinanceiraMediator.get().buscarQuadroPorPk(
                        novoQuadroVO.getPk()), true);
        painelAnexo.setOutputMarkupId(true);

        form.addOrReplace(painelAnexo);
        add(form);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(DefaultPage.class,
                "jquery.maskMoney.js")));
    }

    private class SelecaoDataBase extends DropDownChoice<DataBaseES> {

        public SelecaoDataBase(String id, List<? extends DataBaseES> choices, IModel<DataBaseES> model) {
            super(id, model, choices, new ChoiceRenderer<DataBaseES>("dataBaseFormatada",
                    PropertyUtils.property(PropertyUtils.getPropertyObject(DataBaseES.class).getCodigoDataBase())));
            setMarkupId(id);
            setOutputMarkupPlaceholderTag(true);
        }

        @Override
        public void renderHead(IHeaderResponse response) {
            super.renderHead(response);
            response.render(CssHeaderItem.forReference(new PackageResourceReference(AjaxLinkIndicator.class,
                    "res/indicator.css")));
            response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(AjaxLinkIndicator.class,
                    "res/indicator.js")));
        }

        @Override
        protected void onConfigure() {
            super.onConfigure();
            add(new AjaxFormSubmitCheckSelecao(this));
        }

        @Override
        protected CharSequence getDefaultChoice(String selectedValue) {
            return null;
        }
    }

    private class AjaxFormSubmitCheckSelecao extends AjaxFormSubmitBehavior {
        private static final String MASK_HIDE = "Mask.hide('";
        private static final String FECHA_PARENTESES = "');";
        private SelecaoDataBase ddcDelecaoDataBase;

        public AjaxFormSubmitCheckSelecao(SelecaoDataBase ddcDelecaoDataBase) {
            super("onchange");
            this.ddcDelecaoDataBase = ddcDelecaoDataBase;
        }

        @Override
        protected void onSubmit(AjaxRequestTarget target) {
            QuadroPosicaoFinanceiraMediator.get().descartarAlteracoesNovoQuadroAtual(
                    ddcDelecaoDataBase.getModelObject(), perfilRiscoAtual);
           
            avancarParaNovaPagina(
                    new GerenciarQuadroPosicaoFinanceira(perfilRiscoAtual, ddcDelecaoDataBase.getModelObject()),
                    getPaginaAtual().getPaginaAnterior());
        }

        @Override
        protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
            super.updateAjaxAttributes(attributes);
            attributes.getAjaxCallListeners().add(new AjaxCallListener() {
                @Override
                public CharSequence getPrecondition(Component component) {
                    return "Mask.show('" + ddcDelecaoDataBase.getMarkupId() + FECHA_PARENTESES;
                }

                @Override
                public CharSequence getSuccessHandler(Component component) {
                    return MASK_HIDE + ddcDelecaoDataBase.getMarkupId() + FECHA_PARENTESES;
                }

                @Override
                public CharSequence getFailureHandler(Component component) {
                    return MASK_HIDE + ddcDelecaoDataBase.getMarkupId() + FECHA_PARENTESES;
                }
            });
        }
    }

    @Override
    protected void setPaginaAnterior(DefaultPage paginaAnterior) {
        super.setPaginaAnterior(paginaAnterior);
    }

    private class BotaoConcluirNovoQuadro extends SubmitLink {

        private static final String BTT_CONCLUIR = "bttConcluir";

        public BotaoConcluirNovoQuadro() {
            super(BTT_CONCLUIR);
        }

        @Override
        public void onSubmit() {
            QuadroPosicaoFinanceiraMediator.get().salvarConclusao(novoQuadroVO, perfilRiscoAtual);
            DefaultPage page = new PerfilDeRiscoPage(perfilRiscoAtual.getCiclo().getPk());
            page.info("Novo quadro concluído e perfil de risco atualizado com sucesso.");
            avancarParaNovaPagina(page, getPaginaAtual().getPaginaAnterior().getPaginaAnterior());
        }

        @Override
        protected void onConfigure() {
            super.onConfigure();
            setMarkupId(BTT_CONCLUIR);
            setOutputMarkupId(true);
            setOutputMarkupPlaceholderTag(true);
        }

        @Override
        public boolean isEnabled() {
            return !novoQuadroVO.isAjustadoAtivoDiferentePassivo()
                    && CollectionUtils.isNotEmpty(novoQuadroVO.getListaContasQuadro());
        }

    }

    @Override
    public String getTitulo() {
        return "Gestão do Quadro da Posição Financeira";
    }

    @Override
    public String getCodigoTela() {
        return "APSFW0210";
    }

    public SelecaoDataBase getSelecaoDataBase() {
        return selecaoDataBase;
    }

    public PainelSelecaoContaAtivo getPainelSelecaoContaAtivo() {
        return painelSelecaoContaAtivo;
    }

    public PainelSelecaoContaPassivo getPainelSelecaoContaPassivo() {
        return painelSelecaoContaPassivo;
    }

    public PainelQuadroPosicaoFinanceira getPainelQuadroPosicaoFinanceira() {
        return painelQuadroPosicaoFinanceira;
    }

    public PainelAjustePatrimonioReferencia getPainelAjustePatrimonioReferencia() {
        return painelAjustePatrimonioReferencia;
    }

    public PainelAjusteIndices getPainelAjusteIndices() {
        return painelAjusteIndices;
    }

    public PainelAjusteResultado getPainelAjusteResultado() {
        return painelAjusteResultado;
    }

    public void lancarInfoAjax(String msgInfo, WebMarkupContainer conteiner, AjaxRequestTarget target) {
        this.getFeedbackMessages().clear();
        target.add(this.getFeedBack());
        target.add(conteiner);
        target.add(scriptErro);
        this.info(msgInfo);
        target.focusComponent(this.getFeedBack());
    }

    public BotaoConcluirNovoQuadro getBotaoConcluirNovoQuadro() {
        return botaoConcluirNovoQuadro;
    }

    public Boolean getAjusteRealizado() {
        return QuadroPosicaoFinanceiraMediator.get().isAjusteRealizado();
    }

    public PainelAnexoQPF getPainelAnexo() {
        return painelAnexo;
    }

    public void setPainelAnexo(PainelAnexoQPF painelAnexo) {
        this.painelAnexo = painelAnexo;
    }

    public PainelAjustePatrimonioNovo getPainelAjustePatrimonioNovo() {
        return painelAjustePatrimonioNovo;
    }

    public PainelAjusteIndicesNovo getPainelAjusteIndicesNovo() {
        return painelAjusteIndicesNovo;
    }

    public PainelAjusteResultadoNovo getPainelAjusteResultadoNovo() {
        return painelAjusteResultadoNovo;
    }
    
}
