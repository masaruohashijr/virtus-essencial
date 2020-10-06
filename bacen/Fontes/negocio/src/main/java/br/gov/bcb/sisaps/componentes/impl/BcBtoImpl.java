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
package br.gov.bcb.sisaps.componentes.impl;

import java.util.Date;
import java.util.Map;

import org.springframework.stereotype.Component;

import br.gov.bcb.comum.bto.negocio.fachada.Bto;
import br.gov.bcb.comum.excecoes.BCInfraException;
import br.gov.bcb.sisaps.adaptadores.bto.IBcBto;

@Component()
public class BcBtoImpl implements IBcBto {

    @Override
    public void agendarRotinaBatch(String jobName, String jobGroup, Date dataPedido, Map<String, String> parametros) {
        try {
            Bto.getInstancia().agendarBatch(jobName, jobGroup, dataPedido, parametros);
        } catch (BCInfraException e) {
            throw new RuntimeException(e);
        }
    }
    
}