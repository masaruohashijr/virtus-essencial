package br.gov.bcb.sisaps.src.mediator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.src.dao.AvaliacaoRiscoControleExternoDao;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControleExterno;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.ParametroGrupoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoARCEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoGrupoEnum;
import br.gov.bcb.sisaps.src.vo.ArcNotasVO;

@Service
@Transactional(readOnly = true)
public class AvaliacaoRiscoControleExternoMediator {
    
    @Autowired
    private AvaliacaoRiscoControleExternoDao avaliacaoRiscoControleExternoDao;

    public static AvaliacaoRiscoControleExternoMediator get() {
        return SpringUtils.get().getBean(AvaliacaoRiscoControleExternoMediator.class);
    }
    
    public void evict(AvaliacaoRiscoControleExterno arcExterno) {
        avaliacaoRiscoControleExternoDao.evict(arcExterno);
    }
    
    @Transactional
    public void criarArcExterno(Matriz matriz) {
        if (matriz.getPercentualGovernancaCorporativoInt() > 0) {
            ParametroGrupoRiscoControle parametroRCExterno = 
                    ParametroGrupoRiscoControleMediator.get().buscarParametroRCExterno(
                            matriz.getCiclo().getMetodologia());
            
            if (!avaliacaoRiscoControleExternoDao.existeArcExternoCiclo(
                    matriz.getCiclo().getPk(), parametroRCExterno.getPk())) {
                AvaliacaoRiscoControle arc = new AvaliacaoRiscoControle();
                arc.setTipo(TipoGrupoEnum.EXTERNO);
                arc.setEstado(EstadoARCEnum.PREVISTO);
                AvaliacaoRiscoControleMediator.get().incluir(arc);
                
                AvaliacaoRiscoControleExterno externo = new AvaliacaoRiscoControleExterno();
                externo.setAvaliacaoRiscoControle(arc);
                externo.setCiclo(matriz.getCiclo());
                externo.setParametroGrupoRiscoControle(parametroRCExterno);
                avaliacaoRiscoControleExternoDao.save(externo);
            }
        }
    }
    
    public AvaliacaoRiscoControle buscarUltimoArcExterno(Ciclo ciclo) {
        return avaliacaoRiscoControleExternoDao.buscarUltimoArcExterno(ciclo.getPk());
    }
    
    public ArcNotasVO buscarUltimoArcExternoVO(Integer pkCiclo) {
        ArcNotasVO arc = avaliacaoRiscoControleExternoDao.buscarUltimoArcExternoVO(pkCiclo);
        if (arc != null && arc.getArcVigentePk() != null) {
            arc.setArcVigente(AvaliacaoRiscoControleMediator.get().consultarNotasArcVigente(arc.getArcVigentePk()));
        }
        return arc;
    }

    public AvaliacaoRiscoControleExterno buscarArcExterno(Integer pkArc) {
        return avaliacaoRiscoControleExternoDao.buscarArcExterno(pkArc);
    }

    @Transactional
    public void salvarArcExterno(AvaliacaoRiscoControleExterno arc) {
        avaliacaoRiscoControleExternoDao.save(arc);
        avaliacaoRiscoControleExternoDao.flush();
    }
    
    @Transactional
    public void criarArcExternoNovoCiclo(PerfilRisco perfilRiscoCicloAtual, Ciclo novoCiclo) {
        AvaliacaoRiscoControle arc = PerfilRiscoMediator.get().getArcExterno(perfilRiscoCicloAtual);
        AvaliacaoRiscoControleExterno arcExterno = buscarArcExterno(arc.getPk());
        AvaliacaoRiscoControleMediator.get().criarARCNovoCiclo(
                perfilRiscoCicloAtual.getCiclo(), novoCiclo, arcExterno.getParametroGrupoRiscoControle(), arc);
    }

    public AvaliacaoRiscoControle buscarArcExternoPerfilAtual(Ciclo ciclo, PerfilRisco perfil) {
        AvaliacaoRiscoControle arcExterno = null;
        Matriz matriz = PerfilRiscoMediator.get().getMatrizAtualPerfilRisco(perfil);
        if (matriz.getPercentualGovernancaCorporativoInt() > 0) {
            arcExterno = avaliacaoRiscoControleExternoDao.buscarUltimoArcExterno(ciclo.getPk());
        }
        return arcExterno;
    }
    
    public List<AvaliacaoRiscoControleExterno> buscarArcExternoPorCiclo(Integer pkCiclo) {
        return avaliacaoRiscoControleExternoDao.buscarArcExternoPorCiclo(pkCiclo);
    }

}
