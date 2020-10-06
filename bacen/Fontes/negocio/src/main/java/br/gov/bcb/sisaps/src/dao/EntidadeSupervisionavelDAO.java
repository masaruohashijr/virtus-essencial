package br.gov.bcb.sisaps.src.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.NoResultException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.springframework.stereotype.Repository;

import br.gov.bcb.app.stuff.seguranca.UsuarioCorrente;
import br.gov.bcb.sisaps.dao.GenericDAOParaListagens;
import br.gov.bcb.sisaps.seguranca.UsuarioAplicacao;
import br.gov.bcb.sisaps.src.dominio.EntidadeSupervisionavel;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoARCEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoCicloEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.vo.ConsultaCicloVO;
import br.gov.bcb.sisaps.src.vo.ConsultaEntidadeSupervisionavelVO;
import br.gov.bcb.sisaps.src.vo.EntidadeSupervisionavelVO;
import br.gov.bcb.sisaps.src.vo.SinteseRiscoRevisaoVO;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;

@Repository
public class EntidadeSupervisionavelDAO
        extends
        GenericDAOParaListagens<EntidadeSupervisionavel, Integer, EntidadeSupervisionavelVO, ConsultaEntidadeSupervisionavelVO> {
    private static final String UNCHECKED = "unchecked";
    private static final String PROP_VERSAO_PERFIL_RISCO_PK = "versaoPerfilRisco.pk";
    private static final String VERSAO_PERFIL_RISCO = "versaoPerfilRisco";
    private static final String PRIORIDADE = "prioridade";
    private static final String LOCALIZACAO = "localizacao";
    private static final String NOME = "nome";
    private static final String PROP_PK = "pk";
    private static final String PERTENCE_SRC = "pertenceSrc";
    private static final String ESTADO_CICLO = "estadoCiclo";
    private static final String CNPJ = "conglomeradoOuCnpj";
    private static final long serialVersionUID = 1L;

    public EntidadeSupervisionavelDAO() {
        super(EntidadeSupervisionavel.class, EntidadeSupervisionavelVO.class);
    }

    public EntidadeSupervisionavel buscarEntidadeSupervisionavelPorPK(Integer id) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(PROP_PK, id));
        return (EntidadeSupervisionavel) criteria.uniqueResult();
    }

    public List<EntidadeSupervisionavel> buscarPertenceSrc() {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        adicionarFiltroPertenceAoSrc(criteria, SimNaoEnum.SIM);
        return cast(criteria.list());
    }

    @Override
    protected void montarCriterios(Criteria criteria, ConsultaEntidadeSupervisionavelVO consulta) {
        adicionarFiltroPertenceAoSrc(criteria, SimNaoEnum.SIM);
        addFiltrosConsultaEntidadeSupervisionavelVO(criteria, consulta);
        // Atributos que serão retornados na consulta
        addColunasRetornadas(criteria);
    }

    private void addColunasRetornadas(Criteria criteria) {
        criteria.setProjection(Projections.projectionList().add(Projections.property(PROP_PK), PROP_PK)
                .add(Projections.property(NOME), NOME).add(Projections.property(LOCALIZACAO), LOCALIZACAO)
                .add(Projections.property(PRIORIDADE), PRIORIDADE)
                .add(Projections.property(PROP_VERSAO_PERFIL_RISCO_PK), "pkVersaoPerfilRisco"));
    }

    private void addFiltrosConsultaEntidadeSupervisionavelVO(Criteria criteria,
            ConsultaEntidadeSupervisionavelVO consulta) {
        adicionarFiltroVersaoPerfilDeRisco(consulta, criteria);
        adicionarFiltroNome(consulta, criteria);
        adicionarFiltroEquipe(consulta, criteria);
        adicionarFiltroPrioridade(consulta, criteria);
        adicionarFiltroEstadoCiclo(consulta, criteria);
        adicionarFiltroCnpj(consulta, criteria);
    }

    @SuppressWarnings("unchecked")
    public List<EntidadeSupervisionavelVO> consultarEntidadesSemCicloPainelConsulta(
            ConsultaEntidadeSupervisionavelVO consulta) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        addFiltrosConsultaEntidadeSupervisionavelVO(criteria, consulta);
        adicionarFiltroPertenceAoSrc(criteria, SimNaoEnum.SIM);
        criteria.add(Restrictions.isNull(VERSAO_PERFIL_RISCO));
        addColunasRetornadas(criteria);
        criteria.setResultTransformer(new AliasToBeanResultTransformer(EntidadeSupervisionavelVO.class));
        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    public List<EntidadeSupervisionavelVO> consultarEntidadesComCicloEmAndamentoCorecPainelConsulta(
            ConsultaEntidadeSupervisionavelVO consulta) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        consulta.setEstadosCiclo(Arrays.asList(EstadoCicloEnum.EM_ANDAMENTO, EstadoCicloEnum.COREC));
        addFiltrosConsultaEntidadeSupervisionavelVO(criteria, consulta);
        consulta.setEstadosCiclo(null);
        adicionarFiltroPertenceAoSrc(criteria, SimNaoEnum.SIM);
        addColunasRetornadas(criteria);
        criteria.setResultTransformer(new AliasToBeanResultTransformer(EntidadeSupervisionavelVO.class));
        return criteria.list();
    }

    private void adicionarFiltroEstadoCiclo(ConsultaEntidadeSupervisionavelVO consulta, Criteria criteria) {
        if (CollectionUtils.isNotEmpty(consulta.getEstadosCiclo())) {
            Criteria criteriaCiclos = criteria.createCriteria("ciclos");
            criteriaCiclos.createAlias(ESTADO_CICLO, ESTADO_CICLO);
            criteriaCiclos.add(Restrictions.in("estadoCiclo.estado", consulta.getEstadosCiclo()));
        }
    }

    private void adicionarFiltroCnpj(ConsultaEntidadeSupervisionavelVO consulta, Criteria criteria) {
        if (StringUtils.isNotEmpty(consulta.getConglomeradoOuCnpj())) {
            criteria.add(Restrictions.ilike(CNPJ, consulta.getConglomeradoOuCnpj().trim(), MatchMode.EXACT));
        }
    }

    private void adicionarFiltroPertenceAoSrc(Criteria criteria, SimNaoEnum simounao) {
        criteria.add(Restrictions.eq(PERTENCE_SRC, simounao));
    }

    private void adicionarFiltroNome(ConsultaEntidadeSupervisionavelVO consulta, Criteria criteria) {
        if (StringUtils.isNotEmpty(consulta.getNome())) {
            criteria.add(Restrictions.ilike(NOME, consulta.getNome(), MatchMode.ANYWHERE));
        }
    }

    private void adicionarFiltroEquipe(ConsultaEntidadeSupervisionavelVO consulta, Criteria criteria) {
        if (StringUtils.isNotEmpty(consulta.getLocalizacao())) {
            if (consulta.isBuscarHierarquiaInferior()) {
                criteria.add(Restrictions.like(LOCALIZACAO, consulta.getLocalizacao(), MatchMode.ANYWHERE));
            } else {
                criteria.add(Restrictions.eq(LOCALIZACAO, consulta.getLocalizacao()));
            }
        }
    }

    private void adicionarFiltroPrioridade(ConsultaEntidadeSupervisionavelVO consulta, Criteria criteria) {
        if (consulta.isPossuiPrioridade()) {
            criteria.add(Restrictions.isNotNull(PRIORIDADE));
        }
        if (consulta.getPrioridade() != null) {
            criteria.add(Restrictions.eq(PRIORIDADE, consulta.getPrioridade()));
        }
    }

    private void adicionarFiltroVersaoPerfilDeRisco(ConsultaEntidadeSupervisionavelVO consulta, Criteria criteria) {
        if (consulta.isPossuiVersaoPerfilDeRisco() != null) {
            if (consulta.isPossuiVersaoPerfilDeRisco()) {
                versaoPerfilDeRiscoNaoNula(criteria);
            } else {
                versaoPerfilDeRiscoNula(criteria);
            }
        }
    }

    private void versaoPerfilDeRiscoNula(Criteria criteria) {
        criteria.add(Restrictions.isNull(VERSAO_PERFIL_RISCO));
    }

    private void versaoPerfilDeRiscoNaoNula(Criteria criteria) {
        criteria.add(Restrictions.isNotNull(VERSAO_PERFIL_RISCO));
    }

    @SuppressWarnings(UNCHECKED)
    public List<EntidadeSupervisionavelVO> buscarEntidadeSemCiclo() {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        versaoPerfilDeRiscoNula(criteria);
        adicionarFiltroPertenceAoSrc(criteria, SimNaoEnum.SIM);
        UsuarioAplicacao usuario = ((UsuarioAplicacao) UsuarioCorrente.get());
        criteria.add(Restrictions.eq(LOCALIZACAO,
                usuario.getServidorVO().getLocalizacaoAtual(PerfilAcessoEnum.SUPERVISOR)));

        criteria.add(Restrictions.isNotNull(PRIORIDADE));

        // Atributos que serão retornados na consulta
        criteria.setProjection(Projections.projectionList().add(Projections.property(PROP_PK), PROP_PK)
                .add(Projections.property(NOME), NOME).add(Projections.property(LOCALIZACAO), LOCALIZACAO)
                .add(Projections.property(PRIORIDADE), PRIORIDADE));
        criteria.setResultTransformer(new AliasToBeanResultTransformer(EntidadeSupervisionavelVO.class));
        return criteria.list();
    }

    public EntidadeSupervisionavel buscarPorPerfilRisco(Integer pkPerfilRisco) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        Criteria versaoPerfilRiscoCriteria = criteria.createCriteria(VERSAO_PERFIL_RISCO);
        Criteria perfilRiscoCriteria = versaoPerfilRiscoCriteria.createCriteria("perfisRisco");
        perfilRiscoCriteria.add(Restrictions.eq(PROP_PK, pkPerfilRisco));
        return (EntidadeSupervisionavel) criteria.uniqueResult();
    }

    @SuppressWarnings(UNCHECKED)
    public List<EntidadeSupervisionavel> buscarVersoeESs() {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        return criteria.list();
    }
    
    public List<SinteseRiscoRevisaoVO> buscarSintesesRiscoRevisao(String localizacao) {
        List<SinteseRiscoRevisaoVO> sinteses = new ArrayList<SinteseRiscoRevisaoVO>();
        sinteses.addAll(buscarSintesesRiscoRevisaoArc(localizacao));
        sinteses.addAll(buscarSintesesRiscoRevisaoArcExt(localizacao));
        return sinteses;
    }

    @SuppressWarnings(UNCHECKED)
    public List<SinteseRiscoRevisaoVO> buscarSintesesRiscoRevisaoArc(String localizacao) {
        StringBuilder hql = new StringBuilder();
        hql.append("select new br.gov.bcb.sisaps.src.vo.SinteseRiscoRevisaoVO( ");
        hql.append("entidade.pk, entidade.nome, parametroRC.pk, parametroRC.nomeAbreviado, count(arc.pk)) ");
        hql.append("from AvaliacaoRiscoControle arc, CelulaRiscoControle celula ");
        hql.append("inner join celula.parametroGrupoRiscoControle parametroRC ");
        hql.append("inner join celula.atividade as atividade inner join atividade.matriz as matriz ");
        hql.append("inner join matriz.ciclo as ciclo inner join ciclo.entidadeSupervisionavel as entidade ");
        hql.append("where (arc.pk = celula.arcRisco.pk or arc.pk = celula.arcControle.pk) ");
        hql.append("and ciclo.matriz.pk = matriz.pk ");
        hql.append("and entidade.localizacao = :localizacao and arc.estado = :estadoARC ");
        hql.append("group by entidade.pk, entidade.nome, parametroRC.pk, parametroRC.nomeAbreviado");
        Query query = getCurrentSession().createQuery(hql.toString());
        query.setString(LOCALIZACAO, localizacao);
        query.setParameter("estadoARC", EstadoARCEnum.ANALISADO);
        return query.list();
    }

    @SuppressWarnings(UNCHECKED)
    public List<SinteseRiscoRevisaoVO> buscarSintesesRiscoRevisaoArcExt(String localizacao) {
        StringBuilder hql = new StringBuilder();
        hql.append("select new br.gov.bcb.sisaps.src.vo.SinteseRiscoRevisaoVO(arc.pk, ");
        hql.append("entidade.pk, entidade.nome, parametroRC.pk, parametroRC.nomeAbreviado, count(arc.pk)) ");
        hql.append("from AvaliacaoRiscoControle arc ");
        hql.append("inner join arc.avaliacaoRiscoControleExterno as arcExterno ");
        hql.append("inner join arcExterno.parametroGrupoRiscoControle parametroRC ");
        hql.append("inner join arcExterno.ciclo as ciclo inner join ciclo.estadoCiclo as estadoCiclo ");
        hql.append("inner join ciclo.matriz as matriz ");
        hql.append("inner join ciclo.entidadeSupervisionavel as entidade ");
        hql.append("where matriz.numeroFatorRelevanciaAE is not null and matriz.numeroFatorRelevanciaAE > 0 and ");
        hql.append("entidade.localizacao = :localizacao and arc.estado = :estadoARC ");
        hql.append("group by arc.pk, entidade.pk, entidade.nome, parametroRC.pk, parametroRC.nomeAbreviado");
        Query query = getCurrentSession().createQuery(hql.toString());
        query.setString(LOCALIZACAO, localizacao);
        query.setParameter("estadoARC", EstadoARCEnum.ANALISADO);
        return query.list();
    }

    @SuppressWarnings(UNCHECKED)
    public List<String> consultarLocalizacoesESsAtivas() {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        adicionarFiltroPertenceAoSrc(criteria, SimNaoEnum.SIM);
        criteria.add(Restrictions.isNotNull(PRIORIDADE));
        criteria.setProjection(Projections.projectionList().add(Projections.groupProperty(LOCALIZACAO)));
        criteria.addOrder(Order.asc(LOCALIZACAO));
        return criteria.list();
    }

    @SuppressWarnings(UNCHECKED)
    public List<EntidadeSupervisionavelVO> consultaESsAtivas(ConsultaCicloVO consulta) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.isNotNull(PRIORIDADE));
        adicionarFiltroPertenceAoSrc(criteria, SimNaoEnum.SIM);
        criteria.setProjection(Projections.projectionList().add(Projections.property(PROP_PK), PROP_PK)
                .add(Projections.property(NOME), NOME).add(Projections.property(LOCALIZACAO), LOCALIZACAO)
                .add(Projections.property(PRIORIDADE), PRIORIDADE));
        criteria.setResultTransformer(new AliasToBeanResultTransformer(EntidadeSupervisionavelVO.class));
        return criteria.list();
    }

    public EntidadeSupervisionavelVO buscarEntidadeSRCPorCnpj(Integer codigo) {
        System.out.println(codigo);
        System.out.println(codigo.toString().length());
        StringBuilder hql = new StringBuilder();
        hql.append("select ent.pk, ent.nome, ent.conglomeradoOuCnpj ");
        hql.append("from EntidadeSupervisionavel as ent ");
        hql.append("where cast((case when ent.conglomeradoOuCnpj = '' then '-1' ");
        hql.append("when(substring(ent.conglomeradoOuCnpj, 1, 1) = 'C') ");
        hql.append("then substring(ent.conglomeradoOuCnpj, 4, 5) ");
        hql.append("else ent.conglomeradoOuCnpj end) as int)= " + codigo + " order by ent.pk desc");
        Query query = getCurrentSession().createQuery(hql.toString());

        EntidadeSupervisionavelVO ent = null;

        try {
            Object object = query.list();
            if (object != null && !query.list().isEmpty()) {
                Object obj = query.list().get(0);
                ent = new EntidadeSupervisionavelVO();
                ent.setPk(((Integer) ((Object[]) obj)[0]));
                ent.setNome(((String) ((Object[]) obj)[1]));
                ent.setConglomeradoOuCnpj((String) ((Object[]) obj)[2]);
            }
        } catch (NoResultException e) {
            System.out.println("Registro não encontrado.");
        }

        return ent;
    }

    public List<EntidadeSupervisionavel> buscarEssPorCNPJ(String cnpj, boolean isCicloMigrado) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq("conglomeradoOuCnpj", cnpj));
        if(!isCicloMigrado){
        	criteria.add(Restrictions.gt(PROP_PK, 0));
        }
        criteria.addOrder(Order.desc("ultimaAtualizacao"));
        return cast(criteria.list());
    }


}
