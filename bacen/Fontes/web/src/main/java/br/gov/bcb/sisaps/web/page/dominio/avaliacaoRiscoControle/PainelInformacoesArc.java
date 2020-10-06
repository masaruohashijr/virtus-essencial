package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.src.dominio.Atividade;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.ParametroGrupoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoUnidadeAtividadeEnum;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.Util;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.analise.AnalisarArcPage;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.consulta.ConsultaArcPerfilDeRiscoPage;

@SuppressWarnings("serial")
public class PainelInformacoesArc extends PainelSisAps {

    private static final String PROP_ESTADO_DESCRICAO = "estado.descricao";
    private static final String NOME = "nome";
    private AvaliacaoRiscoControle avaliacao;
    private final Atividade atividade;
    private final ParametroGrupoRiscoControle grupo;
    private String nomeOperador;
    private final WebMarkupContainer wbcExibirEstado = new WebMarkupContainer("wmcExibirEstado");
    private final WebMarkupContainer wbcExibirUnidade = new WebMarkupContainer("wmcExibirUnidade");
    private final WebMarkupContainer wbcExibirAtividade = new WebMarkupContainer("wmcExibirAtividade");
    private final WebMarkupContainer wbcExibirRiscoControle = new WebMarkupContainer("wmcExibirRiscoControle");
    @SpringBean
    private AvaliacaoRiscoControleMediator avaliacaoRiscoControleMediator;
    private Label lblEstado;
    private Label labelUltimaAtualizacao;
    private final Ciclo ciclo;
    private final boolean isArcExterno;

    public PainelInformacoesArc(String id, AvaliacaoRiscoControle avaliacao, Ciclo ciclo, Atividade atividade,
            ParametroGrupoRiscoControle grupo) {
        super(id);
        this.avaliacao = avaliacao;
        this.ciclo = ciclo;
        this.atividade = atividade == null ? new Atividade() : atividade;
        this.grupo = grupo;
        this.nomeOperador = avaliacao == null ? "" : avaliacao.getNomeOperador() + Constantes.EM;
        this.isArcExterno = atividade == null;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        if (avaliacao != null) {
            avaliacao = AvaliacaoRiscoControleMediator.get().buscar(avaliacao.getPk());
        }
        
        addOrReplaceComponents();
    }

    private void addOrReplaceComponents() {
        wbcExibirUnidade.addOrReplace(new Label("idUnidadeArc", new PropertyModel<String>(atividade, "unidade.nome")));

        wbcExibirUnidade.setVisible(atividade.getUnidade() != null
                && !TipoUnidadeAtividadeEnum.CORPORATIVO.equals(atividade.getTipoAtividade()) && !isArcExterno);
        addOrReplace(wbcExibirUnidade);

        wbcExibirAtividade.addOrReplace(new Label("idAtividadeArc", new PropertyModel<String>(atividade, NOME)));
        wbcExibirAtividade.setVisible(!isArcExterno);
        addOrReplace(wbcExibirAtividade);
        
        String idGrupo = "idGrupoArc";
        if (grupo == null) {
            addOrReplace(new Label(idGrupo, ""));
        } else {
            addOrReplace(new Label(idGrupo, new PropertyModel<String>(grupo, "nomeAbreviado")));
        }
        
        
        Model<String> model = new Model<String>();
        if (avaliacao != null) {
            model = new Model<String>(avaliacao.getTipo().getDescricao());
        }
        
        wbcExibirRiscoControle.addOrReplace(new Label("idTipoArc", model));
        wbcExibirRiscoControle.setVisible(!isArcExterno);
        addOrReplace(wbcExibirRiscoControle);
        
        PropertyModel<String> modelEstado = new PropertyModel<String>("", "");
        if (avaliacao != null) {
            modelEstado = new PropertyModel<String>(avaliacao, PROP_ESTADO_DESCRICAO);
        }
        

        lblEstado = new Label("idEstadoArc", modelEstado) {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setDefaultModel(new PropertyModel<String>(avaliacao, PROP_ESTADO_DESCRICAO));
            }
        };
        wbcExibirEstado.addOrReplace(lblEstado);
        wbcExibirEstado.setVisible(!(getPaginaAtual() instanceof ConsultaArcPerfilDeRiscoPage));
        addOrReplace(wbcExibirEstado);

