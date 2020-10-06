/*
 * Sistema APS
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software � confidencial e propriedade do Banco Central do Brasil.
 * N�o � permitida sua distribui��o ou divulga��o do seu conte�do sem
 * expressa autoriza��o do Banco Central.
 * Este arqui cont�m informa��es propriet�rias.
 */
package br.gov.bcb.sisaps.src.validacao;

import java.util.ArrayList;

import br.gov.bcb.sisaps.src.dominio.Elemento;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;

public class RegraEdicaoARCInspetorCamposObrigatoriosElemento {
    
    private Elemento elemento;
    private boolean lancarExcecao = true;
    
    public RegraEdicaoARCInspetorCamposObrigatoriosElemento(Elemento elemento) {
        this.elemento = elemento;
    }

    public RegraEdicaoARCInspetorCamposObrigatoriosElemento(Elemento elemento, boolean lancarExcecao) {
        this(elemento);
        this.lancarExcecao = lancarExcecao;
    }

    public ArrayList<ErrorMessage> validar() {
        ArrayList<ErrorMessage> erros = new ArrayList<ErrorMessage>();
        
        SisapsUtil.validarObrigatoriedade(elemento.getParametroNotaInspetor(), 
                "Nova nota do Elemento de " + elemento.getParametroElemento().getNome(), erros);
        
        if (lancarExcecao) {
            SisapsUtil.lancarNegocioException(erros);
        }
        
        return erros;
    }
}