package crt2.dominio.corec.ajustenotasarcscomite;

import java.util.List;

import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoObjetoVersionadorEnum;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.src.mediator.VersaoPerfilRiscoMediator;
import br.gov.bcb.sisaps.src.vo.AvaliacaoRiscoControleVO;
import crt2.ConfiguracaoTestesNegocio;

public class TestR001AjusteNotasARCsComite extends ConfiguracaoTestesNegocio {

    public List<AvaliacaoRiscoControleVO> consultaARCsAjusteComite(int cicloPK) {
        PerfilRisco perfilAtual = PerfilRiscoMediator.get().obterPerfilRiscoAtual(cicloPK);

        return AvaliacaoRiscoControleMediator.get().consultaArcPerfil(
                perfilAtual.getCiclo().getMatriz(),
                VersaoPerfilRiscoMediator.get().buscarVersoesPerfilRisco(perfilAtual.getPk(),
                        TipoObjetoVersionadorEnum.ARC));

    }

    public String getID(AvaliacaoRiscoControleVO arc) {
        return arc.getPk().toString();
    }

    public String getAtividade(AvaliacaoRiscoControleVO arc) {
        return arc.getAtividade().getNome() == null ? "" : arc.getAtividade().getNome();
    }

    public String getGrupo(AvaliacaoRiscoControleVO arc) {
        return arc.getParametroGrupoRiscoControle().getNomeAbreviado();
    }

    public String getTipo(AvaliacaoRiscoControleVO arc) {
        return arc.getTipo().getAbreviacao();
    }

    public String getNotaFinal(AvaliacaoRiscoControleVO arc) {
        AvaliacaoRiscoControle arcLoad = AvaliacaoRiscoControleMediator.get().loadPK(arc.getPk());
        return arcLoad.getNotaVigenteDescricaoValor();
    }

}
