package br.gov.bcb.sisaps.src.mediator.analisequantitativa;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Hibernate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.sisaps.src.dao.analisequantitativa.AnaliseQuantitativaEconomicaETLDAO;
import br.gov.bcb.sisaps.src.dao.analisequantitativa.ContaQuadroPosicaoFinanceiraDAO;
import br.gov.bcb.sisaps.src.dao.analisequantitativa.QuadroPosicaoFinanceiraDAO;
import br.gov.bcb.sisaps.src.dao.analisequantitativa.ResultadoQuadroPosicaoFinanceiraDAO;
import br.gov.bcb.sisaps.src.dominio.AnexoQuadroPosicaoFinanceira;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.VersaoPerfilRisco;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.AnaliseQuantitativaEconomicaETL;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.ContaAnaliseQuantitativaETL;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.ContaQuadroPosicaoFinanceira;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.DataBaseES;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.LayoutContaAnaliseQuantitativa;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.OutraInformacaoQuadroPosicaoFinanceira;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.QuadroPosicaoFinanceira;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.ResultadoQuadroPosicaoFinanceira;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoConta;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoInformacaoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoObjetoVersionadorEnum;
import br.gov.bcb.sisaps.src.mediator.AnexoQuadroPosicaoFinanceiraMediator;
import br.gov.bcb.sisaps.src.mediator.EventoConsolidadoMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.src.mediator.VersaoPerfilRiscoMediator;
import br.gov.bcb.sisaps.src.vo.analisequantitativa.IndiceVO;
import br.gov.bcb.sisaps.src.vo.analisequantitativa.PatrimonioVO;
import br.gov.bcb.sisaps.src.vo.analisequantitativa.QuadroPosicaoFinanceiraVO;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import br.gov.bcb.sisaps.util.enumeracoes.TipoSubEventoPerfilRiscoSRC;
import br.gov.bcb.sisaps.util.spring.SpringUtilsExtended;

@Service
public class QuadroPosicaoFinanceiraMediator {

    @Autowired
    private PerfilRiscoMediator perfilRiscoMediator;
    @Autowired
    private QuadroPosicaoFinanceiraDAO quadroPosicaoFinanceiraDAO;
    @Autowired
    private AnaliseQuantitativaEconomicaETLDAO analiseQuantitativaEconomicaETLDAO;
    @Autowired
    private ContaQuadroPosicaoFinanceiraDAO contaQuadroPosicaoFinanceiraDAO;
    @Autowired
    private ResultadoQuadroPosicaoFinanceiraDAO resultadoQuadroPosicaoFinanceiraDAO;
    @Autowired
    private EventoConsolidadoMediator eventoConsolidadoMediator;
    private Boolean ajusteRealizado = Boolean.FALSE;

    public static QuadroPosicaoFinanceiraMediator get() {
        return SpringUtilsExtended.get().getBean(QuadroPosicaoFinanceiraMediator.class);
    }

    @Transactional
    public QuadroPosicaoFinanceiraVO preencherNovoQuadroNovoModelo(DataBaseES dataBaseES, PerfilRisco perfilRisco) {
        QuadroPosicaoFinanceira quadroBase =
                quadroPosicaoFinanceiraDAO.buscarQuadroRascunhoPorDataBase(dataBaseES, perfilRisco.getCiclo());
        
        if (quadroBase == null && dataBaseES != null) {
            QuadroPosicaoFinanceira quadroRascunhoAtual =
                    QuadroPosicaoFinanceiraMediator.get().buscarQuadroRascunhoPorDataBase(perfilRisco);
            QuadroPosicaoFinanceira quadroVigente = obterUltimaVersaoQuadroVigente(perfilRisco);
            
            QuadroPosicaoFinanceira novoQuadro = criarNovoQuadroNovoModelo(perfilRisco, dataBaseES);
            quadroPosicaoFinanceiraDAO.desconectar(novoQuadro);
            String cnpjES = perfilRisco.getCiclo().getEntidadeSupervisionavel().getConglomeradoOuCnpj();
            OutraInformacaoQuadroPosicaoFinanceiraMediator.get().incluirPatrimonios(
                    perfilRisco, dataBaseES, cnpjES, novoQuadro);
            OutraInformacaoQuadroPosicaoFinanceiraMediator.get().incluirIndices(
                    perfilRisco, dataBaseES, cnpjES, novoQuadro);
            OutraInformacaoQuadroPosicaoFinanceiraMediator.get().incluirResultados(
                    perfilRisco, dataBaseES, cnpjES, novoQuadro);
            incluirContas(dataBaseES, novoQuadro, perfilRisco);
            QuadroPosicaoFinanceira novoQuadroAjustado = incluirAjustesQuadroVigenteNovoModelo(dataBaseES, perfilRisco);
            
            if (quadroRascunhoAtual == null && quadroVigente != null) {
                AnexoQuadroPosicaoFinanceiraMediator.get().duplicarAnexos(quadroVigente, novoQuadro, true, false);
            }
            quadroPosicaoFinanceiraDAO.desconectar(quadroRascunhoAtual);
            return new QuadroPosicaoFinanceiraVO(dataBaseES, novoQuadroAjustado, perfilRisco);
        }
        
        return new QuadroPosicaoFinanceiraVO(dataBaseES, quadroBase, perfilRisco);
    }

