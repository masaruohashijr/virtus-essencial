package br.gov.bcb.sisaps.src.mediator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.src.dao.ParametroAtaCorecDAO;
import br.gov.bcb.sisaps.src.dominio.ParametroAtaCorec;

@Service
@Transactional(readOnly = true)
public class ParametroAtaCorecMediator {

    @Autowired
    private ParametroAtaCorecDAO parametroAtaCorecDAO;

    public static ParametroAtaCorecMediator get() {
        return SpringUtils.get().getBean(ParametroAtaCorecMediator.class);
    }

    public ParametroAtaCorec buscarPorPK(Integer pk) {
        return parametroAtaCorecDAO.buscarPorPK(pk);
    }

    
    public ParametroAtaCorec buscar() {
        return parametroAtaCorecDAO.buscar();
    }


}
