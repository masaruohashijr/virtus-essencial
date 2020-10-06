package crt2.dominio.analisequantitativa.designaraqt;

import java.util.List;

import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.DesignacaoAQTMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR002DesignarAQT extends ConfiguracaoTestesNegocio {
    private AnaliseQuantitativaAQT analiseQuantitativaAQT;

    public List<ServidorVO> listarServidoresEquipe(int anef) {
        analiseQuantitativaAQT = AnaliseQuantitativaAQTMediator.get().buscar(anef);

        return DesignacaoAQTMediator.get().buscarListaServidorDesignacao(analiseQuantitativaAQT,
                analiseQuantitativaAQT.getCiclo().getEntidadeSupervisionavel().getLocalizacao());
    }

    public List<ServidorVO> listarServidoresEquipe(int anef, String unidadeDeinf) {
        analiseQuantitativaAQT = AnaliseQuantitativaAQTMediator.get().buscar(anef);
        return DesignacaoAQTMediator.get().buscarListaServidorDesignacao(analiseQuantitativaAQT,
                unidadeDeinf);
    }

    public String getNomeServidor(ServidorVO servidor) {
        return servidor.getNome();
    }

}
