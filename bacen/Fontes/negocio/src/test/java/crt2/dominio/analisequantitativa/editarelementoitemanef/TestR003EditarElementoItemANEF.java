package crt2.dominio.analisequantitativa.editarelementoitemanef;

import br.gov.bcb.sisaps.src.dominio.Documento;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ItemElementoAQT;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.ItemElementoAQTMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR003EditarElementoItemANEF extends ConfiguracaoTestesNegocio {
    private String msg;

    public void salvarItemElemento(Integer idAnef, Integer idItemElemento, String justificativa) {
        AnaliseQuantitativaAQT anAqt = AnaliseQuantitativaAQTMediator.get().buscar(idAnef);
        ItemElementoAQT itemElemento = ItemElementoAQTMediator.get().buscarPorPk(idItemElemento);
        if (itemElemento.getDocumento() == null) {
            Documento doc = new Documento();
            doc.setJustificativa(justificativa);
            itemElemento.setDocumento(doc);
        } else {
            Documento doc2 = itemElemento.getDocumento();
            doc2.setJustificativa(justificativa);
            itemElemento.setDocumento(doc2);
        }

        msg = ItemElementoAQTMediator.get().salvarJustificativaItemElementoAQT(anAqt, itemElemento, false);
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMensagem() {
        return msg;
    }

}
