package br.gov.bcb.sisaps.src.mediator;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.src.dao.analisequantitativa.LayoutAnaliseQuantitativaDAO;
import br.gov.bcb.sisaps.src.dao.analisequantitativa.LayoutOutraInformacaoAnaliseQuantitativaDAO;
import br.gov.bcb.sisaps.src.dao.analisequantitativa.OutraInformacaoAnaliseQuantitativaDAO;
import br.gov.bcb.sisaps.src.dao.analisequantitativa.OutraInformacaoQuadroPosicaoFinanceiraDAO;
import br.gov.bcb.sisaps.src.dao.analisequantitativa.QuadroPosicaoFinanceiraDAO;
import br.gov.bcb.sisaps.src.dao.analisequantitativa.ResultadoQuadroPosicaoFinanceiraDAO;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.LayoutAnaliseQuantitativa;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.LayoutOutraInformacaoAnaliseQuantitativa;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.OutraInformacaoAnaliseQuantitativa;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.OutraInformacaoQuadroPosicaoFinanceira;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.QuadroPosicaoFinanceira;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.ResultadoQuadroPosicaoFinanceira;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoEdicaoInformacaoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoInformacaoEnum;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import br.gov.bcb.utils.logging.BCLogFactory;
import br.gov.bcb.utils.logging.BCLogger;

@Service
@Transactional(readOnly = true)
public class MigracaoDadosQuadroPosicaoFinanceiraMediator {
    
    private static final String RESULTADO_LUCRO_LL = "LL";
    private static final String RESULTADO_RSPLA_R_9_3_00_0 = "R_9_3_00_0";
    private static final String PATRIMONIO_PR_NIV1 = "PR_niv1";
    private static final String PATRIMONIO_CP = "CP";
    private static final String PATRIMONIO_CC = "CC";
    private static final String PATRIMONIO_PR_NIV2 = "PR_niv2";
    private static final String INDICE_BASILEIA_C_3_1_02_1 = "C_3_1_02_1";
    private static final String INDICE_BASILEIA_AMPLO_C_3_1_02_0 = "C_3_1_02_0";
    private static final String INDICE_IMOBILIZACAO_C_3_1_03_0 = "C_3_1_03_0";
    
    private static final BCLogger LOG = BCLogFactory.getLogger("BatchMigracaoDadosQuadroPosicaoFinanceira");
    
    @Autowired
    private OutraInformacaoAnaliseQuantitativaDAO outraInformacaoAnaliseQuantitativaDAO;
    
    @Autowired
    private LayoutOutraInformacaoAnaliseQuantitativaDAO layoutOutraInformacaoAnaliseQuantitativaDAO;
    
    @Autowired
    private LayoutAnaliseQuantitativaDAO layoutAnaliseQuantitativaDAO;
    
    @Autowired
    private QuadroPosicaoFinanceiraDAO quadroPosicaoFinanceiraDAO;
    
    @Autowired
    private OutraInformacaoQuadroPosicaoFinanceiraDAO outraInformacaoQuadroPosicaoFinanceiraDAO;
    
    @Autowired
    private ResultadoQuadroPosicaoFinanceiraDAO resultadoQuadroPosicaoFinanceiraDAO;
    
    public static MigracaoDadosQuadroPosicaoFinanceiraMediator get() {
        return SpringUtils.get().getBean(MigracaoDadosQuadroPosicaoFinanceiraMediator.class);
    }
    
    @Transactional
    public void migrarDadosQuadroPosicaoFinanceira() {
        // Criar os dados das outras informações
        criarOutrasInformacoesAnaliseQuantitativa();
        
        // Buscar todos os layouts existentes e colocar nos layouts os dados das outras informações
        preencherLayouts();
        
        // Buscar todos os quadros das posições financeiras e criar os dados das outras informações do quadro
        criarOutrasInformacoesQuadroPosicaoFinanceira();
    }
    
    private void criarOutrasInformacoesAnaliseQuantitativa() {
        LOG.info("Criar os Patrimônios, Índices e Resultados da análise quantitativa.");
        criarPatrimonios();
        criarIndices();
        criarResultados();
        outraInformacaoAnaliseQuantitativaDAO.flush();
    }

