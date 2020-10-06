package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.analise;

import org.apache.wicket.markup.html.basic.Label;

import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Documento;
import br.gov.bcb.sisaps.src.dominio.Elemento;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.edicao.TabelaAnexoDocumento;

public class PainelDetalharAnaliseEmEdicao extends PainelSisAps {
    private final Elemento elementoARCVigente;
    private final AvaliacaoRiscoControle avaliacao;
    private final Ciclo ciclo;
    private final Elemento elemento;

    public PainelDetalharAnaliseEmEdicao(String id, AvaliacaoRiscoControle avaliacao, Ciclo ciclo, Elemento elemento,
            Elemento elementoARCVigente) {
        this(id, avaliacao, ciclo, elemento, elementoARCVigente, null);
    }

    public PainelDetalharAnaliseEmEdicao(String id, AvaliacaoRiscoControle avaliacao, Ciclo ciclo, Elemento elemento,
            Elemento elementoARCVigente, Documento documentoVigente) {
        super(id);
        this.avaliacao = avaliacao;
        this.ciclo = ciclo;
        this.elemento = elemento;
        this.elementoARCVigente = elementoARCVigente;
        addOrReplace(new TabelaAnexoDocumento("idTabelaAnexoDocumentoVigente", null, ciclo, false)
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

    public AvaliacaoRiscoControle getAvaliacao() {
        return avaliacao;
    }

    public Elemento getElemento() {
        return elemento;
    }

}