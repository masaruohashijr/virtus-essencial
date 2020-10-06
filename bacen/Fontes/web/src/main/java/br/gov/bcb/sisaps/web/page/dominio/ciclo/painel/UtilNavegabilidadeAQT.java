package br.gov.bcb.sisaps.web.page.dominio.ciclo.painel;

import org.apache.wicket.model.IModel;

import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TituloTelaAnefEnum;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.util.Constantes;

public class UtilNavegabilidadeAQT {

    private static TituloTelaAnefEnum novaPaginaDeAcordoStatus(AnaliseQuantitativaAQT analiseQuantitativaAQT,
            PerfilRisco perfil, PerfilAcessoEnum perfilAcesso, boolean isPerfilAtual) {
        TituloTelaAnefEnum tela = null;
        
        if (PerfilAcessoEnum.CONSULTA_TUDO.equals(perfilAcesso)) {
        	tela = paginaConsulta(analiseQuantitativaAQT);
        } else if (!isPerfilAtual) {
            if (analiseQuantitativaAQT.getNotaVigenteDescricaoValor().equals(Constantes.ASTERISCO_A)) {
                tela = null;
            } else {
                tela = TituloTelaAnefEnum.PAGINA_DETALHE;
            }
        } else if (PerfilAcessoEnum.SUPERVISOR.equals(perfilAcesso)) {
            tela = paginaSupervisor(analiseQuantitativaAQT, perfil);
        } else if (PerfilAcessoEnum.INSPETOR.equals(perfilAcesso)) {
            tela = paginaInspetor(analiseQuantitativaAQT);
        } else if (PerfilAcessoEnum.GERENTE.equals(perfilAcesso)) {
            tela = paginaGerente(analiseQuantitativaAQT, perfil);
        }

        return tela;
    }

    private static TituloTelaAnefEnum paginaGerente(AnaliseQuantitativaAQT analiseQuantitativaAQT, PerfilRisco perfil) {
        TituloTelaAnefEnum tela = null;
        if (PerfilRiscoMediator.get().perfilAtual(analiseQuantitativaAQT.getCiclo(), perfil)
                || !analiseQuantitativaAQT.getNotaVigenteDescricaoValor().equals(Constantes.ASTERISCO_A)) {
            tela = TituloTelaAnefEnum.PAGINA_DETALHE;
        }
        return tela;
    }

    private static TituloTelaAnefEnum paginaConsulta(final AnaliseQuantitativaAQT analiseQuantitativaAQT) {
        TituloTelaAnefEnum tela = null;
        if (!analiseQuantitativaAQT.getNotaVigenteDescricaoValor().equals(Constantes.ASTERISCO_A)) {
            tela = TituloTelaAnefEnum.PAGINA_CONSULTA;
        }
        return tela;
    }

    private static TituloTelaAnefEnum telaAnaliseDetalhe(AnaliseQuantitativaAQT analiseQuantitativaAQT) {
        TituloTelaAnefEnum tela = null;
        if (AnaliseQuantitativaAQTMediator.get().telaAnaliseSupervisor(analiseQuantitativaAQT)) {
            tela = TituloTelaAnefEnum.PAGINA_ANALISE;
        } else if (AnaliseQuantitativaAQTMediator.get().telaDetalheSupervisor(analiseQuantitativaAQT)
                || !analiseQuantitativaAQT.getNotaVigenteDescricaoValor().equals(Constantes.ASTERISCO_A)) {
            tela = TituloTelaAnefEnum.PAGINA_DETALHE;
        }
        return tela;
    }

