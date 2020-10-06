/*
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software e confidencial e propriedade do Banco Central do Brasil.
 * Nao e permitida sua distribuicao ou divulgacao do seu conteudo sem
 * expressa autorizacao do Banco Central.
 * Este arquivo contem informacoes proprietarias.
 */

package br.gov.bcb.sisaps.web.page.painelConsulta;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.markup.html.form.Form;

import br.gov.bcb.sisaps.integracao.seguranca.PerfisAcesso;
import br.gov.bcb.sisaps.src.mediator.EntidadeSupervisionavelMediator;
import br.gov.bcb.sisaps.src.vo.ComponenteOrganizacionalVO;
import br.gov.bcb.sisaps.src.vo.ConsultaEntidadeSupervisionavelVO;
import br.gov.bcb.sisaps.web.page.DefaultPageMenu;
import br.gov.bcb.sisaps.web.page.componentes.autorizacao.anotacao.SisApsAuthorizeAction;

@SisApsAuthorizeAction(action = Action.RENDER, roles = {PerfisAcesso.CONSULTA_TUDO,
        PerfisAcesso.CONSULTA_NAO_BLOQUEADOS, PerfisAcesso.CONSULTA_RESUMO_NAO_BLOQUEADOS})
public class PerfilConsultaPage extends DefaultPageMenu {

    private static final String PAINEL_DO_CONSULTA = "Painel de consulta";
    private ConsultaEntidadeSupervisionavelVO consultaParametro = new ConsultaEntidadeSupervisionavelVO();
    private ConsultaEntidadeSupervisionavelVO consultaResultado = new ConsultaEntidadeSupervisionavelVO();
    private PainelResultadoConsultaEsAtiva panielConsultaEsAtiva;

    @Override
    protected void onInitialize() {
        super.onInitialize();
        consultaParametro.setPossuiPrioridade(true);
        consultaResultado.setPossuiPrioridade(true);
        List<ComponenteOrganizacionalVO> lista = EntidadeSupervisionavelMediator.get().consultarUnidadesESsAtivas();
        if (lista.size() == 1) {
            consultaResultado.setLocalizacao(lista.get(0).getSigla());
            consultaResultado.setBuscarHierarquiaInferior(true);
        }
        Form<?> form = new Form<Object>("formulario");
        form.add(new PainelParametrosConsulta("painelParametros", consultaParametro, consultaResultado));
        panielConsultaEsAtiva = new PainelResultadoConsultaEsAtiva("painelResultado", consultaResultado, true);
        form.add(panielConsultaEsAtiva);
        add(form);
    }

    public void atualizar(AjaxRequestTarget target) {
        target.add(panielConsultaEsAtiva);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitulo() {
        return PAINEL_DO_CONSULTA;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCodigoTela() {
        return "APSFW0401";
    }
}
