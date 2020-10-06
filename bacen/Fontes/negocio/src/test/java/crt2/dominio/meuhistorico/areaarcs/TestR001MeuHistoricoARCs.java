package crt2.dominio.meuhistorico.areaarcs;

import java.util.List;

import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoARCEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoGrupoEnum;
import br.gov.bcb.sisaps.src.mediator.AtividadeMediator;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.src.vo.AvaliacaoRiscoControleVO;
import br.gov.bcb.sisaps.src.vo.ConsultaAvaliacaoRiscoControleVO;
import crt2.ConfiguracaoTestesNegocio;

public class TestR001MeuHistoricoARCs extends ConfiguracaoTestesNegocio {

    private final ConsultaAvaliacaoRiscoControleVO consulta = new ConsultaAvaliacaoRiscoControleVO();

    public List<AvaliacaoRiscoControleVO> consultaHistoricoArc(String loginUsuarioLogado) {
        logar(loginUsuarioLogado);
        return AvaliacaoRiscoControleMediator.get().consultaHistoricoARC(consulta);
    }

    public void preencherConsulta(String nomeES, String nomeAtividade, String nomeGrupo, String tipo, String estado) {
        consulta.setNomeES(nomeES);
        consulta.setNomeAtividade(nomeAtividade);
        consulta.setNomeGrupoRiscoControle(nomeGrupo);
        consulta.setTipoGrupo(TipoGrupoEnum.RISCO);
        consulta.setEstadoARC(EstadoARCEnum.ANALISADO);
    }

    public String getID(AvaliacaoRiscoControleVO arc) {
        return arc.getPk().toString();
    }

    public String getES(AvaliacaoRiscoControleVO arc) {
        return arc.getNomeEs();
    }

    public String getAtividade(AvaliacaoRiscoControleVO arc) {
        return AtividadeMediator.get().retornarNomeAtividade(arc.getAtividade());
    }

    public String getGrupo(AvaliacaoRiscoControleVO arc) {
        return arc.getParametroGrupoRiscoControle().getNomeAbreviado();
    }

    public String getRiscoControle(AvaliacaoRiscoControleVO arc) {
        return arc.getTipo().getAbreviacao().toString();
    }

    public String getAcao(AvaliacaoRiscoControleVO arc) {
        return arc.getAcao();
    }

    public String getVersao(AvaliacaoRiscoControleVO arc) {
        return arc.getVersao();
    }


}
