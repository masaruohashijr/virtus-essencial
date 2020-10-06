package crt2.dominio.administrador.detalharcomiterealizar;

import java.util.List;

import br.gov.bcb.sisaps.src.dominio.AgendaCorec;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.EmailCorec;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoEmailCorecEnum;
import br.gov.bcb.sisaps.src.mediator.AgendaCorecMediator;
import br.gov.bcb.sisaps.src.mediator.CargaParticipanteMediator;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.EmailCorecMediator;
import br.gov.bcb.sisaps.src.vo.ParticipanteComiteVO;
import crt2.ConfiguracaoTestesNegocio;

public class TestR002DetalharComiteRealizar extends ConfiguracaoTestesNegocio {
    private AgendaCorec agenda;

    public List<ParticipanteComiteVO> listar(Long cicloES) {
        Ciclo ciclo = CicloMediator.get().buscarCicloPorPK(cicloES.intValue());
        agenda = AgendaCorecMediator.get().buscarAgendaCorecPorCiclo(ciclo.getPk());
        if (agenda == null) {
            agenda = new AgendaCorec();
        }

        return CargaParticipanteMediator.get().buscarParticipantesPossiveisES(ciclo.getEntidadeSupervisionavel());
    }

    public String getNome(ParticipanteComiteVO resultado) {
        return resultado.getNome();
    }

    public String getEmailDisponibilidade(ParticipanteComiteVO resultado) {

        EmailCorec emailParticipante =
                EmailCorecMediator.get().buscarEmailParticipante(agenda.getPk(), resultado.getMatricula(),
                        TipoEmailCorecEnum.DISPONIBILIDADE);

        return emailParticipante == null ? "" : emailParticipante.getDataHoraFormatada();
    }

}
