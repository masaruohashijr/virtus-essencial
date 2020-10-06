/*
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software � confidencial e propriedade do Banco Central do Brasil.
 * N�o � permitida sua distribui��o ou divulga��o do seu conte�do sem
 * expressa autoriza��o do Banco Central.
 * Este arquivo cont�m informa��es propriet�rias.
 */
package br.gov.bcb.sisaps.web.page.componentes.drops;

import java.util.List;

import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

@SuppressWarnings("serial")
public class CustomDropDownChoice<T> extends DropDownChoice<T> {

    private String valorPadrao;

    public CustomDropDownChoice(String id, String valorPadrao, List<T> valores, ChoiceRenderer<T> choice) {
        super(id, valores, choice);
        this.valorPadrao = valorPadrao;
    }

    public CustomDropDownChoice(String id, String valorPadrao, IModel<T> model, List<? extends T> data,
            IChoiceRenderer<? super T> renderer) {
        super(id, model, data, renderer);
        this.valorPadrao = valorPadrao;
    }

    public CustomDropDownChoice(final String id, final PropertyModel<T> propertyModel, final List<? extends T> data) {
        super(id, propertyModel, data);
    }

    @Override
    protected CharSequence getDefaultChoice(String selected) {
        return "<option value=\"\">" + valorPadrao + "</option>";
    }
}
