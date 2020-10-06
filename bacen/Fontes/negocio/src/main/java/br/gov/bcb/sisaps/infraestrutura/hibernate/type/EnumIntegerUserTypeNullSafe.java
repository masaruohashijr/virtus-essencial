package br.gov.bcb.sisaps.infraestrutura.hibernate.type;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;

import br.gov.bcb.utils.logging.BCLogFactory;
import br.gov.bcb.utils.logging.BCLogger;

public abstract class EnumIntegerUserTypeNullSafe<E extends Enum<E>> {

    protected static final BCLogger LOG = BCLogFactory.getLogger(EnumIntegerUserTypeNullSafe.class);
    protected static final int SQL_TYPE = Types.INTEGER;

    public void nullSafeSet(PreparedStatement pstm, Object value, int index, SessionImplementor session)
            throws HibernateException, SQLException {
        if (value == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Binding null to parameter: " + index);
            }
            pstm.setNull(index, SQL_TYPE);
        } else {
            @SuppressWarnings("unchecked")
            Integer codigoEnum = ((EnumeracaoComCodigoDescricao<Integer>) value).getCodigo();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Binding '" + codigoEnum + "' to parameter: " + index);
            }
            pstm.setInt(index, codigoEnum);
        }
    }

    @SuppressWarnings("PMD.SuspiciousEqualsMethodName")
    public boolean equals(Object x, Object y) throws HibernateException {
        if (x == null || y == null) {
            return false;
        }
        return x.equals(y);
    }

    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

}