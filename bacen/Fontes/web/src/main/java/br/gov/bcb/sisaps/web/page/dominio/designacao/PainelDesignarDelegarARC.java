package br.gov.bcb.sisaps.web.page.dominio.designacao;

import java.util.ArrayList;
import java.util.List;

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
import br.gov.bcb.sisaps.src.vo.ComponenteOrganizacionalVO;
import br.gov.bcb.sisaps.web.page.PainelSisAps;

public class PainelDesignarDelegarARC extends PainelSisAps {

    private static final String PROP_MATRICULA = "matricula";
    private static final String PROP_NOME = "nome";
    private static final String PROP_ROTULO = "rotulo";

    private IModel<ServidorVO> modelServidorEquipe = new Model<ServidorVO>();
    private IModel<ComponenteOrganizacionalVO> modelUnidade = new Model<ComponenteOrganizacionalVO>();
    private IModel<ServidorVO> modelServidorUnidade = new Model<ServidorVO>();
    private DropDownChoice<ServidorVO> selectServidorUnidade;

    @SpringBean
    private EntidadeSupervisionavelMediator entidadeSupervisionavelMediator;

    public PainelDesignarDelegarARC(String id, Ciclo ciclo) {
        super(id);
        addCampos(ciclo);
    }

    private void addCampos(Ciclo ciclo) {
        addComboServidoresEquipe(ciclo);
        addComboUnidade();
        addComboServidoresUnidade();
    }

    private void addComboServidoresEquipe(Ciclo ciclo) {
        ChoiceRenderer<ServidorVO> renderer = new ChoiceRenderer<ServidorVO>(PROP_NOME, PROP_MATRICULA);
        List<ServidorVO> lista =
                entidadeSupervisionavelMediator.buscarEquipeES(ciclo.getEntidadeSupervisionavel().getLocalizacao());
        DropDownChoice<ServidorVO> selectServidorEquipe =
                new DropDownChoice<ServidorVO>("idServidorEquipe", modelServidorEquipe, lista, renderer);
        addOrReplace(selectServidorEquipe);
    }

    private void addComboUnidade() {
        ChoiceRenderer<ComponenteOrganizacionalVO> renderer =
                new ChoiceRenderer<ComponenteOrganizacionalVO>(PROP_ROTULO, PROP_ROTULO);

        List<ComponenteOrganizacionalVO> lista = BcPessoaAdapter.get().consultarUnidadesAtivas();

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

    private void addComboServidoresUnidade() {
        ChoiceRenderer<ServidorVO> renderer = new ChoiceRenderer<ServidorVO>(PROP_NOME, PROP_MATRICULA);
        selectServidorUnidade =
                new DropDownChoice<ServidorVO>("idServidorUnidadeSelecionada", modelServidorUnidade,
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
