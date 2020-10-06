package br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;

@Service
@Transactional(readOnly = true)
public class AnexoJustificativaItemAQTMediator {

    public static AnexoJustificativaItemAQTMediator get() {
        return SpringUtils.get().getBean(AnexoJustificativaItemAQTMediator.class);
    }

}
