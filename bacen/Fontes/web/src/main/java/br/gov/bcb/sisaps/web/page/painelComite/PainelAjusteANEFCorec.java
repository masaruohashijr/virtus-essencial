package br.gov.bcb.sisaps.web.page.painelComite;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroNotaAQT;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoObjetoVersionadorEnum;
import br.gov.bcb.sisaps.src.mediator.VersaoPerfilRiscoMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.ParametroNotaAQTMediator;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.ProviderGenericoList;
import br.gov.bcb.sisaps.web.page.componentes.botoes.AjaxSubmitLinkSisAps;
import br.gov.bcb.sisaps.web.page.componentes.drops.CustomDropDownChoice;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Coluna;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Configuracao;
import br.gov.bcb.sisaps.web.page.componentes.tabela.IColunaComponente;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Tabela;

public class PainelAjusteANEFCorec extends PainelSisAps {

    private static final String WIDTH_15 = "width:15%";
    private static final String OPCAO_SELECIONE = "Selecione";
    private static final String ASTERISTICO = " *";

    private Tabela<AnaliseQuantitativaAQT> tabela;
    private final PerfilRisco perfilRiscoAtual;
    private Label labelAlertaPainel;
    private AjaxSubmitLinkSisAps botaoSalvarInformacoes;
    private List<AnaliseQuantitativaAQT> listaAnefs;
    private final List<String> idsAlertas = new ArrayList<String>();

    public PainelAjusteANEFCorec(String id, PerfilRisco perfilRiscoAtual) {
        super(id);
        setOutputMarkupId(true);
        this.perfilRiscoAtual = perfilRiscoAtual;

    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        this.listaAnefs =
                AnaliseQuantitativaAQTMediator.get().buscarAQTsPerfilRisco(
                        VersaoPerfilRiscoMediator.get().buscarVersoesPerfilRisco(perfilRiscoAtual.getPk(),
                                TipoObjetoVersionadorEnum.AQT));
        addComponents();
    }

    private void addComponents() {
        addListaCiclo();
        addBotaoSalvarInformacoes();

    }

    private void addBotaoSalvarInformacoes() {
        botaoSalvarInformacoes = botaoSalvarInformacoes();
        botaoSalvarInformacoes.setOutputMarkupId(true);
        botaoSalvarInformacoes.setEnabled(false);
        addOrReplace(botaoSalvarInformacoes);

        labelAlertaPainel = new Label("idAlertaPainelAnef", "");
        labelAlertaPainel.setOutputMarkupId(true);
        addOrReplace(labelAlertaPainel);

    }

