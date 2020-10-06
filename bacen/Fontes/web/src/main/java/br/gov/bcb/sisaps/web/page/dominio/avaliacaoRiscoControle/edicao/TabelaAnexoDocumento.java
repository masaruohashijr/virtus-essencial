package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.edicao;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.gov.bcb.sisaps.src.dominio.AnexoDocumento;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.ItemElemento;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoEntidadeAnexoDocumentoEnum;
import br.gov.bcb.sisaps.src.mediator.AnexoDocumentoMediator;
import br.gov.bcb.sisaps.src.mediator.ItemElementoMediator;
import br.gov.bcb.sisaps.src.vo.AnexoDocumentoVo;
import br.gov.bcb.sisaps.util.validacao.NegocioException;

public class TabelaAnexoDocumento extends TabelaAnexoComum<AnexoDocumentoVo> {

    @SpringBean
    private ItemElementoMediator itemElementoMediator;
    private ItemElemento itemElemento;
    private Ciclo ciclo;

    public TabelaAnexoDocumento(String id, final ItemElemento itemElemento, Ciclo ciclo, final boolean isEdicao) {
        super(id, new AbstractReadOnlyModel<List<AnexoDocumentoVo>>() {
            @Override
            public List<AnexoDocumentoVo> getObject() {
                List<AnexoDocumento> anexosItem = AnexoDocumentoMediator.get().buscar(itemElemento);
                return AnexoDocumentoVo.converterParaListaVo(anexosItem);
            }
        }, isEdicao, null, "AnexoDocumento", true);
        this.ciclo = ciclo;
    }

    @Override
    protected void excluirAnexo(AjaxRequestTarget target, IModel<AnexoDocumentoVo> model) {
        itemElemento = itemElementoMediator.obterItemElementoPorDocumento(model.getObject().getDocumento());
        AnexoDocumentoMediator.get().excluirAnexo(model.getObject(), itemElemento,
                TipoEntidadeAnexoDocumentoEnum.ITEM_ELEMENTO, ciclo);
        target.add(tabela, tabela.getMarkupId());
    }

    @Override
    protected void onDownLoad(AnexoDocumentoVo vo) {
        String link = vo.getLink();
        try {
            itemElemento = itemElementoMediator.obterItemElementoPorDocumento(vo.getDocumento());
            byte[] arquivoBytes =
                    AnexoDocumentoMediator.get().recuperarArquivo(link, itemElemento,
                            TipoEntidadeAnexoDocumentoEnum.ITEM_ELEMENTO, ciclo);
            executarDowload(link, arquivoBytes);
        } catch (NegocioException e) {
            error("Arquivo '" + link + "' não foi encontrado.");
        }
    }

}
