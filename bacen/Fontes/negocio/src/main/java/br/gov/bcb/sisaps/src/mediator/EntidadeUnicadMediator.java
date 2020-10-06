package br.gov.bcb.sisaps.src.mediator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.src.dao.EntidadeUnicadDao;
import br.gov.bcb.sisaps.src.dominio.EntidadeUnicad;

@Service
@Transactional(readOnly = true)
public class EntidadeUnicadMediator   {
    
    @Autowired 
    private EntidadeUnicadDao entidadeUnicadDao;
    
    public static EntidadeUnicadMediator get() {
        return SpringUtils.get().getBean(EntidadeUnicadMediator.class);
    }
    
    public EntidadeUnicad buscarEntidadeUnicadPorCnpj(String cnpj) {
        String cnpjFormatado = limparString(cnpj);
        return entidadeUnicadDao.buscarEntidadeUnicadPorCnpj(Integer.valueOf(cnpjFormatado)); 
    }
    
    public List<EntidadeUnicad> buscarEntidadesConsolidado(EntidadeUnicad entidadeUnicad) {
        return entidadeUnicadDao.buscarEntidadesConsolidado(entidadeUnicad);
    }

    private String limparString(String cnpjEs) {
        String cnpjFormatado = cnpjEs;
        cnpjFormatado = cnpjFormatado.replace(".", "");
        cnpjFormatado = cnpjFormatado.replace(",", "");
        cnpjFormatado = cnpjFormatado.replace("'", "");
        cnpjFormatado = cnpjFormatado.replace(";", "");
        cnpjFormatado = cnpjFormatado.replace("C", "");
        cnpjFormatado = cnpjFormatado.trim();
        return cnpjFormatado;
    }
}
