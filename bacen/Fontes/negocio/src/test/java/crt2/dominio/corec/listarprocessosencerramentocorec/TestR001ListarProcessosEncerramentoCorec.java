package crt2.dominio.corec.listarprocessosencerramentocorec;

import java.util.List;

import br.gov.bcb.sisaps.src.dominio.ControleBatchEncerrarCorec;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.ControleBatchEncerrarCorecMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR001ListarProcessosEncerramentoCorec extends ConfiguracaoTestesNegocio {

    public List<ControleBatchEncerrarCorec> listarProcessos() {
        return ControleBatchEncerrarCorecMediator.get().listarProcessos();
    }
    
    public String getID(ControleBatchEncerrarCorec controle) {
        return controle.getPk().toString();
    }
    
    public String getES(ControleBatchEncerrarCorec controle) {
        return CicloMediator.get().loadPK(controle.getCiclo().getPk()).getEntidadeSupervisionavel().getNome();
    }
    
    public String getDataCorec(ControleBatchEncerrarCorec controle) {
        return controle.getCiclo().getDataPrevisaoFormatada();
    }
    
    public String getEstado(ControleBatchEncerrarCorec controle) {
        return controle.getEstadoExecucao().getDescricao();
    }
    
    public String getData(ControleBatchEncerrarCorec controle) {
        return controle.getDataHoraFormatada();
    }

}
