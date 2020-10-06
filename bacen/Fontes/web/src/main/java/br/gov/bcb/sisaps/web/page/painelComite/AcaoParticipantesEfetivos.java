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

public class AcaoParticipantesEfetivos extends Panel {

    public AcaoParticipantesEfetivos(String id, final IModel<ParticipanteComiteVO> model, 
            final List<ParticipanteComiteVO> possiveisParticipantes, 
            final List<ParticipanteComiteVO> participantesEfetivos, 
            final List<ParticipanteComiteVO> participantesEfetivosIncluidos, 
            final List<ParticipanteComiteVO> participantesEfetivosExcluidos) {
        super(id, model);

        AjaxSubmitLink linkExcluir = new AjaxSubmitLink("linkExcluir") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                excluir(model, possiveisParticipantes, participantesEfetivos, participantesEfetivosIncluidos,
                        participantesEfetivosExcluidos, target);
            }
        };
        linkExcluir.add(new Image("excluir", ConstantesImagens.IMG_EXCLUIR));
        linkExcluir.setOutputMarkupId(true);
        add(linkExcluir);
    }
    
    private void excluir(final IModel<ParticipanteComiteVO> model,
            final List<ParticipanteComiteVO> possiveisParticipantes,
            final List<ParticipanteComiteVO> participantesEfetivos,
            final List<ParticipanteComiteVO> participantesEfetivosIncluidos,
            final List<ParticipanteComiteVO> participantesEfetivosExcluidos, AjaxRequestTarget target) {
        ParticipanteComiteVO participanteExcluido = model.getObject();
        participantesEfetivos.remove(participanteExcluido);
        possiveisParticipantes.add(participanteExcluido);
        
        if (participanteExcluido.getPkParticipanteAgendaCorec() != null
                && !participantesEfetivosExcluidos.contains(participanteExcluido)) {
            participantesEfetivosExcluidos.add(participanteExcluido);
        }
        
        if (participantesEfetivosIncluidos.contains(participanteExcluido)) {
            participantesEfetivosIncluidos.remove(participanteExcluido);
        }
        
        ((GestaoCorecPage) getPage()).atualizarBotaoEAlertaPainelParticipantesVisiveis();
        ((GestaoCorecPage) getPage()).atualizarTabelasParticipantes(target);
    }

}