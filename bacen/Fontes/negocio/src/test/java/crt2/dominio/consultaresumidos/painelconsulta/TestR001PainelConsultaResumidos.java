package crt2.dominio.consultaresumidos.painelconsulta;

import java.util.List;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.adaptadores.pessoa.BcPessoaAdapter;
import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.src.mediator.EntidadeSupervisionavelMediator;
import br.gov.bcb.sisaps.src.vo.ConsultaEntidadeSupervisionavelVO;
import br.gov.bcb.sisaps.src.vo.EntidadeSupervisionavelVO;
import crt2.ConfiguracaoTestesNegocio;

public class TestR001PainelConsultaResumidos extends ConfiguracaoTestesNegocio {

    public List<EntidadeSupervisionavelVO> listar(String nomeES, String equipe, String supervisor) {
        ConsultaEntidadeSupervisionavelVO consulta = new ConsultaEntidadeSupervisionavelVO();
        consulta.setPossuiPrioridade(false);
        consulta.setPaginada(false);
        consulta.setBuscarHierarquiaInferior(true);
        consulta.setAdministrador(false);

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

        return EntidadeSupervisionavelMediator.get().consultarEntidadesPerfilConsulta(consulta);
    }

    public List<EntidadeSupervisionavelVO> listar() {
        ConsultaEntidadeSupervisionavelVO consulta = new ConsultaEntidadeSupervisionavelVO();
        consulta.setPossuiPrioridade(true);
        consulta.setPaginada(false);
        consulta.setAdministrador(false);
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

    public String getCorecPrevisto(EntidadeSupervisionavelVO ent) {
        return ent.getDataPrevisaoFormatada();
    }

    public String getAnosPrevisaoCorec(EntidadeSupervisionavelVO ent) {
        return ent.getCiclos();
    }

    private void setarServidor(String supervisor, ConsultaEntidadeSupervisionavelVO consulta) {
        ServidorVO servidorVO =
                SpringUtils.get().getBean(BcPessoaAdapter.class).buscarServidorPorLoginSemExcecao(supervisor);
        consulta.setLocalizacao(servidorVO.getLocalizacaoAtual());
    }

}
