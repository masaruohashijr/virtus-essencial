package crt2.dominio.agenda.listarcomitesataassinada;

import java.util.List;

import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.src.dominio.ParticipanteAgendaCorec;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.ParticipanteAgendaCorecMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR001ListarComitesAtaAssinada extends ConfiguracaoTestesNegocio {

    public List<ParticipanteAgendaCorec> getComitesAtaAssinada() {
        return ParticipanteAgendaCorecMediator.get().consultaAtasAssinadas();
    }

    public String getInicioCorec(ParticipanteAgendaCorec participante) {
        return participante.getAgenda().getCiclo().getDataInicioFormatada().toString();
    }

    public String getCorecPrevisto(ParticipanteAgendaCorec participante) {
        return participante.getAgenda().getCiclo().getDataPrevisaoFormatada().toString();
    }

    public String getNomeES(ParticipanteAgendaCorec participante) {
        return participante.getAgenda().getCiclo().getEntidadeSupervisionavel().getNome();
    }

    public String getEquipe(ParticipanteAgendaCorec participante) {
        return participante.getAgenda().getCiclo().getEntidadeSupervisionavel().getLocalizacao();
    }

    public String getSupervisor(ParticipanteAgendaCorec participante) {

        ServidorVO servidor =
                CicloMediator.get().buscarChefeAtual(
                        participante.getAgenda().getCiclo().getEntidadeSupervisionavel().getLocalizacao());
        return servidor == null ? "" : servidor.getNome();
    }

    public String getPrioridade(ParticipanteAgendaCorec participante) {
        return participante.getAgenda().getCiclo().getEntidadeSupervisionavel().getPrioridade().getDescricao();
    }

}
