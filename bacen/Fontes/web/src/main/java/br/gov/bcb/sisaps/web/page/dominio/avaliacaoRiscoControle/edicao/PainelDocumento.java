package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.edicao;

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

import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Documento;
import br.gov.bcb.sisaps.src.dominio.ItemElemento;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoARCEnum;
import br.gov.bcb.sisaps.src.mediator.AnexoItemElementoMediator;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.ItemElementoMediator;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.Util;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.botoes.AjaxSubmitLinkSisAps;
import br.gov.bcb.sisaps.web.page.componentes.grupo.GrupoExpansivel;
import br.gov.bcb.sisaps.web.page.componentes.textarea.CKEditorTextArea;
import br.gov.bcb.sisaps.web.page.componentes.util.CKEditorUtils;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.analise.AnalisarArcPage;

public class PainelDocumento extends PainelSisAps {

    private static final String SEM_ALTERACOES_SALVAS = "Sem alterações salvas.";

    private static final String ID_ALERTA_DADOS_NAO_SALVOS_DOCUMENTO = "idAlertaDadosNaoSalvosDocumento";

    private static final String ID_FIELD_UPLOAD_ANEXO = "idFieldUploadAnexo";

    private static final String ID_TABELA_ANEXO_DOCUMENTO = "idTabelaAnexoDocumento";

    private static final String ID_BOTAO_ANEXAR_AQUIVO = "botaoAnexarAquivo";

    private static final String ID_NOVA_JUSTIFICATIVA = "idNovaJustificativa";

    private static final String ID_BTT_SALVAR_JUSTIFICATIVA_ITEM = "bttSalvarJustificativaItem";

    @SpringBean
    private ItemElementoMediator itemElementoMediator;

    private List<FileUpload> filesUpload;
    private final ItemElemento itemElemento;
    private TabelaAnexoComum<?> tabelaAnexoDocumentoEdicao;

    private final AvaliacaoRiscoControle arc;

    private Label labelUltimaAlteracaoDocumento;

    private AvaliacaoRiscoControle arcVigente;

    public PainelDocumento(String id, final ItemElemento itemElemento, ItemElemento itemElementoARCVigente,
            Ciclo ciclo, AvaliacaoRiscoControle arc, List<String> idAlertas) {
        super(id);
        this.arc = arc;
        this.arcVigente = arc.getAvaliacaoRiscoControleVigente();
        setOutputMarkupId(true);
        this.itemElemento = itemElemento;
        buildComponentes(ciclo, arc, itemElementoARCVigente, idAlertas);
    }

    private void buildComponentes(Ciclo ciclo, AvaliacaoRiscoControle arc, ItemElemento itemElementoARCVigente,
            List<String> idAlertas) {
        buildComponentesDocEdicao(ciclo, arc, itemElementoARCVigente, idAlertas);
        buildComponentesDocVigenteDetalhe(ciclo, itemElementoARCVigente);
    }

