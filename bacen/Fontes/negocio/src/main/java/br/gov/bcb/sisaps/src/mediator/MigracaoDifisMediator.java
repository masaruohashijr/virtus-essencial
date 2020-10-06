package br.gov.bcb.sisaps.src.mediator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.src.dao.batchmigracaodifis.NotaRiscoQualitativaDAO;
import br.gov.bcb.sisaps.src.dao.batchmigracaodifis.NotasArcsQualitativaDAO;
import br.gov.bcb.sisaps.src.dao.batchmigracaodifis.NotasComponentesQuantitativaDAO;
import br.gov.bcb.sisaps.src.dao.batchmigracaodifis.NotasElementosQualitativaDAO;
import br.gov.bcb.sisaps.src.dao.batchmigracaodifis.NotasElementosQuantitativaDAO;
import br.gov.bcb.sisaps.src.dao.batchmigracaodifis.NotasGeraisFinalDAO;
import br.gov.bcb.sisaps.src.dao.batchmigracaodifis.PerfilRiscoMigracaoDAO;
import br.gov.bcb.sisaps.src.dominio.AjusteCorec;
import br.gov.bcb.sisaps.src.dominio.Atividade;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoARC;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControleExterno;
import br.gov.bcb.sisaps.src.dominio.CelulaRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Elemento;
import br.gov.bcb.sisaps.src.dominio.GrauPreocupacaoES;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.Metodologia;
import br.gov.bcb.sisaps.src.dominio.NotaMatriz;
import br.gov.bcb.sisaps.src.dominio.ParametroGrupoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.ParametroNota;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.PerspectivaES;
import br.gov.bcb.sisaps.src.dominio.SinteseDeRisco;
import br.gov.bcb.sisaps.src.dominio.VersaoPerfilRisco;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ElementoAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.NotaAjustadaAEF;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroNotaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.PesoAQT;
import br.gov.bcb.sisaps.src.dominio.batchmigracaodifis.NotasArcsQualitativa;
import br.gov.bcb.sisaps.src.dominio.batchmigracaodifis.NotasComponentesQuantitativa;
import br.gov.bcb.sisaps.src.dominio.batchmigracaodifis.NotasElementosQualitativa;
import br.gov.bcb.sisaps.src.dominio.batchmigracaodifis.NotasElementosQuantitativa;
import br.gov.bcb.sisaps.src.dominio.batchmigracaodifis.NotasGeraisFinal;
import br.gov.bcb.sisaps.src.dominio.batchmigracaodifis.NotasRiscoControleQualitativa;
import br.gov.bcb.sisaps.src.dominio.batchmigracaodifis.PerfilRiscoMigracao;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoARCEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfisNotificacaoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoGrupoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoObjetoVersionadorEnum;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.NotaAjustadaAEFMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.PesoAQTMediator;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.utils.logging.BCLogFactory;
import br.gov.bcb.utils.logging.BCLogger;

@Service
@Transactional(readOnly = true)
public class MigracaoDifisMediator {

    private static final String COREC = " (Corec)";

    private static final String CRIADA = " CRIADA.";

    private static final BCLogger LOG = BCLogFactory.getLogger("MigracaoDifisMediator");

    @Autowired
    private NotasGeraisFinalDAO notasGeraisFinalDAO;

    @Autowired
    private NotaRiscoQualitativaDAO notaRiscoQualitativaDAO;

    @Autowired
    private NotasArcsQualitativaDAO notasArcsQualitativaDAO;

    @Autowired
    private NotasElementosQualitativaDAO notasElementosQualitativaDAO;

    @Autowired
    private NotasComponentesQuantitativaDAO notasComponentesQuantitativaDAO;

    @Autowired
    private NotasElementosQuantitativaDAO notasElementosQuantitativaDAO;

    @Autowired
    private PerfilRiscoMigracaoDAO perfilRiscoMigracaoDAO;

    @Autowired
    private PerfilRiscoMediator perfilRiscoMediator;

    @Autowired
    private VersaoPerfilRiscoMediator versaoPerfilRiscoMediator;

    @Autowired
    private AtividadeMediator atividadeMediator;

    @Autowired
    private LinhaMatrizMediator linhaMatrizMediator;

    @Autowired
    private MatrizCicloMediator matrizCicloMediator;

    @Autowired
    private ParametroNotaMediator parametroNotaMediator;

    @Autowired
    private ParametroGrupoRiscoControleMediator parametroGrupoRiscoControleMediator;

    @Autowired
    private AnaliseQuantitativaAQTMediator analiseQuantitativaAQTMediator;

    @Autowired
    private NotaAjustadaAEFMediator notaAjustadaAEFMediator;

    @Autowired
    private GrauPreocupacaoESMediator grauPreocupacaoESMediator;

    @Autowired
    private PerspectivaESMediator perspectivaESMediator;

    @Autowired
    private AvaliacaoRiscoControleMediator avaliacaoRiscoControleMediator;

    @Autowired
    private SinteseDeRiscoMediator sinteseDeRiscoMediator;

    @Autowired
    private AjusteCorecMediator ajusteCorecMediator;

    @Autowired
    private NotaMatrizMediator notaMatrizMediator;

    @Autowired
    private CicloMediator cicloMediator;

    @Autowired
    private PesoAQTMediator pesoAQTMediator;

    @Autowired
    private AvaliacaoARCMediator avaliacaoARCMediator;

    @Autowired
    private AvaliacaoRiscoControleExternoMediator avaliacaoRiscoControleExternoMediator;

    @Autowired
    private MetodologiaMediator metodologiaMediator;

    @Autowired
    public static MigracaoDifisMediator get() {
        return SpringUtils.get().getBean(MigracaoDifisMediator.class);
    }

    public void criarDadosPerfilRiscoCiclo(Ciclo ciclo, NotasGeraisFinal notasGeraisFinal) {
        List<PerfilRisco> perfis = perfilRiscoMediator.consultarPerfisRiscoCiclo(ciclo.getPk(), true);
        for (int i = 0; i < perfis.size(); i++) {
            PerfilRisco perfilAnterior;
            PerfilRisco perfilAtual = perfis.get(i);
            if (i > 0) {
                perfilAnterior = perfis.get(i - 1);
                List<VersaoPerfilRisco> versoesCriadas =
                        versaoPerfilRiscoMediator.buscarVersoesCriadasPerfilAtual(perfilAtual.getPk(),
                                versaoPerfilRiscoMediator.buscarCodigosVersoesPerfilRisco(perfilAnterior.getPk()));
                salvarPerfilRisco(perfilAtual, versoesCriadas, notasGeraisFinal);
            } else {
                salvarPerfilRisco(perfilAtual, perfilAtual.getVersoesPerfilRisco(), notasGeraisFinal);
            }
        }
    }

    public NotasGeraisFinal criarDadosNotasGerais(Ciclo ciclo, PerfilRisco perfilRisco, Matriz matriz,
            List<VersaoPerfilRisco> versoesPerfilRiscoCelulas, List<CelulaRiscoControle> listaChoices) {
        NotasGeraisFinal notasGeraisFinal = new NotasGeraisFinal();

        // ES
        setDadosESNotasGerais(ciclo, perfilRisco, notasGeraisFinal);

        // Ciclo
        setDadosCicloNotasGerais(ciclo, perfilRisco, notasGeraisFinal);

        // Notas Qlt
        setNotasQltNotasGerais(ciclo, perfilRisco, matriz, versoesPerfilRiscoCelulas, listaChoices, notasGeraisFinal);

        // Notas Qnt
        setNotasQntNotasGerais(perfilRisco, notasGeraisFinal);

        salvarNotaGeral(notasGeraisFinal);
        return notasGeraisFinal;
    }

