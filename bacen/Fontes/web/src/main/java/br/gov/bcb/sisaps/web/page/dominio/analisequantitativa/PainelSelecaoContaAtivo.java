package br.gov.bcb.sisaps.web.page.dominio.analisequantitativa;

import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.model.Model;

import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoConta;
import br.gov.bcb.sisaps.src.vo.analisequantitativa.QuadroPosicaoFinanceiraVO;

public class PainelSelecaoContaAtivo extends PainelBaseAtivoPassivo {

    public PainelSelecaoContaAtivo(String id, final QuadroPosicaoFinanceiraVO vo) {
        super(id, vo, TipoConta.ATIVO, true);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        labelInformacoes = new LabelInformacoesNaoSalvas("labelInformacoesNaoSalvasContaAtivo") {
            @Override
            public boolean isVisibleAllowed() {
                return isInformacoesNaoSalvas();
            }
        };
        addOrReplace(labelInformacoes);
        hiddenFieldScroll = new HiddenField<String>("ativoHidden", new Model<String>(""));
        addOrReplace(hiddenFieldScroll);
    }

}
