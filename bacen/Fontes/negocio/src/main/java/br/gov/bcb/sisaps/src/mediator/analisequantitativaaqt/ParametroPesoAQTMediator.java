package br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;

@Service
@Transactional(readOnly = true)
public class ParametroPesoAQTMediator {

    public static ParametroPesoAQTMediator get() {
        return SpringUtils.get().getBean(ParametroPesoAQTMediator.class);
    }

}
