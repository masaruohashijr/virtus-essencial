/*
 * Sistema APS
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arquivo contém informações proprietárias.
 */
package br.gov.bcb.sisaps.infraestrutura;

import java.util.Collection;
import java.util.List;

import br.gov.bcb.sisaps.util.geral.SisapsExcecaoUtil;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistente;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;

public abstract class RegraBase<T> implements IRegra<T> {

    protected String getMensagemErro() {
        return SisapsExcecaoUtil.VAZIO;
    }

    @Override
    public void validar(T entidade) {
        throw new UnsupportedOperationException();
    }

    protected void validar(boolean condicao) {
        validar(getMensagemErro(), condicao);
    }

    protected void validar(String mensagem, boolean condicao) {
        ErrorMessage erro = new ErrorMessage(mensagem);
        validar(erro, condicao);
    }

    protected void validar(ErrorMessage erro, boolean condicao) {
        SisapsUtil.lancarNegocioException(erro, condicao);
    }

    protected boolean contemItensValidos(Collection<? extends ObjetoPersistente> itens) {
        return SisapsUtil.contemItensValidos(itens);
    }
    
    protected ErrorMessage error(String msg) {
        return new ErrorMessage(msg);
    }
    
    protected void lancarNegocioException(List<ErrorMessage> erros) {
        SisapsUtil.lancarNegocioException(erros);
    }
    
    protected boolean validarObrigatoriedade(Object objeto, String nomeCampo, List<ErrorMessage> mensagens) {
       return SisapsUtil.validarObrigatoriedade(objeto, nomeCampo, mensagens);
    }
}