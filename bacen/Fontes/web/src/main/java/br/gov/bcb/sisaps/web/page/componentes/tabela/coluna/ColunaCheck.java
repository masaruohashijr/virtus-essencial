package br.gov.bcb.sisaps.web.page.componentes.tabela.coluna;

import org.apache.wicket.markup.html.form.Check;

import br.gov.bcb.sisaps.web.page.PainelSisAps;

@SuppressWarnings("serial")
public class ColunaCheck extends PainelSisAps {

    public ColunaCheck(String id, Check<?> check) {
        super(id);
        add(check);
    }

}
