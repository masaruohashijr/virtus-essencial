package br.gov.bcb.sisaps.web.page.dominio.gestaoes;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;

import br.gov.bcb.sisaps.src.mediator.EntidadeSupervisionavelMediator;
import br.gov.bcb.sisaps.src.vo.ComponenteOrganizacionalVO;
import br.gov.bcb.sisaps.src.vo.ConsultaEntidadeSupervisionavelVO;
import br.gov.bcb.sisaps.web.page.DefaultPage;

public class GestaoES extends DefaultPage {

    private final ConsultaEntidadeSupervisionavelVO consultaParametro = new ConsultaEntidadeSupervisionavelVO();
    private final ConsultaEntidadeSupervisionavelVO consultaResultado = new ConsultaEntidadeSupervisionavelVO();
    private PainelResultadoConsultaEsAtivaGestaoES panielConsultaEsAtiva;
    private PainelParametrosConsultaGestaoES painelConsultaGestaoES;
    private Form<?> form;
    private boolean isIndicator;

    @Override
    protected void onInitialize() {
        super.onInitialize();
        consultaDefault();
        form = new Form<Object>("formulario");
        List<ComponenteOrganizacionalVO> lista = EntidadeSupervisionavelMediator.get().consultarUnidadesESsAtivas();
        if (lista.size() == 1) {
            consultaResultado.setLocalizacao(lista.get(0).getSigla());
            consultaResultado.setBuscarHierarquiaInferior(true);
        }

        painelConsultaGestaoES =
                new PainelParametrosConsultaGestaoES("painelParametros", consultaParametro, consultaResultado);
        form.add(painelConsultaGestaoES);
        panielConsultaEsAtiva = new PainelResultadoConsultaEsAtivaGestaoES("painelResultado", consultaResultado);
        form.add(panielConsultaEsAtiva);
        form.add(new LinkVoltar());
        addOrReplace(form);
    }

    private void consultaDefault() {
        consultaParametro.setPossuiPrioridade(true);
        consultaResultado.setPossuiPrioridade(true);
        consultaParametro.setAdministrador(true);
        consultaResultado.setBuscarHierarquiaInferior(true);
    }

    public void atualizarLista(AjaxRequestTarget target) {
        target.add(panielConsultaEsAtiva.getTabela());
        target.add(painelConsultaGestaoES);
    }

    public void atualizar(AjaxRequestTarget target) {
        panielConsultaEsAtiva.setConsultaEntidadeSupervisionavelVO(consultaParametro);
        target.add(panielConsultaEsAtiva.getTabela());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitulo() {
        return "Gestão de ESs";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCodigoTela() {
        return "APSFW0503";
    }

    public boolean isIndicator() {
        return isIndicator;
    }

    public void setIndicator(boolean isIndicator) {
        this.isIndicator = isIndicator;
    }

    public PainelResultadoConsultaEsAtivaGestaoES getPanielConsultaEsAtiva() {
        return panielConsultaEsAtiva;
    }

    public void setPanielConsultaEsAtiva(PainelResultadoConsultaEsAtivaGestaoES panielConsultaEsAtiva) {
        this.panielConsultaEsAtiva = panielConsultaEsAtiva;
    }

    public ConsultaEntidadeSupervisionavelVO getConsultaResultado() {
        return consultaResultado;
    }

}
