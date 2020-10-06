package br.gov.bcb.sisaps.web.page.dominio.ciclo.painel;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoLinhaAtividadeVOEnum;
import br.gov.bcb.sisaps.src.mediator.LinhaAtividadeVOMediator;
import br.gov.bcb.sisaps.src.vo.LinhaAtividadeVO;
import br.gov.bcb.sisaps.web.page.componentes.modal.AjaxSubmitLinkModalWindow;
import br.gov.bcb.sisaps.web.page.componentes.modal.PainelModalWindow;
import br.gov.bcb.sisaps.web.page.componentes.util.ConstantesImagens;
import br.gov.bcb.sisaps.web.page.dominio.matriz.EdicaoMatrizPage;

public class PainelControleExcluirLinha extends PainelModalWindow {

    private static final String ESPACO = " ";
    private static final String CLASS = "class";
    @SpringBean
    private LinhaAtividadeVOMediator linhaAtividadeMediator;

    private IModel<LinhaAtividadeVO> model;
    private ModalWindow modalWindow;
    private List<LinhaAtividadeVO> listaExcluidos = new ArrayList<LinhaAtividadeVO>();

    public PainelControleExcluirLinha(ModalWindow modalWindow, IModel<LinhaAtividadeVO> model) {
        super(modalWindow.getContentId());
        this.model = model;
        this.modalWindow = modalWindow;

    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        Form<?> form = new Form<Object>("form");
        listaExcluidos.add(model.getObject());
        if (model.getObject().getFilhos() != null && !model.getObject().getFilhos().isEmpty()) {
            listaExcluidos.addAll(model.getObject().getFilhos());
            if (TipoLinhaAtividadeVOEnum.UNIDADE.equals(model.getObject().getTipo())) {
                incluirFilhosUnidade();
            }
        }
        addAdvertencias(form);
        form.addOrReplace(new LinkFechar());
        form.addOrReplace(new LinkConfirmar("bttConfirmarExclusao"));
        form.addOrReplace(addListviewLinhasAtividades());
        addOrReplace(form);
    }

    private void addAdvertencias(Form<?> form) {
        WebMarkupContainer advertenciaExcluirUnidade = new WebMarkupContainer("advertenciaExcluirUnidade") {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(TipoLinhaAtividadeVOEnum.UNIDADE.equals(model.getObject().getTipo()));
            }
        };
        advertenciaExcluirUnidade.addOrReplace(
                new Image("imgAdvertenciaExcluirUnidade", ConstantesImagens.IMG_ADVERTENCIA));
        form.addOrReplace(advertenciaExcluirUnidade);
        WebMarkupContainer advertenciaExcluirAtividade = new WebMarkupContainer("advertenciaExcluirAtividade") {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(TipoLinhaAtividadeVOEnum.ATIVIDADE.equals(model.getObject().getTipo()));
            }
        };
        advertenciaExcluirAtividade.addOrReplace(
                new Image("imgAdvertenciaExcluirAtividade", ConstantesImagens.IMG_ADVERTENCIA));
        form.addOrReplace(advertenciaExcluirAtividade);
    }

    private void incluirFilhosUnidade() {
        for (LinhaAtividadeVO linha : model.getObject().getFilhos()) {
            if (linha.getFilhos() != null && !linha.getFilhos().isEmpty()) {
                listaExcluidos.addAll(linha.getFilhos());
            }
        }
    }

    protected class LinkFechar extends AjaxLink<Void> {
        public LinkFechar() {
            super("bttVoltarExclusao");
        }

        @Override
        public void onClick(AjaxRequestTarget target) {
            modalWindow.close(target);
        }
    }

    private ListView<LinhaAtividadeVO> addListviewLinhasAtividades() {

        ListView<LinhaAtividadeVO> listviewAtividades =
                new ListView<LinhaAtividadeVO>("listaDeAtividades", listaExcluidos) {

                    @Override
                    protected void populateItem(ListItem<LinhaAtividadeVO> item) {
                        item.add(new Label("nome", item.getModelObject().nomeFormatadoVigente()).setEscapeModelStrings(
                                false).add(new AttributeAppender(CLASS, obterClassCelula(item.getModel()), ESPACO)));

                        item.add(new Label("tipo", item.getModelObject().getNomeParametroTipoAtividade()));
                        item.add(new Label("peso", item.getModelObject().getParametroPeso().getDescricao())
                                .add(new AttributeAppender(CLASS, obterCorPeso(item.getModelObject().getCorCelula()),
                                        ESPACO)));

                    }
                };
        return listviewAtividades;

    }

    private IModel<String> obterCorPeso(final String cor) {
        IModel<String> model = new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                return cor;
            }
        };
        return model;
    }

    private IModel<?> obterClassCelula(final IModel<LinhaAtividadeVO> linha) {

        IModel<String> model = new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {

                if (linha.getObject().isFilho()) {
                    if (TipoLinhaAtividadeVOEnum.ARC.equals(linha.getObject().getTipo())) {
                        return "nomeArc";
                    }
                    return "nomeAtividade";
                } else {
                    return "nomeUnidade";
                }

            }
        };
        return model;

    }

    protected class LinkConfirmar extends AjaxSubmitLinkModalWindow {

        public LinkConfirmar(String id) {
            super(id);
        }

        @Override
        public void executeSubmit(AjaxRequestTarget target) {
            linhaAtividadeMediator.excluirLinhaAtividade(model.getObject());
            modalWindow.close(target);
            atualizar(target, model.getObject().getNome() + " excluido com sucesso");

        }
    }

    private void atualizar(AjaxRequestTarget target, String msg) {
        ((EdicaoMatrizPage) getPage()).atualizarPaineis(target, msg, true);
    }

}