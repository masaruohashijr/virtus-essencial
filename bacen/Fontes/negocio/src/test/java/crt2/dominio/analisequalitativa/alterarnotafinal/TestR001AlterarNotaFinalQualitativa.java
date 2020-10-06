package crt2.dominio.analisequalitativa.alterarnotafinal;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.NotaMatriz;
import br.gov.bcb.sisaps.src.dominio.ParametroNota;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.NotaMatrizMediator;
import br.gov.bcb.sisaps.src.mediator.ParametroNotaMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR001AlterarNotaFinalQualitativa extends ConfiguracaoTestesNegocio {

    private NotaMatriz notaAjustada;
    private ParametroNota notaMatriz;

    public String salvar(Integer idCiclo, String nota, String justificativa) {
        atualizarValores(idCiclo);
        if (nota.equals("&ltRemover ajuste>")) {
            notaAjustada.setNotaFinalMatriz(null);
            notaAjustada.setJustificativaNota(null);
        } else {
            notaMatriz = ParametroNotaMediator.get().buscarPorPK(Integer.parseInt(nota));
            notaAjustada.setNotaFinalMatriz(notaMatriz);
        }
        if (!justificativa.isEmpty()) {
            notaAjustada.setJustificativaNota(justificativa);
        }
        return NotaMatrizMediator.get().salvarNovaNotaMatrizAjustada(notaAjustada);
    }

    private void atualizarValores(Integer idCiclo) {
        Ciclo ciclo = CicloMediator.get().buscarCicloPorPK(idCiclo);
        notaAjustada = NotaMatrizMediator.get().buscarNotaMatrizRascunho(ciclo.getMatriz());
        if (notaAjustada == null) {
            notaAjustada = new NotaMatriz();
            notaAjustada.setMatriz(ciclo.getMatriz());
        }
    }

    public void limpar() {
        notaAjustada = null;
        notaMatriz = null;
    }
}
