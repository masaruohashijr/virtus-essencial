package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.detalhe;

import org.apache.wicket.authorization.Action;

import br.gov.bcb.sisaps.integracao.seguranca.PerfisAcesso;
import br.gov.bcb.sisaps.src.dominio.Atividade;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoARCEnum;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.autorizacao.anotacao.SisApsAuthorizeAction;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.consulta.ConsultaHistoricoPage;

@SuppressWarnings("serial")
@SisApsAuthorizeAction(action = Action.RENDER, roles = {PerfisAcesso.SUPERVISOR})
public class PainelAcoesArc extends PainelSisAps {

    private final AvaliacaoRiscoControle avaliacao;
    private final Atividade atividade;

    private final Matriz matriz;
    private final boolean apresentarAnalise;
    private final boolean isPerfilAtual;

    public PainelAcoesArc(String id, AvaliacaoRiscoControle avaliacao, Atividade atividade, Matriz matriz,
            boolean apresentarAnalise, boolean isPerfilAtual) {
        super(id);
        setOutputMarkupId(true);
        this.avaliacao = avaliacao;
        this.atividade = atividade;
        this.matriz = matriz;
        this.apresentarAnalise = apresentarAnalise;
        this.isPerfilAtual = isPerfilAtual;

        addComponents();
    }

    public PainelAcoesArc(String id, AvaliacaoRiscoControle avaliacao, Atividade atividade, Matriz matriz,
            boolean isPerfilAtual) {
        this(id, avaliacao, atividade, matriz, true, isPerfilAtual);
        setOutputMarkupId(true);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        setVisibilityAllowed(!(getPaginaAtual().getPaginaAnterior() instanceof ConsultaHistoricoPage)
                && AvaliacaoRiscoControleMediator.get().exibirAcoes(avaliacao, matriz.getCiclo(),
                perfilSupervisor(getPaginaAtual()))
                && isPerfilAtual);
    }

    private void addComponents() {
        add(new PainelAcoesAnaliseArc("idPainelAnalisarArc", avaliacao, matriz, atividade) {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                this.setVisibilityAllowed(apresentarAnalise && podeAnalisar());
            }

        });

        add(new PainelAcoesDesignarArc("idPainelDesignarArc", avaliacao, matriz, atividade) {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                this.setVisibilityAllowed(!EstadoARCEnum.CONCLUIDO.equals(avaliacao.getEstado()));
            }
        });

        add(new PainelAcoesDelegarArc("idPainelDelegarArc", avaliacao, matriz, atividade) {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                this.setVisibilityAllowed(podeAnalisar());
            }
        });
    }

    private boolean podeAnalisar() {
        return EstadoARCEnum.PREENCHIDO.equals(avaliacao.getEstado())
                || EstadoARCEnum.ANALISE_DELEGADA.equals(avaliacao.getEstado())
                || EstadoARCEnum.EM_ANALISE.equals(avaliacao.getEstado())
                || EstadoARCEnum.ANALISADO.equals(avaliacao.getEstado());
    }

}