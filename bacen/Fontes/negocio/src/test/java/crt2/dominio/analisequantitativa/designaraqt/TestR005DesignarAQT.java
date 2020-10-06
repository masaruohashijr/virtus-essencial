package crt2.dominio.analisequantitativa.designaraqt;

import br.gov.bcb.sisaps.adaptadores.pessoa.BcPessoaAdapter;
import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.DesignacaoAQTMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR005DesignarAQT extends   ConfiguracaoTestesNegocio {
    private static final String VAZIO = "<vazio>";
    private String msg;

    public void designarAQT(int aqt, String matriculaServidorEquipe) {
        designarAQT(aqt, matriculaServidorEquipe, null);
    }

    public void designarOutroAQT(int aqt, String matriculaServidorOutro) {
        designarAQT(aqt, null, matriculaServidorOutro);
    }
    
    
    
    public void designarAQT(int aqt, String matriculaServidorEquipe, String matriculaServidor) {
        ServidorVO servidorEquipe = null;
        ServidorVO servidorUnidade = null;

        if (matriculaServidorEquipe != null && !matriculaServidorEquipe.equals(VAZIO)) {
            servidorEquipe = BcPessoaAdapter.get().buscarServidor(matriculaServidorEquipe);
        }

        if (matriculaServidor != null && !matriculaServidor.equals(VAZIO)) {
            servidorUnidade = BcPessoaAdapter.get().buscarServidor(matriculaServidor);
        }

        msg =
                DesignacaoAQTMediator.get().incluir(AnaliseQuantitativaAQTMediator.get().buscar(aqt), servidorEquipe,
                        servidorUnidade);
        

    }

    public String getMensagem() {
        return msg;
    }

}
