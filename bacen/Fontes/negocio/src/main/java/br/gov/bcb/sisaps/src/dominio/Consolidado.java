package br.gov.bcb.sisaps.src.dominio;

import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistenteAuditavelVersionado;

@Entity
@Table(name = "CON_CONSOLIDADO_APS", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = Consolidado.CAMPO_ID)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "CON_DH_ATUALZ", nullable = false)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "CON_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(20)")),
        @AttributeOverride(name = ObjetoPersistenteAuditavelVersionado.PROP_VERSAO, column = @Column(nullable = false, name = "CON_NU_VERSAO"))})
public class Consolidado extends ObjetoPersistenteAuditavelVersionado<Integer> {

    public static final String CAMPO_ID = "CON_ID";
    
    private String supervisao;
    private String cnpjEsDefault;
    private List<EntidadeUnicad> entidadesUnicad;

    @Column(name = "CON_DS_SUPERVISAO", length = 30)
    public String getSupervisao() {
        return supervisao;
    }

    @Column(name = "CON_CD_CNPJ_DEFAULT", length = 10)
    public String getCnpjEsDefault() {
        return cnpjEsDefault;
    }

    public void setSupervisao(String supervisao) {
        this.supervisao = supervisao;
    }

    public void setCnpjEsDefault(String cnpjEsDefault) {
        this.cnpjEsDefault = cnpjEsDefault;
    }

    @OneToMany(mappedBy = "consolidado", fetch = FetchType.LAZY)
    public List<EntidadeUnicad> getEntidadesUnicad() {
        return entidadesUnicad;
    }

    public void setEntidadesUnicad(List<EntidadeUnicad> entidadesUnicad) {
        this.entidadesUnicad = entidadesUnicad;
    }

}
