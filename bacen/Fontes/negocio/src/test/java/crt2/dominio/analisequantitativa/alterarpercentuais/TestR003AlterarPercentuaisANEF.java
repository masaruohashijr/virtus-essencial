package crt2.dominio.analisequantitativa.alterarpercentuais;

import java.util.ArrayList;
import java.util.List;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.PesoAQT;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.PesoAQTMediator;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import crt2.ConfiguracaoTestesNegocio;

public class TestR003AlterarPercentuaisANEF extends ConfiguracaoTestesNegocio {

    private List<PesoAQT> listaPesoVigente = new ArrayList<PesoAQT>();
    private List<PesoAQT> listaPesoRascunhos = new ArrayList<PesoAQT>();

    public String exibirBotaoConfirmar(Integer idCiclo) {
        Ciclo ciclo = CicloMediator.get().buscarCicloPorPK(idCiclo);
        listaPesoRascunhos = PesoAQTMediator.get().montarListaPesoEmEdicao(ciclo);
        listaPesoVigente = PesoAQTMediator.get().montarListaPesoVigente(ciclo);
        boolean retorno = PesoAQTMediator.get().exibirBotaoConfirmar(listaPesoVigente, listaPesoRascunhos);
        listaPesoVigente.clear();
        listaPesoRascunhos.clear();
        return SimNaoEnum.getTipo(retorno).getDescricao();

    }

}
