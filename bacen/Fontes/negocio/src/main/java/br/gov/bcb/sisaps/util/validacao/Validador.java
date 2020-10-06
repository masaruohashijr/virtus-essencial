package br.gov.bcb.sisaps.util.validacao;

import java.util.Arrays;
import java.util.List;

import org.springframework.util.StringUtils;

import br.gov.bcb.app.stuff.exception.NegocioException;

public class Validador {

    public static final String CAMPO_OBRIGATORIO = "O campo %s é de preenchimento obrigatório.";
    public static final String CAMPO_MENOR_QUE = "O campo %s deve ter no máximo %d caracteres.";

    private NegocioException erro = new NegocioException();

    public List<String> getMensagens() {
        return erro.getMensagens();
    }

    public Validador nulo(Object obj, String mensagem) {
        if (obj != null) {
            adicionar(mensagem);
        }
        return this;
    }

    public Validador igual(Object esperado, Object pretendido, String mensagem) {
        boolean igual = esperado == null ? pretendido == null : esperado.equals(pretendido);
        if (!igual) {
            adicionar(mensagem);
        }
        return this;
    }

    public Validador naoVazio(String valor, String campo) {
        if (StringUtils.isEmpty(valor) || "".equals(valor.trim())) {
            adicionar(String.format(CAMPO_OBRIGATORIO, campo));
        }
        return this;
    }

    public Validador tamanhoMenorQue(String valor, String campo, int maximo) {
        if (valor == null) {
            return this;
        }
        if (valor.length() > maximo) {
            adicionar(String.format(CAMPO_MENOR_QUE, campo, maximo));
        }
        return this;
    }

    public Validador adicionar(String mensagem) {
        erro.addMensagens(Arrays.asList(mensagem));
        return this;
    }

    public Validador adicionarMensagem(String mensagem) {
        getMensagens().add(mensagem);
        return this;
    }

    public void validar() {
        if (!erro.getMensagens().isEmpty()) {
            throw erro;
        }
    }
}