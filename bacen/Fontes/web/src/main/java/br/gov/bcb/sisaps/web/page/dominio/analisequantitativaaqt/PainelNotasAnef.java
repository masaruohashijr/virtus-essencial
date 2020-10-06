package br.gov.bcb.sisaps.web.page.dominio.analisequantitativaaqt;

import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AvaliacaoAQT;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.LabelLinhas;

public class PainelNotasAnef extends PainelSisAps {
    private static final String NUMERO_1 = "1";

    private static final String NUMERO_3 = "3";

    private static final String ROWSPAN = "rowspan";

    private static final String VAZIO = "";

    private static final String STYLE = "style";

    private final WebMarkupContainer wmcExibirNotaAjustadaSupervisor = new WebMarkupContainer(
            "wmcExibirNotaAjustadaSupervisor");
    private final WebMarkupContainer wmcExibirNotaCalculadaSupervisor = new WebMarkupContainer(
            "wmcExibirNotaCalculadaSupervisor");
    private final WebMarkupContainer wmcExibirCalculadaInspetor = new WebMarkupContainer("wmcExibirCalculadaInspetor");
    private final WebMarkupContainer wmcExibirNotaCorec = new WebMarkupContainer("wmcExibirNotaCorec");
    private final WebMarkupContainer wmcExibirAjusteInspetor = new WebMarkupContainer("wmcExibirJustificativaInspetor");
    private final WebMarkupContainer wmcExibirVigente = new WebMarkupContainer("wmcExibirVigente");

    private final AnaliseQuantitativaAQT aqt;
    private final AnaliseQuantitativaAQT aqtVigente;

    private final String notaCalculadaInspetor;

    private final String notaCalculadaSupervisor;
    private String notaCorecAjustada;

    private final boolean isPerfilRiscoAtual;

    public PainelNotasAnef(String id, AnaliseQuantitativaAQT aqt, boolean isPerfilRiscoAtual) {
        super(id);
        this.aqt = aqt;
        this.isPerfilRiscoAtual = isPerfilRiscoAtual;
        this.aqtVigente = AnaliseQuantitativaAQTMediator.get().obterAnefVigente(aqt);
        this.notaCalculadaInspetor = aqt.getNotaCalculadaInspetor();
        this.notaCalculadaSupervisor = aqt.getNotaCalculadaSupervisorOuInspetor();
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        if (aqtVigente == null) {
            notaCorecAjustada = "";
        } else {
            notaCorecAjustada = AnaliseQuantitativaAQTMediator.get().notaAjustadaFinal(
                    isPerfilRiscoAtual ? aqtVigente : aqt, getPaginaAtual().getPerfilPorPagina(), isPerfilRiscoAtual);
        }
        add(new Label("idNomeDoGrupo", aqt.getParametroAQT().getDescricao()));
        addDadosVigente();
        addDadosInspetor();
        addDadosSupervisor();
        addDadosCorec();
    }

    private void addDadosVigente() {
        wmcExibirVigente.addOrReplace(new Label("idNotaVigenteAnef", getNotaVigente()));
        wmcExibirVigente.setVisible(exibirNotaVigente());
        addOrReplace(wmcExibirVigente);
    }

    private String getNotaVigente() {
        if (aqtVigente == null) {
            return VAZIO;
        } else if (!notaCorecAjustada.equals(VAZIO)) {
            return notaCorecAjustada;
        } else if (aqtVigente.getNotaVigente() != null) {
            return aqtVigente.getNotaVigente().getValorString();
        } else if (aqtVigente.getValorNota() != null) {
            return aqtVigente.getValorNotaDescricao();
        } else {
            return VAZIO;
        }
    }

    private boolean exibirNotaVigente() {
        return !AnaliseQuantitativaAQTMediator.get().estadoConcluido(aqt.getEstado());
    }

    private void addDadosCorec() {
        wmcExibirNotaCorec.setVisibilityAllowed(exibirNotaCorec(notaCorecAjustada));
        notaCorecAjustada = notaCorecAjustada.replace(Constantes.COREC, Constantes.VAZIO);
        wmcExibirNotaCorec.addOrReplace(new Label("idNotaCorec", notaCorecAjustada));
        addOrReplace(wmcExibirNotaCorec);
    }

    private boolean exibirNotaCorec(String notaCorecAjustada) {
        boolean exibir = false;

        if (AnaliseQuantitativaAQTMediator.get().estadoConcluido(aqt.getEstado())
                && StringUtils.isNotBlank(notaCorecAjustada) && notaCorecAjustada.contains(Constantes.COREC)) {
            if (CicloMediator.get().cicloEmAndamento(aqt.getCiclo()) && aqt.getNotaCorecAnterior() != null) {
                exibir = true;
            } else if (aqt.getNotaCorecAnterior() != null || aqt.getNotaCorecAtual() != null) {
                exibir = true;
            }
        }

        return exibir;
    }

