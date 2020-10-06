package br.gov.bcb.sisaps.web.page.dominio.gerenciaes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.PropertyModel;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.GrauPreocupacaoES;
import br.gov.bcb.sisaps.src.dominio.ParametroNota;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.GrauPreocupacaoESMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import br.gov.bcb.sisaps.web.page.DefaultPage;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.LabelLinhas;
import br.gov.bcb.sisaps.web.page.componentes.botoes.AjaxSubmitLinkSisAps;
import br.gov.bcb.sisaps.web.page.componentes.drops.CustomDropDownChoice;
import br.gov.bcb.sisaps.web.page.componentes.textarea.CKEditorTextArea;
import br.gov.bcb.sisaps.web.page.componentes.util.CKEditorUtils;
import br.gov.bcb.sisaps.web.page.componentes.util.ConstantesImagens;

public class PainelNotaFinalEdicao extends PainelSisAps {

    private static final String ATUALIZADO_POR = "Atualizado por ";
    private static final String ENCAMINHADO_POR = "Encaminhado por ";
    private static final String PONTO = ".";
    private final PerfilRisco perfilRisco;
    private final Ciclo ciclo;
    private AjaxSubmitLinkSisAps botaoSalvar;
    private GrauPreocupacaoES grauPreocupacaoESNovo;
    private BigDecimal percentualEdicao;
    private Label novoPercentualNegocio;
    private String notaFinalCalculada = "";
    private String notaAnef = "";
    private String notaMatriz = "";
    private String notaFinalRefinada = "";
    private CustomDropDownChoice<?> selectParametroNota;
    private String markupIdAlerta;
    private WebMarkupContainer painelSupervisor;
    private final WebMarkupContainer wmcJustificativa = new WebMarkupContainer("wmcJustificativa");
    private ModalWindow modalAjuda;

    public PainelNotaFinalEdicao(String id, Integer cicloId, PerfilRisco perfilRisco) {
        super(id);
        this.perfilRisco = perfilRisco;
        this.ciclo = CicloMediator.get().buscarCicloPorPK(cicloId);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        criarGrauPreocupacao();
        addComponentes();
    }
    
    private void criarGrauPreocupacao() {
    	grauPreocupacaoESNovo =
                getPaginaAtual().perfilGerente(getPaginaAtual()) ? GrauPreocupacaoESMediator.get()
                        .getGrauPreocupacaoPendencia(ciclo) : GrauPreocupacaoESMediator.get()
                        .buscarGrauPreocupacaoESRascunho(ciclo.getPk());
        if (grauPreocupacaoESNovo == null) {
            setarDadosNovoGrauPreocupacao();
        }
    }

    private void addComponentes() {
        WebMarkupContainer tabela = new WebMarkupContainer("tDados");
        tabela.setMarkupId("notaFinal_" + tabela.getId());
        addOrReplace(tabela);
        addPainelSupervisor(tabela);
        addModalAjuda();
        addPainelGerente(tabela);
    }

    private void setarDadosNovoGrauPreocupacao() {
        grauPreocupacaoESNovo = new GrauPreocupacaoES();
        grauPreocupacaoESNovo.setCiclo(ciclo);
        GrauPreocupacaoES grauvigente = GrauPreocupacaoESMediator.get().buscarPorPerfilRisco(perfilRisco.getPk());
        grauPreocupacaoESNovo.setGrauPreocupacaoESAnterior(grauvigente);
        if (grauvigente != null) {
            grauPreocupacaoESNovo.setParametroNota(grauvigente.getParametroNota());
            grauPreocupacaoESNovo.setJustificativa(grauvigente.getJustificativa());
            if (grauvigente.getNumeroFatorRelevanciaAnef() != null) {
                grauPreocupacaoESNovo.setNumeroFatorRelevanciaAnef(grauvigente.getNumeroFatorRelevanciaAnef());
            } else if (GrauPreocupacaoESMediator.get().cicloMaisTresAnos(ciclo)) {
                grauPreocupacaoESNovo.setNumeroFatorRelevanciaAnef(new BigDecimal(0.5)
                        .setScale(2, RoundingMode.HALF_UP));
            } else {
                grauPreocupacaoESNovo.setNumeroFatorRelevanciaAnef(new BigDecimal(0.3)
                        .setScale(2, RoundingMode.HALF_UP));
            }
        } else if (GrauPreocupacaoESMediator.get().cicloMaisTresAnos(ciclo)) {
            grauPreocupacaoESNovo.setNumeroFatorRelevanciaAnef(new BigDecimal(0.5).setScale(2, RoundingMode.HALF_UP));
        } else {
            grauPreocupacaoESNovo.setNumeroFatorRelevanciaAnef(new BigDecimal(0.3).setScale(2, RoundingMode.HALF_UP));
        }
    }

