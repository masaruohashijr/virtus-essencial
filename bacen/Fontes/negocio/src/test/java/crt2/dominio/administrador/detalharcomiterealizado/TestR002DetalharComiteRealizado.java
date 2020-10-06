package crt2.dominio.administrador.detalharcomiterealizado;

import java.util.ArrayList;
import java.util.List;

import br.gov.bcb.sisaps.src.dominio.AgendaCorec;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.mediator.AgendaCorecMediator;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.ParticipanteAgendaCorecMediator;
import br.gov.bcb.sisaps.src.vo.ParticipanteComiteVO;
import crt2.ConfiguracaoTestesNegocio;

public class TestR002DetalharComiteRealizado extends ConfiguracaoTestesNegocio {

    public List<ParticipanteComiteVO> listar(Long cicloES) {
        Ciclo ciclo = CicloMediator.get().buscarCicloPorPK(cicloES.intValue());

        AgendaCorec agenda = AgendaCorecMediator.get().buscarAgendaCorecPorCiclo(ciclo.getPk());
        if (agenda == null) {
            agenda = new AgendaCorec();
            agenda.setCiclo(ciclo);
        }
        List<AgendaCorec> lista = new ArrayList<AgendaCorec>();
        lista.add(agenda);
        return ParticipanteAgendaCorecMediator.get().buscarParticipantesEfetivos(agenda.getPk());
    }

    public String getNome(ParticipanteComiteVO resultado) {
        return resultado.getNome();
    }

}
