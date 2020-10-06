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

import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.ParametroNota;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoObjetoVersionadorEnum;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.ParametroNotaMediator;
import br.gov.bcb.sisaps.src.mediator.VersaoPerfilRiscoMediator;
import br.gov.bcb.sisaps.src.vo.AvaliacaoRiscoControleVO;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.ProviderGenericoList;
import br.gov.bcb.sisaps.web.page.componentes.botoes.AjaxSubmitLinkSisAps;
import br.gov.bcb.sisaps.web.page.componentes.drops.CustomDropDownChoice;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Coluna;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Configuracao;
import br.gov.bcb.sisaps.web.page.componentes.tabela.IColunaComponente;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Tabela;

public class PainelAjusteArcCorec extends PainelSisAps {

    private static final String ATIVIDADE_NOME = "atividade.nome";
    private static final String WIDTH_15 = "width:15%";
    private static final String OPCAO_SELECIONE = "Selecione";
    private static final String ASTERISTICO = " *";

    private static final String WIDTH_10 = "width:10%;";

    private Tabela<AvaliacaoRiscoControleVO> tabela;
    private final PerfilRisco perfilRiscoAtual;
    private Label labelAlertaPainel;
    private AjaxSubmitLinkSisAps botaoSalvarInformacoes;
    private List<AvaliacaoRiscoControleVO> listaArc;
    private final List<String> idsAlertas  = new ArrayList<String>();

    public PainelAjusteArcCorec(String id, PerfilRisco perfilRiscoAtual) {
        super(id);
        setOutputMarkupId(true);
        this.perfilRiscoAtual = perfilRiscoAtual;

    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        this.listaArc =
                AvaliacaoRiscoControleMediator.get().consultaArcPerfil(
                        perfilRiscoAtual.getCiclo().getMatriz(),
                        VersaoPerfilRiscoMediator.get().buscarVersoesPerfilRisco(perfilRiscoAtual.getPk(),
                                TipoObjetoVersionadorEnum.ARC));
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

        labelAlertaPainel = new Label("idAlertaPainel", "");
        labelAlertaPainel.setOutputMarkupId(true);
        addOrReplace(labelAlertaPainel);

    }

