package br.gov.bcb.sisaps.web.page.dominio.matriz;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoMatrizEnum;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.MatrizCicloMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.web.page.DefaultPage;
import br.gov.bcb.sisaps.web.page.componentes.botoes.AjaxSubmitLinkSisAps;
import br.gov.bcb.sisaps.web.page.componentes.botoes.CustomButton;
import br.gov.bcb.sisaps.web.page.dominio.ciclo.painel.PainelAtividades;
import br.gov.bcb.sisaps.web.page.dominio.ciclo.painel.PainelDadosMatrizPercentual;
import br.gov.bcb.sisaps.web.page.dominio.ciclo.painel.PainelResumoCiclo;
import br.gov.bcb.sisaps.web.page.dominio.perfilderisco.PerfilDeRiscoPage;

public abstract class AbstractMatrizPage extends DefaultPage {

    protected CompoundPropertyModel<Ciclo> cicloModel;
    protected Form<Ciclo> form;
    protected CustomButton botaoEditar;
    protected CustomButton botaoLiberar;
    protected AjaxSubmitLinkSisAps botaoDesfazer;
    private Matriz matrizAtual;
    private PerfilRisco perfilRisco;;

    @SpringBean
    private CicloMediator cicloMediator;

    @SpringBean
    private MatrizCicloMediator matrizCicloMediator;

    @SpringBean
    private PerfilRiscoMediator perfilRiscoMediator;

    private PainelAtividades painelAtividades;
    private PainelDadosMatrizPercentual painelMatrizEsbocada;
    private Matriz matrizVigente;
    private PainelEdicaoBlocoPercentual painelPercentual;
    private PainelEdicaoBlocoPercentualNovo painelPercentualNovo;
    private final Ciclo cicloSelecionado;
    private boolean mostraLiberar;

    public AbstractMatrizPage(Integer pkCiclo) {
        super();
        this.cicloSelecionado = cicloMediator.loadPK(pkCiclo);

    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        cicloModel = new CompoundPropertyModel<Ciclo>(cicloSelecionado);
        matrizAtual = matrizCicloMediator.getMatrizEmEdicao(cicloSelecionado);
        perfilRisco = perfilRiscoMediator.obterPerfilRiscoAtual(cicloSelecionado.getPk());
        matrizVigente = perfilRiscoMediator.getMatrizAtualPerfilRisco(perfilRisco);
        mostraLiberar = matrizAtual.getVersao() > 0;
        addComponentes();
    }

    protected void addComponentes() {
        form = new Form<Ciclo>("formulario", cicloModel);
        addPainel();
        addButtons();
        addOrReplace(form);
    }

    private void addButtons() {
        form.add(addBotaoLiberar());
        form.add(addBotaoDesfazer());
        form.addOrReplace(new LinkVoltarPerfilRisco(cicloSelecionado.getPk()));
    }

    private CustomButton addBotaoLiberar() {
        botaoLiberar = new CustomButton("bttLiberar") {
            @Override
            public boolean isVisible() {
                return EstadoMatrizEnum.ESBOCADA.equals(matrizAtual.getEstadoMatriz());
            }

            @Override
            public void executeSubmit() {
                String msg = matrizCicloMediator.liberarMatrizCiclo(matrizCicloMediator.loadPK(matrizAtual.getPk()));

                avancarParaNovaPagina(new PerfilDeRiscoPage(cicloModel.getObject().getPk(), msg, false),
                        getPaginaAnterior().getPaginaAnterior());
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                setEnabled(mostraLiberar);
            }
        };
        return botaoLiberar;
    }

    private AjaxSubmitLinkSisAps addBotaoDesfazer() {

        botaoDesfazer = new AjaxSubmitLinkSisAps("bttDesfazer") {

            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                matrizCicloMediator.evict(matrizAtual);
                MatrizCicloMediator.get().desfazerMatrizCiclo(matrizVigente, cicloSelecionado, perfilRisco);
                avancarParaNovaPagina(new SucessoEdicaoMatrizPage(cicloModel.getObject().getPk(), false),
                        getPaginaAnterior());
            }

        };
        return botaoDesfazer;
    }

    private void addPainel() {
        PainelResumoCiclo painelDados =
                new PainelResumoCiclo("idPainelDados", cicloModel.getObject().getPk(), perfilRisco);
        form.addOrReplace(painelDados);

        PainelDadosMatrizPercentual painelMatrizVigente =
                new PainelDadosMatrizPercentual("idPainelMatrizVigente",
                        perfilRiscoMediator.obterPerfilRiscoAtual(cicloModel.getObject().getPk()), "Matriz vigente",
                        true, true, true);
        form.addOrReplace(painelMatrizVigente);

        addMatrizEsbocada();

        painelPercentual = new PainelEdicaoBlocoPercentual("idPainelBloco", matrizAtual, matrizVigente);
        form.addOrReplace(painelPercentual);
        
        painelPercentualNovo = new PainelEdicaoBlocoPercentualNovo("idPainelBlocoNovo", matrizAtual, matrizVigente);
        form.addOrReplace(painelPercentualNovo);

        painelAtividades = new PainelAtividades("idPainelAtividades", matrizAtual);
        form.addOrReplace(painelAtividades);
    }

    private void addMatrizEsbocada() {
        painelMatrizEsbocada =
                new PainelDadosMatrizPercentual("idPainelMatriz", null, "Matriz em edição (notas vigentes)",
                        matrizAtual, false, true, false);
        painelMatrizEsbocada.setVisible(EstadoMatrizEnum.ESBOCADA.equals(matrizAtual.getEstadoMatriz()));
        form.addOrReplace(painelMatrizEsbocada);
    }

    public void atualizarPaineis(AjaxRequestTarget target, String sucesso, boolean exibirLiberar) {
        this.mostraLiberar = exibirLiberar;
        target.add(painelPercentual);
        target.add(painelPercentualNovo);
        target.add(painelAtividades);
        target.add(painelMatrizEsbocada);
        success(sucesso);
        target.add(this.get("feedback"));
        target.add(scriptErro);
        target.add(botaoLiberar);
        target.add(botaoDesfazer);
    }

}