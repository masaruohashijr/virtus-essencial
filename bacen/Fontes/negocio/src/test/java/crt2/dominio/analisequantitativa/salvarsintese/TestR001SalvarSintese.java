package crt2.dominio.analisequantitativa.salvarsintese;

import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.SinteseDeRiscoAQT;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.SinteseDeRiscoAQTMediator;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import crt2.ConfiguracaoTestesNegocio;

public class TestR001SalvarSintese extends ConfiguracaoTestesNegocio {
    private Integer sintese;
    private String textoSintese;
    
    public String mensagemEsperadaAoSalvar() {
        String retorno = null;
        try {
            SinteseDeRiscoAQT sinteseAQT = SinteseDeRiscoAQTMediator.get().obterSinteseRiscoPorPk(sintese);
            sinteseAQT.setJustificativa(textoSintese);
            retorno = SinteseDeRiscoAQTMediator.get().salvarOuAtualizarSintese(sinteseAQT).trim();
        } catch (NegocioException e) {
            retorno = e.getMensagens().get(0).getMessage();
        }
        
        return retorno;
    }

    public Integer getSintese() {
        return sintese;
    }

    public void setSintese(Integer sintese) {
        this.sintese = sintese;
    }

    public String getTextoSintese() {
        return textoSintese;
    }

    public void setTextoSintese(String textoSintese) {
        this.textoSintese = textoSintese;
    }
    
    
    
    
}
