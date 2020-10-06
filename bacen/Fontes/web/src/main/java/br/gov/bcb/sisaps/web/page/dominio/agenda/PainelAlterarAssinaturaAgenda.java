package br.gov.bcb.sisaps.web.page.dominio.agenda;

import java.util.ArrayList;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;

import br.gov.bcb.sisaps.src.dominio.AgendaCorec;
import br.gov.bcb.sisaps.src.dominio.ParticipanteAgendaCorec;
import br.gov.bcb.sisaps.src.mediator.ParticipanteAgendaCorecMediator;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;
import br.gov.bcb.sisaps.web.page.componentes.botoes.AjaxSubmitLinkSisAps;
import br.gov.bcb.sisaps.web.page.componentes.modal.AjaxSubmitLinkModalWindow;
import br.gov.bcb.sisaps.web.page.componentes.modal.PainelModalWindow;
import br.gov.bcb.sisaps.web.page.dominio.gestaoAgenda.PainelCicloAgenda;
import br.gov.bcb.sisaps.web.page.dominio.gestaoAgenda.PainelListagemParticipantesAgenda;

public class PainelAlterarAssinaturaAgenda extends PainelModalWindow {

    private final Form<?> form = new Form<Object>("form");
    private final ModalWindow modalAlteracao;
    private PainelCicloAgenda painelCiclo;
    private LinkIncluir linkSalvar;
    private AgendaCorec agenda;
    private ParticipanteAgendaCorec participante;
    private PainelAssinaturaAtaComite painelAssinatura;

    public PainelAlterarAssinaturaAgenda(ModalWindow modalAlteracao, ParticipanteAgendaCorec participante) {
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
        addBotaoSalvar();
        form.addOrReplace(new PainelListagemParticipantesAgenda("participantes", agenda));
        painelAssinatura = new PainelAssinaturaAtaComite("ata", participante, true);
        form.addOrReplace(painelAssinatura);
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
            if ((Boolean) painelAssinatura.getCheck().getDefaultModelObject()) {
                String msg = ParticipanteAgendaCorecMediator.get().assinarATA(participante);
                modalAlteracao.close(target);
                AgendaPage page = (AgendaPage) getPage();
                target.add(page.get("feedback"));
                page.atualizar(target, msg);
            } else {
                ArrayList<ErrorMessage> erros = new ArrayList<ErrorMessage>();
                erros.add(new ErrorMessage("A ata não foi assinada."));
                SisapsUtil.lancarNegocioException(erros);
            }

        }
    }

}
