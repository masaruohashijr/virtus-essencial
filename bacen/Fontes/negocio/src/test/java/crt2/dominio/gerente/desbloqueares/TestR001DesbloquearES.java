package crt2.dominio.gerente.desbloqueares;

import org.springframework.beans.factory.annotation.Autowired;

import br.gov.bcb.sisaps.src.dominio.EntidadeSupervisionavel;
import br.gov.bcb.sisaps.src.mediator.BloqueioESMediator;
import br.gov.bcb.sisaps.src.mediator.EntidadeSupervisionavelMediator;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import crt2.ConfiguracaoTestesNegocio;

public class TestR001DesbloquearES extends ConfiguracaoTestesNegocio {
    
    @Autowired
    private BloqueioESMediator bloqueioESMediator; 
    
    public String isBloqueavel(Integer idES) {
        EntidadeSupervisionavel entidadeSupervisionavel = 
                EntidadeSupervisionavelMediator.get().buscarEntidadeSupervisionavelPorPK(idES);
        SimNaoEnum isBloqueavel = SimNaoEnum.getTipo(
                bloqueioESMediator.isESBloqueada(entidadeSupervisionavel.getConglomeradoOuCnpj()));
        return isBloqueavel.getDescricao();
    }

}
