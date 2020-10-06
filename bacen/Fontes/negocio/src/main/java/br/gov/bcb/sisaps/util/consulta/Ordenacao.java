package br.gov.bcb.sisaps.util.consulta;

import java.io.Serializable;

public class Ordenacao implements Serializable {

    private String propriedade;
    private boolean crescente = true;
    private Boolean nulosNoInicio = Boolean.TRUE;

    public String getPropriedade() {
        return propriedade;
    }

    public void setPropriedade(String propriedade) {
        this.propriedade = propriedade;
    }

    public boolean isCrescente() {
        return crescente;
    }

    public void setCrescente(boolean crescente) {
        this.crescente = crescente;
    }

    public Boolean getNulosNoInicio() {
        return nulosNoInicio;
    }

    public void setNulosNoInicio(Boolean nulosNoInicio) {
        this.nulosNoInicio = nulosNoInicio;
    }
}