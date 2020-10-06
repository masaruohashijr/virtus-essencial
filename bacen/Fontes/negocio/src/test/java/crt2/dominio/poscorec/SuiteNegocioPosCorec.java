package crt2.dominio.poscorec;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import crt2.dominio.poscorec.botaoencerrarciclo.SuiteBotaoEncerrarCiclo;
import crt2.dominio.poscorec.gestaoposcorec.SuiteGestaoPosCorec;
import crt2.dominio.poscorec.listarciclosposcorec.TestR001ListarCiclosPosCorec;

@RunWith(Suite.class)
@SuiteClasses({TestR001ListarCiclosPosCorec.class, SuiteGestaoPosCorec.class, SuiteBotaoEncerrarCiclo.class})
public class SuiteNegocioPosCorec {

}
