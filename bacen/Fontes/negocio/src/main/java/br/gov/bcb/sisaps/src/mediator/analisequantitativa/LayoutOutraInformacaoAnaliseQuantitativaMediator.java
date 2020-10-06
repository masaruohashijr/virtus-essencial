package br.gov.bcb.sisaps.src.mediator.analisequantitativa;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.sisaps.src.dao.analisequantitativa.LayoutOutraInformacaoAnaliseQuantitativaDAO;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.DataBaseES;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.LayoutOutraInformacaoAnaliseQuantitativa;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.QuadroPosicaoFinanceira;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoInformacaoEnum;
import br.gov.bcb.sisaps.util.spring.SpringUtilsExtended;

@Service
public class LayoutOutraInformacaoAnaliseQuantitativaMediator {

    @Autowired
    private LayoutOutraInformacaoAnaliseQuantitativaDAO layoutOutraInformacaoAnaliseQuantitativaDAO;

    public static LayoutOutraInformacaoAnaliseQuantitativaMediator get() {
        return SpringUtilsExtended.get().getBean(LayoutOutraInformacaoAnaliseQuantitativaMediator.class);
    }

    @Transactional(readOnly = true)
    public List<LayoutOutraInformacaoAnaliseQuantitativa> obterLayout(
            DataBaseES dataBaseES, TipoInformacaoEnum tipoInformacao) {
        return layoutOutraInformacaoAnaliseQuantitativaDAO.obterLayout(dataBaseES, tipoInformacao);
    }
    
    @Transactional(readOnly = true)
    public List<LayoutOutraInformacaoAnaliseQuantitativa> obterLayoutsOutraInformacaoQuadro(
            QuadroPosicaoFinanceira quadro, TipoInformacaoEnum tipoInformacao) {
        return layoutOutraInformacaoAnaliseQuantitativaDAO.obterLayoutsOutraInformacaoQuadro(
                quadro.getPk(), tipoInformacao);
    }

}
