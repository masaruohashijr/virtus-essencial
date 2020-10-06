package crt2.dominio.analisequantitativa.analisarconcluiranef;

import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import crt2.ConfiguracaoTestesNegocio;

public class TestR003AnalisarConcluirANEF extends ConfiguracaoTestesNegocio {
    public String concluirEdicaoAnef(Integer integer) {
        AnaliseQuantitativaAQTMediator.get().limpaSessao();
        try {
            erro = null;
            AnaliseQuantitativaAQT aqt = AnaliseQuantitativaAQTMediator.get().buscar(integer);
            return AnaliseQuantitativaAQTMediator.get().concluirAnaliseANEFSupervisor(aqt, perfilUsuario());
        } catch (NegocioException e) {
            erro = e;
        }
        return "";
    }
}
