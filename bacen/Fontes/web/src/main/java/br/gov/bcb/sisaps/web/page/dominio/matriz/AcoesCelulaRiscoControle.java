package br.gov.bcb.sisaps.web.page.dominio.matriz;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import br.gov.bcb.sisaps.src.dominio.Atividade;
import br.gov.bcb.sisaps.src.dominio.CelulaRiscoControle;
import br.gov.bcb.sisaps.src.mediator.CelulaRiscoControleMediator;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Tabela;
import br.gov.bcb.sisaps.web.page.componentes.util.ConstantesImagens;

public class AcoesCelulaRiscoControle extends Panel {

    private ModalWindow modalConfimarExclusao;

    public AcoesCelulaRiscoControle(String id, final Atividade atividade, final IModel<CelulaRiscoControle> model,
            final Tabela<CelulaRiscoControle> tabela, final List<CelulaRiscoControle> listaExcluidos,
            String sufixoMarkupId, final boolean isInclusao) {
        super(id, model);
        modalConfimarExclusao = new ModalWindow("modalExcluir");
        modalConfimarExclusao.setResizable(false);
        modalConfimarExclusao.setOutputMarkupId(true);
        modalConfimarExclusao.setMarkupId("modalExcluirARC_"
                + SisapsUtil.criarMarkupId(model.getObject().getParametroGrupoRiscoControle().getNomeAbreviado())
                + sufixoMarkupId);
        modalConfimarExclusao.setAutoSize(false);
        modalConfimarExclusao.setInitialHeight(100);
        modalConfimarExclusao.setInitialWidth(500);

        AjaxSubmitLink linkExcluir = new AjaxSubmitLink("linkExcluir") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                if (isInclusao) {
                    CelulaRiscoControle celula = model.getObject();
                    atividade.getCelulasRiscoControle().remove(celula);
                    listaExcluidos.add(celula);
                    target.add(tabela);
                } else {
                    modalConfimarExclusao.setContent(new PainelConfirmarExcluirArc(modalConfimarExclusao,
                            CelulaRiscoControleMediator.get().msgAoExcluirCelula(), atividade, model, tabela,
                            listaExcluidos));
                    modalConfimarExclusao.setOutputMarkupId(true);
                    modalConfimarExclusao.show(target);
                }

            }
        };
        linkExcluir.add(new Image("excluir", ConstantesImagens.IMG_EXCLUIR));
        linkExcluir.setOutputMarkupId(true);
        linkExcluir.setMarkupId("linkExcluirARC_"
                + SisapsUtil.criarMarkupId(model.getObject().getParametroGrupoRiscoControle().getNomeAbreviado())
                + sufixoMarkupId);
        add(linkExcluir);
        add(modalConfimarExclusao);
    }

}