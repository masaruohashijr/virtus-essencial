package br.gov.bcb.sisaps.src.mediator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.src.dao.ParametroPrioridadeDAO;
import br.gov.bcb.sisaps.src.dominio.ParametroPrioridade;

@Service
@Transactional(readOnly = true)
public class ParametroPrioridadeMediator {

    @Autowired
    private ParametroPrioridadeDAO parametroPrioridadeDAO;
    
    public static ParametroPrioridadeMediator get() {
        return SpringUtils.get().getBean(ParametroPrioridadeMediator.class);
    }

    public List<ParametroPrioridade> buscarTodasPrioridades() {
        return parametroPrioridadeDAO.buscarTodasPrioridades();
    }

    public ParametroPrioridade buscarPrioridadeNome(String descricao) {
        return parametroPrioridadeDAO.buscarPrioridadeNome(descricao);
    }
    
    public boolean buscarParametro(Integer pk) {
        return parametroPrioridadeDAO.buscarParametro(pk);
    }
}
