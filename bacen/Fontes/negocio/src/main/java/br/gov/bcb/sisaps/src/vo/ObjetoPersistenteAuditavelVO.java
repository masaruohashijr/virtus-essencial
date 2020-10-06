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

	// Formata a informação de alteração.
    public static String getDadosFormatados(ObjetoPersistenteAuditavelVO objeto) {
    	// Declarações
        ServidorVO servidorVO;
        StringBuilder odh;
        String dados;
        
        // Inicializações
        dados = "Sem alterações salvas.";
        
        // Valida o objeto.
        if (objeto != null && objeto.getOperadorAtualizacao() != null && objeto.getUltimaAtualizacao() != null) {
            try {
            	// Formata os dados.
                servidorVO = BcPessoaAdapter.get().buscarServidorPorLogin(objeto.getOperadorAtualizacao());
                odh = new StringBuilder("Última alteração salva ");
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
