package br.gov.bcb.sisaps.web.page.dominio.gerenciarnotasintese.painel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.Metodologia;
import br.gov.bcb.sisaps.src.dominio.ParametroGrupoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.ParametroNota;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.SinteseDeRisco;
import br.gov.bcb.sisaps.src.dominio.VersaoPerfilRisco;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoObjetoVersionadorEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoParametroGrupoRiscoControleEnum;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.CelulaRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.ParametroGrupoRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.src.mediator.SinteseDeRiscoMediator;
import br.gov.bcb.sisaps.src.mediator.VersaoPerfilRiscoMediator;
import br.gov.bcb.sisaps.src.vo.ArcNotasVO;
import br.gov.bcb.sisaps.src.vo.CelulaRiscoControleVO;
import br.gov.bcb.sisaps.src.vo.LinhaNotasMatrizVO;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.botoes.AjaxSubmitLinkSisAps;
import br.gov.bcb.sisaps.web.page.componentes.drops.CustomDropDownChoice;
import br.gov.bcb.sisaps.web.page.componentes.textarea.CKEditorTextArea;
import br.gov.bcb.sisaps.web.page.componentes.util.CKEditorUtils;
import br.gov.bcb.sisaps.web.page.componentes.util.ConstantesImagens;
import br.gov.bcb.sisaps.web.page.dominio.gerenciarnotasintese.GerenciarNotaSintesePage;

public class PainelGerenciarSintese extends PainelSisAps {

    private static final String PONTO = ".";
    private static final String MOVER = "mover";
    private static final String DESCRICAO = "descricao";
    @SpringBean
    private SinteseDeRiscoMediator sinteseDeRiscoMediator;
    @SpringBean
    private VersaoPerfilRiscoMediator versaoPerfilRiscoMediator;

    private final List<String> idsAlertas;
    private final Ciclo ciclo;

    private final ParametroGrupoRiscoControle parametroGrupoRisco;
    private final Matriz matriz;
    private final List<Integer> idsParametrosGrupoRiscoMatriz;
    private SinteseDeRisco sintese;
    private WebMarkupContainer tabelaDados;
    private final WebMarkupContainer wmcExibirRiscoLabel = new WebMarkupContainer("wmcExibirRiscoLabel");
    private final WebMarkupContainer wmcExibirRiscoCombo = new WebMarkupContainer("wmcExibirRiscoCombo");
    private Label alertaDadosNaoSalvos;
    private final Metodologia metodologia;
    private PerfilRisco perfilRisco;
    private List<VersaoPerfilRisco> versoesPerfilRiscoARCs;
    private boolean existeARCAnalisadoGrupoRisco;
    private boolean existeGrupoRiscoMatriz;
    private ArcNotasVO arcExterno;

    public PainelGerenciarSintese(String id, PerfilRisco perfilRisco, Metodologia metodologia, Matriz matriz,
            ParametroGrupoRiscoControle parametroGrupoRisco, List<Integer> idsParametrosGrupoRiscoMatriz,
            List<String> idsAlertas) {
        super(id);
        this.perfilRisco = perfilRisco;
        this.ciclo = perfilRisco.getCiclo();
        this.metodologia = metodologia;
        this.matriz = matriz;
        this.parametroGrupoRisco = parametroGrupoRisco;
        this.idsParametrosGrupoRiscoMatriz = idsParametrosGrupoRiscoMatriz;
        this.idsAlertas = idsAlertas;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        tabelaDados = new WebMarkupContainer("tDadosSintese");
        tabelaDados.setMarkupId("t" + getMarkupId());
        add(tabelaDados);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

        if (TipoParametroGrupoRiscoControleEnum.EXTERNO.equals(parametroGrupoRisco.getTipoGrupo())) {

            arcExterno = AvaliacaoRiscoControleMediator.get().consultarNotasArcExterno(perfilRisco.getPk());
            existeARCAnalisadoGrupoRisco =
                    arcExterno != null && (arcExterno.getNotaSupervisor() != null || arcExterno.getValorNota() != null);
        } else {
            existeARCAnalisadoGrupoRisco =
                    ParametroGrupoRiscoControleMediator.get().existeARCAnalisadoGrupoRisco(parametroGrupoRisco, matriz);
        }
        addUltimaSinteseParametroGrupoRisco();
    }

