package br.gov.bcb.sisaps.web.page.dominio.gerenciaes;

import java.io.InputStream;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.ConclusaoES;
import br.gov.bcb.sisaps.src.dominio.Documento;
import br.gov.bcb.sisaps.src.dominio.PerfilAtuacaoES;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.ConclusaoESMediator;
import br.gov.bcb.sisaps.src.mediator.DocumentoMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilAtuacaoESMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import br.gov.bcb.sisaps.web.page.DefaultPage;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.botoes.AjaxSubmitLinkSisAps;
import br.gov.bcb.sisaps.web.page.componentes.textarea.CKEditorTextArea;
import br.gov.bcb.sisaps.web.page.componentes.util.CKEditorUtils;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.edicao.AjaxBotaoAnexarArquivo;

public class PainelPerfilAtuacaoConclusao extends PainelSisAps {

    private static final String NOVA_CONCLUSAO = "Nova conclusão";

    private static final String NOVO_PERFIL_DE_ATUACAO = "Novo perfil de atuação";

    private static final String ATUALIZADO_POR = "Atualizado por ";

    private static final String TITULO_CONCLUSAO = "Conclusão";

    private static final String PERFIL_DE_ATUACAO = "Perfil de atuação";

    private static final String ID_TABELA_ANEXO_DOCUMENTO_VIGENTE = "idTabelaAnexoDocumentoVigente";

    private static final String ID_TABELA_ANEXO_DOCUMENTO = "idTabelaAnexoDocumento";
    private static final String ID_TABELA_ANEXO_DOCUMENTO_ENCAMINHADO = "idTabelaAnexoDocumentoEncaminhado";

    private static final String CONCLUSAO = "conclusao_";

    private static final String PERFIL_ATUACAO = "perfilAtuacao_";

    private static final String PROP_DOCUMENTO_JUSTIFICATIVA = "documento.justificativa";

    private static final String ULTIMA_ALTERACAO_SALVA = "Última alteração salva ";

    private static final String ENCAMINHADO_POR = "Encaminhado por ";

    private static final String PONTO = ".";

    @SpringBean
    private PerfilAtuacaoESMediator perfilAtuacaoESMediator;
    @SpringBean
    private ConclusaoESMediator conclusaoESMediator;
    @SpringBean
    private CicloMediator cicloMediator;

    private final Ciclo ciclo;
    private final boolean isPerfilAtuacao;
    private PerfilAtuacaoES perfilAtuacaoESVigente;
    private PerfilAtuacaoES perfilAtuacaoES;
    private ConclusaoES conclusaoESVigente;
    private ConclusaoES conclusaoES;

    private List<FileUpload> filesUpload;

    private String markupIdAlerta;

    private TabelaAnexoPerfilAtuacaoConclusao tabelaAnexo;
    private WebMarkupContainer painelSupervisor;
    private WebMarkupContainer painelGerente;
    private final PerfilRisco perfilRisco;

    public PainelPerfilAtuacaoConclusao(String id, Integer cicloId, boolean isPerfilAtuacao, PerfilRisco perfilRisco) {
        super(id);
        this.perfilRisco = perfilRisco;
        this.ciclo = cicloMediator.buscarCicloPorPK(cicloId);
        this.isPerfilAtuacao = isPerfilAtuacao;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        criarDados();
        addComponentes();
    }

    private void criarDados() {
        if (isPerfilAtuacao) {
            criarPerfilAtuacaoES();
        } else {
            criarConclusaoES();
        }
    }

    private void criarPerfilAtuacaoES() {
        perfilAtuacaoESVigente = perfilAtuacaoESMediator.getPerfilAtuacaoESPorPerfil(perfilRisco.getPk());
        perfilAtuacaoES =
                getPaginaAtual().perfilGerente(getPaginaAtual()) ? perfilAtuacaoESMediator
                        .getPerfilAtuacaoESPendencia(ciclo) : perfilAtuacaoESMediator
                        .getPerfilAtuacaoESSemPerfilRisco(ciclo);

        if (perfilAtuacaoES == null) {
            setarDadosNovoPerfil();
        } else {
            perfilAtuacaoES.setDocumento(DocumentoMediator.get().buscarPorPk(perfilAtuacaoES.getDocumento().getPk()));
        }

    }

