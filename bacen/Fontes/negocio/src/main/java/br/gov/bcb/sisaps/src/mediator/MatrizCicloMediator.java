package br.gov.bcb.sisaps.src.mediator;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Hibernate;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.src.dao.MatrizCicloDAO;
import br.gov.bcb.sisaps.src.dominio.AnexoARC;
import br.gov.bcb.sisaps.src.dominio.Atividade;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoARC;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.CelulaRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Elemento;
import br.gov.bcb.sisaps.src.dominio.ItemElemento;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.Metodologia;
import br.gov.bcb.sisaps.src.dominio.ParametroGrupoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.ParametroNota;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.TendenciaARC;
import br.gov.bcb.sisaps.src.dominio.Unidade;
import br.gov.bcb.sisaps.src.dominio.VersaoPerfilRisco;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoARCEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoCicloEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoMatrizEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoGrupoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoLinhaMatrizVOEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoObjetoVersionadorEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoUnidadeAtividadeEnum;
import br.gov.bcb.sisaps.src.util.BufferAnexos;
import br.gov.bcb.sisaps.src.validacao.RegraAlterarPercentualGovernancaValidacao;
import br.gov.bcb.sisaps.src.validacao.RegraAlterarPercentualMatrizValidacao;
import br.gov.bcb.sisaps.src.validacao.RegraLiberacaoMatrizValidacao;
import br.gov.bcb.sisaps.src.vo.ArcNotasVO;
import br.gov.bcb.sisaps.src.vo.CelulaRiscoControleVO;
import br.gov.bcb.sisaps.src.vo.ConsultaMatrizVO;
import br.gov.bcb.sisaps.src.vo.LinhaMatrizVO;
import br.gov.bcb.sisaps.src.vo.MatrizVO;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.Util;
import br.gov.bcb.sisaps.util.enumeracoes.TipoSubEventoPerfilRiscoSRC;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.ConstantesMensagens;

@Service
public class MatrizCicloMediator extends AbstractMediatorPaginado<MatrizVO, Integer, ConsultaMatrizVO> {
    private static final String PERCENTUAL_ALTERADO_COM_SUCESSO = "Percentual alterado com sucesso.";
    private static final String A_AVALIAR = "*A";
    private static final BigDecimal FATOR_RELEVANCIA_PADRAO = new BigDecimal("0.5");
    private static final BigDecimal FATOR_RELEVANCIA_ARC_EXTERNO = null;
    private static final String PERCENTUAL = "%";

    @Autowired
    private MatrizCicloDAO matrizCicloDAO;

    @Autowired
    private UnidadeMediator unidadeMediator;

    @Autowired
    private AtividadeMediator atividadeMediator;

    @Autowired
    private MetodologiaMediator metodologiaMediator;

    @Autowired
    private AvaliacaoRiscoControleMediator avaliacaoRiscoControleMediator;

    @Autowired
    private CicloMediator cicloMediator;

    @Autowired
    private ElementoMediator elementoMediator;

    @Autowired
    private PerfilRiscoMediator perfilRiscoMediator;

    @Autowired
    private EventoConsolidadoMediator eventoConsolidadoMediator;

    public static MatrizCicloMediator get() {
        return SpringUtils.get().getBean(MatrizCicloMediator.class);
    }

    @Override
    protected MatrizCicloDAO getDao() {
        return matrizCicloDAO;
    }

    public void evict(Matriz obj) {
        matrizCicloDAO.evict(obj);
    }

    @Transactional(readOnly = true)
    public BigDecimal notaResidualParametroGrupo(Matriz matriz, ParametroGrupoRiscoControle grupo,
            List<VersaoPerfilRisco> versoesPerfilRiscoARCs, boolean notaVigente, TipoGrupoEnum tipo,
            List<CelulaRiscoControle> listaArcDoGrupo, PerfilAcessoEnum perfilTela, PerfilRisco perfilRisco) {

        BigDecimal somaPercentual = new BigDecimal(Constantes.ZERO);
        BigDecimal somatorioNotaPercentual = new BigDecimal(Constantes.ZERO);
        boolean possuiNota = false;
        for (CelulaRiscoControle celula : listaArcDoGrupo) {
            AvaliacaoRiscoControle arcload = avaliacaoRiscoControleMediator.loadNotaEPeso(
                    TipoGrupoEnum.RISCO.equals(tipo) ? celula.getArcRisco().getPk() : celula.getArcControle().getPk());

            Atividade atividade = celula.getAtividade();
            if (atividade != null) {

                BigDecimal percentualArc = percentualARC(matriz, versoesPerfilRiscoARCs, arcload, atividade, celula);

                if (notaVigente) {
                    String notaRisco =
                            avaliacaoRiscoControleMediator.notaArc(arcload, matriz.getCiclo(), perfilTela, perfilRisco);
                    if (!Constantes.ASTERISCO_A.equals(notaRisco)) {
                        BigDecimal notaArc = new BigDecimal(notaRisco.replace(',', '.'));
                        somaPercentual = somaPercentual.add(percentualArc);
                        somatorioNotaPercentual = somatorioNotaPercentual.add(notaArc.multiply(percentualArc));
                        possuiNota = true;
                    }
                } else {
                    if (!Constantes.ASTERISCO_A.equals(arcload.getNotaEmAnaliseDescricaoValor())) {
                        BigDecimal notaArc = new BigDecimal(arcload.getNotaEmAnaliseDescricaoValor().replace(',', '.'));
                        somaPercentual = somaPercentual.add(percentualArc);
                        somatorioNotaPercentual = somatorioNotaPercentual.add(notaArc.multiply(percentualArc));
                        possuiNota = true;
                    }
                }

            }

        }

        if (possuiNota) {
            if (somaPercentual.compareTo(BigDecimal.ZERO) == 0) {
                return BigDecimal.ZERO;
            } else {
                return somatorioNotaPercentual.divide(somaPercentual, MathContext.DECIMAL32);
            }
        }

        return null;

    }

    @Transactional(readOnly = true)
    public String mediaResidualParametroGrupo(Matriz matriz, ParametroGrupoRiscoControle grupo,
            List<VersaoPerfilRisco> versoesPerfilRiscoARCs, boolean notaVigente,
            List<CelulaRiscoControle> listaArcDoGrupo, PerfilAcessoEnum perfilTela, PerfilRisco perfilRisco) {
        BigDecimal somaPercentual = new BigDecimal(Constantes.ZERO);
        BigDecimal somatorioNotaPercentual = new BigDecimal(Constantes.ZERO);
        boolean possuiNota = false;
        for (CelulaRiscoControle celula : listaArcDoGrupo) {
            AvaliacaoRiscoControle arcload = celula.getArcRisco();
            AvaliacaoRiscoControle arcloadControle = celula.getArcControle();
            if (celula.getAtividade() != null) {
                BigDecimal percentualArc =
                        percentualARC(matriz, versoesPerfilRiscoARCs, arcload, celula.getAtividade(), celula);
                BigDecimal percentualArcControle =
                        percentualARC(matriz, versoesPerfilRiscoARCs, arcloadControle, celula.getAtividade(), celula);
                if (notaVigente) {
                    String notaRisco =
                            avaliacaoRiscoControleMediator.notaArc(arcload, matriz.getCiclo(), perfilTela, perfilRisco);
                    if (!Constantes.ASTERISCO_A.equals(notaRisco)) {
                        BigDecimal notaArc = new BigDecimal(notaRisco.replace(',', '.'));
                        somaPercentual = somaPercentual.add(percentualArc);
                        somatorioNotaPercentual = somatorioNotaPercentual.add(notaArc.multiply(percentualArc));
                        possuiNota = true;
                    }
                    String notaControle = avaliacaoRiscoControleMediator.notaArc(arcloadControle, matriz.getCiclo(),
                            perfilTela, perfilRisco);
                    if (!Constantes.ASTERISCO_A.equals(notaControle)) {
                        BigDecimal notaArc = new BigDecimal(notaControle.replace(',', '.'));
                        somaPercentual = somaPercentual.add(percentualArcControle);
                        somatorioNotaPercentual = somatorioNotaPercentual.add(notaArc.multiply(percentualArcControle));
                        possuiNota = true;
                    }
                } else {
                    if (!Constantes.ASTERISCO_A.equals(arcload.getNotaEmAnaliseDescricaoValor())) {
                        BigDecimal notaArc = new BigDecimal(arcload.getNotaEmAnaliseDescricaoValor().replace(',', '.'));
                        somaPercentual = somaPercentual.add(percentualArc);
                        somatorioNotaPercentual = somatorioNotaPercentual.add(notaArc.multiply(percentualArc));
                        possuiNota = true;
                    }
                    if (!Constantes.ASTERISCO_A.equals(arcloadControle.getNotaEmAnaliseDescricaoValor())) {
                        BigDecimal notaArc =
                                new BigDecimal(arcloadControle.getNotaEmAnaliseDescricaoValor().replace(',', '.'));
                        somaPercentual = somaPercentual.add(percentualArcControle);
                        somatorioNotaPercentual = somatorioNotaPercentual.add(notaArc.multiply(percentualArcControle));
                        possuiNota = true;
                    }
                }
            }
        }
        return retonarResultadoMediaResidual(somaPercentual, somatorioNotaPercentual, possuiNota);
    }

