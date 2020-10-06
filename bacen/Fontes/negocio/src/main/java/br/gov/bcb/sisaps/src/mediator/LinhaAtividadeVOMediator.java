package br.gov.bcb.sisaps.src.mediator;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.gov.bcb.sisaps.src.dominio.Atividade;
import br.gov.bcb.sisaps.src.dominio.CelulaRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.ParametroPeso;
import br.gov.bcb.sisaps.src.dominio.Unidade;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoLinhaAtividadeVOEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoUnidadeAtividadeEnum;
import br.gov.bcb.sisaps.src.vo.LinhaAtividadeVO;

@Service
public class LinhaAtividadeVOMediator {

    private static final String RED = "red";

    @Autowired
    protected MatrizCicloMediator matrizCicloMediator;

    @Autowired
    protected UnidadeMediator unidadeMediator;
    @Autowired
    protected AtividadeMediator atividadeMediator;
    private int sequencial;

    private String buscarCorUnidadeEAtividadeNegocio(List<Unidade> listaUnidades, List<Atividade> atividadesMatriz,
            ParametroPeso maiorPeso) {
        boolean possui =
                UnidadeMediator.get().existeUnidadeEAtividadeDeNegocioComMaiorPeso(listaUnidades, atividadesMatriz,
                        maiorPeso.getPk());
        return possui ? "" : RED;
    }

    private String buscarCorAtividade(List<Atividade> listaAtividades, ParametroPeso maiorPeso) {
        boolean possui = AtividadeMediator.get().atividadesComPesoMax(listaAtividades, maiorPeso.getPk());
        return possui ? "" : RED;
    }

    private String buscarCorCelulasRiscoControleDaAtividade(Atividade atividade, ParametroPeso maiorPeso) {
        boolean possui =
                AtividadeMediator.get().atividadePossuiCelulaRiscoControleComPesoMax(atividade, maiorPeso.getPk());
        return possui ? "" : RED;
    }

    private LinhaAtividadeVO criarVOUnidade(Unidade unidade, TipoUnidadeAtividadeEnum tipoAtividade, String corUnidade) {
        LinhaAtividadeVO linhaUnidadeVO = new LinhaAtividadeVO(unidade.getPk());
        linhaUnidadeVO.setNome(unidade.getNome());
        linhaUnidadeVO.setTipo(TipoLinhaAtividadeVOEnum.UNIDADE);
        linhaUnidadeVO.setAtividade(tipoAtividade);
        linhaUnidadeVO.setSequencial(sequencial);
        linhaUnidadeVO.setParametroPeso(unidade.getParametroPeso());
        linhaUnidadeVO.setCorCelula(corUnidade);
        linhaUnidadeVO.setPkMatriz(unidade.getMatriz().getPk());
        return linhaUnidadeVO;
    }

    public void excluirLinhaAtividade(LinhaAtividadeVO linha) {
        if (TipoLinhaAtividadeVOEnum.ATIVIDADE.equals(linha.getTipo())) {
            Atividade atividade = atividadeMediator.loadPK(linha.getPk());
            atividadeMediator.excluir(atividade, true);
        } else {
            unidadeMediator.excluir(linha.getPk());
        }
    }

    public List<LinhaAtividadeVO> consultarLinhasAtividadeVOSemUnidades(Matriz matriz) {
        List<LinhaAtividadeVO> listaLinhasTotalVO = new ArrayList<LinhaAtividadeVO>();
        ParametroPeso maiorPeso =
                MetodologiaMediator.get().buscarMaiorPesoMetodologia(matriz.getCiclo().getMetodologia());
        listaLinhasTotalVO.addAll(consultarLinhasAtividades(matriz, null, maiorPeso, TipoUnidadeAtividadeEnum.NEGOCIO,
                false));
        return ordenarLinhas(listaLinhasTotalVO);
    }

    public List<LinhaAtividadeVO> consultarLinhasAtividadeVOUnidades(Matriz matriz,
            TipoUnidadeAtividadeEnum tipoUnidadeAtividade) {
        List<LinhaAtividadeVO> listaLinhasTotalVO = new ArrayList<LinhaAtividadeVO>();
        List<Unidade> listaUnidadeNegocio = unidadeMediator.buscarUnidadesMatriz(matriz, tipoUnidadeAtividade);
        ParametroPeso maiorPeso =
                MetodologiaMediator.get().buscarMaiorPesoMetodologia(
                        MetodologiaMediator.get().buscarMetodologiaPorPK(matriz.getCiclo().getMetodologia().getPk()));
        String corUnidade =
                buscarCorUnidadeEAtividadeNegocio(listaUnidadeNegocio,
                        atividadeMediator.buscarAtividadesMatriz(matriz), maiorPeso);
        for (Unidade unidade : listaUnidadeNegocio) {
            sequencial++;
            if (tipoUnidadeAtividade.equals(TipoUnidadeAtividadeEnum.NEGOCIO)) {
                LinhaAtividadeVO linhaUnidadeVO = criarVOUnidade(unidade, TipoUnidadeAtividadeEnum.NEGOCIO, corUnidade);
                listaLinhasTotalVO.add(linhaUnidadeVO);
                List<LinhaAtividadeVO> filhos =
                        consultarLinhasAtividades(matriz, unidade, maiorPeso, tipoUnidadeAtividade, true);
                linhaUnidadeVO.setFilhos(filhos);
            } else {
                listaLinhasTotalVO.addAll(consultarLinhasAtividades(matriz, unidade, maiorPeso, tipoUnidadeAtividade,
                        false));
            }

        }

        List<LinhaAtividadeVO> retorno = ordenarLinhas(listaLinhasTotalVO);

        return retorno;
    }

