package br.gov.bcb.sisaps.web.page.dominio.gestaoAgenda;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import br.gov.bcb.sisaps.src.dominio.AgendaCorec;
import br.gov.bcb.sisaps.src.dominio.ObservacaoAgendaCorec;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Tabela;
import br.gov.bcb.sisaps.web.page.componentes.util.ConstantesImagens;

public class PainelAcaoObservacao extends Panel {

    public PainelAcaoObservacao(String id, final AgendaCorec agenda, final IModel<ObservacaoAgendaCorec> model,
            final Tabela<ObservacaoAgendaCorec> tabela, final List<ObservacaoAgendaCorec> listaExcluidos) {
        super(id, model);

        AjaxSubmitLink linkExcluir = new AjaxSubmitLink("linkExcluir") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {

                agenda.getObservacoes().remove(model.getObject());
                if (model.getObject().getPk() != null) {
                    listaExcluidos.add(model.getObject());
                }
                target.add(tabela);

            }
        };
        linkExcluir.add(new Image("excluir", ConstantesImagens.IMG_EXCLUIR));
        linkExcluir.setOutputMarkupId(true);
        add(linkExcluir);
    }

}