    private void criarPatrimonios() {
        LOG.info("Criando Patrimônios...");
        OutraInformacaoAnaliseQuantitativa patrimonioPRNivel1 = new OutraInformacaoAnaliseQuantitativa();
        patrimonioPRNivel1.setTipoInformacao(TipoInformacaoEnum.PATRIMONIO);
        patrimonioPRNivel1.setNome(PATRIMONIO_PR_NIV1);
        patrimonioPRNivel1.setDescricao("PR nível 1");
        patrimonioPRNivel1.setCodigoDataBaseInicio(200801);
        outraInformacaoAnaliseQuantitativaDAO.save(patrimonioPRNivel1);
        LOG.info("Criado patrimônio 'PR nível 1'");
        
        OutraInformacaoAnaliseQuantitativa patrimonioCapitalPrincipal = new OutraInformacaoAnaliseQuantitativa();
        patrimonioCapitalPrincipal.setTipoInformacao(TipoInformacaoEnum.PATRIMONIO);
        patrimonioCapitalPrincipal.setNome(PATRIMONIO_CP);
        patrimonioCapitalPrincipal.setDescricao("Capital Principal");
        patrimonioCapitalPrincipal.setCodigoDataBaseInicio(200801);
        outraInformacaoAnaliseQuantitativaDAO.save(patrimonioCapitalPrincipal);
        LOG.info("Criado patrimônio 'Capital Principal'");
        
        OutraInformacaoAnaliseQuantitativa patrimonioCapitalComplementar = new OutraInformacaoAnaliseQuantitativa();
        patrimonioCapitalComplementar.setTipoInformacao(TipoInformacaoEnum.PATRIMONIO);
        patrimonioCapitalComplementar.setNome(PATRIMONIO_CC);
        patrimonioCapitalComplementar.setDescricao("Capital Complementar");
        patrimonioCapitalComplementar.setCodigoDataBaseInicio(200801);
        outraInformacaoAnaliseQuantitativaDAO.save(patrimonioCapitalComplementar);
        LOG.info("Criado patrimônio 'Capital Complementar'");
        
        OutraInformacaoAnaliseQuantitativa patrimonioPRNivel2 = new OutraInformacaoAnaliseQuantitativa();
        patrimonioPRNivel2.setTipoInformacao(TipoInformacaoEnum.PATRIMONIO);
        patrimonioPRNivel2.setNome(PATRIMONIO_PR_NIV2);
        patrimonioPRNivel2.setDescricao("PR nível 2");
        patrimonioPRNivel2.setCodigoDataBaseInicio(200801);
        outraInformacaoAnaliseQuantitativaDAO.save(patrimonioPRNivel2);
        LOG.info("Criado patrimônio 'PR nível 2'");
    }

    private void criarIndices() {
        LOG.info("Criando Índices...");
        OutraInformacaoAnaliseQuantitativa indiceBasileia = new OutraInformacaoAnaliseQuantitativa();
        indiceBasileia.setTipoInformacao(TipoInformacaoEnum.INDICE);
        indiceBasileia.setNome(INDICE_BASILEIA_C_3_1_02_1);
        indiceBasileia.setDescricao("Basiléia");
        indiceBasileia.setCodigoDataBaseInicio(200801);
        outraInformacaoAnaliseQuantitativaDAO.save(indiceBasileia);
        LOG.info("Criado índice 'Basiléia'");
        
        OutraInformacaoAnaliseQuantitativa indiceBasileiaAmplo = new OutraInformacaoAnaliseQuantitativa();
        indiceBasileiaAmplo.setTipoInformacao(TipoInformacaoEnum.INDICE);
        indiceBasileiaAmplo.setNome(INDICE_BASILEIA_AMPLO_C_3_1_02_0);
        indiceBasileiaAmplo.setDescricao("Basiléia Amplo (inclui RBAN)");
        indiceBasileiaAmplo.setCodigoDataBaseInicio(200801);
        outraInformacaoAnaliseQuantitativaDAO.save(indiceBasileiaAmplo);
        LOG.info("Criado índice 'Basiléia Amplo (inclui RBAN)'");
        
        OutraInformacaoAnaliseQuantitativa indiceImobilizacao = new OutraInformacaoAnaliseQuantitativa();
        indiceImobilizacao.setTipoInformacao(TipoInformacaoEnum.INDICE);
        indiceImobilizacao.setNome(INDICE_IMOBILIZACAO_C_3_1_03_0);
        indiceImobilizacao.setDescricao("Imobilização");
        indiceImobilizacao.setCodigoDataBaseInicio(200801);
        outraInformacaoAnaliseQuantitativaDAO.save(indiceImobilizacao);
        LOG.info("Criado índice 'Imobilização'");
    }

    private void criarResultados() {
        LOG.info("Criando Resultados...");
        OutraInformacaoAnaliseQuantitativa resultadoLucro = new OutraInformacaoAnaliseQuantitativa();
        resultadoLucro.setTipoInformacao(TipoInformacaoEnum.RESULTADO);
        resultadoLucro.setNome(RESULTADO_LUCRO_LL);
        resultadoLucro.setDescricao("Lucro");
        resultadoLucro.setCodigoDataBaseInicio(200801);
        outraInformacaoAnaliseQuantitativaDAO.save(resultadoLucro);
        LOG.info("Criado resultado 'Lucro'");
        
        OutraInformacaoAnaliseQuantitativa resultadoRSPLA = new OutraInformacaoAnaliseQuantitativa();
        resultadoRSPLA.setTipoInformacao(TipoInformacaoEnum.RESULTADO);
        resultadoRSPLA.setNome(RESULTADO_RSPLA_R_9_3_00_0);
        resultadoRSPLA.setDescricao("RSPLA");
        resultadoRSPLA.setCodigoDataBaseInicio(200801);
        outraInformacaoAnaliseQuantitativaDAO.save(resultadoRSPLA);
        LOG.info("Criado resultado 'RSPLA'");
    }
    
