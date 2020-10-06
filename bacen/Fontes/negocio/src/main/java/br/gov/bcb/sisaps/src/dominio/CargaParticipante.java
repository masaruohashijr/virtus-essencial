package br.gov.bcb.sisaps.src.dominio;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistente;
import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;

@Entity
@Table(name = "CPC_CARGA_PARTICIPANTES_COMITE", schema = "SUP")
@AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = CargaParticipante.CAMPO_ID))
public class CargaParticipante extends ObjetoPersistente<Integer> {

    public static final String CAMPO_ID = "CPC_ID";
    public static final int TAMANHO_NOME = 200;
    public static final int TAMANHO_EQUIPE = 100;

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

    @Column(name = "CPC_CD_PRIORIDADE", length = 10)
    public String getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(String prioridade) {
        this.prioridade = prioridade;
    }

    @Column(name = "CPC_DS_EMAIL", length = 50)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "CPC_CD_MATRICULA", length = 10)
    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    @Column(name = "CPC_NM_PARTICIPANTE", length = TAMANHO_NOME)
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Column(name = "CPC_DS_EQUIPE", length = TAMANHO_EQUIPE)
    public String getEquipe() {
        return equipe;
    }

    public void setEquipe(String equipe) {
        this.equipe = equipe;
    }

    @Column(name = "CPC_CD_FUNCAO", length = 20)
    public String getFuncao() {
        return funcao;
    }

    public void setFuncao(String funcao) {
        this.funcao = funcao;
    }

    @Column(name = "CPC_DS_EQP_EXCLUIR", length = TAMANHO_EQUIPE)
    public String getEquipeExcluir() {
        return equipeExcluir;
    }

    public void setEquipeExcluir(String equipeExcluir) {
        this.equipeExcluir = equipeExcluir;
    }

    @Column(name = "CPC_CD_SUB_EXCLUIR", length = 20)
    public String getSubordinadasExcluir() {
        return subordinadasExcluir;
    }

    public void setSubordinadasExcluir(String subordinadaExcluir) {
        this.subordinadasExcluir = subordinadaExcluir;
    }

    @Column(name = "CPC_DS_EQP_INCLUIR", length = TAMANHO_EQUIPE)
    public String getEquipeIncluir() {
        return equipeIncluir;
    }

    public void setEquipeIncluir(String equipeIncluir) {
        this.equipeIncluir = equipeIncluir;
    }

    @Column(name = "CPC_CD_SUB_INCLUIR", length = 20)
    public String getSubordinadasIncluir() {
        return subordinadasIncluir;
    }

    public void setSubordinadasIncluir(String subordinadaIncluir) {
        this.subordinadasIncluir = subordinadaIncluir;
    }

}
