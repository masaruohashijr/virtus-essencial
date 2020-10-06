package br.gov.bcb.sisaps.infraestrutura.hibernate.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.usertype.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.gov.bcb.dominio.stuff.enumeracao.EnumComCodigo;

public abstract class EnumStringUserTypeVerificacao<E extends Enum<E>> extends EnumStringUserTypeHelper<E> implements
        UserType {

    protected static final int SQL_TYPE = Types.VARCHAR;
    private static final Logger LOG = LoggerFactory.getLogger(EnumStringUserTypeVerificacao.class);

    protected EnumComCodigo<String> verificaResultSet(ResultSet rs, String[] names, String codigo) throws SQLException {
        EnumComCodigo<String> enumObjectTemp = null;
        if (rs.wasNull()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Returning null as column '" + names[0] + "'.");
            }
        } else {
            enumObjectTemp = getEnum(codigo);
        }
        return enumObjectTemp;
    }

    protected void verificaObjeto(PreparedStatement pstm, Object value, int index) throws SQLException {
        if (value == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Binding null to parameter: " + index);
            }
            pstm.setNull(index, SQL_TYPE);
        } else {
            criaPrepareStatement(pstm, value, index);
        }
    }

    @SuppressWarnings("unchecked")
	private void criaPrepareStatement(PreparedStatement pstm, Object value, int index) throws SQLException {
        String codigoEnum = ((EnumComCodigo<String>) value).getCodigo();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Binding '" + codigoEnum + "' to parameter: " + index);
        }
        pstm.setString(index, codigoEnum);
    }

    @SuppressWarnings(RAWTYPES)
    @Override
    public Class<? extends Enum> returnedClass() {
        return clazz;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected EnumComCodigo<String> getEnum(String codigo) {
        Class<? extends Enum> enumClass = returnedClass();
        Enum[] enums = enumClass.getEnumConstants();
        for (int i = 0; i < enums.length; i++) {
            if (((EnumComCodigo<String>) enums[i]).getCodigo().equals(codigo)) {
                return (EnumComCodigo<String>) enums[i];
            }
        }
        return null;
    }

}