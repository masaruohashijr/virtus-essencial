package br.gov.bcb.sisaps.web.page.dominio.perfilderisco;

import java.io.File;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.markup.html.link.ExternalLink;

import br.gov.bcb.sisaps.integracao.seguranca.PerfisAcesso;
import br.gov.bcb.sisaps.src.dominio.EntidadeUnicad;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.EntidadeUnicadMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.util.AmbienteEmail;
import br.gov.bcb.sisaps.web.page.componentes.autorizacao.anotacao.SisApsAuthorizeAction;
import br.gov.bcb.sisaps.web.page.componentes.relatorios.GerarRelatorioDossieResumidoLink;
import br.gov.bcb.sisaps.web.page.componentes.util.ConstantesImagens;

@SisApsAuthorizeAction(action = Action.RENDER, roles = {PerfisAcesso.CONSULTA_RESUMO_NAO_BLOQUEADOS,
        PerfisAcesso.CONSULTA_NAO_BLOQUEADOS, PerfisAcesso.CONSULTA_TUDO})
public class PerfilDeRiscoPageResumido extends AbstractPerfilDeRiscoPage {
	
	private static final String ENDERECO = "{ENDERECO}";
	private static final String HOMOLOGACAO = "Homologacao";
    private static final String DESENVOLVIMENTO = "Desenvolvimento";

    public PerfilDeRiscoPageResumido(Integer pkCiclo) {
        ciclo = CicloMediator.get().loadPK(pkCiclo);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        perfilRiscoAtual = PerfilRiscoMediator.get().obterPerfilRiscoAtual(ciclo.getPk());
        Form<?> form = new Form<Object>("formulario");
        addPainelFiltroPerfilDeRisco(form);
        addPainelResumoCiclo(form);
        addPainelDetalhesES(form);
        addPainelAnaliseQuantitativa(form);
        addPainelSintese(form);
        addPainelSinteseAQT(form);
        form.addOrReplace(new LinkVoltar() {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(CicloMediator.get().exibirBotaoVoltar(ciclo));
            }
        });
        addLinkDossie(form);
        addLinkComentario(form);
        addOrReplace(form);
    }

    private void addLinkDossie(Form<?> form) {

        DownloadLink dossieResumido =
                new GerarRelatorioDossieResumidoLink("linkImprimirDossie", new File("dossie.pdf"), getPaginaAtual()) {
                    @Override
                    public PerfilRisco getPerfilRiscoSelecionado() {
                        return painelFiltroPerfilDeRisco.getPerfilRiscoSelecionado();
                    }

                    @Override
                    protected void onConfigure() {
                        setVisibilityAllowed(!isPerfilRiscoCicloMigrado());
                        super.onConfigure();
                    }
                };

        dossieResumido.add(new Image("imprimir", ConstantesImagens.IMG_IMPRESSORA));

        form.addOrReplace(dossieResumido);
    }

    private void addLinkComentario(Form<?> form) {
		String labelLink = "";
		String link = null;
		String cnpjES = perfilRiscoAtual.getCiclo().getEntidadeSupervisionavel().getConglomeradoOuCnpj();
		EntidadeUnicad entidadeUnicad = EntidadeUnicadMediator.get().buscarEntidadeUnicadPorCnpj(cnpjES);
		if (entidadeUnicad != null && entidadeUnicad.getConsolidado() != null) {
			labelLink = "Comentários históricos";
			link = montarLinkPainelES(entidadeUnicad.getConsolidado().getPk());
		} else {
			labelLink = "Para comentários históricos. contate suporte.aps@bcb.gov.br";
		}
		form.addOrReplace(new ExternalLink("linkComentariosHistoricos", link, labelLink));
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

    @Override
    public String getTitulo() {
        return "Perfil de risco";
    }

    @Override
    public String getCodigoTela() {
        return "APSFW0202";
    }

}