    private void preencherLayouts() {
        LOG.info("Preencher layouts existentes com os Patrimônios, Índices e Resultados");
        List<LayoutAnaliseQuantitativa> layouts = layoutAnaliseQuantitativaDAO.getAll();
        for (LayoutAnaliseQuantitativa layout : layouts) {
            LOG.info("Preenchendo layout de ID " + layout.getPk());
            criarLayoutsPatrimonios(layout);
            criarLayoutsIndices(layout);
            criarLayoutsResultados(layout);
        }
        layoutOutraInformacaoAnaliseQuantitativaDAO.flush();
    }
    
    private void criarLayoutsPatrimonios(LayoutAnaliseQuantitativa layout) {
        LOG.info("Preenchendo layout com os Patrimônios...");
        criarPatrimonioPRNivel1(layout);
        LOG.info("Preenchido layout com o Patrimônio 'PR nível 1'");
        criarPatrimonioCapitalPrincipal(layout);
        LOG.info("Preenchido layout com o Patrimônio 'Capital Principal'");
        criarPatrimonioCapitalComplementar(layout);
        LOG.info("Preenchido layout com o Patrimônio 'Capital Complementar'");
        criarPatrimonioPRNivel2(layout);
        LOG.info("Preenchido layout com o Patrimônio 'PR nível 2'");
    }

    private void criarPatrimonioPRNivel2(LayoutAnaliseQuantitativa layout) {
        OutraInformacaoAnaliseQuantitativa patrimonioPRNivel2 = 
                outraInformacaoAnaliseQuantitativaDAO.buscarPorNome(
                        PATRIMONIO_PR_NIV2, TipoInformacaoEnum.PATRIMONIO);
        LayoutOutraInformacaoAnaliseQuantitativa layoutPatrimonioPRNivel2 = 
                new LayoutOutraInformacaoAnaliseQuantitativa();
        layoutPatrimonioPRNivel2.setLayoutAnaliseQuantitativa(layout);
        layoutPatrimonioPRNivel2.setSequencial(4);
        layoutPatrimonioPRNivel2.setExibirNulo(SimNaoEnum.NAO);
        layoutPatrimonioPRNivel2.setTipoEdicaoInformacaoEnum(TipoEdicaoInformacaoEnum.AJUSTE);
        layoutPatrimonioPRNivel2.setNumeroCasasInteiras(5);
        layoutPatrimonioPRNivel2.setNumeroCasasDecimais(0);
        layoutPatrimonioPRNivel2.setOutraInformacaoAnaliseQuantitativa(patrimonioPRNivel2);
        layoutOutraInformacaoAnaliseQuantitativaDAO.save(layoutPatrimonioPRNivel2);
    }

    private void criarPatrimonioCapitalComplementar(LayoutAnaliseQuantitativa layout) {
        OutraInformacaoAnaliseQuantitativa patrimonioCapitalComplementar = 
                outraInformacaoAnaliseQuantitativaDAO.buscarPorNome(
                        PATRIMONIO_CC, TipoInformacaoEnum.PATRIMONIO);
        LayoutOutraInformacaoAnaliseQuantitativa layoutPatrimonioCapitalComplementar = 
                new LayoutOutraInformacaoAnaliseQuantitativa();
        layoutPatrimonioCapitalComplementar.setLayoutAnaliseQuantitativa(layout);
        layoutPatrimonioCapitalComplementar.setSequencial(3);
        layoutPatrimonioCapitalComplementar.setExibirNulo(SimNaoEnum.NAO);
        layoutPatrimonioCapitalComplementar.setTipoEdicaoInformacaoEnum(TipoEdicaoInformacaoEnum.AJUSTE);
        layoutPatrimonioCapitalComplementar.setNumeroCasasInteiras(5);
        layoutPatrimonioCapitalComplementar.setNumeroCasasDecimais(0);
        layoutPatrimonioCapitalComplementar.setOutraInformacaoAnaliseQuantitativa(patrimonioCapitalComplementar);
        layoutOutraInformacaoAnaliseQuantitativaDAO.save(layoutPatrimonioCapitalComplementar);
    }

