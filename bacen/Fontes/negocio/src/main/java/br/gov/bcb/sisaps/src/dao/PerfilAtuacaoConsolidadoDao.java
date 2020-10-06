package br.gov.bcb.sisaps.src.dao;

import javax.persistence.NoResultException;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import br.gov.bcb.sisaps.dao.GenericDAOLocal;
import br.gov.bcb.sisaps.src.dominio.PerfilAtuacaoConsolidado;

@Repository
public class PerfilAtuacaoConsolidadoDao extends GenericDAOLocal<PerfilAtuacaoConsolidado, Integer> {

    private static final String WHERE_CON_PK = "where con.pk = ";
    private static final String ORDER_BY_PERFIL_ULTIMA_ATUALIZACAO_DESC = "order by perfil.ultimaAtualizacao desc";
    private static final String ORDER_BY_PERFIL_PK_DESC = "order by perfil.pk desc";
    private static final String LEFT_JOIN_PERFIL_CONSOLIDADO_AS_CON = "left join perfil.consolidado as con ";
    private static final String SELECT = "select perfil.pk from PerfilAtuacaoConsolidado as perfil ";
    private static final String REGISTRO_NAO_ENCONTRADO = "Registro não encontrado.";

    public PerfilAtuacaoConsolidadoDao() {
        super(PerfilAtuacaoConsolidado.class);
    }
    
    public PerfilAtuacaoConsolidado buscarPublicado(Integer pkConsolidado) {
        StringBuilder hql = new StringBuilder();
        hql.append(SELECT);
        hql.append(LEFT_JOIN_PERFIL_CONSOLIDADO_AS_CON);
        hql.append(WHERE_CON_PK + pkConsolidado + " and perfil.status = 1 and perfil.perfilPosterior is null ");
        hql.append(ORDER_BY_PERFIL_ULTIMA_ATUALIZACAO_DESC);
        Query query = getCurrentSession().createQuery(hql.toString());

        PerfilAtuacaoConsolidado perfilAtuacao = null;
        
        try {
            Object object = query.list();
            if (object != null && !query.list().isEmpty()) {
                Object obj = query.list().get(0);
                Integer pk = ((Integer) (obj));
                perfilAtuacao = load(pk);
            }
        } catch (NoResultException e) {
            System.out.println(REGISTRO_NAO_ENCONTRADO);
        }
        
        return perfilAtuacao;        
    }
    
    public PerfilAtuacaoConsolidado buscarPublicadoBatch(Integer pkConsolidado) {
        StringBuilder hql = new StringBuilder();
        hql.append(SELECT);
        hql.append(LEFT_JOIN_PERFIL_CONSOLIDADO_AS_CON);
        hql.append(WHERE_CON_PK + pkConsolidado + " and perfil.status = 1 ");
        hql.append(ORDER_BY_PERFIL_PK_DESC);
        Query query = getCurrentSession().createQuery(hql.toString());

        PerfilAtuacaoConsolidado perfilAtuacao = null;
        
        try {
            Object object = query.list();
            if (object != null && !query.list().isEmpty()) {
                Object obj = query.list().get(0);
                Integer pk = ((Integer) (obj));
                perfilAtuacao = load(pk);
            }
        } catch (NoResultException e) {
            System.out.println(REGISTRO_NAO_ENCONTRADO);
        }
        
        return perfilAtuacao;        
    }
    
    
    public PerfilAtuacaoConsolidado buscarEmEdicao(Integer pkConsolidado) {
        StringBuilder hql = new StringBuilder();
        hql.append(SELECT);
        hql.append(LEFT_JOIN_PERFIL_CONSOLIDADO_AS_CON);
        hql.append(WHERE_CON_PK + pkConsolidado + " and perfil.status = 2 ");
        hql.append(ORDER_BY_PERFIL_ULTIMA_ATUALIZACAO_DESC);
        Query query = getCurrentSession().createQuery(hql.toString());

        PerfilAtuacaoConsolidado perfilAtuacao = null;
        
        try {
            Object object = query.list();
            if (object != null && !query.list().isEmpty()) {
                Object obj = query.list().get(0);
                Integer pk = ((Integer) (obj));
                perfilAtuacao = load(pk);
            }
        } catch (NoResultException e) {
            System.out.println(REGISTRO_NAO_ENCONTRADO);
        }
        
        return perfilAtuacao;        
    }
    
    public PerfilAtuacaoConsolidado buscarEmHomologacao(Integer pkConsolidado) {
        StringBuilder hql = new StringBuilder();
        hql.append(SELECT);
        hql.append(LEFT_JOIN_PERFIL_CONSOLIDADO_AS_CON);
        hql.append(WHERE_CON_PK + pkConsolidado + " and perfil.status = 3 ");
        hql.append(ORDER_BY_PERFIL_ULTIMA_ATUALIZACAO_DESC);
        Query query = getCurrentSession().createQuery(hql.toString());

        PerfilAtuacaoConsolidado perfilAtuacao = null;
        
        try {
            Object object = query.list();
            if (object != null && !query.list().isEmpty()) {
                Object obj = query.list().get(0);
                Integer pk = ((Integer) (obj));
                perfilAtuacao = load(pk);
            }
        } catch (NoResultException e) {
            System.out.println(REGISTRO_NAO_ENCONTRADO);
        }
        
        return perfilAtuacao;        
    }

}
