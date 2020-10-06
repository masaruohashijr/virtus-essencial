package br.gov.bcb.sisaps.src.mediator.analisequantitativa;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.sisaps.src.dao.analisequantitativa.OutraInformacaoAnaliseQuantitativaETLDAO;
import br.gov.bcb.sisaps.src.dao.analisequantitativa.OutraInformacaoQuadroPosicaoFinanceiraDAO;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.DataBaseES;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.LayoutOutraInformacaoAnaliseQuantitativa;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.OutraInformacaoAnaliseQuantitativa;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.OutraInformacaoAnaliseQuantitativaETL;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.OutraInformacaoQuadroPosicaoFinanceira;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.QuadroPosicaoFinanceira;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoEdicaoInformacaoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoInformacaoEnum;
import br.gov.bcb.sisaps.src.vo.analisequantitativa.OutraInformacaoVO;
import br.gov.bcb.sisaps.src.vo.analisequantitativa.QuadroPosicaoFinanceiraVO;
import br.gov.bcb.sisaps.util.spring.SpringUtilsExtended;

@Service
public class OutraInformacaoQuadroPosicaoFinanceiraMediator {

    private static final int QTD_3_SEMESTRES_ANTERIORES = 3;
    @Autowired
    private OutraInformacaoQuadroPosicaoFinanceiraDAO outraInformacaoQuadroPosicaoFinanceiraDAO;
    @Autowired
    private OutraInformacaoAnaliseQuantitativaETLDAO outraInformacaoAnaliseQuantitativaETLDAO;

    public static OutraInformacaoQuadroPosicaoFinanceiraMediator get() {
        return SpringUtilsExtended.get().getBean(OutraInformacaoQuadroPosicaoFinanceiraMediator.class);
    }

    public List<OutraInformacaoQuadroPosicaoFinanceira> buscarOutraInformacaoQuadroPorTipo(Integer pkQuadro,
            TipoInformacaoEnum tipoInformacao) {
        return outraInformacaoQuadroPosicaoFinanceiraDAO.buscarOutraInformacaoQuadroPorTipo(pkQuadro, tipoInformacao,
                true);
    }

    @Transactional
    public void incluirPatrimonios(PerfilRisco perfilRisco, DataBaseES dataBaseES, String cnpjES,
            QuadroPosicaoFinanceira novoQuadro) {
        List<LayoutOutraInformacaoAnaliseQuantitativa> layoutPatrimonios =
                LayoutOutraInformacaoAnaliseQuantitativaMediator.get().obterLayout(dataBaseES,
                        TipoInformacaoEnum.PATRIMONIO);
        incluirOutrasInformacoes(perfilRisco, dataBaseES, cnpjES, novoQuadro, layoutPatrimonios,
                TipoInformacaoEnum.PATRIMONIO);
    }

    @Transactional
    public void incluirIndices(PerfilRisco perfilRisco, DataBaseES dataBaseES, String cnpjES,
            QuadroPosicaoFinanceira novoQuadro) {
        List<LayoutOutraInformacaoAnaliseQuantitativa> layoutIndices =
                LayoutOutraInformacaoAnaliseQuantitativaMediator.get().obterLayout(dataBaseES,
                        TipoInformacaoEnum.INDICE);
        incluirOutrasInformacoes(perfilRisco, dataBaseES, cnpjES, novoQuadro, layoutIndices, TipoInformacaoEnum.INDICE);
    }

    @Transactional
    public void incluirResultados(PerfilRisco perfilRisco, DataBaseES dataBaseES, String cnpjES,
            QuadroPosicaoFinanceira novoQuadro) {
        List<LayoutOutraInformacaoAnaliseQuantitativa> layoutResultados =
                LayoutOutraInformacaoAnaliseQuantitativaMediator.get().obterLayout(dataBaseES,
                        TipoInformacaoEnum.RESULTADO);
        incluirOutrasInformacoes(perfilRisco, dataBaseES, cnpjES, novoQuadro, layoutResultados,
                TipoInformacaoEnum.RESULTADO);
    }

