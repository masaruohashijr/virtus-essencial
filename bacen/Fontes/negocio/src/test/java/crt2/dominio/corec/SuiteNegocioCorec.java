package crt2.dominio.corec;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import crt2.dominio.corec.ajustegraupreocupacaocomite.SuiteAjustarGrauPreocupacao;
import crt2.dominio.corec.ajustenotaanalisequalitativacomite.SuiteAjustarNotaQualitativa;
import crt2.dominio.corec.ajustenotaanalisequantitativacomite.SuiteAjustarNotaQuantitativa;
import crt2.dominio.corec.ajustenotasanefscomite.SuiteAjustarNotaAnefs;
import crt2.dominio.corec.ajustenotasarcscomite.SuiteAjustarNotaArcs;
import crt2.dominio.corec.ajusteperspectivacomite.SuiteAjustarPerspectiva;
import crt2.dominio.corec.alterarstatusciclo.TestR001AlterarStatusCiclo;
import crt2.dominio.corec.botaoiniciarcorec.SuiteIniciarCorec;
import crt2.dominio.corec.listarcorecsativos.TestR001ListarCorecsAtivos;
import crt2.dominio.corec.participantescomite.SuiteParticipantesComite;

@RunWith(Suite.class)
@SuiteClasses({TestR001ListarCorecsAtivos.class, SuiteIniciarCorec.class, TestR001AlterarStatusCiclo.class,
        SuiteAjustarNotaArcs.class, SuiteAjustarNotaAnefs.class, SuiteAjustarNotaQualitativa.class,
        SuiteAjustarNotaQuantitativa.class, SuiteAjustarGrauPreocupacao.class, SuiteAjustarPerspectiva.class,
        SuiteParticipantesComite.class})
public class SuiteNegocioCorec {

}
