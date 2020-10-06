package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.detalhe;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.PropertyModel;

import br.gov.bcb.sisaps.src.dominio.AvaliacaoARC;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.ParametroGrupoRiscoControle;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.LabelLinhas;

public class PainelNotaVigenteArc extends PainelSisAps {
    private static final String ID_NOME_DO_GRUPO = "idNomeDoGrupo";
    private final WebMarkupContainer wmcNovaNotaArc = new WebMarkupContainer("exibirNotaVigente");
    private final WebMarkupContainer wmcExibirNota = new WebMarkupContainer("wmcExibirNota");
    private final WebMarkupContainer wmcExibirNotaInspetor = new WebMarkupContainer("wmcExibirNotaInspetor");
    private final WebMarkupContainer wmcExibirGrupo = new WebMarkupContainer("exibirNomeGrupo");

    private AvaliacaoRiscoControle avaliacao;
    private final boolean isConsultaArc;
    private final boolean exibirNota;
    private final ParametroGrupoRiscoControle parametroGrupoRiscoControle;
    private final Ciclo ciclo;
    private final boolean isPerfilAtual;

    public PainelNotaVigenteArc(String id, ParametroGrupoRiscoControle parametroGrupoRiscoControle, 
            AvaliacaoRiscoControle avaliacao, boolean isConsultaArc, boolean exibirNota, Ciclo ciclo, 
            boolean isPerfilAtual) {
        super(id);
        this.parametroGrupoRiscoControle = parametroGrupoRiscoControle;
        this.avaliacao = avaliacao;
        this.isConsultaArc = isConsultaArc;
        this.exibirNota = exibirNota;
        this.ciclo = ciclo;
        this.isPerfilAtual = isPerfilAtual;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        wmcExibirGrupo.addOrReplace(new Label(ID_NOME_DO_GRUPO, new PropertyModel<String>(parametroGrupoRiscoControle,
                "nome") {
            @Override
            public String getObject() {
                return parametroGrupoRiscoControle.getNome(avaliacao.getTipo());
            }
        }));
        addOrReplace(wmcExibirGrupo);
        exibirNotaVigente();
        AvaliacaoARC avaliacaoArc = avaliacao.getAvaliacaoARCInspetor();
        notaGerente(avaliacaoArc);
        notaInspetor(avaliacaoArc);
    }

    private void notaInspetor(AvaliacaoARC avaliacaoArc) {
        novaNotaArc();
        notaJustificativa(avaliacaoArc, "idNovaNotaArcAjustada", wmcExibirNota);
        addNotaAjustada(avaliacaoArc);
        addOrReplace(wmcExibirNota);
    }

    private void notaJustificativa(AvaliacaoARC avaliacaoArc, String id, WebMarkupContainer componente) {
        componente.addOrReplace(new Label(id, avaliacaoArc == null || avaliacaoArc.getJustificativa() == null ? ""
                : avaliacaoArc.getParametroNota().getDescricaoValor()));
    }

    private void notaGerente(AvaliacaoARC avaliacaoArc) {
        wmcExibirNotaInspetor.add(new Label("idNotaArrastoArcInspetor", new PropertyModel<String>(avaliacao,
                AvaliacaoRiscoControleMediator.get().getDescricaoNotaCalculadaInspetor(avaliacao))));
        addOrReplace(wmcExibirNotaInspetor);
        notaJustificativa(avaliacaoArc, "idNovaNotaArcAjustadaPrenchido", wmcExibirNotaInspetor);
        addJustificativa(avaliacaoArc, "idJustificativaPreenchido", wmcExibirNotaInspetor);
        wmcExibirNotaInspetor.setVisibilityAllowed(perfilGerente(getPaginaAtual()));
        addOrReplace(wmcExibirNotaInspetor);
    }

    private void addNotaAjustada(AvaliacaoARC avaliacaoArc) {
        String titulo = isConsultaArc ? "Nota do ARC ajustada" : "Nova nota do ARC (ajustada)";
        Label idTituloNovaNotaArc = new Label("idTituloNovaAjustada", titulo);
        wmcExibirNota.addOrReplace(idTituloNovaNotaArc);
        addJustificativa(avaliacaoArc, "idJustificativa", wmcExibirNota);
    }

    private void addJustificativa(final AvaliacaoARC avaliacaoArc, String id, WebMarkupContainer componente) {
        Label justifi =
                new LabelLinhas(id, avaliacaoArc == null ? ""
                        : avaliacaoArc.getJustificativa() == null ? "" : avaliacaoArc.getJustificativa()) {
                    @Override
                    protected void onConfigure() {
                        super.onConfigure();
                        setVisible(avaliacaoArc != null && avaliacaoArc.getJustificativa() != null);
                    }
                };
        justifi.setEscapeModelStrings(false);
        componente.addOrReplace(justifi);

    }


    private void novaNotaArc() {
        String titulo = isConsultaArc ? "Nota do ARC calculada" : "Nova nota do ARC";
        Label idTituloNovaNotaArc = new Label("idTituloNovaNotaArc", titulo);
        wmcExibirNota.addOrReplace(idTituloNovaNotaArc);
        wmcExibirNota.addOrReplace(new Label("idNovaNotaArc", new PropertyModel<String>(avaliacao,
                AvaliacaoRiscoControleMediator.get().getDescricaoNotaCalculadaSupervisor(avaliacao))));
        wmcExibirNota.setVisible(exibirNota);
        addOrReplace(wmcExibirNota);
    }

    private void exibirNotaVigente() {
        String notaVigente =
                AvaliacaoRiscoControleMediator.get()
                        .notaArcIndicadorCorec(avaliacao, ciclo, getPerfilPorPagina(), true, isPerfilAtual);
        if ("".equals(notaVigente)) {
            notaVigente = avaliacao.getNotaVigenteDescricaoValor();
        }

        wmcNovaNotaArc.addOrReplace(new Label("idNotaVigenteArc", 
                notaVigente.equals(Constantes.ASTERISCO_A) ? "" : notaVigente));
        wmcNovaNotaArc.setVisible(!AvaliacaoRiscoControleMediator.get().estadoConcluido(avaliacao.getEstado()));
        addOrReplace(wmcNovaNotaArc);
    }

    public void setAvaliacao(AvaliacaoRiscoControle avaliacao) {
        this.avaliacao = avaliacao;
    }

}