    private void incluirOutrasInformacoes(PerfilRisco perfilRisco, DataBaseES dataBaseES, String cnpjES,
            QuadroPosicaoFinanceira novoQuadro, List<LayoutOutraInformacaoAnaliseQuantitativa> layouts,
            TipoInformacaoEnum tipoInformacao) {
        for (LayoutOutraInformacaoAnaliseQuantitativa layout : layouts) {
            OutraInformacaoAnaliseQuantitativa outraInformacaoAnaliseQuantitativa =
                    layout.getOutraInformacaoAnaliseQuantitativa();
            if (tipoInformacao.equals(TipoInformacaoEnum.RESULTADO)) {
                incluirOutrasInformacoesResultado(dataBaseES, cnpjES, novoQuadro, layout,
                        outraInformacaoAnaliseQuantitativa);
            } else {
                OutraInformacaoAnaliseQuantitativaETL extracao =
                        outraInformacaoAnaliseQuantitativaETLDAO.extrair(dataBaseES, cnpjES,
                                outraInformacaoAnaliseQuantitativa);
                if (extracao == null) {
                    OutraInformacaoQuadroPosicaoFinanceira outraInfoQuadro =
                            criarOutraInfo(dataBaseES, novoQuadro, layout, null);

                    obterValorQuadroVigente(perfilRisco, tipoInformacao, layout, outraInfoQuadro);
                    outraInformacaoQuadroPosicaoFinanceiraDAO.save(outraInfoQuadro);
                    outraInformacaoQuadroPosicaoFinanceiraDAO.flush();
                } else {
                    OutraInformacaoQuadroPosicaoFinanceira outraInfoQuadro =
                            criarOutraInfo(dataBaseES, novoQuadro, layout, extracao);
                    outraInformacaoQuadroPosicaoFinanceiraDAO.save(outraInfoQuadro);
                    outraInformacaoQuadroPosicaoFinanceiraDAO.flush();
                }
            }

        }
    }

    private void obterValorQuadroVigente(PerfilRisco perfilRisco, TipoInformacaoEnum tipoInformacao,
            LayoutOutraInformacaoAnaliseQuantitativa layout, OutraInformacaoQuadroPosicaoFinanceira outraInfoQuadro) {
        QuadroPosicaoFinanceira quadroVigente =
                QuadroPosicaoFinanceiraMediator.get().obterUltimaVersaoQuadroVigente(perfilRisco);
        if (quadroVigente != null) {
            List<OutraInformacaoQuadroPosicaoFinanceira> outraInfoQuadroVigente =
                    buscarPorQuadroELayout(quadroVigente, layout, true);
            if (CollectionUtils.isNotEmpty(outraInfoQuadroVigente)
                    && outraInfoQuadroVigente.get(0).getValorEditado() != null) {
                if (!tipoInformacao.equals(TipoInformacaoEnum.INDICE)) {
                    outraInfoQuadro.setValorEditado(outraInfoQuadroVigente.get(0).getValorEditado());
                }
            }
        }
    }

    private void incluirOutrasInformacoesResultado(DataBaseES dataBaseES, String cnpjES,
            QuadroPosicaoFinanceira novoQuadro, LayoutOutraInformacaoAnaliseQuantitativa layout,
            OutraInformacaoAnaliseQuantitativa outraInformacaoAnaliseQuantitativa) {
        List<Integer> codigoSemestresAnteriores = obterCodigosSemestresAnteriores(dataBaseES.getCodigoDataBase());

        List<OutraInformacaoAnaliseQuantitativaETL> extracoes =
                outraInformacaoAnaliseQuantitativaETLDAO.buscarValoresSemestraisPorDataBaseExtracao(dataBaseES, cnpjES,
                        outraInformacaoAnaliseQuantitativa, codigoSemestresAnteriores);

        for (Integer codigoSemestral : codigoSemestresAnteriores) {
            OutraInformacaoQuadroPosicaoFinanceira resultado = criarOutraInfo(dataBaseES, novoQuadro, layout, null);
            resultado.setPeriodo(codigoSemestral);
            if (CollectionUtils.isNotEmpty(extracoes)) {
                for (OutraInformacaoAnaliseQuantitativaETL extracao : extracoes) {
                    if (extracao.getDataBaseES().getCodigoDataBase().equals(resultado.getPeriodo())
                            && extracao
                                    .getOutraInformacaoAnaliseQuantitativa()
                                    .getPk()
                                    .equals(resultado.getLayoutOutraInformacaoAnaliseQuantitativa()
                                            .getOutraInformacaoAnaliseQuantitativa().getPk())) {
                        resultado.setValor(extracao.getValor());
                    }
                }
            }
            outraInformacaoQuadroPosicaoFinanceiraDAO.save(resultado);
            outraInformacaoQuadroPosicaoFinanceiraDAO.flush();
        }
    }

