package crt2.dominio.analisequantitativa.gerenciarquadroposicaofinanceira;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import br.gov.bcb.sisaps.src.dominio.AnexoQuadroPosicaoFinanceira;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.QuadroPosicaoFinanceira;
import br.gov.bcb.sisaps.src.mediator.AnexoQuadroPosicaoFinanceiraMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativa.QuadroPosicaoFinanceiraMediator;
import br.gov.bcb.sisaps.src.validacao.RegraAnexosValidacaoPDFA4;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import crt2.ConfiguracaoTestesNegocio;

public class TestR004GerenciarQuadroPosicaoFinanceira extends ConfiguracaoTestesNegocio {
    private String nomeArquivo;
    private Integer quadro;

    public String mensagemDeAlerta() {
        String retorno = null;
        QuadroPosicaoFinanceira quadroPosicaoFinanceira =
                QuadroPosicaoFinanceiraMediator.get().buscarQuadroPorPk(quadro);
        try {
            RegraAnexosValidacaoPDFA4 validacao = new RegraAnexosValidacaoPDFA4();
            validacao.validar(getInputStream(nomeArquivo), nomeArquivo);
            AnexoQuadroPosicaoFinanceira anexoQPF = new AnexoQuadroPosicaoFinanceira();
            anexoQPF.setQuadroPosicaoFinanceira(quadroPosicaoFinanceira);
            anexoQPF.setLink(nomeArquivo);
            AnexoQuadroPosicaoFinanceiraMediator.get().anexarArquivo(quadroPosicaoFinanceira, nomeArquivo, null,
                    anexoQPF, false, false);
        } catch (FileNotFoundException e) {
            retorno = "Nenhum arquivo foi selecionado.";
        } catch (NegocioException e) {
            retorno = e.getMessage().replace("[", "").replace("]", "");
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

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public Integer getQuadro() {
        return quadro;
    }

    public void setQuadro(Integer quadro) {
        this.quadro = quadro;
    }

}
