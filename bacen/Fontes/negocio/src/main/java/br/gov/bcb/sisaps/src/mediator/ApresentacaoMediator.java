package br.gov.bcb.sisaps.src.mediator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.src.dao.ApresentacaoDao;
import br.gov.bcb.sisaps.src.dominio.Apresentacao;
import br.gov.bcb.sisaps.src.dominio.Atividade;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.CelulaRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.Metodologia;
import br.gov.bcb.sisaps.src.dominio.ParametroGrupoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.ParametroNota;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.SinteseDeRisco;
import br.gov.bcb.sisaps.src.dominio.SituacaoES;
import br.gov.bcb.sisaps.src.dominio.VersaoPerfilRisco;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.NotaAjustadaAEF;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroNotaAQT;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoCicloEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoGrupoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoObjetoVersionadorEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoParametroGrupoRiscoControleEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoUnidadeAtividadeEnum;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.NotaAjustadaAEFMediator;
import br.gov.bcb.sisaps.src.vo.ApresentacaoVO;
import br.gov.bcb.sisaps.src.vo.ApresentacaoVO.DadosCicloVO;
import br.gov.bcb.sisaps.src.vo.LinhaAtividadeVO;
import br.gov.bcb.sisaps.src.vo.RiscoVO;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;

@Service
@Scope("singleton")
public class ApresentacaoMediator {

    @Autowired
    private ApresentacaoDao apresentacaoDao;

    // Mediators
    @Autowired
    private PerfilRiscoMediator perfilRiscoMediator;
    @Autowired
    private CicloMediator cicloMediator;
    @Autowired
    private LinhaMatrizMediator linhaMatrizMediator;
    @Autowired
    private VersaoPerfilRiscoMediator versaoPerfilRiscoMediator;
    @Autowired
    private CelulaRiscoControleMediator celulaRiscoControleMediator;
    @Autowired
    private MatrizCicloMediator matrizCicloMediator;
    @Autowired
    private SinteseDeRiscoMediator sinteseDeRiscoMediator;
    @Autowired
    private ParametroNotaMediator parametroNotaMediator;
    @Autowired
    private AnaliseQuantitativaAQTMediator analiseQuantitativaAQTMediator;
    @Autowired
    private LinhaAtividadeVOMediator linhaAtividadeVOMediator;
    @Autowired
    private PerspectivaESMediator perspectivaESMediator;
    @Autowired
    private ParametroGrupoRiscoControleMediator parametroGrupoRiscoControleMediator;

    public static ApresentacaoMediator get() {
        return SpringUtils.get().getBean(ApresentacaoMediator.class);
    }

    // Retorna uma apresentação pela pk.
    @Transactional(readOnly = true)
    public ApresentacaoVO consultar(Integer apresentacaoPk) {
        return buscarPk(apresentacaoPk).toVO();
    }

    // Cria uma nova apresentação.
    @Transactional
    public Apresentacao criar(Ciclo ciclo) {
        // Declarações
        Apresentacao apresentacao = apresentacaoDao.buscarPorCiclo(ciclo.getPk());

        if (apresentacao == null) {
            // Nova apresentação.
            apresentacao = new Apresentacao();
            apresentacao.setCiclo(ciclo);
            // Salva a apresentação.
            apresentacaoDao.save(apresentacao);
            apresentacaoDao.flush();
        }

        return apresentacao;
    }

    @Transactional(readOnly = true)
    public Apresentacao buscarPk(Integer pk) {
        return apresentacaoDao.load(pk);
    }