    public void criarNotasGruposArcsQlt(PerfilRisco perfilRisco, Matriz matriz, List<CelulaRiscoControle> listaChoices,
            List<VersaoPerfilRisco> versoesPerfilRiscoCelulas, NotasGeraisFinal notasGeraisFinal) {
        List<CelulaRiscoControle> listaCelulaGrupo;
        List<Integer> idsParametrosGrupoRiscoMatriz =
                parametroGrupoRiscoControleMediator.buscarIdsGruposRiscoDaMatriz(matriz);
        List<ParametroGrupoRiscoControle> listaGrupo =
                parametroGrupoRiscoControleMediator
                        .buscarGruposRiscoDaMatrizESinteseObrigatoria(idsParametrosGrupoRiscoMatriz);
        for (ParametroGrupoRiscoControle grupo : listaGrupo) {
            NotasRiscoControleQualitativa notaRiscoQualitativa = new NotasRiscoControleQualitativa();
            listaCelulaGrupo = linhaMatrizMediator.listaCelulaGrupo(listaChoices, grupo, matriz);

            // Notas gerais
            notaRiscoQualitativa.setNotasGeraisFinal(notasGeraisFinal);

            // Nome
            notaRiscoQualitativa.setNomeRiscoControle(grupo.getNomeRisco());

            // Notas
            setNotasGrupoRiscoControle(perfilRisco, matriz, versoesPerfilRiscoCelulas, listaCelulaGrupo, grupo,
                    notaRiscoQualitativa);

            // Percentual grupo
            String percentualGrupo =
                    matrizCicloMediator.percentualDoGrupo(matriz, listaCelulaGrupo, versoesPerfilRiscoCelulas);
            percentualGrupo = percentualGrupo.substring(0, percentualGrupo.length() - 1);
            notaRiscoQualitativa.setPercentualRiscoControle(convertStringToBigDecimal(percentualGrupo));

            List<VersaoPerfilRisco> versoesPerfilRiscoSinteses =
                    versaoPerfilRiscoMediator.buscarVersoesPerfilRisco(perfilRisco.getPk(),
                            TipoObjetoVersionadorEnum.SINTESE_RISCO);

            // Síntese
            SinteseDeRisco sinteseDeRisco =
                    sinteseDeRiscoMediator.getUltimaSinteseParametroGrupoRisco(grupo, perfilRisco.getCiclo(),
                            versoesPerfilRiscoSinteses);

            if (sinteseDeRisco != null) {
                notaRiscoQualitativa.setDataSintese(sinteseDeRisco.getUltimaAtualizacao());
                notaRiscoQualitativa.setOperadorSintese(sinteseDeRisco.getOperadorAtualizacao());
            }

            salvarNotaRiscoQualitativa(notaRiscoQualitativa);
            criarNotasArcsElementosQlt(listaCelulaGrupo, perfilRisco, notasGeraisFinal, notaRiscoQualitativa);
        }

        salvarNotasArcExterno(perfilRisco, notasGeraisFinal);
    }

    private void salvarNotasArcExterno(PerfilRisco perfilRisco, NotasGeraisFinal notasGeraisFinal) {

        // Arc EXTERNO e seus elementos 
        AvaliacaoRiscoControle arcExterno =
                avaliacaoRiscoControleMediator.buscarArcExternoPorPerfilRisco(perfilRisco.getPk());

        if (SisapsUtil.isNaoNuloOuVazio(arcExterno)) {
            AvaliacaoRiscoControle arcExternoInicializado = getArcVigente(arcExterno, perfilRisco.getCiclo());
            if (SisapsUtil.isNaoNuloOuVazio(arcExternoInicializado)) {
                NotasRiscoControleQualitativa notaRiscoQualitativa = new NotasRiscoControleQualitativa();

                // Notas gerais
                notaRiscoQualitativa.setNotasGeraisFinal(notasGeraisFinal);

                AvaliacaoRiscoControleExterno arc =
                        avaliacaoRiscoControleExternoMediator.buscarArcExterno(arcExternoInicializado.getPk());

                // Nome
                ParametroGrupoRiscoControle parametroGrupo = arc.getParametroGrupoRiscoControle();

                notaRiscoQualitativa.setNomeRiscoControle(parametroGrupo.getNomeAbreviado());

                // Síntese
                SinteseDeRisco sinteseDeRisco =
                        sinteseDeRiscoMediator.getUltimaSinteseParametroGrupoRisco(parametroGrupo,
                                perfilRisco.getCiclo(), null);

                if (sinteseDeRisco != null) {
                    notaRiscoQualitativa.setDataSintese(sinteseDeRisco.getUltimaAtualizacao());
                    notaRiscoQualitativa.setOperadorSintese(sinteseDeRisco.getOperadorAtualizacao());
                }

                // Notas Arc Externo
                String valorNota = arcExternoInicializado.getNotaVigenteDescricaoValor();
                if (possuiNotaVigente(valorNota)) {
                    if (possuiNotaVigente(valorNota)) {
                        notaRiscoQualitativa.setNotaResidualFinalVig(convertStringToBigDecimal(valorNota));
                    }

                    // Conceito residual.
                    ParametroNota notaRefinada =
                            parametroNotaMediator.buscarPorMetodologiaENota(perfilRisco.getCiclo().getMetodologia(),
                                    new BigDecimal(valorNota.replace(',', '.')), true);
                    if (possuiNotaVigente(notaRefinada)) {
                        notaRiscoQualitativa.setConceitoGeralFinalVig(notaRefinada.getDescricaoValor());
                    }
                }

                salvarNotaRiscoQualitativa(notaRiscoQualitativa);
                NotasArcsQualitativa notasArcsQualitativaControle =
                        criarDadosNotasArc(null, arcExternoInicializado, perfilRisco, notasGeraisFinal,
                                notaRiscoQualitativa, arcExterno.getPk());
                criarDadosElementosArc(arcExternoInicializado.getElementos(), perfilRisco.getCiclo(),
                        notasArcsQualitativaControle);
            }
        }
    }

    public AvaliacaoRiscoControle getArcVigente(AvaliacaoRiscoControle arc, Ciclo ciclo) {
        AvaliacaoRiscoControle arcVigente = null;
        if (EstadoARCEnum.CONCLUIDO.equals(arc.getEstado())) {
            arcVigente = avaliacaoRiscoControleMediator.loadPK(arc.getPk());
        } else if (arc.getAvaliacaoRiscoControleVigente() != null) {
            arcVigente = avaliacaoRiscoControleMediator.loadPK(arc.getAvaliacaoRiscoControleVigente().getPk());
        }
        return arcVigente;
    }

