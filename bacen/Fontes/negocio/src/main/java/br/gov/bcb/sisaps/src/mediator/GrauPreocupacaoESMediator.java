package br.gov.bcb.sisaps.src.mediator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.joda.time.DateTime;
import org.joda.time.Years;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.seguranca.UsuarioCorrente;
import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.seguranca.UsuarioAplicacao;
import br.gov.bcb.sisaps.src.dao.GrauPreocupacaoESDAO;
import br.gov.bcb.sisaps.src.dominio.AjusteCorec;
import br.gov.bcb.sisaps.src.dominio.Atividade;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.GrauPreocupacaoES;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.Metodologia;
import br.gov.bcb.sisaps.src.dominio.ParametroGrauPreocupacao;
import br.gov.bcb.sisaps.src.dominio.ParametroNota;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.VersaoPerfilRisco;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.NotaAjustadaAEF;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroNotaAQT;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoObjetoVersionadorEnum;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.NotaAjustadaAEFMediator;
import br.gov.bcb.sisaps.src.validacao.RegraAlterarPercentualNotaFinalValidacao;
import br.gov.bcb.sisaps.src.vo.ArcNotasVO;
import br.gov.bcb.sisaps.src.vo.CelulaRiscoControleVO;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import br.gov.bcb.sisaps.util.enumeracoes.TipoSubEventoPerfilRiscoSRC;
import br.gov.bcb.sisaps.util.geral.DataUtil;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;

@Service
@Transactional(readOnly = true)
public class GrauPreocupacaoESMediator {

    @Autowired
    private GrauPreocupacaoESDAO grauPreocupacaoESDAO;

    @Autowired
    private PerfilRiscoMediator perfilRiscoMediator;

    @Autowired
    private EventoConsolidadoMediator eventoConsolidadoMediator;

    public static GrauPreocupacaoESMediator get() {
        return SpringUtils.get().getBean(GrauPreocupacaoESMediator.class);
    }

    @Transactional(readOnly = true)
    public GrauPreocupacaoES getUltimoGrauPreocupacaoES(Ciclo ciclo) {
        GrauPreocupacaoES ultimoGrauPreocupacaoES = grauPreocupacaoESDAO.getUltimoGrauPreocupacaoES(ciclo);
        if (ultimoGrauPreocupacaoES != null) {
            Hibernate.initialize(ultimoGrauPreocupacaoES.getParametroGrauPreocupacao());
            Hibernate.initialize(ultimoGrauPreocupacaoES.getParametroNota());
            if (ultimoGrauPreocupacaoES.getGrauPreocupacaoESAnterior() != null) {
                Hibernate.initialize(
                        ultimoGrauPreocupacaoES.getGrauPreocupacaoESAnterior().getParametroGrauPreocupacao());
                Hibernate.initialize(ultimoGrauPreocupacaoES.getGrauPreocupacaoESAnterior().getParametroNota());
            }
        }
        return ultimoGrauPreocupacaoES;
    }

    @Transactional(readOnly = true)
    public GrauPreocupacaoES getGrauPreocupacaoESPorPerfil(PerfilRisco perfil, boolean isGestaoDetalhesES,
            PerfilAcessoEnum perfilAcessoEnum) {
        GrauPreocupacaoES ultimoGrauPreocupacaoES = new GrauPreocupacaoES();
        if (isGestaoDetalhesES) {
            ultimoGrauPreocupacaoES = notaCorecAnteriorOuNotaPerfilRisco(perfil);
        } else {
            ultimoGrauPreocupacaoES = notaCorecAnteriorAtualOuNotaPerfilRisco(perfil, perfilAcessoEnum);
        }
        if (ultimoGrauPreocupacaoES != null) {
            Hibernate.initialize(ultimoGrauPreocupacaoES.getParametroGrauPreocupacao());
            Hibernate.initialize(ultimoGrauPreocupacaoES.getParametroNota());
            if (ultimoGrauPreocupacaoES.getGrauPreocupacaoESAnterior() != null) {
                Hibernate.initialize(
                        ultimoGrauPreocupacaoES.getGrauPreocupacaoESAnterior().getParametroGrauPreocupacao());
                Hibernate.initialize(ultimoGrauPreocupacaoES.getGrauPreocupacaoESAnterior().getParametroNota());
            }
            grauPreocupacaoESDAO.evict(ultimoGrauPreocupacaoES);
        }
        return ultimoGrauPreocupacaoES;
    }

