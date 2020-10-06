package br.gov.bcb.sisaps.web.page.dominio.analisequantitativaaqt;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.web.page.PainelSisAps;

public class PainelConsultaFiltroPerfilDeRiscoAnef extends PainelSisAps {

    private final IModel<AnaliseQuantitativaAQT> modelAnef = new Model<AnaliseQuantitativaAQT>();
    private List<AnaliseQuantitativaAQT> listaAnef;
    private final AnaliseQuantitativaAQT aqt;
    private final Ciclo ciclo;

    public PainelConsultaFiltroPerfilDeRiscoAnef(String id, AnaliseQuantitativaAQT aqt) {
        super(id);
        this.aqt = aqt;
        this.ciclo = aqt.getCiclo();
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        listaAnef = AnaliseQuantitativaAQTMediator.get().listarANEFConsulta(aqt.getParametroAQT(), ciclo);
        addComboPerfisRisco();
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        setVisibilityAllowed(CollectionUtils.isNotEmpty(listaAnef));
    }

    private void addComboPerfisRisco() {
        DropDownChoice<AnaliseQuantitativaAQT> selectAnef =
                new DropDownChoice<AnaliseQuantitativaAQT>("idAnef", modelAnef, listaAnef,
                        new AnaliseQuantitativaAQTChoiceRenderer());
        selectAnef.add(new OnChangeAjaxBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                atualizarPagina(target);
            }
        });
        setValorDefaultAnaliseQuantitativaAQT();
        selectAnef.setNullValid(false);
        addOrReplace(selectAnef);
    }

    private void setValorDefaultAnaliseQuantitativaAQT() {
        modelAnef.setObject(aqt != null ? aqt : listaAnef.get(0));
    }

    private void atualizarPagina(AjaxRequestTarget target) {
        ((ConsultaAQT) getPage()).atualizarPagina(target);
    }

    private class AnaliseQuantitativaAQTChoiceRenderer extends ChoiceRenderer<AnaliseQuantitativaAQT> {
        @Override
        public Object getDisplayValue(AnaliseQuantitativaAQT anef) {
			return anef.getDataVersaoConsulta(PerfilRiscoMediator.get()
					.obterPerfilRiscoAtual(anef.getCiclo().getPk()));
        }

        @Override
        public String getIdValue(AnaliseQuantitativaAQT anef, int index) {
            return anef.getPk().toString();
        }
    }

    public AnaliseQuantitativaAQT getAnaliseQuantitativaAQTSelecionado() {
        return modelAnef.getObject();
    }
    
}
