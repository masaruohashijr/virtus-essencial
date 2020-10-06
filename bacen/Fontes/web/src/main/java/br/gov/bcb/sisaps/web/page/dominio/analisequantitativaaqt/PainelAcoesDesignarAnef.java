package br.gov.bcb.sisaps.web.page.dominio.analisequantitativaaqt;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.DesignacaoAQTMediator;

@SuppressWarnings("serial")
public class PainelAcoesDesignarAnef extends AbstracSelecionarServidoresPainelAnef {

    public PainelAcoesDesignarAnef(String id, AnaliseQuantitativaAQT analiseQuantitativaAQT) {
        super(id, analiseQuantitativaAQT, false);
        this.analiseQuantitativaAQT = analiseQuantitativaAQT;

    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        setOutputMarkupId(true);
        addTitulo();
        addBotao("bttDesignar");
        addCampos();
    }

    private void addTitulo() {
        String titulo = analiseQuantitativaAQT.getDesignacao() == null ? "Designação" : "Nova designação";
        Label lblTitulo = new Label("idNovaDesignacao", titulo);
        addOrReplace(lblTitulo);

    }

    @Override
    protected void submitButon(AjaxRequestTarget target) {
        String mensagemSucesso =
                DesignacaoAQTMediator.get().incluir(analiseQuantitativaAQT, modelServidorEquipe.getObject(),
                modelServidorUnidade.getObject());

        getPaginaAtual().avancarParaNovaPagina(new DetalharAQT(analiseQuantitativaAQT, true, mensagemSucesso),
                getPaginaAtual().getPaginaAnterior());
    }

    private String buscarMensagens() {
        return DesignacaoAQTMediator.get().mostraAlertaBotaoDesignar(analiseQuantitativaAQT);
    }

    private void addCampos() {
        addComboServidoresEquipe(analiseQuantitativaAQT.getCiclo(), "idServidorEquipe");
        addComboUnidade("idUnidade");
        addComboServidoresUnidade("idServidorUnidadeSelecionada");
        IModel<String> msgBotao = new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                return buscarMensagens();
            }
        };

        Label msgButton = new Label("idMsg", msgBotao);

        addOrReplace(msgButton);
    }

}
