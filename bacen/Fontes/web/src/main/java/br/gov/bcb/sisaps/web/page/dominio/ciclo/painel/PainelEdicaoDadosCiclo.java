package br.gov.bcb.sisaps.web.page.dominio.ciclo.painel;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.StringValidator;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.EntidadeSupervisionavel;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.EntidadeSupervisionavelMediator;
import br.gov.bcb.sisaps.util.Util;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.calendar.CalendarTextField;
import br.gov.bcb.sisaps.web.page.componentes.drops.CustomDropDownChoice;
import br.gov.bcb.sisaps.web.page.componentes.infraestrutura.ExceptionUtils;
import br.gov.bcb.sisaps.web.page.componentes.mascara.MascaraNumericaBehaviour;

@SuppressWarnings("serial")
public class PainelEdicaoDadosCiclo extends PainelSisAps {
    private static final String PATTERN = "dd/MM/yyyy";
    private static final String ONCHANGE = "onchange";
    private static final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern(PATTERN);
    private final Ciclo ciclo;

    private PainelResponsavelCicloGrupo painelEquipe;
    private Label metodologia;

    public PainelEdicaoDadosCiclo(String id, Ciclo ciclo) {
        super(id);
        this.ciclo = ciclo;
        addComponentes();

    }

    private void addComponentes() {
        TextField<String> numeroPTPE =
                new TextField<String>("numeroPT", new PropertyModel<String>(ciclo, "codigoPTPE"));
        numeroPTPE.setOutputMarkupId(true);
        numeroPTPE.add(new MascaraNumericaBehaviour("############"));
        numeroPTPE.add(StringValidator.maximumLength(12));
        numeroPTPE.add(new AjaxFormComponentUpdatingBehavior(ONCHANGE) {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                //Não precisa implementar
            }
        });
        addOrReplace(numeroPTPE);

        metodologia = new Label("metodologia", new PropertyModel<String>(ciclo, "metodologia.titulo"));
        metodologia.setOutputMarkupId(true);

        addOrReplace(metodologia);

        addDatas();

        addEntidadeSupervisonavel();

        addPainel();

    }

    private void addDatas() {
        IModel<String> modelInicio = new IModel<String>() {

            @Override
            public void detach() {
                // TODO não precisa implementar
            }

            @Override
            public String getObject() {
                return ciclo.getDataInicioFormatada();
            }

            @Override
            public void setObject(String object) {
                atualizarDataInicio(object);
            }

        };
        IModel<String> modelFinal = new IModel<String>() {

            @Override
            public void detach() {
                // TODO não precisa implementar
            }

            @Override
            public String getObject() {
                return ciclo.getDataPrevisaoFormatada();
            }

            @Override
            public void setObject(String object) {
                atualizarDataCorec(object);
            }

        };

        CalendarTextField<String> txtDataInicio = new CalendarTextField<String>("idDataInicio", modelInicio);
        addOrReplace(txtDataInicio);

        CalendarTextField<String> txtDataPrevCorec = new CalendarTextField<String>("idDataDataPrevCorec", modelFinal);
        addOrReplace(txtDataPrevCorec);
    }

    private void atualizarDataCorec(String object) {
        try {
            ciclo.setDataPrevisaoCorec(Util.isNuloOuVazio(object) ? null : FORMATTER.parseLocalDate(object).toDate());
        } catch (IllegalArgumentException e) {
            ExceptionUtils.tratarIllegalArgumentExceptio(e, getPage(), "Data prevista do corec");
        }
    }

    private void atualizarDataInicio(String object) {
        try {
            ciclo.setDataInicio(Util.isNuloOuVazio(object) ? null : FORMATTER.parseLocalDate(object).toDate());
        } catch (IllegalArgumentException e) {
            ExceptionUtils.tratarIllegalArgumentExceptio(e, getPage(), "Data de início do ciclo");
        }
    }

    private void addPainel() {
        String matriculaSupervisor =
                ciclo.getEntidadeSupervisionavel() == null ? null : CicloMediator.get()
                        .buscarChefeAtual(ciclo.getEntidadeSupervisionavel().getLocalizacao()).getMatricula();
        painelEquipe = new PainelResponsavelCicloGrupo("idPainelEquipe", matriculaSupervisor, true);
        painelEquipe.setOutputMarkupId(true);
        addOrReplace(painelEquipe);
    }

    private void addEntidadeSupervisonavel() {
        ChoiceRenderer<EntidadeSupervisionavel> renderer =
                new ChoiceRenderer<EntidadeSupervisionavel>("nome", EntidadeSupervisionavel.PROP_ID);
        List<EntidadeSupervisionavel> listaChoices = EntidadeSupervisionavelMediator.get().buscarPertenceSrc();
        PropertyModel<EntidadeSupervisionavel> propertyModel =
                new PropertyModel<EntidadeSupervisionavel>(ciclo, "entidadeSupervisionavel");
        DropDownChoice<EntidadeSupervisionavel> selectTipoDocumento =
                new CustomDropDownChoice<EntidadeSupervisionavel>("idEntidadeSupervisionavel", "Selecione",
                        propertyModel, listaChoices, renderer);
        selectTipoDocumento.setDefaultModelObject(null);
        selectTipoDocumento.add(new AjaxFormComponentUpdatingBehavior(ONCHANGE) {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                ciclo.setMetodologia(ciclo.getEntidadeSupervisionavel() == null ? null : ciclo
                        .getEntidadeSupervisionavel().getMetodologia());
                addPainel();
                target.add(metodologia);
                target.add(painelEquipe);
            }
        });

        addOrReplace(selectTipoDocumento);
    }

}