    private void criarPatrimonioCapitalPrincipal(LayoutAnaliseQuantitativa layout) {
        OutraInformacaoAnaliseQuantitativa patrimonioCapitalPrincipal = 
                outraInformacaoAnaliseQuantitativaDAO.buscarPorNome(
                        PATRIMONIO_CP, TipoInformacaoEnum.PATRIMONIO);
        LayoutOutraInformacaoAnaliseQuantitativa layoutPatrimonioCapitalPrincipal = 
                new LayoutOutraInformacaoAnaliseQuantitativa();
        layoutPatrimonioCapitalPrincipal.setLayoutAnaliseQuantitativa(layout);
        layoutPatrimonioCapitalPrincipal.setSequencial(2);
        layoutPatrimonioCapitalPrincipal.setExibirNulo(SimNaoEnum.NAO);
        layoutPatrimonioCapitalPrincipal.setTipoEdicaoInformacaoEnum(TipoEdicaoInformacaoEnum.AJUSTE);
        layoutPatrimonioCapitalPrincipal.setNumeroCasasInteiras(5);
        layoutPatrimonioCapitalPrincipal.setNumeroCasasDecimais(0);
        layoutPatrimonioCapitalPrincipal.setOutraInformacaoAnaliseQuantitativa(patrimonioCapitalPrincipal);
        layoutOutraInformacaoAnaliseQuantitativaDAO.save(layoutPatrimonioCapitalPrincipal);
    }

    private void criarPatrimonioPRNivel1(LayoutAnaliseQuantitativa layout) {
        OutraInformacaoAnaliseQuantitativa patrimonioPRNivel1 = 
                outraInformacaoAnaliseQuantitativaDAO.buscarPorNome(
                        PATRIMONIO_PR_NIV1, TipoInformacaoEnum.PATRIMONIO);
        LayoutOutraInformacaoAnaliseQuantitativa layoutPatrimonioPRNivel1 = 
                new LayoutOutraInformacaoAnaliseQuantitativa();
        layoutPatrimonioPRNivel1.setLayoutAnaliseQuantitativa(layout);
        layoutPatrimonioPRNivel1.setSequencial(1);
        layoutPatrimonioPRNivel1.setExibirNulo(SimNaoEnum.NAO);
        layoutPatrimonioPRNivel1.setTipoEdicaoInformacaoEnum(TipoEdicaoInformacaoEnum.AJUSTE);
        layoutPatrimonioPRNivel1.setNumeroCasasInteiras(5);
        layoutPatrimonioPRNivel1.setNumeroCasasDecimais(0);
        layoutPatrimonioPRNivel1.setOutraInformacaoAnaliseQuantitativa(patrimonioPRNivel1);
        layoutOutraInformacaoAnaliseQuantitativaDAO.save(layoutPatrimonioPRNivel1);
    }

    private void criarLayoutsIndices(LayoutAnaliseQuantitativa layout) {
        LOG.info("Preenchendo layout com os Índices...");
        criarLayoutIndiceBasileia(layout);
        LOG.info("Preenchido layout com o Índice 'Basiléia'");
        criarLayoutIndiceBasileiaAmplo(layout);
        LOG.info("Preenchido layout com o Índice 'Basiléia Amplo (inclui RBAN)'");
        criarLayoutIndiceImobilizacao(layout);
        LOG.info("Preenchido layout com o Índice 'Imobilização'");
    }

    private void criarLayoutIndiceBasileia(LayoutAnaliseQuantitativa layout) {
        OutraInformacaoAnaliseQuantitativa indiceBasileia = 
                outraInformacaoAnaliseQuantitativaDAO.buscarPorNome(
                        INDICE_BASILEIA_C_3_1_02_1, TipoInformacaoEnum.INDICE);
        LayoutOutraInformacaoAnaliseQuantitativa layoutIndiceBasileia = 
                new LayoutOutraInformacaoAnaliseQuantitativa();
        layoutIndiceBasileia.setLayoutAnaliseQuantitativa(layout);
        layoutIndiceBasileia.setSequencial(1);
        layoutIndiceBasileia.setExibirNulo(SimNaoEnum.NAO);
        layoutIndiceBasileia.setTipoEdicaoInformacaoEnum(TipoEdicaoInformacaoEnum.AJUSTADO);
        layoutIndiceBasileia.setNumeroCasasInteiras(2);
        layoutIndiceBasileia.setNumeroCasasDecimais(2);
        layoutIndiceBasileia.setOutraInformacaoAnaliseQuantitativa(indiceBasileia);
        layoutOutraInformacaoAnaliseQuantitativaDAO.save(layoutIndiceBasileia);
    }

