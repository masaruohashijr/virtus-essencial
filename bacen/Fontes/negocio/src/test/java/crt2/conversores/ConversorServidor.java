package crt2.conversores;

import org.specrunner.converters.ConverterException;
import org.specrunner.converters.core.ConverterNotNullNotEmpty;

import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;

public class ConversorServidor extends ConverterNotNullNotEmpty {
    
    @Override
    public Object convert(Object value, Object[] args) throws ConverterException {
        String[] parametros = String.valueOf(value).split(";");
        ServidorVO servidorVO = new ServidorVO();
        servidorVO.setMatricula(parametros[0]);
        servidorVO.setNome(parametros[1]);
        return servidorVO;
    }

}
