package br.gov.bcb.sisaps.src.mediator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.comum.util.string.StringUtil;
import br.gov.bcb.sisaps.src.dao.AtividadeDAO;
import br.gov.bcb.sisaps.src.dominio.Atividade;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.CelulaRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.Unidade;
import br.gov.bcb.sisaps.src.dominio.VersaoPerfilRisco;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoObjetoVersionadorEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoUnidadeAtividadeEnum;
import br.gov.bcb.sisaps.src.validacao.RegraAtividadeValidacao;
import br.gov.bcb.sisaps.src.validacao.RegraTransferirARC;
import br.gov.bcb.sisaps.src.vo.AtividadeVO;
import br.gov.bcb.sisaps.src.vo.ConsultaAtividadeVO;

@Service
public class AtividadeMediator extends AbstractMediatorPaginado<AtividadeVO, Integer, ConsultaAtividadeVO> {

    private static final String ATIVIDADE = "Atividade ";

    @Autowired
    private AtividadeDAO atividadeDAO;

    @Autowired
    private UnidadeMediator unidadeMediator;

    public static AtividadeMediator get() {
        return SpringUtils.get().getBean(AtividadeMediator.class);
    }

    @Override
    protected AtividadeDAO getDao() {
        return atividadeDAO;
    }

    @Transactional(readOnly = true)
    public List<Atividade> buscarAtividadesMatriz(Matriz matriz) {
        return atividadeDAO.buscarAtividadesMatriz(matriz);
    }

    @Transactional(readOnly = true)
    public Atividade buscarAtividadesMatrizPorAvaliacao(Matriz matriz, AvaliacaoRiscoControle avaliacao) {
        return atividadeDAO.buscarAtividadesMatriz(matriz, avaliacao);
    }

    @Transactional(readOnly = true)
    public List<Atividade> buscarAtividadesUnidade(Unidade unidade) {
        return atividadeDAO.buscarAtividadesUnidade(unidade);
    }

    @Transactional
    public String incluir(Atividade atividade) {
        incluirDadosComplementares(atividade);
        validarAtividade(atividade, false);
        incluirARCsCelulas(atividade);
        atividadeDAO.save(atividade);
        atividadeDAO.flush();
        return ATIVIDADE + atividade.getNome() + " incluída com sucesso.";
    }

    private void incluirARCsCelulas(Atividade atividade) {
        List<CelulaRiscoControle> celulasRiscoControle = atividade.getCelulasRiscoControle();
        if (celulasRiscoControle != null && !celulasRiscoControle.isEmpty()) {
            for (CelulaRiscoControle celulaRiscoControle : celulasRiscoControle) {
                if (celulaRiscoControle.getPk() == null) {
                    AvaliacaoRiscoControleMediator.get().incluir(celulaRiscoControle.getArcRisco());
                    AvaliacaoRiscoControleMediator.get().incluir(celulaRiscoControle.getArcControle());
                }
            }
        }
    }

    public void validarAtividade(Atividade atividade, boolean isAlteracao) {
        new RegraAtividadeValidacao(atividade, this, isAlteracao).validar();
    }

    public boolean existeAtividadeMesmoNome(Atividade atividade) {
        ConsultaAtividadeVO consulta = new ConsultaAtividadeVO();
        consulta.setNome(atividade.getNome());
        consulta.setMatriz(atividade.getMatriz());
        Long total = atividadeDAO.total(consulta);
        return total > 0;
    }

    private void incluirDadosComplementares(Atividade atividade) {
        if (TipoUnidadeAtividadeEnum.CORPORATIVO.equals(atividade.getTipoAtividade())) {
            atividade.setParametroTipoAtividadeNegocio(null);
            if (StringUtil.isVazioOuNulo(atividade.getNome()) && atividade.getParametroTipoAtividadeNegocio() == null) {
                atividade.setNome("Atividade Corporativa");
            }
        } else {
            if (StringUtil.isVazioOuNulo(atividade.getNome()) && atividade.getParametroTipoAtividadeNegocio() != null) {
                atividade.setNome(atividade.getParametroTipoAtividadeNegocio().getNome());
            }
        }
    }

    @Transactional(readOnly = true)
    public Atividade getPK(Integer pk) {
        Atividade result = atividadeDAO.load(pk);
        return result;
    }

    @Transactional(readOnly = true)
    public Atividade loadPK(Integer pk) {
        Atividade result = atividadeDAO.load(pk);
        inicializarDependencias(result);
        return result;
    }