    private void criarLayoutIndiceBasileiaAmplo(LayoutAnaliseQuantitativa layout) {
        OutraInformacaoAnaliseQuantitativa indiceBasileiaAmplo = 
                outraInformacaoAnaliseQuantitativaDAO.buscarPorNome(
                        INDICE_BASILEIA_AMPLO_C_3_1_02_0, TipoInformacaoEnum.INDICE);
        LayoutOutraInformacaoAnaliseQuantitativa layoutIndiceBasileiaAmplo = 
                new LayoutOutraInformacaoAnaliseQuantitativa();
        layoutIndiceBasileiaAmplo.setLayoutAnaliseQuantitativa(layout);
        layoutIndiceBasileiaAmplo.setSequencial(2);
        layoutIndiceBasileiaAmplo.setExibirNulo(SimNaoEnum.NAO);
        layoutIndiceBasileiaAmplo.setTipoEdicaoInformacaoEnum(TipoEdicaoInformacaoEnum.AJUSTADO);
        layoutIndiceBasileiaAmplo.setNumeroCasasInteiras(2);
        layoutIndiceBasileiaAmplo.setNumeroCasasDecimais(2);
        layoutIndiceBasileiaAmplo.setOutraInformacaoAnaliseQuantitativa(indiceBasileiaAmplo);
        layoutOutraInformacaoAnaliseQuantitativaDAO.save(layoutIndiceBasileiaAmplo);
    }

    private void criarLayoutIndiceImobilizacao(LayoutAnaliseQuantitativa layout) {
        OutraInformacaoAnaliseQuantitativa indiceImobilizacao = 
                outraInformacaoAnaliseQuantitativaDAO.buscarPorNome(
                        INDICE_IMOBILIZACAO_C_3_1_03_0, TipoInformacaoEnum.INDICE);
        LayoutOutraInformacaoAnaliseQuantitativa layoutIndiceImobilizacao = 
                new LayoutOutraInformacaoAnaliseQuantitativa();
        layoutIndiceImobilizacao.setLayoutAnaliseQuantitativa(layout);
        layoutIndiceImobilizacao.setSequencial(3);
        layoutIndiceImobilizacao.setExibirNulo(SimNaoEnum.NAO);
        layoutIndiceImobilizacao.setTipoEdicaoInformacaoEnum(TipoEdicaoInformacaoEnum.AJUSTADO);
        layoutIndiceImobilizacao.setNumeroCasasInteiras(2);
        layoutIndiceImobilizacao.setNumeroCasasDecimais(2);
        layoutIndiceImobilizacao.setOutraInformacaoAnaliseQuantitativa(indiceImobilizacao);
        layoutOutraInformacaoAnaliseQuantitativaDAO.save(layoutIndiceImobilizacao);
    }

    private void criarLayoutsResultados(LayoutAnaliseQuantitativa layout) {
        LOG.info("Preenchendo layout com os Resultados...");
        OutraInformacaoAnaliseQuantitativa resultadoLucro =
                outraInformacaoAnaliseQuantitativaDAO.buscarPorNome(
                        RESULTADO_LUCRO_LL, TipoInformacaoEnum.RESULTADO);
        LayoutOutraInformacaoAnaliseQuantitativa layoutResultadoLucro = 
                new LayoutOutraInformacaoAnaliseQuantitativa();
        layoutResultadoLucro.setLayoutAnaliseQuantitativa(layout);
        layoutResultadoLucro.setSequencial(1);
        layoutResultadoLucro.setExibirNulo(SimNaoEnum.SIM);
        layoutResultadoLucro.setTipoEdicaoInformacaoEnum(TipoEdicaoInformacaoEnum.AJUSTE);
        layoutResultadoLucro.setNumeroCasasInteiras(5);
        layoutResultadoLucro.setNumeroCasasDecimais(0);
        layoutResultadoLucro.setOutraInformacaoAnaliseQuantitativa(resultadoLucro);
        layoutOutraInformacaoAnaliseQuantitativaDAO.save(layoutResultadoLucro);
        LOG.info("Preenchido layout com o Resultado 'Lucro'");
        OutraInformacaoAnaliseQuantitativa resultadoRSPLA = 
                outraInformacaoAnaliseQuantitativaDAO.buscarPorNome(
                        RESULTADO_RSPLA_R_9_3_00_0, TipoInformacaoEnum.RESULTADO);
        LayoutOutraInformacaoAnaliseQuantitativa layoutResultadoRSPLA = 
                new LayoutOutraInformacaoAnaliseQuantitativa();
        layoutResultadoRSPLA.setLayoutAnaliseQuantitativa(layout);
        layoutResultadoRSPLA.setSequencial(2);
        layoutResultadoRSPLA.setExibirNulo(SimNaoEnum.SIM);
        layoutResultadoRSPLA.setTipoEdicaoInformacaoEnum(TipoEdicaoInformacaoEnum.AJUSTADO);
        layoutResultadoRSPLA.setNumeroCasasInteiras(2);
        layoutResultadoRSPLA.setNumeroCasasDecimais(1);
        layoutResultadoRSPLA.setOutraInformacaoAnaliseQuantitativa(resultadoRSPLA);
        layoutOutraInformacaoAnaliseQuantitativaDAO.save(layoutResultadoRSPLA);
        LOG.info("Preenchido layout com o Resultado 'RSPLA'");
    }

