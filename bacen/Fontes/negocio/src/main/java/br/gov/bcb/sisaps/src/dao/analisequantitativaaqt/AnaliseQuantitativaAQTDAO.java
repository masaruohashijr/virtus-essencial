package br.gov.bcb.sisaps.src.dao.analisequantitativaaqt;

import static br.gov.bcb.app.stuff.util.props.PropertyUtils.getPropertyObject;
import static br.gov.bcb.app.stuff.util.props.PropertyUtils.property;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import br.gov.bcb.sisaps.comparador.ComparadorVersaoAnef;
import br.gov.bcb.sisaps.dao.GenericDAOParaListagens;
import br.gov.bcb.sisaps.seguranca.UsuarioAplicacao;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.VersaoPerfilRisco;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroAQT;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoAQTEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoARCEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoCicloEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.vo.ConsultaAnaliseQuantitativaAQTVO;
import br.gov.bcb.sisaps.src.vo.analisequantitativa.aqt.AnaliseQuantitativaAQTVO;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.Util;

@Repository
public class AnaliseQuantitativaAQTDAO extends
        GenericDAOParaListagens<AnaliseQuantitativaAQT, Integer, AnaliseQuantitativaAQTVO, ConsultaAnaliseQuantitativaAQTVO> {

    private static final String PORCENTAGEM = "%";
    private static final String ESTADOS_AQT = "estadosAQT";
    private static final String ULTIMA_ATUALIZACAO = "ultimaAtualizacao";
    private static final String C = "c";
    private static final String C_PK = "c.pk";
    private static final String PARAMETRO_AQT_ORDEM = "parametroAQT.ordem";
    private static final String VERSAO_PERFIL_RISCO = "versaoPerfilRisco";
    private static final String PK = "pk";
    private static final String ENTIDADE_SUPERVISIONAVEL_LOCALIZACAO = "entidadeSupervisionavel.localizacao";
    private static final String DELEGACAO_MATRICULA_SERVIDOR = "delegacao.matriculaServidor";
    private static final String CICLO_ENTIDADE_SUPERVISIONAVEL = "ciclo.entidadeSupervisionavel";
    private static final String ESTADO = "estado";
    private static final String ENTIDADE_SUPERVISIONAVEL = "entidadeSupervisionavel";
    private static final String PARAMETRO = "parametro";
    private static final String PARAMETRO_AQT = "parametroAQT";
    private static final String ESTADO_CICLO = "estadoCiclo";
    private static final String DESIGNACAO = "designacao";
    private static final String DELEGACAO = "delegacao";
    private static final String CICLO = "ciclo";

    public AnaliseQuantitativaAQTDAO() {
        super(AnaliseQuantitativaAQT.class, AnaliseQuantitativaAQTVO.class);
    }

    public AnaliseQuantitativaAQT buscarAnefPorPK(Integer id) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(PK, id));
        return (AnaliseQuantitativaAQT) criteria.uniqueResult();
    }

    @Override
    protected void montarCriterios(Criteria criteria, ConsultaAnaliseQuantitativaAQTVO consulta) {
        // TODO Auto-generated method stub

    }

    private Criteria criteriaAQT() {
        return getCurrentSession().createCriteria(AnaliseQuantitativaAQT.class);
    }

    private void addOrdemParametroAQT(Criteria aqtsCriteria) {
        aqtsCriteria.addOrder(Order.asc("parametro.ordem"));
    }

    private void addRestricaoEstadoCiclo(Criteria aqtsCriteria) {
        aqtsCriteria.add(Restrictions.eq("estadoCiclo.estado", EstadoCicloEnum.EM_ANDAMENTO));
    }

    private void addRestricaoEstadoAQT(Criteria aqtsCriteria, List<EstadoAQTEnum> lista) {
        aqtsCriteria.add(Restrictions.in(ESTADO, lista));
    }

    private Criteria criarCriteriaConsultaPainelIspetor() {
        Criteria aqtsCriteria = criteriaAQT();
        aqtsCriteria.createAlias(PARAMETRO_AQT, PARAMETRO, JoinType.INNER_JOIN);
        aqtsCriteria.createAlias(CICLO, CICLO, JoinType.INNER_JOIN);
        aqtsCriteria.createAlias("ciclo.estadoCiclo", ESTADO_CICLO, JoinType.INNER_JOIN);
        return aqtsCriteria;
    }

    @SuppressWarnings(UNCHECKED)
    public List<AnaliseQuantitativaAQT> consultaPainelAQTDesignados() {
        UsuarioAplicacao usuario = usuarioAplicacao();
        Criteria aqtsCriteria = criarCriteriaConsultaPainelIspetor();
        aqtsCriteria.createAlias(DESIGNACAO, DESIGNACAO);
        addRestricaoEstadoAQT(aqtsCriteria, EstadoAQTEnum.listaEstadosDesignadoEdicao());
        addRestricaoEstadoCiclo(aqtsCriteria);
        aqtsCriteria.add(Restrictions.eq("designacao.matriculaServidor", usuario.getMatricula()));
        addOrdemParametroAQT(aqtsCriteria);
        return aqtsCriteria.list();
    }

    @SuppressWarnings(UNCHECKED)
    public List<AnaliseQuantitativaAQT> consultaPainelAQTDelegados() {
        UsuarioAplicacao usuario = usuarioAplicacao();
        Criteria aqtsCriteria = criarCriteriaConsultaPainelIspetor();
        aqtsCriteria.createAlias(DELEGACAO, DELEGACAO);
        addRestricaoEstadoAQT(aqtsCriteria, EstadoAQTEnum.listaEstadosDelegadoAnalise());
        addRestricaoEstadoCiclo(aqtsCriteria);
        aqtsCriteria.add(Restrictions.eq(DELEGACAO_MATRICULA_SERVIDOR, usuario.getMatricula()));
        addOrdemParametroAQT(aqtsCriteria);
        return aqtsCriteria.list();
    }

    @SuppressWarnings(UNCHECKED)
    public List<AnaliseQuantitativaAQT> consultaPainelAQTAnalise() {
        UsuarioAplicacao usuario = usuarioAplicacao();
        Criteria aqtsCriteria = criarCriteriaConsultaPainelIspetor();
        aqtsCriteria.createAlias(DELEGACAO, DELEGACAO, JoinType.LEFT_OUTER_JOIN);
        aqtsCriteria.createAlias(CICLO_ENTIDADE_SUPERVISIONAVEL, ENTIDADE_SUPERVISIONAVEL, JoinType.INNER_JOIN);
        addRestricaoEstadoCiclo(aqtsCriteria);
        addRestricaoEstadoAQT(aqtsCriteria, EstadoAQTEnum.listaEstadosAnaliseSupervisor());

        aqtsCriteria.add(Restrictions.or(
                Restrictions.eq(ENTIDADE_SUPERVISIONAVEL_LOCALIZACAO, 
                		usuario.getServidorVO().getLocalizacaoAtual(PerfilAcessoEnum.SUPERVISOR)),
                Restrictions.eq(DELEGACAO_MATRICULA_SERVIDOR, usuario.getMatricula())));

        addOrdemParametroAQT(aqtsCriteria);
        return aqtsCriteria.list();
    }


    @SuppressWarnings(UNCHECKED)
    public List<AnaliseQuantitativaAQT> consultaPainelAQTAnalisado() {
        UsuarioAplicacao usuario = usuarioAplicacao();
        Criteria aqtsCriteria = criarCriteriaConsultaPainelIspetor();
        aqtsCriteria.createAlias(CICLO_ENTIDADE_SUPERVISIONAVEL, ENTIDADE_SUPERVISIONAVEL, JoinType.INNER_JOIN);
        addRestricaoEstadoCiclo(aqtsCriteria);
        addRestricaoEstadoAQT(aqtsCriteria, Arrays.asList(EstadoAQTEnum.ANALISADO));
        aqtsCriteria.add(Restrictions.eq(ENTIDADE_SUPERVISIONAVEL_LOCALIZACAO, usuario.getServidorVO()
                .getLocalizacaoAtual(PerfilAcessoEnum.SUPERVISOR)));
        addOrdemParametroAQT(aqtsCriteria);
        return aqtsCriteria.list();
    }

    @SuppressWarnings(UNCHECKED)
    public List<AnaliseQuantitativaAQT> buscarAQTsPerfilRisco(List<VersaoPerfilRisco> versoes) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.createAlias(PARAMETRO_AQT, PARAMETRO_AQT);
        criteria.add(Restrictions.in(VERSAO_PERFIL_RISCO, versoes));
        criteria.addOrder(Order.asc(PARAMETRO_AQT_ORDEM));
        return criteria.list();
    }

    public AnaliseQuantitativaAQT buscarAQTRascunho(AnaliseQuantitativaAQT aqt) {
        Criteria criteria = criarAliasParaAQT(aqt.getParametroAQT(), aqt.getCiclo());
        criteria.add(Restrictions.isNull(VERSAO_PERFIL_RISCO));
        return (AnaliseQuantitativaAQT) criteria.uniqueResult();
    }

    private Criteria criarAliasParaAQT(ParametroAQT parametro, Ciclo ciclo) {
        Criteria criteria = criteriaAQT();
        criteria.createAlias(PARAMETRO_AQT, "p");
        criteria.createAlias(CICLO, C);
        criteria.add(Restrictions.eq("p.pk", parametro.getPk()));
        criteria.add(Restrictions.eq(C_PK, ciclo.getPk()));
        return criteria;
    }

    public AnaliseQuantitativaAQT buscarAQTVigenteEstadoConcluido(AnaliseQuantitativaAQT aqtRascunho) {
        Criteria criteria = criarAliasParaAQT(aqtRascunho.getParametroAQT(), aqtRascunho.getCiclo());
        criteria.add(Restrictions.eq(ESTADO, EstadoARCEnum.CONCLUIDO));
        Criteria subCriteria = subCriteriaDataConclusao(aqtRascunho, criteria);
        subCriteria.add(Restrictions.eq(ESTADO, EstadoARCEnum.CONCLUIDO));
        return (AnaliseQuantitativaAQT) subCriteria.uniqueResult();
    }

    public AnaliseQuantitativaAQT buscarAQTVigente(AnaliseQuantitativaAQT aqtRascunho) {
        Criteria criteria = criarAliasParaAQT(aqtRascunho.getParametroAQT(), aqtRascunho.getCiclo());
        criteria.add(Restrictions.isNotNull(VERSAO_PERFIL_RISCO));
        Criteria subCriteria = subCriteria(aqtRascunho, criteria);
        subCriteria.add(Restrictions.isNotNull(VERSAO_PERFIL_RISCO));
        List<AnaliseQuantitativaAQT> list = cast(subCriteria.list());
        return Util.isNuloOuVazio(list) ? null : list.get(0);
    }

    private Criteria subCriteriaDataConclusao(AnaliseQuantitativaAQT aqtRascunho, Criteria criteria) {
        criteria.setProjection(Projections.max(property(getPropertyObject(AnaliseQuantitativaAQT.class)
                .getDataConclusao())));
        DateTime dt = ((DateTime) criteria.uniqueResult());
        Criteria criteriaRetorno = criarAliasParaAQT(aqtRascunho.getParametroAQT(), aqtRascunho.getCiclo());
        criteriaRetorno.add(Restrictions.eq(
                property(getPropertyObject(AnaliseQuantitativaAQT.class).getDataConclusao()), dt));
        return criteriaRetorno;
    }

    private Criteria subCriteria(AnaliseQuantitativaAQT aqtRascunho, Criteria criteria) {
        criteria.setProjection(Projections.max(property(getPropertyObject(AnaliseQuantitativaAQT.class)
                .getUltimaAtualizacao())));
        DateTime dt = ((DateTime) criteria.uniqueResult());
        Criteria criteriaRetorno = criarAliasParaAQT(aqtRascunho.getParametroAQT(), aqtRascunho.getCiclo());
        criteriaRetorno.add(Restrictions.eq(property(getPropertyObject(AnaliseQuantitativaAQT.class)
                .getUltimaAtualizacao()), dt));
        return criteriaRetorno;
    }


    @SuppressWarnings("unchecked")
    public List<AnaliseQuantitativaAQT> listarANEFsRascunho(Ciclo ciclo) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.createAlias(CICLO, C);
        criteria.add(Restrictions.eq(C_PK, ciclo.getPk()));
        criteria.createAlias(PARAMETRO_AQT, PARAMETRO_AQT);
        criteria.add(Restrictions.isNull(VERSAO_PERFIL_RISCO));
        criteria.addOrder(Order.asc(PARAMETRO_AQT_ORDEM));
        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    public List<AnaliseQuantitativaAQT> listarANEFsVigentes(Ciclo ciclo) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.createAlias(CICLO, C);
        criteria.add(Restrictions.eq(C_PK, ciclo.getPk()));
        criteria.createAlias(PARAMETRO_AQT, PARAMETRO_AQT);
        criteria.add(Restrictions.isNotNull(VERSAO_PERFIL_RISCO));
        criteria.addOrder(Order.asc(PARAMETRO_AQT_ORDEM));
        return criteria.list();
    }

    public AnaliseQuantitativaAQT buscarAQTRascunhoParametroECiclo(ParametroAQT parametro, Ciclo ciclo) {
        Criteria criteria = criarAliasParaAQT(parametro, ciclo);
        criteria.add(Restrictions.isNull(VERSAO_PERFIL_RISCO));
        return (AnaliseQuantitativaAQT) criteria.uniqueResult();
    }

    public AnaliseQuantitativaAQT buscarAQTVigentePorPerfil(ParametroAQT parametro, Ciclo ciclo, PerfilRisco perfil) {
        Criteria criteria = criarAliasParaAQT(parametro, ciclo);
        criteria.add(Restrictions.isNotNull(VERSAO_PERFIL_RISCO));
        criteria.add(Restrictions.in(VERSAO_PERFIL_RISCO, perfil.getVersoesPerfilRisco()));
        return (AnaliseQuantitativaAQT) criteria.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<AnaliseQuantitativaAQT> listarANEFConsulta(ParametroAQT parametro, Ciclo ciclo) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.createAlias(CICLO, C);
        criteria.createAlias(PARAMETRO_AQT, PARAMETRO_AQT);
        criteria.add(Restrictions.eq(PARAMETRO_AQT, parametro));
        criteria.add(Restrictions.eq(C_PK, ciclo.getPk()));
        criteria.add(Restrictions.isNotNull(VERSAO_PERFIL_RISCO));
        criteria.add(Restrictions.eq(ESTADO, EstadoARCEnum.CONCLUIDO));
        criteria.addOrder(Order.desc(ULTIMA_ATUALIZACAO));
        return criteria.list();
    }

    @SuppressWarnings(UNCHECKED)
    public PerfilRisco buscarUltimoPerfilRisco(AnaliseQuantitativaAQT anef) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.createAlias(VERSAO_PERFIL_RISCO, "versaoPerfil");
        criteria.add(Restrictions.eq("versaoPerfil.pk", anef.getVersaoPerfilRisco().getPk()));
        criteria.setProjection(Projections.property(VERSAO_PERFIL_RISCO));
        VersaoPerfilRisco versao = (VersaoPerfilRisco) criteria.uniqueResult();
        List<PerfilRisco> perfisRisco = versao.getPerfisRisco();
        ComparatorChain cc = new ComparatorChain();
        cc.addComparator(new BeanComparator("dataCriacao"));
        cc.setReverseSort(0);
        Collections.sort(perfisRisco, cc);
        return perfisRisco.get(0);
    }

    @SuppressWarnings("unchecked")
    public List<AnaliseQuantitativaAQTVO> consultarHistoricoAQTfinal(ConsultaAnaliseQuantitativaAQTVO consulta) {
        UsuarioAplicacao usuario = usuarioAplicacao();
        String sqlOperadorPreenchido = "and (lower(aqt.operadorPreenchido) = :operadorPreenchido)";
        String sqlOperadorAnalisado = "and (lower(aqt.operadorAnalise) = :operadorAnalisado)";

        StringBuilder hqlOperadorPreenchido = new StringBuilder();
        hqlOperadorPreenchido.append(obterSelectHistoricoAnefs());
        sqlCodigoOperador(hqlOperadorPreenchido, sqlOperadorPreenchido, false);
        montarFiltrosHistorico(consulta, hqlOperadorPreenchido);
        Query queryOperadorPreenchido = getCurrentSession().createQuery(hqlOperadorPreenchido.toString());
        queryOperadorPreenchido.setParameterList(ESTADOS_AQT,
                EstadoAQTEnum.listaEstadosPreenchidoAnalisadoEmAnaliseConcluido());
        setFiltroNomeEntidadeSupervisionavel(consulta, queryOperadorPreenchido);
        setFiltroNomeComponente(consulta, queryOperadorPreenchido);
        setFiltroEstadoANEF(consulta, queryOperadorPreenchido, usuario);
        queryOperadorPreenchido.setString("operadorPreenchido", usuario.getLogin().toLowerCase());

        StringBuilder hqlOperadorAnalisado = new StringBuilder();
        hqlOperadorAnalisado.append(obterSelectHistoricoAnefs());
        sqlCodigoOperador(hqlOperadorAnalisado, sqlOperadorAnalisado, true);
        montarFiltrosHistorico(consulta, hqlOperadorAnalisado);
        Query queryOperadorAnalisado = getCurrentSession().createQuery(hqlOperadorAnalisado.toString());
        setFiltroNomeEntidadeSupervisionavel(consulta, queryOperadorAnalisado);
        setFiltroNomeComponente(consulta, queryOperadorAnalisado);
        setFiltroEstadoANEF(consulta, queryOperadorAnalisado, usuario);
        queryOperadorAnalisado.setParameterList(ESTADOS_AQT,
                EstadoAQTEnum.listaEstadosPreenchidoAnalisadoEmAnaliseConcluido());
        queryOperadorAnalisado.setString("operadorAnalisado", usuario.getLogin().toLowerCase());


        List<AnaliseQuantitativaAQTVO> listaAnefPreenchidos = queryOperadorPreenchido.list();
        preencherAcao(EstadoAQTEnum.PREENCHIDO, listaAnefPreenchidos);

        List<AnaliseQuantitativaAQTVO> listaAnefAnalisados = queryOperadorAnalisado.list();
        preencherAcao(EstadoAQTEnum.ANALISADO, listaAnefAnalisados);

        List<AnaliseQuantitativaAQTVO> listaANEFs = new LinkedList<AnaliseQuantitativaAQTVO>();
        if (consulta.isAcao()) {
            if (EstadoAQTEnum.PREENCHIDO.equals(consulta.getEstadoANEF())) {
                listaANEFs.addAll(listaAnefPreenchidos);
            } else {
                listaANEFs.addAll(listaAnefAnalisados);
            }
        } else {
            listaANEFs.addAll(listaAnefPreenchidos);
            listaANEFs.addAll(listaAnefAnalisados);
        }

        Collections.sort(listaANEFs, new ComparadorVersaoAnef());
        return listaANEFs;

    }

    private void preencherVersao(AnaliseQuantitativaAQTVO anef) {
        if (EstadoAQTEnum.CONCLUIDO.equals(anef.getEstado())) {
            if (anef.getDataConclusao() != null) {
                anef.setVersao(anef.getDataConclusao().toString(Constantes.FORMATO_DATA_COM_BARRAS));
            }
        } else {
            anef.setDataConclusao(null);
            anef.setVersao(anef.getEstado().getDescricao());
        }
    }

    private void preencherAcao(EstadoAQTEnum estado, List<AnaliseQuantitativaAQTVO> listaANEFs) {
        for (AnaliseQuantitativaAQTVO anef : listaANEFs) {
            anef.setAcao(estado.getDescricao());
            preencherVersao(anef);
        }
    }

    private void setFiltroNomeEntidadeSupervisionavel(ConsultaAnaliseQuantitativaAQTVO consulta, Query query) {
        if (consulta.getNomeES() != null) {
            query.setString("nomeES", PORCENTAGEM + consulta.getNomeES() + PORCENTAGEM);
        }
    }

    private void setFiltroNomeComponente(ConsultaAnaliseQuantitativaAQTVO consulta, Query query) {
        if (consulta.getNomeComponente() != null) {
            query.setString("nomeComponente", PORCENTAGEM + consulta.getNomeComponente() + PORCENTAGEM);
        }
    }

    private void setFiltroEstadoANEF(ConsultaAnaliseQuantitativaAQTVO consulta, Query query, UsuarioAplicacao usuario) {
        if (consulta.getEstadoANEF() != null) {
            consulta.setAcao(true);
            if (EstadoAQTEnum.PREENCHIDO.equals(consulta.getEstadoANEF())) {
                query.setString("operPreenchido", usuario.getLogin());
            } else if (EstadoAQTEnum.ANALISADO.equals(consulta.getEstadoANEF())) {
                query.setString("operAnalise", usuario.getLogin());
            }
        }
    }

    private void sqlCodigoOperador(StringBuilder hql, String operador, boolean isAnalise) {
        hql.append("from AnaliseQuantitativaAQT aqt ");
        hql.append("inner join aqt.ciclo as ciclo ");
        hql.append("inner join ciclo.entidadeSupervisionavel as entidadeSupervisionavel ");
        hql.append("inner join aqt.parametroAQT as parametroAQT ");
        hql.append("where aqt.ciclo.pk = ciclo.pk and aqt.estado in (:estadosAQT) ");
        hql.append(operador);
        hql.append(" and not exists (select aqt3.pk, aqt3.dataConclusao from AnaliseQuantitativaAQT as aqt3 ");
        hql.append("where aqt.ciclo.pk = aqt3.ciclo.pk and aqt.parametroAQT.pk = aqt3.parametroAQT.pk ");
        hql.append("and aqt3.dataConclusao > aqt.dataConclusao ");
        hql.append("and day(aqt.dataConclusao) = day(aqt3.dataConclusao) ");
        hql.append("and month(aqt.dataConclusao) = month(aqt3.dataConclusao) ");
        hql.append("and year(aqt.dataConclusao) = year(aqt3.dataConclusao)) ");
        
        hql.append("and aqt.pk not in( "
                + "select aqt5.pk from AnaliseQuantitativaAQT as aqt5 "
                + "where aqt5.ultimaAtualizacao in ( "
                + "select min(aqt6.ultimaAtualizacao) from  AnaliseQuantitativaAQT as aqt6 "
                + "where aqt6.versaoPerfilRisco is not null "
                + "and aqt6.ciclo.pk = aqt5.ciclo.pk "
                + "group by aqt6.ciclo, aqt6.parametroAQT ) )");
    }

    private StringBuilder obterSelectHistoricoAnefs() {
        StringBuilder hql = new StringBuilder();
        hql.append("select distinct "
                + "new br.gov.bcb.sisaps.src.vo.analisequantitativa.aqt.AnaliseQuantitativaAQTVO(aqt.pk, ");
        hql.append("aqt.estado, aqt.parametroAQT, aqt.ciclo, ciclo.entidadeSupervisionavel, ");
        hql.append("aqt.operadorPreenchido, aqt.operadorAnalise, aqt.dataConclusao)");
        return hql;
    }

    private void montarFiltrosHistorico(ConsultaAnaliseQuantitativaAQTVO consulta, StringBuilder hql) {
        if (consulta.getNomeES() != null) {
            hql.append(" and lower(entidadeSupervisionavel.nome) like lower(:nomeES)");
        }

        if (consulta.getNomeComponente() != null) {
            hql.append(" and lower(parametroAQT.descricao) like lower(:nomeComponente)");
        }

        if (consulta.getEstadoANEF() != null) {
            if (EstadoAQTEnum.PREENCHIDO.equals(consulta.getEstadoANEF())) {
                hql.append("and (aqt.operadorPreenchido = :operPreenchido ) ");
            } else if (EstadoAQTEnum.ANALISADO.equals(consulta.getEstadoANEF())) {
                hql.append("and (aqt.operadorAnalise = :operAnalise ) ");
            }
        } else {
            consulta.setAcao(false);
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<AnaliseQuantitativaAQT> buscarAnefPorCiclo(Integer pkCiclo, List<VersaoPerfilRisco> versoesAQT) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.createAlias(CICLO, C);
        criteria.add(Restrictions.eq(C_PK, pkCiclo));
        if (CollectionUtils.isNotEmpty(versoesAQT)) {
            criteria.add(Restrictions.and(Restrictions.or(
                    Restrictions.not(Restrictions.in(VERSAO_PERFIL_RISCO, versoesAQT)),
                    Restrictions.isNull(VERSAO_PERFIL_RISCO))));
        }
        return criteria.list();
    }

}
