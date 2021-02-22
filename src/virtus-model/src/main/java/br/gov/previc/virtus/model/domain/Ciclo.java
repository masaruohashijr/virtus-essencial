package br.gov.previc.virtus.model.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@lombok.Data
@Table(name = "ciclos")
@SequenceGenerator(allocationSize = 1, initialValue = 1, name = "ciclos_id_seq", sequenceName = "ciclos_id_seq")
public class Ciclo {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ciclos_id_seq")
	private Long id;

	@Column(name = "nome", nullable = false, length = 255)
	private String nome;

	@Column(name = "descricao", nullable = false, length = 4000)
	private String descricao;
	
	@Column(name = "referencia", nullable = false, length = 500)
	private String referencia;
	
	@Column(name = "autor_id")
	private Long autorId;
	
	@Column(name = "criado_em")
	@Temporal(TemporalType.TIMESTAMP)
	private Date criadoEm;
	
	@Column(name = "status_id")
	private Long statusId;
	
}
