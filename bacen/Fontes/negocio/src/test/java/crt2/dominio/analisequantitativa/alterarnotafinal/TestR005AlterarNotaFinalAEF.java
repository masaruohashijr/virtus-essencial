package crt2.dominio.analisequantitativa.alterarnotafinal;

import java.util.List;

import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.CelulaRiscoControle;
import br.gov.bcb.sisaps.src.dominio.NotaMatriz;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.VersaoPerfilRisco;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoObjetoVersionadorEnum;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.CelulaRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.MatrizCicloMediator;
import br.gov.bcb.sisaps.src.mediator.NotaMatrizMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.src.mediator.VersaoPerfilRiscoMediator;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import crt2.ConfiguracaoTestesNegocio;

public class TestR005AlterarNotaFinalAEF extends ConfiguracaoTestesNegocio {

    private String notaCalculada;
    private String msg;

    public String confirmarNotaMatriz(Integer perfil) {
        erro = null;
        notaCalculada = "";
        try {

            notaCalculada = getNotaCalculada(perfil);
            PerfilRisco perfilRisco = PerfilRiscoMediator.get().obterPerfilRiscoPorPk(perfil);
            NotaMatriz notaRascunho =
                    NotaMatrizMediator.get().buscarNotaMatrizRascunho(perfilRisco.getCiclo().getMatriz());
            msg = NotaMatrizMediator.get().confirmarNotaMatriz(notaRascunho, notaCalculada);
            return msg;
        } catch (NegocioException e) {
            erro = e;
        }
        return erro == null ? "" : erro.getMessage();

    }

    public String getNotaCalculada(Integer perfil) {
        PerfilRisco perfilAtual = PerfilRiscoMediator.get().obterPerfilRiscoPorPk(perfil);
        String notaCalculadaFinal = notaCalculada(perfilAtual);
        return notaCalculadaFinal.replace(".", ",");
    }

    private String notaCalculada(PerfilRisco perfilAtual) {
        List<VersaoPerfilRisco> versoesPerfilRiscoCelulas =
                VersaoPerfilRiscoMediator.get().buscarVersoesPerfilRisco(perfilAtual.getPk(),
                        TipoObjetoVersionadorEnum.CELULA_RISCO_CONTROLE);
        List<CelulaRiscoControle> listaChoices =
                CelulaRiscoControleMediator.get().buscarCelulasPorPerfilRisco(perfilAtual.getPk());
        AvaliacaoRiscoControle arcExterno =
                AvaliacaoRiscoControleMediator.get().buscarArcExternoPorPerfilRisco(perfilAtual.getPk());
        String notaCalculadaFinal =
                MatrizCicloMediator.get().notaCalculadaFinal(perfilAtual.getCiclo().getMatriz(), listaChoices,
                        versoesPerfilRiscoCelulas, true, PerfilAcessoEnum.SUPERVISOR, perfilAtual, arcExterno);
        return notaCalculadaFinal;
    }
}
