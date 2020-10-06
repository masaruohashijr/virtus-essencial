package crt2.dominio.analisequalitativa.listararcdelegado;

import br.gov.bcb.sisaps.src.vo.AvaliacaoRiscoControleVO;
import crt2.ConfiguracaoTestesNegocio;

public class TestR001ListarARCDelegadoInspetor extends ConfiguracaoTestesNegocio{
    
    //    public List<AvaliacaoRiscoControleVO> consultaPainelInspetor(String loginUsuarioLogado){
    //        logar(loginUsuarioLogado);
    //        return AvaliacaoRiscoControleMediator.get().consultaPainelArcDelegados();
    //    }
  
    public Integer getID(AvaliacaoRiscoControleVO arc) {
        return arc.getPk();
    }

    public String getEs(AvaliacaoRiscoControleVO arc) {
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
