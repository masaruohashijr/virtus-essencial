package br.gov.bcb.sisaps.src.mediator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.src.dao.ParametroTendenciaDAO;
import br.gov.bcb.sisaps.src.dominio.ParametroTendencia;

@Service
@Transactional(readOnly = true)
public class ParametroTendenciaMediator {

    @Autowired
    private ParametroTendenciaDAO parametroTendenciaDAO;
    
    public static ParametroTendenciaMediator get() {
        return SpringUtils.get().getBean(ParametroTendenciaMediator.class);
    }

    public ParametroTendencia load(Integer pk) {
        return parametroTendenciaDAO.load(pk);
    }
    
    public List<ParametroTendencia> buscarParametros(Integer pkMetodologia) {
        return parametroTendenciaDAO.buscarParametros(pkMetodologia);
    }
}
