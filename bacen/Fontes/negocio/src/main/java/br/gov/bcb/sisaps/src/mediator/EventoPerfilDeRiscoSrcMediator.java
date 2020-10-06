package br.gov.bcb.sisaps.src.mediator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.src.dao.EventoPerfilDeRiscoSrcDao;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.EventoConsolidado;
import br.gov.bcb.sisaps.src.dominio.EventoPerfilDeRiscoSrc;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.SubEventoPerfilDeRiscoSrc;
import br.gov.bcb.sisaps.util.enumeracoes.TipoSubEventoPerfilRiscoSRC;

@Service
@Transactional(readOnly = true)
public class EventoPerfilDeRiscoSrcMediator {

    @Autowired
    private EventoPerfilDeRiscoSrcDao eventoPerfilDeRiscoSrcDao;

    @Autowired
    private PerfilRiscoMediator perfilRiscoMediator;

    @Autowired
    private SubEventoPerfilDeRiscoSrcMediator subEventoPerfilDeRiscoSrcMediator;

    public static EventoPerfilDeRiscoSrcMediator get() {
        return SpringUtils.get().getBean(EventoPerfilDeRiscoSrcMediator.class);
    }

    public void incluirEventoESubEventoPerfilRisco(TipoSubEventoPerfilRiscoSRC tipoEvento, Ciclo ciclo,
            EventoConsolidado eventoConsolidado) {
        PerfilRisco perfilRiscoAtual = perfilRiscoMediator.obterPerfilRiscoAtual(ciclo.getPk());
        EventoPerfilDeRiscoSrc eventoPerfilRisco = new EventoPerfilDeRiscoSrc();
        eventoPerfilRisco.setPerfilRisco(perfilRiscoAtual);
        eventoPerfilRisco.setEventoConsolidado(eventoConsolidado);
        eventoPerfilDeRiscoSrcDao.save(eventoPerfilRisco);
        subEventoPerfilDeRiscoSrcMediator.incluirSubEventoPerfilRisco(tipoEvento, eventoPerfilRisco);
    }

    public void atualizarEventoESubEventoPerfilRisco(TipoSubEventoPerfilRiscoSRC tipoEvento, Ciclo ciclo,
            EventoConsolidado eventoConsolidado) {
        PerfilRisco perfilRiscoAtual = perfilRiscoMediator.obterPerfilRiscoAtual(ciclo.getPk());
        EventoPerfilDeRiscoSrc eventoPerfilDeRiscoSrc =
                eventoPerfilDeRiscoSrcDao.getEventoPerfilRiscoPorConsolidado(eventoConsolidado);
        SubEventoPerfilDeRiscoSrc subEvento =
                subEventoPerfilDeRiscoSrcMediator.buscarSubEventoPerfil(eventoPerfilDeRiscoSrc, tipoEvento);
        if (subEvento == null) {
            subEventoPerfilDeRiscoSrcMediator.incluirSubEventoPerfilRisco(tipoEvento, eventoPerfilDeRiscoSrc);
        } else {
            subEventoPerfilDeRiscoSrcMediator.atualizarSubEventoPerfilRisco(subEvento, eventoPerfilDeRiscoSrc,
                    tipoEvento);
        }
        eventoPerfilDeRiscoSrc.setPerfilRisco(perfilRiscoAtual);
        eventoPerfilDeRiscoSrcDao.update(eventoPerfilDeRiscoSrc);
    }
    
    
    public List<EventoPerfilDeRiscoSrc> buscarEventosPerfilDeRiscoSrc(String cnpj) {
        return eventoPerfilDeRiscoSrcDao.buscarEventosPerfilDeRiscoSrc(cnpj);
    }

    public void update(EventoPerfilDeRiscoSrc evcPerfilRisco) {
        eventoPerfilDeRiscoSrcDao.update(evcPerfilRisco);
    }
}
