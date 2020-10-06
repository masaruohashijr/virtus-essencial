package br.gov.bcb.sisaps.web.page.dominio.perfilderisco.painel;

import java.io.File;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.EntidadeUnicad;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.mediator.AnexoPosCorecMediator;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.EntidadeUnicadMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.util.AmbienteEmail;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.botoes.CustomButtonSupervisor;
import br.gov.bcb.sisaps.web.page.componentes.relatorios.GerarRelatorioDossieLink;
import br.gov.bcb.sisaps.web.page.componentes.relatorios.GerarRelatorioMatrizLink;
import br.gov.bcb.sisaps.web.page.dominio.agenda.PainelAtaComite;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.edicao.TabelaAnexoPosCorec;
import br.gov.bcb.sisaps.web.page.dominio.perfilderisco.apresentacao.ApresentacaoPage;
import br.gov.bcb.sisaps.web.page.dominio.perfilderisco.gestaoApresentacao.GestaoApresentacaoPage;

@SuppressWarnings("serial")
public class PainelLinkOutrasInformacoes extends PainelSisAps {
	
	private static final String ENDERECO = "{ENDERECO}";
	private static final String HOMOLOGACAO = "Homologacao";
    private static final String DESENVOLVIMENTO = "Desenvolvimento";
	
    private static final String OFICIO = "Ofício";
    private PerfilRisco perfilRisco;
    private final PainelFiltroPerfilDeRisco painelFiltroPerfilDeRisco;
    private boolean exibirAtaOficio;
    private final WebMarkupContainer wmcExibirOficio = new WebMarkupContainer("exibirOficio");
    private final WebMarkupContainer wmcExibirEspacamento = new WebMarkupContainer("exibirEspacamento");

    public PainelLinkOutrasInformacoes(String id, PerfilRisco perfilRisco,
            PainelFiltroPerfilDeRisco painelFiltroPerfilDeRisco) {
        super(id);
        setMarkupId(id);
        this.perfilRisco = perfilRisco;
        this.painelFiltroPerfilDeRisco = painelFiltroPerfilDeRisco;

    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        exibirAtaOficio =
                CicloMediator.get().cicloPosCorecEncerrado(perfilRisco.getCiclo())
                        && getPerfilPorPagina() == PerfilAcessoEnum.CONSULTA_TUDO;
        setVisibilityAllowed(PerfilRiscoMediator.get().getMatrizAtualPerfilRisco(getPerfilRisco()) != null);
        addLinkAnexos();
        addLinkApresentacao();
        addComentariosHistoricos();
        addAta();
        addOficio();

    }

    private void addOficio() {
        TabelaAnexoPosCorec tabelaAnexoPosCorec =
                new TabelaAnexoPosCorec("linkOficio", perfilRisco.getCiclo(), OFICIO, false, false);
        wmcExibirOficio.addOrReplace(tabelaAnexoPosCorec);
        wmcExibirOficio.setVisibilityAllowed(exibirAtaOficio
                && !AnexoPosCorecMediator.get().listarAnexos(perfilRisco.getCiclo(), OFICIO).isEmpty());
        add(wmcExibirOficio);
    }

    private void addAta() {
        PainelAtaComite painelAtaComite = new PainelAtaComite("linkAta", perfilRisco.getCiclo());
        painelAtaComite.setVisibilityAllowed(exibirAtaOficio);
        addOrReplace(painelAtaComite);
    }

	private void addComentariosHistoricos() {
		String labelLink = "";
		String link = null;
		String cnpjES = perfilRisco.getCiclo().getEntidadeSupervisionavel().getConglomeradoOuCnpj();
		EntidadeUnicad entidadeUnicad = EntidadeUnicadMediator.get().buscarEntidadeUnicadPorCnpj(cnpjES);
		if (entidadeUnicad != null && entidadeUnicad.getConsolidado() != null) {
			labelLink = "Comentários históricos";
			link = montarLinkPainelES(entidadeUnicad.getConsolidado().getPk());
		} else {
			labelLink = "Para comentários históricos. contate suporte.aps@bcb.gov.br";
			wmcExibirEspacamento.setVisibilityAllowed(false);
		}
		addOrReplace(new ExternalLink("linkComentariosHistoricos", link, labelLink));
		addOrReplace(wmcExibirEspacamento);
	}
    
