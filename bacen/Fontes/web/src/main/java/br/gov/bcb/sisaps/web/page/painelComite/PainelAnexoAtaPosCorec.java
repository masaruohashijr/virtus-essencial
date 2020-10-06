package br.gov.bcb.sisaps.web.page.painelComite;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.http.entity.ContentType;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.handler.resource.ResourceRequestHandler;
import org.apache.wicket.request.resource.ByteArrayResource;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.gov.bcb.sisaps.src.dominio.AgendaCorec;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.mediator.AgendaCorecMediator;
import br.gov.bcb.sisaps.src.mediator.AnexoPosCorecMediator;
import br.gov.bcb.sisaps.src.mediator.ParticipanteAgendaCorecMediator;
import br.gov.bcb.sisaps.src.validacao.RegraAnexosValidacaoPDFA4;
import br.gov.bcb.sisaps.src.vo.AnexoPosCorecVO;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.botoes.AjaxSubmitLinkSisAps;
import br.gov.bcb.sisaps.web.page.componentes.relatorios.GerarRelatorioAtaCorecLink;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.edicao.AjaxBotaoAnexarArquivo;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.edicao.TabelaAnexoComum;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.edicao.TabelaAnexoPosCorec;

public class PainelAnexoAtaPosCorec extends PainelSisAps {
    private static final String BOTAO_ANEXAR_AQUIVO_ATA = "botaoAnexarAquivoAta";
    private static final String ATA = "Ata";
    private AjaxSubmitLinkSisAps botaoExcluir;
    private final Ciclo ciclo;
    @SpringBean
    private AnexoPosCorecMediator anexoPosCorecMediator;
    private List<FileUpload> filesUpload;
    private TabelaAnexoComum<?> tabelaAnexo;
    private WebMarkupContainer edicao;
    private WebMarkupContainer lista;
    private final AgendaCorec agenda;
    private boolean existeParticipante;
    private AnexoPosCorecVO anexo;
    private final WebMarkupContainer wmcExibirAta = new WebMarkupContainer("wmcExibirAta");
    private final WebMarkupContainer wmcExibirAnexo = new WebMarkupContainer("wmcExibirAnexo");

    public PainelAnexoAtaPosCorec(String id, Ciclo ciclo) {
        super(id);
        setOutputMarkupId(true);
        this.ciclo = ciclo;
        this.agenda = AgendaCorecMediator.get().buscarAgendaCorecPorCiclo(ciclo.getPk());

    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        addComponents();
    }

    private void addComponents() {
        addComponentes();
        addBotaoSalvarInformacoes();
    }

    private void addComponentes() {
        if (agenda != null) {
            existeParticipante =
                    CollectionUtils.isNotEmpty(ParticipanteAgendaCorecMediator.get().buscarParticipanteAgendaCorec(
                            agenda.getPk()));
        }
        edicao = new WebMarkupContainer("idEdicaoAta");
        edicao.setVisible(isPossivelAnexarAta() && !anexoPosCorecMediator.existeAnexo(ciclo, ATA));
        edicao.setMarkupId(edicao.getId());
        edicao.setOutputMarkupId(true);
        edicao.setOutputMarkupPlaceholderTag(true);
        lista = new WebMarkupContainer("idListaAta");
        lista.setVisible(isPossivelAnexarAta() && anexoPosCorecMediator.existeAnexo(ciclo, ATA));
        lista.setMarkupId(lista.getId());
        lista.setOutputMarkupId(true);
        lista.setOutputMarkupPlaceholderTag(true);
        addBotaoAnexar();
        addOrReplace(edicao);
        addOrReplace(lista);
        addListaAnexo();
        addLinkAta();
        addAnexo();
        addOrReplace(wmcExibirAta);
        addOrReplace(wmcExibirAnexo);
    }

    private void addAnexo() {
        wmcExibirAnexo.addOrReplace(new TabelaAnexoPosCorec("idTabelaAta", ciclo, ATA, false, false));
        wmcExibirAnexo.setVisibilityAllowed(!isPossivelAnexarAta() && anexoPosCorecMediator.existeAnexo(ciclo, ATA));
    }

    private boolean isPossivelAnexarAta() {
        return AnexoPosCorecMediator.get().isPossivelAnexarAta(ciclo);
    }

