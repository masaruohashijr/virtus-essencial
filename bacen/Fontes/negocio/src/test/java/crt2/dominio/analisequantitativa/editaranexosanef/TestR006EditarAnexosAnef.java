package crt2.dominio.analisequantitativa.editaranexosanef;

import java.util.List;

import br.gov.bcb.sisaps.src.dominio.AnexoDocumento;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ItemElementoAQT;
import br.gov.bcb.sisaps.src.mediator.AnexoDocumentoMediator;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnexoItemElementoAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.ItemElementoAQTMediator;
import br.gov.bcb.sisaps.src.vo.AnexoDocumentoVo;
import crt2.ConfiguracaoTestesNegocio;

public class TestR006EditarAnexosAnef extends ConfiguracaoTestesNegocio {

    public String excluirAnexoItemAnef(Integer itemElementoAnef, String caminho) {
        ItemElementoAQT item = ItemElementoAQTMediator.get().buscarPorPk(itemElementoAnef);
        List<AnexoDocumento> anexoDocumento = AnexoDocumentoMediator.get().buscar(item.getDocumento());
        Ciclo ciclo = CicloMediator.get().load(item.getElemento().getAnaliseQuantitativaAQT().getCiclo());
        return AnexoItemElementoAQTMediator.get().excluirAnexo(
                AnexoDocumentoVo.converterParaEntidade(anexoDocumento.get(0)), item, ciclo);
    }
}
