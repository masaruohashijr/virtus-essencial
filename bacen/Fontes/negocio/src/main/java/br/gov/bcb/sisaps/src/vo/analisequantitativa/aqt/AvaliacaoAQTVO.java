package br.gov.bcb.sisaps.src.vo.analisequantitativa.aqt;

import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroNotaAQT;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfisNotificacaoEnum;
import br.gov.bcb.sisaps.src.vo.ObjetoPersistenteVO;

public class AvaliacaoAQTVO extends ObjetoPersistenteVO {

    public static final String CAMPO_ID = "AVT_ID";
    private AnaliseQuantitativaAQT analiseQuantitativaAQT;
    private ParametroNotaAQT notaSupervisor;
    private PerfisNotificacaoEnum perfil;
    private String justificativa;

    public AnaliseQuantitativaAQT getAnaliseQuantitativaAQT() {
        return analiseQuantitativaAQT;
    }

    public void setAnaliseQuantitativaAQT(AnaliseQuantitativaAQT analiseQuantitativaAQT) {
        this.analiseQuantitativaAQT = analiseQuantitativaAQT;
    }

    public ParametroNotaAQT getNotaSupervisor() {
        return notaSupervisor;
    }

    public void setNotaSupervisor(ParametroNotaAQT notaSupervisor) {
        this.notaSupervisor = notaSupervisor;
    }

    public PerfisNotificacaoEnum getPerfil() {
        return perfil;
    }

    public void setPerfil(PerfisNotificacaoEnum perfil) {
        this.perfil = perfil;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }

}
