package crt2.dominio.analisequalitativa.notacalculada;

import java.util.List;

import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.CelulaRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.VersaoPerfilRisco;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoObjetoVersionadorEnum;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.CelulaRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.MatrizCicloMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.src.mediator.VersaoPerfilRiscoMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR001NotaCalculadaQualitativa extends ConfiguracaoTestesNegocio{

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
        String notaCalculadaFinal = MatrizCicloMediator.get().notaCalculadaFinal(perfilAtual.getCiclo().getMatriz(), listaChoices,
                versoesPerfilRiscoCelulas, true, PerfilAcessoEnum.SUPERVISOR, perfilAtual, arcExterno);
        return notaCalculadaFinal;
    }

    public String getNotaRefinada(Integer perfil) {
        PerfilRisco perfilAtual = PerfilRiscoMediator.get().obterPerfilRiscoPorPk(perfil);
        String notaCalculadaFinal = notaCalculada(perfilAtual);
        return MatrizCicloMediator.get().notaRefinadaFinal(perfilAtual.getCiclo().getMetodologia(), notaCalculadaFinal);
    }
    
    public String getNotaCalculadaMatriz(Integer perfil) {
        PerfilRisco perfilAtual = PerfilRiscoMediator.get().obterPerfilRiscoPorPk(perfil);

        List<CelulaRiscoControle> listaChoices =
                CelulaRiscoControleMediator.get().buscarCelulasPorPerfilRisco(perfilAtual.getPk());

        List<VersaoPerfilRisco> versoesPerfilRiscoCelulas =
                VersaoPerfilRiscoMediator.get().buscarVersoesPerfilRisco(perfilAtual.getPk(),
                        TipoObjetoVersionadorEnum.CELULA_RISCO_CONTROLE);

        return MatrizCicloMediator
                .get()
                .notaCalculada(perfilAtual.getCiclo().getMatriz(), listaChoices, versoesPerfilRiscoCelulas, true,
                        PerfilAcessoEnum.SUPERVISOR, perfilAtual).replace('.', ',');
    }

    public String getPercentualMatriz(Integer perfil){
        PerfilRisco perfilAtual = PerfilRiscoMediator.get().obterPerfilRiscoPorPk(perfil);
        Matriz matriz = MatrizCicloMediator.get().getUltimaMatrizCiclo(perfilAtual.getCiclo());
        return matriz.getPercentualBlocoAtividades();
    }

    public String getPercentualARCGovernanca(Integer perfil) {
        PerfilRisco perfilAtual = PerfilRiscoMediator.get().obterPerfilRiscoPorPk(perfil);
        Matriz matriz = MatrizCicloMediator.get().getUltimaMatrizCiclo(perfilAtual.getCiclo());
        return matriz.getPercentualGovernancaCorporativa();
    }

    public String getNotaCalculadaARCGovernanca(Integer perfil) {
        PerfilRisco perfilAtual = PerfilRiscoMediator.get().obterPerfilRiscoPorPk(perfil);
        AvaliacaoRiscoControle arcExterno =
                AvaliacaoRiscoControleMediator.get().buscarArcExternoPorPerfilRisco(perfilAtual.getPk());
        return arcExterno == null ? "" : arcExterno.getAvaliacaoArcDescricaoValor();
    }

}
