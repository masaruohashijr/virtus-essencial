package br.gov.bcb.sisaps.web.page.dominio.regraPerfilAcesso;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

import br.gov.bcb.sisaps.src.mediator.RegraPerfilAcessoMediator;
import br.gov.bcb.sisaps.src.vo.RegraPerfilAcessoVO;
import br.gov.bcb.sisaps.web.page.componentes.botoes.AjaxSubmitLinkSisAps;
import br.gov.bcb.sisaps.web.page.componentes.modal.PainelModalWindow;

public class PainelControleExcluirRegraPerfilAcesso extends PainelModalWindow {

    private IModel<RegraPerfilAcessoVO> model;
    private ModalWindow modalWindow;
    private PainelGestaoPerfilDeRisco painel;

    public PainelControleExcluirRegraPerfilAcesso(String contentId, ModalWindow modalWindow,
            IModel<RegraPerfilAcessoVO> model, PainelGestaoPerfilDeRisco painel) {
        super(contentId);
        this.model = model;
        this.modalWindow = modalWindow;
        this.painel = painel;

        Form<?> form = new Form<Object>("form");
        form.add(new LinkFechar());
        form.add(new LinkConfirmar("bttConfirmar"));
        add(form);

    }

    protected class LinkFechar extends AjaxLink<Void> {
        public LinkFechar() {
            super("bttVoltar");
        }

        @Override
        public void onClick(AjaxRequestTarget target) {
            modalWindow.close(target);
        }
    }

    protected class LinkConfirmar extends AjaxSubmitLinkSisAps {

        public LinkConfirmar(String id) {
            super(id);
        }

        @Override
        public void executeSubmit(AjaxRequestTarget target) {
            RegraPerfilAcessoMediator.get().excluir(
                    RegraPerfilAcessoMediator.get().buscarPorPk(model.getObject().getPk()));
            modalWindow.close(target);
            target.add(painel);
            getPage().success("Regra de perfil de acesso excluída com sucesso.");

        }
    }

}