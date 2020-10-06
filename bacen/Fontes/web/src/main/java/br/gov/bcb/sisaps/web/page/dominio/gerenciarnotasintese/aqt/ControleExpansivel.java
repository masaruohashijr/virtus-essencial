package br.gov.bcb.sisaps.web.page.dominio.gerenciarnotasintese.aqt;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;

import br.gov.bcb.sisaps.web.page.componentes.util.ConstantesImagens;

@SuppressWarnings("serial")
public class ControleExpansivel extends Panel {

    private String sufixoId;
    private boolean expandido;

    private Image imagem;
    private Component[] componentes;

    public ControleExpansivel(String id, boolean expandido, final Component... componentes) {
        this(id, System.currentTimeMillis() + "_" + System.nanoTime(), expandido, componentes);
    }

    public ControleExpansivel(String id, String sufixoIdLink, boolean expandido, final Component... componentes) {
        super(id);
        setOutputMarkupId(true);
        sufixoId = sufixoIdLink;
        this.expandido = expandido;
        this.componentes = componentes;
        for (Component c : componentes) {
            c.setVisible(expandido);
            c.setOutputMarkupId(true);
            c.setOutputMarkupPlaceholderTag(true);
        }
        AjaxFallbackLink<String> link = new AjaxFallbackLink<String>("link") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                expandirColapsar(target);
            }

            @Override
            public String getMarkupId() {
                return getMarkupIdLink();
            }
        };
        link.setOutputMarkupId(true);
        add(link);

        imagem = new Image("imagem", expandido ? ConstantesImagens.IMG_MENOS : ConstantesImagens.IMG_MAIS);
        imagem.setOutputMarkupId(true);
        imagem.setOutputMarkupPlaceholderTag(true);
        link.add(imagem);
    }

    public String getMarkupIdLink() {
        return "controle_link_" + sufixoId;
    }

    public void inverter() {
        setExpandido(!isExpandido());
        for (Component c : componentes) {
            c.setVisible(isExpandido());
        }
        imagem.setImageResourceReference(isExpandido() ? ConstantesImagens.IMG_MENOS : ConstantesImagens.IMG_MAIS);
    }

    public boolean isExpandido() {
        return expandido;
    }

    public void setExpandido(boolean expandido) {
        this.expandido = expandido;
    }

    public void expandirColapsar(AjaxRequestTarget target) {
        inverter();
        target.add(imagem);
        for (Component c : componentes) {
            target.add(c);
        }
    }
}