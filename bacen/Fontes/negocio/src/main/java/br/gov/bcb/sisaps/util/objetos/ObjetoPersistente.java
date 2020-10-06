/*
 * Sistema APS
 * ObjetoPersistente.java
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arquivo contém informações proprietárias.
 */
package br.gov.bcb.sisaps.util.objetos;

import java.io.Serializable;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import br.gov.bcb.app.stuff.hibernate.IObjetoPersistente;

@MappedSuperclass
public class ObjetoPersistente implements IObjetoPersistente<Integer>, Serializable {

    public static final String JODA_LOCAL_DATE = "br.gov.bcb.sisaps.infraestrutura.hibernate.type.LocalDateUserType";

    public static final String JODA_TIME = "br.gov.bcb.sisaps.infraestrutura.hibernate.type.LocalTimeUserType";

    public static final String JODA_DATE_TIME = "org.jadira.usertype.dateandtime.joda.PersistentDateTime";

    public static final String PROP_ID = "pk";

    private static final long serialVersionUID = 1L;

    private Integer pk;

    @Transient
    public Integer getPk() {
        return pk;
    }

    public void setPk(Integer pk) {
        this.pk = pk;
    }

}
