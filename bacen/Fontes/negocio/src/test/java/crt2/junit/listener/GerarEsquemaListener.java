package crt2.junit.listener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import crt2.AbstractTestesNegocio;

public class GerarEsquemaListener extends AbstractTestExecutionListener {

    private static boolean gerado = false;

    @Override
    public void prepareTestInstance(TestContext testContext) throws Exception {
        if (!gerado) {
            boolean old = AbstractTestesNegocio.usarBaseExterna;
            try {
                AbstractTestesNegocio.usarBaseExterna = false;
                ApplicationContext context = testContext.getApplicationContext();
                LocalSessionFactoryBean sessionFactory = context.getBean(LocalSessionFactoryBean.class);
                Configuration c = sessionFactory.getConfiguration();
                c.setProperty(Environment.DEFAULT_SCHEMA, "SUP");
                c.configure(GerarEsquemaListener.class.getResource("dialeto.xml"));

                String bancoHibernate = "src/test/resources/sgbd_hibernate.sql";
                SchemaExport export = new SchemaExport(c);
                export.setOutputFile(bancoHibernate);
                export.create(true, false);

                FileWriter fw = null;
                try {
                    String bancoFixo = "src/test/resources/sgbd_manual.sql";
                    String bancoFinal = "src/test/resources/sgbd_final.sql";
                    fw = new FileWriter(bancoFinal);
                    String[] partes = new String[] {bancoFixo, bancoHibernate};
                    for (String s : partes) {
                        BufferedReader br = new BufferedReader(new FileReader(new File(s)));
                        String input = br.readLine();
                        while (input != null) {
                            fw.write(input + "\n");
                            input = br.readLine();
                        }
                        br.close();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    try {
                        fw.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                gerado = true;
            } finally {
                AbstractTestesNegocio.usarBaseExterna = old;
            }
        }
    }
}
