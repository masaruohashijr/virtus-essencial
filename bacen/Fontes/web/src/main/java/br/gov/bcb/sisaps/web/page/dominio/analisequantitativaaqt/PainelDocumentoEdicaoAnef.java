package br.gov.bcb.sisaps.web.page.dominio.analisequantitativaaqt;

import java.io.InputStream;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Documento;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ItemElementoAQT;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoAQTEnum;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnexoItemElementoAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.ItemElementoAQTMediator;
import br.gov.bcb.sisaps.src.vo.AnexoDocumentoVo;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.botoes.AjaxSubmitLinkSisAps;
import br.gov.bcb.sisaps.web.page.componentes.grupo.GrupoExpansivel;
import br.gov.bcb.sisaps.web.page.componentes.textarea.CKEditorTextArea;
import br.gov.bcb.sisaps.web.page.componentes.util.CKEditorUtils;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.analise.AnalisarArcPage;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.edicao.AjaxBotaoAnexarArquivo;

public class PainelDocumentoEdicaoAnef extends PainelSisAps {

    private static final String SEM_ALTERACOES_SALVAS = "Sem alterações salvas.";

    private static final String ID_ALERTA_DADOS_NAO_SALVOS_DOCUMENTO = "idAlertaDadosNaoSalvosDocumento";

    private static final String ID_FIELD_UPLOAD_ANEXO = "idFieldUploadAnexo";

    private static final String ID_TABELA_ANEXO_DOCUMENTO = "idTabelaAnexoDocumento";

    private static final String ID_BOTAO_ANEXAR_AQUIVO = "botaoAnexarAquivo";

    private static final String ID_NOVA_JUSTIFICATIVA = "idNovaJustificativa";

    private static final String ID_BTT_SALVAR_JUSTIFICATIVA_ITEM = "bttSalvarJustificativaItem";

    @SpringBean
    private ItemElementoAQTMediator itemElementoMediator;

    private List<FileUpload> filesUpload;
    private TabelaAnexoDocumentoAQT tabelaAnexoDocumentoEdicao;

    private Label labelUltimaAlteracaoDocumento;

    private final AnaliseQuantitativaAQT aqt;

    private Ciclo ciclo = new Ciclo();
    private final ItemElementoAQT itemElemento;

    private FileUploadField fileUploadFieldArquivo;

    public PainelDocumentoEdicaoAnef(String id, ItemElementoAQT itemElemento, ItemElementoAQT itemElementoANEFVigente,
            AnaliseQuantitativaAQT aqt, List<String> idsAlertas) {
        super(id);
        this.itemElemento = itemElemento;
        this.aqt = aqt;
        setOutputMarkupId(true);
        this.ciclo = aqt.getCiclo();
        if (this.itemElemento.getDocumento() == null) {
            this.itemElemento.setDocumento(new Documento());
        }
        buildComponentes(itemElementoANEFVigente, idsAlertas);
    }

    private void buildComponentes(ItemElementoAQT itemElementoANEFVigente, List<String> idsAlertas) {
        buildComponentesDocEdicao(idsAlertas, itemElementoANEFVigente);
        buildComponentesDocVigenteDetalhe(itemElementoANEFVigente);
    }

    private void buildComponentesDocEdicao(List<String> idAlertas, final ItemElementoAQT itemElementoANEFVigente) {
        addOrReplace(new Label("tituloJustificativa", titulo(aqt.getEstado())));

        addTextAreaJustificativa();
        IModel<String> modelUltimaAlteracaoDocumento = new LoadableDetachableModel<String>() {
            @Override
            public String load() {
                boolean rascunhoEVigenteIguais = isAvaliacoesRascunhoEVigenteIguais(itemElementoANEFVigente);
                String ultimaAlteracaoDocumento = itemElemento.getDocumento().getNomeOperadorDataHora();
                return rascunhoEVigenteIguais || StringUtils.isBlank(ultimaAlteracaoDocumento) ? SEM_ALTERACOES_SALVAS
                        : "Última alteração salva " + ultimaAlteracaoDocumento;
            }
        };

        labelUltimaAlteracaoDocumento = new Label("idUltimaAlteracaoDocumento", modelUltimaAlteracaoDocumento);
        addOrReplace(labelUltimaAlteracaoDocumento);
        addTextAreaJustificativa();
        addAlerta(idAlertas);
        tabelaAnexoDocumentoEdicao = new TabelaAnexoDocumentoAQT(ID_TABELA_ANEXO_DOCUMENTO, itemElemento, ciclo, true) {
            @Override
            protected void excluirAnexo(AjaxRequestTarget target, IModel<AnexoDocumentoVo> model) {
                String mensagemSucesso =
                        AnexoItemElementoAQTMediator.get().excluirAnexo(model.getObject(), itemElemento, ciclo);
                target.add(tabela, tabela.getMarkupId());
                mensagemSucessoAnexo(mensagemSucesso);
            }

        };
        tabelaAnexoDocumentoEdicao.setMarkupId(ID_TABELA_ANEXO_DOCUMENTO + itemElemento.getPk());
        tabelaAnexoDocumentoEdicao.setOutputMarkupId(true);
        tabelaAnexoDocumentoEdicao.setOutputMarkupPlaceholderTag(true);
        addOrReplace(tabelaAnexoDocumentoEdicao);

        addOrReplace(botaoSalvar());

        fileUploadFieldArquivo =
                new FileUploadField(ID_FIELD_UPLOAD_ANEXO, new PropertyModel<List<FileUpload>>(this, "filesUpload"));
        fileUploadFieldArquivo.setMarkupId(ID_FIELD_UPLOAD_ANEXO + itemElemento.getPk());
        fileUploadFieldArquivo.setOutputMarkupId(true);
        addOrReplace(fileUploadFieldArquivo);
        addBotaoAnexar();
    }

