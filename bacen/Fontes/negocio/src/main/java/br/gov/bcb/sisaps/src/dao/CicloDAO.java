package br.gov.bcb.sisaps.src.dao;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import br.gov.bcb.app.stuff.seguranca.UsuarioCorrente;
import br.gov.bcb.sisaps.dao.GenericDAOParaListagens;
import br.gov.bcb.sisaps.seguranca.UsuarioAplicacao;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.HistoricoLegado;
import br.gov.bcb.sisaps.src.dominio.NotaMatriz;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoCicloEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.vo.CicloVO;
import br.gov.bcb.sisaps.src.vo.ConsultaCicloVO;
import br.gov.bcb.sisaps.util.Util;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;

@Repository
public class CicloDAO extends GenericDAOParaListagens<Ciclo, Integer, CicloVO, ConsultaCicloVO> {

    private static final String LEGADO_MATRICULA_SUPERVISOR = "legado.matriculaSupervisor";

    private static final String ENTIDADE_SUPERVISIONAVEL_CNPJ = "entidadeSupervisionavel.conglomeradoOuCnpj";

    private static final String ENTIDADE_SUPERVISIONAVEL_PRIORIDADE = "entidadeSupervisionavel.prioridade";

    private static final String ENTIDADE_SUPERVISIONAVEL_NOME = "entidadeSupervisionavel.nome";

    private static final String PROP_SITUACOES_ES = "situacoesES";

    private static final String PROP_PERSPECTIVAS_ES = "perspectivasES";

    private static final String PROP_CONCLUSOES_ES = "conclusoesES";

    private static final String PROP_PERFIS_ATUACAO_ES = "perfisAtuacaoES";

    private static final String PROP_GRAUS_PREOCUPACAO_ES = "grausPreocupacaoES";

    private static final String PROP_CICLO = "ciclo";

    private static final String PROP_ESTADO_CICLO_ESTADO = "estadoCiclo.estado";

    private static final String PROP_ENTIDADE_SUPERVISIONAVEL_LOCALIZACAO = "entidadeSupervisionavel.localizacao";

    private static final String PROP_ENTIDADE_SUPERVISIONAVEL_PK = "entidadeSupervisionavel.pk";

    private static final String DATA_PREVISAO_COREC = "dataPrevisaoCorec";

    private static final String DATA_INICIO = "dataInicio";
    private static final String DATA_INICIO_COREC = "dataInicioCorec";

    private static final String PK = "pk";

    private static final String ESTADO_CICLO = "estadoCiclo";

    private static final String ENTIDADE_SUPERVISIONAVEL = "entidadeSupervisionavel";

    private static final long serialVersionUID = 1L;
    
    // Constates para Migração da nova metodologia
    private static final List<String> CODIGOS_ESS_SITUACAO_1 = Arrays.asList("C0080295", "C0081658", "C0080219",
            "C0080927", "C0080989", "C0084624", "C0080123", "C0081421", "C0080570", "C0081359", "C0080350", "C0080271",
            "C0080556", "C0080673", "C0081012", "C0081799", "C0081593", "C0081469", "C0081744", "C0081517", "C0080941",
            "C0080532");

    private static final List<String> CODIGOS_ESS_SITUACAO_2 = Arrays.asList("C0081579", "C0080075", "C0080862",
            "C0081696", "C0081263", "C0081782", "C0080855", "C0081689");

    private static final List<String> CODIGOS_ESS_SITUACAO_3 = Arrays.asList("C0080185", "C0080563", "C0080697",
            "C0081452");
    
    private static final String MIGRACAO_SITUACAO_1 = "migracao1";
    private static final String MIGRACAO_SITUACAO_2 = "migracao2";

    public CicloDAO() {
        super(Ciclo.class, CicloVO.class);
    }

