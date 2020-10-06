package br.gov.bcb.sisaps.util.spring;

import org.springframework.context.ApplicationContext;

import br.gov.bcb.app.stuff.util.SpringUtils;

public class SpringUtilsExtended extends SpringUtils {

    public static SpringUtilsExtended get() {
        return (SpringUtilsExtended) SpringUtils.get();
    }

    @Override
    public ApplicationContext getApplicationContext() {
        return super.getApplicationContext();
    }
}