    @Transactional
    public QuadroPosicaoFinanceira incluirAjustesQuadroVigenteNovoModelo(DataBaseES dataBaseES, PerfilRisco perfilRisco) {
        QuadroPosicaoFinanceira quadroVigente = obterUltimaVersaoQuadroVigente(perfilRisco);
        QuadroPosicaoFinanceira quadroRascunho =
                dataBaseES == null ? QuadroPosicaoFinanceiraMediator.get().buscarQuadroRascunhoPorDataBase(perfilRisco)
                        : QuadroPosicaoFinanceiraMediator.get()
                                .buscarQuadroRascunhoPorDataBase(dataBaseES, perfilRisco);
        if (quadroVigente != null && quadroRascunho != null) {
            OutraInformacaoQuadroPosicaoFinanceiraMediator.get().incluirAjustesPatrimonioQuadroVigente(
                    quadroVigente, quadroRascunho);
            OutraInformacaoQuadroPosicaoFinanceiraMediator.get().incluirAjustesIndicesQuadroVigente(
                    quadroVigente, quadroRascunho, perfilRisco, dataBaseES);
            OutraInformacaoQuadroPosicaoFinanceiraMediator.get().incluirAjustesResultadosQuadroVigente(
                    quadroVigente, quadroRascunho, dataBaseES);
            incluirAjustesContas(quadroVigente, quadroRascunho);
        }
        return quadroRascunho;
    }

    private QuadroPosicaoFinanceira criarNovoQuadroNovoModelo(PerfilRisco perfilRisco, DataBaseES dataBaseES) {
        QuadroPosicaoFinanceira quadroPosicaoFinanceira = new QuadroPosicaoFinanceira();
        quadroPosicaoFinanceira.setCodigoDataBase(dataBaseES.getCodigoDataBase());
        quadroPosicaoFinanceira.setCiclo(perfilRisco.getCiclo());
        quadroPosicaoFinanceiraDAO.save(quadroPosicaoFinanceira);
        return quadroPosicaoFinanceiraDAO.load(quadroPosicaoFinanceira.getPk());
    }

    @Transactional
    public void descartarAlteracoesNovoQuadroAtual(DataBaseES dataBaseES, PerfilRisco perfilRiscoAtual) {
        QuadroPosicaoFinanceira quadroNaBase =
                QuadroPosicaoFinanceiraMediator.get().buscarQuadroRascunhoPorDataBase(perfilRiscoAtual);
        if (quadroNaBase != null && !quadroNaBase.getCodigoDataBase().equals(dataBaseES.getCodigoDataBase())) {
            descartarQuadro(quadroNaBase, dataBaseES, perfilRiscoAtual);
        }
    }

