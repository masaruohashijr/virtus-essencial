package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.detalhe;

import org.apache.wicket.request.mapper.parameter.PageParameters;

import br.gov.bcb.comum.util.string.StringUtil;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.PainelInformacoesArc;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.analise.PainelResumoElementosArcs;

public class DetalharArcAnalise extends DetalharArcComum {

    public DetalharArcAnalise(AvaliacaoRiscoControle avaliacao, Matriz matriz, Integer pkAtividade, boolean perfilAtual) {
        super(avaliacao, matriz, pkAtividade, perfilAtual);
    }
    
    public DetalharArcAnalise(AvaliacaoRiscoControle avaliacao, Matriz matriz, Integer pkAtividade, String msg) {
        super(avaliacao, matriz, pkAtividade, true);
        if (!StringUtil.isVazioOuNulo(msg)) {
            success(msg);
        }
    }

    public DetalharArcAnalise(PageParameters parameters) {
  		super(parameters);
  	}
    
    @Override
    protected void onConfigure() {
        super.onConfigure();
        form.addOrReplace(
                new PainelInformacoesArc("idPainelInformacoesArc", avaliacao, matriz.getCiclo(), atividade, grupo));
        form.addOrReplace(new PainelResumoElementosArcs("idPainelResumoElementosArc", this.avaliacao, matriz, avaliacao, 
                isPerfilAtual));
        form.addOrReplace(new PainelNotasArc("idPainelRiscoMercado", grupo, avaliacao, matriz.getCiclo(),
                true, true, false, avaliacao, isPerfilAtual));
        form.addOrReplace(new PainelElementosARC("idPainelElementos", avaliacao, matriz.getCiclo()));
        form.addOrReplace(new PainelTendenciaRiscoMercadoDetalheArc("idPainelTendenciaRiscoMercado",
                grupo, avaliacao, true, true, true));
    }

}
