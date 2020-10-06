package crt2.dominio.meuhistorico.areaanefs;

import java.util.List;

import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoAQTEnum;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.src.vo.ConsultaAnaliseQuantitativaAQTVO;
import br.gov.bcb.sisaps.src.vo.analisequantitativa.aqt.AnaliseQuantitativaAQTVO;
import crt2.ConfiguracaoTestesNegocio;

public class TestR001MeuHistoricoANEFs extends ConfiguracaoTestesNegocio {

    private final ConsultaAnaliseQuantitativaAQTVO consulta = new ConsultaAnaliseQuantitativaAQTVO();


    public List<AnaliseQuantitativaAQTVO> consultaPainelSupervisor(String loginUsuarioLogado, String nomeES,
            String nomeComponente, String estado) {
        logar(loginUsuarioLogado);
        if (!nomeES.isEmpty()) {
            consulta.setNomeES(nomeES);
        }
        if (!nomeComponente.isEmpty()) {
            consulta.setNomeComponente(nomeComponente);
        }
        if (!estado.isEmpty()) {
            if (EstadoAQTEnum.ANALISADO.getDescricao().equals(estado)) {
                consulta.setEstadoANEF(EstadoAQTEnum.ANALISADO);
            } else if (EstadoAQTEnum.PREENCHIDO.getDescricao().equals(estado)) {
                consulta.setEstadoANEF(EstadoAQTEnum.PREENCHIDO);
            }
        }
        return AnaliseQuantitativaAQTMediator.get().consultarHistoricoAQTfinal(consulta);
    }

    public void limpar() {
        consulta.setNomeES(null);
        consulta.setNomeComponente(null);
        consulta.setEstadoANEF(null);
    }

    public String getID(AnaliseQuantitativaAQTVO anef) {
        return anef.getPk().toString();
    }

    public String getES(AnaliseQuantitativaAQTVO anef) {
        return anef.getEntidadeSupervisionavel().getNome();
    }

    public String getComponente(AnaliseQuantitativaAQTVO anef) {
        return anef.getParametroAQT().getDescricao();
    }

    public String getAcao(AnaliseQuantitativaAQTVO anef) {
        return anef.getAcao();
    }

    public String getVersao(AnaliseQuantitativaAQTVO anef) {
        return anef.getVersao();
    }

}
