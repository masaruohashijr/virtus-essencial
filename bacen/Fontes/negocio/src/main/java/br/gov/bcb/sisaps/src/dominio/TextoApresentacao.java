package br.gov.bcb.sisaps.src.dominio;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.CampoApresentacaoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.SecaoApresentacaoEnum;
import br.gov.bcb.sisaps.src.vo.TextoApresentacaoVO;

@Entity
@Table(name = "TAP_TEXTO_APRESENTACAO", schema = "SUP")
@AttributeOverrides(value = {
		@AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = TextoApresentacao.CAMPO_ID)),
		@AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "TAP_DH_ATUALZ", nullable = false)),
		@AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "TAP_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(20)")) })
public class TextoApresentacao extends ObjetoPersistenteAuditavel<Integer>  {
	public static final String CAMPO_ID = "TAP_ID";
	public static final String PROP_APRESENTACAO = "apresentacao";

	private static final int TAMANHO_TEXTO = 100000;
    private static final String DEFINICAO_SMALLINT = "smallint";
	private Apresentacao apresentacao;
	private String texto;
	private SecaoApresentacaoEnum secao;
	private CampoApresentacaoEnum campo;

	@ManyToOne(optional = false)
	@JoinColumn(name = Apresentacao.CAMPO_ID, nullable = false)
	public Apresentacao getApresentacao() {
		return apresentacao;
	}

	public void setApresentacao(Apresentacao apresentacao) {
		this.apresentacao = apresentacao;
	}

    @Lob
    @Basic(fetch = FetchType.EAGER)
	@Column(name = "TAP_AN_TEXTO", nullable = false, length = TAMANHO_TEXTO)
	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	@Column(name = "TAP_CD_SECAO", nullable = false, columnDefinition = DEFINICAO_SMALLINT)
    @Type(type = "br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumIntegerUserType", parameters = {@Parameter(name = "enumClass", value = SecaoApresentacaoEnum.CLASS_NAME)})
	public SecaoApresentacaoEnum getSecao() {
		return secao;
	}

	public void setSecao(SecaoApresentacaoEnum secao) {
		this.secao = secao;
	}

	@Column(name = "TAP_CD_CAMPO", nullable = false, columnDefinition = DEFINICAO_SMALLINT)
    @Type(type = "br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumIntegerUserType", parameters = {@Parameter(name = "enumClass", value = CampoApresentacaoEnum.CLASS_NAME)})
	public CampoApresentacaoEnum getCampo() {
		return campo;
	}
	
	public void setCampo(CampoApresentacaoEnum campo) {
		this.campo = campo;
	}
	
	// Monta o VO.
	public TextoApresentacaoVO toVO() {
		// Declarações
		TextoApresentacaoVO vo;

		// Inicializações
		vo = new TextoApresentacaoVO();
		vo.setPk(this.getPk());
		vo.setTexto(this.getTexto());
		vo.setSecao(this.getSecao());
		vo.setCampo(this.getCampo());
		vo.setOperadorAtualizacao(this.getOperadorAtualizacao());
		vo.setUltimaAtualizacao(this.getUltimaAtualizacao());

		return vo;
	}

}