    private AjaxSubmitLinkSisAps botaoSalvarInformacoes() {
        return new AjaxSubmitLinkSisAps("bttSalvarInformacoesANEF") {

            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                success(AnaliseQuantitativaAQTMediator.get().ajustarNotasANEFsCorec(listaAnefs));
                labelAlertaPainel.setDefaultModelObject("");
                target.add(labelAlertaPainel);
                botaoSalvarInformacoes.setEnabled(false);
                target.add(botaoSalvarInformacoes);
                target.add(tabela);
                atualizarBotaoEncerrarCorec(target);
                ((GestaoCorecPage) getPage()).exibirBotaoVoltarAoSalvar();
               
            }

        };
    }

    private void addListaCiclo() {
        // configuração
        Configuracao cfg = obterConfiguracao();
        // colunas da tabela
        List<Coluna<AnaliseQuantitativaAQT>> colunas = obterColunas();

        // tabela
        tabela = new Tabela<AnaliseQuantitativaAQT>("tabela", cfg, colunas, criarProvider(), false);
        addOrReplace(tabela);
    }

    private ProviderGenericoList<AnaliseQuantitativaAQT> criarProvider() {
        ProviderGenericoList<AnaliseQuantitativaAQT> provider =
                new ProviderGenericoList<AnaliseQuantitativaAQT>("parametroAQT.ordem", SortOrder.ASCENDING,
                        obterModel());
        return provider;
    }

    private IModel<List<AnaliseQuantitativaAQT>> obterModel() {
        IModel<List<AnaliseQuantitativaAQT>> modelConsulta = new AbstractReadOnlyModel<List<AnaliseQuantitativaAQT>>() {
            @Override
            public List<AnaliseQuantitativaAQT> getObject() {

                return listaAnefs;
            }
        };
        return modelConsulta;
    }

    private Configuracao obterConfiguracao() {
        Configuracao cfg = new Configuracao("idTitulo", "idDados");
        cfg.setMensagemVazio(Model.of(Constantes.NENHUM_REGISTRO_ENCONTRADO));
        cfg.setExibirPaginador(false);
        cfg.setExibirTitulo(false);
        return cfg;

    }

    private List<Coluna<AnaliseQuantitativaAQT>> obterColunas() {
        List<Coluna<AnaliseQuantitativaAQT>> colunas = new LinkedList<Coluna<AnaliseQuantitativaAQT>>();

        colunas.add(new Coluna<AnaliseQuantitativaAQT>().setCabecalho("Componente")
                .setPropriedade("parametroAQT.descricao").setEstiloCabecalho("width:30%").setOrdenar(true));

        colunas.add(new Coluna<AnaliseQuantitativaAQT>().setCabecalho("Nota final").setPropriedade("valorNota")
                .setPropriedadeTela("notaSupervisorDescricaoValor").setOrdenar(true).setEstiloCabecalho(WIDTH_15));

        colunas.add(new Coluna<AnaliseQuantitativaAQT>().setCabecalho("Nota comitê").setPropriedade("pk")
                .setComponente(new ComponenteCelulaDropDown()).setOrdenar(false).setEstiloCabecalho(WIDTH_15));

        return colunas;
    }

    private class ComponenteCelulaDropDown implements IColunaComponente<AnaliseQuantitativaAQT> {
        @Override
        public Component obterComponente(Item<ICellPopulator<AnaliseQuantitativaAQT>> cellItem, String componentId,
                final IModel<AnaliseQuantitativaAQT> rowModel) {

            final Label labelAlerta = new Label("idAlerta", rowModel.getObject().getAlterouNota() ? ASTERISTICO : "");
            labelAlerta.setMarkupId("idAlertaAnef_" + rowModel.getObject().getPk());
            labelAlerta.setOutputMarkupId(true);

            ChoiceRenderer<ParametroNotaAQT> renderer =
                    new ChoiceRenderer<ParametroNotaAQT>("valor", ParametroNotaAQT.PROP_ID);
            List<ParametroNotaAQT> listaChoices =
                    ParametroNotaAQTMediator.get().buscarParametrosNotaANEFCorec(rowModel.getObject(),
                            perfilRiscoAtual.getCiclo().getMetodologia().getPk());
            PropertyModel<ParametroNotaAQT> propertyModel =
                    new PropertyModel<ParametroNotaAQT>(rowModel.getObject(), "notaCorecAtual");
            DropDownChoice<ParametroNotaAQT> selectParametroNota =
                    new CustomDropDownChoice<ParametroNotaAQT>("idParametroNota", OPCAO_SELECIONE, propertyModel,
                            listaChoices, renderer);
            selectParametroNota.setMarkupId("idParametroNotaAnef_" + rowModel.getObject().getPk());
            selectParametroNota.setOutputMarkupId(true);
            selectParametroNota.add(new OnChangeAjaxBehavior() {
                @Override
                protected void onUpdate(AjaxRequestTarget target) {
                    rowModel.getObject().setAlterouNota(true);
                    labelAlerta.setDefaultModelObject(ASTERISTICO);
                    target.add(labelAlerta);

                    labelAlertaPainel.setDefaultModelObject("*Atenção informações não salvas.");
                    labelAlertaPainel.setOutputMarkupId(true);
                    idsAlertas.add(labelAlertaPainel.getMarkupId());
                    target.add(labelAlertaPainel);
                    botaoSalvarInformacoes.setEnabled(true);
                    target.add(botaoSalvarInformacoes);
                    ((GestaoCorecPage) getPage()).exibirBotaoVoltarSemSalvar();
                    atualizarBotaoEncerrarCorec(target);
                }
            });

            return new PainelDropDownAlerta(componentId, selectParametroNota, labelAlerta);
        }
    }

    public Tabela<AnaliseQuantitativaAQT> getTabela() {
        return tabela;
    }

    public AjaxSubmitLinkSisAps getBotaoSalvarInformacoes() {
        return botaoSalvarInformacoes;
    }
    
    private void atualizarBotaoEncerrarCorec(AjaxRequestTarget target) {
        ((GestaoCorecPage) getPage()).atualizarBotaoEncerrarCorec(target);
    }

    public List<String> getIdsAlertas() {
        return idsAlertas;
    }
    
    
    
}
