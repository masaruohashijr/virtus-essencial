package br.gov.bcb.sisaps.web.page.dominio.unidade;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;

import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.ParametroPeso;
import br.gov.bcb.sisaps.src.dominio.Unidade;
import br.gov.bcb.sisaps.src.mediator.MatrizCicloMediator;
import br.gov.bcb.sisaps.src.mediator.MetodologiaMediator;
import br.gov.bcb.sisaps.src.mediator.UnidadeMediator;
import br.gov.bcb.sisaps.web.page.componentes.drops.CustomDropDownChoice;
import br.gov.bcb.sisaps.web.page.componentes.modal.AjaxSubmitLinkModalWindow;
import br.gov.bcb.sisaps.web.page.componentes.modal.PainelModalWindow;
import br.gov.bcb.sisaps.web.page.dominio.matriz.EdicaoMatrizPage;

public class PainelGerenciarUnidadeMatriz extends PainelModalWindow {
    private static final long serialVersionUID = 1L;
    private Matriz matriz;
    private Unidade unidade;
    private boolean isInclusao;
    private Form<?> form = new Form<Object>("form");
    @SpringBean
    private MetodologiaMediator metodologiaMediator;
    @SpringBean
    private UnidadeMediator unidadeMediator;

    public PainelGerenciarUnidadeMatriz(ModalWindow modalWindow, Matriz matriz, Unidade unidade, boolean isInclusao) {
        super(modalWindow.getContentId());
        if (unidade == null) {
            this.unidade = new Unidade();
        } else {
            this.unidade = unidade;
        }

        this.isInclusao = isInclusao;
        this.matriz = matriz;
        this.modalEdicao = modalWindow;

    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        if (isInclusao) {
            unidade.setMatriz(matriz);
        }
        addTextAreaNome();
        addSelectPeso();
        form.addOrReplace(new LinkAdicionarAlterar("bttIncluirUnidade", isInclusao).setVisible(isInclusao));
        form.addOrReplace(new LinkAdicionarAlterar("bttConfirmarAlteracao", isInclusao).setVisible(!isInclusao));

        addBotaoFechar();
        add(form);
    }

    private void addTextAreaNome() {
        TextField<String> nomeUnidade =
                new TextField<String>("idNomeUnidade", new PropertyModel<String>(unidade, "nome"));
        nomeUnidade.add(StringValidator.maximumLength(Unidade.TAMANHO_NOME));
        form.addOrReplace(nomeUnidade);
    }

    private void addSelectPeso() {
        ChoiceRenderer<ParametroPeso> renderer = new ChoiceRenderer<ParametroPeso>("descricao", ParametroPeso.PROP_ID);
        List<ParametroPeso> listaChoices =
                metodologiaMediator.buscarListaParametroPesoMetodologia(matriz.getCiclo().getMetodologia());
        PropertyModel<ParametroPeso> propertyModel = new PropertyModel<ParametroPeso>(unidade, "parametroPeso");

        DropDownChoice<ParametroPeso> selectParametroPeso =
                new CustomDropDownChoice<ParametroPeso>("idPeso", "Selecionar", propertyModel, listaChoices, renderer);
        if (isInclusao && unidade.getPk() == null) {
            ParametroPeso maiorPeso =
                    metodologiaMediator.buscarMaiorPesoMetodologia(matriz.getCiclo().getMetodologia());
            selectParametroPeso.setDefaultModelObject(maiorPeso);
        }
        form.addOrReplace(selectParametroPeso);
    }

    protected class LinkAdicionarAlterar extends AjaxSubmitLinkModalWindow {
        private static final long serialVersionUID = 1L;
        private final boolean isInclusao;

        public LinkAdicionarAlterar(String id, boolean isInclusao) {
            super(id);
            this.isInclusao = isInclusao;
        }

        @Override
        public void executeSubmit(AjaxRequestTarget target) {
            if (isInclusao) {
                unidadeMediator.incluirUnidadeNegocio(unidade);
            } else {
                unidadeMediator.alterarUnidade(unidade, unidade.getTipoUnidade());
            }
            MatrizCicloMediator.get().alterarVersaoMatriz(unidade.getMatriz());
            modalEdicao.close(target);
            atualizar(target);

        }
    }

    private void atualizar(AjaxRequestTarget target) {
        ((EdicaoMatrizPage) getPage()).atualizarPaineis(target, "Unidade " + unidade.getNome()
                + (isInclusao ? " incluída com sucesso" : " alterada com sucesso"), true);
    }

    private void addBotaoFechar() {
        LinkFechar linkFechar = new LinkFechar();
        linkFechar.setOutputMarkupId(true);
        linkFechar.setMarkupId(linkFechar.getId());
        form.addOrReplace(linkFechar);
    }

    protected class LinkFechar extends AjaxLink<Void> {
        public LinkFechar() {
            super("bttVoltarModal");
        }

        @Override
        public void onClick(AjaxRequestTarget target) {
            modalEdicao.close(target);
        }

    }

}