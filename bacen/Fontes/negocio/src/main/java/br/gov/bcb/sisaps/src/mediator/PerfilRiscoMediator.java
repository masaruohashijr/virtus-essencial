package br.gov.bcb.sisaps.src.mediator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.src.dao.PerfilRiscoDAO;
import br.gov.bcb.sisaps.src.dao.analisequantitativa.QuadroPosicaoFinanceiraDAO;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.CelulaRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.ConclusaoES;
import br.gov.bcb.sisaps.src.dominio.EntidadeSupervisionavel;
import br.gov.bcb.sisaps.src.dominio.EstadoCiclo;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.NotaMatriz;
import br.gov.bcb.sisaps.src.dominio.PerfilAtuacaoES;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.PerspectivaES;
import br.gov.bcb.sisaps.src.dominio.SinteseDeRisco;
import br.gov.bcb.sisaps.src.dominio.SituacaoES;
import br.gov.bcb.sisaps.src.dominio.VersaoPerfilRisco;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.DataBaseES;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.QuadroPosicaoFinanceira;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.PesoAQT;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoObjetoVersionadorEnum;
import br.gov.bcb.sisaps.src.mediator.analisequantitativa.DataBaseESMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.src.vo.ConsultaPerfilRiscoVO;
import br.gov.bcb.sisaps.src.vo.PerfilRiscoVO;
import br.gov.bcb.sisaps.src.vo.analisequantitativa.QuadroPosicaoFinanceiraVO;
import br.gov.bcb.sisaps.util.geral.DataUtil;
import br.gov.bcb.sisaps.util.objetos.IObjetoVersionador;
import br.gov.bcb.sisaps.util.objetos.ObjetoVersionadorAuditavelVersionado;

@Service
public class PerfilRiscoMediator extends AbstractMediatorPaginado<PerfilRiscoVO, Integer, ConsultaPerfilRiscoVO> {

    @Autowired
    private PerfilRiscoDAO perfilRiscoDAO;

    @Autowired
    private QuadroPosicaoFinanceiraDAO quadroPosicaoFinanceiraDAO;

    @Override
    protected PerfilRiscoDAO getDao() {
        return perfilRiscoDAO;
    }

    public static PerfilRiscoMediator get() {
        return SpringUtils.get().getBean(PerfilRiscoMediator.class);
    }

    @Transactional
    public void gerarVersaoPerfilRiscoInicial(Ciclo ciclo, List<AnaliseQuantitativaAQT> listaAQTs,
            List<PesoAQT> listaPesosAQTs) {
        PerfilRisco perfilRisco = new PerfilRisco();
        perfilRisco.setCiclo(ciclo);
        perfilRisco.addVersaoPerfilRisco(ciclo.getEstadoCiclo().getVersaoPerfilRisco());

        for (AnaliseQuantitativaAQT aqt : listaAQTs) {
            if (aqt.getVersaoPerfilRisco() != null) {
                perfilRisco.addVersaoPerfilRisco(aqt.getVersaoPerfilRisco());
            }
        }

        for (PesoAQT pesoAQT : listaPesosAQTs) {
            if (pesoAQT.getVersaoPerfilRisco() != null) {
                perfilRisco.addVersaoPerfilRisco(pesoAQT.getVersaoPerfilRisco());
            }
        }

        if (ciclo.getEntidadeSupervisionavel().getVersaoPerfilRisco() == null) {
            VersaoPerfilRiscoMediator.get().criarVersao(ciclo.getEntidadeSupervisionavel(),
                    TipoObjetoVersionadorEnum.ENTIDADE_SUPERVISIONAVEL);
            EntidadeSupervisionavelMediator.get().alterar(ciclo.getEntidadeSupervisionavel());
        }

        perfilRisco.addVersaoPerfilRisco(ciclo.getEntidadeSupervisionavel().getVersaoPerfilRisco());

        perfilRisco.setDataCriacao(DataUtil.getDateTimeAtual());
        perfilRiscoDAO.save(perfilRisco);
    }

