package br.gov.bcb.sisaps.src.dominio;

import java.io.InputStream;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.SecaoApresentacaoEnum;
import br.gov.bcb.sisaps.src.vo.AnexoApresentacaoVO;
import br.gov.bcb.sisaps.util.IEntidadeJcifs;

@Entity
@Table(name = "AAP_ANEXO_APRESENTACAO", schema = "SUP")
@AttributeOverrides(value = {
		@AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = AnexoApresentacao.CAMPO_ID)),
		@AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "AAP_DH_ATUALZ", nullable = false)),
		@AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "AAP_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(20)")) })
public class AnexoApresentacao extends ObjetoPersistenteAuditavel<Integer>
		implements IEntidadeJcifs {
	public static final String CAMPO_ID = "AAP_ID";
	public static final String PROP_APRESENTACAO = "apresentacao";

	private static final int TAMANHO_LINK = 200;
	private Apresentacao apresentacao;
	private String link;
	private SecaoApresentacaoEnum secao;
	private transient InputStream inputStream;

	@ManyToOne(optional = false)
	@JoinColumn(name = Apresentacao.CAMPO_ID, nullable = false)
	public Apresentacao getApresentacao() {
		return apresentacao;
	}

	public void setApresentacao(Apresentacao apresentacao) {
		this.apresentacao = apresentacao;
	}

	@Column(name = "AAP_DS_LINK", nullable = false, length = TAMANHO_LINK)
	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	@Column(name = "AAP_CD_SECAO", nullable = false, columnDefinition = "smallint")
    @Type(type = "br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumIntegerUserType", parameters = {@Parameter(name = "enumClass", value = SecaoApresentacaoEnum.CLASS_NAME)})
	public SecaoApresentacaoEnum getSecao() {
		return secao;
	}

	public void setSecao(SecaoApresentacaoEnum secao) {
		this.secao = secao;
	}

	@Override
	@Transient
	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	// Monta o VO.
	public AnexoApresentacaoVO toVO() {
		// Declarações
		AnexoApresentacaoVO vo;

		// Inicializações
		vo = new AnexoApresentacaoVO();
		vo.setPk(this.getPk());
		vo.setDescricao("Anexo");
		vo.setLink(this.getLink());
		vo.setSecao(this.getSecao());
		vo.setOperadorAtualizacao(this.getOperadorAtualizacao());
		vo.setUltimaAtualizacao(this.getUltimaAtualizacao());

		return vo;
	}

}
