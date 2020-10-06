package br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.src.dao.analisequantitativaaqt.ParametroElementoAQTDAO;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroElementoAQT;

@Service
@Transactional(readOnly = true)
public class ParametroElementoAQTMediator {

    @Autowired
    private ParametroElementoAQTDAO parametroElementoAQTDAO;

    public static ParametroElementoAQTMediator get() {
        return SpringUtils.get().getBean(ParametroElementoAQTMediator.class);
    }

    public List<ParametroElementoAQT> buscarParElementoPorParAQT(ParametroAQT parametroAQT) {
        return parametroElementoAQTDAO.buscarParElementoPorParAQT(parametroAQT);
    }

}