    public void inicializarDependencias(Atividade result) {
        if (result.getUnidade() != null) {
            Hibernate.initialize(result.getUnidade());
            unidadeMediator.inicializarDependencias(result.getUnidade());
        }
        if (result.getParametroTipoAtividadeNegocio() != null) {
            Hibernate.initialize(result.getParametroTipoAtividadeNegocio());
        }
        if (result.getCelulasRiscoControle() != null) {
            Hibernate.initialize(result.getCelulasRiscoControle());
            for (CelulaRiscoControle celulaRiscoControle : result.getCelulasRiscoControle()) {
                Hibernate.initialize(celulaRiscoControle.getParametroPeso());
                Hibernate.initialize(celulaRiscoControle.getParametroGrupoRiscoControle());
            }
        }
        if (result.getParametroPeso() != null) {
            Hibernate.initialize(result.getParametroPeso());
        }

        if (result.getMatriz() != null) {
            Hibernate.initialize(result.getMatriz());
        }

        if (result.getMatriz().getCiclo() != null) {
            Hibernate.initialize(result.getMatriz().getCiclo());
        }

        if (result.getMatriz().getCiclo().getEntidadeSupervisionavel() != null) {
            Hibernate.initialize(result.getMatriz().getCiclo().getEntidadeSupervisionavel());
        }

    }

    /**
     * Foi atulizado para excluir o relacionamento em cascade com as avaliações
     * */
    @Transactional
    public void excluir(Atividade atividade, boolean alterarVersaoMatriz) {
        if (alterarVersaoMatriz) {
            MatrizCicloMediator.get().alterarVersaoMatriz(atividade.getMatriz());
        }
        List<AvaliacaoRiscoControle> listaARCsExcluidos =
                excluirCelulasRiscoControle(atividade.getCelulasRiscoControle());
        atividadeDAO.delete(atividade);
        AvaliacaoRiscoControleMediator.get().excluir(listaARCsExcluidos);
        atividadeDAO.flush();
    }

    private List<AvaliacaoRiscoControle> excluirCelulasRiscoControle(List<CelulaRiscoControle> celulasRiscoControle) {
        List<AvaliacaoRiscoControle> arcsAExcluir = new ArrayList<AvaliacaoRiscoControle>();
        for (CelulaRiscoControle celulaRiscoControle : celulasRiscoControle) {
            if (celulaRiscoControle.getArcRisco().getVersaoPerfilRisco() == null) {
                arcsAExcluir.add(AvaliacaoRiscoControleMediator.get().buscarPorPk(
                        celulaRiscoControle.getArcRisco().getPk()));
            }
            if (celulaRiscoControle.getArcControle().getVersaoPerfilRisco() == null) {
                arcsAExcluir.add(AvaliacaoRiscoControleMediator.get().buscarPorPk(
                        celulaRiscoControle.getArcControle().getPk()));
            }
            CelulaRiscoControleMediator.get().excluir(celulaRiscoControle);
        }
        return arcsAExcluir;
    }

    @Transactional
    public void excluir(List<Atividade> lista) {
        if (lista != null && !lista.isEmpty()) {
            for (Atividade atividade : lista) {
                excluir(loadPK(atividade.getPk()), false);
            }
        }
    }

    @Transactional
    public String alterar(Atividade atividade, List<CelulaRiscoControle> listaExcluidos) {
        incluirDadosComplementares(atividade);
        validarAtividade(atividade, true);
        incluirARCsCelulas(atividade);
        List<AvaliacaoRiscoControle> listaARCsExcluidos = excluirCelulasRiscoControle(listaExcluidos);
        AvaliacaoRiscoControleMediator.get().excluir(listaARCsExcluidos);
        atividadeDAO.merge(atividade);
        atividadeDAO.flush();
        return ATIVIDADE + atividade.getNome() + " alterada com sucesso.";
    }

