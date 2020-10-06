package crt2.dominio.analisequantitativa.analisarajustenotasanef;

import java.math.BigDecimal;

import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AvaliacaoAQT;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AvaliacaoAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.ParametroNotaAQTMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR002AnalisarAjusteNotasANEF extends ConfiguracaoTestesNegocio {

    private int anef;
    private String nota;
    private String justificativa;

    private AnaliseQuantitativaAQT analiseQuantitativaAQT;

    public String mensagem() {

        analiseQuantitativaAQT = AnaliseQuantitativaAQTMediator.get().buscar(anef);
        AvaliacaoAQT supervisorAqt;
        if (analiseQuantitativaAQT.avaliacaoSupervisor() == null) {
            supervisorAqt = new AvaliacaoAQT();
        } else {
            supervisorAqt = AvaliacaoAQTMediator.get().loadPK(analiseQuantitativaAQT.avaliacaoSupervisor().getPk());
        }

        if ("Selecione".equals(nota)) {
            supervisorAqt.setParametroNota(null);
            supervisorAqt.setJustificativa(null);
        } else {
            supervisorAqt.setParametroNota(ParametroNotaAQTMediator.get().buscarPorMetodologiaENota(
                    analiseQuantitativaAQT.getCiclo().getMetodologia(), new BigDecimal(nota)));
            supervisorAqt.setJustificativa(justificativa);
        }

        return AvaliacaoAQTMediator.get().salvarAterarAvalicaoSupervisor(analiseQuantitativaAQT, supervisorAqt);
    }

    public int getAnef() {
        return anef;
    }

    public void setAnef(int anef) {
        this.anef = anef;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }

}
