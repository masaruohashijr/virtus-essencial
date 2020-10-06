package br.gov.bcb.sisaps.web.page.dominio.gerenciaes;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Metodologia;
import br.gov.bcb.sisaps.src.dominio.ParametroPerspectiva;
import br.gov.bcb.sisaps.src.dominio.ParametroSituacao;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.PerspectivaES;
import br.gov.bcb.sisaps.src.dominio.SituacaoES;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.MetodologiaMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.src.mediator.PerspectivaESMediator;
import br.gov.bcb.sisaps.src.mediator.SituacaoESMediator;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import br.gov.bcb.sisaps.web.page.DefaultPage;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.botoes.AjaxSubmitLinkSisAps;
import br.gov.bcb.sisaps.web.page.componentes.drops.CustomDropDownChoice;
import br.gov.bcb.sisaps.web.page.componentes.textarea.CKEditorTextArea;
import br.gov.bcb.sisaps.web.page.componentes.util.CKEditorUtils;

public class PainelPerspectivaSituacao extends PainelSisAps {

    private static final String SITUACAO = "Situação";

    private static final String PERSPECTIVA = "Perspectiva";

    private static final String NOME = "nome";

    private static final String SITUACAO_UNDERLINE = "situacao_";

    private static final String PERSPECTIVA_UNDERLINE = "perspectiva_";

    private static final String ID_CONCEITO_NOVO = "idConceitoNovo";

    private static final String SELECIONE = "Selecione";

    private static final String PROP_DESCRICAO = "descricao";

    private static final String ULTIMA_ALTERACAO_SALVA = "Última alteração salva ";

    private static final String ATUALIZADO_POR = "Atualizado por ";

    private static final String PONTO = ".";

    private static final String ENCAMINHADO_POR = "Encaminhado por ";

    private final WebMarkupContainer wmcJustificativa = new WebMarkupContainer("wmcJustificativa");

    @SpringBean
    private PerspectivaESMediator perspectivaESMediator;
    @SpringBean
    private SituacaoESMediator situacaoESMediator;
    @SpringBean
    private MetodologiaMediator metodologiaMediator;
    @SpringBean
    private CicloMediator cicloMediator;

    private final Ciclo ciclo;
    private final boolean isPerspectiva;
    private PerspectivaES perspectivaESVigente;
    private PerspectivaES perspectivaES;
    private SituacaoES situacaoESVigente;
    private SituacaoES situacaoES;
    private final Metodologia metodologia;
    private String markupIdAlerta;
    private WebMarkupContainer painelSupervisor;
    private WebMarkupContainer painelGerente;
    private final PerfilRisco perfilRisco;

    private CustomDropDownChoice<?> selectConceito;

    public PainelPerspectivaSituacao(String id, Integer cicloId, boolean isPespectiva, PerfilRisco perfilRisco) {
        super(id);
        this.perfilRisco = perfilRisco;
        this.ciclo = cicloMediator.buscarCicloPorPK(cicloId);
        this.isPerspectiva = isPespectiva;
        metodologia = metodologiaMediator.buscarMetodologiaPorPK(ciclo.getMetodologia().getPk());
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        if (isPerspectiva) {
            criarPerspectivaES();
        } else {
            criarSituacaoES();
        }
        addComponentes();
    }

    private void criarPerspectivaES() {
        perspectivaESVigente =
                perspectivaESMediator.getUltimaPerspectiva(perfilRisco, true, getPaginaAtual().getPerfilPorPagina());
        perspectivaES =
                getPaginaAtual().perfilGerente(getPaginaAtual()) ? perspectivaESMediator
                        .getPerspectivaESPendencia(ciclo) : perspectivaESMediator.getPerspectivaESSemPerfilRisco(ciclo);
        if (perspectivaES == null) {
            setarDadosNovaPerspectiva();
        }
    }

