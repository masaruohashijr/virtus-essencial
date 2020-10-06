package br.gov.bcb.sisaps.web.page.dominio.gerenciarnotasintese.aqt.painel;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.SinteseDeRiscoAQT;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoAQTEnum;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.SinteseDeRiscoAQTMediator;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.botoes.AjaxSubmitLinkSisAps;
import br.gov.bcb.sisaps.web.page.componentes.textarea.CKEditorTextArea;
import br.gov.bcb.sisaps.web.page.componentes.util.CKEditorUtils;
import br.gov.bcb.sisaps.web.page.componentes.util.ConstantesImagens;
import br.gov.bcb.sisaps.web.page.dominio.gerenciarnotasintese.aqt.GerenciarNotaSinteseAQTPage;

public class PainelGerenciarSinteseAQT extends PainelSisAps {

    @SpringBean
    private SinteseDeRiscoAQTMediator sinteseDeRiscoMediator;
    @SpringBean
    private final List<String> idsAlertas;
    private final Ciclo ciclo;

    private final ParametroAQT parametroAQT;
    private SinteseDeRiscoAQT sintese;
    private WebMarkupContainer tabelaDados;
    private Label alertaDadosNaoSalvos;
    private PerfilRisco perfilRisco;
    private AjaxSubmitLinkSisAps botaoConcluir;
    private SinteseDeRiscoAQT sintevigente;

    public PainelGerenciarSinteseAQT(String id, PerfilRisco perfilRisco, ParametroAQT parametroGrupoRisco,
            List<String> idsAlertas) {
        super(id);
        this.perfilRisco = perfilRisco;
        this.ciclo = perfilRisco.getCiclo();
        this.parametroAQT = parametroGrupoRisco;
        this.idsAlertas = idsAlertas;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        tabelaDados = new WebMarkupContainer("tDadosSintese");
        tabelaDados.setMarkupId("t" + getMarkupId());
        addOrReplace(tabelaDados);
        addUltimaSinteseParametroGrupoRisco();
        addCamposNota();
    }

    private void addCamposNota() {
        AnaliseQuantitativaAQT analiseRascunho =
                AnaliseQuantitativaAQTMediator.get().buscarAQTRascunhoParametroECiclo(parametroAQT, ciclo);
        Image image = new Image("mover", ConstantesImagens.IMG_PROXIMO);
        image.setVisibilityAllowed(analiseRascunho != null
                && EstadoAQTEnum.ANALISADO.equals(analiseRascunho.getEstado()));
        tabelaDados.addOrReplace(image);
        AnaliseQuantitativaAQT anefVigente =
                AnaliseQuantitativaAQTMediator.get().buscarAQTVigentePorPerfil(parametroAQT, ciclo, perfilRisco);
        Label notaVigente =
                new Label("idConceitoSinteseVigente", anefVigente != null ? anefVigente.getNotaVigenteDescricaoValor()
                        : "");
        tabelaDados.addOrReplace(notaVigente);
        Label notaAnalisado =
                new Label("idConceitoSinteseAnalisado", analiseRascunho == null ? ""
                        : analiseRascunho.getNotaVigenteDescricaoValor());
        tabelaDados.addOrReplace(notaAnalisado);
        notaAnalisado.setVisibilityAllowed(analiseRascunho != null
                && EstadoAQTEnum.ANALISADO.equals(analiseRascunho.getEstado()));
    }

    private void addUltimaSinteseParametroGrupoRisco() {
        Label titulo = new Label("idTituloSintese", parametroAQT.getDescricao());
        titulo.setMarkupId(titulo.getId() + SisapsUtil.criarMarkupId(parametroAQT.getDescricao()));
        tabelaDados.addOrReplace(titulo);

        sintese = sinteseDeRiscoMediator.getUltimaSinteseParametroAQTEdicao(parametroAQT, perfilRisco.getCiclo());
        if (sintese == null) {
            sintese = new SinteseDeRiscoAQT();
            sintese.setParametroAQT(parametroAQT);
            sintese.setCiclo(perfilRisco.getCiclo());
        } else if (sintese.getVersaoPerfilRisco() != null) {
            sintese = new SinteseDeRiscoAQT();
            sintese.setParametroAQT(parametroAQT);
            sintese.setCiclo(perfilRisco.getCiclo());
        }

        addSinteseVigente();
        addNovaSintese();
    }

    private void addSinteseVigente() {
        sintevigente = sinteseDeRiscoMediator.getUltimaSinteseVigente(parametroAQT, ciclo);
        String strDataSinteseVigente = "";
        String strDescricaoSinteseVigente = "";
        if (sintevigente != null && sintevigente.getVersaoPerfilRisco() != null) {
            strDataSinteseVigente = "Atualizado por " + sintevigente.getNomeOperadorDataHora() + ".";
            if (sintevigente.getJustificativa() != null) {
                strDescricaoSinteseVigente = sintevigente.getJustificativa();
            }
        }
        tabelaDados.addOrReplace(new Label("idDataSinteseVigente", strDataSinteseVigente));
        tabelaDados.addOrReplace(new Label("idDescricaoSinteseVigente", strDescricaoSinteseVigente)
                .setEscapeModelStrings(false));
    }

