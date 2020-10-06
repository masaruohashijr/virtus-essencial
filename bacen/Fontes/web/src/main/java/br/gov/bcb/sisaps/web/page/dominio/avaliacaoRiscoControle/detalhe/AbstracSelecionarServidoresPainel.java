package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.detalhe;

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
import br.gov.bcb.sisaps.src.dominio.Atividade;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.mediator.DelegacaoMediator;
import br.gov.bcb.sisaps.src.mediator.DesignacaoMediator;
import br.gov.bcb.sisaps.src.vo.ComponenteOrganizacionalVO;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.botoes.AjaxSubmitLinkSisAps;
import br.gov.bcb.sisaps.web.page.componentes.drops.CustomDropDownChoice;

public abstract class AbstracSelecionarServidoresPainel extends PainelSisAps {

    private static final String PROP_MATRICULA = "matricula";
    private static final String PROP_NOME = "nome";
    private static final String PROP_ROTULO = "rotulo";
    private static final String OPCAO_SELECIONE = "Selecionar";
    @SpringBean
    protected DesignacaoMediator designacaoMediator;
    @SpringBean
    protected DelegacaoMediator delecagaoMediator;
    protected AvaliacaoRiscoControle avaliacao;
    protected IModel<ServidorVO> modelServidorEquipe = new Model<ServidorVO>();
    protected IModel<ServidorVO> modelServidorUnidade = new Model<ServidorVO>();
    protected Matriz matriz;
    protected Atividade atividade;
    private final IModel<ComponenteOrganizacionalVO> modelUnidade = new Model<ComponenteOrganizacionalVO>();
    private DropDownChoice<ServidorVO> selectServidorUnidade;

    public AbstracSelecionarServidoresPainel(String id, AvaliacaoRiscoControle avaliacao, Matriz matriz,
            Atividade atividade) {
        super(id);
        this.avaliacao = avaliacao;
        this.matriz = matriz;
        this.atividade = atividade;
    }

    protected void addBotao(String id) {
        AjaxSubmitLinkSisAps botton = new AjaxSubmitLinkSisAps(id) {

            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                submitButon(target);

            }
        };

        add(botton);
    }

    protected abstract void submitButon(AjaxRequestTarget target);

    @SuppressWarnings("unchecked")
    protected void addComboServidoresEquipe(Ciclo ciclo, String id) {
        ChoiceRenderer<ServidorVO> renderer = new ChoiceRenderer<ServidorVO>(PROP_NOME, PROP_MATRICULA);
        List<ServidorVO> lista =
                BcPessoaAdapter.get().consultarServidoresAtivos(ciclo.getEntidadeSupervisionavel().getLocalizacao());
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
            List<ServidorVO> lista =
                    BcPessoaAdapter.get().consultarServidoresAtivos(modelUnidade.getObject().getRotulo());
            selectServidorUnidade.setChoices(lista);
        }
    }

}