    private boolean isAvaliacoesRascunhoEVigenteIguais(ItemElementoAQT itemElementoANEFVigente) {
        if (itemElementoANEFVigente == null) {
            return false;
        } else {
            String justificativaRascunho =
                    itemElemento.getDocumento().getJustificativa() == null ? "" : itemElemento.getDocumento()
                            .getJustificativa();

            String justificativaVigente =
                    itemElementoANEFVigente.getDocumento() == null ? "" : itemElementoANEFVigente.getDocumento()
                            .getJustificativa() == null ? "" : itemElementoANEFVigente.getDocumento()
                            .getJustificativa();

            return justificativaRascunho.equals(justificativaVigente)
                    && itemElemento.getDataAlteracao() != null
                    && aqt.getData(itemElemento.getDataAlteracao()).equals(
                            aqt.getData(itemElementoANEFVigente.getDataAlteracao()))
                    && itemElemento.getOperadorAlteracao().equals(itemElementoANEFVigente.getOperadorAlteracao());
        }
    }

    private void addBotaoAnexar() {
        AjaxBotaoAnexarArquivo botaoAnexarArquivo =
                new AjaxBotaoAnexarArquivo(ID_BOTAO_ANEXAR_AQUIVO, fileUploadFieldArquivo, true) {
                    @Override
                    public void executarSubmit(final AjaxRequestTarget target, String clientFileName,
                            InputStream inputStream) {
                        String mensagemSucesso =
                                AnexoItemElementoAQTMediator.get().anexarArquivo(itemElemento, clientFileName,
                                        inputStream);
                        AnaliseQuantitativaAQTMediator.get().alterarEstadoANEFParaEmEdicao(aqt);
                        target.add(tabelaAnexoDocumentoEdicao, tabelaAnexoDocumentoEdicao.getMarkupId());
                        target.add(fileUploadFieldArquivo);
                        target.add(labelUltimaAlteracaoDocumento);
                        atualizarPaineis(target);
                        mensagemSucessoAnexo(mensagemSucesso);
                    }

                };

        botaoAnexarArquivo.setMarkupId(ID_BOTAO_ANEXAR_AQUIVO + itemElemento.getPk());
        addOrReplace(botaoAnexarArquivo);
    }

    private void mensagemSucessoAnexo(String mensagemSucesso) {
        getPage().success(mensagemSucesso);
    }

    private void atualizarPaineis(AjaxRequestTarget target) {
        if (getPaginaAtual() instanceof EdicaoAQT) {
            ((EdicaoAQT) getPage()).atualizarPaineis(target);
        } else if (getPaginaAtual() instanceof AnalisarArcPage) {
            ((AnalisarArcPage) getPage()).atualizarNovaNotaArc(target);
        }

    }

    private String titulo(EstadoAQTEnum estado) {
        String titulo = "Nova justificativa";
        if (AnaliseQuantitativaAQTMediator.get().estadoDesignado(estado)
                || AnaliseQuantitativaAQTMediator.get().estadoEmEdicao(estado)) {
            titulo = "Nova avaliação";
        }
        return titulo;
    }

    private void addAlerta(List<String> idAlertas) {
        Label alertaDadosNaoSalvos = new Label(ID_ALERTA_DADOS_NAO_SALVOS_DOCUMENTO, "Atenção texto não salvo.");
        alertaDadosNaoSalvos.setMarkupId(ID_ALERTA_DADOS_NAO_SALVOS_DOCUMENTO + itemElemento.getPk());
        addOrReplace(alertaDadosNaoSalvos);
        idAlertas.add(alertaDadosNaoSalvos.getMarkupId());
    }

