package crt2.dominio.perfilderisco.gerenciardetalheses;

import java.math.BigDecimal;

import br.gov.bcb.sisaps.src.dominio.GrauPreocupacaoES;
import br.gov.bcb.sisaps.src.mediator.GrauPreocupacaoESMediator;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import crt2.ConfiguracaoTestesNegocio;


public class TestR007Gerenciar_detalhes_da_ES extends ConfiguracaoTestesNegocio {
    

    public String alterar(Integer ciclo, BigDecimal percentual) {
        String retorno = "";
        BigDecimal percentualEdicao = percentual.divide(new BigDecimal(100));
        GrauPreocupacaoES grauEs = GrauPreocupacaoESMediator.get().buscarGrauPreocupacaoESRascunho(ciclo);
        try {
            GrauPreocupacaoESMediator.get().alterarPercentualGrauPreocupacao(grauEs, percentualEdicao);
            retorno = "Campo percentual da Nota final da ES alterado com sucesso.";
        } catch (NegocioException e) {
            retorno = e.getMessage().replace("[", "").replace("]", "");
        }
        return retorno;
    }
}
