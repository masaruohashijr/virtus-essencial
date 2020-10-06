package br.gov.bcb.sisaps.web.page;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoARCEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TituloTelaARCEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TituloTelaAnefEnum;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.MatrizCicloMediator;
import br.gov.bcb.sisaps.src.vo.ArcNotasVO;
import br.gov.bcb.sisaps.src.vo.ArcResumidoVO;
import br.gov.bcb.sisaps.src.vo.AvaliacaoRiscoControleVO;
import br.gov.bcb.sisaps.web.page.componentes.botoes.CustomBotaoExpandirColapsar;
import br.gov.bcb.sisaps.web.page.componentes.grupo.GrupoExpansivel;
import br.gov.bcb.sisaps.web.page.componentes.util.ConstantesWeb;
import br.gov.bcb.sisaps.web.page.dominio.analisequantitativaaqt.AnaliseAQT;
import br.gov.bcb.sisaps.web.page.dominio.analisequantitativaaqt.ConsultaAQT;
import br.gov.bcb.sisaps.web.page.dominio.analisequantitativaaqt.DetalharAQT;
import br.gov.bcb.sisaps.web.page.dominio.analisequantitativaaqt.EdicaoAQT;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.analise.AnalisarArcPage;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.consulta.ConsultaArcPerfilDeRiscoPage;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.detalhe.DetalharArcAnalisado;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.detalhe.DetalharArcAnalise;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.detalhe.DetalharArcConcluido;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.detalhe.DetalharArcDelegado;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.detalhe.DetalharArcEdicao;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.detalhe.DetalharArcPreenchido;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.detalhe.DetalharArcPrevistoDesignado;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.edicao.EdicaoArcPage;

@SuppressWarnings("serial")
public class PainelSisAps extends Panel {
    protected ModalWindow modalEdicao;

    public PainelSisAps(String id) {
        super(id);
    }

    public PainelSisAps(String id, IModel<?> model) {
        super(id, model);
    }

    protected PerfilAcessoEnum getPerfilPorPagina() {
        return ((ISisApsPage) super.getPage()).getPerfilPorPagina();
    }

    public DefaultPage getPaginaAtual() {
        return (DefaultPage) super.getPage();
    }

    /**
     * Link que por padrão retorna para a página anterior da página atual.
     */
    @SuppressWarnings("rawtypes")
    protected class LinkVoltar extends Link {
        public LinkVoltar() {
            super(ConstantesWeb.WID_BOTAO_VOLTAR);
        }

        public LinkVoltar(String id) {
            super(id);
        }

        @Override
        public void onClick() {
            if (getPaginaAtual().getPaginaAnterior() == null) {
                super.setResponsePage(HomePage.class);
            } else {
                super.setResponsePage(getPaginaAtual().getPaginaAnterior());
            }

        }
    }

    protected static class BotaoExpandirColapsar extends CustomBotaoExpandirColapsar {
        private final GrupoExpansivel[] grupos;

        public BotaoExpandirColapsar(GrupoExpansivel... grupos) {
            super(ConstantesWeb.WID_BOTOES_EXPANDIR_COLAPSAR);
            this.grupos = grupos;
        }

        @Override
        public void expandirColapsarGrupos(AjaxRequestTarget target) {

            for (GrupoExpansivel grupo : grupos) {
                grupo.getControle().expandirColapsar(target);
            }
        }
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
    }

    public void adicionarModal() {
        modalEdicao = new ModalWindow("modalEdicao");
        add(modalEdicao);
        modalEdicao.setResizable(false);
        modalEdicao.setAutoSize(true);
    }

    public String[] atualizarDadosPagina(DefaultPage paginaAtual) {
        String[] dados = new String[3];
        dados[0] = paginaAtual.getTrilha(paginaAtual);
        dados[1] = paginaAtual.getCodigoTela();
        dados[2] = paginaAtual.getTitulo();
        return dados;
    }

    public boolean perfilSupervisor(DefaultPage paginaAtual) {
        return paginaAtual.perfilSupervisor(paginaAtual);
    }

    public boolean perfilConsulta(DefaultPage paginaAtual) {
        return paginaAtual.perfilConsulta(paginaAtual);
    }

    public boolean perfilConsultaResumido(DefaultPage paginaAtual) {
        return paginaAtual.perfilConsultaResumido(paginaAtual);
    }

    public boolean perfilInspetor(DefaultPage paginaAtual) {
        return paginaAtual.perfilInspetor(paginaAtual);
    }

    public boolean perfilGerente(DefaultPage paginaAtual) {
        return paginaAtual.perfilGerente(paginaAtual);
    }
    
