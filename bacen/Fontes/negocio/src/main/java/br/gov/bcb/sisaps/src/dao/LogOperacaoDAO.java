package br.gov.bcb.sisaps.src.dao;

import org.hibernate.Criteria;
import org.springframework.stereotype.Repository;

import br.gov.bcb.sisaps.dao.GenericDAOParaListagens;
import br.gov.bcb.sisaps.src.dominio.LogOperacao;
import br.gov.bcb.sisaps.src.vo.ConsultaLogOperacaoVO;
import br.gov.bcb.sisaps.src.vo.LogOperacaoVO;

@Repository
public class LogOperacaoDAO extends GenericDAOParaListagens<LogOperacao, Integer, LogOperacaoVO, ConsultaLogOperacaoVO> {

    public LogOperacaoDAO() {
        super(LogOperacao.class);
    }

    @Override
    protected void montarCriterios(Criteria criteria, ConsultaLogOperacaoVO consulta) {
        //TODO não precisa implementar
    }

}
