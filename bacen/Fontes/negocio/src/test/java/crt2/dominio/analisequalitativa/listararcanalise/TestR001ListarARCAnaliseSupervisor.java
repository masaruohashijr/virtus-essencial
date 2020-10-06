package crt2.dominio.analisequalitativa.listararcanalise;

import br.gov.bcb.sisaps.src.vo.AvaliacaoRiscoControleVO;
import crt2.ConfiguracaoTestesNegocio;

public class TestR001ListarARCAnaliseSupervisor extends ConfiguracaoTestesNegocio{

    //    public List<AvaliacaoRiscoControleVO> consultaPainelSupervisor(String loginUsuarioLogado){
    //        logar(loginUsuarioLogado);
    //        return AvaliacaoRiscoControleMediator.get().consultaPainelSupervisor();
    //    }
  
    public Integer getID(AvaliacaoRiscoControleVO arc) {
        return arc.getPk();
    }

    public String getES(AvaliacaoRiscoControleVO arc) {
        return arc.getMatrizVigente().getCiclo().getEntidadeSupervisionavel().getNome();
    }

    public String getAtividade(AvaliacaoRiscoControleVO arc) {
        return arc.getAtividade().getNome() == null ? "" : arc.getAtividade().getNome();
    }

    public String getGrupo(AvaliacaoRiscoControleVO arc) {
        return arc.getParametroGrupoRiscoControle().getNome();
    }

    public String getRC(AvaliacaoRiscoControleVO arc) {
        return arc.getTipo().getAbreviacao();
    }

    public String getEstado(AvaliacaoRiscoControleVO arc) {
        return arc.getEstado().getDescricao();
    }
}