    @Transactional
    public VersaoPerfilRisco gerarNovaVersaoPerfilRisco(Ciclo ciclo, IObjetoVersionador original,
            TipoObjetoVersionadorEnum tipoObjetoVersionador) {
        PerfilRisco novoPerfilRisco = new PerfilRisco();

        PerfilRisco perfilRiscoAtual = obterPerfilRiscoAtual(ciclo.getPk());
        for (VersaoPerfilRisco versaoOriginal : perfilRiscoAtual.getVersoesPerfilRisco()) {
            if (original == null || original.getVersaoPerfilRisco() == null
                    || !versaoOriginal.getPk().equals(original.getVersaoPerfilRisco().getPk())) {
                novoPerfilRisco.addVersaoPerfilRisco(versaoOriginal);
            }
        }

        VersaoPerfilRisco novaVersao = new VersaoPerfilRisco();
        novaVersao.setTipoObjetoVersionador(tipoObjetoVersionador);
        novoPerfilRisco.addVersaoPerfilRisco(novaVersao);

        setarDadosNovoPerfilRisco(ciclo, novoPerfilRisco);
        perfilRiscoDAO.save(novoPerfilRisco);
        return novaVersao;
    }

    @Transactional
    public VersaoPerfilRisco incluirVersaoPerfilRiscoAtual(Ciclo ciclo, IObjetoVersionador objetoVersionador,
            TipoObjetoVersionadorEnum tipoObjetoVersionador) {
        if (objetoVersionador.getVersaoPerfilRisco() == null) {
            VersaoPerfilRiscoMediator.get().criarVersao(objetoVersionador, tipoObjetoVersionador);
        }
        PerfilRisco perfilRiscoAtual = obterPerfilRiscoAtual(ciclo.getPk());
        perfilRiscoAtual.addVersaoPerfilRisco(objetoVersionador.getVersaoPerfilRisco());
        perfilRiscoDAO.save(perfilRiscoAtual);
        return objetoVersionador.getVersaoPerfilRisco();
    }

    @Transactional
    public void incluirVersaoPerfilRiscoAtual(Ciclo ciclo,
            List<? extends ObjetoVersionadorAuditavelVersionado> objetosVersionador,
            TipoObjetoVersionadorEnum tipoObjetoVersionador) {
        if (CollectionUtils.isNotEmpty(objetosVersionador)) {
            PerfilRisco perfilRiscoAtual = obterPerfilRiscoAtual(ciclo.getPk());
            for (ObjetoVersionadorAuditavelVersionado objetoVersionador : objetosVersionador) {
                if (objetoVersionador.getVersaoPerfilRisco() == null) {
                    VersaoPerfilRisco novaVersao =
                            VersaoPerfilRiscoMediator.get().criarVersao(objetoVersionador, tipoObjetoVersionador);
                    objetoVersionador.setVersaoPerfilRisco(novaVersao);
                }
                perfilRiscoAtual.addVersaoPerfilRisco(objetoVersionador.getVersaoPerfilRisco());
            }
            perfilRiscoDAO.save(perfilRiscoAtual);
        }
    }

    @Transactional(readOnly = true)
    public PerfilRisco obterPerfilRiscoAtual(Integer pkCiclo) {
        PerfilRisco perfilRisco = perfilRiscoDAO.obterPerfilRiscoAtual(pkCiclo);
        inicializarReferencias(perfilRisco);
        return perfilRisco;
    }

    @Transactional(readOnly = true)
    public PerfilRisco obterPerfilRiscoPorPk(Integer pkPerfilRisco) {
        PerfilRisco perfilRisco = perfilRiscoDAO.load(pkPerfilRisco);
        inicializarReferencias(perfilRisco);
        return perfilRisco;
    }

    private void inicializarReferencias(PerfilRisco perfilRisco) {
        if (perfilRisco != null) {
            if (!Hibernate.isInitialized(perfilRisco.getVersoesPerfilRisco())) {
                Hibernate.initialize(perfilRisco.getVersoesPerfilRisco());
            }
            if (!Hibernate.isInitialized(perfilRisco.getCiclo())) {
                Hibernate.initialize(perfilRisco.getCiclo());
            }
            if (!Hibernate.isInitialized(perfilRisco.getCiclo().getEntidadeSupervisionavel())) {
                Hibernate.initialize(perfilRisco.getCiclo().getEntidadeSupervisionavel());
            }
            if (!Hibernate.isInitialized(perfilRisco.getCiclo().getMatriz())) {
                Hibernate.initialize(perfilRisco.getCiclo().getMatriz());
            }
            if (!Hibernate.isInitialized(perfilRisco.getCiclo().getMetodologia())) {
                Hibernate.initialize(perfilRisco.getCiclo().getMetodologia());
            }
        }
    }

