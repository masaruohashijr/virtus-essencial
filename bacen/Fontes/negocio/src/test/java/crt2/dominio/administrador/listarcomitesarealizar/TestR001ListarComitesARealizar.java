package crt2.dominio.administrador.listarcomitesarealizar;

import java.util.List;

import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.vo.CicloVO;
import br.gov.bcb.sisaps.src.vo.ConsultaCicloVO;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import crt2.ConfiguracaoTestesNegocio;

public class TestR001ListarComitesARealizar extends ConfiguracaoTestesNegocio {

    public List<CicloVO> getComitesARealizar() {
        return CicloMediator.get().listarComitesRealizar(new ConsultaCicloVO());
    }

    public String getInicioCorec(CicloVO ciclo) {
        return ciclo.getDataInicioFormatada().toString();
    }

    public String getCorecPrevisto(CicloVO ciclo) {
        return ciclo.getDataPrevisaoFormatada().toString();
    }

    public String getNomeES(CicloVO ciclo) {
        return ciclo.getEntidadeSupervisionavel().getNome();
    }

    public String getEquipe(CicloVO ciclo) {
        return ciclo.getEntidadeSupervisionavel().getLocalizacao();
    }

    public String getSupervisor(CicloVO ciclo) {
        return ciclo.getNomeSupervisor();
    }

    public String getPrioridade(CicloVO ciclo) {
        return ciclo.getEntidadeSupervisionavel().getPrioridade().getDescricao();
    }

    public String getLocal(CicloVO ciclo) {
        return ciclo.getLocal();
    }

    public String isPossivelEditar(CicloVO ciclo) {
        return SimNaoEnum.getTipo(ciclo.isPodeAlterar()).getDescricao();
    }

}
