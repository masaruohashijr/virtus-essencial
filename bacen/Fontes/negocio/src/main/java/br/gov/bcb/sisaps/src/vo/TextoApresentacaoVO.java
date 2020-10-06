package br.gov.bcb.sisaps.src.vo;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import br.gov.bcb.sisaps.src.dominio.TextoApresentacao;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.CampoApresentacaoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.SecaoApresentacaoEnum;

public class TextoApresentacaoVO extends ObjetoPersistenteAuditavelVO implements
		Serializable {
	private String textoString;
	private SecaoApresentacaoEnum secao;
	private CampoApresentacaoEnum campo;

	public String getTexto() {
		return textoString;
	}

	public void setTexto(String texto) {
		this.textoString = texto;
	}

	public SecaoApresentacaoEnum getSecao() {
		return secao;
	}

	public void setSecao(SecaoApresentacaoEnum secao) {
		this.secao = secao;
	}

	public CampoApresentacaoEnum getCampo() {
		return campo;
	}

	public void setCampo(CampoApresentacaoEnum campo) {
		this.campo = campo;
	}

	public static List<TextoApresentacaoVO> converterParaListaVo(
			List<TextoApresentacao> anexos) {
		// Declarações
		List<TextoApresentacaoVO> listaVo;

		// Inicializações
		listaVo = new LinkedList<TextoApresentacaoVO>();

		// Converte os anexos para VO.
		for (TextoApresentacao textoApresentacao : anexos) {
			listaVo.add(textoApresentacao.toVO());
		}

		return listaVo;
	}

}