    @Override
    protected void montarCriterios(Criteria criteria, ConsultaCicloVO consulta) {
        criarAliasEntidadeSupervisionavel(criteria);
        criarAliasEstadoCiclo(criteria);
        adicionarFiltroEstados(consulta, criteria);
        adicionarFiltroPerfil(consulta, criteria);
        adicionarFiltroESCiclo(consulta, criteria);
        //filtros sobre a ES
        adicionarFiltroNome(consulta, criteria);
        adicionarFiltroEquipe(consulta, criteria);
        adicionarFiltroPrioridade(consulta, criteria);
        // Atributos que serão retornados na consulta
        criteria.setProjection(Projections.projectionList().add(Projections.property(PK), PK)
                .add(Projections.property(DATA_INICIO), DATA_INICIO)
                .add(Projections.property(DATA_INICIO_COREC), DATA_INICIO_COREC)
                .add(Projections.property(DATA_PREVISAO_COREC), DATA_PREVISAO_COREC)
                .add(Projections.property(ENTIDADE_SUPERVISIONAVEL), ENTIDADE_SUPERVISIONAVEL)
                .add(Projections.property(ESTADO_CICLO), ESTADO_CICLO));
    }

    private void adicionarFiltroNome(ConsultaCicloVO consulta, Criteria criteria) {
        if (StringUtils.isNotEmpty(consulta.getEntidadeSupervisionavel().getNome())) {
            criteria.add(Restrictions.ilike(ENTIDADE_SUPERVISIONAVEL_NOME, consulta.getEntidadeSupervisionavel()
                    .getNome(), MatchMode.ANYWHERE));
        }
    }

    private void adicionarFiltroEquipe(ConsultaCicloVO consulta, Criteria criteria) {
        if (StringUtils.isNotEmpty(consulta.getEntidadeSupervisionavel().getLocalizacao())) {
            if (consulta.isBuscarHierarquiaInferior()) {
                SimpleExpression locallizacaoInferior =
                        Restrictions.like(PROP_ENTIDADE_SUPERVISIONAVEL_LOCALIZACAO, consulta
                                .getEntidadeSupervisionavel().getLocalizacao(), MatchMode.ANYWHERE);
                if (!Util.isNuloOuVazio(consulta.getMatriculaSupervisor())) {
                    SimpleExpression matriculaIgual =
                            Restrictions.eq(LEGADO_MATRICULA_SUPERVISOR, consulta.getMatriculaSupervisor());
                    criteria.add(Restrictions.or(locallizacaoInferior, matriculaIgual));
                } else {
                    criteria.add(locallizacaoInferior);
                }
            } else {

                SimpleExpression localizacaoIgual =
                        Restrictions.eq(PROP_ENTIDADE_SUPERVISIONAVEL_LOCALIZACAO, consulta
                                .getEntidadeSupervisionavel().getLocalizacao());

                if (!Util.isNuloOuVazio(consulta.getMatriculaSupervisor())) {
                    SimpleExpression matriculaIgual =
                            Restrictions.eq(LEGADO_MATRICULA_SUPERVISOR, consulta.getMatriculaSupervisor());
                    criteria.add(Restrictions.or(localizacaoIgual, matriculaIgual));
                } else {
                    criteria.add(localizacaoIgual);
                }
            }
        }
    }

    private void adicionarFiltroPrioridade(ConsultaCicloVO consulta, Criteria criteria) {
        if (consulta.getEntidadeSupervisionavel().getPrioridade() != null) {
            criteria.add(Restrictions.eq(ENTIDADE_SUPERVISIONAVEL_PRIORIDADE, consulta.getEntidadeSupervisionavel()
                    .getPrioridade()));
        }
    }

    private void adicionarFiltroEstados(ConsultaCicloVO consulta, Criteria criteria) {
        if (CollectionUtils.isNotEmpty(consulta.getEstados())) {
            criteria.add(Restrictions.in(PROP_ESTADO_CICLO_ESTADO, consulta.getEstados()));
        }
    }

    private void adicionarFiltroLocalizacaoES(ConsultaCicloVO consulta, Criteria criteria) {
        if (StringUtils.isNotEmpty(consulta.getRotuloLocalizacao())) {
            criteria.add(Restrictions.eq(PROP_ENTIDADE_SUPERVISIONAVEL_LOCALIZACAO, consulta.getRotuloLocalizacao()));
        }
    }

    private void adicionarFiltroPerfil(ConsultaCicloVO consulta, Criteria criteria) {
        if (PerfilAcessoEnum.SUPERVISOR.equals(consulta.getPerfil())
                || PerfilAcessoEnum.INSPETOR.equals(consulta.getPerfil())
                || PerfilAcessoEnum.GERENTE.equals(consulta.getPerfil())) {
            adicionarFiltroLocalizacaoES(consulta, criteria);
        }
    }

