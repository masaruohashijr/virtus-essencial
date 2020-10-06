package crt2.dominio.analisequantitativa.designaraqt;

import br.gov.bcb.sisaps.adaptadores.pessoa.BcPessoaAdapter;
import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.DesignacaoAQTMediator;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import crt2.ConfiguracaoTestesNegocio;

public class TestR003DesignarAQT extends ConfiguracaoTestesNegocio {

    private static final String VAZIO = "<vazio>";
    
    private int anef;
    private AnaliseQuantitativaAQT analiseQuantitativaAQT;
    private String matriculaServidorEquipe;
    private String matriculaOutroServidor;

    public String mensagemEsperadaAoDesignar() {
        erro = null;
        ServidorVO servidorEquipe = null;
        ServidorVO servidorUnidade = null;

        if (matriculaServidorEquipe != null && !matriculaServidorEquipe.equals(VAZIO)) {
            servidorEquipe = BcPessoaAdapter.get().buscarServidor(matriculaServidorEquipe);
        }

        if (matriculaOutroServidor != null && !matriculaOutroServidor.equals(VAZIO)) {
            servidorUnidade = BcPessoaAdapter.get().buscarServidor(matriculaOutroServidor);
        }

        try {
            DesignacaoAQTMediator.get().incluir(AnaliseQuantitativaAQTMediator.get().buscar(anef), servidorEquipe,
                    servidorUnidade);
        } catch (NegocioException e) {
            erro = e;
        }
        return erro == null ? "" : erro.getMessage();
        
    }
    
    
    public String estado() {

        analiseQuantitativaAQT = AnaliseQuantitativaAQTMediator.get().buscar(anef);

        return analiseQuantitativaAQT.getEstado().getDescricao();
    }


    public int getAnef() {
        return anef;
    }


    public void setAnef(int anef) {
        this.anef = anef;
    }


    public String getMatriculaServidorEquipe() {
        return matriculaServidorEquipe;
    }


    public void setMatriculaServidorEquipe(String matriculaServidorEquipe) {
        this.matriculaServidorEquipe = matriculaServidorEquipe;
    }


    public String getMatriculaOutroServidor() {
        return matriculaOutroServidor;
    }


    public void setMatriculaOutroServidor(String matriculaOutroServidor) {
        this.matriculaOutroServidor = matriculaOutroServidor;
    }
    
    
    
    
    
    
   

}
