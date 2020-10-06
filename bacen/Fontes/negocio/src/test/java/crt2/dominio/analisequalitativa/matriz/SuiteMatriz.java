package crt2.dominio.analisequalitativa.matriz;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import crt2.dominio.analisequalitativa.matriz.test.TestAPSFW0205_Edicao_de_matriz_secao_blocos;
import crt2.dominio.analisequalitativa.matriz.test.TestE3_18LiberacaoVersionamentoMatriz;
import crt2.dominio.analisequalitativa.matriz.test.TestE3_1InclusaoLinhasColunas;
import crt2.dominio.analisequalitativa.matriz.test.TestE3_2AlteracaoExclusaoLinhasColunas;

@RunWith(Suite.class)
@SuiteClasses({TestE3_2AlteracaoExclusaoLinhasColunas.class, TestE3_1InclusaoLinhasColunas.class,
        TestE3_18LiberacaoVersionamentoMatriz.class, TestAPSFW0205_Edicao_de_matriz_secao_blocos.class})
public class SuiteMatriz {
}
