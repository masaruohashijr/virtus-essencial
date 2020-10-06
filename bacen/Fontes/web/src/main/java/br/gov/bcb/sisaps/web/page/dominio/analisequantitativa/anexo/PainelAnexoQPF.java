package br.gov.bcb.sisaps.web.page.dominio.analisequantitativa.anexo;

import java.io.InputStream;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.PropertyModel;

import br.gov.bcb.sisaps.src.dominio.analisequantitativa.QuadroPosicaoFinanceira;
import br.gov.bcb.sisaps.src.mediator.AnexoQuadroPosicaoFinanceiraMediator;
import br.gov.bcb.sisaps.src.vo.AnexoQPFVo;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.dominio.analisequantitativa.GerenciarQuadroPosicaoFinanceira;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.edicao.AjaxBotaoAnexarArquivo;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.edicao.TabelaAnexoComum;

public class PainelAnexoQPF extends PainelSisAps {
    private List<FileUpload> filesUpload;

    private QuadroPosicaoFinanceira quadro;
    private final boolean isEdicao;
    private WebMarkupContainer tituloEdicao;
    private WebMarkupContainer tituloDetalhe;
    private WebMarkupContainer tabela = new WebMarkupContainer("tAnexosQPF");
    private TabelaAnexoComum<AnexoQPFVo> tabelaAnexoArc;

    public PainelAnexoQPF(String id, final QuadroPosicaoFinanceira quadro, boolean isEdicao) {
        super(id);
        this.quadro = quadro;
        this.isEdicao = isEdicao;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        componentes();
    }

    private void componentes() {
        tituloEdicao = new WebMarkupContainer("tituloEdicao") {
            @Override
            public boolean isVisible() {
                return isEdicao;
            }
        };
        tabela.addOrReplace(tituloEdicao);
        tituloDetalhe = new WebMarkupContainer("tituloDetalhe") {
            @Override
            public boolean isVisible() {
                return !isEdicao;
            }
        };
        tabela.addOrReplace(tituloDetalhe);
        tabelaAnexoArc = new TabelaAnexoQPF("idTabelaAnexoQPF", quadro, isEdicao);
        tabela.addOrReplace(tabelaAnexoArc);
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
                        AnexoQuadroPosicaoFinanceiraMediator.get().anexarArquivo(quadro, clientFileName, inputStream,
                                null, false, false);
                        target.add(edicao, tabelaAnexoArc, fileUploadFieldArquivo);
                        target.add(PainelAnexoQPF.this);
                        GerenciarQuadroPosicaoFinanceira gerenciarQuadroPosicaoFinanceira =
                                (GerenciarQuadroPosicaoFinanceira) getPage();
                        gerenciarQuadroPosicaoFinanceira.lancarInfoAjax("Anexo incluido com sucesso.",
                                PainelAnexoQPF.this, target);
                        target.add(gerenciarQuadroPosicaoFinanceira.getPainelQuadroPosicaoFinanceira());
                    }
                };
        botaoAnexar.setMarkupId("botaoAnexarAquivoQPF");
        edicao.addOrReplace(botaoAnexar);
        edicao.setVisible(isEdicao);
        tabela.addOrReplace(edicao);
        tabela.setOutputMarkupId(true);
        StringBuffer id = new StringBuffer("tAnexosQPF");
        id.append(isEdicao ? "Edicao" : "Detalhe");
        tabela.setMarkupId(id.toString());
        addOrReplace(tabela);
    }

    public List<FileUpload> getFilesUpload() {
        return filesUpload;
    }

    public void setFilesUpload(List<FileUpload> filesUpload) {
        this.filesUpload = filesUpload;
    }

}
