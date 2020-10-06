package br.gov.bcb.sisaps.web.page.dominio.analisequantitativaaqt;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnexoAQT;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnexoAQTMediator;
import br.gov.bcb.sisaps.src.vo.analisequantitativa.aqt.AnexoAQTVO;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Configuracao;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.edicao.TabelaAnexoComum;

public class TabelaAnexoAqt extends TabelaAnexoComum<AnexoAQTVO> {

    private Ciclo ciclo;
    private AnaliseQuantitativaAQT aqt;

    public TabelaAnexoAqt(String id, Ciclo ciclo, final AnaliseQuantitativaAQT aqt, boolean isEdicao) {
        super(id, new AbstractReadOnlyModel<List<AnexoAQTVO>>() {

            @Override
            public List<AnexoAQTVO> getObject() {
                return AnexoAQTVO.converterParaListaVo(anexos(aqt));
            }
        }, isEdicao, configureTabela(), "AnexoArc", true);
        this.ciclo = ciclo;
        this.aqt = aqt;
        setOutputMarkupId(true);
    }

    private static List<AnexoAQT> anexos(final AnaliseQuantitativaAQT aqt) {
        if (aqt.getPk() != null) {
            return AnexoAQTMediator.get().buscar(aqt);
        }
        return new ArrayList<AnexoAQT>();
    }

    @Override
    protected void excluirAnexo(AjaxRequestTarget target, IModel<AnexoAQTVO> rowModel) {
        String mensagemSucesso = AnexoAQTMediator.get().excluirAnexo(rowModel.getObject(), ciclo, aqt);
        target.add(tabela, tabela.getMarkupId());
        target.add(getParent());
        getPage().success(mensagemSucesso);
    }

    @Override
    protected void onDownLoad(AnexoAQTVO vo) {

        String link = vo.getLink();
        try {
            byte[] arquivoBytes = AnexoAQTMediator.get().recuperarArquivo(link, ciclo, aqt);
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
