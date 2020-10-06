package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.edicao;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.mediator.AnexoPosCorecMediator;
import br.gov.bcb.sisaps.src.vo.AnexoPosCorecVO;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Configuracao;

public class TabelaAnexoPosCorec extends TabelaAnexoComum<AnexoPosCorecVO> {

    private Ciclo ciclo;
    private boolean isModal;

    public TabelaAnexoPosCorec(String id, final Ciclo ciclo, final String tipo, boolean isEdicao, boolean isModal) {
        super(id, new AbstractReadOnlyModel<List<AnexoPosCorecVO>>() {

            @Override
            public List<AnexoPosCorecVO> getObject() {
                return AnexoPosCorecVO.converterParaListaVo(AnexoPosCorecMediator.get().listarAnexos(ciclo, tipo));
            }
        }, isEdicao, configureTabela(), "AnexoArc", false, "link", isModal);
        this.ciclo = ciclo;
        this.isModal = isModal;
        setOutputMarkupId(true);
    }

    @Override
    protected void excluirAnexo(AjaxRequestTarget target, IModel<AnexoPosCorecVO> rowModel) {
    }

    @Override
    protected void onDownLoad(AnexoPosCorecVO vo) {
        String link = null;
        try {
            link = vo.getLink();
            byte[] arquivoBytes = AnexoPosCorecMediator.get().recuperarArquivo(link, ciclo, vo.getTipo());
            executarDowload(link, arquivoBytes);
        } catch (NegocioException e) {
            error("Arquivo '" + link + "' não foi encontrado.");
        }
    }

    private static Configuracao configureTabela() {
        Configuracao cfg = new Configuracao("idTitulo", "idDados");
        cfg.setMensagemVazio(Model.of("Nenhum arquivo anexado."));
        cfg.setExibirTituloHeader(false);
        cfg.setExibirTitulo(false);
        cfg.setCssImpar(Model.of("fundoPadraoAClaro2"));
        cfg.setCssPar(Model.of("fundoPadraoAClaro3"));

        return cfg;
    }

}
