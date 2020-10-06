package br.gov.bcb.sisaps.src.mediator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.src.dominio.Atividade;
import br.gov.bcb.sisaps.src.dominio.CelulaRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.ParametroGrupoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.Unidade;
import br.gov.bcb.sisaps.src.dominio.VersaoPerfilRisco;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoGrupoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoLinhaMatrizVOEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoUnidadeAtividadeEnum;
import br.gov.bcb.sisaps.src.vo.ArcNotasVO;
import br.gov.bcb.sisaps.src.vo.CelulaRiscoControleVO;
import br.gov.bcb.sisaps.src.vo.LinhaMatrizVO;
import br.gov.bcb.sisaps.src.vo.LinhaNotasMatrizVO;

@Service
@Transactional(readOnly = true)
public class LinhaMatrizMediator {

    private static final String A = "*A";

    @Autowired
    protected MatrizCicloMediator matrizCicloMediator;

    @Autowired
    protected UnidadeMediator unidadeMediator;
    @Autowired
    protected AtividadeMediator atividadeMediator;

    private int sequencial;

    public static LinhaMatrizMediator get() {
        return SpringUtils.get().getBean(LinhaMatrizMediator.class);
    }

    public List<LinhaMatrizVO> consultar(Matriz matriz, boolean isCorporativa, List<Atividade> listaAtividade) {

        List<LinhaMatrizVO> listaVO = new ArrayList<LinhaMatrizVO>();

        if (isCorporativa) {
            listaVO.addAll(montarListaVOCorporativa(matriz, listaAtividade));
        } else {
            listaVO.addAll(montarListaVONegocio(matriz, listaAtividade));
        }

        return listaVO;
    }

    private List<LinhaMatrizVO> montarListaVOCorporativa(Matriz matriz, List<Atividade> listaAtividade) {
        List<Unidade> listaUnidadeCorporativaList =
                unidadeMediator.buscarUnidadesMatriz(matriz, TipoUnidadeAtividadeEnum.CORPORATIVO);
        return montarListaVO(listaUnidadeCorporativaList, TipoUnidadeAtividadeEnum.CORPORATIVO, listaAtividade, matriz);

    }

    private List<LinhaMatrizVO> montarListaVONegocio(Matriz matriz, List<Atividade> listaAtividade) {
        List<Unidade> listaUnidadeNegocio =
                unidadeMediator.buscarUnidadesMatriz(matriz, TipoUnidadeAtividadeEnum.NEGOCIO);
        //Montar Lista de Unidades ligadas na Matriz
        return montarListaVO(listaUnidadeNegocio, TipoUnidadeAtividadeEnum.NEGOCIO, listaAtividade, matriz);
    }

    public List<LinhaMatrizVO> montarListaVOAtividadeNegocio(Matriz matriz, List<Atividade> listaAtividade) {
        List<LinhaMatrizVO> listaVO = new ArrayList<LinhaMatrizVO>();

        List<Atividade> listaAtividades = atividadeMediator.buscarAtividadesMatriz(matriz);
        listaAtividade.addAll(listaAtividades);
        //Montar Lista de Atividades ligadas direto na Matriz
        listaVO.addAll(montarListaAtividadeVO(TipoUnidadeAtividadeEnum.NEGOCIO, listaAtividades, false, matriz));

        return listaVO;
    }

    private List<LinhaMatrizVO> montarListaVO(List<Unidade> listaUnidade, TipoUnidadeAtividadeEnum tipoAtividade,
            List<Atividade> listaAtividade, Matriz matriz) {
        List<LinhaMatrizVO> lista = new ArrayList<LinhaMatrizVO>();
        for (Unidade unidade : listaUnidade) {
            sequencial++;
            if (TipoUnidadeAtividadeEnum.NEGOCIO.equals(tipoAtividade)) {
                lista.add(criarVOUnidade(unidade, tipoAtividade));
            }
            List<Atividade> listaAtividades = atividadeMediator.buscarAtividadesUnidade(unidade);
            listaAtividade.addAll(listaAtividades);
            //Montar Lista de Atividades ligadas a unidade
            lista.addAll(montarListaAtividadeVO(tipoAtividade, listaAtividades,
                    !TipoUnidadeAtividadeEnum.CORPORATIVO.equals(tipoAtividade), matriz));
        }

        return lista;
    }

    private List<LinhaMatrizVO> montarListaAtividadeVO(TipoUnidadeAtividadeEnum tipoAtividade,
            List<Atividade> listaAtividades, boolean isfilho, Matriz matriz) {
        List<LinhaMatrizVO> lista = new ArrayList<LinhaMatrizVO>();
        if (!listaAtividades.isEmpty()) {
            for (Atividade atividade : listaAtividades) {
                sequencial++;
                lista.add(criarVOAtividade(atividade, tipoAtividade, isfilho, matriz));
            }
        }
        return lista;
    }