    private void criarNotasArcsElementosQlt(List<CelulaRiscoControle> listaChoices, PerfilRisco perfilRisco,
            NotasGeraisFinal notasGeraisFinal, NotasRiscoControleQualitativa notasRiscoControleQualitativa) {
        for (CelulaRiscoControle celulaRiscoControle : listaChoices) {
            AvaliacaoRiscoControle arcRiscoInicializado =
                    getArcVigente(celulaRiscoControle.getArcRisco(), perfilRisco.getCiclo());
            AvaliacaoRiscoControle arcControleInicializado =
                    getArcVigente(celulaRiscoControle.getArcControle(), perfilRisco.getCiclo());

            // Arcs e elementos RISCO
            if (SisapsUtil.isNaoNuloOuVazio(arcRiscoInicializado)) {
                NotasArcsQualitativa notasArcsQualitativaRisco =
                        criarDadosNotasArc(celulaRiscoControle, arcRiscoInicializado, perfilRisco, notasGeraisFinal,
                                notasRiscoControleQualitativa, celulaRiscoControle.getArcRisco().getPk());
                criarDadosElementosArc(arcRiscoInicializado.getElementos(), perfilRisco.getCiclo(),
                        notasArcsQualitativaRisco);
            }

            // Arcs e elementos CONTROLE
            if (SisapsUtil.isNaoNuloOuVazio(arcControleInicializado)) {
                NotasArcsQualitativa notasArcsQualitativaControle =
                        criarDadosNotasArc(celulaRiscoControle, arcControleInicializado, perfilRisco, notasGeraisFinal,
                                notasRiscoControleQualitativa, celulaRiscoControle.getArcControle().getPk());
                criarDadosElementosArc(arcControleInicializado.getElementos(), perfilRisco.getCiclo(),
                        notasArcsQualitativaControle);
            }
        }

    }

    private NotasArcsQualitativa criarDadosNotasArc(CelulaRiscoControle celulaRiscoControle,
            AvaliacaoRiscoControle arcVigente, PerfilRisco perfilRisco, NotasGeraisFinal notasGeraisFinal,
            NotasRiscoControleQualitativa notasRiscoControleQualitativa, Integer pkArcAtual) {
        Atividade atividade =
                celulaRiscoControle == null ? null : atividadeMediator.loadPK(celulaRiscoControle.getAtividade()
                        .getPk());
        NotasArcsQualitativa notasArcsQualitativa = new NotasArcsQualitativa();

        // Arc
        notasArcsQualitativa.setNotasGeraisFinal(notasGeraisFinal);
        notasArcsQualitativa.setNotasRiscoControleQualitativa(notasRiscoControleQualitativa);
        if (SisapsUtil.isNaoNuloOuVazio(atividade)) {
            notasArcsQualitativa.setNomeAtividade(atividade.getNome());
        }

        if (SisapsUtil.isNaoNuloOuVazio(arcVigente.getTipo())
                && SisapsUtil.isNaoNuloOuVazio(arcVigente.getTipo().getDescricao())) {
            notasArcsQualitativa.setTipoArc(arcVigente.getTipo().getDescricao());
        }
        if (SisapsUtil.isNaoNuloOuVazio(arcVigente.getEstado())
                && SisapsUtil.isNaoNuloOuVazio(arcVigente.getEstado().getDescricao())) {
            notasArcsQualitativa.setEstadoArc(arcVigente.getEstado().getDescricao());
        }

        if (SisapsUtil.isNaoNuloOuVazio(atividade) && SisapsUtil.isNaoNuloOuVazio(atividade.getUnidade())) {
            notasArcsQualitativa.setNomeUnidade(atividade.getUnidade().getNome());
        }
        if (SisapsUtil.isNaoNuloOuVazio(arcVigente.getTendenciaARCVigenteValor())) {
            notasArcsQualitativa.setTendenciaFinalVigente(arcVigente.getTendenciaARCVigenteValor());
        }
        if (SisapsUtil.isNaoNuloOuVazio(arcVigente.getTendenciaARCInspetorValor())) {
            notasArcsQualitativa.setTendenciaInspetor(arcVigente.getTendenciaARCInspetorValor());
        }
        if (SisapsUtil.isNaoNuloOuVazio(arcVigente.getTendenciaARCInspetorOuSupervisorValor())) {
            notasArcsQualitativa.setTendenciaSupervisor(arcVigente.getTendenciaARCInspetorOuSupervisorValor());
        }

        // Dados auditoria
        setDadosAuditoriaArc(arcVigente, notasArcsQualitativa);

        // Notas
        setNotasArc(arcVigente, notasArcsQualitativa, perfilRisco, pkArcAtual);

        // Peso
        if (SisapsUtil.isNaoNuloOuVazio(atividade) && SisapsUtil.isNaoNuloOuVazio(atividade.getParametroPeso())) {
            notasArcsQualitativa.setPesoAtividade(new BigDecimal(atividade.getParametroPeso().getValor()));
        }
        salvarNotaArcQualitativa(notasArcsQualitativa);
        return notasArcsQualitativa;
    }

    private void criarDadosElementosArc(List<Elemento> elementos, Ciclo ciclo, NotasArcsQualitativa notasArcsQualitativa) {
        for (Elemento elemento : elementos) {
            NotasElementosQualitativa notasElementosQualitativa = new NotasElementosQualitativa();
            notasElementosQualitativa.setNotasArcsQualitativa(notasArcsQualitativa);
            notasElementosQualitativa.setNomeElemento(elemento.getParametroElemento().getNome());
            if (possuiNotaVigenteElemento(elemento.getParametroNotaInspetor())) {
                notasElementosQualitativa.setNotaElementoQltFinalInspetor(convertStringToBigDecimal(elemento
                        .getParametroNotaInspetor().getValorString()));
            }
            if (possuiNotaVigenteElemento(elemento.getNotaSupervisor())) {
                String notaSupervisor = elemento.getNotaSupervisor();
                notasElementosQualitativa.setNotaElementoQltFinalVigente(getNotaElemento(notaSupervisor));
                notasElementosQualitativa.setNotaElementoQltFinalSupervisor(getNotaElemento(notaSupervisor));
            }
            if (SisapsUtil.isNaoNuloOuVazio(notasElementosQualitativa.getNotaElementoQltFinalVigente())) {
                salvarNotaElementoQualitativa(notasElementosQualitativa);
            }
        }
    }

    public void criarComponentesElementosQnt(PerfilRisco perfilRisco, NotasGeraisFinal notasGeraisFinal) {
        List<AnaliseQuantitativaAQT> anefsVigentesCicloAtual =
                analiseQuantitativaAQTMediator.buscarAQTsPerfilRisco(versaoPerfilRiscoMediator
                        .buscarVersoesPerfilRisco(perfilRisco.getPk(), TipoObjetoVersionadorEnum.AQT));
        if (SisapsUtil.isNaoNuloOuVazio(anefsVigentesCicloAtual)) {
            for (AnaliseQuantitativaAQT anef : anefsVigentesCicloAtual) {
                String notaVigente =
                        analiseQuantitativaAQTMediator.notaAnef(anef, perfilRisco.getCiclo(),
                                PerfilAcessoEnum.SUPERVISOR, false, perfilRisco);
                String notaAjustada = analiseQuantitativaAQTMediator.notaAjustadaSupervisorOuInspetor(anef);
                NotasComponentesQuantitativa notasComponentesQuantitativa = new NotasComponentesQuantitativa();
                notasComponentesQuantitativa.setNotasGeraisFinal(notasGeraisFinal);
                notasComponentesQuantitativa.setNomeComponente(anef.getParametroAQT().getDescricao());
                notasComponentesQuantitativa.setEstado(anef.getEstado().getDescricao());
                if (possuiNotaVigente(anef.getNotaCalculadaSupervisorOuInspetor())) {
                    notasComponentesQuantitativa.setNotaComponenteCalculada(convertStringToBigDecimal(anef
                            .getNotaCalculadaSupervisorOuInspetor()));
                }
                if (possuiNotaVigente(notaVigente)) {
                    notasComponentesQuantitativa.setNotaComponenteFinalVig(convertStringToBigDecimal(notaVigente));
                }
                if (possuiNotaVigente(anef.getNotaCorec())) {
                    notasComponentesQuantitativa.setNotaComponenteAjustadaCorec(convertStringToBigDecimal(anef
                            .getNotaCorec()));
                }
                if (possuiNotaVigente(notaAjustada)) {
                    notasComponentesQuantitativa
                            .setNotaComponenteAjustadaSuperv(convertStringToBigDecimal(notaAjustada));
                }
                PesoAQT pesoVigente = pesoAQTMediator.obterPesoVigente(anef.getParametroAQT(), anef.getCiclo());
                if (SisapsUtil.isNaoNuloOuVazio(pesoVigente)) {
                    notasComponentesQuantitativa.setPeso(new BigDecimal(pesoVigente.getValor()));
                }
                setDadosAuditoriaAnef(anef, notasComponentesQuantitativa);
                salvarNotaComponenteQuantitativa(notasComponentesQuantitativa);
                criarDadosElementosQnt(anef.getElementos(), perfilRisco.getCiclo(), notasComponentesQuantitativa);
            }
        }
    }

