package br.gov.bcb.sisaps.web.page.dominio.analisequantitativaaqt;

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

import br.gov.bcb.sisaps.adaptadores.pessoa.BcPessoaAdapter;
import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.DelegacaoAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.DesignacaoAQTMediator;
import br.gov.bcb.sisaps.src.vo.ComponenteOrganizacionalVO;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.botoes.AjaxSubmitLinkSisAps;
import br.gov.bcb.sisaps.web.page.componentes.drops.CustomDropDownChoice;

public abstract class AbstracSelecionarServidoresPainelAnef extends PainelSisAps {

    private static final String PROP_MATRICULA = "matricula";
    private static final String PROP_NOME = "nome";
    private static final String PROP_ROTULO = "rotulo";
    private static final String OPCAO_SELECIONE = "Selecionar";
    protected IModel<ServidorVO> modelServidorEquipe = new Model<ServidorVO>();
    protected IModel<ServidorVO> modelServidorUnidade = new Model<ServidorVO>();
    protected AnaliseQuantitativaAQT analiseQuantitativaAQT;
    private final IModel<ComponenteOrganizacionalVO> modelUnidade = new Model<ComponenteOrganizacionalVO>();
    private DropDownChoice<ServidorVO> selectServidorUnidade;
    private final boolean isDelegacao;

    public AbstracSelecionarServidoresPainelAnef(String id, AnaliseQuantitativaAQT analiseQuantitativaAQT,
            boolean isDelegacao) {
        super(id);
        this.analiseQuantitativaAQT = analiseQuantitativaAQT;
        this.isDelegacao = isDelegacao;
    }

    protected void addBotao(String id) {
        AjaxSubmitLinkSisAps botton = new AjaxSubmitLinkSisAps(id) {

            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                submitButon(target);
            }
        };

        addOrReplace(botton);
    }

    protected abstract void submitButon(AjaxRequestTarget target);

    @SuppressWarnings("unchecked")
    protected void addComboServidoresEquipe(Ciclo ciclo, String id) {
        ChoiceRenderer<ServidorVO> renderer = new ChoiceRenderer<ServidorVO>(PROP_NOME, PROP_MATRICULA);

        List<ServidorVO> lista = new ArrayList<ServidorVO>();

        if (isDelegacao) {
            lista.addAll(DelegacaoAQTMediator.get().buscarListaServidorDelegacao(analiseQuantitativaAQT,
                    BcPessoaAdapter.get().buscarLocalizacao(), getPerfilPorPagina()));
        } else {
            lista.addAll(DesignacaoAQTMediator.get().buscarListaServidorDesignacao(analiseQuantitativaAQT,
                    BcPessoaAdapter.get().buscarLocalizacao()));
        }

        List<BeanComparator> sortFields = new ArrayList<BeanComparator>();
        sortFields.add(new BeanComparator(PROP_NOME));
        ComparatorChain multiSort = new ComparatorChain(sortFields);
        Collections.sort(lista, multiSort);
        DropDownChoice<ServidorVO> selectServidorEquipe =
                new CustomDropDownChoice<ServidorVO>(id, OPCAO_SELECIONE, modelServidorEquipe, lista, renderer);
        addOrReplace(selectServidorEquipe);
    }

    @SuppressWarnings("unchecked")
    protected void addComboUnidade(String id) {
        ChoiceRenderer<ComponenteOrganizacionalVO> renderer =
                new ChoiceRenderer<ComponenteOrganizacionalVO>(PROP_ROTULO, PROP_ROTULO);

        List<ComponenteOrganizacionalVO> lista = BcPessoaAdapter.get().consultarUnidadesAtivas();
        List<BeanComparator> sortFields = new ArrayList<BeanComparator>();
        sortFields.add(new BeanComparator(PROP_ROTULO));
        ComparatorChain multiSort = new ComparatorChain(sortFields);
        Collections.sort(lista, multiSort);
        ComponenteOrganizacionalVO unidadePadrao = new ComponenteOrganizacionalVO();
        unidadePadrao.setRotulo("DESUP");
        modelUnidade.setObject(unidadePadrao);

        DropDownChoice<ComponenteOrganizacionalVO> selectUnidade =
                new DropDownChoice<ComponenteOrganizacionalVO>(id, modelUnidade, lista, renderer);

        selectUnidade.add(new OnChangeAjaxBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                atualizarOpcoesComboServidor();
                target.add(selectServidorUnidade);
            }
        });

        addOrReplace(selectUnidade);
    }

    protected void addComboServidoresUnidade(String id) {
        ChoiceRenderer<ServidorVO> renderer = new ChoiceRenderer<ServidorVO>(PROP_NOME, PROP_MATRICULA);
        selectServidorUnidade =
                new CustomDropDownChoice<ServidorVO>(id, OPCAO_SELECIONE, modelServidorUnidade,
                        new ArrayList<ServidorVO>(), renderer);
        addOrReplace(selectServidorUnidade);
        atualizarOpcoesComboServidor();
    }

    private void atualizarOpcoesComboServidor() {
        if (modelUnidade.getObject() != null) {
            List<ServidorVO> lista = new ArrayList<ServidorVO>();
            if (isDelegacao) {
                lista.addAll(DelegacaoAQTMediator.get().buscarListaServidorDelegacao(analiseQuantitativaAQT,
                        modelUnidade.getObject().getRotulo(), getPerfilPorPagina()));
            } else {
                lista.addAll(DesignacaoAQTMediator.get().buscarListaServidorDesignacao(analiseQuantitativaAQT,
                        modelUnidade.getObject().getRotulo()));
            }
            selectServidorUnidade.setChoices(lista);
        }
    }

}