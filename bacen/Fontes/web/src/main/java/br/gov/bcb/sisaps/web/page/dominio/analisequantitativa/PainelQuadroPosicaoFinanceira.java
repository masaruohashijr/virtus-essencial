package br.gov.bcb.sisaps.web.page.dominio.analisequantitativa;

import org.apache.commons.collections.CollectionUtils;

import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.QuadroPosicaoFinanceira;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoInformacaoEnum;
import br.gov.bcb.sisaps.src.mediator.analisequantitativa.QuadroPosicaoFinanceiraMediator;
import br.gov.bcb.sisaps.src.vo.analisequantitativa.QuadroPosicaoFinanceiraVO;
import br.gov.bcb.sisaps.web.page.dominio.analisequantitativa.TabelaSemFormatoHelper.WebMarkupContainerExibicao;
import br.gov.bcb.sisaps.web.page.dominio.analisequantitativa.anexo.PainelAnexoQPF;

public class PainelQuadroPosicaoFinanceira extends PainelQuadroPosicaoFinanceiraComum {

    private static final String RESULTADOS_NOVO_QUADRO = "resultadosNovoQuadro";
    private static final String INDICES_NOVO_QUADRO = "indicesNovoQuadro";
    private static final String PATRIMONIOS_NOVO_QUADRO = "patrimoniosNovoQuadro";
    private PainelAtivoNovoQuadro painelAtivoNovoQuadro;
    private PainelPassivoNovoQuadro painelPassivoNovoQuadro;

    public PainelQuadroPosicaoFinanceira(String id, QuadroPosicaoFinanceiraVO novoQuadroVO, PerfilRisco perfilRiscoAtual) {
        super(id, novoQuadroVO, perfilRiscoAtual);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        buildQuadroVigente();
        buildNovoQuadro(novoQuadroVO);
        setVisibilityAllowed(true);
    }
    
    @Override
    protected void onConfigure() {
        super.onConfigure();
    }

    private void buildQuadroVigente() {
        addOrReplace(new PainelAnaliseQuantitativa("quadroVigente", perfilRiscoAtual, false));
    }

    private void buildNovoQuadro(final QuadroPosicaoFinanceiraVO novoQuadroVO) {
        painelAtivoNovoQuadro = new PainelAtivoNovoQuadro("novoQuadroAtivo", novoQuadroVO);
        addOrReplace(painelAtivoNovoQuadro);
        painelPassivoNovoQuadro = new PainelPassivoNovoQuadro("novoQuadroPassivo", novoQuadroVO);
        addOrReplace(painelPassivoNovoQuadro);
        buildPatrimonios(novoQuadroVO, "wmcPatrimonioReferencia");
        buildIndices(novoQuadroVO, "wmcIndices");
        buildResultados(novoQuadroVO, "wmcResultados");
        addPainelAnexo(novoQuadroVO);
    }
    
    private void addPainelAnexo(final QuadroPosicaoFinanceiraVO quadroVigenteVO) {
        QuadroPosicaoFinanceira quadro;
        if (quadroVigenteVO.getPk() == null) {
            quadro = new QuadroPosicaoFinanceira();
        } else {
            quadro = QuadroPosicaoFinanceiraMediator.get().buscarQuadroPorPk(quadroVigenteVO.getPk());
        }

        addOrReplace(new PainelAnexoQPF("anexo", quadro, false));
    }


    private void buildPatrimonios(final QuadroPosicaoFinanceiraVO novoQuadroLocalVO, String id) {
        WebMarkupContainerExibicao wmcPatrimonioReferencia = new WebMarkupContainerExibicao(id) {
            @Override
            protected boolean getVisible() {
                return CollectionUtils.isNotEmpty(novoQuadroLocalVO.getPatrimoniosNovo());
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                addOrReplace(new LinkScroll("patrimoniosLinkScroll",
                        ((GerenciarQuadroPosicaoFinanceira) getPage()).getPainelAjustePatrimonioNovo()));
                
                addOrReplace(new TabelaPatrimoniosIndicesQuadro(
                        PATRIMONIOS_NOVO_QUADRO, novoQuadroVO, TipoInformacaoEnum.PATRIMONIO, false));
            }
        };
        addOrReplace(wmcPatrimonioReferencia);
    }

    private void buildIndices(final QuadroPosicaoFinanceiraVO novoQuadroLocalVO, String id) {
        WebMarkupContainerExibicao wmcIndices = new WebMarkupContainerExibicao(id) {
            @Override
            protected boolean getVisible() {
                return CollectionUtils.isNotEmpty(novoQuadroLocalVO.getIndicesNovo());
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                addOrReplace(new LinkScroll("indicesLinkScroll",
                        ((GerenciarQuadroPosicaoFinanceira) getPage()).getPainelAjusteIndicesNovo()));
                addOrReplace(new TabelaPatrimoniosIndicesQuadro(
                        INDICES_NOVO_QUADRO, novoQuadroVO, TipoInformacaoEnum.INDICE, false));
            }
        };
        addOrReplace(wmcIndices);
    }

    private void buildResultados(final QuadroPosicaoFinanceiraVO novoQuadroLocalVO, String id) {
        WebMarkupContainerExibicao wmcResultados = new WebMarkupContainerExibicao(id) {
            @Override
            protected boolean getVisible() {
                return CollectionUtils.isNotEmpty(novoQuadroLocalVO.getNomesResultados());
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                addOrReplace(new LinkScroll("resultadosLinkScroll",
                        ((GerenciarQuadroPosicaoFinanceira) getPage()).getPainelAjusteResultadoNovo()));
                addOrReplace(new TabelaResultadosQuadro(RESULTADOS_NOVO_QUADRO, novoQuadroVO, false, false));
            }
        };
        addOrReplace(wmcResultados);
    }

    public PainelAtivoNovoQuadro getPainelAtivoNovoQuadro() {
        return painelAtivoNovoQuadro;
    }

    public PainelPassivoNovoQuadro getPainelPassivoNovoQuadro() {
        return painelPassivoNovoQuadro;
    }

}
