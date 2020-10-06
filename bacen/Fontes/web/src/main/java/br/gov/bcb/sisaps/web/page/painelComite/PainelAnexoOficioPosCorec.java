package br.gov.bcb.sisaps.web.page.painelComite;

import java.io.InputStream;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.gov.bcb.sisaps.src.dominio.AnexoPosCorec;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.mediator.AnexoPosCorecMediator;
import br.gov.bcb.sisaps.src.validacao.RegraAnexosValidacaoPDFA4;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.botoes.AjaxSubmitLinkSisAps;
import br.gov.bcb.sisaps.web.page.componentes.calendar.CalendarTextField;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.edicao.AjaxBotaoAnexarArquivo;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.edicao.TabelaAnexoComum;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.edicao.TabelaAnexoPosCorec;

public class PainelAnexoOficioPosCorec extends PainelSisAps {
    private static final String ID_EDICAO_OFICIO = "idEdicaoOficio";
    private static final String ID_LISTA_OFICIO = "idListaOficio";
    private static final String OFICIO = "Ofício";
    private AjaxSubmitLinkSisAps botaoExcluir;
    private final Ciclo ciclo;
    @SpringBean
    private AnexoPosCorecMediator anexoPosCorecMediator;
    private IModel<String> modelData;
    private AnexoPosCorec anexo;
    private List<FileUpload> filesUpload;
    private TabelaAnexoComum<?> tabelaAnexo;
    private WebMarkupContainer edicao;
    private WebMarkupContainer lista;

    public PainelAnexoOficioPosCorec(String id, Ciclo ciclo) {
        super(id);
        setOutputMarkupId(true);
        this.ciclo = ciclo;

    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        this.anexo = anexoPosCorecMediator.buscarAnexo(ciclo, OFICIO);
        addComponents();
    }

    private void addComponents() {
        addComponentes();
        addBotaoSalvarInformacoes();

    }

    private void addComponentes() {
        edicao = new WebMarkupContainer(ID_EDICAO_OFICIO);
        edicao.setVisible(!anexoPosCorecMediator.existeAnexo(ciclo, OFICIO));
        edicao.setMarkupId(edicao.getId());
        edicao.setOutputMarkupId(true);
        edicao.setOutputMarkupPlaceholderTag(true);
        lista = new WebMarkupContainer(ID_LISTA_OFICIO);
        lista.setVisible(anexoPosCorecMediator.existeAnexo(ciclo, OFICIO));
        lista.setMarkupId(lista.getId());
        lista.setOutputMarkupId(true);
        lista.setOutputMarkupPlaceholderTag(true);
        addBotaoAnexar();
        addDatas();
        addListaAnexo();
        addOrReplace(edicao);
        addOrReplace(lista);
    }

    private void addBotaoAnexar() {
        final FileUploadField fileUploadFieldArquivo = getFileUpload();
        edicao.addOrReplace(fileUploadFieldArquivo);
        AjaxBotaoAnexarArquivo botaoAnexar =
                new AjaxBotaoAnexarArquivo("botaoAnexarAquivoOficio", fileUploadFieldArquivo, true,
                        new RegraAnexosValidacaoPDFA4()) {
                    @Override
                    public void executarSubmit(AjaxRequestTarget target, String clientFileName, InputStream inputStream) {

                        success(anexoPosCorecMediator.anexarArquivo(ciclo, OFICIO,
                                modelData == null ? null : modelData.getObject(), clientFileName, inputStream));
                        atualizaPainel();
                        target.add(edicao, lista, tabelaAnexo, botaoExcluir, fileUploadFieldArquivo);
                        GestaoPosCorecPage page = (GestaoPosCorecPage) getPaginaAtual();
                        page.atualizarPainelParticipantes(target);
                    }
                };
        botaoAnexar.setMarkupId("botaoAnexarAquivoARC");
        edicao.addOrReplace(botaoAnexar);
    }

    private FileUploadField getFileUpload() {
        FileUploadField fileUploadFieldArquivo =
                new FileUploadField("idFieldUploadAnexoOficio",
                        new PropertyModel<List<FileUpload>>(this, "filesUpload"));
        fileUploadFieldArquivo.setVisible(true);
        return fileUploadFieldArquivo;
    }

    private void addDatas() {
        modelData = new Model<String>();
        CalendarTextField<String> txtDataInicio = new CalendarTextField<String>("idData", modelData);
        txtDataInicio.setOutputMarkupId(true);
        txtDataInicio.setMarkupId(txtDataInicio.getId());
        edicao.addOrReplace(txtDataInicio);
    }

    private void addBotaoSalvarInformacoes() {
        botaoExcluir = botaoExcluirAnexo();
        botaoExcluir.setOutputMarkupId(true);
        botaoExcluir.setEnabled(anexoPosCorecMediator.existeAnexo(ciclo, OFICIO));
        lista.addOrReplace(botaoExcluir);

    }

    private AjaxSubmitLinkSisAps botaoExcluirAnexo() {
        return new AjaxSubmitLinkSisAps("bttExcluirAnexoOficio", true) {

            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                String msg = AnexoPosCorecMediator.get().excluirAnexo(ciclo, OFICIO);
                getPaginaAtual().success(msg);
                modelData.setObject("");
                atualizaPainel();
                target.add(edicao, lista, tabelaAnexo);
            }

        };
    }

    private void addListaAnexo() {
        lista.addOrReplace(getData());
        tabelaAnexo = new TabelaAnexoPosCorec("idTabelaAnexoOficio", ciclo, OFICIO, false, false);
        lista.addOrReplace(tabelaAnexo);
    }

    private Label getData() {
        String data = "";
        if (anexo != null && anexo.getDataEntrega() != null) {
            data = anexo.getDataEntregaFormatada();
        }
        Label dataEntrega = new Label("dataEntrega", data);
        return dataEntrega;
    }

    private void atualizaPainel() {
        anexo = anexoPosCorecMediator.buscarAnexo(ciclo, OFICIO);
        edicao.setVisible(!anexoPosCorecMediator.existeAnexo(ciclo, OFICIO));
        lista.setVisible(anexoPosCorecMediator.existeAnexo(ciclo, OFICIO));
        lista.addOrReplace(getData());
        botaoExcluir.setEnabled(anexoPosCorecMediator.existeAnexo(ciclo, OFICIO));
    }

    public List<FileUpload> getFilesUpload() {
        return filesUpload;
    }

    public void setFilesUpload(List<FileUpload> filesUpload) {
        this.filesUpload = filesUpload;
    }

}
