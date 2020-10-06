package br.gov.bcb.sisaps.web.page.dominio.ciclo.painel;

import org.apache.wicket.model.IModel;

import br.gov.bcb.app.stuff.seguranca.UsuarioCorrente;
import br.gov.bcb.sisaps.seguranca.UsuarioAplicacao;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoARCEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TituloTelaARCEnum;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.LinhaMatrizMediator;
import br.gov.bcb.sisaps.src.mediator.MatrizCicloMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.src.vo.ArcNotasVO;
import br.gov.bcb.sisaps.src.vo.ArcResumidoVO;
import br.gov.bcb.sisaps.src.vo.AvaliacaoRiscoControleVO;
import br.gov.bcb.sisaps.util.Constantes;

public class UtilNavegabilidadeARC {


    public static TituloTelaARCEnum novaPaginaDeAcordoStatus(Matriz matriz,
            ArcNotasVO avaliacaoRisco, boolean emAnalise,
            boolean perfilAtual, PerfilAcessoEnum perfilAcesso) {
        TituloTelaARCEnum tela = null;
        if (PerfilAcessoEnum.CONSULTA_TUDO.equals(perfilAcesso)) {
            tela = paginaConsulta(matriz, avaliacaoRisco, emAnalise, perfilAcesso);
        } else if (PerfilAcessoEnum.SUPERVISOR.equals(perfilAcesso)) {
            tela = paginaSupervisor(avaliacaoRisco, matriz, perfilAtual);
        } else if (PerfilAcessoEnum.INSPETOR.equals(perfilAcesso)) {
            tela = paginaInspetor(matriz, avaliacaoRisco, perfilAtual);
        } else if (PerfilAcessoEnum.GERENTE.equals(perfilAcesso)) {
            tela = paginaGerente(matriz, avaliacaoRisco, emAnalise, perfilAcesso, perfilAtual);
        }
        return tela;
    }


    private static TituloTelaARCEnum paginaGerente(Matriz matriz,
            final ArcNotasVO avaliacaoRisco, boolean emAnalise,
            PerfilAcessoEnum perfilAcesso, boolean perfilAtual) {
        TituloTelaARCEnum tela = null;
        if (perfilAtual) {
            AvaliacaoRiscoControleMediator avaliacaoRiscoControleMediator = AvaliacaoRiscoControleMediator.get();
            EstadoARCEnum estado = retornarEstadoArc(avaliacaoRisco);
            if (avaliacaoRiscoControleMediator.estadoPrevisto(estado)
                    || avaliacaoRiscoControleMediator.estadoDesignado(estado)) {
                tela = TituloTelaARCEnum.PAGINA_DETALHE_PREVISTO_DESIGNADO;
            } else if (avaliacaoRiscoControleMediator.estadoEmEdicao(estado)) {
                tela = TituloTelaARCEnum.PAGINA_DETALHE_EDICAO;
            } else if (avaliacaoRiscoControleMediator.estadoConcluido(estado)) {
                tela = TituloTelaARCEnum.PAGINA_DETALHE_CONCLUIDO;
            } else if (avaliacaoRiscoControleMediator.estadoEmAnalise(estado)) {
                tela = TituloTelaARCEnum.PAGINA_DETALHE_ANALISE;
            } else if (avaliacaoRiscoControleMediator.estadoAnalisado(estado)) {
                tela = TituloTelaARCEnum.PAGINA_DETALHE_ANALISADO;
            } else if (avaliacaoRiscoControleMediator.estadoAnaliseDelegada(estado)) {
                tela = TituloTelaARCEnum.PAGINA_DETALHE_DELEGADO;
            } else if (avaliacaoRiscoControleMediator.estadoPreenchido(estado)) {
                tela = TituloTelaARCEnum.PAGINA_DETALHE_PREENCHIDO;
            }
        } else if (!AvaliacaoRiscoControleMediator.get().getNotaEmAnaliseDescricaoValor(avaliacaoRisco)
                .equals(Constantes.ASTERISCO_A)) {
            tela = TituloTelaARCEnum.PAGINA_DETALHE_CONCLUIDO;
        }
        return tela;
    }

    private static TituloTelaARCEnum paginaConsulta(Matriz matriz,
            final ArcNotasVO avaliacaoRisco, boolean emAnalise,
            PerfilAcessoEnum perfilAcesso) {

        TituloTelaARCEnum tela = null;
        if (!CicloMediator.get().cicloEmAndamento(matriz.getCiclo())) {
            tela = TituloTelaARCEnum.PAGINA_CONSULTA;
        } else {
            PerfilRisco perfilRiscoAtual = PerfilRiscoMediator.get().obterPerfilRiscoAtual(matriz.getCiclo().getPk());
            if (!LinhaMatrizMediator.get()
                    .buscarNota(avaliacaoRisco, emAnalise, perfilAcesso, matriz.getCiclo(), perfilRiscoAtual)
                    .equals(Constantes.ASTERISCO_A)) {
                tela = TituloTelaARCEnum.PAGINA_CONSULTA;
            }
        }
        return tela;
    }

