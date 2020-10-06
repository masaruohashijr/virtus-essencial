/*
 * Sistema: TBC
 * 
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arquivo contém informações proprietárias.
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
