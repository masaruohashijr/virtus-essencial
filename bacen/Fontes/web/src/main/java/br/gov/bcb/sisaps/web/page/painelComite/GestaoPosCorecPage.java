/*
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software e confidencial e propriedade do Banco Central do Brasil.
 * Nao e permitida sua distribuicao ou divulgacao do seu conteudo sem
 * expressa autorizacao do Banco Central.
 * Este arquivo contem informacoes proprietarias.
 */

package br.gov.bcb.sisaps.web.page.painelComite;

import org.apache.commons.collections.CollectionUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.gov.bcb.sisaps.integracao.seguranca.PerfisAcesso;
import br.gov.bcb.sisaps.src.dominio.AgendaCorec;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.mediator.AgendaCorecMediator;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.ParticipanteAgendaCorecMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.src.vo.CicloVO;
import br.gov.bcb.sisaps.web.page.DefaultPage;
import br.gov.bcb.sisaps.web.page.componentes.autorizacao.anotacao.SisApsAuthorizeAction;
import br.gov.bcb.sisaps.web.page.componentes.botoes.AjaxSubmitLinkSisAps;
import br.gov.bcb.sisaps.web.page.componentes.botoes.BotaoConfirmacao;
import br.gov.bcb.sisaps.web.page.dominio.ciclo.painel.PainelResumoCiclo;

@SisApsAuthorizeAction(action = Action.RENDER, roles = {PerfisAcesso.COMITE})
public class GestaoPosCorecPage extends DefaultPage {

    private static final String MENSAGEM = "Deseja realmente encerrar o Ciclo?";
    private PainelResumoCiclo painelResumoCiclo;
    private final Ciclo ciclo;
    private final PerfilRisco perfilRiscoAtual;
    private final Form<?> form = new Form<Object>("formulario");
    private PainelAnexoAtaPosCorec painelAnexoAta;
    private PainelAnexoOficioPosCorec painelAnexoOficio;
    private PainelListagemParticipantesAgenda painelParticipantes;
    @SpringBean
    private CicloMediator cicloMediator;

    public GestaoPosCorecPage(CicloVO cicloVO) {
        this.ciclo = cicloMediator.buscarCicloPorPK(cicloVO.getPk());
        perfilRiscoAtual = PerfilRiscoMediator.get().obterPerfilRiscoAtual(ciclo.getPk());
        form.setMultiPart(true);
        form.add(addPainelResumoCiclo());
        form.add(addPainelAnexoAta());
        form.add(addPainelAnexoOficio());
        form.add(addPainelParticipantes());
        form.add(new LinkVoltar());
        addBotaoEncerrarCiclo(form);
        add(form);
    }

    private WebMarkupContainer addPainelParticipantes() {
        AgendaCorec agenda = AgendaCorecMediator.get().buscarAgendaCorecPorCiclo(ciclo.getPk());
        WebMarkupContainer tabelaPainel = new WebMarkupContainer("idParticipantes");
        painelParticipantes = new PainelListagemParticipantesAgenda("participantesCorec", ciclo, agenda);
        painelParticipantes.setMarkupId(painelParticipantes.getId());
        tabelaPainel.add(painelParticipantes);
        tabelaPainel.setVisibilityAllowed(agenda == null ? false : CollectionUtils
                .isNotEmpty(ParticipanteAgendaCorecMediator.get().buscarParticipanteAgendaCorec(agenda.getPk())));
        return tabelaPainel;
    }

    private PainelAnexoOficioPosCorec addPainelAnexoOficio() {
        painelAnexoOficio = new PainelAnexoOficioPosCorec("anexoOficioPanel", ciclo);
        painelAnexoOficio.setMarkupId(painelAnexoOficio.getId());
        return painelAnexoOficio;
    }

    private void addBotaoEncerrarCiclo(Form<?> form) {
        AjaxSubmitLinkSisAps botaoEncerrarCiclo = botaoEncerrarCiclo();
        form.addOrReplace(botaoEncerrarCiclo);
    }

    private BotaoConfirmacao botaoEncerrarCiclo() {
        return new BotaoConfirmacao("bttEncerrarCiclo", MENSAGEM) {

            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                String mensagemSucesso = cicloMediator.encerrarCiclo(ciclo);
                getPaginaAnterior().success(mensagemSucesso);
                setResponsePage(getPaginaAnterior());
            }

        };
    }

    private PainelResumoCiclo addPainelResumoCiclo() {
        painelResumoCiclo = new PainelResumoCiclo("resumoCicloPanel", ciclo.getPk(), perfilRiscoAtual);
        painelResumoCiclo.setMarkupId(painelResumoCiclo.getId());
        return painelResumoCiclo;
    }

    private PainelAnexoAtaPosCorec addPainelAnexoAta() {
        painelAnexoAta = new PainelAnexoAtaPosCorec("anexoAtaPanel", ciclo);
        painelAnexoAta.setMarkupId(painelAnexoAta.getId());
        return painelAnexoAta;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitulo() {
        return "Gestão Pós-Corec";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCodigoTela() {
        return "APSFW0603";
    }

    public void atualizar(AjaxRequestTarget target) {
        target.add(addPainelResumoCiclo());
        target.add(addPainelAnexoAta());
    }

    public void atualizarPainelParticipantes(AjaxRequestTarget target) {
        target.add(painelParticipantes.getTabelaParticipantes());
    }

}