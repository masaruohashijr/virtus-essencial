package br.gov.bcb.sisaps.util.objetos;

import br.gov.bcb.sisaps.src.dominio.VersaoPerfilRisco;

public abstract class ObjetoVersionadorAuditavelVersionado extends ObjetoPersistenteAuditavelVersionado<Integer> 
    implements IObjetoVersionador {

    protected VersaoPerfilRisco versaoPerfilRisco;

    public abstract VersaoPerfilRisco getVersaoPerfilRisco();

    public void setVersaoPerfilRisco(VersaoPerfilRisco versaoPerfilRisco) {
        this.versaoPerfilRisco = versaoPerfilRisco;
    }
    
}