    private static TituloTelaARCEnum paginaSupervisor(ArcNotasVO avaliacaoRiscoControle,
            Matriz matriz, boolean perfilAtual) {
        TituloTelaARCEnum tela = null;
        EstadoARCEnum estadoArc = retornarEstadoArc(avaliacaoRiscoControle);
        UsuarioAplicacao usuario = retornarUsuarioAplicacao();
        tela = telasDetalhamentoEdicaoSupervisor(avaliacaoRiscoControle, matriz, estadoArc, usuario, perfilAtual);
        return tela;
    }

    private static TituloTelaARCEnum telasDetalhamentoEdicaoSupervisor(ArcNotasVO avaliacaoRiscoControle, Matriz matriz,
            EstadoARCEnum estadoArc, UsuarioAplicacao usuario, boolean perfilAtual) {
        TituloTelaARCEnum tela = null;
        AvaliacaoRiscoControleMediator avaliacaoRiscoControleMediator = AvaliacaoRiscoControleMediator.get();
        if (!CicloMediator.get().cicloEmAndamento(matriz.getCiclo())
                && !CicloMediator.get().cicloCorec(matriz.getCiclo()) && avaliacaoRiscoControle.getArcVigente() != null
                || (avaliacaoRiscoControleMediator.estadoDesignado(estadoArc) && !perfilAtual)) {
            tela = TituloTelaARCEnum.PAGINA_DETALHE_CONCLUIDO;
        } else if (avaliacaoRiscoControle.getArcVigente() == null
                && (avaliacaoRiscoControleMediator.estadoPrevisto(estadoArc)
                        || avaliacaoRiscoControleMediator.estadoDesignado(estadoArc))) {
            tela = TituloTelaARCEnum.PAGINA_DETALHE_PREVISTO_DESIGNADO_BRANCO;
        } else if (avaliacaoRiscoControle.getArcVigente() != null
                && (avaliacaoRiscoControleMediator.estadoPrevisto(estadoArc)
                        || avaliacaoRiscoControleMediator.estadoDesignado(estadoArc))) {
            tela = TituloTelaARCEnum.PAGINA_DETALHE_PREVISTO_DESIGNADO;
        } else if (avaliacaoRiscoControleMediator.estadoEmEdicao(estadoArc)) {
            tela = TituloTelaARCEnum.PAGINA_DETALHE_EDICAO;
        } else if (avaliacaoRiscoControleMediator.estadoPreenchido(estadoArc) && perfilAtual) {
            tela = TituloTelaARCEnum.PAGINA_ANALISE;
        }

        if (tela != null) {
            return tela;
        } else {
            tela = contTelasDetalhamentoEdicaoSupervisor(avaliacaoRiscoControle, matriz, estadoArc, usuario,
                    perfilAtual);
            return tela;
        }
    }

    private static TituloTelaARCEnum contTelasDetalhamentoEdicaoSupervisor(
            ArcNotasVO avaliacaoRiscoControle,
            Matriz matriz, EstadoARCEnum estadoArc, UsuarioAplicacao usuario, boolean perfilAtual) {
        TituloTelaARCEnum tela = null;

        AvaliacaoRiscoControleMediator avaliacaoRiscoControleMediator = AvaliacaoRiscoControleMediator.get();

        if (avaliacaoRiscoControleMediator.estadoPreenchido(avaliacaoRiscoControle, estadoArc, usuario, matriz)
                && !perfilAtual) {
            tela = TituloTelaARCEnum.PAGINA_DETALHE_CONCLUIDO;
        } else if (avaliacaoRiscoControleMediator.estadoAnaliseDelegada(estadoArc)) {
            tela = TituloTelaARCEnum.PAGINA_DETALHE_DELEGADO;
        } else if (avaliacaoRiscoControleMediator.estadoEmAnaliseSupervisor(avaliacaoRiscoControle, estadoArc)) {
            tela = TituloTelaARCEnum.PAGINA_ANALISE;
        } else if (avaliacaoRiscoControleMediator.estadoEmAnaliseOutroUsuario(avaliacaoRiscoControle, estadoArc,
                usuario)) {
            tela = TituloTelaARCEnum.PAGINA_DETALHE_ANALISE;
        } else if (avaliacaoRiscoControleMediator.estadoAnalisado(estadoArc)) {
            tela = TituloTelaARCEnum.PAGINA_DETALHE_ANALISADO;
        } else if (avaliacaoRiscoControleMediator.estadoConcluido(estadoArc)) {
            tela = TituloTelaARCEnum.PAGINA_DETALHE_CONCLUIDO;
        }

        return tela;
    }