    private void criarDadosElementosQnt(List<ElementoAQT> elementos, Ciclo ciclo,
            NotasComponentesQuantitativa notasComponentesQuantitativa) {
        for (ElementoAQT elemento : elementos) {
            NotasElementosQuantitativa notasElementosQuantitativa = new NotasElementosQuantitativa();
            notasElementosQuantitativa.setNotasComponentesQuantitativa(notasComponentesQuantitativa);
            notasElementosQuantitativa.setNomeElemento(elemento.getParametroElemento().getDescricao());
            if (possuiNotaVigenteElemento(elemento.getParametroNotaInspetor())) {
                notasElementosQuantitativa.setNotaElementoQntFinalInspetor(convertStringToBigDecimal(elemento
                        .getParametroNotaInspetor().getValorString()));
            }
            if (possuiNotaVigenteElemento(elemento.getNotaSupervisor())) {
                notasElementosQuantitativa
                        .setNotaElementoQntFinalVigente(getNotaElemento(elemento.getNotaSupervisor()));
                notasElementosQuantitativa.setNotaElementoQntFinalSupervisor(getNotaElemento(elemento
                        .getNotaSupervisor()));
            }
            salvarNotaElementoQuantitativa(notasElementosQuantitativa);
        }
    }

    private void setNotasQntNotasGerais(PerfilRisco perfilRisco, NotasGeraisFinal notasGeraisFinal) {
        String notaCalculadaQnt =
                analiseQuantitativaAQTMediator.getNotaCalculadaAEF(perfilRisco, PerfilAcessoEnum.SUPERVISOR, false);
        NotaAjustadaAEF notaQntAjustadaAEF =
                notaAjustadaAEFMediator.buscarNotaAjustadaAEF(perfilRisco.getCiclo(), perfilRisco);
        String notaRefinadaAEF =
                analiseQuantitativaAQTMediator.getNotaRefinadaAEF(perfilRisco, PerfilAcessoEnum.SUPERVISOR, false);
        ParametroNotaAQT notaAjustadaCorec =
                ajusteCorecMediator.notaAjustadaCorecAEF(perfilRisco, perfilRisco.getCiclo(),
                        PerfilAcessoEnum.SUPERVISOR);

        // Nota refinada
        if (possuiNotaVigente(notaRefinadaAEF)) {
            notasGeraisFinal.setNotaQntRefinada(notaRefinadaAEF);
        }

        // Nota calculada
        if (possuiNotaVigente(notaCalculadaQnt)) {
            notasGeraisFinal.setNotaQntCalculada(convertStringToBigDecimal(notaCalculadaQnt));
        }

        // Nota ajustada supervisor
        if (SisapsUtil.isNaoNuloOuVazio(notaQntAjustadaAEF)
                && possuiNotaVigente(notaQntAjustadaAEF.getParamentroNotaAQT())) {
            ParametroNotaAQT notaAjustada = notaQntAjustadaAEF.getParamentroNotaAQT();
            String notaAjustadaString =
                    perfilRisco.getCiclo().getMetodologia().getIsMetodologiaNova() ? notaAjustada.getDescricao()
                            : notaAjustada.getValorString();
            notasGeraisFinal.setNotaQntAjustadaSuperv(notaAjustadaString);
        } else {
            Ciclo cicloPerfilInicializado = cicloMediator.load(perfilRisco.getCiclo());
            Ciclo cicloAnterior =
                    cicloMediator.consultarUltimoCicloPosCorecEncerradoES(cicloPerfilInicializado
                            .getEntidadeSupervisionavel().getPk());
            if (SisapsUtil.isNaoNuloOuVazio(cicloAnterior)) {
                NotaAjustadaAEF notaQntAjustadaAEFAnterior =
                        notaAjustadaAEFMediator.buscarNotaAjustadaAEF(cicloAnterior,
                                perfilRiscoMediator.obterPerfilRiscoAtual(cicloAnterior.getPk()));
                if (SisapsUtil.isNaoNuloOuVazio(notaQntAjustadaAEFAnterior)
                        && SisapsUtil.isNaoNuloOuVazio(notaQntAjustadaAEFAnterior.getParamentroNotaAQT())) {
                    ParametroNotaAQT notaAjustada = notaQntAjustadaAEFAnterior.getParamentroNotaAQT();
                    String notaAjustadaString =
                            perfilRisco.getCiclo().getMetodologia().getIsMetodologiaNova() ? notaAjustada
                                    .getDescricao() : notaAjustada.getValorString();
                    notasGeraisFinal.setNotaQntAjustadaSuperv(notaAjustadaString);
                }
            }
        }

        // Nota Corec
        if (SisapsUtil.isNaoNuloOuVazio(notaAjustadaCorec)) {
            String notaAjustadaString =
                    perfilRisco.getCiclo().getMetodologia().getIsMetodologiaNova() ? notaAjustadaCorec.getDescricao()
                            : notaAjustadaCorec.getValorString();
            notasGeraisFinal.setNotaQntAjustadaCorec(notaAjustadaString);
        }

        // Nota Final
        if (SisapsUtil.isNaoNuloOuVazio(notaAjustadaCorec)) {
            String notaAjustadaString =
                    perfilRisco.getCiclo().getMetodologia().getIsMetodologiaNova() ? notaAjustadaCorec.getDescricao()
                            : notaAjustadaCorec.getValorString();
            notasGeraisFinal.setNotaQntFinalVigente(notaAjustadaString);
        } else if (SisapsUtil.isNaoNuloOuVazio(notaQntAjustadaAEF)
                && possuiNotaVigente(notaQntAjustadaAEF.getParamentroNotaAQT())) {
            String notaAjustadaString =
                    perfilRisco.getCiclo().getMetodologia().getIsMetodologiaNova() ? notaQntAjustadaAEF
                            .getParamentroNotaAQT().getDescricao() : notaQntAjustadaAEF.getParamentroNotaAQT()
                            .getValorString();
            notasGeraisFinal.setNotaQntFinalVigente(notaAjustadaString);
        } else if (SisapsUtil.isNaoNuloOuVazio(notasGeraisFinal.getNotaQntRefinada())) {
            notasGeraisFinal.setNotaQntFinalVigente(notasGeraisFinal.getNotaQntRefinada());
            notasGeraisFinal.setNotaQntAjustadaSuperv(null);
            notasGeraisFinal.setNotaQntAjustadaCorec(null);
        }
    }

