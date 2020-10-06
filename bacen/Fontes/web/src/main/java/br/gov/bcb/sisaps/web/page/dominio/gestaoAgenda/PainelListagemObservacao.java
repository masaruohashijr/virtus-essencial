package br.gov.bcb.sisaps.web.page.dominio.gestaoAgenda;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import br.gov.bcb.app.stuff.seguranca.UsuarioCorrente;
import br.gov.bcb.sisaps.src.dominio.AgendaCorec;
import br.gov.bcb.sisaps.src.dominio.ObservacaoAgendaCorec;
import br.gov.bcb.sisaps.util.geral.DataUtil;
import br.gov.bcb.sisaps.util.validacao.ValidadorObservacaoAgenda;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.ProviderGenericoList;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Coluna;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Configuracao;
import br.gov.bcb.sisaps.web.page.componentes.tabela.IColunaComponente;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Tabela;
import br.gov.bcb.sisaps.web.page.componentes.util.ConstantesImagens;

public class PainelListagemObservacao extends PainelSisAps {

    private static final String ID_TEXTO_NOVA_OBSERVACAO = "idTextoNovaObservacao";
    private static final String WIDTH_5 = "width:5%";

    private Tabela<ObservacaoAgendaCorec> tabela;
    private IModel<List<ObservacaoAgendaCorec>> modelConsulta;
    private ProviderGenericoList<ObservacaoAgendaCorec> provider;
    private AgendaCorec agenda;
    private boolean isAlteracao;
    private String novaObservacao;
    private List<ObservacaoAgendaCorec> listaExcluidos = new ArrayList<ObservacaoAgendaCorec>();
    private TextField<String> observacaoDescricao;

    public PainelListagemObservacao(String id, AgendaCorec agenda, boolean isAlteracao) {
        super(id);
        this.agenda = agenda;
        this.isAlteracao = isAlteracao;
        this.novaObservacao = "";
    }

    @Override
    protected void onConfigure() {
        addTabela();

        addTextNovaObservacao();
        addBotaoLinkIncluir();
    }