    private List<Integer> obterCodigosSemestresAnteriores(Integer codigoDataBase) {
        List<Integer> codigosDataBase = new ArrayList<Integer>();
        codigosDataBase.add(codigoDataBase);
        Integer base = codigoDataBase;
        for (int i = 0; i < QTD_3_SEMESTRES_ANTERIORES; i++) {
            String identificador = String.valueOf(base);
            int mes = Integer.valueOf(identificador.substring(4));
            int ano = Integer.valueOf(identificador.substring(0, 4));
            if (mes >= 1 && mes <= 6) {
                base = Integer.valueOf(ano - 1 + "12");
            } else {
                base = Integer.valueOf(ano + "06");
            }
            codigosDataBase.add(base);
        }

        return codigosDataBase;
    }

    private OutraInformacaoQuadroPosicaoFinanceira criarOutraInfo(DataBaseES dataBaseES,
            QuadroPosicaoFinanceira novoQuadro, LayoutOutraInformacaoAnaliseQuantitativa layout,
            OutraInformacaoAnaliseQuantitativaETL extracao) {
        OutraInformacaoQuadroPosicaoFinanceira outraInfoQuadro = new OutraInformacaoQuadroPosicaoFinanceira();
        outraInfoQuadro.setQuadroPosicaoFinanceira(novoQuadro);
        outraInfoQuadro.setLayoutOutraInformacaoAnaliseQuantitativa(layout);
        outraInfoQuadro.setPeriodo(extracao == null ? dataBaseES.getCodigoDataBase() : extracao.getDataBaseES()
                .getCodigoDataBase());
        outraInfoQuadro.setValor(extracao == null ? null : extracao.getValor());
        return outraInfoQuadro;
    }

    @Transactional
    public void incluirAjustesResultadosQuadroVigente(QuadroPosicaoFinanceira quadroVigente,
            QuadroPosicaoFinanceira quadroRascunho, DataBaseES dataBaseES) {
        boolean isPeriodoCompativel = isPeriodoCompativel(quadroVigente, quadroRascunho);
        if (isPeriodoCompativel) {
            List<LayoutOutraInformacaoAnaliseQuantitativa> layoutsQuadroVigente =
                    LayoutOutraInformacaoAnaliseQuantitativaMediator.get().obterLayoutsOutraInformacaoQuadro(
                            quadroVigente, TipoInformacaoEnum.RESULTADO);

            List<LayoutOutraInformacaoAnaliseQuantitativa> layoutsQuadroRascunho =
                    LayoutOutraInformacaoAnaliseQuantitativaMediator.get().obterLayoutsOutraInformacaoQuadro(
                            quadroRascunho, TipoInformacaoEnum.RESULTADO);

            for (LayoutOutraInformacaoAnaliseQuantitativa layoutRascunho : layoutsQuadroRascunho) {
                for (LayoutOutraInformacaoAnaliseQuantitativa layoutVigente : layoutsQuadroVigente) {
                    if (layoutRascunho.getOutraInformacaoAnaliseQuantitativa().getPk()
                            .equals(layoutVigente.getOutraInformacaoAnaliseQuantitativa().getPk())) {
                        List<OutraInformacaoQuadroPosicaoFinanceira> resultadosRascunho =
                                OutraInformacaoQuadroPosicaoFinanceiraMediator.get().buscarPorQuadroELayout(
                                        quadroRascunho, layoutRascunho, false);

                        List<OutraInformacaoQuadroPosicaoFinanceira> resultadosVigente =
                                OutraInformacaoQuadroPosicaoFinanceiraMediator.get().buscarPorQuadroELayout(
                                        quadroVigente, layoutVigente, false);

                        for (OutraInformacaoQuadroPosicaoFinanceira resultadoRascunho : resultadosRascunho) {
                            for (OutraInformacaoQuadroPosicaoFinanceira resultadoVigente : resultadosVigente) {
                                if (resultadoRascunho.getPeriodo().equals(resultadoVigente.getPeriodo())
                                        || (resultadosRascunho.get(0).getPk().equals(resultadoRascunho.getPk()) 
                                                && resultadosVigente.get(0).getPk().equals(resultadoVigente.getPk()))) {
                                    extrairValorEditado(resultadoRascunho, resultadoVigente);
                                }
                            }
                            outraInformacaoQuadroPosicaoFinanceiraDAO.update(resultadoRascunho);
                        }
                    }
                }
            }
        }
    }

