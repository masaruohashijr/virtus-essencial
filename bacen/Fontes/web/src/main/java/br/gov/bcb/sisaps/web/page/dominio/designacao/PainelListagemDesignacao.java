package br.gov.bcb.sisaps.web.page.dominio.designacao;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.CheckGroupSelector;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TituloTelaARCEnum;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.DesignacaoMediator;
import br.gov.bcb.sisaps.src.mediator.MatrizCicloMediator;
import br.gov.bcb.sisaps.src.vo.ARCDesignacaoVO;
import br.gov.bcb.sisaps.src.vo.ArcNotasVO;
import br.gov.bcb.sisaps.src.vo.ConsultaARCDesignacaoVO;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.ProviderGenericoList;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Coluna;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Configuracao;
import br.gov.bcb.sisaps.web.page.componentes.tabela.IColunaComponente;
import br.gov.bcb.sisaps.web.page.componentes.tabela.IColunaComponenteHeader;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Tabela;
import br.gov.bcb.sisaps.web.page.componentes.tabela.cabecalho.CabecalhoCheckGroupSelector;
import br.gov.bcb.sisaps.web.page.componentes.tabela.coluna.ColunaCheck;
import br.gov.bcb.sisaps.web.page.componentes.tabela.coluna.ColunaLink;
import br.gov.bcb.sisaps.web.page.dominio.ciclo.painel.UtilNavegabilidadeARC;

public class PainelListagemDesignacao extends PainelSisAps {

    private static final String ESTADO_DESCRICAO = "estado.descricao";
    private static final String WIDTH_25 = "width:25%;";
    private static final String NOME_ATIVIDADE = "nomeAtividade";

    private Tabela<ARCDesignacaoVO> tabelaARCs;
    private List<ARCDesignacaoVO> arcsSelecionados = new ArrayList<ARCDesignacaoVO>();
    private final CheckGroup<ARCDesignacaoVO> checkGroupARCsSelecionados = new CheckGroup<ARCDesignacaoVO>(
            "checkGroupARC", new PropertyModel<List<ARCDesignacaoVO>>(this, "arcsSelecionados"));

    @SpringBean
    private DesignacaoMediator designacaoMediator;
    private final Ciclo ciclo;

    private IModel<List<ARCDesignacaoVO>> modelConsulta;

    private final boolean exibirCheckBox;
    private final String titulo;
    private final ConsultaARCDesignacaoVO consulta;
    private final List<ARCDesignacaoVO> listaArcs;
    private final String id;

    public PainelListagemDesignacao(String id, Ciclo ciclo, ConsultaARCDesignacaoVO consulta, boolean exibirCheckBox,
            String titulo) {
        this(id, ciclo, null, consulta, exibirCheckBox, titulo);
    }

    public PainelListagemDesignacao(String id, Ciclo ciclo, List<ARCDesignacaoVO> listaArcs,
            ConsultaARCDesignacaoVO consulta, boolean exibirCheckBox, String titulo) {
        super(id);
        this.id = id;
        this.listaArcs = listaArcs;
        this.ciclo = ciclo;
        this.consulta = consulta;
        this.exibirCheckBox = exibirCheckBox;
        this.titulo = titulo;
        addTabela(ciclo);
    }

    private void addTabela(Ciclo ciclo) {
        Configuracao cfg = obterConfiguracao();
        ProviderGenericoList<ARCDesignacaoVO> providerGenericoList = criarProvider(ciclo);
        List<Coluna<ARCDesignacaoVO>> colunas = obterColunas();
        tabelaARCs = new Tabela<ARCDesignacaoVO>("tabela", cfg, colunas, providerGenericoList, true);
        tabelaARCs.setEscapeModelStrings(true);
        checkGroupARCsSelecionados.addOrReplace(tabelaARCs);
        addOrReplace(checkGroupARCsSelecionados);
    }

    private Configuracao obterConfiguracao() {
        Configuracao cfg = new Configuracao(id + "Titulo", id + "Dados");
        cfg.setExibirTitulo(true);
        cfg.setTitulo(Model.of(titulo));
        cfg.setCssTitulo(Model.of("tabela fundoPadraoAEscuro3"));
        cfg.setExibirPaginador(false);
        cfg.setMensagemVazio(Model.of(Constantes.NENHUM_REGISTRO_ENCONTRADO));
        return cfg;
    }

    private ProviderGenericoList<ARCDesignacaoVO> criarProvider(Ciclo ciclo) {
        ProviderGenericoList<ARCDesignacaoVO> provider =
                new ProviderGenericoList<ARCDesignacaoVO>(NOME_ATIVIDADE, SortOrder.ASCENDING, obterModel(ciclo));
        return provider;
    }

    private IModel<List<ARCDesignacaoVO>> obterModel(final Ciclo ciclo) {
        modelConsulta = new AbstractReadOnlyModel<List<ARCDesignacaoVO>>() {
            @Override
            public List<ARCDesignacaoVO> getObject() {
                return listaArcs == null ? designacaoMediator.buscarListaArcs(ciclo, consulta) : listaArcs;
            }
        };
        return modelConsulta;
    }

