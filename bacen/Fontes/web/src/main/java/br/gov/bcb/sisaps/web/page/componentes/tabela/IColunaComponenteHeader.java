package br.gov.bcb.sisaps.web.page.componentes.tabela;

import java.io.Serializable;

import org.apache.wicket.Component;

public interface IColunaComponenteHeader<T> extends Serializable {

    Component obterComponenteHeader(String componentId);
}