    private void addNovaSintese() {
        String strDataNovaSintese = "";
        String nomeOperadorDataHora = sintese.getNomeOperadorDataHora();
        if (nomeOperadorDataHora == null || sinteseDeRiscoMediator.sinteseRascunhoIgualVigente(sintevigente, sintese)) {
            strDataNovaSintese = "Sem alterações salvas.";
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
                new CKEditorTextArea<String>(wicketId, new PropertyModel<String>(sintese, "justificativa")) {
                    @Override
                    protected String onKeyUpJS() {
                        return CKEditorUtils.jsAtualizarAlerta(componenteMarkupIdAlerta, true)
                                + atualizarBotoesVoltar();
                    }

                };
        justificativa.setMarkupId(componenteMarkupId);
        // justificativa.add(new DefaultFocusBehavious());
        tabelaDados.addOrReplace(justificativa);
    }

    private String alerta() {
        String wicketIdAlerta = "idAlertaDadosNaoSalvosNovaSintese";
        final String componenteMarkupIdAlerta = criarComponentMarkupId(wicketIdAlerta);
        alertaDadosNaoSalvos = new Label(wicketIdAlerta, "Atenção informações não salvas.");
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
                mensagemSucessoSalvarConcluirSinteseVigente(sinteseDeRiscoMediator.salvarOuAtualizarSintese(sintese));
                target.add(PainelGerenciarSinteseAQT.this);
            }
        };
        botaoSalvar.setMarkupId(componenteMarkupId);
        tabelaDados.addOrReplace(botaoSalvar);
    }

    private void addBotoesConcluir() {
        addBotaoConcluirSintese();

    }

    private void addBotaoConcluirSintese() {
        String wicketId = "bttConcluirSintese";
        final String componenteMarkupId = criarComponentMarkupId(wicketId);
        botaoConcluir = new AjaxSubmitLinkSisAps(wicketId, true) {
            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                mensagemSucessoSalvarConcluirSinteseVigente(sinteseDeRiscoMediator.concluirNovaSintesAQT(parametroAQT,
                        ciclo));
                atualizar(target);
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                setEnabled(isBotaoConcluirHabilidado());

            }
        };
        String tituloBotaoConcluirSintese = sinteseDeRiscoMediator.tituloBotaoConcluirSintese(parametroAQT, ciclo);
        botaoConcluir.add(new AttributeAppender("value", tituloBotaoConcluirSintese));
        botaoConcluir.add(new AttributeAppender("style", "Confirmar síntese vigente sem alteração e publicar ANEF"
                .equals(tituloBotaoConcluirSintese) ? "width: 350px;" : "width: 300px;"));
        botaoConcluir.setMarkupId(componenteMarkupId);
        botaoConcluir.setOutputMarkupId(true);
        tabelaDados.addOrReplace(botaoConcluir);
    }

    private boolean isBotaoConcluirHabilidado() {
        SinteseDeRiscoAQT vigente = sinteseDeRiscoMediator.getUltimaSinteseVigente(parametroAQT, ciclo);

        SinteseDeRiscoAQT edicao = sinteseDeRiscoMediator.getUltimaSinteseParametroAQTEdicao(parametroAQT, ciclo);
        AnaliseQuantitativaAQT analiseRascunho =
                AnaliseQuantitativaAQTMediator.get().buscarAQTRascunhoParametroECiclo(parametroAQT, ciclo);

        return sinteseDeRiscoMediator.botaoConcluirHabilitado(analiseRascunho, vigente, edicao);
    }

    private void atualizar(AjaxRequestTarget target) {
        atualizarPagina(target);
        target.add(botaoConcluir);
        target.add(PainelGerenciarSinteseAQT.this);
    }

    private void mensagemSucessoSalvarConcluirSinteseVigente(String msg) {
        success(msg);
    }

    private String criarComponentMarkupId(String wicketId) {
        return wicketId + "_" + SisapsUtil.criarMarkupId(parametroAQT.getDescricao().replace("/", ""));
    }

    public List<String> getIdsAlertas() {
        return idsAlertas;
    }

    private String atualizarBotoesVoltar() {
        return ((GerenciarNotaSinteseAQTPage) getPage()).jsAtualizarBotoesVoltar();
    }

    public void atualizarPerfilRiscoAtual() {
        this.perfilRisco = PerfilRiscoMediator.get().obterPerfilRiscoAtual(ciclo.getPk());
    }

    private void atualizarPagina(AjaxRequestTarget target) {
        atualizarPerfilRiscoAtual();
        GerenciarNotaSinteseAQTPage gerenciarNotaSintesePage = (GerenciarNotaSinteseAQTPage) getPage();
        gerenciarNotaSintesePage.atualizarPerfilRiscoAtual();
        gerenciarNotaSintesePage.atualizarBotoesAlerta(target, alertaDadosNaoSalvos.getMarkupId());
        gerenciarNotaSintesePage.atualizarPainelNovoQuadroAQT(target);
        gerenciarNotaSintesePage.atualizaPainelQuadroVigenteAQT(target);
        gerenciarNotaSintesePage.atualizaPainelListaAnefs(target);
    }

}