    private LinhaMatrizVO criarVOAtividade(Atividade atividade, TipoUnidadeAtividadeEnum tipoAtividade,
            boolean isfilho, Matriz matriz) {
        LinhaMatrizVO linhaAtividadeVO = new LinhaMatrizVO(atividade.getPk());
        linhaAtividadeVO.setNome(atividade.getNome());
        linhaAtividadeVO.setTipo(TipoLinhaMatrizVOEnum.ATIVIDADE);
        linhaAtividadeVO.setAtividade(tipoAtividade);
        linhaAtividadeVO.setFilho(isfilho);
        linhaAtividadeVO.setSequencial(sequencial);
        linhaAtividadeVO.setPkMatriz(matriz.getPk());
        return linhaAtividadeVO;

    }

    private LinhaMatrizVO criarVOUnidade(Unidade unidade, TipoUnidadeAtividadeEnum tipoAtividade) {
        LinhaMatrizVO linhaUnidadeVO = new LinhaMatrizVO(unidade.getPk());
        linhaUnidadeVO.setNome(unidade.getNome());
        linhaUnidadeVO.setTipo(TipoLinhaMatrizVOEnum.UNIDADE);
        linhaUnidadeVO.setAtividade(tipoAtividade);
        linhaUnidadeVO.setSequencial(sequencial);
        return linhaUnidadeVO;
    }

    public void montarListaNotasEMediasResiduaisVO(Matriz matriz, List<VersaoPerfilRisco> versoesPerfilRiscoARCs,
            boolean notaVigente, List<ParametroGrupoRiscoControle> listaGrupo, List<LinhaNotasMatrizVO> notaResiduais,
            List<LinhaNotasMatrizVO> mediaResiduais, List<CelulaRiscoControleVO> listaCelula,
            PerfilAcessoEnum perfilTela, PerfilRisco perfilRisco) {
        for (ParametroGrupoRiscoControle grupo : listaGrupo) {
            List<CelulaRiscoControleVO> listaCelulaGrupo = listaCelulaGrupoVO(listaCelula, grupo, matriz);
            BigDecimal notaRisco =
                    matrizCicloMediator.notaResidualParametroGrupoVO(matriz, grupo, versoesPerfilRiscoARCs, notaVigente,
                            TipoGrupoEnum.RISCO, listaCelulaGrupo, perfilTela, perfilRisco);
            BigDecimal notaControle =
                    matrizCicloMediator.notaResidualParametroGrupoVO(matriz, grupo, versoesPerfilRiscoARCs, notaVigente,
                            TipoGrupoEnum.CONTROLE, listaCelulaGrupo, perfilTela, perfilRisco);
            addNotasRiscoEControle(notaResiduais, grupo, obterNota(notaRisco), obterNota(notaControle));

            LinhaNotasMatrizVO media = new LinhaNotasMatrizVO();
            media.setGrupo(grupo);
            media.setTipo(TipoGrupoEnum.RISCO);
            media.setNota(mediaResidual(notaRisco, notaControle, matriz).replace('.', ','));
            mediaResiduais.add(media);

        }

    }

    public void montarListaNotasEMediasResiduais(Matriz matriz, List<VersaoPerfilRisco> versoesPerfilRiscoARCs,
            boolean notaVigente, List<ParametroGrupoRiscoControle> listaGrupo, List<LinhaNotasMatrizVO> notaResiduais,
            List<LinhaNotasMatrizVO> mediaResiduais, List<CelulaRiscoControle> listaCelula,
            PerfilAcessoEnum perfilTela, PerfilRisco perfilRisco) {
        for (ParametroGrupoRiscoControle grupo : listaGrupo) {
            List<CelulaRiscoControle> listaCelulaGrupo = listaCelulaGrupo(listaCelula, grupo, matriz);
            BigDecimal notaRisco =
                    matrizCicloMediator.notaResidualParametroGrupo(matriz, grupo, versoesPerfilRiscoARCs, notaVigente,
                            TipoGrupoEnum.RISCO, listaCelulaGrupo, perfilTela, perfilRisco);
            BigDecimal notaControle =
                    matrizCicloMediator.notaResidualParametroGrupo(matriz, grupo, versoesPerfilRiscoARCs, notaVigente,
                            TipoGrupoEnum.CONTROLE, listaCelulaGrupo, perfilTela, perfilRisco);
            addNotasRiscoEControle(notaResiduais, grupo, obterNota(notaRisco), obterNota(notaControle));

            LinhaNotasMatrizVO media = new LinhaNotasMatrizVO();
            media.setGrupo(grupo);
            media.setTipo(TipoGrupoEnum.RISCO);
            media.setNota(mediaResidual(notaRisco, notaControle, matriz).replace('.', ','));
            mediaResiduais.add(media);
        }

    }

