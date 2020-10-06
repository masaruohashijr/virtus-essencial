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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.collections.CollectionUtils;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;
import br.gov.bcb.sisaps.src.vo.AnexoApresentacaoVO;
import br.gov.bcb.sisaps.src.vo.ApresentacaoVO;
import br.gov.bcb.sisaps.src.vo.TextoApresentacaoVO;

@Entity
@Table(name = "APR_APRESENTACAO_SRC", schema = "SUP")
@AttributeOverrides(value = {
		@AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = Apresentacao.CAMPO_ID)),
		@AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "APR_DH_ATUALZ", nullable = false)),
		@AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "APR_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(20)")) })
public class Apresentacao extends ObjetoPersistenteAuditavel<Integer> {

	// Constantes
	public static final String CAMPO_ID = "APR_ID";
	private static final long serialVersionUID = 1L;

	// Ciclo da apresentação.
	private Ciclo ciclo;

	// Todos os anexos da apresentação.
	private List<AnexoApresentacao> anexos;

	// Todos os textos da apresentação.
	private List<TextoApresentacao> textos;
	
	@OneToOne(optional = false)
	@JoinColumn(name = Ciclo.CAMPO_ID, nullable = false)
	public Ciclo getCiclo() {
		return ciclo;
	}

	public void setCiclo(Ciclo ciclo) {
		this.ciclo = ciclo;
	}

	@OneToMany(mappedBy = "apresentacao", fetch = FetchType.LAZY)
	public List<AnexoApresentacao> getAnexos() {
		return anexos;
	}

	public void setAnexos(List<AnexoApresentacao> anexos) {
		this.anexos = anexos;
	}

	@OneToMany(mappedBy = "apresentacao", fetch = FetchType.LAZY)
	public List<TextoApresentacao> getTextos() {
		return textos;
	}
	
	public void setTextos(List<TextoApresentacao> textos) {
		this.textos = textos;
	}
	
	// Monta o VO.
	public ApresentacaoVO toVO() {
		// Declarações
		ApresentacaoVO vo;
		List<AnexoApresentacaoVO> anexosVO;
		List<TextoApresentacaoVO> textosVO;

		// Inicializações
		vo = new ApresentacaoVO();
		vo.setPk(this.getPk());
		vo.setOperadorAtualizacao(this.getOperadorAtualizacao());
		vo.setUltimaAtualizacao(this.getUltimaAtualizacao());
		
		vo.setNomeEs(ciclo.getEntidadeSupervisionavel().getNomeConglomeradoFormatado()); 
		
		if (CollectionUtils.isNotEmpty(this.getAnexos())) {
		    // Inicializa a lista de VOs.
		    anexosVO = new ArrayList<AnexoApresentacaoVO>(this.getAnexos().size());
		    
		    // Converte os anexos para VO.
		    for (AnexoApresentacao anexo : this.getAnexos()) {
		        // Adiciona o anexo à lista.
		        anexosVO.add(anexo.toVO());
		    }
		    
		    // Recupera os anexos.
		    vo.setAnexosVO(anexosVO);
		} else {
		    vo.setAnexosVO(new ArrayList<AnexoApresentacaoVO>());
		}
		
		if (CollectionUtils.isNotEmpty(this.getTextos())) {
		    // Inicializa a lista de VOs.
		    textosVO = new ArrayList<TextoApresentacaoVO>(this.getTextos().size());
		    
		    // Converte os textos para VO.
		    for (TextoApresentacao texto : this.getTextos()) {
		        // Adiciona o texto à lista.
		        textosVO.add(texto.toVO());
		    }
		    
		    // Recupera os textos.
		    vo.setTextosVO(textosVO);
		} else {
		    vo.setTextosVO(new ArrayList<TextoApresentacaoVO>());
		}
		
		return vo;
	}

}
