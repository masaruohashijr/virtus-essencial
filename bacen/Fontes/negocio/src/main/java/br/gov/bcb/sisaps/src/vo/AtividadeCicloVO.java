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
    		retorno = "Não iniciada";
    	} else if ("2".equals(situacao.trim())) {
    		retorno = "Em execução";
    	} else if ("3".equals(situacao.trim())) {
    		retorno = "Concluída";
    	} else if ("4".equals(situacao.trim())) {
    		retorno = "Cancelada";
    	} else if ("5".equals(situacao.trim())) {
    		retorno = "Suspensa";
    	} else if ("6".equals(situacao.trim())) {
    		retorno = "Ações";
    	} else if ("7".equals(situacao.trim())) {
    		retorno = "Início em atraso";
    	} else if ("8".equals(situacao.trim())) {
    		retorno = "Fim em atraso";
    	} else if ("9".equals(situacao.trim())) {
    		retorno = "Cancelamento solicitado";
    	} else if ("10".equals(situacao.trim())) {
    		retorno = "Pendente de definição";
    	} else if ("11".equals(situacao.trim())) {
    		retorno = "Interrupção solicitada";
    	} else if ("12".equals(situacao.trim())) {
    		retorno = "Interrompida";
    	} else {
    		retorno = "Situação desconhecida: " + situacao;
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
