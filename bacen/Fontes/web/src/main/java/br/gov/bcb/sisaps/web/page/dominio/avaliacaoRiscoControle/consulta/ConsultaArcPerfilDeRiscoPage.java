/*
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software e confidencial e propriedade do Banco Central do Brasil.
 * Nao e permitida sua distribuicao ou divulgacao do seu conteudo sem
 * expressa autorizacao do Banco Central.
 * Este arquivo contem informacoes proprietarias.
 */

package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.consulta;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.time.Duration;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopyFields;

import br.gov.bcb.sisaps.integracao.seguranca.PerfisAcesso;
import br.gov.bcb.sisaps.src.dominio.Atividade;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.ParametroGrupoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.mediator.AtividadeMediator;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleExternoMediator;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.CelulaRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.LogOperacaoMediator;
import br.gov.bcb.sisaps.src.mediator.MatrizCicloMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.src.vo.ArcNotasVO;
import br.gov.bcb.sisaps.web.page.DefaultPage;
import br.gov.bcb.sisaps.web.page.componentes.autorizacao.anotacao.SisApsAuthorizeAction;
import br.gov.bcb.sisaps.web.page.componentes.util.ConstantesImagens;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.PainelInformacoesArc;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.analise.PainelResumoElementosArcs;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.detalhe.PainelDetalhesARC;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.detalhe.PainelElementosConsulta;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.detalhe.PainelTendenciaRiscoMercadoAnalise;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.edicao.PainelAnexoArc;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.relatorioArc.JuncaoPDFMap;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.relatorioArc.RelatorioArcMatriz;
import br.gov.bcb.sisaps.web.page.dominio.ciclo.painel.PainelResumoCiclo;

@SuppressWarnings("serial")
@SisApsAuthorizeAction(action = Action.RENDER, roles = {PerfisAcesso.CONSULTA_RESUMO_NAO_BLOQUEADOS,
        PerfisAcesso.CONSULTA_NAO_BLOQUEADOS, PerfisAcesso.CONSULTA_TUDO, PerfisAcesso.SUPERVISOR,
        PerfisAcesso.INSPETOR})
public class ConsultaArcPerfilDeRiscoPage extends DefaultPage {
    @SpringBean
    private MatrizCicloMediator matrizMediator;
    @SpringBean
    private AvaliacaoRiscoControleMediator avaliacaoRiscoControleMediator;

    private AvaliacaoRiscoControle avaliacao;
    private Matriz matriz;
    private Atividade atividade;
    private ParametroGrupoRiscoControle grupo;

    private final ArcNotasVO avaliacaoVigente;
    private final int pkMatriz;
    private final Integer pkAtividade;
    private PainelConsultaFiltroPerfilDeRiscoArc painelFiltro;
    private PainelResumoElementosArcs painelResumoElemento;
    private PainelDetalhesARC painelRiscoMercado;
    private PainelElementosConsulta painelElementos;
    private PainelTendenciaRiscoMercadoAnalise painelTendencia;
    private PainelAnexoArc painelAnexoArc;
    private PainelInformacoesArc painelInformacoesArc;
    private final AvaliacaoRiscoControle avaliacaoRascunho;
    private final boolean isPerfilRiscoAtual;

    public ConsultaArcPerfilDeRiscoPage(ArcNotasVO arcVO, Matriz matriz, Integer pkAtividade,
            boolean perfilAtual) {
        this(arcVO, matriz.getPk(), pkAtividade, perfilAtual);
    }

    public ConsultaArcPerfilDeRiscoPage(ArcNotasVO arcVO, Integer pkMatriz, Integer pkAtividade, 
            boolean perfilAtual) {
        this.isPerfilRiscoAtual = perfilAtual;
        this.avaliacaoRascunho = avaliacaoRiscoControleMediator.buscarPorPk(arcVO.getPk());
        this.avaliacaoVigente = avaliacaoRiscoControleMediator.consultarNotasArc(arcVO.getArcVigentePk());
        this.pkAtividade = pkAtividade;
        this.pkMatriz = pkMatriz;
    }

    @Override
    protected void onConfigure() {
        matriz = matrizMediator.loadPK(pkMatriz);

        avaliacao = avaliacaoRiscoControleMediator.loadPK(avaliacaoVigente.getPk());
        PerfilRisco perfilRiscoAtual = PerfilRiscoMediator.get().obterPerfilRiscoAtual(matriz.getCiclo().getPk());
        LogOperacaoMediator.get().gerarLogDetalhamento(matriz.getCiclo().getEntidadeSupervisionavel(),
                perfilRiscoAtual, atualizarDadosPagina(getPaginaAtual()));
        if (pkAtividade == null) {
            grupo = AvaliacaoRiscoControleExternoMediator.get().buscarArcExterno(avaliacao.getPk())
                    .getParametroGrupoRiscoControle();
        } else {
            atividade = AtividadeMediator.get().loadPK(pkAtividade);
            grupo = CelulaRiscoControleMediator.get().buscarCelularPorAvaliacao(avaliacao)
                    .getParametroGrupoRiscoControle();
        }
        addOrReplaceComponentes();
    }

