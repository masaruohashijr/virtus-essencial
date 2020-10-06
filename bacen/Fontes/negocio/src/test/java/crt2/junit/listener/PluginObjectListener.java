package crt2.junit.listener;

import org.specrunner.SRServices;
import org.specrunner.plugins.core.objects.IObjectManager;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

public class PluginObjectListener extends AbstractTestExecutionListener {

    @Override
    public void beforeTestClass(final TestContext testContext) {
        SRServices.get(IObjectManager.class).clear();
    }
}
