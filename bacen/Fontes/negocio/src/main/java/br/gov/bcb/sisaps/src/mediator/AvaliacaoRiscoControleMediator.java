package br.gov.bcb.sisaps.src.mediator;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.exception.NegocioException;
import br.gov.bcb.app.stuff.seguranca.UsuarioCorrente;
import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.adaptadores.pessoa.BcPessoaAdapter;
import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.seguranca.UsuarioAplicacao;
import br.gov.bcb.sisaps.src.dao.AvaliacaoRiscoControleDAO;
import br.gov.bcb.sisaps.src.dao.TendenciaARCDAO;
import br.gov.bcb.sisaps.src.dominio.AnexoARC;
import br.gov.bcb.sisaps.src.dominio.Atividade;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoARC;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControleExterno;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Delegacao;
import br.gov.bcb.sisaps.src.dominio.Designacao;
import br.gov.bcb.sisaps.src.dominio.Elemento;
import br.gov.bcb.sisaps.src.dominio.EntidadeSupervisionavel;
import br.gov.bcb.sisaps.src.dominio.EstadoCiclo;
import br.gov.bcb.sisaps.src.dominio.ItemElemento;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.Metodologia;
import br.gov.bcb.sisaps.src.dominio.ParametroGrupoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.ParametroNota;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.TendenciaARC;
import br.gov.bcb.sisaps.src.dominio.VersaoPerfilRisco;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoARCEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoCicloEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoMatrizEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfisNotificacaoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoGrupoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoObjetoVersionadorEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoUnidadeAtividadeEnum;
import br.gov.bcb.sisaps.src.validacao.RegraAnaliseARCSupervisorPermissaoAlteracao;
import br.gov.bcb.sisaps.src.validacao.RegraBotaoAnalisarARC;
import br.gov.bcb.sisaps.src.validacao.RegraConclusaoEdicaoARCInspetorValidacao;
import br.gov.bcb.sisaps.src.validacao.RegraEdicaoARCInspetorPermissaoAlteracao;
import br.gov.bcb.sisaps.src.vo.ARCDesignacaoVO;
import br.gov.bcb.sisaps.src.vo.ArcNotasVO;
import br.gov.bcb.sisaps.src.vo.ArcResumidoVO;
import br.gov.bcb.sisaps.src.vo.AvaliacaoRiscoControleVO;
import br.gov.bcb.sisaps.src.vo.ConsultaARCDesignacaoVO;
import br.gov.bcb.sisaps.src.vo.ConsultaAvaliacaoRiscoControleVO;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import br.gov.bcb.sisaps.util.geral.DataUtil;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.ConstantesMensagens;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;

