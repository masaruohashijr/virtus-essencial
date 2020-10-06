package crt2.dominio.corec.participantescomite;

import java.util.ArrayList;
import java.util.List;

import br.gov.bcb.sisaps.src.dominio.AgendaCorec;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.mediator.AgendaCorecMediator;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.ParticipanteAgendaCorecMediator;
import br.gov.bcb.sisaps.src.vo.ParticipanteComiteVO;
import crt2.ConfiguracaoTestesNegocio;

public class TestR002ParticipantesComite extends ConfiguracaoTestesNegocio {
    
    private List<ParticipanteComiteVO> listaParticipantesIncluidos = new ArrayList<ParticipanteComiteVO>();
    private List<ParticipanteComiteVO> listaParticipantesExcluidos = new ArrayList<ParticipanteComiteVO>();
    private String mensagem;
    
    public void limparListaParticipantes() {
        listaParticipantesExcluidos.clear();
        listaParticipantesIncluidos.clear();
    }
    
    public void addParticipantesEfetivosExcluidos(Integer id, String matricula) {
        ParticipanteComiteVO participanteComiteVO = new ParticipanteComiteVO();
        participanteComiteVO.setPkParticipanteAgendaCorec(id);
        participanteComiteVO.setMatricula(matricula);
        listaParticipantesExcluidos.add(participanteComiteVO);
    }
    
    public void addParticipantesEfetivosIncluidos(Integer id, String matricula) {
        ParticipanteComiteVO participanteComiteVO = new ParticipanteComiteVO();
        participanteComiteVO.setPkCargaParticipante(id);
        participanteComiteVO.setMatricula(matricula);
        listaParticipantesIncluidos.add(participanteComiteVO);
    }
    
    public void salvarParticipantes(Integer idCiclo) {
        Ciclo ciclo = CicloMediator.get().buscarCicloPorPK(idCiclo);
        AgendaCorec agendaCorec = AgendaCorecMediator.get().buscarAgendaCorecPorCiclo(idCiclo);
        mensagem = ParticipanteAgendaCorecMediator.get().salvarParticipantesEfetivos(ciclo,
                agendaCorec, listaParticipantesIncluidos, listaParticipantesExcluidos);
    }
    
    public String getMensagem() {
        return mensagem;
    }

}
