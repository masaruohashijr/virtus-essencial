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