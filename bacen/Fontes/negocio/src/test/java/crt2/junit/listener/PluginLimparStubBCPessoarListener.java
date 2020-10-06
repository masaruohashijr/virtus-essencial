package crt2.junit.listener;

import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import br.gov.bcb.especificacao.spring.listener.stub.Stub;
import br.gov.bcb.sisaps.stubs.integracao.pessoa.BcPessoaProviderStub;

public class PluginLimparStubBCPessoarListener extends AbstractTestExecutionListener {

    @Override
    public void afterTestClass(TestContext testContext) throws Exception {
        ApplicationContext applicationContext = testContext.getApplicationContext();
        String[] beanNeames = applicationContext.getBeanNamesForType(Stub.class);
        for (String beanName : beanNeames) {
            Stub stub = (Stub) applicationContext.getBean(beanName);

            if (stub instanceof BcPessoaProviderStub) {
                ((BcPessoaProviderStub) stub).limparTodos();
            } 

        }
    }
}
