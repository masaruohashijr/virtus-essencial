package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.consulta;

import java.util.List;
import java.util.Set;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoAQTEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoARCEnum;
import br.gov.bcb.sisaps.src.vo.ConsultaAnaliseQuantitativaAQTVO;
import br.gov.bcb.sisaps.web.page.PainelSisAps;

public class PainelFiltroHistoricoAnef extends PainelSisAps {
    private static final String OPTION_SELECIONE = "<option value=\"\">Todos</option>";
    private static final String CODIGO = "codigo";
    private static final String DESCRICAO = "descricao";
    private DropDownChoice<EstadoAQTEnum> selectAcao;
    private Set<EstadoARCEnum> listaEstado;
    private final ConsultaAnaliseQuantitativaAQTVO consulta;

    public PainelFiltroHistoricoAnef(String id, ConsultaAnaliseQuantitativaAQTVO consulta) {
        super(id);
        this.consulta = consulta;
        addComponentes();
    }

    private void addComponentes() {
        addNomeES();
        addComponente();
        addComboAcao();
        addBotoesFiltro();
    }

    private void addNomeES() {
        TextField<String> txtAtividade =
                new TextField<String>("idNomeEs", new PropertyModel<String>(consulta, "nomeES"));
        txtAtividade.setMarkupId(txtAtividade.getMarkupId());
        txtAtividade.setOutputMarkupId(true);
        txtAtividade.setOutputMarkupPlaceholderTag(true);
        addOrReplace(txtAtividade);
    }

    private void addComponente() {
        TextField<String> txtAtividade =
                new TextField<String>("idComponente", new PropertyModel<String>(consulta, "nomeComponente"));
        txtAtividade.setMarkupId(txtAtividade.getMarkupId());
        txtAtividade.setOutputMarkupId(true);
        txtAtividade.setOutputMarkupPlaceholderTag(true);
        addOrReplace(txtAtividade);
    }

    private void addComboAcao() {
        ChoiceRenderer<EstadoAQTEnum> ren = new ChoiceRenderer<EstadoAQTEnum>(DESCRICAO, CODIGO);
        List<EstadoAQTEnum> lista = EstadoAQTEnum.listaEstadosPreenchidoAnalisado();
        selectAcao =
                new DropDownChoiceValorPadrao<EstadoAQTEnum>("idAcao", new PropertyModel<EstadoAQTEnum>(consulta,
                        "estadoANEF"), lista, ren);
        add(selectAcao);
    }

    private void addBotoesFiltro() {
        addOrReplace(new AjaxButton("bttFiltrarAnef") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                ((ConsultaHistoricoPage) getPage()).atualizarListaANEFs(target);
            }
        });

        addOrReplace(new AjaxButton("bttLimparFiltrosAnef") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                limparCampos(target);
                ((ConsultaHistoricoPage) getPage()).limparFiltroANEFs(target);
                ((ConsultaHistoricoPage) getPage()).atualizarListaANEFs(target);
            }

        });
    }

    private void limparCampos(AjaxRequestTarget target) {
        consulta.setNomeComponente(null);
        consulta.setTipoGrupo(null);
        consulta.setEstadoANEF(null);
        consulta.setNomeES(null);
        target.add(selectAcao);
    }

    private static class DropDownChoiceValorPadrao<T> extends DropDownChoice<T> {

        public DropDownChoiceValorPadrao(String id, IModel<T> model, List<? extends T> data,
                IChoiceRenderer<? super T> renderer) {
            super(id, model, data, renderer);
        }

        public DropDownChoiceValorPadrao(String id, IModel<T> model, List<? extends T> data) {
            super(id, model, data);
        }

        @Override
        protected CharSequence getDefaultChoice(String selectedValue) {
            return OPTION_SELECIONE;
        }

    }

    public Set<EstadoARCEnum> getListaEstado() {
        return listaEstado;
    }

    public void setListaEstado(Set<EstadoARCEnum> listaEstado) {
        this.listaEstado = listaEstado;
    }

}
