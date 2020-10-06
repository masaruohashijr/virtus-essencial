package br.gov.bcb.sisaps.web.page.dominio.perfilderisco;

import org.apache.wicket.markup.html.WebMarkupContainer;

import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.web.page.PainelSisAps;

@SuppressWarnings("serial")
public class PainelMenuLink extends PainelSisAps {
    private WebMarkupContainer mostrarAcao = new WebMarkupContainer("mostrarAcoes");
    private WebMarkupContainer mostrarOutrasInformacoes = new WebMarkupContainer("mostrarOutrasInformacoes");
    private WebMarkupContainer mostrarCorec = new WebMarkupContainer("mostrarCorec");
    private PerfilRisco perfilRisco;

    public PainelMenuLink(String id, PerfilRisco perfilRisco) {
        super(id);
        this.perfilRisco = perfilRisco;

    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        mostrarAcao.setVisibilityAllowed(!isPerfilRiscoCicloMigrado());
        addOrReplace(mostrarAcao);

        mostrarOutrasInformacoes.setVisibilityAllowed(!isPerfilRiscoCicloMigrado());
        addOrReplace(mostrarOutrasInformacoes);

        mostrarCorec.setVisibilityAllowed(CicloMediator.get().exibirSecaoCorec(getPerfilRisco().getCiclo(),
                getPerfilRisco(), getPerfilPorPagina()));
        addOrReplace(mostrarCorec);

    }

    public PerfilRisco getPerfilRisco() {
        return perfilRisco;
    }

    public void setPerfilRisco(PerfilRisco perfilRisco) {
        this.perfilRisco = perfilRisco;
    }

    protected boolean isPerfilRiscoCicloMigrado() {
        return SisapsUtil.isCicloMigrado(getPerfilRisco().getCiclo());
    }
}
