package crt2;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import crt2.analisequalitativa.SuiteWebAQT;
import crt2.painelconsulta.SuiteWebPainelConsulta;
import crt2.paineldoinspetor.SuiteWebPainelDoInspetor;
import crt2.paineldosupervisor.SuiteWebPainelDoSupervisor;
import crt2.perfildeacesso.SuiteWebPerfilDeAcesso;
import crt2.perfilderisco.SuiteWebPerfilDeRisco;

@RunWith(Suite.class)
@SuiteClasses({SuiteWebAQT.class, SuiteWebPainelConsulta.class, SuiteWebPainelDoInspetor.class,
    SuiteWebPainelDoSupervisor.class, SuiteWebPerfilDeAcesso.class, SuiteWebPerfilDeRisco.class})
public class SuiteWebCompleta {
}
