package br.gov.bcb.sisaps.web.page.dominio.analisequantitativaaqt;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.Util;
import br.gov.bcb.sisaps.web.page.PainelSisAps;

@SuppressWarnings("serial")
public class PainelInformacoesAQT extends PainelSisAps {

    private static final String PROP_ESTADO_DESCRICAO = "estado.descricao";
    private String nomeOperador;
    private final WebMarkupContainer wbcExibirEstado = new WebMarkupContainer("wmcExibirEstado");
    @SpringBean
    private AnaliseQuantitativaAQTMediator analiseQuantitativaAQTMediator;
    private Label lblEstado;
    private AnaliseQuantitativaAQT analiseQuantitativaAQT;

    public PainelInformacoesAQT(String id, AnaliseQuantitativaAQT analiseQuantitativaAQT) {
        super(id);
        this.analiseQuantitativaAQT = analiseQuantitativaAQT;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        this.analiseQuantitativaAQT = analiseQuantitativaAQTMediator.buscar(analiseQuantitativaAQT.getPk());
        this.nomeOperador = analiseQuantitativaAQT.getNomeOperador() + Constantes.EM;
        addOrReplaceComponents();
    }

    private void addOrReplaceComponents() {
        addComponenteEstado();
        addResponsavelAQT();
        addUltimaAtualizacao();
        addPreenchidoPor();
        addAnalisadoPor();
        addConcluidoPor();
    }

    private void addComponenteEstado() {
        lblEstado =
                new Label("idComponente", new PropertyModel<String>(analiseQuantitativaAQT.getParametroAQT(),
                        "descricao"));
        addOrReplace(lblEstado);
        addOrReplace(new Label("idEstadoAqt", new PropertyModel<String>(analiseQuantitativaAQT, PROP_ESTADO_DESCRICAO))
                .setVisibilityAllowed(!(getPaginaAtual() instanceof ConsultaAQT)));
    }

    private void addPreenchidoPor() {
        WebMarkupContainer linhaMostrarPrenchidoPor = new WebMarkupContainer("wmcExibirPreenchidoPor") {
            @Override
            protected void onConfigure() {
                setVisible(analiseQuantitativaAQTMediator.exibirPreenchidoPor(analiseQuantitativaAQT.getEstado()));
            }
        };
        Label labelAtualizacao =
                new Label("idPrenchidoPor", Util.nomeOperador(analiseQuantitativaAQT.getOperadorPreenchido())
                        + Constantes.EM + analiseQuantitativaAQT.getData(analiseQuantitativaAQT.getDataPreenchido()));
        linhaMostrarPrenchidoPor.addOrReplace(labelAtualizacao);
        linhaMostrarPrenchidoPor.setOutputMarkupId(true);
        addOrReplace(linhaMostrarPrenchidoPor);

    }

    private void addAnalisadoPor() {
        WebMarkupContainer linhaMostrarAnalisadoPor = new WebMarkupContainer("wmcExibirAnalisadoPor") {
            @Override
            protected void onConfigure() {
                setVisible(analiseQuantitativaAQTMediator.exibirAnalisadoPor(analiseQuantitativaAQT.getEstado()));
            }
        };
        Label labelAtualizacao =
                new Label("idAnalisadoPor", Util.nomeOperador(analiseQuantitativaAQT.getOperadorAnalise())
                        + Constantes.EM + analiseQuantitativaAQT.getData(analiseQuantitativaAQT.getDataAnalise()));
        linhaMostrarAnalisadoPor.addOrReplace(labelAtualizacao);
        linhaMostrarAnalisadoPor.setOutputMarkupId(true);
        addOrReplace(linhaMostrarAnalisadoPor);

    }

