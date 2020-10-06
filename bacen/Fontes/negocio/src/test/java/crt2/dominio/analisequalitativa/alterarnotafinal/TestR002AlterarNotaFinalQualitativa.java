package crt2.dominio.analisequalitativa.alterarnotafinal;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.NotaMatriz;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.NotaMatrizMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import crt2.ConfiguracaoTestesNegocio;

public class TestR002AlterarNotaFinalQualitativa extends ConfiguracaoTestesNegocio {

    public String exibirBotaoConfirmar(Integer idCiclo) {
        Ciclo ciclo = CicloMediator.get().buscarCicloPorPK(idCiclo);
        NotaMatriz notaRascunho = NotaMatrizMediator.get().buscarNotaMatrizRascunho(ciclo.getMatriz());
        PerfilRisco perfilRisco = PerfilRiscoMediator.get().obterPerfilRiscoAtual(ciclo.getPk());
        NotaMatriz notaVigente = NotaMatrizMediator.get().buscarPorPerfilRisco(perfilRisco.getPk());
        return SimNaoEnum.getTipo(NotaMatrizMediator.get().isHabilitarbotaoConfirmar(notaRascunho, notaVigente))
                .getDescricao();
    }

}
