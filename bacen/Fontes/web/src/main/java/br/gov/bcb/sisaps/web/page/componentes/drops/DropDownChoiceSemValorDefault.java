package br.gov.bcb.sisaps.web.page.componentes.drops;

import java.util.List;

import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

/**
 * Um drop down choice cujo valor default é o primeiro valor da lista.
 * 
 */
public class DropDownChoiceSemValorDefault<T> extends DropDownChoice<T> {

    private static final long serialVersionUID = 1L;

    public DropDownChoiceSemValorDefault(String id, IModel<T> model, List<T> valores, ChoiceRenderer<T> renderer) {
        super(id, model, valores, renderer);
    }

    public DropDownChoiceSemValorDefault(String id, IModel<T> model, IModel<List<T>> choices, ChoiceRenderer<T> renderer) {
        super(id, model, choices, renderer);
    }

    public DropDownChoiceSemValorDefault(final String id, final PropertyModel<T> propertyModel,
            final List<? extends T> data) {
        super(id, propertyModel, data);
    }

    // Elimina o valor default
    @Override
    protected CharSequence getDefaultChoice(String selected) {
        return null;
    }
}
