package br.gov.bcb.sisaps.web.page.dominio.analisequantitativaaqt;

import org.apache.wicket.markup.html.basic.Label;

import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ElementoAQT;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.web.page.PainelSisAps;

public class PainelAnaliseVigente extends PainelSisAps {

    private static final String BRANCO = "";
    private final ElementoAQT elementoARCVigente;
    private final ElementoAQT elemento;
    private final AnaliseQuantitativaAQT aqt;

    public PainelAnaliseVigente(String id, ElementoAQT elemento, ElementoAQT elementoARCVigente,
            AnaliseQuantitativaAQT aqt) {
        super(id);
        this.elemento = elemento;
        this.elementoARCVigente = elementoARCVigente;
        this.aqt = aqt;
        addComponentes();
    }

    private void addComponentes() {
        addValorAvaliacaoVigente();
    }

    private void addValorAvaliacaoVigente() {
        String justificativaSupervisorVigente = "";
        if (AnaliseQuantitativaAQTMediator.get().estadoConcluido(aqt.getEstado())) {
            justificativaSupervisorVigente =
                    elemento == null || elemento.getJustificativaSupervisor() == null ? BRANCO : elemento
                            .getJustificativaSupervisor();
        } else {
            justificativaSupervisorVigente =
                    elementoARCVigente == null || elementoARCVigente.getJustificativaSupervisor() == null ? BRANCO
                            : elementoARCVigente.getJustificativaSupervisor();
        }

        addOrReplace(new Label("idAvaliacaoVigente", justificativaSupervisorVigente).setEscapeModelStrings(false));
    }

    public ElementoAQT getElemento() {
        return elemento;
    }

}
