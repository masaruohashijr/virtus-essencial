package crt2.dominio.administrador.editaremail;

import br.gov.bcb.sisaps.src.dominio.ParametroEmail;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoEmailCorecEnum;
import br.gov.bcb.sisaps.src.mediator.ParametroEmailMediator;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import crt2.ConfiguracaoTestesNegocio;

public class TestR002EditarEmailApresentacao extends ConfiguracaoTestesNegocio {
    
    public String salvarParametroEmail(String tipoEmail, String remetente, String titulo, String corpo, Integer prazo) {
        TipoEmailCorecEnum tipoEmailCorecEnum = TipoEmailCorecEnum.valueOfDescricao(tipoEmail);
        ParametroEmail parametroEmail = ParametroEmailMediator.get().buscarPorTipo(tipoEmailCorecEnum);
        parametroEmail.setRemetente(remetente);
        parametroEmail.setTitulo(titulo);
        parametroEmail.setCorpo(corpo);
        parametroEmail.setPrazo(prazo);
        try {
            return ParametroEmailMediator.get().salvar(parametroEmail);
        } catch (NegocioException e) {
            erro = e;
        }
        return erro == null ? "" : erro.getMensagens().get(0).getMessage();
    }

}