    private void setarDadosNovaPerspectiva() {
        PerspectivaES vigente =  perspectivaESMediator.getPerspectivaESPorPerfil(perfilRisco.getPk());
        perspectivaES = new PerspectivaES();
        perspectivaES.setCiclo(ciclo);
        perspectivaES.setPerspectivaESAnterior(vigente);
        if (vigente != null) {
            perspectivaES.setDescricao(vigente.getDescricao());
            perspectivaES.setParametroPerspectiva(vigente.getParametroPerspectiva());
        }
    }

    private void criarSituacaoES() {
        situacaoESVigente = situacaoESMediator.getSituacaoESPorPerfil(perfilRisco.getPk());
        situacaoES =
                getPaginaAtual().perfilGerente(getPaginaAtual()) ? situacaoESMediator.getSituacaoESPendencia(ciclo)
                        : situacaoESMediator.getSituacaoESSemPerfilRisco(ciclo);
        if (situacaoES == null) {
            setarDadosNovaSituacao();
        }

    }

    private void setarDadosNovaSituacao() {
        situacaoES = new SituacaoES();
        situacaoES.setCiclo(ciclo);
        situacaoES.setSituacaoESAnterior(situacaoESVigente);
        if (situacaoESVigente != null) {
            situacaoES.setDescricao(situacaoESVigente.getDescricao());
            situacaoES.setParametroSituacao(situacaoESVigente.getParametroSituacao());
        }
    }

    private void addComponentes() {
        WebMarkupContainer tabela = new WebMarkupContainer("tDados");
        String titulo = null;
        if (isPerspectiva) {
            titulo = PERSPECTIVA;
            tabela.setMarkupId(PERSPECTIVA_UNDERLINE + tabela.getId());
        } else {
            titulo = SITUACAO;
            tabela.setMarkupId(SITUACAO_UNDERLINE + tabela.getId());
        }
        addOrReplace(tabela);
        tabela.addOrReplace(new Label("idTitulo", titulo));

        addDadosVigentes(tabela);
        addPainelSupervisor(tabela);
        addPainelGerente(tabela);

    }

    private void addPainelGerente(WebMarkupContainer tabela) {
        painelGerente = new WebMarkupContainer("paineilGerente") {
            @Override
            public boolean isVisible() {
                return getPaginaAtual().perfilGerente(getPaginaAtual()) && possuiPendencia();
            }

            private boolean possuiPendencia() {
                return situacaoES != null && SimNaoEnum.SIM.equals(situacaoES.getPendente()) || perspectivaES != null
                        && SimNaoEnum.SIM.equals(perspectivaES.getPendente());
            }
        };
        addConfirmarNovosDados(painelGerente);
        painelGerente.add(new Label("idConceitoEncaminhado",
                situacaoES != null ? situacaoES.getParametroSituacao() == null ? "" : situacaoES.getParametroSituacao()
                        .getDescricao() : perspectivaES.getParametroPerspectiva() == null ? "" : perspectivaES
                        .getParametroPerspectiva().getDescricao()));

        painelGerente.add(new Label("idDescricaoEncaminhado", situacaoES != null ? situacaoES.getDescricao()
                : perspectivaES.getDescricao()).setEscapeModelStrings(false));
        tabela.addOrReplace(painelGerente);
    }

    private void addConfirmarNovosDados(WebMarkupContainer painelGerente) {
        String campo = "";
        String strDataEncaminhamento = "";
        String nomeOperadorDataHora = "";
        if (isPerspectiva) {
            campo = "Nova perspectiva";
            nomeOperadorDataHora = perspectivaES.getNomeOperadorEncaminhamentoDataHora();
            if (nomeOperadorDataHora != null) {
                strDataEncaminhamento = ENCAMINHADO_POR + nomeOperadorDataHora + PONTO;
            }
        } else {
            campo = "Nova situação";
            nomeOperadorDataHora = situacaoES.getNomeOperadorEncaminhamentoDataHora();
            if (nomeOperadorDataHora != null) {
                strDataEncaminhamento = ENCAMINHADO_POR + nomeOperadorDataHora + PONTO;
            }
        }
        painelGerente.addOrReplace(new Label("idCampoEncaminhado", campo));
        painelGerente.addOrReplace(new Label("idDataEncaminhado", strDataEncaminhamento));

        addBotaoConfirmarNovaPerspectivaES(painelGerente);
        addBotaoConfirmarNovaSituacaoES(painelGerente);
    }