    private void addOrReplaceComponentes() {
        Form<AvaliacaoRiscoControle> form =
                new Form<AvaliacaoRiscoControle>("formulario", new CompoundPropertyModel<AvaliacaoRiscoControle>(
                        avaliacao));
        painelFiltro = new PainelConsultaFiltroPerfilDeRiscoArc("filtroVersaoPanel", avaliacao, matriz.getCiclo());
        form.addOrReplace(painelFiltro);
        form.addOrReplace(new PainelResumoCiclo("idPainelCicloResumo", matriz.getCiclo().getPk()));
        painelInformacoesArc = 
                new PainelInformacoesArc("idPainelInformacoesArc", avaliacao, matriz.getCiclo(), atividade, grupo);
        form.addOrReplace(painelInformacoesArc);
        painelResumoElemento =
                new PainelResumoElementosArcs("idPainelResumoElementosArc", avaliacao, matriz, avaliacaoRascunho, 
                        isPerfilRiscoAtual);
        form.addOrReplace(painelResumoElemento);
        painelRiscoMercado =
                new PainelDetalhesARC("idPainelRiscoMercado", grupo, avaliacao, matriz.getCiclo(),
                        avaliacaoRascunho, isPerfilRiscoAtual);
        form.addOrReplace(painelRiscoMercado);
        painelElementos = new PainelElementosConsulta("idPainelElementos", avaliacao, matriz.getCiclo());
        form.addOrReplace(painelElementos);
        painelTendencia =
                new PainelTendenciaRiscoMercadoAnalise("idPainelTendenciaRiscoMercado", grupo, avaliacao,
                        matriz.getCiclo(), true, true, true, new ArrayList<String>());
        form.addOrReplace(painelTendencia);
        painelAnexoArc = new PainelAnexoArc("idPainelAnexoArc", matriz.getCiclo(), avaliacao, false, true);
        form.addOrReplace(painelAnexoArc);
        form.addOrReplace(new LinkVoltar());
        addLinkImprimirArc(form);
        addOrReplace(form);
    }

    private void addLinkImprimirArc(Form<AvaliacaoRiscoControle> form) {

        DownloadLink test2 = new DownloadLink("linkImprimirArc", new AbstractReadOnlyModel<File>() {
            private static final long serialVersionUID = 1L;

            @Override
            public File getObject() {
                return addAnexoImpressaoArc();
            }

        }).setCacheDuration(Duration.NONE);

        test2.add(new Image("imprimir", ConstantesImagens.IMG_IMPRESSORA));

        form.addOrReplace(test2);
    }

    private File addAnexoImpressaoArc() {
        File tempFile;
        PdfCopyFields copy;
        BufferedOutputStream bos = null;
        try {
            tempFile = File.createTempFile(RelatorioArcMatriz.NOME_ARQUIVO_RELATORIO, ".pdf");
            RelatorioArcMatriz relatorio =
                    new RelatorioArcMatriz(avaliacao, avaliacaoRascunho, matriz.getCiclo(), atividade,
                            getPerfilPorPagina(), isPerfilRiscoAtual);
            bos = new BufferedOutputStream(new FileOutputStream(tempFile)); //Criamos o arquivo  
            copy = new PdfCopyFields(bos);
            copy.addDocument(JuncaoPDFMap.gerarPDF(relatorio.gerarRelatorioPDF(getPaginaAtual()),
                    relatorio.getMapAnexoElemento()));

            copy.close();
            bos.flush();
            getPage().getResponse().close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return tempFile;
    }

    public void atualizarPagina(AjaxRequestTarget target) {
        atualizarPaineis(target);
    }

    private void atualizarPaineis(AjaxRequestTarget target) {
        avaliacao =
                avaliacaoRiscoControleMediator.buscarPorPk(painelFiltro.getAvaliacaoRiscoControleSelecionado().getPk());
        painelInformacoesArc.setAvaliacao(avaliacao);
        target.add(painelInformacoesArc);
        painelResumoElemento.setArc(avaliacao);
        target.add(painelResumoElemento);
        painelRiscoMercado.setAvaliacao(avaliacao);
        target.add(painelRiscoMercado);
        painelElementos.setArc(avaliacao);
        target.add(painelElementos);
        painelTendencia.setAvaliacao(avaliacao);
        target.add(painelTendencia);
        painelAnexoArc.setAvaliacao(avaliacao);
        target.add(painelAnexoArc);
    }

    @Override
    public String getTitulo() {
        return "Consulta de ARCs";
    }

    @Override
    public String getCodigoTela() {
        return "APSFW0402";
    }

}
