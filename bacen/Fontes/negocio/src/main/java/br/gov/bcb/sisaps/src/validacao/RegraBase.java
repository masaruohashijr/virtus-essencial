/*
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software e confidencial e propriedade do Banco Central do Brasil.
 * Nao e permitida sua distribuicao ou divulgacao do seu conteudo sem
 * expressa autorizacao do Banco Central.
 * Este arquivo contem informacoes proprietarias.
 */
package br.gov.bcb.sisaps.src.validacao;

import br.gov.bcb.sisaps.util.geral.ExcecaoUtil;
import br.gov.bcb.sisaps.util.geral.ValidacaoException;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;

public abstract class RegraBase<T> implements IRegra<T> {

    private ValidadorBase<T> validador;

    protected void validarParcialmente(boolean condicao) {
        validarParcialmente(getMensagemErro(), condicao);
    }

    protected void validarParcialmente(ErrorMessage mensagem, boolean condicao) {
        ExcecaoUtil.verificarLancamentoExcecao(new ValidacaoException(mensagem), condicao);
    }

    protected abstract ErrorMessage getMensagemErro();

    @Override
    public void setValidador(ValidadorBase<T> validador) {
        this.validador = validador;
    }
    
    @Override
    public ValidadorBase<T> getValidador() {
        return this.validador;
    }
}