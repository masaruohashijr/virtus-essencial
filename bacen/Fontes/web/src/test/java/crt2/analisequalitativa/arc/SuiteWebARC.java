package crt2.analisequalitativa.arc;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({TestAPSFW0206_Gestao_de_ARCs.class, TestAPSFW0402_Consulta_de_ARCs.class, 
        TestE3_11_Designacao_ARC.class, TestE3_31_Delegacao_redesignacao_ARC.class, 
        TestE3_32_Analise_ARC.class, TestE3_7_Edicao_de_ARCs_perfil_inspetor.class})
public class SuiteWebARC {
}
