package crt2.dominio.analisequantitativa.consultaranef;

import java.util.List;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR001ConsultaANEF extends ConfiguracaoTestesNegocio {

    public List<AnaliseQuantitativaAQT> isConsultaVersao(int anef) {
        AnaliseQuantitativaAQT aqt = AnaliseQuantitativaAQTMediator.get().buscar(anef);
        Ciclo ciclo = CicloMediator.get().loadPK(aqt.getCiclo().getPk());

        return AnaliseQuantitativaAQTMediator.get().listarANEFConsulta(aqt.getParametroAQT(), ciclo);
    }

    public String getData(AnaliseQuantitativaAQT aqt) {
        AnaliseQuantitativaAQT anef = AnaliseQuantitativaAQTMediator.get().buscar(aqt.getPk());
        return anef.getDataVersaoConsulta(PerfilRiscoMediator.get().obterPerfilRiscoAtual(anef.getCiclo().getPk()));
    }

    public String getPK(AnaliseQuantitativaAQT aqt) {
        return aqt.getPk().toString();
    }
}
