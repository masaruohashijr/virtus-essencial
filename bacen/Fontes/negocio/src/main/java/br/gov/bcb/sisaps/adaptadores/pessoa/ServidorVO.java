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
package br.gov.bcb.sisaps.adaptadores.pessoa;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;

public class ServidorVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String login;
    private String matricula;
    private String nome;
    private String unidade;
    private String nomeChefe;
    private String matriculaChefe;
    private String funcaoChefe;
    private String nomeSubstituto;
    private String matriculaSubstituto;
    private String email;
    private String funcao;
    private String situacao;
    private String localizacao;
    private String localizacaoSubstituicao;
    private String localizacaoSimulada;
    private Map<PerfilAcessoEnum, String> localizacaoSubstituicaoPorPerfil;
    
    public ServidorVO() {
    	localizacaoSubstituicaoPorPerfil = new HashMap<PerfilAcessoEnum, String>();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * Nome do chefe imediato do usuário logado.
     * 
     * @return
     */
    public String getNomeChefe() {
        return nomeChefe;
    }

    public void setNomeChefe(String nomeChefe) {
        this.nomeChefe = nomeChefe;
    }

    /**
     * Matrícula do chefe imediato do usuário logado.
     * 
     * @return
     */
    public String getMatriculaChefe() {
        return matriculaChefe;
    }

    public void setMatriculaChefe(String matriculaChefe) {
        this.matriculaChefe = matriculaChefe;
    }

    public String getFuncaoChefe() {
        return funcaoChefe;
    }

    public void setFuncaoChefe(String funcaoChefe) {
        this.funcaoChefe = funcaoChefe;
    }

    /**
     * Nome do substituto eventual do chefe imediado do usuário logado.
     * 
     * @return
     */
    public String getNomeSubstituto() {
        return nomeSubstituto;
    }

    public void setNomeSubstituto(String nomeSubstituto) {
        this.nomeSubstituto = nomeSubstituto;
    }

    /**
     * Matrícula do substituto eventual do chefe imediado do usuário logado.
     * 
     * @return
     */
    public String getMatriculaSubstituto() {
        return matriculaSubstituto;
    }

    public void setMatriculaSubstituto(String matriculaSubstituto) {
        this.matriculaSubstituto = matriculaSubstituto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public String getFuncao() {
        return funcao;
    }

    public void setFuncao(String funcao) {
        this.funcao = funcao;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public String getLocalizacaoSubstituicao() {
        return localizacaoSubstituicao;
    }

    public void setLocalizacaoSubstituicao(String localizacaoSubstituicao) {
        this.localizacaoSubstituicao = localizacaoSubstituicao;
    }

    public String getLocalizacaoSimulada() {
        return localizacaoSimulada;
    }

    public void setLocalizacaoSimulada(String localizacaoSimulada) {
        this.localizacaoSimulada = localizacaoSimulada;
    }
    
    public Map<PerfilAcessoEnum, String> getLocalizacaoSubstituicaoPorPerfil() {
		return localizacaoSubstituicaoPorPerfil;
	}

	public String getLocalizacaoAtual() {
        return getLocalizacaoAtual(null);
    }
    
    public String getLocalizacaoAtual(PerfilAcessoEnum perfil) {
        String retorno = null;
        if (localizacaoSimulada != null) {
        	retorno =  localizacaoSimulada;
        } else if (perfil == null && localizacaoSubstituicao != null) {
        	retorno = localizacaoSubstituicao;
        } else if (perfil != null && localizacaoSubstituicaoPorPerfil.get(perfil) != null) {
        	retorno = localizacaoSubstituicaoPorPerfil.get(perfil);
        } else {
        	retorno = localizacao;
        }
        return retorno == null ? null : retorno.trim();
    }

}
