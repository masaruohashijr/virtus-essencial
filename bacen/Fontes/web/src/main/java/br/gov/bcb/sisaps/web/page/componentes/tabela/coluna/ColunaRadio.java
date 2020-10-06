package br.gov.bcb.sisaps.web.page.componentes.tabela.coluna;

import org.apache.wicket.markup.html.form.Radio;

import br.gov.bcb.sisaps.web.page.PainelSisAps;

@SuppressWarnings("serial")
public class ColunaRadio extends PainelSisAps {

    public ColunaRadio(String id, Radio<?> radio) {
        super(id);
        add(radio);
    }

}