    private void criarOutrasInformacoesQuadroPosicaoFinanceira() {
        LOG.info("Criar as informações dos quadros das posições financeiras");
        
        List<QuadroPosicaoFinanceira> quadrosPosicaoFinanceira = quadroPosicaoFinanceiraDAO.getAll();
        for (QuadroPosicaoFinanceira quadro : quadrosPosicaoFinanceira) {
            LOG.info("Criando as informações do quadro de ID: " + quadro.getPk());
            criarPatrimoniosQuadroPosicaoFinanceira(layoutOutraInformacaoAnaliseQuantitativaDAO, 
                    outraInformacaoQuadroPosicaoFinanceiraDAO, quadro);
            criarIndicesQuadroPosicaoFinanceira(quadro);
            criarResultadosQuadroPosicaoFinanceira(quadro);
        }
    }
    
    private void criarPatrimoniosQuadroPosicaoFinanceira(
            LayoutOutraInformacaoAnaliseQuantitativaDAO layoutOutraInformacaoAnaliseQuantitativaDAO, 
            OutraInformacaoQuadroPosicaoFinanceiraDAO outraInformacaoQuadroPosicaoFinanceiraDAO, 
            QuadroPosicaoFinanceira quadro) {
        LOG.info("Criando Patrimônios no quadro...");
        criarPatrimonioPRNivel1NoQuadro(quadro);
        LOG.info("Criado Patrimônio 'PR nível 1'");
        criarPatrimonioCapitalPrincipalNoQuadro(quadro);
        LOG.info("Criado Patrimônio 'Capital Principal'");
        criarPatrimonioCapitalComplementarNoQuadro(quadro);
        LOG.info("Criado Patrimônio 'Capital Complementar'");
        criarPatrimonioPRNivel2NoQuadro(quadro);
        LOG.info("Criado Patrimônio 'PR nível 2'");
    }

    private void criarPatrimonioPRNivel1NoQuadro(QuadroPosicaoFinanceira quadro) {
        LayoutOutraInformacaoAnaliseQuantitativa layoutPatrimonioPRNivel1 = 
                layoutOutraInformacaoAnaliseQuantitativaDAO.obterLayoutOutraInformacaoQuadro(
                quadro.getCodigoDataBase(), TipoInformacaoEnum.PATRIMONIO, PATRIMONIO_PR_NIV1);
        OutraInformacaoQuadroPosicaoFinanceira patrimonioPRNivel1 = new OutraInformacaoQuadroPosicaoFinanceira();
        patrimonioPRNivel1.setLayoutOutraInformacaoAnaliseQuantitativa(layoutPatrimonioPRNivel1);
        patrimonioPRNivel1.setPeriodo(quadro.getCodigoDataBase());
        patrimonioPRNivel1.setQuadroPosicaoFinanceira(quadro);
        patrimonioPRNivel1.setValor(quadro.getPrNivelUm() == null ? 
                null : new BigDecimal(quadro.getPrNivelUm()));
        patrimonioPRNivel1.setValorEditado(quadro.getAjustePrNivelUm() == null ? 
                null : new BigDecimal(quadro.getAjustePrNivelUm()));
        outraInformacaoQuadroPosicaoFinanceiraDAO.save(patrimonioPRNivel1);
    }

    private void criarPatrimonioCapitalPrincipalNoQuadro(QuadroPosicaoFinanceira quadro) {
        LayoutOutraInformacaoAnaliseQuantitativa layoutPatrimonioCapitalPrincipal = 
                layoutOutraInformacaoAnaliseQuantitativaDAO.obterLayoutOutraInformacaoQuadro(
                quadro.getCodigoDataBase(), TipoInformacaoEnum.PATRIMONIO, PATRIMONIO_CP);
        OutraInformacaoQuadroPosicaoFinanceira patrimonioCapitalPrincipal = 
                new OutraInformacaoQuadroPosicaoFinanceira();
        patrimonioCapitalPrincipal.setLayoutOutraInformacaoAnaliseQuantitativa(layoutPatrimonioCapitalPrincipal);
        patrimonioCapitalPrincipal.setPeriodo(quadro.getCodigoDataBase());
        patrimonioCapitalPrincipal.setQuadroPosicaoFinanceira(quadro);
        patrimonioCapitalPrincipal.setValor(quadro.getCapitalPrincipal() == null ? 
                null : new BigDecimal(quadro.getCapitalPrincipal()));
        patrimonioCapitalPrincipal.setValorEditado(quadro.getAjusteCapitalPrincipal() == null ? 
                null : new BigDecimal(quadro.getAjusteCapitalPrincipal()));
        outraInformacaoQuadroPosicaoFinanceiraDAO.save(patrimonioCapitalPrincipal);
    }

