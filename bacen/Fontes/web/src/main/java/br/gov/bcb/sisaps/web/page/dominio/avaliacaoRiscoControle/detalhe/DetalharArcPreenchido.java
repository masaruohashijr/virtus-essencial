package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.detalhe;

import org.apache.wicket.request.mapper.parameter.PageParameters;

import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.PainelInformacoesArc;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.analise.PainelResumoElementosPreenchidoArcs;

public class DetalharArcPreenchido extends DetalharArcComum {

    public DetalharArcPreenchido(AvaliacaoRiscoControle avaliacao, Matriz matriz, Integer pkAtividade,
            boolean isPerfilAtual) {
        super(avaliacao, matriz, pkAtividade, isPerfilAtual);
    }
    
    public DetalharArcPreenchido(PageParameters parameters) {
  		super(parameters);
  	}

    @Override
    protected void onConfigure() {
        super.onConfigure();
        form.addOrReplace(
                new PainelInformacoesArc("idPainelInformacoesArc", avaliacao, matriz.getCiclo(), atividade, grupo));
        form.addOrReplace(new PainelResumoElementosPreenchidoArcs("idPainelResumoElementosArc", avaliacao, matriz,
                avaliacao, isPerfilAtual));
        form.addOrReplace(new PainelNotaVigenteArc("idPainelRiscoMercado", grupo, avaliacao, true, false,
                matriz.getCiclo(), isPerfilAtual));
        form.addOrReplace(new PainelElementosPreenchidoARC("idPainelElementos", avaliacao, matriz.getCiclo()));
        form.addOrReplace(new PainelTendenciaRiscoMercadoDetalheArc("idPainelTendenciaRiscoMercado",
                grupo, avaliacao, true, true, false));
        addOrReplace(form);
    }

    @Override
    public String getCodigoTela() {
        return "APSFW0207";
    }

}