    private void descartarQuadro(QuadroPosicaoFinanceira quadro, DataBaseES dataBaseES, PerfilRisco perfilRiscoAtual) {
        for (ContaQuadroPosicaoFinanceira conta : contaQuadroPosicaoFinanceiraDAO.buscarPorQuadro(quadro)) {
            contaQuadroPosicaoFinanceiraDAO.delete(conta);
            quadroPosicaoFinanceiraDAO.desconectar(conta.getQuadroPosicaoFinanceira());
        }
        
        QuadroPosicaoFinanceiraVO novoQuadro =
                QuadroPosicaoFinanceiraMediator.get().preencherNovoQuadroNovoModelo(dataBaseES, perfilRiscoAtual);
        QuadroPosicaoFinanceira quadroNovo = buscarQuadroPorPk(novoQuadro.getPk());

        List<AnexoQuadroPosicaoFinanceira> listaAnexo = AnexoQuadroPosicaoFinanceiraMediator.get().buscar(quadro);
        if (!listaAnexo.isEmpty()) {
            for (AnexoQuadroPosicaoFinanceira anexoQuadroPosicaoFinanceira : listaAnexo) {
                quadroPosicaoFinanceiraDAO.desconectar(anexoQuadroPosicaoFinanceira.getQuadroPosicaoFinanceira());
                anexoQuadroPosicaoFinanceira.setQuadroPosicaoFinanceira(quadroNovo);
                AnexoQuadroPosicaoFinanceiraMediator.get().salvar(anexoQuadroPosicaoFinanceira);
            }
        }
        quadroPosicaoFinanceiraDAO.delete(quadro);

    }

    public void incluirContas(DataBaseES dataBaseES, QuadroPosicaoFinanceira novoQuadro, PerfilRisco perfilRisco) {
        List<LayoutContaAnaliseQuantitativa> layoutContas =
                LayoutContaAnaliseQuantitativaMediator.get().obterLayoutContas(dataBaseES);
        for (LayoutContaAnaliseQuantitativa layout : layoutContas) {
            ContaQuadroPosicaoFinanceira conta = new ContaQuadroPosicaoFinanceira();
            conta.setLayoutContaAnaliseQuantitativa(layout);
            conta.setQuadroPosicaoFinanceira(novoQuadro);
            conta.setExibir(layout.getObrigatorio());
            ContaAnaliseQuantitativaETL extracaoContaAnaliseQuantitativaETL =
                    ContaAnaliseQuantitativaMediator.get().extrairContaAnaliseQuantitativaETL(
                            layout.getContaAnaliseQuantitativa(), dataBaseES, perfilRisco);
            if (extracaoContaAnaliseQuantitativaETL != null) {
                conta.setValor(extracaoContaAnaliseQuantitativaETL.getValor());
            }
            contaQuadroPosicaoFinanceiraDAO.salvar(conta);
            contaQuadroPosicaoFinanceiraDAO.flush();
        }
    }

    @Transactional
    public void salvarContas(QuadroPosicaoFinanceiraVO novoQuadroVO, TipoConta tipoConta) {
        List<ContaQuadroPosicaoFinanceira> listaContasSelecionadasAlteradas =
                novoQuadroVO.getListaContasSelecionadasQuadroPorTipo(tipoConta);
        List<ContaQuadroPosicaoFinanceira> listaContasExcluidas = novoQuadroVO.getListaContasExcluidas();
        for (ContaQuadroPosicaoFinanceira conta : listaContasSelecionadasAlteradas) {
            if (conta.getPk() != null) {
                for (ContaQuadroPosicaoFinanceira contaExcluida : listaContasExcluidas) {
                    contaExcluida.setExibir(SimNaoEnum.NAO);
                    contaQuadroPosicaoFinanceiraDAO.update(contaExcluida);
                    contaQuadroPosicaoFinanceiraDAO.flush();
                }
                contaQuadroPosicaoFinanceiraDAO.update(conta);
                contaQuadroPosicaoFinanceiraDAO.flush();
            }
        }

    }

    @Transactional(readOnly = true)
    private QuadroPosicaoFinanceira obterQuadroPorPk(Integer pkQuadro) {
        return quadroPosicaoFinanceiraDAO.obterQuadroPorPk(pkQuadro);
    }

    @Transactional
    public void salvarAjusteResultado(QuadroPosicaoFinanceiraVO novoQuadroVO) {
        // TODO remover
        for (ResultadoQuadroPosicaoFinanceira resultado : novoQuadroVO.getResultados()) {
            if (resultado.getPk() != null) {
                resultadoQuadroPosicaoFinanceiraDAO.update(resultado);
                resultadoQuadroPosicaoFinanceiraDAO.flush();
            }
        }
    }

    @Transactional
    public void salvarAjusteIndices(QuadroPosicaoFinanceiraVO novoQuadroVO) {
        // TODO remover
        QuadroPosicaoFinanceira quadro = obterQuadroPorPk(novoQuadroVO.getPk());
        IndiceVO.adicionarValores(quadro, novoQuadroVO.getIndices());
        quadroPosicaoFinanceiraDAO.update(quadro);
        quadroPosicaoFinanceiraDAO.flush();
    }

