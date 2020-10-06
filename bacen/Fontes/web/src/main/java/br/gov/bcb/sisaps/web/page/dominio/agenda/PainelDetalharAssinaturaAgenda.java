package br.gov.bcb.sisaps.web.page.dominio.agenda;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.Form;

import br.gov.bcb.sisaps.src.dominio.AgendaCorec;
import br.gov.bcb.sisaps.src.dominio.ParticipanteAgendaCorec;
import br.gov.bcb.sisaps.web.page.componentes.botoes.AjaxSubmitLinkSisAps;
import br.gov.bcb.sisaps.web.page.componentes.modal.PainelModalWindow;
import br.gov.bcb.sisaps.web.page.dominio.agenda.PainelAlterarAssinaturaAgenda.LinkIncluir;
import br.gov.bcb.sisaps.web.page.dominio.gestaoAgenda.PainelCicloAgenda;
import br.gov.bcb.sisaps.web.page.dominio.gestaoAgenda.PainelListagemParticipantesAgenda;

public class PainelDetalharAssinaturaAgenda extends PainelModalWindow {

    private final Form<?> form = new Form<Object>("form");
    private final ModalWindow modalAlteracao;
    private PainelCicloAgenda painelCiclo;
    private LinkIncluir linkSalvar;
    private AgendaCorec agenda;
    private ParticipanteAgendaCorec participante;

    public PainelDetalharAssinaturaAgenda(ModalWindow modalAlteracao, ParticipanteAgendaCorec participante) {
        super(modalAlteracao.getContentId());
        this.modalAlteracao = modalAlteracao;
        this.participante = participante;
        this.agenda = participante.getAgenda();
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        painelCiclo = new PainelCicloAgenda("dadosCiclo", agenda, false);
        form.addOrReplace(painelCiclo);
        form.addOrReplace(new LinkFechar());
        form.addOrReplace(new PainelListagemParticipantesAgenda("participantes", agenda));
        form.addOrReplace(new PainelAssinaturaAtaComite("ata", participante, false));
        setOutputMarkupId(true);
        add(form);

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
