package br.gov.bcb.sisaps.web.page.dominio.perfilderisco;

import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoMatrizEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.mediator.BloqueioESMediator;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.LogOperacaoMediator;
import br.gov.bcb.sisaps.src.mediator.MatrizCicloMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.src.mediator.RegraPerfilAcessoMediator;
import br.gov.bcb.sisaps.src.validacao.RegraGestaoARC;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.web.page.DefaultPage;
import br.gov.bcb.sisaps.web.page.componentes.botoes.CustomButton;
import br.gov.bcb.sisaps.web.page.componentes.botoes.CustomButtonSupervisor;
import br.gov.bcb.sisaps.web.page.dominio.ajustenotafinal.AjusteNotaFinal;
import br.gov.bcb.sisaps.web.page.dominio.analisequantitativa.GerenciarQuadroPosicaoFinanceira;
import br.gov.bcb.sisaps.web.page.dominio.analisequantitativa.PainelAnaliseQuantitativa;
import br.gov.bcb.sisaps.web.page.dominio.aqt.consulta.PainelResumoSinteseAQT;
import br.gov.bcb.sisaps.web.page.dominio.aqt.consulta.PainelSinteseAQTPerfilRisco;
import br.gov.bcb.sisaps.web.page.dominio.ciclo.painel.PainelDadosMatrizPercentual;
import br.gov.bcb.sisaps.web.page.dominio.ciclo.painel.PainelResumoCiclo;
import br.gov.bcb.sisaps.web.page.dominio.gerenciaes.GerenciarES;
import br.gov.bcb.sisaps.web.page.dominio.gerenciararc.GerenciarArc;
import br.gov.bcb.sisaps.web.page.dominio.gerenciarnotasintese.GerenciarNotaSintesePage;
import br.gov.bcb.sisaps.web.page.dominio.gerenciarnotasintese.aqt.GerenciarNotaSinteseAQTPage;
import br.gov.bcb.sisaps.web.page.dominio.matriz.EdicaoMatrizPage;
import br.gov.bcb.sisaps.web.page.dominio.matriz.SucessoEdicaoMatrizPage;
import br.gov.bcb.sisaps.web.page.dominio.perfilderisco.painel.PainelAtividadesCiclo;
import br.gov.bcb.sisaps.web.page.dominio.perfilderisco.painel.PainelCorec;
import br.gov.bcb.sisaps.web.page.dominio.perfilderisco.painel.PainelDetalhesES;
import br.gov.bcb.sisaps.web.page.dominio.perfilderisco.painel.PainelFiltroPerfilDeRisco;
import br.gov.bcb.sisaps.web.page.dominio.perfilderisco.painel.PainelInformacoesCicloMigrado;
import br.gov.bcb.sisaps.web.page.dominio.perfilderisco.painel.PainelLinkOutrasInformacoes;
import br.gov.bcb.sisaps.web.page.dominio.perfilderisco.painel.PainelSinteseDeRisco;
import br.gov.bcb.sisaps.web.page.painelConsulta.PerfilConsultaPage;

public abstract class AbstractPerfilDeRiscoPage extends DefaultPage {

    protected static final String ANALISE_QUANTITATIVA_PANEL = "analiseQuantitativaPanel";

    protected static final String MATRIZ_VIGENTE_PANEL = "matrizVigentePanel";
    
    protected Ciclo ciclo;
    protected PerfilRisco perfilRiscoAtual;
    protected PainelFiltroPerfilDeRisco painelFiltroPerfilDeRisco;
    protected PainelResumoCiclo painelResumoCiclo;
    protected PainelDetalhesES painelDetalhesES;
    protected PainelDadosMatrizPercentual painelMatrizVigente;
    protected PainelSinteseDeRisco painelSintese;

    protected CustomButton botaoGerenciarNotasSinteses;

    protected CustomButton botaoAjusteNotafinal;

    protected CustomButton botaoGerenciarArcs;

    protected CustomButton botaoEditarMatriz;

    protected CustomButton botaoGerenciarDetalhesES;

    protected boolean isOrigemPainelConsulta;
    
    protected PainelAtividadesCiclo painelAtividadesCiclo;

    protected CustomButtonSupervisor botaoGerenciarQuadroPosicaoFinanceira;

    protected CustomButtonSupervisor botaoGerenciarAnaliseEconomicoFinanceira;

    protected PainelAnaliseQuantitativa painelAnaliseQuantitativa;

