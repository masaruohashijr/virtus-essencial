package br.gov.bcb.sisaps.web.page.dominio.analisequantitativaaqt;

import org.apache.commons.collections.CollectionUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

import br.gov.bcb.sisaps.src.dominio.Documento;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ItemElementoAQT;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.web.page.componentes.LabelLinhas;

public class PainelAvaliacaoVigenteAnef extends Panel {

    private final PropertyModel<String> modelJustificativa;
    private final Documento documento;
    private Label labelJustVigente;
    private Label labelTitulo;
    private final AnaliseQuantitativaAQT aqt;
    private final String titulo;
    private final ItemElementoAQT itemElementoANEFVigente;

    public PainelAvaliacaoVigenteAnef(String id, AnaliseQuantitativaAQT aqt, PropertyModel<String> modelJustificativa,
            ItemElementoAQT itemElementoANEFVigente, Documento documento, String titulo) {
        super(id);
        this.aqt = aqt;
        this.modelJustificativa = modelJustificativa;
        this.itemElementoANEFVigente = itemElementoANEFVigente;
        this.documento = documento;
        this.titulo = titulo;
        tituloAvaliacao();
        addTitulo();
    }

    private void addTitulo() {
        labelTitulo = new LabelLinhas("tituloAvaliacaoVigente", titulo);
        labelTitulo.setVisible(AnaliseQuantitativaAQTMediator.get().estadoPreenchido(aqt.getEstado())
                || AnaliseQuantitativaAQTMediator.get().estadoEmAnalise(aqt.getEstado())
                || AnaliseQuantitativaAQTMediator.get().estadoAnaliseDelegada(aqt.getEstado()));
        addOrReplace(labelTitulo);
    }

    private void tituloAvaliacao() {
        labelJustVigente = new LabelLinhas("idJustificativaVigente", modelJustificativa);
        labelJustVigente.setEscapeModelStrings(false);
        labelJustVigente.setEscapeModelStrings(false);
        addOrReplace(labelJustVigente);

        TabelaAnexoDocumentoAQT anexoDocumento =
                new TabelaAnexoDocumentoAQT("idTabelaAnexoDocumentoVigente",
                        itemElementoANEFVigente, aqt.getCiclo(), false);
        anexoDocumento.setVisibilityAllowed(documento != null 
                && CollectionUtils.isNotEmpty(documento.getAnexosItemElemento()));
        addOrReplace(anexoDocumento);
    }

    public Label getLabelJustVigente() {
        return labelJustVigente;
    }

    public void setLabelJustVigente(Label labelJustVigente) {
        this.labelJustVigente = labelJustVigente;
    }

}
