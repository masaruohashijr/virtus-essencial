package br.gov.bcb.sisaps.web.page.painelComite;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;

import br.gov.bcb.sisaps.web.page.PainelSisAps;

@SuppressWarnings("serial")
public class PainelDropDownAlerta extends PainelSisAps {

    public PainelDropDownAlerta(String id, DropDownChoice<?> drop, Label label) {
        super(id);
        setOutputMarkupId(true);
        add(drop.setOutputMarkupId(true));
        add(label.setOutputMarkupId(true));
    }

}
