package br.gov.bcb.sisaps.web.page.dominio.gerenciaes;

import br.gov.bcb.sisaps.src.dominio.GrauPreocupacaoES;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.mediator.GrauPreocupacaoESMediator;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.LabelLinhas;

public class PainelGrauPreocupacao extends PainelSisAps {

    private static final String ATUALIZADO_POR = "Atualizado por ";
    private static final String PONTO = ".";
    private GrauPreocupacaoES grauPreocupacaoESVigente;
    private final PerfilRisco perfilRisco;
    
    public PainelGrauPreocupacao(String id, Integer cicloId, PerfilRisco perfilRisco) {
        super(id);
        this.perfilRisco = perfilRisco;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        grauPreocupacaoESVigente = GrauPreocupacaoESMediator.get().buscarPorPerfilRisco(perfilRisco.getPk());
        addGrauPreocupacaoVigente();
        setVisibilityAllowed(grauPreocupacaoESVigente != null
                && grauPreocupacaoESVigente.getParametroGrauPreocupacao() != null);
    }

    private void addGrauPreocupacaoVigente() {
        String strGrauPreocupacao = "";
        String strDataVigente = "";
        if (grauPreocupacaoESVigente != null && grauPreocupacaoESVigente.getPk() == null) {
            strDataVigente = ATUALIZADO_POR + grauPreocupacaoESVigente.getNomeOperadorEncaminhamentoDataHora() + PONTO;
            strGrauPreocupacao = grauPreocupacaoESVigente.getDescricaoNotaFinal() + " (Corec)";
        } else if (grauPreocupacaoESVigente != null) {
            strDataVigente =
                    ATUALIZADO_POR + grauPreocupacaoESVigente.getNomeOperadorEncaminhamentoDataHoraPublicacao() + PONTO;
            strGrauPreocupacao = grauPreocupacaoESVigente.getDescricaoNotaFinal();
        }
        addOrReplace(new LabelLinhas("idDataVigente", strDataVigente));
        addOrReplace(new LabelLinhas("idGrauPreocupacaoVigente", strGrauPreocupacao));
    }


}
