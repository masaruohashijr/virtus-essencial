package br.gov.bcb.sisaps.web.page.dominio.analisequantitativa.anexo;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import br.gov.bcb.sisaps.src.dominio.AnexoQuadroPosicaoFinanceira;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.QuadroPosicaoFinanceira;
import br.gov.bcb.sisaps.src.mediator.AnexoQuadroPosicaoFinanceiraMediator;
import br.gov.bcb.sisaps.src.vo.AnexoQPFVo;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Configuracao;
import br.gov.bcb.sisaps.web.page.dominio.analisequantitativa.GerenciarQuadroPosicaoFinanceira;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.edicao.TabelaAnexoComum;

public class TabelaAnexoQPF extends TabelaAnexoComum<AnexoQPFVo> {

    private QuadroPosicaoFinanceira quadro;

    public TabelaAnexoQPF(String id, final QuadroPosicaoFinanceira quadro, boolean isEdicao) {
        super(id, new AbstractReadOnlyModel<List<AnexoQPFVo>>() {

            @Override
            public List<AnexoQPFVo> getObject() {
                return AnexoQPFVo.converterParaListaVo(anexos(quadro));
            }
        }, isEdicao, configureTabela(), "AnexoArc", true);
        this.quadro = quadro;
        setOutputMarkupId(true);
    }

    private static List<AnexoQuadroPosicaoFinanceira> anexos(final QuadroPosicaoFinanceira quadro) {
        if (quadro != null && quadro.getPk() != null) {
            return AnexoQuadroPosicaoFinanceiraMediator.get().buscar(quadro);
        }
        return new ArrayList<AnexoQuadroPosicaoFinanceira>();
    }

    @Override
    protected void excluirAnexo(AjaxRequestTarget target, IModel<AnexoQPFVo> rowModel) {
        AnexoQuadroPosicaoFinanceiraMediator.get().excluirAnexo(rowModel.getObject(), quadro);
        target.add(tabela, tabela.getMarkupId());
        GerenciarQuadroPosicaoFinanceira gerenciarQuadroPosicaoFinanceira =
                (GerenciarQuadroPosicaoFinanceira) getPage();
        gerenciarQuadroPosicaoFinanceira.lancarInfoAjax("Anexo excluído com sucesso.", TabelaAnexoQPF.this, target);
        target.add(gerenciarQuadroPosicaoFinanceira.getPainelQuadroPosicaoFinanceira());
        target.add(gerenciarQuadroPosicaoFinanceira.getPainelAnexo());
    }

    @Override
    protected void onDownLoad(AnexoQPFVo vo) {
        String link = vo.getLink();
        try {
            byte[] arquivoBytes = AnexoQuadroPosicaoFinanceiraMediator.get().recuperarArquivo(link, quadro);
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
        return cfg;
    }

}
