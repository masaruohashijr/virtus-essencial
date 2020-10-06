package br.gov.bcb.sisaps.web.page.dominio.regraPerfilAcesso;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import br.gov.bcb.sisaps.src.vo.RegraPerfilAcessoVO;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.web.page.componentes.util.ConstantesImagens;

public class AcoesPanel extends Panel {
    private PainelGestaoPerfilDeRisco painel;
    private IModel<RegraPerfilAcessoVO> model;
    private ModalWindow modalEdicao;

    public AcoesPanel(String id, final IModel<RegraPerfilAcessoVO> model, PainelGestaoPerfilDeRisco painel) {
        super(id, model);
        this.model = model;
        this.painel = painel;

    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        colunaExcluir(model);
        adicionarModal();
    }

    private void colunaExcluir(final IModel<RegraPerfilAcessoVO> model) {

        AjaxSubmitLink link = new AjaxSubmitLink("linkExcluir") {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                modalEdicao.setContent(new PainelControleExcluirRegraPerfilAcesso(modalEdicao.getContentId(),
                        modalEdicao, model, painel));
                modalEdicao.show(target);
            }

        };
        link.add(new Image("excluir", ConstantesImagens.IMG_EXCLUIR));
        String nomeUnico = "Regra" + model.getObject().getPk().toString();
        link.setMarkupId("linkExcluir_" + SisapsUtil.criarMarkupId(nomeUnico));
        add(link);
    }

    private void adicionarModal() {
        modalEdicao = new ModalWindow("modalEdicao");
        modalEdicao.setResizable(false);
        modalEdicao.setAutoSize(true);
        add(modalEdicao);

    }

}