/*
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software e confidencial e propriedade do Banco Central do Brasil.
 * Nao e permitida sua distribuicao ou divulgacao do seu conteudo sem
 * expressa autorizacao do Banco Central.
 * Este arquivo contem informacoes proprietarias.
 */
package br.gov.bcb.sisaps.src.validacao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.geral.ValidacaoException;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;
import br.gov.bcb.sisaps.util.validacao.NegocioException;




public class ValidadorBase<T> implements Serializable {

    private T objeto;

    private List<IRegra<T>> validadores = new ArrayList<IRegra<T>>();
    private List<ErrorMessage> mensagens = new ArrayList<ErrorMessage>();

    public ValidadorBase(T objeto) {
        this.objeto = objeto;
    }

    protected void adicionarValidador(IRegra<T> validador) {
        validador.setValidador(this);
        validadores.add(validador);
    }

    public void validar() {
        for (IRegra<T> validador : validadores) {
            validarRegra(validador);
        }
        if (!SisapsUtil.isNuloOuVazio(mensagens)) {
            throw new NegocioException(mensagens);
        }
    }

    protected void validarRegra(IRegra<T> validador) {
        try {
            validador.validar(objeto);
        } catch (ValidacaoException e) {
            mensagens.addAll(e.getMensagens());
        }
    }   
    
}