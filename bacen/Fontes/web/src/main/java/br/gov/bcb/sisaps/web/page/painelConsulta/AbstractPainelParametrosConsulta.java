package br.gov.bcb.sisaps.web.page.painelConsulta;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.src.dominio.ParametroPrioridade;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.EntidadeSupervisionavelMediator;
import br.gov.bcb.sisaps.src.mediator.ParametroPrioridadeMediator;
import br.gov.bcb.sisaps.src.mediator.ServidorVOMediator;
import br.gov.bcb.sisaps.src.vo.ComponenteOrganizacionalVO;
import br.gov.bcb.sisaps.src.vo.ConsultaCicloVO;
import br.gov.bcb.sisaps.src.vo.ConsultaEntidadeSupervisionavelVO;
import br.gov.bcb.sisaps.src.vo.EntidadeSupervisionavelVO;
import br.gov.bcb.sisaps.util.Util;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.calendar.CalendarTextField;
import br.gov.bcb.sisaps.web.page.componentes.drops.CustomDropDownChoice;
import br.gov.bcb.sisaps.web.page.componentes.drops.DropDownChoiceSemValorDefault;
import br.gov.bcb.sisaps.web.page.dominio.agenda.AgendaPage;
import br.gov.bcb.sisaps.web.page.dominio.gestaoAgenda.GestaoAgendaPage;
import br.gov.bcb.sisaps.web.page.dominio.gestaoes.GestaoES;
import br.gov.bcb.sisaps.web.page.dominio.gestaoes.PainelResultadoConsultaEsAtivaGestaoES;

public abstract class AbstractPainelParametrosConsulta extends PainelSisAps {

    private static final String DESUP = "DESUP";
    private static final String ID_UNIDADE = "idUnidade";
    private static final String FEEDBACK = "feedback";
    private static final String DD_MM_YYYY = "dd/MM/yyyy";
    private static final String PROP_LOCALIZACAO = "localizacao";
    private static final String PROP_NOME = "nome";
    private static final String BTT_FILTRAR = "bttFiltrar";
    private static final String ID_PARTE_NOME_ES = "idParteNomeES";
    private static final String SIGLA = "sigla";
    private static final String TODOS = "Todos";
    private static final long serialVersionUID = -6234572464711348805L;
    private static final String PROP_ROTULO = "rotulo";
    private final ConsultaEntidadeSupervisionavelVO consultaParametro;
    private final ConsultaEntidadeSupervisionavelVO consultaResultado;
    private TextField<String> nomeEs;
    private ServidorVO servidor;
    private DropDownChoice<ParametroPrioridade> selectPrioridade;
    private DropDownChoice<ComponenteOrganizacionalVO> selectUnidade;
    private DropDownChoice<ComponenteOrganizacionalVO> selectSubUnidade;
    private DropDownChoice<ComponenteOrganizacionalVO> selectSubUnidade2;
    private DropDownChoice<ComponenteOrganizacionalVO> selectSubUnidade3;
    private DropDownChoice<ServidorVO> selectSupervisor;
    private ComponenteOrganizacionalVO modelUnidade = new ComponenteOrganizacionalVO();
    private ComponenteOrganizacionalVO modelSubUnidade = new ComponenteOrganizacionalVO();
    private ComponenteOrganizacionalVO modelSubUnidade2 = new ComponenteOrganizacionalVO();
    private ComponenteOrganizacionalVO modelSubUnidade3 = new ComponenteOrganizacionalVO();
    private List<ComponenteOrganizacionalVO> listaUnidade;
    private ConsultaCicloVO consultaCiclo;
    private IModel<String> modelInicio = new Model<String>();
    private IModel<String> modelFim = new Model<String>();
    private final WebMarkupContainer wmcExibirData = new WebMarkupContainer("wmcExibirData");

    public AbstractPainelParametrosConsulta(String id, ConsultaEntidadeSupervisionavelVO consultaParametro,
            ConsultaEntidadeSupervisionavelVO consultaResultado) {
        super(id);
        this.consultaParametro = consultaParametro;
        this.consultaResultado = consultaResultado;
    }

    public AbstractPainelParametrosConsulta(String id, ConsultaEntidadeSupervisionavelVO consultaParametro,
            ConsultaEntidadeSupervisionavelVO consultaResultado, ConsultaCicloVO consultaCiclo) {
        super(id);
        this.consultaParametro = consultaParametro;
        this.consultaResultado = consultaResultado;
        this.consultaCiclo = consultaCiclo;
    }

