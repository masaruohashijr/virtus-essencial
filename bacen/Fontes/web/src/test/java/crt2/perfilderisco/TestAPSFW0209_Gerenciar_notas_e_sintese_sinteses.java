package crt2.perfilderisco;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Documento;
import br.gov.bcb.sisaps.src.dominio.ItemElemento;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoEntidadeAnexoDocumentoEnum;
import br.gov.bcb.sisaps.src.mediator.AnexoArcMediator;
import br.gov.bcb.sisaps.src.mediator.AnexoDocumentoMediator;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.DocumentoMediator;
import br.gov.bcb.sisaps.src.mediator.ItemElementoMediator;
import crt2.ConfiguracaoTestesWeb;

public class TestAPSFW0209_Gerenciar_notas_e_sintese_sinteses extends ConfiguracaoTestesWeb {
    
    public void criarAnexoDocumentoItemElemento(String nomeDoArquivo, String caminhoDoArquivo, 
            String idCiclo, String idItemElemento, String idDocumento) throws FileNotFoundException {
        File arquivo = new File(caminhoDoArquivo);
        InputStream inputStream = new FileInputStream(arquivo);
        Ciclo ciclo = CicloMediator.get().buscarCicloPorPK(Integer.valueOf(idCiclo));
        ItemElemento itemElemento = ItemElementoMediator.get().buscarPorPk(Integer.valueOf(idItemElemento));
        Documento documento = DocumentoMediator.get().buscarPorPk(Integer.valueOf(idDocumento));
        AnexoDocumentoMediator.get().anexarArquivo(ciclo, itemElemento, documento, 
                TipoEntidadeAnexoDocumentoEnum.ITEM_ELEMENTO, nomeDoArquivo, inputStream, false);
    }
    
    public void criarAnexoARC(String nomeDoArquivo, String caminhoDoArquivo, 
            String idCiclo, String idARC) throws FileNotFoundException {
        File arquivo = new File(caminhoDoArquivo);
        InputStream inputStream = new FileInputStream(arquivo);
        Ciclo ciclo = CicloMediator.get().buscarCicloPorPK(Integer.valueOf(idCiclo));
        AvaliacaoRiscoControle arc = AvaliacaoRiscoControleMediator.get().buscarPorPk(Integer.valueOf(idARC));
        AnexoArcMediator.get().anexarArquivo(ciclo, arc, nomeDoArquivo, inputStream, true, null, false);
    }

}
