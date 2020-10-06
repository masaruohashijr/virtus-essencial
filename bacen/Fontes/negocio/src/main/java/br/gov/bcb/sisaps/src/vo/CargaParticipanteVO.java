package br.gov.bcb.sisaps.src.vo;

import java.io.Serializable;

public class CargaParticipanteVO extends ObjetoPersistenteAuditavelVO implements Serializable {

    private String prioridade;
    private String email;
    private String matricula;
    private String nome;
    private String equipe;
    private String funcao;
    private String equipeExcluir;
    private String subordinadasExcluir;
    private String equipeIncluir;
    private String subordinadasIncluir;

    public String getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(String prioridade) {
        this.prioridade = prioridade;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEquipe() {
        return equipe;
    }

    public void setEquipe(String equipe) {
        this.equipe = equipe;
    }

    public String getFuncao() {
        return funcao;
    }

    public void setFuncao(String funcao) {
        this.funcao = funcao;
    }

    public String getEquipeExcluir() {
        return equipeExcluir;
    }

    public void setEquipeExcluir(String equipeExcluir) {
        this.equipeExcluir = equipeExcluir;
    }

    public String getSubordinadasExcluir() {
        return subordinadasExcluir;
    }

    public void setSubordinadasExcluir(String subordinadasExcluir) {
        this.subordinadasExcluir = subordinadasExcluir;
    }

    public String getEquipeIncluir() {
        return equipeIncluir;
    }

    public void setEquipeIncluir(String equipeIncluir) {
        this.equipeIncluir = equipeIncluir;
    }

    public String getSubordinadasIncluir() {
        return subordinadasIncluir;
    }

    public void setSubordinadasIncluir(String subordinadasIncluir) {
        this.subordinadasIncluir = subordinadasIncluir;
    }

}
