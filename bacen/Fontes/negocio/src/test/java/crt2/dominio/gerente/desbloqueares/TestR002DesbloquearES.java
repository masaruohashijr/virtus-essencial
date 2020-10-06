package crt2.dominio.gerente.desbloqueares;

import org.springframework.beans.factory.annotation.Autowired;

import br.gov.bcb.sisaps.src.dominio.EntidadeSupervisionavel;
import br.gov.bcb.sisaps.src.mediator.BloqueioESMediator;
import br.gov.bcb.sisaps.src.mediator.EntidadeSupervisionavelMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR002DesbloquearES extends ConfiguracaoTestesNegocio {
    
    @Autowired
    private BloqueioESMediator bloqueioESMediator; 
    
    public String bloquearES(Integer idES) {
        EntidadeSupervisionavel entidadeSupervisionavel = 
                EntidadeSupervisionavelMediator.get().buscarEntidadeSupervisionavelPorPK(idES);
        return bloqueioESMediator.desbloquearES(entidadeSupervisionavel.getConglomeradoOuCnpj()); 
    }

}
