package listeners;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.specrunner.sql.database.DatabaseException;
import org.specrunner.sql.database.DatabaseRegisterEvent;
import org.specrunner.sql.database.DatabaseTableEvent;
import org.specrunner.sql.database.IDatabaseListener;
import org.specrunner.sql.meta.Table;
import org.specrunner.util.xom.node.RowAdapter;
import org.specrunner.util.xom.node.TableAdapter;

public class DatabaseStrictListener implements IDatabaseListener {

    @Override
    public void initialize() {
    }

    @Override
    public void onTableIn(DatabaseTableEvent event) throws DatabaseException {
        // deinf.thiagol: comparação estrita
        TableAdapter adapter = event.getAdapter();
        boolean strict = Boolean.valueOf(adapter.getAttribute("strict", "false"));
        if (strict) {
            checkCount(event.getConnection(), event.getTable(), adapter.getRows());
        }
    }

    @Override
    public void onRegisterIn(DatabaseRegisterEvent event) throws DatabaseException {
    }

    @Override
    public void onRegisterOut(DatabaseRegisterEvent event) throws DatabaseException {
    }

    @Override
    public void onTableOut(DatabaseTableEvent event) throws DatabaseException {
    }

    private void checkCount(Connection con, Table table, List<RowAdapter> rows) throws DatabaseException {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.createStatement();
            String alias = table.getParent().getName() + "." + table.getName();
            rs = stmt.executeQuery("select count(*) from " + alias);
            if (rs.next()) {
                int received = rs.getInt(1);
                int expected = rows.size() - 1;
                if (received != expected) {
                    throw new DatabaseException("The number of elements in table '" + alias + "' table is " + received
                            + ", expected was " + expected);
                }
            } else {
                throw new DatabaseException("Could not count rows for table '" + alias + "'.");
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    throw new DatabaseException(e);
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    throw new DatabaseException(e);
                }
            }
        }
    }
}
