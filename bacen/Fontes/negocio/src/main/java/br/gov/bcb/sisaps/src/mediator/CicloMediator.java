package br.gov.bcb.sisaps.src.mediator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Hibernate;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.seguranca.UsuarioCorrente;
import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.comum.pessoa.negocio.componenteorganizacional.ConsultaChefia;
import br.gov.bcb.sisaps.adaptadores.pessoa.BcPessoaAdapter;
import br.gov.bcb.sisaps.adaptadores.pessoa.ChefiaVO;
import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.seguranca.UsuarioAplicacao;
import br.gov.bcb.sisaps.src.dao.CicloDAO;
import br.gov.bcb.sisaps.src.dao.EstadoCicloDAO;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.CelulaRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.EntidadeSupervisionavel;
import br.gov.bcb.sisaps.src.dominio.EntidadeSupervisionavelETL;
import br.gov.bcb.sisaps.src.dominio.EstadoCiclo;
import br.gov.bcb.sisaps.src.dominio.HistoricoLegado;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.Metodologia;
import br.gov.bcb.sisaps.src.dominio.NotaMatriz;
import br.gov.bcb.sisaps.src.dominio.ParametroNota;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.SupervisaoCiclo;
import br.gov.bcb.sisaps.src.dominio.VersaoPerfilRisco;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.NotaAjustadaAEF;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroNotaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.PesoAQT;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoCicloEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoObjetoVersionadorEnum;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.NotaAjustadaAEFMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.ParametroNotaAQTMediator;
import br.gov.bcb.sisaps.src.validacao.RegraCicloInclusaoValidacaoCampos;
import br.gov.bcb.sisaps.src.validacao.RegraCriterioPesquisaSemResultado;
import br.gov.bcb.sisaps.src.vo.CicloVO;
import br.gov.bcb.sisaps.src.vo.ConsultaCicloVO;
import br.gov.bcb.sisaps.src.vo.ConsultaEntidadeSupervisionavelVO;
import br.gov.bcb.sisaps.src.vo.EntidadeSupervisionavelVO;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.consulta.Ordenacao;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import br.gov.bcb.sisaps.util.geral.DataUtil;
import br.gov.bcb.sisaps.util.geral.DataUtilLocalDate;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.ConstantesMensagens;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;
import br.gov.bcb.utils.logging.BCLogFactory;
import br.gov.bcb.utils.logging.BCLogger;

@Service
@Transactional(readOnly = true)
public class CicloMediator extends AbstractMediatorPaginado<CicloVO, Integer, ConsultaCicloVO> {

    private static final String DATA_INICIO_COREC_COREC_PREVISTO = "Corec inicial";
    private static final String DATA_FIM_COREC_COREC_PREVISTO = "Corec final";

    private static final String DATA_PREVISAO_COREC = "dataPrevisaoCorec";

    private static final BCLogger LOG = BCLogFactory.getLogger("CicloMediator");

    @Autowired
    private CicloDAO cicloDAO;

    @Autowired
    private EstadoCicloDAO estadoCicloDAO;

    @Override
    public List<CicloVO> consultar(ConsultaCicloVO consulta) {
        return consultarSemValidacao(consulta, true);
    }

    public List<CicloVO> consultarSemValidacao(ConsultaCicloVO consulta, boolean isValidar) {
        List<CicloVO> lista = cicloDAO.consultar(consulta);
        if (isValidar) {
            new RegraCriterioPesquisaSemResultado().validar(lista);
        }
        return lista;
    }

    @Transactional(readOnly = true)
    public Ciclo buscarCicloPorPK(Integer id) {
        Ciclo ciclo = cicloDAO.buscarCicloPorPK(id);
        inicializarDependencias(ciclo);
        return ciclo;
    }

    public List<CicloVO> consultarES(ConsultaCicloVO consulta) {
        return cicloDAO.consultar(consulta);
    }

    public static CicloMediator get() {
        return SpringUtils.get().getBean(CicloMediator.class);
    }

    @Override
    protected CicloDAO getDao() {
        return cicloDAO;
    }

