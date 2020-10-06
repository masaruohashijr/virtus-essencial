package br.gov.bcb.sisaps.src.mediator.analisequantitativa;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.sisaps.src.dao.analisequantitativa.LayoutContaAnaliseQuantitativaDAO;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.DataBaseES;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.LayoutContaAnaliseQuantitativa;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoConta;
import br.gov.bcb.sisaps.util.spring.SpringUtilsExtended;

@Service
public class LayoutContaAnaliseQuantitativaMediator {

    @Autowired
    private LayoutContaAnaliseQuantitativaDAO layoutContaAnaliseQuantitativaDAO;

    public static LayoutContaAnaliseQuantitativaMediator get() {
        return SpringUtilsExtended.get().getBean(LayoutContaAnaliseQuantitativaMediator.class);
    }

    @Transactional(readOnly = true)
    public List<LayoutContaAnaliseQuantitativa> obterLayoutContas(DataBaseES dataBaseES) {
        if (dataBaseES != null) {
            List<LayoutContaAnaliseQuantitativa> lista = obterLayout(dataBaseES);
            for (LayoutContaAnaliseQuantitativa layoutContaAnaliseQuantitativa : lista) {
                if (!Hibernate.isInitialized(layoutContaAnaliseQuantitativa.getContaAnaliseQuantitativa())) {
                    Hibernate.initialize(layoutContaAnaliseQuantitativa.getContaAnaliseQuantitativa());
                }
                if (layoutContaAnaliseQuantitativa.getContaAnaliseQuantitativaPai() != null
                        && !Hibernate.isInitialized(layoutContaAnaliseQuantitativa.getContaAnaliseQuantitativaPai())) {
                    Hibernate.initialize(layoutContaAnaliseQuantitativa.getContaAnaliseQuantitativaPai());
                }
            }
            return lista;
        }
        return new ArrayList<LayoutContaAnaliseQuantitativa>();
    }

    @Transactional(readOnly = true)
    public LayoutContaAnaliseQuantitativa obterLayoutPai(Integer pkContaPai, Integer pklayoutAnalise,
            TipoConta tipoConta) {
        return layoutContaAnaliseQuantitativaDAO.obterLayoutPai(pkContaPai, pklayoutAnalise, tipoConta);
    }

    @Transactional(readOnly = true)
    public List<LayoutContaAnaliseQuantitativa> obterLayout(DataBaseES dataBaseES) {
        return layoutContaAnaliseQuantitativaDAO.obterLayout(dataBaseES);
    }

}
