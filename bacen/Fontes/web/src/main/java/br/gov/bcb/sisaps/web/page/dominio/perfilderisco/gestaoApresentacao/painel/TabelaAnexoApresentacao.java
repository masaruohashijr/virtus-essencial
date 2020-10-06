package br.gov.bcb.sisaps.web.page.dominio.perfilderisco.gestaoApresentacao.painel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

import br.gov.bcb.sisaps.src.mediator.AnexoApresentacaoMediator;
import br.gov.bcb.sisaps.src.vo.AnexoApresentacaoVO;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.edicao.TabelaAnexoComum;

public class TabelaAnexoApresentacao extends TabelaAnexoComum<AnexoApresentacaoVO> {

    // O model.
    private final ModelAnexosApresentacao modelAnexosApresentacao;

    // Construtor
    public TabelaAnexoApresentacao(String id, ModelAnexosApresentacao modelAnexosApresentacao) {
        super(id, modelAnexosApresentacao, true, null, "AnexoApresentacao", true);
        setOutputMarkupId(true);

        // Guarda o model.
        this.modelAnexosApresentacao = modelAnexosApresentacao;
    }

    @Override
    protected void excluirAnexo(AjaxRequestTarget target, IModel<AnexoApresentacaoVO> model) {
        // Exclui o anexo.
        modelAnexosApresentacao.excluirAnexo(target, model.getObject());

        // Atualiza a tabela de anexos.
        target.add(tabela, tabela.getMarkupId());
    }

    @Override
    protected void onDownLoad(AnexoApresentacaoVO vo) {

        try {
            // Declarações
            byte[] arquivoBytes;

            // Recupera o arquivo.
            arquivoBytes = AnexoApresentacaoMediator.get().recuperarArquivo(vo);

            // Faz o download.
            executarDowload(vo.getLink(), arquivoBytes);
        } catch (NegocioException e) {
            error("Arquivo '" + vo.getLink() + "' não foi encontrado.");
        }
    }

}
