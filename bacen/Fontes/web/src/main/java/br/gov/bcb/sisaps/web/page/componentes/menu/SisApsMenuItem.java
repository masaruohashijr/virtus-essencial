package br.gov.bcb.sisaps.web.page.componentes.menu;

import java.util.Arrays;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.mediator.RegraPerfilAcessoMediator;
import br.gov.bcb.wicket.menu.MenuItem;

public class SisApsMenuItem extends MenuItem {
    
    private PerfilAcessoEnum[] perfis;

    public SisApsMenuItem(String id, PerfilAcessoEnum... perfis) {
        super(id);
        this.perfis = perfis;
    }
    
    public SisApsMenuItem addItem(String id) {
        SisApsMenuItem item = new SisApsMenuItem(id);
        add(item);
        return item;
    }
    
    @Override
    protected void onConfigure() {
        super.onConfigure();
        if (perfis != null && perfis.length != 0) {
            setVisibilityAllowed(SpringUtils.get().getBean(RegraPerfilAcessoMediator.class)
                    .isAcessoPermitido(Arrays.asList(perfis)));
        }
    }
    
    public SisApsMenuItem addItem(String id, PerfilAcessoEnum... perfis) {
        SisApsMenuItem item = new SisApsMenuItem(id, perfis);
        add(item);
        return item;
    }
    
}
