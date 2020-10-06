package br.gov.bcb.sisaps.web.page.dominio.matriz;

import java.math.BigDecimal;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;

import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.mediator.MatrizCicloMediator;
import br.gov.bcb.sisaps.web.page.componentes.botoes.AjaxSubmitLinkSisAps;
import br.gov.bcb.sisaps.web.page.componentes.modal.AjaxSubmitLinkModalWindow;
import br.gov.bcb.sisaps.web.page.componentes.modal.PainelModalWindow;

public class PainelExclusaoGov extends PainelModalWindow {

    private final Form<?> form = new Form<Object>("form");
    private final BigDecimal numeroFatorRelevanciaAE;
    private final Matriz matriz;
    private LinkIncluir linkSim;

    public PainelExclusaoGov(ModalWindow modalWindow, final Matriz matriz, final BigDecimal numeroFatorRelevanciaAE) {
        super(modalWindow.getContentId());
        this.modalEdicao = modalWindow;
        this.numeroFatorRelevanciaAE = numeroFatorRelevanciaAE;
        this.matriz = matriz;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        form.addOrReplace(new Label("msg",
                "Atenção. Essa operação exclui todo o conteúdo do ARC de Governança. Deseja continuar?"));
        addBotaoSim();
        form.addOrReplace(new LinkNao());
        add(form);
    }

    protected class LinkIncluir extends AjaxSubmitLinkModalWindow {
        public LinkIncluir(String id) {
            super(id);
        }

        @Override
        public void executeSubmit(AjaxRequestTarget target) {
            String msg = MatrizCicloMediator.get().alterarPercentualAE(matriz, numeroFatorRelevanciaAE);
            modalEdicao.close(target);
            EdicaoMatrizPage page = (EdicaoMatrizPage) getPage();
            page.atualizarPaineis(target, msg, true);
        }
    }

    private void addBotaoSim() {
        linkSim = new LinkIncluir("bttSim");
        linkSim.setOutputMarkupId(true);
        linkSim.setMarkupId(linkSim.getId());
        form.addOrReplace(linkSim);
    }

    protected class LinkNao extends AjaxSubmitLinkSisAps {
        public LinkNao() {
            super("bttNao", true);
        }

        @Override
        public void executeSubmit(AjaxRequestTarget target) {
            modalEdicao.close(target);
        }
    }

}