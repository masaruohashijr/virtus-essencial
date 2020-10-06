package br.gov.bcb.sisaps.web.page.dominio.gestaoAgenda;

import java.util.ArrayList;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.Form;

import br.gov.bcb.sisaps.src.dominio.AgendaCorec;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.ObservacaoAgendaCorec;
import br.gov.bcb.sisaps.src.mediator.AgendaCorecMediator;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.web.page.componentes.botoes.AjaxSubmitLinkSisAps;
import br.gov.bcb.sisaps.web.page.componentes.modal.PainelModalWindow;

public class PainelDetalharAgenda extends PainelModalWindow {

    private static final String PARTICIPANTES = "participantes";
    private final Form<?> form = new Form<Object>("form");
    private final ModalWindow modalAlteracao;
    private PainelCicloAgenda painelCiclo;
    private PainelListagemObservacao painelListagemObservacao;
    private PainelDadosAgenda painelDadosAgenda;
    private AgendaCorec agenda;
    private int pkCiclo;

    public PainelDetalharAgenda(ModalWindow modalAlteracao, int pkCiclo) {
        super(modalAlteracao.getContentId());
        this.modalAlteracao = modalAlteracao;
        this.pkCiclo = pkCiclo;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        inicializarAgenda();
        painelCiclo = new PainelCicloAgenda("dadosCiclo", agenda, false);
        form.addOrReplace(painelCiclo);
        form.addOrReplace(new PainelDatasEmail("dataEmail", agenda, false));
        painelDadosAgenda = new PainelDadosAgenda("dadosAgenda", agenda, false);
        form.addOrReplace(painelDadosAgenda);
        painelListagemObservacao = new PainelListagemObservacao("observacao", agenda, false);
        form.addOrReplace(painelListagemObservacao);
        form.addOrReplace(new LinkFechar());
        form.addOrReplace(new PainelListagemParticipantesAgenda(PARTICIPANTES, agenda));
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

}
