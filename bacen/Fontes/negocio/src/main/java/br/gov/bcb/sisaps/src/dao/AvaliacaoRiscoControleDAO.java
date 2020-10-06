package br.gov.bcb.sisaps.src.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.springframework.stereotype.Repository;

import br.gov.bcb.sisaps.comparador.ComparadorVersaoARC;
import br.gov.bcb.sisaps.dao.GenericDAOParaListagens;
import br.gov.bcb.sisaps.seguranca.UsuarioAplicacao;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.ParametroGrupoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.VersaoPerfilRisco;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoAQTEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoARCEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoCicloEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoMatrizEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoGrupoEnum;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.src.vo.ARCDesignacaoVO;
import br.gov.bcb.sisaps.src.vo.ArcNotasVO;
import br.gov.bcb.sisaps.src.vo.ArcResumidoVO;
import br.gov.bcb.sisaps.src.vo.AvaliacaoRiscoControleVO;
import br.gov.bcb.sisaps.src.vo.ConsultaARCDesignacaoVO;
import br.gov.bcb.sisaps.src.vo.ConsultaAvaliacaoRiscoControleVO;
import br.gov.bcb.sisaps.util.Constantes;

@Repository
public class AvaliacaoRiscoControleDAO
        extends
        GenericDAOParaListagens<AvaliacaoRiscoControle, Integer, 
        AvaliacaoRiscoControleVO, ConsultaAvaliacaoRiscoControleVO> {

    private static final String SELECT_DISTINCT_NEW_BR_GOV_BCB_SISAPS_SRC_VO_ARC_DESIGNACAO_VO = 
            "select distinct new br.gov.bcb.sisaps.src.vo.ARCDesignacaoVO(";
    private static final String AND_DESIGNACAO_MATRICULA_SERVIDOR_MATRICULA_SERVIDOR = 
            "and designacao.matriculaServidor = :matriculaServidor ";
    private static final String AND_ARC_ESTADO_IN_ESTADOS_ARC = "and arc.estado in (:estadosARC) ";
    private static final String ESTADO_CICLO = "estadoCiclo";
    private static final String WHERE_ARC_PK_CELULA_ARC_RISCO_PK_OR_ARC_PK_CELULA_ARC_CONTROLE_PK_AND = 
            "where (arc.pk = celula.arcRisco.pk or arc.pk = celula.arcControle.pk) and ";
    private static final String INNER_JOIN_MATRIZ_CICLO_AS_CICLO_INNER_JOIN_CICLO_ENTIDADE_SUPERVISIONAVEL_AS_ENTIDADE =
            "inner join matriz.ciclo as ciclo inner join ciclo.entidadeSupervisionavel as entidade ";
    private static final String INNER_JOIN_CELULA_ATIVIDADE_AS_ATIVIDADE_INNER_JOIN_ATIVIDADE_MATRIZ_AS_MATRIZ = 
            "inner join celula.atividade as atividade inner join atividade.matriz as matriz ";
    private static final String SELECT_DISTINCT_NEW_BR_GOV_BCB_SISAPS_SRC_VO_AVALIACAO_RISCO_CONTROLE_VO_ARC_PK = 
            "select distinct  new br.gov.bcb.sisaps.src.vo.AvaliacaoRiscoControleVO(arc.pk, ";
    private static final String WHERE_ARC_PK_CELULA_ARC_RISCO_PK_OR_ARC_PK_CELULA_ARC_CONTROLE_PK = 
            "where (arc.pk = celula.arcRisco.pk or arc.pk = celula.arcControle.pk) ";
    private static final String CELULA_RISCO_CONTROLE_CELULA_INNER_JOIN_CELULA_ATIVIDADE_AS_ATIVIDADE = 
            "CelulaRiscoControle celula inner join celula.atividade as atividade ";
    private static final String AND_ARC_TIPO_TIPO_GRUPO = "and arc.tipo = :tipoGrupo ";
    private static final String INNER_JOIN_CELULA_PARAMETRO_GRUPO_RISCO_CONTROLE_AS_PARAMETRO_RC = 
            "inner join celula.parametroGrupoRiscoControle as parametroRC ";
    private static final String TIPO_GRUPO = "tipoGrupo";
    private static final String NOME_ABREVIADO = "nomeAbreviado";
    private static final String NOME_ATIVIDADE = "nomeAtividade";
    private static final String PORCENTAGEM = "%";
    private static final String PARAMETRO_ESTADOS_ARC = "estadosARC";
    private static final String VERSAO_PERFIL_RISCO = "versaoPerfilRisco";
    private static final String PARAMETRO_MATRICULA_SERVIDOR = "matriculaServidor";
    private static final String PARAMETRO_ESTADO_MATRIZ = "estadoMatriz";
    private static final String PROP_TIPO = "tipo";
    private static final String ATV1 = "atv1";
    private static final String MATRIZ_PK = "matriz.pk";
    private static final String PROP_ATIVIDADES = "atividades";
    private static final String PROP_PARAMETRO_GRUPO_RISCO_CONTROLE = "parametroGrupoRiscoControle";
    private static final String PROP_ESTADO = "estado";
    private static final String PK = "pk";

    private static final String SELECT_DISTINCT_ARC_DESCRICAO_VO =
            "select distinct  new br.gov.bcb.sisaps.src.vo.ArcResumidoVO(arc.pk, ";

    public AvaliacaoRiscoControleDAO() {
        super(AvaliacaoRiscoControle.class, AvaliacaoRiscoControleVO.class);
    }

    @Override
    protected void montarCriterios(Criteria criteria, ConsultaAvaliacaoRiscoControleVO consulta) {
        adicionarFiltroAtividade(consulta, criteria);

        // Atributos que serão retornados na consulta
        criteria.setProjection(Projections.projectionList().add(Projections.property(PK), PK));
    }

    private void adicionarFiltroAtividade(ConsultaAvaliacaoRiscoControleVO consulta, Criteria criteria) {
        if (consulta.getAtividade() != null) {
            avaliacaoPorIdAtividade(consulta.getAtividade().getPk(), consulta.getAtividade().getMatriz() == null ? null
                    : consulta.getAtividade().getMatriz().getPk(), criteria);
        }
    }

    public AvaliacaoRiscoControle buscarAvaliacaoAtividade(ParametroGrupoRiscoControle parametro, Integer atividade,
            Integer matriz, List<VersaoPerfilRisco> versoesPerfilRiscoARCs, TipoGrupoEnum tipo) {
        Criteria arcsCriteria = criteriaAvaliacaoRiscoControle();
        arcsCriteria.add(Restrictions.eq(PROP_TIPO, tipo));
        arcsCriteria.add(Restrictions.eq(PROP_PARAMETRO_GRUPO_RISCO_CONTROLE, parametro));
        adicionarFiltroArcRecenteOUVersaoesPerfilRisco(null, versoesPerfilRiscoARCs, arcsCriteria);
        arcsCriteria = avaliacaoPorIdAtividade(atividade, matriz, arcsCriteria);
        arcsCriteria.addOrder(Order.desc(PK));
        arcsCriteria.setMaxResults(1);
        return CollectionUtils.isNotEmpty(arcsCriteria.list()) ? (AvaliacaoRiscoControle) arcsCriteria.list().get(0)
                : null;
    }

    @SuppressWarnings(UNCHECKED)
    public List<AvaliacaoRiscoControle> buscarAvaliacaoPorAtividade(Integer atividade, Integer matriz,
            boolean apenasRisco) {
        Criteria arcsCriteria = criteriaAvaliacaoRiscoControle();
        arcsCriteria = avaliacaoPorIdAtividade(atividade, matriz, arcsCriteria);
        if (apenasRisco) {
            adicionarFiltroArcRecenteOUVersaoesPerfilRisco(null, null, arcsCriteria);
            arcsCriteria.createAlias(PROP_PARAMETRO_GRUPO_RISCO_CONTROLE, PROP_PARAMETRO_GRUPO_RISCO_CONTROLE);
            arcsCriteria.add(Restrictions.isNotNull(PROP_PARAMETRO_GRUPO_RISCO_CONTROLE + ".parametroControle"));
            arcsCriteria.addOrder(Order.asc(PROP_PARAMETRO_GRUPO_RISCO_CONTROLE + ".nome").ignoreCase());
        }

        return arcsCriteria.list();
    }

    private Criteria avaliacaoPorIdAtividade(Integer atividade, Integer matriz, Criteria arcsCriteria) {
        Criteria atividadesCriteria = arcsCriteria.createCriteria(PROP_ATIVIDADES, ATV1);
        List<Integer> atividades = new ArrayList<Integer>();
        atividades.add(atividade);
        atividadesCriteria.add(Restrictions.in(PK, atividades));
        if (matriz != null) {
            atividadesCriteria.add(Restrictions.eq(MATRIZ_PK, matriz));
        }

        return arcsCriteria;
    }

    public AvaliacaoRiscoControle buscarArcPorPK(Integer id) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(PK, id));
        return (AvaliacaoRiscoControle) criteria.uniqueResult();
    }

    public List<ARCDesignacaoVO> consultaARCsDesignacaoVO(ConsultaARCDesignacaoVO consulta) {
        List<ARCDesignacaoVO> arcs = consultaARCsDesignacaoVOMatriz(consulta);
        if (StringUtils.isEmpty(consulta.getNomeAtividade())
                && consulta.getTipoGrupo() == null) {
            arcs.addAll(consultaARCsDesignacaoVOExterno(consulta));
        }
        return arcs;
    }

    @SuppressWarnings(UNCHECKED)
    private List<ARCDesignacaoVO> consultaARCsDesignacaoVOExterno(ConsultaARCDesignacaoVO consulta) {
        StringBuilder hql = new StringBuilder();
        hql.append(SELECT_DISTINCT_NEW_BR_GOV_BCB_SISAPS_SRC_VO_ARC_DESIGNACAO_VO);
        hql.append("arc.pk, matriz.pk, arcExterno.parametroGrupoRiscoControle.nomeAbreviado, arc.tipo, arc.estado, ");
        hql.append("designacao.pk, designacao.matriculaServidor) ");
        addFromConsultaArcsExternos(hql);
        hql.append("and matriz.pk = :pkMatriz ");
        adicionarFiltroEstado(consulta, hql);
        adicionarFiltroMatriculaResponsavel(consulta, hql);
        adicionarFiltroNomeGrupo(consulta, hql, true);
        Query query = getCurrentSession().createQuery(hql.toString());
        setFiltroMatriz(consulta, query);
        setFiltroEstado(consulta, query);
        setFiltroMatriculaResponsavel(consulta, query);
        setFiltroNomeGrupo(consulta, query);
        return query.list();
    }

    @SuppressWarnings(UNCHECKED)
    private List<ARCDesignacaoVO> consultaARCsDesignacaoVOMatriz(ConsultaARCDesignacaoVO consulta) {
        StringBuilder hql = new StringBuilder();
        hql.append(SELECT_DISTINCT_NEW_BR_GOV_BCB_SISAPS_SRC_VO_ARC_DESIGNACAO_VO);
        hql.append("arc.pk, atividade.matriz.pk, atividade.pk, atividade.nome, parametroRC.nomeAbreviado, ");
        hql.append("arc.tipo, arc.estado, designacao.pk, designacao.matriculaServidor) ");
        hql.append("from AvaliacaoRiscoControle arc left join arc.designacao as designacao, CelulaRiscoControle celula ");
        hql.append(INNER_JOIN_CELULA_PARAMETRO_GRUPO_RISCO_CONTROLE_AS_PARAMETRO_RC);
        hql.append("inner join celula.atividade as atividade ");
        hql.append(WHERE_ARC_PK_CELULA_ARC_RISCO_PK_OR_ARC_PK_CELULA_ARC_CONTROLE_PK);
        hql.append("and atividade.matriz.pk = :pkMatriz ");
        adicionarFiltroNomeAtividade(consulta, hql);
        adicionarFiltroNomeGrupo(consulta, hql, false);
        adicionarFiltroTipoGrupo(consulta, hql);
        adicionarFiltroEstado(consulta, hql);
        adicionarFiltroMatriculaResponsavel(consulta, hql);
        Query query = getCurrentSession().createQuery(hql.toString());
        setFiltroMatriz(consulta, query);
        setFiltroNomeAtividade(consulta, query);
        setFiltroNomeGrupo(consulta, query);
        setFiltroTipoGrupo(consulta, query);
        setFiltroEstado(consulta, query);
        setFiltroMatriculaResponsavel(consulta, query);
        return query.list();
    }

    private void setFiltroMatriz(ConsultaARCDesignacaoVO consulta, Query query) {
        query.setParameter("pkMatriz", consulta.getMatriz().getPk());
    }

    private void setFiltroNomeAtividade(ConsultaARCDesignacaoVO consulta, Query query) {
        if (consulta.getNomeAtividade() != null) {
            query.setString(NOME_ATIVIDADE, consulta.getNomeAtividade());
        }
    }

    private void setFiltroNomeGrupo(ConsultaARCDesignacaoVO consulta, Query query) {
        if (consulta.getNomeGrupoRiscoControle() != null) {
            query.setString(NOME_ABREVIADO, consulta.getNomeGrupoRiscoControle());
        }
    }

    private void setFiltroTipoGrupo(ConsultaARCDesignacaoVO consulta, Query query) {
        if (consulta.getTipoGrupo() != null) {
            query.setParameter(TIPO_GRUPO, consulta.getTipoGrupo());
        }
    }

    private void setFiltroEstado(ConsultaARCDesignacaoVO consulta, Query query) {
        if (consulta.getEstadoARC() == null) {
            query.setParameterList("estados", consulta.getListaEstados());
        } else {
            query.setParameter(PROP_ESTADO, consulta.getEstadoARC());
        }
    }

    private void setFiltroMatriculaResponsavel(ConsultaARCDesignacaoVO consulta, Query query) {
        if (consulta.getServidor() != null) {
            query.setString(PARAMETRO_MATRICULA_SERVIDOR, consulta.getServidor().getMatricula());
        }
    }

    private void adicionarFiltroNomeAtividade(ConsultaARCDesignacaoVO consulta, StringBuilder hql) {
        if (consulta.getNomeAtividade() != null) {
            hql.append("and atividade.nome = :nomeAtividade ");
        }
    }

    private void adicionarFiltroNomeGrupo(ConsultaARCDesignacaoVO consulta, StringBuilder hql, boolean isArcExterno) {
        if (consulta.getNomeGrupoRiscoControle() != null) {
            if(isArcExterno){
                hql.append(" and arcExterno.parametroGrupoRiscoControle.nomeAbreviado = :nomeAbreviado ");
            }else
            {
                hql.append("and parametroRC.nomeAbreviado = :nomeAbreviado ");
            }
        }
    }

    private void adicionarFiltroTipoGrupo(ConsultaARCDesignacaoVO consulta, StringBuilder hql) {
        if (consulta.getTipoGrupo() != null) {
            hql.append(AND_ARC_TIPO_TIPO_GRUPO);
        }
    }

    private void adicionarFiltroMatriculaResponsavel(ConsultaARCDesignacaoVO consulta, StringBuilder hql) {
        if (consulta.getServidor() != null) {
            hql.append("and designacao.matriculaServidor = :matriculaServidor");
        }
    }

    private void adicionarFiltroEstado(ConsultaARCDesignacaoVO consulta, StringBuilder hql) {
        if (consulta.getEstadoARC() == null) {
            hql.append("and arc.estado in (:estados) ");
        } else {
            hql.append("and arc.estado = (:estado) ");
        }
    }

    public List<ArcResumidoVO> consultaPainelArcDesignados() {
        UsuarioAplicacao usuario = usuarioAplicacao();
        List<ArcResumidoVO> arcsDesignados = new ArrayList<ArcResumidoVO>();
        arcsDesignados.addAll(consultarArcsDesignadosMatriz(usuario));
        arcsDesignados.addAll(consultarArcsDesignadosExterno(usuario));
        return arcsDesignados;
    }

    private StringBuilder obterSelectConsultaARCsResumidos() {
        StringBuilder hql = new StringBuilder();
        hql.append(SELECT_DISTINCT_ARC_DESCRICAO_VO);
        hql.append("entidade.nome, atividade.nome, celula.parametroGrupoRiscoControle.nomeAbreviado, ");
        hql.append("arc.tipo, arc.estado, atividade.pk, matriz.pk) ");
        return hql;
    }
    
    private void addFromConsultaArcsExternos(StringBuilder hql) {
        hql.append("from AvaliacaoRiscoControle arc left join arc.designacao as designacao ");
        hql.append("inner join arc.avaliacaoRiscoControleExterno as arcExterno ");
        hql.append("inner join arcExterno.ciclo as ciclo inner join ciclo.estadoCiclo as estadoCiclo ");
        hql.append("inner join ciclo.matriz as matriz where ");
        hql.append("matriz.numeroFatorRelevanciaAE is not null and matriz.numeroFatorRelevanciaAE > 0 ");
    }

    private void addFromConsultaArcsResumidosExternos(StringBuilder hql) {
        hql.append("from AvaliacaoRiscoControle arc left join arc.designacao as designacao ");
        hql.append("inner join arc.avaliacaoRiscoControleExterno as arcExterno ");
        hql.append("inner join arcExterno.ciclo as ciclo inner join ciclo.estadoCiclo as estadoCiclo ");
        hql.append("inner join ciclo.entidadeSupervisionavel as entidade  ");
        hql.append("inner join ciclo.matriz as matriz where ");
        hql.append("matriz.numeroFatorRelevanciaAE is not null and matriz.numeroFatorRelevanciaAE > 0 ");
    }
    
    @SuppressWarnings(UNCHECKED)
    private List<ArcResumidoVO> consultarArcsDesignadosExterno(UsuarioAplicacao usuario) {
        StringBuilder hql = new StringBuilder();
        hql.append("select distinct new br.gov.bcb.sisaps.src.vo.ArcResumidoVO(");
        hql.append("arc.pk, entidade.nome, arcExterno.parametroGrupoRiscoControle.nomeAbreviado, ");
        hql.append("arc.tipo, arc.estado, matriz.pk) ");
        addFromConsultaArcsResumidosExternos(hql);
        hql.append(AND_ARC_ESTADO_IN_ESTADOS_ARC);
        hql.append("and estadoCiclo.estado = :estadoCiclo ");
        hql.append(AND_DESIGNACAO_MATRICULA_SERVIDOR_MATRICULA_SERVIDOR);
        hql.append("order by arcExterno.parametroGrupoRiscoControle.nomeAbreviado asc, arc.tipo asc");
        Query query = getCurrentSession().createQuery(hql.toString());
        query.setParameterList(PARAMETRO_ESTADOS_ARC, EstadoARCEnum.listaEstadosDesignadoEdicao());
        query.setParameter(ESTADO_CICLO, EstadoCicloEnum.EM_ANDAMENTO);
        query.setString(PARAMETRO_MATRICULA_SERVIDOR, usuario.getMatricula());
        return query.list();
    }

    @SuppressWarnings(UNCHECKED)
    private List<ArcResumidoVO> consultarArcsDesignadosMatriz(UsuarioAplicacao usuario) {
        StringBuilder hql = new StringBuilder();
        hql.append(obterSelectConsultaARCsResumidos());
        hql.append("from AvaliacaoRiscoControle arc inner join arc.designacao as designacao, ");
        hql.append(consultaArcsDesignadosDelegados());
        hql.append(AND_DESIGNACAO_MATRICULA_SERVIDOR_MATRICULA_SERVIDOR);
        ordenarConsultaPainel(hql);
        Query query = getCurrentSession().createQuery(hql.toString());
        setFiltrosConsultaARCsDesignadosDelegados(query, EstadoARCEnum.listaEstadosDesignadoEdicao());
        query.setString(PARAMETRO_MATRICULA_SERVIDOR, usuario.getMatricula());
        return query.list();
    }

    private void ordenarConsultaPainel(StringBuilder hql) {
        hql.append(" order by atividade.nome asc, "
                + "  celula.parametroGrupoRiscoControle.nomeAbreviado asc, arc.tipo asc ");
    }

    @SuppressWarnings(UNCHECKED)
    public List<ArcResumidoVO> consultarArcsDelegadosMatriz(UsuarioAplicacao usuario) {
        StringBuilder hql = new StringBuilder();
        hql.append(obterSelectConsultaARCsResumidos());
        hql.append("from AvaliacaoRiscoControle arc inner join arc.delegacao as delegacao, ");
        hql.append(consultaArcsDesignadosDelegados());
        hql.append("and delegacao.matriculaServidor = :matriculaServidor ");
        ordenarConsultaPainel(hql);
        Query query = getCurrentSession().createQuery(hql.toString());
        setFiltrosConsultaARCsDesignadosDelegados(query, EstadoARCEnum.listaEstadosDelegadoAnalise());
        query.setString(PARAMETRO_MATRICULA_SERVIDOR, usuario.getMatricula());
        return query.list();
    }

    private void setFiltrosConsultaARCsDesignadosDelegados(Query query, List<EstadoARCEnum> listaEstados) {
        query.setParameter(PARAMETRO_ESTADO_MATRIZ, EstadoMatrizEnum.VIGENTE);
        query.setParameterList(PARAMETRO_ESTADOS_ARC, listaEstados);
        query.setParameter(ESTADO_CICLO, EstadoCicloEnum.EM_ANDAMENTO);
    }

    private StringBuilder consultaArcsDesignadosDelegados() {
        StringBuilder hql = new StringBuilder();
        hql.append("CelulaRiscoControle celula inner join celula.atividade as atividade inner join atividade.matriz as matriz ");
        hql.append(INNER_JOIN_MATRIZ_CICLO_AS_CICLO_INNER_JOIN_CICLO_ENTIDADE_SUPERVISIONAVEL_AS_ENTIDADE);
        hql.append("inner join ciclo.estadoCiclo as estadoCiclo ");
        hql.append(WHERE_ARC_PK_CELULA_ARC_RISCO_PK_OR_ARC_PK_CELULA_ARC_CONTROLE_PK);
        hql.append("and ciclo.matriz.pk = matriz.pk and matriz.estadoMatriz = :estadoMatriz ");
        hql.append("and arc.estado in (:estadosARC)  and estadoCiclo.estado = :estadoCiclo ");
        return hql;
    }
    
    private Criteria criteriaAvaliacaoRiscoControle() {
        return getCurrentSession().createCriteria(AvaliacaoRiscoControle.class, "arc");
    }

    @SuppressWarnings(UNCHECKED)
    public List<ArcResumidoVO> consultaPainelSupervisorEmAnalise(UsuarioAplicacao usuario) {
        StringBuilder hql = new StringBuilder();
        hql.append(obterSelectConsultaARCsResumidos());
        hql.append("from AvaliacaoRiscoControle arc left join arc.delegacao as delegacao, ");
        hql.append("CelulaRiscoControle celula ");
        hql.append(INNER_JOIN_CELULA_ATIVIDADE_AS_ATIVIDADE_INNER_JOIN_ATIVIDADE_MATRIZ_AS_MATRIZ);
        hql.append(INNER_JOIN_MATRIZ_CICLO_AS_CICLO_INNER_JOIN_CICLO_ENTIDADE_SUPERVISIONAVEL_AS_ENTIDADE);
        hql.append(WHERE_ARC_PK_CELULA_ARC_RISCO_PK_OR_ARC_PK_CELULA_ARC_CONTROLE_PK_AND);
        hql.append("ciclo.matriz.pk = matriz.pk and ");
        hql.append("( entidade.localizacao = :localizacao or ");
        hql.append("(not entidade.localizacao = :localizacao and delegacao.matriculaServidor = :matriculaServidor)) ");
        hql.append("and arc.estado in (:estadosARC) and ");
        hql.append("(delegacao.avaliacaoRiscoControle is null or delegacao.matriculaServidor = :matriculaServidor) ");
        Query query = getCurrentSession().createQuery(hql.toString());
        query.setString("localizacao", usuario.getServidorVO().getLocalizacaoAtual(PerfilAcessoEnum.SUPERVISOR));
        query.setString(PARAMETRO_MATRICULA_SERVIDOR, usuario.getMatricula());
        query.setParameterList(PARAMETRO_ESTADOS_ARC, EstadoAQTEnum.listaEstadosAnaliseSupervisor());
        return query.list();
    }

    @SuppressWarnings(UNCHECKED)
    public List<AvaliacaoRiscoControle> buscarArcDaMatrizPorGrupo(Matriz matriz, ParametroGrupoRiscoControle grupo,
            List<VersaoPerfilRisco> versoesPerfilRiscoARCs, TipoGrupoEnum tipo) {
        Criteria criteria = criteriaAvaliacaoRiscoControle();
        criteria.createAlias(PROP_PARAMETRO_GRUPO_RISCO_CONTROLE, PROP_PARAMETRO_GRUPO_RISCO_CONTROLE);

        criteria.add(Restrictions.eq(PROP_PARAMETRO_GRUPO_RISCO_CONTROLE, grupo));
        criteria.add(Restrictions.eq(PROP_TIPO, tipo));

        Criteria atividadesCriteria = criteria.createCriteria(PROP_ATIVIDADES, ATV1);
        atividadesCriteria.add(Restrictions.eq(MATRIZ_PK, matriz.getPk()));

        adicionarFiltroArcRecenteOUVersaoesPerfilRisco(null, versoesPerfilRiscoARCs, criteria);
        return criteria.list();
    }

    private void adicionarFiltroArcRecenteOUVersaoesPerfilRisco(ParametroGrupoRiscoControle grupo,
            List<VersaoPerfilRisco> versoesPerfilRiscoARCs, Criteria criteria) {
        if (versoesPerfilRiscoARCs == null || versoesPerfilRiscoARCs.isEmpty()) {
            DetachedCriteria subArcsCriteria = DetachedCriteria.forClass(AvaliacaoRiscoControle.class, "arc2");
            if (grupo != null) {

                subArcsCriteria.add(Restrictions.eq(PROP_PARAMETRO_GRUPO_RISCO_CONTROLE, grupo));
            }
            DetachedCriteria subAtividadesCriteria = subArcsCriteria.createCriteria(PROP_ATIVIDADES, "atv2");
            subAtividadesCriteria.add(Property.forName("atv1.pk").eqProperty("atv2.pk"));
            subArcsCriteria.add(Property.forName("arc2.ultimaAtualizacao").gtProperty("arc.ultimaAtualizacao"));

            criteria.add(Subqueries.notExists(subArcsCriteria.setProjection(Projections.property("arc2.pk"))));
        } else {
            adicionarFiltroVersoesPerfilRisco(versoesPerfilRiscoARCs, criteria);
        }
    }

    private void adicionarFiltroVersoesPerfilRisco(List<VersaoPerfilRisco> versoesPerfilRiscoARCs, Criteria criteria) {
        if (CollectionUtils.isNotEmpty(versoesPerfilRiscoARCs)) {
            criteria.add(Restrictions.in(VERSAO_PERFIL_RISCO, versoesPerfilRiscoARCs));
        }
    }

    @SuppressWarnings(UNCHECKED)
    public List<AvaliacaoRiscoControle> buscarArcsConcluidoDaMatriz(List<VersaoPerfilRisco> versoesPerfilRiscoARCs) {
        Criteria criteria = criteriaAvaliacaoRiscoControle();
        criteria.add(Restrictions.eq(PROP_ESTADO, EstadoARCEnum.CONCLUIDO));
        adicionarFiltroVersoesPerfilRisco(versoesPerfilRiscoARCs, criteria);
        return criteria.list();
    }

    @SuppressWarnings(UNCHECKED)
    public PerfilRisco buscarUltimoPerfilRisco(AvaliacaoRiscoControle avaliacaoRiscoControle) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.createAlias(VERSAO_PERFIL_RISCO, "versaoPerfil");
        criteria.add(Restrictions.eq("versaoPerfil.pk", avaliacaoRiscoControle.getVersaoPerfilRisco().getPk()));
        criteria.setProjection(Projections.property(VERSAO_PERFIL_RISCO));
        VersaoPerfilRisco versao = (VersaoPerfilRisco) criteria.uniqueResult();
        List<PerfilRisco> perfisRisco = versao.getPerfisRisco();
        ComparatorChain cc = new ComparatorChain();
        cc.addComparator(new BeanComparator("dataCriacao"));
        cc.setReverseSort(0);
        Collections.sort(perfisRisco, cc);
        return perfisRisco.get(0);
    }

    @SuppressWarnings(UNCHECKED)
    public List<AvaliacaoRiscoControle> buscarTodosArcsDoPerfilRisco(Ciclo ciclo) {
        PerfilRisco perfilAtual = PerfilRiscoMediator.get().obterPerfilRiscoAtual(ciclo.getPk());
        perfilAtual.getVersoesPerfilRisco();

        Criteria criteria = criteriaAvaliacaoRiscoControle();
        adicionarFiltroVersoesPerfilRisco(perfilAtual.getVersoesPerfilRisco(), criteria);
        return criteria.list();
    }

    private StringBuilder obterSelectConsulta() {
        StringBuilder hql = new StringBuilder();
        hql.append(SELECT_DISTINCT_NEW_BR_GOV_BCB_SISAPS_SRC_VO_AVALIACAO_RISCO_CONTROLE_VO_ARC_PK);
        hql.append("arc.estado, arc.tipo, parametroGrupoRiscoControle, ");
        hql.append("matriz, atividade, arc.ultimaAtualizacao,");
        hql.append(" parametroGrupoRiscoControle.ordem, atividade.tipoAtividade, notaSupervisor, vigente.valorNota ) ");
        return hql;
    }

    @SuppressWarnings(UNCHECKED)
    public List<AvaliacaoRiscoControleVO> consultaArcPerfil(Matriz matrizvigente,
            List<VersaoPerfilRisco> versoesPerfilRiscoCiclo) {
        StringBuilder hql = new StringBuilder();
        hql.append(obterSelectConsulta());
        hql.append("from AvaliacaoRiscoControle arc , CelulaRiscoControle celula ");
        hql.append("inner join arc.avaliacaoRiscoControleVigente as vigente ");
        hql.append("left join vigente.notaSupervisor as notaSupervisor ");
        hql.append("inner join celula.parametroGrupoRiscoControle as parametroGrupoRiscoControle ");
        hql.append(INNER_JOIN_CELULA_ATIVIDADE_AS_ATIVIDADE_INNER_JOIN_ATIVIDADE_MATRIZ_AS_MATRIZ);
        hql.append(INNER_JOIN_MATRIZ_CICLO_AS_CICLO_INNER_JOIN_CICLO_ENTIDADE_SUPERVISIONAVEL_AS_ENTIDADE);
        hql.append("where (arc.pk = celula.arcRisco.pk or arc.pk = celula.arcControle.pk) and  ");
        hql.append("arc.versaoPerfilRisco  in (:versaoPerfilRisco) and matriz.pk = :matriz ");
        hql.append("order by atividade.tipoAtividade desc ,  parametroGrupoRiscoControle.ordem asc ");
        Query query = getCurrentSession().createQuery(hql.toString());
        query.setParameterList(VERSAO_PERFIL_RISCO, versoesPerfilRiscoCiclo);
        query.setInteger("matriz", matrizvigente.getPk());
        return query.list();
    }

    @SuppressWarnings(UNCHECKED)
    public List<AvaliacaoRiscoControleVO> consultaPainelArcsHistorico(ConsultaAvaliacaoRiscoControleVO consulta) {
        UsuarioAplicacao usuario = usuarioAplicacao();

        //arc externo preenchido
        StringBuilder hqlOperadorPreenchidoArcExt = new StringBuilder();
        obterSelectHistoricoARCsExt(hqlOperadorPreenchidoArcExt);
        sqlCodigoOperadorArcExt(hqlOperadorPreenchidoArcExt, "and (lower(arc.operadorPreenchido) = :operadorPreenchido) ");
        montarFiltrosHistorico(consulta, hqlOperadorPreenchidoArcExt, true);
        Query queryOperadorPreenchidoArcExt = getCurrentSession().createQuery(hqlOperadorPreenchidoArcExt.toString());
        estadosARCs(consulta, queryOperadorPreenchidoArcExt);
        filtrosHistoricoARC(consulta, usuario, queryOperadorPreenchidoArcExt, true);
        queryOperadorPreenchidoArcExt.setString("operadorPreenchido", usuario.getLogin().toLowerCase());
        
        // arc externo analisado
        StringBuilder hqlOperadorAnaliseArcExt = new StringBuilder();
        obterSelectHistoricoARCsExt(hqlOperadorAnaliseArcExt);
        sqlCodigoOperadorArcExt(hqlOperadorAnaliseArcExt, "and (lower(arc.operadorAnalise) = :operadorAnalisado) ");
        montarFiltrosHistorico(consulta, hqlOperadorAnaliseArcExt, true);
        Query queryOperadorAnaliseArcExt = getCurrentSession().createQuery(hqlOperadorAnaliseArcExt.toString());
        estadosARCs(consulta, queryOperadorAnaliseArcExt);
        filtrosHistoricoARC(consulta, usuario, queryOperadorAnaliseArcExt, true);
        queryOperadorAnaliseArcExt.setString("operadorAnalisado", usuario.getLogin().toLowerCase());
        
        //arc preenchido
        StringBuilder hqlOperadorPreenchido = new StringBuilder();
        obterSelectHistoricoARCs(hqlOperadorPreenchido);
        sqlCodigoOperador(hqlOperadorPreenchido, "and (lower(arc.operadorPreenchido) = :operadorPreenchido) ");
        montarFiltrosHistorico(consulta, hqlOperadorPreenchido, false);
        Query queryOperadorPreenchido = getCurrentSession().createQuery(hqlOperadorPreenchido.toString());
        estadosARCs(consulta, queryOperadorPreenchido);
        filtrosHistoricoARC(consulta, usuario, queryOperadorPreenchido, false);
        queryOperadorPreenchido.setString("operadorPreenchido", usuario.getLogin().toLowerCase());

        //arc analisado
        StringBuilder hqlOperadorAnalise = new StringBuilder();
        obterSelectHistoricoARCs(hqlOperadorAnalise);
        sqlCodigoOperador(hqlOperadorAnalise, "and (lower(arc.operadorAnalise) = :operadorAnalisado) ");
        montarFiltrosHistorico(consulta, hqlOperadorAnalise, false);
        Query queryOperadorAnalise = getCurrentSession().createQuery(hqlOperadorAnalise.toString());
        estadosARCs(consulta, queryOperadorAnalise);
        filtrosHistoricoARC(consulta, usuario, queryOperadorAnalise, false);
        queryOperadorAnalise.setString("operadorAnalisado", usuario.getLogin().toLowerCase());

        //arc 
        List<AvaliacaoRiscoControleVO> listaArcsPreenchidos = queryOperadorPreenchido.list();
        preencherAcao(EstadoARCEnum.PREENCHIDO, listaArcsPreenchidos);

        List<AvaliacaoRiscoControleVO> listaArcsAnalisados = queryOperadorAnalise.list();
        preencherAcao(EstadoARCEnum.ANALISADO, listaArcsAnalisados);

        //ARC externo
        List<AvaliacaoRiscoControleVO> listaArcsExtEPreenchidos = queryOperadorPreenchidoArcExt.list();
        preencherAcao(EstadoARCEnum.PREENCHIDO, listaArcsExtEPreenchidos);

        List<AvaliacaoRiscoControleVO> listaArcsExtAnalisados = queryOperadorAnaliseArcExt.list();
        preencherAcao(EstadoARCEnum.ANALISADO, listaArcsExtAnalisados);
        
        List<AvaliacaoRiscoControleVO> listaARCs = new ArrayList<AvaliacaoRiscoControleVO>();
        setLista(consulta, listaArcsPreenchidos, listaArcsAnalisados, listaARCs);
        Collections.sort(listaARCs, new ComparadorVersaoARC());
        setLista(consulta, listaArcsExtEPreenchidos, listaArcsExtAnalisados, listaARCs);
        
        return listaARCs;
    }

    private void setLista(ConsultaAvaliacaoRiscoControleVO consulta,
            List<AvaliacaoRiscoControleVO> listaArcsPreenchidos,
            List<AvaliacaoRiscoControleVO> listaArcsAnalisados, List<AvaliacaoRiscoControleVO> listaARCs) {
        if (consulta.isAcao()) {
            if (EstadoARCEnum.PREENCHIDO.equals(consulta.getEstadoARC())) {
                listaARCs.addAll(listaArcsPreenchidos);
            } else {
                listaARCs.addAll(listaArcsAnalisados);
            }
        } else {
            listaARCs.addAll(listaArcsPreenchidos);
            listaARCs.addAll(listaArcsAnalisados);
        }
    }

    private void estadosARCs(ConsultaAvaliacaoRiscoControleVO consulta, Query query) {
        query.setParameterList(
                PARAMETRO_ESTADOS_ARC,
                consulta.isTeste() ? EstadoARCEnum.listaEstados() : EstadoARCEnum
                        .listaEstadosPreenchidoAnaliseConcluido());
    }

    private void filtrosHistoricoARC(ConsultaAvaliacaoRiscoControleVO consulta, UsuarioAplicacao usuario, Query query, boolean isArcExterno) {
        setFiltroNomeEntidadeSupervisionavel(consulta, query);
        setFiltroNomeAtividade(consulta, query, isArcExterno);
        setFiltroTipoGrupo(consulta, query);
        setFiltroEstadoANEF(consulta, query, usuario);
        setFiltroNomeGrupo(consulta, query);
    }

    private void adicionarFiltroNomeGrupo(ConsultaAvaliacaoRiscoControleVO consulta, StringBuilder hql, boolean isArcExterno) {
        if (consulta.getNomeGrupoRiscoControle() != null) {
            if (isArcExterno) {
                hql.append("and parametro.nomeAbreviado = :nomeAbreviado ");
            } else {
                hql.append("and celula.parametroGrupoRiscoControle.nomeAbreviado = :nomeAbreviado ");
            }
        }
    }

    private void montarFiltrosHistorico(ConsultaAvaliacaoRiscoControleVO consulta, StringBuilder hql, boolean isArcExterno) {
        if (consulta.getNomeES() != null) {
            if (isArcExterno) {
                hql.append(" and lower(entidade.nome) like lower(:nomeES)");
            } else {
                hql.append(" and lower(atividade.matriz.ciclo.entidadeSupervisionavel.nome) like lower(:nomeES) ");
            }
        }

        if (consulta.getNomeAtividade() != null) {
            if (isArcExterno) {
                hql.append(" and parametro.nomeAbreviado = :nomeAtividade    ");
            } else {
                hql.append(" and ((lower(atividade.nome) like lower(:nomeAtividade) "
                        + "and atividade.parametroTipoAtividadeNegocio is null)"
                        + " or lower(atividade.nome) like lower(:nomeAtividade))");
            }
        }

        if (consulta.getEstadoARC() != null) {
            if (EstadoARCEnum.PREENCHIDO.equals(consulta.getEstadoARC())) {
                hql.append("and (lower(arc.operadorPreenchido) = :operPreenchido ) ");
            } else if (EstadoARCEnum.ANALISADO.equals(consulta.getEstadoARC())) {
                hql.append("and (lower(arc.operadorAnalise) = :operAnalise ) ");
            }
        } else {
            consulta.setAcao(false);
        }

        adicionarFiltroTipoGrupo(consulta, hql);
        adicionarFiltroNomeGrupo(consulta, hql, isArcExterno);
    }

    private void setFiltroEstadoANEF(ConsultaAvaliacaoRiscoControleVO consulta, Query query, UsuarioAplicacao usuario) {
        if (consulta.getEstadoARC() != null) {
            consulta.setAcao(true);
            if (EstadoARCEnum.PREENCHIDO.equals(consulta.getEstadoARC())) {
                query.setString("operPreenchido", usuario.getLogin());
            } else if (EstadoARCEnum.ANALISADO.equals(consulta.getEstadoARC())) {
                query.setString("operAnalise", usuario.getLogin());
            }
        }
    }

    private void adicionarFiltroTipoGrupo(ConsultaAvaliacaoRiscoControleVO consulta, StringBuilder hql) {
        if (consulta.getTipoGrupo() != null) {
            hql.append(AND_ARC_TIPO_TIPO_GRUPO);
        }
    }

    private void setFiltroNomeAtividade(ConsultaAvaliacaoRiscoControleVO consulta, Query query, boolean isArcExterno) {
        if (consulta.getNomeAtividade() != null) {
            if(isArcExterno){
                query.setString(NOME_ATIVIDADE, PORCENTAGEM + consulta.getNomeAtividade() + PORCENTAGEM);
            }else
            {
                query.setString(NOME_ATIVIDADE, PORCENTAGEM + consulta.getNomeAtividade() + PORCENTAGEM);
            }
        }
    }

    private void setFiltroNomeEntidadeSupervisionavel(ConsultaAvaliacaoRiscoControleVO consulta, Query query) {
        if (consulta.getNomeES() != null) {
            query.setString("nomeES", PORCENTAGEM + consulta.getNomeES() + PORCENTAGEM);
        }
    }

    private void setFiltroNomeGrupo(ConsultaAvaliacaoRiscoControleVO consulta, Query query) {
        if (consulta.getNomeGrupoRiscoControle() != null) {
            query.setString(NOME_ABREVIADO, consulta.getNomeGrupoRiscoControle());
        }
    }

    private void preencherAcao(EstadoARCEnum estado, List<AvaliacaoRiscoControleVO> listaARCs) {
        for (AvaliacaoRiscoControleVO arc : listaARCs) {
            arc.setAcao(estado.getDescricao());
            preencherVersao(arc);
            preencherNomeEs(arc);
        }
    }

    private void preencherNomeEs(AvaliacaoRiscoControleVO arcVo) {
        if (TipoGrupoEnum.EXTERNO.equals(arcVo.getTipo())) {
            arcVo.setNomeEs(arcVo.getMatrizVigente().getCiclo().getEntidadeSupervisionavel().getNome());
        } else {
            arcVo.setNomeEs(arcVo.getAtividade().getMatriz().getCiclo().getEntidadeSupervisionavel().getNome());
        }
    }

    private void preencherVersao(AvaliacaoRiscoControleVO arcVo) {
        if (EstadoARCEnum.CONCLUIDO.equals(arcVo.getEstado())) {
            if (TipoGrupoEnum.EXTERNO.equals(arcVo.getTipo())) {
                if (arcVo.getUltimaAtualizacao() != null) {
                    arcVo.setDataConclusao(arcVo.getUltimaAtualizacao());
                }
            }
            if (arcVo.getDataConclusao() != null) {
                arcVo.setVersao(arcVo.getDataConclusao().toString(Constantes.FORMATO_DATA_COM_BARRAS));
            }
        } else {
            arcVo.setVersao(arcVo.getEstado().getDescricao());
        }
    }

    private void setFiltroTipoGrupo(ConsultaAvaliacaoRiscoControleVO consulta, Query query) {
        if (consulta.getTipoGrupo() != null) {
            query.setParameter(TIPO_GRUPO, consulta.getTipoGrupo());
        }
    }
    
    private void sqlCodigoOperador(StringBuilder hql, String operador) {
        hql.append("from AvaliacaoRiscoControle arc ,");
        hql.append(CELULA_RISCO_CONTROLE_CELULA_INNER_JOIN_CELULA_ATIVIDADE_AS_ATIVIDADE);
        hql.append(INNER_JOIN_CELULA_PARAMETRO_GRUPO_RISCO_CONTROLE_AS_PARAMETRO_RC);
        hql.append("left join atividade.matriz as mat1 ");
        hql.append("left join atividade.unidade as und ");
        hql.append("left join und.matriz as mat2 left join mat1.ciclo as cic1 left join mat2.ciclo as cic2 ");
        hql.append("where (arc.pk = celula.arcRisco.pk ");
        hql.append("or arc.pk = celula.arcControle.pk) ");
        hql.append("and celula.ultimaAtualizacao in ");
        hql.append("(select max(crc2.ultimaAtualizacao) from CelulaRiscoControle crc2 ");
        hql.append("where crc2.versaoPerfilRisco is not null ");
        hql.append("group by crc2.arcRisco.pk, crc2.arcControle.pk) ");
        hql.append(operador);
        hql.append(AND_ARC_ESTADO_IN_ESTADOS_ARC);
        hql.append("and not exists (select arc3.pk, arc3.dataConclusao from AvaliacaoRiscoControle as arc3, ");
        hql.append("CelulaRiscoControle celula2 inner join celula2.parametroGrupoRiscoControle as parametroRC2 ");
        hql.append("inner join celula2.atividade as atividade2 ");
        hql.append("left join atividade2.matriz as matriz1 ");
        hql.append("left join atividade2.unidade as und2 ");
        hql.append("left join und2.matriz as matriz2 left join matriz1.ciclo as ciclo1 left join matriz2.ciclo as ciclo2 ");
        hql.append("where (arc3.pk = celula2.arcRisco.pk or arc3.pk = celula2.arcControle.pk) ");
        hql.append("and (cic1.pk = ciclo1.pk or cic2.pk = ciclo2.pk) ");
        hql.append("and parametroRC.pk = parametroRC2.pk and arc.tipo = arc3.tipo and arc3.dataConclusao is not null ");
        hql.append("and arc3.dataConclusao >= arc.dataConclusao and arc3.pk > arc.pk ");
        hql.append("and day(arc3.dataConclusao) = day(arc.dataConclusao) ");
        hql.append("and month(arc3.dataConclusao) = month(arc.dataConclusao) ");
        hql.append("and year(arc3.dataConclusao) = year(arc.dataConclusao)) ");
        hql.append("and arc.pk not in( " + "select arc56.avaliacaoRiscoControleVigente.pk "
                + "from AvaliacaoRiscoControle arc56 , CelulaRiscoControle celula56 "
                + "inner join celula56.atividade as atividade56 " + "left join atividade56.matriz as mat56 "
                + "left join mat56.ciclo as ciclo56 "
                + "where (arc56.pk = celula56.arcRisco.pk  or arc56.pk = celula56.arcControle.pk) "
                + "and arc56.avaliacaoRiscoControleVigente in( "
                + "select arc57.pk from AvaliacaoRiscoControle arc57 , CelulaRiscoControle as celula57 "
                + "inner join  celula57.atividade as atividade57 " + "left join atividade57.matriz as mat57 "
                + "left join mat57.ciclo as ciclo57 "
                + "where (arc57.pk = celula57.arcRisco.pk  or arc57.pk = celula57.arcControle.pk) "
                + "and ciclo57.pk <> ciclo56.pk  ) )");
    }

    private void obterSelectHistoricoARCs(StringBuilder hql) {
        hql.append("select distinct new br.gov.bcb.sisaps.src.vo.AvaliacaoRiscoControleVO(arc.pk, ");
        hql.append("arc.estado, arc.tipo, celula.parametroGrupoRiscoControle, atividade, ");
        hql.append("arc.dataConclusao) ");
    }
    
    private void obterSelectHistoricoARCsExt(StringBuilder hql) {
        hql.append("select distinct new br.gov.bcb.sisaps.src.vo.AvaliacaoRiscoControleVO(");
        hql.append("arc.pk, arc.estado, arc.tipo, arcExterno.parametroGrupoRiscoControle, ");
        hql.append("ciclo.matriz, arc.dataConclusao) ");
    }
    

    private void montarSelectArcsRascunhoHistorico(StringBuilder hql) {
        hql.append("Select arc from AvaliacaoRiscoControle arc "
                + "where  arc.avaliacaoRiscoControleVigente.pk = :pkAtual");

    }

    @SuppressWarnings(UNCHECKED)
    public AvaliacaoRiscoControle buscarRascunhoPorArcVigente(AvaliacaoRiscoControle arcVigente) {

        StringBuilder hql = new StringBuilder();
        montarSelectArcsRascunhoHistorico(hql);

        Query query = getCurrentSession().createQuery(hql.toString());
        query.setParameter("pkAtual", arcVigente.getPk());
        List<AvaliacaoRiscoControle> lista = query.list();
        if (!lista.isEmpty()) {
            return lista.get(0);
        }

        return null;

    }
    
    public Ciclo buscarCicloARC(AvaliacaoRiscoControle avaliacaoRiscoControle) {
        StringBuilder hql = new StringBuilder();
        hql.append("select ciclo ");
        hql.append("from AvaliacaoRiscoControle arc, CelulaRiscoControle celula ");
        hql.append(INNER_JOIN_CELULA_ATIVIDADE_AS_ATIVIDADE_INNER_JOIN_ATIVIDADE_MATRIZ_AS_MATRIZ);
        hql.append("inner join matriz.ciclo as ciclo ");
        hql.append(WHERE_ARC_PK_CELULA_ARC_RISCO_PK_OR_ARC_PK_CELULA_ARC_CONTROLE_PK_AND);
        hql.append("arc.pk = :pk ");
        Query query = getCurrentSession().createQuery(hql.toString());
        query.setInteger(PK, avaliacaoRiscoControle.getPk());
        return (Ciclo) query.uniqueResult();
    }

    public List<ArcResumidoVO> consultaPainelArcDelegados() {
        UsuarioAplicacao usuario = usuarioAplicacao();
        List<ArcResumidoVO> arcsDesignados = new ArrayList<ArcResumidoVO>();
        arcsDesignados.addAll(consultarArcsDelegadosMatriz(usuario));
        arcsDesignados.addAll(consultarArcsDelegadosExterno(usuario));
        return arcsDesignados;
    }
    
    @SuppressWarnings(UNCHECKED)
    private List<ArcResumidoVO> consultarArcsDelegadosExterno(UsuarioAplicacao usuario) {
        StringBuilder hql = new StringBuilder();
        hql.append("select distinct new br.gov.bcb.sisaps.src.vo.ArcResumidoVO(");
        hql.append("arc.pk, entidade.nome, arcExterno.parametroGrupoRiscoControle.nomeAbreviado, ");
        hql.append("arc.tipo, arc.estado, matriz.pk) ");
        hql.append("from AvaliacaoRiscoControle arc inner join arc.delegacao as delegacao ");
        hql.append("inner join arc.avaliacaoRiscoControleExterno as arcExterno ");
        hql.append("inner join arcExterno.ciclo as ciclo inner join ciclo.estadoCiclo as estadoCiclo ");
        hql.append("inner join ciclo.entidadeSupervisionavel as entidade ");
        hql.append("inner join ciclo.matriz as matriz where ");
        hql.append("matriz.numeroFatorRelevanciaAE is not null and matriz.numeroFatorRelevanciaAE > 0 ");
        hql.append(AND_ARC_ESTADO_IN_ESTADOS_ARC);
        hql.append("and estadoCiclo.estado = :estadoCiclo ");
        hql.append("and delegacao.matriculaServidor = :matriculaServidor ");
        hql.append("order by arcExterno.parametroGrupoRiscoControle.nomeAbreviado asc, arc.tipo asc");
        Query query = getCurrentSession().createQuery(hql.toString());
        query.setParameterList(PARAMETRO_ESTADOS_ARC, EstadoARCEnum.listaEstadosDelegadoAnalise());
        query.setParameter(ESTADO_CICLO, EstadoCicloEnum.EM_ANDAMENTO);
        query.setString(PARAMETRO_MATRICULA_SERVIDOR, usuario.getMatricula());
        return query.list();
    }
    
    @SuppressWarnings("unchecked")
    private List<ArcResumidoVO> consultarArcsAnaliseExterno(UsuarioAplicacao usuario) {
        StringBuilder hql = new StringBuilder();
        hql.append("select distinct new br.gov.bcb.sisaps.src.vo.ArcResumidoVO(");
        hql.append("arc.pk, entidade.nome, arcExterno.parametroGrupoRiscoControle.nomeAbreviado, ");
        hql.append("arc.tipo, arc.estado, matriz.pk) ");
        hql.append("from AvaliacaoRiscoControle arc left join arc.delegacao as delegacao ");
        hql.append("inner join arc.avaliacaoRiscoControleExterno as arcExterno ");
        hql.append("inner join arcExterno.ciclo as ciclo inner join ciclo.estadoCiclo as estadoCiclo ");
        hql.append("inner join ciclo.matriz as matriz ");
        hql.append("inner join ciclo.entidadeSupervisionavel as entidade ");
        hql.append("where matriz.numeroFatorRelevanciaAE is not null and matriz.numeroFatorRelevanciaAE > 0 and ");
        hql.append("( entidade.localizacao = :localizacao or ");
        hql.append("(not entidade.localizacao = :localizacao and delegacao.matriculaServidor = :matriculaServidor)) ");
        hql.append("and arc.estado in (:estadosARC) and ");
        hql.append("(delegacao.avaliacaoRiscoControle is null or delegacao.matriculaServidor = :matriculaServidor) ");
        hql.append("order by arcExterno.parametroGrupoRiscoControle.nomeAbreviado asc, arc.tipo asc");
        Query query = getCurrentSession().createQuery(hql.toString());
        query.setString("localizacao", usuario.getServidorVO().getLocalizacaoAtual(PerfilAcessoEnum.SUPERVISOR));
        query.setString(PARAMETRO_MATRICULA_SERVIDOR, usuario.getMatricula());
        query.setParameterList(PARAMETRO_ESTADOS_ARC, EstadoAQTEnum.listaEstadosAnaliseSupervisor());
        return query.list();
    }

    public List<ArcResumidoVO> consultaPainelSupervisor() {
        UsuarioAplicacao usuario = usuarioAplicacao();
        List<ArcResumidoVO> arcsDesignados = new ArrayList<ArcResumidoVO>();
        arcsDesignados.addAll(consultaPainelSupervisorEmAnalise(usuario));
        arcsDesignados.addAll(consultarArcsAnaliseExterno(usuario));
        return arcsDesignados;
    }

    private void sqlCodigoOperadorArcExt(StringBuilder hql, String operador) {
        hql.append("from AvaliacaoRiscoControle arc ");
        hql.append("inner join arc.avaliacaoRiscoControleExterno as arcExterno ");
        hql.append("inner join arcExterno.parametroGrupoRiscoControle as parametro ");
        hql.append("inner join arcExterno.ciclo as ciclo ");
        hql.append("inner join ciclo.entidadeSupervisionavel as entidade ");
        hql.append("inner join ciclo.estadoCiclo as estadoCiclo ");
        hql.append("inner join ciclo.matriz as matriz ");
        hql.append("where matriz.numeroFatorRelevanciaAE is not null and matriz.numeroFatorRelevanciaAE > 0 ");
        hql.append("and arc.estado in (:estadosARC) ");
        hql.append(operador);
        hql.append("and not exists( ");
        hql.append("select arc2.pk, arc2.dataConclusao from AvaliacaoRiscoControle as arc2 ");
        hql.append("where arc2.pk > arc.pk and arc2.dataConclusao is not null ");
        hql.append("and arc2.dataConclusao >= arc.dataConclusao ");
        hql.append("and day(arc2.dataConclusao) = day(arc.dataConclusao) ");
        hql.append("and month(arc2.dataConclusao) = month(arc.dataConclusao) ");
        hql.append("and year(arc2.dataConclusao) = year(arc.dataConclusao)) ");

    }

    @SuppressWarnings(UNCHECKED)
    public List<AvaliacaoRiscoControle> buscarArcPorCiclo(Integer pkCiclo) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.createCriteria(VERSAO_PERFIL_RISCO, "versao");
        criteria.createCriteria("versao.perfisRisco", "perfil");
        criteria.createCriteria("perfil.ciclo", "ciclo");
        criteria.add(Restrictions.eq("ciclo.pk", pkCiclo));
        return criteria.list();
    }

    public AvaliacaoRiscoControle buscarArcExternoPorPerfilRisco(Integer pkPerfilRisco) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        Criteria versaoPerfilRiscoCriteria = criteria.createCriteria(VERSAO_PERFIL_RISCO);
        Criteria perfilRiscoCriteria = versaoPerfilRiscoCriteria.createCriteria("perfisRisco");
        criteria.add(Restrictions.eq(PROP_TIPO, TipoGrupoEnum.EXTERNO));
        perfilRiscoCriteria.add(Restrictions.eq(PK, pkPerfilRisco));
        return (AvaliacaoRiscoControle) criteria.uniqueResult();
    }

    private void retornoConsultaArcVO(StringBuilder hql) {
        hql.append("select distinct new br.gov.bcb.sisaps.src.vo.ArcNotasVO(");
        hql.append("arc.pk, arc.valorNota, notaSupervisor, ");
        hql.append("notaCorec, arcVigente.pk, arc.estado, ");
        hql.append("designacao.pk, delegacao.pk, arc.tipo) ");
        hql.append("from AvaliacaoRiscoControle arc ");
    }

    public ArcNotasVO consultarNotasArc(Integer arcPk) {
        StringBuilder hql = new StringBuilder();
        retornoConsultaArcVO(hql);
        hql.append("left join arc.avaliacaoRiscoControleVigente as arcVigente ");
        hql.append("left join arc.designacao as designacao ");
        hql.append("left join arc.delegacao as delegacao ");
        hql.append("left join arc.notaSupervisor as notaSupervisor ");
        hql.append("left join arc.notaCorec as notaCorec ");
        hql.append("where arc.pk = :pkArc ");
        Query query = getCurrentSession().createQuery(hql.toString());
        query.setParameter("pkArc", arcPk);
        return (ArcNotasVO) query.uniqueResult();
    }

    public ArcNotasVO consultarNotasArcExterno(Integer pkPerfilRisco) {
        StringBuilder hql = new StringBuilder();
        retornoConsultaArcVO(hql);
        hql.append("left join arc.avaliacaoRiscoControleVigente as arcVigente ");
        hql.append("left join arc.designacao as designacao ");
        hql.append("left join arc.delegacao as delegacao ");
        hql.append("left join arc.notaSupervisor as notaSupervisor ");
        hql.append("left join arc.notaCorec as notaCorec ");
        hql.append("inner join arc.versaoPerfilRisco as versao ");
        hql.append("inner join versao.perfisRisco as perfil ");
        hql.append("where arc.tipo = :tipo ");
        hql.append("and perfil.pk = :pk ");
        Query query = getCurrentSession().createQuery(hql.toString());
        query.setParameter("tipo", TipoGrupoEnum.EXTERNO);
        query.setInteger(PK, pkPerfilRisco);
        return (ArcNotasVO) query.uniqueResult();
    }

}
