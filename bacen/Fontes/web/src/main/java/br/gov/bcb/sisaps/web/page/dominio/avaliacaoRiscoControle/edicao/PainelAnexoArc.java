package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.edicao;

import java.io.InputStream;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.PropertyModel;

import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.mediator.AnexoArcMediator;
import br.gov.bcb.sisaps.web.page.PainelSisAps;

public class PainelAnexoArc extends PainelSisAps {
    private List<FileUpload> filesUpload;

    private AvaliacaoRiscoControle arc;
    private final boolean isEdicao;
    private final Ciclo ciclo;

    private final boolean isConsulta;

    public PainelAnexoArc(String id, final Ciclo ciclo, final AvaliacaoRiscoControle arc, boolean isEdicao) {
        this(id, ciclo, arc, isEdicao, false);
    }

    public PainelAnexoArc(String id, final Ciclo ciclo, final AvaliacaoRiscoControle arc, boolean isEdicao,
            boolean isConsulta) {
        super(id);
        this.arc = arc;
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
        Label dataAtualizacaoElemento = new Label("idDataAnexo", "Atualizado por " + arc.getNomeOperadorDataHora()) {
            @Override
            protected void onConfigure() {
                setVisibilityAllowed(!AnexoArcMediator.get().buscar(arc).isEmpty() && !isConsulta);
            }
        };
        addOrReplace(dataAtualizacaoElemento);
        final TabelaAnexoComum<?> tabelaAnexoArc = new TabelaAnexoArc("idTabelaAnexoArc", ciclo, arc, isEdicao);
        addOrReplace(tabelaAnexoArc);

        final WebMarkupContainer edicao = new WebMarkupContainer("idEdicao");
        edicao.setOutputMarkupId(true);
        final FileUploadField fileUploadFieldArquivo =
                new FileUploadField("idFieldUploadAnexo", new PropertyModel<List<FileUpload>>(this, "filesUpload"));
        fileUploadFieldArquivo.setVisible(isEdicao);
        edicao.addOrReplace(fileUploadFieldArquivo);
        AjaxBotaoAnexarArquivo botaoAnexar =
                new AjaxBotaoAnexarArquivo("botaoAnexarAquivo", fileUploadFieldArquivo, isEdicao) {
                    @Override
                    public void executarSubmit(AjaxRequestTarget target, String clientFileName, InputStream inputStream) {
                        AnexoArcMediator.get().anexarArquivo(ciclo, arc, clientFileName, inputStream, false, null,
                                false);
                        ((EdicaoArcPage) getPage()).atualizarPainelInformacoesArc(target);
                        target.add(edicao, tabelaAnexoArc, fileUploadFieldArquivo);
                    }
                };
        botaoAnexar.setMarkupId("botaoAnexarAquivoARC");
        edicao.addOrReplace(botaoAnexar);
        addOrReplace(edicao);
        edicao.setVisible(isEdicao);
    }

    public List<FileUpload> getFilesUpload() {
        return filesUpload;
    }

    public void setFilesUpload(List<FileUpload> filesUpload) {
        this.filesUpload = filesUpload;
    }

    public void setAvaliacao(AvaliacaoRiscoControle avaliacao) {
        this.arc = avaliacao;
    }

}
