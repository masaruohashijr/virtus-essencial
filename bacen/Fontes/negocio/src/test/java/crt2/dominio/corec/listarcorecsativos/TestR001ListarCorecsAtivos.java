package crt2.dominio.corec.listarcorecsativos;

import java.util.List;

import br.gov.bcb.sisaps.adaptadores.pessoa.BcPessoaAdapter;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.vo.CicloVO;
import crt2.ConfiguracaoTestesNegocio;

public class TestR001ListarCorecsAtivos extends ConfiguracaoTestesNegocio {

    public List<CicloVO> consultaPainelComite(String usuario) {
        logar(usuario, BcPessoaAdapter.get().buscarServidorPorLoginSemExcecao(usuario), true);
        return CicloMediator.get().consultarCicloCorec();
    }

    public String getID(CicloVO ciclo) {
        return ciclo.getPk().toString();
    }

    public String getES(CicloVO ciclo) {
        return ciclo.getEntidadeSupervisionavel().getNome();
    }

    public String getSupervisorTitular(CicloVO ciclo) {
        return ciclo.getNomeSupervisorCorec();
    }

    public String getEntradaCorec(CicloVO ciclo) {
        return ciclo.getDataInicioCorecFormatada();
    }

    public String getCorec(CicloVO ciclo) {
        return ciclo.getDataPrevisaoFormatada();
    }

}
