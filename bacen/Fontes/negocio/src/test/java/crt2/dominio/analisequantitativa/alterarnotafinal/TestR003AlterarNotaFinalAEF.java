package crt2.dominio.analisequantitativa.alterarnotafinal;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.NotaAjustadaAEF;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.NotaAjustadaAEFMediator;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import crt2.ConfiguracaoTestesNegocio;

public class TestR003AlterarNotaFinalAEF extends ConfiguracaoTestesNegocio {

    private String msg;

    public String confirmarAjusteAEF(Integer idCiclo) {
        erro = null;
        try {
            Ciclo ciclo = CicloMediator.get().buscarCicloPorPK(idCiclo);
            NotaAjustadaAEF notaRascunho = NotaAjustadaAEFMediator.get().buscarNotaAjustadaRascunho(ciclo);
            msg = NotaAjustadaAEFMediator.get().confirmarNotaAjustadaAEF(notaRascunho, "");
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
