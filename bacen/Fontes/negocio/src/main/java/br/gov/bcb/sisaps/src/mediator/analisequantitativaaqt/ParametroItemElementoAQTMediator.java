package br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.src.dao.analisequantitativaaqt.ParametroItemElementoAQTDAO;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroElementoAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroItemElementoAQT;

@Service
@Transactional(readOnly = true)
public class ParametroItemElementoAQTMediator {

    @Autowired
    private ParametroItemElementoAQTDAO parametroItemElementoAQTDAO;

    public static ParametroItemElementoAQTMediator get() {
        return SpringUtils.get().getBean(ParametroItemElementoAQTMediator.class);
    }

    public List<ParametroItemElementoAQT> buscarParamentos(ParametroElementoAQT parametroElementoAQT) {
        return parametroItemElementoAQTDAO.buscarItemElementoAQT(parametroElementoAQT);
    }

}
