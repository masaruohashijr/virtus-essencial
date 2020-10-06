/*
 * Sistema: TBC
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software � confidencial e propriedade do Banco Central do Brasil.
 * N�o � permitida sua distribui��o ou divulga��o do seu conte�do sem
 * expressa autoriza��o do Banco Central.
 * Este arquivo cont�m informa��es propriet�rias.
 */
package br.gov.bcb.sisaps.adaptadores.bto;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.gov.bcb.app.stuff.util.SpringUtils;

/**
 * Adapter para acesso ao componente BcBto.
 */
@Component
public class BcBtoAdapter {

    @Autowired
    private IBcBto bcBto;

    public static BcBtoAdapter get() {
        return SpringUtils.get().getBean(BcBtoAdapter.class);
    }
    
    public void agendarRotinaBatch(String jobName, String jobGroup, Date dataPedido, Map<String, String> parametros) {
        bcBto.agendarRotinaBatch(jobName, jobGroup, dataPedido, parametros);
    }

}