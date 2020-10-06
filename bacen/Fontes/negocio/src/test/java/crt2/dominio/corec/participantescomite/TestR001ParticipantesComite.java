package crt2.dominio.corec.participantescomite;

import java.util.List;

import br.gov.bcb.sisaps.src.dominio.AgendaCorec;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.mediator.AgendaCorecMediator;
import br.gov.bcb.sisaps.src.mediator.CargaParticipanteMediator;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.ParticipanteAgendaCorecMediator;
import br.gov.bcb.sisaps.src.vo.ParticipanteComiteVO;
import crt2.ConfiguracaoTestesNegocio;

public class TestR001ParticipantesComite extends ConfiguracaoTestesNegocio {
    
    public List<ParticipanteComiteVO> buscarPossiveisParticipantes(Integer idCiclo) {
        Ciclo ciclo = CicloMediator.get().buscarCicloPorPK(idCiclo);
        AgendaCorec agendaCorec = AgendaCorecMediator.get().buscarAgendaCorecPorCiclo(idCiclo);
        return CargaParticipanteMediator.get().buscarParticipantesPossiveisESSemEfetivos(
                ciclo.getEntidadeSupervisionavel(), agendaCorec);
    }
    
    public List<ParticipanteComiteVO> buscarParticipantesEfetivos(Integer idCiclo) {
        AgendaCorec agendaCorec = AgendaCorecMediator.get().buscarAgendaCorecPorCiclo(idCiclo);
        return ParticipanteAgendaCorecMediator.get().buscarParticipantesEfetivos(
                agendaCorec == null ? null : agendaCorec.getPk());
    }
    
    public String getNome(ParticipanteComiteVO participanteComiteVO) {
        return participanteComiteVO.getNome();
    }

}
