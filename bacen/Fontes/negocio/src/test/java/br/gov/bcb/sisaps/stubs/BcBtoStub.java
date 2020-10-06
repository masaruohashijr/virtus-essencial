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
package br.gov.bcb.sisaps.stubs;

import java.util.Date;
import java.util.Map;

import org.springframework.stereotype.Component;

import br.gov.bcb.especificacao.spring.listener.stub.Stub;
import br.gov.bcb.sisaps.adaptadores.bto.IBcBto;

@Component(IBcBto.NOME)
public class BcBtoStub implements IBcBto, Stub {

    @Override
    public void limpar() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void agendarRotinaBatch(String jobName, String jobGroup, Date dataPedido, Map<String, String> parametros) {
        // TODO Auto-generated method stub
        
    }

}
