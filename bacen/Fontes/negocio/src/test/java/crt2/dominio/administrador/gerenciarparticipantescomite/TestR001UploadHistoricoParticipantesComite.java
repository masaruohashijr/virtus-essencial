package crt2.dominio.administrador.gerenciarparticipantescomite;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import br.gov.bcb.sisaps.src.mediator.AnexoParticipanteComiteMediator;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import crt2.ConfiguracaoTestesNegocio;

public class TestR001UploadHistoricoParticipantesComite extends ConfiguracaoTestesNegocio {

    private String arquivo;

    public String mensagemDeAlerta() {
        String msg = "";
        try {
            File file = new File(getArquivo());
            file.createNewFile();
            msg =   AnexoParticipanteComiteMediator.get().anexarArquivo(new FileInputStream(getArquivo()), file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NegocioException e) {
            msg = e.getMessage().replace("[", "").replace("]", "");
        }
        return msg;
    }

    public String getArquivo() {
        return arquivo;
    }

    public void setArquivo(String arquivo) {
        this.arquivo = arquivo;
    }

}