    private void addDadosInspetor() {
        wmcExibirCalculadaInspetor.addOrReplace(new Label("idNovaNotaInspetor", "Nova nota inspetor") {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                add(new AttributeModifier(STYLE, new Model<String>(definirStyleNovaNota())));
                add(new AttributeModifier(ROWSPAN, new Model<String>(definirRowspanNovaNota())));
            }

        });
        wmcExibirCalculadaInspetor.addOrReplace(new Label("idNotaCalculada", "Calculada") {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                add(new AttributeModifier(STYLE, new Model<String>(definirStyleCalculada())));
            }
        });

        wmcExibirCalculadaInspetor.add(new Label("idNotaArrastoAnefInspetor", notaCalculadaInspetor));
        wmcExibirCalculadaInspetor.setVisible(isExibirNotaCalculadaAjustadaInspetor()
                && !notaCalculadaInspetor.equals(Constantes.VAZIO));
        addOrReplace(wmcExibirCalculadaInspetor);

        AvaliacaoAQT avaliacaoArcInspetor = aqt.getAvaliacaoInspetor();

        wmcExibirAjusteInspetor.add(new Label("idNotaAnefAjustadaInspetor", avaliacaoArcInspetor == null
                || avaliacaoArcInspetor.getParametroNota() == null ? Constantes.VAZIO : avaliacaoArcInspetor
                .getParametroNota().getDescricaoValor()));

        wmcExibirAjusteInspetor.add(new LabelLinhas("idJustificativaInspetor", avaliacaoArcInspetor == null
                || avaliacaoArcInspetor.getJustificativa() == null ? Constantes.VAZIO : avaliacaoArcInspetor
                .getJustificativa()).setEscapeModelStrings(false));

        wmcExibirAjusteInspetor.setVisible(isExibirNotaCalculadaAjustadaInspetor() && avaliacaoArcInspetor != null);
        addOrReplace(wmcExibirAjusteInspetor);
        setVisible(true);
    }

    private String definirRowspanNovaNota() {
        if (wmcExibirAjusteInspetor.isVisible()) {
            return NUMERO_3;
        } else {
            return NUMERO_1;
        }
    }

    private String definirRowspanSupervisor() {
        if (wmcExibirNotaAjustadaSupervisor.isVisible()) {
            return NUMERO_3;
        } else {
            return NUMERO_1;
        }
    }

    private String definirStyleNovaNota() {
        String retorno = "vertical-align: middle; text-align: right;";
        if (!exibirNotaVigente()) {
            retorno = "width: 19%;";
        }
        return retorno;
    }

    private String definirStyleCalculada() {
        String retorno = "border-left: 1px solid white;";
        if (!exibirNotaVigente()) {
            retorno += "width: 5%;";
        }
        return retorno;
    }

    private void addDadosSupervisor() {
        wmcExibirNotaCalculadaSupervisor.add(new Label("idNotaArrastoAnefSupervisor", notaCalculadaSupervisor) {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                add(AttributeModifier.replace(STYLE, corNotaSupervisor(compararNotaCalculada())));
            }

            @Override
            public boolean isVisible() {
                return isExibirNotaCalculadaAjustadaSupervisor() && !notaCalculadaSupervisor.equals(Constantes.VAZIO);
            }
        });
        addOrReplace(wmcExibirNotaCalculadaSupervisor);
        wmcExibirNotaCalculadaSupervisor.setVisibilityAllowed(isExibirNotaCalculadaAjustadaSupervisor()
                && !notaCalculadaSupervisor.equals(Constantes.VAZIO));

        final AvaliacaoAQT avaliacaoARCSupervisor = aqt.avaliacaoSupervisor();
        wmcExibirNotaAjustadaSupervisor.add(new Label("idNovaNotaAnefAjustadaSupervisor",
                avaliacaoARCSupervisor == null || avaliacaoARCSupervisor.getParametroNota() == null ? Constantes.VAZIO
                        : avaliacaoARCSupervisor.getParametroNota().getDescricaoValor()) {
            @Override
            public boolean isVisible() {
                return isExibirNotaCalculadaAjustadaSupervisor() && avaliacaoARCSupervisor != null;
            };
        });
        wmcExibirNotaAjustadaSupervisor.add(new LabelLinhas("idJustificativaSupervisor", avaliacaoARCSupervisor == null
                || avaliacaoARCSupervisor.getJustificativa() == null ? Constantes.VAZIO : avaliacaoARCSupervisor
                .getJustificativa()) {
            @Override
            public boolean isVisible() {
                return isExibirNotaCalculadaAjustadaSupervisor() && avaliacaoARCSupervisor != null;
            };

        }.setEscapeModelStrings(false));
        wmcExibirNotaAjustadaSupervisor.setVisibilityAllowed(isExibirNotaCalculadaAjustadaSupervisor()
                && avaliacaoARCSupervisor != null);
        wmcExibirNotaAjustadaSupervisor.setVisible(isExibirNotaCalculadaAjustadaSupervisor()
                && avaliacaoARCSupervisor != null);
        addOrReplace(wmcExibirNotaAjustadaSupervisor);
        wmcExibirNotaCalculadaSupervisor.addOrReplace(new Label("tituloNovaNotaSupervisor", "Nova nota supervisor") {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                add(new AttributeModifier(ROWSPAN, new Model<String>(definirRowspanSupervisor())));
            }

        });
    }

    @Transient
    private IModel<String> corNotaSupervisor(final boolean possui) {
        IModel<String> model = new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                if (possui) {
                    return "color:#000000";
                } else {
                    return "color:#999999";
                }
            }
        };
        return model;
    }

    private boolean compararNotaCalculada() {
        return AnaliseQuantitativaAQTMediator.get().possuiNotaElementosSupervisor(aqt)
                || !notaCalculadaSupervisor.equals(notaCalculadaInspetor);
    }

    public AnaliseQuantitativaAQT getAqt() {
        return aqt;
    }

    private boolean isExibirNotaCalculadaAjustadaInspetor() {
        return !AnaliseQuantitativaAQTMediator.get().estadosPrevistoDesignado(aqt.getEstado());
    }

    private boolean isExibirNotaCalculadaAjustadaSupervisor() {
        return AnaliseQuantitativaAQTMediator.get().exibirColunaSupervisor(aqt.getEstado())
                && !(getPaginaAtual() instanceof ConsultaAQT) || (getPaginaAtual() instanceof AnaliseAQT);
    }

}
