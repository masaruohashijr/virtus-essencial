package br.gov.bcb.sisaps.web.page.dominio.matriz;

import java.math.BigDecimal;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;

import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.mediator.MatrizCicloMediator;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.botoes.AjaxSubmitLinkSisAps;

@SuppressWarnings("serial")
public class PainelEdicaoBlocoPercentualNovo extends PainelSisAps {
	
	private static final String ID_NOVO_PERCENTUAL_BLOCO_ATIVIDADES = "idNovoPercentualBlocoAtividades";
    private static final String ID_NOVO_PERCENTUAL_GOVERNANCA_CORPORATIVA = "idNovoPercentualBlocoGovernancaCorporativa";
    private static final String BTT_ALTERAR_PERCENTUAL_NOVO = "bttAlterarPercentualNovo";
    private Matriz matrizEsbocada;
    private final Matriz matrizVigente;
    private BigDecimal percentualEdicao;
    private Label novoPercentualNegocio;
    private ModalWindow modalExclusaoGov;

    public PainelEdicaoBlocoPercentualNovo(String id, Matriz matrizEsbocada, Matriz matrizVigente) {
        super(id);
        this.matrizVigente = matrizVigente;
        this.matrizEsbocada = matrizEsbocada;
        addModalExclusao();
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        matrizEsbocada = MatrizCicloMediator.get().getMatrizEmEdicao(matrizEsbocada.getCiclo());
        addComponents();
        addBotoes();
    }

    private void addComponents() {
        addOrReplace(new Label("idPercentualVigenteBlocoAtividades", matrizVigente == null ? ""
                : matrizVigente.getPercentualBlocoAtividades()));
        addOrReplace(new Label("idPercentualVigenteGovernancaCorporativa", matrizVigente == null ? ""
                : matrizVigente.getPercentualGovernancaCorporativa()));
        addOrReplace(new Label("idPercentualEsbocadaBlocoAtividades", matrizEsbocada == null ? ""
                : matrizEsbocada.getPercentualBlocoAtividades()));
        addOrReplace(new Label("idPercentualEsbocadaGovernancaCorporativa", matrizEsbocada == null ? ""
                : matrizEsbocada.getPercentualGovernancaCorporativa()));
        novoPercentualNegocio =
                new Label(ID_NOVO_PERCENTUAL_BLOCO_ATIVIDADES,
                        new PropertyModel<String>(this, "novoPercentualGovernancaCorporativaEdicao"));
        addOrReplace(novoPercentualNegocio);
        addTextPercentual();

    }

    private void addBotoes() {
        AjaxSubmitLink botaoFiltrar = new AjaxSubmitLinkSisAps(BTT_ALTERAR_PERCENTUAL_NOVO) {
            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                BigDecimal numeroFatorRelevanciaAE = null;
                if (percentualEdicao == null) {
                    numeroFatorRelevanciaAE = null;
                    alterarPercentual(target, numeroFatorRelevanciaAE);
                } else if (percentualEdicao.compareTo(BigDecimal.ZERO) == 0) {
                    numeroFatorRelevanciaAE = percentualEdicao.divide(new BigDecimal(100));
                    modalExclusaoGov.setContent(new PainelExclusaoGov(modalExclusaoGov, matrizEsbocada,
                            numeroFatorRelevanciaAE));
                    modalExclusaoGov.setOutputMarkupId(true);
                    modalExclusaoGov.show(target);
                } else {
                    numeroFatorRelevanciaAE = percentualEdicao.divide(new BigDecimal(100));
                    alterarPercentual(target, numeroFatorRelevanciaAE);
                }
                percentualEdicao = null;
            }
        };
        botaoFiltrar.setOutputMarkupId(true);
        botaoFiltrar.setMarkupId(BTT_ALTERAR_PERCENTUAL_NOVO);
        addOrReplace(botaoFiltrar);
    }

    public void alterarPercentual(AjaxRequestTarget target, BigDecimal numeroFatorRelevanciaAE) {
        String msg = MatrizCicloMediator.get().alterarPercentualAE(matrizEsbocada, numeroFatorRelevanciaAE);
        ((EdicaoMatrizPage) getPage()).atualizarPaineis(target, msg, true);
    }

    private void addTextPercentual() {
        TextField<BigDecimal> percentualNegocio =
                new TextField<BigDecimal>(ID_NOVO_PERCENTUAL_GOVERNANCA_CORPORATIVA, new PropertyModel<BigDecimal>(this,
                        "percentualEdicao"));
        percentualNegocio.add(new AjaxFormComponentUpdatingBehavior("onkeyup") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {

                target.add(novoPercentualNegocio);
            }
        });
        percentualNegocio.setMarkupId(ID_NOVO_PERCENTUAL_GOVERNANCA_CORPORATIVA);
        percentualNegocio.setOutputMarkupId(true);
        percentualNegocio.setOutputMarkupPlaceholderTag(true);
        addOrReplace(percentualNegocio);
    }

    public String getNovoPercentualGovernancaCorporativaEdicao() {

        if (percentualEdicao == null) {
            return "";
        } else {
            return new BigDecimal(100).subtract(percentualEdicao).toBigInteger().toString() + "%";
        }

    }

    private void addModalExclusao() {
        modalExclusaoGov = new ModalWindow("modalExclusaoGov");
        modalExclusaoGov.setResizable(false);
        modalExclusaoGov.setInitialHeight(100);
        modalExclusaoGov.setInitialWidth(500);
        modalExclusaoGov.setOutputMarkupId(true);

        addOrReplace(modalExclusaoGov);
    }

}