    private void addTextAreaJustificativa() {
        CKEditorTextArea<String> justificativa =
                new CKEditorTextArea<String>(ID_NOVA_JUSTIFICATIVA, new PropertyModel<String>(itemElemento,
                        "documento.justificativa")) {
                    @Override
                    protected String onKeyUpJS() {
                        return mostrarAlertas();
                    }
                };
        justificativa.setMarkupId(ID_NOVA_JUSTIFICATIVA + itemElemento.getPk());
        addOrReplace(justificativa);
    }

    private String mostrarAlertas() {
        return CKEditorUtils.jsAtualizarAlerta(ID_ALERTA_DADOS_NAO_SALVOS_DOCUMENTO + itemElemento.getPk(), true)
                + CKEditorUtils.jsAtualizarAlerta(EdicaoAQT.ID_ALERTA_DADOS_NAO_SALVOS, true);
    }

    private void atualizarPainel(AjaxRequestTarget target) {
        target.add(labelUltimaAlteracaoDocumento);
        ((EdicaoAQT) getPage()).atualizarAlertaPrincipal(target,
                ID_ALERTA_DADOS_NAO_SALVOS_DOCUMENTO + itemElemento.getPk());
    }

    private void ultimaAlteracao(final ItemElementoAQT itemElementoARCVigente) {
        IModel<String> modelUltimaAlteracaoDocumento = new LoadableDetachableModel<String>() {
            @Override
            public String load() {
                String ultimaAlteracaoDocumento = valorItemVigente(itemElementoARCVigente);
                return StringUtils.isBlank(ultimaAlteracaoDocumento) ? SEM_ALTERACOES_SALVAS : "Preenchido por "
                        + ultimaAlteracaoDocumento;
            }

        };

        Label labelUltimaAlteracaoDocumento =
                new Label("idUltimaAlteracaoDocumentoVigente", modelUltimaAlteracaoDocumento);
        labelUltimaAlteracaoDocumento.setOutputMarkupId(true);
        labelUltimaAlteracaoDocumento.setVisible(!"".equals(valorItemVigente(itemElementoARCVigente)));
        addOrReplace(labelUltimaAlteracaoDocumento);
    }

    private String valorItemVigente(ItemElementoAQT itemElementoARCVigente) {
        return itemElementoARCVigente == null ? "" : itemElementoARCVigente.getDocumento() == null ? ""
                : itemElementoARCVigente.getDocumento().getNomeOperadorDataHora();
    }

    private void buildComponentesDocVigenteDetalhe(ItemElementoAQT itemElementoARCVigente) {
        Documento documentoVigente = null;
        if (itemElementoARCVigente != null) {
            documentoVigente = itemElementoARCVigente.getDocumento();
        }

        ultimaAlteracao(itemElementoARCVigente);

        PainelItemElementoVigenteEdicaoAnef painel =
                new PainelItemElementoVigenteEdicaoAnef("idPainelJustificativaVigente", aqt, ciclo,
                        itemElementoARCVigente, documentoVigente);

        GrupoExpansivel grupo =
                new GrupoExpansivel("GrupoEspansivelAvaliacao", "Avaliação vigente", false, new Component[] {painel});
        addOrReplace(painel);
        grupo.setOutputMarkupId(true);
        grupo.setVisible(documentoVigente != null);
        addOrReplace(grupo);
    }

    public List<FileUpload> getFilesUpload() {
        return filesUpload;
    }

    public void setFilesUpload(List<FileUpload> filesUpload) {
        this.filesUpload = filesUpload;
    }

    private AjaxSubmitLinkSisAps botaoSalvar() {
        AjaxSubmitLinkSisAps botaoSalvar = new AjaxSubmitLinkSisAps(ID_BTT_SALVAR_JUSTIFICATIVA_ITEM) {
            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                String mensagem = itemElementoMediator.salvarJustificativaItemElementoAQT(aqt, itemElemento, false);
                mensagemSucesso(mensagem);
                atualizarPainel(target);
            }
        };
        botaoSalvar.setMarkupId(ID_BTT_SALVAR_JUSTIFICATIVA_ITEM + itemElemento.getPk());
        return botaoSalvar;
    }

    private void mensagemSucesso(String mensagem) {
        getPage().success(mensagem);
    }

    public void validarAnexos() throws NegocioException {
        SisapsUtil.lancarNegocioException("Um documento foi selecionado e não anexado.",
                fileUploadFieldArquivo.getFileUpload() != null);
    }

}