    private void addBotaoAnexar() {
        final FileUploadField fileUploadFieldArquivo =
                new FileUploadField("idFieldUploadAnexoAta", new PropertyModel<List<FileUpload>>(this, "filesUpload"));
        fileUploadFieldArquivo.setVisible(edicao.isVisible());
        edicao.addOrReplace(fileUploadFieldArquivo);
        AjaxBotaoAnexarArquivo botaoAnexar =
                new AjaxBotaoAnexarArquivo(BOTAO_ANEXAR_AQUIVO_ATA, fileUploadFieldArquivo, true,
                        new RegraAnexosValidacaoPDFA4()) {
                    @Override
                    public void executarSubmit(AjaxRequestTarget target, String clientFileName, InputStream inputStream) {
                        getPaginaAtual().success(
                                anexoPosCorecMediator.anexarArquivo(ciclo, ATA, null, clientFileName, inputStream));
                        atualizaPainel();
                        target.add(edicao, lista, tabelaAnexo, botaoExcluir, fileUploadFieldArquivo);
                        target.add(PainelAnexoAtaPosCorec.this);
                        if (agenda != null && agenda.getParticipantes() != null && !agenda.getParticipantes().isEmpty()) {
                            GestaoPosCorecPage page = (GestaoPosCorecPage) getPaginaAtual();
                            page.atualizarPainelParticipantes(target);
                        }
                    }
                };
        botaoAnexar.setMarkupId(BOTAO_ANEXAR_AQUIVO_ATA);
        edicao.addOrReplace(botaoAnexar);
    }

    private void addBotaoSalvarInformacoes() {
        botaoExcluir = botaoSalvarInformacoes();
        botaoExcluir.setOutputMarkupId(true);
        botaoExcluir.setEnabled(anexoPosCorecMediator.existeAnexo(ciclo, ATA));
        lista.addOrReplace(botaoExcluir);

    }

    private AjaxSubmitLinkSisAps botaoSalvarInformacoes() {
        return new AjaxSubmitLinkSisAps("bttExcluirAnexoAta", true) {
            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                String msg = AnexoPosCorecMediator.get().excluirAnexo(ciclo, ATA);
                getPaginaAtual().success(msg);
                atualizaPainel();
                target.add(edicao, lista, tabelaAnexo);
                target.add(PainelAnexoAtaPosCorec.this);
                if (agenda != null && agenda.getParticipantes() != null && !agenda.getParticipantes().isEmpty()) {
                    GestaoPosCorecPage page = (GestaoPosCorecPage) getPaginaAtual();
                    page.atualizarPainelParticipantes(target);
                }
            }
        };
    }

    private void addListaAnexo() {
        tabelaAnexo = new TabelaAnexoPosCorec("idTabelaAnexoAta", ciclo, ATA, false, false);
        lista.addOrReplace(tabelaAnexo);
    }

    private void addLinkAta() {
        GerarRelatorioAtaCorecLink relatoriolink =
                new GerarRelatorioAtaCorecLink("linkImprimirAta", new File("est.pdf"), ciclo, getPaginaAtual());
        wmcExibirAta.addOrReplace(relatoriolink);
        wmcExibirAta.setVisibilityAllowed(!isPossivelAnexarAta() && !anexoPosCorecMediator.existeAnexo(ciclo, ATA));

    }

    protected void onDownLoad(AnexoPosCorecVO vo) {
        String link = vo.getLink();
        byte[] arquivoBytes = AnexoPosCorecMediator.get().recuperarArquivo(link, ciclo, vo.getTipo());
        executarDowload(link, arquivoBytes);
    }

    protected void executarDowload(String link, byte[] arquivoBytes) {
        if (arquivoBytes == null) {
            error("Arquivo '" + link + "' não foi encontrado.");
        } else {
            ByteArrayResource resouce =
                    new ByteArrayResource(ContentType.DEFAULT_BINARY.getMimeType(), arquivoBytes, link);
            getRequestCycle().scheduleRequestHandlerAfterCurrent(
                    new ResourceRequestHandler(resouce, getPage().getPageParameters()));
        }
    }

    private void atualizaPainel() {
        edicao.setVisible(!anexoPosCorecMediator.existeAnexo(ciclo, ATA) && !existeParticipante);
        lista.setVisible(anexoPosCorecMediator.existeAnexo(ciclo, ATA) && !existeParticipante);
        botaoExcluir.setEnabled(anexoPosCorecMediator.existeAnexo(ciclo, ATA) && !existeParticipante);
    }

    public List<FileUpload> getFilesUpload() {
        return filesUpload;
    }

    public void setFilesUpload(List<FileUpload> filesUpload) {
        this.filesUpload = filesUpload;
    }

}
