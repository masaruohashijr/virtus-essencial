package crt2.dominio.perfilderisco.gerenciardetalheses;

import br.gov.bcb.sisaps.src.dominio.GrauPreocupacaoES;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.mediator.AjusteCorecMediator;
import br.gov.bcb.sisaps.src.mediator.GrauPreocupacaoESMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;

public class TestR008Gerenciar_detalhes_da_ES extends TestR004Gerenciar_detalhes_da_ES_Anexos {

    @Override
    public String mensagem() {
        return super.mensagem();
    }

    @Override
    public String botaoConfirmarHabilitado() {
        return super.botaoConfirmarHabilitado();
    }

    public String getNotaCalculadaARC(Integer perfil) {
        PerfilRisco perfilAtual = PerfilRiscoMediator.get().obterPerfilRiscoPorPk(perfil);
        return GrauPreocupacaoESMediator.get().getNotaMatrizFinal(perfilAtual, PerfilAcessoEnum.SUPERVISOR);
    }

    public String getNotaRefinada(Integer perfil) {
        PerfilRisco perfilAtual = PerfilRiscoMediator.get().obterPerfilRiscoPorPk(perfil);
        return GrauPreocupacaoESMediator.get().getNotaFinalRefinada(perfilAtual.getCiclo().getMetodologia(),
                getNotaFinalCalculada(perfil));
    }

    public String getNotaCalculadaAEF(Integer perfil) {
        PerfilRisco perfilRisco = PerfilRiscoMediator.get().obterPerfilRiscoPorPk(perfil);
        return GrauPreocupacaoESMediator.get().getNotaAEF(perfilRisco, PerfilAcessoEnum.SUPERVISOR);
    }

    public String getNotaAjustadaVigente(Integer perfil) {
        String notaFinalAjustada = "";
        PerfilRisco perfilRisco = PerfilRiscoMediator.get().obterPerfilRiscoPorPk(perfil);
        String ajusteCorec =
                AjusteCorecMediator.get().notaAjustadaCorecES(perfilRisco, perfilRisco.getCiclo(),
                        PerfilAcessoEnum.SUPERVISOR);
        GrauPreocupacaoES grau =
                GrauPreocupacaoESMediator.get().buscarPorPerfilRisco(perfilRisco.getPk());
        if (ajusteCorec != null) {
            notaFinalAjustada = ajusteCorec + " (Corec)";
        } else if (grau != null) {
            notaFinalAjustada = grau.getDescricaoNotaFinal();
        }
        return notaFinalAjustada;
    }

    public String getNotaFinalCalculada(Integer perfil) {
        PerfilRisco perfilRisco = PerfilRiscoMediator.get().obterPerfilRiscoPorPk(perfil);
        GrauPreocupacaoES grau =
                GrauPreocupacaoESMediator.get().buscarPorPerfilRisco(perfilRisco.getPk());
        return GrauPreocupacaoESMediator.get().getNotaFinalCalculada(grau, getNotaCalculadaAEF(perfil),
                getNotaCalculadaARC(perfil), perfilRisco.getCiclo());
    }

}
