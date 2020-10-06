package br.gov.bcb.sisaps.web.page.dominio.gestaoAgenda;

import java.util.ArrayList;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import br.gov.bcb.sisaps.src.dominio.AgendaCorec;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.ObservacaoAgendaCorec;
import br.gov.bcb.sisaps.src.mediator.AgendaCorecMediator;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.validacao.RegraEdicaoAgendaValidarDataCorecEHorario;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.web.page.componentes.botoes.AjaxSubmitLinkSisAps;
import br.gov.bcb.sisaps.web.page.componentes.modal.AjaxSubmitLinkModalWindow;
import br.gov.bcb.sisaps.web.page.componentes.modal.PainelModalWindow;

public class PainelAlterarAgenda extends PainelModalWindow {

    private final Form<?> form = new Form<Object>("form");
    private final ModalWindow modalAlteracao;
    private PainelCicloAgenda painelCiclo;
    private PainelListagemObservacao painelListagemObservacao;
    private PainelDadosAgenda painelDadosAgenda;

    private LinkIncluir linkSalvar;
    private AgendaCorec agenda;
    private int pkCiclo;

    public PainelAlterarAgenda(ModalWindow modalAlteracao, int pkCiclo) {
        super(modalAlteracao.getContentId());
        this.modalAlteracao = modalAlteracao;
        this.pkCiclo = pkCiclo;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        inicializarAgenda();
        painelCiclo = new PainelCicloAgenda("dadosCiclo", agenda, true);
        form.addOrReplace(painelCiclo);
        form.addOrReplace(new PainelDatasEmail("dataEmail", agenda, true));
        painelDadosAgenda = new PainelDadosAgenda("dadosAgenda", agenda, true);
        form.addOrReplace(painelDadosAgenda);
        painelListagemObservacao = new PainelListagemObservacao("observacao", agenda, true);
        form.addOrReplace(painelListagemObservacao);
        form.addOrReplace(new LinkFechar());
        addBotaoSalvar();
        form.addOrReplace(new PainelListagemParticipantesAgenda("participantes", agenda));
        setOutputMarkupId(true);
        add(form);

    }

    private void inicializarAgenda() {
        agenda = AgendaCorecMediator.get().buscarAgendaCorecPorCiclo(pkCiclo);
        if (agenda == null) {
            Ciclo ciclo = CicloMediator.get().buscarCicloPorPK(pkCiclo);
            agenda = new AgendaCorec();
            agenda.setCiclo(ciclo);
            agenda.setObservacoes(new ArrayList<ObservacaoAgendaCorec>());
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

    private void addBotaoSalvar() {
        linkSalvar = new LinkIncluir("bttSalvar");
        linkSalvar.setOutputMarkupId(true);
        linkSalvar.setMarkupId(linkSalvar.getId());
        linkSalvar.setBody(new Model<String>("Teste"));
        form.addOrReplace(linkSalvar);
    }

    protected class LinkIncluir extends AjaxSubmitLinkModalWindow {

        public LinkIncluir(String id) {
            super(id);
        }

        @Override
        public void executeSubmit(AjaxRequestTarget target) {
            new RegraEdicaoAgendaValidarDataCorecEHorario(painelCiclo.getModelDataCorec().getObject(),
                    painelDadosAgenda.getModelHorario().getObject()).validar();
            atualizarHorario(painelDadosAgenda.getModelHorario().getObject());
            painelCiclo.atualizarDataCorec(painelCiclo.getModelDataCorec().getObject());
            String msg =
                    AgendaCorecMediator.get().salvar(agenda, painelListagemObservacao.getListaExcluidos(),
                            painelCiclo.getDataCorec());
            modalAlteracao.close(target);
            GestaoAgendaPage page = (GestaoAgendaPage) getPage();
            target.add(page.get("feedback"));
            page.atualizar(target, msg);

        }
    }

    private void atualizarHorario(String object) {
        if (!SisapsUtil.isNuloOuVazio(object)) {
            DateTimeFormatter sdf = DateTimeFormat.forPattern(Constantes.FORMATO_HORA_AGENDA);
            DateTime date = null;
            date = sdf.parseDateTime(object);
            agenda.setHoraCorec(date);
        }
    }

}
