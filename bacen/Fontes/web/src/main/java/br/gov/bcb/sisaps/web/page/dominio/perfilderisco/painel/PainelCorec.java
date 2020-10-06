package br.gov.bcb.sisaps.web.page.dominio.perfilderisco.painel;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.image.Image;

import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.VerificarPendenciasCorecMediator;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.botoes.AjaxSubmitLinkSisAps;
import br.gov.bcb.sisaps.web.page.componentes.util.ConstantesImagens;
import br.gov.bcb.sisaps.web.page.dominio.perfilderisco.PerfilDeRiscoPage;

public class PainelCorec extends PainelSisAps {

    private PerfilRisco perfilRisco;
    private AjaxSubmitLinkSisAps botaoCorec;
    private boolean exibirInfo;
    private List<String> mensagem = new ArrayList<String>();

    public PainelCorec(String id, PerfilRisco perfilRisco) {
        super(id);
        setMarkupId(id);
        this.perfilRisco = perfilRisco;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        addBotoes();
    }

    private void addBotoes() {
        AjaxSubmitLinkSisAps botaoPendencia = botaoVerificarPendencia();
        addOrReplace(botaoPendencia);
        Image image = new Image("mover", ConstantesImagens.IMG_PROXIMO);
        addOrReplace(image);
        botaoCorec = botaoIniciarCorec();
        botaoCorec.setEnabled(false);
        addOrReplace(botaoCorec);
    }

    private AjaxSubmitLinkSisAps botaoVerificarPendencia() {
        return new AjaxSubmitLinkSisAps("bttVerificarPendencia", true) {

            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                ArrayList<ErrorMessage> erros =
                        VerificarPendenciasCorecMediator.get().mostraAlertaBotaoVerificarPendencia(
                                perfilRisco.getCiclo());
                if (erros.isEmpty()) {
                    botaoCorec.setEnabled(true);
                    target.add(botaoCorec);
                }
                exibirInfo = true;
                mensagem = (VerificarPendenciasCorecMediator.get().mensagensAlerta(perfilRisco.getCiclo()));
                ((PerfilDeRiscoPage) getPage()).atualizarMensagem(target, mensagem);
                SisapsUtil.lancarNegocioException(erros);
            }
        };
    }

    private AjaxSubmitLinkSisAps botaoIniciarCorec() {
        return new AjaxSubmitLinkSisAps("bttIniciarCorec") {

            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                CicloMediator.get().iniciarCorec(perfilRisco.getCiclo());
                ((PerfilDeRiscoPage) getPage()).avancarParaNovaPagina(new PerfilDeRiscoPage(perfilRisco.getCiclo()
                        .getPk()), getPaginaAtual().getPaginaAnterior());
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisible(CicloMediator.get().exibirBotaoCorec(perfilRisco.getCiclo(), perfilRisco,
                        getPerfilPorPagina()));
            }

        };
    }

    public PerfilRisco getPerfilRisco() {
        return perfilRisco;
    }

    public void setPerfilRisco(PerfilRisco perfilRisco) {
        this.perfilRisco = perfilRisco;
    }

    public boolean isExibirInfo() {
        return exibirInfo;
    }

    public void setExibirInfo(boolean exibirInfo) {
        this.exibirInfo = exibirInfo;
    }

    public List<String> getMensagem() {
        return mensagem;
    }

    public void setMensagem(List<String> mensagem) {
        this.mensagem = mensagem;
    }

}
