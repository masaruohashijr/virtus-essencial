package br.gov.bcb.sisaps.src.dominio.analisequantitativa;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistente;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoConta;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;

@Entity
@Table(name = "CAQ_CONTA_ANALISE_QUANT", schema = "SUP")
@AttributeOverrides(value = {@AttributeOverride(name = ObjetoPersistente.PROP_ID, column = @Column(name = ContaAnaliseQuantitativa.CAMPO_ID))})
public class ContaAnaliseQuantitativa extends ObjetoPersistente<Integer> {

    public static final String CAMPO_ID = "CAQ_ID";
    public static final int TAMANHO_NOME = 30;
    public static final int TAMANHO_DESCRICAO = 100;
    private static final String SMALLINT = "smallint";
    private static final String ENUM_CLASS = "enumClass";

    private String nome;
    private String descricao;
    private TipoConta tipoConta;
    private SimNaoEnum diversos;
    private Integer codigoDataBaseInicio;
    private Integer codigoDataBaseFim;

    @Column(name = "CAQ_NM_CONTA", columnDefinition = "varchar(30)", length = TAMANHO_NOME)
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Column(name = "CAQ_DS", columnDefinition = "varchar(100)", length = TAMANHO_DESCRICAO)
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Column(name = "CAQ_CD_TP_CONTA", nullable = false, columnDefinition = SMALLINT)
    @Type(type = "br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumIntegerUserType", parameters = {@Parameter(name = ENUM_CLASS, value = TipoConta.CLASS_NAME)})
    public TipoConta getTipoConta() {
        return tipoConta;
    }

    public void setTipoConta(TipoConta tipoConta) {
        this.tipoConta = tipoConta;
    }

    @Column(name = "CAQ_IB_DIVERSOS", nullable = false, columnDefinition = SMALLINT)
    @Type(type = "br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumIntegerUserType", parameters = {@Parameter(name = ENUM_CLASS, value = SimNaoEnum.CLASS_NAME)})
    public SimNaoEnum getDiversos() {
        return diversos;
    }

    public void setDiversos(SimNaoEnum diversos) {
        this.diversos = diversos;
    }
    
    @Column(name = "CAQ_CD_DATABE_INI", nullable = false)
    public Integer getCodigoDataBaseInicio() {
        return codigoDataBaseInicio;
    }

    public void setCodigoDataBaseInicio(Integer codigoDataBaseInicio) {
        this.codigoDataBaseInicio = codigoDataBaseInicio;
    }

    @Column(name = "CAQ_CD_DATABE_FIM")
    public Integer getCodigoDataBaseFim() {
        return codigoDataBaseFim;
    }

    public void setCodigoDataBaseFim(Integer codigoDataBaseFim) {
        this.codigoDataBaseFim = codigoDataBaseFim;
    }
}
