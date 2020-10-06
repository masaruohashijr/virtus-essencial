package crt2.dominio.agenda.assinarata;

import java.util.List;

import br.gov.bcb.sisaps.src.dominio.ParticipanteAgendaCorec;
import br.gov.bcb.sisaps.src.mediator.ParticipanteAgendaCorecMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR001AssinarAta extends ConfiguracaoTestesNegocio {
    private String msg;

    public void assinarAta(Long idCiclo) {
        ParticipanteAgendaCorec participante = buscar(idCiclo.intValue());
        msg = ParticipanteAgendaCorecMediator.get().assinarATA(participante);
    }

    private ParticipanteAgendaCorec buscar(int ciclo) {
        List<ParticipanteAgendaCorec> lista = ParticipanteAgendaCorecMediator.get().consultaAtasPendente();
        for (ParticipanteAgendaCorec participanteAgendaCorec : lista) {
            if (participanteAgendaCorec.getAgenda().getCiclo().getPk().equals(ciclo)) {
                return participanteAgendaCorec;
            }
        }
        return null;
    }

    public String getMensagem() {
        return msg;
    }

}
