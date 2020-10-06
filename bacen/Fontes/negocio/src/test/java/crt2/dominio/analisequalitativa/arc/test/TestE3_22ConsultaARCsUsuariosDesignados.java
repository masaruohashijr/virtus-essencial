package crt2.dominio.analisequalitativa.arc.test;

import org.springframework.beans.factory.annotation.Autowired;

import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.src.vo.AvaliacaoRiscoControleVO;
import crt2.ConfiguracaoTestesNegocio;

public class TestE3_22ConsultaARCsUsuariosDesignados extends ConfiguracaoTestesNegocio {

    @Autowired
    private AvaliacaoRiscoControleMediator avaliacaoRiscoControleMediator;

    //    public List<AvaliacaoRiscoControleVO> consultaPainelInspetor(String loginUsuarioLogado, Long matriculaUsuarioLogado) {
    //        logar(loginUsuarioLogado, matriculaUsuarioLogado.toString());
    //        return avaliacaoRiscoControleMediator.consultaPainelArcDesignados();
    //    }

    public String getES(AvaliacaoRiscoControleVO arcVO) {
        return arcVO.getMatrizVigente().getCiclo().getEntidadeSupervisionavel().getNome();
    }

    public String getAtividade(AvaliacaoRiscoControleVO arcVO) {
        return arcVO.getAtividade().getNome();
    }

    public String getGrupo(AvaliacaoRiscoControleVO arcVO) {
        return arcVO.getParametroGrupoRiscoControle().getNome();
    }
}
