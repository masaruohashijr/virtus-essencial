package crt2.dominio.analisequantitativa.alterarnotafinal;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.NotaAjustadaAEF;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroNotaAQT;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.NotaAjustadaAEFMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.ParametroNotaAQTMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR001AlterarNotaFinalAEF extends ConfiguracaoTestesNegocio {

    private NotaAjustadaAEF notaAjustadaAEF;
    private ParametroNotaAQT notaAQT;

    public String salvar(Integer idCiclo, String nota, String justificativa) {
        atualizarValores(idCiclo);
        if (nota.equals("&ltRemover ajuste>")) {
            notaAjustadaAEF.setParamentroNotaAQT(null);
            notaAjustadaAEF.setJustificativa(null);
        } else {
            notaAQT = ParametroNotaAQTMediator.get().buscarPorPK(Integer.parseInt(nota));
            notaAjustadaAEF.setParamentroNotaAQT(notaAQT);
        }
        if (!justificativa.isEmpty()) {
            notaAjustadaAEF.setJustificativa(justificativa);
        }
        return NotaAjustadaAEFMediator.get().salvarNotaAjustadaAEF(notaAjustadaAEF);
    }

    private void atualizarValores(Integer idCiclo) {
        Ciclo ciclo = CicloMediator.get().buscarCicloPorPK(idCiclo);
        notaAjustadaAEF = NotaAjustadaAEFMediator.get().buscarNotaAjustadaRascunho(ciclo);
        if (notaAjustadaAEF == null) {
            notaAjustadaAEF = new NotaAjustadaAEF();
        }
        notaAjustadaAEF.setCiclo(ciclo);
    }

    public void limpar() {
        notaAjustadaAEF = null;
        notaAQT = null;
    }

}
