/*
] * Sistema APS
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;

public class EnumIntegerUserType<E extends Enum<E>> extends
        EnumIntegerUserTypeNullSafe<E> implements UserType, ParameterizedType {

    private static final String RAWTYPES = "rawtypes";

    private static final int[] SQL_TYPES = {SQL_TYPE};

    private Class<E> clazz;

    @Override
    @SuppressWarnings("unchecked")
    public void setParameterValues(Properties params) {
        String enumClassName = params.getProperty("enumClass");
        if (enumClassName == null) {
            throw new MappingException("enumClassName parameter not specified");
        }
        try {
            this.clazz = (Class<E>) Class.forName(enumClassName);
        } catch (java.lang.ClassNotFoundException e) {
            throw new MappingException("enumClass " + enumClassName
                    + " not found", e);
        }
    }

    @Override
    public Object assemble(Serializable cache, Object owner)
            throws HibernateException {
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
    public Object nullSafeGet(ResultSet rs, String[] names,
            SessionImplementor session, Object owner)
            throws HibernateException, SQLException {
        Integer codigo = rs.getInt(names[0]);
        EnumeracaoComCodigoDescricao<Integer> enumObject = null;
        if (rs.wasNull()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Returning null as column '" + names[0] + "'.");
            }
        } else {
            enumObject = getEnum(codigo);
        }
        return enumObject;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private EnumeracaoComCodigoDescricao<Integer> getEnum(Integer codigo) {
        Class<? extends Enum> enumClass = returnedClass();
        Enum[] enums = enumClass.getEnumConstants();
        for (int i = 0; i < enums.length; i++) {
            if (((EnumeracaoComCodigoDescricao<Integer>) enums[i]).getCodigo().equals(
                    codigo)) {
                return (EnumeracaoComCodigoDescricao<Integer>) enums[i];
            }
        }
        return null;
    }

    @Override
    public Object replace(Object original, Object target, Object owner)
            throws HibernateException {
        return original;
    }

    @Override
    @SuppressWarnings(RAWTYPES)
    public Class returnedClass() {
        return clazz;
    }

    @Override
    public int[] sqlTypes() {
        return SQL_TYPES.clone();
    }

}
