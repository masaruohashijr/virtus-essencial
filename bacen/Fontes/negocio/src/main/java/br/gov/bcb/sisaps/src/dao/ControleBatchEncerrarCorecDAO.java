package br.gov.bcb.sisaps.src.dao;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import br.gov.bcb.sisaps.dao.GenericDAOLocal;
import br.gov.bcb.sisaps.src.dominio.ControleBatchEncerrarCorec;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoExecucaoEnum;

@Repository
public class ControleBatchEncerrarCorecDAO extends GenericDAOLocal<ControleBatchEncerrarCorec, Integer> {

    private static final String ULTIMA_ATUALIZACAO = "ultimaAtualizacao";
    private static final String CICLO = "ciclo";

    public ControleBatchEncerrarCorecDAO() {
        super(ControleBatchEncerrarCorec.class);
    }
    
    public boolean existeBatchAgendadoOuEmAndamento(Integer idCiclo) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.createAlias(CICLO, CICLO);
        criteria.add(Restrictions.eq("ciclo.pk", idCiclo));
        criteria.add(Restrictions.in("estadoExecucao", 
                Arrays.asList(EstadoExecucaoEnum.AGENDADO, EstadoExecucaoEnum.EM_ANDAMENTO)));
        return CollectionUtils.isNotEmpty(criteria.list());
    }

    public List<ControleBatchEncerrarCorec> listarProcessos(DateTime dataLimite) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.gt(ULTIMA_ATUALIZACAO, dataLimite));
        criteria.addOrder(Order.desc(ULTIMA_ATUALIZACAO));
        return cast(criteria.list());
    }
    
}
