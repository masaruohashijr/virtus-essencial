package crt2.dominio.administrador.detalharcomiterealizado;

import java.util.ArrayList;
import java.util.List;

import br.gov.bcb.sisaps.src.dominio.AgendaCorec;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.mediator.AgendaCorecMediator;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR001DetalharComiteRealizado extends ConfiguracaoTestesNegocio {

    public List<AgendaCorec> listar(Long cicloES) {
        Ciclo ciclo = CicloMediator.get().buscarCicloPorPK(cicloES.intValue());

        AgendaCorec agenda = AgendaCorecMediator.get().buscarAgendaCorecPorCiclo(ciclo.getPk());
        if (agenda == null) {
            agenda = new AgendaCorec();
            agenda.setCiclo(ciclo);
        }
        List<AgendaCorec> lista = new ArrayList<AgendaCorec>();
        lista.add(agenda);
        return lista;
    }

    public String getDataApresentacao(AgendaCorec resultado) {
        return resultado.getDataEnvioApresentacaoFormatada();
    }

    public String getDataDisponibilidade(AgendaCorec resultado) {
        return resultado.getDataEnvioDisponibilidadeFormatada();
    }

}
