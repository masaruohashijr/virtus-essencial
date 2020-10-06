package br.gov.bcb.sisaps.web.page.dominio.gestaoAgenda;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.PropertyModel;

import br.gov.bcb.sisaps.src.mediator.AnexoParticipanteComiteMediator;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.edicao.AjaxBotaoAnexarArquivo;

public class PainelArquivoParticipante extends PainelSisAps {
    private List<FileUpload> filesUpload;
    private PainelListagemAnexoParticipantes painelListagemArquivos;

    public PainelArquivoParticipante(String id) {
        super(id);
        addComponentes();
    }

    private void addComponentes() {
        addBotaoAnexar();
        addPainelListagem();

    }

    private void addBotaoAnexar() {
        final FileUploadField fileUploadFieldArquivo = getFileUpload();
        addOrReplace(fileUploadFieldArquivo);
        AjaxBotaoAnexarArquivo botaoAnexar =
                new AjaxBotaoAnexarArquivo("botaoAnexarAquivoOficio", fileUploadFieldArquivo, true, null) {
                    @Override
                    public void executarSubmit(AjaxRequestTarget target, String clientFileName, InputStream inputStream) {
                        FileUpload upload = fileUploadFieldArquivo.getFileUpload();
                        if (upload != null) {
                            File arquivo = new File(upload.getClientFileName());
                            try {
                                arquivo.createNewFile();
                                upload.writeTo(arquivo);
                                String msg =  AnexoParticipanteComiteMediator.get().anexarArquivo(inputStream, arquivo);
                                GestaoAgendaPage page = (GestaoAgendaPage) getPaginaAtual();
                                target.add(page.getPainelComitesRealizar().getPainelArquivo()
                                        .getPainelListagemArquivos());
                                target.add(fileUploadFieldArquivo);
                                if (!msg.isEmpty()) {
                                    success(msg);
                                }
                            } catch (IOException e) {
                                GestaoAgendaPage page = (GestaoAgendaPage) getPaginaAtual();
                                target.add(page.get("feedback"));
                            }
                        }
                    }
                };
        botaoAnexar.setMarkupId("botaoAnexarAquivoARC");
        addOrReplace(botaoAnexar);
    }

    private FileUploadField getFileUpload() {
        FileUploadField fileUploadFieldArquivo =
                new FileUploadField("idFieldUploadAnexoOficio",
                        new PropertyModel<List<FileUpload>>(this, "filesUpload"));
        fileUploadFieldArquivo.setVisible(true);
        return fileUploadFieldArquivo;
    }

    private void addPainelListagem() {
        painelListagemArquivos = new PainelListagemAnexoParticipantes("painelListagemArquivo");
        addOrReplace(painelListagemArquivos);
    }

    public List<FileUpload> getFilesUpload() {
        return filesUpload;
    }

    public void setFilesUpload(List<FileUpload> filesUpload) {
        this.filesUpload = filesUpload;
    }

    public PainelListagemAnexoParticipantes getPainelListagemArquivos() {
        return painelListagemArquivos;
    }

    public void setPainelListagemArquivos(PainelListagemAnexoParticipantes painelListagemArquivos) {
        this.painelListagemArquivos = painelListagemArquivos;
    }

}
