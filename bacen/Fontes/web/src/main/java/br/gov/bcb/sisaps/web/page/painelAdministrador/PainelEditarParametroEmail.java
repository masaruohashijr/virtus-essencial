package br.gov.bcb.sisaps.web.page.painelAdministrador;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;

import br.gov.bcb.sisaps.src.dominio.ParametroEmail;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.textarea.CKEditorTextArea;

@SuppressWarnings("serial")
public class PainelEditarParametroEmail extends PainelSisAps {

    private final ParametroEmail parametroEmail;

    public PainelEditarParametroEmail(String id, ParametroEmail parametroEmail) {
        super(id);
        this.parametroEmail = parametroEmail;
        setMarkupId(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        addTituloSecao();
        addCampos(parametroEmail);
    }

    private void addTituloSecao() {
        String titulo = null;
        switch (parametroEmail.getTipo()) {
            case APRESENTACAO:
                titulo = "apresentação COREC";
                break;
            case DISPONIBILIDADE:
                titulo = "disponibilidade COREC";
                break;
            case SOLICITACAO_ASSINATURA:
                titulo = "solicitação de assinatura da ata COREC";
                break;
            default:
                break;
        }
        addOrReplace(new Label("idTituloSecao", "E-mail " + titulo));
    }

    private void addCampos(final ParametroEmail parametroEmail) {
        addOrReplace(new TextField<String>("idRemetente", new PropertyModel<String>(parametroEmail, "remetente")));
        addOrReplace(new TextField<String>("idTitulo", new PropertyModel<String>(parametroEmail, "titulo")));
        addOrReplace(new CKEditorTextArea<String>("idCorpo", new PropertyModel<String>(parametroEmail, "corpo")));
        
        WebMarkupContainer wmcPrazo = new WebMarkupContainer("wmcPrazo") {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(parametroEmail.getPrazoObrigatorio().booleanValue());
            }
        };
        addOrReplace(wmcPrazo);
        TextField<Integer> prazo = new TextField<Integer>("idPrazo", new PropertyModel<Integer>(parametroEmail, "prazo"));
        wmcPrazo.addOrReplace(prazo);
    }
}