    private List<LinhaAtividadeVO> ordenarLinhas(List<LinhaAtividadeVO> listaLinhasTotalVO) {
        List<LinhaAtividadeVO> retorno = new ArrayList<LinhaAtividadeVO>();
        for (LinhaAtividadeVO linhaAtividadeVO : listaLinhasTotalVO) {
            retorno.add(linhaAtividadeVO);
            for (LinhaAtividadeVO linhaUnidade : linhaAtividadeVO.getFilhos()) {
                retorno.add(linhaUnidade);
                if (CollectionUtils.isNotEmpty(linhaUnidade.getFilhos())) {
                    for (LinhaAtividadeVO linhaCelulaRiscoControle : linhaUnidade.getFilhos()) {
                        retorno.add(linhaCelulaRiscoControle);
                    }
                }
            }
        }
        return retorno;
    }

    private List<LinhaAtividadeVO> consultarLinhasAtividades(Matriz matriz, Unidade unidade, ParametroPeso maiorPeso,
            TipoUnidadeAtividadeEnum tipoUnidadeAtividade, boolean isFilho) {
        List<Atividade> listaAtividades = null;
        if (unidade == null) {
            listaAtividades = atividadeMediator.buscarAtividadesMatriz(matriz);
        } else {
            listaAtividades = atividadeMediator.buscarAtividadesUnidade(unidade);
        }
        List<LinhaAtividadeVO> filhos = new ArrayList<LinhaAtividadeVO>();
        String corAtividade = null;
        if (unidade == null && tipoUnidadeAtividade.equals(TipoUnidadeAtividadeEnum.NEGOCIO)) {
            corAtividade =
                    buscarCorUnidadeEAtividadeNegocio(
                            unidadeMediator.buscarUnidadesMatriz(matriz, TipoUnidadeAtividadeEnum.NEGOCIO),
                            listaAtividades, maiorPeso);
        } else {
            corAtividade = buscarCorAtividade(listaAtividades, maiorPeso);
        }
        for (Atividade atividade : listaAtividades) {
            sequencial++;
            LinhaAtividadeVO linhaAtividadeVO =
                    criarVOAtividade(atividade, tipoUnidadeAtividade, isFilho, matriz, corAtividade, maiorPeso);
            filhos.add(linhaAtividadeVO);
        }
        return filhos;
    }

    private LinhaAtividadeVO criarVOAtividade(Atividade atividade, TipoUnidadeAtividadeEnum tipoAtividade,
            boolean isFilho, Matriz matriz, String corAtividade, ParametroPeso maiorPeso) {
        LinhaAtividadeVO linhaAtividadeVO = setDados(atividade, tipoAtividade, isFilho, matriz, corAtividade);
        List<LinhaAtividadeVO> filhos = new ArrayList<LinhaAtividadeVO>();
        String corArcs = buscarCorCelulasRiscoControleDaAtividade(atividade, maiorPeso);

        for (CelulaRiscoControle celulaRiscoControle : atividade.getCelulasRiscoControle()) {
            filhos.add(criarVOCelulaRiscoControle(celulaRiscoControle, tipoAtividade, matriz, corArcs,
                    atividade.getPk()));
        }

        linhaAtividadeVO.setFilhos(filhos);
        return linhaAtividadeVO;
    }

    private LinhaAtividadeVO setDados(Atividade atividade, TipoUnidadeAtividadeEnum tipoAtividade, boolean isFilho,
            Matriz matriz, String corAtividade) {
        LinhaAtividadeVO linhaAtividadeVO = new LinhaAtividadeVO(atividade.getPk());
        linhaAtividadeVO.setNome(atividade.getNome());
        linhaAtividadeVO.setTipo(TipoLinhaAtividadeVOEnum.ATIVIDADE);
        linhaAtividadeVO.setAtividade(tipoAtividade);
        linhaAtividadeVO.setFilho(isFilho);
        linhaAtividadeVO.setSequencial(sequencial);
        linhaAtividadeVO.setPkMatriz(matriz.getPk());
        linhaAtividadeVO.setParametroPeso(atividade.getParametroPeso());
        linhaAtividadeVO.setCorCelula(corAtividade);
        if (atividade.getParametroTipoAtividadeNegocio() == null) {
            linhaAtividadeVO.setNomeParametroTipoAtividade("Corporativa");
        } else {
            linhaAtividadeVO.setNomeParametroTipoAtividade(atividade.getParametroTipoAtividadeNegocio().getNome());
        }
        return linhaAtividadeVO;
    }

    private LinhaAtividadeVO criarVOCelulaRiscoControle(CelulaRiscoControle celulaRiscoControle,
            TipoUnidadeAtividadeEnum tipoAtividade, Matriz matriz, String corArcs, Integer pkAtividade) {
        LinhaAtividadeVO linhaARCVO = new LinhaAtividadeVO(celulaRiscoControle.getPk());
        linhaARCVO.setNome("ARCs " + celulaRiscoControle.getParametroGrupoRiscoControle().getNomeAbreviado());
        linhaARCVO.setTipo(TipoLinhaAtividadeVOEnum.ARC);
        linhaARCVO.setFilho(true);
        linhaARCVO.setSequencial(sequencial);
        linhaARCVO.setPkMatriz(matriz.getPk());
        linhaARCVO.setAtividade(tipoAtividade);
        linhaARCVO.setParametroPeso(celulaRiscoControle.getParametroPeso());
        linhaARCVO.setCorCelula(corArcs);
        linhaARCVO.setPkAtividade(pkAtividade);
        return linhaARCVO;
    }

}