    private boolean isPeriodoCompativel(QuadroPosicaoFinanceira quadroVigente, QuadroPosicaoFinanceira quadroRascunho) {
        List<OutraInformacaoQuadroPosicaoFinanceira> resultadosVigente =
                outraInformacaoQuadroPosicaoFinanceiraDAO.buscarOutraInformacaoQuadroPorTipo(quadroVigente.getPk(),
                        TipoInformacaoEnum.RESULTADO, false);
        List<OutraInformacaoQuadroPosicaoFinanceira> resultadosRascunho =
                outraInformacaoQuadroPosicaoFinanceiraDAO.buscarOutraInformacaoQuadroPorTipo(quadroRascunho.getPk(),
                        TipoInformacaoEnum.RESULTADO, false);
        boolean isPeriodoCompativel =
                CollectionUtils.isNotEmpty(resultadosRascunho) && CollectionUtils.isNotEmpty(resultadosVigente)
                        && resultadosRascunho.get(1).getPeriodo().equals(resultadosVigente.get(1).getPeriodo());
        return isPeriodoCompativel;
    }

    @Transactional
    public void incluirAjustesIndicesQuadroVigente(QuadroPosicaoFinanceira quadroVigente,
            QuadroPosicaoFinanceira quadroRascunho, PerfilRisco perfilRisco, DataBaseES dataBaseES) {
        List<OutraInformacaoQuadroPosicaoFinanceira> indicesVigentes =
                outraInformacaoQuadroPosicaoFinanceiraDAO.buscarOutraInformacaoQuadroPorTipo(quadroVigente.getPk(),
                        TipoInformacaoEnum.INDICE, true);
        List<OutraInformacaoQuadroPosicaoFinanceira> indicesRascunho =
                outraInformacaoQuadroPosicaoFinanceiraDAO.buscarOutraInformacaoQuadroPorTipo(quadroRascunho.getPk(),
                        TipoInformacaoEnum.INDICE, true);
        for (OutraInformacaoQuadroPosicaoFinanceira indiceRascunho : indicesRascunho) {
            OutraInformacaoAnaliseQuantitativaETL extracao =
                    outraInformacaoAnaliseQuantitativaETLDAO.extrair(dataBaseES, perfilRisco.getCiclo()
                            .getEntidadeSupervisionavel().getConglomeradoOuCnpj(), indiceRascunho
                            .getLayoutOutraInformacaoAnaliseQuantitativa().getOutraInformacaoAnaliseQuantitativa());
            if (extracao != null) {
                for (OutraInformacaoQuadroPosicaoFinanceira indiceVigente : indicesVigentes) {
                    if (indiceRascunho
                            .getLayoutOutraInformacaoAnaliseQuantitativa()
                            .getOutraInformacaoAnaliseQuantitativa()
                            .getNome()
                            .equals(indiceVigente.getLayoutOutraInformacaoAnaliseQuantitativa()
                                    .getOutraInformacaoAnaliseQuantitativa().getNome())) {
                        extrairValorEditado(indiceRascunho, indiceVigente);
                    }
                }
                outraInformacaoQuadroPosicaoFinanceiraDAO.update(indiceRascunho);
            }
        }
    }

    @Transactional
    public void incluirAjustesPatrimonioQuadroVigente(QuadroPosicaoFinanceira quadroVigente,
            QuadroPosicaoFinanceira quadroRascunho) {
        List<OutraInformacaoQuadroPosicaoFinanceira> patrimoniosVigentes =
                outraInformacaoQuadroPosicaoFinanceiraDAO.buscarOutraInformacaoQuadroPorTipo(quadroVigente.getPk(),
                        TipoInformacaoEnum.PATRIMONIO, true);
        List<OutraInformacaoQuadroPosicaoFinanceira> patrimoniosRascunho =
                outraInformacaoQuadroPosicaoFinanceiraDAO.buscarOutraInformacaoQuadroPorTipo(quadroRascunho.getPk(),
                        TipoInformacaoEnum.PATRIMONIO, true);

        for (OutraInformacaoQuadroPosicaoFinanceira patrimonioRascunho : patrimoniosRascunho) {
            for (OutraInformacaoQuadroPosicaoFinanceira patrimonioVigente : patrimoniosVigentes) {
                if (patrimonioRascunho
                        .getLayoutOutraInformacaoAnaliseQuantitativa()
                        .getOutraInformacaoAnaliseQuantitativa()
                        .getNome()
                        .equals(patrimonioVigente.getLayoutOutraInformacaoAnaliseQuantitativa()
                                .getOutraInformacaoAnaliseQuantitativa().getNome())) {
                    extrairValorEditado(patrimonioRascunho, patrimonioVigente);
                }
            }
            outraInformacaoQuadroPosicaoFinanceiraDAO.update(patrimonioRascunho);
        }
    }