    private void adicionarFiltroESCiclo(ConsultaCicloVO consulta, Criteria criteria) {
        if (consulta.getEntidadeSupervisionavel() != null && consulta.getEntidadeSupervisionavel().getPk() != null) {
            criteria.add(Restrictions.eq(PROP_ENTIDADE_SUPERVISIONAVEL_PK, consulta.getEntidadeSupervisionavel()
                    .getPk()));
        }
    }

    public Ciclo buscarCicloPorPK(Integer id) {
        return getRecord(id);
    }

    @SuppressWarnings(UNCHECKED)
    public List<Ciclo> consultarCiclosEntidadeSupervisionavel(String cnpjEntidadeSupervisionavel,
            boolean excluirCiclosMigrados) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criarAliasEntidadeSupervisionavel(criteria);
        criteria.add(Restrictions.eq(ENTIDADE_SUPERVISIONAVEL_CNPJ, cnpjEntidadeSupervisionavel));
        if (excluirCiclosMigrados) {
            criteria.add(Restrictions.gt(PK, 0));
        }
        criteria.addOrder(Order.desc(DATA_PREVISAO_COREC));
        return criteria.list();
    }

    public Ciclo consultarUltimoCicloEmAndamentoCorecES(Integer pkEntidadeSupervisionavel) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criarAliasEntidadeSupervisionavel(criteria);
        criarAliasEstadoCiclo(criteria);
        criteria.add(Restrictions.eq(PROP_ENTIDADE_SUPERVISIONAVEL_PK, pkEntidadeSupervisionavel));
        criteria.add(Restrictions.in(PROP_ESTADO_CICLO_ESTADO,
                Arrays.asList(EstadoCicloEnum.EM_ANDAMENTO, EstadoCicloEnum.COREC)));
        return (Ciclo) criteria.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public Ciclo consultarUltimoCicloCorecPosEncerradoES(Integer pkEntidadeSupervisionavel) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criarAliasEntidadeSupervisionavel(criteria);
        criarAliasEstadoCiclo(criteria);
        criteria.add(Restrictions.eq(PROP_ENTIDADE_SUPERVISIONAVEL_PK, pkEntidadeSupervisionavel));
        criteria.add(Restrictions.in(PROP_ESTADO_CICLO_ESTADO,
                Arrays.asList(EstadoCicloEnum.COREC, EstadoCicloEnum.POS_COREC, EstadoCicloEnum.ENCERRADO)));
        criteria.addOrder(Order.desc(DATA_PREVISAO_COREC));
        List<Ciclo> resultado = criteria.list();
        return CollectionUtils.isNotEmpty(resultado) ? (Ciclo) resultado.get(0) : null;

    }

    public Ciclo consultarUltimoCicloPosCorecES(Integer pkEntidadeSupervisionavel) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criarAliasEntidadeSupervisionavel(criteria);
        criarAliasEstadoCiclo(criteria);
        criteria.add(Restrictions.eq(PROP_ENTIDADE_SUPERVISIONAVEL_PK, pkEntidadeSupervisionavel));
        criteria.add(Restrictions.in(PROP_ESTADO_CICLO_ESTADO, Arrays.asList(EstadoCicloEnum.POS_COREC)));
        criteria.list();
        return (Ciclo) criteria.uniqueResult();
    }

    public HistoricoLegado buscarHistoricoLegadoCiclo(Integer cicloPk) {
        Criteria criteria = getCurrentSession().createCriteria(HistoricoLegado.class);
        criteria.createAlias(PROP_CICLO, PROP_CICLO);
        criteria.add(Restrictions.eq("ciclo.pk", cicloPk));
        return (HistoricoLegado) criteria.uniqueResult();
    }

    public List<Ciclo> consultarCiclosEmAndamento() {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criarAliasEstadoCiclo(criteria);
        addFiltroEstadoCicloEmAndamento(criteria);
        return cast(criteria.list());
    }

    public Ciclo consultarUltimoCicloPosCorecEncerradoES(Integer pkEntidadeSupervisionavel) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criarAliasEntidadeSupervisionavel(criteria);
        criarAliasEstadoCiclo(criteria);
        criteria.add(Restrictions.eq(PROP_ENTIDADE_SUPERVISIONAVEL_PK, pkEntidadeSupervisionavel));
        criteria.add(Restrictions.in(PROP_ESTADO_CICLO_ESTADO,
                Arrays.asList(EstadoCicloEnum.POS_COREC, EstadoCicloEnum.ENCERRADO)));

        criteria.addOrder(Order.desc(DATA_PREVISAO_COREC));
        criteria.setMaxResults(1);
        return CollectionUtils.isNotEmpty(criteria.list()) ? (Ciclo) criteria.list().get(0) : null;
    }

    public Ciclo getUltimoCiclo() {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.addOrder(Order.desc(NotaMatriz.PROP_ULTIMA_ATUALIZACAO));
        @SuppressWarnings(UNCHECKED)
        List<Ciclo> resultado = criteria.list();
        return CollectionUtils.isNotEmpty(resultado) ? (Ciclo) resultado.get(0) : null;
    }

    @SuppressWarnings(UNCHECKED)
    public List<CicloVO> getPendenciasGerente() {
        UsuarioAplicacao usuarioAplicacao = (UsuarioAplicacao) UsuarioCorrente.get();
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criarAliasEntidadeSupervisionavel(criteria);
        criarAliasEstadoCiclo(criteria);
        criteria.createAlias(PROP_GRAUS_PREOCUPACAO_ES, PROP_GRAUS_PREOCUPACAO_ES, JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias(PROP_PERFIS_ATUACAO_ES, PROP_PERFIS_ATUACAO_ES, JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias(PROP_CONCLUSOES_ES, PROP_CONCLUSOES_ES, JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias(PROP_PERSPECTIVAS_ES, PROP_PERSPECTIVAS_ES, JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias(PROP_SITUACOES_ES, PROP_SITUACOES_ES, JoinType.LEFT_OUTER_JOIN);
        addFiltroEstadoCicloEmAndamento(criteria);
        criteria.add(Restrictions.ilike(PROP_ENTIDADE_SUPERVISIONAVEL_LOCALIZACAO, usuarioAplicacao.getServidorVO()
                .getLocalizacaoAtual(PerfilAcessoEnum.GERENTE), MatchMode.START));
        criteria.add(Restrictions.isNotNull(ENTIDADE_SUPERVISIONAVEL_PRIORIDADE));
        criteria.add(Restrictions.or(Restrictions.eq("grausPreocupacaoES.pendente", SimNaoEnum.SIM),
                Restrictions.eq("perfisAtuacaoES.pendente", SimNaoEnum.SIM),
                Restrictions.eq("conclusoesES.pendente", SimNaoEnum.SIM),
                Restrictions.eq("perspectivasES.pendente", SimNaoEnum.SIM),
                Restrictions.eq("situacoesES.pendente", SimNaoEnum.SIM)));
        criteria.setProjection(Projections.projectionList().add(Projections.groupProperty(PK), PK)
                .add(Projections.groupProperty(ENTIDADE_SUPERVISIONAVEL_NOME), "nomeES"));
        criteria.addOrder(Order.asc(ENTIDADE_SUPERVISIONAVEL_NOME));
        criteria.setResultTransformer(new AliasToBeanResultTransformer(CicloVO.class));
        return criteria.list();
    }

    private void criarAliasEntidadeSupervisionavel(Criteria criteria) {
        criteria.createAlias(ENTIDADE_SUPERVISIONAVEL, ENTIDADE_SUPERVISIONAVEL);
        criteria.createAlias("historicoLegado", "legado", JoinType.LEFT_OUTER_JOIN);
    }

    private void criarAliasEstadoCiclo(Criteria criteria) {
        criteria.createAlias(ESTADO_CICLO, ESTADO_CICLO);
    }

    private void addFiltroEstadoCicloEmAndamento(Criteria criteria) {
        criteria.add(Restrictions.eq(PROP_ESTADO_CICLO_ESTADO, EstadoCicloEnum.EM_ANDAMENTO));
    }

    @SuppressWarnings("unchecked")
    public List<CicloVO> consultarCicloAndamentoCorecES(ConsultaCicloVO consulta) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criarAliasEntidadeSupervisionavel(criteria);
        criarAliasEstadoCiclo(criteria);
        addFiltrosConsultaCicloVO(criteria, consulta);
        criteria.add(Restrictions.in(PROP_ESTADO_CICLO_ESTADO,
                Arrays.asList(EstadoCicloEnum.EM_ANDAMENTO, EstadoCicloEnum.COREC)));
        addColunasRetornadas(criteria);
        criteria.addOrder(Order.asc(DATA_PREVISAO_COREC));
        criteria.addOrder(Order.asc(ENTIDADE_SUPERVISIONAVEL_NOME));
        criteria.setResultTransformer(new AliasToBeanResultTransformer(CicloVO.class));
        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    public List<CicloVO> consultarCicloPosCorecEncerradoES(ConsultaCicloVO consulta) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criarAliasEntidadeSupervisionavel(criteria);
        criarAliasEstadoCiclo(criteria);
        addFiltrosConsultaCicloVO(criteria, consulta);

        criteria.add(Restrictions.in(PROP_ESTADO_CICLO_ESTADO,
                Arrays.asList(EstadoCicloEnum.POS_COREC, EstadoCicloEnum.ENCERRADO)));
        addColunasRetornadas(criteria);
        criteria.addOrder(Order.asc(DATA_PREVISAO_COREC));
        criteria.addOrder(Order.asc(ENTIDADE_SUPERVISIONAVEL_NOME));
        criteria.setResultTransformer(new AliasToBeanResultTransformer(CicloVO.class));
        return criteria.list();
    }

    private void addColunasRetornadas(Criteria criteria) {
        criteria.setProjection(Projections.projectionList().add(Projections.property(PK), PK)
                .add(Projections.property(ENTIDADE_SUPERVISIONAVEL), ENTIDADE_SUPERVISIONAVEL)
                .add(Projections.property(DATA_INICIO), DATA_INICIO)
                .add(Projections.property(DATA_PREVISAO_COREC), DATA_PREVISAO_COREC));
    }

    private void addFiltrosConsultaCicloVO(Criteria criteria, ConsultaCicloVO consulta) {
        adicionarFiltroData(consulta, criteria);
        adicionarFiltroNome(consulta, criteria);
        adicionarFiltroEquipe(consulta, criteria);
        adicionarFiltroPrioridade(consulta, criteria);

    }

    private void adicionarFiltroData(ConsultaCicloVO consulta, Criteria criteria) {
        ajustePreenchendoDataInicio(consulta, criteria);
        ajustePreenchendoDataFim(consulta, criteria);
        ajustePreenchendoDataInicioFim(consulta, criteria);
    }

    private void ajustePreenchendoDataInicio(ConsultaCicloVO consulta, Criteria criteria) {
        if (consulta.getDataCorecInicio() != null && consulta.getDataCorecFim() == null) {
            criteria.add(Restrictions.ge(DATA_PREVISAO_COREC, consulta.getDataCorecInicio().toDate()));
        }
    }

    private void ajustePreenchendoDataFim(ConsultaCicloVO consulta, Criteria criteria) {
        if (consulta.getDataCorecInicio() == null && consulta.getDataCorecFim() != null) {
            criteria.add(Restrictions.le(DATA_PREVISAO_COREC, consulta.getDataCorecFim().toDate()));
        }
    }

    private void ajustePreenchendoDataInicioFim(ConsultaCicloVO consulta, Criteria criteria) {
        if (consulta.getDataCorecInicio() != null && consulta.getDataCorecFim() != null) {
            criteria.add(Restrictions.between(DATA_PREVISAO_COREC, consulta.getDataCorecInicio().toDate(), consulta
                    .getDataCorecFim().toDate()));
        }
    }

    @SuppressWarnings("unchecked")
    public List<CicloVO> consultaCiclosParaEnvioEmail(boolean isDiponibilidade, DateTime dataParametro) {
        StringBuilder hql = new StringBuilder();

        hql.append("select new br.gov.bcb.sisaps.src.vo.CicloVO(ciclo.pk) from Ciclo as ciclo ");
        hql.append("inner join ciclo.estadoCiclo as estado ");
        hql.append("left outer join ciclo.agenda as agenda ");
        hql.append("where estado.estado in (2,1) and ");
        if (isDiponibilidade) {
            hql.append("(agenda.dataEnvioDisponibilidade is not null or ");
        } else {
            hql.append("(agenda.dataEnvioApresentacao is null and ");
        }
        hql.append("ciclo.dataPrevisaoCorec <= :data )");

        Query query = getCurrentSession().createQuery(hql.toString());
        query.setDate("data", dataParametro.toDate());

        return query.list();
    }

    @SuppressWarnings(UNCHECKED)
    public List<Ciclo> consultarCiclosMigracao() {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criarAliasEstadoCiclo(criteria);
        criteria.add(Restrictions.ne(PROP_ESTADO_CICLO_ESTADO, EstadoCicloEnum.EXCLUIDO));
        criteria.add(Restrictions.gt(PK, 0));
        return criteria.list();
    }
    
    public List<Ciclo> consultarCiclosAndamentoCorec() {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.in(PROP_ESTADO_CICLO_ESTADO,
                Arrays.asList(EstadoCicloEnum.EM_ANDAMENTO, EstadoCicloEnum.COREC)));
        criarAliasEstadoCiclo(criteria);
        return cast(criteria.list());
    }
    
    public CicloVO buscar(Integer codigoEs) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq("entidadeSupervisionavel.pk", codigoEs));
        
        @SuppressWarnings("unchecked")
        List<Ciclo> lista = criteria.list();
        
        CicloVO retorno = null;
        
        if (lista != null && !lista.isEmpty()) {
            retorno = new CicloVO();
            retorno.setPk(lista.get(0).getPk());
        }
        
        return retorno;
    }
    
    @SuppressWarnings(UNCHECKED)
    public Ciclo consultarCicloAnteriorEntidadeSupervisionavel(Ciclo ciclo, String cnpjEntidadeSupervisionavel) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criarAliasEntidadeSupervisionavel(criteria);
        criteria.add(Restrictions.eq(ENTIDADE_SUPERVISIONAVEL_CNPJ, cnpjEntidadeSupervisionavel));
        criteria.add(Restrictions.lt(DATA_PREVISAO_COREC, ciclo.getDataPrevisaoCorec()));
        criteria.add(Restrictions.gt(PK, 0));
        criteria.addOrder(Order.desc(DATA_PREVISAO_COREC));
        List<Ciclo> resultado = criteria.list();
        return CollectionUtils.isNotEmpty(resultado) ? (Ciclo) resultado.get(0) : null;
    }
    
    public List<Ciclo> consultarCiclosAndamentoNovaMetodologia() {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criarAliasEstadoCiclo(criteria);
        criteria.createAlias(ENTIDADE_SUPERVISIONAVEL, ENTIDADE_SUPERVISIONAVEL);
        criteria.add(Restrictions.eq(PROP_ESTADO_CICLO_ESTADO, EstadoCicloEnum.EM_ANDAMENTO));
        criteria.add(Restrictions.eq(ENTIDADE_SUPERVISIONAVEL_CNPJ, "C0080374"));
        return cast(criteria.list());
    }
    
    public List<Ciclo> consultarCiclosNovaMetodologiaCorec(String identificadorMigracao) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criarAliasEstadoCiclo(criteria);
        criteria.createAlias(ENTIDADE_SUPERVISIONAVEL, ENTIDADE_SUPERVISIONAVEL);
        criteria.add(Restrictions.eq(PROP_ESTADO_CICLO_ESTADO, EstadoCicloEnum.COREC));
        if (identificadorMigracao.equals(MIGRACAO_SITUACAO_1)) {
            criteria.add(Restrictions.in(ENTIDADE_SUPERVISIONAVEL_CNPJ, CODIGOS_ESS_SITUACAO_1));
        } else if (identificadorMigracao.equals(MIGRACAO_SITUACAO_2)) {
            criteria.add(Restrictions.in(ENTIDADE_SUPERVISIONAVEL_CNPJ, CODIGOS_ESS_SITUACAO_2));
        } else {
            criteria.add(Restrictions.in(ENTIDADE_SUPERVISIONAVEL_CNPJ, CODIGOS_ESS_SITUACAO_3));
        }
        return cast(criteria.list());
    }
    
}
