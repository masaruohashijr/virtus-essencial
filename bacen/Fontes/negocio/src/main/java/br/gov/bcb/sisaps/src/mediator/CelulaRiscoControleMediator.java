package br.gov.bcb.sisaps.src.mediator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.src.dao.CelulaRiscoControleDAO;
import br.gov.bcb.sisaps.src.dominio.Atividade;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.CelulaRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.ParametroGrupoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.VersaoPerfilRisco;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoARCEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoGrupoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoObjetoVersionadorEnum;
import br.gov.bcb.sisaps.src.vo.CelulaRiscoControleVO;

@Service
@Transactional(readOnly = true)
public class CelulaRiscoControleMediator {

    @Autowired
    private CelulaRiscoControleDAO celulaRiscoControleDAO;

    public static CelulaRiscoControleMediator get() {
        return SpringUtils.get().getBean(CelulaRiscoControleMediator.class);
    }

    @Transactional
    public void excluir(CelulaRiscoControle celulaRiscoControle) {
        celulaRiscoControleDAO.delete(celulaRiscoControleDAO.getRecord(celulaRiscoControle.getPk()));
    }

    public CelulaRiscoControle buscarCelularPorAvaliacaoEAtividade(Atividade atividade, AvaliacaoRiscoControle arc) {
        return celulaRiscoControleDAO.buscarCelularPorAvaliacaoEAtividade(atividade, arc);
    }
    
    
    public List<CelulaRiscoControle> buscarCelularPorAtividade(Atividade atividade) {
        return celulaRiscoControleDAO.buscarCelularPorAtividade(atividade);
    }

    public CelulaRiscoControle buscarCelularPorAvaliacao(AvaliacaoRiscoControle arc) {
        return celulaRiscoControleDAO.buscarCelularPorAvaliacao(arc);
    }

    public List<CelulaRiscoControle> buscarCelulasMatriz(Integer pkMatriz) {
        return celulaRiscoControleDAO.buscarCelulasMatriz(pkMatriz);
    }

    public void criarARCsCelula(CelulaRiscoControle celulaRiscoControle) {
        if (celulaRiscoControle.getArcRisco() == null) {
            AvaliacaoRiscoControle arcRisco = new AvaliacaoRiscoControle();
            arcRisco.setTipo(TipoGrupoEnum.RISCO);
            arcRisco.setEstado(EstadoARCEnum.PREVISTO);
            celulaRiscoControle.setArcRisco(arcRisco);
        }
        if (celulaRiscoControle.getArcControle() == null) {
            AvaliacaoRiscoControle arcControle = new AvaliacaoRiscoControle();
            arcControle.setTipo(TipoGrupoEnum.CONTROLE);
            arcControle.setEstado(EstadoARCEnum.PREVISTO);
            celulaRiscoControle.setArcControle(arcControle);
        }
    }

    public CelulaRiscoControle buscar(Integer pk) {
        return celulaRiscoControleDAO.getRecord(pk);
    }

    @Transactional
    public void alterar(CelulaRiscoControle celulaRiscoControle) {
        celulaRiscoControleDAO.update(celulaRiscoControle);
        celulaRiscoControleDAO.getSessionFactory().getCurrentSession().flush();
    }

    @Transactional
    public void incluir(CelulaRiscoControle celulaRiscoControle) {
        celulaRiscoControleDAO.save(celulaRiscoControle);
    }

    public List<CelulaRiscoControle> criarVersaoCelulasRiscoControle(Atividade atividade, Atividade atividadeNova,
            List<VersaoPerfilRisco> versoesPerfilRisco, Ciclo cicloAnterior, Ciclo cicloNovo, boolean isEncerrarCorec) {
        List<CelulaRiscoControle> novasCelulasRiscoControle = new ArrayList<CelulaRiscoControle>();
        for (CelulaRiscoControle celulaRiscoControle : buscarCelulaPorAtividadeEVersaoPerfil(atividade,
                versoesPerfilRisco)) {
            CelulaRiscoControle novaCelula = new CelulaRiscoControle();
            novaCelula.setArcRisco(celulaRiscoControle.getArcRisco());
            novaCelula.setArcControle(celulaRiscoControle.getArcControle());
            novaCelula.setParametroGrupoRiscoControle(celulaRiscoControle.getParametroGrupoRiscoControle());
            novaCelula.setParametroPeso(celulaRiscoControle.getParametroPeso());
            novaCelula.setAtividade(atividadeNova);
            if (isEncerrarCorec) {
                novaCelula.setArcRisco(AvaliacaoRiscoControleMediator.get().criarARCNovoCiclo(
                        cicloAnterior, cicloNovo, celulaRiscoControle.getParametroGrupoRiscoControle(), 
                        celulaRiscoControle.getArcRisco()));
                novaCelula.setArcControle(AvaliacaoRiscoControleMediator.get().criarARCNovoCiclo(
                        cicloAnterior, cicloNovo, celulaRiscoControle.getParametroGrupoRiscoControle(), 
                        celulaRiscoControle.getArcControle()));
                novaCelula.setUltimaAtualizacao(celulaRiscoControle.getUltimaAtualizacao());
                novaCelula.setOperadorAtualizacao(celulaRiscoControle.getOperadorAtualizacao());
                novaCelula.setAlterarDataUltimaAtualizacao(false);
                PerfilRiscoMediator.get().incluirVersaoPerfilRiscoAtual(cicloNovo, novaCelula,
                        TipoObjetoVersionadorEnum.CELULA_RISCO_CONTROLE);
            }
            novasCelulasRiscoControle.add(novaCelula);
        }
        return novasCelulasRiscoControle;
    }

