package crt2;

import org.junit.runner.RunWith;
import org.specrunner.junit.SRRunnerSpringScenario;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import br.gov.bcb.especificacao.spring.listener.StubCleanerListener;
import crt2.junit.listener.GerarEsquemaListener;
import crt2.junit.listener.PluginLimparStubBCPessoarListener;
import crt2.junit.listener.PluginObjectListener;
import crt2.junit.listener.SpringUtilExtendedSetterListener;
import crt2.junit.listener.UsuarioCorrenteSetterListener;

@RunWith(SRRunnerSpringScenario.class)
@ContextConfiguration(locations = {"classpath:/applicationContext-teste.xml"})
@TestExecutionListeners(inheritListeners = false, listeners = {DependencyInjectionTestExecutionListener.class,
        SpringUtilExtendedSetterListener.class, UsuarioCorrenteSetterListener.class, StubCleanerListener.class,
        GerarEsquemaListener.class, PluginObjectListener.class, PluginLimparStubBCPessoarListener.class})
@SuppressWarnings({"PMD.AbstractClassWithoutAnyMethod", "PMD.AbstractClassWithoutAbstractMethod",
        "PMD.AtLeastOneConstructor", "PMD.AbstractNaming"})
public abstract class ConfiguracaoTestesWeb extends AbstractTestesWeb {

}