    private void addPainelSupervisor(WebMarkupContainer tabela) {
        painelSupervisor = new WebMarkupContainer("paineilSupervisor") {
            @Override
            public boolean isVisible() {
                return getPaginaAtual().perfilSupervisor(getPaginaAtual());
            }
        };
        addNovosDados(painelSupervisor);
        tabela.addOrReplace(painelSupervisor);
    }

    private void addDadosVigentes(WebMarkupContainer tabela) {
        String campoVigente = "";
        String strDataVigente = "";
        String strDescricaoVigente = "";
        String strConceitoVigente = "";
        if (isPerspectiva) {
            campoVigente = PERSPECTIVA;
            if (perspectivaESVigente != null) {
                strDataVigente =
                        ATUALIZADO_POR + perspectivaESVigente.getNomeOperadorEncaminhamentoDataHoraPublicacao() + PONTO;

                if (perspectivaESVigente.getDescricao() != null) {
                    strDescricaoVigente = perspectivaESVigente.getDescricao();
                }
                if (perspectivaESVigente.getParametroPerspectiva() != null) {
                    strConceitoVigente = perspectivaESVigente.getParametroPerspectiva().getNome();
                    if (perspectivaESVigente.getPk() == null) {
                        strConceitoVigente += " (Corec)";
                    }
                }
            }
        } else {
            campoVigente = SITUACAO;
            if (situacaoESVigente != null) {
                strDataVigente =
                        ATUALIZADO_POR + situacaoESVigente.getNomeOperadorEncaminhamentoDataHoraPublicacao() + PONTO;
                if (situacaoESVigente.getDescricao() != null) {
                    strDescricaoVigente = situacaoESVigente.getDescricao();
                }
                if (situacaoESVigente.getParametroSituacao() != null) {
                    strConceitoVigente = situacaoESVigente.getParametroSituacao().getNome();
                }
            }
        }
        tabela.addOrReplace(new Label("idCampoVigente", campoVigente));
        tabela.addOrReplace(new Label("idDataVigente", strDataVigente));
        tabela.addOrReplace(new Label("idConceitoVigente", strConceitoVigente));
        tabela.addOrReplace(new Label("idDescricaoVigente", strDescricaoVigente).setEscapeModelStrings(false));
    }

    private void addNovosDados(WebMarkupContainer tabela) {
        String campo = "";
        String strDataNova = "";
        String nomeOperadorDataHora = "";
        if (isPerspectiva) {
            campo = "perspectiva";
            nomeOperadorDataHora = perspectivaES.getNomeOperadorDataHora();
            if (perspectivaES.getPendente() != null && !SimNaoEnum.NAO.equals(perspectivaES.getPendente())) {
                strDataNova = ENCAMINHADO_POR + perspectivaES.getNomeOperadorEncaminhamentoDataHora() + PONTO;
            } else if (nomeOperadorDataHora != null) {
                strDataNova = ULTIMA_ALTERACAO_SALVA + nomeOperadorDataHora + PONTO;
            }
        } else {
            campo = "situação";
            nomeOperadorDataHora = situacaoES.getNomeOperadorDataHora();
            if (situacaoES.getPendente() != null  && !SimNaoEnum.NAO.equals(situacaoES.getPendente())) {
                strDataNova = ENCAMINHADO_POR + situacaoES.getNomeOperadorEncaminhamentoDataHora() + PONTO;
            } else if (nomeOperadorDataHora != null) {
                strDataNova = ULTIMA_ALTERACAO_SALVA + nomeOperadorDataHora + PONTO;
            }
        }
        tabela.addOrReplace(new Label("idCampo", campo));
        tabela.addOrReplace(new Label("idDataNova", strDataNova));

        addComboConceitoNovo(tabela);
        addTextAreaDescricaoNova(tabela);
        addBotaoSalvar(tabela);
        addBotaoEncaminharNovaSituacaoES(tabela);
        addBotaoEncaminharNovaPerspectivaES(tabela);
    }