    private void criarPatrimonioCapitalComplementarNoQuadro(QuadroPosicaoFinanceira quadro) {
        LayoutOutraInformacaoAnaliseQuantitativa layoutPatrimonioCapitalComplementar = 
                layoutOutraInformacaoAnaliseQuantitativaDAO.obterLayoutOutraInformacaoQuadro(
                quadro.getCodigoDataBase(), TipoInformacaoEnum.PATRIMONIO, PATRIMONIO_CC);
        OutraInformacaoQuadroPosicaoFinanceira patrimonioCapitalComplementar = 
                new OutraInformacaoQuadroPosicaoFinanceira();
        patrimonioCapitalComplementar.setLayoutOutraInformacaoAnaliseQuantitativa(
                layoutPatrimonioCapitalComplementar);
        patrimonioCapitalComplementar.setPeriodo(quadro.getCodigoDataBase());
        patrimonioCapitalComplementar.setQuadroPosicaoFinanceira(quadro);
        patrimonioCapitalComplementar.setValor(quadro.getCapitalComplementar() == null ? 
                null : new BigDecimal(quadro.getCapitalComplementar()));
        patrimonioCapitalComplementar.setValorEditado(quadro.getAjusteCapitalComplementar() == null ? 
                null : new BigDecimal(quadro.getAjusteCapitalComplementar()));
        outraInformacaoQuadroPosicaoFinanceiraDAO.save(patrimonioCapitalComplementar);
    }

    private void criarPatrimonioPRNivel2NoQuadro(QuadroPosicaoFinanceira quadro) {
        LayoutOutraInformacaoAnaliseQuantitativa layoutPatrimonioPRNivel2 = 
                layoutOutraInformacaoAnaliseQuantitativaDAO.obterLayoutOutraInformacaoQuadro(
                quadro.getCodigoDataBase(), TipoInformacaoEnum.PATRIMONIO, PATRIMONIO_PR_NIV2);
        OutraInformacaoQuadroPosicaoFinanceira patrimonioPRNivel2 = new OutraInformacaoQuadroPosicaoFinanceira();
        patrimonioPRNivel2.setLayoutOutraInformacaoAnaliseQuantitativa(layoutPatrimonioPRNivel2);
        patrimonioPRNivel2.setPeriodo(quadro.getCodigoDataBase());
        patrimonioPRNivel2.setQuadroPosicaoFinanceira(quadro);
        patrimonioPRNivel2.setValor(quadro.getPrNivelDois() == null ? 
                null : new BigDecimal(quadro.getPrNivelDois()));
        patrimonioPRNivel2.setValorEditado(quadro.getAjustePrNivelDois() == null ? 
                null : new BigDecimal(quadro.getAjustePrNivelDois()));
        outraInformacaoQuadroPosicaoFinanceiraDAO.save(patrimonioPRNivel2);
    }

