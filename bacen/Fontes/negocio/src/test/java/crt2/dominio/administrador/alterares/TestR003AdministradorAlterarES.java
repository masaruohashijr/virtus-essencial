package crt2.dominio.administrador.alterares;

import org.apache.commons.lang.StringUtils;

import br.gov.bcb.sisaps.src.dominio.EntidadeSupervisionavel;
import br.gov.bcb.sisaps.src.dominio.ParametroPrioridade;
import br.gov.bcb.sisaps.src.mediator.EntidadeSupervisionavelMediator;
import br.gov.bcb.sisaps.src.mediator.ParametroPrioridadeMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR003AdministradorAlterarES extends ConfiguracaoTestesNegocio {

    private String msg;

    public void salvar(Integer idES, String prioridade, String ciclo) {
        EntidadeSupervisionavel entidade = EntidadeSupervisionavelMediator.get().buscarEntidadeSupervisionavelPorPK(idES);
        if (StringUtils.isNotEmpty(prioridade)) {
            ParametroPrioridade parametroPrioridade = ParametroPrioridadeMediator.get().buscarPrioridadeNome(prioridade);
            entidade.setPrioridade(parametroPrioridade);
        }
        if (StringUtils.isNotEmpty(ciclo)) {
            entidade.setQuantidadeAnosPrevisaoCorec(new Short(ciclo));
        }
        EntidadeSupervisionavelMediator.get().evict(entidade);
        msg = EntidadeSupervisionavelMediator.get().salvarAlteracaoGestaoES(entidade);
    }

    public String getMensagem() {
        return msg;
    }

}
