package crt2.dominio.analisequalitativa.concluirsintese;

import br.gov.bcb.sisaps.src.dominio.SinteseDeRisco;
import br.gov.bcb.sisaps.src.mediator.SinteseDeRiscoMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR001ConcluirSinteseARC extends ConfiguracaoTestesNegocio {

    private String mensagem;

    public void concluirSinteseARC(Integer idSintese) {
        SinteseDeRisco sintese = SinteseDeRiscoMediator.get().buscarSinteseMatrizPorPk(idSintese);
        mensagem = SinteseDeRiscoMediator.get().concluirNovaSinteseMatrizVigenteEPublicarARCs(sintese, sintese.getCiclo());
    }

    public String getMensagem() {
        return mensagem;
    }
    

}