    // Monta todos os dados da apresentação.
    @Transactional(readOnly = true)
    public ApresentacaoVO montar(PerfilRisco perfilRisco, PerfilAcessoEnum perfilMenu) {
        // Declarações
        ApresentacaoVO apresentacaoVO;
        Matriz matriz;
        List<VersaoPerfilRisco> versoesPerfilRiscoCelulas;
        List<Atividade> listaAtividade;
        List<CelulaRiscoControle> listaChoices;
        List<ParametroGrupoRiscoControle> listaGrupo;
        Ciclo cicloAnterior;
        Matriz matrizAnterior;
        
        // Recupera o VO.
        apresentacaoVO = consultar(perfilRisco.getCiclo().getApresentacao().getPk());

        // Inicializações
        listaAtividade = new LinkedList<Atividade>();

        // Recupera a matriz.
        matriz = perfilRiscoMediator.getMatrizAtualPerfilRisco(perfilRisco);

        // Recupera as versões.
        versoesPerfilRiscoCelulas =
                versaoPerfilRiscoMediator.buscarVersoesPerfilRisco(perfilRisco.getPk(),
                        TipoObjetoVersionadorEnum.CELULA_RISCO_CONTROLE);

        // Recupera a lista de atividades.
        linhaMatrizMediator.montarListaVOAtividadeNegocio(matriz, listaAtividade);
        linhaMatrizMediator.consultar(matriz, false, listaAtividade);
        linhaMatrizMediator.consultar(matriz, true, listaAtividade);

        // Recupera a lista de escolhas.
        listaChoices = celulaRiscoControleMediator.buscarParametroDaMatriz(listaAtividade, versoesPerfilRiscoCelulas);

        // Recupera os grupos.
        List<Integer> idsParametrosGrupoRiscoMatriz =
                parametroGrupoRiscoControleMediator.buscarIdsGruposRiscoDaMatriz(matriz);
        listaGrupo =
                parametroGrupoRiscoControleMediator
                        .buscarGruposRiscoDaMatrizESinteseObrigatoria(idsParametrosGrupoRiscoMatriz);

        AvaliacaoRiscoControle arcExterno = null;
        
        if (matriz != null && matriz.getPercentualGovernancaCorporativoInt() > 0) {
            for (ParametroGrupoRiscoControle prcGrupoRiscoControle : matriz.getCiclo().getMetodologia()
                    .getParametrosGrupoRiscoControle()) {
                if (TipoParametroGrupoRiscoControleEnum.EXTERNO.equals(prcGrupoRiscoControle.getTipoGrupo())
                        && !listaGrupo.contains(prcGrupoRiscoControle)) {
                    listaGrupo.add(prcGrupoRiscoControle);
                    idsParametrosGrupoRiscoMatriz.add(prcGrupoRiscoControle.getPk());
                    break;
                }
            }
            
            arcExterno = AvaliacaoRiscoControleMediator.get().buscarArcExternoPorPerfilRisco(perfilRisco.getPk());
        }

        // Recupera a situação da ES.
        recuperarSituacao(apresentacaoVO, perfilRisco);

        // Calcula a nota quantitativa.
        apresentacaoVO.getNotaQuantitativa()[0] =
                analiseQuantitativaAQTMediator.getNotaCalculadaAEF(perfilRisco, perfilMenu, false);
        refinarNota(apresentacaoVO.getNotaQuantitativa(), perfilRisco.getCiclo().getMetodologia());
        notaAjustada(perfilRisco, perfilMenu, apresentacaoVO);

        // Calcula a nota qualitativa.
        apresentacaoVO.getNotaQualitativa()[0] =
                matrizCicloMediator.notaCalculadaFinal(
                        matriz, listaChoices, versoesPerfilRiscoCelulas, true, perfilMenu, perfilRisco, arcExterno);
        refinarNota(apresentacaoVO.getNotaQualitativa(), perfilRisco.getCiclo().getMetodologia());

        List<String> notaAjustada = NotaMatrizMediator.get().notaAjustada(matriz, perfilRisco, perfilMenu);
        String corec = "";

        if (notaAjustada.size() > 0) {
            corec =
                    notaAjustada.get(1) == null ? "" : notaAjustada.get(1).equals(Constantes.COREC) ? notaAjustada
                            .get(1) : "";
            apresentacaoVO.getNotaQualitativa()[3] =
                    StringUtils.isBlank(notaAjustada.get(0)) ? "" : notaAjustada.get(0) + corec;

            apresentacaoVO.getNotaQualitativa()[4] =
                    notaAjustada.get(1) == null || notaAjustada.get(1).equals(Constantes.COREC) ? "" : notaAjustada
                            .get(1);
            apresentacaoVO.getNotaQualitativa()[4].toString();
        }

        // Monta os riscos.
        apresentacaoVO.setRiscosVO(montarRiscos(perfilRisco, matriz, listaGrupo, listaChoices,
                versoesPerfilRiscoCelulas, perfilMenu));

        // Monta os dados do ciclo.
        apresentacaoVO.setDadosCicloVO(montarDadosCiclo(perfilRisco, matriz, perfilMenu));

        // Recupera o ciclo anterior ao especificado.
        cicloAnterior = getCicloAnterior(perfilRisco.getCiclo());

        // Valida o ciclo anterior.
        if (cicloAnterior != null) {
            // Recupera a matriz do ciclo anterior.
            matrizAnterior = cicloAnterior.getMatriz();

            // Monta os dados do ciclo anterior.
            apresentacaoVO
                    .setDadosCicloAnteriorVO(montarDadosCiclo(
                            PerfilRiscoMediator.get().obterPerfilRiscoAtual(cicloAnterior.getPk()), matrizAnterior,
                            perfilMenu));
        }

        return apresentacaoVO;
    }

