package br.gov.bcb.sisaps.src.mediator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.src.dao.LogOperacaoDAO;
import br.gov.bcb.sisaps.src.dominio.EntidadeSupervisionavel;
import br.gov.bcb.sisaps.src.dominio.LogOperacao;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.vo.ConsultaLogOperacaoVO;
import br.gov.bcb.sisaps.src.vo.LogOperacaoVO;
import br.gov.bcb.sisaps.util.consulta.IPaginado;

@Service
@Transactional(readOnly = true)
public class LogOperacaoMediator extends AbstractMediatorPaginado<LogOperacaoVO, Integer, ConsultaLogOperacaoVO> {


    @Autowired
    private LogOperacaoDAO logOperacaoDAO;

    public static LogOperacaoMediator get() {
        return SpringUtils.get().getBean(LogOperacaoMediator.class);
    }

    @Override
    protected IPaginado<LogOperacaoVO, Integer, ConsultaLogOperacaoVO> getDao() {
        return null;
    }
    
    @Transactional
    public void gerarLogDetalhamento(EntidadeSupervisionavel entidadeSupervisionavel, PerfilRisco perfilRisco,
            String[] dados) {
        EntidadeSupervisionavel esBase = EntidadeSupervisionavelMediator.get().loadPK(entidadeSupervisionavel.getPk());
        LogOperacao logOperacao = new LogOperacao();
        logOperacao.setCodigoCnpj(esBase.getConglomeradoOuCnpj());
        logOperacao.setNome(esBase.getNome());
        logOperacao.setPerfilRisco(perfilRisco);
        logOperacao.setTrilha(dados[0]);
        logOperacao.setCodigoTela(dados[1]);
        logOperacao.setTituloTela(dados[2]);
        logOperacaoDAO.save(logOperacao);
        logOperacaoDAO.evict(logOperacao);
    }
}
