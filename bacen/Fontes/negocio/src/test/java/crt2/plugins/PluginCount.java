package crt2.plugins;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.specrunner.context.IContext;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.type.Assertion;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.result.status.Success;
import org.specrunner.sql.AbstractPluginDatabase;
import org.specrunner.sql.IDataSourceProvider;
import org.specrunner.sql.PluginConnection;
import org.specrunner.sql.PluginDatabase;
import org.specrunner.sql.PluginSchema;
import org.specrunner.sql.meta.EMode;
import org.specrunner.sql.meta.Schema;
import org.specrunner.sql.util.StringUtil;
import org.specrunner.util.UtilLog;
import org.specrunner.util.xom.node.TableAdapter;

public class PluginCount extends AbstractPluginDatabase {

    public PluginCount() {
        super(EMode.OUTPUT);
    }

    @Override
    public void doEnd(IContext context, IResultSet result, TableAdapter tableAdapter) throws PluginException {
        Schema schema = PluginSchema.getSchema(context, getSchema());
        if (UtilLog.LOG.isDebugEnabled()) {
            UtilLog.LOG.debug(getClass().getSimpleName() + "     schema(" + getSchema() + "):" + schema);
        }
        String[] sources = StringUtil.tokenize(
                getDatasource() != null ? getDatasource() : PluginConnection.DEFAULT_CONNECTION_NAME, getSeparator());
        String[] bases = StringUtil
                .tokenize(getDatabase() != null ? getDatabase() : PluginDatabase.DEFAULT_DATABASE_NAME, getSeparator());
        int erros = 0;
        for (int i = 0; i < sources.length && i < bases.length; i++) {
            String source = sources[i];
            String base = bases[i];
            IDataSourceProvider datasource = PluginConnection.getProvider(context, source);
            DataSource ds = datasource.getDataSource();
            Connection connection = null;
            Statement stmt = null;
            ResultSet rs = null;
            try {
                connection = ds.getConnection();
                if (UtilLog.LOG.isDebugEnabled()) {
                    DatabaseMetaData metaData = connection.getMetaData();
                    UtilLog.LOG
                            .debug(getClass().getSimpleName() + " connection:(" + metaData.getURL() + ")" + connection);
                }
                stmt = connection.createStatement();
                String tableAlias = tableAdapter.getCaption(0).getValue();
                String expectedSize = tableAdapter.getRow(1).getCell(1).getValue();
                rs = stmt.executeQuery(
                        "select count(1) from " + schema.getName() + "." + schema.getAlias(tableAlias).getName());
                if (rs.next()) {
                    Object received = rs.getObject(1);
                    if (!expectedSize.equals(String.valueOf(received))) {
                        result.addResult(Failure.INSTANCE, context.peek(),
                                new PluginException("Expected '" + expectedSize + "', received '" + received + "'"));
                    }
                } else {
                    result.addResult(Failure.INSTANCE, context.peek(),
                            new PluginException("Table '" + expectedSize + "'not found."));
                }
            } catch (SQLException e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
                erros++;
                result.addResult(Failure.INSTANCE, context.peek(),
                        new PluginException("Error in datasource: " + source + ", and database: " + base
                                + ", and schema: " + schema.getAlias() + ". Error: " + e.getMessage(), e));
            } finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                } catch (SQLException e) {
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug(e.getMessage(), e);
                    }
                    erros++;
                    result.addResult(Failure.INSTANCE, context.peek(),
                            new PluginException("Error in datasource: " + source + ", and database: " + base
                                    + ", and schema: " + schema.getAlias() + ". Error: " + e.getMessage(), e));
                }
                try {
                    if (stmt != null) {
                        stmt.close();
                    }
                } catch (SQLException e) {
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug(e.getMessage(), e);
                    }
                    erros++;
                    result.addResult(Failure.INSTANCE, context.peek(),
                            new PluginException("Error in datasource: " + source + ", and database: " + base
                                    + ", and schema: " + schema.getAlias() + ". Error: " + e.getMessage(), e));
                }
                try {
                    if (connection != null) {
                        connection.commit();
                    }
                } catch (SQLException e) {
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug(e.getMessage(), e);
                    }
                    erros++;
                    result.addResult(Failure.INSTANCE, context.peek(),
                            new PluginException("Error in datasource: " + source + ", and database: " + base
                                    + ", and schema: " + schema.getAlias() + ". Error: " + e.getMessage(), e));
                }
            }
        }
        if (erros == 0) {
            result.addResult(Success.INSTANCE, context.peek());
        }
    }

    @Override
    public ActionType getActionType() {
        return Assertion.INSTANCE;
    }
}