    private void buildComponentesDocEdicao(final Ciclo ciclo, final AvaliacaoRiscoControle arc, 
            final ItemElemento itemElementoARCVigente, List<String> idAlertas) {
        addOrReplace(new Label("tituloJustificativa", titulo(arc.getEstado())));

        addTextAreaJustificativa();
        IModel<String> modelUltimaAlteracaoDocumento = new LoadableDetachableModel<String>() {
            public String load() {
                boolean rascunhoEVigenteIguais = isAvaliacoesRascunhoEVigenteIguais(itemElementoARCVigente);
                String ultimaAlteracaoDocumento = itemElemento.getDocumento() == null ? 
                        "" : itemElemento.getDocumento().getNomeOperadorDataHora();
                return rascunhoEVigenteIguais || StringUtils.isBlank(ultimaAlteracaoDocumento) ? 
                        SEM_ALTERACOES_SALVAS : "Última alteração salva " + ultimaAlteracaoDocumento;
            }
        };

        labelUltimaAlteracaoDocumento = new Label("idUltimaAlteracaoDocumento", modelUltimaAlteracaoDocumento);
        addOrReplace(labelUltimaAlteracaoDocumento);
        addTextAreaJustificativa();
        addAlerta(idAlertas);
        tabelaAnexoDocumentoEdicao =
                new TabelaAnexoDocumento(ID_TABELA_ANEXO_DOCUMENTO, itemElemento, ciclo, true);
        tabelaAnexoDocumentoEdicao.setMarkupId(ID_TABELA_ANEXO_DOCUMENTO + itemElemento.getPk());
        addOrReplace(tabelaAnexoDocumentoEdicao);

        addOrReplace(botaoSalvar(ciclo, arc));

        final FileUploadField fileUploadFieldArquivo =
                new FileUploadField(ID_FIELD_UPLOAD_ANEXO, new PropertyModel<List<FileUpload>>(this, "filesUpload"));
        fileUploadFieldArquivo.setMarkupId(ID_FIELD_UPLOAD_ANEXO + itemElemento.getPk());
        fileUploadFieldArquivo.setOutputMarkupId(true);
        addOrReplace(fileUploadFieldArquivo);
        AjaxBotaoAnexarArquivo botaoAnexarArquivo =
                new AjaxBotaoAnexarArquivo(ID_BOTAO_ANEXAR_AQUIVO, fileUploadFieldArquivo, true) {
                    @Override
                    public void executarSubmit(final AjaxRequestTarget target, String clientFileName,
                            InputStream inputStream) {
                        AnexoItemElementoMediator.get().anexarArquivo(arc, ciclo, itemElemento, clientFileName,
                                inputStream, false);
                        target.add(tabelaAnexoDocumentoEdicao, tabelaAnexoDocumentoEdicao.getMarkupId());
                        target.add(fileUploadFieldArquivo);
                        target.add(labelUltimaAlteracaoDocumento);
                        atualizarPaineis(target);
                    }

                };

        botaoAnexarArquivo.setMarkupId(ID_BOTAO_ANEXAR_AQUIVO + itemElemento.getPk());
        addOrReplace(botaoAnexarArquivo);
    }
    
    private boolean isAvaliacoesRascunhoEVigenteIguais(ItemElemento itemElementoANEFVigente) {
        if (itemElementoANEFVigente == null) {
            return false;
        } else {
            String justificativaRascunho = itemElemento.getDocumento() == null ? 
                    "" : itemElemento.getDocumento().getJustificativa() == null ? "" : itemElemento.getDocumento()
                            .getJustificativa();

            String justificativaVigente =
                    itemElementoANEFVigente.getDocumento() == null ? "" : itemElementoANEFVigente.getDocumento()
                            .getJustificativa() == null ? "" : itemElementoANEFVigente.getDocumento()
                            .getJustificativa();

            return justificativaRascunho.equals(justificativaVigente)
                    && itemElemento.getDataAlteracao() != null
                    && arc.getData(itemElemento.getDataAlteracao()).equals(
                            arc.getData(itemElementoANEFVigente.getDataAlteracao()))
                    && itemElemento.getOperadorAlteracao().equals(itemElementoANEFVigente.getOperadorAlteracao());
        }
    }

    private void atualizarPaineis(AjaxRequestTarget target) {
        if (getPaginaAtual() instanceof EdicaoArcPage) {
            ((EdicaoArcPage) getPage()).atualizarPainelInformacoesArc(target);
        } else if (getPaginaAtual() instanceof AnalisarArcPage) {
            ((AnalisarArcPage) getPage()).atualizarNovaNotaArc(target);
        }

    }

