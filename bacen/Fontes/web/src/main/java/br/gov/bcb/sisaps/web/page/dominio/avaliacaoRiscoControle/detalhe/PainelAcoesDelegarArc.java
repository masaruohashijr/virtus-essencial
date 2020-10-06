package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.detalhe;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

import br.gov.bcb.app.stuff.seguranca.UsuarioCorrente;
import br.gov.bcb.sisaps.seguranca.UsuarioAplicacao;
import br.gov.bcb.sisaps.src.dominio.Atividade;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoARCEnum;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.web.page.componentes.botoes.AjaxSubmitLinkSisAps;

@SuppressWarnings("serial")
public class PainelAcoesDelegarArc extends AbstracSelecionarServidoresPainel {
    private static final String ARC_DELEGADO_COM_SUCESSO = "ARC Delegado com sucesso.";

    public PainelAcoesDelegarArc(String id, AvaliacaoRiscoControle avaliacao, Matriz matriz, Atividade atividade) {
        super(id, avaliacao, matriz, atividade);
        setOutputMarkupId(true);
        add(botaoDelegar());
        addCampos(matriz.getCiclo());
    }

    private AjaxSubmitLinkSisAps botaoDelegar() {
        return new AjaxSubmitLinkSisAps("bttDelegar") {
            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                avaliacao = AvaliacaoRiscoControleMediator.get().loadPK(avaliacao.getPk());
                avaliacao.setAlterarDataUltimaAtualizacao(false);
                incluirDelegacao(avaliacao);
                if (AvaliacaoRiscoControleMediator.get().estadoAnaliseDelegada(avaliacao.getEstado())) {
                    getPaginaAtual().avancarParaNovaPagina(
                            new DetalharArcDelegado(avaliacao, matriz, 
                                    atividade == null ? null : atividade.getPk(), ARC_DELEGADO_COM_SUCESSO),
                            getPaginaAtual().getPaginaAnterior());
                } else {
                    getPaginaAtual().avancarParaNovaPagina(
                            new DetalharArcAnalise(avaliacao, matriz, 
                                    atividade == null ? null : atividade.getPk(), ARC_DELEGADO_COM_SUCESSO),
                            getPaginaAtual().getPaginaAnterior());
                }
            }

        };
    }

    private void incluirDelegacao(AvaliacaoRiscoControle avaliacao) {
        UsuarioAplicacao usuarioAplicacao = (UsuarioAplicacao) UsuarioCorrente.get();
        boolean isSupervisor =
                CicloMediator.get().isSupervisor(
                        matriz.getCiclo().getEntidadeSupervisionavel().getLocalizacao());
        delecagaoMediator.incluir(
                delecagaoMediator.isARCDelegado(avaliacao, usuarioAplicacao.getMatricula())
                        && !isSupervisor, avaliacao, modelServidorEquipe.getObject(),
                modelServidorUnidade.getObject());
    }

    private void addCampos(Ciclo ciclo) {
        addComboServidoresEquipe(ciclo, "idServidorEquipeDelegar");
        addComboUnidade("idUnidadeDelegar");
        addComboServidoresUnidade("idServidorUnidadeSelecionadaDelegar");

        IModel<String> msgBotao = new IModel<String>() {
            @Override
            public String getObject() {
                if (EstadoARCEnum.EM_ANALISE.equals(avaliacao.getEstado())) {
                    return "Atenção ARC com análise em andamento";
                }
                return "";
            }

            @Override
            public void detach() {
                //TODO não precisa implementar
            }

            @Override
            public void setObject(String object) {
                //TODO não precisa implementar
            }
        };

        Label msgButton = new Label("idMsg", msgBotao);
        addOrReplace(msgButton);
    }

    @Override
    protected void submitButon(AjaxRequestTarget target) {
        //TODO não precisa implementar
    }

}