    @Transactional
    public void salvarAjustePatrimonios(QuadroPosicaoFinanceiraVO novoQuadroVO) {
        // TODO remover
        QuadroPosicaoFinanceira quadro = obterQuadroPorPk(novoQuadroVO.getPk());
        PatrimonioVO.adicionarValores(quadro, novoQuadroVO.getPatrimonios());
        quadroPosicaoFinanceiraDAO.update(quadro);
        quadroPosicaoFinanceiraDAO.flush();
    }

    @Transactional(readOnly = true)
    public DataBaseES obterDataBaseESVerificandoExistenciaQuadroCadastrado(PerfilRisco perfilRisco) {
        QuadroPosicaoFinanceira quadroRascunho =
                quadroPosicaoFinanceiraDAO.buscarQuadroPorCiclo(perfilRisco.getCiclo());
        QuadroPosicaoFinanceira quadroVigente = obterUltimaVersaoQuadroVigente(perfilRisco);
        DataBaseES dataBaseES = null;
        if (quadroVigente != null && quadroRascunho == null) {
            dataBaseES =
                    DataBaseESMediator.get().buscarPorIdentificador(quadroVigente.getCodigoDataBase(),
                            quadroVigente.getCiclo().getEntidadeSupervisionavel().getConglomeradoOuCnpj());
        } else if (quadroRascunho == null) {
            dataBaseES = new QuadroPosicaoFinanceiraVO(perfilRisco).getDataBaseES();
        } else {
            dataBaseES =
                    DataBaseESMediator.get().buscarPorIdentificador(quadroRascunho.getCodigoDataBase(),
                            quadroRascunho.getCiclo().getEntidadeSupervisionavel().getConglomeradoOuCnpj());
        }
        return dataBaseES;
    }

    @Transactional(readOnly = true)
    public QuadroPosicaoFinanceira buscarQuadroPorPk(Integer pk) {
        return quadroPosicaoFinanceiraDAO.obterQuadroPorPk(pk);
    }

    @Transactional(readOnly = true)
    public QuadroPosicaoFinanceira buscarQuadroPorPkInicializado(Integer pk) {
        QuadroPosicaoFinanceira quadro = quadroPosicaoFinanceiraDAO.obterQuadroPorPk(pk);
        if (quadro.getCiclo() != null) {
            Hibernate.initialize(quadro.getCiclo());
        }
        if (quadro.getVersaoPerfilRisco() != null) {
            Hibernate.initialize(quadro.getVersaoPerfilRisco());
        }
        return quadro;
    }

    @Transactional
    public void salvarConclusao(QuadroPosicaoFinanceiraVO vo, PerfilRisco perfilRisco) {
        QuadroPosicaoFinanceira quadroRascunho = quadroPosicaoFinanceiraDAO.obterQuadroPorPk(vo.getPk());
        QuadroPosicaoFinanceira quadroVigente = obterUltimaVersaoQuadroVigente(perfilRisco);
        atualizarQuadroRascunho(vo, quadroRascunho);
        if (quadroVigente != null) {
            criarQuadroVigenteAtual(quadroRascunho, vo, perfilRisco);
        } else {
            criarQuadroVigente(perfilRisco, quadroRascunho);
            criarQuadroRascunho(quadroRascunho, false, quadroRascunho.getCiclo(), false);
        }
        eventoConsolidadoMediator.incluirEventoPerfilDeRisco(perfilRisco.getCiclo(),
                TipoSubEventoPerfilRiscoSRC.QUADRO_POSICAO_FINANCEIRA);
    }

    public QuadroPosicaoFinanceira obterUltimaVersaoQuadroVigente(PerfilRisco perfilRisco) {
        QuadroPosicaoFinanceira quadroVigente = null;
        VersaoPerfilRisco versao =
                VersaoPerfilRiscoMediator.get().buscarVersaoPerfilRisco(perfilRisco.getPk(),
                        TipoObjetoVersionadorEnum.QUADRO_POSISAO_FINANCEIRA);
        if (versao != null) {
            quadroVigente = quadroPosicaoFinanceiraDAO.obterQuadroVigente(perfilRisco, versao.getPk());
            quadroPosicaoFinanceiraDAO.desconectar(quadroVigente);
        }
        return quadroVigente;
    }

