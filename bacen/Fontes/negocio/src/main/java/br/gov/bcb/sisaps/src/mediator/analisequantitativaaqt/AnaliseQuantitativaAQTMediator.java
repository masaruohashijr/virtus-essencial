package br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.seguranca.UsuarioCorrente;
import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.comum.util.string.StringUtil;
import br.gov.bcb.sisaps.adaptadores.pessoa.BcPessoaAdapter;
import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.seguranca.UsuarioAplicacao;
import br.gov.bcb.sisaps.src.dao.analisequantitativaaqt.AnaliseQuantitativaAQTDAO;
import br.gov.bcb.sisaps.src.dao.analisequantitativaaqt.DelegacaoAQTDAO;
import br.gov.bcb.sisaps.src.dao.analisequantitativaaqt.ElementoAQTDAO;
import br.gov.bcb.sisaps.src.dao.analisequantitativaaqt.ItemElementoAQTDAO;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.EntidadeSupervisionavel;
import br.gov.bcb.sisaps.src.dominio.Metodologia;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.VersaoPerfilRisco;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AvaliacaoAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.DelegacaoAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ElementoAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ItemElementoAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroElementoAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroItemElementoAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroNotaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.PesoAQT;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoAQTEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoCicloEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfisNotificacaoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoObjetoVersionadorEnum;
import br.gov.bcb.sisaps.src.mediator.AbstractMediatorPaginado;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.EntidadeSupervisionavelMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.src.mediator.RegraPerfilAcessoMediator;
import br.gov.bcb.sisaps.src.mediator.VersaoPerfilRiscoMediator;
import br.gov.bcb.sisaps.src.validacao.RegraAnaliseANEFSupervisorPermissaoAlteracao;
import br.gov.bcb.sisaps.src.validacao.RegraBotaoAnalisarAnef;
import br.gov.bcb.sisaps.src.validacao.RegraConclusaoEdicaoANEFInspetorValidacao;
import br.gov.bcb.sisaps.src.validacao.RegraEdicaoANEFInspetorPermissaoAlteracao;
import br.gov.bcb.sisaps.src.vo.ConsultaAnaliseQuantitativaAQTVO;
import br.gov.bcb.sisaps.src.vo.analisequantitativa.aqt.AnaliseQuantitativaAQTVO;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.geral.DataUtil;

