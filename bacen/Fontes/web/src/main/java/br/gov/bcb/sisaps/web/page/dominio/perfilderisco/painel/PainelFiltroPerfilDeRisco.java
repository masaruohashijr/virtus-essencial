package br.gov.bcb.sisaps.web.page.dominio.perfilderisco.painel;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.EntidadeSupervisionavel;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoCicloEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.LogOperacaoMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.src.mediator.RegraPerfilAcessoMediator;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.behavior.AjaxLinkIndicator;
import br.gov.bcb.sisaps.web.page.dominio.perfilderisco.PerfilDeRiscoPage;
import br.gov.bcb.sisaps.web.page.dominio.perfilderisco.PerfilDeRiscoPageResumido;

public class PainelFiltroPerfilDeRisco extends PainelSisAps {

    private static final String VIGENTE = "vigente";
	private static final String RES_INDICATOR_CSS = "res/indicator.css";
    private static final String RES_INDICATOR_JS = "res/indicator.js";
    private static final String MASK_HIDE = "Mask.hide('";
    private static final String FECHA_PARENTESES = "');";
    @SpringBean
    private CicloMediator cicloMediator;
    @SpringBean
    private PerfilRiscoMediator perfilRiscoMediator;
    private final IModel<Ciclo> modelCiclo = new Model<Ciclo>();
    private final IModel<PerfilRisco> modelPerfilRisco = new Model<PerfilRisco>();
    private DropDownChoice<PerfilRisco> selectPerfilRisco;
    private IModel<List<Ciclo>> listaCiclos;
    private IModel<List<PerfilRisco>> listaPerfisRisco;
    private Label labelHistorica;
    private final EntidadeSupervisionavel entidadeSupervisionavel;
    private final boolean isOrigemPainelConsulta;
    private DropDownChoice<Ciclo> selectCiclo;
    private boolean isVigente;