    private void setarDadosNovoPerfil() {
        perfilAtuacaoES = new PerfilAtuacaoES();
        perfilAtuacaoES.setCiclo(ciclo);
        perfilAtuacaoES.setPerfilAtuacaoESAnterior(perfilAtuacaoESVigente);
        perfilAtuacaoES.setDocumento(new Documento());
        if (perfilAtuacaoESVigente != null) {
            perfilAtuacaoES.getDocumento().setJustificativa(perfilAtuacaoESVigente.getDocumento().getJustificativa());
        }
    }

    private void criarConclusaoES() {
        conclusaoESVigente = conclusaoESMediator.getConclusaoESPorPerfil(perfilRisco.getPk());
        conclusaoES =
                getPaginaAtual().perfilGerente(getPaginaAtual()) ? conclusaoESMediator.getConclusaoESPendencia(ciclo)
                        : conclusaoESMediator.getConclusaoESSemPerfilRisco(ciclo);
        if (conclusaoES == null) {
            setarDadosNovaConclusao();
        } else {
            conclusaoES.setDocumento(DocumentoMediator.get().buscarPorPk(conclusaoES.getDocumento().getPk()));
        }
    }

    private void setarDadosNovaConclusao() {
        conclusaoES = new ConclusaoES();
        conclusaoES.setCiclo(ciclo);
        conclusaoES.setConclusaoESAnterior(conclusaoESVigente);
        conclusaoES.setDocumento(new Documento());
        if (conclusaoESVigente != null) {
            conclusaoES.getDocumento().setJustificativa(conclusaoESVigente.getDocumento().getJustificativa());
        }
    }

    private void addComponentes() {
        WebMarkupContainer tabela = new WebMarkupContainer("tDados");

        String titulo = null;
        if (isPerfilAtuacao) {
            titulo = PERFIL_DE_ATUACAO;
            tabela.setMarkupId(PERFIL_ATUACAO + tabela.getId());
        } else {
            titulo = TITULO_CONCLUSAO;
            tabela.setMarkupId(CONCLUSAO + tabela.getId());
        }
        addOrReplace(tabela);
        tabela.addOrReplace(new Label("idTitulo", titulo));
        addDadosVigentes(tabela);

        addPainelSupervisor(tabela);
        addPainelGerente(tabela);
    }

    private void addPainelSupervisor(WebMarkupContainer tabela) {
        painelSupervisor = new WebMarkupContainer("paineilSupervisor") {
            @Override
            public boolean isVisible() {
                return isSupervisor();
            }
        };
        addNovosDados(painelSupervisor);

        tabela.addOrReplace(painelSupervisor);
    }

    private boolean isSupervisor() {
        return getPaginaAtual().perfilSupervisor(getPaginaAtual());
    }

    private void addPainelGerente(WebMarkupContainer tabela) {
        painelGerente = new WebMarkupContainer("paineilGerente") {
            @Override
            public boolean isVisible() {
                return getPaginaAtual().perfilGerente(getPaginaAtual()) && possuiPendencia();
            }

            private boolean possuiPendencia() {
                return conclusaoES != null && SimNaoEnum.SIM.equals(conclusaoES.getPendente())
                        || perfilAtuacaoES != null && SimNaoEnum.SIM.equals(perfilAtuacaoES.getPendente());
            }
        };
        addConfirmarNovosDados(painelGerente);
        tabela.addOrReplace(painelGerente);
    }

