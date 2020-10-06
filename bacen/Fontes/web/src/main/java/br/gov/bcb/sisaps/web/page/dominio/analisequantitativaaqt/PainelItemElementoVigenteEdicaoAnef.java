package br.gov.bcb.sisaps.web.page.dominio.analisequantitativaaqt;

import org.apache.wicket.markup.html.basic.Label;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Documento;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ItemElementoAQT;
import br.gov.bcb.sisaps.web.page.PainelSisAps;

public class PainelItemElementoVigenteEdicaoAnef extends PainelSisAps {
    private final ItemElementoAQT itemElementoARCVigente;
    private final AnaliseQuantitativaAQT aqt;
    private final Ciclo ciclo;
    private final Documento documentoVigente;

    public PainelItemElementoVigenteEdicaoAnef(String id, AnaliseQuantitativaAQT aqt, Ciclo ciclo,
            ItemElementoAQT itemElementoARCVigente, Documento documentoVigente) {
        super(id);
        this.aqt = aqt;
        this.ciclo = ciclo;
        this.itemElementoARCVigente = itemElementoARCVigente;
        this.documentoVigente = documentoVigente;
        addOrReplace(new TabelaAnexoDocumentoAQT("idTabelaAnexoDocumentoVigente", itemElementoARCVigente, ciclo, false)
                .setVisibilityAllowed(documentoVigente != null && documentoVigente.getAnexosItemElemento().size() != 0));
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        addComponentes();
        addTitulo();
    }


    private void addTitulo() {
        Label lblTitulo = new Label("idTituloVigente", "Avaliação vigente");
        lblTitulo.setVisible(documentoVigente == null);
        addOrReplace(lblTitulo);
    }

    private void addComponentes() {
        addOrReplace(new Label("idAvaliacaoVigente", itemElementoARCVigente != null
                && itemElementoARCVigente.getDocumento() != null ? itemElementoARCVigente.getDocumento()
                .getJustificativaDetalhe() : "").setEscapeModelStrings(false));
    }

    public Ciclo getCiclo() {
        return ciclo;
    }

    public AnaliseQuantitativaAQT getAqt() {
        return aqt;
    }

}