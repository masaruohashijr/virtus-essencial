package crt2.dominio;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import crt2.dominio.analisequalitativa.SuiteAQT;
import crt2.dominio.analisequantitativa.SuiteNegocioAnaliseQuantitativaAQT;
import crt2.dominio.analisequantitativa.SuiteNegocioGerenciarQuadro;
import crt2.dominio.corec.SuiteNegocioCorec;
import crt2.dominio.metodologiaeparametros.SuiteMetodologia;
import crt2.dominio.paineldosupervisor.SuitePainelDoSupervisor;
import crt2.dominio.perfildeacesso.SuitePerfilDeAcesso;
import crt2.dominio.perfilderisco.SuitePerfilDeRisco;
import crt2.dominio.poscorec.SuiteNegocioPosCorec;

@RunWith(Suite.class)
@SuiteClasses({SuiteMetodologia.class, SuiteAQT.class, 
    SuitePainelDoSupervisor.class, SuitePerfilDeAcesso.class, SuitePerfilDeRisco.class, 
    SuiteNegocioAnaliseQuantitativaAQT.class, SuiteNegocioGerenciarQuadro.class,
    SuiteNegocioCorec.class, SuiteNegocioPosCorec.class})
public class SuiteCompleta {
}