    private String titulo(EstadoARCEnum estado) {
        String titulo = "Nova justificativa";
        if (AvaliacaoRiscoControleMediator.get().estadoDesignado(estado)
                || AvaliacaoRiscoControleMediator.get().estadoEmEdicao(estado)) {
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
                + CKEditorUtils.jsAtualizarAlerta(EdicaoArcPage.ID_ALERTA_DADOS_NAO_SALVOS, true);
    }

    private void atualizarPainel(AjaxRequestTarget target) {
        target.add(labelUltimaAlteracaoDocumento);
        ((EdicaoArcPage) getPage()).atualizarAlertaPrincipal(target, ID_ALERTA_DADOS_NAO_SALVOS_DOCUMENTO
                + itemElemento.getPk());
    }

    private void buildComponentesDocVigenteDetalhe(Ciclo ciclo, final ItemElemento itemElementoARCVigente) {
        Documento documentoVigente = null;
        if (itemElementoARCVigente != null) {
            documentoVigente = itemElementoARCVigente.getDocumento();
        }
        
        ultimaAlteracao(itemElementoARCVigente);

        PainelItemElementoVigenteEdicao painel =
                new PainelItemElementoVigenteEdicao("idPainelJustificativaVigente", arc, ciclo, itemElementoARCVigente,
                        documentoVigente);

        GrupoExpansivel grupo =
                new GrupoExpansivel("GrupoEspansivelAvaliacao", "Avaliação vigente", false, new Component[] {painel}) {
            @Override
            public boolean isControleVisivel() {
                return !(arcVigente != null && itemElementoARCVigente.getDocumento() == null);
            }
        };
        grupo.setVisible(arcVigente != null);
        addOrReplace(painel);
        addOrReplace(grupo);
    }
    
    private void ultimaAlteracao(final ItemElemento itemElementoARCVigente) {
        IModel<String> modelUltimaAlteracaoDocumento = new LoadableDetachableModel<String>() {
            @Override
            public String load() {
                return getUltimaAlteracaoDocumentoVigente(itemElementoARCVigente);
            }

        };

        Label labelUltimaAlteracaoDocumento =
                new Label("idUltimaAlteracaoDocumentoVigente", modelUltimaAlteracaoDocumento);
        labelUltimaAlteracaoDocumento.setOutputMarkupId(true);
        labelUltimaAlteracaoDocumento.setVisible(itemElementoARCVigente != null);
        addOrReplace(labelUltimaAlteracaoDocumento);
    }
    
    private String getUltimaAlteracaoDocumentoVigente(ItemElemento itemElementoARCVigente) {
        String retorno = "";
        if (arcVigente != null && itemElementoARCVigente != null) {
            if (itemElementoARCVigente.getDocumento() == null) {
                retorno = "Não avaliado por " + Util.nomeOperador(arcVigente.getOperadorPreenchido()) 
                        + Constantes.EM + arcVigente.getData(arcVigente.getDataPreenchido());
            } else {
                retorno = "Preenchido por " + Util.nomeOperador(arcVigente.getOperadorPreenchido()) 
                        + Constantes.EM + arcVigente.getData(arcVigente.getDataPreenchido());
            }
        }
        return retorno;
    }

    public List<FileUpload> getFilesUpload() {
        return filesUpload;
    }

    public void setFilesUpload(List<FileUpload> filesUpload) {
        this.filesUpload = filesUpload;
    }

    private AjaxSubmitLinkSisAps botaoSalvar(final Ciclo ciclo, final AvaliacaoRiscoControle arc) {
        AjaxSubmitLinkSisAps botaoSalvar = new AjaxSubmitLinkSisAps(ID_BTT_SALVAR_JUSTIFICATIVA_ITEM) {
            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                itemElementoMediator.salvarJustificativaItemElementoARC(ciclo, arc, itemElemento, false);
                mensagemSucesso();
                atualizarPainel(target);
            }
        };
        botaoSalvar.setMarkupId(ID_BTT_SALVAR_JUSTIFICATIVA_ITEM + itemElemento.getPk());
        return botaoSalvar;
    }

    private void mensagemSucesso() {
        getPage().success(
                "Justificativa para " + itemElemento.getParametroItemElemento().getNome() + " salva com sucesso.");
    }

}
