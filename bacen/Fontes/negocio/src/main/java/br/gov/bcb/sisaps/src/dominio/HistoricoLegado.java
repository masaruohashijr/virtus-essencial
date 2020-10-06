package br.gov.bcb.sisaps.src.dominio;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "HLE_HISTORICO_LEGADO", schema = "SUP")
public class HistoricoLegado implements Serializable {

    private static final String DEFINICAO_COLUNA_MATRICULA = "character(8)";
    private Ciclo ciclo;
    private Integer dataBase;
    private String matriculaSupervisor;
    private String matriculaGerente;
    
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Ciclo.CAMPO_ID, nullable = false)
    public Ciclo getCiclo() {
        return ciclo;
    }
    
    public void setCiclo(Ciclo ciclo) {
        this.ciclo = ciclo;
    }
    
    @Column(name = "HLE_CD_DATA_BASE", nullable = false)
    public Integer getDataBase() {
        return dataBase;
    }
    
    public void setDataBase(Integer dataBase) {
        this.dataBase = dataBase;
    }
    
    @Column(name = "HLE_CD_MAT_SUPERVISOR", length = 8, nullable = false, columnDefinition = DEFINICAO_COLUNA_MATRICULA)
    public String getMatriculaSupervisor() {
        return matriculaSupervisor;
    }
    
    public void setMatriculaSupervisor(String matriculaSupervisor) {
        this.matriculaSupervisor = matriculaSupervisor;
    }
    
    @Column(name = "HLE_CD_MAT_GERENTE", length = 8, nullable = false, columnDefinition = "character(8)")
    public String getMatriculaGerente() {
        return matriculaGerente;
    }
    
    public void setMatriculaGerente(String matriculaGerente) {
        this.matriculaGerente = matriculaGerente;
    }
    
}
