package br.gov.bcb.sisaps.web.page.painelAdministrador;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.StringValidator;

import br.gov.bcb.app.stuff.seguranca.UsuarioCorrente;
import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.seguranca.UsuarioAplicacao;
import br.gov.bcb.sisaps.src.mediator.ServidorVOMediator;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.botoes.AjaxSubmitLinkSisAps;

@SuppressWarnings("serial")
public class PainelAlterarLocalizacaoTemporaria extends PainelSisAps {
    private static final String FEEDBACK = "feedback";
    ServidorVO servidorLogado;
    String novaLocalizacaoSimulada;
    private Label localizacaoSimuladaAtual;
    private TextField<String> localizacao;

    public PainelAlterarLocalizacaoTemporaria(String id) {
        super(id);
        setMarkupId(id);
        servidorLogado = ((UsuarioAplicacao) UsuarioCorrente.get()).getServidorVO();

    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        addComponents();
    }

    private void addComponents() {
        localizacaoSimuladaAtual =
                new Label("idLocalizacaoSimulada", new PropertyModel<String>(servidorLogado, "localizacaoSimulada"));
        localizacaoSimuladaAtual.setOutputMarkupId(true);
        localizacaoSimuladaAtual.setMarkupId(localizacaoSimuladaAtual.getId());
        add(localizacaoSimuladaAtual);
        addTextFieldLocalizacao();
        add(new BttAlterar());
        add(new BttLimparLocalizacao());

    }

    private void addTextFieldLocalizacao() {
        localizacao =
                new TextField<String>("idNovaLocalizacaoSimulada", new PropertyModel<String>(this,
                        "novaLocalizacaoSimulada"));
        localizacao.add(StringValidator.maximumLength(30));
        localizacao.setOutputMarkupId(true);
        localizacao.setMarkupId(localizacao.getId());
        addOrReplace(localizacao);
    }

    private class BttAlterar extends AjaxSubmitLinkSisAps {
        public BttAlterar() {
            super("bttAlterarLocalizacao");
        }

        @Override
        public void executeSubmit(AjaxRequestTarget target) {
            Page pagina = this.getPage();
            pagina.success(ServidorVOMediator.get().alterarLocalizacaoSimulada(servidorLogado, novaLocalizacaoSimulada));
            target.add(pagina.get(FEEDBACK));
            target.add(localizacaoSimuladaAtual);
            target.add(PainelAlterarLocalizacaoTemporaria.this);

        }
    }

    private class BttLimparLocalizacao extends AjaxSubmitLinkSisAps {

        public BttLimparLocalizacao() {
            super("bttLimparLocalicacao");
        }

        @Override
        public void executeSubmit(AjaxRequestTarget target) {
            Page pagina = this.getPage();
            pagina.success(ServidorVOMediator.get().cancelarAlterarLocalizacaoSimulada(servidorLogado));
            target.add(pagina.get(FEEDBACK));
            novaLocalizacaoSimulada = "";
            target.add(localizacaoSimuladaAtual);
            target.add(PainelAlterarLocalizacaoTemporaria.this);
          
        }
    }

    public String getNovaLocalizacaoSimulada() {
        return novaLocalizacaoSimulada;
    }

    public void setNovaLocalizacaoSimulada(String novaLocalizacaoSimulada) {
        this.novaLocalizacaoSimulada = novaLocalizacaoSimulada;
    }

}
