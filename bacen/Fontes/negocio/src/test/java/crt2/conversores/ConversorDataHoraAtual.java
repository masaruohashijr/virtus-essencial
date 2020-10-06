package crt2.conversores;

import org.specrunner.converters.ConverterException;
import org.specrunner.converters.core.ConverterDateCurrentTemplate;

import br.gov.bcb.sisaps.util.geral.DataUtil;

public class ConversorDataHoraAtual extends ConverterDateCurrentTemplate {
    
    public ConversorDataHoraAtual() {
        super(new String[] {"hora atual", "data hora atual", "current time", "current timestamp"});
    }
    
    @Override
    public Object convert(Object value, Object[] args) throws ConverterException {
        return DataUtil.getDateTimeAtual().toDate();
    }

}