    public void instanciarPaginaDestino(AnaliseQuantitativaAQT analiseQuantitativaAQT,
            TituloTelaAnefEnum tituloTelaAnefEnum, boolean isPerfilRiscoAtual) {
        if (TituloTelaAnefEnum.PAGINA_DETALHE.equals(tituloTelaAnefEnum)) {
            getPaginaAtual().avancarParaNovaPagina(new DetalharAQT(analiseQuantitativaAQT, isPerfilRiscoAtual, false));
        } else if (TituloTelaAnefEnum.PAGINA_CONSULTA.equals(tituloTelaAnefEnum)) {
            getPaginaAtual().avancarParaNovaPagina(new ConsultaAQT(analiseQuantitativaAQT, isPerfilRiscoAtual));
        } else if (TituloTelaAnefEnum.PAGINA_EDICAO.equals(tituloTelaAnefEnum)) {
            getPaginaAtual().avancarParaNovaPagina(new EdicaoAQT(analiseQuantitativaAQT));
        } else if (TituloTelaAnefEnum.PAGINA_ANALISE.equals(tituloTelaAnefEnum)) {
            getPaginaAtual().avancarParaNovaPagina(new AnaliseAQT(analiseQuantitativaAQT));
        }
    }

    public void instanciarPaginaDestinoARC(final ArcNotasVO avaliacaoRiscoControle, Matriz matriz,
            Integer pkAtividade, TituloTelaARCEnum tituloTelaARCEnum, boolean perfilAtual) {
        avancarParaNovaPagina(tituloTelaARCEnum, perfilAtual, avaliacaoRiscoControle,
                MatrizCicloMediator.get().buscar(matriz.getPk()), pkAtividade);
    }

    public void instanciarPaginaDestinoARCResumido(final IModel<ArcResumidoVO> rowModel,
            TituloTelaARCEnum tituloTelaARCEnum, boolean perfilAtual) {
        ArcNotasVO avaliacaoRiscoControle =
                AvaliacaoRiscoControleMediator.get().consultarNotasArc(rowModel.getObject().getPk());
        Matriz matriz = MatrizCicloMediator.get().buscar(rowModel.getObject().getPkMatriz());
        avancarParaNovaPagina(tituloTelaARCEnum, perfilAtual, avaliacaoRiscoControle, matriz,
                rowModel.getObject().getPkAtividade());
    }

    private void avancarParaNovaPagina(TituloTelaARCEnum tituloTelaARCEnum, boolean perfilAtual, ArcNotasVO arcVO,
            Matriz matriz, Integer atividadePk) {

        if (CicloMediator.get().cicloCorec(matriz.getCiclo())
                && !TituloTelaARCEnum.PAGINA_CONSULTA.equals(tituloTelaARCEnum)) {
            getPaginaAtual().avancarParaNovaPagina(new DetalharArcConcluido(
                    AvaliacaoRiscoControleMediator.get().buscar(arcVO.getPk()), matriz, atividadePk, perfilAtual));
        } else {
            if (TituloTelaARCEnum.PAGINA_CONSULTA.equals(tituloTelaARCEnum)) {
                getPaginaAtual().avancarParaNovaPagina(
                        new ConsultaArcPerfilDeRiscoPage(arcVO, matriz, atividadePk, perfilAtual));
            } else if (TituloTelaARCEnum.PAGINA_DETALHE_ANALISADO.equals(tituloTelaARCEnum)) {
                getPaginaAtual().avancarParaNovaPagina(new DetalharArcAnalisado(
                        AvaliacaoRiscoControleMediator.get().buscar(arcVO.getPk()), matriz, atividadePk, perfilAtual));
            } else if (TituloTelaARCEnum.PAGINA_DETALHE_CONCLUIDO.equals(tituloTelaARCEnum)) {
                getPaginaAtual().avancarParaNovaPagina(new DetalharArcConcluido(
                        AvaliacaoRiscoControleMediator.get().buscar(arcVO.getPk()), matriz, atividadePk, perfilAtual));
            } else if (TituloTelaARCEnum.PAGINA_DETALHE_DELEGADO.equals(tituloTelaARCEnum)) {
                getPaginaAtual().avancarParaNovaPagina(new DetalharArcDelegado(
                        AvaliacaoRiscoControleMediator.get().buscar(arcVO.getPk()), matriz, atividadePk, perfilAtual));
            } else if (TituloTelaARCEnum.PAGINA_DETALHE_EDICAO.equals(tituloTelaARCEnum)) {
                getPaginaAtual().avancarParaNovaPagina(new DetalharArcEdicao(
                        AvaliacaoRiscoControleMediator.get().buscar(arcVO.getPk()), matriz, atividadePk, perfilAtual));
            } else if (TituloTelaARCEnum.PAGINA_DETALHE_ANALISE.equals(tituloTelaARCEnum)) {
                AvaliacaoRiscoControle arc = AvaliacaoRiscoControleMediator.get().loadPK(arcVO.getPk());
                getPaginaAtual().avancarParaNovaPagina(new DetalharArcAnalise(arc, matriz, atividadePk, perfilAtual));
            } else if (TituloTelaARCEnum.PAGINA_DETALHE_PREVISTO_DESIGNADO_BRANCO.equals(tituloTelaARCEnum)) {
                AvaliacaoRiscoControle avaliacaoEmBranco =
                        AvaliacaoRiscoControleMediator.get().inicializarArcEmBranco(arcVO.getPk(), arcVO.getEstado());
                avaliacaoEmBranco.setPk(arcVO.getPk());
                getPaginaAtual().avancarParaNovaPagina(
                        new DetalharArcPrevistoDesignado(avaliacaoEmBranco, matriz, atividadePk, "", perfilAtual));
            } else if (TituloTelaARCEnum.PAGINA_DETALHE_PREVISTO_DESIGNADO.equals(tituloTelaARCEnum)) {
                getPaginaAtual().avancarParaNovaPagina(
                        new DetalharArcPrevistoDesignado(AvaliacaoRiscoControleMediator.get().buscar(arcVO.getPk()),
                                matriz, atividadePk, "", perfilAtual));
            } else if (TituloTelaARCEnum.PAGINA_DETALHE_PREVISTO_BRANCO.equals(tituloTelaARCEnum)) {
                AvaliacaoRiscoControle avaliacaoEmBranco = AvaliacaoRiscoControleMediator.get()
                        .inicializarArcEmBranco(arcVO.getPk(), EstadoARCEnum.PREVISTO);
                getPaginaAtual().avancarParaNovaPagina(
                        new DetalharArcPrevistoDesignado(avaliacaoEmBranco, matriz, atividadePk, "", perfilAtual));
            } else if (TituloTelaARCEnum.PAGINA_EDICAO.equals(tituloTelaARCEnum)) {
                getPaginaAtual().avancarParaNovaPagina(
                        new EdicaoArcPage(arcVO.getPk(), matriz.getCiclo().getPk(), atividadePk));
            } else if (TituloTelaARCEnum.PAGINA_ANALISE.equals(tituloTelaARCEnum)) {
                getPaginaAtual().avancarParaNovaPagina(new AnalisarArcPage(arcVO.getPk(), matriz.getPk(), atividadePk));
            } else if (TituloTelaARCEnum.PAGINA_DETALHE_PREENCHIDO.equals(tituloTelaARCEnum)) {
                getPaginaAtual().avancarParaNovaPagina(new DetalharArcPreenchido(
                        AvaliacaoRiscoControleMediator.get().buscar(arcVO.getPk()), matriz, atividadePk, perfilAtual));
            }
        }
    }

