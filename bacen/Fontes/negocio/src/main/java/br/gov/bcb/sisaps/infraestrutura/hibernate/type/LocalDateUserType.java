/*
 * Sistema APS..
 * 
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software � confidencial e propriedade do Banco Central do Brasil.
 * N�o � permitida sua distribui��o ou divulga��o do seu conte�do sem
 * expressa autoriza��o do Banco Central.
 * Este arquivo cont�m informa��es propriet�rias.
 */
package br.gov.bcb.sisaps.infraestrutura.hibernate.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.StandardBasicTypes;
import org.jadira.usertype.dateandtime.joda.PersistentLocalDate;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;

public class LocalDateUserType extends PersistentLocalDate {

    @Override
    public void nullSafeSet(PreparedStatement preparedStatement, Object value,
            int index, SessionImplementor session) throws HibernateException,
            SQLException {

        if (value == null) {
            StandardBasicTypes.DATE.nullSafeSet(preparedStatement, null, index,
                    session);
        } else {
            preparedStatement.setString(index, value.toString());
        }
    }

    @Override
    public LocalDate nullSafeGet(ResultSet resultSet, String[] string,
            SessionImplementor session, Object value) throws SQLException {
        Object timestamp = StandardBasicTypes.DATE.nullSafeGet(resultSet,
                string, session, value);
        if (timestamp == null) {
            return null;
        }
        return new LocalDate(timestamp, DateTimeZone.UTC);
    }
}
