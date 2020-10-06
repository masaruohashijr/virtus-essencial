package br.gov.bcb.sisaps.web.page.dominio.analisequantitativa;

import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoConta;
import br.gov.bcb.sisaps.src.vo.analisequantitativa.QuadroPosicaoFinanceiraVO;

public class PainelPassivoVigente extends PainelBaseAtivoPassivo {

    public PainelPassivoVigente(String id, QuadroPosicaoFinanceiraVO quadroVigenteVO) {
        super(id, quadroVigenteVO, TipoConta.PASSIVO, false);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
    }

}
