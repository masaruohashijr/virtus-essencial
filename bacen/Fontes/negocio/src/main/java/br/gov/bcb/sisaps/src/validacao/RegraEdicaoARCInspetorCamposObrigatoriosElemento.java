/*
 * Sistema APS
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arqui contém informações proprietárias.
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