    public List<CelulaRiscoControle> buscarParametroDaMatriz(List<Atividade> listaAtividade,
            List<VersaoPerfilRisco> versoesPerfilRiscoARCs) {
        List<CelulaRiscoControle> lista = new LinkedList<CelulaRiscoControle>();
        if (listaAtividade.isEmpty()) {
            return lista;
        }
        return celulaRiscoControleDAO.buscarParametroDaMatriz(listaAtividade, versoesPerfilRiscoARCs);
    }

    public List<CelulaRiscoControleVO> buscarParametroDaMatrizVO(List<Atividade> listaAtividade,
            List<VersaoPerfilRisco> versoesPerfilRiscoARCs) {
        List<CelulaRiscoControleVO> lista = new LinkedList<CelulaRiscoControleVO>();
        if (listaAtividade.isEmpty()) {
            return lista;
        }
        return celulaRiscoControleDAO.buscarParametroDaMatrizVO(listaAtividade, versoesPerfilRiscoARCs);
    }

    public List<CelulaRiscoControle> buscarArcDaMatrizPorGrupo(Matriz matriz, ParametroGrupoRiscoControle grupo,
            List<VersaoPerfilRisco> versoesPerfilRiscoARCs) {
        return celulaRiscoControleDAO.buscarArcDaMatrizPorGrupo(matriz, grupo, versoesPerfilRiscoARCs);
    }

    public Long somarPesoDosArcPorAtividades(Matriz matriz, int pkAtividade,
            List<VersaoPerfilRisco> versoesPerfilRiscoARCs) {
        return celulaRiscoControleDAO.somarPesoDosArcPorAtividades(matriz, pkAtividade, versoesPerfilRiscoARCs);
    }

    public CelulaRiscoControle duplicarCelulaRiscoControlePublicacaoARC(CelulaRiscoControle celulaAntiga) {
        CelulaRiscoControle novaCelula = new CelulaRiscoControle();
        novaCelula.setAtividade(celulaAntiga.getAtividade());
        novaCelula.setParametroGrupoRiscoControle(celulaAntiga.getParametroGrupoRiscoControle());
        novaCelula.setParametroPeso(celulaAntiga.getParametroPeso());
        return novaCelula;
    }

    public List<CelulaRiscoControle> buscarCelulasPorPerfilRisco(Integer pkPerfilRisco) {
        return celulaRiscoControleDAO.buscarCelulasPorPerfilRisco(pkPerfilRisco);
    }

    public List<CelulaRiscoControle> buscarCelulaPorVersaoPerfil(List<VersaoPerfilRisco> versoesPerfilRisco) {
        return celulaRiscoControleDAO.buscarCelulaPorVersaoPerfil(versoesPerfilRisco);
    }

    public List<CelulaRiscoControleVO> buscarCelulaPorVersaoPerfilVO(List<VersaoPerfilRisco> versoesPerfilRisco) {
        return celulaRiscoControleDAO.buscarCelulaPorVersaoPerfilVO(versoesPerfilRisco);
    }

    public List<CelulaRiscoControle> buscarCelulaPorAtividadeEVersaoPerfil(Atividade atividade,
            List<VersaoPerfilRisco> versaoPerfilRisco) {
        return celulaRiscoControleDAO.buscarCelulaPorAtividadeEVersaoPerfil(atividade, versaoPerfilRisco);
    }
    
    public List<CelulaRiscoControle> getCelulasPorGrupo(List<CelulaRiscoControle> listaCelulas,
            ParametroGrupoRiscoControle grupo) {
        List<CelulaRiscoControle> listaCelula = new ArrayList<CelulaRiscoControle>();
        for (CelulaRiscoControle celula : listaCelulas) {
            if (celula.getParametroGrupoRiscoControle().equals(grupo)) {
                listaCelula.add(celula);

            }
        }

        return listaCelula;

    }

    public List<CelulaRiscoControleVO> getCelulasPorGrupoVO(List<CelulaRiscoControleVO> listaCelulas,
            ParametroGrupoRiscoControle grupo) {
        List<CelulaRiscoControleVO> listaCelula = new ArrayList<CelulaRiscoControleVO>();
        for (CelulaRiscoControleVO celula : listaCelulas) {
            if (celula.getParametroGrupoPk().equals(grupo.getPk())) {
                listaCelula.add(celula);
            }
        }
        return listaCelula;
    }

    @Transactional
    public void atualizarARCsCelulasMatrizEsbocada(Ciclo ciclo, List<AvaliacaoRiscoControle> novosARCs) {
        Matriz matrizEmEdicao = MatrizCicloMediator.get().getMatrizEmEdicao(ciclo);
        if (matrizEmEdicao != null) {
            for (AvaliacaoRiscoControle arc : novosARCs) {
                CelulaRiscoControle celula =
                        celulaRiscoControleDAO.buscarCelulaMatrizEsbocada(matrizEmEdicao,
                                arc.getAvaliacaoRiscoControleVigente());
                if (celula != null) {
                    arc.setAlterarDataUltimaAtualizacao(false);
                    if (celula.getArcRisco().getPk().equals(arc.getAvaliacaoRiscoControleVigente().getPk())) {
                        celula.setArcRisco(arc);
                        alterar(celula);
                    } else if (celula.getArcControle().getPk().equals(arc.getAvaliacaoRiscoControleVigente().getPk())) {
                        celula.setArcControle(arc);
                        alterar(celula);
                    }
                }
            }
        }
    }

    public String msgAoExcluirCelula() {
        return "Atenção, essa operação exclui todo o conteúdo dos ARCs.";
    }

}
