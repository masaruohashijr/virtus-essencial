package br.gov.bcb.sisaps.src.mediator;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.src.dao.MetodologiaDAO;
import br.gov.bcb.sisaps.src.dominio.AjusteCorec;
import br.gov.bcb.sisaps.src.dominio.Atividade;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControleExterno;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.GrauPreocupacaoES;
import br.gov.bcb.sisaps.src.dominio.Metodologia;
import br.gov.bcb.sisaps.src.dominio.NotaMatriz;
import br.gov.bcb.sisaps.src.dominio.ParametroGrupoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.ParametroPeso;
import br.gov.bcb.sisaps.src.dominio.SinteseDeRisco;
import br.gov.bcb.sisaps.src.dominio.VersaoPerfilRisco;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.NotaAjustadaAEF;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.NotaAjustadaAEFMediator;
import br.gov.bcb.sisaps.src.vo.ConsultaMetodologiaVO;
import br.gov.bcb.sisaps.src.vo.MetodologiaVO;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import br.gov.bcb.utils.logging.BCLogFactory;
import br.gov.bcb.utils.logging.BCLogger;

@Service
@Transactional(readOnly = true)
public class MetodologiaMediator extends AbstractMediatorPaginado<MetodologiaVO, Integer, ConsultaMetodologiaVO> {
    
    private static final BCLogger LOG = BCLogFactory.getLogger("MetodologiaMediator");

    @Autowired
    private MetodologiaDAO metodologiaDAO;

    @Override
    protected MetodologiaDAO getDao() {
        return metodologiaDAO;
    }

    public static MetodologiaMediator get() {
        return SpringUtils.get().getBean(MetodologiaMediator.class);
    }

    @Transactional(readOnly = true)
    public Metodologia buscarMetodologiaPorPK(Integer id) {
        return metodologiaDAO.buscarMetodologiaPorPK(id);
    }

    public ParametroPeso buscarMaiorPesoMetodologia(Metodologia met) {
        return metodologiaDAO.buscarMaiorPesoMetodologia(met);
    }

    @Transactional(readOnly = true)
    public List<ParametroPeso> buscarListaParametroPesoMetodologia(Metodologia metodologia) {
        return metodologiaDAO.buscarListaParametroPesoMetodologia(metodologia);
    }

    public List<ParametroGrupoRiscoControle> buscarGrupoParametroGrupoRisco(List<Atividade> listaAtividade,
            List<VersaoPerfilRisco> versoesPerfilRiscoARCs) {
        List<ParametroGrupoRiscoControle> lista = new LinkedList<ParametroGrupoRiscoControle>();
        if (listaAtividade.isEmpty()) {
            return lista;
        }
        return metodologiaDAO.buscarGrupoParametroGrupoRisco(listaAtividade, versoesPerfilRiscoARCs);
    }

    @Transactional(readOnly = true)
    public Metodologia loadPK(Integer pk) {
        Metodologia metodologia = metodologiaDAO.load(pk);
        inicializarDependencias(metodologia);
        return metodologia;
    }

    public void inicializarDependencias(Metodologia metodologia) {

        if (metodologia.getParametrosTipoAtividadeNegocio() != null) {
            Hibernate.initialize(metodologia.getParametrosTipoAtividadeNegocio());
        }
        if (metodologia.getParametrosGrupoRiscoControle() != null) {
            Hibernate.initialize(metodologia.getParametrosGrupoRiscoControle());
        }
        if (metodologia.getParametrosFatorRelevancia() != null) {
            Hibernate.initialize(metodologia.getParametrosFatorRelevancia());
        }
        if (metodologia.getParametrosPeso() != null) {
            Hibernate.initialize(metodologia.getParametrosPeso());
        }
        if (metodologia.getParametrosNotaAQT() != null) {
            Hibernate.initialize(metodologia.getParametrosNotaAQT());
        }

    }

    public List<ParametroGrupoRiscoControle> buscarApenasRiscos(Metodologia metodologia) {
        return metodologiaDAO.buscarApenasRiscos(metodologia);
    }

    public Metodologia buscarMetodologiaDefault() {
        return metodologiaDAO.buscarMetodologiaDefault();
    }
    
