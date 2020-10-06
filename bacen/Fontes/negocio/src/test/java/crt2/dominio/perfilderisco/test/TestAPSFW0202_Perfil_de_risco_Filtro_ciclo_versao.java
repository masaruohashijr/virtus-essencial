package crt2.dominio.perfilderisco.test;

import org.springframework.beans.factory.annotation.Autowired;

import br.gov.bcb.sisaps.src.dominio.EntidadeSupervisionavel;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.EntidadeSupervisionavelMediator;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import crt2.ConfiguracaoTestesNegocio;

public class TestAPSFW0202_Perfil_de_risco_Filtro_ciclo_versao extends ConfiguracaoTestesNegocio {
    
    @Autowired
    private CicloMediator cicloMediator;
    
    @Autowired
    private EntidadeSupervisionavelMediator entidadeSupervisionavelMediator;
    
    public void detalharPerfilRisco(String idEntidadeSupervisionavel) {
        
        EntidadeSupervisionavel entidadeSupervisionavel = 
                entidadeSupervisionavelMediator.buscarEntidadeSupervisionavelPorPK(
                        Integer.valueOf(idEntidadeSupervisionavel));
        
        erro = null;
        try {
            cicloMediator.consultarCiclosEntidadeSupervisionavel(
                    entidadeSupervisionavel.getConglomeradoOuCnpj(), false);
        } catch (NegocioException e) {
            erro = e;
        }
    }

}
