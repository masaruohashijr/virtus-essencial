package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.detalhe;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.PropertyModel;

import br.gov.bcb.sisaps.src.dominio.AvaliacaoARC;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.ParametroGrupoRiscoControle;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.grupo.GrupoExpansivel;

public class PainelDetalhesARC extends PainelSisAps {

    private static final String COREC = " (Corec)";
    private AvaliacaoRiscoControle avaliacao;
    private final ParametroGrupoRiscoControle parametroGrupoRiscoControle;
    private final Ciclo ciclo;
    private String notaArcIndicadorCorec;
    private final AvaliacaoRiscoControle arcRascunho;
    private final boolean isPerfilRiscoAtual;

    public PainelDetalhesARC(String id, ParametroGrupoRiscoControle parametroGrupoRiscoControle, 
            AvaliacaoRiscoControle avaliacao, Ciclo ciclo, AvaliacaoRiscoControle arcRascunho, 
            boolean isPerfilRiscoAtual) {
        super(id);
        this.parametroGrupoRiscoControle = parametroGrupoRiscoControle;
        this.avaliacao = avaliacao;
        this.ciclo = ciclo;
        this.arcRascunho = arcRascunho;
        this.isPerfilRiscoAtual = isPerfilRiscoAtual;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        addOrReplace(new Label("idNomeDoGrupo", new PropertyModel<String>(parametroGrupoRiscoControle, "nome") {
            @Override
            public String getObject() {
                return parametroGrupoRiscoControle.getNome(avaliacao.getTipo());
            }
        }));
        novaNotaArc();
        AvaliacaoARC avaliacaoArc = avaliacao.getAvaliacaoARC();
        notaArcIndicadorCorec =
                AvaliacaoRiscoControleMediator.get().notaArcIndicadorCorec(arcRascunho, ciclo, getPerfilPorPagina(),
                        true, isPerfilRiscoAtual);
        if ("".equals(notaArcIndicadorCorec)) {
            notaArcIndicadorCorec = avaliacao.getAvaliacaoArcDescricaoValor();
        }
        Label novaNotaAjustada = new Label("idNovaNotaArcAjustada", notaArcIndicadorCorec.replace(COREC, ""));
        addOrReplace(novaNotaAjustada);
        grupo(avaliacaoArc);
    }

    private void novaNotaArc() {
        addOrReplace(new Label("idNovaNotaArc", AvaliacaoRiscoControleMediator.get().getNotaCalculadaFinal(avaliacao)));
    }

    private void grupo(AvaliacaoARC avaliacaoArc) {
        PainelJustificativaArc painelConsulta = new PainelJustificativaArc("idPainelNota", avaliacao);
        GrupoExpansivel grupo =
                new GrupoExpansivel("GrupoEspansivelNota", "Nota do ARC ajustada", false,
                        new Component[] {painelConsulta}) {

                    private static final long serialVersionUID = 1L;

                    @Override
                    public String getMarkupIdControle() {
                        return "bttExpandirNotaAjustada";
                    }

                    @Override
                    public String getMarkupIdTitulo() {
                        return "bttExpandir";
                    }

                    @Override
                    public boolean isControleVisivel() {
                        return !(notaArcIndicadorCorec.contains(COREC));
                    }

                };
        grupo.addStyleGrupo("text-align:right;");
        grupo.setOutputMarkupId(true);
        grupo.setMarkupId(grupo.getMarkupId() + avaliacao.getPk());
        grupo.setVisibilityAllowed((avaliacaoArc != null && (avaliacaoArc.getJustificativa() != null && avaliacaoArc
                .getParametroNota() != null)) || !"".equals(notaArcIndicadorCorec));
        addOrReplace(painelConsulta);
        addOrReplace(grupo);
    }

    public void setAvaliacao(AvaliacaoRiscoControle avaliacao) {
        this.avaliacao = avaliacao;
    }

}
