package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.detalhe;

import org.apache.wicket.markup.html.basic.Label;

import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Elemento;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.web.page.PainelSisAps;

public class PainelAnaliseVigenteARC extends PainelSisAps {

    private final Elemento elemento;
    private final Elemento elementoARCVigente;
    private final AvaliacaoRiscoControle arc;

    public PainelAnaliseVigenteARC(String id, Elemento elemento, Elemento elementoARCVigente, AvaliacaoRiscoControle arc) {
        super(id);
        this.elemento = elemento;
        this.elementoARCVigente = elementoARCVigente;
        this.arc = arc;
    }
    
    @Override
    protected void onInitialize() {
        super.onInitialize();
        addComponentes();
    }
    
    private void addComponentes() {
        addValorAvaliacaoVigente();
    }

    private void addValorAvaliacaoVigente() {
        String justificativaSupervisorVigente = "";
        if (AvaliacaoRiscoControleMediator.get().estadoConcluido(arc.getEstado())) {
            justificativaSupervisorVigente =
                    elemento == null || elemento.getJustificativaSupervisor() == null ? 
                            Constantes.ESPACO_EM_BRANCO : elemento.getJustificativaSupervisor();
        } else {
            justificativaSupervisorVigente =
                    elementoARCVigente == null || elementoARCVigente.getJustificativaSupervisor() == null ? 
                            Constantes.ESPACO_EM_BRANCO : elementoARCVigente.getJustificativaSupervisor();
        }

        addOrReplace(new Label("idAvaliacaoVigente", justificativaSupervisorVigente).setEscapeModelStrings(false));
    }

}