    public String justificativaGrauPreocupacao(PerfilRisco perfil, PerfilAcessoEnum perfilAcessoEnum) {
        GrauPreocupacaoES grauPreocupacaoES = getGrauPreocupacaoESPorPerfil(perfil, false, perfilAcessoEnum);
        return grauPreocupacaoES == null ? "" : grauPreocupacaoES.getJustificativa();
    }

    private GrauPreocupacaoES notaCorecAnteriorAtualOuNotaPerfilRisco(PerfilRisco perfil,
            PerfilAcessoEnum perfilAcessoEnum) {
        if (NotaMatrizMediator.get().cicloAndamentoCorecDiferenteSupervisor(perfil.getCiclo().getMatriz(),
                perfilAcessoEnum)) {
            return notaCorecAnteriorOuNotaPerfilRisco(perfil);
        } else {
            AjusteCorec ajusteCorec = AjusteCorecMediator.get().buscarPorCiclo(perfil.getCiclo().getPk());

            PerfilRisco perfilRiscoAtual = PerfilRiscoMediator.get().obterPerfilRiscoAtual(perfil.getCiclo().getPk());
            if (!perfilRiscoAtual.getPk().equals(perfil.getPk()) || ajusteCorec == null
                    || (ajusteCorec.getGrauPreocupacao() == null && ajusteCorec.getNotaFinal() == null)) {
                return grauPreocupacaoESDAO.buscarPorPerfilRisco(perfil.getPk());
            } else {
                return montarGrauPreocupacaoCorec(ajusteCorec);
            }
        }
    }

    public GrauPreocupacaoES notaCorecAnteriorOuNotaPerfilRisco(PerfilRisco perfil) {
        GrauPreocupacaoES grauPreocupacao = grauPreocupacaoESDAO.buscarPorPerfilRisco(perfil.getPk());
        if (exibirCorecAnteriorNotaFinalES(perfil)) {
            Ciclo cicloAnterior = CicloMediator.get()
                    .consultarUltimoCicloPosCorecEncerradoES(perfil.getCiclo().getEntidadeSupervisionavel().getPk());
            AjusteCorec ajusteCorec = AjusteCorecMediator.get().buscarPorCiclo(cicloAnterior.getPk());
            return montarGrauPreocupacaoCorec(ajusteCorec);
        } else {
            return grauPreocupacao;
        }
    }

    private GrauPreocupacaoES montarGrauPreocupacaoCorec(AjusteCorec ajusteCorec) {
        GrauPreocupacaoES ultimoGrauPreocupacaoES = new GrauPreocupacaoES();
        ultimoGrauPreocupacaoES.setParametroGrauPreocupacao(ajusteCorec.getGrauPreocupacao());
        ultimoGrauPreocupacaoES.setParametroNota(ajusteCorec.getNotaFinal());
        ultimoGrauPreocupacaoES.setDataEncaminhamento(ajusteCorec.getUltimaAtualizacao());
        ultimoGrauPreocupacaoES.setOperadorEncaminhamento(ajusteCorec.getOperadorAtualizacao());
        grauPreocupacaoESDAO.evict(ultimoGrauPreocupacaoES);
        return ultimoGrauPreocupacaoES;
    }

    @Transactional(readOnly = true)
    public GrauPreocupacaoES getGrauPreocupacaoPendencia(Ciclo ciclo) {
        GrauPreocupacaoES grauPreocupacao = grauPreocupacaoESDAO.buscarPorPendencia(ciclo);
        inicializarPametroGrau(grauPreocupacao);
        return grauPreocupacao;

    }

    private void inicializarPametroGrau(GrauPreocupacaoES grauPreocupacao) {
        if (grauPreocupacao != null) {
            Hibernate.initialize(grauPreocupacao.getParametroGrauPreocupacao());
            Hibernate.initialize(grauPreocupacao.getParametroNota());
        }
    }

