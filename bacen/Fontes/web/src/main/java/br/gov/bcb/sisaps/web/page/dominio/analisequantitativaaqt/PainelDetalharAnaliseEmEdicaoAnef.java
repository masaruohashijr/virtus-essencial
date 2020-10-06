package br.gov.bcb.sisaps.web.page.dominio.analisequantitativaaqt;

import org.apache.wicket.markup.html.basic.Label;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Documento;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ElementoAQT;
import br.gov.bcb.sisaps.web.page.PainelSisAps;

public class PainelDetalharAnaliseEmEdicaoAnef extends PainelSisAps {
    private final ElementoAQT elementoARCVigente;
    private Ciclo ciclo;
    private final ElementoAQT elemento;

    public PainelDetalharAnaliseEmEdicaoAnef(String id, AnaliseQuantitativaAQT aqt,
            ElementoAQT elemento, ElementoAQT elementoARCVigente) {
        this(id, aqt, elemento, elementoARCVigente, null);
    }

    public PainelDetalharAnaliseEmEdicaoAnef(String id, AnaliseQuantitativaAQT aqt, ElementoAQT elemento,
            ElementoAQT elementoARCVigente, Documento documentoVigente) {
        super(id);
        this.elemento = elemento;
        this.elementoARCVigente = elementoARCVigente;
        addOrReplace(new TabelaAnexoDocumentoAQT("idTabelaAnexoDocumentoVigente", null, aqt.getCiclo(), false)
                .setVisibilityAllowed(documentoVigente != null && documentoVigente.getAnexosItemElemento().size() != 0));
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        addComponentes();
    }

    private void addComponentes() {
        String justificativaSupervisorVigente =
                elementoARCVigente == null || elementoARCVigente.getJustificativaSupervisor() == null ? ""
                        : elementoARCVigente.getJustificativaSupervisor();
        addOrReplace(new Label("idAvaliacaoVigente", justificativaSupervisorVigente).setEscapeModelStrings(false));
    }

    public Ciclo getCiclo() {
        return ciclo;
    }

    public ElementoAQT getElemento() {
        return elemento;
    }

}