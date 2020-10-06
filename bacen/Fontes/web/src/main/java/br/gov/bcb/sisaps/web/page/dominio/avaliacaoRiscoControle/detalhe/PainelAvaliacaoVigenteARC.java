package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.detalhe;

import org.apache.commons.collections.CollectionUtils;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Documento;
import br.gov.bcb.sisaps.src.dominio.ItemElemento;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.web.page.componentes.LabelLinhas;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.edicao.TabelaAnexoDocumento;

public class PainelAvaliacaoVigenteARC extends Panel {

    private final Ciclo ciclo;
    private final AvaliacaoRiscoControle arc;
    private final PropertyModel<String> modelJustificativaVigente;
    private final ItemElemento itemElementoARCVigente;
    private final Documento documento;
    private final String titulo;

    public PainelAvaliacaoVigenteARC(String id, Ciclo ciclo, AvaliacaoRiscoControle arc, 
            PropertyModel<String> modelJustificativaVigente, ItemElemento itemElementoARCVigente, 
            Documento documento, String titulo) {
        super(id);
        this.ciclo = ciclo;
        this.arc = arc;
        this.modelJustificativaVigente = modelJustificativaVigente;
        this.itemElementoARCVigente = itemElementoARCVigente;
        this.documento = documento;
        this.titulo = titulo;
    }
    
    @Override
    protected void onInitialize() {
        super.onInitialize();
        addComponentes();
    }

    private void addComponentes() {
        tituloAvaliacao();
        addTitulo();
    }
    
    private void addTitulo() {
        LabelLinhas labelTitulo = new LabelLinhas("tituloAvaliacaoVigente", titulo);
        labelTitulo.setVisible(AvaliacaoRiscoControleMediator.get().estadoPreenchido(arc.getEstado())
                || AvaliacaoRiscoControleMediator.get().estadoEmAnalise(arc.getEstado())
                || AvaliacaoRiscoControleMediator.get().estadoAnaliseDelegada(arc.getEstado()));
        addOrReplace(labelTitulo);
    }

    private void tituloAvaliacao() {
        LabelLinhas labelJustVigente = new LabelLinhas("idJustificativaVigente", modelJustificativaVigente);
        labelJustVigente.setEscapeModelStrings(false);
        labelJustVigente.setEscapeModelStrings(false);
        addOrReplace(labelJustVigente);

        TabelaAnexoDocumento anexoDocumento =
                new TabelaAnexoDocumento("idTabelaAnexoDocumentoVigente",
                        itemElementoARCVigente, ciclo, false);
        anexoDocumento.setVisibilityAllowed(documento != null 
                && CollectionUtils.isNotEmpty(documento.getAnexosItemElemento()));
        addOrReplace(anexoDocumento);
    }

}