@Service
public class AvaliacaoRiscoControleMediator extends
        AbstractMediatorPaginado<AvaliacaoRiscoControleVO, Integer, ConsultaAvaliacaoRiscoControleVO> {

    private static final String INDICADOR_COREC = " (Corec)";
    
    @Autowired
    private AvaliacaoRiscoControleDAO avaliacaoRiscoControleDAO;

    @Autowired
    private AnexoArcMediator anexoArcMediator;

    @Autowired
    private ElementoMediator elementoMediator;

    @Autowired
    private TendenciaMediator tendenciaMediator;

    @Autowired
    private TendenciaARCDAO tendenciaARCDAO;

    public static AvaliacaoRiscoControleMediator get() {
        return SpringUtils.get().getBean(AvaliacaoRiscoControleMediator.class);
    }

    @Override
    protected AvaliacaoRiscoControleDAO getDao() {
        return avaliacaoRiscoControleDAO;
    }

    public String notaArc(AvaliacaoRiscoControle avaliacao, Ciclo ciclo, PerfilAcessoEnum perfilEnum,
            PerfilRisco perfilRisco) {
        return notaArc(avaliacao, ciclo, perfilEnum, perfilRisco, false);
    }

    public String notaArc(AvaliacaoRiscoControle avaliacao, Ciclo ciclo, PerfilAcessoEnum perfilEnum,
            PerfilRisco perfilRisco, boolean isAtaCorec) {
        PerfilRisco perfilRiscoAtual = PerfilRiscoMediator.get().obterPerfilRiscoAtual(ciclo.getPk());
        EstadoCiclo estado = EstadoCicloMediator.get().buscarPorPerfilRisco(perfilRiscoAtual.getPk());

        if (isAtaCorec) {
            return avaliacao.getNotaVigenteDescricaoValor();
        } else if (EstadoCicloEnum.EM_ANDAMENTO.equals(estado.getEstado())) {
            return obterNotaEstadoCicloEmAndamento(avaliacao);
        } else if (EstadoCicloEnum.POS_COREC.equals(estado.getEstado())
                || EstadoCicloEnum.ENCERRADO.equals(estado.getEstado())) {
            return obterNotaEstadoCicloPosCorecEncerrado(avaliacao, perfilRisco, perfilRiscoAtual);
        } else if (EstadoCicloEnum.COREC.equals(estado.getEstado())) {
            return obterNotaEstadoCicloCorec(avaliacao, perfilEnum, perfilRisco, perfilRiscoAtual);
        }
        return "";

    }

    private String obterNotaEstadoCicloCorec(AvaliacaoRiscoControle avaliacao, PerfilAcessoEnum perfilEnum,
            PerfilRisco perfilRisco, PerfilRisco perfilRiscoAtual) {
        if (isSupervisorOuGerente(perfilEnum) && avaliacao.getNotaCorec() != null
                && perfilRisco.getPk().equals(perfilRiscoAtual.getPk())) {
            return avaliacao.getNotaCorec().getDescricaoValor();
        }
        return avaliacao.getNotaVigenteDescricaoValor();
    }

    private String obterNotaEstadoCicloPosCorecEncerrado(AvaliacaoRiscoControle avaliacao, PerfilRisco perfilRisco,
            PerfilRisco perfilRiscoAtual) {
        if (avaliacao.getNotaCorec() != null && perfilRisco.getPk().equals(perfilRiscoAtual.getPk())) {
            return avaliacao.getNotaCorec().getDescricaoValor();
        }
        return avaliacao.getNotaVigenteDescricaoValor();
    }

    private String obterNotaEstadoCicloEmAndamento(AvaliacaoRiscoControle avaliacao) {
        if (avaliacao.getAvaliacaoRiscoControleVigente() != null
                && avaliacao.getAvaliacaoRiscoControleVigente().getNotaCorec() != null) {
            return avaliacao.getAvaliacaoRiscoControleVigente().getNotaCorec().getDescricaoValor();
        }
        return avaliacao.getNotaVigenteDescricaoValor();
    }

    @Transactional(readOnly = true)
    public AvaliacaoRiscoControle buscarPorPk(Integer pk) {
        SisapsUtil.lancarNegocioException(new ErrorMessage(ConstantesMensagens.MSG_APS_ARC_ERRO_SELECIONE_ARC),
                pk == null);
        AvaliacaoRiscoControle arc = avaliacaoRiscoControleDAO.load(pk);
        Hibernate.initialize(arc.getEstado());
        return arc;
    }

    @Transactional(readOnly = true)
    public AvaliacaoRiscoControle buscar(Integer pk) {
        AvaliacaoRiscoControle arc = avaliacaoRiscoControleDAO.buscarArcPorPK(pk);
        inicializarElementos(arc);
        if (arc.getAvaliacaoRiscoControleVigente() != null) {
            inicializarElementos(arc.getAvaliacaoRiscoControleVigente());
        }
        return arc;
    }

    @Transactional
    public void incluir(AvaliacaoRiscoControle arc) {
        avaliacaoRiscoControleDAO.saveOrUpdate(arc);
    }

    @Transactional(readOnly = true)
    public AvaliacaoRiscoControle buscarAvaliacaoAtividade(ParametroGrupoRiscoControle parametro, Integer atividade,
            Integer matriz, List<VersaoPerfilRisco> versoesPerfilRiscoARCs, TipoGrupoEnum tipo) {
        AvaliacaoRiscoControle result =
                avaliacaoRiscoControleDAO.buscarAvaliacaoAtividade(parametro, atividade, matriz,
                        versoesPerfilRiscoARCs, tipo);
        return result;
    }

    @Transactional
    public void excluir(List<AvaliacaoRiscoControle> arcs) {
        for (AvaliacaoRiscoControle arc : arcs) {
            excluir(arc);
        }
    }

    @Transactional
    public void excluir(AvaliacaoRiscoControle avaliacao) {
        if (avaliacao.getVersaoPerfilRisco() != null) {
            VersaoPerfilRiscoMediator.get().excluir(avaliacao.getVersaoPerfilRisco());
        }
        avaliacaoRiscoControleDAO.delete(avaliacao);
    }

    @Transactional
    public void alterar(AvaliacaoRiscoControle avaliacao) {
        avaliacaoRiscoControleDAO.merge(avaliacao);
    }

    @Transactional(readOnly = true)
    public List<ARCDesignacaoVO> consultaARCsDesignacaoVO(ConsultaARCDesignacaoVO consulta) {
        if (consulta.getMatriz() == null) {
            return new ArrayList<ARCDesignacaoVO>();
        } else {
            return avaliacaoRiscoControleDAO.consultaARCsDesignacaoVO(consulta);
        }
    }

    @Transactional
    public void alterarEstadoARCParaEmEdicao(AvaliacaoRiscoControle avaliacaoRiscoControle) {
        AvaliacaoRiscoControle arc = buscar(avaliacaoRiscoControle.getPk());
        arc.setUltimaAtualizacao(DataUtil.getDateTimeAtual());
        arc.setEstado(EstadoARCEnum.EM_EDICAO);
        avaliacaoRiscoControleDAO.update(arc);
    }

    @Transactional
    public void alterarEstadoARCParaEmAnalise(AvaliacaoRiscoControle avaliacaoRiscoControle) {
        AvaliacaoRiscoControle arc = buscar(avaliacaoRiscoControle.getPk());
        arc.setUltimaAtualizacao(DataUtil.getDateTimeAtual());
        arc.setEstado(EstadoARCEnum.EM_ANALISE);
        avaliacaoRiscoControleDAO.update(arc);
    }

    @Transactional(readOnly = true)
    public List<ArcResumidoVO> consultaPainelArcDesignados() {
        return avaliacaoRiscoControleDAO.consultaPainelArcDesignados();
    }

    @Transactional(readOnly = true)
    public List<ArcResumidoVO> consultaPainelArcDelegados() {
        return avaliacaoRiscoControleDAO.consultaPainelArcDelegados();
    }

    @Transactional(readOnly = true)
    public List<ArcResumidoVO> consultaPainelSupervisor() {
        return avaliacaoRiscoControleDAO.consultaPainelSupervisor();
    }

    @Transactional(readOnly = true)
    public List<AvaliacaoRiscoControle> buscarAvaliacaoPorAtividade(Integer atividade, Integer matriz) {
        return avaliacaoRiscoControleDAO.buscarAvaliacaoPorAtividade(atividade, matriz, false);
    }

    @Transactional(readOnly = true)
    public AvaliacaoRiscoControle loadPK(Integer pk) {
        AvaliacaoRiscoControle result = avaliacaoRiscoControleDAO.buscarArcPorPK(pk);
        Hibernate.initialize(result);
        inicializarDependencias(result);
        return result;
    }

    @Transactional(readOnly = true)
    public AvaliacaoRiscoControle loadNotaEPeso(Integer pk) {
        AvaliacaoRiscoControle result = avaliacaoRiscoControleDAO.load(pk);
        Hibernate.initialize(result);
        if (result.getAvaliacoesArc() != null) {
            Hibernate.initialize(result.getAvaliacoesArc());
        }
        if (result.getAvaliacaoRiscoControleVigente() != null) {
            Hibernate.initialize(result);
        }
        return result;
    }

    @Transactional(readOnly = true)
    private void inicializarDependencias(AvaliacaoRiscoControle result) {
        if (result.getAnexosArc() != null) {
            Hibernate.initialize(result.getAnexosArc());
        }

        inicializarElementos(result);

        if (result.getTendenciasArc() != null) {
            Hibernate.initialize(result.getTendenciasArc());
        }

        if (result.getAvaliacaoRiscoControleVigente() != null) {
            Hibernate.initialize(result.getAvaliacaoRiscoControleVigente());
        }

    }

    @Transactional(readOnly = true)
    private void inicializarElementos(AvaliacaoRiscoControle result) {
        if (result.getElementos() != null) {
            Hibernate.initialize(result.getElementos());
            for (Elemento elemento : result.getElementos()) {
                Hibernate.initialize(elemento.getItensElemento());
                for (ItemElemento item : elemento.getItensElemento()) {
                    Hibernate.initialize(item.getDocumento());
                    if (item.getDocumento() != null) {
                        Hibernate.initialize(item.getDocumento().getAnexosItemElemento());
                    }

                }

            }
        }
        inicializandoOutrosElementos(result);

    }

    @Transactional(readOnly = true)
    private void inicializandoOutrosElementos(AvaliacaoRiscoControle result) {
        if (result.getAvaliacoesArc() != null) {
            Hibernate.initialize(result.getAvaliacoesArc());
        }
        if (result.getAnexosArc() != null) {
            Hibernate.initialize(result.getAnexosArc());
        }
        if (result.getTendenciasArc() != null) {
            Hibernate.initialize(result.getTendenciasArc());
        }
        if (result.getTendenciaARCInspetor() != null) {
            Hibernate.initialize(result.getTendenciaARCInspetor());
        }
        if (result.getTendenciaARCInspetorOuSupervisor() != null) {
            Hibernate.initialize(result.getTendenciaARCInspetorOuSupervisor());
        }
        if (result.getTendenciaARCVigente() != null) {
            Hibernate.initialize(result.getTendenciaARCVigente());
        }
    }

    @Transactional
    public String concluirEdicaoARCInspetor(Ciclo ciclo, Integer arc) {
        AvaliacaoRiscoControle arcValidacao = loadPK(arc);
        new RegraEdicaoARCInspetorPermissaoAlteracao(ciclo, arcValidacao).validar();
        new RegraConclusaoEdicaoARCInspetorValidacao(arcValidacao).validar();
        arcValidacao.setEstado(EstadoARCEnum.PREENCHIDO);
        UsuarioAplicacao usuario = (UsuarioAplicacao) UsuarioCorrente.get();
        arcValidacao.setOperadorPreenchido(usuario.getLogin());
        arcValidacao.setDataPreenchido(DataUtil.getDateTimeAtual());
        arcValidacao.setAlterarDataUltimaAtualizacao(false);
        if (arcValidacao.getTendenciaARCInspetor() != null) {
            arcValidacao.getTendenciaARCInspetor().setOperadorAtualizacao(usuario.getLogin());
            arcValidacao.getTendenciaARCInspetor().setUltimaAtualizacao(DataUtil.getDateTimeAtual());
        }
        avaliacaoRiscoControleDAO.merge(arcValidacao);
        return "ARC concluído com sucesso.";
    }

    @Transactional(readOnly = true)
    public boolean validarEstadoMatriz(List<ARCDesignacaoVO> arcsDesignacao) {
        boolean vigente = false;
        for (ARCDesignacaoVO arc : arcsDesignacao) {
            if (arc.getPkAtividade() == null) {
                return true;
            } else {
                Atividade atividade = AtividadeMediator.get().loadPK(arc.getPkAtividade());
                vigente = EstadoMatrizEnum.VIGENTE.equals(atividade.getMatriz().getEstadoMatriz());
                if (vigente) {
                    return vigente;
                }
            }
        }
        return vigente;
    }

    @Transactional(readOnly = true)
    public boolean validarUsuarioDelegado(String matricula, List<ARCDesignacaoVO> arcsDesignacao) {
        boolean vigente = false;
        for (ARCDesignacaoVO arc : arcsDesignacao) {
            AvaliacaoRiscoControle avaliacao = loadPK(arc.getPk());
            if (DelegacaoMediator.get().isARCDelegado(avaliacao, matricula)) {
                return true;
            }

        }
        return vigente;
    }

    @Transactional
    public AvaliacaoRiscoControle publicarARC(Ciclo ciclo, AvaliacaoRiscoControle arc) {
        // duplicar arc
        AvaliacaoRiscoControle novoARC = duplicarARCConclusaoAnalise(ciclo, arc, true);

        // duplicar anexo do arc
        anexoArcMediator.duplicarAnexosARCConclusaoAnalise(ciclo, arc, novoARC, false);
        // duplicar elemento do arc
        elementoMediator.duplicarElementosARCConclusaoAnalise(ciclo, arc, novoARC);

        tendenciaMediator.duplicarTendenciaARCConclusaoAnalise(arc, novoARC, false);

        arc.setNotaCorec(null);

        arc.setEstado(EstadoARCEnum.CONCLUIDO);
        arc.setAlterarDataUltimaAtualizacao(false);
        novoARC.setAlterarDataUltimaAtualizacao(false);
        atualizarArc(arc);

        DesignacaoMediator.get().duplicarDesignacaoARCConclusaoAnalise(ciclo, arc, novoARC, false);

        if (novoARC.getAvaliacaoRiscoControleExterno() != null) {
            novoARC.getAvaliacaoRiscoControleExterno().setUltimaAtualizacao(DataUtil.getDateTimeAtual());
            AvaliacaoRiscoControleExternoMediator.get().salvarArcExterno(novoARC.getAvaliacaoRiscoControleExterno());
        }
        
        return novoARC;
    }

    private void atualizarArc(AvaliacaoRiscoControle arc) {
        arc.setValorNota(arc.getValorNota());
        arc.setDataConclusao(DataUtil.getDateTimeAtual());
        arc.setOperadorConclusao(((UsuarioAplicacao) UsuarioCorrente.get()).getLogin());
        avaliacaoRiscoControleDAO.update(arc);
        avaliacaoRiscoControleDAO.flush(); //garante que a data de atualização do novo ARC será posterior à do antigo
    }

    @Transactional
    private AvaliacaoRiscoControle duplicarARCConclusaoAnalise(Ciclo ciclo, AvaliacaoRiscoControle arc,
            boolean isCopiarUsuarioAnterior) {
        AvaliacaoRiscoControle novoARC = new AvaliacaoRiscoControle();
        novoARC.setPercentualParticipacao(arc.getPercentualParticipacao());
        novoARC.setEnderecoManual(arc.getEnderecoManual());
        novoARC.setEstado(montarEstadoArc(ciclo, arc));
        novoARC.setTipo(arc.getTipo());
        novoARC.setNotaCorec(arc.getNotaCorec());

        novoARC.setAvaliacaoRiscoControleVigente(arc);
        if (isCopiarUsuarioAnterior) {
            novoARC.setAlterarDataUltimaAtualizacao(false);
            novoARC.setUltimaAtualizacao(arc.getUltimaAtualizacao());
            novoARC.setOperadorAtualizacao(arc.getOperadorAtualizacao());
        }
        avaliacaoRiscoControleDAO.save(novoARC);
        avaliacaoRiscoControleDAO.flush();

        if (arc.getAvaliacaoRiscoControleExterno() != null) {
            AvaliacaoRiscoControleExterno arcExternoNovo = new AvaliacaoRiscoControleExterno();
            arcExternoNovo.setAvaliacaoRiscoControle(novoARC);
            arcExternoNovo.setCiclo(ciclo);
            arcExternoNovo.setParametroGrupoRiscoControle(arc.getAvaliacaoRiscoControleExterno()
                    .getParametroGrupoRiscoControle());
            if (isCopiarUsuarioAnterior) {
                arcExternoNovo.setAlterarDataUltimaAtualizacao(false);
                arcExternoNovo.setOperadorAtualizacao(arc.getAvaliacaoRiscoControleExterno().getOperadorAtualizacao());
                arcExternoNovo.setUltimaAtualizacao(arc.getAvaliacaoRiscoControleExterno().getUltimaAtualizacao());
            }
            AvaliacaoRiscoControleExternoMediator.get().salvarArcExterno(arcExternoNovo);
            novoARC.setAvaliacaoRiscoControleExterno(arcExternoNovo);
        }

        return novoARC;
    }

    private EstadoARCEnum montarEstadoArc(Ciclo ciclo, AvaliacaoRiscoControle arc) {
        if (arc.getDesignacao() != null
                && EntidadeSupervisionavelMediator.get()
                        .validarServidorEquipeEs(arc.getDesignacao().getMatriculaServidor(),
                                ciclo.getEntidadeSupervisionavel().getLocalizacao())) {
            return EstadoARCEnum.DESIGNADO;
        }
        return EstadoARCEnum.PREVISTO;
    }

    @Transactional
    public String concluirAnaliseARCSupervisor(Ciclo ciclo, Integer arc) {
        AvaliacaoRiscoControle arcValidacao = loadPK(arc);
        new RegraAnaliseARCSupervisorPermissaoAlteracao(ciclo, arcValidacao).validar();
        new RegraBotaoAnalisarARC(arcValidacao).validar();

        ArrayList<ErrorMessage> erros = new ArrayList<ErrorMessage>();
        erros.addAll(TendenciaMediator.get().validarTendencia(arcValidacao.getTendenciaARCSupervisor()));
        SisapsUtil.lancarNegocioException(erros);

        arcValidacao.setEstado(EstadoARCEnum.ANALISADO);
        UsuarioAplicacao usuario = (UsuarioAplicacao) UsuarioCorrente.get();
        arcValidacao.setOperadorAnalise(usuario.getLogin());
        arcValidacao.setDataAnalise(DataUtil.getDateTimeAtual());

        if (arcValidacao.getAvaliacaoARC() != null && arcValidacao.getAvaliacaoARC().getParametroNota() != null) {
            arcValidacao.setValorNota(arcValidacao.getAvaliacaoARC().getParametroNota().getValor());
        } else {
            arcValidacao.setValorNota(new BigDecimal(getNotaCalculadaSupervisor(arcValidacao).replace(',', '.')));
        }

        TendenciaARC tendencia = arcValidacao.getTendenciaARCSupervisor();
        if (tendencia != null) {
            tendencia.setOperadorAtualizacao(usuario.getLogin());
            tendencia.setUltimaAtualizacao(DataUtil.getDateTimeAtual());
            tendenciaARCDAO.merge(tendencia);
        }

        arcValidacao.setValorNota(calcularNotaFinalSupervisor(arcValidacao));
        arcValidacao.setAlterarDataUltimaAtualizacao(false);
        avaliacaoRiscoControleDAO.update(arcValidacao);
        avaliacaoRiscoControleDAO.flush();

        return "Análise do ARC concluída. ARC não publicado. "
                + "Acesse e revise a Síntese do Risco para publicar este ARC no Perfil de Risco.";
    }

    private BigDecimal calcularNotaFinalSupervisor(AvaliacaoRiscoControle arc) {
        // Caso o supervisor tenha preenchido a 'Nota ajustada', a nota final é a 'Nota ajustada' do supervisor.
        if (arc.avaliacaoSupervisor() != null) {
            return arc.avaliacaoSupervisor().getParametroNota().getValor();
        } else if (arc.possuiNotaElementosSupervisor()) {
            // Senão, caso o supervisor tenha feito ajuste em alguma nota dos elementos, a nota final é a 'Nota Calculada' do supervisor.
            return new BigDecimal(arc.getMediaNotaElementosSupervisor().replace(',', '.'));
        } else if (arc.getAvaliacaoARCInspetor() != null) {
            // Senão, caso haja ajuste de nota do inspetor, a nota final é a 'Nota ajustada' pelo inspetor.
            return arc.getAvaliacaoARCInspetor().getParametroNota().getValor();
        } else if (arc.possuiNotaElementosInspetor()) {
            // Senão, a nota final é a 'Nota Calculada' do inspetor.
            return new BigDecimal(arc.getMediaNotaElementosInspetor().replace(',', '.'));
        }
        return null;
    }

    @Transactional(readOnly = true)
    public List<AvaliacaoRiscoControle> buscarArcDaMatrizPorGrupo(Matriz matriz, ParametroGrupoRiscoControle grupo,
            List<VersaoPerfilRisco> versoesPerfilRiscoARCs, TipoGrupoEnum tipo) {
        return avaliacaoRiscoControleDAO.buscarArcDaMatrizPorGrupo(matriz, grupo, versoesPerfilRiscoARCs, tipo);
    }

    @Transactional(readOnly = true)
    public List<AvaliacaoRiscoControleVO> consultaHistoricoARC(ConsultaAvaliacaoRiscoControleVO consulta) {
        return avaliacaoRiscoControleDAO.consultaPainelArcsHistorico(consulta);
    }

    public boolean estadoPrevisto(EstadoARCEnum estadoArc) {
        return EstadoARCEnum.PREVISTO.equals(estadoArc);
    }

    public boolean estadoDesignado(EstadoARCEnum estado) {
        return EstadoARCEnum.DESIGNADO.equals(estado);
    }

    public boolean estadoEmEdicao(EstadoARCEnum estadoArc) {
        return EstadoARCEnum.EM_EDICAO.equals(estadoArc);
    }

    public boolean estadoPreenchidoAnaliseDelegadaEmAnaliseAnalisado(EstadoARCEnum estadoArc) {
        return estadoPreenchido(estadoArc) || estadoAnaliseDelegada(estadoArc) || estadoEmAnalise(estadoArc)
                || estadoAnalisado(estadoArc);
    }

    public boolean estadoPreenchido(EstadoARCEnum estadoArc) {
        return EstadoARCEnum.PREENCHIDO.equals(estadoArc);
    }

    public boolean estadoAnaliseDelegada(EstadoARCEnum estadoArc) {
        return EstadoARCEnum.ANALISE_DELEGADA.equals(estadoArc);
    }

    public boolean estadoEmAnalise(EstadoARCEnum estadoArc) {
        return EstadoARCEnum.EM_ANALISE.equals(estadoArc);
    }

    public boolean estadoAnalisado(EstadoARCEnum estadoArc) {
        return EstadoARCEnum.ANALISADO.equals(estadoArc);
    }

    public boolean estadoConcluido(EstadoARCEnum estadoArc) {
        return EstadoARCEnum.CONCLUIDO.equals(estadoArc);
    }

    private boolean estadoAnalisadoConcluido(EstadoARCEnum estado) {
        return estadoAnalisado(estado) || estadoConcluido(estado);
    }

    private boolean estadoAnaliseDelegadaEmAnalise(EstadoARCEnum estado) {
        return estadoEmAnalise(estado) || estadoAnalisadoConcluido(estado);
    }

    public boolean exibirColunaFinal(EstadoARCEnum estado) {
        return estadoAnaliseDelegadaEmAnalise(estado);
    }

    public boolean exibirColunaSupervisor(EstadoARCEnum estado) {
        return estadoAnaliseDelegadaEmAnalise(estado);
    }

    public boolean exibirColunaInspetor(EstadoARCEnum estado) {
        return estadoEmEdicao(estado) || estadoAnaliseDelegada(estado) || estadoEmAnalise(estado)
                || estadoAnalisadoConcluido(estado);
    }

    public boolean exibirColunaVigente(EstadoARCEnum estado) {
        return !estadoConcluido(estado);
    }

    public boolean estadoPreenchidoSupervisor(AvaliacaoRiscoControle avaliacaoRiscoControle, EstadoARCEnum estadoArc,
            UsuarioAplicacao usuario, Matriz matriz) {
        return estadoPreenchido(estadoArc);
    }

    public boolean estados(EstadoARCEnum estadoArc) {
        return estadosEmAnaliseAnalisado(estadoArc) || EstadoARCEnum.EM_ANALISE.equals(estadoArc)
                || EstadoARCEnum.ANALISADO.equals(estadoArc) || EstadoARCEnum.PREENCHIDO.equals(estadoArc);
    }

    public boolean estadosEmAnaliseAnalisado(EstadoARCEnum estadoArc) {
        return EstadoARCEnum.EM_EDICAO.equals(estadoArc) || EstadoARCEnum.ANALISE_DELEGADA.equals(estadoArc);
    }

    public boolean estadosPrevistoDesignado(EstadoARCEnum estadoArc) {
        return estadoPrevistoDesignado(estadoArc) || EstadoARCEnum.CONCLUIDO.equals(estadoArc);
    }

    public boolean estadoPrevistoDesignado(EstadoARCEnum estadoArc) {
        return EstadoARCEnum.PREVISTO.equals(estadoArc) || EstadoARCEnum.DESIGNADO.equals(estadoArc);
    }

    @Transactional(readOnly = true)
    public List<AvaliacaoRiscoControle> buscarArcsConcluidoDaMatriz(List<VersaoPerfilRisco> versoesPerfilRiscoARCs) {
        return avaliacaoRiscoControleDAO.buscarArcsConcluidoDaMatriz(versoesPerfilRiscoARCs);
    }

    public boolean estadosPrevistoDelegado(EstadoARCEnum estadoArc) {
        return EstadoARCEnum.PREVISTO.equals(estadoArc) || EstadoARCEnum.ANALISE_DELEGADA.equals(estadoArc);
    }

    @Transactional(readOnly = true)
    public AvaliacaoRiscoControle inicializarArcEmBranco(Integer arcPk, EstadoARCEnum estado) {
        AvaliacaoRiscoControle avaliacaoBase = buscar(arcPk);
        AvaliacaoRiscoControle avaliacaoNova = new AvaliacaoRiscoControle();
        avaliacaoNova.setPk(avaliacaoBase.getPk());
        avaliacaoNova.setTipo(avaliacaoBase.getTipo());

        for (Elemento elemento : avaliacaoBase.getElementos()) {
            try {
                Elemento elementoEmBranco = new Elemento();
                BeanUtils.copyProperties(elementoEmBranco, elemento);
                elementoEmBranco.setParametroNotaInspetor(null);
                elementoEmBranco.setParametroNotaSupervisor(null);
                elementoEmBranco.setItensElemento(new ArrayList<ItemElemento>());
                for (ItemElemento itemElemento : elemento.getItensElemento()) {
                    ItemElemento itemElementoEmBranco = new ItemElemento();
                    BeanUtils.copyProperties(itemElementoEmBranco, itemElemento);
                    itemElementoEmBranco.setElemento(elementoEmBranco);
                    itemElementoEmBranco.setDocumento(null);
                    elementoEmBranco.getItensElemento().add(itemElementoEmBranco);
                }
                avaliacaoNova.getElementos().add(elementoEmBranco);
            } catch (IllegalAccessException e) {
                throw new NegocioException(e.getMessage(), e);
            } catch (InvocationTargetException e) {
                throw new NegocioException(e.getMessage(), e);
            }
        }
        avaliacaoNova.setAnexosArc(new ArrayList<AnexoARC>());
        avaliacaoNova.setTendenciasArc(null);
        avaliacaoNova.setAnexosArc(new ArrayList<AnexoARC>());
        avaliacaoNova.setEstado(estado);
        return avaliacaoNova;

    }

    public ServidorVO atualizarResponsavel(AvaliacaoRiscoControle avaliacao,
            EntidadeSupervisionavel entidadeSupervisionavel) {
        String matriculaSupervisor;
        AvaliacaoRiscoControle avaliacaoBase = buscarPorPk(avaliacao.getPk());
        if (estadoAnaliseDelegada(avaliacaoBase.getEstado()) || delegacaoEstadoAnalisadoEmAnalise(avaliacaoBase)) {
            matriculaSupervisor =
                    avaliacaoBase.getDelegacao() == null ? "" : avaliacaoBase.getDelegacao().getMatriculaServidor();
        } else if (estadoPreenchido(avaliacaoBase.getEstado()) || estadoEmAnalise(avaliacaoBase.getEstado())) {
            matriculaSupervisor =
                    CicloMediator.get().buscarChefeAtual(entidadeSupervisionavel.getLocalizacao()).getMatricula();
        } else {
            matriculaSupervisor =
                    avaliacaoBase.getDesignacao() == null ? "" : avaliacaoBase.getDesignacao().getMatriculaServidor();
        }

        return BcPessoaAdapter.get().buscarServidor(matriculaSupervisor);
    }

    private boolean delegacaoEstadoAnalisadoEmAnalise(AvaliacaoRiscoControle avaliacaoBase) {
        return avaliacaoBase.getDelegacao() != null
                && (estadoAnalisado(avaliacaoBase.getEstado()) || estadoEmAnalise(avaliacaoBase.getEstado()));
    }

    @Transactional(readOnly = true)
    public PerfilRisco buscarUltimoPerfilRisco(AvaliacaoRiscoControle avaliacaoRiscoControle) {
        return avaliacaoRiscoControleDAO.buscarUltimoPerfilRisco(avaliacaoRiscoControle);
    }

    @Transactional
    public void alterarEstadoARCBotaoAnalisar(AvaliacaoRiscoControle arc) {
        avaliacaoRiscoControleDAO.evict(arc);
        AvaliacaoRiscoControle arcLoad = avaliacaoRiscoControleDAO.getRecord(arc.getPk());
        if (arcLoad.getEstado().equals(EstadoARCEnum.ANALISADO)) {
            arcLoad.setEstado(EstadoARCEnum.EM_ANALISE);
            DelegacaoMediator.get().excluirDelegacaoARC(arcLoad);
        } else if (arcLoad.getEstado().equals(EstadoARCEnum.ANALISE_DELEGADA)) {
            DelegacaoMediator.get().excluirDelegacaoARC(arcLoad);
            arcLoad.setEstado(EstadoARCEnum.PREENCHIDO);
        } else if (arcLoad.getEstado().equals(EstadoARCEnum.EM_ANALISE)) {
            DelegacaoMediator.get().excluirDelegacaoARC(arcLoad);
        }
        arcLoad.setDataAnalise(null);
        arcLoad.setOperadorAnalise(null);
        arcLoad.setAlterarDataUltimaAtualizacao(false);
        avaliacaoRiscoControleDAO.saveOrUpdate(arcLoad);
        avaliacaoRiscoControleDAO.flush();
    }

    @Transactional(readOnly = true)
    public List<AvaliacaoRiscoControleVO> consultaArcPerfil(Matriz matrizvigente,
            List<VersaoPerfilRisco> versoesPerfilRiscoCiclo) {

        List<AvaliacaoRiscoControleVO> consultaArcPerfil =
                avaliacaoRiscoControleDAO.consultaArcPerfil(matrizvigente, versoesPerfilRiscoCiclo);

        if (matrizvigente.getPercentualGovernancaCorporativoInt() > 0) {
            AvaliacaoRiscoControle arc =
                    AvaliacaoRiscoControleExternoMediator.get().buscarUltimoArcExterno(matrizvigente.getCiclo());
            if (arc != null) {

                ParametroGrupoRiscoControle parametro =
                        arc.getAvaliacaoRiscoControleExterno().getParametroGrupoRiscoControle();
                AvaliacaoRiscoControleVO arcVO =
                        new AvaliacaoRiscoControleVO(arc.getPk(), arc.getEstado(), arc.getTipo(), parametro,
                                matrizvigente, new Atividade(), arc.getUltimaAtualizacao(),
                                arc.getAvaliacaoRiscoControleExterno().getParametroGrupoRiscoControle().getOrdem(),
                                TipoUnidadeAtividadeEnum.CORPORATIVO, arc.getNotaVigente(), arc.getNovaNotaVigente());
                consultaArcPerfil.add(arcVO);
            }

        }
        return consultaArcPerfil;
    }

    @Transactional
    public void ajustarNotasARCsCorec(List<AvaliacaoRiscoControleVO> listArcVO) {
        for (AvaliacaoRiscoControleVO avaliacaoRiscoControleVO : listArcVO) {
            if (avaliacaoRiscoControleVO.isAlterouNota()) {
                AvaliacaoRiscoControle arc = loadPK(avaliacaoRiscoControleVO.getPk());
                arc.setNotaCorec(avaliacaoRiscoControleVO.getNotaCorec());
                avaliacaoRiscoControleDAO.saveOrUpdate(arc);
                avaliacaoRiscoControleVO.setAlterouNota(false);
            }
        }
        avaliacaoRiscoControleDAO.flush();

    }

    public boolean exibirAcoes(AvaliacaoRiscoControle avaliacao, Ciclo ciclo, boolean paginaSupervisor) {
        return (CicloMediator.get().cicloEmAndamento(ciclo) && (paginaSupervisor && CicloMediator.get().isSupervisor(
                ciclo.getEntidadeSupervisionavel().getLocalizacao())));
    }

    private boolean isSupervisorOuGerente(PerfilAcessoEnum perfil) {
        return PerfilAcessoEnum.GERENTE.equals(perfil) || PerfilAcessoEnum.SUPERVISOR.equals(perfil);
    }

    public String notaArcIndicadorCorec(AvaliacaoRiscoControle avaliacao, Ciclo ciclo, PerfilAcessoEnum perfilEnum,
            boolean indicadorCorec, boolean isPerfilRiscoAtual) {
        PerfilRisco perfil = PerfilRiscoMediator.get().obterPerfilRiscoAtual(ciclo.getPk());
        EstadoCiclo estado = EstadoCicloMediator.get().buscarPorPerfilRisco(perfil.getPk());
        if (EstadoCicloEnum.EM_ANDAMENTO.equals(estado.getEstado())) {
            if (avaliacao.getAvaliacaoRiscoControleVigente() != null
                    && avaliacao.getAvaliacaoRiscoControleVigente().getNotaCorec() != null) {
                return avaliacao.getAvaliacaoRiscoControleVigente().getNotaCorec().getDescricaoValor()
                        + (indicadorCorec ? INDICADOR_COREC : "");
            }
        } else if (EstadoCicloEnum.POS_COREC.equals(estado.getEstado())
                || EstadoCicloEnum.ENCERRADO.equals(estado.getEstado())) {
            if (avaliacao.getNotaCorec() != null && isPerfilRiscoAtual) {
                return avaliacao.getNotaCorec().getDescricaoValor() + (indicadorCorec ? INDICADOR_COREC : "");
            }

        } else if (EstadoCicloEnum.COREC.equals(estado.getEstado())) {
            if (isSupervisorOuGerente(perfilEnum) && avaliacao.getNotaCorec() != null && isPerfilRiscoAtual) {
                return avaliacao.getNotaCorec().getDescricaoValor() + (indicadorCorec ? INDICADOR_COREC : "");
            }
        }

        return "";
    }

    public String notaArcDescricaoValor(AvaliacaoRiscoControle avaliacao, Ciclo ciclo, PerfilAcessoEnum perfilEnum,
            boolean indicadorCorec, boolean isPerfilRiscoAtual) {
        String notaCorec = notaArcIndicadorCorec(avaliacao, ciclo, perfilEnum, indicadorCorec, isPerfilRiscoAtual);
        if (!"".equals(notaCorec)) {
            return notaCorec;
        }

        return avaliacao.getNotaVigenteDescricaoValor();

    }

    @Transactional
    public AvaliacaoRiscoControle criarARCNovoCiclo(Ciclo cicloAnterior, Ciclo cicloNovo,
            ParametroGrupoRiscoControle parametroGrupoRiscoControle, AvaliacaoRiscoControle arcPerfilRiscoVigente) {
        // Copiar todos os dados do ARC anterior para o ARC do perfil de risco vigente
        copiarDadosARCAnterior(cicloAnterior, arcPerfilRiscoVigente);
        avaliacaoRiscoControleDAO.flush();
        // Criar novo ARC com os seguintes dados do respectivo ARC do perfil de risco vigente:
        AvaliacaoRiscoControle novoARCCiclo =
                criarNovoARCCiclo(cicloAnterior, cicloNovo, parametroGrupoRiscoControle, arcPerfilRiscoVigente);
        DesignacaoMediator.get().copiarDesignacaoARCAnterior(arcPerfilRiscoVigente);
        return novoARCCiclo;
    }

    private void copiarDadosARCAnterior(Ciclo cicloAnterior, AvaliacaoRiscoControle arcPerfilRiscoVigente) {
        AvaliacaoRiscoControle arcAnterior = arcPerfilRiscoVigente.getAvaliacaoRiscoControleVigente();
        arcPerfilRiscoVigente.setPercentualParticipacao(arcAnterior.getPercentualParticipacao());
        arcPerfilRiscoVigente.setEnderecoManual(arcAnterior.getEnderecoManual());
        arcPerfilRiscoVigente.setEstado(arcAnterior.getEstado());
        arcPerfilRiscoVigente.setTipo(arcAnterior.getTipo());
        arcPerfilRiscoVigente.setValorNota(arcAnterior.getValorNota());
        arcPerfilRiscoVigente.setAlterarDataUltimaAtualizacao(false);
        arcPerfilRiscoVigente.setDataPreenchido(arcAnterior.getDataPreenchido());
        arcPerfilRiscoVigente.setOperadorPreenchido(arcAnterior.getOperadorPreenchido());
        arcPerfilRiscoVigente.setDataAnalise(arcAnterior.getDataAnalise());
        arcPerfilRiscoVigente.setOperadorAnalise(arcAnterior.getOperadorAnalise());
        arcPerfilRiscoVigente.setDataConclusao(arcAnterior.getDataConclusao());
        arcPerfilRiscoVigente.setOperadorConclusao(arcAnterior.getOperadorConclusao());
        arcPerfilRiscoVigente.setUltimaAtualizacao(arcAnterior.getUltimaAtualizacao());
        arcPerfilRiscoVigente.setOperadorAtualizacao(arcAnterior.getOperadorAtualizacao());
        arcPerfilRiscoVigente.setAlterarDataUltimaAtualizacao(false);
        DelegacaoMediator.get().copiarDelegacaoARCAnterior(arcPerfilRiscoVigente);
        ElementoMediator.get().copiarDadosElementoARCAnterior(cicloAnterior, arcPerfilRiscoVigente);
        AvaliacaoARCMediator.get().copiarDadosAvaliacaoARCAnterior(arcPerfilRiscoVigente);
        TendenciaMediator.get().copiarDadosTendenciaARCAnterior(arcPerfilRiscoVigente);
        AnexoArcMediator.get().copiarAnexosARCAnterior(cicloAnterior, arcPerfilRiscoVigente);
        avaliacaoRiscoControleDAO.saveOrUpdate(arcPerfilRiscoVigente);
    }

    private AvaliacaoRiscoControle criarNovoARCCiclo(Ciclo cicloAnterior, Ciclo cicloNovo,
            ParametroGrupoRiscoControle parametroGrupoRiscoControle, AvaliacaoRiscoControle arcPerfilRiscoVigente) {
        AvaliacaoRiscoControle novoARC = duplicarARCConclusaoAnalise(cicloNovo, arcPerfilRiscoVigente, true);
        DesignacaoMediator.get().duplicarDesignacaoARCConclusaoAnalise(cicloNovo, arcPerfilRiscoVigente, novoARC, true);
        // Anexos do ARC
        AnexoArcMediator.get()
                .duplicarAnexosARCConclusaoCorec(cicloAnterior, cicloNovo, arcPerfilRiscoVigente, novoARC);
        // Justificativa da tendência vigente (pode ter sido informada pelo inspetor ou pelo supervisor)
        tendenciaMediator.duplicarTendenciaARCConclusaoAnalise(arcPerfilRiscoVigente, novoARC, true);
        // Justificativas dos itens a avaliar dadas pelo inspetor
        // Anexos dos itens a avaliar informados pelo inspetor
        elementoMediator.duplicarElementosARCConclusaoCorec(cicloAnterior, cicloNovo, parametroGrupoRiscoControle,
                arcPerfilRiscoVigente, novoARC);
        avaliacaoRiscoControleDAO.flush();
        PerfilRiscoMediator.get().incluirVersaoPerfilRiscoAtual(cicloNovo, novoARC, TipoObjetoVersionadorEnum.ARC);
        return novoARC;
    }

    public boolean possuiNotaElementosSupervisor(AvaliacaoRiscoControle arc) {
        List<Elemento> elementos = arc.getElementos();
        for (Elemento elemento : elementos) {
            if (elemento.getParametroNotaSupervisor() != null) {
                return true;
            }
        }
        return false;
    }

    @Transactional(readOnly = true)
    public void limpaSessao() {
        // A limpeza da sessão evita o erro de mais de um objeto com o mesmo id
        avaliacaoRiscoControleDAO.getSessionFactory().getCurrentSession().clear();
    }

    public boolean exibirResponsavel(EstadoARCEnum estado) {
        return estadoDesignado(estado) || estadoEmEdicaoAnaliseDelegadaEmAnalise(estado);
    }

    public boolean exibirResponsavelTelaAnalise(EstadoARCEnum estado, Delegacao delegacao) {
        return (estadoAnaliseDelegada(estado) || estadoEmEdicaoAnaliseDelegadaEmAnalise(estado)) && delegacao != null;
    }

    public boolean exibirUltimaAtualizacao(EstadoARCEnum estado) {
        return estadoPreenchido(estado) || estadoEmEdicaoAnaliseDelegadaEmAnalise(estado);
    }

    private boolean estadoEmEdicaoAnaliseDelegadaEmAnalise(EstadoARCEnum estado) {
        return estadoEmEdicao(estado) || estadoAnaliseDelegada(estado) || estadoEmAnalise(estado);
    }

    public boolean exibirPreenchidoPor(EstadoARCEnum estado) {
        return estadoPreenchido(estado) || estadoAnalisadoConcluido(estado) || estadoEmAnalise(estado);
    }

    public boolean exibirAnalisadoPor(EstadoARCEnum estado) {
        return estadoAnalisadoConcluido(estado);
    }

    public boolean exibirConcluidoPor(EstadoARCEnum estado) {
        return estadoConcluido(estado);
    }

    public ServidorVO valorResponsavel(AvaliacaoRiscoControle arc, EntidadeSupervisionavel entidadeSupervisionavel) {

        String matriculaSupervisor;
        AvaliacaoRiscoControle arcBase = buscar(arc.getPk());
        if (estadoAnaliseDelegada(arcBase.getEstado()) || delegacaoEstadoAnalisadoEmAnalise(arcBase)) {
            matriculaSupervisor = arcBase.getDelegacao() == null ? "" : arcBase.getDelegacao().getMatriculaServidor();
        } else if (estadoPreenchido(arcBase.getEstado()) || estadoEmAnalise(arcBase.getEstado())) {
            ServidorVO chefeLocalizacaoES =
                    CicloMediator.get().buscarChefeAtual(entidadeSupervisionavel.getLocalizacao());
            matriculaSupervisor = chefeLocalizacaoES == null ? "" : chefeLocalizacaoES.getMatricula();
        } else {
            matriculaSupervisor = arcBase.getDesignacao() == null ? "" : arcBase.getDesignacao().getMatriculaServidor();
        }

        return BcPessoaAdapter.get().buscarServidor(matriculaSupervisor);
    }

    @Transactional(readOnly = true)
    public AvaliacaoRiscoControle buscarRascunhoPorArcVigente(AvaliacaoRiscoControle arc) {
        return avaliacaoRiscoControleDAO.buscarRascunhoPorArcVigente(arc);
    }

    @Transactional(readOnly = true)
    public Ciclo buscarCicloARC(AvaliacaoRiscoControle avaliacaoRiscoControle) {
        return avaliacaoRiscoControleDAO.buscarCicloARC(avaliacaoRiscoControle);
    }

    @Transactional(readOnly = true)
    public AvaliacaoRiscoControle buscarArcExternoPorPerfilRisco(Integer pkPerfilRisco) {
        return avaliacaoRiscoControleDAO.buscarArcExternoPorPerfilRisco(pkPerfilRisco);
    }

    @Transactional(readOnly = true)
    public String getNotaCalculadaFinal(AvaliacaoRiscoControle arc) {
        return isMetodologiaCalculoMedia(arc) ? arc.getMediaCalculadaFinal() : arc
                .getNotaCalculadaFinal();
    }

    @Transactional(readOnly = true)
    public String getNotaCalculadaInspetor(AvaliacaoRiscoControle arc) {
        return isMetodologiaCalculoMedia(arc) ? arc.getMediaNotaElementosInspetor()
                : arc.getNotaArrastoInspetor();
    }

    @Transactional(readOnly = true)
    public String getNotaCalculadaSupervisor(AvaliacaoRiscoControle arc) {
        return isMetodologiaCalculoMedia(arc) ? arc
                .getMediaNotaElementosSupervisor() : arc.getNotaCalculadaSupervisor();
    }

    @Transactional(readOnly = true)
    public String getDescricaoNotaCalculadaSupervisor(AvaliacaoRiscoControle arc) {
        return isMetodologiaCalculoMedia(arc) ? "mediaNotaElementosSupervisor"
                : "notaCalculadaSupervisor";
    }

    @Transactional(readOnly = true)
    public String getDescricaoNotaCalculadaInspetor(AvaliacaoRiscoControle arc) {
        return isMetodologiaCalculoMedia(arc) ? "mediaNotaElementosInspetor"
                : "notaArrastoInspetor";
    }

    public boolean isMetodologiaCalculoMedia(AvaliacaoRiscoControle arc) {
        Ciclo ciclo =
                arc.getTipo().equals(TipoGrupoEnum.EXTERNO) ? AvaliacaoRiscoControleExternoMediator.get()
                        .buscarArcExterno(arc.getPk()).getCiclo()
                        : buscarCicloARC(arc);
        return ciclo.getMetodologia().getIsCalculoMedia() != null
                && ciclo.getMetodologia().getIsCalculoMedia().equals(SimNaoEnum.SIM);
    }
    
    @Transactional(readOnly = true)
    public List<AvaliacaoRiscoControle> buscarArcPorCiclo(Integer pkCiclo) {
        return avaliacaoRiscoControleDAO.buscarArcPorCiclo(pkCiclo);
    }
    
    @Transactional(readOnly = true)
    public AvaliacaoRiscoControle buscarArcInicializado(Integer pk) {
        AvaliacaoRiscoControle result = avaliacaoRiscoControleDAO.buscarArcPorPK(pk);
        Hibernate.initialize(result);
        if (result.getAvaliacoesArc() != null) {
            Hibernate.initialize(result.getAvaliacoesArc());
        }
        inicializarElementos(result);
        return result;
    }
    
    @Transactional
    public void atualizarDadosNovaMetodologia(List<AvaliacaoRiscoControle> arcs, Metodologia metodologia, boolean isMedia) {
        for (AvaliacaoRiscoControle arc : arcs) {
            atualizarDados(metodologia, isMedia, arc);
        }
    }

    @Transactional
    public void atualizarDadosNovaMetodologiaArcExterno(List<AvaliacaoRiscoControleExterno> arcs, Metodologia metodologia, boolean isMedia) {
        for (AvaliacaoRiscoControleExterno arc : arcs) {
            atualizarDados(metodologia, isMedia, arc.getAvaliacaoRiscoControle());
        }
    }
    
    private void atualizarDados(Metodologia metodologia, boolean isMedia, AvaliacaoRiscoControle arc) {
        AvaliacaoRiscoControle arcInicializado = buscarArcInicializado(arc.getPk());
        if (arcInicializado.getNotaSupervisor() != null) {
            if (isMedia) {
                arcInicializado.setValorNota(calcularNotaFinalSupervisor(arcInicializado));
            } else {
                arcInicializado.setValorNota(arcInicializado.getNotaSupervisor().getValor());
                salvarAvaliacaoArrasto(arcInicializado, metodologia);
            }
            arcInicializado.setNotaSupervisor(null);
        }
        if (arcInicializado.getNotaCorec() != null) {
            ParametroNota novaNotaCorec =
                    ParametroNotaMediator.get().buscarNota(metodologia,
                            arcInicializado.getNotaCorec().getValor());
            arcInicializado.setNotaCorec(novaNotaCorec);
        }
        ElementoMediator.get().atualizarDadosNovaMetodologia(arcInicializado.getElementos(), metodologia);
        AvaliacaoARCMediator.get().atualizarDadosNovaMetodologia(arcInicializado.getAvaliacoesArc(), metodologia);
        arcInicializado.setAlterarDataUltimaAtualizacao(false);
        avaliacaoRiscoControleDAO.update(arcInicializado);
    }
    
    private void salvarAvaliacaoArrasto(AvaliacaoRiscoControle arc, Metodologia metodologia) {
        if (arc.avaliacaoSupervisor() == null) {
            if (arc.possuiNotaElementosSupervisor()) {
                incluirAvaliacao(arc, arc.getParametroMenorNotaElementosSupervisorInspetor(), metodologia);
            } else if (arc.getAvaliacaoARCInspetor() == null && arc.possuiNotaElementosInspetor()) {
                incluirAvaliacao(arc, arc.getParametroMenorNotaElementosInspetor(), metodologia);
            }
        }
    }

    private void incluirAvaliacao(AvaliacaoRiscoControle arc, ParametroNota notaArrasto, Metodologia metodologia) {
        ParametroNota novaNota = ParametroNotaMediator.get().buscarNota(metodologia, notaArrasto.getValor());
        AvaliacaoARC avaliacao = new AvaliacaoARC();
        avaliacao.setAvaliacaoRiscoControle(arc);
        avaliacao.setJustificativa("Migração nota calculada.");
        avaliacao.setParametroNota(novaNota);
        avaliacao.setPerfil(PerfisNotificacaoEnum.SUPERVISOR);
        AvaliacaoARCMediator.get().incluir(avaliacao);
    }
    
    public String notaArc(ArcNotasVO arcVO, Ciclo ciclo, PerfilAcessoEnum perfilEnum, PerfilRisco perfilRisco,
            boolean isAtaCorec) {
        PerfilRisco perfilRiscoAtual = PerfilRiscoMediator.get().obterPerfilRiscoAtual(ciclo.getPk());
        EstadoCiclo estado = EstadoCicloMediator.get().buscarPorPerfilRisco(perfilRiscoAtual.getPk());

        if (isAtaCorec) {
            return getNotaVigenteDescricaoValor(arcVO);
        } else if (EstadoCicloEnum.EM_ANDAMENTO.equals(estado.getEstado())) {
            return obterNotaEstadoCicloEmAndamento(arcVO);
        } else if (EstadoCicloEnum.POS_COREC.equals(estado.getEstado())
                || EstadoCicloEnum.ENCERRADO.equals(estado.getEstado())) {
            return obterNotaEstadoCicloPosCorecEncerrado(arcVO, perfilRisco, perfilRiscoAtual);
        } else if (EstadoCicloEnum.COREC.equals(estado.getEstado())) {
            return obterNotaEstadoCicloCorec(arcVO, perfilEnum, perfilRisco, perfilRiscoAtual);
        }
        return "";
    }

    private String obterNotaEstadoCicloPosCorecEncerrado(ArcNotasVO arcVO, PerfilRisco perfilRisco,
            PerfilRisco perfilRiscoAtual) {
        if (arcVO.getNotaCorec() != null && perfilRisco.getPk().equals(perfilRiscoAtual.getPk())) {
            return arcVO.getNotaCorec().getDescricaoValor();
        }
        return getNotaVigenteDescricaoValor(arcVO);
    }

    private String obterNotaEstadoCicloEmAndamento(ArcNotasVO arcVO) {
        if (arcVO.getArcVigente() != null && arcVO.getArcVigente().getNotaCorec() != null) {
            return arcVO.getArcVigente().getNotaCorec().getDescricaoValor();
        }
        return getNotaVigenteDescricaoValor(arcVO);
    }

    public String getNotaVigenteDescricaoValor(ArcNotasVO arcVO) {
        ParametroNota notavigente = getNotaVigente(arcVO);
        BigDecimal notaVigente = getNovaNotaVigente(arcVO);
        if (notavigente != null) {
            return notavigente.getDescricaoValor();
        } else if (notaVigente != null) {
            return notaVigente.compareTo(BigDecimal.ZERO) == 1 ? notaVigente.toString().replace('.', ',') : "N/A";
        }
        return Constantes.ASTERISCO_A;
    }

    public ParametroNota getNotaVigente(ArcNotasVO arcVO) {
        if (arcVO.getArcVigente() != null) {
            return arcVO.getArcVigente().getNotaSupervisor();
        }
        return null;
    }

    public BigDecimal getNovaNotaVigente(ArcNotasVO arcVO) {
        if (arcVO.getArcVigente() != null) {
            return arcVO.getArcVigente().getValorNota();
        }
        return null;
    }

    private String obterNotaEstadoCicloCorec(ArcNotasVO arcVO, PerfilAcessoEnum perfilEnum,
            PerfilRisco perfilRisco, PerfilRisco perfilRiscoAtual) {
        if (isSupervisorOuGerente(perfilEnum) && arcVO.getNotaCorec() != null
                && perfilRisco.getPk().equals(perfilRiscoAtual.getPk())) {
            return arcVO.getNotaCorec().getDescricaoValor();
        }
        return getNotaVigenteDescricaoValor(arcVO);
    }

    public String getNotaEmAnaliseDescricaoValor(ArcNotasVO arcVO) {
        if (EstadoARCEnum.ANALISADO.equals(arcVO.getEstado())) {
            return arcVO.getNotaSupervisor() != null ? arcVO.getNotaSupervisor().getValor().toString()
                    : arcVO.getValorNota().toString();
        }
        if (arcVO.getArcVigente() != null && arcVO.getArcVigente().getNotaCorec() != null) {
            return arcVO.getArcVigente().getNotaCorec().getDescricaoValor();
        }
        return getNotaVigenteDescricaoValor(arcVO);
    }

    public boolean estadoDesignadoInspetorResponsavel(ArcNotasVO avaliacaoRiscoControle, EstadoARCEnum estadoArc,
            UsuarioAplicacao usuario) {
        return estadoDesignado(estadoArc) && isDesignado(avaliacaoRiscoControle, usuario);
    }

    public boolean estadoEmEdicaoInspetorResponsavel(ArcNotasVO avaliacaoRiscoControle, EstadoARCEnum estadoArc,
            UsuarioAplicacao usuario) {
        return estadoEmEdicao(estadoArc) && isDesignado(avaliacaoRiscoControle, usuario);
    }

    public boolean estadoAnaliseDelegadaInspetorResponsavel(ArcNotasVO avaliacaoRiscoControle, EstadoARCEnum estadoArc,
            UsuarioAplicacao usuario) {
        return estadoAnaliseDelegada(estadoArc) && delegadoParaInspetor(avaliacaoRiscoControle, usuario);
    }

    public boolean estadoEmAnaliseInspetorResponsavel(ArcNotasVO avaliacaoRiscoControle, EstadoARCEnum estadoArc,
            UsuarioAplicacao usuario) {
        return estadoEmAnalise(estadoArc) && delegadoParaInspetor(avaliacaoRiscoControle, usuario);
    }

    public boolean isResponsavelARC(ArcNotasVO avaliacaoRiscoControle, UsuarioAplicacao usuario) {
        return (avaliacaoRiscoControle.getDesignacaoPk() != null
                && responsavelDesignacao(avaliacaoRiscoControle, usuario))
                || (avaliacaoRiscoControle.getDelegacaoPk() != null
                        && responsavelDelegacao(avaliacaoRiscoControle, usuario));
    }

    public boolean isResponsavelDesignacaoDelegacaoARC(ArcNotasVO avaliacaoRiscoControle, UsuarioAplicacao usuario) {
        return (avaliacaoRiscoControle.getDesignacaoPk() != null && isDesignado(avaliacaoRiscoControle, usuario))
                || (avaliacaoRiscoControle.getDelegacaoPk() != null && isDelegado(avaliacaoRiscoControle, usuario));
    }

    public boolean responsavelDelegacao(ArcNotasVO avaliacaoRiscoControle, UsuarioAplicacao usuario) {
        return isDelegado(avaliacaoRiscoControle, usuario)
                && responsavelEstadoAnaliseDelegadaEmAnalise(avaliacaoRiscoControle, usuario);
    }

    public boolean responsavelEstadoAnaliseDelegadaEmAnalise(ArcNotasVO avaliacaoRiscoControle,
            UsuarioAplicacao usuario) {
        return avaliacaoRiscoControle.getDelegacaoPk() != null
                && (estadoAnaliseDelegada(avaliacaoRiscoControle.getEstado())
                        || estadoEmAnalise(avaliacaoRiscoControle.getEstado()));
    }

    private boolean responsavelDesignacao(ArcNotasVO avaliacaoRiscoControle, UsuarioAplicacao usuario) {
        return avaliacaoRiscoControle.getDesignacaoPk() != null && isDesignado(avaliacaoRiscoControle, usuario)
                && (estadoDesignado(avaliacaoRiscoControle.getEstado())
                        || estadoEmEdicao(avaliacaoRiscoControle.getEstado()));
    }

    public boolean estadoPreenchido(ArcNotasVO avaliacaoRiscoControle, EstadoARCEnum estadoArc,
            UsuarioAplicacao usuario, Matriz matriz) {
        return estadoPreenchido(estadoArc);
    }

    public boolean estadoEmAnaliseOutroUsuario(ArcNotasVO avaliacaoRiscoControle, EstadoARCEnum estadoArc,
            UsuarioAplicacao usuario) {
        return estadoEmAnalise(estadoArc) && avaliacaoRiscoControle.getDelegacaoPk() != null;
    }

    public boolean estadoEmAnaliseSupervisor(ArcNotasVO avaliacaoRiscoControle, EstadoARCEnum estadoArc) {
        return estadoEmAnalise(estadoArc) && avaliacaoRiscoControle.getDelegacaoPk() == null;
    }

    private boolean isDesignado(ArcNotasVO avaliacaoRiscoControle, UsuarioAplicacao usuario) {
        Designacao designacao =
                DesignacaoMediator.get().buscarDesignacaoPorPk(avaliacaoRiscoControle.getDesignacaoPk());
        return usuario.getMatricula().equals(designacao.getMatriculaServidor());
    }

    public boolean delegadoParaInspetor(ArcNotasVO avaliacaoRiscoControle, UsuarioAplicacao usuario) {
        return avaliacaoRiscoControle.getDelegacaoPk() != null && isDelegado(avaliacaoRiscoControle, usuario);
    }

    private boolean isDelegado(ArcNotasVO avaliacaoRiscoControle, UsuarioAplicacao usuario) {
        Delegacao delegacao = DelegacaoMediator.get().buscarDelegacaoPorPk(avaliacaoRiscoControle.getDelegacaoPk());
        return delegacao != null && usuario.getMatricula().equals(delegacao.getMatriculaServidor());
    }

    public boolean mostraLinkArcsInspetor(ArcNotasVO arc, UsuarioAplicacao usuario) {
        if (arc == null) {
            return false;
        } else if (arc.getArcVigente() == null) {
            if (isResponsavelDesignacaoDelegacaoARC(arc, usuario) && semLinkParaInspetorResponsavelNosEstados(arc)) {
                return false;
            } else if (!isResponsavelDesignacaoDelegacaoARC(arc, usuario)
                    && semLinkParaInspetorNaoResponsavelNosEstados(arc)) {
                return false;
            }
        }

        return true;
    }

    private boolean semLinkParaInspetorResponsavelNosEstados(ArcNotasVO arc) {
        return EstadoARCEnum.PREVISTO.equals(arc.getEstado()) || EstadoARCEnum.PREENCHIDO.equals(arc.getEstado())
                || EstadoARCEnum.ANALISADO.equals(arc.getEstado());
    }

    private boolean semLinkParaInspetorNaoResponsavelNosEstados(ArcNotasVO arc) {
        return semLinkParaInspetorResponsavelNosEstados(arc) || EstadoARCEnum.DESIGNADO.equals(arc.getEstado())
                || EstadoARCEnum.EM_ANALISE.equals(arc.getEstado()) || estadosEmAnaliseAnalisado(arc.getEstado());
    }

    public String notaArcAnalise(ArcNotasVO avaliacao, Ciclo ciclo, PerfilAcessoEnum perfilEnum,
            PerfilRisco perfilRisco) {
        if (EstadoARCEnum.ANALISADO.equals(avaliacao.getEstado())) {
            return avaliacao.getNotaSupervisor() != null ? avaliacao.getNotaSupervisor().getDescricaoValor()
                    : avaliacao.getValorNota().toString().replace('.', ',');
        }
        return notaArc(avaliacao, ciclo, perfilEnum, perfilRisco);
    }

    public String notaArc(ArcNotasVO arcVO, Ciclo ciclo, PerfilAcessoEnum perfilEnum, PerfilRisco perfilRisco) {
        return notaArc(arcVO, ciclo, perfilEnum, perfilRisco, false);
    }

    @Transactional(readOnly = true)
    public ArcNotasVO consultarNotasArc(Integer arcPk) {
        ArcNotasVO arc = avaliacaoRiscoControleDAO.consultarNotasArc(arcPk);
        if (arc != null && arc.getArcVigentePk() != null) {
            arc.setArcVigente(avaliacaoRiscoControleDAO.consultarNotasArc(arc.getArcVigentePk()));
        }
        return arc;
    }

    @Transactional(readOnly = true)
    public ArcNotasVO consultarNotasArcExterno(Integer pkPerfilRisco) {
        ArcNotasVO arc = avaliacaoRiscoControleDAO.consultarNotasArcExterno(pkPerfilRisco);
        if (arc != null && arc.getArcVigentePk() != null) {
            arc.setArcVigente(avaliacaoRiscoControleDAO.consultarNotasArc(arc.getArcVigentePk()));
        }
        return arc;
    }

    @Transactional(readOnly = true)
    public ArcNotasVO consultarNotasArcVigente(Integer arcPk) {
        return avaliacaoRiscoControleDAO.consultarNotasArc(arcPk);
    }

}