    @Transactional
    public void criarVersaoAtividades(Matriz matrizBase, Matriz matrizNova, PerfilRisco perfilRiscoAtual,
            boolean isEncerrarCorec) {

        List<VersaoPerfilRisco> versoesPerfilRiscoCelulas =
                VersaoPerfilRiscoMediator.get().buscarVersoesPerfilRisco(perfilRiscoAtual.getPk(),
                        TipoObjetoVersionadorEnum.CELULA_RISCO_CONTROLE);

        List<CelulaRiscoControle> celulasRiscoControle =
                CelulaRiscoControleMediator.get().buscarCelulasPorPerfilRisco(perfilRiscoAtual.getPk());
        Set<Atividade> atividades = new HashSet<Atividade>();
        for (CelulaRiscoControle celula : celulasRiscoControle) {
            atividades.add(celula.getAtividade());
        }

        for (Atividade atividade : atividades) {
            Atividade atividadeNova = new Atividade();
            atividadeNova.setMatriz(matrizNova);
            atividadeNova.setNome(atividade.getNome());
            atividadeNova.setParametroPeso(atividade.getParametroPeso());
            atividadeNova.setParametroTipoAtividadeNegocio(TipoUnidadeAtividadeEnum.NEGOCIO.equals(atividade
                    .getTipoAtividade()) ? atividade.getParametroTipoAtividadeNegocio() : null);
            atividadeNova.setPercentualParticipacao(atividade.getPercentualParticipacao());
            atividadeNova.setTipoAtividade(atividade.getTipoAtividade());
            if (atividade.getUnidade() != null) {
                atividadeNova.setUnidade(unidadeMediator.getUnidadeVersionada(atividade.getUnidade(), matrizNova));
            }
            if (isEncerrarCorec) {
                atividadeNova.setUltimaAtualizacao(atividade.getUltimaAtualizacao());
                atividadeNova.setOperadorAtualizacao(atividade.getOperadorAtualizacao());
                atividadeNova.setAlterarDataUltimaAtualizacao(false);
            }
            atividadeNova.setCelulasRiscoControle(CelulaRiscoControleMediator.get().criarVersaoCelulasRiscoControle(
                    atividade, atividadeNova, versoesPerfilRiscoCelulas, matrizBase.getCiclo(), matrizNova.getCiclo(),
                    isEncerrarCorec));
            atividadeDAO.save(atividadeNova);
        }
        atividadeDAO.flush();
    }

    @Transactional
    public void merge(Atividade ativ) {
        atividadeDAO.merge(ativ);
    }

    @Transactional
    public void update(Atividade atividade) {
        atividadeDAO.update(atividade);
    }

    @Transactional(readOnly = true)
    public Long somarPesoAtividade(Matriz matriz, TipoUnidadeAtividadeEnum tipoAtividade, Integer pkUnidade) {
        return atividadeDAO.somarPesoAtividade(matriz, tipoAtividade, pkUnidade);
    }

    @Transactional(readOnly = true)
    public boolean atividadePossuiCelulaRiscoControleComPesoMax(Atividade atividade, Integer idMaiorPeso) {
        for (CelulaRiscoControle celulaRiscoControle : atividade.getCelulasRiscoControle()) {
            if (celulaRiscoControle.getParametroPeso().getPk().equals(idMaiorPeso)) {
                return true;
            }
        }
        return false;
    }

    @Transactional(readOnly = true)
    public boolean atividadesComPesoMax(List<Atividade> atividades, Integer idMaiorPeso) {
        for (Atividade atividade : atividades) {
            if (atividade.getParametroPeso().getPk().equals(idMaiorPeso)) {
                return true;
            }
        }
        return false;
    }

    @Transactional(readOnly = true)
    public List<Atividade> buscarTodasAtividadesMatriz(Matriz matriz) {
        return atividadeDAO.buscarTodasAtividadesMatriz(matriz);
    }

    @Transactional
    public void transferirArcs(Atividade antiga, Atividade nova, CelulaRiscoControle celulaRiscoControle) {
        new RegraTransferirARC(nova, celulaRiscoControle).validarTransferencia();
        MatrizCicloMediator.get().alterarVersaoMatriz(antiga.getMatriz());

        celulaRiscoControle.setAtividade(nova);
        CelulaRiscoControleMediator.get().alterar(celulaRiscoControle);
        atividadeDAO.flush();
        nova.getCelulasRiscoControle().add(celulaRiscoControle);
        atividadeDAO.flush();
        antiga.getCelulasRiscoControle().remove(celulaRiscoControle);
        atividadeDAO.flush();
        if (antiga.getCelulasRiscoControle().isEmpty()) {
            atividadeDAO.delete(antiga);
        }

        atividadeDAO.flush();

    }

    public String retornarNomeAtividade(Atividade atividade) {
        return atividade.getNome() == null ? "" : atividade.getNome();
    }

    public boolean podeExcluirAtividade(Atividade atividade) {
        return atividade.getCelulasRiscoControle().isEmpty();
    }

}