    private void criarQuadroVigenteAtual(QuadroPosicaoFinanceira quadroRascunho, QuadroPosicaoFinanceiraVO vo,
            PerfilRisco perfilRisco) {
        QuadroPosicaoFinanceira quadroVigenteAtual = new QuadroPosicaoFinanceira();
        quadroVigenteAtual.setCiclo(quadroRascunho.getCiclo());
        quadroVigenteAtual.setCodigoDataBase(vo.getDataBaseES().getCodigoDataBase());
        quadroPosicaoFinanceiraDAO.save(quadroVigenteAtual);
        quadroPosicaoFinanceiraDAO.flush();

        versionarQuadro(perfilRisco, quadroVigenteAtual);
        quadroPosicaoFinanceiraDAO.update(quadroVigenteAtual);
        quadroPosicaoFinanceiraDAO.flush();
        incluirContasVigente(vo, quadroVigenteAtual);
        incluirAjustesVigenteNovo(quadroRascunho, quadroVigenteAtual);
        AnexoQuadroPosicaoFinanceiraMediator.get().duplicarAnexos(quadroRascunho, quadroVigenteAtual, true, false);
    }

    private void versionarQuadro(PerfilRisco perfilRisco, QuadroPosicaoFinanceira quadroVigenteAtual) {
        QuadroPosicaoFinanceira quadroPosicaoFinanceiraVigente = obterUltimaVersaoQuadroVigente(perfilRisco);
        VersaoPerfilRisco versao =
                perfilRiscoMediator.gerarNovaVersaoPerfilRisco(perfilRisco.getCiclo(), quadroPosicaoFinanceiraVigente,
                        TipoObjetoVersionadorEnum.QUADRO_POSISAO_FINANCEIRA);
        quadroVigenteAtual.setVersaoPerfilRisco(versao);
    }
    
    private void incluirAjustesVigenteNovo(QuadroPosicaoFinanceira quadroRascunho, 
            QuadroPosicaoFinanceira novoQuadroVigente) {
        for (OutraInformacaoQuadroPosicaoFinanceira outraInformacao : quadroRascunho.getOutrasInformacoesQuadro()) {
            OutraInformacaoQuadroPosicaoFinanceira outraInfoVigente  = new OutraInformacaoQuadroPosicaoFinanceira();
            BeanUtils.copyProperties(outraInformacao, outraInfoVigente);
            outraInfoVigente.setPk(null);
            outraInfoVigente.setQuadroPosicaoFinanceira(novoQuadroVigente);
            OutraInformacaoQuadroPosicaoFinanceiraMediator.get().save(outraInfoVigente);
            quadroPosicaoFinanceiraDAO.flush();
        }
    }

    private void incluirContasVigente(QuadroPosicaoFinanceiraVO vo, QuadroPosicaoFinanceira novoQuadroVigente) {
        ContaQuadroPosicaoFinanceira contaVigente = null;
        for (ContaQuadroPosicaoFinanceira conta : vo.getListaContasQuadro()) {
            contaVigente = new ContaQuadroPosicaoFinanceira();
            BeanUtils.copyProperties(conta, contaVigente);
            contaVigente.setPk(null);
            contaVigente.setVersao(0);
            contaVigente.setQuadroPosicaoFinanceira(novoQuadroVigente);
            contaQuadroPosicaoFinanceiraDAO.save(contaVigente);
            contaQuadroPosicaoFinanceiraDAO.flush();
        }
    }

    private void atualizarQuadroRascunho(QuadroPosicaoFinanceiraVO vo, QuadroPosicaoFinanceira quadroRascunho) {
        salvarContas(vo, TipoConta.PASSIVO);
        salvarContas(vo, TipoConta.ATIVO);
        OutraInformacaoQuadroPosicaoFinanceiraMediator.get()
            .salvarAjustesOutrasInformacoes(vo, TipoInformacaoEnum.RESULTADO);
        OutraInformacaoQuadroPosicaoFinanceiraMediator.get()
            .salvarAjustesOutrasInformacoes(vo, TipoInformacaoEnum.PATRIMONIO);
        OutraInformacaoQuadroPosicaoFinanceiraMediator.get()
            .salvarAjustesOutrasInformacoes(vo, TipoInformacaoEnum.INDICE);
        quadroPosicaoFinanceiraDAO.update(quadroRascunho);
        quadroPosicaoFinanceiraDAO.flush();
    }

