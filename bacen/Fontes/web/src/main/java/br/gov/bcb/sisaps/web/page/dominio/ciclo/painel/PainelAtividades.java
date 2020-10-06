package br.gov.bcb.sisaps.web.page.dominio.ciclo.painel;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoLinhaAtividadeVOEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoUnidadeAtividadeEnum;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.LinhaAtividadeVOMediator;
import br.gov.bcb.sisaps.src.mediator.MatrizCicloMediator;
import br.gov.bcb.sisaps.src.vo.LinhaAtividadeVO;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.dominio.matriz.PainelEdicaoLinhaMatriz;
import br.gov.bcb.sisaps.web.page.dominio.unidade.PainelGerenciarUnidadeMatriz;

@SuppressWarnings("serial")
public class PainelAtividades extends PainelSisAps {
    private static final String ESPACO = " ";
    private static final String CLASS = "class";
    private ModalWindow modalAtividades;
    private ModalWindow modalUnidade;
    @SpringBean
    private LinhaAtividadeVOMediator linhaAtividadeVOMediator;
    private List<LinhaAtividadeVO> listaLinhaAtividade;
    private Matriz matriz;

    public PainelAtividades(String id, Matriz matriz) {
        super(id);
        this.matriz = matriz;
        addModal();
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        this.matriz = MatrizCicloMediator.get().loadPK(matriz.getPk());
        listaLinhaAtividade = new ArrayList<LinhaAtividadeVO>();
        listaLinhaAtividade.addAll(linhaAtividadeVOMediator.consultarLinhasAtividadeVOUnidades(matriz,
                TipoUnidadeAtividadeEnum.NEGOCIO));
        listaLinhaAtividade.addAll(linhaAtividadeVOMediator.consultarLinhasAtividadeVOSemUnidades(matriz));
        listaLinhaAtividade.addAll(linhaAtividadeVOMediator.consultarLinhasAtividadeVOUnidades(matriz,
                TipoUnidadeAtividadeEnum.CORPORATIVO));
        addCamposNovasUnidadesEAtividades();
        addListviewLinhasAtividades();
    }

    private void addListviewLinhasAtividades() {

        ListView<LinhaAtividadeVO> listviewAtividades =
                new ListView<LinhaAtividadeVO>("listaDeAtividades", listaLinhaAtividade) {

                    @Override
                    protected void populateItem(ListItem<LinhaAtividadeVO> item) {
                        item.add(new Label("nome", item.getModelObject().nomeFormatadoVigente()).setEscapeModelStrings(
                                false).add(new AttributeAppender(CLASS, obterClassCelula(item.getModel()), ESPACO)));

                        item.add(new Label("tipo", item.getModelObject().getNomeParametroTipoAtividade()));
                        item.add(new Label("peso", item.getModelObject().getParametroPeso().getDescricao())
                                .add(new AttributeAppender(CLASS, obterCorPeso(item.getModelObject().getCorCelula()),
                                        ESPACO)));

                        item.add(new AcoesLinhaAtividadeVOPanel("painelAcao", item.getModel(), matriz));
                    }
                };
        addOrReplace(listviewAtividades);

    }

    private IModel<String> obterCorPeso(final String cor) {
        IModel<String> model = new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                return cor;
            }
        };
        return model;
    }

    private IModel<?> obterClassCelula(final IModel<LinhaAtividadeVO> linha) {

        IModel<String> model = new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {

                if (linha.getObject().isFilho()) {
                    if (TipoLinhaAtividadeVOEnum.ARC.equals(linha.getObject().getTipo())) {
                        return "nomeArc";
                    }
                    return "nomeAtividade";
                } else {
                    return "nomeUnidade";
                }

            }
        };
        return model;

    }

    private void addModal() {
        modalAtividades = new ModalWindow("modalAtividades");
        modalAtividades.setResizable(false);
        modalAtividades.setInitialWidth(800);
        modalAtividades.setInitialHeight(400);
        modalAtividades.setOutputMarkupId(true);
        addOrReplace(modalAtividades);
        modalUnidade = new ModalWindow("modalUnidade");
        modalUnidade.setOutputMarkupId(true);
        modalUnidade.setResizable(false);
        modalUnidade.setInitialWidth(600);
        modalUnidade.setInitialHeight(180);
        addOrReplace(modalUnidade);

    }

    private void addCamposNovasUnidadesEAtividades() {
        AjaxSubmitLink linkIncluirUnidade = new AjaxSubmitLink("linkIncluirUnidade") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                modalUnidade.setContent(new PainelGerenciarUnidadeMatriz(modalUnidade, matriz, null, true));
                modalUnidade.setOutputMarkupId(true);
                modalUnidade.show(target);
            }
        };
        linkIncluirUnidade.setBody(new PropertyModel<String>("Nova unidade", ""));
        linkIncluirUnidade.setMarkupId(linkIncluirUnidade.getId());
        linkIncluirUnidade.setOutputMarkupId(true);
        addOrReplace(linkIncluirUnidade);

        AjaxSubmitLink linkIncluirAtividade = new AjaxSubmitLink("linkIncluirAtividade") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {

                modalAtividades.setContent(new PainelEdicaoLinhaMatriz(modalAtividades, CicloMediator.get().load(
                        matriz.getCiclo()), null, true));
                modalAtividades.setOutputMarkupId(true);
                modalAtividades.show(target);
            }
        };
        linkIncluirAtividade.setBody(new PropertyModel<String>("Nova Atividade", ""));
        linkIncluirAtividade.setMarkupId(linkIncluirAtividade.getId());
        linkIncluirAtividade.setOutputMarkupId(true);
        addOrReplace(linkIncluirAtividade);

    }

}
