package crt2.dominio.analisequantitativa.detalharcabecalhoanexoaqt;

import java.util.ArrayList;
import java.util.List;

import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnexoAQT;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnexoAQTMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR002DetalharCabecalhoAnexoAQT extends ConfiguracaoTestesNegocio {

    private int anef;

    public List<AnexoAQT> getAnexos(int anef) {
        return AnexoAQTMediator.get().buscar(AnaliseQuantitativaAQTMediator.get().buscar(anef));
    }

    public String getDescricao(AnexoAQT anexo) {
        return anexo.getLink();
    }

    public List<String> oQueDeveSerExibido() {
        List<AnexoAQT> buscar = AnexoAQTMediator.get().buscar(AnaliseQuantitativaAQTMediator.get().buscar(anef));
        List<String> retorno = new ArrayList<String>();
        for (AnexoAQT anexo : buscar) {
            retorno.add(anexo.getLink());
        }
        return retorno;
    }

    public int getAnef() {
        return anef;
    }

    public void setAnef(int anef) {
        this.anef = anef;
    }
}