    private void setNotasQltNotasGerais(Ciclo ciclo, PerfilRisco perfilRisco, Matriz matriz,
            List<VersaoPerfilRisco> versoesPerfilRiscoCelulas, List<CelulaRiscoControle> listaChoices,
            NotasGeraisFinal notasGeraisFinal) {

        // Caso exista, consulta ARC externo
        AvaliacaoRiscoControle arcExterno =
                AvaliacaoRiscoControleMediator.get().buscarArcExternoPorPerfilRisco(perfilRisco.getPk());

        Metodologia metodologia = perfilRisco.getCiclo().getMetodologia();
        
        String notaCalculadaMatriz = matrizCicloMediator.notaCalculada(matriz, listaChoices, versoesPerfilRiscoCelulas,
                true,
                PerfilAcessoEnum.SUPERVISOR, perfilRisco);

        String notaCalculadaQlt;
        if (SisapsUtil.isNaoNuloOuVazio(arcExterno)) {
            notaCalculadaQlt =
                    matrizCicloMediator.notaCalculadaFinal(matriz, listaChoices, versoesPerfilRiscoCelulas, true,
                            PerfilAcessoEnum.SUPERVISOR, perfilRisco, arcExterno);

            // Nota arc externo
            String notaArcExterno =
                    avaliacaoRiscoControleMediator.notaArc(arcExterno, perfilRisco.getCiclo(),
                            PerfilAcessoEnum.SUPERVISOR, perfilRisco);
            if (possuiNotaVigente(notaArcExterno)) {
                notasGeraisFinal.setNotaFinalGovernanca(new BigDecimal(notaArcExterno.replace(',', '.')));
            }
        } else {
            notaCalculadaQlt = notaCalculadaMatriz;
        }
        
        // Nota calculada matriz
        if (possuiNotaVigente(notaCalculadaMatriz)) {
            notasGeraisFinal.setNotaMatrizCalc(new BigDecimal(notaCalculadaMatriz.replace(',', '.')));
        }

        NotaMatriz notaSupervidor = perfilRiscoMediator.getNotaMatrizPerfilRisco(perfilRisco);
        AjusteCorec ajusteCorec = ajusteCorecMediator.buscarPorCiclo(perfilRisco.getCiclo().getPk());
        List<String> notaFinal = notaMatrizMediator.notaAjustada(matriz, perfilRisco, PerfilAcessoEnum.SUPERVISOR);

        // Nota calculada
        if (possuiNotaVigente(notaCalculadaQlt)) {
            notasGeraisFinal.setNotaQltCalculada(convertStringToBigDecimal(notaCalculadaQlt));
            ParametroNota notaRefinada =
                    parametroNotaMediator.buscarPorMetodologiaENota(ciclo.getMetodologia(), new BigDecimal(
                            notaCalculadaQlt.replace(',', '.')), false);
            // Nota refinada
            if (possuiNotaVigente(notaRefinada)) {
                String notaRefinadaString =
                        metodologia.getIsMetodologiaNova() ? notaRefinada.getDescricao() : notaRefinada
                                .getDescricaoValor();
                notasGeraisFinal.setNotaQltRefinada(notaRefinadaString);
            }
        }

        // Nota supervisor
        if (SisapsUtil.isNaoNuloOuVazio(notaSupervidor)
                && SisapsUtil.isNaoNuloOuVazio(notaSupervidor.getNotaFinalMatriz())
                && possuiNotaVigente(notaSupervidor.getNotaFinalMatriz())) {
            ParametroNota notaAjustada = notaSupervidor.getNotaFinalMatriz();
            String notaAjustadaString =
                    metodologia.getIsMetodologiaNova() ? notaAjustada.getDescricao() : notaAjustada.getDescricaoValor();
            notasGeraisFinal.setNotaQltAjustadaSuperv(notaAjustadaString);
        } else {
            Ciclo cicloPerfilInicializado = cicloMediator.load(ciclo);
            Ciclo cicloAnterior =
                    cicloMediator.consultarUltimoCicloPosCorecEncerradoES(cicloPerfilInicializado
                            .getEntidadeSupervisionavel().getPk());
            if (SisapsUtil.isNaoNuloOuVazio(cicloAnterior)) {
                NotaMatriz notaAnterior =
                        perfilRiscoMediator.getNotaMatrizPerfilRisco(perfilRiscoMediator
                                .obterPerfilRiscoAtual(cicloAnterior.getPk()));
                if (SisapsUtil.isNaoNuloOuVazio(notaAnterior)
                        && SisapsUtil.isNaoNuloOuVazio(notaAnterior.getNotaFinalMatriz())) {
                    ParametroNota notaAjustada = notaAnterior.getNotaFinalMatriz();
                    String notaAjustadaString =
                            metodologia.getIsMetodologiaNova() ? notaAjustada.getDescricao() : notaAjustada
                                    .getDescricaoValor();
                    notasGeraisFinal.setNotaQltAjustadaSuperv(notaAjustadaString);
                }
            }
        }

        // Nota corec
        if (SisapsUtil.isNaoNuloOuVazio(ajusteCorec) && SisapsUtil.isNaoNuloOuVazio(ajusteCorec.getNotaQualitativa())) {
            ParametroNota notaAjustadaCorec = ajusteCorec.getNotaQualitativa();
            String notaAjustadaCorecString =
                    metodologia.getIsMetodologiaNova() ? notaAjustadaCorec.getDescricao() : notaAjustadaCorec
                            .getDescricaoValor();
            notasGeraisFinal.setNotaQltAjustadaCorec(notaAjustadaCorecString);
        } else {
            AjusteCorec corecAnterior = notaMatrizMediator.notaCicloAnterior(ciclo);
            if (SisapsUtil.isNaoNuloOuVazio(corecAnterior)
                    && SisapsUtil.isNaoNuloOuVazio(corecAnterior.getNotaQualitativa())) {
                ParametroNota notaAjustadaCorec = corecAnterior.getNotaQualitativa();
                String notaAjustadaCorecString =
                        metodologia.getIsMetodologiaNova() ? notaAjustadaCorec.getDescricao() : notaAjustadaCorec
                                .getDescricaoValor();
                notasGeraisFinal.setNotaQltAjustadaCorec(notaAjustadaCorecString);
            }
        }

        // Nota final
        if (notaFinal != null && notaFinal.size() > 0 && SisapsUtil.isNaoNuloOuVazio(notaFinal.get(0))) {
            notasGeraisFinal.setNotaQltFinalVig(notaFinal.get(0));
            // Se não possuir nota supervisor ou corec, a final fica sendo a refinada
        } else if (SisapsUtil.isNaoNuloOuVazio(notasGeraisFinal.getNotaQltRefinada())) {
            notasGeraisFinal.setNotaQltFinalVig(notasGeraisFinal.getNotaQltRefinada());
            notasGeraisFinal.setNotaQltAjustadaCorec(null);
            notasGeraisFinal.setNotaQltAjustadaSuperv(null);
        }

        // Pesos governança x matriz
        if (SisapsUtil.isNaoNuloOuVazio(matriz) && SisapsUtil.isNaoNuloOuVazio(matriz.getNumeroFatorRelevanciaAE())) {
            notasGeraisFinal.setPesoGovernanca(matriz.getNumeroFatorRelevanciaAE());
            notasGeraisFinal.setPesoMatriz(BigDecimal.ONE.subtract(matriz.getNumeroFatorRelevanciaAE()));
        }
    }

