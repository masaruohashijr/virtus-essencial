package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.detalhe;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import br.gov.bcb.sisaps.src.dominio.AvaliacaoARC;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.ParametroGrupoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfisNotificacaoEnum;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.LabelLinhas;

public class PainelNotasArc extends PainelSisAps {
    private static final String ROWSPAN = "rowspan";
    private AvaliacaoRiscoControle avaliacao;
    private final Ciclo ciclo;
    private final WebMarkupContainer wmcExibirNotaSupervisor = new WebMarkupContainer("wmcExibirNotaSupervisor");
    private final WebMarkupContainer wmcExibirNotaVigente = new WebMarkupContainer("wmcExibirNotaVigente");
    private final WebMarkupContainer wmcExibirNotaCorec = new WebMarkupContainer("wmcExibirNotaCorec");
    private final boolean exibirSupervisor;
    private final boolean exibirNotaVigente;
    private final boolean exibirNotaCorec;
    private final ParametroGrupoRiscoControle parametroGrupoRiscoControle;
    private final AvaliacaoRiscoControle arcRascunho;
    private Label tituloNotaSupervisor;
    private Label tituloNotaInspetor;
    private final boolean isPerfilAtual;

    public PainelNotasArc(String id, ParametroGrupoRiscoControle parametroGrupoRiscoControle, 
            AvaliacaoRiscoControle avaliacao, Ciclo ciclo, boolean exibirSupervisor, boolean exibirNotaVigente, 
            boolean exibirNotaCorec, AvaliacaoRiscoControle arcRascunho, boolean isPerfilAtual) {
        super(id);
        this.parametroGrupoRiscoControle = parametroGrupoRiscoControle;
        this.avaliacao = avaliacao;
        this.ciclo = ciclo;
        this.exibirSupervisor = exibirSupervisor;
        this.exibirNotaVigente = exibirNotaVigente;
        this.exibirNotaCorec = exibirNotaCorec;
        this.arcRascunho = arcRascunho;
        this.isPerfilAtual = isPerfilAtual;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new Label("idNomeDoGrupo", new PropertyModel<String>(parametroGrupoRiscoControle,
                "parametroGrupoRiscoControle.nome") {
            @Override
            public String getObject() {
                return parametroGrupoRiscoControle.getNome(avaliacao.getTipo());
            }
        }));
        addDadosInspetor();
        addDadosSupervisor();
        addDadosNotaCorec();
    }

    private void addDadosInspetor() {
        String notaVigente;

        if (arcRascunho.getNotaVigente() == null && arcRascunho.getValorNota() == null) {
            notaVigente = "*A";
        } else {
            notaVigente =
                    AvaliacaoRiscoControleMediator.get().notaArcDescricaoValor(arcRascunho, ciclo,
                            getPerfilPorPagina(), true, true);
        }

        wmcExibirNotaVigente.addOrReplace(new Label("idNotaVigenteArc", notaVigente));
        wmcExibirNotaVigente.setVisible(exibirNotaVigente);
        addOrReplace(wmcExibirNotaVigente);

        add(new Label("idNotaArrastoArcInspetor", avaliacao == null ? "" : AvaliacaoRiscoControleMediator.get()
                .getNotaCalculadaInspetor(avaliacao)));

        AvaliacaoARC avaliacaoArcInspetor = avaliacao == null ? null : avaliacao.getAvaliacaoARCInspetor();

        final String notaAjustadaInspetor =
                avaliacaoArcInspetor == null || avaliacaoArcInspetor.getParametroNota() == null ? ""
                        : avaliacaoArcInspetor.getParametroNota().getDescricaoValor();
        add(new Label("idNotaArcAjustadaInspetor", notaAjustadaInspetor).setVisible(!"".equals(notaAjustadaInspetor)));

        boolean estadoConcluido =
                AvaliacaoRiscoControleMediator.get().estadoConcluido(avaliacao == null ? null : avaliacao.getEstado());

        tituloNotaInspetor =
                new Label("tituloNovaNotaInspetor", estadoConcluido ? "Nota inspetor" : "Nova nota inspetor") {
                    @Override
                    protected void onConfigure() {
                        super.onConfigure();
                        add(AttributeModifier.replace(ROWSPAN, obterRowSpan("".equals(notaAjustadaInspetor))));
                    }
                };
        add(tituloNotaInspetor);
        add(new LabelLinhas("idJustificativaInspetor", avaliacaoArcInspetor == null
                || avaliacaoArcInspetor.getJustificativa() == null ? "" : avaliacaoArcInspetor.getJustificativa())
                .setEscapeModelStrings(false));
    }

    private IModel<String> corNotaSupervisor(final boolean possui) {
        IModel<String> model = new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                if (possui) {
                    return "#000000";
                } else {
                    return "#999999";
                }
            }
        };
        return model;
    }

    private IModel<String> obterRowSpan(final boolean naoPossui) {
        IModel<String> model = new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                if (naoPossui) {
                    return "0";
                } else {
                    return "2";
                }
            }
        };
        return model;
    }

    private void addDadosSupervisor() {
        Model<String> propertyModel =
                new Model<String>(avaliacao == null ? "" : AvaliacaoRiscoControleMediator.get().getNotaCalculadaFinal(
                        avaliacao));

        Label label = new Label("idNotaArrastoArcSupervisor", propertyModel) {
            @Override
            protected void onConfigure() {
                add(AttributeModifier.replace("color", corNotaSupervisor(avaliacao.possuiNotaElementosSupervisor())));
            }

        };
        wmcExibirNotaSupervisor.add(label);
        AvaliacaoARC avaliacaoARCSupervisor = avaliacao == null ? null : avaliacao.getAvaliacaoARC();
        AvaliacaoARC avaliacaoARCInspetor = avaliacao == null ? null : avaliacao.getAvaliacaoARCInspetor();
        if (avaliacaoARCInspetor != null && avaliacaoARCInspetor.getPk().equals(avaliacaoARCSupervisor.getPk())) {
            avaliacaoARCSupervisor = new AvaliacaoARC();
            avaliacaoARCSupervisor.setAvaliacaoRiscoControle(avaliacao);
            avaliacaoARCSupervisor.setPerfil(PerfisNotificacaoEnum.SUPERVISOR);
        }

        addTituloNotaSupervisor(avaliacaoARCSupervisor);

        wmcExibirNotaSupervisor.add(new LabelLinhas("idJustificativaSupervisor", avaliacaoARCSupervisor == null
                || avaliacaoARCSupervisor.getJustificativa() == null ? "" : avaliacaoARCSupervisor.getJustificativa())
                .setEscapeModelStrings(false));

        wmcExibirNotaSupervisor.setVisible(exibirSupervisor);
        addOrReplace(wmcExibirNotaSupervisor);

    }

    private void addTituloNotaSupervisor(AvaliacaoARC avaliacaoARCSupervisor) {
        final String notaAjustadaSupervisor =
                avaliacaoARCSupervisor == null || avaliacaoARCSupervisor.getParametroNota() == null ? ""
                        : avaliacaoARCSupervisor.getParametroNota().getDescricaoValor();
        wmcExibirNotaSupervisor.add(new Label("idNovaNotaArcAjustadaSupervisor", notaAjustadaSupervisor).setVisible(!""
                .equals(notaAjustadaSupervisor)));

        tituloNotaSupervisor =
                new Label("tituloNovaNotaSupervisor", AvaliacaoRiscoControleMediator.get().estadoConcluido(
                        avaliacao == null ? null : avaliacao.getEstado()) ? "Nota supervisor" : "Nova nota supervisor") {
                    @Override
                    protected void onConfigure() {
                        super.onConfigure();
                        add(AttributeModifier.replace(ROWSPAN, obterRowSpan("".equals(notaAjustadaSupervisor))));
                    }
                };
        wmcExibirNotaSupervisor.add(tituloNotaSupervisor);
    }

    private void addDadosNotaCorec() {
        String notaArcIndicadorCorec =
                AvaliacaoRiscoControleMediator.get().notaArcIndicadorCorec(
                        arcRascunho, ciclo, getPerfilPorPagina(), 
                        exibirNotaCorec && !AvaliacaoRiscoControleMediator.get().estadoConcluido(
                                avaliacao == null ? null : avaliacao.getEstado()),
                        isPerfilAtual);
        wmcExibirNotaCorec.add(new Label("idNotaAjusteArcCorec", notaArcIndicadorCorec));
        wmcExibirNotaCorec.setVisible(exibirNotaCorec && !"".equals(notaArcIndicadorCorec));
        addOrReplace(wmcExibirNotaCorec);
    }

    public AvaliacaoRiscoControle getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(AvaliacaoRiscoControle avaliacao) {
        this.avaliacao = avaliacao;
    }

    public Ciclo getCiclo() {
        return ciclo;
    }

    public boolean isExibirSupervisor() {
        return exibirSupervisor;
    }
}