    public void instanciarPaginaMeuHistorico(final IModel<AvaliacaoRiscoControleVO> rowModel,
            TituloTelaARCEnum tituloTelaARCEnum) {

        AvaliacaoRiscoControle avaliacaoRiscoControle =
                AvaliacaoRiscoControleMediator.get().buscar(rowModel.getObject().getPk());
        Matriz matriz = MatrizCicloMediator.get().buscar(rowModel.getObject().getMatrizVigente().getPk());
        Integer atividadePk = rowModel.getObject().getAtividade() == null ? 
                null : rowModel.getObject().getAtividade().getPk();
        avancarParaNovaPaginaMeuHistorico(tituloTelaARCEnum, true, avaliacaoRiscoControle, matriz, atividadePk);
    }

    private void avancarParaNovaPaginaMeuHistorico(TituloTelaARCEnum tituloTelaARCEnum, boolean perfilAtual,
            AvaliacaoRiscoControle avaliacaoRiscoControle, Matriz matriz, Integer atividadePk) {

        if (TituloTelaARCEnum.PAGINA_DETALHE_DELEGADO.equals(tituloTelaARCEnum)) {
            getPaginaAtual().avancarParaNovaPagina(
                    new DetalharArcDelegado(avaliacaoRiscoControle, matriz, atividadePk, perfilAtual));
        } else if (TituloTelaARCEnum.PAGINA_DETALHE_ANALISE.equals(tituloTelaARCEnum)) {
            getPaginaAtual().avancarParaNovaPagina(
                    new DetalharArcAnalise(avaliacaoRiscoControle, matriz, atividadePk, perfilAtual));
        } else if (TituloTelaARCEnum.PAGINA_DETALHE_CONCLUIDO.equals(tituloTelaARCEnum)) {
            getPaginaAtual().avancarParaNovaPagina(
                    new DetalharArcConcluido(avaliacaoRiscoControle, matriz, atividadePk, perfilAtual));
        } else if (TituloTelaARCEnum.PAGINA_DETALHE_ANALISADO.equals(tituloTelaARCEnum)) {
            getPaginaAtual().avancarParaNovaPagina(
                    new DetalharArcAnalisado(avaliacaoRiscoControle, matriz, atividadePk, perfilAtual));
        } else if (TituloTelaARCEnum.PAGINA_DETALHE_PREENCHIDO.equals(tituloTelaARCEnum)) {
            getPaginaAtual().avancarParaNovaPagina(
                    new DetalharArcPreenchido(avaliacaoRiscoControle, matriz, atividadePk, perfilAtual));
        }

    }

}
