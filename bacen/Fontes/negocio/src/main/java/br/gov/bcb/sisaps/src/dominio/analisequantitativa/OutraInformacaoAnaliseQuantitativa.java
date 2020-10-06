package br.gov.bcb.sisaps.src.dominio.analisequantitativa;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistente;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoInformacaoEnum;

@Entity
@Table(name = "OIA_OUTRA_INFO_ANALISE_QUANT", schema = "SUP")
@AttributeOverrides(value = {@AttributeOverride(name = ObjetoPersistente.PROP_ID, column = @Column(name = OutraInformacaoAnaliseQuantitativa.CAMPO_ID))})
public class OutraInformacaoAnaliseQuantitativa extends ObjetoPersistente<Integer> {

    public static final String CAMPO_ID = "OIA_ID";
    public static final int TAMANHO_NOME = 30;
    public static final int TAMANHO_DESCRICAO = 100;
    private static final String SMALLINT = "smallint";
    private static final String ENUM_CLASS = "enumClass";

    private String nome;
    private String descricao;
    private TipoInformacaoEnum tipoInformacao;
    private Integer codigoDataBaseInicio;
    private Integer codigoDataBaseFim;

    @Column(name = "OIA_NM", columnDefinition = "varchar(30)", length = TAMANHO_NOME)
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Column(name = "OIA_DS", columnDefinition = "varchar(100)", length = TAMANHO_DESCRICAO)
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Column(name = "OIA_CD_TIPO", nullable = false, columnDefinition = SMALLINT)
    @Type(type = "br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumIntegerUserType", 
        parameters = {@Parameter(name = ENUM_CLASS, value = TipoInformacaoEnum.CLASS_NAME)})
    public TipoInformacaoEnum getTipoInformacao() {
        return tipoInformacao;
    }

    public void setTipoInformacao(TipoInformacaoEnum tipoInformacao) {
        this.tipoInformacao = tipoInformacao;
    }
    
    @Column(name = "OIA_CD_DATABE_INI", nullable = false)
    public Integer getCodigoDataBaseInicio() {
        return codigoDataBaseInicio;
    }

    public void setCodigoDataBaseInicio(Integer codigoDataBaseInicio) {
        this.codigoDataBaseInicio = codigoDataBaseInicio;
    }

    @Column(name = "OIA_CD_DATABE_FIM")
    public Integer getCodigoDataBaseFim() {
        return codigoDataBaseFim;
    }

    public void setCodigoDataBaseFim(Integer codigoDataBaseFim) {
        this.codigoDataBaseFim = codigoDataBaseFim;
    }
}
