package br.gov.bcb.sisaps.web.page.componentes.grupo;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

@SuppressWarnings("serial")
public class GrupoExpansivelCampoValor extends Panel {
    private static final String CELLPADDING = "cellpadding";
    private static final String CELLSPACING = "cellspacing";
    private static final String SEPARATOR_CLASS = " ";
    private final String sufixoId;
    private final String nomeCampo;
    private final String valorCampo;
    private final boolean expandido;
    private final Component[] componentes;
    private ControleExpansivel controle;
    private AjaxLink<String> linkExpandirColapsar;
    private final boolean isExpansivelVisivel;
    
    public GrupoExpansivelCampoValor(String id, String nomeCampo, String valorCampo, boolean expandido,
            final Component... componentes) {
        super(id);
        setOutputMarkupId(true);
        sufixoId = System.currentTimeMillis() + "_" + System.nanoTime();
        this.nomeCampo = nomeCampo;
        this.valorCampo = valorCampo;
        this.expandido = expandido;
        this.componentes = componentes;
        this.isExpansivelVisivel = true;
    }
    
    public GrupoExpansivelCampoValor(String id, String nomeCampo, String valorCampo, boolean expandido,
            boolean isExpansivelVisivel, final Component... componentes) {
        super(id);
        setOutputMarkupId(true);
        sufixoId = System.currentTimeMillis() + "_" + System.nanoTime();
        this.nomeCampo = nomeCampo;
        this.valorCampo = valorCampo;
        this.expandido = expandido;
        this.componentes = componentes;
        this.isExpansivelVisivel = isExpansivelVisivel;
    }

    public void addCellPaddingTabelaDados(String tamanho) {
        this.add(new AttributeAppender(CELLPADDING, Model.of(tamanho), SEPARATOR_CLASS));
    }

    public void addCellSpacingTabelaDados(String tamanho) {
        this.add(new AttributeAppender(CELLSPACING, Model.of(tamanho), SEPARATOR_CLASS));
    }

    public void ajustarEspacamentosPadroes() {
        addCellPaddingTabelaDados("0");
        addCellSpacingTabelaDados("1");
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        controle = new ControleExpansivel("controle", expandido, componentes) {
            
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(isExpansivelVisivel);
            }
            
            @Override
            public String getMarkupIdLink() {
                return getMarkupIdControle();
            }
        };
        add(controle);

        Label lblNomeCampo = new Label("nomeCampo", Model.of(nomeCampo)) {
            @Override
            public String getMarkupId() {
                return getMarkupIdNomeCampo();
            }
        };
        lblNomeCampo.setOutputMarkupId(true);
        add(lblNomeCampo);

        addLinhaValorCampo();
    }


    
    public String definirStyleGrupoExpansivel() {
        return "";
    }

    public String definirCorGrupoExpansivel() {
        return "";
    }

    private void addLinhaValorCampo() {
        Label lblValorCampo = new Label("valorCampo", Model.of(valorCampo)) {
            @Override
            public String getMarkupId() {
                return getMarkupIdValorCampo();
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(valorCampo != null);
            }
        };
        lblValorCampo.setOutputMarkupId(true);
        add(lblValorCampo);

        linkExpandirColapsar = new AjaxLink<String>("linkExpandirColapsar") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                if (getLinkExpandirColapsar().getBody() == null) {
                    controle.expandirColapsar(target);
                } else {
                    executeOnClick();
                }
            }
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(valorCampo == null);
            }
        };
        add(linkExpandirColapsar);
    }


    public void executeOnClick() {
        //TODO método sobrescrito.
    }


    public String getMarkupIdControle() {
        return "grupo_controle_" + getId();
    }

    public String getMarkupIdNomeCampo() {
        return "grupo_nome_campo_" + sufixoId;
    }

    public String getMarkupIdValorCampo() {
        return "grupo_valor_campo_" + sufixoId;
    }

    public ControleExpansivel getControle() {
        return controle;
    }

    public void setControle(ControleExpansivel controle) {
        this.controle = controle;
    }

    public AjaxLink<String> getLinkExpandirColapsar() {
        return linkExpandirColapsar;
    }


}
