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

public interface IRegra<T> extends Serializable {

    void validar(T objeto);

    void setValidador(ValidadorBase<T> validador);

    ValidadorBase<T> getValidador();
}