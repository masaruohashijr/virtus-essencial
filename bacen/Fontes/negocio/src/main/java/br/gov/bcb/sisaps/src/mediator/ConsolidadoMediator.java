package br.gov.bcb.sisaps.src.mediator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.src.dao.ConsolidadoDao;
import br.gov.bcb.sisaps.src.dominio.Consolidado;

@Service
@Transactional(readOnly = true)
public class ConsolidadoMediator {

    @Autowired
    private ConsolidadoDao consolidadoDao;
    
    public static ConsolidadoMediator get() {
        return SpringUtils.get().getBean(ConsolidadoMediator.class);
    }
    
    
    public Consolidado buscarConsolidado(Integer codigo) {
        return consolidadoDao.buscarbuscarConsolidado(codigo); 
    }
}
