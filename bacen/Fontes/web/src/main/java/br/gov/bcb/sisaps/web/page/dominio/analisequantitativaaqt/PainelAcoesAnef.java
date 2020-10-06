package br.gov.bcb.sisaps.web.page.dominio.analisequantitativaaqt;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.gov.bcb.sisaps.integracao.seguranca.PerfisAcesso;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.DesignacaoAQTMediator;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.autorizacao.anotacao.SisApsAuthorizeAction;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.consulta.ConsultaHistoricoPage;

@SuppressWarnings("serial")
@SisApsAuthorizeAction(action = Action.RENDER, roles = {PerfisAcesso.SUPERVISOR})
public class PainelAcoesAnef extends PainelSisAps {

    @SpringBean
    private AnaliseQuantitativaAQTMediator analiseQuantitativaAQTMediator;

    private final AnaliseQuantitativaAQT analiseQuantitativaAQT;

    public PainelAcoesAnef(String id, AnaliseQuantitativaAQT analiseQuantitativaAQT) {
        super(id);
        this.analiseQuantitativaAQT = analiseQuantitativaAQT;
        setOutputMarkupId(true);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

        setVisibilityAllowed(!(getPaginaAtual().getPaginaAnterior() instanceof ConsultaHistoricoPage)
                && analiseQuantitativaAQTMediator.podeExibirPainelAcoesAnef(analiseQuantitativaAQT));
        addComponents();
    }

    private void addComponents() {
        add(new PainelAcoesAnaliseAnef("idPainelAnalisarAnef", analiseQuantitativaAQT) {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                this.setVisibilityAllowed(analiseQuantitativaAQTMediator.podeAnalisar(analiseQuantitativaAQT));
            }

        });

        addOrReplace(new PainelAcoesDesignarAnef("idPainelDesignarAnef", analiseQuantitativaAQT) {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                this.setVisibilityAllowed(DesignacaoAQTMediator.get().podeDesignar(analiseQuantitativaAQT));
            }
        });

        addOrReplace(new PainelAcoesDelegarAnef("idPainelDelegarAnef", analiseQuantitativaAQT) {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                this.setVisibilityAllowed(analiseQuantitativaAQTMediator.podeDelegar(analiseQuantitativaAQT));
            }
        });

        addOrReplace(new PainelAcoesExcluirDesignarAnef("idPainelExcluirDesignarAnef", analiseQuantitativaAQT) {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                this.setVisibilityAllowed(DesignacaoAQTMediator.get().podeExcluirDesignar(analiseQuantitativaAQT));
            }
        });
    }

    public AnaliseQuantitativaAQT getAnaliseQuantitativaAQT() {
        return analiseQuantitativaAQT;
    }

}