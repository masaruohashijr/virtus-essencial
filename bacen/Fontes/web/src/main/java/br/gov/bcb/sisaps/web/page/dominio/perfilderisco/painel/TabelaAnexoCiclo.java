package br.gov.bcb.sisaps.web.page.dominio.perfilderisco.painel;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.mediator.AnexoCicloMediator;
import br.gov.bcb.sisaps.src.vo.AnexoCicloVO;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Configuracao;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.edicao.TabelaAnexoComum;

public class TabelaAnexoCiclo extends TabelaAnexoComum<AnexoCicloVO> {

    private final PerfilRisco perfilRisco;

    public TabelaAnexoCiclo(String id, final PerfilRisco perfilRisco) {
        super(id, new AbstractReadOnlyModel<List<AnexoCicloVO>>() {

            @Override
            public List<AnexoCicloVO> getObject() {
                return AnexoCicloVO.converterParaListaVo(AnexoCicloMediator.get().buscar(perfilRisco));
            }
        }, false, configureTabela(), "AnexoCiclo", false, "nomeArquivoAnexo", false);
        this.perfilRisco = perfilRisco;
        setOutputMarkupId(true);
    }

    @Override
    protected void excluirAnexo(AjaxRequestTarget target, IModel<AnexoCicloVO> rowModel) {
        // TODO não há exclusão de anexo de ciclo no momento
    }

    @Override
    protected void onDownLoad(AnexoCicloVO vo) {
        String link = vo.getLink();
        try {
            byte[] arquivoBytes =
                    AnexoCicloMediator.get().recuperarArquivo(link, perfilRisco.getCiclo(), vo.getVersaoPerfilRisco());
            executarDowload(link, arquivoBytes);
        } catch (NegocioException e) {
            error("Arquivo '" + link + "' não foi encontrado.");
        }
    }

    private static Configuracao configureTabela() {
        Configuracao cfg = new Configuracao("idTituloAnexosCiclo", "idDadosAnexosCiclo");
        cfg.setMensagemVazio(Model.of("Nenhum arquivo anexado."));
        cfg.setExibirTituloHeader(false);
        cfg.setExibirTitulo(false);
        return cfg;
    }

}
