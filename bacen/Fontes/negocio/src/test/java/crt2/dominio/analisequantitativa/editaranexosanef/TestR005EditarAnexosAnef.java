package crt2.dominio.analisequantitativa.editaranexosanef;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnexoAQT;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnexoAQTMediator;
import br.gov.bcb.sisaps.src.vo.analisequantitativa.aqt.AnexoAQTVO;
import crt2.ConfiguracaoTestesNegocio;

public class TestR005EditarAnexosAnef extends ConfiguracaoTestesNegocio {

    public String excluirAnexoAnef(Integer cicloPK, Integer anefPK, String caminho) {
        Ciclo ciclo = CicloMediator.get().buscarCicloPorPK(cicloPK);
        AnaliseQuantitativaAQT anef = AnaliseQuantitativaAQTMediator.get().buscar(anefPK);
        AnexoAQT anexoAqt = AnexoAQTMediator.get().buscarPorNome(anef, caminho);
        return AnexoAQTMediator.get().excluirAnexo(AnexoAQTVO.converterParaEntidadeVo(anexoAqt), ciclo, anef);
    }
}
