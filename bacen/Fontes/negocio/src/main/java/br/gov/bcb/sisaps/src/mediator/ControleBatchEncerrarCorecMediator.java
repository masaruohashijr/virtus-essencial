package br.gov.bcb.sisaps.src.mediator;

import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.src.dao.ControleBatchEncerrarCorecDAO;
import br.gov.bcb.sisaps.src.dominio.ControleBatchEncerrarCorec;
import br.gov.bcb.sisaps.util.geral.DataUtil;

@Service
@Transactional(readOnly = true)
public class ControleBatchEncerrarCorecMediator {

    private static final int QUANTIDADE_ANOS = 1;
    
    @Autowired
    private ControleBatchEncerrarCorecDAO controleBatchEncerrarCorecDAO;

    public static ControleBatchEncerrarCorecMediator get() {
        return SpringUtils.get().getBean(ControleBatchEncerrarCorecMediator.class);
    }
    
    public List<ControleBatchEncerrarCorec> listarProcessos() {
        DateTime dataLimite = DataUtil.getDateTimeAtual();
        dataLimite = dataLimite.minusYears(QUANTIDADE_ANOS);
        return controleBatchEncerrarCorecDAO.listarProcessos(dataLimite);
    }
    
}
