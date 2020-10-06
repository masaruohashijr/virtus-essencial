package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.detalhe;

import br.gov.bcb.comum.util.string.StringUtil;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.PainelInformacoesArc;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.analise.PainelResumoElementosArcs;

public class DetalharArcPrevistoDesignado extends DetalharArcComum {

    public DetalharArcPrevistoDesignado(AvaliacaoRiscoControle avaliacao, Matriz matriz, Integer pkAtividade,
            String msg, boolean isPerfilAtual) {
        super(avaliacao, matriz, pkAtividade, isPerfilAtual);
        if (!StringUtil.isVazioOuNulo(msg)) {
            success(msg);
        }
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        form.addOrReplace(
                new PainelInformacoesArc("idPainelInformacoesArc", avaliacao, matriz.getCiclo(), atividade, grupo));
        form.addOrReplace(new PainelResumoElementosArcs("idPainelResumoElementosArc", avaliacao, matriz, avaliacao, 
                isPerfilAtual));
        form.addOrReplace(new PainelNotaVigenteArc("idPainelRiscoMercado", grupo, avaliacao, true, false,
                matriz.getCiclo(), isPerfilAtual));
        form.addOrReplace(new PainelElementosARC("idPainelElementos", avaliacao, matriz.getCiclo()));
        form.addOrReplace(new PainelTendenciaRiscoMercadoDetalheArc("idPainelTendenciaRiscoMercado",
                grupo, avaliacao, true, false, false));
        addOrReplace(form);
    }

}
