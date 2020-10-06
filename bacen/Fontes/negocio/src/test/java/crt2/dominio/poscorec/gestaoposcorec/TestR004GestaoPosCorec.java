package crt2.dominio.poscorec.gestaoposcorec;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import br.gov.bcb.sisaps.src.mediator.AnexoPosCorecMediator;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.validacao.RegraAnexosValidacaoPDFA4;
import crt2.ConfiguracaoTestesNegocio;

public class TestR004GestaoPosCorec extends ConfiguracaoTestesNegocio {

    public String anexarOficio(Integer ciclo, String dtEntrega, String caminhoAnexo) {
        String retorno = "";
        try {
            RegraAnexosValidacaoPDFA4 validacao = new RegraAnexosValidacaoPDFA4();
            validacao.validar(getInputStream(ciclo, caminhoAnexo), caminhoAnexo);
            retorno =
                    AnexoPosCorecMediator.get().anexarArquivo(CicloMediator.get().buscarCicloPorPK(ciclo), "Ofício",
                            dtEntrega, caminhoAnexo, getInputStream(ciclo, caminhoAnexo));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return retorno;
    }

    private InputStream getInputStream(Integer idCiclo, String caminhoAnexo) throws FileNotFoundException {
        File arquivo = new File(caminhoAnexo);
        return new FileInputStream(arquivo);
    }
}