    private static TituloTelaAnefEnum paginaSupervisor(AnaliseQuantitativaAQT analiseQuantitativaAQT, PerfilRisco perfil) {
        TituloTelaAnefEnum tela = null;

        AnaliseQuantitativaAQT aqtBase = AnaliseQuantitativaAQTMediator.get().buscar(analiseQuantitativaAQT.getPk());
        AnaliseQuantitativaAQT aqtRascunho = null;
        if (AnaliseQuantitativaAQTMediator.get().estadoConcluido(aqtBase.getEstado())
                && !CicloMediator.get().cicloEmAndamento(aqtBase.getCiclo())) {
            tela = TituloTelaAnefEnum.PAGINA_DETALHE;
        } else {
            if (aqtBase.getVersaoPerfilRisco() == null) {
                aqtRascunho = aqtBase;
                tela = telaAnaliseDetalhe(aqtRascunho);
            } else {
                if (perfil == null) {
                    aqtRascunho = AnaliseQuantitativaAQTMediator.get().buscarAQTRascunho(aqtBase);
                    tela = telaAnaliseDetalhe(aqtRascunho);
                } else {
                    if (PerfilRiscoMediator.get().perfilAtual(analiseQuantitativaAQT.getCiclo(), perfil)) {
                        aqtRascunho = AnaliseQuantitativaAQTMediator.get().buscarAQTRascunho(aqtBase);
                        tela = telaAnaliseDetalhe(aqtRascunho);
                    } else if (!analiseQuantitativaAQT.getNotaVigenteDescricaoValor().equals(Constantes.ASTERISCO_A)) {
                        tela = TituloTelaAnefEnum.PAGINA_DETALHE;
                    }
                }
            }
        }
        return tela;
    }

    private static TituloTelaAnefEnum paginaInspetor(AnaliseQuantitativaAQT analiseQuantitativaAQT) {
        TituloTelaAnefEnum tela = null;
        AnaliseQuantitativaAQT aqtBase = AnaliseQuantitativaAQTMediator.get().buscar(analiseQuantitativaAQT.getPk());
        AnaliseQuantitativaAQT aqtRascunho = null;
        String nota = null;

        if (aqtBase.getVersaoPerfilRisco() == null) {
            aqtRascunho = aqtBase;
            aqtBase = AnaliseQuantitativaAQTMediator.get().buscarAQTVigente(aqtRascunho);
        } else {
            aqtRascunho = AnaliseQuantitativaAQTMediator.get().buscarAQTRascunho(aqtBase);
        }
        tela = telaEdicaoAnaliseDetalheInspetor(analiseQuantitativaAQT, aqtRascunho);
        nota = aqtBase.getNotaVigenteDescricaoValor();
        if (tela == null) {
            if (!CicloMediator.get().cicloEmAndamento(aqtRascunho.getCiclo()) || !(nota).equals(Constantes.ASTERISCO_A)) {
                tela = TituloTelaAnefEnum.PAGINA_DETALHE;
            }
        }
        return tela;
    }

    private static TituloTelaAnefEnum telaEdicaoAnaliseDetalheInspetor(AnaliseQuantitativaAQT analiseQuantitativaAQT,
            AnaliseQuantitativaAQT aqtRascunho) {
        TituloTelaAnefEnum tela = null;
        if (AnaliseQuantitativaAQTMediator.get().verificarTelaDestinoDesignado(aqtRascunho)) {
            tela = TituloTelaAnefEnum.PAGINA_EDICAO;
        } else if (AnaliseQuantitativaAQTMediator.get().verificarTelaDestinoDelegado(aqtRascunho)) {
            tela = TituloTelaAnefEnum.PAGINA_ANALISE;
        }
        return tela;
    }

    public static TituloTelaAnefEnum buscarPaginaDeAcordoEstado(final IModel<AnaliseQuantitativaAQT> rowModel,
            PerfilAcessoEnum perfilAcesso, boolean isPerfilAtual) {
        AnaliseQuantitativaAQT aqtBase = AnaliseQuantitativaAQTMediator.get().buscar(rowModel.getObject().getPk());
        return novaPaginaDeAcordoStatus(aqtBase, null, perfilAcesso, isPerfilAtual);
    }

    public static TituloTelaAnefEnum buscarPaginaDeAcordoEstado(final IModel<AnaliseQuantitativaAQT> rowModel,
            PerfilRisco perfilRisco, PerfilAcessoEnum perfilAcesso, boolean isPerfilAtual) {
        AnaliseQuantitativaAQT aqtBase = AnaliseQuantitativaAQTMediator.get().buscar(rowModel.getObject().getPk());
        return novaPaginaDeAcordoStatus(aqtBase, perfilRisco, perfilAcesso, isPerfilAtual);
    }

}
