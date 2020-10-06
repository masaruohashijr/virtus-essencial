package br.gov.bcb.sisaps.web.page.dominio.analisequantitativa;

import org.apache.wicket.model.LoadableDetachableModel;

import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.QuadroPosicaoFinanceira;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoInformacaoEnum;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativa.QuadroPosicaoFinanceiraMediator;
import br.gov.bcb.sisaps.src.vo.analisequantitativa.QuadroPosicaoFinanceiraVO;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.dominio.analisequantitativa.anexo.PainelAnexoQPF;

public class PainelAnaliseQuantitativa extends PainelSisAps {

    private static final String INDICES_VIGENTE = "indicesVigente";
    private static final String PATRIMONIOS_VIGENTE = "patrimoniosVigente";
    private static final String RESULTADOS_VIGENTE = "resultadosVigente";
    private PerfilRisco perfilRiscoAtual;
    private final boolean isApresentacao;

    public PainelAnaliseQuantitativa(String id, PerfilRisco perfilRiscoAtual, boolean isApresentacao) {
        super(id);
        this.perfilRiscoAtual = perfilRiscoAtual;
        this.isApresentacao = isApresentacao;
    }

    
    @Override
    protected void onConfigure() {
        super.onConfigure();
        final QuadroPosicaoFinanceiraVO vo = PerfilRiscoMediator.get().obterQuadroVigente(this.perfilRiscoAtual);
        buildQuadroVigente(new LoadableDetachableModel<QuadroPosicaoFinanceiraVO>() {
            @Override
            protected QuadroPosicaoFinanceiraVO load() {
                return vo;
            }
        }.getObject());
        setVisibilityAllowed(vo.getPk() != null);
    }
   

    private void buildQuadroVigente(final QuadroPosicaoFinanceiraVO quadroVigenteVO) {
        addOrReplace(new PainelAtivoVigente("quadroVigenteAtivo", quadroVigenteVO));
        addOrReplace(new PainelPassivoVigente("quadroVigentePassivo", quadroVigenteVO));
        addOrReplace(new TabelaPatrimoniosIndicesQuadro(
                PATRIMONIOS_VIGENTE, quadroVigenteVO, TipoInformacaoEnum.PATRIMONIO, false));
        addOrReplace(new TabelaPatrimoniosIndicesQuadro(
                INDICES_VIGENTE, quadroVigenteVO, TipoInformacaoEnum.INDICE, false));
        addOrReplace(new TabelaResultadosQuadro(RESULTADOS_VIGENTE, quadroVigenteVO, false, isApresentacao));

        addPainelAnexo(quadroVigenteVO);
    }

    private void addPainelAnexo(final QuadroPosicaoFinanceiraVO quadroVigenteVO) {
        QuadroPosicaoFinanceira quadro;
        if (quadroVigenteVO.getPk() == null) {
            quadro = new QuadroPosicaoFinanceira();
        } else {
            quadro = QuadroPosicaoFinanceiraMediator.get().buscarQuadroPorPk(quadroVigenteVO.getPk());
        }

        addOrReplace(new PainelAnexoQPF("anexoVigente", quadro, false));
    }

    public void setPerfilRiscoAtual(PerfilRisco perfilRiscoAtual) {
        this.perfilRiscoAtual = perfilRiscoAtual;
    }

}
