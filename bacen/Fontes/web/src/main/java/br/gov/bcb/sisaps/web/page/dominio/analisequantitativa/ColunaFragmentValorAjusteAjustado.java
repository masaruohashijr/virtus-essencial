package br.gov.bcb.sisaps.web.page.dominio.analisequantitativa;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.convert.IConverter;

public class ColunaFragmentValorAjusteAjustado<T> extends Fragment {
    public static final String RETURN_SO_NUMEROS_THIS = "return soNumerosSinalSubtracao(this);";
    private Boolean isMostrar = Boolean.FALSE;
    private T objeto;
    private Boolean isCampoVisible = Boolean.TRUE;

    private TextField<T> textField;
    private String expressao;
    private Label lbAsteriscoEdicao;
    private Label labelAjuste;

    public ColunaFragmentValorAjusteAjustado(String id, String markupId, final MarkupContainer markupProvider,
            T objeto, String expressao) {
        super(id, markupId, markupProvider);
        this.expressao = expressao;
        this.objeto = objeto;
    }

    public ColunaFragmentValorAjusteAjustado(String id, String markupId, MarkupContainer markupProvider, T objeto,
            String expressao, boolean isCampoVisible) {
        this(id, markupId, markupProvider, objeto, expressao);
        this.isCampoVisible = isCampoVisible;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

        lbAsteriscoEdicao = new Label(getLabelIdCustomizado(), "*") {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(new AbstractReadOnlyModel<Boolean>() {
                    @Override
                    public Boolean getObject() {
                        return isMostrar;
                    }
                }.getObject());
            }
        };

        textField = new TextFieldCustomizado(getIdCustomizado());
        lbAsteriscoEdicao.setMarkupId(lbAsteriscoEdicao.getId() + objeto);
        lbAsteriscoEdicao.setOutputMarkupId(true);
        lbAsteriscoEdicao.setOutputMarkupPlaceholderTag(true);
        addOrReplace(lbAsteriscoEdicao);
        textField.setMarkupId(textField.getId() + objeto);
        textField.setOutputMarkupId(true);
        textField.setOutputMarkupPlaceholderTag(true);
        addOrReplace(textField);

        labelAjuste = new Label("label", new PropertyModel<T>(objeto, expressao)) {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                onAddConfiguracaoLabel(this);
                setOutputMarkupId(true);
                setOutputMarkupPlaceholderTag(true);
                setVisibilityAllowed(!textField.isVisibilityAllowed());
            };

            @Override
            public <C> IConverter<C> getConverter(Class<C> type) {
                if (getConverterLabel(type) == null) {
                    return super.getConverter(type);
                } else {
                    return getConverterLabel(type);
                }
            }
        };
        addOrReplace(labelAjuste);
    }

    private class TextFieldCustomizado extends TextField<T> {
        public TextFieldCustomizado(String id) {
            super(id, new PropertyModel<T>(objeto, expressao));
            add(new AttributeModifier("maxlength", 12));
            add(new AjaxFormComponentUpdatingBehavior("onkeyup") {
                @Override
                protected void onUpdate(AjaxRequestTarget target) {
                    onUpdateLocal(target);
                }
            });
        }

        @Override
        protected void onConfigure() {
            super.onConfigure();

            setVisibilityAllowed(isCampoVisible);
            onAddConfiguracaoTextField(this);
        }

        private void onUpdateLocal(AjaxRequestTarget target) {
            isMostrar = Boolean.TRUE;
            target.add(lbAsteriscoEdicao);
            executorOnUpdateExterno(target);
        }

        @Override
        public <C> IConverter<C> getConverter(Class<C> type) {
            if (getConverterTextField(type) == null) {
                return super.getConverter(type);
            } else {
                return getConverterTextField(type);
            }
        }
    }

    protected <C> IConverter<C> getConverterTextField(Class<C> type) {
        return null;
    }

    protected <C> IConverter<C> getConverterLabel(Class<C> type) {
        return null;
    }

    protected void onAddConfiguracaoTextField(TextField<T> textFieldCustomizado) {
        // Usar se for necessário adicionar mais configurações no componente.
    }

    protected void onAddConfiguracaoLabel(Label lb) {
        // Usar se for necessário adicionar mais configurações no componente.
    }

    protected String getLabelIdCustomizado() {
        return "asteriscoEdicao";
    }

    protected String getIdCustomizado() {
        return "input";
    }

    protected Boolean isFragmentVisible() {
        return true;
    }

    protected void executorOnUpdateExterno(AjaxRequestTarget target) {
        //fazer Override se necessário
    }

    protected T getObjeto() {
        return objeto;
    }

    public TextField<T> getTextField() {
        return textField;
    }

    public void setIsCampoVisible(Boolean isCampoVisible) {
        this.isCampoVisible = isCampoVisible;
    }

    public Label getLbAsteriscoEdicao() {
        return lbAsteriscoEdicao;
    }

    public Label getLabelAjuste() {
        return labelAjuste;
    }

    public void setIsMostrar(Boolean isMostrar) {
        this.isMostrar = isMostrar;
    }

    public Boolean getIsMostrar() {
        return isMostrar;
    }

}