    @Transactional(readOnly = true)
    private BigDecimal percentualARC(Matriz matriz, List<VersaoPerfilRisco> versoesPerfilRiscoARCs,
            AvaliacaoRiscoControle arcload, Atividade atividade, CelulaRiscoControle celula) {
        String percentualAtividadeString;
        percentualAtividadeString = getPercentualAtividade(matriz, atividade);

        if (percentualAtividadeString != null) {
            CelulaRiscoControle celulaInicializada = CelulaRiscoControleMediator.get().buscar(celula.getPk());

            BigDecimal percentualAtividade = new BigDecimal(percentualAtividadeString.replace(',', '.'));

            BigDecimal somaDosPesosDosArcAtividade = new BigDecimal(CelulaRiscoControleMediator.get()
                    .somarPesoDosArcPorAtividades(matriz, atividade.getPk(), versoesPerfilRiscoARCs));

            if (somaDosPesosDosArcAtividade.compareTo(BigDecimal.ZERO) != 0) {
                BigDecimal pesoArc = new BigDecimal(celulaInicializada.getParametroPeso().getValor()
                        + celulaInicializada.getParametroPeso().getValor());

                BigDecimal multiplica = pesoArc.multiply(percentualAtividade);

                BigDecimal percentualArc = multiplica.divide(somaDosPesosDosArcAtividade, MathContext.DECIMAL32);

                BigDecimal percentualParticipacaoArc = TipoGrupoEnum.RISCO.equals(arcload.getTipo())
                        ? celulaInicializada.getAtividade().getMatriz().getCiclo().getMetodologia()
                                .getParametrosFatorRelevancia().get(0).getValorAlfa()
                        : celulaInicializada.getAtividade().getMatriz().getCiclo().getMetodologia()
                                .getParametrosFatorRelevancia().get(0).getValorBeta();

                percentualArc = percentualArc.multiply(percentualParticipacaoArc);
                return percentualArc.divide(new BigDecimal(100), MathContext.DECIMAL32);
            }
        }

        return null;
    }

    @Transactional(readOnly = true)
    private String getPercentualAtividade(Matriz matriload, Atividade atividade) {
        String percentualAtividadeString = null;
        if (atividade.getParametroTipoAtividadeNegocio() == null) {
            percentualAtividadeString = percentualDasAtividadesCorporativa(matriload, atividade.getPk(), false);
        } else {
            if (atividade.getUnidade() == null) {
                percentualAtividadeString = percentualDasAtividadesDaMatriz(matriload, atividade.getPk(), false);
            } else {
                percentualAtividadeString = percentualDasAtividadesDeNegocio(matriload, atividade.getPk(), false);
            }
        }
        if (percentualAtividadeString != null) {
            return percentualAtividadeString.substring(0, percentualAtividadeString.length() - 1);
        }
        return null;
    }

    @Transactional(readOnly = true)
    public String percentualDasAtividadesCorporativa(Matriz matriz, int atividade, boolean isArredondar) {

        Atividade ativ = atividadeMediator.getPK(atividade);

        TipoUnidadeAtividadeEnum tipoUnidade = ativ.getUnidade() == null ? null : ativ.getUnidade().getTipoUnidade();
        Integer pkUnidade = ativ.getUnidade() == null ? null : ativ.getUnidade().getPk();

        BigDecimal somaCorporativa = somaPesoDasAtividadesDaUnidadeOUMatriz(matriz, tipoUnidade, pkUnidade);
        BigDecimal somaAtividade = new BigDecimal(ativ.getParametroPeso().getValor());
        if (somaCorporativa.compareTo(BigDecimal.ZERO) != 0) {
            BigDecimal multiplica = somaAtividade.multiply(new BigDecimal(matriz.getPercentualCorporativoInt()));
            BigDecimal percentualValor = multiplica.divide(somaCorporativa, MathContext.DECIMAL32);
            if (isArredondar) {
                percentualValor = percentualValor.setScale(1, RoundingMode.HALF_UP);
            }
            return percentualValor.toString().replace('.', ',') + PERCENTUAL;

        }
        return null;
    }

    @Transactional(readOnly = true)
    public String percentualDasAtividadesDeNegocio(Matriz matriz, int pkAtividade, boolean isArredondar) {
        Atividade atividade = atividadeMediator.getPK(pkAtividade);

        String percentualUnidadeString = percentualDaUnidade(matriz, atividade.getUnidade(), false);

        percentualUnidadeString =
                percentualUnidadeString.substring(0, percentualUnidadeString.length() - 1).replace(',', '.');

        BigDecimal percentualUnidade = new BigDecimal(percentualUnidadeString);

        TipoUnidadeAtividadeEnum tipoUnidade =
                atividade.getUnidade() == null ? null : atividade.getUnidade().getTipoUnidade();
        Integer pkUnidade = atividade.getUnidade() == null ? null : atividade.getUnidade().getPk();

        BigDecimal somaUnidade = somaPesoDasAtividadesDaUnidadeOUMatriz(matriz, tipoUnidade, pkUnidade);
        if (somaUnidade.compareTo(BigDecimal.ZERO) != 0) {
            BigDecimal somaAtividade = new BigDecimal(atividade.getParametroPeso().getValor());
            BigDecimal multiplica = somaAtividade.multiply(percentualUnidade);
            BigDecimal percentualValor = multiplica.divide(somaUnidade, MathContext.DECIMAL32);
            if (isArredondar) {
                percentualValor = percentualValor.setScale(1, RoundingMode.HALF_UP);
            }
            return percentualValor.toString().replace('.', ',') + PERCENTUAL;

        }

        return null;

    }

    @Transactional(readOnly = true)
    public String percentualDasAtividadesDaMatriz(Matriz matriz, int pkAtividade, boolean isArredondar) {
        Atividade atividade = atividadeMediator.getPK(pkAtividade);

        BigDecimal somaTipo =
                somaPesoDasUnidadesNegocio(matriz).add(somaPesoDasAtividadesDaUnidadeOUMatriz(matriz, null, null));

        if (somaTipo.compareTo(BigDecimal.ZERO) != 0) {
            BigDecimal percentual = new BigDecimal(matriz.getPercentualNegocioInt());
            BigDecimal somaAtividade = new BigDecimal(atividade.getParametroPeso().getValor());
            BigDecimal multiplica = somaAtividade.multiply(percentual);
            BigDecimal percentualValor = multiplica.divide(somaTipo, MathContext.DECIMAL32);
            if (isArredondar) {
                percentualValor = percentualValor.setScale(1, RoundingMode.HALF_UP);
            }
            return percentualValor.toString().replace('.', ',') + PERCENTUAL;
        }
        return null;

    }

    @Transactional(readOnly = true)
    public String notaCalculada(Matriz matriz, List<CelulaRiscoControle> celula,
            List<VersaoPerfilRisco> versoesPerfilRiscoARCs, boolean notaVigente, PerfilAcessoEnum perfilTela,
            PerfilRisco perfilRisco) {
        return notaCalculada(matriz, celula, versoesPerfilRiscoARCs, notaVigente, perfilTela, perfilRisco, false);
    }