    private void addPainelSupervisor(WebMarkupContainer tabela) {
        painelSupervisor = new WebMarkupContainer("paineilSupervisor") {
            @Override
            public boolean isVisible() {
                return getPaginaAtual().perfilSupervisor(getPaginaAtual());
            }
        };
        addNovoGrauPreocupacao(painelSupervisor);
        tabela.addOrReplace(painelSupervisor);
    }

    private void addNovoGrauPreocupacao(WebMarkupContainer tabela) {
        // Notas
        addDadosNotas(tabela);

        // Dados última atualização
        addDadosAtualizacao(tabela);

        // Percentuais
        addDadosPercentuais(tabela);

        // Nova nota ajustada
        addDadosNotaAjustada(tabela);

        // Botões
        addBotaoSalvar(tabela);
        addBotaoConfirmar(tabela);
    }

    private void addDadosNotas(WebMarkupContainer tabela) {
        notaAnef = GrauPreocupacaoESMediator.get().getNotaAEF(perfilRisco, getPerfilPorPagina());
        notaMatriz = GrauPreocupacaoESMediator.get().getNotaMatrizFinal(perfilRisco, getPerfilPorPagina());
        notaFinalCalculada =
                GrauPreocupacaoESMediator.get().getNotaFinalCalculada(grauPreocupacaoESNovo, notaAnef, notaMatriz, perfilRisco.getCiclo());
        notaFinalRefinada =
                GrauPreocupacaoESMediator.get().getNotaFinalRefinada(ciclo.getMetodologia(), notaFinalCalculada);

        tabela.addOrReplace(new LabelLinhas("idNotaMatrizNova", notaMatriz));
        tabela.addOrReplace(new LabelLinhas("idNotaAnefNova", notaAnef));
        tabela.addOrReplace(new LabelLinhas("idNotaAnefNova", notaAnef));
        tabela.addOrReplace(new LabelLinhas("idNotaFinalCalculadaNova", notaFinalCalculada));
        tabela.addOrReplace(new LabelLinhas("idNotaFinalRefinadaNova", notaFinalRefinada));
    }

    private void addDadosAtualizacao(WebMarkupContainer tabela) {
        String strDataRascunho = "";
        String nomeOperadorDataHora = grauPreocupacaoESNovo.getNomeOperadorDataHora();

        if (grauPreocupacaoESNovo.getPendente() != null && !SimNaoEnum.NAO.equals(grauPreocupacaoESNovo.getPendente())) {
            strDataRascunho = ENCAMINHADO_POR + grauPreocupacaoESNovo.getNomeOperadorEncaminhamentoDataHora() + PONTO;
        } else if (nomeOperadorDataHora != null) {
            strDataRascunho = ATUALIZADO_POR + nomeOperadorDataHora + PONTO;
        }

        if (grauPreocupacaoESNovo != null && grauPreocupacaoESNovo.getVersaoPerfilRisco() != null) {
            strDataRascunho = "Sem alterações salvas.";
        }
        tabela.addOrReplace(new LabelLinhas("idDataRascunho", strDataRascunho));
    }