    private void addBotaoLinkIncluir() {
        AjaxSubmitLink linkIncluir = new AjaxSubmitLink("LinkIncluir") {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                add(new Image("incluir", ConstantesImagens.IMG_INCLUIR));
                setOutputMarkupId(true);
                setVisible(isAlteracao);
            }

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                ObservacaoAgendaCorec observacao = new ObservacaoAgendaCorec();
                observacao.setDescricao(novaObservacao);
                observacao.setAgenda(agenda);
                observacao.setOperadorAtualizacao(UsuarioCorrente.get().getLogin());
                observacao.setUltimaAtualizacao(DataUtil.getDateTimeAtual());
                if (agenda.getObservacoes() == null) {
                    agenda.setObservacoes(new ArrayList<ObservacaoAgendaCorec>());
                }

                ValidadorObservacaoAgenda validador = new ValidadorObservacaoAgenda();
                validador.camposObrigatorios(observacao);
                if (validador.getMensagens().isEmpty()) {
                    agenda.getObservacoes().add(observacao);
                    novaObservacao = "";
                    target.add(tabela);
                    target.add(observacaoDescricao);
                } else {
                    adicionarErros(validador);
                }
                target.add(getParent().getParent().getParent().get("feedbackmodalwindow"));
            }
        };

        addOrReplace(linkIncluir);
    }

    private void adicionarErros(ValidadorObservacaoAgenda validador) {
        for (String erro : validador.getMensagens()) {
            error(erro);
        }
    }

    protected void addTextNovaObservacao() {
        observacaoDescricao =
                new TextField<String>(ID_TEXTO_NOVA_OBSERVACAO, new PropertyModel<String>(this, "novaObservacao"));
        observacaoDescricao.setVisible(isAlteracao);
        addOrReplace(observacaoDescricao);
    }

    private void addTabela() {
        Configuracao cfg = obterConfiguracao();
        ProviderGenericoList<ObservacaoAgendaCorec> providerGenericoList = criarProvider();
        List<Coluna<ObservacaoAgendaCorec>> colunas = obterColunas();
        tabela = new Tabela<ObservacaoAgendaCorec>("tabela", cfg, colunas, providerGenericoList, true);
        addOrReplace(tabela);
    }

    private Configuracao obterConfiguracao() {
        Configuracao cfg = new Configuracao("idTitulo", "idDados");
        cfg.setExibirTitulo(false);
        cfg.setStyleDados(Model.of("width:100%;"));
        cfg.setExibirPaginador(false);
        cfg.setMensagemVazio(Model.of(""));
        return cfg;
    }

    private ProviderGenericoList<ObservacaoAgendaCorec> criarProvider() {
        provider =
                new ProviderGenericoList<ObservacaoAgendaCorec>("ultimaAtualizacao", SortOrder.ASCENDING, obterModel());
        return provider;
    }

    private IModel<List<ObservacaoAgendaCorec>> obterModel() {
        modelConsulta = new AbstractReadOnlyModel<List<ObservacaoAgendaCorec>>() {
            @Override
            public List<ObservacaoAgendaCorec> getObject() {
                return agenda.getObservacoes();
            }
        };
        return modelConsulta;
    }

    private List<Coluna<ObservacaoAgendaCorec>> obterColunas() {
        List<Coluna<ObservacaoAgendaCorec>> colunas = new LinkedList<Coluna<ObservacaoAgendaCorec>>();

        colunas.add(new Coluna<ObservacaoAgendaCorec>().setCabecalho("Observação").setOrdenar(false)
                .setPropriedade("descricao").setEstiloCabecalho("width:40%"));

        colunas.add(new Coluna<ObservacaoAgendaCorec>().setCabecalho("Usuário").setOrdenar(false)
                .setPropriedade("NomeOperador").setEstiloCabecalho("width:30%"));

        colunas.add(new Coluna<ObservacaoAgendaCorec>().setCabecalho("Data/Hora").setPropriedade("DataHoraFormatada")
                .setOrdenar(false).setEstiloCabecalho("width:20%"));
        if (isAlteracao) {

            colunas.add(new Coluna<ObservacaoAgendaCorec>().setCabecalho("Ação").setEstiloCabecalho(WIDTH_5)
                    .setComponente(new IColunaComponente<ObservacaoAgendaCorec>() {
                        @Override
                        public Component obterComponente(Item<ICellPopulator<ObservacaoAgendaCorec>> cellItem,
                                String componentId, IModel<ObservacaoAgendaCorec> rowModel) {
                            return new PainelAcaoObservacao(componentId, agenda, rowModel, tabela, listaExcluidos);
                        }
                    }));
        }
        return colunas;
    }

    public Tabela<ObservacaoAgendaCorec> getTabelaEntidades() {
        return tabela;
    }

    public void setTabelaEntidades(Tabela<ObservacaoAgendaCorec> tabelaEntidades) {
        this.tabela = tabelaEntidades;
    }

    public IModel<List<ObservacaoAgendaCorec>> getModelConsulta() {
        return modelConsulta;
    }

    public void setModelConsulta(IModel<List<ObservacaoAgendaCorec>> modelConsulta) {
        this.modelConsulta = modelConsulta;
    }

    public ProviderGenericoList<ObservacaoAgendaCorec> getProvider() {
        return provider;
    }

    public void setProvider(ProviderGenericoList<ObservacaoAgendaCorec> provider) {
        this.provider = provider;
    }

    public String getNovaObservacao() {
        return novaObservacao;
    }

    public void setNovaObservacao(String novaObservacao) {
        this.novaObservacao = novaObservacao;
    }

    public List<ObservacaoAgendaCorec> getListaExcluidos() {
        return listaExcluidos;
    }

    public void setListaExcluidos(List<ObservacaoAgendaCorec> listaExcluidos) {
        this.listaExcluidos = listaExcluidos;
    }

}
