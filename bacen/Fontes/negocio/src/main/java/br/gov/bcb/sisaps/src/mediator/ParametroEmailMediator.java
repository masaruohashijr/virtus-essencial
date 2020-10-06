package br.gov.bcb.sisaps.src.mediator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.src.dao.ParametroEmailDAO;
import br.gov.bcb.sisaps.src.dominio.ParametroEmail;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoEmailCorecEnum;
import br.gov.bcb.sisaps.src.validacao.RegraSalvarParametroEmailValidacao;

@Service
@Transactional(readOnly = true)
public class ParametroEmailMediator {

    @Autowired
    private ParametroEmailDAO parametroEmailDAO;

    public static ParametroEmailMediator get() {
        return SpringUtils.get().getBean(ParametroEmailMediator.class);
    }

    public ParametroEmail buscarPorPK(Integer pk) {
        return parametroEmailDAO.buscarPorPK(pk);
    }

    public ParametroEmail buscarPorTipo(TipoEmailCorecEnum tipo) {
        return parametroEmailDAO.buscarPorTipo(tipo);
    }
    
    @Transactional
    public String salvar(ParametroEmail parametroEmail) {
        new RegraSalvarParametroEmailValidacao(parametroEmail).validar();
        parametroEmailDAO.saveOrUpdate(parametroEmail);
        return "Parâmetro salvo com sucesso.";
    }
}
