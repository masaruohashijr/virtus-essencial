package crt2.dominio.analisequalitativa.arc;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import crt2.dominio.analisequalitativa.analisarconcluirarc.TestR001AnalisarConcluirARC;
import crt2.dominio.analisequalitativa.arc.test.TestAPSFW0206_Gestao_de_ARCs;
import crt2.dominio.analisequalitativa.arc.test.TestE3_11DesignacaoARC;
import crt2.dominio.analisequalitativa.arc.test.TestE3_21_Consulta_ARC_pendente_de_analise_perfil_supervisor;
import crt2.dominio.analisequalitativa.arc.test.TestE3_22ConsultaARCsUsuariosDesignados;
import crt2.dominio.analisequalitativa.arc.test.TestE3_31_Delegacao_redesignacao_ARC;
import crt2.dominio.analisequalitativa.arc.test.TestE3_32_Analise_ARC;
import crt2.dominio.analisequalitativa.arc.test.TestE3_7_Edicao_de_ARCs_perfil_inspetor;

@RunWith(Suite.class)
@SuiteClasses({TestAPSFW0206_Gestao_de_ARCs.class,
        TestE3_11DesignacaoARC.class,
        TestE3_21_Consulta_ARC_pendente_de_analise_perfil_supervisor.class,
        TestE3_22ConsultaARCsUsuariosDesignados.class,
        TestE3_31_Delegacao_redesignacao_ARC.class,
        TestE3_32_Analise_ARC.class,
        TestE3_7_Edicao_de_ARCs_perfil_inspetor.class,
        TestR001AnalisarConcluirARC.class})
public class SuiteARC {
}
