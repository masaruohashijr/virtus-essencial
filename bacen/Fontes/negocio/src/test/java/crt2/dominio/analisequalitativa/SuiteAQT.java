package crt2.dominio.analisequalitativa;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import crt2.dominio.analisequalitativa.arc.SuiteARC;
import crt2.dominio.analisequalitativa.matriz.SuiteMatriz;

@RunWith(Suite.class)
@SuiteClasses({SuiteARC.class, SuiteMatriz.class})
public class SuiteAQT {
}