    @Transactional(readOnly = true)
    public String notaCalculada(Matriz matriz, List<CelulaRiscoControle> celula,
            List<VersaoPerfilRisco> versoesPerfilRiscoARCs, boolean notaVigente, PerfilAcessoEnum perfilTela,
            PerfilRisco perfilRisco, boolean isAtaCorec) {
        if (matriz == null) {
            return null;
        } else {
            BigDecimal somaPercentual = new BigDecimal(Constantes.ZERO);
            BigDecimal somatorioNotaPercentual = new BigDecimal(Constantes.ZERO);
            boolean possuiNota = false;
            for (CelulaRiscoControle cel : celula) {
                if (cel.getAtividade() != null) {
                    BigDecimal percentualArcRisco =
                            percentualARC(matriz, versoesPerfilRiscoARCs, cel.getArcRisco(), cel.getAtividade(), cel);
                    BigDecimal percentualArcControle = percentualARC(matriz, versoesPerfilRiscoARCs,
                            cel.getArcControle(), cel.getAtividade(), cel);
                    if (notaVigente) {
                        String notaRisco = avaliacaoRiscoControleMediator.notaArc(cel.getArcRisco(), matriz.getCiclo(),
                                perfilTela, perfilRisco, isAtaCorec);
                        if (!Constantes.ASTERISCO_A.equals(notaRisco)) {
                            BigDecimal notaArc = new BigDecimal(notaRisco.replace(',', '.'));
                            somaPercentual = somaPercentual.add(percentualArcRisco);
                            somatorioNotaPercentual = somatorioNotaPercentual.add(notaArc.multiply(percentualArcRisco));
                            possuiNota = true;
                        }
                        String notaControle = avaliacaoRiscoControleMediator.notaArc(cel.getArcControle(),
                                matriz.getCiclo(), perfilTela, perfilRisco, isAtaCorec);
                        if (!Constantes.ASTERISCO_A.equals(notaControle)) {
                            BigDecimal notaArc = new BigDecimal(notaControle.replace(',', '.'));
                            somaPercentual = somaPercentual.add(percentualArcControle);
                            somatorioNotaPercentual =
                                    somatorioNotaPercentual.add(notaArc.multiply(percentualArcControle));
                            possuiNota = true;
                        }
                    } else {
                        if (!Constantes.ASTERISCO_A.equals(cel.getArcRisco().getNotaEmAnaliseDescricaoValor())) {
                            BigDecimal notaArc = new BigDecimal(
                                    cel.getArcRisco().getNotaEmAnaliseDescricaoValor().replace(',', '.'));
                            somaPercentual = somaPercentual.add(percentualArcRisco);
                            somatorioNotaPercentual = somatorioNotaPercentual.add(notaArc.multiply(percentualArcRisco));
                            possuiNota = true;
                        }
                        if (!Constantes.ASTERISCO_A.equals(cel.getArcControle().getNotaEmAnaliseDescricaoValor())) {
                            BigDecimal notaArc = new BigDecimal(
                                    cel.getArcControle().getNotaEmAnaliseDescricaoValor().replace(',', '.'));
                            somaPercentual = somaPercentual.add(percentualArcControle);
                            somatorioNotaPercentual =
                                    somatorioNotaPercentual.add(notaArc.multiply(percentualArcControle));
                            possuiNota = true;
                        }
                    }
                }
            }
            return retonarResultadoMediaResidual(somaPercentual, somatorioNotaPercentual, possuiNota);
        }
    }

    @Transactional(readOnly = true)
    public String percentualDoGrupo(Matriz matriz, List<CelulaRiscoControle> listaCelulaArcDoGrupo,
            List<VersaoPerfilRisco> versoesPerfilRiscoARCs) {
        Matriz matriload = load(matriz.getPk());
        BigDecimal somaPercentual = new BigDecimal(Constantes.ZERO);

        for (CelulaRiscoControle celula : listaCelulaArcDoGrupo) {

            Atividade atividade = celula.getAtividade();
            if (atividade != null) {

                String percentualAtividadeString;
                if (atividade.getParametroTipoAtividadeNegocio() == null) {
                    percentualAtividadeString = percentualDasAtividadesCorporativa(matriload, atividade.getPk(), false);
                } else {
                    if (atividade.getUnidade() == null) {
                        percentualAtividadeString =
                                percentualDasAtividadesDaMatriz(matriload, atividade.getPk(), false);
                    } else {
                        percentualAtividadeString =
                                percentualDasAtividadesDeNegocio(matriload, atividade.getPk(), false);
                    }
                }
                percentualAtividadeString = percentualAtividadeString
                        .substring(0, percentualAtividadeString.length() - 1).replace(',', '.');
                BigDecimal percentualAtividade = new BigDecimal(percentualAtividadeString);
                BigDecimal somaDosPesosDosArcAtividade = new BigDecimal(CelulaRiscoControleMediator.get()
                        .somarPesoDosArcPorAtividades(matriz, atividade.getPk(), versoesPerfilRiscoARCs));

                BigDecimal pesoArc = new BigDecimal(celula.getParametroPeso().getValor());
                BigDecimal multiplica = pesoArc.multiply(percentualAtividade);
                somaPercentual =
                        somaPercentual.add(multiplica.divide(somaDosPesosDosArcAtividade, MathContext.DECIMAL32));
            }

        }
        somaPercentual = somaPercentual.multiply(BigDecimal.valueOf(2));
        BigDecimal percentual = somaPercentual.setScale(1, RoundingMode.HALF_UP);
        Integer intPercentual = percentual.intValue();
        if (percentual.toString().contains(".0")) {
            return intPercentual.toString() + PERCENTUAL;
        }

        return percentual.toString().replace('.', ',') + PERCENTUAL;
    }

    @Transactional(readOnly = true)
    public String notaCalculadaFinal(Matriz matriz, List<CelulaRiscoControle> celula,
            List<VersaoPerfilRisco> versoesPerfilRiscoARCs, boolean notaVigente, PerfilAcessoEnum perfilTela,
            PerfilRisco perfilRisco, AvaliacaoRiscoControle arcExterno) {
        return notaCalculadaFinal(matriz, celula, versoesPerfilRiscoARCs, notaVigente, perfilTela, perfilRisco,
                arcExterno, false);
    }

    @Transactional(readOnly = true)
    public String notaCalculadaFinal(Matriz matriz, List<CelulaRiscoControle> celula,
            List<VersaoPerfilRisco> versoesPerfilRiscoARCs, boolean notaVigente, PerfilAcessoEnum perfilTela,
            PerfilRisco perfilRisco, AvaliacaoRiscoControle arcExterno, boolean isAtaCorec) {

        String notaCalculadoMatriz =
                notaCalculada(matriz, celula, versoesPerfilRiscoARCs, notaVigente, perfilTela, perfilRisco, isAtaCorec);

        if (arcExterno == null) {
            return notaCalculadoMatriz;
        } else {
            Integer pencentualArcExterno = matriz.getPercentualGovernancaCorporativoInt();
            Integer pencentualMatriz = Integer.valueOf(100) - pencentualArcExterno;
            String notaArcExterno = AvaliacaoRiscoControleMediator.get().notaArc(arcExterno, matriz.getCiclo(),
                    perfilTela, perfilRisco);

            String notaCalculadaFinalMAtriz = A_AVALIAR;

            if (notaCalculadoMatriz != A_AVALIAR) {
                BigDecimal notaCalculadoMatrizBD = new BigDecimal(notaCalculadoMatriz);
                notaCalculadoMatrizBD = notaCalculadoMatrizBD.multiply(new BigDecimal(pencentualMatriz.intValue()));
                notaCalculadaFinalMAtriz = notaCalculadoMatrizBD.divide(new BigDecimal(100), MathContext.DECIMAL32)
                        .setScale(2, RoundingMode.HALF_UP).toString();
            }

            if (!"".equals(notaArcExterno) && !A_AVALIAR.equals(notaArcExterno)) {
                if (!notaVigente) {
                    notaArcExterno = arcExterno.getNotaEmAnaliseDescricaoValor();
                }
                BigDecimal notaArcExternoDB = new BigDecimal(notaArcExterno.replace(',', '.'));
                notaArcExternoDB = notaArcExternoDB.multiply(new BigDecimal(pencentualArcExterno.intValue()));
                BigDecimal notaCalculadaFinalAE = notaArcExternoDB.divide(new BigDecimal(100), MathContext.DECIMAL32)
                        .setScale(2, RoundingMode.HALF_UP);

                if (notaCalculadaFinalMAtriz == A_AVALIAR) {
                    return notaArcExterno;
                } else {
                    BigDecimal notaFinal = notaCalculadaFinalAE.add(new BigDecimal(notaCalculadaFinalMAtriz));
                    return notaFinal.toString();
                }
            }
            if (notaCalculadoMatriz != A_AVALIAR) {
                return notaCalculadoMatriz;
            }
        }

        return A_AVALIAR;
    }

    @Transactional
    public Matriz incluirMatrizCiclo(Ciclo ciclo) {
        Matriz matriz = new Matriz();
        matriz.setNumeroFatorRelevanciaUC(FATOR_RELEVANCIA_PADRAO);
        matriz.setNumeroFatorRelevanciaAE(FATOR_RELEVANCIA_ARC_EXTERNO);
        matriz.setEstadoMatriz(EstadoMatrizEnum.ESBOCADA);
        if (ciclo.getPk() != null) {
            matriz.setCiclo(ciclo);
        }
        matrizCicloDAO.save(matriz);
        unidadeMediator.incluirUnidadeCorporativa(matriz, ciclo);
        return matriz;
    }

    @Transactional(readOnly = true)
    public Matriz loadPK(Integer pk) {
        Matriz matriz = matrizCicloDAO.load(pk);
        inicializarDependencias(matriz);
        return matriz;
    }
    
