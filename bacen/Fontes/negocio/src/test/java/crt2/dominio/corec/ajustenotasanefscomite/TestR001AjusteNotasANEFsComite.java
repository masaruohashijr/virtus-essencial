package crt2.dominio.corec.ajustenotasanefscomite;

import java.util.List;

import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.VersaoPerfilRisco;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoObjetoVersionadorEnum;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.src.mediator.VersaoPerfilRiscoMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR001AjusteNotasANEFsComite extends ConfiguracaoTestesNegocio {

    public List<AnaliseQuantitativaAQT> consultaANEFsAjusteComite(int cicloPK) {
        PerfilRisco perfilAtual = PerfilRiscoMediator.get().obterPerfilRiscoAtual(cicloPK);

        List<VersaoPerfilRisco> listaVersoesPerfilRisco =
                VersaoPerfilRiscoMediator.get().buscarVersoesPerfilRisco(perfilAtual.getPk(),
                        TipoObjetoVersionadorEnum.AQT);
        return AnaliseQuantitativaAQTMediator.get().buscarAQTsPerfilRisco(listaVersoesPerfilRisco);

    }

    public String getID(AnaliseQuantitativaAQT arc) {
        return arc.getPk().toString();
    }

    public String getParametro(AnaliseQuantitativaAQT anef) {
        return anef.getParametroAQT().getDescricao();
    }

    public String getNotaSupervisor(AnaliseQuantitativaAQT anef) {
        return anef.getNotaCorecAnteriorOuVigente();
    }

}
