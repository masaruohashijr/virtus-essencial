package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.detalhe;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.web.page.DefaultPage;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.PainelInformacoesArc;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.analise.AnalisarArcPage;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.analise.PainelResumoElementosArcs;
import br.gov.bcb.sisaps.web.page.dominio.gerenciarnotasintese.GerenciarNotaSintesePage;

public class DetalharArcAnalisado extends DetalharArcComum {
    private final WebMarkupContainer wmcExibirMensagem = new WebMarkupContainer("mensagemSucessoConclusaoARC");
    private final WebMarkupContainer wmcExibirMensagemDelegado = new WebMarkupContainer(
            "mensagemSucessoConclusaoARCDelegado");

    private boolean mostrarMensagem;

    public DetalharArcAnalisado(AvaliacaoRiscoControle avaliacao, Matriz matriz, Integer pkAtividade,
            boolean mostrarMensagem, boolean isPerfilAtual) {
        this(avaliacao, matriz, pkAtividade, isPerfilAtual);
        this.mostrarMensagem = mostrarMensagem;
    }

    public DetalharArcAnalisado(AvaliacaoRiscoControle avaliacao, Matriz matriz, Integer pkAtividade,
            boolean isPerfilAtual) {
        super(avaliacao, matriz, pkAtividade, isPerfilAtual);
    }
    
    public DetalharArcAnalisado(PageParameters parameters) {
  		super(parameters);
  	}

    private void addMensagem() {
        AjaxSubmitLink link = new AjaxSubmitLink("linkPaginaGerenciarNotasSinteses") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                avancarParaNovaPagina(new GerenciarNotaSintesePage(PerfilRiscoMediator.get().obterPerfilRiscoAtual(
                        matriz.getCiclo().getPk()), grupo.getNomeAbreviado()));
            }
        };
        link.setMarkupId(link.getId());
        link.setOutputMarkupId(true);
        wmcExibirMensagem.addOrReplace(link);

        wmcExibirMensagem.setVisibilityAllowed(mostrarMensagem && ehPerfilSupervisor());
        form.addOrReplace(wmcExibirMensagem);

        wmcExibirMensagemDelegado.setVisibilityAllowed(mostrarMensagem && !ehPerfilSupervisor());
        form.addOrReplace(wmcExibirMensagemDelegado);
    }

    private boolean ehPerfilSupervisor() {
        return perfilSupervisor(getPaginaAnterior());
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        addMensagem();
        form.addOrReplace(
                new PainelInformacoesArc("idPainelInformacoesArc", avaliacao, matriz.getCiclo(), atividade, grupo));
        form.addOrReplace(new PainelResumoElementosArcs("idPainelResumoElementosArc", avaliacao, matriz, avaliacao, 
                isPerfilAtual));
        form.addOrReplace(new PainelNotasArc("idPainelRiscoMercado", grupo, avaliacao, matriz.getCiclo(),
                true, true, false, avaliacao, isPerfilAtual));
        form.addOrReplace(new PainelElementosARC("idPainelElementos", avaliacao, matriz.getCiclo()));
        form.addOrReplace(new PainelTendenciaRiscoMercadoDetalheArc("idPainelTendenciaRiscoMercado",
                grupo, avaliacao, true, true, true));
        form.addOrReplace(new LinkVoltar() {
            @Override
            public void onClick() {
                if (getPaginaAnterior() instanceof AnalisarArcPage) {
                    DefaultPage paginaOrigemAnalise = getPaginaAnterior().getPaginaAnterior();
                    if (paginaOrigemAnalise instanceof DetalharArcAnalisado
                            || paginaOrigemAnalise instanceof DetalharArcDelegado
                            || paginaOrigemAnalise instanceof DetalharArcAnalise) {
                        setResponsePage(paginaOrigemAnalise.getPaginaAnterior());
                    } else {
                        setResponsePage(paginaOrigemAnalise);
                    }
                } else {
                    setResponsePage(getPaginaAnterior());
                }
            }
        });
    }

}
