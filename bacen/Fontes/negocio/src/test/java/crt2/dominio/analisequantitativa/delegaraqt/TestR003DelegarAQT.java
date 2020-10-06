package crt2.dominio.analisequantitativa.delegaraqt;

import br.gov.bcb.sisaps.adaptadores.pessoa.BcPessoaAdapter;
import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.DelegacaoAQTMediator;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import crt2.ConfiguracaoTestesNegocio;

public class TestR003DelegarAQT extends ConfiguracaoTestesNegocio {

    private static final String VAZIO = "<vazio>";

    public String delegarAQT(int aqt, String matriculaServidorEquipe, String matriculaServidor) {
        erro = null;
        ServidorVO servidorEquipe = null;
        ServidorVO servidorUnidade = null;

        if (!matriculaServidorEquipe.equals(VAZIO)) {
            servidorEquipe = BcPessoaAdapter.get().buscarServidor(matriculaServidorEquipe);
        }

        if (matriculaServidor != null && !matriculaServidor.equals(VAZIO)) {
            servidorUnidade = BcPessoaAdapter.get().buscarServidor(matriculaServidor);
        }

        try {
            AnaliseQuantitativaAQT analiseQuantitativaAQT = AnaliseQuantitativaAQTMediator.get().buscar(aqt);
            DelegacaoAQTMediator.get().incluir(analiseQuantitativaAQT, servidorEquipe, servidorUnidade);
        } catch (NegocioException e) {
            erro = e;
        }
        return erro == null ? "" : erro.getMessage();
    }

    public String estado(int aqt) {
        AnaliseQuantitativaAQT analiseQuantitativaAQT = AnaliseQuantitativaAQTMediator.get().buscar(aqt);
        return analiseQuantitativaAQT.getEstado().getDescricao();
    }
}
