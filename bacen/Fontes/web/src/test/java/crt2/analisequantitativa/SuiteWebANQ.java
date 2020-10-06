package crt2.analisequantitativa;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import crt2.analisequantitativa.detalharquadroposicaofinanceira.TestR001DetalharQuadroPosicaoFinanceira;
import crt2.analisequantitativa.gerenciarquadroposicaofinanceira.TestR001GerenciarQuadroPosicaoFinanceira;
import crt2.analisequantitativa.gerenciarquadroposicaofinanceira.TestR003GerenciarQuadroPosicaoFinanceira;
import crt2.analisequantitativa.gerenciarquadroposicaofinanceira.TestR004GerenciarQuadroPosicaoFinanceira;

@RunWith(Suite.class)
@SuiteClasses({TestR001DetalharQuadroPosicaoFinanceira.class, TestR001GerenciarQuadroPosicaoFinanceira.class,
        TestR003GerenciarQuadroPosicaoFinanceira.class, TestR004GerenciarQuadroPosicaoFinanceira.class})
public class SuiteWebANQ {
}
