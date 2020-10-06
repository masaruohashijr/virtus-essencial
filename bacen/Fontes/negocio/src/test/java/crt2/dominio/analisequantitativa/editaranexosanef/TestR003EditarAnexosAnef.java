package crt2.dominio.analisequantitativa.editaranexosanef;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnexoAQTMediator;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import crt2.ConfiguracaoTestesNegocio;

public class TestR003EditarAnexosAnef extends ConfiguracaoTestesNegocio {

    public String anexoAnef(Integer cicloPK, Integer anefPK, String caminho) {
        Ciclo ciclo = CicloMediator.get().buscarCicloPorPK(cicloPK);
        AnaliseQuantitativaAQT anef = AnaliseQuantitativaAQTMediator.get().buscar(anefPK);
        String mensagem = null;
        try {
            mensagem = AnexoAQTMediator.get().anexarArquivo(ciclo, anef, caminho, null, false, null, false);
        } catch (NegocioException e) {
            return e.getMessage();
        }
        return mensagem;
    }

}
