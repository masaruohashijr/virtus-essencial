package br.gov.bcb.sisaps.web.page.dominio.agenda;

import java.io.File;

import org.apache.http.entity.ContentType;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.handler.resource.ResourceRequestHandler;
import org.apache.wicket.request.resource.ByteArrayResource;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.mediator.AnexoPosCorecMediator;
import br.gov.bcb.sisaps.src.vo.AnexoPosCorecVO;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.relatorios.GerarRelatorioAtaCorecLink;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.edicao.TabelaAnexoPosCorec;

@SuppressWarnings("serial")
public class PainelAtaComite extends PainelSisAps {

    private static final String LINK2 = "link";
    private static final String ATA = "Ata";
    private final WebMarkupContainer wmcExibirAta = new WebMarkupContainer("wmcExibirAta");
    private final WebMarkupContainer wmcExibirAnexo = new WebMarkupContainer("wmcExibirAnexo");
    private AnexoPosCorecVO anexo;
    private final Ciclo ciclo;

    public PainelAtaComite(String id, Ciclo ciclo) {
        super(id);
        this.ciclo = ciclo;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        anexo = AnexoPosCorecVO.converterParaVo(AnexoPosCorecMediator.get().buscarAnexo(ciclo, ATA));
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        addLinkAta();
        addAnexo();
        addOrReplace(wmcExibirAta);
        addOrReplace(wmcExibirAnexo);
    }

    private void addAnexo() {
        wmcExibirAnexo.add(new TabelaAnexoPosCorec("idTabelaAta", ciclo, ATA, false, false));
        wmcExibirAnexo.setVisibilityAllowed(anexo.getPk() != null);
    }

    private void addLinkAta() {
        GerarRelatorioAtaCorecLink relatoriolink =
                new GerarRelatorioAtaCorecLink("linkImprimirAta", new File("est.pdf"), ciclo, getPaginaAtual());
        wmcExibirAta.addOrReplace(relatoriolink);
        wmcExibirAta.setVisibilityAllowed(anexo.getPk() == null);

        final IModel<AnexoPosCorecVO> model = new IModel<AnexoPosCorecVO>() {

            private AnexoPosCorecVO anexoInterno = anexo;

            @Override
            public void detach() {
            }

            @Override
            public void setObject(AnexoPosCorecVO object) {
                anexoInterno = object;

            }

            @Override
            public AnexoPosCorecVO getObject() {
                return anexoInterno;
            }
        };

        Link<AnexoPosCorecVO> link = new Link<AnexoPosCorecVO>(LINK2, model) {
            @Override
            public void onClick() {
                onDownLoad(model.getObject());
            }
        };
        link.setBody(new PropertyModel<String>(link.getModelObject(), LINK2));
        link.setMarkupId(link.getId() + "anexo" + model.getObject().getPk());
        link.setOutputMarkupId(true);
        link.setVisibilityAllowed(anexo.getPk() == null);
        wmcExibirAta.addOrReplace(link);

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

}
