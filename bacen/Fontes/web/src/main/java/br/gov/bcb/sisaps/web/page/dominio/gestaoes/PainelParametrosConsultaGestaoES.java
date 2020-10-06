package br.gov.bcb.sisaps.web.page.dominio.gestaoes;

import br.gov.bcb.sisaps.src.vo.ConsultaCicloVO;
import br.gov.bcb.sisaps.src.vo.ConsultaEntidadeSupervisionavelVO;
import br.gov.bcb.sisaps.web.page.painelConsulta.AbstractPainelParametrosConsulta;

public class PainelParametrosConsultaGestaoES extends AbstractPainelParametrosConsulta {

    public PainelParametrosConsultaGestaoES(String id, ConsultaEntidadeSupervisionavelVO consultaParametro,
            ConsultaEntidadeSupervisionavelVO consultaResultado) {
        super(id, consultaParametro, consultaResultado);
    }

    public PainelParametrosConsultaGestaoES(String id, ConsultaEntidadeSupervisionavelVO consultaParametro,
            ConsultaEntidadeSupervisionavelVO consultaResultado, ConsultaCicloVO consultaCiclo) {
        super(id, consultaParametro, consultaResultado, consultaCiclo);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        addDatas();
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
