package br.gov.bcb.sisaps.web.page.dominio.designacao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoARCEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoGrupoEnum;
import br.gov.bcb.sisaps.src.vo.ARCDesignacaoVO;
import br.gov.bcb.sisaps.src.vo.ConsultaARCDesignacaoVO;
import br.gov.bcb.sisaps.web.page.PainelSisAps;

public class PainelFiltroDesignarArc extends PainelSisAps {
    private static final String OPTION_SELECIONE = "<option value=\"\">Todos</option>";
    private static final String CODIGO = "codigo";
    private static final String DESCRICAO = "descricao";
    private DropDownChoice<String> selectAtividade;
    private DropDownChoice<String> selectGrupoRiscoControle;
    private DropDownChoice<EstadoARCEnum> selectEstacoARC;
    private DropDownChoice<TipoGrupoEnum> selectRiscoControle;
    private DropDownChoice<ServidorVO> selectResponsavel;
    private List<ARCDesignacaoVO> listaArcsDesignado;
    private final Ciclo ciclo;
    private final List<ServidorVO> listServidores;
    private List<String> listaGrupo;
    private Set<EstadoARCEnum> listaEstado;
    private Set<String> listaAtidade;
    private Set<ServidorVO> listaResponsavel;
    private final ConsultaARCDesignacaoVO consulta;
    private final PainelDesignacaoARC painelDesignacao;
    private ServidorVO servidor = new ServidorVO();

    public PainelFiltroDesignarArc(String id, ConsultaARCDesignacaoVO consulta,
            List<ARCDesignacaoVO> listaArcsDesignado, Ciclo ciclo, List<ServidorVO> listServidores,
            PainelDesignacaoARC painelListagemDesignacao) {
        super(id);
        this.consulta = consulta;
        this.listaArcsDesignado = listaArcsDesignado;
        this.ciclo = ciclo;
        this.listServidores = listServidores;
        this.painelDesignacao = painelListagemDesignacao;
        
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        addComponentes();
    }
    private void addComponentes() {
        preencherAtividadeGrupo();
        addComboAtividade();
        addGrupoRiscoControle();
        addComboTipoRiscoControle();
        addComboEstadoARC();
        addComboResponsavelARC();
        addBotoesFiltro();
    }

    private void addComboAtividade() {
        selectAtividade =
                new DropDownChoiceValorPadrao<String>("idAtividade", new PropertyModel<String>(consulta,
                        "nomeAtividade"), new ArrayList<String>(listaAtidade));
        selectAtividade.setOutputMarkupId(true);
        selectAtividade.setMarkupId(selectAtividade.getId());
        addOrReplace(selectAtividade);
    }

    private void addGrupoRiscoControle() {
        selectGrupoRiscoControle =
                new DropDownChoiceValorPadrao<String>("idGrupoRiscoControle", new PropertyModel<String>(consulta,
                        "nomeGrupoRiscoControle"), new ArrayList<String>(listaGrupo));
        addOrReplace(selectGrupoRiscoControle);
    }

    private void addComboTipoRiscoControle() {
        ChoiceRenderer<TipoGrupoEnum> renderer = new ChoiceRenderer<TipoGrupoEnum>("abreviacao", CODIGO);
        List<TipoGrupoEnum> lista = TipoGrupoEnum.TIPO_GRUPO_MATRIZ;
        selectRiscoControle =
                new DropDownChoiceValorPadrao<TipoGrupoEnum>("idTipoGrupoRiscoControle",
                        new PropertyModel<TipoGrupoEnum>(consulta, "tipoGrupo"), lista, renderer);
        addOrReplace(selectRiscoControle);
    }

    private void addComboResponsavelARC() {
        ChoiceRenderer<ServidorVO> renderer = new ChoiceRenderer<ServidorVO>("nome", "matricula");
        selectResponsavel =
                new DropDownChoiceValorPadrao<ServidorVO>("idResponsavelARC", new PropertyModel<ServidorVO>(consulta,
                        "servidor"), new ArrayList<ServidorVO>(listaResponsavel), renderer);
        addOrReplace(selectResponsavel);
    }

    private void addComboEstadoARC() {
        ChoiceRenderer<EstadoARCEnum> ren =
                new ChoiceRenderer<EstadoARCEnum>(DESCRICAO, CODIGO);
        List<EstadoARCEnum> lista = EstadoARCEnum.listaEstadosPrevistoDesignado();

        selectEstacoARC =
                new DropDownChoiceValorPadrao<EstadoARCEnum>("idEstadoARC",
                        new PropertyModel<EstadoARCEnum>(consulta, "estadoARC"), lista, ren);
        addOrReplace(selectEstacoARC);
    }

    private void addBotoesFiltro() {
        addOrReplace(new AjaxButton("bttFiltrar") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                target.add(painelDesignacao.getPainelListagemDesignacao().getTabelaARCs());
            }
        });

        addOrReplace(new AjaxButton("bttLimparFiltros") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                limparCampos(target);
                target.add(painelDesignacao.getPainelListagemDesignacao().getTabelaARCs());
            }

        });
    }

    private void limparCampos(AjaxRequestTarget target) {
        consulta.setNomeAtividade(null);
        consulta.setNomeGrupoRiscoControle(null);
        consulta.setTipoGrupo(null);
        consulta.setEstadoARC(null);
        consulta.setMatriculaResponsavel(null);
        consulta.setServidor(null);
        target.add(selectAtividade);
        target.add(selectGrupoRiscoControle);
        target.add(selectEstacoARC);
        target.add(selectRiscoControle);
        target.add(selectResponsavel);
    }

    private void preencherAtividadeGrupo() {
        listaGrupo = new ArrayList<String>();
        listaAtidade = new HashSet<String>();
        listaResponsavel = new HashSet<ServidorVO>();

        for (ARCDesignacaoVO arc : listaArcsDesignado) {
            adicionarNaListaGrupo(arc);
            
            if (StringUtils.isNotEmpty(arc.getNomeAtividade())) {
                listaAtidade.add(arc.getNomeAtividade());
            }

            if (arc.getMatriculaResponsavel() != null) {
                if (servidor.getMatricula() == null) {
                    atualizarServidor(arc);
                }
                if (!servidor.getMatricula().equals(arc.getMatriculaResponsavel())) {
                    servidor = new ServidorVO();
                    atualizarServidor(arc);
                }

            }

        }
        Collections.sort(listaGrupo);
        
    }

    private void adicionarNaListaGrupo(ARCDesignacaoVO arc) {
        if (!listaGrupo.contains(arc.getNomeGrupoRiscoControle())) {
            listaGrupo.add(arc.getNomeGrupoRiscoControle());
        }
    }


    private void atualizarServidor(ARCDesignacaoVO arc) {
        servidor.setMatricula(arc.getMatriculaResponsavel());
        servidor.setNome(arc.getResponsavel());
        listaResponsavel.add(servidor);
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

    public Ciclo getCiclo() {
        return ciclo;
    }

    public List<ServidorVO> getListServidores() {
        return listServidores;
    }

    public Set<EstadoARCEnum> getListaEstado() {
        return listaEstado;
    }

    public void setListaEstado(Set<EstadoARCEnum> listaEstado) {
        this.listaEstado = listaEstado;
    }
    
    public void atualizarComboResponsavel(AjaxRequestTarget target, List<ARCDesignacaoVO> listaArcsDesignado) {
        this.listaArcsDesignado = listaArcsDesignado;
        preencherAtividadeGrupo();
        selectResponsavel.setChoices(new ArrayList<ServidorVO>(listaResponsavel));
        target.add(selectResponsavel);
    }

}
