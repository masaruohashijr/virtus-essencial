package br.gov.bcb.sisaps.web.page.dominio.gestaoAgenda;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import br.gov.bcb.sisaps.src.dominio.AgendaCorec;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoEmailCorecEnum;
import br.gov.bcb.sisaps.src.mediator.AgendaCorecMediator;
import br.gov.bcb.sisaps.web.page.PainelSisAps;

@SuppressWarnings("serial")
public class PainelDatasEmail extends PainelSisAps {
    private static final String STYLE = "style";
    private static final String ESPACO = " ";
    private AgendaCorec agenda;
    private boolean isAlteracao;

    public PainelDatasEmail(String id, AgendaCorec agenda, boolean isAlteracao) {
        super(id);
        this.agenda = agenda;
        this.isAlteracao = isAlteracao;

    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        Label labelApresentacao =
                new Label("idDataApresentacao", AgendaCorecMediator.get().getDataEmailApresentacao(agenda));

        Label labelDisponibilidade =
                new Label("idDataDisponibilidade", AgendaCorecMediator.get().getDataEmailDisponibilidade(agenda));

        if (AgendaCorecMediator.get().dataVencida(agenda.getCiclo(), agenda.getDataEnvioApresentacao(),
                TipoEmailCorecEnum.APRESENTACAO)) {
            labelApresentacao.add(new AttributeAppender(STYLE, obterCor(), ESPACO));
        }

        if (AgendaCorecMediator.get().dataVencida(agenda.getCiclo(), agenda.getDataEnvioDisponibilidade(),
                TipoEmailCorecEnum.DISPONIBILIDADE)) {
            labelDisponibilidade.add(new AttributeAppender(STYLE, obterCor(), ESPACO));
        }

        addOrReplace(labelApresentacao);
        addOrReplace(labelDisponibilidade);
    }

    private IModel<String> obterCor() {
        IModel<String> model = new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                return "color: #FF0000;";
            }
        };
        return model;
    }

    public void atualizarPainel(AjaxRequestTarget target) {
        target.add(PainelDatasEmail.this);
    }

}
