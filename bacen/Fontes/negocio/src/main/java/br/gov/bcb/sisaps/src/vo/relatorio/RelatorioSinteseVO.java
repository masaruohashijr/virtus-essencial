/*
 * Projeto: Rating
 * Rating
 * Arquivo: RelatorioRatingVO.java
 * RelatorioRatingVO
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software � confidencial e propriedade do Banco Central do Brasil.
 * N�o � permitida sua distribui��o ou divulga��o do seu conte�do sem
 * expressa autoriza��o do Banco Central.
 * Este arquivo cont�m informa��es propriet�rias.
 */
package br.gov.bcb.sisaps.src.vo.relatorio;

import java.io.Serializable;

public class RelatorioSinteseVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nomeParametroGrupoRisco;
    private String descricaoRiscoSintese;
    private String descricaoSinteseVigente;
    private String campo;

    public String getNomeParametroGrupoRisco() {
        return nomeParametroGrupoRisco;
    }

    public void setNomeParametroGrupoRisco(String nomeParametroGrupoRisco) {
        this.nomeParametroGrupoRisco = nomeParametroGrupoRisco;
    }

    public String getDescricaoRiscoSintese() {
        return descricaoRiscoSintese;
    }

    public void setDescricaoRiscoSintese(String descricaoRiscoSintese) {
        this.descricaoRiscoSintese = descricaoRiscoSintese;
    }

    public String getDescricaoSinteseVigente() {
        return descricaoSinteseVigente;
    }

    public void setDescricaoSinteseVigente(String descricaoSinteseVigente) {
        this.descricaoSinteseVigente = descricaoSinteseVigente;
    }

    public String getCampo() {
        return campo;
    }

    public void setCampo(String campo) {
        this.campo = campo;
    }

}