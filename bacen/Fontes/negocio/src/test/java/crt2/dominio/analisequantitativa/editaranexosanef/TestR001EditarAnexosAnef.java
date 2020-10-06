package crt2.dominio.analisequantitativa.editaranexosanef;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnexoAQTMediator;
import br.gov.bcb.sisaps.src.validacao.RegraAnexosValidacaoPDFA4;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import crt2.ConfiguracaoTestesNegocio;

public class TestR001EditarAnexosAnef extends ConfiguracaoTestesNegocio {
    private String anef;
    private String anexo;
    private String tipoAnexo;

    public String mensagemDeAlerta() {
        String retorno = null;
        AnaliseQuantitativaAQT aqt = AnaliseQuantitativaAQTMediator.get().buscar(Integer.valueOf(anef));
        try {
            RegraAnexosValidacaoPDFA4 validacao = new RegraAnexosValidacaoPDFA4();
            validacao.validar(getInputStream(anexo), anexo);
            AnexoAQTMediator.get().anexarArquivo(aqt.getCiclo(), aqt, anexo, null, false, null, false);
        } catch (FileNotFoundException e) {
            retorno = "Nenhum arquivo foi selecionado.";
        } catch (NegocioException e) {
            retorno = e.getMessage();
        }
        if (retorno == null || "null".equals(retorno)) {
            retorno = "";
        }
        return retorno;
    }

    private InputStream getInputStream(String caminhoAnexo) throws FileNotFoundException {
        File arquivo = new File(caminhoAnexo);
        return new FileInputStream(arquivo);
    }

    public void setAnef(String anef) {
        this.anef = anef;
    }

    public void setAnexo(String anexo) {
        this.anexo = anexo;
    }

    public void setTipoAnexo(String tipoAnexo) {
        this.tipoAnexo = tipoAnexo;
    }

    public String getAnef() {
        return anef;
    }

    public String getAnexo() {
        return anexo;
    }

    public String getTipoAnexo() {
        return tipoAnexo;
    }
}
