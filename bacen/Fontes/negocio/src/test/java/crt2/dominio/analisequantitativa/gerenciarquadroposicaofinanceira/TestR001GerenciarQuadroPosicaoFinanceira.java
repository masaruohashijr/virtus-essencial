package crt2.dominio.analisequantitativa.gerenciarquadroposicaofinanceira;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import br.gov.bcb.sisaps.src.dominio.analisequantitativa.DataBaseES;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.LayoutContaAnaliseQuantitativa;
import br.gov.bcb.sisaps.src.mediator.analisequantitativa.LayoutContaAnaliseQuantitativaMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR001GerenciarQuadroPosicaoFinanceira extends ConfiguracaoTestesNegocio {
    private String database;

    public String quallayoututilizar() {
        DataBaseES dataBaseES = new DataBaseES();
        String data = null;

        if (!getDatabase().isEmpty()) {
            String[] databaseSplit = getDatabase().split("/");
            data = databaseSplit[1].concat(databaseSplit[0]);
        }

        dataBaseES.setCodigoDataBase(Integer.valueOf(data));
        List<LayoutContaAnaliseQuantitativa> layoutContas =
                LayoutContaAnaliseQuantitativaMediator.get().obterLayout(dataBaseES);
        if (CollectionUtils.isNotEmpty(layoutContas)) {
            return layoutContas.get(0).getLayoutAnaliseQuantitativa().getPk().toString();
        }
        return "";
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

}