    private void addUltimaSinteseParametroGrupoRisco() {
        Label titulo = new Label("idTituloSintese", parametroGrupoRisco.getNomeAbreviado().toUpperCase());
        titulo.setMarkupId(titulo.getId() + SisapsUtil.criarMarkupId(parametroGrupoRisco.getNomeAbreviado()));
        tabelaDados.addOrReplace(titulo);

        sintese =
                sinteseDeRiscoMediator.getUltimaSinteseParametroGrupoRiscoEdicao(parametroGrupoRisco,
                        perfilRisco.getCiclo());
        if (sintese == null) {
            sintese = new SinteseDeRisco();
            sintese.setParametroGrupoRiscoControle(parametroGrupoRisco);
            sintese.setCiclo(perfilRisco.getCiclo());
        } else if (sintese.getVersaoPerfilRisco() != null) {
            SinteseDeRisco sinteseAnterior = sintese;
            sintese = new SinteseDeRisco();
            sintese.setParametroGrupoRiscoControle(parametroGrupoRisco);
            sintese.setCiclo(perfilRisco.getCiclo());
            sintese.setSinteseAnterior(sinteseAnterior);
            sintese.setDescricaoSintese(sintese.getSinteseAnterior().getDescricaoSintese());
        }

        addRiscoSinteseLabel();
        addRiscoSinteseCombo();
        addSinteseVigente();
        addNovaSintese();
    }

    private void addRiscoSinteseLabel() {
        existeGrupoRiscoMatriz =
                idsParametrosGrupoRiscoMatriz.contains(sintese.getParametroGrupoRiscoControle().getPk());
        String riscoVigente = "";
        String riscoAnalise = "";
        String labelCampoString = "Risco";
        if (existeGrupoRiscoMatriz) {
            if (TipoParametroGrupoRiscoControleEnum.EXTERNO.equals(sintese.getParametroGrupoRiscoControle()
                    .getTipoGrupo())) {

                if (arcExterno != null) {
                    riscoVigente =
                            AvaliacaoRiscoControleMediator.get().notaArc(
                                    arcExterno, ciclo, getPerfilPorPagina(), perfilRisco);
                    riscoAnalise =
                            AvaliacaoRiscoControleMediator.get()
                                    .notaArcAnalise(arcExterno, ciclo, getPerfilPorPagina(), perfilRisco);
                }
                labelCampoString = "Nota";

            } else {
                versoesPerfilRiscoARCs =
                        versaoPerfilRiscoMediator.buscarVersoesPerfilRisco(perfilRisco.getPk(),
                                TipoObjetoVersionadorEnum.CELULA_RISCO_CONTROLE);

                List<CelulaRiscoControleVO> celulas =
                        CelulaRiscoControleMediator.get().buscarCelulaPorVersaoPerfilVO(versoesPerfilRiscoARCs);

                List<ParametroGrupoRiscoControle> listaParamentro = new ArrayList<ParametroGrupoRiscoControle>();
                listaParamentro.add(parametroGrupoRisco);
                riscoVigente =
                        sinteseDeRiscoMediator.obterDescricaoRisco(celulas, matriz, versoesPerfilRiscoARCs, true,
                                listaParamentro, new LinkedList<LinhaNotasMatrizVO>(),
                                new LinkedList<LinhaNotasMatrizVO>(), getPaginaAtual().getPerfilPorPagina(),
                                perfilRisco);

                riscoAnalise =
                        sinteseDeRiscoMediator.obterDescricaoRisco(celulas, matriz, versoesPerfilRiscoARCs, false,
                                listaParamentro, new LinkedList<LinhaNotasMatrizVO>(),
                                new LinkedList<LinhaNotasMatrizVO>(), getPaginaAtual().getPerfilPorPagina(),
                                perfilRisco);
            }

        }


        Image image = new Image(MOVER, ConstantesImagens.IMG_PROXIMO);

        Label labelSinteseVigente = new Label("idRiscoSinteseVigente", riscoVigente);
        Label labelSinteseAnalise = new Label("idRiscoSinteseAnalise", riscoAnalise) {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisible(existeARCAnalisadoGrupoRisco);
            }
        };
        Label labelCampo = new Label("idLabelCampo", labelCampoString);

        wmcExibirRiscoLabel.addOrReplace(labelCampo);
        wmcExibirRiscoLabel.addOrReplace(labelSinteseVigente);
        wmcExibirRiscoLabel.addOrReplace(labelSinteseAnalise);
        wmcExibirRiscoLabel.addOrReplace(image);

