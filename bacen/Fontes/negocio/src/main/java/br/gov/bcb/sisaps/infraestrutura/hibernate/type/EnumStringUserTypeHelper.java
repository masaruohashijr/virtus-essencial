package br.gov.bcb.sisaps.infraestrutura.hibernate.type;

import java.util.Properties;

import org.hibernate.MappingException;
import org.hibernate.usertype.ParameterizedType;

public class EnumStringUserTypeHelper<E extends Enum<E>> implements ParameterizedType {

    protected static final String RAWTYPES = "rawtypes";
    protected Class<E> clazz;

    @Override
    @SuppressWarnings("unchecked")
    public void setParameterValues(Properties params) {
        try {
            String enumClassName = params.getProperty("enumClass");
            this.clazz = (Class<E>) Class.forName(enumClassName);
        } catch (java.lang.ClassNotFoundException e) {
            throw new MappingException("enumClass " + " not found", e);
        }
    }

}