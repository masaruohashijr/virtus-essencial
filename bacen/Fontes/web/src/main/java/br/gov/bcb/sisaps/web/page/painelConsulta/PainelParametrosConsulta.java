package br.gov.bcb.sisaps.web.page.painelConsulta;

import br.gov.bcb.sisaps.src.vo.ConsultaEntidadeSupervisionavelVO;

public class PainelParametrosConsulta extends AbstractPainelParametrosConsulta {

    public PainelParametrosConsulta(String id, ConsultaEntidadeSupervisionavelVO consultaParametro,
            ConsultaEntidadeSupervisionavelVO consultaResultado) {
        super(id, consultaParametro, consultaResultado);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        addTextNome();
        addComboUnidade();
        addComboSubUnidade();
        addComboSubUnidade2();
        addComboSubUnidade3();
        addSupervisor();
        addPrioridade();
        addBotoes();
    }

}
