package crt2.dominio.corec.ajustenotasanefscomite;

import java.util.ArrayList;
import java.util.List;

import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.ParametroNotaAQTMediator;
import crt2.ConfiguracaoTestesNegocio;


public class TestR005AjusteNotasANEFsComite extends ConfiguracaoTestesNegocio {
    
    public String ajustarNotasANEFsCorec(int cicloPk, int idANEF4, int notaANEF4, int idANEF5, int notaANEF5, int idANEF6) {

        List<AnaliseQuantitativaAQT> listaAnef = new ArrayList<AnaliseQuantitativaAQT>();

        AnaliseQuantitativaAQT anef4 = AnaliseQuantitativaAQTMediator.get().buscar(idANEF4);
        anef4.setAlterouNota(true);
        anef4.setNotaCorecAtual(ParametroNotaAQTMediator.get().buscarPorPK(notaANEF4));
        listaAnef.add(anef4);

        AnaliseQuantitativaAQT anef5 = AnaliseQuantitativaAQTMediator.get().buscar(idANEF5);
        anef5.setAlterouNota(true);
        anef5.setNotaCorecAtual(ParametroNotaAQTMediator.get().buscarPorPK(notaANEF5));
        listaAnef.add(anef5);

        AnaliseQuantitativaAQT anef6 = AnaliseQuantitativaAQTMediator.get().buscar(idANEF6);
        anef6.setAlterouNota(false);
        anef6.setNotaCorecAtual(null);
        listaAnef.add(anef6);

        return AnaliseQuantitativaAQTMediator.get().ajustarNotasANEFsCorec(listaAnef);

    }

}
