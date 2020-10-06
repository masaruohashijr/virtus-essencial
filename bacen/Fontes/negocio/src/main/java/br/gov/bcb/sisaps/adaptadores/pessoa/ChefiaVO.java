/*
 * Sistema APS
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arquivo contém informações proprietárias.
 */
package br.gov.bcb.sisaps.adaptadores.pessoa;

import java.io.Serializable;



public class ChefiaVO implements Serializable {

    /**
     * Usado apenas para o desenvolvimento local
     */
    private String rotuloComponenteOrganizacional;
    
    private ServidorVO chefeTitular;
    private ServidorVO chefeEmExercicio;
    private ServidorVO substitutoEventual;

    public String getRotuloComponenteOrganizacional() {
        return rotuloComponenteOrganizacional;
    }

    public void setRotuloComponenteOrganizacional(String rotuloComponenteOrganizacional) {
        this.rotuloComponenteOrganizacional = rotuloComponenteOrganizacional;
    }

    public ServidorVO getChefeTitular() {
        return chefeTitular;
    }

    public void setChefeTitular(ServidorVO chefeTitular) {
        this.chefeTitular = chefeTitular;
    }

    public ServidorVO getChefeEmExercicio() {
        return chefeEmExercicio;
    }

    public void setChefeEmExercicio(ServidorVO chefeEmExercicio) {
        this.chefeEmExercicio = chefeEmExercicio;
    }

    public ServidorVO getSubstitutoEventual() {
        return substitutoEventual;
    }

    public void setSubstitutoEventual(ServidorVO substitutoEventual) {
        this.substitutoEventual = substitutoEventual;
    }

}