    protected void addSupervisor() {
        ChoiceRenderer<ServidorVO> renderer = new ChoiceRenderer<ServidorVO>(PROP_NOME, PROP_LOCALIZACAO);
        List<ServidorVO> listaChoices = ServidorVOMediator.get().buscarServidoresComEsAtivas();
        PropertyModel<ServidorVO> propertyModel = new PropertyModel<ServidorVO>(this, "servidor");
        selectSupervisor =
                new CustomDropDownChoice<ServidorVO>("idSupervisor", TODOS, propertyModel, listaChoices, renderer);
        selectSupervisor.add(new OnChangeAjaxBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                selectUnidade.setModelObject(null);
                selectSubUnidade.setModelObject(null);
                selectSubUnidade2.setModelObject(null);
                selectSubUnidade3.setModelObject(null);
                atualizarCombosEquipe(target);
            }
        });
        addOrReplace(selectSupervisor);
    }

    protected void atualizarCombosEquipe(AjaxRequestTarget target) {
        selectSubUnidade.setChoices(addListaSubUnidade(selectUnidade));
        selectSubUnidade2.setChoices(addListaSubUnidade(selectSubUnidade));
        selectSubUnidade3.setChoices(addListaSubUnidade(selectSubUnidade2));
        target.add(selectUnidade);
        target.add(selectSubUnidade);
        target.add(selectSubUnidade2);
        target.add(selectSubUnidade3);
    }

    protected void addTextNome() {
        nomeEs = new TextField<String>(ID_PARTE_NOME_ES, new PropertyModel<String>(consultaParametro, PROP_NOME));
        nomeEs.setMarkupId(ID_PARTE_NOME_ES);
        addOrReplace(nomeEs);
    }

    protected void addComboUnidade() {
        ChoiceRenderer<ComponenteOrganizacionalVO> renderer = criarChoiceRenderComboUnidade();

        listaUnidade = EntidadeSupervisionavelMediator.get().consultarUnidadesESsAtivas();
        PropertyModel<ComponenteOrganizacionalVO> propertyModel =
                new PropertyModel<ComponenteOrganizacionalVO>(this, "modelUnidade");

        if (listaUnidade.size() == 1) {
            selectUnidade =
                    new DropDownChoiceSemValorDefault<ComponenteOrganizacionalVO>(ID_UNIDADE, propertyModel,
                            listaUnidade, renderer);
        } else {
            selectUnidade =
                    new CustomDropDownChoice<ComponenteOrganizacionalVO>(ID_UNIDADE, TODOS, propertyModel,
                            listaUnidade, renderer);
        }

        selectUnidade.add(new OnChangeAjaxBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                selectSubUnidade.setModelObject(null);
                selectSubUnidade2.setModelObject(null);
                selectSubUnidade3.setModelObject(null);
                selectSubUnidade.setChoices(addListaSubUnidade(selectUnidade));
                selectSubUnidade2.setChoices(addListaSubUnidade(selectSubUnidade));
                selectSubUnidade3.setChoices(addListaSubUnidade(selectSubUnidade2));
                target.add(selectSubUnidade);
                target.add(selectSubUnidade2);
                target.add(selectSubUnidade3);
                limparComboSupervisor(target);
            }
        });

        addOrReplace(selectUnidade);
    }

    protected void limparComboSupervisor(AjaxRequestTarget target) {
        selectSupervisor.setModelObject(null);
        target.add(selectSupervisor);
    }

    protected void addComboSubUnidade() {

        PropertyModel<ComponenteOrganizacionalVO> propertyModel =
                new PropertyModel<ComponenteOrganizacionalVO>(this, "modelSubUnidade");
        selectSubUnidade =
                new CustomDropDownChoice<ComponenteOrganizacionalVO>("idSubUnidade", TODOS, propertyModel,
                        addListaSubUnidade(selectUnidade), criarChoiceRenderComboUnidade());
        selectSubUnidade.add(new OnChangeAjaxBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                selectSubUnidade2.setModelObject(null);
                selectSubUnidade3.setModelObject(null);
                selectSubUnidade2.setChoices(addListaSubUnidade(selectSubUnidade));
                selectSubUnidade3.setChoices(addListaSubUnidade(selectSubUnidade2));
                target.add(selectSubUnidade2);
                target.add(selectSubUnidade3);
            }
        });

        addOrReplace(selectSubUnidade);
    }

    private List<ComponenteOrganizacionalVO> addListaSubUnidade(DropDownChoice<ComponenteOrganizacionalVO> dropdown) {
        if (dropdown.getChoices().size() == 1) {
            return dropdown.getChoices().get(0).getFilhos();
        } else if (dropdown.getModelObject() == null || dropdown.getModelObject().getFilhos() == null) {
            return new ArrayList<ComponenteOrganizacionalVO>();
        } else {
            return dropdown.getModelObject().getFilhos();
        }
    }

    protected void addComboSubUnidade2() {

        PropertyModel<ComponenteOrganizacionalVO> propertyModel =
                new PropertyModel<ComponenteOrganizacionalVO>(this, "modelSubUnidade2");
        selectSubUnidade2 =
                new CustomDropDownChoice<ComponenteOrganizacionalVO>("idSubUnidade2", TODOS, propertyModel,
                        addListaSubUnidade(selectSubUnidade), criarChoiceRenderComboUnidade());
        selectSubUnidade2.add(new OnChangeAjaxBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                selectSubUnidade3.setModelObject(null);
                selectSubUnidade3.setChoices(addListaSubUnidade(selectSubUnidade2));
                target.add(selectSubUnidade3);
            }
        });

        addOrReplace(selectSubUnidade2);
    }

    private ChoiceRenderer<ComponenteOrganizacionalVO> criarChoiceRenderComboUnidade() {
        return new ChoiceRenderer<ComponenteOrganizacionalVO>(SIGLA, PROP_ROTULO);
    }

    protected void addComboSubUnidade3() {

        PropertyModel<ComponenteOrganizacionalVO> propertyModel =
                new PropertyModel<ComponenteOrganizacionalVO>(this, "modelSubUnidade3");
        selectSubUnidade3 =
                new CustomDropDownChoice<ComponenteOrganizacionalVO>("idSubUnidade3", TODOS, propertyModel,
                        addListaSubUnidade(selectSubUnidade2), criarChoiceRenderComboUnidade());

        addOrReplace(selectSubUnidade3);
    }

    protected void addPrioridade() {
        ChoiceRenderer<ParametroPrioridade> renderer = new ChoiceRenderer<ParametroPrioridade>("descricao", "codigo");
        List<ParametroPrioridade> listaChoices = ParametroPrioridadeMediator.get().buscarTodasPrioridades();
        PropertyModel<ParametroPrioridade> propertyModel =
                new PropertyModel<ParametroPrioridade>(consultaParametro, "prioridade");
        selectPrioridade =
                new CustomDropDownChoice<ParametroPrioridade>("idPrioridade", TODOS, propertyModel, listaChoices,
                        renderer);
        addOrReplace(selectPrioridade);
    }

    private void atualizarPagina(AjaxRequestTarget target) {
        if (getPaginaAtual() instanceof PerfilConsultaPage) {
            ((PerfilConsultaPage) getPaginaAtual()).atualizar(target);
        } else if (getPaginaAtual() instanceof PerfilConsultaResumidoPage) {
            ((PerfilConsultaResumidoPage) getPaginaAtual()).atualizar(target);
        } else if (getPaginaAtual() instanceof GestaoES) {
            PainelResultadoConsultaEsAtivaGestaoES panielConsultaEsAtiva =
                    ((GestaoES) getPaginaAtual()).getPanielConsultaEsAtiva();
            panielConsultaEsAtiva.setConsultaEntidadeSupervisionavelVO(consultaResultado);
            target.add(panielConsultaEsAtiva.getTabela());
        } else if (paginaPainelAgenda()) {
            copiarConsulta();
            ((AgendaPage) getPaginaAtual()).atualizar(target, consultaCiclo);
        } else if (paginaGestaoAgenda()) {
            copiarConsulta();
            ((GestaoAgendaPage) getPaginaAtual()).atualizar(target, consultaCiclo);
        }
    }

    protected void addBotoes() {
        AjaxButton botaoFiltrar = new AjaxButton(BTT_FILTRAR) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                try {
                    if (isFiltroAgenda()) {
                        ArrayList<ErrorMessage> erros = new ArrayList<ErrorMessage>();
                        CicloMediator.get().validarDataInicio(modelInicio.getObject(), modelFim.getObject(), erros);
                    }
                    addLocalizacaoConsulta();

                    if (servidor == null) {
                        consultaParametro.setBuscarHierarquiaInferior(true);
                        if (consultaCiclo != null) {
                            consultaCiclo.setBuscarHierarquiaInferior(true);
                        }

                    } else {
                        consultaParametro.setBuscarHierarquiaInferior(false);
                        consultaParametro.setLocalizacao(servidor.getLocalizacao());
                        consultaParametro.setNomeServidor(servidor.getNome());

                    }

                    Util.copiarPropriedades(consultaResultado, consultaParametro);

                    atualizarPagina(target);
                } catch (NegocioException e) {
                    for (ErrorMessage mensagem : e.getMensagens()) {
                        error(mensagem.toString().replace("[", "").replace("]", ""));
                        target.add(getPage().get(FEEDBACK));
                    }
                }
            }

        };
        botaoFiltrar.setOutputMarkupId(true);
        botaoFiltrar.setMarkupId(BTT_FILTRAR);
        addOrReplace(botaoFiltrar);

        AjaxButton botaoLimparFiltro = new AjaxButton("bttLimparFiltro") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                nomeEs.setDefaultModelObject("");
                if (selectPrioridade != null) {
                    selectPrioridade.setDefaultModelObject(null);
                }

                selectSubUnidade.setDefaultModelObject(null);
                selectSubUnidade2.setDefaultModelObject(null);
                selectSubUnidade3.setDefaultModelObject(null);
                selectSubUnidade3.setDefaultModelObject(null);
                selectSupervisor.setDefaultModelObject(null);

                atualizarPaginaLimpar(target);

            }
        };

        addOrReplace(botaoLimparFiltro);

    }

    private void atualizarPaginaLimpar(AjaxRequestTarget target) {
        limparConsulta();
        if (getPaginaAtual() instanceof GestaoES) {
            ((GestaoES) getPaginaAtual()).atualizarLista(target);
        } else if (paginaPainelAgenda()) {
            modelInicio.setObject(null);
            modelFim.setObject(null);
            copiarConsulta();
            ((AgendaPage) getPaginaAtual()).limpar(target, consultaCiclo);
            getPage().getFeedbackMessages().clear();
            target.add(getPage().get(FEEDBACK));
        } else if (getPaginaAtual() instanceof GestaoAgendaPage) {
            Util.copiarPropriedades(consultaResultado, consultaParametro);
            modelInicio.setObject(null);
            modelFim.setObject(null);
            copiarConsulta();
            ((GestaoAgendaPage) getPaginaAtual()).limpar(target, consultaCiclo);
            getPage().getFeedbackMessages().clear();
            target.add(getPage().get(FEEDBACK));
        } else if (getPaginaAtual() instanceof PerfilConsultaPage) {
            ((PerfilConsultaPage) getPaginaAtual()).atualizar(target);
        } else if (getPaginaAtual() instanceof PerfilConsultaResumidoPage) {
            ((PerfilConsultaResumidoPage) getPaginaAtual()).atualizar(target);
        }
        target.add(AbstractPainelParametrosConsulta.this);
    }

    private boolean isFiltroAgenda() {
        return paginaGestaoAgenda() || paginaPainelAgenda();
    }

    private boolean paginaGestaoAgenda() {
        return getPaginaAtual() instanceof GestaoAgendaPage;
    }

    private boolean paginaPainelAgenda() {
        return getPaginaAtual() instanceof AgendaPage;
    }

    public void limparConsulta() {
        consultaParametro.setNome(null);
        consultaParametro.setLocalizacao(null);
        consultaParametro.setEquipe(null);
        consultaParametro.setPrioridade(null);
        consultaParametro.setCiclo(null);
        consultaParametro.setDataCorec(null);
        selectUnidade.setDefaultModelObject(null);
        Util.copiarPropriedades(consultaResultado, consultaParametro);

        if (listaUnidade.size() == 1) {
            if (consultaCiclo != null) {
                consultaCiclo.setEntidadeSupervisionavel(new EntidadeSupervisionavelVO());
                consultaCiclo.getEntidadeSupervisionavel().setLocalizacao(listaUnidade.get(0).getSigla());
                consultaCiclo.setBuscarHierarquiaInferior(true);
            }

            consultaResultado.setLocalizacao(listaUnidade.get(0).getSigla());
            consultaResultado.setBuscarHierarquiaInferior(true);
        } else {
            if (consultaCiclo != null) {
                consultaCiclo.setEntidadeSupervisionavel(null);
                consultaCiclo.setBuscarHierarquiaInferior(false);
            }
            consultaResultado.setLocalizacao(null);
            consultaResultado.setBuscarHierarquiaInferior(false);
        }

    }

    protected void addLocalizacaoConsulta() {
        String localizacao = null;

        boolean selSubUnid1 = selectSubUnidade.getModelObject() != null;
        boolean selSubUnid2 = selectSubUnidade2.getModelObject() != null;
        boolean selSubUnid3 = selectSubUnidade3.getModelObject() != null;
        boolean selUnid4 = selectUnidade.getModelObject() != null;

        if (selSubUnid3) {
            localizacao = atualizarSelect(selectSubUnidade3);
        } else if (selSubUnid2) {
            localizacao = atualizarSelect(selectSubUnidade2);
        } else if (selSubUnid1) {
            localizacao = atualizarSelect(selectSubUnidade);
        } else if (selUnid4) {
            localizacao = atualizarSelect(selectUnidade);
        }

        consultaParametro.setLocalizacao(localizacao);
    }

    private String atualizarSelect(DropDownChoice<ComponenteOrganizacionalVO> select) {
        return select.getModelObject().getRotulo();
    }

    public ComponenteOrganizacionalVO getModelUnidade() {
        return modelUnidade;
    }

    public void setModelUnidade(ComponenteOrganizacionalVO modelUnidade) {
        this.modelUnidade = modelUnidade;
    }

    public ComponenteOrganizacionalVO getModelSubUnidade() {
        return modelSubUnidade;
    }

    public void setModelSubUnidade(ComponenteOrganizacionalVO modelSubUnidade) {
        this.modelSubUnidade = modelSubUnidade;
    }

    public ComponenteOrganizacionalVO getModelSubUnidade2() {
        return modelSubUnidade2;
    }

    public void setModelSubUnidade2(ComponenteOrganizacionalVO modelSubUnidade2) {
        this.modelSubUnidade2 = modelSubUnidade2;
    }

    public ComponenteOrganizacionalVO getModelSubUnidade3() {
        return modelSubUnidade3;
    }

    public void setModelSubUnidade3(ComponenteOrganizacionalVO modelSubUnidade3) {
        this.modelSubUnidade3 = modelSubUnidade3;
    }

    public ServidorVO getServidor() {
        return servidor;
    }

    public void setServidor(ServidorVO servidor) {
        this.servidor = servidor;
    }

    private void copiarConsulta() {
        EntidadeSupervisionavelVO entVo = new EntidadeSupervisionavelVO();
        if (consultaResultado.getPrioridade() != null) {
            entVo.setPrioridade(consultaResultado.getPrioridade());
        }
        if (consultaResultado.getLocalizacao() != null) {
            entVo.setLocalizacao(consultaResultado.getLocalizacao());
        }
        if (consultaResultado.getNome() != null) {
            entVo.setNome(consultaResultado.getNome());
        }
        if (modelInicio.getObject() != null) {
            LocalDate date = DateTimeFormat.forPattern(DD_MM_YYYY).parseLocalDate(modelInicio.getObject().toString());
            consultaCiclo.setDataCorecInicio(date);
        }
        if (modelFim.getObject() != null) {
            LocalDate date = DateTimeFormat.forPattern(DD_MM_YYYY).parseLocalDate(modelFim.getObject().toString());
            consultaCiclo.setDataCorecFim(date);
        }

        if (servidor != null) {
            consultaCiclo.setMatriculaSupervisor(servidor.getMatricula());
        }

        consultaCiclo.setEntidadeSupervisionavel(entVo);

    }

    protected void addDatas() {

        CalendarTextField<String> txtDataInicio =
                new CalendarTextField<String>("idDataCorecPrevistoInicio", modelInicio);
        txtDataInicio.setParentId("pInicioCiclo");
        txtDataInicio.setOutputMarkupId(true);
        txtDataInicio.setMarkupId(txtDataInicio.getId());
        wmcExibirData.addOrReplace(txtDataInicio);

        CalendarTextField<String> txtDataFim = new CalendarTextField<String>("idDataCorecPrevistoFim", modelFim);
        txtDataInicio.setParentId("pFimCiclo");
        txtDataInicio.setOutputMarkupId(true);
        txtDataInicio.setMarkupId(txtDataInicio.getId());
        wmcExibirData.addOrReplace(txtDataFim);
        wmcExibirData.setVisibilityAllowed(isFiltroAgenda());
        addOrReplace(wmcExibirData);
    }

    public ConsultaCicloVO getConsultaCiclo() {
        return consultaCiclo;
    }

    public void setConsultaCiclo(ConsultaCicloVO consultaCiclo) {
        this.consultaCiclo = consultaCiclo;
    }

    public IModel<String> getModelInicio() {
        return modelInicio;
    }

    public void setModelInicio(IModel<String> modelInicio) {
        this.modelInicio = modelInicio;
    }

    public IModel<String> getModelFim() {
        return modelFim;
    }

    public void setModelFim(IModel<String> modelFim) {
        this.modelFim = modelFim;
    }

}