    private void addComboConceitoNovo(WebMarkupContainer tabela) {
        selectConceito = null;
        if (isPerspectiva) {
            ChoiceRenderer<ParametroPerspectiva> render =
                    new ChoiceRenderer<ParametroPerspectiva>(NOME, ParametroPerspectiva.PROP_ID);
            List<ParametroPerspectiva> lista = metodologia.getParametrosPerspectiva();
            PropertyModel<ParametroPerspectiva> propertyConceito =
                    new PropertyModel<ParametroPerspectiva>(perspectivaES, "parametroPerspectiva");
            selectConceito =
                    new CustomDropDownChoice<ParametroPerspectiva>(ID_CONCEITO_NOVO, SELECIONE, propertyConceito,
                            lista, render);
            selectConceito.setMarkupId(PERSPECTIVA_UNDERLINE + selectConceito.getId());
        } else {
            ChoiceRenderer<ParametroSituacao> render =
                    new ChoiceRenderer<ParametroSituacao>(NOME, ParametroSituacao.PROP_ID);
            List<ParametroSituacao> lista = metodologia.getParametrosSituacao();
            PropertyModel<ParametroSituacao> propertyConceito =
                    new PropertyModel<ParametroSituacao>(situacaoES, "parametroSituacao");
            selectConceito =
                    new CustomDropDownChoice<ParametroSituacao>(ID_CONCEITO_NOVO, SELECIONE, propertyConceito, lista,
                            render);
            selectConceito.setMarkupId(SITUACAO_UNDERLINE + selectConceito.getId());
            if (situacaoES.getPk() == null && situacaoES.getParametroSituacao() == null) {
                selectConceito.setDefaultModelObject(extrairSituacaoNormal(lista));
            }
        }
     
        tabela.addOrReplace(selectConceito);
    }

   
    private ParametroSituacao extrairSituacaoNormal(List<ParametroSituacao> lista) {
        for (ParametroSituacao situacao : lista) {
            if ("normal".equalsIgnoreCase(situacao.getNome())) {
                return situacao;
            }
        }
        return null;
    }

    private void addTextAreaDescricaoNova(WebMarkupContainer tabela) {
        PropertyModel<String> propertyDescricao = null;
        String entidade = null;
        if (isPerspectiva) {
            propertyDescricao = new PropertyModel<String>(perspectivaES, PROP_DESCRICAO);
            entidade = PERSPECTIVA_UNDERLINE;
        } else {
            propertyDescricao = new PropertyModel<String>(situacaoES, PROP_DESCRICAO);
            entidade = SITUACAO_UNDERLINE;
        }
        final Label alerta = new Label("idAlertaDadosNaoSalvos", "Atenção informações não salvas.");
        setMarkupIdAlerta(entidade + alerta.getId());
        alerta.setMarkupId(getMarkupIdAlerta());
        tabela.addOrReplace(alerta);
        CKEditorTextArea<String> descricao = new CKEditorTextArea<String>("idDescricaoNova", propertyDescricao) {
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
        tabela.add(wmcJustificativa);
    }

    private void addBotaoSalvar(WebMarkupContainer tabela) {
        AjaxSubmitLinkSisAps botaoSalvar = new AjaxSubmitLinkSisAps("bttSalvar", true) {
            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                if (isPerspectiva) {
                    perspectivaESMediator.salvarNovaPerspectiva(perspectivaES);
                } else {
                    situacaoESMediator.salvarNovaSituacao(situacaoES);
                }
                mensagemSucessoSalvar();
                atualizarPagina(target);
                target.add(PainelPerspectivaSituacao.this);
            }
        };
        if (isPerspectiva) {
            botaoSalvar.setMarkupId(PERSPECTIVA_UNDERLINE + botaoSalvar.getId());
        } else {
            botaoSalvar.setMarkupId(SITUACAO_UNDERLINE + botaoSalvar.getId());
        }
        tabela.addOrReplace(botaoSalvar);
    }

