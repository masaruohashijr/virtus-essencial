package br.gov.bcb.sisaps.web.page.dominio.gestaoes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.DateTime;

import br.gov.bcb.sisaps.src.dominio.EntidadeSupervisionavel;
import br.gov.bcb.sisaps.src.dominio.ParametroPrioridade;
import br.gov.bcb.sisaps.src.mediator.EntidadeSupervisionavelMediator;
import br.gov.bcb.sisaps.src.mediator.ParametroPrioridadeMediator;
import br.gov.bcb.sisaps.src.vo.EntidadeSupervisionavelVO;
import br.gov.bcb.sisaps.web.page.componentes.botoes.AjaxSubmitLinkSisAps;
import br.gov.bcb.sisaps.web.page.componentes.drops.DropDownChoiceSemValorDefault;
import br.gov.bcb.sisaps.web.page.componentes.modal.AjaxSubmitLinkModalWindow;
import br.gov.bcb.sisaps.web.page.componentes.modal.PainelModalWindow;

public class PainelGestaoES extends PainelModalWindow {

    private final Form<?> form = new Form<Object>("form");
    private final ModalWindow modalAlteracao;
    private final EntidadeSupervisionavel entidade;
    private final EntidadeSupervisionavelVO entidadeVO;
    private final Short qtdsAnosPrevisaoCorecInicial;

    @SpringBean
    private EntidadeSupervisionavelMediator entidadeSupervisionavelMediator;
    private LinkIncluir linkSalvar;
    private Label labelCorecPrevisto;
    private Date dataPrevisaoCorecInicial;

    public PainelGestaoES(ModalWindow modalAlteracao, EntidadeSupervisionavelVO entidadeVO) {
        super(modalAlteracao.getContentId());
        this.modalAlteracao = modalAlteracao;
        this.entidadeVO = entidadeVO;
        this.entidade = entidadeSupervisionavelMediator.buscarEntidadeSupervisionavelPorPK(entidadeVO.getPk());
        this.qtdsAnosPrevisaoCorecInicial = entidade.getQuantidadeAnosPrevisaoCorec();
        this.dataPrevisaoCorecInicial = entidadeVO.getCorecPrevisto();
        setOutputMarkupId(true);
    }

    @Override
    protected void onConfigure() {
        addTitulo();
        addEquipe();
        addSupervisorTitular();
        addPrioridade();
        addCorecPrevisto();
        addCiclo();
        addBotaoSalvar();
        form.addOrReplace(new LinkFechar());
        add(form);
    }
    
    private void addTitulo() {
        Label labelTitulo = new Label("idTitulo", entidade.getNomeConglomeradoFormatado());
        form.addOrReplace(labelTitulo);
    }
    
    private void addEquipe() {
        Label labelEquipe = new Label("idEquipe", entidade.getLocalizacao());
        form.addOrReplace(labelEquipe);
    }
    
    private void addSupervisorTitular() {
        Label labelSupervisorTitular = new Label("idSupervisorTitular", entidade.getNomeSupervisor());
        form.addOrReplace(labelSupervisorTitular);
    }
    
