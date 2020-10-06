package br.gov.bcb.sisaps.src.mediator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.src.dao.ParametroSituacaoDAO;
import br.gov.bcb.sisaps.src.dominio.ParametroSituacao;

@Service
@Transactional(readOnly = true)
public class ParametroSituacaoMediator {

    @Autowired
    private ParametroSituacaoDAO parametroSituacao;

    public static ParametroSituacaoMediator get() {
        return SpringUtils.get().getBean(ParametroSituacaoMediator.class);
    }

    public ParametroSituacao buscarPorPK(Integer pk) {
        return parametroSituacao.buscarPorPK(pk);
    }

    public ParametroSituacao buscarParametrosMetodologiaEDescricao(Integer pkMetodologia, String descricao) {
        return parametroSituacao.buscarParametrosMetodologiaEDescricao(pkMetodologia, descricao);
    }

}
