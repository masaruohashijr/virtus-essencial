package br.gov.bcb.sisaps.src.mediator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.src.dao.SubEventoPerfilDeRiscoSrcDao;
import br.gov.bcb.sisaps.src.dominio.EventoPerfilDeRiscoSrc;
import br.gov.bcb.sisaps.src.dominio.SubEventoPerfilDeRiscoSrc;
import br.gov.bcb.sisaps.util.enumeracoes.TipoSubEventoPerfilRiscoSRC;

@Service
@Transactional(readOnly = true)
public class SubEventoPerfilDeRiscoSrcMediator {
    
    @Autowired 
    private SubEventoPerfilDeRiscoSrcDao subEventoPerfilDeRiscoSrcDao;

    public static SubEventoPerfilDeRiscoSrcMediator get() {
        return SpringUtils.get().getBean(SubEventoPerfilDeRiscoSrcMediator.class);
    }
    
    public void incluirSubEventoPerfilRisco(TipoSubEventoPerfilRiscoSRC tipo,
            EventoPerfilDeRiscoSrc eventoPerfilDeRiscoSrc) {
        SubEventoPerfilDeRiscoSrc subEventoPerfilRisco = new SubEventoPerfilDeRiscoSrc();
        subEventoPerfilRisco.setEventoPerfilDeRiscoSrc(eventoPerfilDeRiscoSrc);
        subEventoPerfilRisco.setTipo(tipo);
        subEventoPerfilDeRiscoSrcDao.save(subEventoPerfilRisco);
    }

    public void atualizarSubEventoPerfilRisco(SubEventoPerfilDeRiscoSrc subEventoPerfilRisco,
            EventoPerfilDeRiscoSrc evento, TipoSubEventoPerfilRiscoSRC tipo) {
        subEventoPerfilRisco.setEventoPerfilDeRiscoSrc(evento);
        subEventoPerfilRisco.setTipo(tipo);
        subEventoPerfilDeRiscoSrcDao.update(subEventoPerfilRisco);
    }

    public SubEventoPerfilDeRiscoSrc buscarSubEventoPerfil(EventoPerfilDeRiscoSrc eventoPerfil,
            TipoSubEventoPerfilRiscoSRC tipo) {
        return subEventoPerfilDeRiscoSrcDao.buscarSubEventoPerfil(eventoPerfil, tipo);
    }

}
