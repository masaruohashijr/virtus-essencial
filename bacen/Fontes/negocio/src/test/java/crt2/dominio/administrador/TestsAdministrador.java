package crt2.dominio.administrador;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import crt2.dominio.administrador.alterarcomiterealizado.TestsAlterarComiteRealizado;
import crt2.dominio.administrador.alterares.SuiteTestAdministradorAlterar;
import crt2.dominio.administrador.detalharcomiterealizado.TestsDetalharComiterRealizado;
import crt2.dominio.administrador.detalharcomiterealizar.TestsDetalharComiterARealizar;
import crt2.dominio.administrador.gerenciarparticipantescomite.SuiteUploadParaticipantesComite;
import crt2.dominio.administrador.listarcomitesarealizar.TestR001ListarComitesARealizar;
import crt2.dominio.administrador.listarcomitesrealizados.TestR001ListarComitesRealizados;

@RunWith(Suite.class)
@SuiteClasses({TestsAlterarComiteRealizado.class, TestsDetalharComiterARealizar.class,
        SuiteTestAdministradorAlterar.class, TestsDetalharComiterRealizado.class, TestsDetalharComiterARealizar.class,
         TestR001ListarComitesARealizar.class,
        TestR001ListarComitesRealizados.class, SuiteUploadParaticipantesComite.class})
public class TestsAdministrador {

}
