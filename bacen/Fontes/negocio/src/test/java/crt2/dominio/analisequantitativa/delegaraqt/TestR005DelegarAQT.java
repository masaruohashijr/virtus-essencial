package crt2.dominio.analisequantitativa.delegaraqt;

import br.gov.bcb.sisaps.adaptadores.pessoa.BcPessoaAdapter;
import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.DelegacaoAQTMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR005DelegarAQT extends ConfiguracaoTestesNegocio {

    private static final String VAZIO = "<vazio>";
    private String msg;

    public void delegarAQT(int aqt, String matriculaServidorEquipe) {
        ServidorVO servidorEquipe = null;
        ServidorVO servidorUnidade = null;

        if (!matriculaServidorEquipe.equals(VAZIO)) {
            servidorEquipe = BcPessoaAdapter.get().buscarServidor(matriculaServidorEquipe);
        }

            AnaliseQuantitativaAQT analiseQuantitativaAQT = AnaliseQuantitativaAQTMediator.get().buscar(aqt);
        msg = DelegacaoAQTMediator.get().incluir(analiseQuantitativaAQT, servidorEquipe, servidorUnidade);
    }

    public String estado(int aqt) {
        AnaliseQuantitativaAQT analiseQuantitativaAQT = AnaliseQuantitativaAQTMediator.get().buscar(aqt);
        return analiseQuantitativaAQT.getEstado().getDescricao();
    }

    public String getMensagem() {
        return msg;
    }

}
