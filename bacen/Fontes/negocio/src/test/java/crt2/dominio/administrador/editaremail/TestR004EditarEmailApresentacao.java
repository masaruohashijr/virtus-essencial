package crt2.dominio.administrador.editaremail;

import org.apache.commons.lang.StringUtils;

import br.gov.bcb.sisaps.src.dominio.ParametroEmail;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoEmailCorecEnum;
import br.gov.bcb.sisaps.src.mediator.ParametroEmailMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR004EditarEmailApresentacao extends ConfiguracaoTestesNegocio {
    
    private String mensagemSucesso; 
    
    public void salvarParametroEmail(String tipoEmail, String remetente, String titulo, String corpo, String prazo) {
        TipoEmailCorecEnum tipoEmailCorecEnum = TipoEmailCorecEnum.valueOfDescricao(tipoEmail);
        ParametroEmail parametroEmail = ParametroEmailMediator.get().buscarPorTipo(tipoEmailCorecEnum);
        parametroEmail.setRemetente(remetente);
        parametroEmail.setTitulo(titulo);
        parametroEmail.setCorpo(corpo);
        parametroEmail.setPrazo(StringUtils.isBlank(prazo) ? null : Integer.valueOf(prazo));
        mensagemSucesso = ParametroEmailMediator.get().salvar(parametroEmail);
    }
    
    public String getMensagem() {
        return mensagemSucesso;
    }

}