    public List<PerfilRisco> consultarPerfisRiscoCiclo(Integer pkCiclo, boolean isAgruparPorData) {
        List<PerfilRisco> consultarPerfisRiscoCiclo = perfilRiscoDAO.consultarPerfisRiscoCiclo(pkCiclo);
        if (isAgruparPorData) {
            consultarPerfisRiscoCiclo = agruparPorData(consultarPerfisRiscoCiclo);
        }
        return consultarPerfisRiscoCiclo;
    }

    public PerfilRisco buscaPrimeiraVersaoPerfil(Integer pkCiclo) {
        return perfilRiscoDAO.buscaPrimeiraVersaoPerfil(pkCiclo);
    }

    private List<PerfilRisco> agruparPorData(List<PerfilRisco> consultarPerfisRiscoCiclo) {
        List<PerfilRisco> retorno = consultarPerfisRiscoCiclo;
        List<PerfilRisco> remover = new ArrayList<PerfilRisco>();
        PerfilRisco item = new PerfilRisco();
        for (PerfilRisco perfilRisco : retorno) {
            if (item.getDataCriacaoFormatadaSemHora().equals(perfilRisco.getDataCriacaoFormatadaSemHora())) {
                remover.add(perfilRisco);
            }
            item = perfilRisco;
        }
        retorno.removeAll(remover);
        return retorno;
    }

    public EstadoCiclo getEstadoCicloPerfilRisco(PerfilRisco perfilRisco) {
        return EstadoCicloMediator.get().buscarPorPerfilRisco(perfilRisco.getPk());
    }

    @Transactional(readOnly = true)
    public EntidadeSupervisionavel getEntidadeSupervisionavelPerfilRisco(PerfilRisco perfilRisco) {
        return EntidadeSupervisionavelMediator.get().buscarPorPerfilRisco(perfilRisco.getPk());
    }

    public Matriz getMatrizAtualPerfilRisco(PerfilRisco perfilRisco) {
        VersaoPerfilRisco versao =
                VersaoPerfilRiscoMediator.get().buscarVersaoPerfilRisco(perfilRisco.getPk(),
                        TipoObjetoVersionadorEnum.MATRIZ);
        if (versao != null) {
            return MatrizCicloMediator.get().buscarPorVersaoPerfilRisco(versao.getPk());
        }
        return null;
    }

    public List<SinteseDeRisco> getSintesesDeRiscoPerfilRisco(PerfilRisco perfilRisco) {
        List<VersaoPerfilRisco> versoes =
                VersaoPerfilRiscoMediator.get().buscarVersoesPerfilRisco(perfilRisco.getPk(),
                        TipoObjetoVersionadorEnum.SINTESE_RISCO);
        return SinteseDeRiscoMediator.get().buscarSintesesDeRiscoPerfilRisco(versoes);
    }

    public boolean versaoEmMaisDeUmPerfilRisco(VersaoPerfilRisco versaoPerfilRisco) {
        return perfilRiscoDAO.versaoEmMaisDeUmPerfilRisco(versaoPerfilRisco);
    }

    @Transactional
    public void excluirVersaoDoPerfilRiscoAtualEIncluirNovaVersao(Ciclo ciclo, VersaoPerfilRisco versaoAExcluir,
            IObjetoVersionador objetoVersionador, TipoObjetoVersionadorEnum tipoObjetoVersionador) {
        VersaoPerfilRisco novaVersao =
                VersaoPerfilRiscoMediator.get().criarVersao(objetoVersionador, tipoObjetoVersionador);
        PerfilRisco perfilRiscoAtual = obterPerfilRiscoAtual(ciclo.getPk());
        perfilRiscoAtual.addVersaoPerfilRisco(novaVersao);
        if (versaoAExcluir != null) {
            VersaoPerfilRisco versaoPerfilRiscoAExcluir = VersaoPerfilRiscoMediator.get().load(versaoAExcluir.getPk());
            perfilRiscoAtual.removeVersaoPerfilRisco(versaoPerfilRiscoAExcluir);
        }
        perfilRiscoDAO.update(perfilRiscoAtual);
    }

