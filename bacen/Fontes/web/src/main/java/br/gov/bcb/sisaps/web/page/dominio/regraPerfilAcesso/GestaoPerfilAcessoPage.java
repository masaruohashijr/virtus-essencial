package br.gov.bcb.sisaps.web.page.dominio.regraPerfilAcesso;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.markup.html.form.Form;

import br.gov.bcb.sisaps.integracao.seguranca.PerfisAcesso;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.web.page.DefaultPage;
import br.gov.bcb.sisaps.web.page.componentes.autorizacao.anotacao.SisApsAuthorizeAction;

@SisApsAuthorizeAction(action = Action.RENDER, roles = {PerfisAcesso.ADMINISTRADOR})
public class GestaoPerfilAcessoPage extends DefaultPage {
    private final PerfilAcessoEnum perfil;
    private PainelGestaoPerfilDeRisco painelGestaoPerfilDeRisco;

    public GestaoPerfilAcessoPage(PerfilAcessoEnum perfil) {
        this.perfil = perfil;
        buildSecoes();
    }

    private void buildSecoes() {
        Form<?> form = new Form<Object>("form_gestao");
        painelGestaoPerfilDeRisco = new PainelGestaoPerfilDeRisco("idPainelPerfilAcesso", perfil);
        form.add(painelGestaoPerfilDeRisco);
        form.add(new LinkVoltar());

        add(form);

    }

    public void atualizarPaineis(AjaxRequestTarget target) {
        Page pagina = this.getPage();
        pagina.success("Regra de perfil de acesso incluída com sucesso.");
        target.add(pagina.get("feedback"));
        target.add(painelGestaoPerfilDeRisco.getTabela());
    }

    @Override
    public String getTitulo() {
        return "Gestão de perfis de acesso";
    }

    @Override
    public String getCodigoTela() {
        return "APSFW0502";
    }

    public PainelGestaoPerfilDeRisco getPainelGestaoPerfilDeRisco() {
        return painelGestaoPerfilDeRisco;
    }

    public void setPainelGestaoPerfilDeRisco(PainelGestaoPerfilDeRisco painelGestaoPerfilDeRisco) {
        this.painelGestaoPerfilDeRisco = painelGestaoPerfilDeRisco;
    }

}