    private void setDadosESNotasGerais(Ciclo ciclo, PerfilRisco perfilRisco, NotasGeraisFinal notasGeraisFinal) {
        GrauPreocupacaoES grauPreocupacaoSupervisor =
                grauPreocupacaoESMediator.buscarPorPerfilRisco(perfilRisco.getPk());
        GrauPreocupacaoES grauPreocupacaoVigente =
                grauPreocupacaoESMediator.getGrauPreocupacaoESPorPerfil(perfilRisco, false,
                        PerfilAcessoEnum.SUPERVISOR);
        PerspectivaES perspectivaSupervisor = perspectivaESMediator.buscarPorPerfilRisco(perfilRisco.getPk());
        PerspectivaES perspectivaVigente =
                perspectivaESMediator.getUltimaPerspectiva(perfilRisco, false, PerfilAcessoEnum.SUPERVISOR);
        AjusteCorec ajusteCorec = ajusteCorecMediator.buscarPorCiclo(ciclo.getPk());

        // Dados grau de preocupação ES
        setDadosGrauPreocupacao(notasGeraisFinal, grauPreocupacaoSupervisor, grauPreocupacaoVigente, ajusteCorec,
                perfilRisco);

        // Dados perspectiva ES
        setDadosPerspectiva(notasGeraisFinal, perspectivaSupervisor, perspectivaVigente, ajusteCorec);
    }

    private void setDadosPerspectiva(NotasGeraisFinal notasGeraisFinal, PerspectivaES perspectivaSupervisor,
            PerspectivaES perspectivaVigente, AjusteCorec ajusteCorec) {
        if (SisapsUtil.isNaoNuloOuVazio(perspectivaVigente)
                && SisapsUtil.isNaoNuloOuVazio(perspectivaVigente.getParametroPerspectiva())) {
            notasGeraisFinal.setPerspectivaFinalVig(perspectivaVigente.getParametroPerspectiva().getNome());
        }
        if (SisapsUtil.isNaoNuloOuVazio(perspectivaSupervisor)) {
            if (SisapsUtil.isNaoNuloOuVazio(perspectivaSupervisor.getParametroPerspectiva())) {
                notasGeraisFinal.setPerspectivaSuperv(perspectivaSupervisor.getParametroPerspectiva().getNome());
            } else if (SisapsUtil.isNaoNuloOuVazio(perspectivaSupervisor.getPerspectivaESAnterior())) {
                notasGeraisFinal.setPerspectivaSuperv(perspectivaSupervisor.getPerspectivaESAnterior()
                        .getParametroPerspectiva().getNome());
            }
        }
        if (SisapsUtil.isNaoNuloOuVazio(ajusteCorec) && SisapsUtil.isNaoNuloOuVazio(ajusteCorec.getPerspectiva())) {
            notasGeraisFinal.setPerspectivaCorec(ajusteCorec.getPerspectiva().getNome());
        } else if (perspectivaVigente != null && perspectivaVigente.getParametroPerspectiva() != null
                && perspectivaVigente.getPk() == null) {
            notasGeraisFinal.setPerspectivaCorec(perspectivaVigente.getParametroPerspectiva().getNome());
        }
    }

    private void setDadosGrauPreocupacao(NotasGeraisFinal notasGeraisFinal,
            GrauPreocupacaoES grauPreocupacaoSupervisor, GrauPreocupacaoES grauPreocupacaoVigente,
            AjusteCorec ajusteCorec, PerfilRisco perfilRisco) {
        if (SisapsUtil.isNaoNuloOuVazio(grauPreocupacaoVigente)) {
            if (SisapsUtil.isNaoNuloOuVazio(grauPreocupacaoVigente.getParametroGrauPreocupacao())) {
                notasGeraisFinal.setGrauPreocFinalVig(grauPreocupacaoVigente.getParametroGrauPreocupacao()
                        .getDescricao());
            } else if (SisapsUtil.isNaoNuloOuVazio(grauPreocupacaoVigente.getParametroNota())) {
                notasGeraisFinal.setGrauPreocFinalVig(grauPreocupacaoVigente.getParametroNota().getDescricao());
            }
        }
        if (SisapsUtil.isNaoNuloOuVazio(grauPreocupacaoSupervisor)) {
            if (SisapsUtil.isNaoNuloOuVazio(grauPreocupacaoSupervisor.getParametroGrauPreocupacao())) {
                notasGeraisFinal.setGrauPreocSuperv(grauPreocupacaoSupervisor.getParametroGrauPreocupacao()
                        .getDescricao());
            } else if (SisapsUtil.isNaoNuloOuVazio(grauPreocupacaoSupervisor.getParametroNota())) {
                notasGeraisFinal.setGrauPreocSuperv(grauPreocupacaoSupervisor.getParametroNota().getDescricao());
            } 
        }
        if (SisapsUtil.isNaoNuloOuVazio(ajusteCorec) && SisapsUtil.isNaoNuloOuVazio(ajusteCorec.getGrauPreocupacao())) {
            notasGeraisFinal.setGrauPreocCorec(ajusteCorec.getGrauPreocupacao().getDescricao());
        } else if (SisapsUtil.isNaoNuloOuVazio(ajusteCorec) && SisapsUtil.isNaoNuloOuVazio(ajusteCorec.getNotaFinal())) {
            notasGeraisFinal.setGrauPreocCorec(ajusteCorec.getNotaFinal().getDescricao());
        } else if (grauPreocupacaoVigente != null && grauPreocupacaoVigente.getPk() == null
                && SisapsUtil.isNaoNuloOuVazio(grauPreocupacaoVigente.getParametroGrauPreocupacao())) {
            notasGeraisFinal.setGrauPreocCorec(grauPreocupacaoVigente.getParametroGrauPreocupacao().getDescricao());
        } else if (grauPreocupacaoVigente != null && grauPreocupacaoVigente.getPk() == null
                && SisapsUtil.isNaoNuloOuVazio(grauPreocupacaoVigente.getParametroNota())) {
            notasGeraisFinal.setGrauPreocCorec(grauPreocupacaoVigente.getParametroNota().getDescricao());
        }
        Metodologia metodologia = metodologiaMediator.loadPK(perfilRisco.getCiclo().getMetodologia().getPk());
        if (metodologia.getIsMetodologiaNova()) {
            String notaAnef = "";
            String notaMatriz = "";
            String notaCaculada = "";
            notaAnef = grauPreocupacaoESMediator.getNotaAEF(perfilRisco, PerfilAcessoEnum.SUPERVISOR);
            notaMatriz = grauPreocupacaoESMediator.getNotaMatrizFinal(perfilRisco, PerfilAcessoEnum.SUPERVISOR);
            notaCaculada =
                    grauPreocupacaoESMediator.getNotaFinalCalculada(grauPreocupacaoVigente, notaAnef, notaMatriz,
                            perfilRisco.getCiclo());
            BigDecimal percentualAnef = null;
            if (possuiNotaVigente(notaCaculada)) {
                notasGeraisFinal.setGrauPreocFinalCalc(new BigDecimal(notaCaculada.replace(",", ".")));
                notasGeraisFinal.setGrauPreocFinalRef(grauPreocupacaoESMediator.getNotaFinalRefinada(perfilRisco
                        .getCiclo().getMetodologia(), notaCaculada));
            }
            if (SisapsUtil.isNuloOuVazio(notasGeraisFinal.getGrauPreocFinalVig())) {
                String descricaoNota =
                        grauPreocupacaoESMediator.getNotaFinal(perfilRisco, PerfilAcessoEnum.SUPERVISOR, perfilRisco
                                .getCiclo().getMetodologia());
                if (descricaoNota.contains(COREC)) {
                    descricaoNota = descricaoNota.replace(COREC, "");
                }
                if (possuiNotaVigente(descricaoNota)) {
                    notasGeraisFinal.setGrauPreocFinalVig(descricaoNota);
                }
            }
            if (grauPreocupacaoVigente != null && grauPreocupacaoVigente.getNumeroFatorRelevanciaAnef() != null) {
                percentualAnef = grauPreocupacaoVigente.getNumeroFatorRelevanciaAnef();
            } else if (grauPreocupacaoESMediator.cicloMaisTresAnos(perfilRisco.getCiclo())) {
                percentualAnef = new BigDecimal(0.5).setScale(2, RoundingMode.HALF_UP);
            } else {
                percentualAnef = new BigDecimal(0.3).setScale(2, RoundingMode.HALF_UP);
            }
            notasGeraisFinal.setPesoAnef(percentualAnef);
            notasGeraisFinal.setPesoRiscoControle(BigDecimal.ONE.subtract(percentualAnef));
        }
    }

