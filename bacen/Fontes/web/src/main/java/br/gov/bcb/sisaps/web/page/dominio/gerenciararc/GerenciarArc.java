package br.gov.bcb.sisaps.web.page.dominio.gerenciararc;

import java.util.List;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.gov.bcb.sisaps.integracao.seguranca.PerfisAcesso;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.DesignacaoMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.src.vo.ARCDesignacaoVO;
import br.gov.bcb.sisaps.src.vo.ConsultaARCDesignacaoVO;
import br.gov.bcb.sisaps.util.Util;
import br.gov.bcb.sisaps.web.page.DefaultPage;
import br.gov.bcb.sisaps.web.page.componentes.autorizacao.anotacao.SisApsAuthorizeAction;
import br.gov.bcb.sisaps.web.page.componentes.botoes.CustomButton;
import br.gov.bcb.sisaps.web.page.dominio.ciclo.painel.PainelDadosMatrizPercentual;
import br.gov.bcb.sisaps.web.page.dominio.ciclo.painel.PainelResumoCiclo;
import br.gov.bcb.sisaps.web.page.dominio.designacao.DesignacaoARCPage;
import br.gov.bcb.sisaps.web.page.dominio.designacao.PainelListagemDesignacao;

@SisApsAuthorizeAction(action = Action.RENDER, roles = {PerfisAcesso.SUPERVISOR, PerfisAcesso.CONSULTA_TUDO})
public class GerenciarArc extends DefaultPage {

    @SpringBean
    private CicloMediator cicloMediator;

    @SpringBean
    private PerfilRiscoMediator perfilRiscoMediator;

    @SpringBean
    private DesignacaoMediator designacaoMediator;

    private final Ciclo ciclo;
    private PerfilRisco perfilRisco;
    private final Form<Ciclo> form = new Form<Ciclo>("formulario");
    private List<ARCDesignacaoVO> listaArcsAnalise;
    private List<ARCDesignacaoVO> listaArcsAtualizacao;
    private final ConsultaARCDesignacaoVO consulta = new ConsultaARCDesignacaoVO();

    public GerenciarArc(Integer pkCiclo) {
        ciclo = cicloMediator.loadPK(pkCiclo);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        perfilRisco = perfilRiscoMediator.obterPerfilRiscoAtual(ciclo.getPk());
        listaArcsAnalise = designacaoMediator.buscarListaARCsAnalise(ciclo, consulta);
        listaArcsAtualizacao = designacaoMediator.buscarListaARCsAtualizacao(ciclo, consulta);
        addPaineis();
        addBotoes();
        addOrReplace(form);
    }

    private void addPaineis() {
        addPainelResumoCiclo();
        addPainelMatrizRiscoControle();
        addPainelArcEmAnalise();
        addPainelEmAtualizacao();
        addPainelDesignarEmLote();
    }

    private void addPainelDesignarEmLote() {
        WebMarkupContainer tabelaDesignar = new WebMarkupContainer("tDesignacaoLote") {
            @Override
            public boolean isVisible() {
                return !Util.isNuloOuVazio(listaArcsAtualizacao);
            }
        };
        tabelaDesignar.addOrReplace(addBotaoDesignarLote());
        tabelaDesignar.setOutputMarkupId(true);
        tabelaDesignar.setMarkupId(tabelaDesignar.getId());
        form.addOrReplace(tabelaDesignar);
    }

    private void addPainelArcEmAnalise() {
        PainelListagemDesignacao arcEmAnalise =
                new PainelListagemDesignacao("idArcAnalise", ciclo, listaArcsAnalise, consulta, false, "Em análise");
        form.addOrReplace(arcEmAnalise);
    }

    private void addPainelEmAtualizacao() {
        PainelListagemDesignacao arcEmAtualizacao =
                new PainelListagemDesignacao("idArcAtualizacao", ciclo, listaArcsAtualizacao, consulta, false,
                        "Em atualização");
        form.addOrReplace(arcEmAtualizacao);
    }

    private void addPainelResumoCiclo() {
        PainelResumoCiclo painelResumoCiclo = new PainelResumoCiclo("resumoCicloPanel", ciclo.getPk(), perfilRisco);
        painelResumoCiclo.setMarkupId(painelResumoCiclo.getId());
        form.addOrReplace(painelResumoCiclo);
    }

    private void addPainelMatrizRiscoControle() {
        PainelDadosMatrizPercentual painelMatrizVigente =
                new PainelDadosMatrizPercentual("idPainelMatrizVigente", perfilRisco, "Matriz vigente", true, true,
                        true) {
                    @Override
                    public boolean getExibirStringAAvaliar() {
                        return true;
                    }
                };
        painelMatrizVigente.setMarkupId(painelMatrizVigente.getId());
        form.addOrReplace(painelMatrizVigente);
    }

    private void addBotoes() {
        form.addOrReplace(new LinkVoltarPerfilRisco(perfilRisco.getCiclo().getPk()));

    }

    private CustomButton addBotaoDesignarLote() {
        CustomButton botaoDesignarLote = new CustomButton("bttDesignacaooLoteARCs") {

            @Override
            public void executeSubmit() {
                avancarParaNovaPagina(new DesignacaoARCPage(ciclo.getPk()));
            }
        };

        return botaoDesignarLote;
    }

    @Override
    public String getTitulo() {
        return "Gestão de ARCs";
    }

    @Override
    public String getCodigoTela() {
        return "APSFW0206";
    }
}
