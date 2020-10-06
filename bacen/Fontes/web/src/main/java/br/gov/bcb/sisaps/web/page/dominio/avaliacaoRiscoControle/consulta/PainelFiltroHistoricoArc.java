package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.consulta;

import java.util.ArrayList;
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

import br.gov.bcb.sisaps.src.dominio.ParametroGrupoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoARCEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoGrupoEnum;
import br.gov.bcb.sisaps.src.mediator.ParametroGrupoRiscoControleMediator;
import br.gov.bcb.sisaps.src.vo.ConsultaAvaliacaoRiscoControleVO;
import br.gov.bcb.sisaps.web.page.PainelSisAps;

public class PainelFiltroHistoricoArc extends PainelSisAps {
    private static final String OPTION_SELECIONE = "<option value=\"\">Todos</option>";
    private static final String CODIGO = "codigo";
    private static final String DESCRICAO = "descricao";
    private DropDownChoice<String> selectGrupoRiscoControle;
    private DropDownChoice<EstadoARCEnum> selectEstacoARC;
    private DropDownChoice<TipoGrupoEnum> selectRiscoControle;
    private final List<String> listaGrupo = new ArrayList<String>();
    private Set<EstadoARCEnum> listaEstado;
    private final ConsultaAvaliacaoRiscoControleVO consulta;

    public PainelFiltroHistoricoArc(String id, ConsultaAvaliacaoRiscoControleVO consulta) {
        super(id);
        this.consulta = consulta;
        addComponentes();
    }

    private void addComponentes() {
        adicionarNaListaGrupo();
        addNomeES();
        addAtividade();
        addGrupoRiscoControle();
        addComboTipoRiscoControle();
        addComboEstadoARC();
        addBotoesFiltro();
    }

    private void adicionarNaListaGrupo() {
        for (ParametroGrupoRiscoControle pgrc : ParametroGrupoRiscoControleMediator.get().buscarGrupos()) {
            if (!listaGrupo.contains(pgrc.getNomeAbreviado())) {
                listaGrupo.add(pgrc.getNomeAbreviado());
            }
        }
    }
    private void addNomeES() {
        TextField<String> txtAtividade =
                new TextField<String>("idNomeEs", new PropertyModel<String>(consulta, "nomeES"));
        txtAtividade.setMarkupId(txtAtividade.getMarkupId());
        txtAtividade.setOutputMarkupId(true);
        txtAtividade.setOutputMarkupPlaceholderTag(true);
        addOrReplace(txtAtividade);
    }

    private void addAtividade() {
        TextField<String> txtAtividade =
                new TextField<String>("idAtividade", new PropertyModel<String>(consulta, "nomeAtividade"));
        txtAtividade.setMarkupId(txtAtividade.getMarkupId());
        txtAtividade.setOutputMarkupId(true);
        txtAtividade.setOutputMarkupPlaceholderTag(true);
        addOrReplace(txtAtividade);
    }

    private void addGrupoRiscoControle() {
        selectGrupoRiscoControle =
                new DropDownChoiceValorPadrao<String>("idGrupoRiscoControle", new PropertyModel<String>(consulta,
                        "nomeGrupoRiscoControle"), new ArrayList<String>(listaGrupo));
        add(selectGrupoRiscoControle);
    }

    private void addComboTipoRiscoControle() {
        ChoiceRenderer<TipoGrupoEnum> renderer = new ChoiceRenderer<TipoGrupoEnum>("abreviacao", CODIGO);
        List<TipoGrupoEnum> lista = (TipoGrupoEnum.listaTipoGrupo());
        selectRiscoControle =
                new DropDownChoiceValorPadrao<TipoGrupoEnum>("idTipoGrupoRiscoControle",
                        new PropertyModel<TipoGrupoEnum>(consulta, "tipoGrupo"), lista, renderer);
        add(selectRiscoControle);
    }

    private void addComboEstadoARC() {
        ChoiceRenderer<EstadoARCEnum> ren =
                new ChoiceRenderer<EstadoARCEnum>(DESCRICAO, CODIGO);
        List<EstadoARCEnum> lista = EstadoARCEnum.listaEstadosPreenchidoAnalisado();

        selectEstacoARC =
                new DropDownChoiceValorPadrao<EstadoARCEnum>("idEstadoARC",
                        new PropertyModel<EstadoARCEnum>(consulta, "estadoARC"), lista, ren);
        add(selectEstacoARC);
    }

    private void addBotoesFiltro() {
        addOrReplace(new AjaxButton("bttFiltrarArc") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                ((ConsultaHistoricoPage) getPage()).atualizarListaARCs(target);
            }
        });

        addOrReplace(new AjaxButton("bttLimparFiltrosArc") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                limparCampos(target);
                ((ConsultaHistoricoPage) getPage()).limparFiltroARCs(target);
                ((ConsultaHistoricoPage) getPage()).atualizarListaARCs(target);
            }

        });
    }

    private void limparCampos(AjaxRequestTarget target) {
        consulta.setNomeES(null);
        consulta.setNomeAtividade(null);
        consulta.setNomeGrupoRiscoControle(null);
        consulta.setTipoGrupo(null);
        consulta.setEstadoARC(null);
        target.add(selectGrupoRiscoControle);
        target.add(selectEstacoARC);
        target.add(selectRiscoControle);
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
