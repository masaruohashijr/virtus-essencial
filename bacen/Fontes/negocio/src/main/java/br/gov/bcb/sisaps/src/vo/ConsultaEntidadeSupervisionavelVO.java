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

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import br.gov.bcb.sisaps.src.dominio.EquipeES;
import br.gov.bcb.sisaps.src.dominio.ParametroPrioridade;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoCicloEnum;
import br.gov.bcb.sisaps.util.consulta.Consulta;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;

public class ConsultaEntidadeSupervisionavelVO extends Consulta<EntidadeSupervisionavelVO> {

    private String nome;
    private String conglomeradoOuCnpj;
    private SimNaoEnum pertenceSrc;
    private EquipeES equipe;
    private ParametroPrioridade prioridade;
    private DateTime dataInclusao;
    private MetodologiaVO metodologia;
    private Boolean possuiVersaoPerfilDeRisco;
    private boolean buscarHierarquiaInferior;
    private String localizacao;
    private Boolean possuiPrioridade;
    private List<EstadoCicloEnum> estadosCiclo;
    private boolean isAdministrador;
    private LocalDate dataPrevistaCorec;
    private String ciclo;
    private String dataCorec;
    private String nomeServidor;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getConglomeradoOuCnpj() {
        return conglomeradoOuCnpj;
    }

    public void setConglomeradoOuCnpj(String conglomeradoOuCnpj) {
        this.conglomeradoOuCnpj = conglomeradoOuCnpj;
    }

    public SimNaoEnum getPertenceSrc() {
        return pertenceSrc;
    }

    public void setPertenceSrc(SimNaoEnum pertenceSrc) {
        this.pertenceSrc = pertenceSrc;
    }

    public EquipeES getEquipe() {
        return equipe;
    }

    public void setEquipe(EquipeES equipe) {
        this.equipe = equipe;
    }

    public ParametroPrioridade getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(ParametroPrioridade prioridade) {
        this.prioridade = prioridade;
    }

    public DateTime getDataInclusao() {
        return dataInclusao;
    }

    public void setDataInclusao(DateTime dataInclusao) {
        this.dataInclusao = dataInclusao;
    }

    public MetodologiaVO getMetodologia() {
        return metodologia;
    }

    public void setMetodologia(MetodologiaVO metodologia) {
        this.metodologia = metodologia;
    }

    public Boolean isPossuiVersaoPerfilDeRisco() {
        return possuiVersaoPerfilDeRisco;
    }

    public void setPossuiVersaoPerfilDeRisco(Boolean possuiVersaoPerfilDeRisco) {
        this.possuiVersaoPerfilDeRisco = possuiVersaoPerfilDeRisco;
    }
    
    public boolean isBuscarHierarquiaInferior() {
        return buscarHierarquiaInferior;
    }

    public void setBuscarHierarquiaInferior(boolean isBuscarHierarquiaInferior) {
        this.buscarHierarquiaInferior = isBuscarHierarquiaInferior;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public Boolean isPossuiPrioridade() {
        return possuiPrioridade;
    }

    public void setPossuiPrioridade(Boolean possuiPrioridade) {
        this.possuiPrioridade = possuiPrioridade;
    }

    public List<EstadoCicloEnum> getEstadosCiclo() {
        return estadosCiclo;
    }

    public void setEstadosCiclo(List<EstadoCicloEnum> estadosCiclo) {
        this.estadosCiclo = estadosCiclo;
    }

    public boolean isAdministrador() {
        return isAdministrador;
    }

    public void setAdministrador(boolean isAdministrador) {
        this.isAdministrador = isAdministrador;
    }

    public LocalDate getDataPrevistaCorec() {
        return dataPrevistaCorec;
    }

    public void setDataPrevistaCorec(LocalDate dataPrevistaCorec) {
        this.dataPrevistaCorec = dataPrevistaCorec;
    }

    public String getCiclo() {
        return ciclo;
    }

    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    public String getDataCorec() {
        return dataCorec;
    }

    public void setDataCorec(String dataCorec) {
        this.dataCorec = dataCorec;
    }

    public String getNomeServidor() {
        return nomeServidor;
    }

    public void setNomeServidor(String nomeServidor) {
        this.nomeServidor = nomeServidor;
    }
    
}