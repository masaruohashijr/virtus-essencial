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
package br.gov.bcb.sisaps.src.dominio;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistenteAuditavelVersionado;

@Entity
@Table(name = "CIC_CICLO_SRC", schema = "SUP")
@AttributeOverrides(value = {
		@AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = Ciclo.CAMPO_ID)),
		@AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "CIC_DH_ATUALZ", nullable = false)),
		@AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "CIC_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(20)")),
		@AttributeOverride(name = ObjetoPersistenteAuditavelVersionado.PROP_VERSAO, column = @Column(nullable = false, name = "CIC_NU_VERSAO")) })
public class Ciclo extends ObjetoPersistenteAuditavelVersionado<Integer> {
    public static final String CAMPO_ID = "CIC_ID";
    private static final String PROP_CICLO = "ciclo";
	private static final int TAMANHO_MATRICULA = 8;
	private static final String DEFINICAO_DATE = "date";
	private static final int TAMANHO_PT_PE = 12;
	private static final long serialVersionUID = 1L;
	private String codigoPTPE;
	private Date dataInicio;
	private Date dataInicioCorec;
	private Date dataPrevisaoCorec;
	private String matriculaSegSubstSupervisor;
	private EntidadeSupervisionavel entidadeSupervisionavel;
	private Matriz matriz;
	private Metodologia metodologia;
	private EstadoCiclo estadoCiclo;
	private Apresentacao apresentacao;
	private List<GrauPreocupacaoES> grausPreocupacaoES;
	private List<PerfilAtuacaoES> perfisAtuacaoES;
	private List<ConclusaoES> conclusoesES;
	private List<PerspectivaES> perspectivasES;
	private List<SituacaoES> situacoesES;
	private AgendaCorec agenda;
	
	private List<HistoricoLegado> historicoLegado;
	
	
   @OneToMany(mappedBy = PROP_CICLO, fetch = FetchType.LAZY)
    public List<HistoricoLegado> getHistoricoLegado() {
        return historicoLegado;
    }

    public void setHistoricoLegado(List<HistoricoLegado> historicoLegado) {
        this.historicoLegado = historicoLegado;
    }
    
    


	@Column(name = "CIC_CD_PT_PE", nullable = true, length = TAMANHO_PT_PE)
	public String getCodigoPTPE() {
		return codigoPTPE;
	}

	public void setCodigoPTPE(String codigoPTPE) {
		this.codigoPTPE = codigoPTPE;
	}

	@Column(name = "CIC_DT_INICIO", columnDefinition = DEFINICAO_DATE)
	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	@Column(name = "CIC_DT_PREV_COREC", columnDefinition = DEFINICAO_DATE)
	public Date getDataPrevisaoCorec() {
		return dataPrevisaoCorec;
	}

	public void setDataPrevisaoCorec(Date dataPrevisaoCorec) {
		this.dataPrevisaoCorec = dataPrevisaoCorec;
	}

	@Column(name = "CIC_CD_MAT_SEG_SUB_SUP", columnDefinition = "varchar(" + TAMANHO_MATRICULA + ")")
	public String getMatriculaSegSubstSupervisor() {
		return matriculaSegSubstSupervisor;
	}

	public void setMatriculaSegSubstSupervisor(String matriculaSegSubstSupervisor) {
		this.matriculaSegSubstSupervisor = matriculaSegSubstSupervisor;
	}

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = EntidadeSupervisionavel.CAMPO_ID, nullable = false)
	public EntidadeSupervisionavel getEntidadeSupervisionavel() {
		return entidadeSupervisionavel;
	}

	public void setEntidadeSupervisionavel(EntidadeSupervisionavel entidadeSupervisionavel) {
		this.entidadeSupervisionavel = entidadeSupervisionavel;
	}

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = Matriz.CAMPO_ID, nullable = true)
	public Matriz getMatriz() {
		return matriz;
	}

	public void setMatriz(Matriz matriz) {
		this.matriz = matriz;
	}

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = Metodologia.CAMPO_ID, nullable = false)
	public Metodologia getMetodologia() {
		return metodologia;
	}

	public void setMetodologia(Metodologia metodologia) {
		this.metodologia = metodologia;
	}

	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = EstadoCiclo.CAMPO_ID, nullable = false)
	public EstadoCiclo getEstadoCiclo() {
		return estadoCiclo;
	}

	public void setEstadoCiclo(EstadoCiclo estadoCiclo) {
		this.estadoCiclo = estadoCiclo;
	}

	@Transient
	public String getDataInicioFormatada() {
		return this.dataInicio == null ? "" : new SimpleDateFormat(Constantes.FORMATO_DATA_COM_BARRAS)
				.format(this.dataInicio);
	}

	@Transient
	public String getDataPrevisaoFormatada() {
		return this.dataPrevisaoCorec == null ? "" : new SimpleDateFormat(Constantes.FORMATO_DATA_COM_BARRAS)
				.format(this.dataPrevisaoCorec);
	}
	
	@Column(name = "CIC_DT_INICIO_COREC", nullable = true, columnDefinition = DEFINICAO_DATE)
	public Date getDataInicioCorec() {
		return dataInicioCorec;
	}

	public void setDataInicioCorec(Date dataInicioCorec) {
		this.dataInicioCorec = dataInicioCorec;
	}

	@OneToOne(mappedBy = PROP_CICLO, fetch = FetchType.LAZY)
	public Apresentacao getApresentacao() {
		return apresentacao;
	}

	public void setApresentacao(Apresentacao apresentacao) {
		this.apresentacao = apresentacao;
	}

	@OneToMany(mappedBy = PROP_CICLO, fetch = FetchType.LAZY)
	public List<GrauPreocupacaoES> getGrausPreocupacaoES() {
        return grausPreocupacaoES;
    }

    public void setGrausPreocupacaoES(List<GrauPreocupacaoES> grausPreocupacaoES) {
        this.grausPreocupacaoES = grausPreocupacaoES;
    }

    @OneToMany(mappedBy = PROP_CICLO, fetch = FetchType.LAZY)
    public List<PerfilAtuacaoES> getPerfisAtuacaoES() {
        return perfisAtuacaoES;
    }

    public void setPerfisAtuacaoES(List<PerfilAtuacaoES> perfisAtuacaoES) {
        this.perfisAtuacaoES = perfisAtuacaoES;
    }

    @OneToMany(mappedBy = PROP_CICLO, fetch = FetchType.LAZY)
    public List<ConclusaoES> getConclusoesES() {
        return conclusoesES;
    }

    public void setConclusoesES(List<ConclusaoES> conclusoesES) {
        this.conclusoesES = conclusoesES;
    }

    @OneToMany(mappedBy = PROP_CICLO, fetch = FetchType.LAZY)
    public List<PerspectivaES> getPerspectivasES() {
        return perspectivasES;
    }

    public void setPerspectivasES(List<PerspectivaES> perspectivasES) {
        this.perspectivasES = perspectivasES;
    }

    @OneToMany(mappedBy = PROP_CICLO, fetch = FetchType.LAZY)
    public List<SituacaoES> getSituacoesES() {
        return situacoesES;
    }

    public void setSituacoesES(List<SituacaoES> situacoesES) {
        this.situacoesES = situacoesES;
    }

    @OneToOne(mappedBy = PROP_CICLO, fetch = FetchType.LAZY)
    public AgendaCorec getAgenda() {
        return agenda;
    }

    public void setAgenda(AgendaCorec agenda) {
        this.agenda = agenda;
    }

}
