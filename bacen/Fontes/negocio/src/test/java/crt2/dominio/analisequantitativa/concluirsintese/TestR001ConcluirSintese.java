package crt2.dominio.analisequantitativa.concluirsintese;

import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.SinteseDeRiscoAQT;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.SinteseDeRiscoAQTMediator;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import crt2.ConfiguracaoTestesNegocio;

public class TestR001ConcluirSintese extends ConfiguracaoTestesNegocio {
    private int anef;
    private AnaliseQuantitativaAQT analiseQuantitativaAQT;

    public String tituloDoBotao() {
        analiseQuantitativaAQT = AnaliseQuantitativaAQTMediator.get().buscar(anef);
        return SinteseDeRiscoAQTMediator.get().tituloBotaoConcluirSintese(analiseQuantitativaAQT.getParametroAQT(),
                analiseQuantitativaAQT.getCiclo());
    }

    public String estado() {
        analiseQuantitativaAQT = AnaliseQuantitativaAQTMediator.get().buscar(anef);
        return analiseQuantitativaAQT.getEstado().getDescricao();
    }

    public String parametroAnef() {
        analiseQuantitativaAQT = AnaliseQuantitativaAQTMediator.get().buscar(anef);
        return analiseQuantitativaAQT.getParametroAQT().getDescricao();
    }

    public String possuiSinteseRascunho() {
        analiseQuantitativaAQT = AnaliseQuantitativaAQTMediator.get().buscar(anef);
        return SimNaoEnum.getTipo(
                SinteseDeRiscoAQTMediator.get().getUltimaSinteseParametroAQTEdicao(
                        analiseQuantitativaAQT.getParametroAQT(), analiseQuantitativaAQT.getCiclo()) != null)
                .getDescricao();
    }

    public String sinteseRacunhoIgualAVigente() {
        analiseQuantitativaAQT = AnaliseQuantitativaAQTMediator.get().buscar(anef);

        SinteseDeRiscoAQT vigente =
                SinteseDeRiscoAQTMediator.get().getUltimaSinteseVigente(analiseQuantitativaAQT.getParametroAQT(),
                        analiseQuantitativaAQT.getCiclo());

        SinteseDeRiscoAQT edicao =
                SinteseDeRiscoAQTMediator.get().getUltimaSinteseParametroAQTEdicao(
                        analiseQuantitativaAQT.getParametroAQT(), analiseQuantitativaAQT.getCiclo());

        return SimNaoEnum.getTipo(SinteseDeRiscoAQTMediator.get().sinteseRascunhoIgualVigente(vigente, edicao))
                .getDescricao();
    }

    public String botaoHabilitado() {

        analiseQuantitativaAQT = AnaliseQuantitativaAQTMediator.get().buscar(anef);

        SinteseDeRiscoAQT vigente =
                SinteseDeRiscoAQTMediator.get().getUltimaSinteseVigente(analiseQuantitativaAQT.getParametroAQT(),
                        analiseQuantitativaAQT.getCiclo());

        SinteseDeRiscoAQT edicao =
                SinteseDeRiscoAQTMediator.get().getUltimaSinteseParametroAQTEdicao(
                        analiseQuantitativaAQT.getParametroAQT(), analiseQuantitativaAQT.getCiclo());

        return SimNaoEnum.getTipo(
                SinteseDeRiscoAQTMediator.get().botaoConcluirHabilitado(analiseQuantitativaAQT, vigente, edicao))
                .getDescricao();

    }

    public int getAnef() {
        return anef;
    }

    public void setAnef(int anef) {
        this.anef = anef;
    }

}
