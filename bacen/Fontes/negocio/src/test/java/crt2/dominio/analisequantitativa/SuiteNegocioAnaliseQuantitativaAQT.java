package crt2.dominio.analisequantitativa;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import crt2.dominio.analisequantitativa.alterarnotafinal.SuiteAlterarNotaFinalAEF;
import crt2.dominio.analisequantitativa.alterarpercentuais.SuiteAlterarPercentuais;
import crt2.dominio.analisequantitativa.analisarajustenotasanef.SuiteAnalisarAnefAjusteNota;
import crt2.dominio.analisequantitativa.analisarconcluiranef.SuiteConcluirAnalise;
import crt2.dominio.analisequantitativa.analisarelementoanef.SuiteAnalisarElementoANEF;
import crt2.dominio.analisequantitativa.concluirsintese.SuiteConcluirSintese;
import crt2.dominio.analisequantitativa.delegaraqt.SuiteDelegarAQT;
import crt2.dominio.analisequantitativa.designaraqt.SuiteDesignarAQT;
import crt2.dominio.analisequantitativa.detalharanaliseeconomicofinanceira.SuiteDetalharAnaliseEconomicaFinanceira;
import crt2.dominio.analisequantitativa.detalharanaliseeconomicofinanceira.TestR001DetalharAnaliseEconomicoFinanceira;
import crt2.dominio.analisequantitativa.detalharanaliseeconomicofinanceira.TestR002DetalharAnaliseEconomicoFinanceira;
import crt2.dominio.analisequantitativa.detalharcabecalhoanexoaqt.SuiteDetalharCabecalhoAnexoAQT;
import crt2.dominio.analisequantitativa.detalhargestaoanaliseeconomicofinanceira.SuiteTesteDetalharGestaoAnaliseEnconomicaFinanceira;
import crt2.dominio.analisequantitativa.editarajustenotasanef.SuiteEdicaoAnefAjusteNota;
import crt2.dominio.analisequantitativa.editaranexosanef.SuiteEditarAnexosAnef;
import crt2.dominio.analisequantitativa.editarconcluiranef.SuiteEditarConcluirANEF;
import crt2.dominio.analisequantitativa.editarelementoitemanef.SuiteEditarElementoItemANEF;
import crt2.dominio.analisequantitativa.excluirdesignacaoaqt.SuiteExcluirDesignarAQT;
import crt2.dominio.analisequantitativa.exibiracoesanef.TestR001AcoesANEF;
import crt2.dominio.analisequantitativa.listaraqtanalise.TestR001ListarAQTAnaliseSupervisor;
import crt2.dominio.analisequantitativa.listaraqtdelegado.TestR001ListarAQTDelegadoInspetor;
import crt2.dominio.analisequantitativa.listaraqtdesignado.TestR001ListarAQTDesignadoInspetor;
import crt2.dominio.analisequantitativa.listaraqtsintese.TestR001ListarAQTRevisaoSupervisor;
import crt2.dominio.analisequantitativa.retomaranaliseaqt.SuiteRetomarAnef;
import crt2.dominio.analisequantitativa.salvarsintese.TestR001SalvarSintese;
import crt2.dominio.corec.SuiteNegocioCorec;
import crt2.dominio.corec.bloquearedicaoinspetorsupervisor.SuiteBloquearEdicaoInspetorSupervisor;
import crt2.dominio.corec.verificacaoregrasiniciarcorec.SuiteVerificacaoRegrasIniciarCorec;
import crt2.dominio.paineldosupervisor.test.TestAPSFW0201_Painel_do_supervisor;

@RunWith(Suite.class)
@SuiteClasses({TestR001ListarAQTRevisaoSupervisor.class, TestR001ListarAQTAnaliseSupervisor.class,
        TestR001ListarAQTDesignadoInspetor.class, TestR001ListarAQTDelegadoInspetor.class,
        TestAPSFW0201_Painel_do_supervisor.class, TestR001DetalharAnaliseEconomicoFinanceira.class,
        TestR002DetalharAnaliseEconomicoFinanceira.class, TestR001SalvarSintese.class, SuiteDelegarAQT.class,
        SuiteDesignarAQT.class, SuiteExcluirDesignarAQT.class, SuiteRetomarAnef.class, TestR001AcoesANEF.class,
        SuiteTesteDetalharGestaoAnaliseEnconomicaFinanceira.class, SuiteAlterarPercentuais.class,
        SuiteNegocioCorec.class, SuiteConcluirSintese.class, SuiteBloquearEdicaoInspetorSupervisor.class,
        SuiteDetalharCabecalhoAnexoAQT.class, SuiteEditarElementoItemANEF.class, SuiteEditarConcluirANEF.class,
        SuiteAnalisarElementoANEF.class, SuiteEdicaoAnefAjusteNota.class, SuiteAnalisarAnefAjusteNota.class,
        SuiteConcluirAnalise.class, SuiteEditarAnexosAnef.class, SuiteVerificacaoRegrasIniciarCorec.class,
        SuiteDetalharAnaliseEconomicaFinanceira.class, SuiteAlterarNotaFinalAEF.class})
public class SuiteNegocioAnaliseQuantitativaAQT {

}
