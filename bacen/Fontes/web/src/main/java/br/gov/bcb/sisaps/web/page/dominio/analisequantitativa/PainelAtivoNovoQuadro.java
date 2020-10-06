package br.gov.bcb.sisaps.web.page.dominio.analisequantitativa;

import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoConta;
import br.gov.bcb.sisaps.src.vo.analisequantitativa.QuadroPosicaoFinanceiraVO;

public class PainelAtivoNovoQuadro extends PainelBaseAtivoPassivo {

    public PainelAtivoNovoQuadro(String id, final QuadroPosicaoFinanceiraVO vo) {
        super(id, vo, TipoConta.ATIVO, false);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        addOrReplace(new LinkScroll("ativoLinkScroll",
                ((GerenciarQuadroPosicaoFinanceira) getPage()).getPainelSelecaoContaAtivo()));
        buildDataBase();
    }

}