    private void addConfirmarNovosDados(WebMarkupContainer painelGerente) {
        String campo = "";
        String strDataEncaminhamento = "";
        String nomeOperadorDataHora = "";
        String strDescricaoEncaminhado = "";
        if (isPerfilAtuacao) {
            campo = NOVO_PERFIL_DE_ATUACAO;
            nomeOperadorDataHora = perfilAtuacaoES.getNomeOperadorEncaminhamentoDataHora();
            if (nomeOperadorDataHora != null) {
                strDataEncaminhamento = ENCAMINHADO_POR + nomeOperadorDataHora + PONTO;
            }
            strDescricaoEncaminhado = perfilAtuacaoES.getDocumento().getJustificativa();
        } else {
            campo = NOVA_CONCLUSAO;
            nomeOperadorDataHora = conclusaoES.getNomeOperadorEncaminhamentoDataHora();
            if (nomeOperadorDataHora != null) {
                strDataEncaminhamento = ENCAMINHADO_POR + nomeOperadorDataHora + PONTO;
            }
            strDescricaoEncaminhado = conclusaoES.getDocumento().getJustificativa();
        }
        painelGerente.addOrReplace(new Label("idCampoEncaminhado", campo));
        painelGerente.addOrReplace(new Label("idDataEncaminhado", strDataEncaminhamento));

        painelGerente.addOrReplace(new Label("idDescricaoEncaminhado", strDescricaoEncaminhado)
                .setEscapeModelStrings(false));

        addTabelaAnexosEdicao(painelGerente, true);
        addBotaoConfirmarNovoPerfilAtuacaoES(painelGerente);
        addBotaoConfirmarNovaConclusaoES(painelGerente);
    }

    private void addDadosVigentes(WebMarkupContainer tabela) {
        String campoVigente = "";
        String strDataVigente = "";
        String strDescricaoVigente = "";
        if (isPerfilAtuacao) {
            campoVigente = PERFIL_DE_ATUACAO;
            if (perfilAtuacaoESVigente != null) {
                strDataVigente =
                        ATUALIZADO_POR + perfilAtuacaoESVigente.getNomeOperadorEncaminhamentoDataHoraPublicacao()
                                + PONTO;
                if (perfilAtuacaoESVigente.getDocumento() != null
                        && perfilAtuacaoESVigente.getDocumento().getJustificativa() != null) {
                    strDescricaoVigente = perfilAtuacaoESVigente.getDocumento().getJustificativa();
                }
            }

        } else {
            campoVigente = TITULO_CONCLUSAO;
            if (conclusaoESVigente != null) {
                strDataVigente =
                        ATUALIZADO_POR + conclusaoESVigente.getNomeOperadorEncaminhamentoDataHoraPublicacao() + PONTO;
                if (conclusaoESVigente.getDocumento() != null
                        && conclusaoESVigente.getDocumento().getJustificativa() != null) {
                    strDescricaoVigente = conclusaoESVigente.getDocumento().getJustificativa();
                }
            }

        }
        tabela.addOrReplace(new Label("idCampoVigente", campoVigente));
        tabela.addOrReplace(new Label("idDataVigente", strDataVigente));
        tabela.addOrReplace(new Label("idDescricaoVigente", strDescricaoVigente).setEscapeModelStrings(false));
        addTabelaAnexoVigente(tabela);
    }

