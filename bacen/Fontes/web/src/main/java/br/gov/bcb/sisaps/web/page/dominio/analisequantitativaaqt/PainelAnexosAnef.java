package br.gov.bcb.sisaps.web.page.dominio.analisequantitativaaqt;

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
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnexoAQT;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnexoAQTMediator;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.edicao.AjaxBotaoAnexarArquivo;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.edicao.TabelaAnexoComum;

@SuppressWarnings("serial")
public class PainelAnexosAnef extends PainelSisAps {
    private List<FileUpload> filesUpload;

    private AnaliseQuantitativaAQT aqt;
    private final boolean isEdicao;
    private final Ciclo ciclo;
    @SpringBean
    private AnexoAQTMediator anexoAQTMediator;
    private final boolean isConsulta;

    private FileUploadField fileUploadFieldArquivo;

    public PainelAnexosAnef(String id, final Ciclo ciclo, final AnaliseQuantitativaAQT arc, boolean isEdicao) {
        this(id, ciclo, arc, isEdicao, false);
    }

    public PainelAnexosAnef(String id, final Ciclo ciclo, final AnaliseQuantitativaAQT arc, boolean isEdicao,
            boolean isConsulta) {
        super(id);
        this.aqt = arc;
        this.isEdicao = isEdicao;
        this.ciclo = ciclo;
        this.isConsulta = isConsulta;

    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        componentes();
    }

    private void componentes() {
        AnexoAQT ultimoAnexoANEF = anexoAQTMediator.buscarUltimoAnexoANEF(aqt);
        Label dataAtualizacaoElemento = new Label("idDataAnexo", ultimoAnexoANEF == null ? 
                "" : "Atualizado por " + SisapsUtil.getNomeOperadorDataHora(ultimoAnexoANEF)) {
            @Override
            protected void onConfigure() {
                setVisibilityAllowed(anexoAQTMediator.possuiAnexos(aqt) && !isConsulta);
            }
        };
        dataAtualizacaoElemento.setOutputMarkupPlaceholderTag(true);
        addOrReplace(dataAtualizacaoElemento);
        final TabelaAnexoComum<?> tabelaAnexoArc = new TabelaAnexoAqt("idTabelaAnexoArc", ciclo, aqt, isEdicao);
        addOrReplace(tabelaAnexoArc);

        final WebMarkupContainer edicao = new WebMarkupContainer("idEdicao");
        edicao.setOutputMarkupId(true);
        fileUploadFieldArquivo =
                new FileUploadField("idFieldUploadAnexo", new PropertyModel<List<FileUpload>>(this, "filesUpload"));
        fileUploadFieldArquivo.setVisible(isEdicao);
        edicao.addOrReplace(fileUploadFieldArquivo);
        AjaxBotaoAnexarArquivo botaoAnexar =
                new AjaxBotaoAnexarArquivo("botaoAnexarAquivo", fileUploadFieldArquivo, isEdicao) {
                    @Override
                    public void executarSubmit(AjaxRequestTarget target, String clientFileName, InputStream inputStream) {
                        String mensagemSucesso = anexoAQTMediator.anexarArquivo(
                                ciclo, aqt, clientFileName, inputStream, false, null, false);
                        incluirMensagemSucesso(mensagemSucesso);
                        target.add(edicao, tabelaAnexoArc, fileUploadFieldArquivo, PainelAnexosAnef.this);
                    }
                };
        botaoAnexar.setMarkupId("botaoAnexarAquivoARC");
        edicao.addOrReplace(botaoAnexar);
        addOrReplace(edicao);
        edicao.setVisible(isEdicao);
    }
    
    private void incluirMensagemSucesso(String mensagemSucesso) {
        success(mensagemSucesso);
    }

    @Override
    public boolean isVisible() {
        return isEdicao || !aqt.getAnexosAqt().isEmpty();
    }

    public List<FileUpload> getFilesUpload() {
        return filesUpload;
    }

    public void setFilesUpload(List<FileUpload> filesUpload) {
        this.filesUpload = filesUpload;
    }

    public void setAvaliacao(AnaliseQuantitativaAQT avaliacao) {
        this.aqt = avaliacao;
    }

    public void validarAnexos() throws NegocioException {
        SisapsUtil.lancarNegocioException("Um documento foi selecionado e não anexado.",
                fileUploadFieldArquivo.getFileUpload() != null);
    }

}