    private void criarIndicesQuadroPosicaoFinanceira(QuadroPosicaoFinanceira quadro) {
        LOG.info("Criando Índices no quadro...");
        LayoutOutraInformacaoAnaliseQuantitativa layoutIndiceBasileia = 
                layoutOutraInformacaoAnaliseQuantitativaDAO.obterLayoutOutraInformacaoQuadro(
                quadro.getCodigoDataBase(), TipoInformacaoEnum.INDICE, INDICE_BASILEIA_C_3_1_02_1);
        OutraInformacaoQuadroPosicaoFinanceira indiceBasileia = new OutraInformacaoQuadroPosicaoFinanceira();
        indiceBasileia.setLayoutOutraInformacaoAnaliseQuantitativa(layoutIndiceBasileia);
        indiceBasileia.setPeriodo(quadro.getCodigoDataBase());
        indiceBasileia.setQuadroPosicaoFinanceira(quadro);
        indiceBasileia.setValor(quadro.getIndiceBaseleia());
        indiceBasileia.setValorEditado(quadro.getIndiceBaseleiaAjustado());
        outraInformacaoQuadroPosicaoFinanceiraDAO.save(indiceBasileia);
        LOG.info("Criado Índice 'Basiléia'");
        LayoutOutraInformacaoAnaliseQuantitativa layoutIndiceBasileiaAmplo = 
                layoutOutraInformacaoAnaliseQuantitativaDAO.obterLayoutOutraInformacaoQuadro(
                quadro.getCodigoDataBase(), TipoInformacaoEnum.INDICE, INDICE_BASILEIA_AMPLO_C_3_1_02_0);
        OutraInformacaoQuadroPosicaoFinanceira indiceBasileiaAmplo = new OutraInformacaoQuadroPosicaoFinanceira();
        indiceBasileiaAmplo.setLayoutOutraInformacaoAnaliseQuantitativa(layoutIndiceBasileiaAmplo);
        indiceBasileiaAmplo.setPeriodo(quadro.getCodigoDataBase());
        indiceBasileiaAmplo.setQuadroPosicaoFinanceira(quadro);
        indiceBasileiaAmplo.setValor(quadro.getIndiceBaseleiaAmplo());
        indiceBasileiaAmplo.setValorEditado(quadro.getIndiceBaseleiaAmploAjustado());
        outraInformacaoQuadroPosicaoFinanceiraDAO.save(indiceBasileiaAmplo);
        LOG.info("Criado Índice 'Basiléia Amplo (inclui RBAN)'");
        LayoutOutraInformacaoAnaliseQuantitativa layoutIndiceImobilizacao = 
                layoutOutraInformacaoAnaliseQuantitativaDAO.obterLayoutOutraInformacaoQuadro(
                quadro.getCodigoDataBase(), TipoInformacaoEnum.INDICE, INDICE_IMOBILIZACAO_C_3_1_03_0);
        OutraInformacaoQuadroPosicaoFinanceira indiceImobilizacao = new OutraInformacaoQuadroPosicaoFinanceira();
        indiceImobilizacao.setLayoutOutraInformacaoAnaliseQuantitativa(layoutIndiceImobilizacao);
        indiceImobilizacao.setPeriodo(quadro.getCodigoDataBase());
        indiceImobilizacao.setQuadroPosicaoFinanceira(quadro);
        indiceImobilizacao.setValor(quadro.getIndiceImobilizacao());
        indiceImobilizacao.setValorEditado(quadro.getIndiceImobilizacaoAjustado());
        outraInformacaoQuadroPosicaoFinanceiraDAO.save(indiceImobilizacao);
        LOG.info("Criado Índice 'Imobilização'");
    }

    private void criarResultadosQuadroPosicaoFinanceira(QuadroPosicaoFinanceira quadro) {
        LOG.info("Criando Resultados no quadro...");
        LayoutOutraInformacaoAnaliseQuantitativa layoutResultadoLucro = 
                layoutOutraInformacaoAnaliseQuantitativaDAO.obterLayoutOutraInformacaoQuadro(
                quadro.getCodigoDataBase(), TipoInformacaoEnum.RESULTADO, RESULTADO_LUCRO_LL);
        
        LayoutOutraInformacaoAnaliseQuantitativa layoutResultadoRSPLA = 
                layoutOutraInformacaoAnaliseQuantitativaDAO.obterLayoutOutraInformacaoQuadro(
                quadro.getCodigoDataBase(), TipoInformacaoEnum.RESULTADO, RESULTADO_RSPLA_R_9_3_00_0);
        
        List<ResultadoQuadroPosicaoFinanceira> resultadosExistentes = 
                resultadoQuadroPosicaoFinanceiraDAO.obterResultados(quadro);
        
        for (ResultadoQuadroPosicaoFinanceira resultadoExistente : resultadosExistentes) {
            OutraInformacaoQuadroPosicaoFinanceira resultadoLucro = new OutraInformacaoQuadroPosicaoFinanceira();
            resultadoLucro.setLayoutOutraInformacaoAnaliseQuantitativa(layoutResultadoLucro);
            resultadoLucro.setPeriodo(resultadoExistente.getPeriodo());
            resultadoLucro.setQuadroPosicaoFinanceira(quadro);
            resultadoLucro.setValor(resultadoExistente.getLucroLiquido() == null ? 
                    null : new BigDecimal(resultadoExistente.getLucroLiquido()));
            resultadoLucro.setValorEditado(resultadoExistente.getAjuste() == null ? 
                    null : new BigDecimal(resultadoExistente.getAjuste()));
            outraInformacaoQuadroPosicaoFinanceiraDAO.save(resultadoLucro);
            LOG.info("Criado Resultado 'Lucro' para o período " + resultadoExistente.getPeriodoFormatado());
            OutraInformacaoQuadroPosicaoFinanceira resultadoRSPLA = new OutraInformacaoQuadroPosicaoFinanceira();
            resultadoRSPLA.setLayoutOutraInformacaoAnaliseQuantitativa(layoutResultadoRSPLA);
            resultadoRSPLA.setPeriodo(resultadoExistente.getPeriodo());
            resultadoRSPLA.setQuadroPosicaoFinanceira(quadro);
            resultadoRSPLA.setValor(resultadoExistente.getRspla());
            resultadoRSPLA.setValorEditado(resultadoExistente.getRsplaAjustado());
            outraInformacaoQuadroPosicaoFinanceiraDAO.save(resultadoRSPLA);
            LOG.info("Criado Resultado 'RSPLA' para o período " + resultadoExistente.getPeriodoFormatado());
        }
        
    }
    
}