    private void addDadosPercentuais(WebMarkupContainer tabela) {
        // Percentual Matriz
        novoPercentualNegocio =
                new Label("idNovoPercentualAnef", new PropertyModel<String>(this, "novoPercentualEdicaoMatriz"));
        tabela.addOrReplace(novoPercentualNegocio);

        // Percentual Anef
        TextField<BigDecimal> percentualNegocio =
                new TextField<BigDecimal>("idNovoPercentualMatriz", new PropertyModel<BigDecimal>(this,
                        "percentualEdicao"));
        percentualNegocio.add(new AjaxFormComponentUpdatingBehavior("onkeyup") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(novoPercentualNegocio);
            }
        });
        percentualNegocio.setMarkupId("idNovoPercentualMatriz");
        percentualNegocio.setOutputMarkupId(true);
        percentualNegocio.setOutputMarkupPlaceholderTag(true);
        tabela.addOrReplace(percentualNegocio);
        tabela.addOrReplace(new LabelLinhas("idPesoAnefAtual", grauPreocupacaoESNovo.getPercentualAnef()));
        tabela.addOrReplace(new LabelLinhas("idPesoMatrizAtual", grauPreocupacaoESNovo.getPercentualMatriz()));
        addBotaoAlterar(tabela);
    }

    private void addDadosNotaAjustada(WebMarkupContainer tabela) {
        ChoiceRenderer<ParametroNota> render = new ChoiceRenderer<ParametroNota>("descricao", ParametroNota.PROP_ID);
        List<ParametroNota> lista = this.ciclo.getMetodologia().getNotasArcAjuste();
        selectParametroNota =
                new CustomDropDownChoice<ParametroNota>("idNovoGrauPreocupacao", "Remover ajuste",
                        new PropertyModel<ParametroNota>(grauPreocupacaoESNovo, "parametroNota"), lista, render);
        selectParametroNota.setMarkupId(selectParametroNota.getId());
        selectParametroNota.add(new OnChangeAjaxBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                exibirJustificativa(target);
            }
        });
        selectParametroNota.setOutputMarkupId(true);
        tabela.addOrReplace(selectParametroNota);

        AjaxSubmitLink link = new AjaxSubmitLink("linkAjuda") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                modalAjuda.setContent(new PainelAjudaNotaFinal(modalAjuda));
                modalAjuda.show(target);
            }
        };
        link.addOrReplace(new Image("ajuda", ConstantesImagens.IMG_AJUDA));
        tabela.addOrReplace(link);

        PropertyModel<String> propertyDescricao = null;
        String entidade = null;
        propertyDescricao = new PropertyModel<String>(grauPreocupacaoESNovo, "justificativa");
        final Label alerta = new Label("idAlertaDadosNaoSalvos", "Atenção informações não salvas.");
        setMarkupIdAlerta(entidade + alerta.getId());
        alerta.setMarkupId(getMarkupIdAlerta());
        tabela.addOrReplace(alerta);
        CKEditorTextArea<String> descricao =
                new CKEditorTextArea<String>("idJustificativaNovaNota", propertyDescricao) {
                    @Override
                    protected String onKeyUpJS() {
                        return CKEditorUtils.jsAtualizarAlerta(alerta.getMarkupId(), true) + atualizarBotoesVoltar();
                    }
                };
        descricao.setMarkupId(entidade + descricao.getId());
        wmcJustificativa.addOrReplace(descricao);
        wmcJustificativa.setOutputMarkupId(true);
        wmcJustificativa.setMarkupId(wmcJustificativa.getMarkupId());
        wmcJustificativa.setOutputMarkupPlaceholderTag(true);
        wmcJustificativa.setVisible(grauPreocupacaoESNovo.getParametroNota() != null);
        tabela.addOrReplace(wmcJustificativa);
    }

    private void addBotaoAlterar(WebMarkupContainer tabela) {
        AjaxSubmitLink botaoAlterar = new AjaxSubmitLinkSisAps("bttAlterarPercentual") {
            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                BigDecimal percentualAnef = null;
                if (percentualEdicao == null) {
                    percentualAnef = null;
                } else {
                    percentualAnef = new BigDecimal(100).subtract(percentualEdicao).divide(new BigDecimal(100));
                }
                String msg =
                        GrauPreocupacaoESMediator.get().alterarPercentualGrauPreocupacao(grauPreocupacaoESNovo,
                                percentualAnef);
                success(msg);
                atualizarPagina(target);
                percentualEdicao = null;
            }
        };
        botaoAlterar.setOutputMarkupId(true);
        botaoAlterar.setMarkupId("bttAlterarPercentual");
        tabela.addOrReplace(botaoAlterar);
    }

    private void addBotaoSalvar(WebMarkupContainer tabela) {
        botaoSalvar = new AjaxSubmitLinkSisAps("bttSalvar", true) {
            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                if ("".equals(selectParametroNota.getModelValue())) {
                    grauPreocupacaoESNovo.setJustificativa(null);
                }
                mensagemDeSucesso(GrauPreocupacaoESMediator.get().salvarNovoGrauPreocupacao(grauPreocupacaoESNovo));
                target.add(PainelNotaFinalEdicao.this);
            }
        };
        botaoSalvar.setOutputMarkupId(true);
        tabela.addOrReplace(botaoSalvar);
    }

    private void addBotaoConfirmar(WebMarkupContainer tabela) {
        AjaxSubmitLinkSisAps botaoConfirmar = new AjaxSubmitLinkSisAps("bttConfirmar", true) {
            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                mensagemDeSucesso(GrauPreocupacaoESMediator.get().confirmarNovoGrauPreocupacao(grauPreocupacaoESNovo,
                        notaFinalCalculada));
                target.add(PainelNotaFinalEdicao.this);
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                setEnabled(grauPreocupacaoESNovo.getPk() != null
                        && (grauPreocupacaoESNovo.getPendente() == null || grauPreocupacaoESNovo.getPendente().equals(
                                SimNaoEnum.NAO)));
            }
        };
        botaoConfirmar.setMarkupId("notaFinal_" + botaoConfirmar.getId());
        tabela.addOrReplace(botaoConfirmar);
    }

    private void addModalAjuda() {
        modalAjuda = new ModalWindow("modalAjuda");
        modalAjuda.setOutputMarkupId(true);
        modalAjuda.setResizable(false);
        modalAjuda.setAutoSize(true);
        addOrReplace(modalAjuda);
    }

    private void exibirJustificativa(AjaxRequestTarget target) {
        if ("".equals(selectParametroNota.getModelValue())) {
            wmcJustificativa.setVisible(false);
        } else {
            wmcJustificativa.setVisible(true);
        }
        target.add(wmcJustificativa);
    }

    public String getNovoPercentualEdicaoMatriz() {
        if (percentualEdicao == null) {
            return "";
        } else {
            return new BigDecimal(100).subtract(percentualEdicao).toBigInteger().toString() + "%";
        }
    }

    private void mensagemDeSucesso(String texto) {
        success(texto);
    }

    public String getMarkupIdAlerta() {
        return markupIdAlerta;
    }

    public void setMarkupIdAlerta(String markupIdAlerta) {
        this.markupIdAlerta = markupIdAlerta;
    }

    private void atualizarPagina(AjaxRequestTarget target) {
        ((GerenciarES) getPage()).atualizarBotoesAlerta(target, getMarkupIdAlerta());
        ((GerenciarES) getPage()).atualizarPainelNotaFinal(target);
    }

    private String atualizarBotoesVoltar() {
        return ((GerenciarES) getPage()).jsAtualizarBotoesVoltar();
    }

    private void addPainelGerente(WebMarkupContainer tabela) {
        String strDataVigente = "";
        String usuario =
                grauPreocupacaoESNovo == null ? "" : grauPreocupacaoESNovo.getNomeOperadorEncaminhamentoDataHora();
        strDataVigente = ENCAMINHADO_POR + usuario + PONTO;
        String notaAnef = "";
        String pesoAnef = "";
        String notaMatriz = "";
        String pesoMatriz = "";
        String notaFinal = "";
        String notaRefinada = "";
        String notaFinalAjustada = "";
        String justificativaNotaFinalAjustada = "";

        WebMarkupContainer painelGerente = new WebMarkupContainer("painelGerente") {
            @Override
            public boolean isVisible() {
                return getPaginaAtual().perfilGerente(getPaginaAtual())
                        && SimNaoEnum.SIM.equals(grauPreocupacaoESNovo.getPendente());
            }
        };
        painelGerente.addOrReplace(new LabelLinhas("idDataEncaminhamento", strDataVigente));
        painelGerente.addOrReplace(addBotaoConfirmarGerente());

        notaAnef = GrauPreocupacaoESMediator.get().getNotaAEF(perfilRisco, getPerfilPorPagina());
        notaMatriz = GrauPreocupacaoESMediator.get().getNotaMatrizFinal(perfilRisco, getPerfilPorPagina());
        notaFinal = GrauPreocupacaoESMediator.get().getNotaFinalCalculada(grauPreocupacaoESNovo, notaAnef, notaMatriz, perfilRisco.getCiclo());
        notaRefinada = GrauPreocupacaoESMediator.get().getNotaFinalRefinada(ciclo.getMetodologia(), notaFinal);

        if (grauPreocupacaoESNovo != null) {
            // Peso Anef
            pesoAnef = grauPreocupacaoESNovo.getPercentualAnef();

            // Peso Matriz
            pesoMatriz = grauPreocupacaoESNovo.getPercentualMatriz();

            // Nota final
            notaFinalAjustada = grauPreocupacaoESNovo.getDescricaoNotaFinal();
            justificativaNotaFinalAjustada = grauPreocupacaoESNovo.getJustificativa();
        }
        
        WebMarkupContainer linhaNotaAjustada = new WebMarkupContainer("linhaNotaAjustada");
        linhaNotaAjustada.setMarkupId(linhaNotaAjustada.getId());
        LabelLinhas labelNotaAjustada = new LabelLinhas("idNotaFinalAjustada", notaFinalAjustada);
        LabelLinhas labelJustificativaNotaAjustada = new LabelLinhas("idJustificativaNotaFinalAjustada", justificativaNotaFinalAjustada);
        linhaNotaAjustada.addOrReplace(labelNotaAjustada);
        linhaNotaAjustada.addOrReplace(labelJustificativaNotaAjustada.setEscapeModelStrings(false));
        linhaNotaAjustada.setVisibilityAllowed(StringUtils.isNotBlank(notaFinalAjustada));

        painelGerente.addOrReplace(new LabelLinhas("idPesoAnef", pesoAnef));
        painelGerente.addOrReplace(new LabelLinhas("idPesoMatriz", pesoMatriz));
        painelGerente.addOrReplace(new LabelLinhas("idNotaMatriz", notaMatriz));
        painelGerente.addOrReplace(new LabelLinhas("idNotaAnef", notaAnef));
        painelGerente.addOrReplace(new LabelLinhas("idNotaFinalCalculada", notaFinal));
        painelGerente.addOrReplace(new LabelLinhas("idNotaFinalRefinada", notaRefinada));
        painelGerente.addOrReplace(linhaNotaAjustada);
        tabela.addOrReplace(painelGerente);
    }

    private AjaxSubmitLinkSisAps addBotaoConfirmarGerente() {
        AjaxSubmitLinkSisAps botaoConfirmarGerente = new AjaxSubmitLinkSisAps("bttConfirmarGerente", true) {
            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                DefaultPage paginaAnterior = getPaginaAtual().getPaginaAnterior();
                String sucess = GrauPreocupacaoESMediator.get().confirmarGrauPreocupacaoGerente(grauPreocupacaoESNovo);
                getPaginaAtual()
                        .avancarParaNovaPagina(
                                new GerenciarES(ciclo.getPk(),
                                        PerfilRiscoMediator.get().obterPerfilRiscoAtual(ciclo.getPk()), sucess),
                                paginaAnterior == null ? getPaginaAtual() : paginaAnterior);
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                setEnabled(grauPreocupacaoESNovo != null && grauPreocupacaoESNovo.getPendente().booleanValue());
            }
        };
        botaoConfirmarGerente.setMarkupId("notaFinal_" + botaoConfirmarGerente.getId());
        return botaoConfirmarGerente;
    }

}
