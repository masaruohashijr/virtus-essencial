/*
 * Sistema APS..
 * 
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arquivo contém informações proprietárias.
 */
package br.gov.bcb.sisaps.infraestrutura.hibernate.type;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.usertype.UserType;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class LocalTimeUserType implements UserType {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormat
            .forPattern("HH:mm:ss");

    @Override
    public int[] sqlTypes() {
        return new int[] {StandardBasicTypes.TIME.sqlType()};
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Class returnedClass() {
        return LocalTime.class;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value;
    }

    @Override
    public Object assemble(Serializable cached, Object owner)
            throws HibernateException {
        return cached;
    }

    @Override
    public Object replace(Object original, Object target, Object owner)
            throws HibernateException {
        return original;
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names,
            SessionImplementor session, Object owner)
            throws HibernateException, SQLException {
        LocalTime localTime = null;

        /*
         * JUSTIFICATIVA PARA OCORRÊNCIA DO LAPSE: PRECISO LER O VALOR DO CAMPO
         * PELO RESULTSET PARA QUE A HORA SEJA CONVERTIDA PARA UM LOCALTIME
         * CORRETAMENTE. VIDE JAVADOC DA CLASSE PARA ENTEDER MELHOR O PROBLEMA.
         */
        Object hora = rs.getObject(names[0]);

        if (hora != null) {
            localTime = TIME_FORMAT.parseDateTime(hora.toString())
                    .toLocalTime();
        }
        return localTime;
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session)
            throws HibernateException, SQLException {

        if (value == null) {
            st.setNull(index, StandardBasicTypes.STRING.sqlType());
        } else {
            LocalTime localTime = (LocalTime) value;
            st.setString(index, localTime.toString(TIME_FORMAT));
        }
    }

    @Override
    @SuppressWarnings("PMD.SuspiciousEqualsMethodName")
    public boolean equals(Object x, Object y) throws HibernateException {

        if (x == null || y == null) {
            return false;
        }

        if (x.equals(y)) {
            return true;
        }
        LocalTime dtx = (LocalTime) x;
        LocalTime dty = (LocalTime) y;
        return dtx.equals(dty);
    }

    @Override
    public int hashCode(Object object) throws HibernateException {
        return object.hashCode();
    }
}
