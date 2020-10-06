package br.gov.bcb.sisaps.src.mediator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.src.dao.ParametroElementoDAO;
import br.gov.bcb.sisaps.src.dominio.ParametroElemento;
import br.gov.bcb.sisaps.src.dominio.ParametroGrupoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoGrupoEnum;
import br.gov.bcb.sisaps.src.vo.ConsultaParametroElementoVO;
import br.gov.bcb.sisaps.src.vo.ParametroElementoVO;

@Service
@Transactional(readOnly = true)
public class ParametroElementoMediator extends
        AbstractMediatorPaginado<ParametroElementoVO, Integer, ConsultaParametroElementoVO> {

    @Autowired
    private ParametroElementoDAO parametroElementoDAO;
    
    public static ParametroElementoMediator get() {
        return SpringUtils.get().getBean(ParametroElementoMediator.class);
    }

    @Override
    protected ParametroElementoDAO getDao() {
        return parametroElementoDAO;
    }

    public List<ParametroElemento> buscarElementosArc(ParametroGrupoRiscoControle grupoRiscoControle) {
        return parametroElementoDAO.buscarElementosArc(grupoRiscoControle);
    }
    
    public List<ParametroElemento> buscarParametrosElementoPorTipo(
            ParametroGrupoRiscoControle parametroGrupoRiscoControle, TipoGrupoEnum tipoGrupoEnum) {
        return parametroElementoDAO.buscarParametrosElementoPorTipo(parametroGrupoRiscoControle, tipoGrupoEnum);
    }

}