    @Transactional
    public void excluirVersaoDoPerfilRiscoAtual(Ciclo ciclo, VersaoPerfilRisco versaoAExcluir) {
        PerfilRisco perfilRiscoAtual = obterPerfilRiscoAtual(ciclo.getPk());
        perfilRiscoAtual.getVersoesPerfilRisco().remove(versaoAExcluir);
        perfilRiscoDAO.update(perfilRiscoAtual);
    }

    public void excluirVersoesDoPerfilRisco(PerfilRisco perfilRisco, List<VersaoPerfilRisco> versoesAExcluir) {
        for (VersaoPerfilRisco versaoAExcluir : versoesAExcluir) {
            perfilRisco.getVersoesPerfilRisco().remove(versaoAExcluir);
        }
        perfilRiscoDAO.update(perfilRisco);
    }

    public void excluirVersoesDoPerfilRiscoAtual(Ciclo ciclo, List<VersaoPerfilRisco> versoesAExcluir) {
        PerfilRisco perfilRiscoAtual = obterPerfilRiscoAtual(ciclo.getPk());
        excluirVersoesDoPerfilRisco(perfilRiscoAtual, versoesAExcluir);
    }

    public void excluirVersaoDoPerfilRisco(PerfilRisco perfilRisco, VersaoPerfilRisco versaoAExcluir) {
        perfilRisco.getVersoesPerfilRisco().remove(versaoAExcluir);
        perfilRiscoDAO.update(perfilRisco);
    }

    public void gerarVersaoPerfilRiscoPublicacaoCelulasARCs(Ciclo ciclo,
            Map<CelulaRiscoControle, CelulaRiscoControle> mapeamentoCelulaAnteriorCelulaNova,
            List<AvaliacaoRiscoControle> novosARCs) {
        PerfilRisco perfilRiscoAtual = obterPerfilRiscoAtual(ciclo.getPk());
        PerfilRisco novoPerfilRisco = new PerfilRisco();
        setarDadosNovoPerfilRisco(ciclo, novoPerfilRisco);
        for (VersaoPerfilRisco versaoOriginal : perfilRiscoAtual.getVersoesPerfilRisco()) {
            if (versaoOriginal.getTipoObjetoVersionador().equals(TipoObjetoVersionadorEnum.ARC)
                    || versaoOriginal.getTipoObjetoVersionador()
                            .equals(TipoObjetoVersionadorEnum.CELULA_RISCO_CONTROLE)) {
                boolean isVersaoExistente = false;
                for (Entry<CelulaRiscoControle, CelulaRiscoControle> entry : mapeamentoCelulaAnteriorCelulaNova
                        .entrySet()) {
                    isVersaoExistente =
                            verificarVersaoExistenteECriarNova(novoPerfilRisco, versaoOriginal, entry, novosARCs);
                    if (isVersaoExistente) {
                        break;
                    }
                }
                if (!isVersaoExistente) {
                    novoPerfilRisco.addVersaoPerfilRisco(versaoOriginal);
                }
            } else {
                novoPerfilRisco.addVersaoPerfilRisco(versaoOriginal);
            }
        }
        perfilRiscoDAO.save(novoPerfilRisco);
        for (CelulaRiscoControle novaCelula : mapeamentoCelulaAnteriorCelulaNova.values()) {
            if (novosARCs.contains(novaCelula.getArcRisco())) {
                novaCelula.getArcRisco().setAlterarDataUltimaAtualizacao(false);
                AvaliacaoRiscoControleMediator.get().alterar(novaCelula.getArcRisco());
            }
            if (novosARCs.contains(novaCelula.getArcControle())) {
                novaCelula.getArcControle().setAlterarDataUltimaAtualizacao(false);
                AvaliacaoRiscoControleMediator.get().alterar(novaCelula.getArcControle());
            }
            CelulaRiscoControleMediator.get().incluir(novaCelula);
        }
    }

