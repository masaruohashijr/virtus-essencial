package br.gov.bcb.sisaps.web.page.dominio.perfilderisco.gestaoApresentacao.painel;

import java.io.InputStream;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.PropertyModel;

import br.gov.bcb.sisaps.src.dominio.enumeracoes.CampoApresentacaoEnum;
import br.gov.bcb.sisaps.src.mediator.AnexoApresentacaoMediator;
import br.gov.bcb.sisaps.src.validacao.RegraAnexosValidacaoImagem;
import br.gov.bcb.sisaps.src.vo.AnexoApresentacaoVO;
import br.gov.bcb.sisaps.src.vo.ApresentacaoVO;
import br.gov.bcb.sisaps.src.vo.TextoApresentacaoVO;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.edicao.AjaxBotaoAnexarArquivo;

public class PainelTextoAnexoApresentacao extends abstractPainelTexto {
    
    // IDs dos componentes.
    private static final String ID_TABELA_ANEXO_DOCUMENTO = "idTabelaAnexo",
            ID_FIELD_UPLOAD_ANEXO = "idFieldUploadAnexo", ID_BOTAO_ANEXAR_AQUIVO = "botaoAnexarAquivo";

    // Arquivos anexados.
    private List<FileUpload> filesUpload;

    // Tabela com a listagem de anexos.
    private TabelaAnexoApresentacao tabelaAnexoApresentacao;

    public PainelTextoAnexoApresentacao(String id, ApresentacaoVO apresentacaoVO, CampoApresentacaoEnum campo) {
        super(id, apresentacaoVO, campo);
     // Monta os componentes.
        montarComponentesAnexo(campo, apresentacaoVO);
    }
    
    @Override
    protected TextoApresentacaoVO buscarTextoApresentacao(final ApresentacaoVO apresentacaoVO) {
        return (TextoApresentacaoVO) apresentacaoVO.getValor(campo , new TextoApresentacaoVO());
    }
    
    // Monta os componentes do painel.
    private void montarComponentesAnexo(final CampoApresentacaoEnum campo, final ApresentacaoVO apresentacaoVO) {
        // Prepara a tabela que lista os anexos.
        tabelaAnexoApresentacao =
                new TabelaAnexoApresentacao(ID_TABELA_ANEXO_DOCUMENTO, new ModelAnexosApresentacao(apresentacaoVO,
                        campo.getSecao()) {
                    @Override
                    public void aoExcluirAnexo(AjaxRequestTarget target) {
                        // Atualiza dados de última atualização.
                        atualizarUltimaAtualizacao(target);
                    }
                });
        tabelaAnexoApresentacao.setMarkupId(ID_TABELA_ANEXO_DOCUMENTO + campo.getIdTag());
        addOrReplace(tabelaAnexoApresentacao);

        // Prepara o campo de seleção de arquivo.
        final FileUploadField fileUploadFieldArquivo =
                new FileUploadField(ID_FIELD_UPLOAD_ANEXO, new PropertyModel<List<FileUpload>>(this, "filesUpload"));
        fileUploadFieldArquivo.setMarkupId(ID_FIELD_UPLOAD_ANEXO + campo.getIdTag());
        fileUploadFieldArquivo.setOutputMarkupId(true);
        addOrReplace(fileUploadFieldArquivo);

        // Prepara o botão de upload.
        AjaxBotaoAnexarArquivo botaoAnexarArquivo =
                new AjaxBotaoAnexarArquivo(ID_BOTAO_ANEXAR_AQUIVO, fileUploadFieldArquivo, true,
                        new RegraAnexosValidacaoImagem()) {
                    // Salva o anexo.
                    @Override
                    public void executarSubmit(final AjaxRequestTarget target, String clientFileName,
                            InputStream inputStream) {
                        // Declarações
                        AnexoApresentacaoVO anexoApresentacaoVO;

                        // Anexa o arquivo.
                        anexoApresentacaoVO =
                                AnexoApresentacaoMediator.get().anexarArquivo(apresentacaoVO.getPk(), clientFileName,
                                        campo, inputStream);

                        // Atualiza o anexo da apresentação.
                        apresentacaoVO.addOrReplace(anexoApresentacaoVO);

                        // Atualiza dados de última atualização.
                        atualizarUltimaAtualizacao(target);

                        // Atualiza os componentes.
                        target.add(tabelaAnexoApresentacao, tabelaAnexoApresentacao.getMarkupId());
                        target.add(fileUploadFieldArquivo);
                    }
                };

        botaoAnexarArquivo.setMarkupId(ID_BOTAO_ANEXAR_AQUIVO + campo.getIdTag());
        addOrReplace(botaoAnexarArquivo);
    }

    public List<FileUpload> getFilesUpload() {
        return filesUpload;
    }

    public void setFilesUpload(List<FileUpload> filesUpload) {
        this.filesUpload = filesUpload;
    }

}
