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
package br.gov.bcb.sisaps.infraestrutura.hibernate.type;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;

public class EnumStringUserType<E extends Enum<E>> extends EnumStringUserTypeSqlTypes<E> {

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    @Override
    public Object assemble(Serializable cache, Object owner) throws HibernateException {
        return cache;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
            throws HibernateException, SQLException {
        String codigo = rs.getString(names[0]);
        return verificaResultSet(rs, names, codigo);
    }

    @Override
    public void nullSafeSet(PreparedStatement pstm, Object value, int index, SessionImplementor session)
            throws HibernateException, SQLException {
        verificaObjeto(pstm, value, index);
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    @Override
    @SuppressWarnings("PMD.SuspiciousEqualsMethodName")
    public boolean equals(Object x, Object y) throws HibernateException {
        if (x == null || y == null) {
            return false;
        }
        return x.equals(y);
    }

}
