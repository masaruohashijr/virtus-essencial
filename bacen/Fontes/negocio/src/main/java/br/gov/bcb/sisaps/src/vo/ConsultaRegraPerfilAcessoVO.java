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

import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.util.consulta.Consulta;

public class ConsultaRegraPerfilAcessoVO extends Consulta<RegraPerfilAcessoVO> {
    
    private PerfilAcessoEnum perfilAcesso;

    public PerfilAcessoEnum getPerfilAcesso() {
        return perfilAcesso;
    }

    public void setPerfilAcesso(PerfilAcessoEnum perfilAcesso) {
        this.perfilAcesso = perfilAcesso;
    }
    
}