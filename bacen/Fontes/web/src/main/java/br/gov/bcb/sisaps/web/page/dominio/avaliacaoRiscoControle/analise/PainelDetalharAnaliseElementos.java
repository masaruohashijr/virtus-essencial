package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.analise;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.PropertyModel;

import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Elemento;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.web.page.PainelSisAps;

public class PainelDetalharAnaliseElementos extends PainelSisAps {
    private static final String ANALISE = "Análise";
    private static final String ANALISE_VIGENTE = ANALISE + " vigente";
    private static final String T_DADOS_ELEMENTOS = "tDadosAnaliseElementoSupervisor";
    private static final String ID_TITULO_ELEMENTO = "idTituloElementoSupervisor";
    private final Elemento elemento;
    private final Elemento elementoARCVigente;
    private final WebMarkupContainer painelElemento = new WebMarkupContainer(T_DADOS_ELEMENTOS);
    private final AvaliacaoRiscoControle avaliacao;
    private final Ciclo ciclo;

    public PainelDetalharAnaliseElementos(String id, AvaliacaoRiscoControle avaliacao, Ciclo ciclo, Elemento elemento,
            Elemento elementoARCVigente) {
        super(id);
        this.avaliacao = avaliacao;
        this.ciclo = ciclo;
        this.elemento = elemento;
        this.elementoARCVigente = elementoARCVigente;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        addComponentes(elemento);
    }

    private void addComponentes(Elemento elemento) {
        String titulo = "Análise do supervisor para o elemento \"" + elemento.getParametroElemento().getNome() + "\"";
        Label nome = new Label(ID_TITULO_ELEMENTO, titulo);
        nome.setMarkupId(ID_TITULO_ELEMENTO + elemento.getPk());
        nome.setOutputMarkupId(true);
        painelElemento.add(nome);
        painelElemento.setMarkupId(T_DADOS_ELEMENTOS + elemento.getPk());

        String justificativaSupervisorVigente =
                elementoARCVigente == null || elementoARCVigente.getJustificativaSupervisor() == null ? ""
                        : elementoARCVigente.getJustificativaSupervisor();
        painelElemento
                .add(new Label("idAvaliacaoVigente", justificativaSupervisorVigente).setEscapeModelStrings(false));

        painelElemento.add(new Label("idNovaAnalise", "Nova análise").setVisibilityAllowed(!estadoConcluido()));

        painelElemento.add(new Label("idNovaAvaliacao", new PropertyModel<String>(elemento, "justificativaSupervisor"))
                .setVisibilityAllowed(!estadoConcluido()).setEscapeModelStrings(false));

        Label tituloAnaliseVigente = new Label("tituloAnaliseVigente", estadoConcluido() ? ANALISE : ANALISE_VIGENTE);
        painelElemento.add(tituloAnaliseVigente);

        add(painelElemento);

    }

    private boolean estadoConcluido() {
        return AvaliacaoRiscoControleMediator.get().estadoConcluido(avaliacao.getEstado());
    }

    public Ciclo getCiclo() {
        return ciclo;
    }

    public AvaliacaoRiscoControle getAvaliacao() {
        return avaliacao;
    }

}