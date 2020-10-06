package crt2.dominio.corec.verificacaoregrasiniciarcorec;

import java.util.ArrayList;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.VerificarPendenciasCorecMediator;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;
import crt2.ConfiguracaoTestesNegocio;
public class TestR005VerificacaoRegrasIniciarCorec extends ConfiguracaoTestesNegocio {

    private Integer ciclo;

    public String botaoHabilitado() {
        Ciclo cicloBase = CicloMediator.get().buscarCicloPorPK(ciclo);
        ArrayList<ErrorMessage> erros = 
                VerificarPendenciasCorecMediator.get().mostraAlertaBotaoVerificarPendencia(cicloBase);
        return SimNaoEnum.getTipo(erros.isEmpty()).getDescricao();
    }

    public Integer getCiclo() {
        return ciclo;
    }

    public void setCiclo(Integer ciclo) {
        this.ciclo = ciclo;
    }

}
