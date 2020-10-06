package crt2.dominio.analisequantitativa;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import crt2.dominio.analisequantitativa.gerenciarquadroposicaofinanceira.TestR002GerenciarQuadroPosicaoFinanceira;
import crt2.dominio.analisequantitativa.gerenciarquadroposicaofinanceira.TestR003GerenciarQuadroPosicaoFinanceira;

@RunWith(Suite.class)
@SuiteClasses({TestR002GerenciarQuadroPosicaoFinanceira.class,
    TestR003GerenciarQuadroPosicaoFinanceira.class})
public class SuiteNegocioGerenciarQuadro {
}