    private void notaAjustada(PerfilRisco perfilRisco, PerfilAcessoEnum perfilMenu, ApresentacaoVO apresentacaoVO) {
        String notaAjustadaQuantitativa = null;
        NotaAjustadaAEF notaAjustadaAEF =
                NotaAjustadaAEFMediator.get().buscarNotaAjustadaAEF(perfilRisco.getCiclo(), perfilRisco);
        ParametroNotaAQT notaAjustadaCorec =
                AjusteCorecMediator.get().notaAjustadaCorecAEF(perfilRisco, perfilRisco.getCiclo(), perfilMenu);
        if (notaAjustadaCorec != null) {
            notaAjustadaQuantitativa = (notaAjustadaCorec.getIsNotaElemento() == null ? 
                    notaAjustadaCorec.getValorString() : notaAjustadaCorec.getDescricao()) + " (Corec)";
        } else {
            if (notaAjustadaAEF == null || notaAjustadaAEF.getParamentroNotaAQT() == null) {
                notaAjustadaQuantitativa = "";
            } else {
                ParametroNotaAQT paramentroNotaAQT = notaAjustadaAEF.getParamentroNotaAQT();
                notaAjustadaQuantitativa = paramentroNotaAQT.getIsNotaElemento() == null ? 
                        paramentroNotaAQT.getValorString() : paramentroNotaAQT.getDescricao();
            }
        }
        apresentacaoVO.getNotaQuantitativa()[3] = notaAjustadaQuantitativa;
    }

    // Recupera a situação da ES.
    private void recuperarSituacao(ApresentacaoVO apresentacaoVO, PerfilRisco perfilRisco) {
        // Declarações
        SituacaoES situacaoES;

        // Inicializações
        situacaoES = PerfilRiscoMediator.get().getSituacaoESPerfilRisco(perfilRisco);

        // Valida a situação.
        if (situacaoES == null)
            return;

        // Guarda os dados da situação.
        apresentacaoVO.setSituacaoNormalidade(situacaoES.getParametroSituacao().getNormalidade());
        apresentacaoVO.setSituacaoNome(situacaoES.getParametroSituacao().getNome());
        apresentacaoVO.setSituacaoJustificativa(situacaoES.getDescricao());
    }

    // Monta as unidades e atividades de um ciclo.
    private DadosCicloVO montarDadosCiclo(PerfilRisco perfilRisco, Matriz matriz, PerfilAcessoEnum perfilMenu) {
        // Declarações
        List<LinhaAtividadeVO> listaLinhaAtividade;
        DadosCicloVO dadosCicloVO;

        // Inicializações
        dadosCicloVO = new DadosCicloVO();
        listaLinhaAtividade = new LinkedList<LinhaAtividadeVO>();

        // Recupera a perspectiva do ciclo.
        dadosCicloVO.setPerspectivaES(perspectivaESMediator.getUltimaPerspectiva(perfilRisco, false, perfilMenu));

        // Valida a matriz.
        if (matriz != null) {
            // Recupera os percentuais de negócio e corporativo.
            dadosCicloVO.setPercentualNegocio(matriz.getPercentualNegocio());
            dadosCicloVO.setPercentualCorporativo(matriz.getPercentualCorporativo());

            // Recupera as atividades de negócio e corporativo.
            listaLinhaAtividade.addAll(linhaAtividadeVOMediator.consultarLinhasAtividadeVOUnidades(matriz,
                    TipoUnidadeAtividadeEnum.NEGOCIO));
            listaLinhaAtividade.addAll(linhaAtividadeVOMediator.consultarLinhasAtividadeVOSemUnidades(matriz));
            listaLinhaAtividade.addAll(linhaAtividadeVOMediator.consultarLinhasAtividadeVOUnidades(matriz,
                    TipoUnidadeAtividadeEnum.CORPORATIVO));
        } else {
            // Recupera os percentuais de negócio e corporativo.
            dadosCicloVO.setPercentualNegocio("");
            dadosCicloVO.setPercentualCorporativo("");
        }

        dadosCicloVO.setAtividades(listaLinhaAtividade);

        return dadosCicloVO;
    }

