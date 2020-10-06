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

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoMatrizEnum;

public class MatrizVO extends ObjetoPersistenteVO {

    private static final long serialVersionUID = 1L;

    private EstadoMatrizEnum estadoMatriz;
    private CicloVO ciclo;

    public MatrizVO() {
        super();
    }
    
    public MatrizVO(Integer pk, EstadoMatrizEnum estadoMatriz, Ciclo ciclo) {
        this.pk = pk;
        this.estadoMatriz = estadoMatriz;
        CicloVO cicloVO = new CicloVO();
        cicloVO.setPk(ciclo.getPk());
        cicloVO.setEntidadeSupervisionavel(ciclo.getEntidadeSupervisionavel());
        this.ciclo = cicloVO;
    }

    public EstadoMatrizEnum getEstadoMatriz() {
        return estadoMatriz;
    }

    public void setEstadoMatriz(EstadoMatrizEnum estadoMatriz) {
        this.estadoMatriz = estadoMatriz;
    }

    public CicloVO getCiclo() {
        return ciclo;
    }

    public void setCiclo(CicloVO ciclo) {
        this.ciclo = ciclo;
    }

}
