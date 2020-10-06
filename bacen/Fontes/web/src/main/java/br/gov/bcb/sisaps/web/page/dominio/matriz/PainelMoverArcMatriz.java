package br.gov.bcb.sisaps.web.page.dominio.matriz;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.gov.bcb.sisaps.src.dominio.Atividade;
import br.gov.bcb.sisaps.src.dominio.CelulaRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.mediator.AtividadeMediator;
import br.gov.bcb.sisaps.web.page.componentes.drops.CustomDropDownChoice;
import br.gov.bcb.sisaps.web.page.componentes.modal.AjaxSubmitLinkModalWindow;
import br.gov.bcb.sisaps.web.page.componentes.modal.PainelModalWindow;

public class PainelMoverArcMatriz extends PainelModalWindow {

    private static final String ID_LISTA_ATIVIDADE = "idAtividadeDestino";
    private static final String OPCAO_SELECIONE = "Selecionar";
    private static final String PROP_NOME = "nome";
    private final Atividade atividadeAtual;
    private Atividade atividadeNova;
    private final ModalWindow modalWindow;
    private final Form<?> form;

    private final Matriz matrizAtual;

    @SpringBean
    private AtividadeMediator atividadeMediator;

    private final CelulaRiscoControle celulaRiscoControle;

    public PainelMoverArcMatriz(ModalWindow modalWindow, Matriz matriz, Atividade atividade,
            CelulaRiscoControle celulaRiscoControle) {
        super(modalWindow.getContentId());
        this.modalWindow = modalWindow;
        this.atividadeAtual = atividade;
        this.matrizAtual = matriz;
        this.celulaRiscoControle = celulaRiscoControle;
        form = new Form<Object>("form");
        addComponentes();
        form.add(new LinkFechar());
        form.add(new LinkConcluir());
        add(form);

    }

    private void addComponentes() {
        Label nomeArc =
                new Label("idNomeGrupoRisco", celulaRiscoControle.getParametroGrupoRiscoControle().getNomeAbreviado());
        form.addOrReplace(nomeArc);
        Label nomeAtividade = new Label("idAtividadeAssociada", atividadeAtual.getNome());
        form.addOrReplace(nomeAtividade);
        addTipoAtividade();
    }

    private void addTipoAtividade() {
        ChoiceRenderer<Atividade> renderer = new ChoiceRenderer<Atividade>(PROP_NOME, Atividade.PROP_ID);
        PropertyModel<Atividade> propertyModel = new PropertyModel<Atividade>(this, "atividadeNova");
        List<Atividade> listaChoices = montarChoices();
        DropDownChoice<Atividade> selectAtividade =
                new CustomDropDownChoice<Atividade>(ID_LISTA_ATIVIDADE, OPCAO_SELECIONE, propertyModel, listaChoices,
                        renderer);
        selectAtividade.setMarkupId(ID_LISTA_ATIVIDADE);
        selectAtividade.setOutputMarkupId(true);
        selectAtividade.setOutputMarkupPlaceholderTag(true);
        form.addOrReplace(selectAtividade);
    }

    private List<Atividade> montarChoices() {
        List<Atividade> retorno = atividadeMediator.buscarTodasAtividadesMatriz(matrizAtual);
        List<Atividade> excluidos = new ArrayList<Atividade>();
        retorno.add(atividadeAtual);
        for (Atividade atividade : retorno) {
            if (celulaRiscoControle.getAtividade().getPk().equals(atividade.getPk())) {
                excluidos.add(atividade);
                break;
            }
        }
        retorno.removeAll(excluidos);
        return retorno;
    }

    protected class LinkFechar extends AjaxLink<Void> {
        public LinkFechar() {
            super("bttVoltarModal");
        }

        @Override
        public void onClick(AjaxRequestTarget target) {
            modalWindow.close(target);
        }
    }

    protected class LinkConcluir extends AjaxSubmitLinkModalWindow {
        public LinkConcluir() {
            super("bttAlterarAssociacao");
        }

        @Override
        public void executeSubmit(AjaxRequestTarget target) {
            if (atividadeNova != null) {
                atividadeNova = AtividadeMediator.get().loadPK(atividadeNova.getPk());
            }
            atividadeMediator.transferirArcs(atividadeAtual, atividadeNova, celulaRiscoControle);
            modalWindow.close(target);
            atualizar(target);

        }
    }

    private void atualizar(AjaxRequestTarget target) {

        ((EdicaoMatrizPage) getPage()).atualizarPaineis(target, "O \""
                + celulaRiscoControle.getParametroGrupoRiscoControle().getNomeAbreviado() + "\" movido com sucesso",
                true);
    }

    public Atividade getAtividadeNova() {
        return atividadeNova;
    }

    public void setAtividadeNova(Atividade atividadeNova) {
        this.atividadeNova = atividadeNova;
    }

}
