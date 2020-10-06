package br.gov.bcb.sisaps.src.dominio;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistente;
import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;

@Entity
@Table(name = "SUC_SUPERVISAO_CICLO", schema = "SUP")
@AttributeOverrides(value = { @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = SupervisaoCiclo.CAMPO_ID)) })
public class SupervisaoCiclo extends ObjetoPersistente<Integer> {

	public static final String CAMPO_ID = "SUC_ID";
	private static final String DEFINICAO_COLUNA_MATRICULA = "VARCHAR(8)";
	private static final String DEFINICAO_DATE = "date";
	private static final int TAMANHO_NOME = 200;
	private static final int TAMANHO_LOCALIZACAO = 30;
	
	private Date dataInicio;
	private Date dataFim;
	private String matriculaSupervisor;
	private String matriculaGerente;
	private String nomeSupervisor;
	private String nomeGerente;
	private String localizacao;

	@Column(name = "SUC_DT_INICIO", columnDefinition = DEFINICAO_DATE)
	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	@Column(name = "SUC_DT_FIM", columnDefinition = DEFINICAO_DATE)
	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	@Column(name = "SUC_CD_MAT_SUPERV", length = 8, nullable = false, columnDefinition = DEFINICAO_COLUNA_MATRICULA)
	public String getMatriculaSupervisor() {
		return matriculaSupervisor;
	}

	public void setMatriculaSupervisor(String matriculaSupervisor) {
		this.matriculaSupervisor = matriculaSupervisor;
	}

	@Column(name = "SUC_CD_MAT_GERENTE", length = 8, nullable = false, columnDefinition = DEFINICAO_COLUNA_MATRICULA)
	public String getMatriculaGerente() {
		return matriculaGerente;
	}

	public void setMatriculaGerente(String matriculaGerente) {
		this.matriculaGerente = matriculaGerente;
	}

	@Column(name = "SUC_NM_SUPERVISOR", nullable = false, length = TAMANHO_NOME)
	public String getNomeSupervisor() {
		return nomeSupervisor;
	}

	public void setNomeSupervisor(String nomeSupervisor) {
		this.nomeSupervisor = nomeSupervisor;
	}

	@Column(name = "SUC_NM_GERENTE", nullable = false, length = TAMANHO_NOME)
	public String getNomeGerente() {
		return nomeGerente;
	}

	public void setNomeGerente(String nomeGerente) {
		this.nomeGerente = nomeGerente;
	}

	@Column(name = "SUC_DS_LOCALIZACAO", nullable = false, length = TAMANHO_LOCALIZACAO)
	public String getLocalizacao() {
		return localizacao;
	}

	public void setLocalizacao(String localizacao) {
		this.localizacao = localizacao;
	}

}
