package br.gov.bcb.sisaps.src.mediator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.src.dao.SinteseDeRiscoDAO;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.CelulaRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.Metodologia;
import br.gov.bcb.sisaps.src.dominio.ParametroGrupoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.ParametroNota;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.SinteseDeRisco;
import br.gov.bcb.sisaps.src.dominio.VersaoPerfilRisco;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoARCEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoObjetoVersionadorEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoParametroGrupoRiscoControleEnum;
import br.gov.bcb.sisaps.src.util.BufferAnexos;
import br.gov.bcb.sisaps.src.validacao.RegraSinteseMatrizSalvarValidacaoCampos;
import br.gov.bcb.sisaps.src.vo.ArcNotasVO;
import br.gov.bcb.sisaps.src.vo.CelulaRiscoControleVO;
import br.gov.bcb.sisaps.src.vo.LinhaNotasMatrizVO;
import br.gov.bcb.sisaps.util.Util;
import br.gov.bcb.sisaps.util.enumeracoes.TipoSubEventoPerfilRiscoSRC;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;

@Service
@Transactional(readOnly = true)
public class SinteseDeRiscoMediator {

    @Autowired
    private SinteseDeRiscoDAO sinteseDeRiscoDAO;

    @Autowired
    private LinhaMatrizMediator linhaMatrizMediator;

    @Autowired
    private PerfilRiscoMediator perfilRiscoMediator;

    @Autowired
    private EventoConsolidadoMediator eventoConsolidadoMediator;

    public static SinteseDeRiscoMediator get() {
        return SpringUtils.get().getBean(SinteseDeRiscoMediator.class);
    }

    public SinteseDeRisco getUltimaSinteseParametroGrupoRisco(ParametroGrupoRiscoControle parametroGrupoRisco,
            Ciclo ciclo, List<VersaoPerfilRisco> versoesPerfilRisco) {
        return sinteseDeRiscoDAO.getUltimaSinteseParametroGrupoRisco(parametroGrupoRisco, ciclo, versoesPerfilRisco);
    }

    public SinteseDeRisco getUltimaSinteseParametroGrupoRiscoEdicao(ParametroGrupoRiscoControle parametroGrupoRisco,
            Ciclo ciclo) {
        return sinteseDeRiscoDAO.getUltimaSinteseParametroGrupoRiscoEdicao(parametroGrupoRisco, ciclo);
    }