    public String obterNota(BigDecimal nota) {
        return nota == null ? "" : nota.setScale(2, RoundingMode.HALF_UP).toString();
    }

    public String mediaResidual(BigDecimal notaRisco, BigDecimal notaControle, Matriz matriz) {

        BigDecimal mediaResidual = BigDecimal.ZERO;

        BigDecimal notaGrupoRisco;
        BigDecimal mediaResidualRisco;
        if (notaRisco != null) {
            notaGrupoRisco = notaRisco;
            BigDecimal fatorRisco =
                    notaControle == null || "".equals(notaControle.toString()) ? new BigDecimal(1) : matriz.getCiclo()
                            .getMetodologia().getParametrosFatorRelevancia().get(0).getValorAlfa();
            mediaResidualRisco = notaGrupoRisco.multiply(fatorRisco);
            mediaResidual = mediaResidual.add(mediaResidualRisco);
        }

        BigDecimal notaGrupoControle;
        BigDecimal mediaResidualControle;
        if (notaControle != null) {
            notaGrupoControle = notaControle;
            BigDecimal fatorControle =
                    notaRisco == null || "".equals(notaRisco.toString()) ? new BigDecimal(1) : matriz.getCiclo()
                            .getMetodologia().getParametrosFatorRelevancia().get(0).getValorBeta();
            mediaResidualControle = notaGrupoControle.multiply(fatorControle);
            mediaResidual = mediaResidual.add(mediaResidualControle);
        }

        return "0".equals(mediaResidual.toString()) ? A : mediaResidual.setScale(2, RoundingMode.HALF_UP).toString();

    }

    public List<CelulaRiscoControle> listaCelulaGrupo(List<CelulaRiscoControle> listaCelulaGrupo,
            ParametroGrupoRiscoControle grupo, Matriz matriz) {

        List<CelulaRiscoControle> lista = new ArrayList<CelulaRiscoControle>();

        for (CelulaRiscoControle celulaRiscoControle : listaCelulaGrupo) {
            CelulaRiscoControle celulaInicializada =
                    CelulaRiscoControleMediator.get().buscar(celulaRiscoControle.getPk());
            if (celulaInicializada.getParametroGrupoRiscoControle().equals(grupo)
                    && celulaInicializada.getAtividade().getMatriz().equals(matriz)) {
                lista.add(celulaInicializada);
            }
        }

        return lista;
    }

    public List<CelulaRiscoControleVO> listaCelulaGrupoVO(List<CelulaRiscoControleVO> listaCelulaGrupo,
            ParametroGrupoRiscoControle grupo, Matriz matriz) {
        List<CelulaRiscoControleVO> lista = new ArrayList<CelulaRiscoControleVO>();
        for (CelulaRiscoControleVO celulaRiscoControle : listaCelulaGrupo) {
            if (celulaRiscoControle.getParametroGrupoPk().equals(grupo.getPk())
                    && celulaRiscoControle.getMatrizPk().equals(matriz.getPk())) {
                lista.add(celulaRiscoControle);
            }
        }
        return lista;
    }

    private void addNotasRiscoEControle(List<LinhaNotasMatrizVO> notaResiduais, ParametroGrupoRiscoControle grupo,
            String notaRisco, String notaControle) {
        LinhaNotasMatrizVO notaRiscoVO = new LinhaNotasMatrizVO();
        notaRiscoVO.setGrupo(grupo);
        notaRiscoVO.setNota(notaRisco.isEmpty() ? A : notaRisco.replace('.', ','));
        notaRiscoVO.setTipo(TipoGrupoEnum.RISCO);
        notaResiduais.add(notaRiscoVO);
        LinhaNotasMatrizVO notaControleVO = new LinhaNotasMatrizVO();
        notaControleVO.setGrupo(grupo);
        notaControleVO.setNota(notaControle.isEmpty() ? A : notaControle.replace('.', ','));
        notaControleVO.setTipo(TipoGrupoEnum.CONTROLE);
        notaResiduais.add(notaControleVO);
    }

    public String buscarNota(final ArcNotasVO avaliacaoRisco, boolean emAnalise,
            PerfilAcessoEnum perflEmun, Ciclo ciclo, PerfilRisco perfilRisco) {
        if (emAnalise) {
            return AvaliacaoRiscoControleMediator.get().getNotaEmAnaliseDescricaoValor(avaliacaoRisco);
        } else {
            return AvaliacaoRiscoControleMediator.get().notaArc(avaliacaoRisco, ciclo, perflEmun, perfilRisco);
        }
    }

}