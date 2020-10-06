package br.gov.bcb.sisaps.src.vo.analisequantitativa.aqt;

import java.util.List;

import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ItemElementoAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroElementoAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroNotaAQT;
import br.gov.bcb.sisaps.src.vo.ObjetoPersistenteVO;

public class ElementoAQTVO extends ObjetoPersistenteVO {

    public static final String CAMPO_ID = "ELA_ID";
    private ParametroElementoAQT parametroElemento;
    private AnaliseQuantitativaAQT analiseQuantitativaAQT;
    private ParametroNotaAQT parametroNotaInspetor;
    private ParametroNotaAQT parametroNotaSupervisor;
    private String justificativaSupervisor;
    private List<ItemElementoAQT> itensElemento;

    public ParametroElementoAQT getParametroElemento() {
        return parametroElemento;
    }

    public void setParametroElemento(ParametroElementoAQT parametroElemento) {
        this.parametroElemento = parametroElemento;
    }

    public AnaliseQuantitativaAQT getAnaliseQuantitativaAQT() {
        return analiseQuantitativaAQT;
    }

    public void setAnaliseQuantitativaAQT(AnaliseQuantitativaAQT analiseQuantitativaAQT) {
        this.analiseQuantitativaAQT = analiseQuantitativaAQT;
    }

    public ParametroNotaAQT getParametroNotaInspetor() {
        return parametroNotaInspetor;
    }

    public void setParametroNotaInspetor(ParametroNotaAQT parametroNotaInspetor) {
        this.parametroNotaInspetor = parametroNotaInspetor;
    }

    public ParametroNotaAQT getParametroNotaSupervisor() {
        return parametroNotaSupervisor;
    }

    public void setParametroNotaSupervisor(ParametroNotaAQT parametroNotaSupervisor) {
        this.parametroNotaSupervisor = parametroNotaSupervisor;
    }

    public String getJustificativaSupervisor() {
        return justificativaSupervisor;
    }

    public void setJustificativaSupervisor(String justificativaSupervisor) {
        this.justificativaSupervisor = justificativaSupervisor;
    }

    public List<ItemElementoAQT> getItensElemento() {
        return itensElemento;
    }

    public void setItensElemento(List<ItemElementoAQT> itensElemento) {
        this.itensElemento = itensElemento;
    }

}
