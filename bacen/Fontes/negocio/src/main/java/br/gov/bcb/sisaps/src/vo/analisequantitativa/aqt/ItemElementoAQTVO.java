package br.gov.bcb.sisaps.src.vo.analisequantitativa.aqt;

import br.gov.bcb.sisaps.src.dominio.Documento;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ElementoAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroItemElementoAQT;
import br.gov.bcb.sisaps.src.vo.ObjetoPersistenteVO;

public class ItemElementoAQTVO extends ObjetoPersistenteVO {

    public static final String CAMPO_ID = "ITA_ID";
    private ElementoAQT elemento;
    private ParametroItemElementoAQT parametroItemElemento;
    private Documento documento;

    public ElementoAQT getElemento() {
        return elemento;
    }

    public void setElemento(ElementoAQT elemento) {
        this.elemento = elemento;
    }

    public ParametroItemElementoAQT getParametroItemElemento() {
        return parametroItemElemento;
    }

    public void setParametroItemElemento(ParametroItemElementoAQT parametroItemElemento) {
        this.parametroItemElemento = parametroItemElemento;
    }

    public Documento getDocumento() {
        return documento;
    }

    public void setDocumento(Documento documento) {
        this.documento = documento;
    }

}
