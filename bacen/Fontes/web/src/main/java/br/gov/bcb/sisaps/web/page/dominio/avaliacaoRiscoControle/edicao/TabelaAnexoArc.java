package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.edicao;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import br.gov.bcb.sisaps.src.dominio.AnexoARC;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.mediator.AnexoArcMediator;
import br.gov.bcb.sisaps.src.vo.AnexoArcVo;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Configuracao;

public class TabelaAnexoArc extends TabelaAnexoComum<AnexoArcVo> {

    private Ciclo ciclo;
    private AvaliacaoRiscoControle arc;

    public TabelaAnexoArc(String id, Ciclo ciclo, final AvaliacaoRiscoControle arc, boolean isEdicao) {
        super(id, new AbstractReadOnlyModel<List<AnexoArcVo>>() {

            @Override
            public List<AnexoArcVo> getObject() {
                return AnexoArcVo.converterParaListaVo(anexos(arc));
            }
        }, isEdicao, configureTabela(), "AnexoArc", true);
        this.ciclo = ciclo;
        this.arc = arc;
        setOutputMarkupId(true);
    }

    private static List<AnexoARC> anexos(final AvaliacaoRiscoControle arc) {
        if (arc.getPk() != null) {
            return AnexoArcMediator.get().buscar(arc);
        }
        return new ArrayList<AnexoARC>();
    }

    @Override
    protected void excluirAnexo(AjaxRequestTarget target, IModel<AnexoArcVo> rowModel) {
        AnexoArcMediator.get().excluirAnexo(rowModel.getObject(), ciclo, arc);
        target.add(tabela, tabela.getMarkupId());
    }

    @Override
    protected void onDownLoad(AnexoArcVo vo) {
        String link = vo.getLink();
        try {
            byte[] arquivoBytes = AnexoArcMediator.get().recuperarArquivo(link, ciclo, arc);
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