    private void setDadosCicloNotasGerais(Ciclo ciclo, PerfilRisco perfilRisco, NotasGeraisFinal notasGeraisFinal) {
        notasGeraisFinal.setCnpj(ciclo.getEntidadeSupervisionavel().getConglomeradoOuCnpj());
        notasGeraisFinal.setDataInicioCiclo(ciclo.getDataInicio());
        notasGeraisFinal.setDataPrevisaoCorec(ciclo.getDataPrevisaoCorec());
        notasGeraisFinal.setDataVersao(perfilRisco.getDataCriacao());
        notasGeraisFinal.setEstadoCiclo(ciclo.getEstadoCiclo().getEstado().getDescricao());
        notasGeraisFinal.setMetodologia(ciclo.getMetodologia().getTitulo());
    }

    private void setNotasGrupoRiscoControle(PerfilRisco perfilRisco, Matriz matriz,
            List<VersaoPerfilRisco> versoesPerfilRiscoCelulas, List<CelulaRiscoControle> listaCelulaGrupo,
            ParametroGrupoRiscoControle grupo, NotasRiscoControleQualitativa notaRiscoQualitativa) {
        String valor;

        // Nota de risco.
        valor =
                linhaMatrizMediator.obterNota(matrizCicloMediator.notaResidualParametroGrupo(matriz, grupo,
                        versoesPerfilRiscoCelulas, true, TipoGrupoEnum.RISCO, listaCelulaGrupo,
                        PerfilAcessoEnum.SUPERVISOR, perfilRisco));
        if (possuiNotaVigente(valor)) {
            notaRiscoQualitativa.setNotaResidualRiscoCalc(convertStringToBigDecimal(valor));
        }

        // Nota de controle.
        valor =
                linhaMatrizMediator.obterNota(
                        matrizCicloMediator.notaResidualParametroGrupo(matriz, grupo,
                        versoesPerfilRiscoCelulas, true, TipoGrupoEnum.CONTROLE, listaCelulaGrupo,
                        PerfilAcessoEnum.SUPERVISOR, perfilRisco));
        if (possuiNotaVigente(valor)) {
            notaRiscoQualitativa.setNotaResidualControleCalc(convertStringToBigDecimal(valor));
        }

        // Nota residual.
        valor =
                matrizCicloMediator.mediaResidualParametroGrupo(matriz, grupo, versoesPerfilRiscoCelulas, true,
                        listaCelulaGrupo, PerfilAcessoEnum.SUPERVISOR, perfilRisco);
        if (possuiNotaVigente(valor)) {
            notaRiscoQualitativa.setNotaResidualFinalVig(convertStringToBigDecimal(valor));
            notaRiscoQualitativa.setNotaResidualCalc(convertStringToBigDecimal(valor));
            // Conceito residual.
            ParametroNota notaRefinada =
                    parametroNotaMediator.buscarPorMetodologiaENota(perfilRisco.getCiclo().getMetodologia(),
                            new BigDecimal(valor.replace(',', '.')), true);
            if (possuiNotaVigente(notaRefinada)) {
                notaRiscoQualitativa.setConceitoGeralFinalVig(notaRefinada.getDescricaoValor());
            }
        }
    }

    private void setNotasArc(AvaliacaoRiscoControle arcVigente, NotasArcsQualitativa notasArcsQualitativa,
            PerfilRisco perfilRisco, Integer pkArcAtual) {
        AvaliacaoRiscoControle arcAtual = avaliacaoRiscoControleMediator.loadPK(pkArcAtual);
        String notaVigente =
                avaliacaoRiscoControleMediator.notaArc(arcAtual, perfilRisco.getCiclo(),
                        PerfilAcessoEnum.SUPERVISOR, perfilRisco);
        String notaAjustada = getNotaAjustadaArc(arcVigente);
        if (possuiNotaVigente(notaVigente)) {
            notasArcsQualitativa.setNotaArcFinalVigente(convertStringToBigDecimal(notaVigente));
        }
        if (avaliacaoRiscoControleMediator.isMetodologiaCalculoMedia(arcVigente)
                && possuiNotaVigente(arcVigente.getMediaCalculadaFinal())) {
            notasArcsQualitativa.setNotaArcCalculada(convertStringToBigDecimal(arcVigente.getMediaCalculadaFinal()));
        } else if (!avaliacaoRiscoControleMediator.isMetodologiaCalculoMedia(arcVigente)
                && possuiNotaVigente(arcVigente.getNotaCalculadaFinal())) {
            notasArcsQualitativa.setNotaArcCalculada(convertStringToBigDecimal(arcVigente.getNotaCalculadaFinal()));
        }
        if (possuiNotaVigente(arcVigente.getNotaCorecDescricao())) {
            notasArcsQualitativa.setNotaArcAjustadaCorec(convertStringToBigDecimal(arcVigente.getNotaCorecDescricao()));
        }
        if (possuiNotaVigente(notaAjustada)) {
            notasArcsQualitativa.setNotaArcAjustadaSuperv(convertStringToBigDecimal(notaAjustada));
        }
    }

    public String getNotaAjustadaArc(AvaliacaoRiscoControle arc) {
        AvaliacaoARC avaliacaoARC;
        AvaliacaoARC supervisor =
                avaliacaoARCMediator.buscarPorIdArcEtipo(arc.getPk(), PerfisNotificacaoEnum.SUPERVISOR);
        if ((supervisor == null || (supervisor != null && supervisor.getParametroNota() == null))
                && !arc.possuiNotaElementosSupervisor()) {
            avaliacaoARC = avaliacaoARCMediator.buscarPorIdArcEtipo(arc.getPk(), PerfisNotificacaoEnum.INSPETOR);
        } else {
            avaliacaoARC = supervisor;
        }
        return avaliacaoARC == null || avaliacaoARC.getParametroNota() == null ? "" : avaliacaoARC.getParametroNota()
                .getDescricaoValor();
    }