        addResponsavel();
        addUltimaAtualizacao();
        addPreenchidoPor();
        addAnalisadoPor();
        addConcluidoPor();

    }

    private void addConcluidoPor() {
        WebMarkupContainer linhaMostrarConcluidoPor = new WebMarkupContainer("wmcExibirConcluidoPor") {
            @Override
            protected void onConfigure() {
                setVisible(avaliacaoRiscoControleMediator.exibirConcluidoPor(avaliacao.getEstado())
                        && !(getPaginaAtual() instanceof ConsultaArcPerfilDeRiscoPage));
            }
        };
        String operador = "";
        if (avaliacao != null) {
            operador =
                    Util.nomeOperador(avaliacao.getOperadorConclusao()) + Constantes.EM
                    + avaliacao.getData(avaliacao.getDataConclusao());

        }
        Label labelAtualizacao =
                new Label("idConcluirPor", operador);
        linhaMostrarConcluidoPor.addOrReplace(labelAtualizacao);
        linhaMostrarConcluidoPor.setOutputMarkupId(true);
        addOrReplace(linhaMostrarConcluidoPor);
    }

    private void addAnalisadoPor() {
        WebMarkupContainer linhaMostrarAnalisadoPor = new WebMarkupContainer("wmcExibirAnalisadoPor") {
            @Override
            protected void onConfigure() {
                setVisible(avaliacaoRiscoControleMediator.exibirAnalisadoPor(avaliacao.getEstado()));
            }
        };
        String operador = "";
        if (avaliacao != null) {
            operador =
                    Util.nomeOperador(avaliacao.getOperadorAnalise()) + Constantes.EM
                    + avaliacao.getData(avaliacao.getDataAnalise());

        }
        
        Label labelAtualizacao =
                new Label("idAnalisadoPor", operador);
        linhaMostrarAnalisadoPor.addOrReplace(labelAtualizacao);
        linhaMostrarAnalisadoPor.setOutputMarkupId(true);
        addOrReplace(linhaMostrarAnalisadoPor);

    }

    private void addPreenchidoPor() {
        WebMarkupContainer linhaMostrarPrenchidoPor = new WebMarkupContainer("wmcExibirPreenchidoPor") {
            @Override
            protected void onConfigure() {
                setVisible(avaliacaoRiscoControleMediator.exibirPreenchidoPor(avaliacao.getEstado()));
            }
        };
        
        String operador = "";
        if (avaliacao != null) {
            operador =
                    Util.nomeOperador(avaliacao.getOperadorPreenchido()) + Constantes.EM
                            + avaliacao.getData(avaliacao.getDataPreenchido());

        }
        Label labelAtualizacao = new Label("idPrenchidoPor", operador);
        linhaMostrarPrenchidoPor.addOrReplace(labelAtualizacao);
        linhaMostrarPrenchidoPor.setOutputMarkupId(true);
        addOrReplace(linhaMostrarPrenchidoPor);

    }

    private void addResponsavel() {
        WebMarkupContainer linhaMostrarResponsavel = new WebMarkupContainer("idMostrarResponsavel") {
            @Override
            protected void onConfigure() {
                setVisible((getPaginaAtual() instanceof AnalisarArcPage) ? avaliacaoRiscoControleMediator
                        .exibirResponsavelTelaAnalise(avaliacao.getEstado(), avaliacao.getDelegacao())
                        : avaliacaoRiscoControleMediator.exibirResponsavel(avaliacao.getEstado()));
            }
        };
        linhaMostrarResponsavel.setMarkupId(linhaMostrarResponsavel.getId());
        linhaMostrarResponsavel.setOutputMarkupId(true);

        ServidorVO servidorvo = null;
        if (avaliacao != null && avaliacao.getPk() != null) {
            servidorvo =
                    avaliacaoRiscoControleMediator.valorResponsavel(avaliacao, ciclo.getEntidadeSupervisionavel());
        }

        Label labelResponsavel =
                new Label("idResponsavel", servidorvo == null ? Constantes.VAZIO : servidorvo.getNome()) {
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
                setVisible(avaliacaoRiscoControleMediator.exibirUltimaAtualizacao(avaliacao.getEstado()));
            }
        };
        labelUltimaAtualizacao = new Label("idUltimaAtualizacao", new PropertyModel<String>(this, "atualizacao"));
        labelUltimaAtualizacao.setVisible(StringUtils.isNotBlank(avaliacao == null ? "" : avaliacao.getNomeOperador()));
        linhaMostrarUltimaAtualizacao.addOrReplace(labelUltimaAtualizacao);
        linhaMostrarUltimaAtualizacao.setOutputMarkupId(true);

        addOrReplace(linhaMostrarUltimaAtualizacao);
    }

    public String getAtualizacao() {
        avaliacao = AvaliacaoRiscoControleMediator.get().buscar(avaliacao.getPk());
        return avaliacao == null ? "" : avaliacao.getNomeOperador() + Constantes.EM
                + avaliacao.getData(avaliacao.getUltimaAtualizacao());
    }

    public String getNomeOperador() {
        return nomeOperador;
    }

    public void setNomeOperador(String nomeOperador) {
        this.nomeOperador = nomeOperador;
    }

    public void setAvaliacao(AvaliacaoRiscoControle avaliacao) {
        this.avaliacao = avaliacao;
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

    public Label getLabelUltimaAtualizacao() {
        return labelUltimaAtualizacao;
    }

    public void setLabelUltimaAtualizacao(Label labelUltimaAtualizacao) {
        this.labelUltimaAtualizacao = labelUltimaAtualizacao;
    }

}