    private void mensagemSucessoSalvar() {
        if (isPerspectiva) {
            success("Perspectiva salva com sucesso.");
        } else {
            success("Situação salva com sucesso.");
        }
    }

    private void addBotaoEncaminharNovaPerspectivaES(WebMarkupContainer tabela) {
        tabela.addOrReplace(new AjaxSubmitLinkSisAps("bttEncaminharNovaPerspectiva", true) {
            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                mensagemSucessoConfirmar(perspectivaESMediator.encaminharNovaPerspectiva(perspectivaES));
                atualizarPagina(target);
                target.add(PainelPerspectivaSituacao.this);
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(isPerspectiva);
                setEnabled(isPerspectiva && perspectivaES.getPk() != null
                        && (perspectivaES.getPendente() == null || SimNaoEnum.NAO.equals(perspectivaES.getPendente())));
            }
        });
    }

    private void addBotaoEncaminharNovaSituacaoES(WebMarkupContainer tabela) {
        tabela.addOrReplace(new AjaxSubmitLinkSisAps("bttEncaminharNovaSituacao", true) {
            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                mensagemSucessoConfirmar(situacaoESMediator.encaminharNovaSituacao(situacaoES));
                atualizarPagina(target);
                target.add(PainelPerspectivaSituacao.this);
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(!isPerspectiva);
                setEnabled(!isPerspectiva && situacaoES.getPk() != null
                        && (situacaoES.getPendente() == null || SimNaoEnum.NAO.equals(situacaoES.getPendente())));
            }
        });
    }

    private void addBotaoConfirmarNovaPerspectivaES(WebMarkupContainer tabela) {
        tabela.addOrReplace(new AjaxSubmitLinkSisAps("bttConcluirNovaPerspectiva", true) {
            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                DefaultPage paginaAnterior = getPaginaAtual().getPaginaAnterior();
                String sucess = perspectivaESMediator.confirmarNovaPerspectiva(perspectivaES);
                getPaginaAtual().avancarParaNovaPagina(
                        new GerenciarES(ciclo.getPk(), PerfilRiscoMediator.get().obterPerfilRiscoAtual(ciclo.getPk()),
                                sucess),
                        paginaAnterior == null ? getPaginaAtual() : paginaAnterior);
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(isPerspectiva);
                setEnabled(isPerspectiva && perspectivaES.getPk() != null);
            }
        });
    }

    private void addBotaoConfirmarNovaSituacaoES(WebMarkupContainer tabela) {
        tabela.addOrReplace(new AjaxSubmitLinkSisAps("bttConcluirNovaSituacao", true) {
            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                DefaultPage paginaAnterior = getPaginaAtual().getPaginaAnterior();
                String sucess = situacaoESMediator.confirmarNovaSituacao(situacaoES);
                getPaginaAtual()
                        .avancarParaNovaPagina(
                                new GerenciarES(ciclo.getPk(),
                                        PerfilRiscoMediator.get().obterPerfilRiscoAtual(ciclo.getPk()), sucess),
                                paginaAnterior == null ? getPaginaAtual() : paginaAnterior);
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(!isPerspectiva);
                setEnabled(!isPerspectiva && situacaoES.getPk() != null);
            }
        });
    }

    private void mensagemSucessoConfirmar(String texto) {
        success(texto);
    }

    private void atualizarPagina(AjaxRequestTarget target) {
        ((GerenciarES) getPage()).atualizarBotoesAlerta(target, getMarkupIdAlerta());
    }

    private String atualizarBotoesVoltar() {
        return ((GerenciarES) getPage()).jsAtualizarBotoesVoltar();
    }


    public String getMarkupIdAlerta() {
        return markupIdAlerta;
    }

    public void setMarkupIdAlerta(String markupIdAlerta) {
        this.markupIdAlerta = markupIdAlerta;
    }

}