    @Transactional
    public void migrarDadosNovaMetodologia(Integer pkCiclo, boolean isMediaArc, boolean isMediaAnef) {
        
        // Ciclo
        Ciclo ciclo = CicloMediator.get().buscarCicloPorPK(pkCiclo);
        
        if (ciclo.getMetodologia().getMetodologiaDefault().equals(SimNaoEnum.NAO)) {
            
            // Nova metodologia
            Metodologia novaMetodologia = MetodologiaMediator.get().buscarMetodologiaDefault();
            
            // Nova metodologia no ciclo
            ciclo.setMetodologia(novaMetodologia);
            ciclo.setAlterarDataUltimaAtualizacao(false);
            CicloMediator.get().alterar(ciclo);
            
            // Dados Nota Final ES
            List<GrauPreocupacaoES> listaGrauPreocupacao = GrauPreocupacaoESMediator.get().buscarPorCiclo(pkCiclo);
            if (!CollectionUtils.isEmpty(listaGrauPreocupacao)) {
                LOG.info("QUANTIDADE DE GRAUS DE PREOCUPAÇÃO: " + listaGrauPreocupacao.size());
                GrauPreocupacaoESMediator.get().atualizarDadosNovaMetodologia(listaGrauPreocupacao, novaMetodologia);
            }
            
            // Dados Corec
            AjusteCorec ajusteCorec = AjusteCorecMediator.get().buscarPorCiclo(pkCiclo);
            if (ajusteCorec != null) {
                AjusteCorecMediator.get().atualizarDadosNovaMetodologia(ajusteCorec, novaMetodologia);
            }
            
            // Dados ARCs
            List<AvaliacaoRiscoControle> arcs = AvaliacaoRiscoControleMediator.get().buscarArcPorCiclo(pkCiclo);
            if (!CollectionUtils.isEmpty(arcs)) {
                LOG.info("QUANTIDADE DE ARCS: " + arcs.size());
                AvaliacaoRiscoControleMediator.get().atualizarDadosNovaMetodologia(arcs, novaMetodologia, isMediaArc);
            }
            
            // Dados ARCs Externos
            List<AvaliacaoRiscoControleExterno> arcsExternos = AvaliacaoRiscoControleExternoMediator.get().buscarArcExternoPorCiclo(pkCiclo);
            if (!CollectionUtils.isEmpty(arcsExternos)) {
                LOG.info("QUANTIDADE DE ARCS EXTERNOS: " + arcs.size());
                AvaliacaoRiscoControleMediator.get().atualizarDadosNovaMetodologiaArcExterno(arcsExternos, novaMetodologia, isMediaArc);
            }
            
            // Dados ANEFs
            List<AnaliseQuantitativaAQT> anefs = AnaliseQuantitativaAQTMediator.get().buscarAnefPorCiclo(pkCiclo);
            if (!CollectionUtils.isEmpty(anefs)) {
                LOG.info("QUANTIDADE DE ANEFS: " + anefs.size());
                AnaliseQuantitativaAQTMediator.get().atualizarDadosNovaMetodologia(anefs, novaMetodologia, isMediaAnef);
            }
            
            // Dados Sínteses ARC
            List<SinteseDeRisco> sinteses = SinteseDeRiscoMediator.get().buscarSintesePorCiclo(pkCiclo);
            if (!CollectionUtils.isEmpty(sinteses)) {
                LOG.info("QUANTIDADE DE SINTESES ARC: " + sinteses.size());
                SinteseDeRiscoMediator.get().atualizarDadosNovaMetodologia(sinteses, novaMetodologia);
            }
            
            // Nota ajustada matriz
            List<NotaMatriz> notasMatriz = NotaMatrizMediator.get().buscarNotaMatrizPorCiclo(pkCiclo);
            if (!CollectionUtils.isEmpty(notasMatriz)) {
                LOG.info("QUANTIDADE DE NOTAS AJUSTADAS MATRIZ: " + notasMatriz.size());
                NotaMatrizMediator.get().atualizarDadosNovaMetodologia(notasMatriz, novaMetodologia);
            }
            
            // Nota ajustada anef
            List<NotaAjustadaAEF> notasAEF = NotaAjustadaAEFMediator.get().buscarNotaAjustadaAEFPorCiclo(pkCiclo);
            if (!CollectionUtils.isEmpty(notasAEF)) {
                LOG.info("QUANTIDADE DE NOTAS AJUSTADAS ANEF: " + notasAEF.size());
                NotaAjustadaAEFMediator.get().atualizarDadosNovaMetodologia(notasAEF, novaMetodologia);
            }
        }
        
    }
}