package br.gov.bcb.sisaps.web.page.dominio.analisequantitativaaqt;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.DesignacaoAQTMediator;

@SuppressWarnings("serial")
public class PainelAcoesExcluirDesignarAnef extends AbstracSelecionarServidoresPainelAnef {

    public PainelAcoesExcluirDesignarAnef(String id, AnaliseQuantitativaAQT analiseQuantitativaAQT) {
        super(id, analiseQuantitativaAQT, false);
        this.analiseQuantitativaAQT = analiseQuantitativaAQT;
        setOutputMarkupId(true);
        addBotao("bttExcluirDesignacao");
        addCampos();
    }

    @Override
    protected void submitButon(AjaxRequestTarget target) {
        DesignacaoAQTMediator.get().excluirDesignacao(analiseQuantitativaAQT);

        getPaginaAtual().avancarParaNovaPagina(
                new DetalharAQT(analiseQuantitativaAQT, true, "Designação de ANEF removida com sucesso."),
                getPaginaAtual().getPaginaAnterior());
    }

    private String buscarMensagens() {
        return DesignacaoAQTMediator.get().mostraAlertaBotaoExcluirDesignar(analiseQuantitativaAQT);
    }

    private void addCampos() {

        IModel<String> msgBotao = new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                return buscarMensagens();
            }
        };

        Label msgButton = new Label("idMsgExcluir", msgBotao);

        addOrReplace(msgButton);
    }

}
