package br.gov.bcb.sisaps.web.page.dominio.analisequantitativaaqt;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.botoes.CustomButton;

public class PainelAcoesAnaliseAnef extends PainelSisAps {

    private final AnaliseQuantitativaAQT analiseQuantitativaAQT;

    public PainelAcoesAnaliseAnef(String id, final AnaliseQuantitativaAQT analiseQuantitativaAQT) {
        super(id);
        this.analiseQuantitativaAQT = analiseQuantitativaAQT;
        setOutputMarkupId(true);

        add(new CustomButton("bttAnalisar") {
            @Override
            public void executeSubmit() {
                AnaliseQuantitativaAQTMediator.get().alterarEstadoAnefBotaoAnalisar(analiseQuantitativaAQT);
                getPaginaAtual().avancarParaNovaPagina(new AnaliseAQT(analiseQuantitativaAQT),
                        getPaginaAtual().getPaginaAnterior());
            }
        }.setOutputMarkupId(true));

        IModel<String> msgBotao = new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                return buscarMensagens();
            }

        };

        Label msgButton = new Label("idMsg", msgBotao);
        addOrReplace(msgButton);
    }

    private String buscarMensagens() {
        return AnaliseQuantitativaAQTMediator.get().mostraAlertaBotaoAnalisar(analiseQuantitativaAQT);
    }

    public AnaliseQuantitativaAQT getAnaliseQuantitativaAQT() {
        return analiseQuantitativaAQT;
    }
}
