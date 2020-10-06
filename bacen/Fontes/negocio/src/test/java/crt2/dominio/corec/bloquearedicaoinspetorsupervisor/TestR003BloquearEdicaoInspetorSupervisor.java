package crt2.dominio.corec.bloquearedicaoinspetorsupervisor;

import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import crt2.ConfiguracaoTestesNegocio;
public class TestR003BloquearEdicaoInspetorSupervisor extends ConfiguracaoTestesNegocio {

    private Integer arc;
    private Integer ciclo;

    public String mostrarSecao() {
        Ciclo cicloAux = CicloMediator.get().buscarCicloPorPK(ciclo);
        AvaliacaoRiscoControle avaliacao = AvaliacaoRiscoControleMediator.get().buscar(arc);
        return SimNaoEnum.getTipo(
                AvaliacaoRiscoControleMediator.get().exibirAcoes(avaliacao, cicloAux,
                        PerfilAcessoEnum.SUPERVISOR.equals(perfilUsuario()))).getDescricao();
    }

    public Integer getArc() {
        return arc;
    }

    public void setArc(Integer arc) {
        this.arc = arc;
    }

    public Integer getCiclo() {
        return ciclo;
    }

    public void setCiclo(Integer ciclo) {
        this.ciclo = ciclo;
    }

}