    private AjaxSubmitLinkSisAps botaoSalvarInformacoes() {
        return new AjaxSubmitLinkSisAps("bttSalvarInformacoesARC") {

            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                AvaliacaoRiscoControleMediator.get().ajustarNotasARCsCorec(listaArc);
                success("Ajustes nos ARCs salvos com sucesso.");
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
        List<Coluna<AvaliacaoRiscoControleVO>> colunas = obterColunas();

        // tabela
        tabela = new Tabela<AvaliacaoRiscoControleVO>("tabela", cfg, colunas, criarProvider(), false);
        addOrReplace(tabela);
    }

    private ProviderGenericoList<AvaliacaoRiscoControleVO> criarProvider() {
        ProviderGenericoList<AvaliacaoRiscoControleVO> provider =
                new ProviderGenericoList<AvaliacaoRiscoControleVO>("", SortOrder.ASCENDING, obterModel());
        return provider;
    }

    private IModel<List<AvaliacaoRiscoControleVO>> obterModel() {
        IModel<List<AvaliacaoRiscoControleVO>> modelConsulta =
                new AbstractReadOnlyModel<List<AvaliacaoRiscoControleVO>>() {
                    @Override
                    public List<AvaliacaoRiscoControleVO> getObject() {

                        return listaArc;
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

    private List<Coluna<AvaliacaoRiscoControleVO>> obterColunas() {
        List<Coluna<AvaliacaoRiscoControleVO>> colunas = new LinkedList<Coluna<AvaliacaoRiscoControleVO>>();

        colunas.add(new Coluna<AvaliacaoRiscoControleVO>().setCabecalho("Atividade").setPropriedade(ATIVIDADE_NOME)
                .setEstiloCabecalho("width:30%").setOrdenar(true));

        colunas.add(new Coluna<AvaliacaoRiscoControleVO>().setCabecalho("Grupo")
                .setPropriedade("parametroGrupoRiscoControle.nomeAbreviado").setOrdenar(true)
                .setEstiloCabecalho(WIDTH_15));

        colunas.add(new Coluna<AvaliacaoRiscoControleVO>().setCabecalho("R/C").setPropriedade("tipo.abreviacao")
                .setOrdenar(true).setEstiloCabecalho("width:5%"));

        colunas.add(new Coluna<AvaliacaoRiscoControleVO>().setCabecalho("Nota final")
                .setPropriedade("notaVigenteSupervisor").setPropriedadeTela("notaVigenteDescricaoValor")
                .setOrdenar(true).setEstiloCabecalho(WIDTH_15));

        colunas.add(new Coluna<AvaliacaoRiscoControleVO>().setCabecalho("Nota comitê").setPropriedade(ATIVIDADE_NOME)
                .setComponente(new ComponenteCelulaDropDown()).setOrdenar(false).setEstiloCabecalho(WIDTH_10));

        return colunas;
    }

    private class ComponenteCelulaDropDown implements IColunaComponente<AvaliacaoRiscoControleVO> {
        @Override
        public Component obterComponente(Item<ICellPopulator<AvaliacaoRiscoControleVO>> cellItem, String componentId,
                final IModel<AvaliacaoRiscoControleVO> rowModel) {

            final Label labelAlerta = new Label("idAlerta", rowModel.getObject().isAlterouNota() ? ASTERISTICO : "");
            labelAlerta.setMarkupId("idAlertaArc_" + rowModel.getObject().getPk());
            labelAlerta.setOutputMarkupId(true);

            AvaliacaoRiscoControle arcLoad = AvaliacaoRiscoControleMediator.get().loadPK(rowModel.getObject().getPk());
            ChoiceRenderer<ParametroNota> renderer = new ChoiceRenderer<ParametroNota>("valor", ParametroNota.PROP_ID);
            List<ParametroNota> listaChoices =
                    ParametroNotaMediator.get().buscarParametrosNotaARCCorec(arcLoad,
                            perfilRiscoAtual.getCiclo().getMetodologia().getPk());
            rowModel.getObject().setNotaCorec(arcLoad.getNotaCorec());
            PropertyModel<ParametroNota> propertyModel =
                    new PropertyModel<ParametroNota>(rowModel.getObject(), "notaCorec");
            DropDownChoice<ParametroNota> selectParametroNota =
                    new CustomDropDownChoice<ParametroNota>("idParametroNota", OPCAO_SELECIONE, propertyModel,
                            listaChoices, renderer);
            selectParametroNota.setMarkupId("idParametroNota_" + rowModel.getObject().getPk());
            selectParametroNota.setOutputMarkupId(true);
            selectParametroNota.add(new OnChangeAjaxBehavior() {
                @Override
                protected void onUpdate(AjaxRequestTarget target) {
                    rowModel.getObject().setAlterouNota(true);
                    labelAlerta.setDefaultModelObject(ASTERISTICO);
                    target.add(labelAlerta);

                    labelAlertaPainel.setDefaultModelObject("*Atenção informações não salvas.");
                    botaoSalvarInformacoes.setEnabled(true);
                    labelAlertaPainel.setOutputMarkupId(true);
                    idsAlertas.add(labelAlertaPainel.getMarkupId());
                    target.add(labelAlertaPainel);
                    target.add(botaoSalvarInformacoes);
                    atualizarBotaoEncerrarCorec(target);
                    ((GestaoCorecPage) getPage()).exibirBotaoVoltarSemSalvar();
                }
            });

            return new PainelDropDownAlerta(componentId, selectParametroNota, labelAlerta);
        }
    }

    public Tabela<AvaliacaoRiscoControleVO> getTabela() {
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
