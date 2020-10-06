/*
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software e confidencial e propriedade do Banco Central do Brasil.
 * Nao e permitida sua distribuicao ou divulgacao do seu conteudo sem
 * expressa autorizacao do Banco Central.
 * Este arquivo contem informacoes proprietarias.
 */

package br.gov.bcb.sisaps.web.page;

import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;

/**
 * Página padrão do sistema.
 * 
 */

public interface ISisApsPage {

    // Retorna o perfil da página.
    public PerfilAcessoEnum getPerfilPorPagina();
}