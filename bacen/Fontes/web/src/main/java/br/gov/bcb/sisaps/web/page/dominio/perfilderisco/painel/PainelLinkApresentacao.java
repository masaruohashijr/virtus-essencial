package br.gov.bcb.sisaps.web.page.dominio.perfilderisco.painel;

import org.apache.wicket.markup.html.link.Link;

import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.dominio.perfilderisco.apresentacao.ApresentacaoPage;

@SuppressWarnings("serial")
public class PainelLinkApresentacao extends PainelSisAps {
    private PerfilRisco perfilRisco;

    public PainelLinkApresentacao(String id, PerfilRisco perfilRisco) {
        super(id);
        setMarkupId(id);
        this.perfilRisco = perfilRisco;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        addLinkApresentacao();
    }

    @SuppressWarnings("rawtypes")
    private void addLinkApresentacao() {
        // Link para a apresentação.
        Link linkApresentacao = new Link("linkApresentacao") {
            @Override
            public void onClick() {
                setResponsePage(new ApresentacaoPage(perfilRisco, perfilRisco.getCiclo(), PerfilAcessoEnum.SUPERVISOR));
            }
        };
        addOrReplace(linkApresentacao);
    }

    public void setPerfilRisco(PerfilRisco perfilRisco) {
        this.perfilRisco = perfilRisco;
    }

}
