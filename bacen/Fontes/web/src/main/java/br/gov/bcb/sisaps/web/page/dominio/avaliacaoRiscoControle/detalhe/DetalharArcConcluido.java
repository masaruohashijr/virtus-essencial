package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.detalhe;

import org.apache.wicket.request.mapper.parameter.PageParameters;

import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.PainelInformacoesArc;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.analise.PainelResumoElementosArcs;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.consulta.ConsultaHistoricoPage;

public class DetalharArcConcluido extends DetalharArcComum {

	private AvaliacaoRiscoControle avaliacaoTela = null;
	
    public DetalharArcConcluido(AvaliacaoRiscoControle avaliacao, Matriz matriz, Integer pkAtividade,
            boolean perfilAtual) {
        super(avaliacao, matriz, pkAtividade, perfilAtual);
    }

    public DetalharArcConcluido(PageParameters parameters) {
  		super(parameters);
  	}
    
    @Override
    protected void onConfigure() {
        super.onConfigure();
        if (getPaginaAtual().getPaginaAnterior() instanceof ConsultaHistoricoPage) {
        	this.avaliacaoTela = avaliacao;
            avaliacao = AvaliacaoRiscoControleMediator.get().buscarRascunhoPorArcVigente(avaliacaoTela);
        } else {
        	if(avaliacaoTela == null){
        		this.avaliacaoTela = avaliacao.getAvaliacaoRiscoControleVigente();
        	}
        }

        form.addOrReplace(
                new PainelInformacoesArc("idPainelInformacoesArc", avaliacaoTela, matriz.getCiclo(), atividade, grupo));
        form.addOrReplace(new PainelResumoElementosArcs("idPainelResumoElementosArc", avaliacaoTela, matriz, avaliacao,
                isPerfilAtual));
        form.addOrReplace(new PainelNotasArc("idPainelRiscoMercado", grupo, avaliacaoTela, matriz
                .getCiclo(), true, false, true, avaliacao, isPerfilAtual));
        form.addOrReplace(new PainelElementosARC("idPainelElementos", avaliacaoTela, matriz.getCiclo()));
        form.addOrReplace(new PainelTendenciaRiscoMercadoDetalheArc("idPainelTendenciaRiscoMercado",
                grupo, avaliacaoTela, false, true, true));
    }
}
