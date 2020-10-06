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

import java.io.Serializable;

import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfisNotificacaoEnum;

public class ResponsavelVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private ServidorVO servidorVO;
    private PerfisNotificacaoEnum perfil;

    public ResponsavelVO() {
        super();
    }

    public ResponsavelVO(ServidorVO servidorVO, PerfisNotificacaoEnum perfil) {
        this.servidorVO = servidorVO;
        this.perfil = perfil;
    }

    public ServidorVO getServidorVO() {
        return servidorVO;
    }

    public void setServidorVO(ServidorVO servidorVO) {
        this.servidorVO = servidorVO;
    }

    public PerfisNotificacaoEnum getPerfil() {
        return perfil;
    }

    public void setPerfil(PerfisNotificacaoEnum perfil) {
        this.perfil = perfil;
    }

}
