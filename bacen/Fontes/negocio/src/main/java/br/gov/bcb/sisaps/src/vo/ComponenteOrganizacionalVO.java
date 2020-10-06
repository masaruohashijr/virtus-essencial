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
package br.gov.bcb.sisaps.src.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class ComponenteOrganizacionalVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String rotulo;
    private String sigla;
    private Date dataInicioVigencia;
    private Date dataFimVigencia;
    private List<ComponenteOrganizacionalVO> filhos;
   
    public Date getDataInicioVigencia() {
        return dataInicioVigencia;
    }

    public void setDataInicioVigencia(Date dataInicioVigencia) {
        this.dataInicioVigencia = dataInicioVigencia;
    }

    public Date getDataFimVigencia() {
        return dataFimVigencia;
    }

    public void setDataFimVigencia(Date dataFimVigencia) {
        this.dataFimVigencia = dataFimVigencia;
    }

    public String getRotulo() {
        return rotulo;
    }

    public void setRotulo(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public List<ComponenteOrganizacionalVO> getFilhos() {
        return filhos;
    }

    public void setFilhos(List<ComponenteOrganizacionalVO> filhos) {
        this.filhos = filhos;
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getSigla()).append(sigla).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ComponenteOrganizacionalVO) {
            ComponenteOrganizacionalVO componente = (ComponenteOrganizacionalVO) obj;
            return new EqualsBuilder().append(getSigla(), componente.getSigla()).isEquals();
        }
        return false;
    }
    
}
