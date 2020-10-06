package br.gov.bcb.sisaps.web.page.dominio.ciclo.pesquisa;

import java.util.ArrayList;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.EntidadeSupervisionavel;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;
import br.gov.bcb.sisaps.web.page.componentes.calendar.CalendarTextField;
import br.gov.bcb.sisaps.web.page.componentes.modal.AjaxSubmitLinkModalWindow;
import br.gov.bcb.sisaps.web.page.componentes.modal.PainelModalWindow;

public class PainelIniciarCiclo extends PainelModalWindow {
    private final Ciclo ciclo = new Ciclo();
    private final Form<?> form = new Form<Object>("form");;
    private final ModalWindow modalInclusaoCiclo;
    private final EntidadeSupervisionavel entidadeSupervisionavel;
    private IModel<String> modelInicio;

    public PainelIniciarCiclo(ModalWindow modalInclusaoCiclo, EntidadeSupervisionavel entidadeSupervisionavel) {
        super(modalInclusaoCiclo.getContentId());
        this.modalInclusaoCiclo = modalInclusaoCiclo;
        this.entidadeSupervisionavel = entidadeSupervisionavel;
        addNomeEs();
        addDatas();
        addBotaoFechar();
        addBotaoAdicionar();
        setOutputMarkupId(true);
        add(form);
    }

    private void addNomeEs() {
        Label nomeEs = new Label("idNomeEs", entidadeSupervisionavel.getNome());
        form.add(nomeEs);
    }

    private void addBotaoAdicionar() {
        LinkIncluir linkIniciarNovoCiclo = new LinkIncluir("btnIniciarNewCiclo");
        linkIniciarNovoCiclo.setOutputMarkupId(true);
        linkIniciarNovoCiclo.setMarkupId(linkIniciarNovoCiclo.getId());
        form.add(linkIniciarNovoCiclo);
    }

    private void addBotaoFechar() {
        LinkFechar linkFechar = new LinkFechar();
        linkFechar.setOutputMarkupId(true);
        linkFechar.setMarkupId(linkFechar.getId());
        form.add(linkFechar);
    }

    protected class LinkFechar extends AjaxLink<Void> {
        public LinkFechar() {
            super("bttVoltar");
        }

        @Override
        public void onClick(AjaxRequestTarget target) {
            modalInclusaoCiclo.close(target);
        }

    }

    protected class LinkIncluir extends AjaxSubmitLinkModalWindow {

        public LinkIncluir(String id) {
            super(id);
        }

        @Override
        public void executeSubmit(AjaxRequestTarget target) {
            if (modelInicio.getObject() == null) {
                ArrayList<ErrorMessage> erros = new ArrayList<ErrorMessage>();
                SisapsUtil.validarObrigatoriedade(modelInicio.getObject(), "Data de início do ciclo", erros);
                SisapsUtil.lancarNegocioException(erros);
            } else {
                ciclo.setEntidadeSupervisionavel(entidadeSupervisionavel);
                ciclo.setMetodologia(entidadeSupervisionavel.getMetodologia());
                CicloMediator.get().incluir(ciclo, modelInicio.getObject(), false);
                modalInclusaoCiclo.close(target);
                atualizar(target);
            }
        }
    }

    private void atualizar(AjaxRequestTarget target) {
        ((ConsultaCicloPage) getPage()).atualizarPaineis(target);
    }

    private void addDatas() {
        modelInicio = new Model<String>();
        CalendarTextField<String> txtDataInicio = new CalendarTextField<String>("idDataInicio", modelInicio);
        txtDataInicio.setParentId("pInicioCiclo");
        txtDataInicio.setOutputMarkupId(true);
        txtDataInicio.setMarkupId(txtDataInicio.getId() + entidadeSupervisionavel.getPk());
        form.addOrReplace(txtDataInicio);
    }

    public EntidadeSupervisionavel getEntidadeSupervisionavel() {
        return entidadeSupervisionavel;
    }

}