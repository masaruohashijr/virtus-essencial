package crt2.dominio.analisequalitativa.alterarnotafinal;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.NotaMatriz;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.NotaMatrizMediator;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import crt2.ConfiguracaoTestesNegocio;

public class TestR003AlterarNotaFinalQualitativa extends ConfiguracaoTestesNegocio {

    private String msg;

    public String confirmarNotaMatriz(Integer idCiclo) {
        erro = null;
        try {
            Ciclo ciclo = CicloMediator.get().buscarCicloPorPK(idCiclo);
            NotaMatriz notaRascunho = NotaMatrizMediator.get().buscarNotaMatrizRascunho(ciclo.getMatriz());
            msg = NotaMatrizMediator.get().confirmarNotaMatriz(notaRascunho, "");
            return msg;
        } catch (NegocioException e) {
            erro = e;
        }
        return erro == null ? "" : erro.getMessage();

    }

    public String getMensagem() {
        return msg;
    }

}
