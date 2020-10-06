package br.gov.bcb.sisaps.web.page.dominio.analisequantitativa;

import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.model.Model;

import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoConta;
import br.gov.bcb.sisaps.src.vo.analisequantitativa.QuadroPosicaoFinanceiraVO;

public class PainelSelecaoContaPassivo extends PainelBaseAtivoPassivo {

    public PainelSelecaoContaPassivo(String id, final QuadroPosicaoFinanceiraVO vo) {
        super(id, vo, TipoConta.PASSIVO, true);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        labelInformacoes = new LabelInformacoesNaoSalvas("labelInformacoesNaoSalvasContaPassivo") {
            @Override
            public boolean isVisibleAllowed() {
                return isInformacoesNaoSalvas();
            }
        };
        addOrReplace(labelInformacoes);

        hiddenFieldScroll = new HiddenField<String>("passivoHidden", new Model<String>(""));
        addOrReplace(hiddenFieldScroll);
    }
}
