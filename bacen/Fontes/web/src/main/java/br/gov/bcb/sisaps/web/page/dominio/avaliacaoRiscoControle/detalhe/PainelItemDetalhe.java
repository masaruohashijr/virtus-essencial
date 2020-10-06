package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.detalhe;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Elemento;
import br.gov.bcb.sisaps.src.dominio.ItemElemento;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.ItemElementoMediator;

public class PainelItemDetalhe extends Panel {
    private static final String ID_PAINEL_DOCUMENTO_UPLOAD_ANEXO = "idPainelDocumentoUploadAnexo";
    private final List<ItemElemento> itensElementoARCVigente = new ArrayList<ItemElemento>();
    private final AvaliacaoRiscoControle avaliacao;

    public PainelItemDetalhe(String id, List<ItemElemento> itensElemento, final Elemento elementoARCVigente,
            final Ciclo ciclo, AvaliacaoRiscoControle avaliacao) {
        super(id);
        this.avaliacao = avaliacao;
        if (elementoARCVigente != null) {
            itensElementoARCVigente.addAll(elementoARCVigente.getItensElemento());
        }
        ListView<ItemElemento> listView = new ListView<ItemElemento>("listaItens", itensElemento) {
            @Override
            protected void populateItem(ListItem<ItemElemento> item) {
                WebMarkupContainer tabelaDadosElemento = new WebMarkupContainer("tabelaDadosItem");
                final ItemElemento itemElemento = item.getModelObject();
                addDadosTabela(ciclo, item, tabelaDadosElemento, itemElemento);
            }

        };
        add(listView);
    }

    private void addDadosTabela(final Ciclo ciclo, ListItem<ItemElemento> item,
            WebMarkupContainer tabelaDadosElemento, final ItemElemento itemElemento) {

        tabelaDadosElemento.add(new Label("idTituloItem", new PropertyModel<String>(itemElemento,
                "parametroItemElemento.nome")));

        ItemElemento itemElementoARCVigente =
                ItemElementoMediator.get().obterItemElementoCorrespondenteARCVigente(itensElementoARCVigente,
                        itemElemento);

        boolean exibirColunaInspetor = AvaliacaoRiscoControleMediator.get().exibirColunaInspetor(avaliacao.getEstado());
        boolean exibirColunaVigente = AvaliacaoRiscoControleMediator.get().exibirColunaVigente(avaliacao.getEstado());
        tabelaDadosElemento.addOrReplace(new PainelDocumentoDetalheARC(ID_PAINEL_DOCUMENTO_UPLOAD_ANEXO, ciclo, 
                itemElemento, itemElementoARCVigente, avaliacao, exibirColunaInspetor, exibirColunaVigente));

        tabelaDadosElemento.setMarkupId(tabelaDadosElemento.getId() + item.getModelObject().getPk());
        item.add(tabelaDadosElemento);
    }

}
