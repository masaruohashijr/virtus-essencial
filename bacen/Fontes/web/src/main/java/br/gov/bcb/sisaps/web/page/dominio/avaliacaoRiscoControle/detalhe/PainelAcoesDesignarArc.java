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

@SuppressWarnings("serial")
public class PainelAcoesDesignarArc extends AbstracSelecionarServidoresPainel {

    private static final String ARC_DESIGNADO_COM_SUCESSO = "ARC Designado com sucesso.";

    public PainelAcoesDesignarArc(String id, AvaliacaoRiscoControle avaliacao, Matriz matriz, Atividade atividade) {
        super(id, avaliacao, matriz, atividade);
        setOutputMarkupId(true);
        addTitulo();
        addBotao("bttDesignar");
        addCampos(matriz.getCiclo());
    }

    private void addTitulo() {
        String titulo =
                AvaliacaoRiscoControleMediator.get().estadoPrevisto(avaliacao.getEstado()) ? "Designação"
                        : "Nova designação";
        Label lblTitulo = new Label("idNovaDesignacao", titulo);
        add(lblTitulo);

    }

    @Override
    protected void submitButon(AjaxRequestTarget target) {
        avaliacao = AvaliacaoRiscoControleMediator.get().loadPK(avaliacao.getPk());
        designacaoMediator.incluir(((UsuarioAplicacao) UsuarioCorrente.get()).getMatricula(), atividade,
                avaliacao, modelServidorEquipe.getObject(), modelServidorUnidade.getObject(), true);
        
        if (AvaliacaoRiscoControleMediator.get().estadoDesignado(avaliacao.getEstado())) {
            getPaginaAtual().avancarParaNovaPagina(
                    new DetalharArcPrevistoDesignado(avaliacao, matriz, atividade == null ? null : atividade.getPk(),
                            ARC_DESIGNADO_COM_SUCESSO, true), getPaginaAtual().getPaginaAnterior());
        } else {
            getPaginaAtual().avancarParaNovaPagina(
                    new DetalharArcEdicao(avaliacao, matriz, atividade == null ? null : atividade.getPk(),
                            ARC_DESIGNADO_COM_SUCESSO, true), getPaginaAtual().getPaginaAnterior());
        }
    }

    private void addCampos(Ciclo ciclo) {
        addComboServidoresEquipe(ciclo, "idServidorEquipe");
        addComboUnidade("idUnidade");
        addComboServidoresUnidade("idServidorUnidadeSelecionada");

        IModel<String> msgBotao = new IModel<String>() {
            @Override
            public String getObject() {
                return atualizarEstadoRiscoControle();
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

    private String atualizarEstadoRiscoControle() {
        if (EstadoARCEnum.PREENCHIDO.equals(avaliacao.getEstado())
                || EstadoARCEnum.ANALISE_DELEGADA.equals(avaliacao.getEstado())) {
            return "Atenção ARC já preenchido por inspetor";
        } else if (EstadoARCEnum.EM_ANALISE.equals(avaliacao.getEstado())
                && avaliacao.getDelegacao() != null) {
            return "Atenção ARC já preenchido por inspetor e com análise em andamento por delegado.";
        }
        return "";
    }
}