    private void addTabelaAnexoVigente(WebMarkupContainer tabela) {
        if (isPerfilAtuacao) {
            Documento documentoVigente = perfilAtuacaoESVigente == null ? null : perfilAtuacaoESVigente.getDocumento();
            tabelaAnexo =
                    new TabelaAnexoPerfilAtuacaoConclusao(ID_TABELA_ANEXO_DOCUMENTO_VIGENTE, perfilAtuacaoESVigente,
                            documentoVigente, ciclo, false, isPerfilAtuacao, PainelPerfilAtuacaoConclusao.this) {
                        @Override
                        protected void onConfigure() {
                            super.onConfigure();
                            setVisibilityAllowed(perfilAtuacaoESVigente != null);
                        }
                    };
            tabela.addOrReplace(tabelaAnexo);
        } else {
            Documento documentoVigente = conclusaoESVigente == null ? null : conclusaoESVigente.getDocumento();
            tabelaAnexo =
                    new TabelaAnexoPerfilAtuacaoConclusao(ID_TABELA_ANEXO_DOCUMENTO_VIGENTE, conclusaoESVigente,
                            documentoVigente, ciclo, false, isPerfilAtuacao, PainelPerfilAtuacaoConclusao.this) {
                        @Override
                        protected void onConfigure() {
                            super.onConfigure();
                            setVisibilityAllowed(conclusaoESVigente != null);
                        }
                    };
        }
        tabela.addOrReplace(tabelaAnexo);
    }

    private void addNovosDados(WebMarkupContainer painelSupervisor) {
        String campo = "";
        String strDataNova = "";
        String nomeOperadorDataHora = "";
        if (isPerfilAtuacao) {
            campo = NOVO_PERFIL_DE_ATUACAO;
            if (perfilAtuacaoES != null) {
                nomeOperadorDataHora = perfilAtuacaoES.getNomeOperadorDataHora();
                boolean mostrar =
                        perfilAtuacaoES.getDataEncaminhamento() == null && perfilAtuacaoES.getPendente() == null;
                if (!mostrar) {
                    if (perfilAtuacaoES.getPendente() != null && !SimNaoEnum.NAO.equals(perfilAtuacaoES.getPendente())) {
                        strDataNova = ENCAMINHADO_POR + perfilAtuacaoES.getNomeOperadorEncaminhamentoDataHora() + PONTO;
                    } else if (nomeOperadorDataHora != null) {
                        strDataNova = ULTIMA_ALTERACAO_SALVA + nomeOperadorDataHora + PONTO;
                    }
                }
            }

        } else {
            campo = NOVA_CONCLUSAO;
            if (conclusaoES != null) {
                nomeOperadorDataHora = conclusaoES.getNomeOperadorDataHora();
                boolean mostrar = conclusaoES.getDataEncaminhamento() == null && conclusaoES.getPendente() == null;
                if (!mostrar) {
                    if (conclusaoES.getPendente() != null && !SimNaoEnum.NAO.equals(conclusaoES.getPendente())) {
                        strDataNova = ENCAMINHADO_POR + conclusaoES.getNomeOperadorEncaminhamentoDataHora() + PONTO;
                    } else if (nomeOperadorDataHora != null) {
                        strDataNova = ULTIMA_ALTERACAO_SALVA + nomeOperadorDataHora + PONTO;
                    }
                }
            }
        }
        painelSupervisor.addOrReplace(new Label("idCampo", campo));
        painelSupervisor.addOrReplace(new Label("idDataNova", strDataNova));

        addTextAreaDescricaoNova(painelSupervisor);
        addBotaoSalvar(painelSupervisor);
        addTabelaAnexosEdicao(painelSupervisor, false);
        addFileUploadEBotaoAnexarArquivo(painelSupervisor);
        addBotaoEncaminharPerfilAtuacaoES(painelSupervisor);
        addBotaoEncaminharConclusaoES(painelSupervisor);
    }

