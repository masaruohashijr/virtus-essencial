package crt2.analisequalitativa;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import crt2.analisequalitativa.arc.SuiteWebARC;
import crt2.analisequalitativa.matriz.SuiteWebMatriz;

@RunWith(Suite.class)
@SuiteClasses({SuiteWebARC.class, SuiteWebMatriz.class})
public class SuiteWebAQT {
}
