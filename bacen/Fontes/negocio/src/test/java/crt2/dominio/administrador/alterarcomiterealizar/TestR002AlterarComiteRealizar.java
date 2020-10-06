package crt2.dominio.administrador.alterarcomiterealizar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.joda.time.DateTime;

import br.gov.bcb.sisaps.src.dominio.AgendaCorec;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.ObservacaoAgendaCorec;
import br.gov.bcb.sisaps.src.mediator.AgendaCorecMediator;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.util.Constantes;
import crt2.ConfiguracaoTestesNegocio;

public class TestR002AlterarComiteRealizar extends ConfiguracaoTestesNegocio {
    private String mensagem;

    public void incluirAgenda(Long idCiclo, String local, String hora) {
        Ciclo ciclo = CicloMediator.get().buscarCicloPorPK(idCiclo.intValue());
        AgendaCorec agenda = new AgendaCorec();
        agenda.setLocal(local);
        agenda.setObservacoes(new ArrayList<ObservacaoAgendaCorec>());

        SimpleDateFormat sdf = new SimpleDateFormat(Constantes.FORMATO_HORA_AGENDA);
        Date date = null;
        try {
            date = sdf.parse(hora);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        agenda.setHoraCorec(new DateTime(date));
        agenda.setCiclo(ciclo);
        mensagem =
                AgendaCorecMediator.get().salvar(agenda, new ArrayList<ObservacaoAgendaCorec>(),
                        agenda.getCiclo().getDataPrevisaoCorec());
    }

    public String getMensagem() {
        return mensagem;
    }

    public void salvarAgenda(Long idAgenda, String local, String hora) {
        AgendaCorec agenda = AgendaCorecMediator.get().buscarAgendaCorec(idAgenda.intValue());
        agenda.setLocal(local);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
        Date date = null;
        try {
            date = sdf.parse(hora);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        agenda.setHoraCorec(new DateTime(date));
        mensagem =
                AgendaCorecMediator.get().salvar(agenda, new ArrayList<ObservacaoAgendaCorec>(),
                        agenda.getCiclo().getDataPrevisaoCorec());
    }

}
