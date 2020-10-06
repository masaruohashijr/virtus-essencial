package br.gov.bcb.sisaps.web.page.dominio.ciclo.painel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.gov.bcb.sisaps.src.dominio.Atividade;
import br.gov.bcb.sisaps.src.dominio.CelulaRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.Unidade;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoLinhaAtividadeVOEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoUnidadeAtividadeEnum;
import br.gov.bcb.sisaps.src.mediator.AtividadeMediator;
import br.gov.bcb.sisaps.src.mediator.CelulaRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.UnidadeMediator;
import br.gov.bcb.sisaps.src.vo.LinhaAtividadeVO;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.web.page.componentes.util.ConstantesImagens;
import br.gov.bcb.sisaps.web.page.dominio.matriz.PainelEdicaoLinhaMatriz;
import br.gov.bcb.sisaps.web.page.dominio.matriz.PainelMoverArcMatriz;
import br.gov.bcb.sisaps.web.page.dominio.unidade.PainelGerenciarUnidadeMatriz;

public class AcoesLinhaAtividadeVOPanel extends Panel {

    @SpringBean
    private CicloMediator cicloMediator;
    @SpringBean
    private AtividadeMediator atividadeMediator;
    @SpringBean
    private UnidadeMediator unidadeMediator;

    private Ciclo ciclo;
    private Matriz matriz;
    private ModalWindow modalEdicao;

    public AcoesLinhaAtividadeVOPanel(String id, final IModel<LinhaAtividadeVO> model, Matriz matriz) {
        super(id, model);
        this.matriz = matriz;
        this.ciclo = matriz.getCiclo();
        colunaAlterar(model);
        colunaExcluir(model);
        colunaMover(model);
        adicionarModal();
    }

    private void colunaAlterar(final IModel<LinhaAtividadeVO> model) {
        final boolean isAtividade = isAtividade(model);
        final boolean isUnidadeNegocio = isUnidadeNegocio(model);
        AjaxSubmitLink link = new AjaxSubmitLink("linkAlterar") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                submitAlterar(model, isAtividade, isUnidadeNegocio, target);
            }

            @Override
            public boolean isVisible() {
                return isAtividade || isUnidadeNegocio;
            }
        };
        link.add(new Image("alterar", ConstantesImagens.IMG_ATUALIZAR));

        String nomeUnico =
                model.getObject().getAtividade().getSigla() + model.getObject().getTipo().getDescricao()
                        + model.getObject().getPk().toString();
        link.setMarkupId("linkAlterar_" + SisapsUtil.criarMarkupId(nomeUnico));
        add(link);
    }

    private void colunaMover(final IModel<LinhaAtividadeVO> model) {

        AjaxSubmitLink link = new AjaxSubmitLink("linkMover") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {

                CelulaRiscoControle celula = CelulaRiscoControleMediator.get().buscar(model.getObject().getPk());
                modalEdicao.setAutoSize(false);
                modalEdicao.setInitialWidth(800);
                modalEdicao.setInitialHeight(190);
                modalEdicao.setContent(new PainelMoverArcMatriz(modalEdicao, matriz, atividadeMediator.loadPK(model
                        .getObject().getPkAtividade()), celula));

                modalEdicao.show(target);
            }

            @Override
            public boolean isVisible() {
                return isARC(model);
            }
        };
        link.add(new Image("mover", ConstantesImagens.IMG_PROXIMO));

        String nomeUnico =
                model.getObject().getNome() + model.getObject().getTipo().getDescricao()
                        + model.getObject().getPk().toString();
        link.setMarkupId("linkMover_" + SisapsUtil.criarMarkupId(nomeUnico));
        add(link);
    }

    private boolean isARC(final IModel<LinhaAtividadeVO> model) {
        return TipoLinhaAtividadeVOEnum.ARC.equals(model.getObject().getTipo());
    }

    private boolean isUnidadeNegocio(final IModel<LinhaAtividadeVO> model) {
        return TipoLinhaAtividadeVOEnum.UNIDADE.equals(model.getObject().getTipo())
                && TipoUnidadeAtividadeEnum.NEGOCIO.equals(model.getObject().getAtividade());
    }

    private boolean isAtividade(final IModel<LinhaAtividadeVO> model) {
        return TipoLinhaAtividadeVOEnum.ATIVIDADE.equals(model.getObject().getTipo());
    }

    private void submitAlterar(final IModel<LinhaAtividadeVO> model, final boolean isAtividade,
            final boolean isUnidadeNegocio, AjaxRequestTarget target) {
        final Ciclo cicloSelecionado = cicloMediator.loadPK(ciclo.getPk());
        if (isAtividade) {
            final Atividade atividade = atividadeMediator.loadPK(model.getObject().getPk());
            modalEdicao.setAutoSize(false);
            modalEdicao.setInitialWidth(800);
            modalEdicao.setInitialHeight(400);
            modalEdicao.setContent(new PainelEdicaoLinhaMatriz(modalEdicao, cicloSelecionado, atividade, false));
        } else if (isUnidadeNegocio) {
            final Unidade unidade = unidadeMediator.loadPK(model.getObject().getPk());
            modalEdicao.setAutoSize(false);
            modalEdicao.setInitialWidth(600);
            modalEdicao.setInitialHeight(180);
            modalEdicao.setContent(new PainelGerenciarUnidadeMatriz(modalEdicao, matriz, unidade, false));
        }
        modalEdicao.show(target);

    }

    private void colunaExcluir(final IModel<LinhaAtividadeVO> model) {
        final boolean isAtividade = isAtividade(model);
        final boolean isUnidadeNegocio = isUnidadeNegocio(model);
        AjaxSubmitLink link = new AjaxSubmitLink("linkExcluir") {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                modalEdicao.setAutoSize(true);
                modalEdicao.setInitialWidth(600);
                modalEdicao.setInitialHeight(200);
                modalEdicao.setContent(new PainelControleExcluirLinha(modalEdicao, model));
                modalEdicao.show(target);
            }

            @Override
            public boolean isVisible() {
                return podeExibirIconeExcluir(model);
            }
          
        };
        link.add(new Image("excluir", ConstantesImagens.IMG_EXCLUIR));
        String nomeUnico =
                model.getObject().getAtividade().getSigla() + model.getObject().getTipo().getDescricao()
                        + model.getObject().getPk().toString();
        link.setMarkupId("linkExcluir_" + SisapsUtil.criarMarkupId(nomeUnico));
        add(link);
    }
    
    private boolean podeExibirIconeExcluir(final IModel<LinhaAtividadeVO> model) {
        if (TipoLinhaAtividadeVOEnum.ATIVIDADE.equals(model.getObject().getTipo())) {
            return atividadeMediator.podeExcluirAtividade(atividadeMediator.loadPK(model.getObject().getPk()));
        } else if (TipoLinhaAtividadeVOEnum.UNIDADE.equals(model.getObject().getTipo())) {
            return unidadeMediator.podeExcluirUnidade(unidadeMediator.loadPK(model.getObject().getPk()));
        }
        return false;
    }

    private void adicionarModal() {
        modalEdicao = new ModalWindow("modalEdicaoLinhaMatriz");
        modalEdicao.setOutputMarkupId(true);
        modalEdicao.setResizable(false);
        add(modalEdicao);

    }

}