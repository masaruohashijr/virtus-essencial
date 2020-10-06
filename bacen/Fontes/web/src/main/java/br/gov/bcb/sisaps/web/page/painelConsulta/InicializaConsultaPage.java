package br.gov.bcb.sisaps.web.page.painelConsulta;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.mediator.RegraPerfilAcessoMediator;
import br.gov.bcb.sisaps.web.page.DefaultPage;

public class InicializaConsultaPage extends DefaultPage {
    
    @Override
    protected void onAfterRender() {
        super.onAfterRender();
        RegraPerfilAcessoMediator regraPerfilAcessoMediator =
                SpringUtils.get().getBean(RegraPerfilAcessoMediator.class);
        if (regraPerfilAcessoMediator.isAcessoPermitido(PerfilAcessoEnum.CONSULTA_TUDO)
                || regraPerfilAcessoMediator.isAcessoPermitido(PerfilAcessoEnum.CONSULTA_NAO_BLOQUEADOS)) {
            setResponsePage(PerfilConsultaPage.class);
        } else {
            setResponsePage(PerfilConsultaResumidoPage.class);
        }
    }
    

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitulo() {
        return "";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCodigoTela() {
        return "";
    }
}
