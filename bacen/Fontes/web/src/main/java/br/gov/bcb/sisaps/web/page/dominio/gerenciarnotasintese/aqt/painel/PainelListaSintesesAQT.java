package br.gov.bcb.sisaps.web.page.dominio.gerenciarnotasintese.aqt.painel;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.gov.bcb.sisaps.src.dominio.Metodologia;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroAQT;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.ParametroAQTMediator;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;

public class PainelListaSintesesAQT extends Panel {

    @SpringBean
    private ParametroAQTMediator parametroAQTMediator;

    private final List<String> idsAlertas = new ArrayList<String>();

    private final Metodologia metodologia;

    private PerfilRisco perfilRisco;

    public PainelListaSintesesAQT(String id, PerfilRisco perfilRisco, Metodologia metodologia) {
        super(id);
        this.perfilRisco = perfilRisco;
        this.metodologia = metodologia;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        addListaSinteses();
    }

    private void addListaSinteses() {
        ListView<ParametroAQT> listView =
                new ListView<ParametroAQT>("listaGrupoRiscoSinteses",
                        parametroAQTMediator.buscarParamentos(metodologia)) {
                    @Override
                    protected void populateItem(ListItem<ParametroAQT> item) {
                        PainelGerenciarSinteseAQT painelGerenciarSintese =
                                new PainelGerenciarSinteseAQT("sintese", perfilRisco, 
                                        item.getModelObject(), idsAlertas);
                        painelGerenciarSintese.setMarkupId("Sintese" + "_"
                                + SisapsUtil.criarMarkupId(item.getModelObject().getDescricao()));
                        painelGerenciarSintese.setOutputMarkupId(true);
                        item.add(painelGerenciarSintese);
                    }
                };
        addOrReplace(listView);
    }

    public List<String> getIdsAlertas() {
        return idsAlertas;
    }

    public void setPerfilRisco(PerfilRisco perfilRisco) {
        this.perfilRisco = perfilRisco;
    }

}