    private static TituloTelaARCEnum paginaInspetor(Matriz matriz,
            ArcNotasVO avaliacaoRiscoControle, boolean perfilAtual) {
        TituloTelaARCEnum tela = null;
        AvaliacaoRiscoControleMediator avaliacaoRiscoControleMediator = AvaliacaoRiscoControleMediator.get();
        EstadoARCEnum estadoArc = retornarEstadoArc(avaliacaoRiscoControle);
        UsuarioAplicacao usuario = retornarUsuarioAplicacao();

        if (!CicloMediator.get().cicloEmAndamento(matriz.getCiclo())
                && avaliacaoRiscoControle.getArcVigente() != null) {
            tela = TituloTelaARCEnum.PAGINA_DETALHE_CONCLUIDO;
        } else if (avaliacaoRiscoControle.getArcVigente() == null
                && !avaliacaoRiscoControleMediator.isResponsavelARC(avaliacaoRiscoControle, usuario)) {
            tela = TituloTelaARCEnum.PAGINA_DETALHE_PREVISTO_BRANCO;
        } else if (avaliacaoRiscoControle.getArcVigente() == null
                && avaliacaoRiscoControleMediator.isResponsavelARC(avaliacaoRiscoControle, usuario)) {
            tela = telaEdicaoAnalise(matriz, avaliacaoRiscoControle, estadoArc, usuario);
        } else if (avaliacaoRiscoControle.getArcVigente() != null
                && avaliacaoRiscoControleMediator.isResponsavelARC(avaliacaoRiscoControle, usuario)) {
            tela = telaEdicaoAnalise(matriz, avaliacaoRiscoControle, estadoArc, usuario);
        } else if (avaliacaoRiscoControle.getArcVigente() != null
                && !avaliacaoRiscoControleMediator.isResponsavelARC(avaliacaoRiscoControle, usuario)) {
            tela = TituloTelaARCEnum.PAGINA_DETALHE_CONCLUIDO;
        }

        return tela;
    }

    private static TituloTelaARCEnum telaEdicaoAnalise(Matriz matriz,
            ArcNotasVO avaliacaoRiscoControle, EstadoARCEnum estadoArc, UsuarioAplicacao usuario) {
        TituloTelaARCEnum tela = null;

        AvaliacaoRiscoControleMediator avaliacaoRiscoControleMediator = AvaliacaoRiscoControleMediator.get();

        if (avaliacaoRiscoControleMediator.estadoDesignadoInspetorResponsavel(avaliacaoRiscoControle, estadoArc,
                usuario)) {
            tela = TituloTelaARCEnum.PAGINA_EDICAO;
        } else if (avaliacaoRiscoControleMediator.estadoEmEdicaoInspetorResponsavel(avaliacaoRiscoControle, estadoArc,
                usuario)) {
            tela = TituloTelaARCEnum.PAGINA_EDICAO;
        } else if (avaliacaoRiscoControleMediator.estadoAnaliseDelegadaInspetorResponsavel(avaliacaoRiscoControle,
                estadoArc, usuario)) {
            tela = TituloTelaARCEnum.PAGINA_ANALISE;
        } else if (avaliacaoRiscoControleMediator.estadoEmAnaliseInspetorResponsavel(avaliacaoRiscoControle, estadoArc,
                usuario)) {
            tela = TituloTelaARCEnum.PAGINA_ANALISE;
        }
        return tela;
    }

    private static UsuarioAplicacao retornarUsuarioAplicacao() {
        return ((UsuarioAplicacao) UsuarioCorrente.get());
    }

    private static EstadoARCEnum retornarEstadoArc(final ArcNotasVO avaliacaoRiscoControle) {
        return avaliacaoRiscoControle.getEstado();
    }

    public static TituloTelaARCEnum avancarParaNovaPagina(final IModel<AvaliacaoRiscoControleVO> rowModel,
            PerfilAcessoEnum perfilAcesso, boolean isPerfilAtual) {
        ArcNotasVO avaliacaoRiscoControle =
                AvaliacaoRiscoControleMediator.get().consultarNotasArc(rowModel.getObject().getPk());
        Integer matrizVigente =
                rowModel.getObject().getMatrizVigente() == null ? rowModel.getObject().getAtividade().getMatriz()
                        .getPk() : rowModel.getObject().getMatrizVigente().getPk();
        Matriz matriz = MatrizCicloMediator.get().buscar(matrizVigente);
        return novaPaginaDeAcordoStatus(matriz, avaliacaoRiscoControle, false, isPerfilAtual,
                perfilAcesso);
    }

    public static TituloTelaARCEnum avancarParaNovaPaginaArcResumido(final IModel<ArcResumidoVO> rowModel,
            PerfilAcessoEnum perfilAcesso, boolean isPerfilAtual) {
        ArcNotasVO avaliacaoRiscoControle =
                AvaliacaoRiscoControleMediator.get().consultarNotasArc(rowModel.getObject().getPk());
        Integer matrizVigente = rowModel.getObject().getPkMatriz();
        Matriz matriz = MatrizCicloMediator.get().buscar(matrizVigente);
        return novaPaginaDeAcordoStatus(matriz, avaliacaoRiscoControle, false, isPerfilAtual, perfilAcesso);
    }

}