    private void addTextAreaDescricaoNova(WebMarkupContainer tabela) {
        PropertyModel<String> propertyDescricao = null;
        String entidade = null;
        if (isPerfilAtuacao) {
            propertyDescricao = new PropertyModel<String>(perfilAtuacaoES, PROP_DOCUMENTO_JUSTIFICATIVA);
            entidade = PERFIL_ATUACAO;
        } else {
            propertyDescricao = new PropertyModel<String>(conclusaoES, PROP_DOCUMENTO_JUSTIFICATIVA);
            entidade = CONCLUSAO;
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
        tabela.addOrReplace(descricao);
    }

    private void addBotaoSalvar(WebMarkupContainer tabela) {
        AjaxSubmitLinkSisAps botaoSalvar = new AjaxSubmitLinkSisAps("bttSalvar", true) {
            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                if (isPerfilAtuacao) {
                    perfilAtuacaoESMediator.salvarNovoPerfilAtuacao(perfilAtuacaoES, true);
                } else {
                    conclusaoESMediator.salvarNovaConclusao(conclusaoES, true);
                }
                mensagemSucessoSalvar();
                atualizarPagina(target);
                target.add(PainelPerfilAtuacaoConclusao.this);
            }
        };
        if (isPerfilAtuacao) {
            botaoSalvar.setMarkupId(PERFIL_ATUACAO + botaoSalvar.getId());
        } else {
            botaoSalvar.setMarkupId(CONCLUSAO + botaoSalvar.getId());
        }
        tabela.addOrReplace(botaoSalvar);
    }

    private void mensagemSucessoSalvar() {
        if (isPerfilAtuacao) {
            success("Perfil de atuação salvo com sucesso.");
        } else {
            success("Conclusão salva com sucesso.");
        }
    }

    private void addTabelaAnexosEdicao(WebMarkupContainer tabela, boolean perfilGerente) {
        if (isPerfilAtuacao) {
            addTabelaAnexosPerfilAtuacaoES(perfilGerente);
        } else {
            addTabelaAnexosConclusaoES(perfilGerente);
        }
        tabela.addOrReplace(tabelaAnexo);
    }

    private void addTabelaAnexosPerfilAtuacaoES(boolean perfilGerente) {

        tabelaAnexo =
                new TabelaAnexoPerfilAtuacaoConclusao(perfilGerente ? ID_TABELA_ANEXO_DOCUMENTO_ENCAMINHADO
                        : ID_TABELA_ANEXO_DOCUMENTO, perfilAtuacaoES, perfilAtuacaoES.getDocumento(), ciclo,
                        isSupervisor(), isPerfilAtuacao, PainelPerfilAtuacaoConclusao.this);

    }

    private void addTabelaAnexosConclusaoES(boolean perfilGerente) {

        tabelaAnexo =
                new TabelaAnexoPerfilAtuacaoConclusao(perfilGerente ? ID_TABELA_ANEXO_DOCUMENTO_ENCAMINHADO
                        : ID_TABELA_ANEXO_DOCUMENTO, conclusaoES, conclusaoES.getDocumento(), ciclo, isSupervisor(),
                        isPerfilAtuacao, PainelPerfilAtuacaoConclusao.this);

    }

    private void addFileUploadEBotaoAnexarArquivo(WebMarkupContainer tabela) {
        final FileUploadField fileUploadFieldArquivo =
                new FileUploadField("idFieldUploadAnexo", new PropertyModel<List<FileUpload>>(this, "filesUpload"));
        fileUploadFieldArquivo.setOutputMarkupId(true);
        tabela.addOrReplace(fileUploadFieldArquivo);

        AjaxBotaoAnexarArquivo botaoAnexarArquivo =
                new AjaxBotaoAnexarArquivo("idAnexarArquivo", fileUploadFieldArquivo, true) {
                    @Override
                    public void executarSubmit(final AjaxRequestTarget target, String clientFileName,
                            InputStream inputStream) {
                        if (isPerfilAtuacao) {
                            perfilAtuacaoESMediator.salvarAnexo(ciclo, perfilAtuacaoES, clientFileName, inputStream,
                                    true);
                        } else {
                            conclusaoESMediator.salvarAnexo(ciclo, conclusaoES, clientFileName, inputStream, true);
                        }
                        target.add(PainelPerfilAtuacaoConclusao.this);
                    }
                };
        tabela.addOrReplace(botaoAnexarArquivo);

        if (isPerfilAtuacao) {
            fileUploadFieldArquivo.setMarkupId(PERFIL_ATUACAO + fileUploadFieldArquivo.getId());
            botaoAnexarArquivo.setMarkupId(PERFIL_ATUACAO + botaoAnexarArquivo.getId());
        } else {
            fileUploadFieldArquivo.setMarkupId(CONCLUSAO + fileUploadFieldArquivo.getId());
            botaoAnexarArquivo.setMarkupId(CONCLUSAO + botaoAnexarArquivo.getId());
        }
    }