    @Transactional(readOnly = true)
    public Matriz load(Integer pk) {
        return matrizCicloDAO.load(pk);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public String liberarMatrizCiclo(Matriz matriz) {
        Util.setIncluirBufferAnexos(true);
        BufferAnexos.resetLocalThreadBufferExclusao();
        validarLiberacao(matriz);
        matriz.setEstadoMatriz(EstadoMatrizEnum.VIGENTE);
        criarElementosMatriz(matriz);
        Ciclo ciclo = matriz.getCiclo();
        if (matriz.getPercentualGovernancaCorporativoInt() > 0) {
            criarElementosArcExterno(ciclo);
        } else if (matriz.getPercentualGovernancaCorporativoInt() == 0) {
            limparArcExterno(ciclo);
        }
        versionarPerfilRiscoMatriz(matriz, ciclo);
        matrizCicloDAO.update(matriz);
        matrizCicloDAO.flush();
        NotaMatrizMediator.get().incluirNovaNotaMatriz(matriz, ciclo,
                NotaMatrizMediator.get().getUltimaNotaMatriz(ciclo.getMatriz()));
        ciclo.setMatriz(matriz);
        cicloMediator.merge(ciclo);
        eventoConsolidadoMediator.incluirEventoPerfilDeRisco(ciclo, TipoSubEventoPerfilRiscoSRC.MATRIZ);
        GeradorAnexoMediator.get().excluirAnexosBuffer();
        return "A matriz de risco e controle foi liberada com sucesso.";
    }

    private void limparArcExterno(Ciclo ciclo) {
        AvaliacaoRiscoControle ultimoArcExterno =
                AvaliacaoRiscoControleExternoMediator.get().buscarUltimoArcExterno(ciclo);
        if (ultimoArcExterno != null) {
            AvaliacaoRiscoControle arcInicializado =
                    AvaliacaoRiscoControleMediator.get().buscar(ultimoArcExterno.getPk());
            if (arcInicializado != null) {
                arcInicializado.setEstado(EstadoARCEnum.PREVISTO);
                arcInicializado.setAvaliacaoRiscoControleExterno(null);
                arcInicializado.setAvaliacaoRiscoControleVigente(null);
                arcInicializado.setDataAnalise(null);
                arcInicializado.setDataConclusao(null);
                arcInicializado.setDataPreenchido(null);
                arcInicializado.setNotaCorec(null);
                arcInicializado.setNotaSupervisor(null);
                arcInicializado.setOperadorAnalise(null);
                arcInicializado.setOperadorConclusao(null);
                arcInicializado.setOperadorPreenchido(null);
                arcInicializado.setPercentualParticipacao(null);
                limparDesignacaoDelegacao(arcInicializado);
                limparElementosArc(arcInicializado, ciclo);
                limparTendenciaArc(arcInicializado);
                limparAvaliacaoArc(arcInicializado);
                limparAnexosArc(arcInicializado, ciclo);
                AvaliacaoRiscoControleMediator.get().alterar(arcInicializado);
            }
        }
    }

    private void limparTendenciaArc(AvaliacaoRiscoControle arc) {
        if (CollectionUtils.isNotEmpty(arc.getTendenciasArc())) {
            for (TendenciaARC tendencia : arc.getTendenciasArc()) {
                TendenciaMediator.get().excluirTendencia(tendencia);
            }
        }
    }

    private void limparElementosArc(AvaliacaoRiscoControle arc, Ciclo ciclo) {
        if (CollectionUtils.isNotEmpty(arc.getElementos())) {
            for (Elemento elemento : arc.getElementos()) {
                elemento.setDataAlteracao(null);
                elemento.setJustificativaSupervisor(null);
                elemento.setOperadorAlteracao(null);
                elemento.setParametroNotaInspetor(null);
                elemento.setParametroNotaSupervisor(null);
                if (CollectionUtils.isNotEmpty(elemento.getItensElemento())) {
                    for (ItemElemento item : elemento.getItensElemento()) {
                        item.setDataAlteracao(null);
                        item.setOperadorAlteracao(null);
                        DocumentoMediator.get().excluirDocumento(ciclo, item);
                        ItemElementoMediator.get().alterar(item);
                    }
                }
                ElementoMediator.get().alterar(elemento);
            }
        }
    }
    
    private void limparDesignacaoDelegacao(AvaliacaoRiscoControle arc) {
        if (arc.getDesignacao() != null) {
            DesignacaoMediator.get().excluir(arc.getDesignacao());
        }
        if (arc.getDelegacao() != null) {
            DelegacaoMediator.get().excluir(arc.getDelegacao());
        }
    }

    private void limparAvaliacaoArc(AvaliacaoRiscoControle arc) {
        if (CollectionUtils.isNotEmpty(arc.getAvaliacoesArc())) {
            for (AvaliacaoARC avaliacao : arc.getAvaliacoesArc()) {
                AvaliacaoARCMediator.get().excluir(avaliacao);
            }
        }
    }

    private void limparAnexosArc(AvaliacaoRiscoControle arc, Ciclo ciclo) {
        if (CollectionUtils.isNotEmpty(arc.getAnexosArc())) {
            for (AnexoARC anexo : arc.getAnexosArc()) {
                AnexoArcMediator.get().excluirAnexo(anexo, ciclo, arc);
            }
        }
    }

    private void criarElementosArcExterno(Ciclo ciclo) {
        ParametroGrupoRiscoControle parametroRCExterno = 
                ParametroGrupoRiscoControleMediator.get().buscarParametroRCExterno(ciclo.getMetodologia());
        AvaliacaoRiscoControle ultimoArcExterno = AvaliacaoRiscoControleExternoMediator.get().buscarUltimoArcExterno(ciclo);
        ElementoMediator.get().criarAtualizarElementosArc(
                ciclo, parametroRCExterno, ultimoArcExterno, TipoGrupoEnum.EXTERNO);
    }

    private void versionarPerfilRiscoMatriz(Matriz matriz, Ciclo ciclo) {
        // Se não existir matriz, incluir no perfil de risco atual, se não, versionar o perfil de risco.
        if (matrizCicloDAO.existeMatrizAnterior(ciclo)) {
            List<VersaoPerfilRisco> versoesCelulasARCsMatrizNotaPerfilRisco =
                    obterVersoesCelulasEARCsPerfilRisco(ciclo);
            versoesCelulasARCsMatrizNotaPerfilRisco.add(ciclo.getMatriz().getVersaoPerfilRisco());
            VersaoPerfilRisco versaoPerfilRisco =
                    perfilRiscoMediator.gerarNovaVersaoPerfilRisco(ciclo, matriz, TipoObjetoVersionadorEnum.MATRIZ);
            matriz.setVersaoPerfilRisco(versaoPerfilRisco);
            perfilRiscoMediator.excluirVersoesDoPerfilRiscoAtual(ciclo, versoesCelulasARCsMatrizNotaPerfilRisco);
        } else {
            perfilRiscoMediator.incluirVersaoPerfilRiscoAtual(ciclo, matriz, TipoObjetoVersionadorEnum.MATRIZ);
        }
        incluirARCsPerfilRiscoAtual(ciclo, matriz);

    }

    private List<VersaoPerfilRisco> obterVersoesCelulasEARCsPerfilRisco(Ciclo ciclo) {
        PerfilRisco perfilRiscoAtual = perfilRiscoMediator.obterPerfilRiscoAtual(ciclo.getPk());
        List<VersaoPerfilRisco> versoesAExcluir = new ArrayList<VersaoPerfilRisco>();
        versoesAExcluir.addAll(VersaoPerfilRiscoMediator.get().buscarVersoesPerfilRisco(perfilRiscoAtual.getPk(),
                TipoObjetoVersionadorEnum.CELULA_RISCO_CONTROLE));
        versoesAExcluir.addAll(VersaoPerfilRiscoMediator.get().buscarVersoesPerfilRisco(perfilRiscoAtual.getPk(),
                TipoObjetoVersionadorEnum.ARC));
        versoesAExcluir.addAll(VersaoPerfilRiscoMediator.get().buscarVersoesPerfilRisco(perfilRiscoAtual.getPk(),
                TipoObjetoVersionadorEnum.NOTA_MATRIZ));
        return versoesAExcluir;
    }

    private void incluirARCsPerfilRiscoAtual(Ciclo ciclo, Matriz matriz) {
        for (Atividade atividade : matriz.getAtividades()) {
            if (CollectionUtils.isNotEmpty(atividade.getCelulasRiscoControle())) {
                for (CelulaRiscoControle celulaRiscoControle : atividade.getCelulasRiscoControle()) {
                    CelulaRiscoControleMediator.get().buscar(celulaRiscoControle.getPk());
                    perfilRiscoMediator.incluirVersaoPerfilRiscoAtual(ciclo, celulaRiscoControle,
                            TipoObjetoVersionadorEnum.CELULA_RISCO_CONTROLE);
                    CelulaRiscoControleMediator.get().alterar(celulaRiscoControle);
                    AvaliacaoRiscoControle arcRisco = 
                            AvaliacaoRiscoControleMediator.get().buscar(celulaRiscoControle.getArcRisco().getPk());
                    AvaliacaoRiscoControle arcControle = 
                            AvaliacaoRiscoControleMediator.get().buscar(celulaRiscoControle.getArcControle().getPk());
                    perfilRiscoMediator.incluirVersaoPerfilRiscoAtual(ciclo, Arrays.asList(arcRisco, arcControle), 
                            TipoObjetoVersionadorEnum.ARC);
                    AvaliacaoRiscoControleMediator.get().alterar(arcRisco);
                    AvaliacaoRiscoControleMediator.get().alterar(arcControle);
                }
            }
        }
        
        if (matriz.getPercentualGovernancaCorporativoInt() > 0) {
            AvaliacaoRiscoControle ultimoArcExterno = 
                    AvaliacaoRiscoControleExternoMediator.get().buscarUltimoArcExterno(ciclo);
            perfilRiscoMediator.incluirVersaoPerfilRiscoAtual(ciclo, ultimoArcExterno,
                    TipoObjetoVersionadorEnum.ARC);
        }
    }

    private void criarElementosMatriz(Matriz matriz) {
        for (Atividade atividade : matriz.getAtividades()) {
            for (CelulaRiscoControle celulaRiscoControle : atividade.getCelulasRiscoControle()) {
                elementoMediator.criarElementosCelulaRiscoControle(matriz.getCiclo(), celulaRiscoControle);
            }
        }
    }

    @Transactional
    public void editarMatrizCiclo(Matriz matriz, PerfilRisco perfilRiscoAtual) {
        validarEdicao(matriz);
        criarVersaoMatriz(matriz, perfilRiscoAtual, false, null);
    }

    @Transactional
    public void desfazerMatrizCiclo(Matriz matrizVigente, Ciclo ciclo, PerfilRisco perfilRiscoAtual) {
        Matriz matrizEsbocada = getUltimaMatrizCiclo(ciclo);
        matrizCicloDAO.flush();
        unidadeMediator.excluirListaUnidade(matrizEsbocada.getUnidades());
        atividadeMediator.excluir(matrizEsbocada.getAtividadesMatriz());
        matrizCicloDAO.delete(matrizEsbocada);
        matrizCicloDAO.flush();

        if (matrizVigente == null) {
            incluirMatrizCiclo(ciclo);
        } else {
            criarVersaoMatriz(matrizVigente, perfilRiscoAtual, false, null);
        }

    }

    private Matriz criarVersaoMatriz(Matriz matrizBase, PerfilRisco perfilRiscoAtual, boolean isEncerrarCorec,
            Ciclo novoCiclo) {
        Matriz matrizNova = new Matriz();
        matrizNova.setCiclo(matrizBase.getCiclo());
        matrizNova.setNumeroFatorRelevanciaUC(matrizBase.getNumeroFatorRelevanciaUC());
        matrizNova.setNumeroFatorRelevanciaAE(matrizBase.getNumeroFatorRelevanciaAE());
        matrizNova.setEstadoMatriz(EstadoMatrizEnum.ESBOCADA);
        if (isEncerrarCorec) {
            matrizNova.setCiclo(novoCiclo);
            matrizNova.setEstadoMatriz(EstadoMatrizEnum.VIGENTE);
            matrizNova.setUltimaAtualizacao(matrizBase.getUltimaAtualizacao());
            matrizNova.setOperadorAtualizacao(matrizBase.getOperadorAtualizacao());
            matrizNova.setAlterarDataUltimaAtualizacao(false);
            PerfilRiscoMediator.get().incluirVersaoPerfilRiscoAtual(novoCiclo, matrizNova,
                    TipoObjetoVersionadorEnum.MATRIZ);
        }
        matrizCicloDAO.save(matrizNova);
        unidadeMediator.criarVersaoUnidades(matrizBase, matrizNova, isEncerrarCorec);
        atividadeMediator.criarVersaoAtividades(matrizBase, matrizNova, perfilRiscoAtual, isEncerrarCorec);
        return matrizNova;
    }

    public String obterSupervisorTitular(Matriz matriz) {
        String supervisor = null;
        if (matriz.getCiclo() != null) {
            supervisor =
                    CicloMediator.get()
                            .buscarChefeAtual(matriz.getCiclo().getEntidadeSupervisionavel().getLocalizacao())
                            .getMatricula();
        }
        return supervisor;
    }

    private void validarLiberacao(Matriz matriz) {
        new RegraLiberacaoMatrizValidacao(matriz, metodologiaMediator).validar();
    }

    private void validarEdicao(Matriz matriz) {
        EstadoCicloEnum estadoCiclo = matriz.getCiclo().getEstadoCiclo().getEstado();
        SisapsUtil.lancarNegocioException(ConstantesMensagens.MSG_APS_MATRIZ_ERRO_016
                + estadoCiclo.getDescricao().toLowerCase() + ".",
                !EstadoCicloEnum.EM_ANDAMENTO.equals(matriz.getCiclo().getEstadoCiclo().getEstado()));
    }

    @Transactional(readOnly = true)
    public void inicializarDependencias(Matriz matriz) {
        if (matriz.getAtividades() != null) {
            Hibernate.initialize(matriz.getAtividades());
        }
        if (matriz.getUnidades() != null) {
            Hibernate.initialize(matriz.getUnidades());
            for (Unidade unidade : matriz.getUnidades()) {
                Hibernate.initialize(unidade.getAtividades());
            }
        }
        if (matriz.getCiclo() != null) {
            CicloMediator.get().inicializarDependencias(matriz.getCiclo());
        }

    }

    @Transactional
    public void alterar(Matriz matriz) {
        matrizCicloDAO.update(matriz);
        matrizCicloDAO.flush();
    }

    @Transactional
    public void merge(Matriz matriz) {
        matrizCicloDAO.merge(matriz);
        matrizCicloDAO.flush();
    }

    @Transactional
    public String alterarPercentualUC(Matriz matriz, BigDecimal numeroFatorRelevanciaUC) {
        new RegraAlterarPercentualMatrizValidacao(numeroFatorRelevanciaUC).validar();
        matriz.setNumeroFatorRelevanciaUC(numeroFatorRelevanciaUC);
        alterar(matriz);
        return PERCENTUAL_ALTERADO_COM_SUCESSO;
    }
    
    @Transactional
    public String alterarPercentualAE(Matriz matriz, BigDecimal numeroFatorRelevanciaAE) {
        new RegraAlterarPercentualGovernancaValidacao(numeroFatorRelevanciaAE).validar();
        matriz.setNumeroFatorRelevanciaAE(numeroFatorRelevanciaAE);
        AvaliacaoRiscoControleExternoMediator.get().criarArcExterno(matriz);
        alterar(matriz);
        return PERCENTUAL_ALTERADO_COM_SUCESSO;
    }

    @Transactional(readOnly = true)
    public Matriz getUltimaMatrizCiclo(Ciclo ciclo) {
        Matriz matriz = matrizCicloDAO.getUltimaMatrizCiclo(ciclo);
        loadPK(matriz.getPk());
        return matriz;
    }

    @Transactional(readOnly = true)
    public Matriz getMatrizEmEdicao(Ciclo ciclo) {
        Matriz matriz = matrizCicloDAO.getMatrizEmEdicao(ciclo);
        if (matriz != null) {
            loadPK(matriz.getPk());
        }
        return matriz;
    }

    public Matriz buscarPorVersaoPerfilRisco(Integer pkVersaoPerfilRisco) {
        return matrizCicloDAO.buscarPorVersaoPerfilRisco(pkVersaoPerfilRisco);
    }

    @Transactional(readOnly = true)
    public Matriz buscar(Integer pk) {
        Matriz matriz = matrizCicloDAO.buscar(pk);
        inicializarDependencias(matriz);
        return matriz;
    }

    @Transactional(readOnly = true)
    public String percentualDaUnidade(Matriz matriz, Unidade unidade, boolean isArredondar) {

        BigDecimal somaTipo =
                somaPesoDasUnidadesNegocio(matriz).add(somaPesoDasAtividadesDaUnidadeOUMatriz(matriz, null, null));
        BigDecimal somaDaUnidade = new BigDecimal(unidade.getParametroPeso().getValor());
        BigDecimal percentualNegocio = new BigDecimal(matriz.getPercentualNegocioInt());
        BigDecimal multiplica = somaDaUnidade.multiply(percentualNegocio);
        BigDecimal percentualValor = multiplica.divide(somaTipo, MathContext.DECIMAL32);
        if (isArredondar) {
            percentualValor = percentualValor.setScale(1, RoundingMode.HALF_UP);
        }
        return percentualValor.toString().replace('.', ',') + PERCENTUAL;

    }

    @Transactional(readOnly = true)
    public String percentualDaUnidadeVO(Matriz matriz, CelulaRiscoControleVO celula, boolean isArredondar) {

        BigDecimal somaTipo =
                somaPesoDasUnidadesNegocio(matriz).add(somaPesoDasAtividadesDaUnidadeOUMatriz(matriz, null, null));
        BigDecimal somaDaUnidade = new BigDecimal(celula.getValorPesoUnidade());
        BigDecimal percentualNegocio = new BigDecimal(matriz.getPercentualNegocioInt());
        BigDecimal multiplica = somaDaUnidade.multiply(percentualNegocio);
        BigDecimal percentualValor = multiplica.divide(somaTipo, MathContext.DECIMAL32);
        if (isArredondar) {
            percentualValor = percentualValor.setScale(1, RoundingMode.HALF_UP);
        }
        return percentualValor.toString().replace('.', ',') + PERCENTUAL;

    }

    @Transactional(readOnly = true)
    public String percentualDasAtividadesCorporativaVO(Matriz matriz, CelulaRiscoControleVO celula,
            boolean isArredondar) {

        BigDecimal somaCorporativa =
                somaPesoDasAtividadesDaUnidadeOUMatriz(matriz, celula.getTipoUnidade(), celula.getUnidadePk());
        BigDecimal somaAtividade = new BigDecimal(celula.getValorPesoAtividade());
        if (somaCorporativa.compareTo(BigDecimal.ZERO) != 0) {
            BigDecimal multiplica = somaAtividade.multiply(new BigDecimal(matriz.getPercentualCorporativoInt()));
            BigDecimal percentualValor = multiplica.divide(somaCorporativa, MathContext.DECIMAL32);
            if (isArredondar) {
                percentualValor = percentualValor.setScale(1, RoundingMode.HALF_UP);
            }
            return percentualValor.toString().replace('.', ',') + PERCENTUAL;

        }
        return null;
    }

    @Transactional(readOnly = true)
    public String percentualDasAtividadesDeNegocioVO(Matriz matriz, CelulaRiscoControleVO celula,
            boolean isArredondar) {
        String percentualUnidadeString = percentualDaUnidadeVO(matriz, celula, false);

        percentualUnidadeString =
                percentualUnidadeString.substring(0, percentualUnidadeString.length() - 1).replace(',', '.');

        BigDecimal percentualUnidade = new BigDecimal(percentualUnidadeString);

        BigDecimal somaUnidade =
                somaPesoDasAtividadesDaUnidadeOUMatriz(matriz, celula.getTipoUnidade(), celula.getUnidadePk());
        if (somaUnidade.compareTo(BigDecimal.ZERO) != 0) {
            BigDecimal somaAtividade = new BigDecimal(celula.getValorPesoAtividade());
            BigDecimal multiplica = somaAtividade.multiply(percentualUnidade);
            BigDecimal percentualValor = multiplica.divide(somaUnidade, MathContext.DECIMAL32);
            if (isArredondar) {
                percentualValor = percentualValor.setScale(1, RoundingMode.HALF_UP);
            }
            return percentualValor.toString().replace('.', ',') + PERCENTUAL;

        }

        return null;

    }

    @Transactional(readOnly = true)
    public String percentualDasAtividadesDaMatrizVO(Matriz matriz, CelulaRiscoControleVO celula, boolean isArredondar) {
        BigDecimal somaTipo =
                somaPesoDasUnidadesNegocio(matriz).add(somaPesoDasAtividadesDaUnidadeOUMatriz(matriz, null, null));

        if (somaTipo.compareTo(BigDecimal.ZERO) != 0) {
            BigDecimal percentual = new BigDecimal(matriz.getPercentualNegocioInt());
            BigDecimal somaAtividade = new BigDecimal(celula.getValorPesoAtividade());
            BigDecimal multiplica = somaAtividade.multiply(percentual);
            BigDecimal percentualValor = multiplica.divide(somaTipo, MathContext.DECIMAL32);
            if (isArredondar) {
                percentualValor = percentualValor.setScale(1, RoundingMode.HALF_UP);
            }
            return percentualValor.toString().replace('.', ',') + PERCENTUAL;
        }
        return null;

    }

    @Transactional(readOnly = true)
    public String percentualDaLinha(Matriz matriz, LinhaMatrizVO linha) {
        Matriz matriload = load(matriz.getPk());
        String percentualRetorno;
        if (TipoLinhaMatrizVOEnum.UNIDADE.equals(linha.getTipo())) {
            if (TipoUnidadeAtividadeEnum.CORPORATIVO.equals(linha.getAtividade())) {
                percentualRetorno = matriz.getPercentualCorporativo();
            } else {
                percentualRetorno = percentualDaUnidade(matriload, UnidadeMediator.get().getPK(linha.getPk()), true);
            }
        } else {
            if (TipoUnidadeAtividadeEnum.CORPORATIVO.equals(linha.getAtividade())) {
                percentualRetorno = percentualDasAtividadesCorporativa(matriload, linha.getPk(), true);
            } else {
                if (linha.isFilho()) {
                    percentualRetorno = percentualDasAtividadesDeNegocio(matriload, linha.getPk(), true);
                } else {
                    percentualRetorno = percentualDasAtividadesDaMatriz(matriload, linha.getPk(), true);
                }
            }
        }

        if (percentualRetorno.contains(",0")) {
            return percentualRetorno.substring(0, percentualRetorno.length() - 3) + PERCENTUAL;
        }

        return percentualRetorno;
    }

    @Transactional(readOnly = true)
    public BigDecimal somaPesoDasUnidadesNegocio(Matriz matriz) {
        return new BigDecimal(unidadeMediator.somarPesoTipoUnidade(matriz, TipoUnidadeAtividadeEnum.NEGOCIO));

    }

    @Transactional(readOnly = true)
    public BigDecimal somaPesoDasAtividadesDaUnidadeOUMatriz(Matriz matriz, TipoUnidadeAtividadeEnum tipoUnidade,
            Integer pkUnidade) {
        if (pkUnidade == null) {
            return new BigDecimal(atividadeMediator.somarPesoAtividade(matriz, TipoUnidadeAtividadeEnum.NEGOCIO, null));
        } else {
            return new BigDecimal(atividadeMediator.somarPesoAtividade(matriz, tipoUnidade, pkUnidade));
        }
    }

    @Transactional(readOnly = true)
    public BigDecimal somaPesoDasAtividade(List<Atividade> listAtividade) {
        BigDecimal soma = BigDecimal.ZERO;
        if (listAtividade != null && !listAtividade.isEmpty()) {
            for (Atividade atividade : listAtividade) {
                soma = soma.add(new BigDecimal(atividade.getParametroPeso().getValor()));

            }
        }
        return soma;

    }

    @Transactional(readOnly = true)
    private String retonarResultadoMediaResidual(BigDecimal somaPercentual, BigDecimal somatorioNotaPercentual,
            boolean possuiNota) {
        if (possuiNota && somatorioNotaPercentual != null && somatorioNotaPercentual.compareTo(BigDecimal.ZERO) == 1
                && somaPercentual != null && somaPercentual.compareTo(BigDecimal.ZERO) == 1) {
            return somatorioNotaPercentual.divide(somaPercentual, MathContext.DECIMAL32)
                    .setScale(2, RoundingMode.HALF_UP).toString();
        }
        return A_AVALIAR;
    }

    @Transactional
    public void alterarVersaoMatriz(Matriz matriz) {
        matriz.setUltimaAtualizacao(new DateTime());
        matrizCicloDAO.merge(matriz);
    }

    @Transactional
    public Matriz criarMatrizNovoCiclo(PerfilRisco perfilRiscoCicloAtual, Ciclo novoCiclo) {
        VersaoPerfilRisco versaoPerfilRisco =
                VersaoPerfilRiscoMediator.get().buscarVersaoPerfilRisco(perfilRiscoCicloAtual.getPk(),
                        TipoObjetoVersionadorEnum.MATRIZ);
        Matriz matrizCicloAtual = buscarPorVersaoPerfilRisco(versaoPerfilRisco.getPk());
        return criarVersaoMatriz(matrizCicloAtual, perfilRiscoCicloAtual, true, novoCiclo);
    }
    
    @Transactional(readOnly = true)
    public String notaRefinadaFinal(Metodologia metodologia, String notaCalculada) {
        if (A_AVALIAR.equals(notaCalculada)) {
            return A_AVALIAR;
        } else {
            ParametroNota parametroNota = ParametroNotaMediator.get().buscarPorMetodologiaENota(metodologia,
                    new BigDecimal(notaCalculada.replace(',', '.')), false);
            if (parametroNota == null) {
                return A_AVALIAR;
            } else {
                return metodologia.getIsMetodologiaNova() ? parametroNota.getDescricao()
                        : parametroNota.getDescricaoValor();
            }
        }
    }

    @Transactional(readOnly = true)
    public String notaCalculadaFinalVO(Matriz matriz, List<CelulaRiscoControleVO> celula,
            List<VersaoPerfilRisco> versoesPerfilRiscoARCs, boolean notaVigente, PerfilAcessoEnum perfilTela,
            PerfilRisco perfilRisco, ArcNotasVO arcExterno) {
        return notaCalculadaFinalVO(matriz, celula, versoesPerfilRiscoARCs, notaVigente, perfilTela, perfilRisco,
                arcExterno, false);
    }

    @Transactional(readOnly = true)
    public String notaCalculadaFinalVO(Matriz matriz, List<CelulaRiscoControleVO> celula,
            List<VersaoPerfilRisco> versoesPerfilRiscoARCs, boolean notaVigente, PerfilAcessoEnum perfilTela,
            PerfilRisco perfilRisco, ArcNotasVO arcExterno, boolean isAtaCorec) {

        String notaCalculadoMatriz = notaCalculadaVO(matriz, celula, versoesPerfilRiscoARCs, notaVigente, perfilTela,
                perfilRisco, isAtaCorec);

        if (arcExterno == null) {
            return notaCalculadoMatriz;
        } else {
            Integer pencentualArcExterno = matriz.getPercentualGovernancaCorporativoInt();
            Integer pencentualMatriz = Integer.valueOf(100) - pencentualArcExterno;
            String notaArcExterno = AvaliacaoRiscoControleMediator.get().notaArc(arcExterno, matriz.getCiclo(),
                    perfilTela, perfilRisco);

            String notaCalculadaFinalMAtriz = A_AVALIAR;

            if (notaCalculadoMatriz != A_AVALIAR) {
                BigDecimal notaCalculadoMatrizBD = new BigDecimal(notaCalculadoMatriz);
                notaCalculadoMatrizBD = notaCalculadoMatrizBD.multiply(new BigDecimal(pencentualMatriz.intValue()));
                notaCalculadaFinalMAtriz = notaCalculadoMatrizBD.divide(new BigDecimal(100), MathContext.DECIMAL32)
                        .setScale(2, RoundingMode.HALF_UP).toString();
            }

            if (!"".equals(notaArcExterno) && !A_AVALIAR.equals(notaArcExterno)) {
                if (!notaVigente) {
                    notaArcExterno = avaliacaoRiscoControleMediator.getNotaEmAnaliseDescricaoValor(arcExterno);
                }
                BigDecimal notaArcExternoDB = new BigDecimal(notaArcExterno.replace(',', '.'));
                notaArcExternoDB = notaArcExternoDB.multiply(new BigDecimal(pencentualArcExterno.intValue()));
                BigDecimal notaCalculadaFinalAE = notaArcExternoDB.divide(new BigDecimal(100), MathContext.DECIMAL32)
                        .setScale(2, RoundingMode.HALF_UP);

                if (notaCalculadaFinalMAtriz == A_AVALIAR) {
                    return notaArcExterno;
                } else {
                    BigDecimal notaFinal = notaCalculadaFinalAE.add(new BigDecimal(notaCalculadaFinalMAtriz));
                    return notaFinal.toString();
                }
            }
            if (notaCalculadoMatriz != A_AVALIAR) {
                return notaCalculadoMatriz;
            }
        }

        return A_AVALIAR;
    }
    
    @Transactional(readOnly = true)
    public String percentualDoGrupoVO(Matriz matriz, List<CelulaRiscoControleVO> listaCelulaArcDoGrupo,
            List<VersaoPerfilRisco> versoesPerfilRiscoARCs) {
        Matriz matriload = load(matriz.getPk());
        BigDecimal somaPercentual = new BigDecimal(Constantes.ZERO);

        for (CelulaRiscoControleVO celula : listaCelulaArcDoGrupo) {
            if (celula.getAtividadePk() != null) {
                String percentualAtividadeString;
                if (celula.getTipoAtividade() == null) {
                    percentualAtividadeString = percentualDasAtividadesCorporativaVO(matriload, celula, false);
                } else {
                    if (celula.getUnidadePk() == null) {
                        percentualAtividadeString = percentualDasAtividadesDaMatrizVO(matriload, celula, false);
                    } else {
                        percentualAtividadeString = percentualDasAtividadesDeNegocioVO(matriload, celula, false);
                    }
                }
                percentualAtividadeString = percentualAtividadeString
                        .substring(0, percentualAtividadeString.length() - 1).replace(',', '.');
                BigDecimal percentualAtividade = new BigDecimal(percentualAtividadeString);
                BigDecimal somaDosPesosDosArcAtividade = new BigDecimal(CelulaRiscoControleMediator.get()
                        .somarPesoDosArcPorAtividades(matriz, celula.getAtividadePk(), versoesPerfilRiscoARCs));
                BigDecimal pesoArc = new BigDecimal(celula.getValorPeso());
                BigDecimal multiplica = pesoArc.multiply(percentualAtividade);
                somaPercentual =
                        somaPercentual.add(multiplica.divide(somaDosPesosDosArcAtividade, MathContext.DECIMAL32));
            }
        }

        somaPercentual = somaPercentual.multiply(BigDecimal.valueOf(2));
        BigDecimal percentual = somaPercentual.setScale(1, RoundingMode.HALF_UP);
        Integer intPercentual = percentual.intValue();
        if (percentual.toString().contains(".0")) {
            return intPercentual.toString() + PERCENTUAL;
        }
        return percentual.toString().replace('.', ',') + PERCENTUAL;
    }

    @Transactional(readOnly = true)
    public BigDecimal notaResidualParametroGrupoVO(Matriz matriz, ParametroGrupoRiscoControle grupo,
            List<VersaoPerfilRisco> versoesPerfilRiscoARCs, boolean notaVigente, TipoGrupoEnum tipo,
            List<CelulaRiscoControleVO> listaArcDoGrupo, PerfilAcessoEnum perfilTela, PerfilRisco perfilRisco) {

        BigDecimal somaPercentual = new BigDecimal(Constantes.ZERO);
        BigDecimal somatorioNotaPercentual = new BigDecimal(Constantes.ZERO);
        boolean possuiNota = false;
        for (CelulaRiscoControleVO celula : listaArcDoGrupo) {
            ArcNotasVO arcVO = TipoGrupoEnum.RISCO.equals(tipo)
                    ? avaliacaoRiscoControleMediator.consultarNotasArc(celula.getArcRiscoPk())
                    : avaliacaoRiscoControleMediator.consultarNotasArc(celula.getArcControlePk());

            if (celula.getAtividadePk() != null) {
                BigDecimal percentualArc = percentualARCVO(matriz, versoesPerfilRiscoARCs, celula, arcVO);

                if (notaVigente) {
                    String notaRisco =
                            avaliacaoRiscoControleMediator.notaArc(arcVO, matriz.getCiclo(), perfilTela, perfilRisco);
                    if (!Constantes.ASTERISCO_A.equals(notaRisco)) {
                        BigDecimal notaArc = new BigDecimal(notaRisco.replace(',', '.'));
                        somaPercentual = somaPercentual.add(percentualArc);
                        somatorioNotaPercentual = somatorioNotaPercentual.add(notaArc.multiply(percentualArc));
                        possuiNota = true;
                    }
                } else {
                    if (!Constantes.ASTERISCO_A
                            .equals(avaliacaoRiscoControleMediator.getNotaEmAnaliseDescricaoValor(arcVO))) {
                        BigDecimal notaArc = new BigDecimal(
                                avaliacaoRiscoControleMediator.getNotaEmAnaliseDescricaoValor(arcVO).replace(',', '.'));
                        somaPercentual = somaPercentual.add(percentualArc);
                        somatorioNotaPercentual = somatorioNotaPercentual.add(notaArc.multiply(percentualArc));
                        possuiNota = true;
                    }
                }
            }
        }

        if (possuiNota) {
            if (somaPercentual.compareTo(BigDecimal.ZERO) == 0) {
                return BigDecimal.ZERO;
            } else {
                return somatorioNotaPercentual.divide(somaPercentual, MathContext.DECIMAL32);
            }
        }
        return null;
    }

    @Transactional(readOnly = true)
    public String mediaResidualParametroGrupoVO(Matriz matriz, ParametroGrupoRiscoControle grupo,
            List<VersaoPerfilRisco> versoesPerfilRiscoARCs, boolean notaVigente,
            List<CelulaRiscoControleVO> listaArcDoGrupo, PerfilAcessoEnum perfilTela, PerfilRisco perfilRisco) {
        BigDecimal somaPercentual = new BigDecimal(Constantes.ZERO);
        BigDecimal somatorioNotaPercentual = new BigDecimal(Constantes.ZERO);
        boolean possuiNota = false;
        for (CelulaRiscoControleVO celula : listaArcDoGrupo) {
            ArcNotasVO arcVORisco = avaliacaoRiscoControleMediator.consultarNotasArc(celula.getArcRiscoPk());
            ArcNotasVO arcVOControle = avaliacaoRiscoControleMediator.consultarNotasArc(celula.getArcControlePk());
            if (celula.getAtividadePk() != null) {
                BigDecimal percentualArc = percentualARCVO(matriz, versoesPerfilRiscoARCs, celula, arcVORisco);
                BigDecimal percentualArcControle =
                        percentualARCVO(matriz, versoesPerfilRiscoARCs, celula, arcVOControle);
                if (notaVigente) {
                    String notaRisco = avaliacaoRiscoControleMediator.notaArc(arcVORisco, matriz.getCiclo(), perfilTela,
                            perfilRisco);
                    if (!Constantes.ASTERISCO_A.equals(notaRisco)) {
                        BigDecimal notaArc = new BigDecimal(notaRisco.replace(',', '.'));
                        somaPercentual = somaPercentual.add(percentualArc);
                        somatorioNotaPercentual = somatorioNotaPercentual.add(notaArc.multiply(percentualArc));
                        possuiNota = true;
                    }
                    String notaControle = avaliacaoRiscoControleMediator.notaArc(arcVOControle, matriz.getCiclo(),
                            perfilTela, perfilRisco);
                    if (!Constantes.ASTERISCO_A.equals(notaControle)) {
                        BigDecimal notaArc = new BigDecimal(notaControle.replace(',', '.'));
                        somaPercentual = somaPercentual.add(percentualArcControle);
                        somatorioNotaPercentual = somatorioNotaPercentual.add(notaArc.multiply(percentualArcControle));
                        possuiNota = true;
                    }
                } else {
                    if (!Constantes.ASTERISCO_A
                            .equals(avaliacaoRiscoControleMediator.getNotaEmAnaliseDescricaoValor(arcVORisco))) {
                        BigDecimal notaArc = new BigDecimal(avaliacaoRiscoControleMediator
                                .getNotaEmAnaliseDescricaoValor(arcVORisco).replace(',', '.'));
                        somaPercentual = somaPercentual.add(percentualArc);
                        somatorioNotaPercentual = somatorioNotaPercentual.add(notaArc.multiply(percentualArc));
                        possuiNota = true;
                    }
                    if (!Constantes.ASTERISCO_A
                            .equals(avaliacaoRiscoControleMediator.getNotaEmAnaliseDescricaoValor(arcVOControle))) {
                        BigDecimal notaArc = new BigDecimal(avaliacaoRiscoControleMediator
                                .getNotaEmAnaliseDescricaoValor(arcVOControle).replace(',', '.'));
                        somaPercentual = somaPercentual.add(percentualArcControle);
                        somatorioNotaPercentual = somatorioNotaPercentual.add(notaArc.multiply(percentualArcControle));
                        possuiNota = true;
                    }
                }
            }
        }
        return retonarResultadoMediaResidual(somaPercentual, somatorioNotaPercentual, possuiNota);
    }

    @Transactional(readOnly = true)
    private BigDecimal percentualARCVO(Matriz matriz, List<VersaoPerfilRisco> versoesPerfilRiscoARCs,
            CelulaRiscoControleVO celula, ArcNotasVO arcVO) {
        String percentualAtividadeString;
        percentualAtividadeString = getPercentualAtividadeVO(matriz, celula);

        if (percentualAtividadeString != null) {

            BigDecimal percentualAtividade = new BigDecimal(percentualAtividadeString.replace(',', '.'));
            BigDecimal somaDosPesosDosArcAtividade = new BigDecimal(CelulaRiscoControleMediator.get()
                    .somarPesoDosArcPorAtividades(matriz, celula.getAtividadePk(), versoesPerfilRiscoARCs));

            if (somaDosPesosDosArcAtividade.compareTo(BigDecimal.ZERO) != 0) {
                BigDecimal pesoArc = new BigDecimal(celula.getValorPeso() + celula.getValorPeso());
                BigDecimal multiplica = pesoArc.multiply(percentualAtividade);
                BigDecimal percentualArc = multiplica.divide(somaDosPesosDosArcAtividade, MathContext.DECIMAL32);
                Metodologia metodologia = metodologiaMediator.buscarMetodologiaPorPK(celula.getMetodologiaPk());
                BigDecimal percentualParticipacaoArc =
                        TipoGrupoEnum.RISCO.equals(arcVO.getTipo())
                                ? metodologia.getParametrosFatorRelevancia().get(0).getValorAlfa()
                                : metodologia.getParametrosFatorRelevancia().get(0).getValorBeta();
                percentualArc = percentualArc.multiply(percentualParticipacaoArc);
                return percentualArc.divide(new BigDecimal(100), MathContext.DECIMAL32);
            }
        }
        return null;
    }

    @Transactional(readOnly = true)
    private String getPercentualAtividadeVO(Matriz matriload, CelulaRiscoControleVO celula) {
        String percentualAtividadeString = null;
        if (celula.getTipoAtividade() == null) {
            percentualAtividadeString = percentualDasAtividadesCorporativaVO(matriload, celula, false);
        } else {
            if (celula.getUnidadePk() == null) {
                percentualAtividadeString = percentualDasAtividadesDaMatrizVO(matriload, celula, false);
            } else {
                percentualAtividadeString = percentualDasAtividadesDeNegocioVO(matriload, celula, false);
            }
        }
        if (percentualAtividadeString != null) {
            return percentualAtividadeString.substring(0, percentualAtividadeString.length() - 1);
        }
        return null;
    }

    @Transactional(readOnly = true)
    public String notaCalculadaVO(Matriz matriz, List<CelulaRiscoControleVO> celula,
            List<VersaoPerfilRisco> versoesPerfilRiscoARCs, boolean notaVigente, PerfilAcessoEnum perfilTela,
            PerfilRisco perfilRisco) {
        return notaCalculadaVO(matriz, celula, versoesPerfilRiscoARCs, notaVigente, perfilTela, perfilRisco, false);
    }

    @Transactional(readOnly = true)
    public String notaCalculadaVO(Matriz matriz, List<CelulaRiscoControleVO> celula,
            List<VersaoPerfilRisco> versoesPerfilRiscoARCs, boolean notaVigente, PerfilAcessoEnum perfilTela,
            PerfilRisco perfilRisco, boolean isAtaCorec) {
        if (matriz == null) {
            return null;
        } else {
            BigDecimal somaPercentual = new BigDecimal(Constantes.ZERO);
            BigDecimal somatorioNotaPercentual = new BigDecimal(Constantes.ZERO);
            boolean possuiNota = false;
            for (CelulaRiscoControleVO cel : celula) {
                if (cel.getAtividadePk() != null) {
                    ArcNotasVO arcRiscoVO = avaliacaoRiscoControleMediator.consultarNotasArc(cel.getArcRiscoPk());
                    ArcNotasVO arcControleVO = avaliacaoRiscoControleMediator.consultarNotasArc(cel.getArcControlePk());

                    BigDecimal percentualArcRisco = percentualARCVO(matriz, versoesPerfilRiscoARCs, cel, arcRiscoVO);
                    BigDecimal percentualArcControle =
                            percentualARCVO(matriz, versoesPerfilRiscoARCs, cel, arcControleVO);

                    if (notaVigente) {
                        String notaRisco = avaliacaoRiscoControleMediator.notaArc(arcRiscoVO, matriz.getCiclo(),
                                perfilTela, perfilRisco, isAtaCorec);
                        if (!Constantes.ASTERISCO_A.equals(notaRisco)) {
                            BigDecimal notaArc = new BigDecimal(notaRisco.replace(',', '.'));
                            somaPercentual = somaPercentual.add(percentualArcRisco);
                            somatorioNotaPercentual = somatorioNotaPercentual.add(notaArc.multiply(percentualArcRisco));
                            possuiNota = true;
                        }
                        String notaControle = avaliacaoRiscoControleMediator.notaArc(arcControleVO, matriz.getCiclo(),
                                perfilTela, perfilRisco, isAtaCorec);
                        if (!Constantes.ASTERISCO_A.equals(notaControle)) {
                            BigDecimal notaArc = new BigDecimal(notaControle.replace(',', '.'));
                            somaPercentual = somaPercentual.add(percentualArcControle);
                            somatorioNotaPercentual =
                                    somatorioNotaPercentual.add(notaArc.multiply(percentualArcControle));
                            possuiNota = true;
                        }
                    } else {
                        if (!Constantes.ASTERISCO_A
                                .equals(avaliacaoRiscoControleMediator.getNotaEmAnaliseDescricaoValor(arcRiscoVO))) {
                            BigDecimal notaArc = new BigDecimal(avaliacaoRiscoControleMediator
                                    .getNotaEmAnaliseDescricaoValor(arcRiscoVO).replace(',', '.'));
                            somaPercentual = somaPercentual.add(percentualArcRisco);
                            somatorioNotaPercentual = somatorioNotaPercentual.add(notaArc.multiply(percentualArcRisco));
                            possuiNota = true;
                        }
                        if (!Constantes.ASTERISCO_A
                                .equals(avaliacaoRiscoControleMediator.getNotaEmAnaliseDescricaoValor(arcControleVO))) {
                            BigDecimal notaArc = new BigDecimal(avaliacaoRiscoControleMediator
                                    .getNotaEmAnaliseDescricaoValor(arcControleVO).replace(',', '.'));
                            somaPercentual = somaPercentual.add(percentualArcControle);
                            somatorioNotaPercentual =
                                    somatorioNotaPercentual.add(notaArc.multiply(percentualArcControle));
                            possuiNota = true;
                        }
                    }
                }
            }
            return retonarResultadoMediaResidual(somaPercentual, somatorioNotaPercentual, possuiNota);
        }
    }

}