    private boolean verificarVersaoExistenteECriarNova(PerfilRisco novoPerfilRisco, VersaoPerfilRisco versaoOriginal,
            Entry<CelulaRiscoControle, CelulaRiscoControle> entry, List<AvaliacaoRiscoControle> novosARCs) {
        boolean isVersaoExistente = false;
        if (versaoOriginal.getTipoObjetoVersionador().equals(TipoObjetoVersionadorEnum.ARC)) {
            if (novosARCs.contains(entry.getValue().getArcRisco())
                    && versaoOriginal.getPk().equals(entry.getKey().getArcRisco().getVersaoPerfilRisco().getPk())) {
                AvaliacaoRiscoControle novoARCRisco = entry.getValue().getArcRisco();
                criarNovaVersaoARC(novoPerfilRisco, novoARCRisco);
                isVersaoExistente = true;
            }
            if (novosARCs.contains(entry.getValue().getArcControle())
                    && versaoOriginal.getPk().equals(entry.getKey().getArcControle().getVersaoPerfilRisco().getPk())) {
                AvaliacaoRiscoControle novoARCControle = entry.getValue().getArcControle();
                criarNovaVersaoARC(novoPerfilRisco, novoARCControle);
                isVersaoExistente = true;
            }
        } else if (versaoOriginal.getPk().equals(entry.getKey().getVersaoPerfilRisco().getPk())) {
            CelulaRiscoControle novaCelula = entry.getValue();
            VersaoPerfilRisco novaVersao = new VersaoPerfilRisco();
            novaVersao.setTipoObjetoVersionador(TipoObjetoVersionadorEnum.CELULA_RISCO_CONTROLE);
            novoPerfilRisco.addVersaoPerfilRisco(novaVersao);
            novaCelula.setVersaoPerfilRisco(novaVersao);
            isVersaoExistente = true;
        }
        return isVersaoExistente;
    }

    private void criarNovaVersaoARC(PerfilRisco novoPerfilRisco, AvaliacaoRiscoControle novoARCControle) {
        VersaoPerfilRisco novaVersao = new VersaoPerfilRisco();
        novaVersao.setTipoObjetoVersionador(TipoObjetoVersionadorEnum.ARC);
        novoPerfilRisco.addVersaoPerfilRisco(novaVersao);
        novoARCControle.setVersaoPerfilRisco(novaVersao);
    }

    private void setarDadosNovoPerfilRisco(Ciclo ciclo, PerfilRisco novoPerfilRisco) {
        novoPerfilRisco.setCiclo(ciclo);
        novoPerfilRisco.setDataCriacao(DataUtil.getDateTimeAtual());
    }

    @Transactional(readOnly = true)
    public PerfilAtuacaoES getPerfilAtuacaoESPerfilRisco(PerfilRisco perfilRisco) {

        PerfilAtuacaoES perfil = PerfilAtuacaoESMediator.get().buscarPorPerfilRisco(perfilRisco.getPk());
        if (perfil != null) {
            perfil.setAlterarDataUltimaAtualizacao(false);
            PerfilAtuacaoESMediator.get().evict(perfil);
        }
        return perfil;
    }

    @Transactional(readOnly = true)
    public ConclusaoES getConclusaoESPerfilRisco(PerfilRisco perfilRisco) {
        ConclusaoES conclusao = ConclusaoESMediator.get().buscarPorPerfilRisco(perfilRisco.getPk());
        if (conclusao != null) {
            conclusao.setAlterarDataUltimaAtualizacao(false);
            ConclusaoESMediator.get().evict(conclusao);
        }
        return conclusao;
    }

    @Transactional(readOnly = true)
    public PerspectivaES getPerspectivaESPerfilRisco(PerfilRisco perfilRisco) {
        PerspectivaES perspectivaES = PerspectivaESMediator.get().buscarPorPerfilRisco(perfilRisco.getPk());
        if (perspectivaES != null) {
            Hibernate.initialize(perspectivaES.getParametroPerspectiva());
            PerspectivaESMediator.get().evict(perspectivaES);
        }
        return perspectivaES;
    }

