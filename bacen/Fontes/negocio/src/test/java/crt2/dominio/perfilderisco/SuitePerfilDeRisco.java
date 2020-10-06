package crt2.dominio.perfilderisco;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import crt2.dominio.perfilderisco.test.TestAPSFW0202_Perfil_de_Risco_Dados_do_ciclo;
import crt2.dominio.perfilderisco.test.TestAPSFW0202_Perfil_de_risco_Filtro_ciclo_versao;
import crt2.dominio.perfilderisco.test.TestAPSFW0205_Edicao_matriz_transferir_arcs;

@RunWith(Suite.class)
@SuiteClasses({TestAPSFW0202_Perfil_de_Risco_Dados_do_ciclo.class, 
    TestAPSFW0202_Perfil_de_risco_Filtro_ciclo_versao.class, TestAPSFW0205_Edicao_matriz_transferir_arcs.class})
public class SuitePerfilDeRisco {

}