    private void criarQuadroVigente(PerfilRisco perfilRisco, QuadroPosicaoFinanceira quadroRascunho) {
        AnexoQuadroPosicaoFinanceiraMediator.get().duplicarAnexosConclusaoRascunho(quadroRascunho);
        versionarQuadro(perfilRisco, quadroRascunho);
        quadroPosicaoFinanceiraDAO.update(quadroRascunho);
        quadroPosicaoFinanceiraDAO.flush();
        quadroPosicaoFinanceiraDAO.desconectar(quadroRascunho);
    }

    @Transactional
    public QuadroPosicaoFinanceira criarQuadroRascunho(QuadroPosicaoFinanceira quadroVigente,
            boolean isCopiarUsuarioAnterior, Ciclo ciclo, boolean isConcluirCorec) {
        QuadroPosicaoFinanceira quadroRascunho = new QuadroPosicaoFinanceira();
        BeanUtils.copyProperties(quadroVigente, quadroRascunho);
        quadroRascunho.setCiclo(ciclo);
        quadroRascunho.setAnexosQuadro(null);
        quadroRascunho.setPk(null);
        quadroRascunho.setVersaoPerfilRisco(null);
        quadroRascunho.setContas(contaQuadroPosicaoFinanceiraDAO.buscarPorQuadro(quadroVigente));
        quadroRascunho.setOutrasInformacoesQuadro(null);
        quadroRascunho.setResultados(null);
        if (isCopiarUsuarioAnterior) {
            quadroRascunho.setAlterarDataUltimaAtualizacao(false);
        }
        quadroPosicaoFinanceiraDAO.save(quadroRascunho);
        quadroPosicaoFinanceiraDAO.flush();
        for (ContaQuadroPosicaoFinanceira conta : quadroRascunho.getContas()) {
            ContaQuadroPosicaoFinanceira contaRascunho = new ContaQuadroPosicaoFinanceira();
            BeanUtils.copyProperties(conta, contaRascunho);
            contaRascunho.setPk(null);
            contaRascunho.setQuadroPosicaoFinanceira(quadroRascunho);
            if (isCopiarUsuarioAnterior) {
                contaRascunho.setAlterarDataUltimaAtualizacao(false);
            }
            contaQuadroPosicaoFinanceiraDAO.save(contaRascunho);
        }

        List<OutraInformacaoQuadroPosicaoFinanceira> outrasInformacoesQuadro = 
                OutraInformacaoQuadroPosicaoFinanceiraMediator.get()
                .buscarPorQuadroELayout(quadroVigente, null, true);
        for (OutraInformacaoQuadroPosicaoFinanceira outraInformacao : outrasInformacoesQuadro) {
            OutraInformacaoQuadroPosicaoFinanceira outraInfoVigente  = new OutraInformacaoQuadroPosicaoFinanceira();
            BeanUtils.copyProperties(outraInformacao, outraInfoVigente);
            outraInfoVigente.setPk(null);
            outraInfoVigente.setQuadroPosicaoFinanceira(quadroRascunho);
            if (isCopiarUsuarioAnterior) {
                outraInfoVigente.setAlterarDataUltimaAtualizacao(false);
            }
            OutraInformacaoQuadroPosicaoFinanceiraMediator.get().save(outraInfoVigente);
        }

        AnexoQuadroPosicaoFinanceiraMediator.get().duplicarAnexos(quadroVigente, quadroRascunho, true, isConcluirCorec);

        return quadroRascunho;
    }

    @Transactional(readOnly = true)
    public QuadroPosicaoFinanceira buscarQuadroRascunhoPorDataBase(PerfilRisco perfilRiscoAtual) {
        return quadroPosicaoFinanceiraDAO.buscarQuadroRascunhoPorDataBase(perfilRiscoAtual);
    }

    @Transactional(readOnly = true)
    public QuadroPosicaoFinanceira buscarQuadroRascunhoPorDataBase(DataBaseES dataBaseES, PerfilRisco perfilRiscoAtual) {
        return quadroPosicaoFinanceiraDAO.buscarQuadroRascunhoPorDataBase(dataBaseES, perfilRiscoAtual.getCiclo());
    }

