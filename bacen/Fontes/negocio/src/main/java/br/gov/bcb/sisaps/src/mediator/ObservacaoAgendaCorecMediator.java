package br.gov.bcb.sisaps.src.mediator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.src.dao.ObservacaoAgendaCorecDao;
import br.gov.bcb.sisaps.src.dominio.ObservacaoAgendaCorec;

@Service
@Transactional(readOnly = true)
public class ObservacaoAgendaCorecMediator {

    @Autowired
    private ObservacaoAgendaCorecDao observacaoAgendaCorecDao;

    public static ObservacaoAgendaCorecMediator get() {
        return SpringUtils.get().getBean(ObservacaoAgendaCorecMediator.class);
    }

    public ObservacaoAgendaCorec buscarObservacao(Integer pk) {
        return observacaoAgendaCorecDao.buscarObservacao(pk);
    }

    public List<ObservacaoAgendaCorec> buscarObservacaoAgenda(Integer pkAgenda) {
        return observacaoAgendaCorecDao.buscarObservacaoAgenda(pkAgenda);
    }

    @Transactional
    public void salvarLista(List<ObservacaoAgendaCorec> observacoes) {
        for (ObservacaoAgendaCorec observacaoAgendaCorec : observacoes) {
            if (observacaoAgendaCorec.getPk() == null) {
                observacaoAgendaCorecDao.save(observacaoAgendaCorec);
            } else {
                observacaoAgendaCorecDao.update(observacaoAgendaCorec);
            }
            observacaoAgendaCorecDao.flush();
        }
    }

    @Transactional
    public void excluir(List<ObservacaoAgendaCorec> observacoes) {
        for (ObservacaoAgendaCorec observacaoAgendaCorec : observacoes) {
            observacaoAgendaCorecDao.delete(observacaoAgendaCorec);
            observacaoAgendaCorecDao.flush();
        }

    }

    public void evict(List<ObservacaoAgendaCorec> observacoes) {
        if (observacoes != null && !observacoes.isEmpty()) {
            for (ObservacaoAgendaCorec observacaoAgendaCorec : observacoes) {
                observacaoAgendaCorec.setAlterarDataUltimaAtualizacao(false);
                observacaoAgendaCorecDao.evict(observacaoAgendaCorec);
            }
        }

    }

}
