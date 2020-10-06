package crt2.dominio.corec.bloquearedicaoinspetorsupervisor;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import crt2.ConfiguracaoTestesNegocio;

public class TestR002BloquearEdicaoInspetorSupervisor extends ConfiguracaoTestesNegocio {

    private Integer ciclo;
    private PerfilRisco perfilRiscoSelecionado;

    private String exibirBotao() {
        Ciclo cicloNovo = CicloMediator.get().buscarCicloPorPK(ciclo);
        perfilRiscoSelecionado = PerfilRiscoMediator.get().obterPerfilRiscoAtual(ciclo);
        return SimNaoEnum.getTipo(
                PerfilRiscoMediator.get().isExibirBotaoCicloEmAndamento(cicloNovo, perfilRiscoSelecionado,
                        perfilUsuario())).getDescricao();
    }

    public String exibirGerenciarDetalhesDaEs() {
        return exibirBotao();
    }

    public String exibirGerenciarSinteses() {
        return exibirBotao();
    }

    public String exibirGerenciarArcs() {
        return exibirBotao();
    }

    public String exibirEditarEstruturaDaMatriz() {
        return exibirBotao();
    }

    public String exibirAjustarNotaFinal() {
        return exibirBotao();
    }

    public String exibirGerenciarAnaliseEconomicofinanceira() {
        Ciclo cicloNovo = CicloMediator.get().buscarCicloPorPK(ciclo);
        perfilRiscoSelecionado = PerfilRiscoMediator.get().obterPerfilRiscoAtual(ciclo);
        return SimNaoEnum.getTipo(
                AnaliseQuantitativaAQTMediator.get().isBotaoGerenciarVisivel(cicloNovo, perfilRiscoSelecionado,
                        perfilUsuario())).getDescricao();
    }

    public String exibirGerenciarQuadroDaPosicaoFinanceira() {
        return exibirBotao();
    }

    public String exibirGerenciarOutrasInformacoes() {
        return exibirBotao();
    }

    public String exibirVerificarPendenciasParaCorec() {
        return exibirBotao();
    }

    public String exibirIniciarFaseCorec() {
        Ciclo cicloNovo = CicloMediator.get().buscarCicloPorPK(ciclo);
        perfilRiscoSelecionado = PerfilRiscoMediator.get().obterPerfilRiscoAtual(ciclo);
        return SimNaoEnum.getTipo(
                CicloMediator.get().exibirBotaoCorec(cicloNovo, perfilRiscoSelecionado, perfilUsuario()))
                .getDescricao();
    }

    public String exibirVoltar() {
        Ciclo cicloNovo = CicloMediator.get().buscarCicloPorPK(ciclo);
        return SimNaoEnum.getTipo(CicloMediator.get().exibirBotaoVoltar(cicloNovo)).getDescricao();
    }

    public Integer getCiclo() {
        return ciclo;
    }

    public void setCiclo(Integer ciclo) {
        this.ciclo = ciclo;
    }

    public PerfilRisco getPerfilRiscoSelecionado() {
        return perfilRiscoSelecionado;
    }

}
