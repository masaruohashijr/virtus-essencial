/*
 * Bacen SIGAS.
 * 
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software e confidencial e propriedade do Banco Central do Brasil.
 * Nao e permitida sua distribuicao ou divulgacao do seu conteudo sem
 * expressa autorizacao do Banco Central.
 * Este arquivo contem informacoes proprietarias.
 */
package br.gov.bcb.sisaps.integracao.seguranca;

public class UsuarioCorrente {

    private static ThreadLocal<Usuario> usuarioLocal = new ThreadLocal<Usuario>();

    protected UsuarioCorrente() {
        super();
    }

    public static Usuario get() {
        return usuarioLocal.get();
    }

    public static void set(Usuario usuario) {
        usuarioLocal.set(usuario);
    }
}