    private List<Coluna<ARCDesignacaoVO>> obterColunas() {
        List<Coluna<ARCDesignacaoVO>> colunas = new LinkedList<Coluna<ARCDesignacaoVO>>();

        if (exibirCheckBox) {
            colunas.add(new Coluna<ARCDesignacaoVO>().setEstiloCabecalho("width:2%; background-color:#93abc5;")
                    .setOrdenar(false).setComponente(new ComponenteCelulaCheckBox())
                    .setComponenteHeader(new ComponenteHeaderCheckBox()));
        }
        colunas.add(new Coluna<ARCDesignacaoVO>().setCabecalho("Atividade").setOrdenar(true)
                .setPropriedade(NOME_ATIVIDADE).setEstiloCabecalho(WIDTH_25));
        colunas.add(new Coluna<ARCDesignacaoVO>().setCabecalho("Grupo").setOrdenar(true)
                .setPropriedade("nomeGrupoRiscoControle").setEstiloCabecalho(WIDTH_25));
        colunas.add(new Coluna<ARCDesignacaoVO>().setCabecalho("R/C").setOrdenar(true)
                .setPropriedade("tipoGrupoRiscoControle").setPropriedadeTela("tipoGrupoRiscoControle.abreviacao")
                .setEstiloCabecalho("width:5%"));
        colunas.add(new Coluna<ARCDesignacaoVO>().setCabecalho("Estado").setOrdenar(true).setPropriedade("estado")
                .setPropriedadeTela(ESTADO_DESCRICAO).setEstiloCabecalho("width:10%;")
                .setComponente(new ComponenteCelulaLink()));
        colunas.add(new Coluna<ARCDesignacaoVO>().setCabecalho("Responsável").setPropriedade("responsavel")
                .setOrdenar(true).setEstiloCabecalho("width:30%;").setComponente(new ComponenteCelulaResponsavel()));
        return colunas;
    }

    private class ComponenteCelulaResponsavel implements IColunaComponente<ARCDesignacaoVO> {
        @Override
        public Component obterComponente(Item<ICellPopulator<ARCDesignacaoVO>> cellItem, final String componentId,
                final IModel<ARCDesignacaoVO> rowModel) {
            String responsavel = designacaoMediator.preencherResponsavel(rowModel.getObject(), ciclo);
            return new Label(componentId, responsavel);
        }
    }

    private class ComponenteCelulaLink implements IColunaComponente<ARCDesignacaoVO> {
        @Override
        public Component obterComponente(Item<ICellPopulator<ARCDesignacaoVO>> cellItem, final String componentId,
                final IModel<ARCDesignacaoVO> rowModel) {

            AjaxSubmitLink link = new AjaxSubmitLink("link") {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    Matriz matriz = MatrizCicloMediator.get().loadPK(rowModel.getObject().getPkMatriz());
                    ArcNotasVO avaliacaoRiscoControle =
                            AvaliacaoRiscoControleMediator.get().consultarNotasArc(rowModel.getObject().getPk());
                    TituloTelaARCEnum titulo =
                            UtilNavegabilidadeARC.novaPaginaDeAcordoStatus(matriz,
                                    avaliacaoRiscoControle, false, true, getPaginaAtual().getPerfilPorPagina());
                    instanciarPaginaDestinoARC(avaliacaoRiscoControle, matriz, 
                            rowModel.getObject().getPkAtividade(), titulo, true);
                }
            };
            link.setBody(new PropertyModel<String>(rowModel.getObject(), ESTADO_DESCRICAO));
            link.setMarkupId(link.getId() + "_Arc" + rowModel.getObject().getPk());
            link.setOutputMarkupId(true);
            return new ColunaLink(componentId, link);
        }
    }

    private class ComponenteCelulaCheckBox implements IColunaComponente<ARCDesignacaoVO> {
        @Override
        public Component obterComponente(Item<ICellPopulator<ARCDesignacaoVO>> cellItem, String componentId,
                final IModel<ARCDesignacaoVO> rowModel) {
            Check<ARCDesignacaoVO> checkbox = new Check<ARCDesignacaoVO>("check", rowModel, checkGroupARCsSelecionados);
            checkbox.setMarkupId(checkbox.getId() + "Arc" + rowModel.getObject().getPk());
            checkbox.setOutputMarkupId(true);
            return new ColunaCheck(componentId, checkbox);
        }
    }

    private class ComponenteHeaderCheckBox implements IColunaComponenteHeader<ARCDesignacaoVO> {
        @Override
        public Component obterComponenteHeader(String componentId) {
            CheckGroupSelector checkGroupSelector = new CheckGroupSelector("checkGroupSelector");
            checkGroupSelector.setMarkupId("marcarTodos");
            checkGroupARCsSelecionados.add(checkGroupSelector);
            return new CabecalhoCheckGroupSelector(componentId, checkGroupSelector);
        }
    }

    public Tabela<ARCDesignacaoVO> getTabelaARCs() {
        return tabelaARCs;
    }

    public Ciclo getCiclo() {
        return ciclo;
    }

    public boolean isExibirCheckBox() {
        return exibirCheckBox;
    }

    public IModel<List<ARCDesignacaoVO>> getModelConsulta() {
        return modelConsulta;
    }

    public List<ARCDesignacaoVO> getArcsSelecionados() {
        return arcsSelecionados;
    }

    public void setArcSelecionados(List<ARCDesignacaoVO> arcsSelecionados) {
        this.arcsSelecionados = arcsSelecionados;
    }

    public void setTabelaARCs(Tabela<ARCDesignacaoVO> tabelaARCs) {
        this.tabelaARCs = tabelaARCs;
    }

    public String getTitulo() {
        return titulo;
    }

    public ConsultaARCDesignacaoVO getConsulta() {
        return consulta;
    }

}
