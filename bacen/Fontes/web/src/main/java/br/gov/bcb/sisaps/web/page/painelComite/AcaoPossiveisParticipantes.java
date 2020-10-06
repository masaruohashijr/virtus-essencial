package br.gov.bcb.sisaps.web.page.painelComite;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import br.gov.bcb.sisaps.src.vo.ParticipanteComiteVO;
import br.gov.bcb.sisaps.web.page.componentes.util.ConstantesImagens;

public class AcaoPossiveisParticipantes extends Panel {

    public AcaoPossiveisParticipantes(String id, final IModel<ParticipanteComiteVO> model, 
            final List<ParticipanteComiteVO> possiveisParticipantes, 
            final List<ParticipanteComiteVO> participantesEfetivos, 
            final List<ParticipanteComiteVO> participantesEfetivosIncluidos, 
            final List<ParticipanteComiteVO> participantesEfetivosExcluidos) {
        super(id, model);

        AjaxSubmitLink linkSelecionar = new AjaxSubmitLink("linkSelecionar") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                selecionar(model, possiveisParticipantes, participantesEfetivos, participantesEfetivosIncluidos,
                        participantesEfetivosExcluidos, target);
            }
        };
        linkSelecionar.add(new Image("selecionar", ConstantesImagens.IMG_SELECIONAR));
        linkSelecionar.setOutputMarkupId(true);
        add(linkSelecionar);
    }
    
    private void selecionar(final IModel<ParticipanteComiteVO> model,
            final List<ParticipanteComiteVO> possiveisParticipantes,
            final List<ParticipanteComiteVO> participantesEfetivos,
            final List<ParticipanteComiteVO> participantesEfetivosIncluidos,
            final List<ParticipanteComiteVO> participantesEfetivosExcluidos, AjaxRequestTarget target) {
        ParticipanteComiteVO participanteIncluido = model.getObject();
        possiveisParticipantes.remove(participanteIncluido);
        participantesEfetivos.add(participanteIncluido);
        
        if (participanteIncluido.getPkParticipanteAgendaCorec() == null
                && !participantesEfetivosIncluidos.contains(participanteIncluido)) {
            participantesEfetivosIncluidos.add(participanteIncluido);
        }
        
        if (participantesEfetivosExcluidos.contains(participanteIncluido)) {
            participantesEfetivosExcluidos.remove(participanteIncluido);
        }
        
        ((GestaoCorecPage) getPage()).atualizarBotaoEAlertaPainelParticipantesVisiveis();
        ((GestaoCorecPage) getPage()).atualizarTabelasParticipantes(target);
    }

}