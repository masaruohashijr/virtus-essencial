package br.gov.bcb.sisaps.util.objetos;

import br.gov.bcb.sisaps.src.dominio.VersaoPerfilRisco;

public interface IObjetoVersionador {

    VersaoPerfilRisco getVersaoPerfilRisco();
    void setVersaoPerfilRisco(VersaoPerfilRisco versaoPerfilRisco);
}