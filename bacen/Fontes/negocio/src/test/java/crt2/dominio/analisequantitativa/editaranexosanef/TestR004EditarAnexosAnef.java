package crt2.dominio.analisequantitativa.editaranexosanef;

import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ItemElementoAQT;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnexoItemElementoAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.ItemElementoAQTMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR004EditarAnexosAnef extends ConfiguracaoTestesNegocio {

    public String anexoItemAnef(Integer idItem, String caminho) {
        ItemElementoAQT item = ItemElementoAQTMediator.get().buscarPorPk(idItem);
        return AnexoItemElementoAQTMediator.get().anexarArquivo(item, caminho, null);
    }

}
