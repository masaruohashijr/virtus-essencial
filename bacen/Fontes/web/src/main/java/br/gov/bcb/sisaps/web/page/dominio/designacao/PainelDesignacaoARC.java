package br.gov.bcb.sisaps.web.page.dominio.designacao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.gov.bcb.sisaps.adaptadores.pessoa.BcPessoaAdapter;
import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.mediator.EntidadeSupervisionavelMediator;
import br.gov.bcb.sisaps.src.vo.ARCDesignacaoVO;
import br.gov.bcb.sisaps.src.vo.ComponenteOrganizacionalVO;
import br.gov.bcb.sisaps.src.vo.ConsultaARCDesignacaoVO;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.drops.CustomDropDownChoice;

public class PainelDesignacaoARC extends PainelSisAps {

    private static final String OPCAO_SELECIONE = "Selecionar";

    @SpringBean
    private EntidadeSupervisionavelMediator entidadeSupervisionavelMediator;

    private final IModel<ServidorVO> modelServidorEquipe = new Model<ServidorVO>();

    private final IModel<ComponenteOrganizacionalVO> modelUnidade = new Model<ComponenteOrganizacionalVO>();
    private final IModel<ServidorVO> modelServidorUnidade = new Model<ServidorVO>();
    private DropDownChoice<ServidorVO> selectServidorUnidade;
    private DropDownChoice<ServidorVO> selectServidoresEquipe;
    private final ConsultaARCDesignacaoVO consulta = new ConsultaARCDesignacaoVO();

    private PainelFiltroDesignarArc painelFiltroDesigArc;

    private Ciclo ciclo;

    private boolean exibirCheckBox;

    private PainelListagemDesignacao painelListagemDesignacao;

    public PainelDesignacaoARC(String id, Ciclo ciclo) {
        super(id);
        addComponentes(ciclo);
    }

    public PainelDesignacaoARC(String id, Ciclo ciclo, boolean exibirCheckBox) {
        this(id, ciclo);
        this.exibirCheckBox = exibirCheckBox;
    }

    private void addComponentes(Ciclo ciclo) {
        this.ciclo = ciclo;
        addComponentes();
        addPaineis();
    }

    private void addPaineis() {
        painelListagem();
        addPainelFitro();
    }

    private void painelListagem() {
        painelListagemDesignacao =
                new PainelListagemDesignacao("idPainelListagemDesignacao", ciclo, consulta, true, "ARCs");
        addOrReplace(painelListagemDesignacao);
    }

    private void addComboServidoresUnidade() {
        ChoiceRenderer<ServidorVO> renderer =
                new ChoiceRenderer<ServidorVO>(Constantes.PROP_NOME, Constantes.PROP_MATRICULA);
        selectServidorUnidade =
                new CustomDropDownChoice<ServidorVO>("idServidorUnidadeSelecionada", "Selecione", modelServidorUnidade,
                        new ArrayList<ServidorVO>(), renderer);
        addOrReplace(selectServidorUnidade);
        atualizarOpcoesComboServidor();
    }

    private void addPainelFitro() {
        painelFiltroDesigArc =
                new PainelFiltroDesignarArc("idPainelFitroDesignacaoARCs", consulta, painelListagemDesignacao
                        .getModelConsulta().getObject(), ciclo, listaServidores(), this);
        addOrReplace(painelFiltroDesigArc);
    }

    private void addComponentes() {
        addComboServidoresEquipe();
        addComboUnidade();
        addComboServidoresUnidade();
    }

    private void addComboServidoresEquipe() {
        ChoiceRenderer<ServidorVO> renderer =
                new ChoiceRenderer<ServidorVO>(Constantes.PROP_NOME, Constantes.PROP_MATRICULA);
        List<ServidorVO> lista = listaServidores();
        selectServidoresEquipe =
                new CustomDropDownChoice<ServidorVO>("idServidorEquipe", OPCAO_SELECIONE, modelServidorEquipe, lista,
                        renderer);
        addOrReplace(selectServidoresEquipe);
    }

    @SuppressWarnings("unchecked")
    private List<ServidorVO> listaServidores() {
        List<ServidorVO> lista =
                BcPessoaAdapter.get().consultarServidoresAtivos(ciclo.getEntidadeSupervisionavel().getLocalizacao());
        List<BeanComparator> sortFields = new ArrayList<BeanComparator>();
        sortFields.add(new BeanComparator("nome"));
        ComparatorChain multiSort = new ComparatorChain(sortFields);
        Collections.sort(lista, multiSort);
        return lista;
    }

    private void addComboUnidade() {
        ChoiceRenderer<ComponenteOrganizacionalVO> renderer =
                new ChoiceRenderer<ComponenteOrganizacionalVO>(Constantes.PROP_ROTULO, Constantes.PROP_ROTULO);

        List<ComponenteOrganizacionalVO> lista = entidadeSupervisionavelMediator.montarUnidadesValidasEAtivas();

        ComponenteOrganizacionalVO unidadePadrao = new ComponenteOrganizacionalVO();
        unidadePadrao.setRotulo("DESUP");
        modelUnidade.setObject(unidadePadrao);

        DropDownChoice<ComponenteOrganizacionalVO> selectUnidade =
                new DropDownChoice<ComponenteOrganizacionalVO>("idUnidade", modelUnidade, lista, renderer);

        selectUnidade.add(new OnChangeAjaxBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                atualizarOpcoesComboServidor();
                target.add(selectServidorUnidade);
            }
        });

        addOrReplace(selectUnidade);
    }

    private void atualizarOpcoesComboServidor() {
        if (modelUnidade.getObject() != null) {
            List<ServidorVO> lista =
                    BcPessoaAdapter.get().consultarServidoresAtivos(modelUnidade.getObject().getRotulo());
            selectServidorUnidade.setChoices(lista);
        }
    }

    public ServidorVO getServidorEquipe() {
        return modelServidorEquipe.getObject();
    }

    public ServidorVO getServidorUnidade() {
        return modelServidorUnidade.getObject();
    }

    public void atualizarTela(AjaxRequestTarget target) {
        modelServidorEquipe.setObject(null);
        modelServidorUnidade.setObject(null);
        painelListagemDesignacao.setArcSelecionados(new ArrayList<ARCDesignacaoVO>());
        target.add(selectServidorUnidade);
        target.add(selectServidoresEquipe);
        target.add(painelListagemDesignacao.getTabelaARCs());
        painelFiltroDesigArc.atualizarComboResponsavel(target, painelListagemDesignacao.getModelConsulta().getObject());
    }

    public boolean isExibirCheckBox() {
        return exibirCheckBox;
    }

    public void setExibirCheckBox(boolean exibirCheckBox) {
        this.exibirCheckBox = exibirCheckBox;
    }

    public PainelListagemDesignacao getPainelListagemDesignacao() {
        return painelListagemDesignacao;
    }

}
