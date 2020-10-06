package br.gov.bcb.sisaps.src.mediator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.src.dao.ParametroPerspectivaDAO;
import br.gov.bcb.sisaps.src.dominio.AjusteCorec;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Metodologia;
import br.gov.bcb.sisaps.src.dominio.ParametroPerspectiva;
import br.gov.bcb.sisaps.src.dominio.PerspectivaES;

@Service
@Transactional(readOnly = true)
public class ParametroPerspectivaMediator {

    @Autowired
    private ParametroPerspectivaDAO parametroPerspectivaDAO;

    public static ParametroPerspectivaMediator get() {
        return SpringUtils.get().getBean(ParametroPerspectivaMediator.class);
    }

    public ParametroPerspectiva buscarPorPK(Integer pk) {
        return parametroPerspectivaDAO.buscarPorPK(pk);
    }

    public ParametroPerspectiva buscarParametrosMetodologiaEDescricao(Integer pkMetodologia, String descricao) {
        return parametroPerspectivaDAO.buscarParametrosMetodologiaEDescricao(pkMetodologia, descricao);
    }

    public List<ParametroPerspectiva> buscarParametrosNotaCorec(Integer pkMetodologia) {
        return parametroPerspectivaDAO.buscarParametrosNotaCorec(pkMetodologia);
    }

    public List<ParametroPerspectiva> buscarParametrosPerspectivaCorec(Metodologia metodologia, Ciclo ciclo,
            AjusteCorec ajusteCorec) {

        List<ParametroPerspectiva> listaPerspectiva = buscarParametrosNotaCorec(metodologia.getPk());

        PerspectivaES perspectivaES = PerfilRiscoMediator.get().getPerspectivaESPerfilRisco(
                PerfilRiscoMediator.get().obterPerfilRiscoAtual(ciclo.getPk()));
        if (ajusteCorec == null || (ajusteCorec != null && ajusteCorec.getPerspectiva() == null)
                || !perspectivaES.getParametroPerspectiva().equals(ajusteCorec.getPerspectiva())) {
            listaPerspectiva.remove(perspectivaES.getParametroPerspectiva());
        }
        return listaPerspectiva;

    }

}
