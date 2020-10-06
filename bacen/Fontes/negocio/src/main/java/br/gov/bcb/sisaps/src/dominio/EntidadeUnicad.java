package br.gov.bcb.sisaps.src.dominio;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistenteAuditavelVersionado;

@Entity
@Table(name = "ENU_ENTIDADE_UNICAD", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = EntidadeUnicad.CAMPO_ID)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "ENU_DH_ATUALZ", nullable = false)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "ENU_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(20)")),
        @AttributeOverride(name = ObjetoPersistenteAuditavelVersionado.PROP_VERSAO, column = @Column(nullable = false, name = "ENU_NU_VERSAO"))})
public class EntidadeUnicad extends ObjetoPersistenteAuditavelVersionado<Integer> {

    public static final String CAMPO_ID = "ENU_ID";
    
    private String nome;
    private String nomeAbreviado;
    private String cnpjConglomerado;
    private String cnpjLider;
    private Consolidado consolidado;
    
    @Column(name = "ENU_NM", length = 200)
    public String getNome() {
        return nome;
    }
    
    @Column(name = "ENU_NM_ABREVIADO", length = 30)
    public String getNomeAbreviado() {
        return nomeAbreviado;
    }
    
    @Column(name = "ENU_CD_CNPJ", length = 10)
    public String getCnpjConglomerado() {
        return cnpjConglomerado;
    }
    
    @Column(name = "ENU_CD_CNPJ_LIDER", length = 8)
    public String getCnpjLider() {
        return cnpjLider;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setNomeAbreviado(String nomeAbreviado) {
        this.nomeAbreviado = nomeAbreviado;
    }

    public void setCnpjConglomerado(String cnpjConglomerado) {
        this.cnpjConglomerado = cnpjConglomerado;
    }

    public void setCnpjLider(String cnpjLider) {
        this.cnpjLider = cnpjLider;
    }

    @JoinColumn(name = "CON_ID", referencedColumnName = "CON_ID")
    @ManyToOne(targetEntity = Consolidado.class)
    public Consolidado getConsolidado() {
        return consolidado;
    }

    public void setConsolidado(Consolidado consolidado) {
        this.consolidado = consolidado;
    }

}
