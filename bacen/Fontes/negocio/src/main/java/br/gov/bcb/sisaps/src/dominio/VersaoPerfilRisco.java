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
package br.gov.bcb.sisaps.src.dominio;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistente;
import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoObjetoVersionadorEnum;

@Entity
@Table(name = "VER_VERSAO_PERFIL_RISCO", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = VersaoPerfilRisco.CAMPO_ID))})
public class VersaoPerfilRisco extends ObjetoPersistente<Integer> {
    
    public static final String CAMPO_ID = "VER_ID";
    
    private TipoObjetoVersionadorEnum tipoObjetoVersionador; 
    
    private List<PerfilRisco> perfisRisco;
    
    @Column(name = "VER_CD_TIPO", nullable = false, columnDefinition = "smallint")
    @Type(type = "br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumStringUserType", parameters = {@Parameter(name = "enumClass", value = TipoObjetoVersionadorEnum.CLASS_NAME)})
    public TipoObjetoVersionadorEnum getTipoObjetoVersionador() {
        return tipoObjetoVersionador;
    }

    public void setTipoObjetoVersionador(TipoObjetoVersionadorEnum tipoObjetoVersionador) {
        this.tipoObjetoVersionador = tipoObjetoVersionador;
    }

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "versoesPerfilRisco")
    public List<PerfilRisco> getPerfisRisco() {
        return perfisRisco;
    }

    public void setPerfisRisco(List<PerfilRisco> perfisRisco) {
        this.perfisRisco = perfisRisco;
    }
    
    public void addPerfilRisco(PerfilRisco perfilRisco) {
        if (perfisRisco == null) {
            List<PerfilRisco> perfis = new ArrayList<PerfilRisco>();
            perfis.add(perfilRisco);
            this.setPerfisRisco(perfis);
        } else {
            perfisRisco.add(perfilRisco);
        }
    }

}
