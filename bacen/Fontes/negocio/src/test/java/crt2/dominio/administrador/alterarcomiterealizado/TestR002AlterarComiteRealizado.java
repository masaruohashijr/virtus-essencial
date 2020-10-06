package crt2.dominio.administrador.alterarcomiterealizado;

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
import crt2.ConfiguracaoTestesNegocio;

public class TestR002AlterarComiteRealizado extends ConfiguracaoTestesNegocio {
    private static final String HH_MM = "hh:mm";
    private String mensagem;

    public void incluirAgenda(Long idCiclo, String local, String hora) {
        Ciclo ciclo = CicloMediator.get().buscarCicloPorPK(idCiclo.intValue());
        AgendaCorec agenda = new AgendaCorec();
        agenda.setLocal(local);

        SimpleDateFormat sdf = new SimpleDateFormat(HH_MM);
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
        SimpleDateFormat sdf = new SimpleDateFormat(HH_MM);
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
