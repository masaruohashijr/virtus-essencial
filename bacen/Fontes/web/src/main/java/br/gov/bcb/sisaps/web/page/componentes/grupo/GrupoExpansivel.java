package br.gov.bcb.sisaps.web.page.componentes.grupo;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import br.gov.bcb.sisaps.web.page.componentes.util.ConstantesWeb;

@SuppressWarnings("serial")
public class GrupoExpansivel extends Panel {
    private static final String CELLPADDING = "cellpadding";
    private static final String CELLSPACING = "cellspacing";
    private static final String SEPARATOR_CLASS = " ";
    private static final String CLASS = "class";
    private String sufixoId;
    private String titulo;
    private boolean expandido;
    private Component[] componentes;
    private  ControleExpansivel controle;
    private  WebMarkupContainer divGrupo = new WebMarkupContainer("divGrupo");

    public GrupoExpansivel(String id, String titulo, boolean expandido, final Component... componentes) {
        super(id);
        setOutputMarkupId(true);
        sufixoId = System.currentTimeMillis() + "_" + System.nanoTime();
        this.setTitulo(titulo);
        this.expandido = expandido;
        this.componentes = componentes;
        
    }
    
    public void addCellPaddingTabelaDados(String tamanho) {
        this.add(new AttributeAppender(CELLPADDING,  Model.of(tamanho), SEPARATOR_CLASS));
    }

    public void addCellSpacingTabelaDados(String tamanho) {
        this.add(new AttributeAppender(CELLSPACING,  Model.of(tamanho), SEPARATOR_CLASS));
    }
    
    public void addStyleGrupo(String tamanho) {
        divGrupo.add(new AttributeAppender(ConstantesWeb.STYLE,  Model.of(tamanho), SEPARATOR_CLASS));
    }

    public void ajustarEspacamentosPadroes() {
        addCellPaddingTabelaDados("0");
        addCellSpacingTabelaDados("1");
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
       
        addOrReplace(divGrupo);
        controle = new ControleExpansivel("controle", expandido, componentes) {
            @Override
            public String getMarkupIdLink() {
                return getMarkupIdControle();
            }
            
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(isControleVisivel());
            }
        };
        divGrupo.addOrReplace(controle);
        Label lblTitulo = new Label("titulo", Model.of(titulo)) {
            @Override
            public String getMarkupId() {
                return getMarkupIdTitulo();
            }
        };
        lblTitulo.setOutputMarkupId(true);
        divGrupo.addOrReplace(lblTitulo);
    }
    
    public boolean isControleVisivel() {
        return true;
    }

    public String getMarkupIdControle() {
        return "grupo_controle_" + sufixoId;
    }

    public String getMarkupIdTitulo() {
        return "grupo_titulo_" + sufixoId;
    }

    public ControleExpansivel getControle() {
        return controle;
    }

    public void setControle(ControleExpansivel controle) {
        this.controle = controle;
    }
    
    public void setCssTitulo(String css) {
        divGrupo.add(new AttributeModifier(CLASS,  Model.of(css)));
    }

    public void addCssTitulo(String css) {
        divGrupo.add(new AttributeAppender(CLASS, Model.of(css), SEPARATOR_CLASS));
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTitulo() {
        return titulo;
    }

    public WebMarkupContainer getDivGrupo() {
        return divGrupo;
    }
    
}
