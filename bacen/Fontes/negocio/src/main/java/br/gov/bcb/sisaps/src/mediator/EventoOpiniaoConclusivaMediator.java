package br.gov.bcb.sisaps.src.mediator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.src.dao.EventoOpiniaoConclusivaDao;
import br.gov.bcb.sisaps.src.dominio.EventoOpiniaoConclusiva;

@Service
@Transactional(readOnly = true)
public class EventoOpiniaoConclusivaMediator {
    
    @Autowired 
    EventoOpiniaoConclusivaDao eventoOpiniaoConsolidadoDao;
    
    public static EventoOpiniaoConclusivaMediator get() {
        return SpringUtils.get().getBean(EventoOpiniaoConclusivaMediator.class);
    }
    
    public List<EventoOpiniaoConclusiva> buscarEventosOpiniaoConclusiva(String cnpj) {
        return eventoOpiniaoConsolidadoDao.buscarEventosOpiniaoConclusiva(cnpj);
    }
    
    public void salvar(EventoOpiniaoConclusiva eventoOpiniaoConclusiva) {
        eventoOpiniaoConsolidadoDao.saveOrUpdate(eventoOpiniaoConclusiva);
    }
}