    @Transactional
    public QuadroPosicaoFinanceira incluirAjustesQuadroVigente(DataBaseES dataBaseES, PerfilRisco perfilRisco) {
        // TODO remover
        QuadroPosicaoFinanceira quadroVigente = obterUltimaVersaoQuadroVigente(perfilRisco);
        QuadroPosicaoFinanceira quadroRascunho =
                dataBaseES == null ? QuadroPosicaoFinanceiraMediator.get().buscarQuadroRascunhoPorDataBase(perfilRisco)
                        : QuadroPosicaoFinanceiraMediator.get()
                                .buscarQuadroRascunhoPorDataBase(dataBaseES, perfilRisco);
        if (quadroVigente != null && quadroRascunho != null) {
            setarAtributosAjustesQuadro(quadroVigente, quadroRascunho, perfilRisco);
            quadroPosicaoFinanceiraDAO.update(quadroRascunho);
            incluirAjustesContas(quadroVigente, quadroRascunho);
            incluirAjustesResultados(quadroVigente, quadroRascunho);
        }
        return quadroRascunho;
    }


    private void incluirAjustesContas(QuadroPosicaoFinanceira quadroVigente, QuadroPosicaoFinanceira quadroRascunho) {
        for (ContaQuadroPosicaoFinanceira contaRascunho : quadroRascunho.getContas()) {
            if (SimNaoEnum.SIM.equals(contaRascunho.getLayoutContaAnaliseQuantitativa().getEditarAjuste())) {
                ContaQuadroPosicaoFinanceira contaVigente =
                        contaQuadroPosicaoFinanceiraDAO.obterContaVigenteEmExibicaoPorNome(contaRascunho
                                .getLayoutContaAnaliseQuantitativa().getContaAnaliseQuantitativa().getNome(),
                                quadroVigente);
                if (contaVigente != null) {
                    contaRascunho.setValorAjuste(contaVigente.getValorAjuste());
                    contaRascunho.setExibir(contaVigente.getExibir());
                    if (contaVigente.getValorAjuste() != null) {
                        ContaQuadroPosicaoFinanceiraMediator.get().ajustarCalculoAjusteContasSuperiores(contaRascunho);
                    }
                    ajusteRealizado = Boolean.TRUE;
                    contaQuadroPosicaoFinanceiraDAO.update(contaRascunho);
                }
            }
        }
    }

    private void incluirAjustesResultados(QuadroPosicaoFinanceira quadroVigente, QuadroPosicaoFinanceira quadroRascunho) {
        List<ResultadoQuadroPosicaoFinanceira> resultadosRascunho = quadroRascunho.getResultados();
        Collections.sort(resultadosRascunho, new Comparator<ResultadoQuadroPosicaoFinanceira>() {
            @Override
            public int compare(ResultadoQuadroPosicaoFinanceira o1, ResultadoQuadroPosicaoFinanceira o2) {
                return o1.getPeriodo().compareTo(o2.getPeriodo());
            }
        });
        List<ResultadoQuadroPosicaoFinanceira> resultadosVigente =
                resultadoQuadroPosicaoFinanceiraDAO.obterResultados(quadroVigente);
        if (CollectionUtils.isNotEmpty(resultadosRascunho) && CollectionUtils.isNotEmpty(resultadosVigente)) {
            contIncluirAjusteResultado(resultadosRascunho, resultadosVigente);

        }
    }

    private void contIncluirAjusteResultado(List<ResultadoQuadroPosicaoFinanceira> resultadosRascunho,
            List<ResultadoQuadroPosicaoFinanceira> resultadosVigente) {
        ResultadoQuadroPosicaoFinanceira resultadoRascunhoAnterior = null;
        ResultadoQuadroPosicaoFinanceira resultadoVigenteAnterior = null;
        if (resultadosRascunho.size() > 1 && resultadosVigente.size() > 1) {
            resultadoRascunhoAnterior = resultadosRascunho.get(resultadosRascunho.size() - 2);
            resultadoVigenteAnterior = resultadosVigente.get(resultadosVigente.size() - 2);
        }

        ResultadoQuadroPosicaoFinanceira resultadoRascunhoAtual = resultadosRascunho.get(resultadosRascunho.size() - 1);
        ResultadoQuadroPosicaoFinanceira resultadoVigenteAtual = resultadosVigente.get(resultadosVigente.size() - 1);

        boolean isAjustarResultado =
                resultadoRascunhoAnterior != null && resultadoVigenteAnterior != null
                        && resultadoRascunhoAnterior.getPeriodo().equals(resultadoVigenteAnterior.getPeriodo());
        if (isAjustarResultado) {
            int resultadoAlterado = 0;
            if (resultadoVigenteAtual.getAjuste() != null) {
                resultadoRascunhoAtual.setAjuste(resultadoVigenteAtual.getAjuste());
                resultadoAlterado++;
            }
            if (resultadoVigenteAtual.getRsplaAjustado() != null) {
                resultadoRascunhoAtual.setRsplaAjustado(resultadoVigenteAtual.getRsplaAjustado());
                resultadoAlterado++;
            }
            if (resultadoAlterado > 0) {
                resultadoQuadroPosicaoFinanceiraDAO.update(resultadoRascunhoAtual);
            }
        }
    }

