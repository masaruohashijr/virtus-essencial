package br.gov.bcb.sisaps.web.page.dominio.analisequantitativaaqt;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.util.time.Duration;

import br.gov.bcb.sisaps.integracao.seguranca.PerfisAcesso;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.mediator.LogOperacaoMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.web.page.DefaultPage;
import br.gov.bcb.sisaps.web.page.componentes.autorizacao.anotacao.SisApsAuthorizeAction;
import br.gov.bcb.sisaps.web.page.componentes.util.ConstantesImagens;
import br.gov.bcb.sisaps.web.page.dominio.aqt.relatorio.RelatorioAQTMatriz;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.relatorioArc.JuncaoPDFMap;
import br.gov.bcb.sisaps.web.page.dominio.ciclo.painel.PainelResumoCiclo;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopyFields;

@SuppressWarnings("serial")
@SisApsAuthorizeAction(action = Action.RENDER, roles = {PerfisAcesso.CONSULTA_RESUMO_NAO_BLOQUEADOS,
        PerfisAcesso.CONSULTA_NAO_BLOQUEADOS, PerfisAcesso.CONSULTA_TUDO, PerfisAcesso.SUPERVISOR,
        PerfisAcesso.INSPETOR})
public class ConsultaAQT extends DefaultPage {

    private final Form<AnaliseQuantitativaAQT> form = new Form<AnaliseQuantitativaAQT>("formulario");
    private AnaliseQuantitativaAQT aqt;
    private PainelConsultaFiltroPerfilDeRiscoAnef painelFiltro;
    private PainelAnexosAnef painelAnexos;
    private PainelInformacoesAQT painelInformacoes;
    private PainelResumoElementosAnefs painelResumoAnefs;
    private PainelElementosConsultaAQT painelElementosConsulta;
    private PainelDetalhesAQT painelDetalhesAqt;
    private Boolean isPerfilRiscoAtual;

    public ConsultaAQT(AnaliseQuantitativaAQT aqt, boolean isPerfilRiscoAtual) {
        this.aqt = AnaliseQuantitativaAQTMediator.get().buscar(aqt.getPk());
        this.isPerfilRiscoAtual = isPerfilRiscoAtual;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        PerfilRisco perfilRiscoAtual = PerfilRiscoMediator.get().obterPerfilRiscoAtual(aqt.getCiclo().getPk());

        LogOperacaoMediator.get().gerarLogDetalhamento(aqt.getCiclo().getEntidadeSupervisionavel(), perfilRiscoAtual,
                atualizarDadosPagina(getPaginaAtual()));
        addPainelFiltroVersao();
        form.addOrReplace(new PainelResumoCiclo("idPainelCicloResumo", aqt.getCiclo().getPk()));
        addPainelInformacoesAQT();
        addPainelResumoAQT();

        addPainelDetalhesAqt();

        addPainelElementosConsulta();

        addPainelAnexosAnef();

        addLinkImprimirArc(form);

        form.addOrReplace(new LinkVoltar());

        addOrReplace(form);

    }

    private void addPainelDetalhesAqt() {
        painelDetalhesAqt = new PainelDetalhesAQT("idPainelDetalhesAQT", aqt, isPerfilRiscoAtual);
        form.addOrReplace(painelDetalhesAqt);

    }

    private void addPainelElementosConsulta() {
        painelElementosConsulta = new PainelElementosConsultaAQT("idPainelElementosConsulta", aqt);
        form.addOrReplace(painelElementosConsulta);

    }

    private void addPainelResumoAQT() {
        painelResumoAnefs = new PainelResumoElementosAnefs("idPainelResumoElementosAQT", aqt, isPerfilRiscoAtual);
        form.addOrReplace(painelResumoAnefs);

    }

    private void addLinkImprimirArc(Form<AnaliseQuantitativaAQT> form) {

        DownloadLink test2 = new DownloadLink("linkImprimirAnef", new AbstractReadOnlyModel<File>() {
            private static final long serialVersionUID = 1L;

            @Override
            public File getObject() {
                return addAnexoImpressaoAqt();
            }

        }).setCacheDuration(Duration.NONE);

        test2.add(new Image("imprimir", ConstantesImagens.IMG_IMPRESSORA));

        form.addOrReplace(test2);
    }

    private void addPainelInformacoesAQT() {
        painelInformacoes = new PainelInformacoesAQT("idPainelInformacoesAnef", aqt);
        painelInformacoes.setOutputMarkupId(true);
        form.addOrReplace(painelInformacoes);

    }

    private void addPainelAnexosAnef() {
        painelAnexos = new PainelAnexosAnef("idPainelAnexoAnef", aqt.getCiclo(), aqt, false, true);
        painelAnexos.setOutputMarkupId(true);
        painelAnexos.setOutputMarkupPlaceholderTag(true);
        form.addOrReplace(painelAnexos);
    }

    private void addPainelFiltroVersao() {
        painelFiltro = new PainelConsultaFiltroPerfilDeRiscoAnef("filtroVersaoPanel", aqt);
        painelFiltro.setOutputMarkupId(true);
        form.addOrReplace(painelFiltro);
    }

    private File addAnexoImpressaoAqt() {
        File tempFile;
        PdfCopyFields copy;
        BufferedOutputStream bos = null;
        try {
            tempFile = File.createTempFile(RelatorioAQTMatriz.NOME_ARQUIVO_RELATORIO, ".pdf");
            RelatorioAQTMatriz relatorio =
                    new RelatorioAQTMatriz(aqt, aqt.getCiclo(), "1.", getPerfilPorPagina(), isPerfilRiscoAtual);
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

    @Override
    public String getCodigoTela() {
        return "APSFW0403";
    }

    @Override
    public String getTitulo() {
        return "Consulta de ANEF";
    }

    public AnaliseQuantitativaAQT getAqt() {
        return aqt;
    }

    public void atualizarPagina(AjaxRequestTarget target) {
        atualizarPaineis(target);
    }

    private void atualizarPaineis(AjaxRequestTarget target) {
        aqt = AnaliseQuantitativaAQTMediator.get().buscar(painelFiltro.getAnaliseQuantitativaAQTSelecionado().getPk());
        painelInformacoes.setAnaliseQuantitativaAQT(aqt);
        target.add(painelInformacoes);
        painelResumoAnefs.setAqt(aqt);
        target.add(painelResumoAnefs);
        painelDetalhesAqt.setAqt(aqt);
        target.add(painelDetalhesAqt);
        painelElementosConsulta.setAqt(aqt);
        target.add(painelElementosConsulta);
        painelAnexos.setAvaliacao(aqt);
        target.add(painelAnexos);
    }

    public PainelResumoElementosAnefs getPainelResumoAnefs() {
        return painelResumoAnefs;
    }

    public void setPainelResumoAnefs(PainelResumoElementosAnefs painelResumoAnefs) {
        this.painelResumoAnefs = painelResumoAnefs;
    }

}
