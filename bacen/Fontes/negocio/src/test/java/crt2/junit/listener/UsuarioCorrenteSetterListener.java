package crt2.junit.listener;

import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import br.gov.bcb.app.stuff.seguranca.ProvedorInformacoesUsuarioSimples;
import br.gov.bcb.app.stuff.seguranca.Usuario;
import br.gov.bcb.app.stuff.seguranca.UsuarioCorrente;

public class UsuarioCorrenteSetterListener extends AbstractTestExecutionListener {

    @Override
    public void beforeTestClass(final TestContext testContext) {
        UsuarioCorrente.set(new Usuario(new ProvedorInformacoesUsuarioSimples("deinf.teste")));
    }

    @Override
    public void afterTestClass(final TestContext testContext) {
        UsuarioCorrente.set(null);
    }
}
