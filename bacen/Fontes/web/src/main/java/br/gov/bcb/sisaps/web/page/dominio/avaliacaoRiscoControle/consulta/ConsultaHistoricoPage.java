package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.consulta;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.markup.html.form.Form;

import br.gov.bcb.sisaps.integracao.seguranca.PerfisAcesso;
import br.gov.bcb.sisaps.src.vo.ConsultaAnaliseQuantitativaAQTVO;
import br.gov.bcb.sisaps.src.vo.ConsultaAvaliacaoRiscoControleVO;
import br.gov.bcb.sisaps.web.page.DefaultPageMenu;
import br.gov.bcb.sisaps.web.page.componentes.autorizacao.anotacao.SisApsAuthorizeAction;

@SisApsAuthorizeAction(action = Action.RENDER, roles = {PerfisAcesso.INSPETOR, PerfisAcesso.SUPERVISOR,
        PerfisAcesso.GERENTE, PerfisAcesso.ADMINISTRADOR, PerfisAcesso.COMITE, PerfisAcesso.CONSULTA_NAO_BLOQUEADOS,
        PerfisAcesso.CONSULTA_TUDO, PerfisAcesso.CONSULTA_RESUMO_NAO_BLOQUEADOS})
public class ConsultaHistoricoPage extends DefaultPageMenu {

    private final Form<?> form = new Form<Object>("formulario");
    private final ConsultaAvaliacaoRiscoControleVO consultaArc = new ConsultaAvaliacaoRiscoControleVO();
    private final ConsultaAnaliseQuantitativaAQTVO consultaAnef = new ConsultaAnaliseQuantitativaAQTVO();
    private PainelHistoricoAnef painelHistANEF;
    private PainelFiltroHistoricoAnef painelFiltroANEF;
    private PainelFiltroHistoricoArc painelFiltroARC;
    private PainelHistoricoArc painelHistARC;

    @Override
    protected void onInitialize() {
        super.onInitialize();
        addComponentes();
    }

    private void addComponentes() {
        historicoArc();
        historicoAnef();
        add(form);
    }

    private void historicoArc() {
        addPainelListagemArc();
        addPainelFiltroArc();
    }

    private void historicoAnef() {
        addPainelFiltroAnef();
        addPainelListagemAnef();
    }

    private void addPainelListagemArc() {
        painelHistARC = new PainelHistoricoArc("idPainelListagemArcs", consultaArc);
        form.addOrReplace(painelHistARC);
    }

    private void addPainelFiltroArc() {
        painelFiltroARC = new PainelFiltroHistoricoArc("idPainelFiltroHistoricoArc", consultaArc);
        form.addOrReplace(painelFiltroARC);
    }

    private void addPainelListagemAnef() {
        painelHistANEF = new PainelHistoricoAnef("idPainelListagemANEFs", consultaAnef);
        form.addOrReplace(painelHistANEF);
    }

    private void addPainelFiltroAnef() {
        painelFiltroANEF = new PainelFiltroHistoricoAnef("idPainelFiltroHistoricANEF", consultaAnef);
        form.addOrReplace(painelFiltroANEF);
    }

    public void atualizarListaANEFs(AjaxRequestTarget target) {
        addPainelListagemAnef();
        target.add(painelHistANEF);
    }

    public void limparFiltroANEFs(AjaxRequestTarget target) {
        target.add(painelFiltroANEF);
    }

    public void atualizarListaARCs(AjaxRequestTarget target) {
        target.add(painelHistARC.getTabela());
    }
    
    public void limparFiltroARCs(AjaxRequestTarget target) {
        target.add(painelFiltroARC);
    }

    @Override
    public String getTitulo() {
        return "Meu histórico";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCodigoTela() {
        return "APSFW0701";
    }

    public Form<?> getForm() {
        return form;
    }

    public ConsultaAvaliacaoRiscoControleVO getConsulta() {
        return consultaArc;
    }

}
