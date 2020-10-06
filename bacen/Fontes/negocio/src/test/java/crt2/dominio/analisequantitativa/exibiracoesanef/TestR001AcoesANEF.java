package crt2.dominio.analisequantitativa.exibiracoesanef;

import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.DesignacaoAQTMediator;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import crt2.ConfiguracaoTestesNegocio;

public class TestR001AcoesANEF extends ConfiguracaoTestesNegocio {

    private int anef;
    private AnaliseQuantitativaAQT analiseQuantitativaAQT;

    public String designacaoPodeSerExcluida() {
        consultarAnef();
        return SimNaoEnum.getTipo(DesignacaoAQTMediator.get().podeExcluirDesignar(analiseQuantitativaAQT))
                .getDescricao();
    }

    public String podeSerDelegado() {
        consultarAnef();
        return SimNaoEnum.getTipo(AnaliseQuantitativaAQTMediator.get().podeDelegar(analiseQuantitativaAQT))
                .getDescricao();
    }

    public String podeSerDesignado() {
        consultarAnef();
        return SimNaoEnum.getTipo(DesignacaoAQTMediator.get().podeDesignar(analiseQuantitativaAQT)).getDescricao();
    }

    private void consultarAnef() {
        analiseQuantitativaAQT = AnaliseQuantitativaAQTMediator.get().buscar(anef);
    }

    public String podeSerRetomado() {
        consultarAnef();
        return SimNaoEnum.getTipo(AnaliseQuantitativaAQTMediator.get().podeAnalisar(analiseQuantitativaAQT))
                .getDescricao();
    }

    public String estado() {

        consultarAnef();

        return analiseQuantitativaAQT.getEstado().getDescricao();
    }

    public String grupoPodeSerExibido() {
        return SimNaoEnum.getTipo(
                AnaliseQuantitativaAQTMediator.get().podeExibirPainelAcoesAnef(analiseQuantitativaAQT)).getDescricao();
    }

    public int getAnef() {
        return anef;
    }

    public void setAnef(int anef) {
        this.anef = anef;
    }

}
