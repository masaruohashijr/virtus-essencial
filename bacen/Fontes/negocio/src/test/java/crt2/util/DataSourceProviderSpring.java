package crt2.util;

import javax.sql.DataSource;

import org.specrunner.sql.IDataSourceProvider;
import org.specrunner.sql.impl.SimpleDataSource;

/**
 * Implementation of a data source provider. Notice that in our application, if you use Spring, for example, you can
 * write the data source provider to recover the context data source.
 * 
 * @author Thiago Santos
 * 
 */
public class DataSourceProviderSpring implements IDataSourceProvider {

    private DataSource source;

    @Override
    public DataSource getDataSource() {
        if (source == null) {
            source =
                    new SimpleDataSource(DataSourceUrlProviderSpring.getDriver(), DataSourceUrlProviderSpring.getUrl(),
                            "sa", "");
        }
        return source;
    }

    @Override
    public void release() {
    }

    @Override
    public String toString() {
        return "DataSourceProviderSpring->" + source;
    }
}
