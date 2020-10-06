package br.gov.bcb.sisaps.web.page.dominio.perfilderisco.painel;

import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.web.page.PainelSisAps;

@SuppressWarnings("serial")
public class PainelInformacoesCicloMigrado extends PainelSisAps {
    
    private PerfilRisco perfilRisco;

    public PainelInformacoesCicloMigrado(String id, PerfilRisco perfilRisco) {
        super(id);
        setMarkupId(id);
        this.perfilRisco = perfilRisco;
    }
    
    public void setPerfilRisco(PerfilRisco perfilRisco) {
        this.perfilRisco = perfilRisco;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        addComponents();
    }

    private void addComponents() {
        addOrReplace(new TabelaAnexoCiclo("idTabelaAnexoDocumentoCiclo", perfilRisco));
    }

}
