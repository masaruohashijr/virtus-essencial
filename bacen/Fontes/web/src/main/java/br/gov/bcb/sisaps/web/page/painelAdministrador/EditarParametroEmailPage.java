/*
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software e confidencial e propriedade do Banco Central do Brasil.
 * Nao e permitida sua distribuicao ou divulgacao do seu conteudo sem
 * expressa autorizacao do Banco Central.
 * Este arquivo contem informacoes proprietarias.
 */

package br.gov.bcb.sisaps.web.page.painelAdministrador;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUploadField;

import br.gov.bcb.sisaps.integracao.seguranca.PerfisAcesso;
import br.gov.bcb.sisaps.src.dominio.ParametroEmail;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoEmailCorecEnum;
import br.gov.bcb.sisaps.src.mediator.ParametroEmailMediator;
import br.gov.bcb.sisaps.web.page.DefaultPageMenu;
import br.gov.bcb.sisaps.web.page.componentes.autorizacao.anotacao.SisApsAuthorizeAction;
import br.gov.bcb.sisaps.web.page.componentes.botoes.AjaxSubmitLinkSisAps;

@SisApsAuthorizeAction(action = Action.RENDER, roles = {PerfisAcesso.ADMINISTRADOR})
public class EditarParametroEmailPage extends DefaultPageMenu {
    
    private final TipoEmailCorecEnum tipoEmail;

    public EditarParametroEmailPage(TipoEmailCorecEnum tipoEmail) {
        super();
        this.tipoEmail = tipoEmail;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        ParametroEmail parametroEmail = ParametroEmailMediator.get().buscarPorTipo(tipoEmail);
        Form<?> form = new Form<Object>("formulario");
        form.addOrReplace(new FileUploadField("idFieldUploadAnexo"));
        form.add(new PainelEditarParametroEmail("painelEditarParametroEmail", parametroEmail));
        addBotaoSalvar(form, parametroEmail);
        form.add(new LinkVoltar());
        add(form);
    }
    
    private void addBotaoSalvar(Form<?> form, final ParametroEmail parametroEmail) {
        form.addOrReplace(new AjaxSubmitLinkSisAps("bttSalvar") {
            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                String mensagemSucesso = ParametroEmailMediator.get().salvar(parametroEmail);
                getPaginaAnterior().success(mensagemSucesso);
                setResponsePage(getPaginaAnterior());
            }
        });        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitulo() {
        return "Editar parâmetro";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCodigoTela() {
        return "APSFW0505";
    }
}
