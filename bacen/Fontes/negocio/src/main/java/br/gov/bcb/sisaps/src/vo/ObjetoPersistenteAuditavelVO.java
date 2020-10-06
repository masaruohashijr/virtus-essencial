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

import org.joda.time.DateTime;

import br.gov.bcb.comum.excecoes.BCNegocioException;
import br.gov.bcb.sisaps.adaptadores.pessoa.BcPessoaAdapter;
import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.geral.SisapsExcecaoUtil;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;


public class ObjetoPersistenteAuditavelVO extends ObjetoPersistenteVO {

    protected String operadorAtualizacao;
    private DateTime ultimaAtualizacao;

	public String getOperadorAtualizacao() {
        return operadorAtualizacao;
    }

	public void setOperadorAtualizacao(String operadorAtualizacao) {
        this.operadorAtualizacao = operadorAtualizacao;
    }

	public DateTime getUltimaAtualizacao() {
		return ultimaAtualizacao;
	}

	public void setUltimaAtualizacao(DateTime ultimaAtualizacao) {
		this.ultimaAtualizacao = ultimaAtualizacao;
	}

	// Formata a informa��o de altera��o.
    public static String getDadosFormatados(ObjetoPersistenteAuditavelVO objeto) {
    	// Declara��es
        ServidorVO servidorVO;
        StringBuilder odh;
        String dados;
        
        // Inicializa��es
        dados = "Sem altera��es salvas.";
        
        // Valida o objeto.
        if (objeto != null && objeto.getOperadorAtualizacao() != null && objeto.getUltimaAtualizacao() != null) {
            try {
            	// Formata os dados.
                servidorVO = BcPessoaAdapter.get().buscarServidorPorLogin(objeto.getOperadorAtualizacao());
                odh = new StringBuilder("�ltima altera��o salva ");
                odh.append(servidorVO == null ? "" : servidorVO.getNome());
                odh.append(" em ");
                odh.append(objeto.getUltimaAtualizacao().toString(Constantes.FORMATO_DATA_HORA_SEMSEGUNDOS));
                odh.append("h");
                dados = odh.toString();
                
            } catch (BCNegocioException e) {
                SisapsExcecaoUtil.lancarNegocioException(new ErrorMessage(e.getMessage()));
            }
        }
        
        return dados;
    }

}