    public PainelFiltroPerfilDeRisco(String id, EntidadeSupervisionavel entidadeSupervisionavel,
            boolean isOrigemPainelConsulta) {
        super(id);
        this.entidadeSupervisionavel = entidadeSupervisionavel;
        this.isOrigemPainelConsulta = isOrigemPainelConsulta;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        criarListaCiclosModel();
        criarListaPerfisRiscoModel();
        addComboCiclos();
        addComboPerfisRisco();
        labelHistorica = new Label("idVersaoHistorica", "histórica") {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisible(SisapsUtil.isCicloMigrado(modelPerfilRisco.getObject().getCiclo()));
            }
        };
        labelHistorica.setOutputMarkupPlaceholderTag(true);
        addOrReplace(labelHistorica);
        setVisibilityAllowed(!(listaCiclos.getObject().size() == 1 && listaPerfisRisco.getObject().size() == 1));
        isVigente = VIGENTE.equals(getVersao());
    }

    private void criarListaPerfisRiscoModel() {
        listaPerfisRisco = new LoadableDetachableModel<List<PerfilRisco>>() {
            @Override
            protected List<PerfilRisco> load() {
                return perfilRiscoMediator.consultarPerfisRiscoCiclo(modelCiclo.getObject().getPk(), true);
            }

        };
    }

    private void criarListaCiclosModel() {
        listaCiclos = new LoadableDetachableModel<List<Ciclo>>() {
            @Override
            protected List<Ciclo> load() {
                boolean excluirCicloMigrados = false;
                if (isOrigemPainelConsulta) {
                    boolean temPerfilConsultaResumoNaoBloqueados =
                            RegraPerfilAcessoMediator.get().isAcessoPermitido(
                                    PerfilAcessoEnum.CONSULTA_RESUMO_NAO_BLOQUEADOS);
                    if (temPerfilConsultaResumoNaoBloqueados
                            && !RegraPerfilAcessoMediator.get().isAcessoPermitido(
                                    Arrays.asList(PerfilAcessoEnum.CONSULTA_TUDO,
                                            PerfilAcessoEnum.CONSULTA_NAO_BLOQUEADOS))) {
                        excluirCicloMigrados = true;
                    }
                }
                return cicloMediator.consultarCiclosEntidadeSupervisionavel(
                        entidadeSupervisionavel.getConglomeradoOuCnpj(), excluirCicloMigrados);
            }
        };
    }

    private void addComboCiclos() {
        selectCiclo = new DropDownChoice<Ciclo>("idCiclo", modelCiclo, listaCiclos, new CicloChoiceRenderer()) {

            @Override
            public void renderHead(IHeaderResponse response) {
                super.renderHead(response);
                addIndicator(response);
            }

        };
        selectCiclo.add(new OnChangeAjaxBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                setValorDefaultPerfilRisco();
                gerarLogDetalhamentoPerfilRisco();
                target.add(selectPerfilRisco);
                target.add(labelHistorica);
                atualizarPagina(target);
            }

            @Override
            protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
                super.updateAjaxAttributes(attributes);
                addListener(attributes);
            }
        });
        modelCiclo.setObject(listaCiclos.getObject().get(0));
        selectCiclo.setNullValid(false);
        addOrReplace(selectCiclo);
    }

    public DropDownChoice<Ciclo> getSelectCiclo() {
        return selectCiclo;
    }

    public DropDownChoice<PerfilRisco> getSelectVersao() {
        return selectPerfilRisco;
    }
    
    private void addIndicator(IHeaderResponse response) {
        response.render(CssHeaderItem.forReference(new PackageResourceReference(AjaxLinkIndicator.class,
                RES_INDICATOR_CSS)));
        response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(AjaxLinkIndicator.class,
                RES_INDICATOR_JS)));
    }

    private void addComboPerfisRisco() {
        selectPerfilRisco =
                new DropDownChoice<PerfilRisco>("idPerfilRisco", modelPerfilRisco, listaPerfisRisco,
                        new PerfilRiscoChoiceRenderer()) {
                    @Override
                    protected void onConfigure() {
                        super.onConfigure();
                        setVisible(!SisapsUtil.isCicloMigrado(modelPerfilRisco.getObject().getCiclo()));
                    }

                    @Override
                    public void renderHead(IHeaderResponse response) {
                        super.renderHead(response);
                        addIndicator(response);
                    }

                };
        selectPerfilRisco.add(new OnChangeAjaxBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                gerarLogDetalhamentoPerfilRisco();
                atualizarPagina(target);
                isVigente = VIGENTE.equals(getVersao());
            }

            @Override
            protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
                super.updateAjaxAttributes(attributes);
                addListener(attributes);
            }

        });
        setValorInicial();
        selectPerfilRisco.setNullValid(false);
        selectPerfilRisco.setOutputMarkupPlaceholderTag(true);
        addOrReplace(selectPerfilRisco);
    }

	private void setValorInicial() {
		List<PerfilRisco> perfisCiclo = perfilRiscoMediator.consultarPerfisRiscoCiclo(modelCiclo.getObject().getPk(), true);
		PerfilRisco ultimoPerfil = perfisCiclo.get(0);
        PerfilRisco perfilSelecionado = getPerfilRiscoSelecionado();
        boolean atualizouPerfil = false;
        if (perfilSelecionado != null && ultimoPerfil != null) {
    		if (!ultimoPerfil.getPk().equals(perfilSelecionado.getPk()) && isVigente) {
    			atualizouPerfil = true;
    		}
        }
        if (atualizouPerfil || perfilSelecionado == null) {
        	setValorDefaultPerfilRisco();
        } else {
        	setValorPerfilRisco(perfilSelecionado);
        }
	}
	
	private String getVersao() {
        DropDownChoice<PerfilRisco> select = getSelectVersao();
        IModel<PerfilRisco> model = select.getModel();
        PerfilRisco perfilRisco = model.getObject();
        IChoiceRenderer<? super PerfilRisco> renderer = select.getChoiceRenderer();
        return perfilRisco == null ? "" : renderer.getDisplayValue(perfilRisco).toString();
    }

    private void addListener(AjaxRequestAttributes attributes) {
        attributes.getAjaxCallListeners().add(new AjaxCallListener() {
            @Override
            public CharSequence getPrecondition(Component component) {
                return "Mask.show('" + selectPerfilRisco.getMarkupId() + FECHA_PARENTESES;
            }

            @Override
            public CharSequence getSuccessHandler(Component component) {
                return MASK_HIDE + selectPerfilRisco.getMarkupId() + FECHA_PARENTESES;
            }

            @Override
            public CharSequence getFailureHandler(Component component) {
                return MASK_HIDE + selectPerfilRisco.getMarkupId() + FECHA_PARENTESES;
            }
        });
    }

    private void setValorDefaultPerfilRisco() {
        modelPerfilRisco.setObject(listaPerfisRisco.getObject().get(0));
        if (getPaginaAtual() instanceof PerfilDeRiscoPage) {
        	((PerfilDeRiscoPage) getPage()).setarPerfilRiscoPaineis(listaPerfisRisco.getObject().get(0));
        }
    }

    public void setValorPerfilRisco(PerfilRisco perfil) {
        modelPerfilRisco.setObject(perfil);
    }

    public void setValorCiclo(Ciclo ciclo) {
        modelCiclo.setObject(ciclo);
        criarListaPerfisRiscoModel();
        addComboPerfisRisco();
    }

    private void atualizarPagina(AjaxRequestTarget target) {
        if (getPaginaAtual() instanceof PerfilDeRiscoPage) {
            ((PerfilDeRiscoPage) getPaginaAtual()).atualizarPagina(target);
        } else {
            ((PerfilDeRiscoPageResumido) getPaginaAtual()).atualizarPagina(target);
        }

    }

    private class PerfilRiscoChoiceRenderer extends ChoiceRenderer<PerfilRisco> {
        @Override
        public Object getDisplayValue(PerfilRisco perfil) {
            if (perfil.getPk().equals(listaPerfisRisco.getObject().get(0).getPk())) {
                if (perfilRiscoMediator.getEstadoCicloPerfilRisco(perfil).getEstado()
                        .equals(EstadoCicloEnum.EM_ANDAMENTO)) {
                    return VIGENTE;
                } else {
                    return "corec";
                }
            } else {
                return perfil.getDataCriacaoFormatadaSemHora();
            }
        }

        @Override
        public String getIdValue(PerfilRisco perfil, int index) {
            return perfil.getPk().toString();
        }
    }

    private class CicloChoiceRenderer extends ChoiceRenderer<Ciclo> {
        @Override
        public Object getDisplayValue(Ciclo ciclo) {
            if (ciclo.getPk().equals(listaCiclos.getObject().get(0).getPk())) {
                return "atual";
            } else {
                return ciclo.getDataPrevisaoFormatada();
            }
        }

        @Override
        public String getIdValue(Ciclo ciclo, int index) {
            return ciclo.getPk().toString();
        }
    }

    public PerfilRisco getPerfilRiscoSelecionado() {
        return modelPerfilRisco.getObject();
    }

    private void gerarLogDetalhamentoPerfilRisco() {
        LogOperacaoMediator.get().gerarLogDetalhamento(entidadeSupervisionavel, modelPerfilRisco.getObject(),
                atualizarDadosPagina(getPaginaAtual()));
    }

}
