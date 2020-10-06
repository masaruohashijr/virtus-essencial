package br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.src.dao.analisequantitativaaqt.ParametroAQTDAO;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.Metodologia;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroAQT;

@Service
@Transactional(readOnly = true)
public class ParametroAQTMediator {
    
    @Autowired
    private ParametroAQTDAO parametroAQTDAO;


    public static ParametroAQTMediator get() {
        return SpringUtils.get().getBean(ParametroAQTMediator.class);
    }

    public List<ParametroAQT> buscarParamentos(Metodologia metodologia) {
        return parametroAQTDAO.buscarParamentos(metodologia);
    }

    public boolean existeAQTAnalisadoGrupoRisco(ParametroAQT parametroAqt, Matriz matriz) {
        return parametroAQTDAO.existeAQTAnalisadoGrupoRisco(parametroAqt, matriz);
    }

    public boolean existeAQTAnalisado(ParametroAQT parametroAQT, Matriz matriz) {
        return false;
    }

    public ParametroAQT buscarParemetroAQT(AnaliseQuantitativaAQT analiseQuantitativaAQT) {
        return parametroAQTDAO.buscarParemetroAQT(analiseQuantitativaAQT);
    }


}
