package crt2.dominio.corec.impactonotacorecversionamentoarc;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.SinteseDeRisco;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.SinteseDeRiscoMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR001ImpactoNotaCorecVersionamentoARC extends ConfiguracaoTestesNegocio {

    public void concluirSinteseARQ(int idCiclo, int idSintese) {
        Ciclo cicloN = CicloMediator.get().buscarCicloPorPK(idCiclo);
        SinteseDeRisco sinteseNova = SinteseDeRiscoMediator.get().buscarSinteseMatrizPorPk(idSintese);
        SinteseDeRiscoMediator.get().concluirNovaSinteseMatrizVigenteEPublicarARCs(sinteseNova, cicloN);
    }
}
