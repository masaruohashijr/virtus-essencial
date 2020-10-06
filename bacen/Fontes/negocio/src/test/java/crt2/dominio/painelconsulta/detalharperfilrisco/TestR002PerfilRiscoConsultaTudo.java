package crt2.dominio.painelconsulta.detalharperfilrisco;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.mediator.BloqueioESMediator;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR002PerfilRiscoConsultaTudo extends ConfiguracaoTestesNegocio {
    
    public String getMensagem(Integer ciclo) {
        Ciclo cicloLoad = CicloMediator.get().buscarCicloPorPK(ciclo);
        return BloqueioESMediator.get().getMensagemPerfilBloqueado(
                cicloLoad.getEntidadeSupervisionavel().getConglomeradoOuCnpj(), PerfilAcessoEnum.CONSULTA_TUDO);
    }
    
}