    private void setDadosAuditoriaArc(AvaliacaoRiscoControle arc, NotasArcsQualitativa notasArcsQualitativa) {
        if (SisapsUtil.isNaoNuloOuVazio(arc.getDataPreenchido())) {
            notasArcsQualitativa.setDataPreenchidoArc(arc.getDataPreenchido());
        }
        if (SisapsUtil.isNaoNuloOuVazio(arc.getOperadorPreenchido())) {
            notasArcsQualitativa.setOperadorPreenchidoArc(arc.getOperadorPreenchido());
        }
        if (SisapsUtil.isNaoNuloOuVazio(arc.getDataAnalise())) {
            notasArcsQualitativa.setDataAnaliseArc(arc.getDataAnalise());
        }
        if (SisapsUtil.isNaoNuloOuVazio(arc.getOperadorAnalise())) {
            notasArcsQualitativa.setOperadorAnaliseArc(arc.getOperadorAnalise());
        }
        if (SisapsUtil.isNaoNuloOuVazio(arc.getDataConclusao())) {
            notasArcsQualitativa.setDataConclusaoArc(arc.getDataConclusao());
        }
        if (SisapsUtil.isNaoNuloOuVazio(arc.getOperadorConclusao())) {
            notasArcsQualitativa.setOperadorConclusaoArc(arc.getOperadorConclusao());
        }
    }

    private void setDadosAuditoriaAnef(AnaliseQuantitativaAQT anef,
            NotasComponentesQuantitativa notasComponentesQuantitativa) {
        if (SisapsUtil.isNaoNuloOuVazio(anef.getDataPreenchido())) {
            notasComponentesQuantitativa.setDataPreenchido(anef.getDataPreenchido());
        }
        if (SisapsUtil.isNaoNuloOuVazio(anef.getOperadorPreenchido())) {
            notasComponentesQuantitativa.setOperadorPreenchido(anef.getOperadorPreenchido());
        }
        if (SisapsUtil.isNaoNuloOuVazio(anef.getDataAnalise())) {
            notasComponentesQuantitativa.setDataAnalise(anef.getDataAnalise());
        }
        if (SisapsUtil.isNaoNuloOuVazio(anef.getOperadorAnalise())) {
            notasComponentesQuantitativa.setOperadorAnalise(anef.getOperadorAnalise());
        }
        if (SisapsUtil.isNaoNuloOuVazio(anef.getDataConclusao())) {
            notasComponentesQuantitativa.setDataConclusao(anef.getDataConclusao());
        }
        if (SisapsUtil.isNaoNuloOuVazio(anef.getOperadorConclusao())) {
            notasComponentesQuantitativa.setOperadorConclusao(anef.getOperadorConclusao());
        }
    }

    private BigDecimal convertStringToBigDecimal(String valor) {
        if (StringUtils.isNotBlank(valor)) {
            return new BigDecimal(valor.replace(',', '.'));
        }
        return null;
    }

    private boolean possuiNotaVigente(String nota) {
        return SisapsUtil.isNaoNuloOuVazio(nota) && !Constantes.ASTERISCO_A.equals(nota)
                && !Constantes.N_A.equals(nota);
    }

    private boolean possuiNotaVigenteElemento(String nota) {
        return SisapsUtil.isNaoNuloOuVazio(nota) && !Constantes.ASTERISCO_A.equals(nota);
    }

    private boolean possuiNotaVigenteElemento(ParametroNota nota) {
        return SisapsUtil.isNaoNuloOuVazio(nota) && SisapsUtil.isNaoNuloOuVazio(nota.getValor());
    }

    private boolean possuiNotaVigente(ParametroNota nota) {
        return SisapsUtil.isNaoNuloOuVazio(nota) && SisapsUtil.isNaoNuloOuVazio(nota.getValor())
                && nota.getValor().compareTo(BigDecimal.ZERO) == 1;
    }

    private boolean possuiNotaVigente(ParametroNotaAQT nota) {
        return SisapsUtil.isNaoNuloOuVazio(nota) && SisapsUtil.isNaoNuloOuVazio(nota.getValor())
                && nota.getValor().compareTo(BigDecimal.ZERO) == 1;
    }

    private boolean possuiNotaVigenteElemento(ParametroNotaAQT nota) {
        return SisapsUtil.isNaoNuloOuVazio(nota) && SisapsUtil.isNaoNuloOuVazio(nota.getValor());
    }

    private BigDecimal getNotaElemento(String notaSupervisor) {
        BigDecimal notaNA = new BigDecimal(-1);
        return Constantes.N_A.equals(notaSupervisor) ? notaNA : convertStringToBigDecimal(notaSupervisor);
    }

    private void salvarPerfilRisco(PerfilRisco perfilRisco, List<VersaoPerfilRisco> versoesCriadas,
            NotasGeraisFinal notasGeraisFinal) {
        for (VersaoPerfilRisco versao : versoesCriadas) {
            PerfilRiscoMigracao perfilMigracao = new PerfilRiscoMigracao();
            perfilMigracao.setNotasGeraisFinal(notasGeraisFinal);
            perfilMigracao.setDataCriacao(perfilRisco.getDataCriacao());
            perfilMigracao.setTipo(versao.getTipoObjetoVersionador().getDescricao());
            salvarPerfilRiscoMigracao(perfilMigracao);
        }
    }

    @Transactional
    public void excluirNotas() {
        notasElementosQuantitativaDAO.excluirNotas();
        notasComponentesQuantitativaDAO.excluirNotas();
        notasElementosQualitativaDAO.excluirNotas();
        notasArcsQualitativaDAO.excluirNotas();
        notaRiscoQualitativaDAO.excluirNotas();
        perfilRiscoMigracaoDAO.excluirNotas();
        notasGeraisFinalDAO.excluirNotas();
    }

    @Transactional
    private void salvarNotaGeral(NotasGeraisFinal notasGeraisFinal) {
        notasGeraisFinalDAO.save(notasGeraisFinal);
        LOG.info("NOTA GERAL: " + notasGeraisFinal.getPk() + CRIADA);
    }

    @Transactional
    private void salvarNotaArcQualitativa(NotasArcsQualitativa notasArcsQualitativa) {
        notasArcsQualitativaDAO.save(notasArcsQualitativa);
        LOG.info("NOTA ARC QUALITATIVA: " + notasArcsQualitativa.getPk() + CRIADA);
    }

    @Transactional
    private void salvarNotaRiscoQualitativa(NotasRiscoControleQualitativa notaRiscoQualitativa) {
        notaRiscoQualitativaDAO.save(notaRiscoQualitativa);
        LOG.info("NOTA RISCO CONTROLE QUALITATIVA: " + notaRiscoQualitativa.getPk() + CRIADA);
    }

    @Transactional
    private void salvarNotaElementoQualitativa(NotasElementosQualitativa notasElementosQualitativa) {
        notasElementosQualitativaDAO.save(notasElementosQualitativa);
        LOG.info("NOTA ELEMENTO QUALITATIVA: " + notasElementosQualitativa.getPk() + CRIADA);
    }

    @Transactional
    private void salvarNotaComponenteQuantitativa(NotasComponentesQuantitativa notasComponentesQuantitativa) {
        notasComponentesQuantitativaDAO.save(notasComponentesQuantitativa);
        LOG.info("NOTA COMPONENTE QUANTITATIVA: " + notasComponentesQuantitativa.getPk() + CRIADA);
    }

    @Transactional
    private void salvarNotaElementoQuantitativa(NotasElementosQuantitativa notasElementosQuantitativa) {
        notasElementosQuantitativaDAO.save(notasElementosQuantitativa);
        LOG.info("NOTA ELEMENTO QUANTITATIVA: " + notasElementosQuantitativa.getPk() + CRIADA);
    }

    @Transactional
    private void salvarPerfilRiscoMigracao(PerfilRiscoMigracao perfilMigracao) {
        perfilRiscoMigracaoDAO.save(perfilMigracao);
        LOG.info("PERFIL RISCO MIGRAÇÃO: " + perfilMigracao.getPk() + " CRIADO.");
    }

}