    private void addBotaoEncaminharPerfilAtuacaoES(WebMarkupContainer tabela) {
        tabela.addOrReplace(new AjaxSubmitLinkSisAps("bttEncaminharPerfilAtuacao", true) {
            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                mensagemSucessoConfirmar(perfilAtuacaoESMediator.encaminharNovoPerfilAtuacao(perfilAtuacaoES));
                atualizarPagina(target);
                target.add(PainelPerfilAtuacaoConclusao.this);
                criarDados();
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(isPerfilAtuacao);
                setEnabled(isPerfilAtuacao && SimNaoEnum.NAO.equals(perfilAtuacaoES.getPendente()));
            }
        });
    }

    private void addBotaoEncaminharConclusaoES(WebMarkupContainer tabela) {
        tabela.addOrReplace(new AjaxSubmitLinkSisAps("bttEncaminharNovaConclusao", true) {
            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                mensagemSucessoConfirmar(conclusaoESMediator.encaminharNovaConclusao(conclusaoES));
                atualizarPagina(target);
                target.add(PainelPerfilAtuacaoConclusao.this);
                criarDados();
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(!isPerfilAtuacao);
                setEnabled(!isPerfilAtuacao && SimNaoEnum.NAO.equals(conclusaoES.getPendente()));
            }
        });
    }

    private void addBotaoConfirmarNovoPerfilAtuacaoES(WebMarkupContainer tabela) {
        tabela.addOrReplace(new AjaxSubmitLinkSisAps("bttConcluirNovoPerfilAtuacao", true) {
            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                DefaultPage paginaAnterior = getPaginaAtual().getPaginaAnterior();
                String sucess = perfilAtuacaoESMediator.confirmarNovoPerfilAtuacao(perfilAtuacaoES);
                perfilAtuacaoESMediator.setarPendenteNull(ciclo);
                getPaginaAtual()
                        .avancarParaNovaPagina(
                                new GerenciarES(ciclo.getPk(),
                                        PerfilRiscoMediator.get().obterPerfilRiscoAtual(ciclo.getPk()), sucess),
                                paginaAnterior == null ? getPaginaAtual() : paginaAnterior);
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(isPerfilAtuacao);
                setEnabled(isPerfilAtuacao && perfilAtuacaoES.getPk() != null);
            }
        });
    }

    private void addBotaoConfirmarNovaConclusaoES(WebMarkupContainer tabela) {
        tabela.addOrReplace(new AjaxSubmitLinkSisAps("bttConcluirNovaConclusao", true) {
            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                DefaultPage paginaAnterior = getPaginaAtual().getPaginaAnterior();
                String sucess = conclusaoESMediator.confirmarNovaConclusao(conclusaoES);
                conclusaoESMediator.setarPendenteNull(ciclo);
                getPaginaAtual().avancarParaNovaPagina(
                        new GerenciarES(ciclo.getPk(), PerfilRiscoMediator.get().obterPerfilRiscoAtual(ciclo.getPk()),
                                sucess),
                        paginaAnterior == null ? getPaginaAtual() : paginaAnterior);
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(!isPerfilAtuacao);
                setEnabled(!isPerfilAtuacao && conclusaoES.getPk() != null);
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

    public List<FileUpload> getFilesUpload() {
        return filesUpload;
    }

    public void setFilesUpload(List<FileUpload> filesUpload) {
        this.filesUpload = filesUpload;
    }

}
