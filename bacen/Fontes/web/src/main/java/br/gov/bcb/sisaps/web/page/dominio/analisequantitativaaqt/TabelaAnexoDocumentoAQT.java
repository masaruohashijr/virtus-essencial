package br.gov.bcb.sisaps.web.page.dominio.analisequantitativaaqt;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.gov.bcb.sisaps.src.dominio.AnexoDocumento;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ItemElementoAQT;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoEntidadeAnexoDocumentoEnum;
import br.gov.bcb.sisaps.src.mediator.AnexoDocumentoMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.ItemElementoAQTMediator;
import br.gov.bcb.sisaps.src.vo.AnexoDocumentoVo;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Configuracao;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.edicao.TabelaAnexoComum;

public class TabelaAnexoDocumentoAQT extends TabelaAnexoComum<AnexoDocumentoVo> {

    @SpringBean
    private ItemElementoAQTMediator itemElementoMediator;
    private ItemElementoAQT itemElemento;
    private Ciclo ciclo;

    public TabelaAnexoDocumentoAQT(String id, ItemElementoAQT itemElemento, Ciclo ciclo, final boolean isEdicao) {
        super(id, obterModel(itemElemento), isEdicao, configureTabela(), "AnexoDocumento", true);
        this.ciclo = ciclo;
        setOutputMarkupId(true);
    }

    private static AbstractReadOnlyModel<List<AnexoDocumentoVo>> obterModel(final ItemElementoAQT itemElementoAQT) {
        return new AbstractReadOnlyModel<List<AnexoDocumentoVo>>() {
            @Override
            public List<AnexoDocumentoVo> getObject() {
                List<AnexoDocumento> anexosItem = AnexoDocumentoMediator.get().buscar(itemElementoAQT);
                return AnexoDocumentoVo.converterParaListaVo(anexosItem);
            }
        };
    }

    @Override
    protected void excluirAnexo(AjaxRequestTarget target, IModel<AnexoDocumentoVo> model) {
        itemElemento = itemElementoMediator.obterItemElementoPorDocumento(model.getObject().getDocumento());
        AnexoDocumentoMediator.get().excluirAnexo(model.getObject(), itemElemento,
                TipoEntidadeAnexoDocumentoEnum.ITEM_ELEMENTO_AQT, ciclo);
        target.add(tabela, tabela.getMarkupId());
    }

    @Override
    protected void onDownLoad(AnexoDocumentoVo vo) {
        String link = vo.getLink();
        try {
            itemElemento = itemElementoMediator.obterItemElementoPorDocumento(vo.getDocumento());
            byte[] arquivoBytes =
                    AnexoDocumentoMediator.get().recuperarArquivo(link, itemElemento,
                            TipoEntidadeAnexoDocumentoEnum.ITEM_ELEMENTO_AQT, ciclo);
            executarDowload(link, arquivoBytes);
        } catch (NegocioException e) {
            error("Arquivo '" + link + "' não foi encontrado.");
        }
    }

    private static Configuracao configureTabela() {
        Configuracao cfg = new Configuracao("idTitulo", "idDados");
        cfg.setMensagemVazio(Model.of(""));
        cfg.setExibirTituloHeader(false);
        cfg.setExibirTitulo(false);
        return cfg;
    }

}
