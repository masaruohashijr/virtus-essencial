package br.gov.bcb.sisaps.web.page.dominio.analisequantitativaaqt;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.DelegacaoAQTMediator;
import br.gov.bcb.sisaps.web.page.componentes.botoes.AjaxSubmitLinkSisAps;

@SuppressWarnings("serial")
public class PainelAcoesDelegarAnef extends AbstracSelecionarServidoresPainelAnef {
    private String titulo;

    public PainelAcoesDelegarAnef(String id, AnaliseQuantitativaAQT analiseQuantitativaAQT) {
        super(id, analiseQuantitativaAQT, true);

        setOutputMarkupId(true);
       
    }
    
    @Override
    protected void onConfigure() {
        super.onConfigure();
        add(botaoDelegar());
        addCampos(analiseQuantitativaAQT.getCiclo());
    }

    private AjaxSubmitLinkSisAps botaoDelegar() {
        return new AjaxSubmitLinkSisAps("bttDelegar") {
            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                String mensagemSucesso =
                        DelegacaoAQTMediator.get().incluir(analiseQuantitativaAQT, modelServidorEquipe.getObject(),
                                modelServidorUnidade.getObject());
                getPaginaAtual().avancarParaNovaPagina(new DetalharAQT(analiseQuantitativaAQT, true, mensagemSucesso),
                        getPaginaAtual().getPaginaAnterior());
            }
        };
    }


    private void addCampos(Ciclo ciclo) {
        addComboServidoresEquipe(ciclo, "idServidorEquipeDelegar");
        addComboUnidade("idUnidadeDelegar");
        addComboServidoresUnidade("idServidorUnidadeSelecionadaDelegar");
        addTitulo();

        IModel<String> msgBotao = new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                return addMensagem();
            }

        };

        Label msgButton = new Label("idMsg", msgBotao);
        addOrReplace(msgButton);
    }

    private String addMensagem() {
        return DelegacaoAQTMediator.get().mostraAlertaBotaoDelegar(analiseQuantitativaAQT);
    }

    private void addTitulo() {
        titulo = "Delegação de análise";
        if (analiseQuantitativaAQT.getDelegacao() != null) {
            titulo = "Nova delegação de análise";
        }

        Label tituloDelegacao = new Label("idTitulo", titulo);
        add(tituloDelegacao);
    }

    @Override
    protected void submitButon(AjaxRequestTarget target) {
        //TODO não precisa implementar
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

}
