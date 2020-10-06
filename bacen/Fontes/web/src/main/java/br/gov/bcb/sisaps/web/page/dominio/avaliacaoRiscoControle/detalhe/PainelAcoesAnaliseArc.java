package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.detalhe;

import br.gov.bcb.sisaps.src.dominio.Atividade;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.botoes.CustomButton;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.analise.AnalisarArcPage;

@SuppressWarnings("serial")
public class PainelAcoesAnaliseArc extends PainelSisAps {

    public PainelAcoesAnaliseArc(String id, final AvaliacaoRiscoControle avaliacao, final Matriz matriz,
            final Atividade atividade) {
        super(id);
        setOutputMarkupId(true);

        add(new CustomButton("bttAnalisar") {
            @Override
            public void executeSubmit() {
                AvaliacaoRiscoControleMediator.get().alterarEstadoARCBotaoAnalisar(avaliacao);
                getPaginaAtual().avancarParaNovaPagina(new AnalisarArcPage(avaliacao, matriz, atividade));
            }
        }.setOutputMarkupId(true));
    }
}