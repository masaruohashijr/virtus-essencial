/*
 * Sistema APS
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software � confidencial e propriedade do Banco Central do Brasil.
 * N�o � permitida sua distribui��o ou divulga��o do seu conte�do sem
 * expressa autoriza��o do Banco Central.
 * Este arquivo cont�m informa��es propriet�rias.
 */
package br.gov.bcb.sisaps.src.vo;

import java.text.SimpleDateFormat;
import java.util.Date;

import br.gov.bcb.sisaps.util.Constantes;


public class AtividadeCicloVO extends ObjetoPersistenteVO {
    
    private String cnpjES;
    private String codigo;
    private Date dataBase;
    private Short ano;
    private String descricao;
    private String situacao;
    
    public String getCnpjES() {
        return cnpjES;
    }
    
    public void setCnpjES(String cnpjES) {
        this.cnpjES = cnpjES;
    }
    
    public String getCodigo() {
        return codigo;
    }
    
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    
    public Date getDataBase() {
        return dataBase;
    }
    
    public void setDataBase(Date dataBase) {
        this.dataBase = dataBase;
    }
    
    public Short getAno() {
        return ano;
    }
    
    public void setAno(Short ano) {
        this.ano = ano;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    public String getSituacao() {
    	String retorno = "";
    	
    	if ("0".equals(situacao.trim())) {
    		retorno = "Proposta de extra - PAS";
    	} else if ("1".equals(situacao.trim())) {
    		retorno = "N�o iniciada";
    	} else if ("2".equals(situacao.trim())) {
    		retorno = "Em execu��o";
    	} else if ("3".equals(situacao.trim())) {
    		retorno = "Conclu�da";
    	} else if ("4".equals(situacao.trim())) {
    		retorno = "Cancelada";
    	} else if ("5".equals(situacao.trim())) {
    		retorno = "Suspensa";
    	} else if ("6".equals(situacao.trim())) {
    		retorno = "A��es";
    	} else if ("7".equals(situacao.trim())) {
    		retorno = "In�cio em atraso";
    	} else if ("8".equals(situacao.trim())) {
    		retorno = "Fim em atraso";
    	} else if ("9".equals(situacao.trim())) {
    		retorno = "Cancelamento solicitado";
    	} else if ("10".equals(situacao.trim())) {
    		retorno = "Pendente de defini��o";
    	} else if ("11".equals(situacao.trim())) {
    		retorno = "Interrup��o solicitada";
    	} else if ("12".equals(situacao.trim())) {
    		retorno = "Interrompida";
    	} else {
    		retorno = "Situa��o desconhecida: " + situacao;
    	}
    	
    	return retorno;
    }
    
    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }
    
    public String getDataBaseFormatada() {
        return this.dataBase == null ? 
                "" : new SimpleDateFormat(Constantes.FORMATO_DATA_MES_ANO).format(this.dataBase);
    }
    
}
