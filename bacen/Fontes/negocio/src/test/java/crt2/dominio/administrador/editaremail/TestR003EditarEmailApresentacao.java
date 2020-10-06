package crt2.dominio.administrador.editaremail;

import org.apache.commons.lang.StringUtils;

import br.gov.bcb.sisaps.src.dominio.ParametroEmail;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoEmailCorecEnum;
import br.gov.bcb.sisaps.src.mediator.ParametroEmailMediator;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import crt2.ConfiguracaoTestesNegocio;

public class TestR003EditarEmailApresentacao extends ConfiguracaoTestesNegocio {
    
    public String isExibirPrazo(Integer idParametro) {
        ParametroEmail parametroEmail = ParametroEmailMediator.get().buscarPorPK(idParametro);
        return SimNaoEnum.getTipo(parametroEmail.getPrazoObrigatorio().booleanValue()).getDescricao();
    }
    
    public String isPrazoObrigatorio(Integer idParametro) {
        ParametroEmail parametroEmail = ParametroEmailMediator.get().buscarPorPK(idParametro);
        return SimNaoEnum.getTipo(parametroEmail.getPrazoObrigatorio().booleanValue()).getDescricao();
    }
    
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
    
    public String salvarParametroEmail(String tipoEmail, String remetente, String titulo, String corpo, String prazo) {
        return salvarParametroEmail(tipoEmail, remetente, titulo, corpo, 
                StringUtils.isBlank(prazo) ? null : Integer.valueOf(prazo));
    }

}