    private void extrairValorEditado(OutraInformacaoQuadroPosicaoFinanceira outraInfoRascunho,
            OutraInformacaoQuadroPosicaoFinanceira outraInfoVigente) {
        TipoEdicaoInformacaoEnum tipoEdicaoVigente = 
                outraInfoVigente.getLayoutOutraInformacaoAnaliseQuantitativa().getTipoEdicaoInformacaoEnum();
        TipoEdicaoInformacaoEnum tipoEdicaoRascunho = 
                outraInfoRascunho.getLayoutOutraInformacaoAnaliseQuantitativa().getTipoEdicaoInformacaoEnum();
        if (tipoEdicaoVigente.equals(tipoEdicaoRascunho)) {
            outraInfoRascunho.setValorEditado(outraInfoVigente.getValorEditado());
        } else {
            BigDecimal valorEditado = null;
            
            if (outraInfoVigente.getValor() != null && outraInfoVigente.getValorEditado() != null) {
                if (tipoEdicaoVigente.equals(TipoEdicaoInformacaoEnum.AJUSTE)) {
                    valorEditado = outraInfoRascunho.getValor().subtract(outraInfoVigente.getValorEditado());
                } else {
                    valorEditado = outraInfoVigente.getValor().subtract(outraInfoVigente.getValorEditado());
                }
            } else if (outraInfoVigente.getValor() == null && outraInfoVigente.getValorEditado() != null) {
                valorEditado = outraInfoVigente.getValorEditado();
            }
            outraInfoRascunho.setValorEditado(valorEditado);
        }
    }

    @Transactional
    public void salvarAjustesOutrasInformacoes(QuadroPosicaoFinanceiraVO novoQuadroVO, TipoInformacaoEnum tipoInformacao) {
        List<OutraInformacaoVO> outrasInformacoes = null;
        switch (tipoInformacao) {
            case PATRIMONIO:
                outrasInformacoes = novoQuadroVO.getPatrimoniosNovo();
                break;
            case INDICE:
                outrasInformacoes = novoQuadroVO.getIndicesNovo();
                break;
            case RESULTADO:
                outrasInformacoes = new ArrayList<OutraInformacaoVO>();
                for (String nomeResultado : novoQuadroVO.getNomesResultados()) {
                    outrasInformacoes.addAll(novoQuadroVO.getResultadosPorNome().get(nomeResultado));
                }
                break;
            default:
                break;
        }
        if (outrasInformacoes != null) {
            for (OutraInformacaoVO outraInformacaoVO : outrasInformacoes) {
                OutraInformacaoQuadroPosicaoFinanceira patrimonio =
                        outraInformacaoQuadroPosicaoFinanceiraDAO.buscarPorPk(outraInformacaoVO.getPk());
                patrimonio.setValorEditado(outraInformacaoVO.getValorEditado());
                outraInformacaoQuadroPosicaoFinanceiraDAO.update(patrimonio);
            }
        }
    }

    @Transactional(readOnly = true)
    public List<OutraInformacaoQuadroPosicaoFinanceira> buscarPorQuadroELayout(QuadroPosicaoFinanceira quadro,
            LayoutOutraInformacaoAnaliseQuantitativa layout, boolean ordemCrescentePeriodo) {
        return outraInformacaoQuadroPosicaoFinanceiraDAO.buscarPorQuadroELayout(quadro.getPk(), layout == null ? null
                : layout.getPk(), ordemCrescentePeriodo);
    }

    @Transactional
    public void save(OutraInformacaoQuadroPosicaoFinanceira outraInformacaoQuadroPosicaoFinanceira) {
        outraInformacaoQuadroPosicaoFinanceiraDAO.save(outraInformacaoQuadroPosicaoFinanceira);
    }

    @Transactional
    public void delete(OutraInformacaoQuadroPosicaoFinanceira outraInformacaoQuadroPosicaoFinanceira) {
        outraInformacaoQuadroPosicaoFinanceiraDAO.delete(outraInformacaoQuadroPosicaoFinanceira);
    }

}
