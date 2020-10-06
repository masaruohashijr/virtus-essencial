package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.edicao;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import br.gov.bcb.sisaps.src.vo.ObjetoPersistenteVO;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.web.page.componentes.botoes.AjaxSubmitLinkSisAps;
import br.gov.bcb.sisaps.web.page.componentes.util.ConstantesImagens;

public abstract class AcaoExcluirAnexo<VO extends ObjetoPersistenteVO> extends Panel {

    public AcaoExcluirAnexo(String id, final IModel<VO> model, String entidade) {
        super(id, model);
        AjaxSubmitLinkSisAps linkExcluir = new AjaxSubmitLinkSisAps("linkExcluir", true) {
            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                onExcluir(target);
            }
        };
        linkExcluir.add(new Image("excluir", ConstantesImagens.IMG_EXCLUIR));
        linkExcluir.setOutputMarkupId(true);
        linkExcluir.setMarkupId("linkExcluir_" + entidade + "_" 
                + SisapsUtil.criarMarkupId(model.getObject().getPk().toString()));
        add(linkExcluir);
    }

    public abstract void onExcluir(AjaxRequestTarget target);
}