    @Transactional(readOnly = true)
    public SituacaoES getSituacaoESPerfilRisco(PerfilRisco perfilRisco) {
        SituacaoES situacaoES = SituacaoESMediator.get().buscarPorPerfilRisco(perfilRisco.getPk());
        if (situacaoES != null) {
            situacaoES.setAlterarDataUltimaAtualizacao(false);
            Hibernate.initialize(situacaoES.getParametroSituacao());
            SituacaoESMediator.get().evict(situacaoES);
        }
        return situacaoES;
    }

    public NotaMatriz getNotaMatrizPerfilRisco(PerfilRisco perfilRisco) {
        return NotaMatrizMediator.get().buscarPorPerfilRisco(perfilRisco.getPk());
    }

    @Transactional(readOnly = true)
    public QuadroPosicaoFinanceiraVO obterQuadroVigente(PerfilRisco perfilRiscoAtual) {
        VersaoPerfilRisco versao =
                VersaoPerfilRiscoMediator.get().buscarVersaoPerfilRisco(perfilRiscoAtual.getPk(),
                        TipoObjetoVersionadorEnum.QUADRO_POSISAO_FINANCEIRA);
        if (versao != null) {
            QuadroPosicaoFinanceira quadroPosicaoFinanceira =
                    quadroPosicaoFinanceiraDAO.obterQuadroVigente(perfilRiscoAtual, versao.getPk());
            if (quadroPosicaoFinanceira != null) {
                DataBaseES dataBaseES =
                        DataBaseESMediator.get()
                                .buscarPorIdentificador(
                                        quadroPosicaoFinanceira.getCodigoDataBase(),
                                        quadroPosicaoFinanceira.getCiclo().getEntidadeSupervisionavel()
                                                .getConglomeradoOuCnpj());
                return new QuadroPosicaoFinanceiraVO(dataBaseES, quadroPosicaoFinanceira, perfilRiscoAtual);
            }
        }
        return new QuadroPosicaoFinanceiraVO();
    }

    public List<AnaliseQuantitativaAQT> getAnalisesQuantitativasAQTPerfilRisco(PerfilRisco perfilRisco) {
        List<VersaoPerfilRisco> versoes =
                VersaoPerfilRiscoMediator.get().buscarVersoesPerfilRisco(perfilRisco.getPk(),
                        TipoObjetoVersionadorEnum.AQT);
        return AnaliseQuantitativaAQTMediator.get().buscarAQTsPerfilRisco(versoes);
    }

    public PerfilRisco gerarVersaoPerfilRiscoPublicacaoSinteseAQT(Ciclo ciclo, PerfilRisco perfilRiscoAtual) {
        PerfilRisco novoPerfilRisco = new PerfilRisco();
        setarDadosNovoPerfilRisco(ciclo, novoPerfilRisco);
        novoPerfilRisco.setVersoesPerfilRisco(new ArrayList<VersaoPerfilRisco>());
        novoPerfilRisco.getVersoesPerfilRisco().addAll(perfilRiscoAtual.getVersoesPerfilRisco());
        return novoPerfilRisco;

    }

    @Transactional
    public void saveOrUpdate(PerfilRisco perfilRisco) {
        perfilRiscoDAO.saveOrUpdate(perfilRisco);
        perfilRiscoDAO.flush();
    }

    public boolean isExibirBotaoCicloEmAndamento(Ciclo ciclo, PerfilRisco perfilRiscoSelecionado,
            PerfilAcessoEnum perfilMemu) {
        PerfilRisco perfilRiscoAtual = PerfilRiscoMediator.get().obterPerfilRiscoAtual(ciclo.getPk());
        return CicloMediator.get().cicloEmAndamento(ciclo)
                && perfilRiscoAtual.getPk().equals(perfilRiscoSelecionado.getPk())
                && PerfilAcessoEnum.SUPERVISOR.equals(perfilMemu);
    }

