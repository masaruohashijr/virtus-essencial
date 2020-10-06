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


public class RiscoVO implements Serializable {

	// Nome do risco
	private String nome;
	
	// Notas do risco.
	private String notaRisco;
	private String notaControle;
	private String notaResidual;
	
    private boolean isArcExterno;

    private boolean isEObrigatorio;

	// Conceito residual.
	private String conceitoResidual;
	
	// Síntese
	private String sintese;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNotaRisco() {
		return notaRisco;
	}

	public void setNotaRisco(String notaRisco) {
		this.notaRisco = notaRisco;
	}

	public String getNotaControle() {
		return notaControle;
	}

	public void setNotaControle(String notaControle) {
		this.notaControle = notaControle;
	}

	public String getNotaResidual() {
		return notaResidual;
	}

	public void setNotaResidual(String notaResidual) {
		this.notaResidual = notaResidual;
	}

	public String getConceitoResidual() {
		return conceitoResidual;
	}

	public void setConceitoResidual(String conceitoResidual) {
		this.conceitoResidual = conceitoResidual;
	}

	public String getSintese() {
		return sintese;
	}

	public void setSintese(String sintese) {
		this.sintese = sintese;
	}

    public boolean isArcExterno() {
        return isArcExterno;
    }

    public void setArcExterno(boolean isArcExterno) {
        this.isArcExterno = isArcExterno;
    }

    public boolean isEObrigatorio() {
        return isEObrigatorio;
    }

    public void setEObrigatorio(boolean isEObrigatorio) {
        this.isEObrigatorio = isEObrigatorio;
    }
	

}
