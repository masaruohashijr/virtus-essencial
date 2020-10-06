package br.gov.bcb.sisaps.src.mediator;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.src.dao.SupervisaoCicloDao;
import br.gov.bcb.sisaps.src.dominio.SupervisaoCiclo;

@Service
@Transactional(readOnly = true)
public class SupervisaoCicloMediator   {
    
    @Autowired 
    private SupervisaoCicloDao supervisaoCicloDao;
    
    public static SupervisaoCicloMediator get() {
        return SpringUtils.get().getBean(SupervisaoCicloMediator.class);
    }
    
    public SupervisaoCiclo buscarSupervisao(Date dataBase, String localizacao) {
        return supervisaoCicloDao.buscarSupervisao(dataBase, localizacao);
    }

}
