package br.gov.bcb.sisaps.src.validacao;

import java.util.ArrayList;

import br.gov.bcb.sisaps.src.dominio.Atividade;
import br.gov.bcb.sisaps.src.dominio.CelulaRiscoControle;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;

public class RegraTransferirARC {

    private Atividade atividadeNova;
    private CelulaRiscoControle celulaRiscoControle;

    public RegraTransferirARC(Atividade nova, CelulaRiscoControle celulaRiscoControle) {
        this.atividadeNova = nova;
        this.celulaRiscoControle = celulaRiscoControle;
    }

    public void validarTransferencia() {

        ArrayList<ErrorMessage> erros = new ArrayList<ErrorMessage>();

        SisapsUtil.adicionarErro(erros, new ErrorMessage(
                "Campo \"Alterar associação para atividade\" é de preenchimento obrigatório."), atividadeNova == null);
        
        if (atividadeNova != null) {
            SisapsUtil.adicionarErro(erros, new ErrorMessage("Atividade já associada ao ARC anteriormente."),
                    atividadePossuiArcComOMesmoGrupo());
        }
        SisapsUtil.lancarNegocioException(erros);

    }

    public boolean atividadePossuiArcComOMesmoGrupo() {
        for (CelulaRiscoControle celula : atividadeNova.getCelulasRiscoControle()) {
            if (celulaRiscoControle.getParametroGrupoRiscoControle().equals(celula.getParametroGrupoRiscoControle())) {
                return true;
            }
        }
        return false;
    }
}