        wmcExibirRiscoLabel.setVisibilityAllowed(existeGrupoRiscoMatriz);
        tabelaDados.addOrReplace(wmcExibirRiscoLabel);
    }

    private void addRiscoSinteseCombo() {
        ChoiceRenderer<ParametroNota> renderer = new ChoiceRenderer<ParametroNota>(DESCRICAO, ParametroNota.PROP_ID);
        List<ParametroNota> listaChoices = metodologia.getNotasArc();
        listaChoices = SisapsUtil.removerParametroNotaNA(listaChoices);
        listaChoices = SisapsUtil.removerParametroNotaSemDescricao(listaChoices);
        PropertyModel<ParametroNota> propertyModel = new PropertyModel<ParametroNota>(sintese, "risco");
        CustomDropDownChoice<ParametroNota> selectRisco =
                new CustomDropDownChoice<ParametroNota>("idRiscoSinteseCombo", "Selecione", propertyModel,
                        listaChoices, renderer);
        selectRisco.setOutputMarkupId(true);
        selectRisco.add(new OnChangeAjaxBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                mostrarAlerta(target);
            }
        });
        selectRisco.setMarkupId(criarComponentMarkupId(selectRisco.getId()));
        wmcExibirRiscoCombo.addOrReplace(selectRisco);

        String strConceitoSinteseVigente = "";
        final boolean existeConceitoSinteseVigente =
                sintese.getSinteseAnterior() != null && sintese.getSinteseAnterior().getRisco() != null;
        if (existeConceitoSinteseVigente) {
            strConceitoSinteseVigente = sintese.getSinteseAnterior().getRisco().getDescricao();
        }
        wmcExibirRiscoCombo.addOrReplace(new Label("idConceitoSinteseVigente", strConceitoSinteseVigente) {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisible(existeConceitoSinteseVigente);
            }
        });
        Image image = new Image(MOVER, ConstantesImagens.IMG_PROXIMO);
        wmcExibirRiscoCombo.addOrReplace(image);

        wmcExibirRiscoCombo.setVisibilityAllowed(idsParametrosGrupoRiscoMatriz == null
                || !idsParametrosGrupoRiscoMatriz.contains(sintese.getParametroGrupoRiscoControle().getPk()));
        tabelaDados.addOrReplace(wmcExibirRiscoCombo);
    }

    private void addSinteseVigente() {
        String strDataSinteseVigente = "";
        String strDescricaoSinteseVigente = "";
        if (sintese.getSinteseAnterior() != null) {
            String nomeOperadorDataHora = sintese.getSinteseAnterior().getNomeOperadorDataHora();
            if (nomeOperadorDataHora == null) {
                strDataSinteseVigente = "Atualizado em " + sintese.getSinteseAnterior().getDataFormatada() + PONTO;
            } else {
                strDataSinteseVigente = "Atualizado por " + nomeOperadorDataHora + PONTO;
            }
            if (sintese.getSinteseAnterior().getDescricaoSintese() != null) {
                strDescricaoSinteseVigente = sintese.getSinteseAnterior().getDescricaoSintese();
            }
        }
        tabelaDados.addOrReplace(new Label("idDataSinteseVigente", strDataSinteseVigente));
        tabelaDados.addOrReplace(new Label("idDescricaoSinteseVigente", strDescricaoSinteseVigente)
                .setEscapeModelStrings(false));
    }

    private void addNovaSintese() {
        String strDataNovaSintese = "";
        String nomeOperadorDataHora = sintese.getNomeOperadorDataHora();
        if (nomeOperadorDataHora == null) {
            strDataNovaSintese = "Sem alterações.";
        } else {
            strDataNovaSintese = "Última alteração salva " + nomeOperadorDataHora;
        }
        tabelaDados.addOrReplace(new Label("idDataNovaSintese", strDataNovaSintese));
        addTextAreaDescricaoNovaSintese();
        addBotaoSalvarTexto();
        addBotoesConcluir();
    }

    private void addTextAreaDescricaoNovaSintese() {
        final String componenteMarkupIdAlerta = alerta();

        String wicketId = "idDescricaoNovaSintese";
        final String componenteMarkupId = criarComponentMarkupId(wicketId);

        CKEditorTextArea<String> justificativa =
                new CKEditorTextArea<String>(wicketId, new PropertyModel<String>(sintese, "descricaoSintese")) {
                    @Override
                    protected String onKeyUpJS() {
                        return CKEditorUtils.jsAtualizarAlerta(componenteMarkupIdAlerta, true)
                                + atualizarBotoesVoltar();
                    }
                };
        justificativa.setMarkupId(componenteMarkupId);
        tabelaDados.addOrReplace(justificativa);
    }

    private String alerta() {
        String wicketIdAlerta = "idAlertaDadosNaoSalvosNovaSintese";
        final String componenteMarkupIdAlerta = criarComponentMarkupId(wicketIdAlerta);
        alertaDadosNaoSalvos = new Label(wicketIdAlerta, "Atenção texto não salvo.");
        alertaDadosNaoSalvos.setMarkupId(componenteMarkupIdAlerta);
        tabelaDados.addOrReplace(alertaDadosNaoSalvos);
        idsAlertas.add(alertaDadosNaoSalvos.getMarkupId());
        return componenteMarkupIdAlerta;
    }

    private void addBotaoSalvarTexto() {
        String wicketId = "bttSalvarNovaSintese";
        final String componenteMarkupId = criarComponentMarkupId(wicketId);
        AjaxSubmitLinkSisAps botaoSalvar = new AjaxSubmitLinkSisAps(wicketId) {
            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                sinteseDeRiscoMediator.salvarOuAtualizarSintese(sintese);
                mensagemSucessoSalvarConcluirSinteseVigente("Síntese salva com sucesso.");
                atualizarPagina(target);
                target.add(PainelGerenciarSintese.this);
            }
        };
        botaoSalvar.setMarkupId(componenteMarkupId);
        tabelaDados.addOrReplace(botaoSalvar);
    }

    private void addBotoesConcluir() {
        addBotaoConcluirSintese();
        addBotaoConcluirSintesePublicarARCs();
    }

    private void addBotaoConcluirSintese() {
        String wicketId = "bttConcluirNovaSintese";
        final String componenteMarkupId = criarComponentMarkupId(wicketId);
        AjaxSubmitLinkSisAps botaoConcluir = new AjaxSubmitLinkSisAps(wicketId) {
            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                sintese = sinteseDeRiscoMediator.concluirNovaSinteseMatrizVigente(sintese, ciclo, true);
                mensagemSucessoSalvarConcluirSinteseVigente("Síntese concluída com sucesso.");
                atualizar(target);
                target.add(PainelGerenciarSintese.this);
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                setEnabled(isBotaoConcluirHabilidado());
                setVisibilityAllowed(!existeARCAnalisadoGrupoRisco);
            }
        };
        botaoConcluir.setMarkupId(componenteMarkupId);
        tabelaDados.addOrReplace(botaoConcluir);
    }

    private boolean isBotaoConcluirHabilidado() {
        return sinteseDeRiscoMediator.botaoConcluirHabilitado(matriz, parametroGrupoRisco, existeGrupoRiscoMatriz,
                sintese.getSinteseAnterior(), sintese, arcExterno);
    }

    private void addBotaoConcluirSintesePublicarARCs() {
        String wicketId = "bttConcluirSintesePublicarARCs";
        final String componenteMarkupId = criarComponentMarkupId(wicketId);
        AjaxSubmitLinkSisAps botaoConcluirSintesePublicarARCs = new AjaxSubmitLinkSisAps(wicketId, true) {
            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                mensagemSucessoSalvarConcluirSinteseVigente(sinteseDeRiscoMediator
                        .concluirNovaSinteseMatrizVigenteEPublicarARCs(sintese, ciclo));
                atualizar(target);
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                setEnabled(isBotaoConcluirHabilidado());
                setVisibilityAllowed(existeARCAnalisadoGrupoRisco);
            }
        };
        botaoConcluirSintesePublicarARCs.setMarkupId(componenteMarkupId);
        tabelaDados.addOrReplace(botaoConcluirSintesePublicarARCs);
    }

    private void atualizar(AjaxRequestTarget target) {
        atualizarPagina(target);
        perfilRisco = PerfilRiscoMediator.get().obterPerfilRiscoAtual(ciclo.getPk());
        target.add(PainelGerenciarSintese.this);
    }

    private void mensagemSucessoSalvarConcluirSinteseVigente(String msg) {
        success(msg);
    }

    private String criarComponentMarkupId(String wicketId) {
        return wicketId + "_" + SisapsUtil.criarMarkupId(parametroGrupoRisco.getNomeAbreviado());
    }

    public List<String> getIdsAlertas() {
        return idsAlertas;
    }

    private void mostrarAlerta(AjaxRequestTarget target) {
        target.appendJavaScript(CKEditorUtils.jsAtualizarAlerta(alertaDadosNaoSalvos.getMarkupId(), true));
        target.appendJavaScript(atualizarBotoesVoltar());
    }

    private String atualizarBotoesVoltar() {
        return ((GerenciarNotaSintesePage) getPage()).jsAtualizarBotoesVoltar();
    }

    private void atualizarPagina(AjaxRequestTarget target) {
        GerenciarNotaSintesePage gerenciarNotaSintesePage = (GerenciarNotaSintesePage) getPage();
        gerenciarNotaSintesePage.atualizarBotoesAlerta(target, alertaDadosNaoSalvos.getMarkupId());
        gerenciarNotaSintesePage.atualizarPerfilRiscoAtual();
        gerenciarNotaSintesePage.atualizarPainelMatrizVigente(target);
        gerenciarNotaSintesePage.atualizarPainelMatrizEmAnalise(target);
    }

}