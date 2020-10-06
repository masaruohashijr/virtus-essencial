package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.detalhe;

import org.apache.wicket.request.mapper.parameter.PageParameters;

import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.PainelInformacoesArc;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.analise.PainelResumoElementosArcs;

public class DetalharArcDelegado extends DetalharArcComum {

    public DetalharArcDelegado(AvaliacaoRiscoControle avaliacao, Matriz matriz, Integer pkAtividade, boolean perfilAtual) {
        super(avaliacao, matriz, pkAtividade, perfilAtual);
    }

    public DetalharArcDelegado(AvaliacaoRiscoControle avaliacao, Matriz matriz, Integer pkAtividade, String msg) {
        super(avaliacao, matriz, pkAtividade, true);
        success(msg);
    }
    
    public DetalharArcDelegado(PageParameters parameters) {
		super(parameters);
	}

    @Override
    protected void onConfigure() {
        super.onConfigure();
        form.addOrReplace(
                new PainelInformacoesArc("idPainelInformacoesArc", avaliacao, matriz.getCiclo(), atividade, grupo));
        form.addOrReplace(new PainelResumoElementosArcs("idPainelResumoElementosArc", avaliacao, matriz, avaliacao, 
                isPerfilAtual));
        form.addOrReplace(new PainelNotasArc("idPainelRiscoMercado", grupo, avaliacao, matriz.getCiclo(),
                false, true, false, avaliacao, isPerfilAtual));
        form.addOrReplace(new PainelElementosARC("idPainelElementos", avaliacao, matriz.getCiclo()));
        form.addOrReplace(new PainelTendenciaRiscoMercadoDetalheArc("idPainelTendenciaRiscoMercado",
                grupo, avaliacao, true, true, false));
    }

}
