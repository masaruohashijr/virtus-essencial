package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.edicao;

import org.apache.wicket.markup.html.basic.Label;

import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Documento;
import br.gov.bcb.sisaps.src.dominio.ItemElemento;
import br.gov.bcb.sisaps.web.page.PainelSisAps;

public class PainelItemElementoVigenteEdicao extends PainelSisAps {
    private final ItemElemento itemElementoARCVigente;
    private final AvaliacaoRiscoControle avaliacao;
    private final Ciclo ciclo;

    public PainelItemElementoVigenteEdicao(String id, AvaliacaoRiscoControle avaliacao, Ciclo ciclo,
            ItemElemento itemElementoARCVigente, Documento documentoVigente) {
        super(id);
        this.avaliacao = avaliacao;
        this.ciclo = ciclo;
        this.itemElementoARCVigente = itemElementoARCVigente;
        addOrReplace(new TabelaAnexoDocumento("idTabelaAnexoDocumentoVigente", itemElementoARCVigente, ciclo, false)
                .setVisibilityAllowed(documentoVigente != null && documentoVigente.getAnexosItemElemento().size() != 0));
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        addComponentes();
    }

    private void addComponentes() {
        addOrReplace(new Label("idAvaliacaoVigente", itemElementoARCVigente != null
                && itemElementoARCVigente.getDocumento() != null ? itemElementoARCVigente.getDocumento()
                .getJustificativaDetalhe() : "").setEscapeModelStrings(false));
    }

    public Ciclo getCiclo() {
        return ciclo;
    }

    public AvaliacaoRiscoControle getAvaliacao() {
        return avaliacao;
    }

}