    public boolean isExibirBotaoCicloEmAndamentoSupervisorGerente(Ciclo ciclo, PerfilRisco perfilRiscoSelecionado,
            PerfilAcessoEnum perfilMemu) {
        PerfilRisco perfilRiscoAtual = PerfilRiscoMediator.get().obterPerfilRiscoAtual(ciclo.getPk());
        return CicloMediator.get().cicloEmAndamento(ciclo)
                && perfilRiscoAtual.getPk().equals(perfilRiscoSelecionado.getPk())
                && (PerfilAcessoEnum.SUPERVISOR.equals(perfilMemu) || PerfilAcessoEnum.GERENTE.equals(perfilMemu));
    }

    public boolean isExibirBotaoCicloEmAndamentoGerente(Ciclo ciclo, PerfilRisco perfilRiscoSelecionado,
            PerfilAcessoEnum perfilMemu) {
        PerfilRisco perfilRiscoAtual = PerfilRiscoMediator.get().obterPerfilRiscoAtual(ciclo.getPk());
        return (CicloMediator.get().cicloEmAndamento(ciclo) || CicloMediator.get().cicloCorec(ciclo))
                && perfilRiscoAtual.getPk().equals(perfilRiscoSelecionado.getPk())
                && PerfilAcessoEnum.GERENTE.equals(perfilMemu);
    }

    public boolean perfilAtual(Ciclo ciclo, PerfilRisco perfil) {
        PerfilRisco perfilRiscoAtual = PerfilRiscoMediator.get().obterPerfilRiscoAtual(ciclo.getPk());
        PerfilRisco perfilRiscoAnterior = null;
        if (perfil == null) {
            return true;
        } else {
            perfilRiscoAnterior = PerfilRiscoMediator.get().obterPerfilRiscoPorPk(perfil.getPk());
            return perfilRiscoAtual != null && perfilRiscoAnterior != null
                    && perfilRiscoAtual.equals(perfilRiscoAnterior);
        }
    }

    @Transactional(readOnly = true)
    public PerfilRisco buscarPrimeiroPerfil(VersaoPerfilRisco versaoPerfilRisco) {
        return perfilRiscoDAO.buscarPrimeiroPerfil(versaoPerfilRisco);
    }

    @Transactional(readOnly = true)
    public PerfilRisco buscarPerfilRiscoAnterior(PerfilRisco perfilRisco) {
        return perfilRiscoDAO.buscarPerfilRiscoAnterior(perfilRisco);
    }

    @Transactional(readOnly = true)
    public AvaliacaoRiscoControle getArcExterno(PerfilRisco perfilRisco) {
        return AvaliacaoRiscoControleMediator.get().buscarArcExternoPorPerfilRisco(perfilRisco.getPk());
    }

    public void gerarVersaoPerfilRiscoPublicacaoArcExterno(Ciclo ciclo, AvaliacaoRiscoControle novoArcExterno,
            AvaliacaoRiscoControle antigoArcExterno) {
        PerfilRisco perfilRiscoAtual = obterPerfilRiscoAtual(ciclo.getPk());
        PerfilRisco novoPerfilRisco = new PerfilRisco();
        setarDadosNovoPerfilRisco(ciclo, novoPerfilRisco);
        novoPerfilRisco.setVersoesPerfilRisco(new ArrayList<VersaoPerfilRisco>());
        novoPerfilRisco.getVersoesPerfilRisco().addAll(perfilRiscoAtual.getVersoesPerfilRisco());
        perfilRiscoDAO.save(novoPerfilRisco);

        if (antigoArcExterno != null) {
            novoPerfilRisco.getVersoesPerfilRisco().remove(antigoArcExterno.getVersaoPerfilRisco());
        }

        VersaoPerfilRisco novaVersao =
                VersaoPerfilRiscoMediator.get().criarVersao(novoArcExterno, TipoObjetoVersionadorEnum.ARC);
        novoPerfilRisco.getVersoesPerfilRisco().add(novaVersao);
        novoArcExterno.setVersaoPerfilRisco(novaVersao);
        PerfilRiscoMediator.get().saveOrUpdate(novoPerfilRisco);
        AvaliacaoRiscoControleMediator.get().alterar(novoArcExterno);
    }

    @Transactional(readOnly = true)
    public PerfilRisco buscarPrimeiroPerfilComAnef(Integer pkCiclo) {
        return perfilRiscoDAO.buscarPrimeiroPerfilComAnef(pkCiclo);
    }

}
