package br.gov.bcb.sisaps.src.mediator;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.src.dao.AtividadeCicloDAO;
import br.gov.bcb.sisaps.src.dominio.AtividadeCiclo;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoObjetoVersionadorEnum;
import br.gov.bcb.sisaps.src.vo.AtividadeCicloVO;
import br.gov.bcb.sisaps.src.vo.ConsultaAtividadeCicloVO;

@Service
@Transactional(readOnly = true)
public class AtividadeCicloMediator extends AbstractMediatorPaginado<AtividadeCicloVO, Integer, ConsultaAtividadeCicloVO> {

    @Autowired
    private CicloMediator cicloMediator;
    
    @Autowired
    private PerfilRiscoMediator perfilRiscoMediator;
    
    @Autowired
    private AtividadeCicloDAO atividadeCicloDAO;

    @Override
    protected AtividadeCicloDAO getDao() {
        return atividadeCicloDAO;
    }
    
    public static AtividadeCicloMediator get() {
        return SpringUtils.get().getBean(AtividadeCicloMediator.class);
    }
    
    @Override
    public List<AtividadeCicloVO> consultar(ConsultaAtividadeCicloVO consulta) {
        if (CollectionUtils.isNotEmpty(consulta.getVersoesPerfilRisco())) {
            return super.consultar(consulta);
        } else {
            return new ArrayList<AtividadeCicloVO>();
        }
    }
    
    public AtividadeCiclo buscarUltimaAtividadeCiclo(String cnpjES, String codigoAtividade) {
    	
        Ciclo ciclosES = buscarCicloAtual(cnpjES);
        PerfilRisco perfilRiscoCicloAtual = perfilRiscoMediator.obterPerfilRiscoAtual(ciclosES.getPk());
        return atividadeCicloDAO.buscarUltimaAtividadeCiclo(cnpjES, perfilRiscoCicloAtual, codigoAtividade);
    }
    
    public List<AtividadeCiclo> buscarAtividadesCiclo(Short ano) {
        return atividadeCicloDAO.buscarAtividadesCiclo(ano);
    }
    
    @Transactional
    public void incluirAtividadeCicloPerfilRiscoAtual(AtividadeCiclo atividadeCiclo) {
        Ciclo cicloAtual = buscarCicloAtual(atividadeCiclo.getCnpjES());
        perfilRiscoMediator.incluirVersaoPerfilRiscoAtual(
                cicloAtual, atividadeCiclo, TipoObjetoVersionadorEnum.ATIVIDADE_CICLO);
        atividadeCicloDAO.save(atividadeCiclo);
    }

    private Ciclo buscarCicloAtual(String cnpj) {
        List<Ciclo> ciclosES = cicloMediator.consultarCiclosEntidadeSupervisionavel(cnpj, true);
        return ciclosES.get(0);
    }
    
    @Transactional
    public void alterarAtividadeCicloPerfilRiscoAtual(AtividadeCiclo atividadeCiclo, AtividadeCiclo novaAtividadeCiclo) {
        Ciclo cicloAtual = buscarCicloAtual(atividadeCiclo.getCnpjES());
        perfilRiscoMediator.excluirVersaoDoPerfilRiscoAtualEIncluirNovaVersao(cicloAtual, 
                atividadeCiclo.getVersaoPerfilRisco(), novaAtividadeCiclo, TipoObjetoVersionadorEnum.ATIVIDADE_CICLO);
        atividadeCicloDAO.save(novaAtividadeCiclo);
    }
    
    @Transactional
    public void alterar(AtividadeCiclo atividadeCiclo) {
        atividadeCicloDAO.update(atividadeCiclo);
    }

    @Transactional
    public void excluirAtividadeCiclo(AtividadeCiclo atividadeCiclo) {
        Ciclo cicloAtual = buscarCicloAtual(atividadeCiclo.getCnpjES());
        PerfilRisco perfilRiscoAtual = perfilRiscoMediator.obterPerfilRiscoAtual(cicloAtual.getPk());
        AtividadeCiclo atividadeCicloAExcluir = atividadeCicloDAO.load(atividadeCiclo.getPk());
        if (perfilRiscoAtual.getVersoesPerfilRisco().contains(atividadeCicloAExcluir.getVersaoPerfilRisco())) {
            perfilRiscoMediator.excluirVersaoDoPerfilRisco(perfilRiscoAtual, atividadeCicloAExcluir.getVersaoPerfilRisco());
            if (!perfilRiscoMediator.versaoEmMaisDeUmPerfilRisco(atividadeCiclo.getVersaoPerfilRisco())) {
                atividadeCicloDAO.delete(atividadeCicloAExcluir);
            }
        }
    }
    
    public List<AtividadeCiclo> buscarPorPerfilRisco(Integer pkPerfilRisco) {
        return atividadeCicloDAO.buscarPorPerfilRisco(pkPerfilRisco);
    }

    @Transactional
    public void criarAtividadesNovoCiclo(PerfilRisco perfilRiscoCicloAtual, Ciclo novoCiclo) {
        List<AtividadeCiclo> atividadesCicloAtual = buscarPorPerfilRisco(perfilRiscoCicloAtual.getPk());
        for (AtividadeCiclo atividadeCicloAtual : atividadesCicloAtual) {
            criarAtividadeNovoCiclo(atividadeCicloAtual, novoCiclo);
        }
    }

    private void criarAtividadeNovoCiclo(AtividadeCiclo atividadeCicloAtual, Ciclo novoCiclo) {
        AtividadeCiclo novaAtividade = new AtividadeCiclo();
        novaAtividade.setAno(atividadeCicloAtual.getAno());
        novaAtividade.setCnpjES(atividadeCicloAtual.getCnpjES());
        novaAtividade.setCodigo(atividadeCicloAtual.getCodigo());
        novaAtividade.setDataBase(atividadeCicloAtual.getDataBase());
        novaAtividade.setDescricao(atividadeCicloAtual.getDescricao());
        novaAtividade.setSituacao(atividadeCicloAtual.getSituacao());
        novaAtividade.setUltimaAtualizacao(atividadeCicloAtual.getUltimaAtualizacao());
        novaAtividade.setOperadorAtualizacao(atividadeCicloAtual.getOperadorAtualizacao());
        perfilRiscoMediator.incluirVersaoPerfilRiscoAtual(novoCiclo, novaAtividade, 
                TipoObjetoVersionadorEnum.ATIVIDADE_CICLO);
        novaAtividade.setAlterarDataUltimaAtualizacao(false);
        atividadeCicloDAO.save(novaAtividade);
    }

}