    private void setarAtributosAjustesQuadro(QuadroPosicaoFinanceira quadroOrigem,
            QuadroPosicaoFinanceira quadroDestino, PerfilRisco perfilRisco) {

        quadroDestino.setAjustePrNivelUm(quadroOrigem.getAjustePrNivelUm());
        quadroDestino.setAjusteCapitalPrincipal(quadroOrigem.getAjusteCapitalPrincipal());
        quadroDestino.setAjusteCapitalComplementar(quadroOrigem.getAjusteCapitalComplementar());
        quadroDestino.setAjustePrNivelDois(quadroOrigem.getAjustePrNivelDois());

        DataBaseES dataBaseES =
                DataBaseESMediator.get().buscarPorIdentificador(quadroDestino.getCodigoDataBase(),
                        perfilRisco.getCiclo().getEntidadeSupervisionavel().getConglomeradoOuCnpj());
        AnaliseQuantitativaEconomicaETL extracao =
                analiseQuantitativaEconomicaETLDAO.extrair(dataBaseES, perfilRisco.getCiclo()
                        .getEntidadeSupervisionavel().getConglomeradoOuCnpj());
        if (extracao.getIndiceBaseleia() != null) {
            quadroDestino.setIndiceBaseleiaAjustado(quadroOrigem.getIndiceBaseleiaAjustado());
        }
        if (extracao.getIndiceBaseleiaAmplo() != null) {
            quadroDestino.setIndiceBaseleiaAmploAjustado(quadroOrigem.getIndiceBaseleiaAmploAjustado());
        }
        if (extracao.getIndiceImobilizacao() != null) {
            quadroDestino.setIndiceImobilizacaoAjustado(quadroOrigem.getIndiceImobilizacaoAjustado());
        }
    }

    public Boolean isAjusteRealizado() {
        return ajusteRealizado;
    }

    public void setAjusteRealizado(Boolean ajusteRealizado) {
        this.ajusteRealizado = ajusteRealizado;
    }

    @Transactional
    public void criarQuadroPosicaoFinanceiraNovoCiclo(PerfilRisco perfilRiscoCicloAtual, Ciclo novoCiclo) {
        // Criar um registro com os seguintes dados do perfil de risco vigente
        // do ciclo Corec e colocar no perfil vigente do novo ciclo:
        // Quadro da posição financeira, assim como suas contas e os dados do
        // resultado do quadro.
        QuadroPosicaoFinanceira quadroCicloAtual = obterUltimaVersaoQuadroVigente(perfilRiscoCicloAtual);
        QuadroPosicaoFinanceira quadroRascunho = buscarQuadroRascunhoPorDataBase(perfilRiscoCicloAtual);

        if (quadroCicloAtual != null) {
            QuadroPosicaoFinanceira novoQuadroVigente = criarQuadroRascunho(quadroCicloAtual, true, novoCiclo, true);
            perfilRiscoMediator.incluirVersaoPerfilRiscoAtual(novoCiclo, novoQuadroVigente,
                    TipoObjetoVersionadorEnum.QUADRO_POSISAO_FINANCEIRA);
            quadroPosicaoFinanceiraDAO.saveOrUpdate(novoQuadroVigente);

            // Criar um registro do quadro da posição financeira(com suas contas
            // e dados do resultado do quadro)
            // de rascunho no novo ciclo com os dados do rascunho do ciclo
            // Corec.
            QuadroPosicaoFinanceira novoQuadroRascunho = criarQuadroRascunho(quadroRascunho, true, novoCiclo, false);
            quadroPosicaoFinanceiraDAO.saveOrUpdate(novoQuadroRascunho);

        }
    }
    
    public void desconectar(QuadroPosicaoFinanceira quadro) {
        quadroPosicaoFinanceiraDAO.desconectar(quadro);
    }
}
