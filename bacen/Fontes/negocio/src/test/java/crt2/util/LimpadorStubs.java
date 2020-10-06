package crt2.util;

import nu.xom.Node;

import org.specrunner.context.IContext;
import org.specrunner.listeners.IScenarioListener;
import org.specrunner.result.IResultSet;
import org.springframework.context.ApplicationContext;

import br.gov.bcb.especificacao.spring.listener.stub.Stub;
import br.gov.bcb.sisaps.util.spring.SpringUtilsExtended;

public class LimpadorStubs implements IScenarioListener {

    @Override
    public void beforeScenario(String title, Node node, IContext context, IResultSet result, Object instance) {
        ApplicationContext applicationContext = SpringUtilsExtended.get().getApplicationContext();
        String[] beanNeames = applicationContext.getBeanNamesForType(Stub.class);
        for (String beanName : beanNeames) {
            Stub stub = (Stub) applicationContext.getBean(beanName);
            stub.limpar();
        }
    }

    @Override
    public void afterScenario(String title, Node node, IContext context, IResultSet result, Object instance) {
    }

}
