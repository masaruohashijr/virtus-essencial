package crt2.dominio.analisequantitativa.gerenciarquadroposicaofinanceira;

import java.util.List;

import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.DataBaseES;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativa.DataBaseESMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR002GerenciarQuadroPosicaoFinanceira extends ConfiguracaoTestesNegocio {

    public List<DataBaseES> consultarDatasBaseDisponiveis(Integer perfil) {
        PerfilRisco perfilRisco = PerfilRiscoMediator.get().obterPerfilRiscoPorPk(Integer.valueOf(perfil));
        return DataBaseESMediator.get().consultarDatasBaseDisponiveis(perfilRisco);
    }

    public String getDataBase(DataBaseES dtb) {
        return dtb.getDataBaseFormatada();
    }

}