    // Recupera o ciclo anterior ao especificado.
    private Ciclo getCicloAnterior(Ciclo ciclo) {
        // Declarações
        List<Ciclo> ciclos;
        Ciclo cicloAnterior;

        // Inicializações
        cicloAnterior = null;

        // Recupera os ciclos da ES.
        ciclos =
                cicloMediator.consultarCiclosEntidadeSupervisionavel(ciclo.getEntidadeSupervisionavel()
                        .getConglomeradoOuCnpj(), false);

        // Encontra o ciclo mais recente anterior ao selecionado.
        for (Ciclo oCiclo : ciclos) {
            // Valida o estado do ciclo.
            if (oCiclo.getEstadoCiclo().getEstado() != EstadoCicloEnum.POS_COREC
                    && oCiclo.getEstadoCiclo().getEstado() != EstadoCicloEnum.ENCERRADO) {
                continue;
            }

            // Verifica se o ciclo é anterior ao atual.
            if (oCiclo.getDataInicio() == null || !oCiclo.getDataInicio().before(ciclo.getDataInicio())) {
                continue;
            }

            // Verifica se o ciclo é mais antigo que o atual.
            if (cicloAnterior != null && oCiclo.getDataInicio().before(cicloAnterior.getDataInicio())) {
                continue;
            }

            // Ciclo encontrado!
            cicloAnterior = oCiclo;
        }

        return cicloAnterior;
    }

