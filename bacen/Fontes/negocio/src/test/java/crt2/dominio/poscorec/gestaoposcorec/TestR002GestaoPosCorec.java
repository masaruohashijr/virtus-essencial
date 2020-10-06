package crt2.dominio.poscorec.gestaoposcorec;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.EntidadeSupervisionavel;
import br.gov.bcb.sisaps.src.mediator.AnexoPosCorecMediator;
import br.gov.bcb.sisaps.src.validacao.RegraAnexosValidacaoPDFA4;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import crt2.ConfiguracaoTestesNegocio;

public class TestR002GestaoPosCorec extends ConfiguracaoTestesNegocio {
    private int ciclo;
    private String anexo;
    private String tipoAnexo;
    private String dataEntregaOficio;

    public String mensagemDeAlerta() {
        String retorno = "";
        Ciclo cicloVo = new Ciclo();
        cicloVo.setPk(ciclo);
        cicloVo.setEntidadeSupervisionavel(new EntidadeSupervisionavel());
        cicloVo.getEntidadeSupervisionavel().setPk(1);
        try {
            RegraAnexosValidacaoPDFA4 validacao = new RegraAnexosValidacaoPDFA4();
            validacao.validar(getInputStream(ciclo, anexo), anexo);
            retorno =
                    AnexoPosCorecMediator.get().anexarArquivo(cicloVo, "Ofício", getDataEntregaOficio(), anexo,
                            getInputStream(ciclo, anexo));
        } catch (FileNotFoundException e) {
            retorno = e.getMessage();
        } catch (NegocioException e) {
            retorno = e.getMessage();
            retorno = retorno.replace("[", "");
            retorno = retorno.replace("]", "");
        }
        if (retorno == null || "null".equals(retorno)) {
            retorno = "";
        }
        return retorno;
    }

    private InputStream getInputStream(Integer idCiclo, String caminhoAnexo) throws FileNotFoundException {
        File arquivo = new File(caminhoAnexo);
        return new FileInputStream(arquivo);
    }

    public int getCiclo() {
        return ciclo;
    }

    public void setCiclo(int ciclo) {
        this.ciclo = ciclo;
    }

    public void setAnexo(String anexo) {
        this.anexo = anexo;
    }

    public String getAnexo() {
        return anexo;
    }

    public void setTipoAnexo(String tipo) {
        this.tipoAnexo = tipo;
    }

    public String getTipoAnexo() {
        return tipoAnexo;
    }

    public String getDataEntregaOficio() {
        return dataEntregaOficio;
    }

    public void setDataEntregaOficio(String dataEntregaOficio) {
        String data = dataEntregaOficio;
        data = data.replace(".", "/");
        this.dataEntregaOficio = data;
    }

}
