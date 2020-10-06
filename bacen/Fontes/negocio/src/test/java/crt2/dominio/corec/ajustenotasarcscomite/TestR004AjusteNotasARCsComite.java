package crt2.dominio.corec.ajustenotasarcscomite;

import java.util.List;

import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.ParametroNota;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.ParametroNotaMediator;
import crt2.ConfiguracaoTestesNegocio;


public class TestR004AjusteNotasARCsComite extends ConfiguracaoTestesNegocio {
    
    public List<ParametroNota> buscarNotasAjusteCorec(int cicloPK, int arcPK) {
        AvaliacaoRiscoControle arc = AvaliacaoRiscoControleMediator.get().buscar(arcPK);
        Ciclo ciclo = CicloMediator.get().loadPK(cicloPK);

        return ParametroNotaMediator.get().buscarParametrosNotaARCCorec(arc,
                ciclo.getMetodologia().getPk());
    }

    public String getNota(ParametroNota paramentroNota) {
        return paramentroNota.getDescricaoValor();
    }

}