    protected WebMarkupContainer wmcExibirMensagem = new WebMarkupContainer("mensagemSucessoConclusaoARC");

    protected boolean exibirMensagem;

    protected PainelInformacoesCicloMigrado painelInformacoesCicloMigrado;

    protected WebMarkupContainer tituloMatriz;

    protected WebMarkupContainer tabelaAnaliseQuantitativa;

    protected PainelLinkOutrasInformacoes painelLinkOutrasInformacoes;

    protected PainelSinteseAQTPerfilRisco painelSinteseAQT;

    protected PainelResumoSinteseAQT painelResumoSinteseAQT;

    protected PainelCorec painelCorec;
    
    protected boolean acessoPerfil;

    protected boolean acessoPorParametro;

    protected Integer idPerfil;

    private CustomButton botaoBloquearDesbloquear;

    private PainelMenuLink painelMenuLink;

    protected void addMensagem() {
        AjaxSubmitLink link = new AjaxSubmitLink("linkPaginaGerenciarNotasSinteses") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                avancarParaNovaPagina(new GerenciarNotaSintesePage(perfilRiscoAtual));
            }
        };
        link.setMarkupId(link.getId());
        link.setOutputMarkupId(true);
        wmcExibirMensagem.addOrReplace(link);
        wmcExibirMensagem.setVisible(exibirMensagem);
    }

    protected void addPainelSinteseAQT(Form<?> form) {
        painelSinteseAQT =
                new PainelSinteseAQTPerfilRisco("grupoDeSintesesAQT", perfilRiscoAtual, getPerfilPorPagina(),
                        "Componentes", false, false, true) {
                    @Override
                    protected void onConfigure() {
                        super.onConfigure();
                        setVisibilityAllowed(!isPerfilRiscoCicloMigrado());
                    }
                };
        painelSinteseAQT.setOutputMarkupPlaceholderTag(true);
        form.addOrReplace(painelSinteseAQT);

        painelResumoSinteseAQT =
                new PainelResumoSinteseAQT("grupoDeResumoSintesesAQT", perfilRiscoAtual, true, getPerfilPorPagina(),
                        false) {
                    @Override
                    protected void onConfigure() {
                        super.onConfigure();
                        setVisibilityAllowed(!isPerfilRiscoCicloMigrado());
                    }
                };

        painelResumoSinteseAQT.setOutputMarkupPlaceholderTag(true);
        form.addOrReplace(painelResumoSinteseAQT);

    }

    protected void addPainelOutrasInformacoes(Form<?> form) {
        painelLinkOutrasInformacoes =
                new PainelLinkOutrasInformacoes("idPainelAnexos", perfilRiscoAtual, painelFiltroPerfilDeRisco) {
                    @Override
                    protected void onConfigure() {
                        super.onConfigure();
                        setVisibilityAllowed(!isPerfilRiscoCicloMigrado());
                    }
                };
        painelLinkOutrasInformacoes.setOutputMarkupPlaceholderTag(true);
        form.addOrReplace(painelLinkOutrasInformacoes);
    }

    protected void addPainelFiltroPerfilDeRisco(Form<?> form) {
        painelFiltroPerfilDeRisco =
                new PainelFiltroPerfilDeRisco("filtroPerfilRiscoPanel", ciclo.getEntidadeSupervisionavel(),
                        isOrigemPainelConsulta);
        form.addOrReplace(painelFiltroPerfilDeRisco);
    }

    protected void addPainelResumoCiclo(Form<?> form) {
        painelResumoCiclo = new PainelResumoCiclo("resumoCicloPanel", ciclo.getPk(), perfilRiscoAtual);
        painelResumoCiclo.setMarkupId(painelResumoCiclo.getId());
        form.addOrReplace(painelResumoCiclo);
    }

    protected void addPainelDetalhesES(Form<?> form) {
        painelDetalhesES = new PainelDetalhesES("detalhesESPanel", perfilRiscoAtual) {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(!isPerfilRiscoCicloMigrado());
            }
        };
        painelDetalhesES.setOutputMarkupPlaceholderTag(true);
        form.addOrReplace(painelDetalhesES);
    }

    protected boolean isPerfilRiscoCicloMigrado() {
        return SisapsUtil.isCicloMigrado(painelFiltroPerfilDeRisco.getPerfilRiscoSelecionado().getCiclo());
    }

    protected void addPainelMatrizVigente(Form<?> form) {
        tituloMatriz = new WebMarkupContainer("tTituloMatriz") {
            @Override
            protected void onConfigure() {
                setVisibilityAllowed(!isPerfilRiscoCicloMigrado());
                super.onConfigure();
            }
        };
        tituloMatriz.setOutputMarkupPlaceholderTag(true);
        tituloMatriz.setMarkupId(tituloMatriz.getId());
        form.addOrReplace(tituloMatriz);
        painelMatrizVigente =
                new PainelDadosMatrizPercentual(MATRIZ_VIGENTE_PANEL, perfilRiscoAtual, false,
                        "Matriz de riscos e controles", false,
                        true) {
                    @Override
                    protected void onConfigure() {
                        super.onConfigure();
                        setVisibilityAllowed(!isPerfilRiscoCicloMigrado());
                    }
                };
        painelMatrizVigente.setOutputMarkupPlaceholderTag(true);
        painelMatrizVigente.setMarkupId(MATRIZ_VIGENTE_PANEL);
        form.addOrReplace(painelMatrizVigente);
    }

    protected void addPainelSintese(Form<?> form) {
        painelSintese = new PainelSinteseDeRisco("grupoDeSinteses", perfilRiscoAtual) {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(!isPerfilRiscoCicloMigrado());
            }
        };
        painelSintese.setOutputMarkupPlaceholderTag(true);
        painelSintese.setOutputMarkupPlaceholderTag(true);
        form.addOrReplace(painelSintese);
    }

    protected void addPainelAnaliseQuantitativa(Form<?> form) {
        tabelaAnaliseQuantitativa = new WebMarkupContainer("tPainelQuantitativa") {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisible(!isPerfilRiscoCicloMigrado());
            }
        };
        tabelaAnaliseQuantitativa.setMarkupId(tabelaAnaliseQuantitativa.getId());
        tabelaAnaliseQuantitativa.setOutputMarkupPlaceholderTag(true);
        form.addOrReplace(tabelaAnaliseQuantitativa);

        painelAnaliseQuantitativa = new PainelAnaliseQuantitativa(ANALISE_QUANTITATIVA_PANEL, perfilRiscoAtual, false) {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisible(!isPerfilRiscoCicloMigrado());
            }
        };
        painelAnaliseQuantitativa.setOutputMarkupPlaceholderTag(true);
        painelAnaliseQuantitativa.setMarkupId(ANALISE_QUANTITATIVA_PANEL);
        tabelaAnaliseQuantitativa.addOrReplace(painelAnaliseQuantitativa);
    }

    protected void addPainelAtividadesCiclo(Form<?> form) {
        painelAtividadesCiclo = new PainelAtividadesCiclo("atividadesCicloPanel", perfilRiscoAtual) {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(!isPerfilRiscoCicloMigrado());
            }
        };
        painelAtividadesCiclo.setOutputMarkupPlaceholderTag(true);
        form.addOrReplace(painelAtividadesCiclo);
    }

    protected void addPainelInformacoesCicloMigrado(Form<?> form) {
        painelInformacoesCicloMigrado =
                new PainelInformacoesCicloMigrado("informacoesCicloMigradoPanel", perfilRiscoAtual) {
                    @Override
                    protected void onConfigure() {
                        super.onConfigure();
                        setVisibilityAllowed(isPerfilRiscoCicloMigrado());
                    }
                };
        painelInformacoesCicloMigrado.setOutputMarkupPlaceholderTag(true);
        form.addOrReplace(painelInformacoesCicloMigrado);
    }

    protected void addPainelCorec(Form<?> form) {
        painelCorec = new PainelCorec("idPainelCorec", perfilRiscoAtual) {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(CicloMediator.get()
                        .exibirSecaoCorec(ciclo, getPerfilRisco(), getPerfilPorPagina()));
            }
        };
        painelCorec.setOutputMarkupPlaceholderTag(true);
        form.addOrReplace(painelCorec);
    }

    protected void addBotoes(Form<?> form) {
        form.addOrReplace(addBotaoGerenciarNotasSinteses());
        form.addOrReplace(addBotaoGerenciarArcs());
        form.addOrReplace(addBotaoEditarEstruturarMatriz());
        form.addOrReplace(addBotaoGerenciarDetalharES());
        form.addOrReplace(addBotaoBloquearDesbloquearES());
        form.addOrReplace(addBotaoGerenciarQuadroPosicaoFinanceira());
        form.addOrReplace(addBotaoGerenciarAnaliseEconomicoFinanceira());
        form.addOrReplace(addBotaoAjusteNotaFinal());
        }
   
    
    protected CustomButtonSupervisor addBotaoGerenciarQuadroPosicaoFinanceira() {
        botaoGerenciarQuadroPosicaoFinanceira = new CustomButtonSupervisor("bttGerenciarQuadroPosicaoFinanceira") {
            @Override
            public void executeSubmit() {
                avancarParaNovaPagina(new GerenciarQuadroPosicaoFinanceira(perfilRiscoAtual));
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisible(PerfilRiscoMediator.get().isExibirBotaoCicloEmAndamento(ciclo,
                        painelFiltroPerfilDeRisco.getPerfilRiscoSelecionado(), getPerfilPorPagina())
                        && !isOrigemPainelConsulta);
            }
        };
        botaoGerenciarQuadroPosicaoFinanceira.setOutputMarkupPlaceholderTag(true);
        return botaoGerenciarQuadroPosicaoFinanceira;
    }

    protected CustomButtonSupervisor addBotaoGerenciarAnaliseEconomicoFinanceira() {
        botaoGerenciarAnaliseEconomicoFinanceira = new CustomButtonSupervisor("bttGerenciarAEF") {
            @Override
            public void executeSubmit() {
                avancarParaNovaPagina(new GerenciarNotaSinteseAQTPage(perfilRiscoAtual));
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(PerfilRiscoMediator.get().isExibirBotaoCicloEmAndamento(ciclo,
                        painelFiltroPerfilDeRisco.getPerfilRiscoSelecionado(), getPerfilPorPagina()));
            }
        };
        botaoGerenciarAnaliseEconomicoFinanceira.setOutputMarkupPlaceholderTag(true);
        return botaoGerenciarAnaliseEconomicoFinanceira;
    }

    protected CustomButton addBotaoGerenciarNotasSinteses() {
        botaoGerenciarNotasSinteses = new CustomButton("bttGerenciarNotasSinteses") {
            @Override
            public void executeSubmit() {
                avancarParaNovaPagina(new GerenciarNotaSintesePage(perfilRiscoAtual));
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(PerfilRiscoMediator.get().isExibirBotaoCicloEmAndamento(ciclo,
                        painelFiltroPerfilDeRisco.getPerfilRiscoSelecionado(), getPerfilPorPagina())
                        && !isOrigemPainelConsulta && perfilRiscoAtual.getCiclo().getMatriz() != null);
            }
        };
        botaoGerenciarNotasSinteses.setOutputMarkupPlaceholderTag(true);
        return botaoGerenciarNotasSinteses;
    }

    protected CustomButton addBotaoGerenciarArcs() {
        botaoGerenciarArcs = new CustomButton("bttGerenciarArcs") {

            @Override
            public void executeSubmit() {
                Matriz matriz = PerfilRiscoMediator.get().getMatrizAtualPerfilRisco(perfilRiscoAtual);
                new RegraGestaoARC(ciclo, matriz).validarGestao();
                avancarParaNovaPagina(new GerenciarArc(ciclo.getPk()));
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(PerfilRiscoMediator.get().isExibirBotaoCicloEmAndamento(ciclo,
                        painelFiltroPerfilDeRisco.getPerfilRiscoSelecionado(), getPerfilPorPagina())
                        && !isOrigemPainelConsulta && perfilRiscoAtual.getCiclo().getMatriz() != null);
            }
        };
        botaoGerenciarArcs.setOutputMarkupPlaceholderTag(true);
        return botaoGerenciarArcs;
    }

    protected CustomButton addBotaoGerenciarDetalharES() {
        botaoGerenciarDetalhesES = new CustomButton("bttGerenciarDetalheES") {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(PerfilRiscoMediator.get().isExibirBotaoCicloEmAndamentoSupervisorGerente(ciclo,
                        painelFiltroPerfilDeRisco.getPerfilRiscoSelecionado(), getPerfilPorPagina())
                        && !isOrigemPainelConsulta);
            }

            @Override
            public void executeSubmit() {
                avancarParaNovaPagina(new GerenciarES(ciclo.getPk(), perfilRiscoAtual));
            }
        };
        botaoGerenciarDetalhesES.setOutputMarkupPlaceholderTag(true);

        return botaoGerenciarDetalhesES;
    }

    protected CustomButton addBotaoBloquearDesbloquearES() {
        botaoBloquearDesbloquear = new CustomButton("bttBloquearDesbloquearES") {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(PerfilRiscoMediator.get().isExibirBotaoCicloEmAndamentoGerente(ciclo,
                        painelFiltroPerfilDeRisco.getPerfilRiscoSelecionado(), getPerfilPorPagina()));
                boolean isESBloqueada =
                        BloqueioESMediator.get().isESBloqueada(
                                ciclo.getEntidadeSupervisionavel().getConglomeradoOuCnpj());
                botaoBloquearDesbloquear.add(new AttributeModifier("value",
                        isESBloqueada ? "Desbloquear consulta da ES" : "Bloquear consulta da ES"));
                botaoBloquearDesbloquear.add(new AttributeModifier("style", isESBloqueada ? "width: 190px;"
                        : "width: 180px;"));
            }

            @Override
            public void executeSubmit() {
                bloquearDesbloquearES();
            }
        };
        botaoBloquearDesbloquear.setOutputMarkupPlaceholderTag(true);
        return botaoBloquearDesbloquear;
    }

    private void bloquearDesbloquearES() {
        String sucesso = null;
        String cnpj = ciclo.getEntidadeSupervisionavel().getConglomeradoOuCnpj();
        if (BloqueioESMediator.get().isESBloqueada(cnpj)) {
            sucesso = BloqueioESMediator.get().desbloquearES(cnpj);
        } else {
            sucesso = BloqueioESMediator.get().bloquearES(cnpj);
        }
        success(sucesso);
    }

    protected CustomButton addBotaoEditarEstruturarMatriz() {
        botaoEditarMatriz = new CustomButton("bttEditarMatriz") {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(PerfilRiscoMediator.get().isExibirBotaoCicloEmAndamento(ciclo,
                        painelFiltroPerfilDeRisco.getPerfilRiscoSelecionado(), getPerfilPorPagina())
                        && !isOrigemPainelConsulta);
            }

            @Override
            public void executeSubmit() {
                Matriz matriz = MatrizCicloMediator.get().getUltimaMatrizCiclo(ciclo);
                if (EstadoMatrizEnum.VIGENTE.equals(matriz.getEstadoMatriz())) {
                    MatrizCicloMediator.get().editarMatrizCiclo(matriz, perfilRiscoAtual);
                    avancarParaNovaPagina(new SucessoEdicaoMatrizPage(ciclo.getPk(), true));
                } else {
                    avancarParaNovaPagina(new EdicaoMatrizPage(ciclo.getPk()));
                }
            }
        };
        botaoEditarMatriz.setOutputMarkupPlaceholderTag(true);

        return botaoEditarMatriz;
    }

    private CustomButton addBotaoAjusteNotaFinal() {
        botaoAjusteNotafinal = new CustomButton("bttAjusteNotaFinal") {
            @Override
            public void executeSubmit() {
                avancarParaNovaPagina(new AjusteNotaFinal(perfilRiscoAtual));
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(PerfilRiscoMediator.get().isExibirBotaoCicloEmAndamento(ciclo,
                        painelFiltroPerfilDeRisco.getPerfilRiscoSelecionado(), getPerfilPorPagina())
                        && !isOrigemPainelConsulta && perfilRiscoAtual.getCiclo().getMatriz() != null);
            }
        };
        botaoAjusteNotafinal.setOutputMarkupPlaceholderTag(true);
        return botaoAjusteNotafinal;
    }

    public void atualizarPagina(AjaxRequestTarget target) {
        if (isAcessoPorParametro() && (RegraPerfilAcessoMediator.get().isAcessoPermitido(PerfilAcessoEnum.CONSULTA_TUDO)
                || RegraPerfilAcessoMediator.get().isAcessoPermitido(PerfilAcessoEnum.CONSULTA_NAO_BLOQUEADOS))) {
            setPaginaAnterior(new PerfilConsultaPage());
        }
        atualizarPaineis(target);
        if (getPaginaAtual() instanceof PerfilDeRiscoPage) {
            atualizarBotoes(target);
        }
        this.getFeedbackMessages().clear();
        target.add(this.getFeedBack());
    }

    protected void atualizarPaineis(AjaxRequestTarget target) {
        setarPerfilRiscoPaineis(painelFiltroPerfilDeRisco.getPerfilRiscoSelecionado());
        if (getPaginaAtual() instanceof PerfilDeRiscoPage) {
            painelMatrizVigente.setPerfilAtual(perfilRiscoAtual.getPk().equals(
                    painelFiltroPerfilDeRisco.getPerfilRiscoSelecionado().getPk()));
            target.add(painelMatrizVigente);
            target.add(painelMenuLink);
            target.add(painelAtividadesCiclo);
            target.add(painelInformacoesCicloMigrado);
            target.add(painelLinkOutrasInformacoes);
            target.add(painelCorec);
            target.add(tituloMatriz);
        }

        target.add(painelResumoCiclo);
        target.add(painelDetalhesES);
        target.add(painelSintese);
        target.add(painelSinteseAQT);
        target.add(painelResumoSinteseAQT);
        target.add(painelAnaliseQuantitativa);
        target.add(tabelaAnaliseQuantitativa);

    }

    public void setarPerfilRiscoPaineis(PerfilRisco perfilRisco) {
        painelResumoCiclo.setPerfilRisco(perfilRisco);
        painelDetalhesES.setPerfilRisco(perfilRisco);
        if (getPaginaAtual() instanceof PerfilDeRiscoPage) {
            painelMatrizVigente.setPerfilRisco(perfilRisco);
            painelLinkOutrasInformacoes.setPerfilRisco(perfilRisco);
            painelCorec.setPerfilRisco(perfilRisco);
            painelInformacoesCicloMigrado.setPerfilRisco(perfilRisco);
            painelAtividadesCiclo.setPerfilRiscoEModelConsulta(perfilRisco,
                    perfilRiscoAtual.getPk().equals(perfilRisco.getPk()));
            painelMenuLink.setPerfilRisco(perfilRisco);
        }

        painelResumoSinteseAQT.setPerfilRisco(perfilRisco);
        painelSintese.setPerfilRisco(perfilRisco);
        painelAnaliseQuantitativa.setPerfilRiscoAtual(perfilRisco);
        painelSinteseAQT.setPerfilRisco(perfilRisco);

    }

    protected void atualizarBotoes(AjaxRequestTarget target) {
        target.add(botaoGerenciarDetalhesES, botaoGerenciarDetalhesES.getMarkupId());
        target.add(botaoBloquearDesbloquear);
        target.add(botaoGerenciarNotasSinteses);
        target.add(botaoGerenciarArcs);
        target.add(botaoEditarMatriz);
        target.add(botaoGerenciarQuadroPosicaoFinanceira);
        target.add(botaoGerenciarAnaliseEconomicoFinanceira);
        target.add(botaoAjusteNotafinal);
    }

    public void atualizarMensagem(AjaxRequestTarget target, List<String> mensagens) {
        Page pagina = painelCorec.getPage();
        for (String m : mensagens) {
            pagina.warn(m);
            target.add(pagina.get("feedback"));
        }
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

        if (painelFiltroPerfilDeRisco.getPerfilRiscoSelecionado() == null) {
            perfilRiscoAtual = PerfilRiscoMediator.get().obterPerfilRiscoAtual(ciclo.getPk());
        } else {
            perfilRiscoAtual = PerfilRiscoMediator.get()
                    .obterPerfilRiscoPorPk(painelFiltroPerfilDeRisco.getPerfilRiscoSelecionado().getPk());
        }
        
        if (acessoPerfil) {
            perfilRiscoAtual = PerfilRiscoMediator.get().obterPerfilRiscoPorPk(idPerfil);
            painelFiltroPerfilDeRisco.setValorCiclo(perfilRiscoAtual.getCiclo());
            painelFiltroPerfilDeRisco.setValorPerfilRisco(perfilRiscoAtual);
        }

        setarPerfilRiscoPaineis(perfilRiscoAtual);
        LogOperacaoMediator.get().gerarLogDetalhamento(ciclo.getEntidadeSupervisionavel(), perfilRiscoAtual,
                atualizarDadosPagina(getPaginaAtual()));
    }

    protected void addPainelMenuLink(Form<?> form) {
        painelMenuLink = new PainelMenuLink("idPainelMenuLink", perfilRiscoAtual);
        painelMenuLink.setOutputMarkupPlaceholderTag(true);
        form.addOrReplace(painelMenuLink);
    }

	public boolean isAcessoPorParametro() {
		return acessoPorParametro;
}
	
	public void setAcessoPorParametro(boolean isAcessoPorParametro) {
		this.acessoPorParametro = isAcessoPorParametro;
	}
}
