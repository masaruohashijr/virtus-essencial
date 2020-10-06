package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.consulta;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.LogOperacaoMediator;
import br.gov.bcb.sisaps.web.page.PainelSisAps;

public class PainelConsultaFiltroPerfilDeRiscoArc extends PainelSisAps {

    private final IModel<AvaliacaoRiscoControle> modelArc = new Model<AvaliacaoRiscoControle>();
    private List<AvaliacaoRiscoControle> listaArcs;
    private final AvaliacaoRiscoControle avaliacao;
    private final Ciclo ciclo;

    public PainelConsultaFiltroPerfilDeRiscoArc(String id, AvaliacaoRiscoControle avaliacao, Ciclo ciclo) {
        super(id);
        this.avaliacao = avaliacao;
        this.ciclo = ciclo;

    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        listaArcs = carregarListaArcs(avaliacao);
        addComboPerfisRisco();
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        setVisibilityAllowed(CollectionUtils.isNotEmpty(listaArcs));
    }

    private List<AvaliacaoRiscoControle> carregarListaArcs(AvaliacaoRiscoControle avaliacao) {

        List<AvaliacaoRiscoControle> listaArcs = new ArrayList<AvaliacaoRiscoControle>();
        listaArcs.add(avaliacao);
        addProximos(avaliacao, listaArcs);
        return listaArcs;
    }

    private void addProximos(AvaliacaoRiscoControle avaliacao, List<AvaliacaoRiscoControle> listaArcs) {
        if (avaliacao.getAvaliacaoRiscoControleVigente() != null) {
            if (!(avaliacao.getUltimaAtualizacao().toLocalDate().equals(avaliacao.getAvaliacaoRiscoControleVigente()
                    .getUltimaAtualizacao().toLocalDate()))) {
                listaArcs.add(avaliacao.getAvaliacaoRiscoControleVigente());
            }
            addProximos(avaliacao.getAvaliacaoRiscoControleVigente(), listaArcs);
        }
    }

    private void addComboPerfisRisco() {
        DropDownChoice<AvaliacaoRiscoControle> selectArc =
                new DropDownChoice<AvaliacaoRiscoControle>("idAvaliacaoRiscoControle", modelArc, listaArcs,
                        new AvaliacaoRiscoControleChoiceRenderer());
        selectArc.add(new OnChangeAjaxBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                PerfilRisco perfilRiscoAtual = AvaliacaoRiscoControleMediator.get().buscarUltimoPerfilRisco(avaliacao);
                LogOperacaoMediator.get().gerarLogDetalhamento(ciclo.getEntidadeSupervisionavel(), perfilRiscoAtual,
                        atualizarDadosPagina(getPaginaAtual()));
                atualizarPagina(target);

            }
        });
        setValorDefaultAvaliacaoRiscoControle();
        selectArc.setNullValid(false);
        addOrReplace(selectArc);
    }

    private void setValorDefaultAvaliacaoRiscoControle() {
        modelArc.setObject(listaArcs.get(0));
    }

    private void atualizarPagina(AjaxRequestTarget target) {
        ((ConsultaArcPerfilDeRiscoPage) getPage()).atualizarPagina(target);
    }

    private class AvaliacaoRiscoControleChoiceRenderer extends ChoiceRenderer<AvaliacaoRiscoControle> {
        @Override
        public Object getDisplayValue(AvaliacaoRiscoControle perfil) {
            if (perfil.getPk().equals(listaArcs.get(0).getPk())) {
                return "vigente";
            } else {
                return perfil.getDataFormatada();
            }
        }

        @Override
        public String getIdValue(AvaliacaoRiscoControle perfil, int index) {
            return perfil.getPk().toString();
        }
    }

    public AvaliacaoRiscoControle getAvaliacaoRiscoControleSelecionado() {
        return modelArc.getObject();
    }

}
