package crt2.dominio.analisequantitativa.alterarnotafinal;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.NotaAjustadaAEF;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.NotaAjustadaAEFMediator;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import crt2.ConfiguracaoTestesNegocio;

public class TestR002AlterarNotaFinalAEF extends ConfiguracaoTestesNegocio {

    public String exibirBotaoConfirmar(Integer idCiclo) {
        Ciclo ciclo = CicloMediator.get().buscarCicloPorPK(idCiclo);
        NotaAjustadaAEF notaRascunho = NotaAjustadaAEFMediator.get().buscarNotaAjustadaRascunho(ciclo);
        PerfilRisco perfilRisco = PerfilRiscoMediator.get().obterPerfilRiscoAtual(ciclo.getPk());
        NotaAjustadaAEF notaVigente = NotaAjustadaAEFMediator.get().buscarNotaAjustadaAEF(ciclo, perfilRisco);
        return SimNaoEnum.getTipo(NotaAjustadaAEFMediator.get().isHabilitarbotaoConfirmar(notaRascunho, notaVigente))
                .getDescricao();
    }

}
