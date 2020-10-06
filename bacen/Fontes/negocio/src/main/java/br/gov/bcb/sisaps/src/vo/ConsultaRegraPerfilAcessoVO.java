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