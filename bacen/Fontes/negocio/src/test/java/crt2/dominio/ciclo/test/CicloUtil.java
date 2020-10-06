package crt2.dominio.ciclo.test;

import java.util.Date;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.EntidadeSupervisionavel;
import br.gov.bcb.sisaps.src.dominio.Metodologia;

public class CicloUtil {

    public static Ciclo criar(String codigoPTPE, EntidadeSupervisionavel entidadeSupervisionavel,
            Metodologia metodologia, Date dataInclusao) {
        Ciclo ciclo = new Ciclo();
        ciclo.setCodigoPTPE(codigoPTPE);
        ciclo.setDataInicio(dataInclusao);
        ciclo.setEntidadeSupervisionavel(entidadeSupervisionavel);
        ciclo.setMetodologia(metodologia);
        return ciclo;
    }
}
