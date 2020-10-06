package crt2.dominio.administrador.alterares;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import crt2.dominio.administrador.gestaoes.TestR001AdministradorGestaoES;

@RunWith(Suite.class)
@SuiteClasses({TestR003AdministradorAlterarES.class,
        TestR001AdministradorGestaoES.class})
public class SuiteTestAdministradorAlterar {

}
