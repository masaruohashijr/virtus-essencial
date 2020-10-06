package crt2.dominio.administrador.gerenciarparticipantescomite;

import java.util.List;

import br.gov.bcb.sisaps.src.dominio.AnexoParticipanteComite;
import br.gov.bcb.sisaps.src.mediator.AnexoParticipanteComiteMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR002UploadHistoricoParticipantesComite extends ConfiguracaoTestesNegocio {

        public List<AnexoParticipanteComite> getUltimosArquivosProcessados() {
            return AnexoParticipanteComiteMediator.get().listarAnexos();
        }
    
        public String getNomeArquivo(AnexoParticipanteComite anexo) {
            return anexo.getNome();
        }
    
        public String getDataUpload(AnexoParticipanteComite anexo) {
            return anexo.getDataUploadFormatada();
        }

}
