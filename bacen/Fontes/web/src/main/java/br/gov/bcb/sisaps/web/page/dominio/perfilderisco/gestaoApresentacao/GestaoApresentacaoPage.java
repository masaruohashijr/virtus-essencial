package br.gov.bcb.sisaps.web.page.dominio.perfilderisco.gestaoApresentacao;

import java.util.Arrays;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import br.gov.bcb.sisaps.integracao.seguranca.PerfisAcesso;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.SecaoApresentacaoEnum;
import br.gov.bcb.sisaps.src.mediator.ApresentacaoMediator;
import br.gov.bcb.sisaps.src.mediator.LogOperacaoMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.src.vo.ApresentacaoVO;
import br.gov.bcb.sisaps.web.page.DefaultPage;
import br.gov.bcb.sisaps.web.page.componentes.autorizacao.anotacao.SisApsAuthorizeAction;
import br.gov.bcb.sisaps.web.page.dominio.ciclo.painel.PainelResumoCiclo;
import br.gov.bcb.sisaps.web.page.dominio.perfilderisco.gestaoApresentacao.painel.PainelSecaoApresentacao;

@SisApsAuthorizeAction(action = Action.RENDER, roles = {PerfisAcesso.SUPERVISOR})
public class GestaoApresentacaoPage extends DefaultPage {

    // As se��es gerenciadas.
    private static final SecaoApresentacaoEnum[] SECOES = new SecaoApresentacaoEnum[] {
            SecaoApresentacaoEnum.EVOLUCAO_DAS_AVALIACOES, SecaoApresentacaoEnum.PERFIL,
            SecaoApresentacaoEnum.ESTRUTURA_JURIDICA_SOCIETARIA_ORGANIZACIONAL, SecaoApresentacaoEnum.GRUPO_ECONOMICO,
            SecaoApresentacaoEnum.ORGANOGRAMA_FUNCIONAL, SecaoApresentacaoEnum.INFORMACOES_OUTROS_DEPARTAMANETOS,
            SecaoApresentacaoEnum.INFORMACOES_OUTROS_ORGAOS, SecaoApresentacaoEnum.ESTRATEGIAS,
            SecaoApresentacaoEnum.ANEXO_ANALISE_ECONOMICO_FINANCEIRA,
            SecaoApresentacaoEnum.NOTAS_QUANTITATIVAS_EVOLUCAO,
            SecaoApresentacaoEnum.CARACTERISTICAS_UNIDADES_ATIVIDADES,
            SecaoApresentacaoEnum.NOTAS_QUALITATIVAS_EVOLUCAO, SecaoApresentacaoEnum.PROPOSTA_ACOES_CICLO,
            SecaoApresentacaoEnum.EQUIPE};

    // Pk do ciclo.
    private final Integer cicloPk;

    // Pk da apresenta��o.
    private final Integer apresentacaoPk;

    // Construtor
    public GestaoApresentacaoPage(PerfilRisco perfil) {
        setSubirTelaAoSalvar(false);
        // Inicializa��es
        this.cicloPk = perfil.getCiclo().getPk();
        this.apresentacaoPk = ApresentacaoMediator.get().criar(perfil.getCiclo()).getPk();
    }

    @Override
    public String getTitulo() {
        return "Gest�o da apresenta��o";
    }

    @Override
    public String getCodigoTela() {
        return "APSFW0214";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

        // Declara��es
        Form<?> form;
        PerfilRisco perfilRiscoAtual;

        // Recupera o perfil de risco.
        perfilRiscoAtual = PerfilRiscoMediator.get().obterPerfilRiscoAtual(cicloPk);

        // Monta o formul�rio.
        form = new Form<Object>("formulario");
        addOrReplace(form);

        // Adiciona o resumo do ciclo.
        addPainelResumoCiclo(form, perfilRiscoAtual);

        // Adiciona as se��es.
        addSecoes(form);

        // Adiciona o bot�o de voltar.
        form.addOrReplace(new LinkVoltar());

        // Log
        LogOperacaoMediator.get().gerarLogDetalhamento(perfilRiscoAtual.getCiclo().getEntidadeSupervisionavel(),
                perfilRiscoAtual, atualizarDadosPagina(getPaginaAtual()));
    }

    // Adiciona o painel de resumo do ciclo.
    private void addPainelResumoCiclo(Form<?> form, PerfilRisco perfilRiscoAtual) {
        // Declara��es
        PainelResumoCiclo painelResumoCiclo;

        // Monta o painel de resumo do ciclo.
        painelResumoCiclo = new PainelResumoCiclo("resumoCicloPanel", cicloPk, perfilRiscoAtual);
        painelResumoCiclo.setMarkupId(painelResumoCiclo.getId());
        form.addOrReplace(painelResumoCiclo);
    }

    // Adiciona as se��es.
    private void addSecoes(Form<?> form) {
        // Declara��es
        final ApresentacaoVO apresentacaoVO;

        // Recupera a apresenta��o.
        apresentacaoVO = ApresentacaoMediator.get().consultar(apresentacaoPk);

        // Monta as se��es.
        form.add(new ListView<SecaoApresentacaoEnum>("idPaineisSecao", Arrays.asList(SECOES)) {
            // Monta os pain�is de se��o.
            @Override
            protected void populateItem(ListItem<SecaoApresentacaoEnum> item) {
                // Adiciona o painel de se��o.
                item.add(new PainelSecaoApresentacao("idPainelSecao", item.getModelObject(), apresentacaoVO));
            }

        });

    }

}
