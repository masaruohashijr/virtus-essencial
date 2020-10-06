package br.gov.bcb.sisaps.src.mediator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.sisaps.src.dao.ParametroItemElementoDAO;
import br.gov.bcb.sisaps.src.dominio.ParametroElemento;
import br.gov.bcb.sisaps.src.dominio.ParametroItemElemento;

@Service
@Transactional(readOnly = true)
public class ParametroItemElementoMediator {

    @Autowired
    private ParametroItemElementoDAO parametroItemElementoDAO;

    @Transactional
    public void salvarItensAvaliar(List<ParametroItemElemento> itensAvaliar) {
        for (ParametroItemElemento itemAvaliar : itensAvaliar) {
            parametroItemElementoDAO.saveOrUpdate(itemAvaliar);
        }
    }

    @Transactional
    public void excluirItensAvaliar(ParametroElemento parametroElemento) {
        List<ParametroItemElemento> itensAvaliar =
                parametroItemElementoDAO.buscarItensAvaliarPorIdParametroElemento(parametroElemento);
        for (ParametroItemElemento itemAvaliar : itensAvaliar) {
            parametroItemElementoDAO.delete(itemAvaliar);
        }
    }

    public List<ParametroItemElemento> buscarItensAvaliarPorIdParametroElemento(ParametroElemento parametroElemento) {
        return parametroItemElementoDAO.buscarItensAvaliarPorIdParametroElemento(parametroElemento);
    }
}