    @Transactional
    public String confirmarGrauPreocupacaoGerente(GrauPreocupacaoES grauPreocupacaoES) {
        grauPreocupacaoES.setPendente(null);
        VersaoPerfilRisco versaoPerfilRisco = perfilRiscoMediator.gerarNovaVersaoPerfilRisco(
                grauPreocupacaoES.getCiclo(), grauPreocupacaoES.getGrauPreocupacaoESAnterior(),
                TipoObjetoVersionadorEnum.GRAU_PREOCUPACAO_ES);
        grauPreocupacaoES.setVersaoPerfilRisco(versaoPerfilRisco);
        eventoConsolidadoMediator.incluirEventoPerfilDeRisco(grauPreocupacaoES.getCiclo(),
                TipoSubEventoPerfilRiscoSRC.GRAU_PREOCUPACAO);
        grauPreocupacaoES.setAlterarDataUltimaAtualizacao(false);
        
        grauPreocupacaoES.setUltimaAtualizacao(DataUtil.getDateTimeAtual());

        grauPreocupacaoESDAO.saveOrUpdate(grauPreocupacaoES);
        return "Atualização da Nota final da ES no perfil de risco realizada com sucesso.";
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public String salvarNovoGrauPreocupacao(GrauPreocupacaoES grauPreocupacaoES) {
        grauPreocupacaoES.setPendente(null);
        if (grauPreocupacaoES.getPk() == null) {
            grauPreocupacaoESDAO.save(grauPreocupacaoES);
        } else {
            grauPreocupacaoESDAO.merge(grauPreocupacaoES);
        }
        return "Nota final da ES salva com sucesso.";
    }

    @Transactional
    public String confirmarNovoGrauPreocupacao(GrauPreocupacaoES grauPreocupacaoES, String notaFinalCalculada) {
        validarConfirmarNovoGrauPreocupacao(grauPreocupacaoES, notaFinalCalculada);
        UsuarioAplicacao usuarioAplicacao = (UsuarioAplicacao) UsuarioCorrente.get();
        grauPreocupacaoES.setPendente(SimNaoEnum.SIM);
        grauPreocupacaoES.setDataEncaminhamento(DataUtil.getDateTimeAtual());
        grauPreocupacaoES.setOperadorEncaminhamento(usuarioAplicacao.getLogin());
        grauPreocupacaoESDAO.saveOrUpdate(grauPreocupacaoES);
        return "Nota final da ES encaminhada para aprovação do gerente com sucesso.";
    }

    private void validarConfirmarNovoGrauPreocupacao(GrauPreocupacaoES grauPreocupacaoES, String notaFinalCalculada) {
        ArrayList<ErrorMessage> erros = new ArrayList<ErrorMessage>();
        if (!possuiNotaVigente(notaFinalCalculada)) {
            SisapsUtil.adicionarErro(erros, new ErrorMessage("Ajuste não pode ser feito, pois não há nota calculada."));
        }
        if (grauPreocupacaoES.getParametroNota() != null) {
            SisapsUtil.validarObrigatoriedade(grauPreocupacaoES.getJustificativa(), "Justificativa da nota final da ES",
                    erros);
        }
        SisapsUtil.lancarNegocioException(erros);
    }

    @Transactional(readOnly = true)
    public GrauPreocupacaoES buscarPorPerfilRisco(Integer pkPerfilRisco) {
        GrauPreocupacaoES grauPreocupacao = grauPreocupacaoESDAO.buscarPorPerfilRisco(pkPerfilRisco);
        if (grauPreocupacao != null) {
            grauPreocupacao.setAlterarDataUltimaAtualizacao(false);
        }
        inicializarPametroGrau(grauPreocupacao);
        return grauPreocupacao;
    }

    @Transactional(readOnly = true)
    public GrauPreocupacaoES buscarGrauPreocupacaoESRascunho(Integer pkCiclo) {
        GrauPreocupacaoES grauPreocupacao = grauPreocupacaoESDAO.buscarGrauPreocupacaoESRascunho(pkCiclo);
        inicializarPametroGrau(grauPreocupacao);
        return grauPreocupacao;
    }

    @Transactional(readOnly = true)
    public List<Integer> quantidadeGrausCicloPorPerfilRisco(PerfilRisco perfilRisco) {
        return grauPreocupacaoESDAO.quantidadeGrausCicloPorPerfilRisco(perfilRisco);
    }

    @Transactional
    public void criarGrauPreocupacaoNovoCiclo(PerfilRisco perfilRiscoCicloAtual, Ciclo novoCiclo) {
        GrauPreocupacaoES grauPreocupacaoCicloAtual = buscarPorPerfilRisco(perfilRiscoCicloAtual.getPk());
        if (grauPreocupacaoCicloAtual != null) {
            grauPreocupacaoESDAO.evict(grauPreocupacaoCicloAtual);

            GrauPreocupacaoES novoGrauPreocupacao = new GrauPreocupacaoES();
            novoGrauPreocupacao.setCiclo(novoCiclo);
            novoGrauPreocupacao.setGrauPreocupacaoESAnterior(grauPreocupacaoCicloAtual);
            novoGrauPreocupacao.setParametroNota(grauPreocupacaoCicloAtual.getParametroNota());
            novoGrauPreocupacao.setJustificativa(grauPreocupacaoCicloAtual.getJustificativa());
            novoGrauPreocupacao.setNumeroFatorRelevanciaAnef(grauPreocupacaoCicloAtual.getNumeroFatorRelevanciaAnef());
            novoGrauPreocupacao.setOperadorAtualizacao(grauPreocupacaoCicloAtual.getOperadorAtualizacao());
            novoGrauPreocupacao.setUltimaAtualizacao(grauPreocupacaoCicloAtual.getUltimaAtualizacao());
            novoGrauPreocupacao.setOperadorEncaminhamento(grauPreocupacaoCicloAtual.getOperadorEncaminhamento());
            novoGrauPreocupacao.setDataEncaminhamento(grauPreocupacaoCicloAtual.getDataEncaminhamento());
            novoGrauPreocupacao.setAlterarDataUltimaAtualizacao(false);
            perfilRiscoMediator.incluirVersaoPerfilRiscoAtual(novoCiclo, novoGrauPreocupacao,
                    TipoObjetoVersionadorEnum.GRAU_PREOCUPACAO_ES);
            grauPreocupacaoESDAO.save(novoGrauPreocupacao);
            grauPreocupacaoESDAO.evict(novoGrauPreocupacao);
        }
    }

    @Transactional(readOnly = true)
    public boolean isGrauPreocupacaoCicloAnterior(Ciclo ciclo) {
        Ciclo cicloAnterior =
                CicloMediator.get().consultarUltimoCicloPosCorecEncerradoES(ciclo.getEntidadeSupervisionavel().getPk());
        if (cicloAnterior != null) {
            AjusteCorec ajusteCorec = AjusteCorecMediator.get().buscarPorCiclo(cicloAnterior.getPk());
            return ajusteCorec != null && ajusteCorec.getGrauPreocupacao() != null;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public boolean exibirCorecAnteriorNotaFinalES(PerfilRisco perfilRisco) {
        Ciclo ciclo = perfilRisco.getCiclo();
        AjusteCorec notaCorecCicloAnterior = null;
        Ciclo cicloAnterior =
                CicloMediator.get().consultarUltimoCicloPosCorecEncerradoES(ciclo.getEntidadeSupervisionavel().getPk());
        notaCorecCicloAnterior = NotaMatrizMediator.get().notaCicloAnterior(ciclo);

        if ((cicloAnterior != null && notaCorecCicloAnterior != null)
                && ((cicloAnterior.getMetodologia().getIsMetodologiaNova()
                        && notaCorecCicloAnterior.getNotaFinal() != null)
                        || (!cicloAnterior.getMetodologia().getIsMetodologiaNova()
                                && notaCorecCicloAnterior.getGrauPreocupacao() != null))) {
            List<Integer> qtdGraus = quantidadeGrausCicloPorPerfilRisco(perfilRisco);
            GrauPreocupacaoES grauCicloAnterior = getUltimoGrauPreocupacaoES(cicloAnterior);
            return (grauCicloAnterior != null && CollectionUtils.isNotEmpty(qtdGraus) && qtdGraus.size() == 1)
                    || (grauCicloAnterior == null && CollectionUtils.isEmpty(qtdGraus));
        }
        return false;
    }

    public void evict(GrauPreocupacaoES grau) {
        grauPreocupacaoESDAO.evict(grau);
    }

    /**
     * Método que verifica se o registro é um "Grau de preocupação" ou uma "Nota final". Caso o
     * parâmetro associado ao registro tiver data de atualização maior que a data limite na
     * Constantes.DATA_LIMITE_GRAU_PREOCUPACAO, o registro é considerado uma "Nota final", senão,
     * um "Grau de preocupação".
     * 
     * @return
     */
    public boolean isNotaFinal(GrauPreocupacaoES grauPreocupacaoES) {
        return grauPreocupacaoES == null || ParametroGrauPreocupacaoMediator.get()
                .isNotaFinal(grauPreocupacaoES.getParametroGrauPreocupacao(), grauPreocupacaoES.getParametroNota());
    }

    @Transactional
    public String alterarPercentualGrauPreocupacao(GrauPreocupacaoES grauPreocupacaoES, BigDecimal percentualAnef) {
        new RegraAlterarPercentualNotaFinalValidacao(percentualAnef).validar();
        grauPreocupacaoES.setNumeroFatorRelevanciaAnef(percentualAnef);
        grauPreocupacaoESDAO.saveOrUpdate(grauPreocupacaoES);
        return "";
    }

    @Transactional(readOnly = true)
    public GrauPreocupacaoES getGrauPreocupacaoPorPk(Integer pkGrau) {
        GrauPreocupacaoES grauPreocupacao = grauPreocupacaoESDAO.load(pkGrau);
        inicializarPametroGrau(grauPreocupacao);
        return grauPreocupacao;
    }

    @Transactional(readOnly = true)
    public String getNotaMatrizFinal(PerfilRisco perfilRisco, PerfilAcessoEnum perfil) {
        return getNotaMatrizFinal(perfilRisco, perfil, false);
    }

    @Transactional(readOnly = true)
    public String getNotaMatrizFinal(PerfilRisco perfilRisco, PerfilAcessoEnum perfil, boolean isAtaCorec) {
        String notaMatriz = "";
        List<Atividade> listaAtividade = new LinkedList<Atividade>();
        List<VersaoPerfilRisco> versoesPerfilRiscoCelulas;

        // Nota Matriz
        Ciclo cicloInicializado = CicloMediator.get().buscarCicloPorPK(perfilRisco.getCiclo().getPk());

        Matriz matriz = null;
        if (cicloInicializado != null && cicloInicializado.getMatriz() != null) {
            matriz = MatrizCicloMediator.get().buscar(cicloInicializado.getMatriz().getPk());
        }

        if (matriz != null) {
            List<String> mapNota = NotaMatrizMediator.get().notaAjustada(matriz, perfilRisco, perfil, isAtaCorec);
            ArcNotasVO arcExterno = AvaliacaoRiscoControleMediator.get().consultarNotasArcExterno(perfilRisco.getPk());
            versoesPerfilRiscoCelulas = VersaoPerfilRiscoMediator.get().buscarVersoesPerfilRisco(perfilRisco.getPk(),
                    TipoObjetoVersionadorEnum.CELULA_RISCO_CONTROLE);
            listaAtividade = AtividadeMediator.get().buscarAtividadesMatriz(matriz);
            LinhaMatrizMediator.get().consultar(matriz, false, listaAtividade);
            LinhaMatrizMediator.get().consultar(matriz, true, listaAtividade);
            List<CelulaRiscoControleVO> listaChoices = CelulaRiscoControleMediator.get()
                    .buscarParametroDaMatrizVO(listaAtividade, versoesPerfilRiscoCelulas);
            if (SisapsUtil.isNaoNuloOuVazio(arcExterno)) {
                notaMatriz = MatrizCicloMediator.get().notaCalculadaFinalVO(matriz, listaChoices,
                        versoesPerfilRiscoCelulas, true, PerfilAcessoEnum.CONSULTA_TUDO, perfilRisco, arcExterno,
                        isAtaCorec);
                notaMatriz = notaMatriz != null ? notaMatriz.replace(".", ",") : Constantes.ASTERISCO_A;
            } else {
                notaMatriz = MatrizCicloMediator.get().notaCalculadaVO(matriz, listaChoices, versoesPerfilRiscoCelulas,
                        true, PerfilAcessoEnum.CONSULTA_TUDO, perfilRisco, isAtaCorec);
                notaMatriz = notaMatriz != null ? notaMatriz.replace(".", ",") : Constantes.ASTERISCO_A;
            }

            if (!mapNota.isEmpty()) {
                String notaDescricao = mapNota.get(0);
                if (!StringUtils.isEmpty(notaDescricao)) {
                    ParametroNota parametroNota = ParametroNotaMediator.get()
                            .buscarPorDescricao(perfilRisco.getCiclo().getMetodologia(), notaDescricao);
                    notaMatriz = parametroNota.getLimiteSuperior().toString().replace(".", ",");
                }
            }
        }
        return notaMatriz;
    }

    @Transactional(readOnly = true)
    public String getNotaFinalCalculada(GrauPreocupacaoES grauPreocupacaoES, String notaCalculadaAnef,
            String notaCalculadaMatriz, Ciclo ciclo) {
        BigDecimal notaPonderada = null;
        BigDecimal notaAnefPonderada = null;
        BigDecimal notaMatrizPonderada = null;
        BigDecimal percentualAnef = null;
        BigDecimal percentualMatriz = null;
        if (grauPreocupacaoES != null && grauPreocupacaoES.getNumeroFatorRelevanciaAnef() != null) {
            percentualAnef = grauPreocupacaoES.getNumeroFatorRelevanciaAnef();
            percentualMatriz = BigDecimal.ONE.subtract(percentualAnef);
        } else if (cicloMaisTresAnos(ciclo)) {
            percentualAnef = new BigDecimal(0.5).setScale(2, RoundingMode.HALF_UP);
            percentualMatriz = BigDecimal.ONE.subtract(percentualAnef);
        } else {
            percentualAnef = new BigDecimal(0.3).setScale(2, RoundingMode.HALF_UP);
            percentualMatriz = BigDecimal.ONE.subtract(percentualAnef);
        }
        if (possuiNotaVigente(notaCalculadaAnef) && possuiNotaVigente(notaCalculadaMatriz)) {
            notaAnefPonderada = new BigDecimal(notaCalculadaAnef.replace(",", ".")).multiply(percentualAnef);
            notaMatrizPonderada = new BigDecimal(notaCalculadaMatriz.replace(",", ".")).multiply(percentualMatriz);
        }
        if (possuiNotaVigente(notaCalculadaAnef) && possuiNotaVigente(notaCalculadaMatriz) && notaAnefPonderada != null
                && notaMatrizPonderada != null) {
            notaPonderada = notaAnefPonderada.add(notaMatrizPonderada).setScale(2, RoundingMode.HALF_UP);
        } else if (possuiNotaVigente(notaCalculadaAnef)) {
            notaPonderada = new BigDecimal(notaCalculadaAnef.replace(",", "."));
        } else if (possuiNotaVigente(notaCalculadaMatriz)) {
            notaPonderada = new BigDecimal(notaCalculadaMatriz.replace(",", "."));
        }
        return notaPonderada == null ? Constantes.ASTERISCO_A : notaPonderada.toString().replace(".", ",");
    }

    @Transactional(readOnly = true)
    public String getNotaFinalRefinada(Metodologia metodologia, String notaCalculada) {
        ParametroNota parametroNotaRefinada = null;
        if (possuiNotaVigente(notaCalculada)) {
            parametroNotaRefinada = ParametroNotaMediator.get().buscarPorMetodologiaENota(metodologia,
                    new BigDecimal(notaCalculada.replace(",", ".")), false);
        }
        String notaRefinadaString = parametroNotaRefinada != null ? parametroNotaRefinada.getDescricao() : "";
        return possuiNotaVigente(notaCalculada) ? notaRefinadaString : Constantes.ASTERISCO_A;
    }

    @Transactional(readOnly = true)
    public String getNotaAEF(PerfilRisco perfilRisco, PerfilAcessoEnum perfil) {
        return getNotaAEF(perfilRisco, perfil, false);
    }

    @Transactional(readOnly = true)
    public String getNotaAEF(PerfilRisco perfilRisco, PerfilAcessoEnum perfil, boolean isAtaCorec) {
        ParametroNotaAQT notaAjustadaCorec = null;
        if (!isAtaCorec) {
            notaAjustadaCorec = AjusteCorecMediator.get().notaAjustadaCorecAEF(perfilRisco, perfilRisco.getCiclo(),
                    perfil);
        }
        NotaAjustadaAEF notaAjustadaSupervisor =
                NotaAjustadaAEFMediator.get().buscarNotaAjustadaAEF(perfilRisco.getCiclo(), perfilRisco);
        if (notaAjustadaCorec != null) {
            return notaAjustadaCorec.getLimiteSuperior().toString().replace('.', ',');
        } else if (notaAjustadaSupervisor != null && notaAjustadaSupervisor.getParamentroNotaAQT() != null) {
            return notaAjustadaSupervisor.getParamentroNotaAQT().getLimiteSuperior().toString().replace('.', ',');
        } else {
            return AnaliseQuantitativaAQTMediator.get().getNotaCalculadaAEF(perfilRisco, perfil, false, isAtaCorec);
        }
    }

    @Transactional(readOnly = true)
    public String getNotaFinal(PerfilRisco perfilRisco, PerfilAcessoEnum perfil, Metodologia metodologia) {
        return getNotaFinal(perfilRisco, perfil, metodologia, false);
    }

    @Transactional(readOnly = true)
    public String getNotaFinal(PerfilRisco perfilRisco, PerfilAcessoEnum perfil, Metodologia metodologia,
            boolean isAtaCorec) {
        String notaAnef = "";
        String notaMatriz = "";
        String notaCaculada = "";
        String notaFinal = "";
        String notaCorec = "";

        GrauPreocupacaoES grau = buscarPorPerfilRisco(perfilRisco.getPk());

        if (!isAtaCorec) {
            notaCorec = AjusteCorecMediator.get().notaAjustadaCorecES(perfilRisco, perfilRisco.getCiclo(), perfil);
        }

        if (metodologia.getIsMetodologiaNova()) {
            notaAnef = getNotaAEF(perfilRisco, perfil, isAtaCorec);
            notaMatriz = getNotaMatrizFinal(perfilRisco, perfil, isAtaCorec);
            notaCaculada = getNotaFinalCalculada(grau, notaAnef, notaMatriz, perfilRisco.getCiclo());
        }

        if (StringUtils.isNotBlank(notaCorec)) {
            notaFinal = notaCorec + " (Corec)";
        } else if (grau != null && grau.getParametroNota() != null) {
            notaFinal = grau.getParametroNota().getDescricao();
        } else if (grau != null && grau.getParametroGrauPreocupacao() != null) {
            notaFinal = grau.getParametroGrauPreocupacao().getDescricao();
        } else if (possuiNotaVigente(notaCaculada)) {
            notaFinal = getNotaFinalRefinada(metodologia, notaCaculada);
        } else {
            notaFinal = Constantes.ASTERISCO_A;
        }

        return notaFinal;
    }

    public boolean possuiNotaVigente(String nota) {
        return SisapsUtil.isNaoNuloOuVazio(nota) && !Constantes.ASTERISCO_A.equals(nota)
                && !Constantes.N_A.equals(nota);
    }

    @Transactional(readOnly = true)
    public List<GrauPreocupacaoES> buscarPorCiclo(Integer pkCiclo) {
        return grauPreocupacaoESDAO.buscarPorCiclo(pkCiclo);
    }

    @Transactional
    public void atualizarDadosNovaMetodologia(List<GrauPreocupacaoES> listaGraus, Metodologia metodologia) {
        for (GrauPreocupacaoES grau : listaGraus) {
            ParametroGrauPreocupacao parametroGrauPreocupacao = grau.getParametroGrauPreocupacao();
            if (parametroGrauPreocupacao != null) {
                Ciclo ciclo = CicloMediator.get().buscarCicloPorPK(grau.getCiclo().getPk());
                BigDecimal percentual = null;
                if (cicloMaisTresAnos(ciclo)) {
                    percentual = new BigDecimal(0.5).setScale(2, RoundingMode.HALF_UP);
                } else {
                    percentual = new BigDecimal(0.3).setScale(2, RoundingMode.HALF_UP);
                }
                ParametroNota parametroNota = ParametroNotaMediator.get().buscarPorDescricao(metodologia,
                        parametroGrauPreocupacao.getDescricao());
                grau.setParametroNota(parametroNota);
                grau.setParametroGrauPreocupacao(null);
                grau.setJustificativa("Migração de dados para nova metodologia");
                grau.setNumeroFatorRelevanciaAnef(percentual);
                grau.setAlterarDataUltimaAtualizacao(false);
                grauPreocupacaoESDAO.update(grau);
            }
        }
    }

    @Transactional(readOnly = true)
    public boolean cicloMaisTresAnos(Ciclo ciclo) {
        DateTime dataInicioCiclo = new DateTime(ciclo.getDataInicio());
        DateTime dataPrevisaoCorec = new DateTime(ciclo.getDataPrevisaoCorec());
        Years years = Years.yearsBetween(dataInicioCiclo, dataPrevisaoCorec);
        return years.getYears() >= 3;

    }

}
