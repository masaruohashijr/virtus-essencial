package crt2.dominio.analisequantitativa.editaranexosanef;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ItemElementoAQT;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnexoItemElementoAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.ItemElementoAQTMediator;
import br.gov.bcb.sisaps.src.validacao.RegraAnexosValidacaoPDFA4;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import crt2.ConfiguracaoTestesNegocio;

public class TestR002EditarAnexosAnef extends ConfiguracaoTestesNegocio {
    private String anexo;
    private String tipoAnexo;
    private String itemElementoAnef;

    public String mensagemDeAlerta() {
        String retorno = null;
        ItemElementoAQT item = ItemElementoAQTMediator.get().buscarPorPk(Integer.valueOf(itemElementoAnef));
        try {
            RegraAnexosValidacaoPDFA4 validacao = new RegraAnexosValidacaoPDFA4();
            validacao.validar(getInputStream(anexo), anexo);
            AnexoItemElementoAQTMediator.get().anexarArquivo(item, anexo, null, false);
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

    public void setAnexo(String anexo) {
        this.anexo = anexo;
    }

    public void setTipoAnexo(String tipoAnexo) {
        this.tipoAnexo = tipoAnexo;
    }


    public String getAnexo() {
        return anexo;
    }

    public String getTipoAnexo() {
        return tipoAnexo;
    }

    public String getItemElementoAnef() {
        return itemElementoAnef;
    }

    public void setItemElementoAnef(String itemElemento) {
        this.itemElementoAnef = itemElemento;
    }
}
