package crt2.dominio.analisequantitativa.delegaraqt;

import java.util.List;

import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.DelegacaoAQTMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR002DelegarAQT extends ConfiguracaoTestesNegocio {

    private AnaliseQuantitativaAQT analiseQuantitativaAQT;

    public List<ServidorVO> listarServidoresEquipe(int anef) {
        analiseQuantitativaAQT = AnaliseQuantitativaAQTMediator.get().buscar(anef);
        return DelegacaoAQTMediator.get().buscarListaServidorDelegacao(analiseQuantitativaAQT,
                analiseQuantitativaAQT.getCiclo().getEntidadeSupervisionavel().getLocalizacao(), perfilUsuario());
    }

    public List<ServidorVO> listarServidoresEquipe(int anef, String unidadeDeinf) {
        analiseQuantitativaAQT = AnaliseQuantitativaAQTMediator.get().buscar(anef);
        return DelegacaoAQTMediator.get().buscarListaServidorDelegacao(analiseQuantitativaAQT, unidadeDeinf, perfilUsuario());
    }

    public String getNomeServidor(ServidorVO servidor) {
        return servidor.getNome();
    }
}
