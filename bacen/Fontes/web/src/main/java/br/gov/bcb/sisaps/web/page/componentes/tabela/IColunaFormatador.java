package br.gov.bcb.sisaps.web.page.componentes.tabela;

import java.io.Serializable;

public interface IColunaFormatador<T> extends Serializable {

    String obterCss(T obj);

    String obterStyle(T obj);
}