    private void addConcluidoPor() {
        WebMarkupContainer linhaMostrarConcluidoPor = new WebMarkupContainer("wmcExibirConcluidoPor") {
            @Override
            protected void onConfigure() {
                setVisible(analiseQuantitativaAQTMediator.exibirConcluidoPor(analiseQuantitativaAQT.getEstado())
                        && !(getPaginaAtual() instanceof ConsultaAQT));
            }
        };
        Label labelAtualizacao =
                new Label("idConcluirPor", Util.nomeOperador(analiseQuantitativaAQT.getOperadorConclusao())
                        + Constantes.EM + analiseQuantitativaAQT.getData(analiseQuantitativaAQT.getDataConclusao()));
        linhaMostrarConcluidoPor.addOrReplace(labelAtualizacao);
        linhaMostrarConcluidoPor.setOutputMarkupId(true);
        addOrReplace(linhaMostrarConcluidoPor);
    }

    private void addResponsavelAQT() {
        WebMarkupContainer linhaMostrarResponsavel = new WebMarkupContainer("idMostrarResponsavel") {
            @Override
            protected void onConfigure() {
                setVisible((getPaginaAtual() instanceof AnaliseAQT) ? analiseQuantitativaAQTMediator
                        .exibirResponsavelTelaAnalise(analiseQuantitativaAQT.getEstado(),
                                analiseQuantitativaAQT.getDelegacao()) : analiseQuantitativaAQTMediator
                        .exibirResponsavel(analiseQuantitativaAQT.getEstado()));
            }
        };
        linhaMostrarResponsavel.setMarkupId(linhaMostrarResponsavel.getId());
        linhaMostrarResponsavel.setOutputMarkupId(true);

        ServidorVO servidorvo = null;
        if (analiseQuantitativaAQT.getPk() != null) {
            servidorvo =
                    analiseQuantitativaAQTMediator.valorResponsavel(analiseQuantitativaAQT, analiseQuantitativaAQT
                            .getCiclo().getEntidadeSupervisionavel());
        }

        Label labelResponsavel =
                new Label("idResponsavelAqt", servidorvo == null ? Constantes.VAZIO : servidorvo.getNome()) {
                    @Override
                    protected void onConfigure() {
                        super.onConfigure();
                    }
                };

        linhaMostrarResponsavel.addOrReplace(labelResponsavel);
        addOrReplace(linhaMostrarResponsavel);
    }

    private void addUltimaAtualizacao() {
        WebMarkupContainer linhaMostrarUltimaAtualizacao = new WebMarkupContainer("idMostrarUltimaAtualizacao") {
            @Override
            protected void onConfigure() {
                setVisible(analiseQuantitativaAQTMediator.exibirUltimaAtualizacao(analiseQuantitativaAQT.getEstado()));
            }
        };
        Label labelAtualizacao =
                new Label("idUltimaAtualizacaoAqt", nomeOperador
                        + analiseQuantitativaAQT.getData(analiseQuantitativaAQT.getUltimaAtualizacao()));
        labelAtualizacao.setVisible(StringUtils.isNotBlank(analiseQuantitativaAQT.getNomeOperador()));
        linhaMostrarUltimaAtualizacao.addOrReplace(labelAtualizacao);
        linhaMostrarUltimaAtualizacao.setOutputMarkupId(true);

        addOrReplace(linhaMostrarUltimaAtualizacao);
    }

    public String getNomeOperador() {
        return nomeOperador;
    }

    public void setNomeOperador(String nomeOperador) {
        this.nomeOperador = nomeOperador;
    }

    public WebMarkupContainer getWbcExibirEstado() {
        return wbcExibirEstado;
    }

    public Label getLblEstado() {
        return lblEstado;
    }

    public void setLblEstado(Label lblEstado) {
        this.lblEstado = lblEstado;
    }

    public AnaliseQuantitativaAQT getAnaliseQuantitativaAQT() {
        return analiseQuantitativaAQT;
    }

    public void setAnaliseQuantitativaAQT(AnaliseQuantitativaAQT analiseQuantitativaAQT) {
        this.analiseQuantitativaAQT = analiseQuantitativaAQT;
    }

}
