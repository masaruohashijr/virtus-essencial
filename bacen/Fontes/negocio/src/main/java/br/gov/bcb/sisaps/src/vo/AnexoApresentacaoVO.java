package br.gov.bcb.sisaps.src.vo;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import br.gov.bcb.sisaps.src.dominio.AnexoApresentacao;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.SecaoApresentacaoEnum;

public class AnexoApresentacaoVO extends ObjetoPersistenteAuditavelVO implements
		Serializable {
	private SecaoApresentacaoEnum secao;
	private String link;
    private String descricao;

	public SecaoApresentacaoEnum getSecao() {
		return secao;
	}

	public void setSecao(SecaoApresentacaoEnum secao) {
		this.secao = secao;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

	public static List<AnexoApresentacaoVO> converterParaListaVo(
			List<AnexoApresentacao> anexos) {
		// Declarações
		List<AnexoApresentacaoVO> listaVo;

		// Inicializações
		listaVo = new LinkedList<AnexoApresentacaoVO>();

		// Converte os anexos para VO.
		for (AnexoApresentacao anexoApresentacao : anexos) {
			listaVo.add(anexoApresentacao.toVO());
		}

		return listaVo;
	}

}
