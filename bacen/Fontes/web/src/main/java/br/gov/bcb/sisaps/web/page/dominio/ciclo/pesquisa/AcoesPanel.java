package br.gov.bcb.sisaps.web.page.dominio.ciclo.pesquisa;

import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import br.gov.bcb.sisaps.src.vo.CicloVO;
import br.gov.bcb.sisaps.web.page.componentes.util.ConstantesImagens;

public class AcoesPanel extends Panel {

    private final IModel<CicloVO> model;

    public AcoesPanel(String id, IModel<CicloVO> model) {
        super(id, model);
        this.model = model;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        Link<CicloVO> link = new Link<CicloVO>("linkDetalhar") {
            @Override
            public void onClick() {
                model.getObject();
                setResponsePage(getPage());
            }
        };
        link.add(new Image("detalhar", ConstantesImagens.IMG_DETALHAR));
        add(link);
    }
}