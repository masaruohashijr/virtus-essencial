package br.gov.bcb.sisaps.web.page.componentes.tabela.cabecalho;

import org.apache.wicket.markup.html.form.CheckGroupSelector;

import br.gov.bcb.sisaps.web.page.PainelSisAps;

public class CabecalhoCheckGroupSelector extends PainelSisAps {

    public CabecalhoCheckGroupSelector(String id, CheckGroupSelector checkGroupSelector) {
        super(id);
        add(checkGroupSelector);
    }

}