    @Transactional
    public void salvarOuAtualizarSintese(SinteseDeRisco sinteseDeRisco) {
        if (SisapsUtil.isTextoCKEditorBrancoOuNulo(sinteseDeRisco.getDescricaoSintese())) {
            sinteseDeRisco.setDescricaoSintese(null);
        }
        sinteseDeRiscoDAO.saveOrUpdate(sinteseDeRisco);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public SinteseDeRisco concluirNovaSinteseMatrizVigente(SinteseDeRisco sintese, Ciclo ciclo,
            boolean isVersionarPerfilRisco) {
        if (sintese.getPk() == null) {
            sinteseDeRiscoDAO.save(sintese);
            sinteseDeRiscoDAO.getSessionFactory().getCurrentSession().flush();
        }
        SinteseDeRisco sinteseMatrizBD = sinteseDeRiscoDAO.buscarSinteseMatrizPorPk(sintese.getPk());
        new RegraSinteseMatrizSalvarValidacaoCampos(sinteseMatrizBD).validar();
        VersaoPerfilRisco versaoPerfilRisco = null;
        if (isVersionarPerfilRisco) {
            versaoPerfilRisco =
                    perfilRiscoMediator.gerarNovaVersaoPerfilRisco(ciclo, sinteseMatrizBD.getSinteseAnterior(),
                            TipoObjetoVersionadorEnum.SINTESE_RISCO);
            sinteseMatrizBD.setVersaoPerfilRisco(versaoPerfilRisco);
        } else {
            VersaoPerfilRisco versaoAExcluir =
                    sinteseMatrizBD.getSinteseAnterior() == null ? null : sinteseMatrizBD.getSinteseAnterior()
                            .getVersaoPerfilRisco();
            perfilRiscoMediator.excluirVersaoDoPerfilRiscoAtualEIncluirNovaVersao(ciclo, versaoAExcluir,
                    sinteseMatrizBD, TipoObjetoVersionadorEnum.SINTESE_RISCO);
        }
        sinteseDeRiscoDAO.update(sinteseMatrizBD);
        sinteseDeRiscoDAO.getSessionFactory().getCurrentSession().flush();
        eventoConsolidadoMediator.incluirEventoPerfilDeRisco(ciclo, TipoSubEventoPerfilRiscoSRC.SINTESE_RISCO);
        return sinteseMatrizBD;
    }

    public List<SinteseDeRisco> buscarSintesesDeRiscoPerfilRisco(List<VersaoPerfilRisco> versoesPerfilRisco) {
        if (CollectionUtils.isEmpty(versoesPerfilRisco)) {
            return null;
        } else {
            return sinteseDeRiscoDAO.buscarSintesesDeRiscoPerfilRisco(versoesPerfilRisco);
        }
    }

    public String obterDescricaoRisco(List<CelulaRiscoControleVO> celulas, Matriz matriz,
            List<VersaoPerfilRisco> versoesPerfilRiscoARCs, boolean notaVigente,
            List<ParametroGrupoRiscoControle> grupoRiscoControle, List<LinhaNotasMatrizVO> notaResiduais,
            List<LinhaNotasMatrizVO> mediaResiduais, PerfilAcessoEnum perfilMenu, PerfilRisco perfilRisco) {
        ParametroNota parametroNota = null;

        if (matriz != null) {
            linhaMatrizMediator.montarListaNotasEMediasResiduaisVO(matriz, versoesPerfilRiscoARCs, notaVigente,
                    grupoRiscoControle, notaResiduais, mediaResiduais, celulas, perfilMenu, perfilRisco);

            if (!"".equals(mediaResiduais.get(0).getNota()) && !"*A".equals(mediaResiduais.get(0).getNota())) {
                parametroNota =
                        ParametroNotaMediator.get().buscarPorMetodologiaENota(matriz.getCiclo().getMetodologia(),
                                new BigDecimal(mediaResiduais.get(0).getNota().replace(',', '.')), true);
            }
        }

        return parametroNota == null ? "" : parametroNota.getDescricao();
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public String concluirNovaSinteseMatrizVigenteEPublicarARCs(SinteseDeRisco sintese, Ciclo ciclo) {
        Util.setIncluirBufferAnexos(true);
        BufferAnexos.resetLocalThreadBufferInclusao();
        PerfilRisco perfilRiscoAtual = perfilRiscoMediator.obterPerfilRiscoAtual(ciclo.getPk());

        if (TipoParametroGrupoRiscoControleEnum.EXTERNO.equals(sintese.getParametroGrupoRiscoControle().getTipoGrupo())) {
            AvaliacaoRiscoControle arc =
                    AvaliacaoRiscoControleMediator.get().buscarArcExternoPorPerfilRisco(perfilRiscoAtual.getPk());

            AvaliacaoRiscoControle novoARCRisco = AvaliacaoRiscoControleMediator.get().publicarARC(ciclo, arc);

            perfilRiscoMediator.gerarVersaoPerfilRiscoPublicacaoArcExterno(perfilRiscoAtual.getCiclo(), novoARCRisco,
                    arc);

        } else {

            Map<CelulaRiscoControle, CelulaRiscoControle> mapeamentoCelulaAnteriorCelulaNova =
                    new HashMap<CelulaRiscoControle, CelulaRiscoControle>();


            List<CelulaRiscoControle> celulasRiscoControle =
                    ParametroGrupoRiscoControleMediator.get().buscarCelulasARCsAnalisadosGrupoRiscoControleEversao(
                            sintese.getParametroGrupoRiscoControle(), perfilRiscoAtual.getVersoesPerfilRisco());
            List<AvaliacaoRiscoControle> novosARCs = new ArrayList<AvaliacaoRiscoControle>();

            for (CelulaRiscoControle celulaRiscoControle : celulasRiscoControle) {
                CelulaRiscoControle novaCelula =
                        CelulaRiscoControleMediator.get().duplicarCelulaRiscoControlePublicacaoARC(celulaRiscoControle);
                setARCRisco(ciclo, celulaRiscoControle, novaCelula, novosARCs);
                setARCControle(ciclo, celulaRiscoControle, novaCelula, novosARCs);
                mapeamentoCelulaAnteriorCelulaNova.put(celulaRiscoControle, novaCelula);
            }

            perfilRiscoMediator.gerarVersaoPerfilRiscoPublicacaoCelulasARCs(ciclo, mapeamentoCelulaAnteriorCelulaNova,
                    novosARCs);

            CelulaRiscoControleMediator.get().atualizarARCsCelulasMatrizEsbocada(ciclo, novosARCs);

        }
        if (sintese.getPk() != null) {
            concluirNovaSinteseMatrizVigente(sintese, ciclo, false);
        } else {
            eventoConsolidadoMediator.incluirEventoPerfilDeRisco(ciclo, TipoSubEventoPerfilRiscoSRC.SINTESE_RISCO);
        }
        GeradorAnexoMediator.get().incluirAnexosBuffer();
        return "Síntese confirmada com sucesso.";
    }

    private void setARCControle(Ciclo ciclo, CelulaRiscoControle celulaRiscoControle, CelulaRiscoControle novaCelula,
            List<AvaliacaoRiscoControle> novosARCs) {
        if (celulaRiscoControle.getArcControle().getEstado().equals(EstadoARCEnum.ANALISADO)) {
            AvaliacaoRiscoControle novoARCRisco =
                    AvaliacaoRiscoControleMediator.get().publicarARC(ciclo, celulaRiscoControle.getArcControle());
            novaCelula.setArcControle(novoARCRisco);
            novosARCs.add(novoARCRisco);
        } else {
            novaCelula.setArcControle(celulaRiscoControle.getArcControle());
        }
    }

    private void setARCRisco(Ciclo ciclo, CelulaRiscoControle celulaRiscoControle, CelulaRiscoControle novaCelula,
            List<AvaliacaoRiscoControle> novosARCs) {
        if (celulaRiscoControle.getArcRisco().getEstado().equals(EstadoARCEnum.ANALISADO)) {
            AvaliacaoRiscoControle novoARCRisco =
                    AvaliacaoRiscoControleMediator.get().publicarARC(ciclo, celulaRiscoControle.getArcRisco());
            novaCelula.setArcRisco(novoARCRisco);
            novosARCs.add(novoARCRisco);
        } else {
            novaCelula.setArcRisco(celulaRiscoControle.getArcRisco());
        }
    }

    public SinteseDeRisco buscarSinteseMatrizPorPk(Integer pkSintese) {
        return sinteseDeRiscoDAO.buscarSinteseMatrizPorPk(pkSintese);
    }

    @Transactional
    public void criarSintesesNovoCiclo(PerfilRisco perfilRiscoCicloAtual, Ciclo novoCiclo) {
        // Criar sínteses vigentes e de rascunho com os mesmos dados das sínteses vigentes do ciclo Corec.
        List<SinteseDeRisco> sintesesVigentesCicloAtual = 
                PerfilRiscoMediator.get().getSintesesDeRiscoPerfilRisco(perfilRiscoCicloAtual);
        for (SinteseDeRisco sinteseVigenteCicloAtual : sintesesVigentesCicloAtual) {
            SinteseDeRisco novaSinteseVigente = new SinteseDeRisco();
            novaSinteseVigente.setCiclo(novoCiclo);
            novaSinteseVigente.setSinteseAnterior(sinteseVigenteCicloAtual);
            novaSinteseVigente.setRisco(sinteseVigenteCicloAtual.getRisco());
            novaSinteseVigente.setDescricaoSintese(sinteseVigenteCicloAtual.getDescricaoSintese());
            novaSinteseVigente.setParametroGrupoRiscoControle(sinteseVigenteCicloAtual.getParametroGrupoRiscoControle());
            novaSinteseVigente.setOperadorAtualizacao(sinteseVigenteCicloAtual.getOperadorAtualizacao());
            novaSinteseVigente.setUltimaAtualizacao(sinteseVigenteCicloAtual.getUltimaAtualizacao());
            PerfilRiscoMediator.get().incluirVersaoPerfilRiscoAtual(novoCiclo, novaSinteseVigente, 
                    TipoObjetoVersionadorEnum.SINTESE_RISCO);
            novaSinteseVigente.setAlterarDataUltimaAtualizacao(false);
            sinteseDeRiscoDAO.save(novaSinteseVigente);
        }
    }
    
    public boolean botaoConcluirHabilitado(Matriz matriz, ParametroGrupoRiscoControle parametroGrupoRisco, 
            boolean existeGrupoRiscoMatriz, SinteseDeRisco vigente, SinteseDeRisco edicao,
            ArcNotasVO arcExterno) {
        
        if (vigente == null && edicao.getPk() == null) {
            return false;
        } else if (vigente == null && edicao.getPk() != null) {
            return true;
        } else if (!sinteseRascunhoIgualVigente(existeGrupoRiscoMatriz, vigente, edicao)) {
            return true;
        } else if (ParametroGrupoRiscoControleMediator.get().existeARCAnalisadoGrupoRisco(parametroGrupoRisco, matriz)) {
            return true;
        } else if (arcExterno != null && (arcExterno.getNotaSupervisor() != null || arcExterno.getValorNota() != null)) {
            return true;
        }

        return false;
    }

    private boolean sinteseRascunhoIgualVigente(boolean existeGrupoRiscoMatriz, 
            SinteseDeRisco vigente, SinteseDeRisco edicao) {
        if (edicao.getNomeOperadorDataHora() == null) {
            return true;
        } else if (existeGrupoRiscoMatriz && vigente != null 
                && vigente.getDescricaoSintese().equals(edicao.getDescricaoSintese())) {
            return true;
        } else if (!existeGrupoRiscoMatriz && vigente != null
                && vigente.getDescricaoSintese().equals(edicao.getDescricaoSintese())
                && vigente.getRisco() != null && edicao.getRisco() != null 
                && vigente.getRisco().getPk().equals(edicao.getRisco().getPk())) {
            return true;
        }
        
        return false;
    }
    
    public List<SinteseDeRisco> buscarSintesePorCiclo(Integer pkCiclo) {
        return sinteseDeRiscoDAO.buscarSintesesPorCiclo(pkCiclo);
    }
    
    @Transactional
    public void atualizarDadosNovaMetodologia(List<SinteseDeRisco> sinteses, Metodologia metodologia) {
        for (SinteseDeRisco sintese : sinteses) {
            if (sintese.getRisco() != null) {
                ParametroNota novaSintese =
                        ParametroNotaMediator.get().buscarNota(metodologia,
                                sintese.getRisco().getValor());
                sintese.setRisco(novaSintese);
            }
            sintese.setAlterarDataUltimaAtualizacao(false);
            sinteseDeRiscoDAO.update(sintese);
        }
    }

}
