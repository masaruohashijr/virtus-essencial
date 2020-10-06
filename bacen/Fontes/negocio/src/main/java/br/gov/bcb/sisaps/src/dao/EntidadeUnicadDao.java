package br.gov.bcb.sisaps.src.dao;

import java.util.List;

import javax.persistence.NoResultException;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.gov.bcb.sisaps.dao.GenericDAOLocal;
import br.gov.bcb.sisaps.src.dominio.Consolidado;
import br.gov.bcb.sisaps.src.dominio.EntidadeUnicad;

@Repository
public class EntidadeUnicadDao extends GenericDAOLocal<EntidadeUnicad, Integer> {

    private static final String CNPJ_CONGLOMERADO = "cnpjConglomerado";
    private static final String UNCHECKED = "unchecked";

    public EntidadeUnicadDao() {
        super(EntidadeUnicad.class);
    }

    public EntidadeUnicad buscarEntidadeUnicadPorCnpj(Integer codigo) {
        StringBuilder hql = new StringBuilder();
        hql.append("select ent.pk, ent.nome, ent.nomeAbreviado, ent.cnpjConglomerado, con ");
        hql.append("from EntidadeUnicad as ent ");
        hql.append("left join ent.consolidado as con ");
        hql.append("where cast((case when ent.cnpjConglomerado = '' then '-1' ");
        hql.append("when(substring(ent.cnpjConglomerado, 1, 1) = 'C') ");
        hql.append("then substring(ent.cnpjConglomerado, 2, 7) ");
        hql.append("else ent.cnpjConglomerado end)as int) = " + codigo);
        Query query = getCurrentSession().createQuery(hql.toString());
        
        EntidadeUnicad ent = null;
        
        try {
            Object object = query.uniqueResult();
            if (object != null) {
                ent = new EntidadeUnicad();
                ent.setPk(((Integer) ((Object[]) object)[0]));
                ent.setNome(((String) ((Object[]) object)[1]));
                ent.setNomeAbreviado(((String) ((Object[]) object)[2]));
                ent.setCnpjConglomerado((String) ((Object[]) object)[3]);
                ent.setConsolidado(((Consolidado) ((Object[]) object)[4]));
            }
        } catch (NoResultException e) {
            System.out.println("Registro não encontrado.");
        }
        
        return ent;
    }

    @SuppressWarnings(UNCHECKED)
    public List<EntidadeUnicad> buscarEntidadesConsolidado(EntidadeUnicad entidadeUnicad) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq("consolidado", entidadeUnicad.getConsolidado()));
        criteria.add(Restrictions.ne(CNPJ_CONGLOMERADO, entidadeUnicad.getCnpjConglomerado()));
        return criteria.list();
    }

}
