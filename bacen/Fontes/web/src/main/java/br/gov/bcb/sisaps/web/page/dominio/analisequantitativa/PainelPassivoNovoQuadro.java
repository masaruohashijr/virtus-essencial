package br.gov.bcb.sisaps.web.page.dominio.analisequantitativa;

import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoConta;
import br.gov.bcb.sisaps.src.vo.analisequantitativa.QuadroPosicaoFinanceiraVO;

public class PainelPassivoNovoQuadro extends PainelBaseAtivoPassivo {

    public PainelPassivoNovoQuadro(String id, final QuadroPosicaoFinanceiraVO vo) {
        super(id, vo, TipoConta.PASSIVO, false);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        addOrReplace(new LinkScroll("passivoLinkScroll",
                ((GerenciarQuadroPosicaoFinanceira) getPage()).getPainelSelecaoContaPassivo()));
    }

}
