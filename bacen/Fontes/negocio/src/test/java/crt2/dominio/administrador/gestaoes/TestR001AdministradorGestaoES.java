package crt2.dominio.administrador.gestaoes;

import java.util.List;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.adaptadores.pessoa.BcPessoaAdapter;
import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.src.dominio.ParametroPrioridade;
import br.gov.bcb.sisaps.src.mediator.EntidadeSupervisionavelMediator;
import br.gov.bcb.sisaps.src.mediator.ParametroPrioridadeMediator;
import br.gov.bcb.sisaps.src.vo.ConsultaEntidadeSupervisionavelVO;
import br.gov.bcb.sisaps.src.vo.EntidadeSupervisionavelVO;
import crt2.ConfiguracaoTestesNegocio;

public class TestR001AdministradorGestaoES extends ConfiguracaoTestesNegocio {

    public List<EntidadeSupervisionavelVO> listar(String nomeES, String equipe, String supervisor, String prioridade) {
        ConsultaEntidadeSupervisionavelVO consulta = new ConsultaEntidadeSupervisionavelVO();
        consulta.setPossuiPrioridade(true);
        consulta.setPaginada(false);
        consulta.setBuscarHierarquiaInferior(true);
        consulta.setAdministrador(true);

        if (!nomeES.isEmpty()) {
            consulta.setNome(nomeES);
        }
        if (!equipe.isEmpty()) {
            consulta.setLocalizacao(equipe);
        }
        if (!supervisor.isEmpty()) {
            setarServidor(supervisor, consulta);
            consulta.setBuscarHierarquiaInferior(false);
        }
        if (!prioridade.isEmpty()) {
            ParametroPrioridade pp = new ParametroPrioridade();
            setarPrioridade(prioridade, pp);
            pp.setDescricao(prioridade);
            consulta.setPrioridade(pp);

        }
        return EntidadeSupervisionavelMediator.get().consultarEntidadesPerfilConsulta(consulta);
    }

    public List<EntidadeSupervisionavelVO> listar() {
        ConsultaEntidadeSupervisionavelVO consulta = new ConsultaEntidadeSupervisionavelVO();
        consulta.setPossuiPrioridade(true);
        consulta.setPaginada(false);
        consulta.setAdministrador(true);
        return EntidadeSupervisionavelMediator.get().consultarEntidadesPerfilConsulta(consulta);
    }

    public String getNomeES(EntidadeSupervisionavelVO ent) {
        return ent.getNome();
    }

    public String getEquipe(EntidadeSupervisionavelVO ent) {
        return ent.getLocalizacao();
    }

    public String getSupervisorTitular(EntidadeSupervisionavelVO ent) {
        return ent.getNomeSupervisor();
    }

    public String getPrioridade(EntidadeSupervisionavelVO ent) {
        return ent.getPrioridade().getDescricao();
    }
    
    private void setarServidor(String supervisor, ConsultaEntidadeSupervisionavelVO consulta) {
        if (!supervisor.isEmpty()) {
            ServidorVO servidorVO =
                    SpringUtils.get().getBean(BcPessoaAdapter.class).buscarServidorPorLoginSemExcecao(supervisor);
            consulta.setLocalizacao(servidorVO.getLocalizacaoAtual());
        }
    }

    public String getCorecPrevisto(EntidadeSupervisionavelVO ent) {
        return ent.getDataPrevisaoFormatada();
    }

    public String getAnosPrevisaoCorec(EntidadeSupervisionavelVO ent) {
        return ent.getCiclos();
    }

    private void setarPrioridade(String descricao, ParametroPrioridade pp) {
        ParametroPrioridade prioridade = ParametroPrioridadeMediator.get().buscarPrioridadeNome(descricao);
        pp.setPk(prioridade.getPk());
    }

}
