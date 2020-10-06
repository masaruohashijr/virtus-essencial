package crt2.analisequantitativa;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import crt2.analisequantitativa.consultaranef.TestR001ConsultarANEF;
import crt2.analisequantitativa.detalharnotasanef.TestR001NotasANEF;
import crt2.analisequantitativa.detalharresumoinformacoesaqt.SuiteResumoInformacoesAQT;
import crt2.analisequantitativa.listaraqtanalise.TestR001ListarAQTAnaliseSupervisor;
import crt2.analisequantitativa.listaraqtdelegado.TestR001ListarAQTDelegadoInspetor;
import crt2.analisequantitativa.listaraqtdesignado.TestR001ListarAQTDesignadoInspetor;
import crt2.analisequantitativa.listaraqtsintese.TestR001ListarAQTRevisaoSupervisor;
import crt2.analisequantitativa.listaraqtsintese.TestR002ListarAQTRevisaoSupervisor;
import crt2.analisequantitativa.navegaraqtconsulta.TestR001NavegarAQTConsulta;
import crt2.analisequantitativa.navegaraqtconsulta.TestR002NavegarAQTConsulta;
import crt2.analisequantitativa.navegaraqtinspetor.TestR001NavegarAQTInspetor;
import crt2.analisequantitativa.navegaraqtsupervisor.SuiteNavegarSupervisorAQT;
import crt2.dominio.analisequantitativa.detalharanaliseeconomicofinanceira.TestR001DetalharAnaliseEconomicoFinanceira;

@RunWith(Suite.class)
@SuiteClasses({SuiteNavegarSupervisorAQT.class, TestR001ListarAQTRevisaoSupervisor.class,
        TestR002ListarAQTRevisaoSupervisor.class, TestR001ListarAQTDesignadoInspetor.class,
        TestR001ListarAQTDelegadoInspetor.class, TestR001ListarAQTAnaliseSupervisor.class,
        TestR001DetalharAnaliseEconomicoFinanceira.class, TestR001NavegarAQTConsulta.class,
        TestR002NavegarAQTConsulta.class, TestR001NavegarAQTInspetor.class, SuiteResumoInformacoesAQT.class,
        TestR001NotasANEF.class, TestR001ConsultarANEF.class})
public class SuiteWebAnaliseQuantitivaAQT {
}
