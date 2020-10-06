package br.gov.bcb.sisaps.web.page.dominio.designacao;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.markup.html.form.Form;

import br.gov.bcb.app.stuff.seguranca.UsuarioCorrente;
import br.gov.bcb.sisaps.integracao.seguranca.PerfisAcesso;
import br.gov.bcb.sisaps.seguranca.UsuarioAplicacao;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.DesignacaoMediator;
import br.gov.bcb.sisaps.web.page.DefaultPage;
import br.gov.bcb.sisaps.web.page.componentes.autorizacao.anotacao.SisApsAuthorizeAction;
import br.gov.bcb.sisaps.web.page.componentes.botoes.AjaxSubmitLinkSisAps;
import br.gov.bcb.sisaps.web.page.dominio.ciclo.painel.PainelResumoCiclo;
import br.gov.bcb.sisaps.web.page.dominio.gerenciararc.GerenciarArc;

@SisApsAuthorizeAction(action = Action.RENDER, roles = {PerfisAcesso.SUPERVISOR})
public class DesignacaoARCPage extends DefaultPage {

    private final Form<?> form = new Form<Object>("formulario");
    private final Ciclo ciclo;

    private PainelDesignacaoARC painelDesignacaoARC;

    public DesignacaoARCPage(Integer pkCiclo) {
        ciclo = CicloMediator.get().loadPK(pkCiclo);
        addPaineis();
        addBotoes(form);
        add(form);
    }

    private void addPaineis() {
        addPainelDadosCiclo();
        addPainelDesignarARC();
    }

    private void addPainelDadosCiclo() {
        PainelResumoCiclo painelDados = new PainelResumoCiclo("idPainelDados", ciclo.getPk());
        form.add(painelDados);
    }

    private void addPainelDesignarARC() {
        painelDesignacaoARC = new PainelDesignacaoARC("designacaoARCPanel", ciclo);
        form.add(painelDesignacaoARC);
    }

    private void addBotoes(Form<?> form) {
        form.add(new LinkVoltar() {
            @Override
            public void onClick() {
                avancarParaNovaPagina(new GerenciarArc(ciclo.getPk()), getPaginaAnterior().getPaginaAnterior());
            }
        });
        form.add(botaoDesignar());
    }

    private AjaxSubmitLink botaoDesignar() {
        return new AjaxSubmitLinkSisAps("bttDesignar") {
            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                DesignacaoMediator.get().incluir(((UsuarioAplicacao) UsuarioCorrente.get()).getMatricula(),
                        painelDesignacaoARC.getPainelListagemDesignacao().getArcsSelecionados(),
                        painelDesignacaoARC.getServidorEquipe(), painelDesignacaoARC.getServidorUnidade(), false);
                success("ARC(s) designado(s) com sucesso.");
                painelDesignacaoARC.atualizarTela(target);
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitulo() {
        return "Designação em lotes de ARCs";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCodigoTela() {
        return "APSFW0208";
    }

}
