package br.gov.bcb.sisaps.src.mediator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.src.dao.EstadoCicloDAO;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.EstadoCiclo;
import br.gov.bcb.sisaps.src.dominio.VersaoPerfilRisco;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoCicloEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoObjetoVersionadorEnum;
import br.gov.bcb.sisaps.src.vo.ConsultaEstadoCicloVO;
import br.gov.bcb.sisaps.src.vo.EstadoCicloVO;

@Service
public class EstadoCicloMediator extends AbstractMediatorPaginado<EstadoCicloVO, Integer, ConsultaEstadoCicloVO> {

    @Autowired
    private EstadoCicloDAO estadoCicloDAO;
    
    @Autowired
    private VersaoPerfilRiscoMediator versaoPerfilRiscoMediator;

    @Override
    protected EstadoCicloDAO getDao() {
        return estadoCicloDAO;
    }

    public static EstadoCicloMediator get() {
        return SpringUtils.get().getBean(EstadoCicloMediator.class);
    }

    @Transactional
    public void incluir(Ciclo ciclo) {
        EstadoCiclo estadoCiclo = new EstadoCiclo();
        estadoCiclo.setEstado(EstadoCicloEnum.EM_ANDAMENTO);
        estadoCiclo.setCiclo(ciclo);
        VersaoPerfilRisco versao = versaoPerfilRiscoMediator.criarVersao(
                estadoCiclo, TipoObjetoVersionadorEnum.ESTADO_CICLO);
        estadoCiclo.setVersaoPerfilRisco(versao);
        estadoCicloDAO.save(estadoCiclo);
        ciclo.setEstadoCiclo(estadoCiclo);
    }
    
    @Transactional
    public void alterar(EstadoCiclo estadoCiclo) {
        estadoCicloDAO.update(estadoCiclo);
    }
    
    @Transactional
    public void save(EstadoCiclo estadoCiclo) {
        estadoCicloDAO.save(estadoCiclo);
    }
    
    public EstadoCiclo buscarPorPk(Integer pk) {
        return estadoCicloDAO.load(pk);
    }
    
    public EstadoCiclo buscarPorPerfilRisco(Integer pkPerfilRisco) {
        return estadoCicloDAO.buscarPorPerfilRisco(pkPerfilRisco);
    }

    public String obterLabelCorec(EstadoCiclo estado) {
        if (estado != null && estado.getEstado().equals(EstadoCicloEnum.EM_ANDAMENTO)) {
            return "Corec previsto";
        } else {
            return "Corec";
        }
    }
    
    
    
    public String obterLabelCorecAgenda(EstadoCiclo estado) {
        if (estado != null && (estado.getEstado().equals(EstadoCicloEnum.EM_ANDAMENTO) || estado.getEstado().equals(EstadoCicloEnum.COREC))) {
            return "Corec previsto";
        } else {
            return "Corec";
        }
    }
}
