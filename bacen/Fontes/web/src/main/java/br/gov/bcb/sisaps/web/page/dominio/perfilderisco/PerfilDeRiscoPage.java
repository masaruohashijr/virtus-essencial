package br.gov.bcb.sisaps.web.page.dominio.perfilderisco;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authorization.UnauthorizedActionException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import br.gov.bcb.comum.util.string.StringUtil;
import br.gov.bcb.sisaps.integracao.seguranca.PerfisAcesso;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.mediator.BloqueioESMediator;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.src.mediator.RegraPerfilAcessoMediator;
import br.gov.bcb.sisaps.web.page.componentes.autorizacao.anotacao.SisApsAuthorizeAction;
import br.gov.bcb.sisaps.web.page.painelConsulta.PerfilConsultaPage;
import br.gov.bcb.sisaps.web.page.painelConsulta.PerfilConsultaResumidoPage;

@SisApsAuthorizeAction(action = Action.RENDER, roles = {PerfisAcesso.SUPERVISOR, PerfisAcesso.CONSULTA_TUDO,
        PerfisAcesso.CONSULTA_NAO_BLOQUEADOS, PerfisAcesso.CONSULTA_RESUMO_NAO_BLOQUEADOS, PerfisAcesso.INSPETOR,
        PerfisAcesso.GERENTE})
public class PerfilDeRiscoPage extends AbstractPerfilDeRiscoPage {

    public PerfilDeRiscoPage(Integer pkCiclo) {
        this(pkCiclo, null, false);
    }

    public PerfilDeRiscoPage(Integer pkCiclo, String sucesso, boolean exibirMensagem) {
        this.exibirMensagem = exibirMensagem;
        ciclo = CicloMediator.get().loadPK(pkCiclo);
        if (!StringUtil.isVazioOuNulo(sucesso)) {
            success(sucesso);
        }
        addMensagem();
    }

    public PerfilDeRiscoPage(Integer pkCiclo, boolean isOrigemPainelConsulta) {
        this(pkCiclo, null, false);
        this.isOrigemPainelConsulta = isOrigemPainelConsulta;
    }

    public PerfilDeRiscoPage(PageParameters parameters) {
        String p1 = parameters.get("cnpj").toString(null);
        String p2 = "";
        if (p1 == null) {
            p2 = parameters.get("perfil").toString(null);
            if (p2 != null) {
                idPerfil = Integer.valueOf(p2);
                acessoPerfil = true;
                ciclo = PerfilRiscoMediator.get().obterPerfilRiscoPorPk(idPerfil).getCiclo();
            }
        } else {
            List<Ciclo> ciclos = CicloMediator.get().consultarCiclosEntidadeSupervisionavel(p1, true);
            if (CollectionUtils.isNotEmpty(ciclos)) {
                ciclo = CicloMediator.get().loadPK(ciclos.get(0).getPk());
            }
        }

        setAcessoPorParametro(true);

        if (RegraPerfilAcessoMediator.get().isAcessoPermitido(PerfilAcessoEnum.CONSULTA_TUDO)
                || RegraPerfilAcessoMediator.get().isAcessoPermitido(PerfilAcessoEnum.CONSULTA_NAO_BLOQUEADOS)) {
            setPaginaAnterior(new PerfilConsultaPage());
        } else if (RegraPerfilAcessoMediator.get().isAcessoPermitido(PerfilAcessoEnum.CONSULTA_RESUMO_NAO_BLOQUEADOS)) {
            PerfilDeRiscoPageResumido perfilDeRiscoPageResumido = new PerfilDeRiscoPageResumido(ciclo.getPk());
            perfilDeRiscoPageResumido.setAcessoPorParametro(true);
			avancarParaNovaPagina(perfilDeRiscoPageResumido, new PerfilConsultaResumidoPage());
        } else {
            throw new UnauthorizedActionException(this, ENABLE);
        }
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        addMensagem();
        perfilRiscoAtual = PerfilRiscoMediator.get().obterPerfilRiscoAtual(ciclo.getPk());
        Form<?> form = new Form<Object>("formulario");
        addPainelMenuLink(form);
        addInformativoESBloqueada(form);
        addPainelFiltroPerfilDeRisco(form);
        addPainelResumoCiclo(form);
        addPainelDetalhesES(form);
        addPainelMatrizVigente(form);

        addPainelAnaliseQuantitativa(form);

        addPainelSintese(form);
        addPainelSinteseAQT(form);

        addPainelAtividadesCiclo(form);
        addPainelInformacoesCicloMigrado(form);
        addPainelCorec(form);
        addBotoes(form);
        form.addOrReplace(new LinkVoltar() {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(CicloMediator.get().exibirBotaoVoltar(ciclo) && !isAcessoPorParametro());
            }
        });
        form.addOrReplace(wmcExibirMensagem);
        addPainelOutrasInformacoes(form);
        addOrReplace(form);
    }

    private void addInformativoESBloqueada(Form<?> form) {
        final IModel<String> mensagemPerfilBloqueado = new LoadableDetachableModel<String>() {
            @Override
            public String load() {
                return BloqueioESMediator.get().getMensagemPerfilBloqueado(
                        ciclo.getEntidadeSupervisionavel().getConglomeradoOuCnpj(), getPerfilPorPagina());
            }
        };
        Label esBloqueada = new Label("perfilBloqueado", mensagemPerfilBloqueado) {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(StringUtils.isNotBlank(mensagemPerfilBloqueado.getObject()));
            }
        };
        form.addOrReplace(esBloqueada);
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