@Service
@Transactional(readOnly = true)
public class AnaliseQuantitativaAQTMediator extends
        AbstractMediatorPaginado<AnaliseQuantitativaAQTVO, Integer, ConsultaAnaliseQuantitativaAQTVO> {

    private static final String COREC = " (Corec)";

    @Autowired
    private AnaliseQuantitativaAQTDAO analiseQuantitativaAQTDAO;

    @Autowired
    private ElementoAQTDAO elementoAQTDAO;

    @Autowired
    private ItemElementoAQTDAO itemElementoAQTDAO;

    @Autowired
    private DelegacaoAQTDAO delegacaoAQTDAO;

    public static AnaliseQuantitativaAQTMediator get() {
        return SpringUtils.get().getBean(AnaliseQuantitativaAQTMediator.class);
    }

    @Override
    protected AnaliseQuantitativaAQTDAO getDao() {
        return analiseQuantitativaAQTDAO;
    }

    @Transactional(readOnly = true)
    public List<AnaliseQuantitativaAQT> consultaPainelAQTDesignados() {
        return analiseQuantitativaAQTDAO.consultaPainelAQTDesignados();
    }

    @Transactional(readOnly = true)
    public List<AnaliseQuantitativaAQT> consultaPainelAQTDelegados() {
        return analiseQuantitativaAQTDAO.consultaPainelAQTDelegados();
    }

    @Transactional(readOnly = true)
    public List<AnaliseQuantitativaAQT> consultaPainelAQTAnalise() {
        return analiseQuantitativaAQTDAO.consultaPainelAQTAnalise();
    }

    @Transactional(readOnly = true)
    public List<AnaliseQuantitativaAQT> consultaPainelAQTAnalisados() {
        return analiseQuantitativaAQTDAO.consultaPainelAQTAnalisado();
    }

    @Transactional(readOnly = true)
    public AnaliseQuantitativaAQT buscar(Integer pk) {
        AnaliseQuantitativaAQT aqt = analiseQuantitativaAQTDAO.buscarAnefPorPK(pk);
        inicializarElementos(aqt);
        return aqt;
    }

    @Transactional(readOnly = true)
    private void inicializarElementos(AnaliseQuantitativaAQT result) {
        if (result.getCiclo() != null) {
            Hibernate.initialize(result.getCiclo());
            Hibernate.initialize(result.getCiclo().getEntidadeSupervisionavel());
        }
        if (result.getNotaSupervisor() != null) {
            Hibernate.initialize(result.getNotaSupervisor());
        }
        if (result.getValorNota() != null) {
            Hibernate.initialize(result.getValorNota());
        }
        if (result.getParametroAQT() != null) {
            Hibernate.initialize(result.getParametroAQT());
        }
        if (result.getVersaoPerfilRisco() != null) {
            Hibernate.initialize(result.getVersaoPerfilRisco());
            Hibernate.initialize(result.getVersaoPerfilRisco().getPerfisRisco());
        }

        if (result.getAvaliacoesAnef() != null) {
            Hibernate.initialize(result.getAvaliacoesAnef());
        }

        if (result.getAnexosAqt() != null) {
            Hibernate.initialize(result.getAnexosAqt());
        }

        inicializarItensElementos(result);

    }

    private void inicializarItensElementos(AnaliseQuantitativaAQT result) {
        if (result.getElementos() != null) {
            Hibernate.initialize(result.getElementos());
            for (ElementoAQT elemento : result.getElementos()) {
                Hibernate.initialize(elemento.getItensElemento());
                for (ItemElementoAQT item : elemento.getItensElemento()) {
                    Hibernate.initialize(item.getDocumento());
                    if (item.getDocumento() != null) {
                        Hibernate.initialize(item.getDocumento().getAnexosItemElemento());
                    }

                }

            }
        }
    }

    public boolean estadoPrevisto(EstadoAQTEnum estadoAQT) {
        return EstadoAQTEnum.PREVISTO.equals(estadoAQT);
    }

    public boolean estadoDesignado(EstadoAQTEnum estadoAQT) {
        return EstadoAQTEnum.DESIGNADO.equals(estadoAQT);
    }

    public boolean estadoEmEdicao(EstadoAQTEnum estadoAQT) {
        return EstadoAQTEnum.EM_EDICAO.equals(estadoAQT);
    }

    public boolean estadoPreenchido(EstadoAQTEnum estadoAQT) {
        return EstadoAQTEnum.PREENCHIDO.equals(estadoAQT);
    }

    public boolean estadoAnaliseDelegada(EstadoAQTEnum estadoAQT) {
        return EstadoAQTEnum.ANALISE_DELEGADA.equals(estadoAQT);
    }

    public boolean estadoEmAnalise(EstadoAQTEnum estadoAQT) {
        return EstadoAQTEnum.EM_ANALISE.equals(estadoAQT);
    }

    public boolean estadoAnalisado(EstadoAQTEnum estadoAQT) {
        return EstadoAQTEnum.ANALISADO.equals(estadoAQT);
    }

    public boolean estadoConcluido(EstadoAQTEnum estadoAQT) {
        return EstadoAQTEnum.CONCLUIDO.equals(estadoAQT);
    }

    public boolean telaAnaliseSupervisor(AnaliseQuantitativaAQT aqt) {
        return validarEstadosEDelegacao(aqt) || validarEstadosEmAnalise(aqt);
    }

    public boolean telaDetalheSupervisor(AnaliseQuantitativaAQT aqt) {
        EstadoAQTEnum estado = aqt.getEstado();
        boolean estadosPrevistoConcluidoDesignado =
                estadoPrevisto(estado) || estadoConcluido(estado) || estadoDesignado(estado);
        boolean estadosEmedicaoAnalisadoAnaliseDelag =
                estadoEmEdicao(estado) || estadoAnalisado(estado) || estadoAnaliseDelegada(estado)
                        || estadoEmAnalise(estado);
        return estadosPrevistoConcluidoDesignado || estadosEmedicaoAnalisadoAnaliseDelag;
    }

    private boolean validarEstadosEDelegacao(AnaliseQuantitativaAQT aqt) {
        return (estadoPreenchido(aqt.getEstado()) && aqt.getDelegacao() == null)
                || (estadoAnaliseDelegada(aqt.getEstado()) && existeDelegacaoParaOUsuario(aqt));
    }

    private boolean validarEstadosEmAnalise(AnaliseQuantitativaAQT aqt) {
        return estadoEmAnalise(aqt.getEstado()) && aqt.getDelegacao() == null;
    }

    public boolean verificarTelaDestinoDesignado(AnaliseQuantitativaAQT aqt) {
        return CicloMediator.get().cicloEmAndamento(aqt.getCiclo()) && isResponsavelDesignacao(aqt)
                && (estadoDesignado(aqt.getEstado()) || estadoEmEdicao(aqt.getEstado()));
    }

    public boolean verificarTelaDestinoDelegado(AnaliseQuantitativaAQT aqt) {
        return CicloMediator.get().cicloEmAndamento(aqt.getCiclo()) && isResponsavelDelegacao(aqt)
                && (estadoAnaliseDelegada(aqt.getEstado()) || estadoEmAnalise(aqt.getEstado()));
    }

    public boolean isResponsavelDesignacao(AnaliseQuantitativaAQT aqt) {
        return aqt.getDesignacao() != null
                && ((UsuarioAplicacao) UsuarioCorrente.get()).getMatricula().equals(
                        aqt.getDesignacao().getMatriculaServidor());
    }

    private boolean isResponsavelDelegacao(AnaliseQuantitativaAQT aqt) {
        return aqt.getDelegacao() != null
                && ((UsuarioAplicacao) UsuarioCorrente.get()).getMatricula().equals(
                        aqt.getDelegacao().getMatriculaServidor());
    }

    public boolean existeDelegacaoParaOUsuario(AnaliseQuantitativaAQT aqt) {
        return aqt.getDelegacao() != null
                && ((UsuarioAplicacao) UsuarioCorrente.get()).getMatricula().equals(
                        aqt.getDelegacao().getMatriculaServidor());
    }

    public List<AnaliseQuantitativaAQT> criarAQTElementosItens(Ciclo ciclo, List<PesoAQT> listaPesosAQTs) {
        List<AnaliseQuantitativaAQT> listaAQTs = new ArrayList<AnaliseQuantitativaAQT>();
        List<ParametroAQT> listaParamentoAQT = ParametroAQTMediator.get().buscarParamentos(ciclo.getMetodologia());
        for (ParametroAQT parametroAQT : listaParamentoAQT) {
            List<ParametroElementoAQT> listaParamentoElementosAQT =
                    ParametroElementoAQTMediator.get().buscarParElementoPorParAQT(parametroAQT);
            for (int i = 0; i < 2; i++) {
                AnaliseQuantitativaAQT analiseQuantitativaAQT = new AnaliseQuantitativaAQT();
                analiseQuantitativaAQT.setEstado(EstadoAQTEnum.PREVISTO);
                analiseQuantitativaAQT.setParametroAQT(parametroAQT);
                analiseQuantitativaAQT.setCiclo(ciclo);
                if (i == 0) {
                    VersaoPerfilRiscoMediator.get().criarVersao(analiseQuantitativaAQT, TipoObjetoVersionadorEnum.AQT);
                }
                analiseQuantitativaAQTDAO.save(analiseQuantitativaAQT);
                analiseQuantitativaAQTDAO.flush();

                for (ParametroElementoAQT parametroElemento : listaParamentoElementosAQT) {
                    ElementoAQT elementoAQT = new ElementoAQT();
                    elementoAQT.setAnaliseQuantitativaAQT(analiseQuantitativaAQT);
                    elementoAQT.setParametroElemento(parametroElemento);
                    elementoAQTDAO.save(elementoAQT);
                    elementoAQTDAO.flush();
                    criarItens(parametroElemento, elementoAQT);
                }

                listaAQTs.add(analiseQuantitativaAQT);
            }
            PesoAQTMediator.get().criarPesoAQT(parametroAQT, ciclo, listaPesosAQTs);
        }
        return listaAQTs;
    }

    private void criarItens(ParametroElementoAQT parametroElemento, ElementoAQT elementoAQT) {
        List<ParametroItemElementoAQT> listaParItensElementosAQT =
                ParametroItemElementoAQTMediator.get().buscarParamentos(parametroElemento);
        for (ParametroItemElementoAQT parametroItemElementoAQT : listaParItensElementosAQT) {
            ItemElementoAQT itemElementoAQT = new ItemElementoAQT();
            itemElementoAQT.setParametroItemElemento(parametroItemElementoAQT);
            itemElementoAQT.setElemento(elementoAQT);
            itemElementoAQTDAO.save(itemElementoAQT);
            itemElementoAQTDAO.flush();
        }
    }

    @Transactional(readOnly = true)
    public List<AnaliseQuantitativaAQT> buscarAQTsPerfilRisco(List<VersaoPerfilRisco> versoes) {
        if (CollectionUtils.isEmpty(versoes)) {
            return null;
        } else {
            return analiseQuantitativaAQTDAO.buscarAQTsPerfilRisco(versoes);
        }
    }

    private Short obterPesoNoPerfil(ParametroAQT parAqt, Ciclo ciclo, PerfilRisco perfilRisco) {
        return PesoAQTMediator.get().buscarPesoAQT(parAqt, ciclo, perfilRisco).getValor();
    }

    private BigDecimal getNotaCalculada(PerfilRisco perfilRisco, PerfilAcessoEnum perfilEnum, boolean isNovoQuadro,
            boolean isAtaCorec) {
        List<AnaliseQuantitativaAQT> aqtsVigentes =
                isNovoQuadro ? listarANEFsNovoQuadro(perfilRisco.getCiclo()) : PerfilRiscoMediator.get()
                        .getAnalisesQuantitativasAQTPerfilRisco(perfilRisco);
        BigDecimal pesos = BigDecimal.ZERO;
        BigDecimal notasPonderadas = BigDecimal.ZERO;

        if (aqtsVigentes != null) {
            for (AnaliseQuantitativaAQT aqt : aqtsVigentes) {
                String notaAnef =
                        AnaliseQuantitativaAQTMediator.get().notaAnef(aqt, perfilRisco.getCiclo(), perfilEnum,
                                isNovoQuadro, perfilRisco, isAtaCorec);
                if (!Constantes.ASTERISCO_A.equals(notaAnef)) {
                    BigDecimal peso =
                            isNovoQuadro ? new BigDecimal(PesoAQTMediator.get()
                                    .obterPesoRascunho(aqt.getParametroAQT(), aqt.getCiclo()).getValor())
                                    : new BigDecimal(obterPesoNoPerfil(aqt.getParametroAQT(), aqt.getCiclo(),
                                            perfilRisco));
                    pesos = pesos.add(peso);
                    notasPonderadas = notasPonderadas.add(new BigDecimal(notaAnef.replace(',', '.')).multiply(peso));
                }
            }

            if (notasPonderadas.equals(BigDecimal.ZERO)) {
                return null;
            } else {
                notasPonderadas = notasPonderadas.divide(pesos, MathContext.DECIMAL32);
                notasPonderadas = notasPonderadas.setScale(2, RoundingMode.HALF_UP);
                return notasPonderadas;
            }
        }
        return null;
    }

    @Transactional(readOnly = true)
    public String getNotaCalculadaAEF(PerfilRisco perfilRisco, PerfilAcessoEnum perfilEnum, boolean isNovoQuadro) {
        return getNotaCalculadaAEF(perfilRisco, perfilEnum, isNovoQuadro, false);
    }

    @Transactional(readOnly = true)
    public String getNotaCalculadaAEF(PerfilRisco perfilRisco, PerfilAcessoEnum perfilEnum, boolean isNovoQuadro,
            boolean isAtaCorec) {
        BigDecimal notaCalculada = getNotaCalculada(perfilRisco, perfilEnum, isNovoQuadro, isAtaCorec);
        if (notaCalculada == null) {
            return Constantes.ASTERISCO_A;
        } else {
            return notaCalculada.toString().replace('.', ',');
        }
    }

    @Transactional(readOnly = true)
    public String getNotaRefinadaAEF(PerfilRisco perfilRisco, PerfilAcessoEnum perfilEnum, boolean isNovoQuadro) {
        BigDecimal notaCalculada = getNotaCalculada(perfilRisco, perfilEnum, isNovoQuadro, false);
        if (notaCalculada == null) {
            return Constantes.ASTERISCO_A;
        } else {
            ParametroNotaAQT parametroNotaAQT =
                    ParametroNotaAQTMediator.get().buscarPorMetodologiaENota(perfilRisco.getCiclo().getMetodologia(),
                            notaCalculada);
            if (parametroNotaAQT == null) {
                return "";
            } else {
                return perfilRisco.getCiclo().getMetodologia().getIsMetodologiaNova() ? parametroNotaAQT
                        .getDescricao() : parametroNotaAQT.getDescricaoValor();
            }
        }
    }

    public boolean isBotaoGerenciarVisivel(Ciclo ciclo, PerfilRisco perfilRisco, PerfilAcessoEnum perfilMenu) {
        return PerfilRiscoMediator.get().isExibirBotaoCicloEmAndamento(ciclo, perfilRisco, perfilMenu);
    }

    @Transactional(readOnly = true)
    public AnaliseQuantitativaAQT buscarAQTRascunho(AnaliseQuantitativaAQT aqt) {
        return analiseQuantitativaAQTDAO.buscarAQTRascunho(aqt);
    }

    @Transactional(readOnly = true)
    public AnaliseQuantitativaAQT buscarAQTRascunhoParametroECiclo(ParametroAQT parametro, Ciclo ciclo) {
        return analiseQuantitativaAQTDAO.buscarAQTRascunhoParametroECiclo(parametro, ciclo);
    }

    @Transactional(readOnly = true)
    public AnaliseQuantitativaAQT buscarAQTVigente(AnaliseQuantitativaAQT aqt) {
        return analiseQuantitativaAQTDAO.buscarAQTVigente(aqt);
    }

    @Transactional(readOnly = true)
    public AnaliseQuantitativaAQT buscarAQTVigentePorPerfil(ParametroAQT parametro, Ciclo ciclo, PerfilRisco perfil) {
        return analiseQuantitativaAQTDAO.buscarAQTVigentePorPerfil(parametro, ciclo, perfil);
    }

    public boolean podeAnalisar(AnaliseQuantitativaAQT analiseQuantitativaAQT) {
        return CicloMediator.get().isSupervisor(
                analiseQuantitativaAQT.getCiclo().getEntidadeSupervisionavel().getLocalizacao())
                && CicloMediator.get().cicloEmAndamento(analiseQuantitativaAQT.getCiclo())
                && (estadoAnalisado(analiseQuantitativaAQT.getEstado()) || delegacaoAnalises(analiseQuantitativaAQT));
    }

    private boolean delegacaoAnalises(AnaliseQuantitativaAQT analiseQuantitativaAQT) {
        return analiseQuantitativaAQT.getDelegacao() != null
                && (estadoAnaliseDelegada(analiseQuantitativaAQT.getEstado()) || estadoEmAnalise(analiseQuantitativaAQT
                        .getEstado()));
    }

    public boolean podeDelegar(AnaliseQuantitativaAQT analiseQuantitativaAQT) {
        return CicloMediator.get().isSupervisor(
                analiseQuantitativaAQT.getCiclo().getEntidadeSupervisionavel().getLocalizacao())
                && CicloMediator.get().cicloEmAndamento(analiseQuantitativaAQT.getCiclo())
                && verificarEstadosPrenchidoAnalisadoEmAnaliseAnaliseDelegada(analiseQuantitativaAQT);
    }

    private boolean verificarEstadosPrenchidoAnalisadoEmAnaliseAnaliseDelegada(
            AnaliseQuantitativaAQT analiseQuantitativaAQT) {
        return EstadoAQTEnum.PREENCHIDO.equals(analiseQuantitativaAQT.getEstado())
                || EstadoAQTEnum.ANALISE_DELEGADA.equals(analiseQuantitativaAQT.getEstado())
                || EstadoAQTEnum.EM_ANALISE.equals(analiseQuantitativaAQT.getEstado())
                || EstadoAQTEnum.ANALISADO.equals(analiseQuantitativaAQT.getEstado());
    }

    @Transactional
    public void alterarEstadoAnefBotaoAnalisar(AnaliseQuantitativaAQT anef) {
        if (estadoAnalisado(anef.getEstado())) {
            anef.setEstado(EstadoAQTEnum.EM_ANALISE);
            DelegacaoAQTMediator.get().excluirDelegacaoARC(anef);
        } else if (estadoAnaliseDelegada(anef.getEstado())) {
            DelegacaoAQTMediator.get().excluirDelegacaoARC(anef);
            anef.setEstado(EstadoAQTEnum.PREENCHIDO);
        } else if (estadoEmAnalise(anef.getEstado())) {
            DelegacaoAQTMediator.get().excluirDelegacaoARC(anef);
        }
        anef.setDataAnalise(null);
        anef.setOperadorAnalise(null);
        anef.setAlterarDataUltimaAtualizacao(false);
        analiseQuantitativaAQTDAO.saveOrUpdate(anef);
    }

    public void alterarEstadoAnefDesignar(AnaliseQuantitativaAQT anef) {
        if (estadoPrevisto(anef.getEstado())) {
            anef.setEstado(EstadoAQTEnum.DESIGNADO);
        } else if (estadoPreenchido(anef.getEstado()) || estadoAnaliseDelegada(anef.getEstado())
                || estadoEmAnalise(anef.getEstado()) || estadoAnalisado(anef.getEstado())) {
            anef.setEstado(EstadoAQTEnum.EM_EDICAO);
        }
        anef.setDataPreenchido(null);
        anef.setOperadorPreenchido(null);
        anef.setDataAnalise(null);
        anef.setOperadorAnalise(null);
        anef.setAlterarDataUltimaAtualizacao(false);
        analiseQuantitativaAQTDAO.saveOrUpdate(anef);
    }

    public void alterarEstadoAnefDelegado(AnaliseQuantitativaAQT anef) {
        if (estadoAnalisado(anef.getEstado())) {
            anef.setEstado(EstadoAQTEnum.EM_ANALISE);
        } else if (estadoPreenchido(anef.getEstado())) {
            anef.setEstado(EstadoAQTEnum.ANALISE_DELEGADA);
        }
        anef.setDataAnalise(null);
        anef.setOperadorAnalise(null);
        anef.setAlterarDataUltimaAtualizacao(false);
        analiseQuantitativaAQTDAO.saveOrUpdate(anef);
    }

    public void alterarEstadoAnefExcluirDesignar(AnaliseQuantitativaAQT anef) {

        anef.setEstado(EstadoAQTEnum.PREVISTO);
        anef.setDataAnalise(null);
        anef.setOperadorAnalise(null);
        anef.setAlterarDataUltimaAtualizacao(false);
        analiseQuantitativaAQTDAO.saveOrUpdate(anef);
    }

    public String mostraAlertaBotaoAnalisar(AnaliseQuantitativaAQT aqt) {
        AnaliseQuantitativaAQTMediator analiseQuantitativaAQTMediator = AnaliseQuantitativaAQTMediator.get();

        if (analiseQuantitativaAQTMediator.estadoAnalisado(aqt.getEstado())) {
            return "Atenção ANEF já analisado.";
        }

        if (AnaliseQuantitativaAQTMediator.get().estadoEmAnalise(aqt.getEstado()) && aqt.getDelegacao() != null) {
            return "Atenção ANEF com análise em andamento por delegado.";
        }

        return "";
    }

    public boolean podeExibirPainelAcoesAnef(AnaliseQuantitativaAQT analiseQuantitativaAQT) {
        return podeAnalisar(analiseQuantitativaAQT) || podeDelegar(analiseQuantitativaAQT)
                || DesignacaoAQTMediator.get().podeDesignar(analiseQuantitativaAQT)
                || DesignacaoAQTMediator.get().podeExcluirDesignar(analiseQuantitativaAQT);
    }

    public boolean anefNaPrimeiraVersaoCiclo(AnaliseQuantitativaAQT analiseQuantitativaAQT) {
        PerfilRisco primeira =
                PerfilRiscoMediator.get().buscaPrimeiraVersaoPerfil(analiseQuantitativaAQT.getCiclo().getPk());

        return analiseQuantitativaAQT.getVersaoPerfilRisco() == null ? false : analiseQuantitativaAQT
                .getVersaoPerfilRisco().getPerfisRisco().contains(primeira);

    }

    @Transactional(readOnly = true)
    public List<AnaliseQuantitativaAQT> listarANEFsNovoQuadro(Ciclo ciclo) {
        List<AnaliseQuantitativaAQT> listaAnefsRascunhos = new ArrayList<AnaliseQuantitativaAQT>();
        PesoAQT peso = null;
        AnaliseQuantitativaAQT anefFinal = null;

        for (AnaliseQuantitativaAQT anefRascunho : buscarANEFsRascunho(ciclo)) {
            AnaliseQuantitativaAQT anefVigente =
                    analiseQuantitativaAQTDAO.buscarAQTVigenteEstadoConcluido(anefRascunho);
            if (estadoAnalisado(anefRascunho.getEstado())) {
                anefFinal = anefRascunho;
                if (anefVigente != null) {
                    anefFinal.setParametroAQT(anefVigente.getParametroAQT());
                }
            } else {
                if (anefVigente == null) {
                    anefFinal = anefRascunho;
                } else {
                    anefFinal = anefVigente;
                }
            }
            peso = PesoAQTMediator.get().obterPesoRascunho(anefRascunho.getParametroAQT(), anefRascunho.getCiclo());
            anefFinal.setPesoAQT(peso);
            listaAnefsRascunhos.add(anefFinal);
        }

        return listaAnefsRascunhos;

    }

    public boolean corDestaqueAnefRascunho(AnaliseQuantitativaAQT analiseQuantitativaAQT) {
        return estadoAnalisado(analiseQuantitativaAQT.getEstado());
    }

    public String buscarResponsavel(AnaliseQuantitativaAQT aqt) {
        String responsavel = null;
        //Caso o estado do ANEF de rascunho seja 'Análise delegada', deverá listar o usuário que possui a delegação.
        if (EstadoAQTEnum.ANALISE_DELEGADA.equals(aqt.getEstado())) {
            responsavel = getNomeOperador(aqt.getDelegacao().getMatriculaServidor());
        } else if (EstadoAQTEnum.EM_ANALISE.equals(aqt.getEstado())) {
            //Caso o estado do ANEF de rascunho 'Em análise', deverá listar o usuário que está analisando o ANEF.
            responsavel = aqt.getNomeOperador();
        } else if (EstadoAQTEnum.DESIGNADO.equals(aqt.getEstado()) || EstadoAQTEnum.EM_EDICAO.equals(aqt.getEstado())) {
            //Caso o estado do ANEF de rascunho seja 'Designado' ou 'Em edição', deverá listar o usuário que possui a designação.
            if (aqt.getDesignacao() != null) {
                responsavel = getNomeOperador(aqt.getDesignacao().getMatriculaServidor());
            }
        }
        if (responsavel == null) {
            responsavel = "";
        }
        return responsavel;
    }

    public String getNomeOperador(String matricula) {
        ServidorVO servidorVO = null;
        StringBuilder odh = null;
        servidorVO = BcPessoaAdapter.get().buscarServidor(matricula);
        odh = new StringBuilder(servidorVO == null ? "" : servidorVO.getNome());
        return odh.toString();
    }

    @Transactional(readOnly = true)
    public List<AnaliseQuantitativaAQT> buscarANEFsRascunho(Ciclo ciclo) {
        return analiseQuantitativaAQTDAO.listarANEFsRascunho(ciclo);
    }

    @Transactional
    public AnaliseQuantitativaAQT publicarANEF(Ciclo ciclo, AnaliseQuantitativaAQT aqtRascunho,
            AnaliseQuantitativaAQT aqtVigente) {
        // duplicar arc
        AnaliseQuantitativaAQT novoAnef = duplicarANEFConclusaoAnalise(ciclo, aqtRascunho);
        // duplicar anexo do arc
        AnexoAQTMediator.get().duplicarAnexosANEFConclusaoAnalise(ciclo, aqtRascunho, novoAnef, false);
        // duplicar elemento do arc
        ElementoAQTMediator.get().duplicarElementosAQTConclusaoAnalise(ciclo, aqtRascunho, novoAnef, false);

        if (aqtVigente.getNotaCorecAtual() != null) {
            aqtRascunho.setNotaCorecAtual(aqtVigente.getNotaCorecAtual());
            aqtVigente.setNotaCorecAtual(null);
            analiseQuantitativaAQTDAO.update(aqtVigente);
        }

        aqtRascunho.setEstado(EstadoAQTEnum.CONCLUIDO);
        aqtRascunho.setAlterarDataUltimaAtualizacao(false);
        novoAnef.setAlterarDataUltimaAtualizacao(false);
        atualizarArc(aqtRascunho);

        DesignacaoAQTMediator.get().duplicarDesignacaoAQTConclusao(aqtRascunho, novoAnef, false);

        analiseQuantitativaAQTDAO.update(novoAnef);
        return novoAnef;
    }

    private void atualizarArc(AnaliseQuantitativaAQT anefRascunho) {
        anefRascunho.setDataConclusao(DataUtil.getDateTimeAtual());
        anefRascunho.setOperadorConclusao(((UsuarioAplicacao) UsuarioCorrente.get()).getServidorVO().getLogin());
        analiseQuantitativaAQTDAO.update(anefRascunho);
        analiseQuantitativaAQTDAO.flush(); //garante que a data de atualização do novo ANEF será posterior à do antigo
    }

    @Transactional
    private AnaliseQuantitativaAQT duplicarANEFConclusaoAnalise(Ciclo ciclo, AnaliseQuantitativaAQT arc) {
        AnaliseQuantitativaAQT novoARC = new AnaliseQuantitativaAQT();
        novoARC.setPesoAQT(arc.getPesoAQT());
        novoARC.setCiclo(arc.getCiclo());
        novoARC.setEstado(montarEstadoArc(ciclo, arc));
        novoARC.setParametroAQT(arc.getParametroAQT());
        novoARC.setOperadorAtualizacao(arc.getOperadorAtualizacao());
        novoARC.setUltimaAtualizacao(arc.getUltimaAtualizacao());
        novoARC.setAlterarDataUltimaAtualizacao(false);
        analiseQuantitativaAQTDAO.save(novoARC);
        analiseQuantitativaAQTDAO.flush();

        return novoARC;
    }

    private EstadoAQTEnum montarEstadoArc(Ciclo ciclo, AnaliseQuantitativaAQT aqt) {
        if (aqt.getDesignacao() != null
                && EntidadeSupervisionavelMediator.get()
                        .validarServidorEquipeEs(aqt.getDesignacao().getMatriculaServidor(),
                                ciclo.getEntidadeSupervisionavel().getLocalizacao())) {
            return EstadoAQTEnum.DESIGNADO;
        }
        return EstadoAQTEnum.PREVISTO;

    }

    @Transactional
    public void alterarEstadoANEFParaEmEdicao(AnaliseQuantitativaAQT anef) {
        anef.setEstado(EstadoAQTEnum.EM_EDICAO);
        analiseQuantitativaAQTDAO.update(anef);
        analiseQuantitativaAQTDAO.flush();
    }

    @Transactional
    public String ajustarNotasANEFsCorec(List<AnaliseQuantitativaAQT> listaANEF) {
        for (AnaliseQuantitativaAQT anefs : listaANEF) {
            if (anefs.getAlterouNota()) {
                analiseQuantitativaAQTDAO.saveOrUpdate(anefs);
                anefs.setAlterouNota(false);
            }
        }
        analiseQuantitativaAQTDAO.flush();
        return "Ajustes nos ANEFs salvos com sucesso.";

    }

    public List<AnaliseQuantitativaAQTVO> listarANEFsResponsaveis(Ciclo ciclo) {
        List<AnaliseQuantitativaAQT> lista = buscarANEFsRascunho(ciclo);
        List<AnaliseQuantitativaAQTVO> retorno = new ArrayList<AnaliseQuantitativaAQTVO>();
        for (AnaliseQuantitativaAQT aqt : lista) {
            AnaliseQuantitativaAQTVO vo =
                    new AnaliseQuantitativaAQTVO(aqt.getPk(), aqt.getEstado(), aqt.getParametroAQT(), null, null);
            vo.setResponsavel(buscarResponsavel(aqt));
            retorno.add(vo);
        }
        return retorno;
    }

    public boolean exibirResponsavel(EstadoAQTEnum estado) {
        return estadoDesignado(estado) || estadoEmEdicaoAnaliseDelegadaEmAnalise(estado);
    }

    public boolean exibirResponsavelTelaAnalise(EstadoAQTEnum estado, DelegacaoAQT delegacao) {
        return (estadoAnaliseDelegada(estado) || estadoEmEdicaoAnaliseDelegadaEmAnalise(estado)) && delegacao != null;
    }

    public boolean exibirPreenchidoPor(EstadoAQTEnum estado) {
        return estadoAnalisadoConcluido(estado) || estadoPreenchido(estado) || estadoEmAnalise(estado);
    }

    public boolean exibirAnalisadoPor(EstadoAQTEnum estado) {
        return estadoAnalisadoConcluido(estado);
    }

    public boolean exibirConcluidoPor(EstadoAQTEnum estado) {
        return estadoConcluido(estado);
    }

    public boolean exibirUltimaAtualizacao(EstadoAQTEnum estado) {
        return estadoPreenchido(estado) || estadoEmEdicaoAnaliseDelegadaEmAnalise(estado);
    }

    public boolean estadoAnalisadoConcluido(EstadoAQTEnum estado) {
        return estadoAnalisado(estado) || estadoConcluido(estado);
    }

    private boolean estadoEmEdicaoAnaliseDelegadaEmAnalise(EstadoAQTEnum estado) {
        return estadoEmEdicao(estado) || estadoAnaliseDelegada(estado) || estadoEmAnalise(estado);
    }

    public ServidorVO valorResponsavel(AnaliseQuantitativaAQT aqt, EntidadeSupervisionavel entidadeSupervisionavel) {

        String matriculaSupervisor;
        AnaliseQuantitativaAQT aqtBase = buscar(aqt.getPk());
        if (estadoAnaliseDelegada(aqtBase.getEstado()) || delegacaoEstadoAnalisadoEmAnalise(aqtBase)) {
            matriculaSupervisor = aqtBase.getDelegacao() == null ? "" : aqtBase.getDelegacao().getMatriculaServidor();
        } else if (estadoPreenchido(aqtBase.getEstado()) || estadoEmAnalise(aqtBase.getEstado())) {
            ServidorVO chefeLocalizacaoES = CicloMediator.get().buscarChefeAtual(entidadeSupervisionavel.getLocalizacao());
            matriculaSupervisor = chefeLocalizacaoES == null ? "" : chefeLocalizacaoES.getMatricula();
        } else {
            matriculaSupervisor = aqtBase.getDesignacao() == null ? "" : aqtBase.getDesignacao().getMatriculaServidor();
        }

        return BcPessoaAdapter.get().buscarServidor(matriculaSupervisor);
    }

    private boolean delegacaoEstadoAnalisadoEmAnalise(AnaliseQuantitativaAQT aqtBase) {
        return aqtBase.getDelegacao() != null
                && (estadoAnalisado(aqtBase.getEstado()) || estadoEmAnalise(aqtBase.getEstado()));
    }

    private boolean estadoAnaliseDelegadaEmAnalise(EstadoAQTEnum estado) {
        return estadoEmAnalise(estado) || estadoAnalisadoConcluido(estado);
    }

    public boolean exibirColunaFinal(EstadoAQTEnum estado) {
        return estadoAnaliseDelegadaEmAnalise(estado);
    }

    public boolean exibirColunaSupervisor(EstadoAQTEnum estado) {
        return estadoAnaliseDelegadaEmAnalise(estado);
    }

    public boolean exibirColunaInspetor(EstadoAQTEnum estado) {
        return estadoEmEdicao(estado) || estadoAnaliseDelegada(estado) || estadoAnaliseDelegadaEmAnalise(estado)
                || estadoPreenchido(estado);
    }

    public boolean exibirColunaVigente(EstadoAQTEnum estado) {
        return !estadoConcluido(estado);
    }

    public String notaAjustadaFinal(AnaliseQuantitativaAQT aqt, PerfilAcessoEnum perfilMenu,
            boolean isPerfilRiscoAtual) {
        String nota = "";
        if (CicloMediator.get().cicloEmAndamento(aqt.getCiclo())) {
            nota = notaAjustadaAnterior(aqt);
        } else if (CicloMediator.get().cicloCorec(aqt.getCiclo())) {
            if (PerfilAcessoEnum.SUPERVISOR.equals(perfilMenu) || PerfilAcessoEnum.GERENTE.equals(perfilMenu)) {
                nota = getNotaCorecAtual(aqt, isPerfilRiscoAtual);
            } else {
                nota = notaAjustadaAnterior(aqt);
            }
        } else {
            nota = getNotaCorecAtual(aqt, isPerfilRiscoAtual);
        }
        return nota;
    }

    private String getNotaCorecAtual(AnaliseQuantitativaAQT aqt, boolean isPerfilRiscoAtual) {
        String nota;
        if (aqt.getNotaCorecAtual() == null || !isPerfilRiscoAtual) {
            nota = notaAjustadaAnterior(aqt);
        } else {
            nota = aqt.getNotaCorecAtual().getValorString() + COREC;
        }
        return nota;
    }

    private String notaAjustadaAnterior(AnaliseQuantitativaAQT aqt) {
        String nota = "";
        if (aqt.getNotaCorecAnterior() == null) {
            nota = notaAjustadaSupervisorOuInspetor(aqt);
        } else {
            nota = aqt.getNotaCorecAnterior().getValorString() + COREC;
        }
        return nota;
    }

    public String notaAjustadaSupervisorOuInspetor(AnaliseQuantitativaAQT aqt) {
        String notaAjustada = "";
        String notaAjustadaSupervisor = AnaliseQuantitativaAQTMediator.get().notaAjustadaSupervisor(aqt);
        if (StringUtils.isNotBlank(notaAjustadaSupervisor)) {
            notaAjustada = notaAjustadaSupervisor;
        } else if (StringUtil.isVazioOuNulo(notaAjustadaSupervisor) && aqt.possuiNotaElementosSupervisor()) {
            notaAjustada = "";
        } else {
            notaAjustada = AnaliseQuantitativaAQTMediator.get().notaAjustadaInspetor(aqt);
        }
        return notaAjustada;
    }

    public String notaAjustadaInspetor(AnaliseQuantitativaAQT aqt) {
        AvaliacaoAQT avaAqtInspet =
                AvaliacaoAQTMediator.get().buscarNotaAvaliacaoANEF(aqt, PerfisNotificacaoEnum.INSPETOR);

        return avaAqtInspet == null ? "" : avaAqtInspet.getParametroNota() == null ? "" : avaAqtInspet
                .getParametroNota().getValorString();
    }

    public String notaAjustadaSupervisor(AnaliseQuantitativaAQT aqt) {
        AvaliacaoAQT avaAqtSupervisor =
                AvaliacaoAQTMediator.get().buscarNotaAvaliacaoANEF(aqt, PerfisNotificacaoEnum.SUPERVISOR);
        return avaAqtSupervisor == null ? "" : avaAqtSupervisor.getParametroNota() == null ? "" : avaAqtSupervisor
                .getParametroNota().getValorString();
    }

    public String notaAjustadaVigente(AnaliseQuantitativaAQT aqt, Boolean isPerfilRiscoAtual) {
        String notaAjustada = "";
        if (aqt.getNotaCorecAnterior() == null) {
            notaAjustada = notaAjustadaSupervisorOuInspetor(aqt);
        } else {
            notaAjustada = aqt.getNotaCorecAnterior().getValorString() + COREC;
        }
        return notaAjustada;
    }

    public String notaCorecAjustada(AnaliseQuantitativaAQT aqt) {
        String nota = "";
        if (aqt.getNotaCorecAtual() != null
                && (CicloMediator.get().cicloCorec(aqt.getCiclo()) && RegraPerfilAcessoMediator.perfilSupervisor())) {
            nota = aqt.getNotaCorecAtual().getValorString();
        } else {
            if (aqt.getNotaCorecAnterior() != null) {
                nota = aqt.getNotaCorecAnterior().getValorString();
            }
        }
        return nota;
    }

    public List<AnaliseQuantitativaAQT> listarANEFConsulta(ParametroAQT parametro, Ciclo ciclo) {
        List<AnaliseQuantitativaAQT> retorno = new ArrayList<AnaliseQuantitativaAQT>();
        AnaliseQuantitativaAQT anterior = null;
        for (AnaliseQuantitativaAQT analiseQuantitativaAQT : analiseQuantitativaAQTDAO.listarANEFConsulta(parametro,
                ciclo)) {
            if (anterior == null) {
                retorno.add(analiseQuantitativaAQT);
                anterior = analiseQuantitativaAQT;
            } else if (!(anterior.getDataFormatada().equals(analiseQuantitativaAQT.getDataFormatada()))) {
                retorno.add(analiseQuantitativaAQT);
                anterior = analiseQuantitativaAQT;
            }
        }

        return retorno;
    }

    public PerfilRisco buscarUltimoPerfilRisco(AnaliseQuantitativaAQT anef) {
        return analiseQuantitativaAQTDAO.buscarUltimoPerfilRisco(anef);
    }

    public boolean estadosPrevistoDesignado(EstadoAQTEnum estado) {
        return estadoPrevisto(estado) || estadoDesignado(estado);
    }

    public boolean estadosDesignadoEmEdicaoAnaliseDelegada(EstadoAQTEnum estado) {
        return estadoDesignado(estado) || estadoEmEdicao(estado) || estadoEmAnalise(estado)
                || estadoAnaliseDelegada(estado);
    }

    public boolean estadosDesignacaoDelegacaoInspetor(AnaliseQuantitativaAQT aqt) {
        boolean designadoEdicao =
                isResponsavelDesignacao(aqt) && (estadoDesignado(aqt.getEstado()) || estadoEmEdicao(aqt.getEstado()));
        boolean analiseDelegada =
                isResponsavelDelegacao(aqt)
                        && (estadoEmAnalise(aqt.getEstado()) || estadoAnaliseDelegada(aqt.getEstado()));
        return designadoEdicao || analiseDelegada;
    }

    public boolean estadosPrevistoDesignadoEmEdicaoDelegado(EstadoAQTEnum estado) {
        return estadosPrevistoDesignado(estado) || estadoEmEdicao(estado) || estadoAnaliseDelegada(estado);
    }

    public boolean estadosPrevistoDesignadoEmEdicao(EstadoAQTEnum estado) {
        return estadosPrevistoDesignado(estado) || estadoEmEdicao(estado);
    }

    //Supervisor: Exibida nos estados 'Em análise', 'Analisado' e 'Concluído'. 
    public boolean podeExibirColunaSupervisor(EstadoAQTEnum estado) {
        return estadoAnalisado(estado) || estadoConcluido(estado) || estadoEmAnalise(estado);
    }

    @Transactional
    public String concluirEdicaoANEFInspetor(AnaliseQuantitativaAQT aqt) {
        AnaliseQuantitativaAQT anefValidacao = buscar(aqt.getPk());
        new RegraEdicaoANEFInspetorPermissaoAlteracao(anefValidacao).validar();
        new RegraConclusaoEdicaoANEFInspetorValidacao(anefValidacao).validar();
        UsuarioAplicacao usuario = (UsuarioAplicacao) UsuarioCorrente.get();
        anefValidacao.setOperadorPreenchido(usuario.getLogin());
        anefValidacao.setDataPreenchido(DataUtil.getDateTimeAtual());
        anefValidacao.setEstado(EstadoAQTEnum.PREENCHIDO);
        anefValidacao.setAlterarDataUltimaAtualizacao(false);
        analiseQuantitativaAQTDAO.merge(anefValidacao);
        analiseQuantitativaAQTDAO.flush();
        return "ANEF concluído com sucesso.";
    }

    @Transactional
    public void alterarEstadoAnefParaEmAnalise(AnaliseQuantitativaAQT analiseQuantitativaAQT) {
        AnaliseQuantitativaAQT analiseQuantitativaAQT2 = analiseQuantitativaAQTDAO.load(analiseQuantitativaAQT.getPk());
        analiseQuantitativaAQT2.setEstado(EstadoAQTEnum.EM_ANALISE);
        analiseQuantitativaAQT2.setUltimaAtualizacao(new DateTime());
        analiseQuantitativaAQTDAO.merge(analiseQuantitativaAQT2);
        analiseQuantitativaAQTDAO.flush();
    }

    @Transactional
    public String concluirAnaliseANEFSupervisor(AnaliseQuantitativaAQT aqt, PerfilAcessoEnum perfilEnum) {
        AnaliseQuantitativaAQT anefBase = buscar(aqt.getPk());
        anefBase.setAlterarDataUltimaAtualizacao(false);
        new RegraAnaliseANEFSupervisorPermissaoAlteracao(anefBase).validar();
        new RegraBotaoAnalisarAnef(anefBase).validar();
        UsuarioAplicacao usuario = (UsuarioAplicacao) UsuarioCorrente.get();
        anefBase.setOperadorAnalise(usuario.getLogin());
        anefBase.setDataAnalise(DataUtil.getDateTimeAtual());
        if (anefBase.getDelegacao() != null) {
            delegacaoAQTDAO.delete(anefBase.getDelegacao());
        }
        anefBase.setEstado(EstadoAQTEnum.ANALISADO);

        anefBase.setValorNota(calcularNotaFinalSupervisor(anefBase));
        anefBase.setAlterarDataUltimaAtualizacao(false);
        analiseQuantitativaAQTDAO.update(anefBase);
        analiseQuantitativaAQTDAO.flush();

        String msg = "Análise do ANEF concluída. ANEF não publicado.";
        if (PerfilAcessoEnum.SUPERVISOR.equals(perfilEnum)) {
            msg += " Acesse e revise a <Síntese do ANEF> para publicar este ANEF no Perfil de Risco.";
        }
        return msg;
    }

    private BigDecimal calcularNotaFinalSupervisor(AnaliseQuantitativaAQT anef) {
        // Caso o supervisor tenha preenchido a 'Nota ajustada', a nota final é a 'Nota ajustada' do supervisor.
        if (anef.avaliacaoSupervisor() != null) {
            return anef.avaliacaoSupervisor().getParametroNota().getValor();
        } else if (anef.possuiNotaElementosSupervisor()) {
            // Senão, caso o supervisor tenha feito ajuste em alguma nota dos elementos, a nota final é a 'Nota Calculada' do supervisor.
            return new BigDecimal(anef.getMediaNotaElementosSupervisor().replace(',', '.'));
        } else if (anef.getAvaliacaoInspetor() != null) {
            // Senão, caso haja ajuste de nota do inspetor, a nota final é a 'Nota ajustada' pelo inspetor.
            return anef.getAvaliacaoInspetor().getParametroNota().getValor();
        } else {
            // Senão, a nota final é a 'Nota Calculada' do inspetor.
            return new BigDecimal(anef.getMediaNotaElementosInspetor().replace(',', '.'));
        }
    }

    public AnaliseQuantitativaAQT buscarAQTVigenteEstadoConcluido(AnaliseQuantitativaAQT aqt) {
        return analiseQuantitativaAQTDAO.buscarAQTVigenteEstadoConcluido(aqt);
    }

    public AnaliseQuantitativaAQT obterAnefVigente(AnaliseQuantitativaAQT aqt) {
        AnaliseQuantitativaAQT anefVigente = AnaliseQuantitativaAQTMediator.get().buscarAQTVigenteEstadoConcluido(aqt);
        if (anefVigente == null) {
            anefVigente = AnaliseQuantitativaAQTMediator.get().buscarAQTVigente(aqt);
        }
        anefVigente = AnaliseQuantitativaAQTMediator.get().buscar(anefVigente.getPk());
        return anefVigente;
    }

    public List<AnaliseQuantitativaAQT> listaAnefsVigentes(Ciclo ciclo) {
        return analiseQuantitativaAQTDAO.listarANEFsVigentes(ciclo);
    }

    @Transactional
    public void saveOrUpdate(AnaliseQuantitativaAQT anef) {
        analiseQuantitativaAQTDAO.saveOrUpdate(anef);
    }

    public void evict(AnaliseQuantitativaAQT anef) {
        analiseQuantitativaAQTDAO.evict(anef);
    }

    public String notaAnef(AnaliseQuantitativaAQT anef, Ciclo ciclo, PerfilAcessoEnum perfilEnum, boolean isNovoQuadro,
            PerfilRisco perfilRisco) {
        return notaAnef(anef, ciclo, perfilEnum, isNovoQuadro, perfilRisco, false);
    }

    public String notaAnef(AnaliseQuantitativaAQT anef, Ciclo ciclo, PerfilAcessoEnum perfilEnum, boolean isNovoQuadro,
            PerfilRisco perfilRisco, boolean isAtaCorec) {
        PerfilRisco perfilRiscoAtual = PerfilRiscoMediator.get().obterPerfilRiscoAtual(perfilRisco.getCiclo().getPk());
        if ((isNovoQuadro && estadoAnalisado(anef.getEstado())) || isAtaCorec) {
            return anef.getNotaSupervisorDescricaoValor();
        } else if (EstadoCicloEnum.EM_ANDAMENTO.equals(ciclo.getEstadoCiclo().getEstado())) {
            return notaAnefEstadoEmAndamento(anef);
        } else if (EstadoCicloEnum.POS_COREC.equals(ciclo.getEstadoCiclo().getEstado())
                || EstadoCicloEnum.ENCERRADO.equals(ciclo.getEstadoCiclo().getEstado())) {
            return notaAnefEstadoPosCorecEEncerrado(anef, perfilRisco, perfilRiscoAtual);
        } else if (EstadoCicloEnum.COREC.equals(ciclo.getEstadoCiclo().getEstado())) {
            return notaAnefEstadoCorec(anef, perfilEnum, perfilRisco, perfilRiscoAtual);
        }

        return "";

    }

    private String notaAnefEstadoCorec(AnaliseQuantitativaAQT anef, PerfilAcessoEnum perfilEnum,
            PerfilRisco perfilRisco, PerfilRisco perfilRiscoAtual) {
        if ((PerfilAcessoEnum.SUPERVISOR.equals(perfilEnum) || PerfilAcessoEnum.GERENTE.equals(perfilEnum))
                && anef.getNotaCorecAtual() != null && perfilRisco.getPk().equals(perfilRiscoAtual.getPk())) {
            return anef.getNotaCorecAtual().getDescricaoValor();
        }
        return anef.getNotaSupervisorDescricaoValor();
    }

    private String notaAnefEstadoEmAndamento(AnaliseQuantitativaAQT anef) {
        if (anef.getNotaCorecAnterior() != null) {
            return anef.getNotaCorecAnterior().getDescricaoValor();
        }
        return anef.getNotaSupervisorDescricaoValor();
    }

    private String notaAnefEstadoPosCorecEEncerrado(AnaliseQuantitativaAQT anef, PerfilRisco perfilRisco,
            PerfilRisco perfilRiscoAtual) {
        if (anef.getNotaCorecAtual() != null && perfilRisco.getPk().equals(perfilRiscoAtual.getPk())) {
            return anef.getNotaCorecAtual().getDescricaoValor();
        }
        return notaAnefEstadoEmAndamento(anef);
    }

    public boolean possuiNotaElementosSupervisor(AnaliseQuantitativaAQT anef) {
        List<ElementoAQT> elementos = ElementoAQTMediator.get().buscarElementosOrdenadosDoAnef(anef.getPk());
        for (ElementoAQT elemento : elementos) {
            if (elemento.getParametroNotaSupervisor() != null) {
                return true;
            }
        }
        return false;
    }

    public void limpaSessao() {
        // A limpeza da sessão evita o erro de mais de um objeto com o mesmo id
        analiseQuantitativaAQTDAO.getSessionFactory().getCurrentSession().clear();
    }

    public List<AnaliseQuantitativaAQTVO> consultarHistoricoAQTfinal(ConsultaAnaliseQuantitativaAQTVO consulta) {
        return analiseQuantitativaAQTDAO.consultarHistoricoAQTfinal(consulta);
    }
    
    @Transactional(readOnly = true)
    public List<AnaliseQuantitativaAQT> buscarAnefPorCiclo(Integer pkCiclo) {
        List<VersaoPerfilRisco> versoes = null;
        Ciclo ciclo = CicloMediator.get().buscarCicloPorPK(pkCiclo);
        List<Ciclo> ciclosES = CicloMediator.get().consultarCiclosEntidadeSupervisionavel(
                ciclo.getEntidadeSupervisionavel().getConglomeradoOuCnpj(), false);
        if (CollectionUtils.isNotEmpty(ciclosES) && ciclosES.size() > 1) {
            PerfilRisco primeiroPerfilAnef = PerfilRiscoMediator.get().buscarPrimeiroPerfilComAnef(pkCiclo);
            versoes = VersaoPerfilRiscoMediator.get().buscarVersaoAnefPerfilRisco(primeiroPerfilAnef.getPk());
        }
        return analiseQuantitativaAQTDAO.buscarAnefPorCiclo(pkCiclo, versoes);
    }
    
    @Transactional
    public void atualizarDadosNovaMetodologia(List<AnaliseQuantitativaAQT> anefs, Metodologia metodologia, boolean isMedia) {
        for (AnaliseQuantitativaAQT anef : anefs) {
            AnaliseQuantitativaAQT anefInicializado = buscar(anef.getPk());
            if (anefInicializado.getNotaSupervisor() != null) {
                if (isMedia) {
                    anefInicializado.setValorNota(calcularNotaFinalSupervisor(anefInicializado));
                } else {
                    anefInicializado.setValorNota(anefInicializado.getNotaSupervisor().getValor());
                    salvarAvaliacaoArrasto(anefInicializado, metodologia);
                }
                anefInicializado.setNotaSupervisor(null);
            }
            if (anefInicializado.getNotaCorecAnterior() != null) {
                ParametroNotaAQT novaNotaCorecAnterior =
                        ParametroNotaAQTMediator.get().buscarPorNota(metodologia,
                                anefInicializado.getNotaCorecAnterior().getValor());
                anefInicializado.setNotaCorecAnterior(novaNotaCorecAnterior);
            }
            if (anefInicializado.getNotaCorecAtual() != null) {
                ParametroNotaAQT novaNotaCorecAtual =
                        ParametroNotaAQTMediator.get().buscarPorNota(metodologia,
                                anefInicializado.getNotaCorecAtual().getValor());
                anefInicializado.setNotaCorecAtual(novaNotaCorecAtual);
            }
            ElementoAQTMediator.get().atualizarDadosNovaMetodologia(anefInicializado.getElementos(), metodologia);
            AvaliacaoAQTMediator.get().atualizarDadosNovaMetodologia(anefInicializado.getAvaliacoesAnef(), metodologia);
            anefInicializado.setAlterarDataUltimaAtualizacao(false);
            analiseQuantitativaAQTDAO.update(anefInicializado);
        }
    }
    
    private void salvarAvaliacaoArrasto(AnaliseQuantitativaAQT anef, Metodologia metodologia) {
        if (anef.avaliacaoSupervisor() == null) {
            if (anef.possuiNotaElementosSupervisor()) {
                incluirAvaliacao(anef, anef.getParametroMenorNotaElementosSupervisorInspetor(), metodologia);
            } else if (anef.getAvaliacaoInspetor() == null && anef.possuiNotaElementosInspetor()) {
                incluirAvaliacao(anef, anef.getParametroMenorNotaElementosInspetor(), metodologia);
            }
        }
    }

    private void incluirAvaliacao(AnaliseQuantitativaAQT anef, ParametroNotaAQT notaArrasto, Metodologia metodologia) {
        ParametroNotaAQT novaNota = ParametroNotaAQTMediator.get().buscarPorNota(metodologia, notaArrasto.getValor());
        AvaliacaoAQT avaliacao = new AvaliacaoAQT();
        avaliacao.setAnaliseQuantitativaAQT(anef);
        avaliacao.setJustificativa("Migração nota calculada.");
        avaliacao.setParametroNota(novaNota);
        avaliacao.setPerfil(PerfisNotificacaoEnum.SUPERVISOR);
        AvaliacaoAQTMediator.get().incluir(avaliacao);
    }

}