    // Monta um risco.
    private List<RiscoVO> montarRiscos(PerfilRisco perfilRisco, Matriz matriz,
            List<ParametroGrupoRiscoControle> listaGrupo, List<CelulaRiscoControle> listaChoices,
            List<VersaoPerfilRisco> versoesPerfilRiscoCelulas, PerfilAcessoEnum perfilMenu) {
        // Declarações
        List<RiscoVO> riscosVO;
        List<VersaoPerfilRisco> versoesSintesesMatriz;
        List<CelulaRiscoControle> listaCelulaGrupo;
        SinteseDeRisco sinteseDeRisco;
        RiscoVO riscoVO;
        String valor;

        // Inicializações
        riscosVO = new ArrayList<RiscoVO>(listaGrupo.size());
        versoesSintesesMatriz =
                versaoPerfilRiscoMediator.buscarVersoesPerfilRisco(perfilRisco.getPk(),
                        TipoObjetoVersionadorEnum.SINTESE_RISCO);

        // Monta os riscos.
        for (ParametroGrupoRiscoControle grupo : listaGrupo) {
            // Novo risco.
            riscoVO = new RiscoVO();
            riscosVO.add(riscoVO);
            riscoVO.setArcExterno(TipoParametroGrupoRiscoControleEnum.EXTERNO.equals(grupo.getTipoGrupo()));


            // Síntese
            sinteseDeRisco =
                    sinteseDeRiscoMediator.getUltimaSinteseParametroGrupoRisco(grupo, perfilRisco.getCiclo(),
                            versoesSintesesMatriz);
            if (sinteseDeRisco == null || sinteseDeRisco.getDescricaoSintese() == null) {
                valor = "";
            } else {
                valor = sinteseDeRisco.getDescricaoSintese();
            }
            riscoVO.setSintese(valor);

            if (riscoVO.isArcExterno()) {
                riscoVO.setNome(grupo.getNomeAbreviado());
                AvaliacaoRiscoControle arcExterno =
                        AvaliacaoRiscoControleMediator.get().buscarArcExternoPorPerfilRisco(perfilRisco.getPk());
                String notaRisco = arcExterno == null ? 
                        "" : AvaliacaoRiscoControleMediator.get().notaArc(
                                arcExterno, perfilRisco.getCiclo(), perfilMenu, perfilRisco);
                notaRisco = formatarNota(notaRisco);
                if (perfilRisco.getCiclo().getMetodologia().getIsCalculoMedia() == null 
                        || !perfilRisco.getCiclo().getMetodologia().getIsCalculoMedia().booleanValue()) {
                    notaRisco = SisapsUtil.adicionar2CasasDecimais(notaRisco);
                }
                riscoVO.setNotaRisco(notaRisco);
            } else {
                // Nome
                riscoVO.setNome(grupo.getNomeRisco());

                if (sinteseDeRisco != null && sinteseDeRisco.getRisco() != null) {
                    riscoVO.setNotaRisco(sinteseDeRisco.getRisco().getDescricao());
                    riscoVO.setEObrigatorio(true);
                } else {

                    listaCelulaGrupo = linhaMatrizMediator.listaCelulaGrupo(listaChoices, grupo, matriz);

                    // Nota de risco.
                    valor =
                            LinhaMatrizMediator.get().obterNota(
                                    matrizCicloMediator.notaResidualParametroGrupo(matriz, grupo,
                                            versoesPerfilRiscoCelulas, true, TipoGrupoEnum.RISCO, listaCelulaGrupo,
                                            perfilMenu, perfilRisco));
                    riscoVO.setNotaRisco(formatarNota(valor));

                    // Nota de controle.
                    valor =
                            LinhaMatrizMediator.get().obterNota(
                                    matrizCicloMediator.notaResidualParametroGrupo(matriz, grupo,
                                            versoesPerfilRiscoCelulas, true, TipoGrupoEnum.CONTROLE, listaCelulaGrupo,
                                            perfilMenu, perfilRisco));
                    riscoVO.setNotaControle(formatarNota(valor));

                    // Nota residual.
                    valor =
                            matrizCicloMediator.mediaResidualParametroGrupo(matriz, grupo, versoesPerfilRiscoCelulas,
                                    true, listaCelulaGrupo, perfilMenu, perfilRisco);
                    riscoVO.setNotaResidual(formatarNota(valor));
                    // Conceito residual.
                    if (valor.isEmpty() || valor.equals(Constantes.ASTERISCO_A)) {
                        riscoVO.setConceitoResidual("");
                    } else {
                        // Conceito residual.
                        ParametroNota notaRefinada =
                                parametroNotaMediator.buscarPorMetodologiaENota(
                                        perfilRisco.getCiclo().getMetodologia(), new BigDecimal(valor), true);
                        riscoVO.setConceitoResidual(notaRefinada.getDescricao());
                    }
                }
            }


        }

        return riscosVO;
    }

    // Formata uma nota.
    private String formatarNota(String nota) {
        if (nota == null || nota.isEmpty()) {
            nota = Constantes.ASTERISCO_A;
        }
        nota = nota.replace('.', ',');

        return nota;
    }

    // Refina uma nota.
    private void refinarNota(String[] nota, Metodologia metodologia) {
        // Declarações
        ParametroNota notaRefinada;

        // Valida a nota calculada.
        if (nota[0] == null || nota[0].equals("") || nota[0].equals(Constantes.ASTERISCO_A)) {
            // Nota não especificada.
            nota[1] = Constantes.ASTERISCO_A;
            nota[2] = "FFFFFF";

        } else {
            // Recupera a nota refinada.
            notaRefinada =
                    parametroNotaMediator.buscarPorMetodologiaENota(metodologia,
                            new BigDecimal(nota[0].replace(',', '.')), false);

            // Extrai os dados da nota.
            nota[1] = metodologia.getIsMetodologiaNova() ? notaRefinada.getDescricao() : notaRefinada.getValorString();
            nota[2] = notaRefinada.getCor();
        }

        // Formata a nota calculada.
        nota[0] = formatarNota(nota[0]);
    }
}
