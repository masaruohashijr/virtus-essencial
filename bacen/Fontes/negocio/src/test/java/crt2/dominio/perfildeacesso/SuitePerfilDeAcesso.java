package crt2.dominio.perfildeacesso;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import crt2.dominio.perfildeacesso.simularlocalizacaoadministrador.SuiteSimularLocalizacaoAdministrador;
import crt2.dominio.perfildeacesso.test.TestAPSFW0502_Gestao_de_perfil_de_acesso;

@RunWith(Suite.class)
@SuiteClasses({TestAPSFW0502_Gestao_de_perfil_de_acesso.class, SuiteSimularLocalizacaoAdministrador.class})
public class SuitePerfilDeAcesso {

}