    public void evict(Ciclo ciclo) {
        cicloDAO.evict(ciclo);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void incluir(Ciclo ciclo, String strDataInicio, boolean isBatch) {
        ciclo.setMatriculaSegSubstSupervisor("");
        validarDataInicio(strDataInicio);
        Date dataInicio = DataUtil.dateFromString(strDataInicio);
        ciclo.setDataInicio(dataInicio);
        validarInclusao(ciclo, isBatch);
        if (isBatch) {
            ciclo.setDataPrevisaoCorec(dataInicio);
        } else {
            addDataCorec(ciclo);
        }

        Matriz matriz = MatrizCicloMediator.get().incluirMatrizCiclo(ciclo);
        EstadoCicloMediator.get().incluir(ciclo);
        cicloDAO.save(ciclo);
        matriz.setCiclo(ciclo);
        MatrizCicloMediator.get().alterar(matriz);
        List<PesoAQT> listaPesosAQTs = new ArrayList<PesoAQT>();
        List<AnaliseQuantitativaAQT> listaAQTs =
                AnaliseQuantitativaAQTMediator.get().criarAQTElementosItens(ciclo, listaPesosAQTs);
        PerfilRiscoMediator.get().gerarVersaoPerfilRiscoInicial(ciclo, listaAQTs, listaPesosAQTs);
        cicloDAO.flush();
    }

    private void validarDataInicio(String strDataInicio) {
        ArrayList<ErrorMessage> erros = new ArrayList<ErrorMessage>();
        SisapsUtil.validarDataInvalida(strDataInicio, "Data de início do ciclo", Constantes.FORMATO_DATA_COM_BARRAS,
                erros);
        SisapsUtil.lancarNegocioException(erros);
    }

    private void addDataCorec(Ciclo ciclo) {
        LocalDate data = new LocalDate(ciclo.getDataInicio());
        data = data.plusYears(ciclo.getEntidadeSupervisionavel().getQuantidadeAnosPrevisaoCorec());
        ciclo.setDataPrevisaoCorec(data.toDate());
    }

    public void validarInclusao(Ciclo ciclo, boolean isBatch) {
        new RegraCicloInclusaoValidacaoCampos(ciclo).validar(isBatch);
    }

    public boolean existeCicloVigenteParaES(EntidadeSupervisionavel entidadeSupervisionavel) {
        ConsultaCicloVO consulta = new ConsultaCicloVO();
        EntidadeSupervisionavelVO entidadeVO = new EntidadeSupervisionavelVO();
        entidadeVO.setPk(entidadeSupervisionavel.getPk());
        consulta.setEntidadeSupervisionavel(entidadeVO);
        consulta.setEstados(incluiEstadosAtivos());
        List<CicloVO> lista = cicloDAO.consultar(consulta);
        return !lista.isEmpty();
    }

    private List<EstadoCicloEnum> incluiEstadosAtivos() {
        List<EstadoCicloEnum> estados = new ArrayList<EstadoCicloEnum>();
        estados.add(EstadoCicloEnum.EM_ANDAMENTO);
        estados.add(EstadoCicloEnum.COREC);
        estados.add(EstadoCicloEnum.POS_COREC);
        return estados;
    }

    public Ciclo load(Ciclo ciclo) {
        Ciclo result = cicloDAO.getRecord(ciclo.getPk());
        inicializarDependencias(result);
        return result;
    }

    public Ciclo loadPK(Integer pk) {
        Ciclo result = cicloDAO.load(pk);
        inicializarDependencias(result);
        return result;
    }

    public void inicializarDependencias(Ciclo result) {
        if (result.getMatriz() != null) {
            Hibernate.initialize(result.getMatriz());
            if (result.getMatriz().getAtividades() != null) {
                Hibernate.initialize(result.getMatriz().getAtividades());
            }
        }
        if (result.getMetodologia() != null) {
            Hibernate.initialize(result.getMetodologia());
            MetodologiaMediator.get().inicializarDependencias(result.getMetodologia());
        }
        if (result.getEntidadeSupervisionavel() != null) {
            Hibernate.initialize(result.getEntidadeSupervisionavel());
        }
    }

    @Transactional
    public void alterar(Ciclo ciclo) {
        cicloDAO.update(ciclo);
    }

    @Transactional
    public void merge(Ciclo ciclo) {
        cicloDAO.merge(ciclo);
    }

    public List<Ciclo> consultarCiclosEntidadeSupervisionavel(String cnpjEntidadeSupervisionavel,
            boolean excluirCiclosMigrados) {
        List<Ciclo> ciclos =
                cicloDAO.consultarCiclosEntidadeSupervisionavel(cnpjEntidadeSupervisionavel, excluirCiclosMigrados);
        ArrayList<ErrorMessage> erros = new ArrayList<ErrorMessage>();
        SisapsUtil.adicionarErro(erros, new ErrorMessage(ConstantesMensagens.MSG_APS_PERFIL_RISCO_ERRO_001),
                ciclos.isEmpty());
        SisapsUtil.lancarNegocioException(erros);
        return ciclos;
    }

    public Ciclo consultarUltimoCicloEmAndamentoCorecES(Integer pkEntidadeSupervisionavel) {
        return cicloDAO.consultarUltimoCicloEmAndamentoCorecES(pkEntidadeSupervisionavel);
    }

    public Ciclo consultarUltimoCicloCorecPosEncerradoES(Integer pkEntidadeSupervisionavel) {
        return cicloDAO.consultarUltimoCicloCorecPosEncerradoES(pkEntidadeSupervisionavel);
    }

    public Ciclo consultarUltimoCicloPosCorecES(Integer pkEntidadeSupervisionavel) {
        return cicloDAO.consultarUltimoCicloPosCorecES(pkEntidadeSupervisionavel);
    }

    @Transactional(readOnly = true)
    public Ciclo consultarUltimoCicloPosCorecEncerradoES(Integer pkEntidadeSupervisionavel) {
        return cicloDAO.consultarUltimoCicloPosCorecEncerradoES(pkEntidadeSupervisionavel);
    }

    public ServidorVO buscarChefeAtual(String localizacao) {
        return buscarChefeAtual(localizacao, null);
    }

    public ServidorVO buscarChefeAtual(String localizacao, Date dataBase) {
    	SupervisaoCiclo supervisaoCiclo = SupervisaoCicloMediator.get().buscarSupervisao(dataBase, localizacao);
    	if (supervisaoCiclo != null) {
    		return getServidor(supervisaoCiclo);
    	}
        ConsultaChefia consulta = criarConsultaChefia(localizacao, dataBase);
        ChefiaVO chefia = BcPessoaAdapter.get().buscarChefia(consulta);
        if (chefia == null) {
            System.out.println("##BUGLOCALIZACAO buscarChefeAtual chefia nula ");
            return null;
        } else {
            if (chefia.getChefeTitular() == null) {
                System.out.println("##BUGLOCALIZACAO buscarChefeAtual chefe titular null ");
                return null;
            } else {
                return BcPessoaAdapter.get().buscarServidor(chefia.getChefeTitular().getMatricula(), dataBase);
            }
        }
    }

    private ServidorVO getServidor(SupervisaoCiclo supervisaoCiclo) {
    	ServidorVO servidor = new ServidorVO();
		servidor.setNome(supervisaoCiclo.getNomeSupervisor());
		servidor.setMatricula(supervisaoCiclo.getMatriculaSupervisor());
		servidor.setNomeChefe(supervisaoCiclo.getNomeGerente());
		servidor.setMatriculaChefe(supervisaoCiclo.getMatriculaGerente());
    	servidor.setLocalizacao(supervisaoCiclo.getLocalizacao());
    	return servidor;
    }
    
    private ConsultaChefia criarConsultaChefia(String localizacao, Date dataBase) {
        ConsultaChefia consulta = new ConsultaChefia();
        consulta.setComponenteOrganizacionalRotulo(localizacao);
        System.out.println("##BUGLOCALIZACAO criarConsultaChefia data: " + dataBase);
        System.out.println("##BUGLOCALIZACAO criarConsultaChefia localizacao: " + localizacao);
        if (dataBase != null && !dataBase.after(DataUtil.getDateTimeAtual().toDate())) {
            System.out.println("##BUGLOCALIZACAO criarConsultaChefia setou data: " + dataBase);
            consulta.setDataBase(dataBase);
        }
        return consulta;
    }

    public boolean isSupervisor(String localizacaoES) {
        return RegraPerfilAcessoMediator.get().isAcessoPermitido(PerfilAcessoEnum.SUPERVISOR)
                && ((UsuarioAplicacao) UsuarioCorrente.get()).getServidorVO().getLocalizacaoAtual(
                		PerfilAcessoEnum.SUPERVISOR).trim().equals(localizacaoES.trim());
    }

    public HistoricoLegado buscarHistoricoLegadoCiclo(Integer cicloPk) {
        return cicloDAO.buscarHistoricoLegadoCiclo(cicloPk);
    }

    @Transactional(propagation = Propagation.REQUIRED, timeout = 240)
    public void iniciarCiclosAutomaticamente(List<EntidadeSupervisionavelETL> entidadesETL) {
        Metodologia metodologiaDefault = null;
        if (CollectionUtils.isNotEmpty(entidadesETL)) {
            metodologiaDefault = MetodologiaMediator.get().buscarMetodologiaDefault();
            if (metodologiaDefault == null) {
                LOG.info("Metodologia default não encontrada. Por favor incluir "
                        + "uma metodologia default para que as ES's sejam incluídas.");
            } else {
                // Criar novo registro na tabela ENS_ENTIDADE_SUPERVISIONAVEL com os dados que estão na tabela EET_ETL_ENS
                for (EntidadeSupervisionavelETL entidadeSupervisionavelETL : entidadesETL) {
                    ConsultaEntidadeSupervisionavelVO consulta = new ConsultaEntidadeSupervisionavelVO();
                    consulta.setPossuiPrioridade(true);
                    consulta.setConglomeradoOuCnpj(entidadeSupervisionavelETL.getCnpj());
                    List<EntidadeSupervisionavelVO> entidadesNaBaseList =
                            EntidadeSupervisionavelMediator.get().consultar(consulta);

                    if (entidadesNaBaseList == null || entidadesNaBaseList.isEmpty()) {

                        LOG.info("\n\n##### Processando a ES " + entidadeSupervisionavelETL.getNome() + "("
                                + entidadeSupervisionavelETL.getCnpj() + ")");
                        LOG.info("Incluindo novo registro na tabela ENS_ENTIDADE_SUPERVISIONAVEL "
                                + "com os dados encontrados na tabela EET_ETL_ENS.");
                        EntidadeSupervisionavel novaEntidadeSupervisionavel =
                                EntidadeSupervisionavelMediator.get().incluirNovaEntidadeSupervisionavel(
                                        metodologiaDefault, entidadeSupervisionavelETL);
                        LOG.info("Novo registro na tabela ENS_ENTIDADE_SUPERVISIONAVEL incluído com sucesso.");
                        // Caso tenha prioridade, iniciar o ciclo automaticamente
                        iniciarNovoCiclo(novaEntidadeSupervisionavel);
                    }
                }
            }
        }
    }

    private void iniciarNovoCiclo(EntidadeSupervisionavel novaEntidadeSupervisionavel) {
        if (novaEntidadeSupervisionavel.getPrioridade() != null) {
            LOG.info("Entidade possui prioridade, iniciando novo ciclo.");
            Ciclo novoCiclo = new Ciclo();
            novoCiclo.setEntidadeSupervisionavel(novaEntidadeSupervisionavel);
            novoCiclo.setMetodologia(novaEntidadeSupervisionavel.getMetodologia());
            incluir(novoCiclo, "01/01/1900", true);
            LOG.info("Novo ciclo iniciado.");
        }
    }

    public List<CicloVO> consultarCicloCorec() {
        return consultaCicloPorEstado(EstadoCicloEnum.COREC);
    }

    private List<CicloVO> consultaCicloPorEstado(EstadoCicloEnum estado) {
        List<EstadoCicloEnum> estados = new ArrayList<EstadoCicloEnum>();
        estados.add(estado);
        return consultaCicloPorEstado(estados);
    }

    public List<CicloVO> consultaCicloPorEstado(List<EstadoCicloEnum> estados) {
        ConsultaCicloVO consulta = new ConsultaCicloVO();
        consulta.setEstados(estados);
        Ordenacao ordenacao = new Ordenacao();
        ordenacao.setPropriedade(DATA_PREVISAO_COREC);
        ordenacao.setCrescente(true);
        consulta.setOrdenacao(ordenacao);
        List<CicloVO> lista = cicloDAO.consultar(consulta);
        return lista;
    }

    public List<CicloVO> consultarCicloPosCorec() {
        return consultaCicloPorEstado(EstadoCicloEnum.POS_COREC);
    }

    public boolean cicloEmAndamento(Ciclo ciclo) {
        return EstadoCicloEnum.EM_ANDAMENTO.equals(ciclo.getEstadoCiclo().getEstado());
    }

    public boolean cicloCorec(Ciclo ciclo) {
        return EstadoCicloEnum.COREC.equals(ciclo.getEstadoCiclo().getEstado());
    }
    
    public boolean cicloPosCorecEncerrado(Ciclo ciclo) {
        return EstadoCicloEnum.POS_COREC.equals(ciclo.getEstadoCiclo().getEstado())
                || EstadoCicloEnum.ENCERRADO.equals(ciclo.getEstadoCiclo().getEstado());
    }

    public boolean exibirSecaoCorec(Ciclo ciclo, PerfilRisco perfilRisco, PerfilAcessoEnum perfilMenu) {
        return PerfilRiscoMediator.get().isExibirBotaoCicloEmAndamento(ciclo, perfilRisco, perfilMenu);
    }

    public boolean exibirBotaoCorec(Ciclo ciclo, PerfilRisco perfilRisco, PerfilAcessoEnum perfilMenu) {
        return PerfilRiscoMediator.get().isExibirBotaoCicloEmAndamento(ciclo, perfilRisco, perfilMenu);
    }

    @Transactional
    public void iniciarCorec(Ciclo ciclo) {
        EstadoCiclo estadoCiclo = ciclo.getEstadoCiclo();
        estadoCiclo.setEstado(EstadoCicloEnum.COREC);
        estadoCiclo.setCiclo(ciclo);
        ciclo.setEstadoCiclo(estadoCiclo);
        ciclo.setDataInicioCorec(DataUtil.getDateTimeAtual().toDate());
        estadoCicloDAO.merge(estadoCiclo);
        cicloDAO.merge(ciclo);
    }

    @Transactional
    public void alterarStatusCiclo(Ciclo ciclo) {
        EstadoCiclo estadoCiclo = ciclo.getEstadoCiclo();
        estadoCiclo.setEstado(EstadoCicloEnum.EM_ANDAMENTO);
        estadoCiclo.setCiclo(ciclo);
        ciclo.setEstadoCiclo(estadoCiclo);
        ciclo.setDataInicioCorec(null);
        estadoCicloDAO.merge(estadoCiclo);
        cicloDAO.merge(ciclo);
    }

    public ParametroNota notaQualitativa(Ciclo ciclo, PerfilAcessoEnum perfil) {
        PerfilRisco perfilAtual = PerfilRiscoMediator.get().obterPerfilRiscoAtual(ciclo.getPk());

        NotaMatriz notaMatriz = NotaMatrizMediator.get().buscarPorPerfilRisco(perfilAtual.getPk());

        if (notaMatriz == null || notaMatriz.getNotaFinalMatriz() == null) {
            List<CelulaRiscoControle> listaChoices =
                    CelulaRiscoControleMediator.get().buscarCelulasPorPerfilRisco(perfilAtual.getPk());

            List<VersaoPerfilRisco> versoesPerfilRiscoCelulas =
                    VersaoPerfilRiscoMediator.get().buscarVersoesPerfilRisco(perfilAtual.getPk(),
                            TipoObjetoVersionadorEnum.CELULA_RISCO_CONTROLE);

            AvaliacaoRiscoControle arc =
                    AvaliacaoRiscoControleMediator.get().buscarArcExternoPorPerfilRisco(perfilAtual.getPk());
            String notaCalculadaFinal =
                    MatrizCicloMediator.get().notaCalculadaFinal(ciclo.getMatriz(), listaChoices,
                            versoesPerfilRiscoCelulas, true, perfil, perfilAtual, arc);

            ParametroNota notaRefinada =
                    ParametroNotaMediator.get().buscarPorMetodologiaENota(ciclo.getMetodologia(),
                            new BigDecimal(notaCalculadaFinal), false);

            return ParametroNotaMediator.get().buscarPorPK(notaRefinada.getPk());
        } else {

            return ParametroNotaMediator.get().buscarPorPK(notaMatriz.getNotaFinalMatriz().getPk());
        }
    }

    public boolean exibirBotaoVoltar(Ciclo ciclo) {
        return cicloCorec(ciclo) || cicloEmAndamento(ciclo) || cicloPosCorecEncerrado(ciclo);
    }

    public ParametroNotaAQT notaQuantitativa(Ciclo ciclo, PerfilAcessoEnum perfilMenu) {
        PerfilRisco perfilAtual = PerfilRiscoMediator.get().obterPerfilRiscoAtual(ciclo.getPk());
        NotaAjustadaAEF notaAjustada = NotaAjustadaAEFMediator.get().buscarNotaAjustadaAEF(ciclo, perfilAtual);
        if (notaAjustada == null || notaAjustada.getParamentroNotaAQT() == null) {
            Metodologia metodologia = ciclo.getMetodologia();
            ParametroNotaAQT nota = null;
            if (metodologia.getIsCalculoMedia() == null || metodologia.getIsCalculoMedia().equals(SimNaoEnum.NAO)) {
                nota =
                        ParametroNotaAQTMediator.get().buscarPorMetodologiaENota(
                                ciclo.getMetodologia(),
                                new BigDecimal(AnaliseQuantitativaAQTMediator.get().getNotaRefinadaAEF(perfilAtual,
                                        perfilMenu, false).replace(",", ".")));
            } else {
                nota =
                        ParametroNotaAQTMediator.get()
                                .buscarPorDescricao(
                                        ciclo.getMetodologia(),
                                        AnaliseQuantitativaAQTMediator.get().getNotaRefinadaAEF(perfilAtual,
                                                perfilMenu, false));
            }
            
            return ParametroNotaAQTMediator.get().buscarPorPK(nota.getPk());
        } else {
            if (notaAjustada.getParamentroNotaAQT() == null) {
                return new ParametroNotaAQT();
            } else {
                return notaAjustada.getParamentroNotaAQT();
            }

        }
    }

    @Transactional
    public String encerrarCiclo(Ciclo ciclo) {
        AnexoPosCorecMediator.get().validarAnexosEncerrarCiclo(ciclo);
        EstadoCiclo estadoCiclo = ciclo.getEstadoCiclo();
        estadoCiclo.setEstado(EstadoCicloEnum.ENCERRADO);
        estadoCiclo.setCiclo(ciclo);
        ciclo.setEstadoCiclo(estadoCiclo);
        estadoCicloDAO.merge(estadoCiclo);
        cicloDAO.merge(ciclo);
        return "O Ciclo foi encerrado com sucesso.";
    }

    public List<Ciclo> consultarCiclosEmAndamento() {
        return cicloDAO.consultarCiclosEmAndamento();
    }

    @Transactional
    public void criarEstruturaANEF(Ciclo ciclo) {
        LOG.info("Processando ciclo de ID " + ciclo.getPk());
        // Criar os ANEFs Vigentes/Rascunho
        // Criar os Pesos Vigentes/Rascunho
        LOG.info("Criando os ANEFs e os Pesos Vigentes/Rascunho...");
        List<PesoAQT> listaPesosAQTs = new ArrayList<PesoAQT>();
        List<AnaliseQuantitativaAQT> listaAnefs =
                AnaliseQuantitativaAQTMediator.get().criarAQTElementosItens(ciclo, listaPesosAQTs);
        LOG.info("ANEFs e os Pesos Vigentes/Rascunho criados com sucesso.");

        // Pendurar os ANEFs Vigentes em todos os Perfis de risco
        // Pendurar os Pesos Vigentes em todos os Perfis de risco
        List<PerfilRisco> perfisRisco = PerfilRiscoMediator.get().consultarPerfisRiscoCiclo(ciclo.getPk(), false);
        for (PerfilRisco perfilRisco : perfisRisco) {
            for (AnaliseQuantitativaAQT anef : listaAnefs) {
                if (anef.getVersaoPerfilRisco() != null) {
                    perfilRisco.addVersaoPerfilRisco(anef.getVersaoPerfilRisco());
                }
            }
            for (PesoAQT peso : listaPesosAQTs) {
                if (peso.getVersaoPerfilRisco() != null) {
                    perfilRisco.addVersaoPerfilRisco(peso.getVersaoPerfilRisco());
                }
            }
            PerfilRiscoMediator.get().saveOrUpdate(perfilRisco);
        }
    }

    public Ciclo getUltimoCiclo() {
        return cicloDAO.getUltimoCiclo();
    }

    public List<CicloVO> getPendenciasGerente() {
        return cicloDAO.getPendenciasGerente();
    }

    public List<CicloVO> listarComitesRealizar(ConsultaCicloVO consulta) {
        List<CicloVO> retorno = new ArrayList<CicloVO>();
        List<CicloVO> entidadesComCicloEmAndamentoCorec = cicloDAO.consultarCicloAndamentoCorecES(consulta);
        retorno.addAll(entidadesComCicloEmAndamentoCorec);
        return retorno;
    }

    public List<CicloVO> listarComitesRealizados(ConsultaCicloVO consulta) {
        List<CicloVO> retorno = new ArrayList<CicloVO>();
        List<CicloVO> entidadesComCicloEmAndamentoCorec = cicloDAO.consultarCicloPosCorecEncerradoES(consulta);
        retorno.addAll(entidadesComCicloEmAndamentoCorec);
        return retorno;
    }

    public void validarDataInicio(String strDataInicio, String strDataFim, ArrayList<ErrorMessage> erros) {

        if (strDataInicio != null) {
            SisapsUtil.validarDataInvalida(strDataInicio, DATA_INICIO_COREC_COREC_PREVISTO,
                    Constantes.FORMATO_DATA_COM_BARRAS, erros);
        }
        if (strDataFim != null) {
            SisapsUtil.validarDataInvalida(strDataFim, DATA_FIM_COREC_COREC_PREVISTO,
                    Constantes.FORMATO_DATA_COM_BARRAS, erros);
        }

        if (erros.isEmpty()) {
            if (strDataInicio != null && strDataFim != null) {
                LocalDate dataInicio = DataUtilLocalDate.stringToLocalDate(strDataInicio);
                LocalDate dataFim = DataUtilLocalDate.stringToLocalDate(strDataFim);
                if (dataFim.isBefore(dataInicio) && !dataInicio.equals(dataFim)) {
                    SisapsUtil.adicionarErro(erros, new ErrorMessage("O valor do campo '"
                            + DATA_FIM_COREC_COREC_PREVISTO + "' precisa ser maior ou igual ao valor do campo '"
                            + DATA_INICIO_COREC_COREC_PREVISTO + "'."));
                }
            }
        }
        SisapsUtil.lancarNegocioException(erros);
    }

    public List<CicloVO> consultaCiclosParaEnvioEmailDisponibilidade(DateTime dataParametro) {
        return cicloDAO.consultaCiclosParaEnvioEmail(true, dataParametro);
    }

    public List<CicloVO> consultaCiclosParaEnvioEmailApresentacao(DateTime dataParametro) {
        return cicloDAO.consultaCiclosParaEnvioEmail(false, dataParametro);
    }

    public List<Ciclo> consultarCiclosMigracao() {
        return cicloDAO.consultarCiclosMigracao();
    }

    public List<Ciclo> consultarCiclosAndamentoCorec() {
        return cicloDAO.consultarCiclosAndamentoCorec();
    }
    
    public Ciclo consultarCicloAnteriorEntidadeSupervisionavel(Ciclo ciclo, String cnpjEntidadeSupervisionavel) {
        return cicloDAO.consultarCicloAnteriorEntidadeSupervisionavel(ciclo, cnpjEntidadeSupervisionavel);
    }
    
    public List<Ciclo> consultarCiclosAndamentoNovaMetodologia() {
        return cicloDAO.consultarCiclosAndamentoNovaMetodologia();
    }
    
    public List<Ciclo> consultarCiclosNovaMetodologiaCorec(String identificadorMigracao) {
        return cicloDAO.consultarCiclosNovaMetodologiaCorec(identificadorMigracao);
    }
    
}
