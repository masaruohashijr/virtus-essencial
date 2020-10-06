package br.gov.bcb.sisaps.web.page.dominio.matriz;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

import br.gov.bcb.sisaps.src.dominio.Atividade;
import br.gov.bcb.sisaps.src.dominio.CelulaRiscoControle;
import br.gov.bcb.sisaps.web.page.componentes.modal.PainelModalWindow;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Tabela;

public class PainelConfirmarExcluirArc extends PainelModalWindow {

    private Form<?> form = new Form<Object>("form");
    private String msg;

    public PainelConfirmarExcluirArc(ModalWindow modalWindow, String msg, final Atividade atividade,
            final IModel<CelulaRiscoControle> model, final Tabela<CelulaRiscoControle> tabela,
            final List<CelulaRiscoControle> listaExcluidos) {
        super(modalWindow.getContentId());
        this.msg = msg;
        this.modalEdicao = modalWindow;

        AjaxSubmitLink linkSim = new AjaxSubmitLink("bttSimModal") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                CelulaRiscoControle celula = model.getObject();
                atividade.getCelulasRiscoControle().remove(celula);
                listaExcluidos.add(celula);
                target.add(tabela);
                modalEdicao.close(target);
            }
        };
        form.addOrReplace(linkSim);

    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        form.addOrReplace(new Label("msg", msg));
        addBotaoNao();
        add(form);
    }

    private void addBotaoNao() {
        LinkNao linkNao = new LinkNao();
        linkNao.setOutputMarkupId(true);
        linkNao.setMarkupId(linkNao.getId());
        form.addOrReplace(linkNao);
    }

    protected class LinkNao extends AjaxLink<Void> {
        public LinkNao() {
            super("btNaoModal");
        }

        @Override
        public void onClick(AjaxRequestTarget target) {
            modalEdicao.close(target);
        }

    }

}