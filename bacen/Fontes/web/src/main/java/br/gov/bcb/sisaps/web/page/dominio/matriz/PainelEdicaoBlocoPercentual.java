package br.gov.bcb.sisaps.web.page.dominio.matriz;

import java.math.BigDecimal;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;

import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.mediator.MatrizCicloMediator;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.botoes.AjaxSubmitLinkSisAps;

@SuppressWarnings("serial")
public class PainelEdicaoBlocoPercentual extends PainelSisAps {

    private static final String ID_NOVO_PERCENTUAL_CORPORATIVO = "idNovoPercentualCorporativo";
    private static final String ID_NOVO_PERCENTUAL_NEGOCIO = "idNovoPercentualNegocio";
    private static final String BTT_ALTERAR_PERCENTUAL = "bttAlterarPercentual";
    private Matriz matrizEsbocada;
    private final Matriz matrizVigente;
    private BigDecimal percentualEdicao;

    private Label novoPercentualNegocio;

    public PainelEdicaoBlocoPercentual(String id, Matriz matrizEsbocada, Matriz matrizVigente) {
        super(id);
        this.matrizVigente = matrizVigente;
        this.matrizEsbocada = matrizEsbocada;
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
        addOrReplace(new Label("idPercentualVigenteNegocio", matrizVigente == null ? ""
                : matrizVigente.getPercentualNegocio()));
        addOrReplace(new Label("dPercentualVigenteCorporativo", matrizVigente == null ? ""
                : matrizVigente.getPercentualCorporativo()));
        addOrReplace(new Label("idPercentualEsbocadaNegocio", matrizEsbocada == null ? ""
                : matrizEsbocada.getPercentualNegocio()));
        addOrReplace(new Label("idPercentualEsbocadaCorporativo", matrizEsbocada == null ? ""
                : matrizEsbocada.getPercentualCorporativo()));
        novoPercentualNegocio =
                new Label(ID_NOVO_PERCENTUAL_NEGOCIO,
                        new PropertyModel<String>(this, "novoPercentualCorporativoEdicao"));
        addOrReplace(novoPercentualNegocio);
        addTextPercentual();

    }

    private void addBotoes() {
        AjaxSubmitLink botaoFiltrar = new AjaxSubmitLinkSisAps(BTT_ALTERAR_PERCENTUAL) {
            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                BigDecimal numeroFatorRelevanciaUC = null;
                if (percentualEdicao == null) {
                    numeroFatorRelevanciaUC = null;
                } else {
                    numeroFatorRelevanciaUC = percentualEdicao.divide(new BigDecimal(100));
                }
                String msg = MatrizCicloMediator.get().alterarPercentualUC(matrizEsbocada, numeroFatorRelevanciaUC);

                ((EdicaoMatrizPage) getPage()).atualizarPaineis(target, msg, true);

                percentualEdicao = null;
            }

        };
        botaoFiltrar.setOutputMarkupId(true);
        botaoFiltrar.setMarkupId(BTT_ALTERAR_PERCENTUAL);
        addOrReplace(botaoFiltrar);
    }

    private void addTextPercentual() {
        TextField<BigDecimal> percentualNegocio =
                new TextField<BigDecimal>(ID_NOVO_PERCENTUAL_CORPORATIVO, new PropertyModel<BigDecimal>(this,
                        "percentualEdicao"));
        percentualNegocio.add(new AjaxFormComponentUpdatingBehavior("onkeyup") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {

                target.add(novoPercentualNegocio);
            }
        });
        percentualNegocio.setMarkupId(ID_NOVO_PERCENTUAL_CORPORATIVO);
        percentualNegocio.setOutputMarkupId(true);
        percentualNegocio.setOutputMarkupPlaceholderTag(true);
        addOrReplace(percentualNegocio);
    }

    public String getNovoPercentualCorporativoEdicao() {

        if (percentualEdicao == null) {
            return "";
        } else {
            return new BigDecimal(100).subtract(percentualEdicao).toBigInteger().toString() + "%";
        }

    }

}
