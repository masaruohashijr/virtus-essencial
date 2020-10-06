package br.gov.bcb.sisaps.src.mediator.analisequantitativa;

import java.util.List;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.sisaps.src.dao.analisequantitativa.DataBaseESDAO;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.DataBaseES;
import br.gov.bcb.sisaps.util.spring.SpringUtilsExtended;

@Service
public class DataBaseESMediator {

    @Autowired
    private DataBaseESDAO dataBaseESDAO;

    public static DataBaseESMediator get() {
        return SpringUtilsExtended.get().getBean(DataBaseESMediator.class);
    }

    @Transactional(readOnly = true)
    public List<DataBaseES> consultarDatasBaseDisponiveis(PerfilRisco perfilRisco) {
        return dataBaseESDAO.listarDataBaseES(perfilRisco);
    }

    @Transactional(readOnly = true)
    public DataBaseES buscarPorIdentificador(Integer codigoDataBase, String cnpj) {
        DataBaseES dataBaseES = dataBaseESDAO.buscarPorCodigoDataBase(codigoDataBase, cnpj);
        if (dataBaseES != null) {
            Hibernate.initialize(dataBaseES);
        }
        return dataBaseES;
    }

    @Transactional(readOnly = true)
    public DataBaseES obterDataBaseESRecente(PerfilRisco perfilRisco) {
        return dataBaseESDAO.obterDataBaseESRecente(perfilRisco);
    }
}