    private void addPrioridade() {
        ChoiceRenderer<ParametroPrioridade> renderer = new ChoiceRenderer<ParametroPrioridade>("descricao", "codigo");
        List<ParametroPrioridade> listaChoices = ParametroPrioridadeMediator.get().buscarTodasPrioridades();
        PropertyModel<ParametroPrioridade> propertyModel =
                new PropertyModel<ParametroPrioridade>(entidade, "prioridade");
        DropDownChoiceSemValorDefault<ParametroPrioridade> selectPrioridade =
                new DropDownChoiceSemValorDefault<ParametroPrioridade>("idPrioridade", propertyModel, listaChoices,
                        renderer);
        selectPrioridade.setDefaultModelObject(entidade.getPrioridade());
        selectPrioridade.add(new OnChangeAjaxBehavior() {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(linkSalvar);
            }

        });
        form.addOrReplace(selectPrioridade);
    }
    
    private void addCorecPrevisto() {
        PropertyModel<String> previsaoCorec = new PropertyModel<String>(entidadeVO, "dataPrevisaoFormatada");
        labelCorecPrevisto = new Label("idCorecPrevisto", previsaoCorec);
        form.addOrReplace(labelCorecPrevisto);
    }
    
    private void addCiclo() {
        List<Short> listaChoices = criarListaAnos();
        PropertyModel<Short> propertyModel = new PropertyModel<Short>(entidade, "quantidadeAnosPrevisaoCorec");
        final DropDownChoiceSemValorDefault<Short> selectCiclo =
                new DropDownChoiceSemValorDefault<Short>("idCiclo", propertyModel, listaChoices);
        selectCiclo.setDefaultModelObject(entidade.getQuantidadeAnosPrevisaoCorec());
        selectCiclo.add(new OnChangeAjaxBehavior() {
            
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                setNovaDataPrevisaoCorec();       
                target.add(labelCorecPrevisto);
                target.add(linkSalvar);
            }
            
        });
        form.addOrReplace(selectCiclo);
    }
    
    private void setNovaDataPrevisaoCorec() {
        if (entidade.getQuantidadeAnosPrevisaoCorec().equals(qtdsAnosPrevisaoCorecInicial)) {
            entidadeVO.setCorecPrevisto(dataPrevisaoCorecInicial);
        } else {
            DateTime dataPrevisaoCorec = new DateTime(entidadeVO.getInicioCiclo().getTime());
            dataPrevisaoCorec = dataPrevisaoCorec.plusYears(entidade.getQuantidadeAnosPrevisaoCorec());
            entidadeVO.setCorecPrevisto(dataPrevisaoCorec.toDate());
        }
    }
    
    private List<Short> criarListaAnos() {
        List<Short> listaChoices = new ArrayList<Short>();
        listaChoices.add(Short.valueOf("1"));
        listaChoices.add(Short.valueOf("2"));
        listaChoices.add(Short.valueOf("3"));
        listaChoices.add(Short.valueOf("4"));
        return listaChoices;
    }
    
    private void addBotaoSalvar() {
        linkSalvar = new LinkIncluir("bttSalvar");
        linkSalvar.setOutputMarkupId(true);
        linkSalvar.setMarkupId(linkSalvar.getId());
        linkSalvar.setBody(new Model<String>("Teste"));
        form.addOrReplace(linkSalvar);
    }
    
    protected class LinkIncluir extends AjaxSubmitLinkModalWindow {

        private static final String SALVAR_E_ATUALIZAR_PERFIL_DE_RISCO = "Salvar e atualizar perfil de risco";
        private static final String SALVAR = "Salvar";
        private boolean isValorSalvar;

        public LinkIncluir(String id) {
            super(id);
        }

        @Override
        public void executeSubmit(AjaxRequestTarget target) {

            String msg = entidadeSupervisionavelMediator.salvarAlteracaoGestaoES(entidade);
            modalAlteracao.close(target);
            GestaoES page = (GestaoES) getPage();
            page.success(msg);
            target.add(page.get("feedback"));
            page.atualizar(target);
        }

        @Override
        protected void onComponentTag(ComponentTag tag) {
            super.onComponentTag(tag);
            tag.put("value", (isValorSalvar ? SALVAR : SALVAR_E_ATUALIZAR_PERFIL_DE_RISCO));
            tag.put("title", (isValorSalvar ? SALVAR : SALVAR_E_ATUALIZAR_PERFIL_DE_RISCO));
        }

        @Override
        protected void onConfigure() {
            super.onConfigure();
            isValorSalvar = entidadeSupervisionavelMediator.exibirNomeBotaoSalvar(entidade);
            add(new AttributeModifier("style", new Model<String>(definirStyle(isValorSalvar))));
        }

    }
    
    private String definirStyle(boolean isValorSalvar) {
        if (isValorSalvar) {
            return "width:100px";
        } else {
            return "width:200px";
        }
    }

    protected class LinkFechar extends AjaxSubmitLinkSisAps {
        public LinkFechar() {
            super("bttVoltar", true);
        }

        @Override
        public void executeSubmit(AjaxRequestTarget target) {
            modalAlteracao.close(target);
        }
    }

}
