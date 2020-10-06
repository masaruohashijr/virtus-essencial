package br.gov.bcb.sisaps.batch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import br.gov.bcb.app.stuff.seguranca.Usuario;
import br.gov.bcb.app.stuff.seguranca.UsuarioCorrente;
import br.gov.bcb.sisaps.batch.util.ProvedorInformacoesUsuarioBatch;

public abstract class AbstractBatchApplication {
    
    public static boolean EM_CONTEXTO_DE_TESTE = 
            System.getProperty("os.name", "linux").toLowerCase().indexOf("windows") >= 0;
    private static ClassPathXmlApplicationContext applicationContext;

    protected void configurarUsuario() {
        Usuario usuario = new Usuario(new ProvedorInformacoesUsuarioBatch());
        UsuarioCorrente.set(usuario);
    }

    protected abstract void executar();

    public List<String> getContextos() {
        if (EM_CONTEXTO_DE_TESTE) {
            return new ArrayList<String>(Arrays.asList("applicationContext-batchTeste.xml"));
        } else {
            return new ArrayList<String>(Arrays.asList("applicationContext-batchQuartz.xml"));
        }
    }

    public void configurarContexto() {
        if (applicationContext == null) {
            applicationContext =
                    new ClassPathXmlApplicationContext(getContextos().toArray(new String[getContextos().size()]));
        }
    }

    protected ApplicationContext getSpringContext() {
        return applicationContext;
    }

    public void init() {
        configurarContexto();
        configurarUsuario();
        executar();
    }

}
