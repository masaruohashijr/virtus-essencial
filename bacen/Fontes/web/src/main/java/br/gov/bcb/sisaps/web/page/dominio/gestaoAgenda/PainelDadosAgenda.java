package br.gov.bcb.sisaps.web.page.dominio.gestaoAgenda;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import br.gov.bcb.sisaps.src.dominio.AgendaCorec;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.mascara.MascaraDeHoraBehaviour;

@SuppressWarnings("serial")
public class PainelDadosAgenda extends PainelSisAps {
    private AgendaCorec agenda;
    private IModel<String> modelHorario = new Model<String>();
    private boolean isAlteracao;

    public PainelDadosAgenda(String id, AgendaCorec agenda, boolean isAlteracao) {
        super(id);
        this.agenda = agenda;
        this.modelHorario.setObject(agenda.getHoraCorecFormatada());
        this.isAlteracao = isAlteracao;

    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        TextField<String> local = new TextField<String>("idLocal", new PropertyModel<String>(agenda, "local"));
        local.setVisible(isAlteracao);
        addOrReplace(local);

        Label localLabel = new Label("idLocalLabel", agenda.getLocal());
        localLabel.setVisible(!isAlteracao);
        addOrReplace(localLabel);

        TextField<String> horario = new TextField<String>("idHorario", modelHorario);
        horario.setVisible(isAlteracao);
        horario.add(new MascaraDeHoraBehaviour());
        addOrReplace(horario);

        Label horarioLabel = new Label("idHorarioLabel", agenda.getHoraCorecFormatada());
        horarioLabel.setVisible(!isAlteracao);
        addOrReplace(horarioLabel);

        addOrReplace(new Label("idUltimaAlteracao", agenda.getNomeOperadorDataHora()));
    }

    public void atualizarPainel(AjaxRequestTarget target) {
        target.add(PainelDadosAgenda.this);
    }

    public IModel<String> getModelHorario() {
        return modelHorario;
    }

    public void setModelHorario(IModel<String> modelHorario) {
        this.modelHorario = modelHorario;
    }

}
