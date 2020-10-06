package br.gov.bcb.sisaps.batch.util;

import br.gov.bcb.app.stuff.seguranca.ProvedorInformacoesUsuario;

public class ProvedorInformacoesUsuarioBatch implements ProvedorInformacoesUsuario {
    
    private static final long serialVersionUID = 1L;

    public String getLogin() {
        return "deinf.batchsisaps";
    }

    public boolean isUserInRole(String role) {
        return true;
    }

}
