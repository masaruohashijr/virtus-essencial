package br.gov.bcb.sisaps.web.page.dominio.gerenciarnotasintese.painel;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.Metodologia;
import br.gov.bcb.sisaps.src.dominio.ParametroGrupoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoParametroGrupoRiscoControleEnum;
import br.gov.bcb.sisaps.src.mediator.ParametroGrupoRiscoControleMediator;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;

public class PainelListaSinteses extends Panel {

    @SpringBean
    private ParametroGrupoRiscoControleMediator parametroGrupoRiscoControleMediator;

    private final Matriz matriz;

    private final List<String> idsAlertas = new ArrayList<String>();

    private final Metodologia metodologia;

    private final PerfilRisco perfilRisco;

    public PainelListaSinteses(String id, PerfilRisco perfilRisco, Metodologia metodologia, Matriz matriz) {
        super(id);
        this.perfilRisco = perfilRisco;
        this.metodologia = metodologia;
        this.matriz = matriz;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        addListaSinteses();
    }

    private void addListaSinteses() {
        final List<Integer> idsParametrosGrupoRiscoMatriz =
                ParametroGrupoRiscoControleMediator.get().buscarIdsGruposRiscoDaMatriz(matriz);
        
        List<ParametroGrupoRiscoControle> listaParamentrosGrupoRiscoControle =
                parametroGrupoRiscoControleMediator
                        .buscarGruposRiscoDaMatrizESinteseObrigatoria(idsParametrosGrupoRiscoMatriz);
        
        if (this.matriz.getPercentualGovernancaCorporativoInt() > 0) {
            for (ParametroGrupoRiscoControle prcGrupoRiscoControle 
                    : this.matriz.getCiclo().getMetodologia().getParametrosGrupoRiscoControle()) {
                if (TipoParametroGrupoRiscoControleEnum.EXTERNO.equals(prcGrupoRiscoControle.getTipoGrupo())
                        && !listaParamentrosGrupoRiscoControle.contains(prcGrupoRiscoControle)) {
                    listaParamentrosGrupoRiscoControle.add(prcGrupoRiscoControle);
                    idsParametrosGrupoRiscoMatriz.add(prcGrupoRiscoControle.getPk());
                    break;
                }
            } 
        }

        ListView<ParametroGrupoRiscoControle> listView =
                new ListView<ParametroGrupoRiscoControle>("listaGrupoRiscoSinteses", listaParamentrosGrupoRiscoControle) {
            @Override
            protected void populateItem(ListItem<ParametroGrupoRiscoControle> item) {
                PainelGerenciarSintese painelGerenciarSintese =
                        new PainelGerenciarSintese("sintese", perfilRisco, metodologia, matriz,
                                item.getModelObject(), idsParametrosGrupoRiscoMatriz, idsAlertas);
                painelGerenciarSintese.setMarkupId("Sintese" + "_"
                        + SisapsUtil.criarMarkupId(item.getModelObject().getNomeAbreviado()));
                painelGerenciarSintese.setOutputMarkupId(true);
                item.add(painelGerenciarSintese);
            }
        };
        
        addOrReplace(listView);
    }

    public List<String> getIdsAlertas() {
        return idsAlertas;
    }

}