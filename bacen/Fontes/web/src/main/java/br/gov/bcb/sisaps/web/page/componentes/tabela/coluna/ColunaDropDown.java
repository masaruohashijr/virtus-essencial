package br.gov.bcb.sisaps.web.page.componentes.tabela.coluna;

import org.apache.wicket.markup.html.form.DropDownChoice;

import br.gov.bcb.sisaps.web.page.PainelSisAps;

@SuppressWarnings("serial")
public class ColunaDropDown extends PainelSisAps {

    public ColunaDropDown(String id, DropDownChoice<?> drop) {
        super(id);
        add(drop);
    }

}
