package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.detalhe;

import br.gov.bcb.comum.util.string.StringUtil;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.PainelInformacoesArc;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.analise.PainelResumoElementosArcs;

public class DetalharArcEdicao extends DetalharArcComum {

    public DetalharArcEdicao(AvaliacaoRiscoControle avaliacao, Matriz matriz, Integer pkAtividade, boolean perfilAtual) {
        this(avaliacao, matriz, pkAtividade, null, perfilAtual);
    }
    
    public DetalharArcEdicao(AvaliacaoRiscoControle avaliacao, Matriz matriz, Integer pkAtividade, 
            String msg, boolean perfilAtual) {
        super(avaliacao, matriz, pkAtividade, perfilAtual);
        if (!StringUtil.isVazioOuNulo(msg)) {
            success(msg);
        }
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        form.addOrReplace(
                new PainelInformacoesArc("idPainelInformacoesArc", avaliacao, matriz.getCiclo(), atividade, grupo));
        form.addOrReplace(new PainelResumoElementosArcs("idPainelResumoElementosArc", this.avaliacao, matriz, avaliacao, 
                isPerfilAtual));
        form.addOrReplace(new PainelNotasArc("idPainelRiscoMercado", grupo, avaliacao, matriz.getCiclo(),
                false, true, false, avaliacao, isPerfilAtual));

        form.addOrReplace(new PainelElementosARC("idPainelElementos", avaliacao, matriz.getCiclo()));
        form.addOrReplace(new PainelTendenciaRiscoMercadoDetalheArc("idPainelTendenciaRiscoMercado",
                grupo, avaliacao, true, true, false));

    }

}
