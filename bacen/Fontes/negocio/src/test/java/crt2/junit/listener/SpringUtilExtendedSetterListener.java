package crt2.junit.listener;

import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import br.gov.bcb.sisaps.util.spring.SpringUtilsExtended;

public class SpringUtilExtendedSetterListener extends AbstractTestExecutionListener {

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        try {
            SpringUtilsExtended.get();
        } catch (Throwable e) {
            SpringUtilsExtended.set(new SpringUtilsExtended());
        }
        SpringUtilsExtended.get().setApplicationContext(testContext.getApplicationContext());
    }
}
