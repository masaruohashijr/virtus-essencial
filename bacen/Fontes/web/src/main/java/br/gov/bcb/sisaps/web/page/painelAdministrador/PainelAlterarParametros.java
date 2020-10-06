package br.gov.bcb.sisaps.web.page.painelAdministrador;

import org.apache.wicket.ajax.AjaxRequestTarget;

import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoEmailCorecEnum;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.botoes.AjaxSubmitLinkSisAps;

@SuppressWarnings("serial")
public class PainelAlterarParametros extends PainelSisAps {

    public PainelAlterarParametros(String id) {
        super(id);
        setMarkupId(id);

    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        AjaxSubmitLinkSisAps linkApresentacao = new AjaxSubmitLinkSisAps("idApresentacao") {

            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                getPaginaAtual().avancarParaNovaPagina(new EditarParametroEmailPage(TipoEmailCorecEnum.APRESENTACAO));
            }
        };

        addOrReplace(linkApresentacao);

        AjaxSubmitLinkSisAps linkDisponibilidade = new AjaxSubmitLinkSisAps("idDisponibilidade") {

            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                getPaginaAtual().avancarParaNovaPagina(new EditarParametroEmailPage(TipoEmailCorecEnum.DISPONIBILIDADE));
            }
        };

        addOrReplace(linkDisponibilidade);

        AjaxSubmitLinkSisAps linkSolicitacao = new AjaxSubmitLinkSisAps("idSolicitarAssinatura") {

            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                getPaginaAtual().avancarParaNovaPagina(
                        new EditarParametroEmailPage(TipoEmailCorecEnum.SOLICITACAO_ASSINATURA));
            }
        };

        addOrReplace(linkSolicitacao);

    }

}
