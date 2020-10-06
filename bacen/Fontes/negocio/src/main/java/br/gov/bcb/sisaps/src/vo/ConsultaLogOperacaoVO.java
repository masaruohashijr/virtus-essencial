/*
 * Sistema APS
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software � confidencial e propriedade do Banco Central do Brasil.
 * N�o � permitida sua distribui��o ou divulga��o do seu conte�do sem
 * expressa autoriza��o do Banco Central.
 * Este arquivo cont�m informa��es propriet�rias.
 */
package br.gov.bcb.sisaps.src.vo;

import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.util.consulta.Consulta;

public class ConsultaLogOperacaoVO extends Consulta<LogOperacaoVO> {
    
    private Integer pk;
    private String codigoCnpj;
    private PerfilRisco perfilRisco;
    private String nome;
    private String trilha;
    private String tituloTela;
    private String codigoTela;
    private String codigoResponsavel;

    public Integer getPk() {
        return pk;
    }

    public void setPk(Integer pk) {
        this.pk = pk;
    }

    public String getCodigoCnpj() {
        return codigoCnpj;
    }

    public String getNome() {
        return nome;
    }

    public String getTrilha() {
        return trilha;
    }

    public String getTituloTela() {
        return tituloTela;
    }

    public String getCodigoTela() {
        return codigoTela;
    }

    public String getCodigoResponsavel() {
        return codigoResponsavel;
    }

    public PerfilRisco getPerfilRisco() {
        return perfilRisco;
    }

    public void setPerfilRisco(PerfilRisco perfilRisco) {
        this.perfilRisco = perfilRisco;
    }

}