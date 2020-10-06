package br.gov.bcb.sisaps.src.mediator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.src.dao.ParticipanteAgendaCorecDao;
import br.gov.bcb.sisaps.src.dominio.AgendaCorec;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.ParticipanteAgendaCorec;
import br.gov.bcb.sisaps.src.vo.ParticipanteComiteVO;
import br.gov.bcb.sisaps.util.geral.DataUtil;
import br.gov.bcb.utils.logging.BCLogFactory;
import br.gov.bcb.utils.logging.BCLogger;

@Service
@Transactional(readOnly = true)
public class ParticipanteAgendaCorecMediator {
    
    private static final BCLogger LOG = BCLogFactory.getLogger("ParticipanteAgendaCorecMediator");

    @Autowired
    private ParticipanteAgendaCorecDao participanteAgendaCorecDao;

    public static ParticipanteAgendaCorecMediator get() {
        return SpringUtils.get().getBean(ParticipanteAgendaCorecMediator.class);
    }

    public List<ParticipanteAgendaCorec> buscarParticipanteAgendaCorec(Integer pkAgenda) {
        return participanteAgendaCorecDao.buscarParticipanteAgendaCorec(pkAgenda);
    }

    @SuppressWarnings("unchecked")
    public List<ParticipanteComiteVO> buscarParticipantesEfetivos(Integer pkAgenda) {
        List<ParticipanteComiteVO> participantesEfetivos =
                participanteAgendaCorecDao.buscarParticipantesEfetivos(pkAgenda);
        List<BeanComparator> sortFields = new ArrayList<BeanComparator>();
        sortFields.add(new BeanComparator("nome"));
        Collections.sort(participantesEfetivos, new ComparatorChain(sortFields));
        LOG.info("SIZE LISTA PARTICIPANTES: " + participantesEfetivos.size());
        return participantesEfetivos;
    }

    public boolean existeParticipanteAgendaCorec(Integer pkCiclo) {
        return participanteAgendaCorecDao.existeParticipanteAgendaCorec(pkCiclo);
    }

    @Transactional
    public String salvarParticipantesEfetivos(Ciclo ciclo, AgendaCorec agenda,
            List<ParticipanteComiteVO> participantesEfetivosIncluidos, List<ParticipanteComiteVO> participantesExcluidos) {
        AgendaCorec novaAgenda = agenda;
        if (agenda == null) {
            novaAgenda = new AgendaCorec();
            novaAgenda.setCiclo(ciclo);
            AgendaCorecMediator.get().save(novaAgenda);
        }

        for (ParticipanteComiteVO participanteIncluido : participantesEfetivosIncluidos) {
            ParticipanteAgendaCorec participante = converterParaEntidade(novaAgenda, participanteIncluido);
            participanteAgendaCorecDao.save(participante);
        }

        for (ParticipanteComiteVO participanteExcluido : participantesExcluidos) {
            ParticipanteAgendaCorec participante = converterParaEntidade(novaAgenda, participanteExcluido);
            participanteAgendaCorecDao.delete(participante);
        }

        return "Participantes salvos com sucesso.";
    }

    private ParticipanteAgendaCorec converterParaEntidade(AgendaCorec agenda, ParticipanteComiteVO participanteVO) {
        ParticipanteAgendaCorec participante = null;
        if (participanteVO.getPkParticipanteAgendaCorec() == null) {
            participante = new ParticipanteAgendaCorec();
            participante.setAgenda(agenda);
            participante.setMatricula(participanteVO.getMatricula());
        } else {
            participante = participanteAgendaCorecDao.load(participanteVO.getPkParticipanteAgendaCorec());
        }
        return participante;
    }

    public List<ParticipanteAgendaCorec> consultaAtasPendente() {
        return participanteAgendaCorecDao.consultaAtasPendente();
    }

    public List<ParticipanteAgendaCorec> consultaAtasAssinadas() {
        return participanteAgendaCorecDao.consultaAtasAssinadas();
    }

    @Transactional
    public String assinarATA(ParticipanteAgendaCorec participante) {
        participante.setAssinatura(DataUtil.getDateTimeAtual());
        participanteAgendaCorecDao.saveOrUpdate(participante);
        return "Ata assinada com sucesso.";
    }

}