    private String montarLinkPainelES(Integer idConsolidado) {
        String ambiente = setAmbiente();
        String link = "{ENDERECO}/aps/#/paineles/perfil?codigo=" + idConsolidado;
        link = link.replace(ENDERECO, ambiente);
        return link;
    }

    private String setAmbiente() {
        String ambiente = "https://";
        if (AmbienteEmail.getAmbiente() == null) {
            ambiente += "localhost:8080";
        } else {
            if (AmbienteEmail.isProducao()) {
                ambiente += "was-p";
            } else if (AmbienteEmail.getAmbiente().equals(DESENVOLVIMENTO)) {
                ambiente += "was-t";
            } else if (AmbienteEmail.getAmbiente().equals(HOMOLOGACAO)) {
                ambiente += "was-h";
            }
        }
        return ambiente;
    }

    private void addLinkAnexos() {
        addOrReplace(new GerarRelatorioDossieLink("linkImprimirDossie", new File("est.pdf"), getPerfilRisco().getPk(),
                getPaginaAtual()));
        addOrReplace(new GerarRelatorioMatrizLink("linkImprimirMatriz", new File("Matriz.xls"), getPerfilRisco()
                .getPk(), getPaginaAtual()));
    }

    @SuppressWarnings("rawtypes")
    private void addLinkApresentacao() {
        // Declarações
        CustomButtonSupervisor btnGerenciarApresentacao;
        Link linkApresentacao;

        boolean podeApresentar;
        boolean podeGerenciar;
        PerfilAcessoEnum perfilAcesso;
        boolean ehAtual;
        boolean ehCorec;
        boolean ehVigente;
        String versao;

        // Inicializações

        versao = getVersao();
        ehCorec = versao.equals("corec");
        ehVigente = versao.equals("vigente");

        // Link para a apresentação.
        linkApresentacao = new Link("linkApresentacao") {
            @Override
            public void onClick() {
                setResponsePage(new ApresentacaoPage(perfilRisco, perfilRisco.getCiclo(), getPaginaAtual()
                        .getPerfilPorPagina()));
            }
        };
        podeApresentar = false;

        perfilAcesso = getPerfilPorPagina();
        if (perfilAcesso == PerfilAcessoEnum.SUPERVISOR || perfilAcesso == PerfilAcessoEnum.GERENTE) {
            // O supervisor/gerente pode ver a apresentação se for a versão vigente ou corec do ciclo.
            podeApresentar = (ehVigente || ehCorec);

        } else if (perfilAcesso == PerfilAcessoEnum.CONSULTA_TUDO) {
            // O consultador pode ver a apresentação se for a versão corec do ciclo.
            ehAtual = ehAtual();
            podeApresentar = (!ehAtual && ehCorec);
        }
        linkApresentacao.setVisible(podeApresentar);
        addOrReplace(linkApresentacao);

        // Botão de gerenciar apresentação.
        btnGerenciarApresentacao = new CustomButtonSupervisor("btnGerenciarApresentacao") {
            @Override
            public void executeSubmit() {
                getPaginaAtual().avancarParaNovaPagina(new GestaoApresentacaoPage(perfilRisco));
            }
        };

        podeGerenciar = false;
        if (perfilSupervisor(getPaginaAtual())) {
            // O supervisor pode gerenciar a apresentação se for a versão vigente do ciclo.
            podeGerenciar = ehVigente;
        }
        btnGerenciarApresentacao.setVisible(podeGerenciar);
        addOrReplace(btnGerenciarApresentacao);
    }

    private boolean ehAtual() {
        DropDownChoice<Ciclo> select = painelFiltroPerfilDeRisco.getSelectCiclo();
        IModel<Ciclo> model = select.getModel();
        Ciclo ciclo = model.getObject();
        IChoiceRenderer<? super Ciclo> renderer = select.getChoiceRenderer();
        return "atual".equals(renderer.getDisplayValue(ciclo));
    }

    private String getVersao() {
        DropDownChoice<PerfilRisco> select = painelFiltroPerfilDeRisco.getSelectVersao();
        IModel<PerfilRisco> model = select.getModel();
        PerfilRisco perfilRisco = model.getObject();
        IChoiceRenderer<? super PerfilRisco> renderer = select.getChoiceRenderer();
        return renderer.getDisplayValue(perfilRisco).toString();
    }

    public void setPerfilRisco(PerfilRisco perfilRisco) {
        this.perfilRisco = perfilRisco;
    }

    private PerfilRisco getPerfilRisco() {
        return perfilRisco;
    }

}
