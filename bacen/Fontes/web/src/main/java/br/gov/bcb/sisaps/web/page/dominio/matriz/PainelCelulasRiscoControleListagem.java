package br.gov.bcb.sisaps.web.page.dominio.matriz;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import br.gov.bcb.sisaps.src.dominio.Atividade;
import br.gov.bcb.sisaps.src.dominio.CelulaRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Metodologia;
import br.gov.bcb.sisaps.src.dominio.ParametroPeso;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.ProviderGenericoList;
import br.gov.bcb.sisaps.web.page.componentes.drops.CustomDropDownChoice;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Coluna;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Configuracao;
import br.gov.bcb.sisaps.web.page.componentes.tabela.IColunaComponente;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Tabela;
import br.gov.bcb.sisaps.web.page.componentes.tabela.coluna.ColunaDropDown;

public class PainelCelulasRiscoControleListagem extends PainelSisAps {

    private static final String CSS_CENTRALIZADO_FUNDO_PADRAO_A_CLARO2 = "centralizado";
    private static final String PROP_PARAMETRO_GRUPO_RISCO_CONTROLE_NOME = "parametroGrupoRiscoControle.abreviado";
    private static final String OPCAO_SELECIONE = "Selecione";
    private final Atividade atividade;
    private final Metodologia metodologia;
    private Tabela<CelulaRiscoControle> tabela;
    
    private final List<CelulaRiscoControle> listaExcluidos = new ArrayList<CelulaRiscoControle>();
    private final String sufixoMarkupId;
    private boolean isInclusao;

    public PainelCelulasRiscoControleListagem(String id, Atividade atividade, Metodologia metodologia, 
            String sufixoMarkupId, boolean isInclusao) {
        super(id);
        this.atividade = atividade;
        this.metodologia = metodologia;
        this.sufixoMarkupId = sufixoMarkupId;
        this.isInclusao = isInclusao;
        setMarkupId(id);
        setOutputMarkupId(true);
    }
    
    @Override
    protected void onInitialize() {
        super.onInitialize();
        
    }
    
    @Override
    protected void onConfigure() {
        super.onConfigure();
        montarTabela();
    }

    private void montarTabela() {
        Configuracao cfg = obterConfiguracao();
        List<Coluna<CelulaRiscoControle>> colunas = obterColunas();
        tabela = new Tabela<CelulaRiscoControle>("tabela", cfg, colunas, criarProvider(), false);
        addOrReplace(tabela);
    }

    private Configuracao obterConfiguracao() {
        Configuracao cfg = new Configuracao("idTituloARC", "idDadosARC");
        cfg.setTitulo(Model.of("Relação de ARCs associados à atividade"));
        cfg.setCssTitulo(Model.of("tabela fundoPadraoAEscuro3"));
        cfg.setCssPar(cfg.getCssImpar());
        cfg.setMensagemVazio(Model.of("Nenhum ARC associado."));
        cfg.setExibirPaginador(false);
        cfg.setExibirTitulo(false);
        cfg.setExibirTituloHeader(true);
        cfg.setExibirCabecalhoSempre(true);
        return cfg;
    }
    
    private List<Coluna<CelulaRiscoControle>> obterColunas() {
        List<Coluna<CelulaRiscoControle>> colunas = new LinkedList<Coluna<CelulaRiscoControle>>();
        colunas.add(new Coluna<CelulaRiscoControle>().setCabecalho("Grupo")
                .setPropriedade(PROP_PARAMETRO_GRUPO_RISCO_CONTROLE_NOME).setOrdenar(false)
                .setEstiloCabecalho("width:60%;"));

        colunas.add(new Coluna<CelulaRiscoControle>().setCabecalho("Peso").setPropriedade("parametroPeso.descricao")
                .setComponente(new ComponenteCelulaDropDown()).setOrdenar(false).setEstiloCabecalho("width:30%;")
                .setCssCabecalho(CSS_CENTRALIZADO_FUNDO_PADRAO_A_CLARO2));

        colunas.add(new Coluna<CelulaRiscoControle>().setCabecalho("Ação").setEstiloCabecalho("width:10%")
                .setCssCabecalho(CSS_CENTRALIZADO_FUNDO_PADRAO_A_CLARO2)
                .setComponente(new IColunaComponente<CelulaRiscoControle>() {
                    @Override
                    public Component obterComponente(Item<ICellPopulator<CelulaRiscoControle>> cellItem,
                            String componentId, IModel<CelulaRiscoControle> rowModel) {
                        return new AcoesCelulaRiscoControle(componentId, atividade, rowModel, 
                                tabela, listaExcluidos, sufixoMarkupId, isInclusao);
                    }
                }));

        return colunas;
    }
    
    private class ComponenteCelulaDropDown implements IColunaComponente<CelulaRiscoControle> {
        @Override
        public Component obterComponente(Item<ICellPopulator<CelulaRiscoControle>> cellItem, String componentId,
                final IModel<CelulaRiscoControle> rowModel) {
            ChoiceRenderer<ParametroPeso> renderer = new ChoiceRenderer<ParametroPeso>("descricao", ParametroPeso.PROP_ID);
            List<ParametroPeso> listaChoices = metodologia.getParametrosPeso();
            PropertyModel<ParametroPeso> propertyModel =
                    new PropertyModel<ParametroPeso>(rowModel.getObject(), "parametroPeso");
            DropDownChoice<ParametroPeso> selectParametroPeso =
                    new CustomDropDownChoice<ParametroPeso>("idParametroPeso", OPCAO_SELECIONE, propertyModel,
                            listaChoices, renderer);
            selectParametroPeso.setMarkupId("idParametroPeso_" + rowModel.getObject().getPk());
            selectParametroPeso.setOutputMarkupId(true);
            selectParametroPeso.add(new OnChangeAjaxBehavior() {
                @Override
                protected void onUpdate(AjaxRequestTarget target) {
                    //TODO não precisa implementar
                }
            });
            return new ColunaDropDown(componentId, selectParametroPeso);
        }
    }
    
    private ProviderGenericoList<CelulaRiscoControle> criarProvider() {
        ProviderGenericoList<CelulaRiscoControle> provider =
                new ProviderGenericoList<CelulaRiscoControle>(PROP_PARAMETRO_GRUPO_RISCO_CONTROLE_NOME,
                        SortOrder.ASCENDING, obterModel());
        return provider;
    }

    private IModel<List<CelulaRiscoControle>> obterModel() {
        IModel<List<CelulaRiscoControle>> modelConsulta = new AbstractReadOnlyModel<List<CelulaRiscoControle>>() {
            @Override
            public List<CelulaRiscoControle> getObject() {
                if (atividade.getCelulasRiscoControle() == null) {
                    atividade.setCelulasRiscoControle(new ArrayList<CelulaRiscoControle>());
                }
                return atividade.getCelulasRiscoControle();
            }

        };
        return modelConsulta;
    }

    public List<CelulaRiscoControle> getListaExcluidos() {
        return listaExcluidos;
    }

}
