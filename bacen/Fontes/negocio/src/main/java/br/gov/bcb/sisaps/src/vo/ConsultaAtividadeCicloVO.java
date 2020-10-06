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
package br.gov.bcb.sisaps.src.vo;

import java.util.List;

import br.gov.bcb.sisaps.src.dominio.VersaoPerfilRisco;
import br.gov.bcb.sisaps.util.consulta.Consulta;

public class ConsultaAtividadeCicloVO extends Consulta<AtividadeCicloVO> {
    
    private String cnpjES;
    private Short ano;
    private List<VersaoPerfilRisco> versoesPerfilRisco;

    public String getCnpjES() {
        return cnpjES;
    }

    public void setCnpjES(String cnpjES) {
        this.cnpjES = cnpjES;
    }

    public Short getAno() {
        return ano;
    }

    public void setAno(Short ano) {
        this.ano = ano;
    }

    public List<VersaoPerfilRisco> getVersoesPerfilRisco() {
        return versoesPerfilRisco;
    }

    public void setVersoesPerfilRisco(List<VersaoPerfilRisco> versoesPerfilRisco) {
        this.versoesPerfilRisco = versoesPerfilRisco;
    }

}